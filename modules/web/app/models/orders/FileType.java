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
public class FileType{

  @Id
  public Long id;
  public FileType.ProductFileType product_file_type;
  public String description;
  public String test;
  //relationship
  //Constructor
  public FileType(){}
  
  
  public enum ProductFileType{
    //1,2,3,4,5
    DRAFT,PRODUCT,REVISION,REFERENCE_MATERIAL,ADDITIONAL_FILE,OTHER
  }
}