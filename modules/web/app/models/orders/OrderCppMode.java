package models.orders;

import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import play.Logger;
import play.Logger.ALogger;
import models.utility.Utilities;
import java.util.Collections;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@Entity
//Would be pay per page, pay per assignment or pay per question 
public class OrderCppMode extends Model{
	@Id
	public Long id;
	//@Constraints.Required(message="CPP mode is required")
	public OrderCppMode.CppModes order_cpp_mode_name;
	public String cpp_mode_description;
	
	//relationship fields
	@OneToMany(mappedBy="orderCppMode")
	public List<OrderDocumentType> orderDocumentType;
	
	public static Map<Map<Long,String>,Boolean>getUnitsCount(OrderCppMode.CppModes order_cpp_mode){
	  Map<Map<Long,String>,Boolean> outerMap = new LinkedHashMap<Map<Long,String>,Boolean>();
	  //Map<Long,String> innerMap = new HashMap<Long,String>();
	  String selectLabel = "";
	  //A page has a minimum of 280 words
	  switch(order_cpp_mode){
	    case perpage:
		  for(int i=1;i<=Utilities.ORDER_UNITS;i++){
		   Map<Long,String> innerMap = new LinkedHashMap<Long,String>();
		   innerMap.put(Long.valueOf(i),i + " page(s) or " + i*Utilities.PAGE_WORD_COUNT + " words");
		   outerMap.put(innerMap,false);
		  }
		  
		  break;
	    case perassignment:
		  for(int i=1;i<=Utilities.ORDER_UNITS;i++){
		    Map<Long,String> innerMap = new LinkedHashMap<Long,String>();
		    innerMap.put(Long.valueOf(i),i + "");
		    outerMap.put(innerMap,false);
		  }
		  
		  break;		  
	    case perquestion:
		  for(int i=1;i<=Utilities.ORDER_UNITS;i++){
		    Map<Long,String> innerMap = new LinkedHashMap<Long,String>();
		    innerMap.put(Long.valueOf(i),i + "");
		    outerMap.put(innerMap,false);
		  }
		  
		  break;
	    default:
	      break;
	  }
	  return outerMap;  
	}
	
	public Map<Map<Long,String>,Boolean> getNumberOfUnitsMapForErrorForm(Long spacing, int units, Long documentId){
	  OrderCppMode.CppModes order_cpp_mode = OrderDocumentType.find().byId(documentId).orderCppMode.order_cpp_mode_name;
	  int spacing_factor = Spacing.find().byId(spacing).factor;
	  Map<Map<Long,String>,Boolean> outerMap = new LinkedHashMap<Map<Long,String>,Boolean>();
	  //Map<Long,String> innerMap = new HashMap<Long,String>();
	  //A page has a minimum of 280 words
	  switch(order_cpp_mode){
	    case perpage:
		  for(int i=1;i<=Utilities.ORDER_UNITS;i++){
		    Map<Long,String> innerMap = new LinkedHashMap<Long,String>();
		    innerMap.put(Long.valueOf(i),i + " page(s) or " + i*Utilities.PAGE_WORD_COUNT*spacing_factor + " words");    
		    if(i == units){
		      outerMap.put(innerMap,true);
		    }else{
		      outerMap.put(innerMap,false);
		    }		   
		  }
		  
		  break;
	    case perassignment:
		  for(int i=1;i<=Utilities.ORDER_UNITS;i++){
		    Map<Long,String> innerMap = new LinkedHashMap<Long,String>();
		    innerMap.put(Long.valueOf(i),i + "");
		    if(i == units){
		      outerMap.put(innerMap,true);
		    }else{
		      outerMap.put(innerMap,false);
		    }	      
		  }  
		  break;
	    case perquestion:
		  for(int i=1;i<=Utilities.ORDER_UNITS;i++){
		    Map<Long,String> innerMap = new LinkedHashMap<Long,String>();
		    innerMap.put(Long.valueOf(i),i + "");
		    if(i == units){
		      outerMap.put(innerMap,true);
		    }else{
		      outerMap.put(innerMap,false);
		    }    
		  }  
		  break;
	    default:
	      break;
	  }
	  return outerMap;
	}
	
	
	public static JSONArray getOrderUnits(OrderCppMode.CppModes order_cpp_mode){
	  JSONArray jArray = new JSONArray();
	  switch(order_cpp_mode){
	    case perpage:
		  for(int i=1;i<=Utilities.ORDER_UNITS;i++){
		    JSONObject jObject = new JSONObject();
		    jObject.put("value",i);
		    jObject.put("text",i + " page(s) or " + i*Utilities.PAGE_WORD_COUNT + " words");
		    jObject.put("label","Order page/word count");
		    jArray.add(jObject);
		  }
		  break;
	    case perassignment:
		  for(int i=1;i<=Utilities.ORDER_UNITS;i++){
		    JSONObject jObject = new JSONObject();
		    jObject.put("value",i);
		    jObject.put("text",i);
		    jObject.put("label","Number of assignments");  
		    jArray.add(jObject);
		  }
		  break;  
	    case perquestion:
		  for(int i=1;i<=Utilities.ORDER_UNITS;i++){
		    JSONObject jObject = new JSONObject();
		    jObject.put("value",i);
		    jObject.put("text",i);
		    jObject.put("label","Number of questions");
		    jArray.add(jObject);
		  }
		  break;
	    default:
	      //do nothing, the empty map wil be returned
	      break;
	  }
	  return jArray;
	}
	
	public static Map<Long,String> orderMap(Map<Long,String> lsMap){
	  Map<Long, String> newMap = new TreeMap(Collections.reverseOrder());
	  newMap.putAll(lsMap);
	  return lsMap;
	}
	
	public enum CppModes{
	  perpage,perassignment,perquestion
	}
} 
