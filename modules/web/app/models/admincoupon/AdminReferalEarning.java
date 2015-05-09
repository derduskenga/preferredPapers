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

@Entity
public class AdminReferalEarning extends Model{
    @Id
    public Long id;
    public double admin_earning_value;//values in US dollars
    public Date date_earned;
    
    //relatiosnhips
    @ManyToOne
    public AdminReferalCode adminReferalCode;
    @OneToOne
    public Orders orders;
    
    public AdminReferalEarning(){}
    
    public static Finder<Long, AdminReferalEarning> find = new Finder<Long, AdminReferalEarning>(Long.class, AdminReferalEarning.class);
    
    public static AdminReferalEarning getEarningByID(Long id){
	    return AdminReferalEarning.find.byId(id);
    }
    
    public void saveAdminReferalEarning(){
	    if(id == null){
		    save();
	    }else{
		    update();
	    }
    }
    
    public void deleteAdminReferalEarning(){
	    delete();
    }
    
    public static double getAdminTotalCodeEarnings(AdminReferalCode adminCode){
	  double total_earnings = 0.00;
	  List<AdminReferalEarning> admin_referal_earning_list = new ArrayList<AdminReferalEarning>();
	  admin_referal_earning_list = adminCode.adminReferalEarning;
	  if(admin_referal_earning_list.isEmpty()){
		  return 0.00;
	  }
	  
	  for(AdminReferalEarning earnings:admin_referal_earning_list){
		    total_earnings = total_earnings + earnings.admin_earning_value;
	  }
	  return total_earnings;
    }
}














