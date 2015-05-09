package models.orders;

import java.util.ArrayList;
import java.util.List;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import play.Logger;
import java.util.Collections;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.util.LinkedHashMap;

@Entity
public class Spacing extends Model{
      //fields
      @Id
      public Long id;
      @Constraints.Required(message="Spacing required")
      public String spacing;
      
      @Constraints.Required(message="Alias required")
      public String alias;
      
      @Constraints.Required(message="Price muliplier factor required")
      public int factor;
      
      
      @OneToMany(mappedBy="spacing")
      public List<Orders> orders;
      
      public Spacing(){}
      
      public static Finder<Long, Spacing> find() {
	      return new Finder<Long, Spacing>(Long.class, Spacing.class);
      }
      
      public static Spacing getSpacingObject(Long id){
	return Spacing.find().byId(id);
      }
      
      public static Map<Spacing,Boolean> getSpacingMap(){
	  List<Spacing> spacingList = new ArrayList<Spacing>();
	  spacingList = Spacing.find().findList();
	  //sort the list
	  Collections.sort(spacingList, new SpacingListSort());
	  Map<Spacing,Boolean> spacingMap = new LinkedHashMap<Spacing,Boolean>();
	  for(int i= 0; i<spacingList.size();i++){
	    if(spacingList.get(i).factor == 1){
	      spacingMap.put(spacingList.get(i),true);
	    }else{
	      spacingMap.put(spacingList.get(i),false);
	    }
	  }
	  return spacingMap;
      }
      
      public Map<Spacing,Boolean> getSpacingMapForErrorForm(Long spacing_selected){
	  List<Spacing> spacingList = new ArrayList<Spacing>();
	  spacingList = Spacing.find().findList();
	  //sort the list
	  Collections.sort(spacingList, new SpacingListSort());
	  Map<Spacing,Boolean> spacingMap = new LinkedHashMap<Spacing,Boolean>();
	  for(int i = 0; i<spacingList.size();i++){
	    if(spacingList.get(i).id.equals(spacing_selected)){
	      spacingMap.put(spacingList.get(i),true);
	    }else{
	      spacingMap.put(spacingList.get(i),false);
	    }
	  }
	  return spacingMap;
      }
      
      public static JSONArray getSpacingArray(){
	List<Spacing> spacingList = new ArrayList<Spacing>();
	spacingList = Spacing.find().findList();
	//sort the list
	Collections.sort(spacingList, new SpacingListSort());	
	JSONArray jArray = new JSONArray();
	List<Spacing> slist = new ArrayList<Spacing>();
	slist = spacingList;
	for(int i=0; i<slist.size();i++){
	  JSONObject job = new JSONObject();
	  job.put("id",slist.get(i).id);
	  job.put("spacing",slist.get(i).spacing);
	  job.put("alias",slist.get(i).alias);
	  job.put("factor",slist.get(i).factor);
	  jArray.add(job);
	}
	return jArray;
      }  
}