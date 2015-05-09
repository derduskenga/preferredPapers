import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future

object GlobalWeb extends GlobalSettings {	
	
	// 404 - page not found error
	override def onHandlerNotFound (request: RequestHeader) = {
		play.mvc.Http.Context.current.set(play.core.j.JavaHelpers.createJavaContext(request));
		var arrays = request.uri.split("/").toList.contains("mydashboard");
		var logged_in = true;
		try{
			var email = request.session("email");
		}catch{
			 case e: Exception => logged_in =false;
		}
		
		if(arrays && logged_in)
			Future.successful(NotFound(views.html.errors.dashboardOnHandlerNotFound(request)))
		else
			Future.successful(NotFound(views.html.errors.onHandlerNotFound(request)))
	}
	
	// 500 - internal server error
	override def onError (request: RequestHeader, throwable: Throwable) = {
		play.mvc.Http.Context.current.set(play.core.j.JavaHelpers.createJavaContext(request));
		var arrays = request.uri.split("/").toList.contains("mydashboard");
		var logged_in = true;
		try{
			var email = request.session("email");
		}catch{
			 case e: Exception => logged_in =false;
		}
		
		if(arrays && logged_in)
			Future.successful(InternalServerError(views.html.errors.dashboardOnError(throwable)))
		else	
			Future.successful(InternalServerError(views.html.errors.onError(throwable))	)
	}
	
	// called when a route is found, but it was not possible to bind the request parameters
	override def onBadRequest (request: RequestHeader, error: String) = {
		var arrays = request.uri.split("/").toList.contains("mydashboard");
		play.mvc.Http.Context.current.set(play.core.j.JavaHelpers.createJavaContext(request));
		
		var logged_in = true;
		try{
			var email = request.session("email");
		}catch{
			 case e: Exception => logged_in =false;
		}
		
		if(arrays && logged_in)
			Future.successful(BadRequest(views.html.errors.dashboardbadrequest.render()));
		else
		Future.successful(BadRequest(views.html.errors.webbadrequest.render()))
	}
}