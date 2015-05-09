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
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@Entity
public class OrderDocumentType extends Model{
	@Id
	public Long id;
	@Constraints.Required(message="Document type is required")
	public String document_type_name;
	//all prices are in dollars
	public double base_price;
	public String description;
	public double additions_factor;
	
	//relationship fields
	@OneToMany(mappedBy="orderDocumentType")
	public List<Orders> orders;
	
	@ManyToMany(mappedBy="orderDocumentType")
	public List<OrderSubject> orderSubject;
	
	@ManyToOne
	public OrderDeadlineCategory orderDeadlineCategory;
	
	@ManyToOne
	public OrderCppMode orderCppMode;
	
	//default constructor
	public OrderDocumentType(){}
	
	 
	public static Finder<Long, OrderDocumentType> find() {
	  return new Finder<Long, OrderDocumentType>(Long.class, OrderDocumentType.class);
	}
	
	public static OrderDocumentType getDocumentObject(Long id){
	  return OrderDocumentType.find().byId(id);
	}
	
	public static Map<Map<Long,String>,Boolean> fetchDocumentMap(){	  
	  List<OrderDocumentType> documentList = OrderDocumentType.find().orderBy("document_type_name").findList();	
	  if(documentList.size()!=0){
	    Map<Long,String> innerMap = new LinkedHashMap<Long,String>();  
	    for(int i = 0; i < documentList.size(); i++){
	      innerMap.put(documentList.get(i).id,documentList.get(i).document_type_name);
	      //Logger.info("here " + documentList.get(i).id);
	    }
	    
	    //get subject by documennt
	    Map<Map<Long,String>,Boolean> subjects = new LinkedHashMap<Map<Long,String>,Boolean>();
	    List<OrderSubject> subjectsForDocument = documentList.get(0).orderSubject;
	    if(subjectsForDocument.size() > 0){
	      for(int j = 0 ; j<subjectsForDocument.size(); j++){
		Map<Long,String>subjectByDocumentMapInnerMap = new LinkedHashMap<Long,String>();
		subjectByDocumentMapInnerMap.put(subjectsForDocument.get(j).id, subjectsForDocument.get(j).subject_name);
		subjects.put(subjectByDocumentMapInnerMap,false);
	      }	     
	    }
	    
	    //Logger.info("size at sending" + subjectByDocumentMapInnerMap.size());
	    TempStore.setDocumentSubjects(subjects);
	    //sort the list
	    Collections.sort(documentList, new ListSort());
	    
	    //get dealines for document 
	    Map<Map<Long,String>,Boolean> documentDeadlineMap = new LinkedHashMap<Map<Long,String>,Boolean>();
	    documentDeadlineMap = OrderDeadlines.getDeadlines(documentList.get(0).orderDeadlineCategory);
	    TempStore.setDocumentDeadlines(documentDeadlineMap);
	    
	    //getcpp mode
	    OrderCppMode.CppModes order_cpp_mode = documentList.get(0).orderCppMode.order_cpp_mode_name;
	    Map<Map<Long,String>,Boolean> numberOfUnitsMap = OrderCppMode.getUnitsCount(order_cpp_mode);
	    //store this map to TempStore so as to retrieve later
	    TempStore.setnumberOfUnitsMap(numberOfUnitsMap);
	    
	    Map<Map<Long,String>,Boolean> documentMap = new HashMap<Map<Long,String>,Boolean>();	
	    //innerMap is sorted by use of TreeMap the added to the documentMap
	    documentMap.put(new TreeMap<Long,String>(innerMap),false);
	    return documentMap; 
	    
	  }else{
	    TempStore.setDocumentSubjects(new TreeMap<Map<Long,String>,Boolean>(new HashMap<Map<Long,String>,Boolean>()));
	    TempStore.setDocumentDeadlines(new HashMap<Map<Long,String>,Boolean>());
	    TempStore.setnumberOfUnitsMap(new  HashMap<Map<Long,String>,Boolean>());
	    
	    return new HashMap<Map<Long,String>,Boolean>(); 
	  }
	}
	
	
	public static Map<Map<Long,String>,Boolean> fetchDocumentMapForErrorForm(Long document_selected){
	  List<OrderDocumentType> documentList = OrderDocumentType.find().orderBy("document_type_name").findList(); 
	  Collections.sort(documentList, new ListSort());
	  Map<Map<Long,String>,Boolean> documentMap = new LinkedHashMap<Map<Long,String>,Boolean>();
	  //innerMap is sorted by use of TreeMap the added to the documentMap   
	  if(documentList.size()!=0){ 
	    for(int i = 0; i < documentList.size(); i++){
	      Map<Long,String> innerMap = new LinkedHashMap<Long,String>(); 
	      innerMap.put(documentList.get(i).id,documentList.get(i).document_type_name);
	      if(documentList.get(i).id == document_selected){
		documentMap.put((innerMap),true);
	      }else{ 
		documentMap.put((innerMap),false);
	      }
	    }
	     }
	     return documentMap; 
	}
	
	public static JSONObject getDocumentById(Long docId){
	  JSONObject finalJsonObject = new JSONObject();
	  OrderDocumentType odt= OrderDocumentType.find().byId(docId);//where().eq("id", docId).findList();
	  //Logger.info("id passed is: " + docId);
	 //Logger.info("id sent: " + odt.orderDeadlineCategory.id);
	  //add base_price
	  //subjects object
	  JSONArray jArray = new JSONArray();
	  List<OrderSubject> orderSubList = odt.orderSubject;
	  if(orderSubList.size()>0){ 
	     for(int i=0;i<orderSubList.size();i++){
	      JSONObject subjectOJObject = new JSONObject();
	      subjectOJObject.put("id",orderSubList.get(i).id);
	      subjectOJObject.put("subject_name",orderSubList.get(i).subject_name);
	      subjectOJObject.put("additional_price",orderSubList.get(i).orderSubjectCategory.additional_price);
	      jArray.add(subjectOJObject);
	     }
	  }	 
	  //getDeadlines
	  //what i need is id,time in seconds,additional price, deadline labels
	  JSONArray deadlineJArray = new JSONArray();
	  finalJsonObject.put("id",odt.id);
	  finalJsonObject.put("additions_factor",odt.additions_factor);
	  finalJsonObject.put("base_price",odt.base_price);
	  finalJsonObject.put("deadline_category",odt.orderDeadlineCategory.order_deadline_category_name);
	  finalJsonObject.put("count_units",OrderCppMode.getOrderUnits(odt.orderCppMode.order_cpp_mode_name));
	  finalJsonObject.put("document_subjects",jArray);
	  finalJsonObject.put("document_deadlines",OrderDeadlines.getDeadlinesArray(odt.orderDeadlineCategory));
	  finalJsonObject.put("cpp_mode",odt.orderCppMode.order_cpp_mode_name.toString());
	  return finalJsonObject;
	}
} 
