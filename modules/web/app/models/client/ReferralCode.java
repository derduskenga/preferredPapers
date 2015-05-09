package models.client;

import java.util.*;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.persistence.Id;
import play.db.ebean.Model;
import play.Logger;
import models.orders.Orders;
import java.util.Random;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import models.admincoupon.*;

@Entity
public class ReferralCode extends Model{ 
	@Id
	public Long referral_code_id;
	//fields
	@Column(nullable=false)
	public String code;
	
	@Column(nullable=false)
	public Date date_generated;
	
	@OneToMany(mappedBy="referralCode")
	public List<ClientReferalEarning> clientReferalEarning;
	
	@Column(nullable=false)
	@OneToOne
	public Client client;
	
	public ReferralCode(){
	
	}
	
	public ReferralCode(String code){
	  this.code = code;
	}

	public void saveReferralCode(){
		if(referral_code_id == null){
			date_generated = new Date();
			save();

		}else{
			update();

		}
	}
	
	public static Finder<Long, ReferralCode> find = new Finder<Long, ReferralCode>(Long.class, ReferralCode.class);
	
	public ReferralCode getReferralCode(String code){
			return find.where().eq("code", code).findUnique();
	}
	
	//generate a random code
	public String generateString(Random rng, int length){
			char[] text = new char[length];
			for (int i = 0; i < length; i++)
			{
				text[i] = alphabet.charAt(rng.nextInt(alphabet.length()));
			}
			String uniq_code = "tmly"+ new String(text);
			ReferralCode rc = getReferralCode(uniq_code);
			if(rc!=null){
				generateString(rng, 5);
			}
			return uniq_code;
	}	
	
	@Transient
	public String alphabet    = "abcdefghijklmnopqrstuvwxyz";
	
	public static JSONArray getReferralCodeArray(){
	  List<ReferralCode> codeList = new ArrayList<ReferralCode>();
	  codeList =  ReferralCode.find.findList();
	  JSONArray jArray = new JSONArray();
	  for(int i = 0; i<codeList.size();i++){
	    JSONObject jObject = new JSONObject();
	    jObject.put("id",codeList.get(i).referral_code_id);
	    jObject.put("code",codeList.get(i).code);
	    jArray.add(jObject);
	  } 
	  return jArray;  
	}
	
	public String determineTypeOfCode(Orders orders){
		/*This function should only be called once you are sure that a coupon code is corect*/
		boolean found = false;
								      
		List<ReferralCode> clientCodeList = new ArrayList<ReferralCode>();
		clientCodeList =  ReferralCode.find.findList();
		
		List<AdminReferalCode> adminCodeList = new ArrayList<AdminReferalCode>();
		adminCodeList =  AdminReferalCode.find.findList();
		
		if(!clientCodeList.isEmpty()){
			for(ReferralCode referralCode:clientCodeList){
				if(orders.coupon_code.equals(referralCode.code)){
					found = true;
				}
			}
		}
		
		if(found){
			Logger.info("client code");
			return "CLIENT";
		}
		Logger.info("ADMIN code");
		return "ADMIN";

	}
} 
