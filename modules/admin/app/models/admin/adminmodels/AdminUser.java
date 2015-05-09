package models.admin.adminmodels;

import play.db.ebean.Model;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Column;
import play.data.validation.Constraints;
import play.Logger;	
import com.avaje.ebean.Expr;
import play.data.validation.ValidationError;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import play.db.ebean.Model;

import models.common.security.PasswordHash;

import models.admin.userpermissions.SecurityRole;
import models.admin.userpermissions.UserPermission;

@Entity
public class AdminUser extends Model implements Subject{
		@Id
		public Long admin_user_id;
		
		@Constraints.Required(message="First name is required.")
		public String first_name;
		
		@Constraints.Required(message="Last name is required.")
		public String last_name;
		
		@Column(unique=true)
		@Constraints.Required(message="Email is required.")
		@Constraints.Email(message="The email you entered does not seem valid.")
		public String email;
		
		public String password;
		
		public String salt;

		@Column(columnDefinition = "boolean default 'true'")
		public Boolean active;
		
		public String admin_user_offset = "-180";
		
		
		public static Finder<Long, AdminUser> adminUserFinder = new Finder<>(Long.class,AdminUser.class); 

		public AdminUser getAdminUser(String username_or_email, String password){
				return adminUserFinder.where(
									Expr.or(Expr.and(Expr.eq("email",username_or_email),Expr.eq("password",password)),
									Expr.and(Expr.eq("email",username_or_email),Expr.eq("password",password)))
									).findUnique();
		}
		
		public static Map<String, Boolean> getAdminLevels(){
				Map<String, Boolean> 	adminLevelsMap = new HashMap<String, Boolean>();
				for (AdminLevel adminLevel : AdminLevel.values()) {
						String adminlev = adminLevel.name().trim();
						adminLevelsMap.put(adminlev, false);
				}
				return adminLevelsMap;
		 }	
		 	
		 
		public Map<String, Boolean> getAdminRoles(List<? extends Role> roles){
				Map<String, Boolean> 	adminRolesMap = new HashMap<String, Boolean>();
				List<String> sRoles = new ArrayList<String>();
				for(Role role:roles){
						role = (SecurityRole)role;
						sRoles.add(role.getName());
						}
				for (SecurityRole secRole : SecurityRole.find.where().findList()) {
						String secRoleName = secRole.name;
						if(sRoles.contains(secRoleName)){
							adminRolesMap.put(secRoleName, true);
						}else{
							adminRolesMap.put(secRoleName, false);
						}
				}
				return adminRolesMap;
		}
		 
		public boolean saveAdminUser(){
				if(admin_user_id != null)
					update();
				else{
					this.active = true;
					save();
				}
				return true;
		 }
		 
		public  AdminUser authenticate(String email, String password) {
			try{
				AdminUser adminUser = adminUserFinder.where().eq("email", email).findUnique();
				if(adminUser==null)
					return null;
				else{
					Boolean valid = PasswordHash.validatePassword(password, adminUser.password+":"+adminUser.salt);	
					if(valid)
						return adminUser;
					else
						return null;
				}
			}catch(Exception e){
				Logger.error("Error authenticating admin user:"+email,e);
			}
			return null;
		}
		 
		public boolean deactivate(){
				this.active = false;
				if(saveAdminUser())
					return true;
			    else
					return false;
		}
		
		public boolean activate(){
				this.active = true;
				if(saveAdminUser())
					return true;
			    else
					return false;
		}
		 
		public List<AdminUser> fetchAll(){
				return adminUserFinder.findList();
		}
		 
		public AdminUser getUserById(Long user_id){
				return adminUserFinder.where().eq("admin_user_id", user_id).findUnique();
		}
	
		@ManyToMany
		public List<SecurityRole> roles;

		@ManyToMany
		public List<UserPermission> permissions;

		public static final Finder<Long, AdminUser> find = new Finder<Long, AdminUser>(Long.class,
																								AdminUser.class);

		@Override
		public List<? extends Role> getRoles()
		{
			return roles;
		}

		@Override
		public List<? extends Permission> getPermissions()
		{
			return permissions;
		}

		@Override
		public String getIdentifier()
		{
			return email;
		}

		public static AdminUser findByUserName(String userName)
		{
			return find.where()
					.eq("userName",
						userName)
					.findUnique();
		}
		public static AdminUser findByEmail(String email)
		{
			return find.where()
					.eq("email",
						email)
					.findUnique();
		}
		
		public List<ValidationError> validate(){
				List<ValidationError> errors = new ArrayList<>();
				AdminUser adminUser = AdminUser.find.where().eq("email", email).findUnique();
				if (adminUser != null && !adminUser.admin_user_id.equals(admin_user_id)) {
							errors.add(new ValidationError("email", "Email already exists."));
							return errors;
				}
				return null;
		}
		
		
}