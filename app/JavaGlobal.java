import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.libs.F.*;
import play.Logger;
import java.lang.reflect.Method;
import play.api.mvc.Handler;
import play.api.mvc.RequestHeader;


import static play.mvc.Results.*;

public class JavaGlobal extends GlobalSettings {

	public JavaGlobal(){
			Logger.info("At the global constructor...");
	}
	
	/*
	private String getSubdomain(Request request){
			Logger.info("Request Host:"+request.host().toString());
			return request.host().replaceFirst("[\\.]?[^\\.]+[\\.][^\\.]+$", "");
	}
	@Override
	public Action onRequest(Request request, Method actionMethod) {
			Logger.info("before each request..." + request.toString());
			//String subDomain = getSubdomain(request).split(".")[0];
			String subDomain = "www";
			Logger.info("subdomain:"+subDomain.toString());
			onRouteRequest(request);
			/*switch (subDomain){
				case "admin":
					scala.Option<Handler> adminhandler = admin.Routes.routes().lift().apply((play.api.mvc.RequestHeader)request);
					if (adminhandler.isDefined()) {
						 adminhandler.get();
						}
					break;
				default:
					scala.Option<Handler> webhandler = web.Routes.routes().lift().apply((play.api.mvc.RequestHeader)request);
					if (webhandler.isDefined()) {
							webhandler.get();
						}
					break;
			} 
			return super.onRequest(request, actionMethod);
		//return super.onRequest(request, actionMethod);
	} */
	 /*@Override
	public Handler onRouteRequest(Http.RequestHeader request){
			//String subDomain = getSubdomain(request).split(".")[0];
			Logger.info("request:"+request.host().toString());
			Logger.info("URI:"+request.uri().toString());
			Logger.info("Path :"+request.path().toString());
			String subDomain = "www";
			Logger.info("subdomain:"+subDomain.toString());

			return null;
	}
	
   public Promise<Result> onHandlerNotFound(RequestHeader request) {
			return Promise.<Result>pure(notFound(
				views.html.notFoundPage.render(request.uri())
			));
	}*/

}