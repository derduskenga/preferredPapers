package controllers.admincontrollers;

import play.*;
import play.mvc.*;
import views.html.*;
import views.html.securityview.adminlogin;
import views.html.adminviews.robots;
import models.admin.userpermissions.AuthorisedUser;
import models.admin.adminmodels.AdminUser;
import models.common.security.PasswordHash;
import models.common.security.RandomString;
import play.data.Form;
import static play.data.Form.form;

public class Application extends Controller{

	public static Result login(){
			return ok(adminlogin.render("Login"));
	}
	
	public static class Login {
		public String email;
		public String password;
	}
	
	public static Result logout(){
		session().clear();
		flash("success","You are now logged out.");
		return redirect(controllers.admincontrollers.routes.Application.login());	
	}
	
	public static Result robots(){
		return ok(robots.render());
	}
	
	public static Result authenticate() {	
		Form<Login> loginForm = form(Login.class).bindFromRequest();
		if(loginForm.hasErrors()) {
				Logger.info("Inside has errors.");
				flash("error", "Wrong email/password");
				return redirect(controllers.admincontrollers.routes.Application.login());	
		}
		String email= loginForm.get().email;
		String password = loginForm.get().password;
		AdminUser adminUser=new AdminUser().authenticate(email, password) ;
		if(adminUser == null){
			flash("error", "Wrong email/password");
			return redirect(controllers.admincontrollers.routes.Application.login());	
		}
		//Context context = Context.current();
		//String referrer = context.session().get("referrer");
		session().clear();
		session("admin_email", email);
		session("f_name", adminUser.first_name);
		session("l_name", adminUser.last_name);
		return redirect(controllers.admincontrollers.routes.AdminActions.index());	
	}

}