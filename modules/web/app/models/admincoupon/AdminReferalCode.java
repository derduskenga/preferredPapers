package models.admincoupon;

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
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@Entity
public class AdminReferalCode extends Model{
    @Id
    public Long id;
    @Constraints.Required(message="Full name is required.")
    public String full_name;
    @Column(nullable=false)
    public String code;
    public Long admin_id;
    //Relationships
    @OneToMany(mappedBy="adminReferalCode")
    public List<AdminReferalEarning> adminReferalEarning;
    
    public static Finder<Long, AdminReferalCode> find = new Finder<Long, AdminReferalCode>(Long.class, AdminReferalCode.class);
    
    public void saveAdminReferalCode(){
	    if(id == null){
		    save();
	    }else{
		    update();
	    }
    }
    
    public AdminReferalCode getFirstAdminCouponCode(Long admin_user_id){
			return find.where().eq("admin_id", admin_user_id).orderBy("admin_id asc").setMaxRows(1).findUnique();
    }
    
    public List<AdminReferalCode> getAdminCouponCodesByAdminId(Long id){
	    return find.where().eq("admin_id", id).findList();
    }
    
    public AdminReferalCode getReferralCode(String code){
		    return find.where().eq("code", code).findUnique();
    }
    
    public void deleteCode(AdminReferalCode admin_code_object){
	    /*Note that you cannot delete an admin code without deleteting its associated earnings:
	    REASON: violation of foreign key constraint*/
	    List<AdminReferalEarning> earnings_list = new ArrayList<AdminReferalEarning>();
	    earnings_list = admin_code_object.adminReferalEarning;
	    if(!earnings_list.isEmpty()){
		    for(AdminReferalEarning earnings:earnings_list){
			    earnings.deleteAdminReferalEarning();
		    }
	    }
	    delete();
    }
    public static JSONArray getAdminReferalCodeArray(){
	  List<AdminReferalCode> codeList = new ArrayList<AdminReferalCode>();
	  codeList =  AdminReferalCode.find.findList();
	  JSONArray jArray = new JSONArray();
	  for(int i = 0; i<codeList.size();i++){
	    JSONObject jObject = new JSONObject();
	    jObject.put("id",codeList.get(i).id);
	    jObject.put("code",codeList.get(i).code);
	    jArray.add(jObject);
	  } 
	  return jArray; 
    }
    
}