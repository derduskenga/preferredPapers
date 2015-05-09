package controllers.web.client;

import views.html.clientarea.*;
import play.*;
import play.mvc.*;
import views.html.clientarea.*;
import models.orders.*;
import models.client.Client;
import models.utility.Utilities;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.*;
import java.text.*;
import controllers.web.Secured;

@Security.Authenticated(Secured.class)
public class ClientMessageActions extends Controller{
  
  public static Result respondToAdditionalPages(Boolean status,String date, Long message_id,Long order_code){
      Orders order = Orders.getOrderByCode(order_code);      
      JSONObject jsonobject = new JSONObject();
      if(order == null){
	jsonobject.put("success",0);
	jsonobject.put("message","Order was not found");
	return ok(Json.parse(jsonobject.toString()));
      }
      OrderMessages message = OrderMessages.getMessageById(message_id);
      if(message == null){
	jsonobject.put("success",0);
	jsonobject.put("message","This message was not found");
	return ok(Json.parse(jsonobject.toString()));
      }
      Client client = Client.getClient(session().get("email"));
      if(client == null){
	jsonobject.put("success",0);
	jsonobject.put("message","An error occured. Please try again");
	return ok(Json.parse(jsonobject.toString()));
      }
      
      if(status){//client accepted to
	//adjust order and save it
	//adjust message to action taken
	//Send a message to writer to tell him that pages have been increased upon his request
	//send a message to client asking him to pay for the added pages
	//redirect client to orders
	order.additional_pages = Integer.parseInt(message.message_promise_value.trim());
	order.additional_pages_value = (order.computeOrderTotalForAdditionalPages(order)-order.order_total);
	order.saveOrder();
	
	message.action_taken = true;
	message.saveClientMessage();
	Logger.info("status is true");
	//create sent to writer from support
	OrderMessages newWriterMessage = new OrderMessages();
	newWriterMessage.msg_to = MessageParticipants.WRITERS;
	newWriterMessage.msg_from = MessageParticipants.SUPPORT;
	newWriterMessage.message_type = OrderMessages.ActionableMessageType.OTHER;
	newWriterMessage.orders = order;
	newWriterMessage.message = OrderMessages.getMessageTemplateForAcceptedAdditionalPagesToWriter(order,status);
	newWriterMessage.sent_on = Utilities.computeUtcTime(client.client_time_zone_offset,date);//this offset should be that of a writer or put a number at the Utilities
	newWriterMessage.saveClientMessage();
	//message for the client 
	OrderMessages newclientMessage = new OrderMessages();
	newclientMessage.msg_to = MessageParticipants.CLIENT;
	newclientMessage.msg_from = MessageParticipants.SUPPORT;
	newclientMessage.message_type = OrderMessages.ActionableMessageType.OTHER;
	newclientMessage.orders = order;
	newclientMessage.message = OrderMessages.getMessageTemplateForClientPayForAdditionalPages(order);
	newclientMessage.sent_on = Utilities.computeUtcTime(client.client_time_zone_offset,date);
	newclientMessage.saveClientMessage();
	
	jsonobject.put("success",1);
	jsonobject.put("message","You just accepted to add " + order.additional_pages + " page(s). <br> We kindly ask you to pay for them <br><br><strong>From support</strong>");
	return ok(Json.parse(jsonobject.toString()));
      }
      //If the client declines
      //adjust message and save it
      //order is not adjusted
      //no message sent to the client
      //send message to writer to explain of the declined request.
      //return json success
      Logger.info("status is false");
      message.action_taken = true;  
      message.saveClientMessage();
      
      //create sent to writer from support
      OrderMessages newWriterMessage = new OrderMessages();
      newWriterMessage.msg_to = MessageParticipants.WRITERS;
      newWriterMessage.msg_from = MessageParticipants.SUPPORT;
      newWriterMessage.message_type = OrderMessages.ActionableMessageType.OTHER;
      newWriterMessage.orders = order;
      newWriterMessage.message = OrderMessages.getMessageTemplateForAcceptedAdditionalPagesToWriter(order,status);
      newWriterMessage.sent_on = Utilities.computeUtcTime(client.client_time_zone_offset,date);
      newWriterMessage.saveClientMessage();
      jsonobject.put("success",1);
      jsonobject.put("message","Your reply has been sent");
      return ok(Json.parse(jsonobject.toString()));
  }
  public static Result respondToExtendDeadlineExtension(Boolean status,String date, Long message_id,Long order_code){
      Orders order = Orders.getOrderByCode(order_code);      
      JSONObject jsonobject = new JSONObject();
      if(order == null){
	jsonobject.put("success",0);
	jsonobject.put("message","Order was not found");
	return ok(Json.parse(jsonobject.toString()));
      }
      OrderMessages message = OrderMessages.getMessageById(message_id);
      if(message == null){
	jsonobject.put("success",0);
	jsonobject.put("message","This message was not found");
	return ok(Json.parse(jsonobject.toString()));
      }
      Client client = Client.getClient(session().get("email"));
      if(client == null){
	jsonobject.put("success",0);
	jsonobject.put("message","An error occured. Please try again");
	return ok(Json.parse(jsonobject.toString()));
      }
      
      if(status){
	//adjust order and save it (deadline)
	//adjust message to action taken
	//Send a message to writer to tell him that has been extended upon upon his/her request
	order.order_deadline = order.strDateToDateObject(message.message_promise_value);
	order.saveOrder();
	
	message.action_taken = true;
	message.saveClientMessage();
	
	//create sent to writer from support
	OrderMessages newWriterMessage = new OrderMessages();
	newWriterMessage.msg_to = MessageParticipants.WRITERS;
	newWriterMessage.msg_from = MessageParticipants.SUPPORT;
	newWriterMessage.message_type = OrderMessages.ActionableMessageType.OTHER;
	newWriterMessage.orders = order;
	newWriterMessage.message = OrderMessages.getMessageForWriterDeadlineRequestResponse(status);
	newWriterMessage.sent_on = OrderMessages.computeMessageUtcTime(Utilities.WRITER_TIMEZONE_OFFSET,date);
	newWriterMessage.saveClientMessage();
	
	jsonobject.put("success",1);
	jsonobject.put("message","Our writer will receive you response. Thank you");
	return ok(Json.parse(jsonobject.toString()));	
      }
      //client does not accept to extend deadline
      //no order update
      //no message to client
      //update message action to true
      //send a message to the writer
      message.action_taken = true;
      message.saveClientMessage();
      
      //create sent to writer from support
      OrderMessages newWriterMessage = new OrderMessages();
      newWriterMessage.msg_to = MessageParticipants.WRITERS;
      newWriterMessage.msg_from = MessageParticipants.SUPPORT;
      newWriterMessage.message_type = OrderMessages.ActionableMessageType.OTHER;
      newWriterMessage.orders = order;
      newWriterMessage.message = OrderMessages.getMessageForWriterDeadlineRequestResponse(status);
      newWriterMessage.sent_on = OrderMessages.computeMessageUtcTime(Utilities.WRITER_TIMEZONE_OFFSET,date);
      newWriterMessage.saveClientMessage();
      
      jsonobject.put("success",1);
      jsonobject.put("message","Our writer will receive your response. Thank you");
      return ok(Json.parse(jsonobject.toString()));      
  } 
}