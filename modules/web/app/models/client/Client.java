package models.client;
import java.util.*;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import models.orders.Orders;
import models.orders.OrderMessages;
import models.common.security.PasswordHash;
import play.Logger;
import models.client.PreferredWriter;
import models.client.ReferralCode;

@Entity
public class Client extends Model{ 
	@Id
	public Long id;
	//fields
	@Constraints.Required(message="First name is required.")
	public String f_name;
	@Constraints.Required(message="Last name is required.")
	public String l_name;
	
	@Constraints.Required(message="Email is required.")
	@Constraints.Email(message="Your email does not seem valid.")
	public String email;
	
	public String c_email;
	
	@Column(columnDefinition="varchar(64)")
	public String password;
	@Column(columnDefinition="varchar(64)")
	public String salt;
	
	@Constraints.Required(message="Country code is required - Phone number is invalid or empty")
	public String country_code; 
	@Constraints.Required(message="Area code is required - Phone number is invalid or empty")
	public String area_code; 
	@Constraints.Required(message="Phone number is required - Phone number is invalid or empty")
	public String phone_number;
	
	public String alternative_phone;
	public boolean receive_company_mail=true;
	public String client_time_zone;
	public String client_time_zone_offset;
	public TimeZone client_time_zone_real;

	
	@Column(name="created_on")
	@Temporal(TemporalType.TIMESTAMP)
	public Date created_on;
	
	//relationship fields
	@OneToMany(mappedBy="client")
	public List <Orders> orders;
	
	@OneToOne
	public Countries country;
	
	@OneToOne(mappedBy="client")
	public ReferralCode referralCode;
	
	@OneToMany(mappedBy="client")
	public List<PreferredWriter> preferredWriter;  
	//@OneToMany(mappedBy="client")
	//public List<ClientReferalEarning> clientReferalEarning;
	
	public static Finder<Long, Client> finder = new Finder<Long, Client>(Long.class, Client.class);
	
	public Client(){
	
	}
	
	@PrePersist
	protected void onCreate() {
	  created_on = new Date();
	}
	
	public Client(String email, String password){
	  this.password = password;
	  this.email = email;
	}
	
	public static Client getClient(String email){
	  return finder.where().eq("email", email).findUnique();
	}
	
	public static Client authenticate(String email, String password) {
		try{
			Client client = finder.where().eq("email", email).findUnique();
			if(client==null)
				return null;
			else{
				Boolean valid = PasswordHash.validatePassword(password, client.password+":"+client.salt);	
				if(valid)
					return client;
				else
					return null;
			}
		}catch(Exception e){
			Logger.error("Error authenticating client:"+email,e);
		}
		return null;
	}
	
	public Long saveClient(){
	  if(this.id == null){
	    save();
	    return id;
	  }else{
	   update();
	   return id;
	  }
	}
	
	public static Finder<Long, PreferredWriter> preferredWriterFinder = new Finder<Long, PreferredWriter>(Long.class, PreferredWriter.class);
	
	public static List<PreferredWriter> getPreferedWriters(Client client){
		List<PreferredWriter> writerList = new ArrayList<PreferredWriter>();
		writerList = client.preferredWriter;
		return writerList;
	}
	
		
} 
