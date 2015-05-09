package models.client;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import javax.persistence.Id;
import play.db.ebean.Model;
import play.Logger;


@Entity
public class ResetPassword extends Model{ 
	@Id
	public Long id;
	//fields
	@Column(nullable=false)
	public String token;
	
	@Column(nullable=false)
	public Date date_generated;
	
	public Boolean used = false;
	
	@Column(nullable=false)
	@ManyToOne
	public Client client;
	
	public ResetPassword(){
	
	}
	
	public ResetPassword(String token){
	  this.token = token;
	}

	public String saveResetPassword(){
		if(id == null){
			date_generated = new Date();
			save();
			Logger.info("ID is null");
			return token;
		}else{
			update();
			Logger.info("ID is not null");
			return token;
		}
	}
	
	public static Finder<Long, ResetPassword> find = new Finder<Long, ResetPassword>(Long.class, ResetPassword.class);
	
	public ResetPassword getResetPassword(String token){
		return find.where().eq("token", token).findUnique();
	}
		
} 
