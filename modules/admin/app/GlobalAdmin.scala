import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future

object GlobalAdmin extends GlobalSettings {	
	
	// 404 - page not found error
	override def onHandlerNotFound (request: RequestHeader) = {
	play.mvc.Http.Context.current.set(play.core.j.JavaHelpers.createJavaContext(request));
	
		var logged_in = true;
		try{
			var email = request.session("admin_email");
		}catch{
				case e: Exception => logged_in =false;
		}
		
	if(logged_in)
		Future.successful(NotFound(views.html.adminviews.errors.onHandlerNotFound(request)))
	else
		Future.successful(Redirect(controllers.admincontrollers.routes.Application.login()));
	}
	
	// 500 - internal server error
	override def onError (request: RequestHeader, throwable: Throwable) = { 
	play.mvc.Http.Context.current.set(play.core.j.JavaHelpers.createJavaContext(request));
	var logged_in = true;
	try{
		var email = request.session("admin_email");
	}catch{
			case e: Exception => logged_in =false;
	}
	if(logged_in)
		Future.successful(InternalServerError(views.html.adminviews.errors.onError(throwable)))
	else
		Future.successful(Redirect(controllers.admincontrollers.routes.Application.login()));
	}
	
	// called when a route is found, but it was not possible to bind the request parameters
	override def onBadRequest (request: RequestHeader, error: String) ={ 
	play.mvc.Http.Context.current.set(play.core.j.JavaHelpers.createJavaContext(request));
	var logged_in = true;
	try{
		var email = request.session("admin_email");
	}catch{
			case e: Exception => logged_in =false;
	}
	
	if(logged_in)
		Future.successful(BadRequest(views.html.adminviews.errors.adminbadrequest(error)))
	else
		Future.successful(Redirect(controllers.admincontrollers.routes.Application.login()));
	}

}
