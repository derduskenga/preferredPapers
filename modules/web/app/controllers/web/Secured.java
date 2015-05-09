package controllers.web;

import play.mvc.Http.Flash;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
import java.util.Date;
import play.mvc.Http.RequestHeader;
import play.Logger;
import play.Play;

public class Secured extends Security.Authenticator {
	@Override
	public String getUsername(Context ctx) {
			//check if sess variables are set
			if(ctx.session().get("email")==null)
				return null;
			//now that sess variables are set, check if sess is expired
			String previousTick = ctx.session().get("userTime");
			
			if (previousTick != null && !previousTick.equals("")) {
				long previousT = Long.valueOf(previousTick);
				long currentT = new Date().getTime();
				long timeout = Long.valueOf(Play.application().configuration().getString("sessionTimeout")) * 1000 * 60;
				if ((currentT - previousT) > timeout) {
					// session expired
					ctx.session().clear();
					return null;
				} 
			}
        
			// update time in session
			String tickString = Long.toString(new Date().getTime());
			ctx.session().put("userTime", tickString);
			
			return ctx.session().get("email");
	}
	@Override
	public Result onUnauthorized(Context ctx) {
		Logger.info("Adding referrer: "+ctx._requestHeader().uri());
		ctx.session().put("referrer", ctx._requestHeader().uri());
		Flash flashObject = ctx.flash();
		flashObject.put("error", "Oops! You must login first");
		return redirect(routes.Application.index());
	}
}