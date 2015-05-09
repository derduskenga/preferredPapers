package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;

@Entity
public class OrderSubjectCategory extends Model{
	@Id
	public Long id;
	@Constraints.Required(message="Subject category is required")
	public String subject_category_name;
	@Constraints.Required(message="Additional price to base price is required")
	//Could be Technical or Academic
	//Note that Academic category has an additional price of zero (0 USD)
	// all prices are in dollars
	public double additional_price;
	//Not required but its is good to have it.
	public String description;
	
	//relationship fields
	@OneToMany(mappedBy="orderSubjectCategory")
	List<OrderSubject> orderSubject;
	
} 
