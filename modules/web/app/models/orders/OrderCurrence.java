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
import java.util.Collections;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@Entity
public class OrderCurrence extends Model{
	@Id
	public Long order_currency_id;
	
	@Constraints.Required(message="Currence name is required")
	public String currency_name;
	
	@Constraints.Required(message="Currence symbol is required")
	public String currency_symbol;
	
	@Constraints.Required(message="Currence symbol is required")
	public String currency_symbol_2;
	
	@Constraints.Required(message="Currence convertion rate is required")
	public double convertion_rate;
	
	
	//relationship fields
	@OneToMany(mappedBy="orderCurrence")
	List<Orders> orders;
	
	public OrderCurrence(){}
	
	public static Finder<Long, OrderCurrence> find() {
	  return new Finder<Long, OrderCurrence>(Long.class, OrderCurrence.class);
	}
	
	public static OrderCurrence getCurrencyObject(Long id){
	  return OrderCurrence.find().byId(id);
	}
	
	public static Map<OrderCurrence,Boolean> getCurrenceList(){
	  Map<OrderCurrence,Boolean> currenceListMap = new LinkedHashMap<OrderCurrence,Boolean>();
	  List<OrderCurrence> currenceList = new ArrayList<OrderCurrence>();
	  currenceList = OrderCurrence.find().findList();
	  //sort the list
	  Collections.sort(currenceList, new CurrenceListSort());  
	  
	  for(int i=0; i<currenceList.size();i++){
	    if(currenceList.get(i).convertion_rate == 1){
	      currenceListMap.put(currenceList.get(i),true);
	    }else{
	      currenceListMap.put(currenceList.get(i),false);
	    }
	  }  
	  return currenceListMap;
	} 
	
	 public Map<OrderCurrence,Boolean> getCurrencyMapForErrorForm(Long currency_selected){
	  Map<OrderCurrence,Boolean> cMap = new LinkedHashMap<OrderCurrence,Boolean>();
	  List<OrderCurrence> currenceList = new ArrayList<OrderCurrence>();
	  currenceList =  OrderCurrence.find().findList();
	  for(int i=0;i<currenceList.size();i++){
	    if(currenceList.get(i).order_currency_id.equals(currency_selected)){
	      cMap.put(currenceList.get(i),true);
	    }else{
	      cMap.put(currenceList.get(i),false);
	    }
	  }
	  return cMap;
	 }
	
	public static JSONArray getCurrencyArray(){
	  List<OrderCurrence> currenceList = new ArrayList<OrderCurrence>();
	  currenceList =  OrderCurrence.find().findList();
	  JSONArray jArray = new JSONArray();
	  for(int i = 0; i<currenceList.size();i++){
	    JSONObject jObject = new JSONObject();
	    jObject.put("id",currenceList.get(i).order_currency_id);
	    jObject.put("currency_name",currenceList.get(i).currency_name);
	    jObject.put("currency_symbol",currenceList.get(i).currency_symbol);
	    jObject.put("currency_symbol_2",currenceList.get(i).currency_symbol_2);
	    jObject.put("convertion_rate",currenceList.get(i).convertion_rate);
	    jArray.add(jObject);
	  } 
	  return jArray;  
	}
	
	
} 
