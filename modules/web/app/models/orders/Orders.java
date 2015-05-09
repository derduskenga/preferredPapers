package models.orders;
import java.util.*;
import play.data.validation.*;
import javax.persistence.*;
import play.db.ebean.Model;
import models.client.*;
import models.utility.Utilities;
import play.Logger;
import com.avaje.ebean.*;
import java.text.*;
import models.admincoupon.*;
import models.writer.FreelanceWriter;


@Entity
public class Orders extends Model{
	//fields
	@Id
	public Long order_id;
	public Long order_code;
	@Constraints.Required(message="Urgency required")
	public int document_deadline;//this is the period given to do the order; it is given in seconds
	@Constraints.Required(message="Order topic required")
	public String topic;
	@Lob
	@Constraints.Required(message="Order description required")
	public String order_instruction; 
	@Constraints.Required(message="Number of assignments/pages/questions required")
	public int number_of_units;// this is the number of pages of order, assingments of questions
	@Constraints.Required(message="Style of writing required")
	public String writing_style;
	@Constraints.Required(message="Number of sources required")
	public int no_of_references;
	public String operating_system;
	public String prog_language;
	public String database;
	public String prefered_writer;//if this is a returning customer
	@Temporal(TemporalType.TIMESTAMP)
	public Date order_date;
	@Temporal(TemporalType.TIMESTAMP)
	public Date order_deadline;
	
	public double order_total;//This is given in USD
	public double amount_paid = 0.0; /*This is shown in USD: value returned from the gateway*/
	public boolean is_paid = false;
	
	public boolean is_writer_assigned = false;//is being worked on 
	public boolean is_complete = false;
	public boolean is_closed = false;
	public boolean on_revision = false;
	public int client_feedback;
	public int additional_pages = 0;
	
	public double additional_pages_value = 0.0;
	public boolean is_additional_pages_paid = false;
	
	public String source_domain;
	public String invoice_id;
	public boolean approved = false;
	public String coupon_code;
	
	public double prefered_writer_value = 0.0;
	public boolean prefered_writer_value_paid = false;
	//Order Files and types of files (e.g for revision, additional files, reference materials, order product,draft)
	//Order fines is an entity(id,date,amount,reason,removed)
	//order revisions (id,revision instructions)
	//fine categories (plagiarism, low quality, late reassignment, late submission, dissapper from order, late revision submission)
	
	//relationship fields
	//@Valid
	@ManyToOne
	public Client client;
	
	//@Valid
	@ManyToOne
	public OrderLevelOfWriting orderLevelOfWriting;
	
	//@Valid
	@ManyToOne
	public OrderDocumentType orderDocumentType;
	@ManyToOne
	public OrderCurrence orderCurrence;
	@OneToMany(mappedBy="orders")
	public List<OrderMessages> orderMessages;
	@ManyToOne
	public Spacing spacing;
	@ManyToMany
	public List<Additions> additions;
	@ManyToOne
	public OrderSubject orderSubject;
	@OneToMany(mappedBy="orders")
	public List<OrderFiles> orderFiles;
	@OneToMany(mappedBy="orders")
	public List<OrderProductFiles> orderProductFiles;	
	@OneToMany(mappedBy="orders")
	public List<OrderFines> orderFines;
	@OneToMany(mappedBy="orders")
	public List<OrderRevision> orderRevision;
	@OneToOne(mappedBy="orders")
	public ClientReferalEarning clientReferalEarning; 
	@OneToOne(mappedBy="orders")
	public AdminReferalEarning adminReferalEarning;
	@ManyToOne
	public FreelanceWriter freelanceWriter;
	
	@ManyToOne
	public ReferralCode referralCode;
	
	@PrePersist
	protected void onCreate(){
	  order_date = new Date();
	}
	
	public static Finder<Long, Orders> find() {
		return new Finder<Long, Orders>(Long.class, Orders.class);
	}
	
	public Long saveOrder(){
	  if(this.order_id == null){
	    save();
	    return order_id;
	  }else{
	    this.order_code = order_id + Utilities.ORDER_CODE_CONSTANT;
	    update();
	    return order_id;
	  }
	}
	
	public static Orders getOrderById(Long id){
	  return Orders.find().byId(id);
	} 
	public static Orders getOrderByCode(Long order_code){
	  return Orders.find().where().eq("order_code",order_code).findUnique();
	}

	public List<Orders> getActiveOrders(Long client_id){
	  return Orders.find().where().eq("is_complete",false).eq("client_id",client_id).orderBy("order_deadline asc").findList();
	}
	
	public  List<Orders> getCompletedOrders(Long client_id){
	  return Orders.find().where().eq("is_complete",true).eq("is_closed",false).eq("client_id",client_id).orderBy("order_deadline asc").findList();
	}
	public List<Orders> getClosedOrders(Long client_id){
	  return Orders.find().where().eq("is_closed",true).eq("client_id",client_id).orderBy("order_deadline asc").findList();
	}
	
	public Map<Orders,Integer> activeOrdersUnreadMessages( List<Orders> ordersList){
	  Map<Orders,Integer> ordersMap = new LinkedHashMap<Orders,Integer>();
	  if(ordersList.isEmpty()){
	    return null;
	  }
	  for(Orders order: ordersList){
	    List<OrderMessages> messagesList = order.orderMessages;
	    order = orderClientLocalTime(order);
	    int i = 0;
	    for(OrderMessages messages: messagesList){
	      if(!messages.status && messages.msg_to == MessageParticipants.CLIENT){
		i = i+1;//count of unread messages
	      }
	    }
	    ordersMap.put(order,i);
	  }
	  return ordersMap;
	}
	
	public Map<Orders,Integer> completeOrdersUnreadMessages(List<Orders> ordersList){
	  Map<Orders,Integer> ordersMap = new LinkedHashMap<Orders,Integer>();
	  if(ordersList.isEmpty()){
	    return null;
	  }
	  for(Orders order: ordersList){
	    List<OrderMessages> messagesList = order.orderMessages;
	    order = orderClientLocalTime(order);
	    int i = 0;
	    for(OrderMessages messages: messagesList){
	      if(!messages.status && messages.msg_to == MessageParticipants.CLIENT){
		i = i+1;//count of unread messages
	      }
	    }
	    ordersMap.put(order,i);
	  }
	  return ordersMap;
	}
	
	public Map<Orders,Integer> closedOrdersUnreadMessages( List<Orders> ordersList){
	  Map<Orders,Integer> ordersMap = new LinkedHashMap<Orders,Integer>();
	  if(ordersList.isEmpty()){
	    return null;
	  }
	  for(Orders order: ordersList){
	    List<OrderMessages> messagesList = order.orderMessages;
	    order = orderClientLocalTime(order);
	    int i = 0;
	    for(OrderMessages messages: messagesList){
	      if(!messages.status && messages.msg_to == MessageParticipants.CLIENT){
		i = i+1;//count of unread messages
	      }
	    }
	    ordersMap.put(order,i);
	  }
	  return ordersMap;
	}
	
	//Method overloading
	public Page<Orders> getActiveOrders(int page, int page_list_size){
	  return Orders.find().where().eq("is_complete",false).eq("is_writer_assigned",false).eq("is_closed",false).orderBy("order_deadline asc").findPagingList(page_list_size).setFetchAhead(true).getPage(page);
	}
	
	public  Page<Orders> getWriterAssignedOrders(int page, int page_list_size){
		return Orders.find().where().eq("is_complete",false).eq("is_writer_assigned",true).eq("on_revision",false).orderBy("order_deadline asc").findPagingList(page_list_size).setFetchAhead(true).getPage(page);
	}
	
	public  Page<Orders> getOnRevisionOrders(int page, int page_list_size){
		return Orders.find().where().eq("on_revision",true).orderBy("order_deadline asc").findPagingList(page_list_size).setFetchAhead(true).getPage(page);
	}
	
	public  Page<Orders> getCompletedOrders(int page, int page_list_size){
	  return Orders.find().where().eq("is_complete",true).eq("is_closed",false).orderBy("order_deadline asc").findPagingList(page_list_size).setFetchAhead(true).getPage(page);
	}
	
	public Page<Orders> getClosedOrders(int page, int page_list_size){
	  return Orders.find().where().eq("is_closed",true).orderBy("order_deadline asc").findPagingList(page_list_size).setFetchAhead(true).getPage(page);
	}
	
	
	public OrderDeadlines getDeadlineObject(int dValue){
	  return OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(dValue));
	}
	
	public double computeOrderTotalForAdditionalPages(Orders newOrder){
	      double order_value = 0.0; 
	      double cost_per_unit = 0.0;
	      if(newOrder.orderDocumentType.orderCppMode.order_cpp_mode_name.name().toString().equals(OrderCppMode.CppModes.perassignment.toString())){
		//perassignment
		Logger.info("type:perassignment");
		double base_price = newOrder.orderDocumentType.base_price;
		double deadline_additional_price = 0.0;
		OrderDeadlines deadline = OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(newOrder.document_deadline));
		List<DeadlineDeadlineCategoryAssociation> ddca = deadline.deadlineDeadlineCategoryAssociation;
		for(DeadlineDeadlineCategoryAssociation d:ddca){
		  if(d.orderDeadlineCategory.id == newOrder.orderDocumentType.orderDeadlineCategory.id){
		    deadline_additional_price = d.additional_price;
		    Logger.info("deadline_additional_price:"+deadline_additional_price);
		  }
		}
		
		double level_of_writing_additional_price = newOrder.orderLevelOfWriting.additional_price;
		int number_of_units = newOrder.number_of_units;//number of assignments
		double total_additions = Additions.getTotalAdditions(newOrder) + Additions.getTotalAdditionsForAdditionalPages(newOrder);
		
		cost_per_unit = base_price + deadline_additional_price + level_of_writing_additional_price;
		order_value = (cost_per_unit*(number_of_units+newOrder.additional_pages)) + total_additions;
		
	      }else if(newOrder.orderDocumentType.orderCppMode.order_cpp_mode_name.name().toString().equals(OrderCppMode.CppModes.perpage.toString())){
		//per_page
		Logger.info("type:per_page");
		double base_price = newOrder.orderDocumentType.base_price;
		double deadline_additional_price = 0.0;
		OrderDeadlines deadline = OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(newOrder.document_deadline));
		
		List<DeadlineDeadlineCategoryAssociation> ddca = deadline.deadlineDeadlineCategoryAssociation;
		for(DeadlineDeadlineCategoryAssociation d:ddca){
		  if(d.orderDeadlineCategory.id == newOrder.orderDocumentType.orderDeadlineCategory.id){
		    deadline_additional_price = d.additional_price;
		    Logger.info("deadline_additional_price:"+deadline_additional_price);
		  }
		}
		
		double subject_additinal_price = newOrder.orderSubject.orderSubjectCategory.additional_price;
		double level_of_writing_additional_price = newOrder.orderLevelOfWriting.additional_price;
		int number_of_units = newOrder.number_of_units;//number of pages  
		double total_additions = Additions.getTotalAdditions(newOrder) + Additions.getTotalAdditionsForAdditionalPages(newOrder);
		int spacing_factor = newOrder.spacing.factor;
		
		cost_per_unit = (base_price + deadline_additional_price + level_of_writing_additional_price+subject_additinal_price)*spacing_factor;
		
		order_value = (cost_per_unit*(number_of_units+newOrder.additional_pages)) + (total_additions*spacing_factor);
	      }else{	  
	      //per question
		Logger.info("type:per_question");
		double base_price = newOrder.orderDocumentType.base_price;
		double deadline_additional_price = 0.0;
		OrderDeadlines deadline = OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(newOrder.document_deadline));
		List<DeadlineDeadlineCategoryAssociation> ddca = deadline.deadlineDeadlineCategoryAssociation;
		for(DeadlineDeadlineCategoryAssociation d:ddca){
		  if(d.orderDeadlineCategory.id == newOrder.orderDocumentType.orderDeadlineCategory.id){
		    deadline_additional_price = d.additional_price;
		    Logger.info("deadline_additional_price:"+deadline_additional_price);
		  }
		}
		
		double subject_additinal_price = newOrder.orderSubject.orderSubjectCategory.additional_price;
		Logger.info("subject_additinal_price:" + subject_additinal_price);
		double level_of_writing_additional_price = newOrder.orderLevelOfWriting.additional_price;
		int number_of_units = newOrder.number_of_units;//number of questions  
		double total_additions = Additions.getTotalAdditions(newOrder) + Additions.getTotalAdditionsForAdditionalPages(newOrder);
		
		cost_per_unit = (base_price + deadline_additional_price + level_of_writing_additional_price+subject_additinal_price);
		
		order_value = (cost_per_unit*(number_of_units+newOrder.additional_pages)) + total_additions;
	      }
	      return Math.round(order_value*100)/100.00;
	}
	
	public double computeOrderTotal(Orders newOrder){
	  double order_value = 0.0; 
	  double cost_per_unit = 0.0;

	  if(newOrder.orderDocumentType.orderCppMode.order_cpp_mode_name.name().toString().equals(OrderCppMode.CppModes.perassignment.toString())){
	    //perassignment
	    Logger.info("type:perassignment");
	    double base_price = newOrder.orderDocumentType.base_price;
	    double deadline_additional_price = 0.0;
	    OrderDeadlines deadline = OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(newOrder.document_deadline));
	    List<DeadlineDeadlineCategoryAssociation> ddca = deadline.deadlineDeadlineCategoryAssociation;
	    for(DeadlineDeadlineCategoryAssociation d:ddca){
	      if(d.orderDeadlineCategory.id == newOrder.orderDocumentType.orderDeadlineCategory.id){
		deadline_additional_price = d.additional_price;
		Logger.info("deadline_additional_price:"+deadline_additional_price);
	      }
	    }
	     
	    double level_of_writing_additional_price = newOrder.orderLevelOfWriting.additional_price;
	    int number_of_units = newOrder.number_of_units;//number of assignments
	    double total_additions = Additions.getTotalAdditions(newOrder);
	    
	    cost_per_unit = base_price + deadline_additional_price + level_of_writing_additional_price;
	    order_value = (cost_per_unit*number_of_units) + total_additions;
	    
	  }else if(newOrder.orderDocumentType.orderCppMode.order_cpp_mode_name.name().toString().equals(OrderCppMode.CppModes.perpage.toString())){
	    //per_page
	    Logger.info("type:per_page");
	    double base_price = newOrder.orderDocumentType.base_price;
	    double deadline_additional_price = 0.0;
	    OrderDeadlines deadline = OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(newOrder.document_deadline));
// 	    Logger.info("id"+deadline.id);
// 	    Logger.info("value:"+deadline.deadline_value);
// 	    Logger.info("unit:"+deadline.deadline_unit);
// 	    Logger.info("time:"+deadline.seconds_elapsing_to_deadline);
	    List<DeadlineDeadlineCategoryAssociation> ddca = deadline.deadlineDeadlineCategoryAssociation;
	    for(DeadlineDeadlineCategoryAssociation d:ddca){
	      if(d.orderDeadlineCategory.id == newOrder.orderDocumentType.orderDeadlineCategory.id){
		deadline_additional_price = d.additional_price;
		Logger.info("deadline_additional_price:"+deadline_additional_price);
	      }
	    }
	    
	    double subject_additinal_price = newOrder.orderSubject.orderSubjectCategory.additional_price;
	    double level_of_writing_additional_price = newOrder.orderLevelOfWriting.additional_price;
	    int number_of_units = newOrder.number_of_units;//number of pages  
	    double total_additions = Additions.getTotalAdditions(newOrder);
	    int spacing_factor = newOrder.spacing.factor;
	    
	    cost_per_unit = (base_price + deadline_additional_price + level_of_writing_additional_price+subject_additinal_price)*spacing_factor;
	    
	    order_value = (cost_per_unit*number_of_units) + (total_additions*spacing_factor);
	  }else{	  
	  //per question
	    Logger.info("type:per_question");
	    double base_price = newOrder.orderDocumentType.base_price;
	    double deadline_additional_price = 0.0;
	    OrderDeadlines deadline = OrderDeadlines.getOrderDeadlinesByValue(Long.valueOf(newOrder.document_deadline));
	    List<DeadlineDeadlineCategoryAssociation> ddca = deadline.deadlineDeadlineCategoryAssociation;
	    for(DeadlineDeadlineCategoryAssociation d:ddca){
	      if(d.orderDeadlineCategory.id == newOrder.orderDocumentType.orderDeadlineCategory.id){
		deadline_additional_price = d.additional_price;
		Logger.info("deadline_additional_price:"+deadline_additional_price);
	      }
	    }
	    
	    if(newOrder.orderDocumentType.id == 7 || newOrder.orderDocumentType.id == 8){
		      deadline_additional_price = deadline_additional_price/6;
	    }
	    
	    double subject_additinal_price = 0.0;
	    
	    if((newOrder.orderDocumentType.id == 7 || newOrder.orderDocumentType.id == 7) && newOrder.orderSubject.orderSubjectCategory.additional_price !=0.0){
		    subject_additinal_price = newOrder.orderSubject.orderSubjectCategory.additional_price/20;
	    }else{
		    subject_additinal_price = newOrder.orderSubject.orderSubjectCategory.additional_price; 
	    }
	    
	    Logger.info("subject_additinal_price:" + subject_additinal_price);
	    double level_of_writing_additional_price = newOrder.orderLevelOfWriting.additional_price;
	    int number_of_units = newOrder.number_of_units;//number of questions  
	    double total_additions = Additions.getTotalAdditions(newOrder);
	    
	    cost_per_unit = (base_price + deadline_additional_price + level_of_writing_additional_price+subject_additinal_price);
	    
	    order_value = (cost_per_unit*number_of_units) + total_additions;
	  }
	  return Math.round(order_value*100)/100.00;
	}
	
	public Date computeDeadline(Date date, int sec){
	  Calendar calender = Calendar.getInstance();
	  calender.setTimeInMillis(date.getTime()); 
	  calender.add(Calendar.SECOND, sec);
	  Date changeDate=calender.getTime();
	  return changeDate;
	}
	
	public String getStringDeadline(Date date){
	  Calendar calender = Calendar.getInstance();
	  calender.setTimeInMillis(date.getTime());
	  String date_ = calender.DATE + "";
	  String year = calender.YEAR + "";
	  String month = (calender.MONTH + 1) + "";
	  String hour = calender.HOUR_OF_DAY + "";
	  String minute = calender.MINUTE + "";
	  return "";
	}
	
	public Date newUtcOrderDeadline(String deadline, Orders order){//deadline is a formated tsring value
	  int client_offset = Integer.parseInt(order.client.client_time_zone_offset);
	  Logger.info("client_offset in Integer:" + client_offset);
	  TimeZone tz = order.client.client_time_zone_real;
	  
	  Calendar calender = Calendar.getInstance(); 
	  SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	  isoFormat.setTimeZone(tz);
	  Date newOrderDate = new Date();
	  try{
	    Date orderDate = isoFormat.parse(deadline);
	    calender.setTimeInMillis(orderDate.getTime());
	    calender.add(Calendar.MINUTE,client_offset);//get UTC time to be stored
	    newOrderDate = calender.getTime();
	  }catch(ParseException pe){
	    Logger.error(pe.getMessage().toString());
	  }	  
	  return newOrderDate;
	}
	
	public Date strDateToDateObject(String deadline){//deadline is a formated tsring value
	  Calendar calender = Calendar.getInstance(); 
	  SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	  Date newOrderDate = new Date();
	  try{
	    Date orderDate = isoFormat.parse(deadline);
	    calender.setTimeInMillis(orderDate.getTime());
	    newOrderDate = calender.getTime();
	  }catch(ParseException pe){
	    Logger.error(pe.getMessage().toString());
	  }	  
	  return newOrderDate;
	}
	
	public Date extendedUtcOrderDeadline(String deadline, Orders order){//deadline is a long value
	  int client_offset = Integer.parseInt(order.client.client_time_zone_offset);
	  Logger.info("client_offset in Integer:" + client_offset);
	  TimeZone tz = order.client.client_time_zone_real;
	  
	  Calendar calender = Calendar.getInstance(); 
	  SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	  isoFormat.setTimeZone(tz);
	  Date newOrderDate = new Date();
	  try{
	    Date orderDate = isoFormat.parse(isoFormat.format(new Date(Long.valueOf(deadline))));
	    calender.setTimeInMillis(orderDate.getTime());
	    calender.add(Calendar.MINUTE,client_offset);//get UTC time to be stored
	    newOrderDate = calender.getTime();
	  }catch(ParseException pe){
	    Logger.error(pe.getMessage().toString());
	  }	  
	  return newOrderDate;
	}
	
	public Orders orderClientLocalTime(Orders order){
	  //This is a deadline
	  Date utcTime = order.order_deadline;
	  int client_offset = Integer.parseInt(order.client.client_time_zone_offset);
	  Calendar calender = Calendar.getInstance();
	  calender.setTimeInMillis(utcTime.getTime());
	  calender.add(Calendar.MINUTE,(client_offset*(-1)));//get local time
	  Date localTime = calender.getTime();
	  order.order_deadline = localTime;
	  return order;
	}
	
	public boolean qualifiesForDiscount(Orders orders){
		/*check if an order qualifies for a discount*/
		boolean qualify = false;
		/*	RULES:
			#order total is greater than or equal to $60
			#is the clients first order
			#order code used is correct
		*/
		
		Client client = orders.client;
		double order_total_ = orders.order_total * orders.orderCurrence.convertion_rate;
		int no_of_orders = client.orders.size();
		
		
		
		if(no_of_orders<2 && order_total_>=Utilities.MINIMUN_ORDER_VALUE_FOR_DISCOUNT && isOrderCodeCorrect(orders)){
			qualify = true;
			Logger.info("qualify");
		}
		return qualify;
	}
	
	public double computeOrderDiscount(Orders orders){
		/*compute discount if customer qualifyes*/
		double client_discount = 0.00;
		if(qualifiesForDiscount(orders)){
			client_discount = orders.order_total * Utilities.FIRST_ORDER_DISCOUNT;
		}
		return client_discount;
	}
	
	public boolean isOrderCodeCorrect(Orders orders){
		boolean found = false;
		if(orders.coupon_code.equals("")){
			return false;
		}
		List<ReferralCode> clientCodeList = new ArrayList<ReferralCode>();
		clientCodeList =  ReferralCode.find.findList();
		
		List<AdminReferalCode> adminCodeList = new ArrayList<AdminReferalCode>();
		adminCodeList =  AdminReferalCode.find.findList();
		
		if(!clientCodeList.isEmpty()){
			for(ReferralCode referralCode:clientCodeList){
				if(orders.coupon_code.equals(referralCode.code)){
					found = true;
				}
			}
		}		
		//proceed		
		if(!found){/*code was not found in the first list*/
			//proceed and search in the next list
			if(!adminCodeList.isEmpty()){
				for(AdminReferalCode adminReferalCode:adminCodeList){
					if(orders.coupon_code.equals(adminReferalCode.code)){
						found = true;
					}
				}
			}

		}
		return found;
		
	}
	
	public void assignOrder(Long order_code, Long writer_id){
		Orders orders = Orders.getOrderByCode(order_code);
		FreelanceWriter fw = new FreelanceWriter().getWriterByWriterId(writer_id);
		orders.freelanceWriter = fw;
		orders.is_writer_assigned = true;
		if(!orders.prefered_writer.equals("")){
			orders.prefered_writer_value = orders.order_total*Utilities.ADDITIONAL_PAY_FOR_PREFERED_WRITER;
		}
		orders.saveOrder();
	}
	
	public boolean checkIfOrderIsFullyPaid(Orders order){
		/*returns true, if order is fully paid for, false otherwise ()*/
		boolean condition_1 = false;
		boolean condition_2 = false;
		boolean condition_3 = false;
		boolean condition_4 = false;
		boolean condition_5 = false;
		
		if(order.is_paid && order.prefered_writer.equals("") && order.additional_pages==0){
			condition_1 = true;
		}
		
		if(order.is_paid && (order.is_writer_assigned && !order.prefered_writer.equals("") && order.prefered_writer_value_paid) && (order.additional_pages>0 && order.is_additional_pages_paid)){
			condition_2 = true;
		}		
		if(order.is_paid && order.prefered_writer.equals("") && (order.additional_pages>0 && order.is_additional_pages_paid)){
			condition_3 = true;
		}		
		if(order.is_paid && (order.is_writer_assigned && !order.prefered_writer.equals("") && order.prefered_writer_value_paid) && order.additional_pages==0){
			condition_4 = true;
		}
		
		if(order.is_paid && (!order.is_writer_assigned && !order.prefered_writer.equals("") && !order.prefered_writer_value_paid) && (order.additional_pages==0 && !order.is_additional_pages_paid)){
			condition_5 = true;
		}
		
		if(condition_1 || condition_2 || condition_3 || condition_4 || condition_5){
			return true;
		}
		return false;
	}
	
	
	
} 
 
