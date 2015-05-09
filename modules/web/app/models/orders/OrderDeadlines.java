package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import play.Logger;
import org.json.simple.*;
//import org.json.simple.JSONArray;
import java.util.Collections;
import java.util.Comparator;

@Entity
//These are real order durations with their time equivalent in seconds
public class OrderDeadlines extends Model{
	@Id
	public Long id;
	//this is the value of units
	@Constraints.Required(message="Value required")
	public int deadline_value;
	//unit of time measure
	@Constraints.Required(message="Unit required Hrs, days or months")
	public String deadline_unit;
	//this is the standard value of duration in seconds
	@Constraints.Required(message="Elapsed time in seconds is required")
	public Long seconds_elapsing_to_deadline; 
	
	//note that value with unit will give the select label in the interface.
	
	//relationship fields
	@OneToMany(mappedBy="orderDeadlines")
	public List<DeadlineDeadlineCategoryAssociation> deadlineDeadlineCategoryAssociation;
	
	public OrderDeadlines(){}
	
	public static Finder<Long, OrderDeadlines> find() {
	  return new Finder<Long, OrderDeadlines>(Long.class, OrderDeadlines.class);
	}
	
	public static OrderDeadlines getOrderDeadlinesByValue(Long dValue){
	  return OrderDeadlines.find().where().eq("seconds_elapsing_to_deadline",dValue).findUnique();
	}
	
	public static Map<Map<Long,String>,Boolean> getDeadlines(OrderDeadlineCategory orderDeadlineCategory){
	  Logger.info("in getDeadlines " + orderDeadlineCategory.order_deadline_category_name);
	  Map<Long,String> documentDeadlinesMap = new TreeMap<Long, String>(java.util.Collections.reverseOrder());
	  List<DeadlineDeadlineCategoryAssociation> deadline_deadline_category_association_list = orderDeadlineCategory.deadlineDeadlineCategoryAssociation;
	  for(int i = 0; i < deadline_deadline_category_association_list.size(); i++){
	    //elapsedseconds
	    documentDeadlinesMap.put(deadline_deadline_category_association_list.get(i).orderDeadlines.seconds_elapsing_to_deadline,
				      String.valueOf(deadline_deadline_category_association_list.get(i).orderDeadlines.deadline_value) + " " +
				      deadline_deadline_category_association_list.get(i).orderDeadlines.deadline_unit);
				      //Logger.info("counting");
	  }
	  Map<Map<Long,String>,Boolean> documentDeadlinesMapOuter =  new HashMap<Map<Long,String>,Boolean>();
	  documentDeadlinesMapOuter.put(documentDeadlinesMap,false);	 
	  return documentDeadlinesMapOuter;	  
	}
	
	public static  Map<Map<Long,String>,Boolean> getDocumentDeadlinesForErrorForm(Long selected_deadline, Long document_id){
	  Map<Map<Long,String>,Boolean> dMap = new HashMap<Map<Long,String>,Boolean>();
	  //OrderDocumentType orderDocument = 
	  //Logger.info("Doc name is " + orderDocument.docugetDocumentDeadlinesForErrorFormment_type_name);
	  OrderDeadlineCategory orderDeadlineCategory = OrderDocumentType.find().byId(document_id).orderDeadlineCategory;
	  Long categoryId = orderDeadlineCategory.id;
	  
	  DeadlineDeadlineCategoryAssociation assObj = new DeadlineDeadlineCategoryAssociation();
	  
	  List<DeadlineDeadlineCategoryAssociation> deadline_deadline_category_association_list = assObj.getThisList(categoryId);
	  
	  Collections.sort(deadline_deadline_category_association_list, new DeadlineDeadlineCategoryAssociationListSort());
	  
	  Logger.info("deadline is " + selected_deadline);
	  for(int i = 0; i < deadline_deadline_category_association_list.size(); i++){
	    Map<Long,String> deadlineMap = new HashMap<Long, String>();
	    deadlineMap.put(deadline_deadline_category_association_list.get(i).orderDeadlines.seconds_elapsing_to_deadline,
				      String.valueOf(deadline_deadline_category_association_list.get(i).orderDeadlines.deadline_value) + " " +
				      deadline_deadline_category_association_list.get(i).orderDeadlines.deadline_unit);
	   
	   if(deadline_deadline_category_association_list.get(i).orderDeadlines.seconds_elapsing_to_deadline.equals(selected_deadline)){
	      dMap.put(OrderCppMode.orderMap(deadlineMap),true); 
	    }else{
	      dMap.put(OrderCppMode.orderMap(deadlineMap),false);
	    }	
	    Logger.info("other deadlines are " + deadline_deadline_category_association_list.get(i).orderDeadlines.seconds_elapsing_to_deadline);
	  }
	  return dMap;
	}
	
	public static JSONArray getDeadlinesArray(OrderDeadlineCategory orderDeadlineCategory){
	  //what i need is id,time in seconds,additional price, deadline labels
	  Logger.info("in getDeadlines " + orderDeadlineCategory.order_deadline_category_name);
	 
	  JSONArray jArray = new JSONArray();
	  List <DeadlineDeadlineCategoryAssociation> deadline_deadline_category_association_list = new ArrayList<DeadlineDeadlineCategoryAssociation>();
	  deadline_deadline_category_association_list = orderDeadlineCategory.deadlineDeadlineCategoryAssociation;
	  
	  if(deadline_deadline_category_association_list!=null){
	    for(int i = 0; i < deadline_deadline_category_association_list.size(); i++){
	      JSONObject jObj = new JSONObject();
	      jObj.put("id",deadline_deadline_category_association_list.get(i).orderDeadlines.id);
	      Logger.info("testing: " + deadline_deadline_category_association_list.get(i).orderDeadlines.deadline_value);
	      jObj.put("time_in_seconds",deadline_deadline_category_association_list.get(i).orderDeadlines.seconds_elapsing_to_deadline);
	      jObj.put("additional_price",deadline_deadline_category_association_list.get(i).additional_price);
	      jObj.put("deadline_labels",deadline_deadline_category_association_list.get(i).orderDeadlines.deadline_value + " " + deadline_deadline_category_association_list.get(i).orderDeadlines.deadline_unit);
	      jArray.add(jObj);
	    }
	  }
	  //sort the array descending
	   return sortArrray(jArray);
	}
	public static JSONArray sortArrray(JSONArray jsonArr){				
	  JSONArray sortedJsonArray = new JSONArray();
	  List<JSONObject> jsonValues = new ArrayList<JSONObject>();
	  for (int i = 0; i < jsonArr.size(); i++) {
	      jsonValues.add((JSONObject)jsonArr.get(i));
	  }
	  Collections.sort( jsonValues, new Comparator<JSONObject>() {
	      //You can change "Name" with "ID" if you want to sort by ID
	      private static final String KEY_NAME = "time_in_seconds";

	      @Override
	      public int compare(JSONObject a, JSONObject b) {
		  Long valA = 1L;
		  Long valB = 1L;				
		  valA = (Long) a.get(KEY_NAME);
		  valB = (Long) b.get(KEY_NAME);
		  return -valA.compareTo(valB);
	      }
	  });

	  for (int i = 0; i < jsonArr.size(); i++) {
	      sortedJsonArray.add(jsonValues.get(i));
	  }
	  return sortedJsonArray;
	}

} 
