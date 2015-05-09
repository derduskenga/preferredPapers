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
public class OrderRevision extends Model{
  @Id
  public Long id;
  @Lob
  @Column(nullable = false)
  public String revision_instruction;
  
  @ManyToOne
  public Orders orders;
  
  public void saveOrderRevision(){
    if(this.id == null){
      save();
    }else{
      update();
    }
  }
}