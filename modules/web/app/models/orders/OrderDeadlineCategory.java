package models.orders;
import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import java.util.Map;
import java.util.HashMap;

@Entity
//Would be;
//1. Normal ranging from 3 hrs to 10 days
//2. For programming ranging from 2 days to 20 days
//3. For Thesis or dissertation ranging from 12 hrs to 2 months
public class OrderDeadlineCategory extends Model{
	@Id
	public Long id;
	//this is the label for 
	@Constraints.Required(message="Deadline label is required")
	public String order_deadline_category_name;
	public String order_deadline_category_description; 
	
	//relationship fields
	@OneToMany(mappedBy="orderDeadlineCategory")
	public List<OrderDocumentType> OrderDocumentType;
	
	@OneToMany(mappedBy="orderDeadlineCategory")
	public List<DeadlineDeadlineCategoryAssociation> deadlineDeadlineCategoryAssociation;
	
	
	
	
} 
