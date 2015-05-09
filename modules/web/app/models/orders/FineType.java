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
public class FineType extends Model{
  @Id
  public Long id;
  @Column(nullable = false)
  public String fine_name;
  public int fine_percentage;//as a percentage of writer order total
  public String fine_description;
  
  //relationship
  @OneToOne(mappedBy="fineType")
  public OrderFines orderFines;
}