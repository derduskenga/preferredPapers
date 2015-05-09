package models.utility;

import java.util.*;
import java.text.*;
import play.Logger;

public class Utilities{
	public static int ORDER_UNITS = 200;
	public static int PAGE_WORD_COUNT = 280;//double spaced
	public static int NUMBER_OF_EFERENCES = 100;
	public static String STYLES [] = {"APA", "MLA", "Harvard", "Chicago", "Oxford", "Vancouver", "Turabian", "IEEE", "CBE", "other"};
	public static String PROGRAMMING_LANGUAGES [] = {"Java","ActionScript","C","C#","C#/Windows mobile","C++","JavaScript","MATLAB","Python","Ruby",
							  "ASP.NET","VB.NET","ASP MVC","Visual Basic","Scala","Java Android","Objective C/iOS","HTML 4/5","PHP","Perl","Other",
							  "Play java/scala framework","Spring","Yii PHP framework","Code PHP Ignitor",
							  "Peanuts PHP framework"};
	public static String DATABASE [] = {"MySQL","PostgreSql","Oracle","Mongo Non-SQL DB","MS Access","DB2","SQL Server","couchDB","SQLite","Sybase","None","Any","Other"};
	public static Long ORDER_CODE_CONSTANT = 30000L;
	public static Long FILE_UPLOAD_SIZE_LIMIT = 25L * 1024L * 1024L;//Unit in MegaBytes expressed in bytes
	public static String WRITER_TIMEZONE_OFFSET =  "-180";
	
	public static String CHECKOUT_URL = "https://www.2checkout.com/checkout/purchase";//live
	//public static String CHECKOUT_URL = "https://sandbox.2checkout.com/checkout/purchase ";//sandbox
	public static String OUR_MERCHANT_ACCOUNT_NO = "202462784";//live or seller ID
	//public static String OUR_MERCHANT_ACCOUNT_NO = "901264062";//sandbox seller ID
	public static String OUR_CO_SECRET_WORD = "samkenga2015$s";
	
	public static double FIRST_ORDER_DISCOUNT = 0.16;// % discount on first order worth $60 and above
	public static double MINIMUN_ORDER_VALUE_FOR_DISCOUNT = 60; //value in US Dollars
	
	public static double MARKETER_EARNING_PER_CLIENT_FIRST_ORDER = 10; //value in US Dollars
	public static int RANDOM_WRITER_ID_MAX = 100000;
	public static int RANDOM_WRITER_ID_MIN = 1111;
	/*Payment types*/
	public static String PAY_ORDER = "PAY_ORDER";/*pay for order total*/
	public static String ADDITIONAL_PAGES_PAYMENT = "ADDITIONAL_PAGES_PAYMENT";/*if a client added more pages*/
	public static String PREFERED_WRITER_PAYMENT = "PREFERED_WRITER_PAYMENT";/*value paid for an order given to a client prefered writer*/
	public static String BONUS_PAYMENT = "BONUS_PAYMENT"; /*bonus paid for good work*/
	public static double ADDITIONAL_PAY_FOR_PREFERED_WRITER = 0.1;/* % extra if order is accepted by client's prefered writer*/
	//public static int ACCESS_PORT = 9000;
	//public static String domain = "myweb";
	public static int ACCESS_PORT = 80;
	public static String domain = "timelyessay";
	
	
      
		      
	public static Date computeUtcTime(String client_time_zone_offset, String date){
	      
	      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      Calendar calender = Calendar.getInstance();
	      TimeZone tz = calender.getTimeZone();
	      int host_offset = tz.getRawOffset();
	      try{
		  Date message_date = formatter.parse(date);
		  //Logger.info("before time::" + message_date.toString());
		  calender.setTimeInMillis(message_date.getTime());
		  //Logger.info("before time" + calender.getTime().toString());
		  int offset = Integer.parseInt(client_time_zone_offset);
		  calender.add(Calendar.MINUTE,offset);
		  //Logger.info("after time::" + calender.getTime().toString());
		  message_date = calender.getTime();
	      }catch(ParseException pe){
		  Logger.info(pe.getMessage().toString());
	      }
	      return calender.getTime();
	}  
}