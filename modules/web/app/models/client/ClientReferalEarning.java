package models.client;

import java.util.*;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Entity;
import javax.persistence.Id;
import play.db.ebean.Model;
import play.Logger;
import models.orders.Orders;

@Entity
public class ClientReferalEarning extends Model{ 
    @Id
    public Long id;
    @Column(nullable=false)
    public double referal_earning_value;//values in US dollars
    public Date date_earned;
    
    //Model relationship
    @ManyToOne
    public ReferralCode referralCode;
    @OneToOne
    public Orders orders;
    
    //constructor
    public ClientReferalEarning(){}
    
    public static Finder<Long, ClientReferalEarning> find() {
	    return new Finder<Long, ClientReferalEarning>(Long.class, ClientReferalEarning.class);
    }
    
    public void saveclientReferalEarning(){
	    if(id == null){
		    date_earned = new Date();
		    save();
	    }else{
		    update();
	    }
    }
    
    public static ClientReferalEarning getEarningByID(Long id){
	   return ClientReferalEarning.find().byId(id);
    }
    
    public static double getClientReferalEarning(String email){
	  double total_earning = 0.00;
	  Client client = Client.getClient(email);
	  ReferralCode referal_code = client.referralCode;
	  if(referal_code == null){
		  return 0.00;
	  }
	  //get a list of earning using the referal_code
	  List<ClientReferalEarning> clientReferalEarningList = new ArrayList<ClientReferalEarning>();
	  clientReferalEarningList = referal_code.clientReferalEarning;
	  if(clientReferalEarningList.isEmpty()){
		 return 0.00;
	  }
	  for(ClientReferalEarning ace: clientReferalEarningList){
		 total_earning = total_earning + ace.referal_earning_value;
	  }
	  return total_earning;
    }
    
}