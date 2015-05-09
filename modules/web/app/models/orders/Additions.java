package models.orders;

import java.util.*;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import play.Logger;

@Entity
public class Additions extends Model{
    //fields
    @Id
    public Long id;
    @Constraints.Required(message="UI label is required")
    public String ui_label;
    @Constraints.Required(message="Additional price is required")
    public double additional_price;
    
    //relationship fields
    @ManyToMany(mappedBy="additions")
    List<Orders> orders;
    
    
     public Additions(){}
      
      public static Finder<Long, Additions> find() {
	return new Finder<Long, Additions>(Long.class, Additions.class);
      }
      
      public static Map<Additions,Boolean> getAdditionsList(){
	Map<Additions,Boolean> additionListMap = new LinkedHashMap<Additions,Boolean>();
	List<Additions> additionList = Additions.find().findList();
	for(int i=0;i<additionList.size();i++){
	  additionListMap.put(additionList.get(i),false);
	}
	return additionListMap;
      }
      
      public static Map<Additions,Boolean> getMapForErrorForm(String []checkedAdditions){
	  Map<Additions,Boolean> sMap = new LinkedHashMap<Additions,Boolean>();
	  List<Additions> additionList = Additions.find().findList();
	
	  List<String> idString = Arrays.asList(checkedAdditions);
	  List<Long> idLong = new ArrayList<Long>();
	  
	  for(String id: idString)
	      idLong.add(Long.valueOf(id));
	      
	  for(Additions addi:additionList)
	      if(idLong.contains(addi.id))
		sMap.put(addi,true);
	      else
		sMap.put(addi,false);
	  return sMap;
      }
      
      public static List<Additions> getListOfAdditionsObjects(String [] checkedAdditions){
	List<Additions> aList = new ArrayList<Additions>();
	for(int i=0; i<checkedAdditions.length;i++){
	  aList.add(Additions.find().byId(Long.valueOf(checkedAdditions[i])));
	}
	return aList;
      }
      
      public static double getTotalAdditions(Orders newOrders){
	  List<Additions> orderAdditions = newOrders.additions;
	  double order_additions = 0.0;
	  if(orderAdditions != null){
	    for(Additions aList:orderAdditions){
	      order_additions = order_additions + aList.additional_price;
	    }
	  }
	  return order_additions*newOrders.number_of_units*newOrders.orderDocumentType.additions_factor;
      }
      
      public static double getTotalAdditionsForAdditionalPages(Orders newOrders){
	  List<Additions> orderAdditions = newOrders.additions;
	  double order_additions = 0.0;
	  if(orderAdditions != null){
	    for(Additions aList:orderAdditions){
	      order_additions = order_additions + aList.additional_price;
	    }
	  }
	  return order_additions*newOrders.additional_pages*newOrders.orderDocumentType.additions_factor;
      }
      
      public static JSONArray getAdditionsArray(){
	JSONArray jArray = new JSONArray();
	List<Additions> additionlist = Additions.find().findList();
	for(int i = 0; i<additionlist.size(); i++){
	  JSONObject jobject = new JSONObject();
	  jobject.put("id",additionlist.get(i).id);
	  jobject.put("ui_label",additionlist.get(i).ui_label);
	  jobject.put("additional_price",additionlist.get(i).additional_price);
	  jArray.add(jobject);
	}
	return jArray;
      }
    
}