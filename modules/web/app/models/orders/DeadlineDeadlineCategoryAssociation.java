package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;

@Entity
public class DeadlineDeadlineCategoryAssociation extends Model{

    //fields
    @Id
    public Long id;
    @ManyToOne
    public OrderDeadlines orderDeadlines;
    @ManyToOne
    public OrderDeadlineCategory orderDeadlineCategory;
    public double additional_price;
    
    public static Finder<Long, DeadlineDeadlineCategoryAssociation> find() {
	  return new Finder<Long, DeadlineDeadlineCategoryAssociation>(Long.class, DeadlineDeadlineCategoryAssociation.class);
    }
    
    public List<DeadlineDeadlineCategoryAssociation> getThisList(Long id){
      return DeadlineDeadlineCategoryAssociation.find().where().eq("order_deadline_category_id",id).findList();
    }
}