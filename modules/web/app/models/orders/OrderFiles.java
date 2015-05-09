package models.orders;
import java.util.*;
import play.data.validation.*;
import javax.persistence.*;
import play.db.ebean.Model;
import models.client.Client;
import models.utility.Utilities;
import play.Logger;
import com.avaje.ebean.*;

@Entity
public class OrderFiles extends Model{
  @Id
  public Long id;
  @Column(nullable = false)
  public FileOwner.OrderFileOwner owner;
  @Column(nullable = false)
  public Long file_size; //in MegaBytes
  @Column(nullable = false)
  public String file_name;
  @Column(nullable = false)
  public FileOwner.OrderFileOwner file_sent_to;
  @Column(nullable = false)
  public String storage_path;
  @Column(nullable = false)
  public String content_type;
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  public Date upload_date;
  public byte[] order_file;
  
  //relationships
  @ManyToOne
  public Orders orders;
  
    //constructor
  public OrderFiles(){}
  
  public void saveOrderFile(){
    if(this.id == null){
      save();
    }else{
      update();
    }
  }
  public static Finder<Long, OrderFiles> find() {
	  return new Finder<Long, OrderFiles>(Long.class, OrderFiles.class);
  }
  
  public static OrderFiles getOrderFileById(Long id){
    return OrderFiles.find().byId(id);
  }
  

}