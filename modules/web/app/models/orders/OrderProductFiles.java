package models.orders;
import java.util.*;
import play.data.validation.*;
import javax.persistence.*;
import play.db.ebean.Model;
import models.client.Client;
import models.utility.Utilities;
import play.Logger;

@Entity
public class OrderProductFiles extends Model{
  @Id
  public Long id;
  @Column(nullable = false)
  public FileOwner.OrderFileOwner owner;
  @Column(nullable = false)
  public Long file_size; 
  @Column(nullable = false)
  public String file_name;
  @Column(nullable = false)
  public FileOwner.OrderFileOwner file_sent_to;
  public String storage_path;
  @Column(nullable = false)
  public String content_type;
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  public Date upload_date;
  public Date download_date;
  public FileType.ProductFileType product_file_type;
  //@Lob
  //public String revision_instructions;
  public boolean has_been_downloaded = false;
  @Column(nullable = false)
  public int plagiarism;//expressed as a percontage
  
  //relationship
  @ManyToOne
  public Orders orders;
  //constructor
  public OrderProductFiles(){}
  
  public static Finder<Long, OrderProductFiles> find() {
    return new Finder<Long, OrderProductFiles>(Long.class, OrderProductFiles.class);
  }
  
  public static OrderProductFiles getOrderProductFiles(Long file_id){
    return OrderProductFiles.find().byId(file_id);
  }
  
  public void saveProductFile(){
    if(this.id == null){
      save();
    }else{
      update();
    }
  }

}