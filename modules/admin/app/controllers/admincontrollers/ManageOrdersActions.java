package controllers.admincontrollers;

import views.html.adminviews.adminhome;
import views.html.adminviews.createadminuser;
import views.html.adminviews.manageusers;
import views.html.adminviews.adminerror;
import views.html.adminviews.adminmessages;
import views.html.adminviews.adminroles;
import views.html.adminviews.manageorders;
import views.html.adminviews.manageorder;
import views.html.adminviews.addrole;
import views.html.adminviews.revisioninstructions;

import play.*;
import play.mvc.*;
import play.data.Form;
import com.avaje.ebean.Ebean;
import static play.data.Form.form;

import java.util.*;
import java.text.*;
import static play.mvc.Http.MultipartFormData;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import models.admin.adminmodels.AdminUser;
import controllers.admincontrollers.AdminSecured;
import models.admin.userpermissions.SecurityRole;
import models.orders.Orders;
import models.orders.MessageParticipants;
import models.orders.OrderProductFiles;
import models.utility.Utilities;
import models.orders.FileOwner;
import models.orders.OrderFiles;
import models.orders.FileType;
import models.client.*;
import be.objectify.deadbolt.java.actions.SubjectNotPresent;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.admin.security.NoUserDeadboltHandler;

import models.orders.OrderMessages;
import models.writer.FreelanceWriter;

import com.avaje.ebean.Page;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@Security.Authenticated(AdminSecured.class)
@Restrict({@Group({"Writer Support"})})
public class ManageOrdersActions extends Controller{
	
	public static Result manageOrders(){
			Orders orders = new Orders();
			Page<Orders> activeOrders = orders.getActiveOrders(0,500);
			Page<Orders> writerAssignedOrders = orders.getWriterAssignedOrders(0,500);
			Page<Orders> onRevisionOrders = orders.getOnRevisionOrders(0,500);
			Page<Orders> completedOrders = orders.getCompletedOrders(0,500);
			Page<Orders> closedOrders = orders.getClosedOrders(0,500);
			return ok(manageorders.render(activeOrders,writerAssignedOrders, onRevisionOrders, completedOrders, closedOrders));
	}
	
	public static Result manageOrder(Long order_code){
			Form<OrderProductFiles> orderFilesForm = Form.form(OrderProductFiles.class);
			Orders order = new Orders().getOrderByCode(order_code);
			return ok(manageorder.render(order,orderFilesForm,new OrderMessages().getAdminUnreadMessages(order_code)));
	}
		
	public static Result uploadProductFile(Long order_code){
			//get the order by order_code
			Orders orders = Orders.getOrderByCode(order_code);
			Form<OrderProductFiles> orderProductFileBoundForm = Form.form(OrderProductFiles.class);
			String type_of_upload = "";
			if(orders==null)
				return badRequest(manageorder.render(null,orderProductFileBoundForm,new OrderMessages().getAdminUnreadMessages(order_code)));
			orderProductFileBoundForm =orderProductFileBoundForm.bindFromRequest();
			Map<String,String> clientProductFileMap = new HashMap<String,String>();
			clientProductFileMap = orderProductFileBoundForm.data();
			String file_local_date = clientProductFileMap.get("file_local_date");
			Logger.info("file_local_date:" + file_local_date);
			if(orderProductFileBoundForm.hasErrors()) {
				flash("fileuploadresponseerror","There was an error.");
				return badRequest(manageorder.render(orders,orderProductFileBoundForm,new OrderMessages().getAdminUnreadMessages(order_code)));
			}
			OrderProductFiles orderFiles = orderProductFileBoundForm.get();
			MultipartFormData body = request().body().asMultipartFormData();
			MultipartFormData.FilePart part = body.getFile("file_name");	  
			if(part != null){
				File order_file = part.getFile();    
				
				if(order_file.length() > Utilities.FILE_UPLOAD_SIZE_LIMIT){
				  flash("fileuploadresponseerror","Please attach a file not exceeding 25 MB");
				  return badRequest(manageorder.render(orders,orderProductFileBoundForm,new OrderMessages().getAdminUnreadMessages(order_code)));
				}   
				
				if(part.getContentType().equals("application/x-ms-dos-executable")){
				  flash("fileuploadresponseerror","File type not allowed!");
				  return badRequest(manageorder.render(orders,orderProductFileBoundForm,new OrderMessages().getAdminUnreadMessages(order_code)));
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
				  //orderFiles.upload_date = new Date();
				  orderFiles.upload_date = Utilities.computeUtcTime(AdminUser.findByEmail(session().get("admin_email")).admin_user_offset,file_local_date);
				  File destination = new File(uploadPath, order_file.getName());
				  orderFiles.file_size = order_file.length();
				  orderFiles.storage_path = destination.toPath().toString();
				  orderFiles.file_sent_to = FileOwner.OrderFileOwner.CLIENT;
				  orderFiles.owner = FileOwner.OrderFileOwner.SUPPORT;
				  String product_file_type = form().bindFromRequest().get("product_file_type");
				  if(product_file_type.equals("")){
					  flash("fileuploadresponseerror","Could not upload file. Select File type.");
					  return badRequest(manageorder.render(orders,orderProductFileBoundForm,new OrderMessages().getAdminUnreadMessages(order_code))); 
				  }
				  if(product_file_type.equals("PRODUCT")){
					  orderFiles.product_file_type=FileType.ProductFileType.PRODUCT;
					  orders.is_complete = true;
					  type_of_upload = "order PRODUCT";
				  }else if(product_file_type.equals("DRAFT")){
					  orderFiles.product_file_type=FileType.ProductFileType.DRAFT;
					  type_of_upload = "order DRAFT";
				  }else if(product_file_type.equals("REVISION")){
					  orders.is_complete = true;
					  orders.on_revision = false;
					  orderFiles.product_file_type=FileType.ProductFileType.REVISION;
					  type_of_upload = "order REVISION";
				  }else if(product_file_type.equals("REFERENCE_MATERIAL")){
					  orderFiles.product_file_type=FileType.ProductFileType.REFERENCE_MATERIAL;
					  type_of_upload ="order REFERENCE MATERIAL";
				  }else if(product_file_type.equals("ADDITIONAL_FILE")){
					  orderFiles.product_file_type=FileType.ProductFileType.ADDITIONAL_FILE;
					  type_of_upload = "a FILE";
				  }else if(product_file_type.equals("OTHER")){
					  orderFiles.product_file_type=FileType.ProductFileType.OTHER;
					  type_of_upload = "a FILE";
				  }
				  //FileUtils.moveFile(order_file, destination);
				  Files.move(order_file,destination);
				  Logger.info("File path:" + destination.toPath().toString());
				  flash("fileuploadresponsesuccess","Your file has been uploaded");
				  orderFiles.saveProductFile();
				  orders.saveOrder();
				  //send email to user
				  new ClientMails().sendGeneralMail(orders.client.email,ClientEmailTemplates.informClientOfUploadedFile(orders,type_of_upload),"Order #" + order_code + ": File has been uploaded");
				  //send message
				  OrderMessages message = new OrderMessages();
				  message.orders = orders;
				  message.msg_to = MessageParticipants.CLIENT;
				  message.msg_from = MessageParticipants.SUPPORT;
				  message.status = false;
				  message.sent_on = Utilities.computeUtcTime(AdminUser.findByEmail(session().get("admin_email")).admin_user_offset,file_local_date);
				  message.message = OrderMessages.fileUploadClientMessage(orders,type_of_upload);
				  message.action_required = false;
				  message.message_promise_value = "none";
				  message.message_type = OrderMessages.ActionableMessageType.OTHER;
				  message.saveClientMessage();
				  return redirect(controllers.admincontrollers.routes.ManageOrdersActions.manageOrder(orders.order_code));
				}catch (IOException ioe) {
				  Logger.error("Server error on file upload: " + ioe.getMessage().toString());
				  flash("fileuploadresponseerror","Server error. Please try again");
				  return badRequest(manageorder.render(orders,orderProductFileBoundForm,new OrderMessages().getAdminUnreadMessages(order_code))); 
				}catch(Exception ex){
				  Logger.error("Server error on file upload: " + ex.getMessage().toString());
				  flash("fileuploadresponseerror","Server error. Please try again");
				  return badRequest(manageorder.render(orders,orderProductFileBoundForm,new OrderMessages().getAdminUnreadMessages(order_code))); 
				}
				
			}
			flash("fileuploadresponseerror","No file was selected");
			return badRequest(manageorder.render(orders,orderProductFileBoundForm,new OrderMessages().getAdminUnreadMessages(order_code)));
		}
	public static Result AskForExtraPages(int pages, Long order_code, String date){
		Orders order = Orders.getOrderByCode(order_code);
		
		JSONObject jsonobject = new JSONObject();
		if(order == null){
		  jsonobject.put("success",0);
		  jsonobject.put("message","Order was not found");
		  return ok(Json.parse(jsonobject.toString()));
		}
		order.additional_pages = pages;
		//CREATE A MESSAGE TO BE SENT TO THE CLIENT FOR SUCH REQUEST
		//double new_order_total = order.computeOrderTotalForAdditionalPages(order);
		//order.order_total = new_order_total;
		//order.saveOrder();
		AdminUser user = AdminUser.findByEmail(session().get("admin_email"));
		if(user == null){
		  //user not found or user is not logged in
		  jsonobject.put("success",0);
		  jsonobject.put("message","Order was not found");
		  //return ok(Json.parse(jsonobject.toString())); 
		  return redirect(controllers.admincontrollers.routes.ManageOrdersActions.manageOrders());
		}
		Logger.info("date:" + date);
		OrderMessages orderMessage = new OrderMessages();
		orderMessage.msg_to = MessageParticipants.CLIENT;
		orderMessage.msg_from = MessageParticipants.WRITERS;
		orderMessage.status = false;
		orderMessage.orders = order;
		orderMessage.sent_on = Utilities.computeUtcTime(user.admin_user_offset,date);
		Long message_id = orderMessage.saveClientMessageReturningId();
		OrderMessages saveMessage = OrderMessages.getMessageById(message_id);
		
		saveMessage.message = OrderMessages.getAdditinalPagesMessageTemplate(order,pages);
		saveMessage.message_type = OrderMessages.ActionableMessageType.ADDITIONAL_PAGES;
		saveMessage.action_required = true;
		saveMessage.message_promise_value = pages + "";
		saveMessage.saveClientMessage();
		//send email to client
		new ClientMails().sendGeneralMail(order.client.email,ClientEmailTemplates.writerAsksForExtraPagesMessage(order,pages),"Message from Writer: #" + order_code);
		jsonobject.put("success",1);
		jsonobject.put("message","Your request has been sent to the client");
		return ok(Json.parse(jsonobject.toString()));
	}
	
	public static Result AskForDeadlineExtension(String deadline_,String date, String reason,Long order_code){
		String deadline_extension_reason = "";
		if(reason.equals("USING_SOFTWARE")){
		  deadline_extension_reason = "using a specific software to complete ";
		}else if(reason.equals("EXTENSIVE_RESEARCH")){
		  deadline_extension_reason = "an extensive research required to complete ";
		}else if(reason.equals("CLOSE_TO_DEADLINE")){
		  deadline_extension_reason = "geting close to the deadline, ";
		}else if(reason.equals("LIMITED_MATERIAL")){
		  deadline_extension_reason = "a limited materials, which are required to complete ";
		}
		Orders order = Orders.getOrderByCode(order_code);
		OrderMessages orderMessage = new OrderMessages();
		JSONObject jsonobject = new JSONObject();
		if(order == null){
		  jsonobject.put("success",0);
		  jsonobject.put("message","Order was not found");
		  return ok(Json.parse(jsonobject.toString()));
		}
		
		AdminUser user = AdminUser.findByEmail(session().get("admin_email"));
		if(user == null){
		  jsonobject.put("success",0);
		  jsonobject.put("message","Order was not found");
		  return ok(Json.parse(jsonobject.toString())); 
		}

		orderMessage.msg_to = MessageParticipants.CLIENT;
		orderMessage.msg_from = MessageParticipants.WRITERS;
		orderMessage.status = false;
		orderMessage.orders = order;
		orderMessage.sent_on = Utilities.computeUtcTime(user.admin_user_offset,date);
		Long message_id = orderMessage.saveClientMessageReturningId();
		OrderMessages saveMessage = OrderMessages.getMessageById(message_id);
		Date deadline = Utilities.computeUtcTime(user.admin_user_offset,deadline_);
		saveMessage.message = OrderMessages.getExtendDeadlineMessageTemplate(order,deadline,deadline_extension_reason);
		saveMessage.message_type = OrderMessages.ActionableMessageType.DEADLINE_EXTENSION;
		saveMessage.action_required = true;
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calender = Calendar.getInstance();
		 calender.setTimeInMillis(deadline.getTime());
		saveMessage.message_promise_value = isoFormat.format(calender.getTime());;
		Logger.info("string date:" + saveMessage.message_promise_value);
		saveMessage.saveClientMessage(); 
		new ClientMails().sendGeneralMail(order.client.email,ClientEmailTemplates.writerAsksForDeadlineExtension(order,deadline,deadline_extension_reason),"Message from Writer Order #" + order_code);
		jsonobject.put("success",1);
		jsonobject.put("message","Your request has been sent to the client");
		return ok(Json.parse(jsonobject.toString()));
		
		
	}

	  public static Result downloadProductFile(Long file_id){
	    OrderProductFiles orderProductFiles = OrderProductFiles.getOrderProductFiles(file_id);
	    if(orderProductFiles == null){
	      flash("adminorderproductfiledownloaderror","File error. Please try again");
	      return ok();

	    }

	    response().setContentType(orderProductFiles.content_type);  

	    response().setHeader("Content-disposition","attachment; filename=" + orderProductFiles.file_name); 

	    response().setHeader("Content-Length",String.valueOf(new File(orderProductFiles.storage_path).length()));

	    try{

	      orderProductFiles.has_been_downloaded = true;

	      orderProductFiles.download_date = new Date();

	      orderProductFiles.saveProductFile();

	      return ok(new File(orderProductFiles.storage_path));

	    }catch(Exception ex){

	      flash("orderproductfiledownloaderror","File error. Please try again");

	      return ok();

	    }

	    

	  }

	  

	  public static Result adminDownloadOrderFile(Long file_id){

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

	  

	  public static Date orderSupportLocalTime(Date date,String support_email){

	    //This is a deadline 

	    AdminUser admin_user = AdminUser.findByEmail(support_email);

	    Date utcTime = date;

	    int client_offset = Integer.parseInt(admin_user.admin_user_offset);

	    Calendar calender = Calendar.getInstance();

	    calender.setTimeInMillis(utcTime.getTime());

	    calender.add(Calendar.MINUTE,(client_offset*(-1)));//get local time

	    Date localTime = calender.getTime();

	    //order.order_deadline = localTime;

	    return localTime;

	  }


	
	
	public static Result orderMessages(Long order_code){
	  Form<OrderMessages> newMessageForm = Form.form(OrderMessages.class);
	  return ok(adminmessages.render(Orders.getOrderByCode(order_code),newMessageForm, OrderMessages.getReceipientsMap("SUPPORT"),getOrderMessages(order_code)));
	}
	
	public static List<OrderMessages> getOrderMessages(Long order_code){
			List<OrderMessages> orderMessages = new ArrayList<OrderMessages>();
			orderMessages = OrderMessages.getAdminOrderMessages(order_code);
			return orderMessages;
	}
	
	public static Result markMessageRead(Long msg_id){
			OrderMessages ordermsg = new OrderMessages().getMessageById(msg_id);
			if(ordermsg!=null){
				ordermsg.status = true;
				ordermsg.saveClientMessage();
			}
			return ok();
	}

	public static Result saveAdminMessage(Long order_code){
			Form<OrderMessages> newBoundMessageForm = Form.form(OrderMessages.class).bindFromRequest();
			Orders orders = Orders.getOrderByCode(order_code);
			if(orders == null){
				return badRequest(adminmessages.render(orders,newBoundMessageForm, OrderMessages.getReceipientsMap("SUPPORT"), getOrderMessages(order_code)));
			}
			if(newBoundMessageForm.hasErrors()) {
				flash("error", "Please correct the form below.");
				flash("show_form", "true");
				return badRequest(adminmessages.render(orders,newBoundMessageForm, OrderMessages.getReceipientsMap("SUPPORT"), getOrderMessages(order_code)));
			}
			
			OrderMessages orderMessage = newBoundMessageForm.get();
			Map<String,String> adminMessageMap = new HashMap<String,String>();
			adminMessageMap = newBoundMessageForm.data();
			if(adminMessageMap.get("sender") == null || adminMessageMap.get("admin_message_upload_date") == null){
				  flash("admin-message-error","Could not save message.");
				  return redirect(controllers.admincontrollers.routes.ManageOrdersActions.orderMessages(order_code));
			}
			String sender = adminMessageMap.get("sender");
			String local_time = adminMessageMap.get("admin_message_upload_date");
			
			if(sender.equals("SUPPORT")){
				orderMessage.msg_from = MessageParticipants.SUPPORT;
			}else{
				orderMessage.msg_from = MessageParticipants.WRITERS;
			}
			
			orderMessage.orders = orders;
			orderMessage.message_promise_value = "none";
			orderMessage.message_type = OrderMessages.ActionableMessageType.OTHER;
			orderMessage.sent_on = Utilities.computeUtcTime(Utilities.WRITER_TIMEZONE_OFFSET,local_time);
			if(orderMessage.saveClientMessage()){
				return redirect(controllers.admincontrollers.routes.ManageOrdersActions.orderMessages(order_code));
			}
			flash("admin-message-error","Could not save message.");
			return redirect(controllers.admincontrollers.routes.ManageOrdersActions.orderMessages(order_code));
	}
	
	public static Result fetchAllWriters(){
		 return ok(Json.parse(FreelanceWriter.getAllWriterCodes().toString()));
	}
	
	public static Result assignOrderToWriter(String date,Long order_code,Long writer_id){
		JSONObject jo = new JSONObject();
		if(session().get("admin_email") == null){
			  jo.put("success",3);
			  jo.put("message","Session expired");
			  return ok(Json.parse(jo.toString()));
		}
		//check writer id and order code
		if(new FreelanceWriter().getWriterByWriterId(writer_id) == null || Orders.getOrderByCode(order_code) == null){
			jo.put("success",0);
			jo.put("message","Writer/order not found");
			return ok(Json.parse(jo.toString()));
		}
		if(!Orders.getOrderByCode(order_code).is_paid){
			jo.put("success",0);
			jo.put("message","You cannot assign this order because the client has not paid for the order");
			return ok(Json.parse(jo.toString()));
		}
		
		if(Orders.getOrderByCode(order_code).is_writer_assigned){
			jo.put("success",0);
			jo.put("message","Order already assigned");
			return ok(Json.parse(jo.toString()));
		}
		new Orders().assignOrder(order_code,writer_id);
		
		OrderMessages.sendAssignmentMessageToClient(order_code,date,AdminUser.findByEmail(session().get("admin_email")).admin_user_offset);
		//send email
		Orders order = Orders.getOrderByCode(order_code);
		new ClientMails().sendGeneralMail(order.client.email,ClientEmailTemplates.informClientOrderHasBeenAssigned(order),"Message from Suport: Order #" + order_code + " has been assigned");
		jo.put("success",1);
		jo.put("message","Order has been assigned to " + writer_id);
		return ok(Json.parse(jo.toString()));
		
	}
	
	public static Result getRevisionInstructions(Long order_code){
		Orders orders = Orders.getOrderByCode(order_code);
		if(orders == null){
			return ok(revisioninstructions.render(null));
		}
		return ok(revisioninstructions.render(orders));
	}
	
	public static Result cancelOrder(Long order_code){
		Orders orders = Orders.getOrderByCode(order_code);
		if(orders == null){
			return ok(revisioninstructions.render(null));
		}
		orders.is_closed = true;
		orders.saveOrder();
		return redirect(controllers.admincontrollers.routes.ManageOrdersActions.manageOrders());
	}
}