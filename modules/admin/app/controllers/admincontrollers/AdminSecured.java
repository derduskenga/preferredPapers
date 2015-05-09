package controllers.admincontrollers;

import play.mvc.Http.Flash;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.RequestHeader;
import play.Logger;

public class AdminSecured extends Security.Authenticator {
	@Override
	public String getUsername(Context ctx) {
			return ctx.session().get("admin_email");
	}
	@Override
	public Result onUnauthorized(Context ctx) {
		Logger.info("Adding referrer: "+ctx._requestHeader().uri());
		ctx.session().put("referrer", ctx._requestHeader().uri());
		Flash flashObject = ctx.flash();
		flashObject.put("error", "Oops! You must login first");
		return redirect(controllers.admincontrollers.routes.Application.login());
	}
}