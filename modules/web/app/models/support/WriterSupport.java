package models.support;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import models.orders.OrderMessages;



@Entity
public class WriterSupport extends Model{

    //fields
    @Id
    public Long id;
    public String f_name;
    
    
    
}