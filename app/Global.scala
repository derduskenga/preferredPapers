import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future

import com.avaje.ebean.Ebean;
import java.util.ArrayList;
import scala.collection.mutable.ArrayBuffer;


import play.api.http.HeaderNames 
import play.api.mvc.{Action, Handler, RequestHeader}

import models.admin.adminmodels.AdminUser;
import models.admin.adminmodels.AdminMails;
import models.admin.userpermissions.SecurityRole;
import models.admin.userpermissions.UserPermission;
import models.common.security.PasswordHash;
import models.common.security.RandomString;

import play.api.Play.current;

object Global extends GlobalSettings {
	
	private def getSubdomain (request: RequestHeader) = request.domain.replaceFirst("[\\.]?[^\\.]+[\\.][^\\.]+$", "")
	
	override def onRouteRequest (request: RequestHeader) = getSubdomain(request) match {
			case "admin" => admin.Routes.routes.lift(request)
			case "www" => web.Routes.routes.lift(request)
			case default => if(play.api.Play.isProd(play.api.Play.current)) 
											Some(redirectToWWW(request))		
									else
											web.Routes.routes.lift(request)
	}
	
	private def redirectToWWW(request: RequestHeader) = Action { 
				MovedPermanently(s"http://www.timelyessay.com${request.path}").withHeaders(HeaderNames.CACHE_CONTROL -> "public, max-age=31556926" ) 
	}

	@throws(classOf[Exception])
	override def onStart(app: Application) {
		if (SecurityRole.find.findRowCount() == 0){
			Logger.info("Equal 0.");
			var security_roles = Array("Super Admin", "Normal Admin", "Writer Support", "Customer Support");
			for ( i <- 0 to (security_roles.length - 1)) {
				val role = new SecurityRole();
				role.name = security_roles(i);
				role.save();
			      }
		}
		if (UserPermission.find.findRowCount() == 0)
		{
		    val permission = new UserPermission();
		    permission.value = "printers.edit";
		    permission.save();
		}
		var random : String = "";
		if (AdminUser.find.findRowCount() == 0)
		{
		    var user = new AdminUser();
		    user.email = "wambua.sam@gmail.com";
		    user.first_name = "Sam";
		    user.last_name = "Wambua";
		    user.active = true;
		    var randomString = new RandomString(8);
			random = randomString.nextString();
			var hashedPassword =PasswordHash.createHash(random);
			var params = hashedPassword.split(":");
			user.password = params(0);
			user.salt = params(1);
		    user.roles = new ArrayList[SecurityRole]();
		    user.roles.add(SecurityRole.findByName("Super Admin"));
		    user.roles.add(SecurityRole.findByName("Normal Admin"));
		    user.roles.add(SecurityRole.findByName("Customer Support"));
		    user.roles.add(SecurityRole.findByName("Writer Support"));
		    user.permissions = new ArrayList[UserPermission]();
		    user.permissions.add(UserPermission.findByValue("printers.edit"));

		    user.save();
		    Ebean.saveManyToManyAssociations(user,
		                                     "roles");
		    Ebean.saveManyToManyAssociations(user,
		                                     "permissions");
		    var am = new AdminMails();
			am.sendRegisteredAdminFirstEmail(user,random);
			Logger.info("Password: "+random);
		}
		
	    Logger.info("Application has started")
	}
	//Handling missing actions and binding errors	
	// 404 - page not found error
	override def onHandlerNotFound (request: RequestHeader) = getSubdomain(request) match {
		case "admin" => GlobalAdmin.onHandlerNotFound(request)
		case _ => GlobalWeb.onHandlerNotFound(request)
	}
	
	/**/
	// 500 - internal server error
	override def onError (request: RequestHeader, throwable: Throwable) = getSubdomain(request) match {
		case "admin" => GlobalAdmin.onError(request, throwable)
		case _ => GlobalWeb.onError(request, throwable)
	}
	
	// called when a route is found, but it was not possible to bind the request parameters
	override def onBadRequest (request: RequestHeader, error: String) = getSubdomain(request) match {
		case "admin" => GlobalAdmin.onBadRequest(request, error)
		case _ => GlobalWeb.onBadRequest(request, error)
	}

}
