package models.admin.adminutils;

import java.util.*; 
import java.text.*;
import models.admin.adminmodels.*;
public class AdminUtils{

	public static String getCurrentClientTime(TimeZone timezone){
		DateFormat formatter= new SimpleDateFormat("HH:mm a Z");
		formatter.setTimeZone(timezone);
		return formatter.format(new Date());
	}
	
	public static String getTimeRemaining(Date deadline){
		Date now = new Date();
		long diff = deadline.getTime() - now.getTime();
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return diffDays + " days, "+diffHours + " Hrs " + diffMinutes+" Mins.";
	}
	
	public static Date adminMessageLocalTime(Date date,String admin_email){
	  //This is a deadline 
	  AdminUser admin_user = AdminUser.findByEmail(admin_email);
	  Date utcTime = date;
	  int client_offset = Integer.parseInt(admin_user.admin_user_offset);
	  Calendar calender = Calendar.getInstance();
	  calender.setTimeInMillis(utcTime.getTime());
	  calender.add(Calendar.MINUTE,(client_offset*(-1)));//get local time
	  Date localTime = calender.getTime();
	  //order.order_deadline = localTime;
	  return localTime;
	}
	
}
