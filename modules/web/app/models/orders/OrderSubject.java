package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import play.Logger;
import play.Logger.ALogger;

@Entity
public class OrderSubject extends Model{
	@Id
	public Long id;
	@Constraints.Required(message="Order subject is required")
	public String subject_name;
	public String description;
	
	
	//relationship field
	@ManyToMany
	public List<OrderDocumentType> orderDocumentType;
	
	@ManyToOne
	public OrderSubjectCategory orderSubjectCategory;
	
	@OneToMany(mappedBy="orderSubject")
	public List<Orders> orders;
	
	public OrderSubject(){}
	
	public static Finder<Long, OrderSubject> find() {
		return new Finder<Long, OrderSubject>(Long.class, OrderSubject.class);
	}

	public static OrderSubject getSubjectObject(Long id){
	  return OrderSubject.find().byId(id);
	}
	
	public static Map<Map<Long,String>,Boolean> getOrderSubjectsForErrorForm(Long subject_selected,Long document_selected){
	  OrderDocumentType documentObject = OrderDocumentType.find().byId(document_selected);
	  List<OrderSubject> subjectsList = documentObject.orderSubject;	  
	  Map<Map<Long,String>,Boolean> documentSubjects = new LinkedHashMap<Map<Long,String>,Boolean>();
	    for(int i = 0; i< subjectsList.size(); i++){
	      Map<Long,String> innerSubjects = new HashMap<Long,String>();
	      innerSubjects.put(subjectsList.get(i).id,subjectsList.get(i).subject_name);
	      if(subjectsList.get(i).id == subject_selected){
		documentSubjects.put(innerSubjects,true);
	      }else{
		documentSubjects.put(innerSubjects,false);
	      }
	    }
	    return documentSubjects;
	} 
} 
