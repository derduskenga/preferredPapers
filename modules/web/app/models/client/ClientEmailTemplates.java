package models.client;

import java.util.*;
import java.text.*;
import models.client.Client;
import models.writer.FreelanceWriter;
import models.support.WriterSupport;
import models.utility.*;
import models.orders.*;
import play.*;
import play.mvc.*;


public class ClientEmailTemplates{
	public static String writerAsksForExtraPagesMessage(Orders order, int pages){
		  String message_text = "<strong>Dear " + order.client.l_name + ",</strong> <br><br>" +
					"<p>Our writer is asking you give " + pages + " additinal page(s) to your work so as to fulfill your requirements</p>" +
					"<p>Please login into our system to respond this concern. <a href='http://www." + Utilities.domain + ".com:" + Utilities.ACCESS_PORT + "/mydashboard/order/messages/" + order.order_code + "'>Login</a></p><br>" + 
					"Your Friends at Timelyessay";
		  return message_text;
	}
	
	public static String writerAsksForDeadlineExtension(Orders order, Date suggested_deadline_in_utc, String reason_label){
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
		Calendar calender = Calendar.getInstance();
		calender.setTimeInMillis(suggested_deadline_in_utc.getTime());
		int offset = Integer.parseInt(order.client.client_time_zone_offset);
		calender.add(Calendar.MINUTE,offset*(-1));//get UTC time to be stored
		String date_label = isoFormat.format(calender.getTime());
		String message_text = "<strong>Dear " + order.client.l_name +   "</strong> <br><br>" +
				      "<p>Because of " + reason_label + " your order, our writer is asking you to allow a deadline<br>extension to " +
				      "" + date_label + " </p>" +
				      "<a href='http://www." + Utilities.domain + ".com:" + Utilities.ACCESS_PORT + "/mydashboard/order/messages/" + order.order_code + "'>Login to our system</a> <br><br>" +
				      "Your Friends at Timelyessay.";
				    
		return message_text;
	}
	
	public static String informClientOrderHasBeenAssigned(Orders order){
		  String message_text = "";
		  if(order.prefered_writer.equals("")){
			  //no prefered writer so send a plain message
			  message_text = "<strong>Dear " + order.client.l_name + ",</strong><br><br>" +
					  "<p>Your order has been assigned to writer " + order.freelanceWriter.writer_id + " </p>" +
					  "<p>We advise you not to share your contact information with writers. </p>" +
					  "<p>Feel free to communicate with the writer directly <p><br>" +
					  "Your Friends at Timelyessay";
			  return message_text;
					  
		  }
		   
		  message_text = "<strong>Dear " + order.client.l_name + ",</strong><br><br>" + 
				  "<p>Your order has been assigned your prefered writer-" + order.freelanceWriter.writer_id + " </p>" +
				  "<p>We are requesting you to pay a 10%(" + order.orderCurrence. currency_symbol_2 + " " + Math.round(order.orderCurrence.convertion_rate*(order.order_total/10)*100)/100.00 + ") of your order value, which goes <br>" +
				  "directly to your prefered writer. <a href='http://www." + Utilities.domain + ".com:" + Utilities.ACCESS_PORT + "/mydashboard/order/proceedtopay/" + order.order_code + "'>PAY NOW</a></p>"+
				  "<p>We advise you not to share your contact information with writers </p><br>" +
				  "Your Friends at Timelyessay";
		  return message_text;
	}
	
	public static String informClientOfUploadedFile(Orders order, String upload_type){
		  String message_text = "<strong>Dear " + order.client.l_name + ",</strong><br><br>" + 
					"<p>Our writer has uploaded " + upload_type + ". <a href='http://www." + Utilities.domain + ".com:" + Utilities.ACCESS_PORT + "/mydashboard/order/view/" + order.order_code + "'>Login</a> into our system to download the file.</p><br>" +
					"Your Friends at Timelyessay.";
		return message_text;
		  
	}
	
}