package models.orders;

import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.util.LinkedHashMap;

@Entity
public class OrderLevelOfWriting extends Model{
	@Id
	public Long id;
	
	@Constraints.Required(message="Level of writing is required")
	public String order_level;
	
	@Constraints.Required(message="Level additional price is required")
	//all prices are in US Dollars
	public double additional_price;
	public String description;
	
	@OneToMany(mappedBy="orderLevelOfWriting")
	public List<Orders> orders;
	
	public OrderLevelOfWriting(){}
	
	
	public static Finder<Long, OrderLevelOfWriting> find() {
	  return new Finder<Long, OrderLevelOfWriting>(Long.class, OrderLevelOfWriting.class);
	}
	
	public static OrderLevelOfWriting getLevelObject(Long id){
	  return OrderLevelOfWriting.find().byId(id);
	}
	
	public static Map<Map<Long,String>,Boolean> fetchLevelMap(){
	  List<OrderLevelOfWriting> levelList = OrderLevelOfWriting.find().orderBy("id").findList();
	  Map<Long,String> innerLevelMap = new HashMap<Long,String>();
	  Map<Map<Long,String>,Boolean> outerLevelMap = new HashMap<Map<Long,String>,Boolean>();
	  if(levelList.size()!=0){
	    for(int i=0;i<levelList.size();i++){
	      innerLevelMap.put(levelList.get(i).id,levelList.get(i).order_level);
	    }
	  }
	  outerLevelMap.put(new TreeMap<Long,String>(innerLevelMap),false);
	  return outerLevelMap;
	}
	
	public Map<Map<Long,String>,Boolean> fetchLevelMapForErrorForm(Long levelId){
	  Map<Map<Long,String>,Boolean> outerLevelMap = new LinkedHashMap<Map<Long,String>,Boolean>();
	  List<OrderLevelOfWriting> levelList = OrderLevelOfWriting.find().orderBy("id").findList();
	  for(int i=0;i<levelList.size();i++){
	    Map<Long,String> innerLevelMap = new LinkedHashMap<Long,String>();
	    innerLevelMap.put(levelList.get(i).id,levelList.get(i).order_level);
	    if(levelList.get(i).id.equals(levelId)){
	      outerLevelMap.put(innerLevelMap,true);
	    }else{
	      outerLevelMap.put(innerLevelMap,false);
	    }
	  }
	  return outerLevelMap;
	}
	
	public static JSONArray getLevelOfWriting(){
	  List<OrderLevelOfWriting> levelList = new ArrayList<OrderLevelOfWriting>();
	  levelList = OrderLevelOfWriting.find().orderBy("id").findList();
	  JSONArray jArray = new JSONArray();
	  for(int i=0; i<levelList.size(); i++){
	    JSONObject jobject = new JSONObject();
	    jobject.put("id",levelList.get(i).id);
	    jobject.put("level_of_writing",levelList.get(i).order_level);
	    jobject.put("additional_price",levelList.get(i).additional_price);
	    jArray.add(jobject);
	  }
	  return jArray;
	}
	
	
	
} 
