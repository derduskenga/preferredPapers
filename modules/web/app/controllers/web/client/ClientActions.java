package controllers.web.client;

import play.*;
import play.mvc.*;
import views.html.clientarea.*;
import models.orders.OrderMessages;
import models.orders.Orders;
import models.orders.OrderFiles;
import models.orders.OrderRevision;
import models.orders.OrderProductFiles;
import models.orders.MessageParticipants;
import models.common.security.PasswordHash;
import play.data.Form;
import models.client.PreferredWriter;
import models.common.mailing.Mailing;
import models.writer.FreelanceWriter;
import models.client.Client;
import models.client.ReferralCode;
import models.client.Countries;
import models.utility.*;
import models.client.ClientMails;
import models.client.ClientReferalEarning;
import models.utility.Utilities;
import java.io.*;
import play.data.validation.ValidationError;
import models.client.ClientSentMail;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.clapper.util.html.HTMLUtil;

import java.util.*;
import java.text.*;
import play.data.validation.Constraints;
import controllers.web.Secured;

import static play.mvc.Http.MultipartFormData;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import models.orders.FileOwner;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import models.admincoupon.*;

import static play.data.Form.form;
@Security.Authenticated(Secured.class)
public class ClientActions extends Controller{
	
	static Form<OrderMessages> newMessageForm = form(OrderMessages.class);
	static Form<PreferredWriterForm> prefWriterForm = form(PreferredWriterForm.class);
	static Form<OrderFiles> orderFilesForm = form(OrderFiles.class);
	static Form<Client> profileForm = form(Client.class);
	static Map<String,List<ValidationError>> error = new LinkedHashMap<String,List<ValidationError>>();
	
	
	public static Result index(){  
	  if(session().get("email") != null){
	    Orders order = new Orders();
	    Client client = Client.getClient(session().get("email"));
	    if(client == null){
			return(ok(clienthome.render(null,null,null)));
	    }
	    Long client_id = Client.getClient(session().get("email")).id;
	    return ok(clienthome.render(order.activeOrdersUnreadMessages(order.getActiveOrders(client_id)),order.completeOrdersUnreadMessages(order.getCompletedOrders(client_id)),order.closedOrdersUnreadMessages(order.getClosedOrders(client_id))));
	  } 
	  return(ok(clienthome.render(null,null,null)));
	}
	
// 	public static Result postFrom2Checkout(){
// 		Map<String, String[]> queryStringMap = request().queryString();
// 		String order_code = queryStringMap.get("li_0_product_id")[0];
// 		Logger.info("order_code:" + order_code);
// 		return TODO;
// 	}
// 	
	
	public static Result postFrom2Checkout(){    
	  if(session().get("email") != null){
	    Orders order = new Orders();
	    Long client_id = Client.getClient(session().get("email")).id;
	    //Post parameters from 2checkout start=========================================
	    Map<String, String[]> checkout_post_parameters = new HashMap<String,String[]>();
	    checkout_post_parameters = request().queryString();
	    if(!checkout_post_parameters.isEmpty()){
		  boolean credit_card_approved = false;
		  //order_code
		  String li_0_product_id[] = checkout_post_parameters.get("li_0_product_id");
		  String order_code = li_0_product_id[0];
		  //invoice_id
		  String invoice_id_array[] = checkout_post_parameters.get("invoice_id");
		  String invoice_id = invoice_id_array[0];
		  //MD5 key
		  String key_array[] = checkout_post_parameters.get("key");
		  String key = key_array[0];
		  //order total
		  String total_array[] = checkout_post_parameters.get("total"); 
		  String total = total_array[0];
		  //order_number
		  String order_number_array[] = checkout_post_parameters.get("order_number");
		  String order_number = order_number_array[0];
		  //credit_card_processed (Y/N)
		  String credit_card_processed_array[] = checkout_post_parameters.get("credit_card_processed");
		  String credit_card_processed = credit_card_processed_array[0];
		  //custom parameter:payment_type
		  String payment_type_array[] = checkout_post_parameters.get("payment_type");
		  String payment_type = payment_type_array[0];
		  
		  if(credit_card_processed.equals("Y")){
		    credit_card_approved = true;
		  }
		  
		  //start checking returned key
		  HashMap<String, String> params= new HashMap<String, String>();
		  params.put("sid", Utilities.OUR_MERCHANT_ACCOUNT_NO);
		  params.put("total", total);
		  params.put("order_number",order_number);/*on real transactions, 1 shud be replaced by 'order_number' variable*/
		  params.put("key",key);
		  
		  boolean result = TwocheckoutReturn.check(params, Utilities.OUR_CO_SECRET_WORD);		  
		  //end checking returned key 
		  
		  if(credit_card_approved && result){
			
			Orders paidOrder = Orders.getOrderByCode(Long.valueOf(order_code));
			
			if(payment_type.equals(Utilities.PAY_ORDER)){
				flash("orderpaymentsuccessresponse","We have received your payment for order #" + order_code + ". A writer will be assigned to your order shortly. Thank you for choosing us.");
				paidOrder.invoice_id = invoice_id;
				paidOrder.amount_paid = Double.parseDouble(total);
				paidOrder.is_paid = true;
			}
			
			if(payment_type.equals(Utilities.PREFERED_WRITER_PAYMENT)){
				flash("orderpaymentsuccessresponse","We have received your payment for order #" + order_code + ". This payment goes directly to your prefered writer. Thank you for choosing us.");
				paidOrder.prefered_writer_value_paid = true;
				 Logger.info("PREFERED_WRITER_PAYMENT order_code:" + order_code);
			}
		
			if(payment_type.equals(Utilities.ADDITIONAL_PAGES_PAYMENT)){
				flash("orderpaymentsuccessresponse","We have received your payment for order #" + order_code + ": additional pages. Our writer will deliver you order in a timely manner. Thank you for choosing us.");
				paidOrder.is_additional_pages_paid = true;
				Logger.info("ADDITIONAL_PAGES_PAYMENT order_code:" + order_code);
			}			
			paidOrder.saveOrder();

			//if order qualifies for a discont, reward the coupon codes
			if(paidOrder.qualifiesForDiscount(paidOrder) && payment_type.equals(Utilities.PAY_ORDER)){
				if(new ReferralCode().determineTypeOfCode(paidOrder).equals("CLIENT")){
					//store a client earning
					ClientReferalEarning clientReferalEarning = new ClientReferalEarning();
					clientReferalEarning.orders = paidOrder;
					clientReferalEarning.referralCode = new ReferralCode().getReferralCode(paidOrder.coupon_code);
					clientReferalEarning.date_earned = new Date();
					clientReferalEarning.referal_earning_value = Utilities.MARKETER_EARNING_PER_CLIENT_FIRST_ORDER;
					clientReferalEarning.saveclientReferalEarning();
				}
				
				if(new ReferralCode().determineTypeOfCode(paidOrder).equals("ADMIN")){
					//store an admin earning
					AdminReferalEarning adminReferalEarning = new AdminReferalEarning();
					adminReferalEarning.admin_earning_value = Utilities.MARKETER_EARNING_PER_CLIENT_FIRST_ORDER;
					adminReferalEarning.date_earned = new Date();
					adminReferalEarning.adminReferalCode = new AdminReferalCode().getReferralCode(paidOrder.coupon_code);
					adminReferalEarning.orders = paidOrder;
					adminReferalEarning.saveAdminReferalEarning();
				}
			}
			
			return ok(payorderresponse.render(order));

		  }
		  Logger.info("order code:" + order_code + " and invoice id is:" + invoice_id + " result:" + result + " credit_card_approved:" + credit_card_approved + " key:" + key);
		  flash("orderpaymentfailresponse","There was a problem and your payment may have not been received");
		  return ok(payorderresponse.render(order));
		  
	    }
	    //End of 2checkout post parameters===============================================
	    flash("orderpaymentfailresponse","Unfortunately, your payment was has not been processsed");
	    return ok(payorderresponse.render(order));
	  } 
	  return ok(payorderresponse.render(null));
	  
	}
		
	public static Result saveClientMessage(Long order_code){
	  ObjectNode result = Json.newObject();

	  Form<OrderMessages> newBoundMessageForm = form(OrderMessages.class).bindFromRequest();
	  
	  Map<String,String> messageFormDataMap = new HashMap<String,String>();
	  messageFormDataMap = newBoundMessageForm.data();
	  String client_local_date = messageFormDataMap.get("client_local_time");
	  Orders orders = Orders.getOrderByCode(order_code);
	  if(orders == null){
	    return badRequest(clientmessages.render(orders,newBoundMessageForm, OrderMessages.getReceipientsMap("CLIENT"), getOrderMessages(order_code)));
	  }
	  
	  Client client = Client.getClient(session().get("email"));
	  if(client == null){
	    return badRequest(clientmessages.render(orders,newBoundMessageForm, OrderMessages.getReceipientsMap("CLIENT"), getOrderMessages(order_code)));
	  }
	  if(newBoundMessageForm.hasErrors()) {
		  flash("error", "Please correct the form below.");
		  flash("show_form", "true");
		  return badRequest(clientmessages.render(orders,newBoundMessageForm, OrderMessages.getReceipientsMap("CLIENT"), getOrderMessages(order_code)));
	  }	
	  OrderMessages orderMessage = newBoundMessageForm.get();
	  orderMessage.msg_from = MessageParticipants.CLIENT;
	  orderMessage.orders = orders;
	  orderMessage.message_promise_value = "none";
	  orderMessage.sent_on = Utilities.computeUtcTime(client.client_time_zone_offset,client_local_date);
	  orderMessage.message_type = OrderMessages.ActionableMessageType.OTHER;
	  if(orderMessage.saveClientMessage()){
		  return redirect(controllers.web.client.routes.ClientActions.orderMessages(order_code));
	  }
	  return redirect(controllers.web.client.routes.ClientActions.orderMessages(order_code));
	}
	
	public static List<OrderMessages> getOrderMessages(Long order_code){
			List<OrderMessages> orderMessages = new ArrayList<OrderMessages>();
			orderMessages = OrderMessages.getClientOrderMessages(order_code);
			return orderMessages;
	}
	
	public static Result orderMessages(Long order_code){
	   return ok(clientmessages.render(Orders.getOrderByCode(order_code),newMessageForm, OrderMessages.getReceipientsMap("CLIENT"),getOrderMessages(order_code)));
	}
	public static Result affiliateProgram(){
			Form<NewEmail> newMailForm = form(NewEmail.class);
			Client client = Client.getClient(session().get("email"));
			String text = new ClientMails().getClientInvitationEmailString(client.referralCode==null? "":client.referralCode.code, client.f_name);
			return ok(affiliateprogram.render(newMailForm, text));
	}
	
	public static Result sendInvitationEmail(){
			//check if client has a coupon code
			Client client = Client.getClient(session().get("email"));
			boolean send = true;
			if(client.referralCode == null){
					send = false;
			}
			Form<NewEmail> newMailForm = form(NewEmail.class).bindFromRequest();
			if(newMailForm.hasErrors() || !send){
				flash("client_mail_invitation_error", !send? "Please request a coupon code using the link above before proceeding.": "Could not send email. Please correct the form below.");
				flash("show_form", "true");
				return ok(affiliateprogram.render(newMailForm, ""));
			}
			NewEmail newMail = newMailForm.get();
			ClientMails mail = new ClientMails();
			mail.sendClientInvitationMail(newMail.email, newMail.message);
			new ClientActions().saveClientSentMessage(client, newMail.email, newMail.message);
			flash("client_mail_invitation_success", "Thanks! Email sent successfully.");
			flash("show_form", "true");
			return redirect(controllers.web.client.routes.ClientActions.affiliateProgram());		
	}
	
	public static Result preferredWriters(){
		if(session().get("email") == null){
			return redirect(controllers.web.client.routes.ClientActions.index());
		}
		Client client = Client.getClient(session().get("email"));
		
		return ok(preferredwriters.render(prefWriterForm,  Client.getPreferedWriters(client)));	
	}
	
	public static Result savePreferredWriter(){
		if(session().get("email") == null){
			return redirect(controllers.web.client.routes.ClientActions.index());
		}
		Form<PreferredWriterForm> preferredWriterForm = form(PreferredWriterForm.class).bindFromRequest();		
		if(preferredWriterForm.hasErrors()){
			flash("show_form","true");
			Client client = Client.getClient(session().get("email"));
			return badRequest(preferredwriters.render(preferredWriterForm,  Client.getPreferedWriters(client)));	
		}
		PreferredWriterForm prefWriter = preferredWriterForm.get();
		FreelanceWriter freelanceWriter = new FreelanceWriter();
		freelanceWriter = freelanceWriter.getWriterByWriterId(prefWriter.writer_id);
		//Logger.info("prefere writer:" + prefWriter.writer_id);
		if(freelanceWriter == null){
			flash("writer_not_found_error","Writer ID  "+prefWriter.writer_id + " not found");
			flash("show_form","true");
			prefWriterForm=preferredWriterForm;
			return redirect(controllers.web.client.routes.ClientActions.preferredWriters());
		}
		PreferredWriter preferredWriter = new PreferredWriter();
		preferredWriter.freelanceWriter = freelanceWriter;
		preferredWriter.client = Client.getClient(session().get("email"));
		preferredWriter.save();
		flash("show_form","true");
		flash("writer_added_success","Successfully added writer "+freelanceWriter.writer_id + " to your preferred writers.");
		return redirect(controllers.web.client.routes.ClientActions.preferredWriters());	
	}	
	public static class NewEmail{
		@Constraints.Required(message="Please enter email.")
		@Constraints.Email(message="The email you entered does not look valid.")
		public String email;
		@Constraints.Required(message="Please enter write your message.")
		public String message;
	}	
	public static class PreferredWriterForm{
		@Constraints.Required(message="Writer ID is required.")
		public Long writer_id;
	}	
	public static Result proceedToPay(Long order_code){
	  Orders orders  = Orders.getOrderByCode(order_code);
	  //send an email to the user containing password
	  //let the user pay now
	  if(orders != null){
	    return ok(payorder.render(orders));
	  }else{
	    return ok(payorder.render(new Orders()));
	  }  
	}
	public static Result clientViewOrder(Long order_code){
	  Orders orders = Orders.getOrderByCode(order_code);
	  if(orders == null){
	    return ok(clientvieworder.render(new Orders(),orderFilesForm,new OrderMessages().getUnreadMessages(order_code)));
	  }
	  return ok(clientvieworder.render(orders.orderClientLocalTime(orders),orderFilesForm,new OrderMessages().getUnreadMessages(order_code)));
	}
	
	public static Result saveOrderFile(Long order_code){
	  //get the order by order_code
	  Orders orders = Orders.getOrderByCode(order_code);
	  if(orders == null){
	    return redirect(controllers.web.client.routes.ClientActions.clientViewOrder(order_code));
	  }
	  Form<OrderFiles> orderFileBoundForm = orderFilesForm.bindFromRequest();
	   Map<String,String> clientOrderFileMap = new HashMap<String,String>();
	   clientOrderFileMap = orderFileBoundForm.data();
	   String file_local_time = clientOrderFileMap.get("file_upload_local_time");
	  if(orderFileBoundForm.hasErrors()) {
	    flash("fileuploadresponseerror","There was an error.");
	    return badRequest(clientvieworder.render(orders.orderClientLocalTime(orders),orderFileBoundForm,new OrderMessages().getUnreadMessages(order_code)));
	  }
	  OrderFiles orderFiles = orderFileBoundForm.get();
	  MultipartFormData body = request().body().asMultipartFormData();
	  MultipartFormData.FilePart part = body.getFile("order_file");	  
	  if(part != null){
	    File order_file = part.getFile();    
	    
	    if(order_file.length() > Utilities.FILE_UPLOAD_SIZE_LIMIT){
	      flash("fileuploadresponseerror","Please attach a file not exceeding 25 MB");
	      return badRequest(clientvieworder.render(orders.orderClientLocalTime(orders),orderFileBoundForm,new OrderMessages().getUnreadMessages(order_code)));
	    }   
	    
	    if(part.getContentType().equals("application/x-ms-dos-executable")){
	      flash("fileuploadresponseerror","File not allowed!");
	      return badRequest(clientvieworder.render(orders.orderClientLocalTime(orders),orderFileBoundForm,new OrderMessages().getUnreadMessages(order_code)));
	    }
	      try{
		//orderFiles.order_file = Files.toByteArray(order_file);
		orderFiles.orders = orders;
		String file_name  = part.getFilename();
		String file_key = part.getKey();
		String contentType = part.getContentType();
		Logger.info("file name:" + file_name + " file key:" + file_key + " content type:" + contentType);	      
		//String myUploadPath = Play.application().configuration().getString("myUploadPath");
		String uploadPath = Play.application().configuration().getString("orderfilespath", "/tmp/");
		//order_file.renameTo(order_file,);
		//order_file.renameTo(new File(uploadPath + file_name));
		orderFiles.file_name = file_name;
		orderFiles.content_type = contentType;
		orderFiles.upload_date = Utilities.computeUtcTime(orders.client.client_time_zone_offset,file_local_time);
		File destination = new File(uploadPath, order_file.getName());
		orderFiles.file_size = order_file.length();
		orderFiles.storage_path = destination.toPath().toString();
		orderFiles.file_sent_to = FileOwner.OrderFileOwner.WRITER;
		orderFiles.owner = FileOwner.OrderFileOwner.CLIENT;
		//FileUtils.moveFile(order_file, destination);
		Files.move(order_file,destination);
		Logger.info("File path:" + destination.toPath().toString());
		flash("fileuploadresponsesuccess","Your file has been uploaded");
		orderFiles.saveOrderFile();
		//send a message to writer
		OrderMessages message = new OrderMessages();
		
		message.orders = orders;
		message.msg_to = MessageParticipants.WRITERS;
		message.msg_from = MessageParticipants.SUPPORT;
		message.status = false;
		message.sent_on = Utilities.computeUtcTime(orders.client.client_time_zone_offset,file_local_time);
		message.message = OrderMessages.clientUploadedFile();
		message.action_required = false;
		message.message_promise_value = "none";
		message.message_type = OrderMessages.ActionableMessageType.OTHER;
		message.saveClientMessage();
		return redirect(controllers.web.client.routes.ClientActions.clientViewOrder(order_code));
	      }catch (IOException ioe){
		Logger.error("Server error on file upload:");
		flash("fileuploadresponseerror","Server error. Please try again");
		return badRequest(clientvieworder.render(orders.orderClientLocalTime(orders),orderFileBoundForm,new OrderMessages().getUnreadMessages(order_code))); 
	      }catch(Exception ex){
		Logger.error("Server error on file upload:");
		flash("fileuploadresponseerror","Server error. Please try again");
		return badRequest(clientvieworder.render(orders.orderClientLocalTime(orders),orderFileBoundForm,new OrderMessages().getUnreadMessages(order_code))); 
	      }
	      
	  }
	  flash("fileuploadresponseerror","No file was selected");
	  return badRequest(clientvieworder.render(orders.orderClientLocalTime(orders),orderFileBoundForm,new OrderMessages().getUnreadMessages(order_code)));
	}
	
	public static Result downloadOrderFile(Long file_id){
	  OrderFiles orderFiles = OrderFiles.getOrderFileById(file_id);
	  if(orderFiles == null){
	    flash("orderfiledownloaderror","File error. Please try again");
	    return ok();
	  }
	  response().setContentType(orderFiles.content_type);  
	  response().setHeader("Content-disposition","attachment; filename=" + orderFiles.file_name); 
	  response().setHeader("Content-Length",String.valueOf(new File(orderFiles.storage_path).length()));
	  try{
	    return ok(new File(orderFiles.storage_path));
	  }catch(Exception ex){
	    flash("orderfiledownloaderror","File error. Please try again");
	    return ok();
	  }
	}
	
	public static Result downloadProductFile(Long file_id, String date){
	  //orderproductfilespath
	  OrderProductFiles orderProductFiles = OrderProductFiles.getOrderProductFiles(file_id);
	  if(orderProductFiles == null){
	    flash("orderproductfiledownloaderror","File error. Please try again");
	    return ok();
	  }
	  response().setContentType(orderProductFiles.content_type);  
	  response().setHeader("Content-disposition","attachment; filename=" + orderProductFiles.file_name); 
	  response().setHeader("Content-Length",String.valueOf(new File(orderProductFiles.storage_path).length()));
	  try{
	    orderProductFiles.has_been_downloaded = true;
	    orderProductFiles.download_date = Utilities.computeUtcTime(orderProductFiles.orders.client.client_time_zone_offset,date);
	    orderProductFiles.saveProductFile();
	    return ok(new File(orderProductFiles.storage_path));
	  }catch(Exception ex){
	    flash("orderproductfiledownloaderror","File error. Please try again");
	    return ok();
	  }
	}
	
	public static Result VoluntaryDeadlineExtension(String new_date, Long order_code){
	  JSONObject jobject = new JSONObject();
	  try{
	    Orders orders = Orders.getOrderByCode(order_code);
	    if(orders == null){
	      jobject.put("success",0);
	      jobject.put("message","Order was not found");
	      return ok(Json.parse(jobject.toString()));
	    }
	    orders.order_deadline = orders.newUtcOrderDeadline(new_date,orders);
	    orders.saveOrder();
	    jobject.put("success",1);
	    jobject.put("message","Deadline has been extended");
	    return ok(Json.parse(jobject.toString()));    
	  }catch(Exception ex){
	    Logger.error("Exception error" + ex.getMessage().toString());
	    jobject.put("success",0);
	    jobject.put("message","An error occured");
	    return ok(Json.parse(jobject.toString()));
	  }
	}
	public static Result fetchDeadline(Long order_code){
	  Orders orders = Orders.getOrderByCode(order_code);
	  if(orders == null){
	    return ok();
	  }
	  return ok(orders.getStringDeadline(orders.order_deadline));
	}
	public static Result askForRevision(Long order_code, String revision_deadline){
	  Map<String, String[]> revisionValues = new HashMap<String,String[]>();
	  revisionValues = request().body().asFormUrlEncoded();
	  JSONObject jobject = new JSONObject();
	  String revision_intructions = "";
	 
	 if(revisionValues.isEmpty()){
	    jobject.put("success",0);
	    jobject.put("message","No data was sent");
	    return ok(Json.parse(jobject.toString()));
	  }
	  
	  String instructionsDetails[] = revisionValues.get("revision_instructions");
	  revision_intructions = instructionsDetails[0];
	  
	  Logger.info("revision_instructions:" + revision_intructions + " revision_deadline:" + revision_deadline);
	  Orders orders = Orders.getOrderByCode(order_code);
	  if(orders == null){
	    return redirect(controllers.web.client.routes.ClientActions.clientViewOrder(order_code));
	  }
	  OrderRevision orderRevision = new OrderRevision(); 
	  orders.on_revision = true;
	  orders.is_complete = false;

	  orders.order_deadline = Utilities.computeUtcTime(orders.client.client_time_zone_offset,revision_deadline);  
	  orderRevision.revision_instruction = revision_intructions;
	  orderRevision.orders = orders;
	  orderRevision.saveOrderRevision();
	  orders.saveOrder();

	  //send message to writer
	  OrderMessages orderMessageSupport = new OrderMessages();
	  orderMessageSupport.msg_from = MessageParticipants.SUPPORT;
	  orderMessageSupport.msg_to = MessageParticipants.WRITERS;
	  orderMessageSupport.orders = orders;
	  orderMessageSupport.message = OrderMessages.revisionRequestFromClient(orders);
	  orderMessageSupport.message_promise_value = "none";
	  orderMessageSupport.sent_on = Utilities.computeUtcTime(orders.client.client_time_zone_offset,revision_deadline);
	  orderMessageSupport.message_type = OrderMessages.ActionableMessageType.OTHER;
	  orderMessageSupport.saveClientMessage();
	  
	  //send message to support
	  OrderMessages orderMessageWriter = new OrderMessages();
	  orderMessageWriter.msg_from = MessageParticipants.CLIENT;
	  orderMessageWriter.msg_to = MessageParticipants.SUPPORT;
	  orderMessageWriter.orders = orders;
	  orderMessageWriter.message = OrderMessages.revisionRequestFromClient(orders);
	  orderMessageWriter.message_promise_value = "none";
	  orderMessageWriter.sent_on = Utilities.computeUtcTime(orders.client.client_time_zone_offset,revision_deadline);
	  orderMessageWriter.message_type = OrderMessages.ActionableMessageType.OTHER;
	  orderMessageWriter.saveClientMessage();
	  jobject.put("success",1);
	  jobject.put("message","Revision has been placed");
	  return ok(Json.parse(jobject.toString()));
} 
	
	public static Result surveyFeedback(Long order_code, int rating){
	  Orders orders = Orders.getOrderByCode(order_code);
	  JSONObject jobject = new JSONObject();
	  if(orders == null){
	    jobject.put("success",0);
	    jobject.put("message","An error occured. Please try again");
	    return ok(Json.parse(jobject.toString()));
	  }    
	  orders.client_feedback = rating;
	  orders.saveOrder();  
	  jobject.put("success",1);
	  jobject.put("message","Thank you for your feedback");
	  return ok(Json.parse(jobject.toString()));
	}
	public static Result myProfile(){
	  if(session().get("email") == null){
	    session().clear();
	    return redirect(controllers.web.client.routes.ClientActions.index());
	  }
	  String user_email = session().get("email");
	  Client client = Client.getClient(user_email);
	  Form<Client> filledClientForm = profileForm.fill(client);
	  Map<Map<Long,String>,Boolean> mapCountries = new HashMap<Map<Long,String>,Boolean>(); 
	  mapCountries = Countries.fetchCountriesMapForErrorForm(client.country.id);  
	  return ok(clientprofile.render(client,filledClientForm,mapCountries));
	}
	public static Result editProfile(){
	  Form<Client> clientBoundForm = profileForm.bindFromRequest();
	  Map<String,String> clientFormDataMap = new HashMap<String,String>();
	  clientFormDataMap = clientBoundForm.data();
	  if(clientBoundForm.hasErrors()){
	    error = clientBoundForm.errors();
	    if(!error.isEmpty()){				
	      for(Map.Entry<String,List<ValidationError>> entry : error.entrySet()){
		String key = entry.getKey();					
		Logger.info("main key:" + key);				
							  
		List<ValidationError> errorKeyValue = entry.getValue();
							    
		for(ValidationError mainVal : errorKeyValue){
		  Logger.info("key:" + mainVal.key() + " message:" + mainVal.message());
							    
		}
	      }
	    }
	  }
	  Logger.info("id:" + clientFormDataMap.get("id"));
	  Client client = clientBoundForm.get();
	  String send_company_mail = clientFormDataMap.get("receive_company_mail");
	  if(send_company_mail.equals("yes")){
	    client.receive_company_mail = true;
	  }else{
	    client.receive_company_mail = false;
	  }
	  client.saveClient();
	  return redirect(controllers.web.client.routes.ClientActions.myProfile());
	}
	
	public static Result viewOrderMessage(Long order_code, Long message_id){
	  Logger.info("order_code:" + order_code + "message_id:" + message_id);
	  JSONObject jo = new JSONObject();
	  OrderMessages message = OrderMessages.getMessageById(message_id);
	  if(message == null){
	    jo.put("success",0);
	    jo.put("message","Request was unsuccessiful");
	    return ok(Json.parse(jo.toString()));
	  }
	  message.status = true;
	  message.saveClientMessage();
	  Logger.info("message status updated");
	  jo.put("success",1);
	  jo.put("message","Request was successfully");
	  return ok(Json.parse(jo.toString()));
	}
	
	public static Result changePassword(){
	  JSONObject jobject = new JSONObject();
	  try{
	  Map<String, String[]> change_password_values = new HashMap<String,String[]>();
	  change_password_values = request().body().asFormUrlEncoded();
	  if(change_password_values.isEmpty()){
	    jobject.put("success",0);
	    jobject.put("message","An error occured. Try again");
	    return ok(Json.parse(jobject.toString()));
	  }
	  String c_password_details[] = change_password_values.get("current_password");
	  //current password
	  String c_password = c_password_details[0];
	  
	  String n_password_details[] = change_password_values.get("new_password");
	  //new password
	  String n_password = n_password_details[0];
	  
	  String email = session().get("email");
	  Client client =  (email == null)? null : Client.getClient(email);
	  if(client == null){
			jobject.put("success",0);
			jobject.put("message","An error occured. Try again.");
			return ok(Json.parse(jobject.toString()));
	  }
	    
	 Boolean valid = PasswordHash.validatePassword(c_password, client.password+":"+client.salt);	
		if(valid){
					String hashedPassword = PasswordHash.createHash(n_password);
					String[] params = hashedPassword.split(":");				
					client.password = params[0];
					client.salt = params[1];
					client.saveClient();
		}else{
				jobject.put("success",0);
				jobject.put("message","Current password entered is invalid.");
				return ok(Json.parse(jobject.toString()));
		}
	  //do you code here; if password change is successful, return the JSONObject as shoen belo
	  jobject.put("success",1);
	  jobject.put("message","Password has been changed");
	  return ok(Json.parse(jobject.toString()));  
	  }catch(Exception e){
					Logger.error("Error creating hashed password",e);
					jobject.put("success",0);
					jobject.put("message","An error occured. Try again.");
					return ok(Json.parse(jobject.toString()));
	 }
	}
	
	public static Result approveOrderProduct(Long order_code){
	    //Logger.info("order_code:" + order_code);
	    Orders orders = Orders.getOrderByCode(order_code);
	    JSONObject jobject = new JSONObject();
	    if(orders == null){
	      jobject.put("success",0);
	      jobject.put("message","An error occured. Please try again");
	      return ok(Json.parse(jobject.toString()));
	    }
	    orders.approved = true;
	    orders.is_closed = true;
	    orders.saveOrder();  
	    jobject.put("success",1);
	    jobject.put("message","Thank you for approving this order");
	    return ok(Json.parse(jobject.toString()));  
	}
	
	public static Result generateUniqueCode(){
			Client client = new Client().getClient(session().get("email"));
			String str = new ReferralCode().generateString(new java.util.Random(), 5);
			ReferralCode rc = new ReferralCode(str);
			rc.client=client;
			rc.saveReferralCode();
			return redirect(controllers.web.client.routes.ClientActions.affiliateProgram());
	}
	
	public static Result pay(Long order_code){
	  return TODO;	
	}
	
	public void saveClientSentMessage(Client client, String email, String message){
			ClientSentMail csm = new ClientSentMail();
			csm.client = client;
			csm.message = message;
			csm.sent_to = email;
			csm.sent_on = new Date();
			csm.saveClientSentMail();
	}
}