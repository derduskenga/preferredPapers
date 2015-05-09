package controllers.admincontrollers;

import views.html.adminviews.adminhome;
import views.html.adminviews.createadminuser;
import views.html.adminviews.manageusers;
import views.html.adminviews.adminerror;
import views.html.adminviews.adminprofile;
import views.html.adminviews.marketingemail;
import views.html.adminviews.adminroles;
import views.html.adminviews.addrole;
import views.html.adminviews.admincouponcode;
import views.html.adminviews.freelancewriter;
import views.html.adminviews.writers;
import play.data.validation.Constraints;
import play.*;
import play.mvc.*;
import play.data.Form;
import com.avaje.ebean.Ebean;
import static play.data.Form.form;

import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


import models.admin.adminmodels.AdminUser;
import models.common.security.PasswordHash;
import models.common.security.RandomString;
import models.admin.adminmodels.AdminMails;
import controllers.admincontrollers.AdminSecured;
import controllers.admincontrollers.ManageOrdersActions;
import models.admin.userpermissions.SecurityRole;
import be.objectify.deadbolt.java.actions.SubjectNotPresent;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.admin.security.NoUserDeadboltHandler;
import models.admincoupon.AdminReferalCode;
import models.client.ReferralCode;
import models.writer.FreelanceWriter;
import models.admin.adminmodels.AdminSentMail;

@Security.Authenticated(AdminSecured.class)
public class AdminActions extends Controller{
	static Form<AdminReferalCode> adminCouponForm = form(AdminReferalCode.class);
	
	static Form<FreelanceWriter> freelanceWriterForm = form(FreelanceWriter.class);
	
	static List<AdminReferalCode> adminReferalCodeList = new ArrayList<AdminReferalCode>();
	
	public static Result index(){
			return ok(adminhome.render());
	}
	
	@Restrict({@Group({"Super Admin"})})
	public static Result createNewAdminUser(){
			Form<AdminUser> adminUserForm = Form.form(AdminUser.class);
			AdminUser adminUser = new AdminUser();
			return ok(createadminuser.render(adminUserForm,adminUser.getAdminRoles(adminUser.getRoles())));
	}
	
	@Restrict({@Group({"Super Admin"})})
	public static Result saveNewAdminUser(){			
			Form<AdminUser> 	adminUserForm = form(AdminUser.class).bindFromRequest();	
			AdminUser adminUser = new AdminUser();
			
			if(adminUserForm.hasErrors()) {
				flash("error", "Please correct the form below.");
						Map<String, String[]> map = request().body().asFormUrlEncoded();
						String[] checkedVal = map.get("admin_priviledges[]");
						List<String> adminPriviledges = null;
						if(checkedVal != null){
								adminPriviledges  = Arrays.asList(checkedVal);
								adminUser.roles = new ArrayList<SecurityRole>();
								for(String priviledge: adminPriviledges){
										SecurityRole secRole = SecurityRole.findByName(priviledge);
										if(secRole != null)
												adminUser.roles.add(secRole);
								}
						}
					return badRequest(createadminuser.render(adminUserForm, adminUser.getAdminRoles(adminUser.getRoles())));
			}
			adminUser = adminUserForm.get();
			Map<String, String[]> map = request().body().asFormUrlEncoded();
			String[] checkedVal = map.get("admin_priviledges[]");
			List<String> adminPriviledges = null;
			if(checkedVal != null){
					 adminPriviledges  = Arrays.asList(checkedVal);
					 adminUser.roles = new ArrayList<SecurityRole>();
					 for(String priviledge: adminPriviledges){
							SecurityRole secRole = SecurityRole.findByName(priviledge);
							if(secRole != null)
									adminUser.roles.add(secRole);
									
					 }
			}
			RandomString randomString = new RandomString(8);
			String random = randomString.nextString();
			if(adminUser.admin_user_id==null){
					try{
							String hashedPassword =PasswordHash.createHash(random);
							String[] params = hashedPassword.split(":");
							adminUser.password = params[0];
							adminUser.salt = params[1];
							AdminMails am = new AdminMails();
							am.sendRegisteredAdminFirstEmail(adminUser,random);
					}catch(Exception e){
							Logger.error("Error creating hashed password and sending mail for new admin user.",e);
							Logger.info(String.format("Error: Admin mail and password %s %s", adminUser.email, random));
					}
			}
			if(adminUser.saveAdminUser()){
				Ebean.saveManyToManyAssociations(adminUser,"roles");
				flash("success", "Admin user successfully saved.");
				return redirect(controllers.admincontrollers.routes.AdminActions.createNewAdminUser());
			}
			flash("error", "Could not save new admin user.");
			return redirect(controllers.admincontrollers.routes.AdminActions.createNewAdminUser());
	}
	
	@Restrict({@Group({"Super Admin"})})
	public static Result manageAdminUsers(){
			AdminUser users = new AdminUser();
			List<AdminUser> list = users.fetchAll();
			return ok(manageusers.render(list));
	}
	
	public static Result myProfile(){
			return ok(adminprofile.render(AdminUser.findByEmail(session().get("admin_email"))));
	}
	
	@Restrict({@Group({"Super Admin"})})
	public static Result deactivateAdminUser(Long admin_user_id){
				ObjectNode result = Json.newObject();
				AdminUser user = new AdminUser();
				user = user.getUserById(admin_user_id);
				if(user != null){
					if(user.active){
							if(!user.deactivate()){
								result.put("status", "0");
								result.put("deactivation", "1");
								result.put("message", "User Not Found");
								return ok(result);
							}
							result.put("status", "1");
							result.put("deactivation", "1");
							result.put("message", user.first_name+" "+user.last_name+" has successfully been deactivated.");
							return ok(result);
						}else{
							if(!user.activate()){
								result.put("status", "0");
								result.put("deactivation", "0");
								result.put("message", "User Not Found");
								return ok(result);
							}
							result.put("status", "1");
							result.put("deactivation", "0");
							result.put("message", user.first_name+" "+user.last_name+" has successfully been activated.");
							return ok(result);
				}
			}
			result.put("status", "-1");
			result.put("deactivation", "-1");
			result.put("message", "User Not Found");
			return ok(result);
	}
		
	public static Result editAdminUser(Long user_id){	
			AdminUser user = new AdminUser();
			user = user.getUserById(user_id);
			if(user==null){
				String error = "User not found.";
				return badRequest(adminerror.render(error));
			}
			Form<AdminUser> adminUserForm = Form.form(AdminUser.class);
			Form<AdminUser> filledUserForm = adminUserForm.fill(user);
			AdminUser adminUser = new AdminUser();
			return ok(createadminuser.render(filledUserForm,adminUser.getAdminRoles(user.getRoles())));
	}
	
	@Restrict({@Group({"Super Admin"})})
	public static Result newAdminRole(){
			Form<SecurityRole> securityRoleForm = Form.form(SecurityRole.class);
			List<SecurityRole> securityRoles = new SecurityRole().getAllSecurityRoles();
			return ok(adminroles.render(securityRoleForm, securityRoles));
	}
	
	@Restrict({@Group({"Super Admin"})})
	public static Result saveNewAdminRole(){
			Form<SecurityRole> boundForm = Form.form(SecurityRole.class).bindFromRequest();
			if(boundForm.hasErrors()){
					flash("error","Please correct the form below.");
					flash("show_form","true");
					List<SecurityRole> securityRoles = new SecurityRole().getAllSecurityRoles();
					return badRequest(adminroles.render(boundForm,securityRoles));
			}
			SecurityRole role = boundForm.get();
			role.save();
			flash("success", "Security role successfully added");
			return redirect(controllers.admincontrollers.routes.AdminActions.newAdminRole());
	}
	
	public static Result changePassword(){
	       JSONObject jobject = new JSONObject();
		   try{
			Map<String, String[]> change_password_values = new HashMap<String,String[]>();
			change_password_values = request().body().asFormUrlEncoded();
			if(change_password_values.isEmpty()){
				jobject.put("success",0);
				jobject.put("message","An error occured. Try again");
				return ok(Json.parse(jobject.toString()));
			}
			String c_password_details[] = change_password_values.get("current_password");
			//current password
			String c_password = c_password_details[0];
			
			String n_password_details[] = change_password_values.get("new_password");
			//new password
			String n_password = n_password_details[0];
			if(n_password.length()<5){
					jobject.put("success",0);
					jobject.put("message","Your new password is too short.");
					return ok(Json.parse(jobject.toString()));
			}
			String email = session().get("admin_email");
			AdminUser adminUser =  (email == null)? null : AdminUser.findByEmail(email);
			if(adminUser == null){
					jobject.put("success",0);
					jobject.put("message","An error occured. Try again.");
					return ok(Json.parse(jobject.toString()));
			}
				
			Boolean valid = PasswordHash.validatePassword(c_password, adminUser.password+":"+adminUser.salt);	
				if(valid){
							String hashedPassword = PasswordHash.createHash(n_password);
							String[] params = hashedPassword.split(":");				
							adminUser.password = params[0];
							adminUser.salt = params[1];
							adminUser.saveAdminUser();
				}else{
						jobject.put("success",0);
						jobject.put("message","Current password entered is invalid.");
						return ok(Json.parse(jobject.toString()));
				}
			//do you code here; if password change is successful, return the JSONObject as shoen belo
			jobject.put("success",1);
			jobject.put("message","Password has been changed");
			return ok(Json.parse(jobject.toString()));  
			}catch(Exception e){
							Logger.error("Error creating hashed password",e);
							jobject.put("success",0);
							jobject.put("message","An error occured. Try again.");
							return ok(Json.parse(jobject.toString()));
			}
	}
	
	public static Result marketingEmail(){
			Form<NewEmail> newMailForm = form(NewEmail.class);
			AdminUser adminUser = AdminUser.findByEmail(session().get("admin_email"));
			AdminReferalCode arc = new AdminReferalCode().getFirstAdminCouponCode(adminUser.admin_user_id);
			String text = new AdminMails().getClientMarketingEmailString(arc==null?"{insert code}":arc.code, adminUser.first_name);
			return ok(marketingemail.render(newMailForm, text));
	}
	
	public static Result sendMarketingEmail(){
			Form<NewEmail> newMailForm = form(NewEmail.class).bindFromRequest();
			boolean send = true;
			AdminUser adminUser = AdminUser.findByEmail(session().get("admin_email"));
			if(new AdminReferalCode().getAdminCouponCodesByAdminId(adminUser.admin_user_id).size()<1)
				send = false;
			if(newMailForm.hasErrors() || !send){
				flash("client_marketing_mail_error", send? "Could not send email. Please correct the form below." : "Please generate at least one coupon code before sending marketing email.");
				return ok(marketingemail.render(newMailForm, ""));
			}
			NewEmail newMail = newMailForm.get();
			AdminMails mail = new AdminMails();
			mail.sendClientMarketingMail(newMail.email, newMail.message);
			
			new AdminActions().saveAdminMarketingEmail(adminUser, newMail.email, newMail.message);
			flash("client_marketing_mail_success", "Email sent successfully.");
			return redirect(controllers.admincontrollers.routes.AdminActions.marketingEmail());		
	}
	
	public static Result generateAdminCouponCode(){
		Form<AdminReferalCode> adminCouponBoundForm = adminCouponForm.bindFromRequest();
		if(session().get("admin_email") != null){
			adminReferalCodeList = new AdminReferalCode().getAdminCouponCodesByAdminId(AdminUser.findByEmail(session().get("admin_email")).admin_user_id);
			//Logger.info("user email:" + )
		}
		if(adminCouponBoundForm.hasErrors()){
			return ok(admincouponcode.render(adminCouponBoundForm,adminReferalCodeList));
		}
		
		AdminReferalCode adminReferalCode = adminCouponBoundForm.get();
		//generate the coupon code
		String coupon_code = new ReferralCode().generateString(new java.util.Random(),5);
		adminReferalCode.code = coupon_code;
		
		AdminUser adminUser = AdminUser.findByEmail(session().get("admin_email"));
		if(adminUser == null){
			return ok(admincouponcode.render(adminCouponForm,adminReferalCodeList));
		}
		adminReferalCode.admin_id = adminUser.admin_user_id;
		adminReferalCode.saveAdminReferalCode();
		flash("save-admin-code-success","You have created a coupon code.");
		//return ok(admincouponcode.render(adminCouponForm,adminReferalCodeList));
		return redirect(controllers.admincontrollers.routes.AdminActions.newAdminCouponCode());
	}
	public static Result newAdminCouponCode(){
		if(session().get("admin_email") == null){
			return ok(admincouponcode.render(adminCouponForm,adminReferalCodeList));
		}
		adminReferalCodeList = new AdminReferalCode().getAdminCouponCodesByAdminId(AdminUser.findByEmail(session().get("admin_email")).admin_user_id);
		return ok(admincouponcode.render(adminCouponForm,adminReferalCodeList));		
	}
	public static Result deleteCouponCode(String coupon_code){
		final AdminReferalCode referral_code = new AdminReferalCode().getReferralCode(coupon_code);
		if(referral_code == null){
			flash("admin-code-not-found","This coupon code was not found!");
			return redirect(controllers.admincontrollers.routes.AdminActions.newAdminCouponCode()); 
		}
		flash("admin-code-deleted","A coupon code has been deleted");
		referral_code.deleteCode(referral_code);
		return redirect(controllers.admincontrollers.routes.AdminActions.newAdminCouponCode());
	}
	
	public static Result saveWriter(){
		Form<FreelanceWriter> freelanceWriterBoundForm = freelanceWriterForm.bindFromRequest();
		if(freelanceWriterBoundForm.hasErrors()){
			return ok(freelancewriter.render(freelanceWriterBoundForm));
		}
		FreelanceWriter freelance_writer = freelanceWriterBoundForm.get();
		//generate unique writer_id 
		flash("writer-registered","Writer has been registered");
		if(freelance_writer.freelance_writer_id == null){
			freelance_writer.writer_id = freelance_writer.generateRandomLongWriterId();
			freelance_writer.saveFreelanceWriter();
			return redirect(controllers.admincontrollers.routes.AdminActions.newWriter());
			
		}
		freelance_writer.saveFreelanceWriter();
		return redirect(controllers.admincontrollers.routes.AdminActions.allWriters());
		
	}
	public static Result newWriter(){
		if(session().get("admin_email") == null){
			return ok(freelancewriter.render(freelanceWriterForm));
		}
		return ok(freelancewriter.render(freelanceWriterForm));
	}
	public static Result allWriters(){
		List<FreelanceWriter> freelance_writer_list = new ArrayList<FreelanceWriter>();
		freelance_writer_list = FreelanceWriter.getAllWriters();
		return ok(writers.render(freelance_writer_list));
	}
	public static Result editWriter(Long writer_id){
		FreelanceWriter freelance_writer = new FreelanceWriter().getWriterByWriterId(writer_id);
		if(freelance_writer == null){
			flash("writer-not-found","This writer was not found");
			return redirect(controllers.admincontrollers.routes.AdminActions.allWriters());
		}
		Form<FreelanceWriter> filledFreelanceWriterForm = freelanceWriterForm.fill(freelance_writer);
		return ok(freelancewriter.render(filledFreelanceWriterForm));
	}
	public void saveAdminMarketingEmail(AdminUser adminUser, String email, String message){
			AdminSentMail asm = new AdminSentMail();
			asm.adminUser = adminUser;
			asm.sent_to = email;
			asm.message = message;
			asm.sent_on = new Date();
			asm.saveAdminSentMail();
	}

	public static Result searchOrder(){
		Map<String, String[]> search_order_code = new HashMap<String,String[]>();
		search_order_code = request().body().asFormUrlEncoded();
		if(!search_order_code.isEmpty()){
			String order_code_array[] =  search_order_code.get("sought_order_code");
			Long order_code = Long.valueOf(order_code_array[0]);
			return redirect(controllers.admincontrollers.routes.ManageOrdersActions.manageOrder(order_code));
		}
		return redirect(controllers.admincontrollers.routes.ManageOrdersActions.manageOrders());
	} 
	public static class NewEmail{
		@Constraints.Required(message="Please enter email.")
		@Constraints.Email(message="The email you entered does not look valid.")
		public String email;
		@Constraints.Required(message="Please enter write your message.")
		public String message;
	}
}