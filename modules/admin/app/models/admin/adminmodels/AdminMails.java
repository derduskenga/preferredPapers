package models.admin.adminmodels;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import com.microtripit.mandrillapp.lutung.view.MandrillUserInfo;

import models.admin.adminmodels.AdminUser;
import models.utility.Utilities;

import java.io.IOException;
import play.Logger;

public class AdminMails{
			
			MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");
			
			
			public void sendRegisteredAdminFirstEmail(final AdminUser adminUser,final String password){
					Thread t = new Thread() {
						public void run() {
										MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");
										MandrillMessage message = new MandrillMessage();
										message.setSubject("Timely Essay");
										message.setFromEmail("admin@timelyessay.com");
										message.setFromName("Timely Essay Admin");
										try{
										message.setHtml("<p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Dear "+adminUser.first_name+",</span></p><p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Welcome to the Timely Essay Family.</span></p><p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Your Login credentials are as follows:</span></p><p style='padding-left: 120px;'><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Email: <strong>"+adminUser.email+"</strong></span></p><p style='padding-left: 120px;'><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Password: <strong>"+password+"</strong></span></p><p>&nbsp;</p><p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Kind Regards,</span></p><p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Your Friends at Timely Essay.</span></p><p>&nbsp;</p>");
										message.setAutoText(true);
										ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
										MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
										recipient.setEmail(adminUser.email);
										recipients.add(recipient);
										message.setTo(recipients);
										message.setPreserveRecipients(false);
										MandrillMessageStatus[] messageStatusReports = mandrillApi
												.messages().send(message, false); 			
										} catch (MandrillApiError mandrillApiError) {
												mandrillApiError.printStackTrace();
										} catch (IOException e) {
												e.printStackTrace();
										}
								
							}
					};
					t.start();
		}
		
			public void sendClientMarketingMail(String email, String html){
					MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");
					MandrillMessage message = new MandrillMessage();
					message.setSubject("Check Out This Service.");
					message.setFromEmail("sales@timelyessay.com");
					message.setFromName("sales@timelyessay.com");
					try{
					message.setHtml(html);
					message.setAutoText(true);
					
					ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
					MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
					recipient.setEmail(email);
					recipients.add(recipient);
					message.setTo(recipients);
					message.setPreserveRecipients(false);
					MandrillMessageStatus[] messageStatusReports = mandrillApi
							.messages().send(message, false); 			
					} catch (MandrillApiError mandrillApiError) {
							mandrillApiError.printStackTrace();
					} catch (IOException e) {
							e.printStackTrace();
					}
			
			}		
			
		public String getClientMarketingEmailString(String code, String name){
			return "<p><span style='font-family: arial black,avant garde;'>Hello,</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>I recently came across TimelyEssay.com .</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'> They provide excellent writing services in all academic fields, and have exceptional customer support. </span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>I am pretty sure you will enjoy their services. <strong>Use the promocode <b>"+code+"</b> and get a "+(Utilities.FIRST_ORDER_DISCOUNT*100)+"% discount</strong>. </span></p>"+
													"<p>&nbsp;</p>"+
													"<p><span style='font-family: arial black,avant garde;'>Cheers,</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>"+name+".</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>Timely Essay Sales Executive.</span></p>";
		}			

}
 
