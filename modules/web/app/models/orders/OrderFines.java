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
public class OrderFines extends Model{
  @Id
  public Long id;
  public Date fine_date;
  public double amount = 0.0;
  public boolean removed = false;
  
  //relationship
  @OneToOne
  public FineType fineType;
  @ManyToOne
  public Orders orders;
}