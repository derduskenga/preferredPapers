package models.orders;

import play.data.validation.Constraints;
import javax.persistence.*;
import play.db.ebean.Model;
import models.client.Client;
import models.writer.FreelanceWriter;
import models.support.WriterSupport;
import models.utility.*;
import play.*;
import play.mvc.*;

import javax.persistence.Column;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import java.util.*;
import java.text.*;
import com.avaje.ebean.Expr;

@Entity
public class OrderMessages extends Model{
	//fields
	@Id
	public Long id;
	
	@Constraints.Required(message = "Please select message receipient.")
	public MessageParticipants msg_to;
	
	public MessageParticipants msg_from;
	
	@Column(nullable=false,columnDefinition="boolean default false")
	public Boolean status = false; 
	
	@Column(nullable=false, columnDefinition="boolean default false")
	public Boolean action_required = false;

	@Column(nullable=false, columnDefinition="boolean default false")
	public Boolean action_taken = false;
	
	@Column(nullable=false)
	public String message_promise_value = "none";
	
	public OrderMessages.ActionableMessageType message_type;
	//relationship fields
	@ManyToOne
	public Orders orders;		
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date sent_on=new Date();
	@Lob
	@Constraints.Required(message = "Please select message receipient")
	public String message;
	
	public OrderMessages(){}
	
	public static Map<String, Boolean> getReceipientsMap(String exclude) {
			Map<String, Boolean> 	receipientsMap = new HashMap<String, Boolean>();
			for (MessageParticipants msgParticipant : MessageParticipants.values()) {
				String receipient = msgParticipant.name().trim();
				if(!receipient.equals(exclude.trim()))
					receipientsMap.put(msgParticipant.name(), false);
			}
			return receipientsMap;
	}	
	
	public Boolean saveClientMessage(){
	      if(this.id == null){
		save();
		return true;
	      }
	      update();
	      return true;
	}
	
	public Long saveClientMessageReturningId(){
	      if(this.id == null){
		save();
		return id;
	      }
	      update();
	      return id;
	}
	
	public static Finder<Long, OrderMessages> orderMessagesFinder = 
					new Finder<Long, OrderMessages>(Long.class, OrderMessages.class);
					
	public static OrderMessages getMessageById(Long id){
		return orderMessagesFinder.byId(id);
	}
	
	public static List<OrderMessages> getClientOrderMessages(Long order_code){
				return  orderMessagesFinder.where()
						.eq("orders.order_code",order_code)
						.or(Expr.eq("msg_to", MessageParticipants.CLIENT),Expr.eq("msg_from", MessageParticipants.CLIENT))
						.orderBy("sent_on asc").findList();
	}
	
	
	//for now, get all messages associated with this order.
	//hence the commented out part of the code below
	public static List<OrderMessages> getAdminOrderMessages(Long order_code){
				return  orderMessagesFinder.where()
						.eq("orders.order_code",order_code)
						//.or(Expr.eq("msg_to", MessageParticipants.SUPPORT),Expr.eq("msg_from", MessageParticipants.SUPPORT))
						.orderBy("sent_on").findList();
	}
	
	public int getUnreadMessages(Long order_code){
	  Orders order = Orders.getOrderByCode(order_code);
	  if(order == null){
	    return 0;
	  }  
	  List<OrderMessages> orderList = OrderMessages.getClientOrderMessages(order_code);  
	  int unread = 0;
	  for(OrderMessages messages:orderList){
	    if(!messages.status && messages.msg_to == MessageParticipants.CLIENT){//unread messages
	      unread = unread + 1;
	    }
	  }
	  return unread;
	}
	
	public static int getAdminUnreadMessages(Long order_code){
	  Orders order = Orders.getOrderByCode(order_code);
	  if(order == null){
	    return 0;
	  }  
	  List<OrderMessages> orderList = OrderMessages.getAdminOrderMessages(order_code);  
	  int unread = 0;
	  for(OrderMessages messages:orderList){
	    if(!messages.status && (messages.msg_to == MessageParticipants.SUPPORT || messages.msg_to == MessageParticipants.WRITERS)){//unread messages
	      unread = unread + 1;
	    }
	  }
	  return unread;
	}
	
	public static String getAdditinalPagesMessageTemplate(Orders order, int pages){
	  String message_text = "<strong>Dear " + order.client.l_name + ",</strong> <br><br>" +
				"Our writer is asking you give " + pages + " additional page(s) to your work so as to fulfill your requirements<br>" +
				"If you agree hit 'Accept' otherwise hit 'Decline'";
	  return message_text;
	}
	
	public static String getExtendDeadlineMessageTemplate(Orders order, Date suggested_deadline_in_utc, String reason_label){
	  //Templates must have local times of recipient
	  SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
	  Calendar calender = Calendar.getInstance();
	  calender.setTimeInMillis(suggested_deadline_in_utc.getTime());
	  int offset = Integer.parseInt(order.client.client_time_zone_offset);
	  calender.add(Calendar.MINUTE,offset*(-1));//get UTC time to be stored
	  String date_label = isoFormat.format(calender.getTime());
	  String message_text = "<strong>Dear " + order.client.l_name +   "</strong> <br><br>" +
				"Because of " + reason_label + " our writer is asking you to allow a deadline<br>extension to " +
				"" + date_label + " <br>" +
				"If you agree hit 'Accept'." + 
				"If you do not accept, we recommend that you suggest your own deadline";
			      
	  return message_text;
	}
	
	
	public static void sendAssignmentMessageToClient(Long order_code, String date, String client_time_zone_offset){
		OrderMessages message = new OrderMessages();
		Orders order = Orders.getOrderByCode(order_code);
		message.orders = order;
		message.sent_on = Utilities.computeUtcTime(client_time_zone_offset,date);
		message.msg_from = MessageParticipants.SUPPORT; 
		message.msg_to = MessageParticipants.CLIENT;
		message.message = orderAssignedMessageTemplate(order);
		message.saveClientMessage();
	}
	
	public static String getMessageTemplateForAcceptedAdditionalPagesToWriter(Orders order, boolean status){
	  String message_text = "";
	  if(status){
	      message_text = "<strong>Dear writer,</strong><br><br>" +
				"The number of pages was by raised " + order.additional_pages + " upon your request. <br>" +
				"We ask you to follow the instructions and complete the order in a timely manner <br><br>";
	      return message_text;
	  }
	  message_text = "<strong>Dear writer,</strong><br><br>" +
			  "Unfortunately, your request for additional pages from the client was declined<br>" +
			  "We ask you to complete the order using the client's earlier instructions.<br>" +
			    "If you have any questions, do not hesitate to talk to us";
	  return message_text;	  
	}
	
	public static String getMessageTemplateForClientPayForAdditionalPages(Orders order){
	  String message_text = "<strong>Dear " + order.client.l_name + ",</strong><br><br>" +
				"You raised the number pages of order <a href='/mydashboard/order/view/" + order.order_code + "'>#" + order.order_code + "</a> by " + order.additional_pages + " upon our writer's request.<br>" +
				"We kindly ask you to make the additional payment. <a href='/mydashboard/order/proceedtopay/" + order.order_code + "'>PAY NOW</a>";
	  return message_text;
	}
	
	public static String getMessageForWriterDeadlineRequestResponse(boolean status){
	  String message_text = "";
	  if(status){
	    message_text = "<strong>Dear writer,</strong><br><br>" +
			    "The deadline of this order was extended upon your request.<br>" +
			    "We ask you to complete this order in a timely manner.<br>" + 
			    "If you have any questions, do not hesitate to talk to us";
	    return message_text;
	  }
	    message_text = "<strong>Dear writer,</strong><br><br>" +
			    "Unfortunately, the client refused to extended the deadline as you had requested.<br>" +
			    "We, therefore, ask you to complete this order based on its current deadline<br>" + 
			    "If you have any questions, do not hesitate to talk to us"; 
	    return message_text;
	}
	
	public static String orderAssignedMessageTemplate(Orders order){
		  String message_text = "";
		  if(order.prefered_writer.equals("")){
			  //no prefered writer so send a plain message
			  message_text = "<strong>Dear " + order.client.l_name + ",</strong><br><br>" +
					  "Your order has been assigned to writer " + order.freelanceWriter.writer_id + " <br>" +
					  "We advise you not to share your contact information with writers <br>" +
					  "Feel free to communicate with the writer directly";
			  return message_text;
					  
		  }
		  
		  message_text = "<strong>Dear " + order.client.l_name + ",</strong><br><br>" + 
				  "Your order has been assigned your prefered writer-" + order.freelanceWriter.writer_id + " <br>" +
				  "We are requesting you to pay a 10%(" + order.orderCurrence. currency_symbol_2 + " " + Math.round(order.orderCurrence.convertion_rate*(order.order_total/10)*100)/100.00 + ") of your order value, which goes <br>" +
				  "directly to your prefered writer. <a href='/mydashboard/order/proceedtopay/" + order.order_code + "'>PAY NOW</a><br>"+
				  "We advise you not to share your contact information with writers";
		  return message_text;
	}
	
	public static String fileUploadClientMessage(Orders order, String upload_type){
		String message_text = "<strong>Dear " + order.client.l_name + ",</strong><br><br>" + 
				      "We have uploaded " + upload_type + ".<br>" +
				      "<a href='/mydashboard/order/view/" + order.order_code + "'>View</a> file.";
		return message_text;
	}
	
	public static String revisionRequestFromClient(Orders order){
		String message_text = "<p>The client has requested revision on order #" +order.order_code + ".</p>" +
				      "<p>Please follow the revision instructions and complete the order in a timely manner.</p>";
		return message_text;
	}
	
	public static String clientUploadedFile(){
	      String message_text = "<p>The client uploaded a file for this order.</p>";
	      return message_text;
	}
	public static Date computeMessageUtcTime(String client_time_zone_offset, String date){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calender = Calendar.getInstance();
		TimeZone tz = calender.getTimeZone();
		int host_offset = tz.getRawOffset();
		Logger.info("host:" + host_offset);		  
		try{
		  Date message_date = formatter.parse(date);
		  //Logger.info("before time::" + message_date.toString());
		  calender.setTimeInMillis(message_date.getTime());
		  //Logger.info("before time" + calender.getTime().toString());
		  int offset = Integer.parseInt(client_time_zone_offset);
		  calender.add(Calendar.MINUTE,offset);//get UTC time to be stored
		  //Logger.info("after time::" + calender.getTime().toString());
		  message_date = calender.getTime();
		}catch(ParseException pe){
		  Logger.info(pe.getMessage().toString());
		}
		return calender.getTime();
		//return message_date;
	}
	
	
	public static Date clientMessageLocalTime(Date date,String client_email){
	  //This is a deadline 
	  Client client = Client.getClient(client_email);
	  Date utcTime = date;
	  int client_offset = Integer.parseInt(client.client_time_zone_offset);
	  Calendar calender = Calendar.getInstance();
	  calender.setTimeInMillis(utcTime.getTime());
	  calender.add(Calendar.MINUTE,(client_offset*(-1)));//get local time
	  Date localTime = calender.getTime();
	  //order.order_deadline = localTime;
	  return localTime;
	}
		      
	public enum ActionableMessageType{
	  ADDITIONAL_PAGES,DEADLINE_EXTENSION,OTHER
	}
}