package models.writer;

import java.util.*;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import models.orders.OrderMessages;
import models.client.PreferredWriter;
import models.orders.Orders;
import models.utility.Utilities;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
@Entity
public class FreelanceWriter extends Model{
  //fields
  @Id
  public Long freelance_writer_id;
  
  public Long writer_id;
  
  @Constraints.Required(message="Full names required.")
  public String full_name;
  
  @Constraints.Required(message="Email required.")
  @Constraints.Email(message="The email address does not seem valid.")
  public String email;
  
  @Constraints.Required(message="Phone number required.")
  @Constraints.Pattern(value = "[0-9]+", message="Phone number must be numeric")
  public String phone_number;
  //relationship fields
  @OneToMany(mappedBy="freelanceWriter")
  public List<PreferredWriter> preferredWriter;
  
  @OneToMany(mappedBy="freelanceWriter")
  public List <Orders> orders;
  
  public static Finder<Long, FreelanceWriter> find = new Finder<Long, FreelanceWriter>(Long.class, FreelanceWriter.class);
  
  public static FreelanceWriter findFreelanceWriterById(Long writer_id){
		return find.where().eq("freelance_writer_id",writer_id).findUnique();
  }
  
  public Long generateRandomLongWriterId(){
	  Integer R = (int) (Math.random() * (Utilities.RANDOM_WRITER_ID_MAX - Utilities.RANDOM_WRITER_ID_MIN)) + Utilities.RANDOM_WRITER_ID_MIN;
	  return R.longValue();
  }
  public void saveFreelanceWriter(){
	  if(freelance_writer_id == null){
		  save();
	  }else{
		  update();
	  }
  }
  
  public static List<FreelanceWriter> getAllWriters(){
	  return find.findList();
  }
  
  public FreelanceWriter getWriterByWriterId(Long writer_id){
	  return find.where().eq("writer_id",writer_id).findUnique();
  }
  
  public static JSONArray getAllWriterCodes(){
	  List<FreelanceWriter> writerIdList = new ArrayList<FreelanceWriter>();
	  writerIdList = FreelanceWriter.find.findList();
	  JSONArray jArray = new JSONArray();
	  
	  for(FreelanceWriter fw:writerIdList){
	    JSONObject jObject = new JSONObject();
	    jObject.put("freelance_writer_id",fw.freelance_writer_id);
	    jObject.put("writer_id",fw.writer_id);
	    jArray.add(jObject);
	  } 
	  return jArray;
  }
  
}