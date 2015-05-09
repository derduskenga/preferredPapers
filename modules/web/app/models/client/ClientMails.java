package models.client;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import com.microtripit.mandrillapp.lutung.view.MandrillUserInfo;

import models.client.Client;
import models.utility.Utilities;

import java.io.IOException;
import play.Logger;

public class ClientMails{
			
			MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");
			
			
			public void sendClientInvitationMail(String email){
					MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");
					MandrillMessage message = new MandrillMessage();
					message.setSubject("Check Out This Service.");
					message.setFromEmail("sales@timelyessay.com");
					message.setFromName("sales@timelyessay.com");
					try{
					message.setHtml("<p><span style='font-family: arial black,avant garde;'>Hello,</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>I recently came across TimelyEssay.com.</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'> They provide excellent writing services in all academic fields, and have exceptional customer support. </span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>I am pretty sure you will enjoy their services. <strong>Use the promocode {INSERT_CODE_HERE} and get a 12% discount</strong>. </span></p>"+
													"<a href='timelyessay.com/neworder'>Checkout the site</a>" +
													"<p>&nbsp;</p>"+
													"<p><span style='font-family: arial black,avant garde;'>Cheers,</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>{INSERT_NAME_HERE}</span></p>");
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
			
			public void sendClientInvitationMail(String email, String html){
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
			
			public void sendRegisteredClientFirstEmail(final Client client,final String password){
					Thread t = new Thread() {
						public void run() {
										MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");
										MandrillMessage message = new MandrillMessage();
										message.setSubject("Timely Essay Login Details");
										message.setFromEmail("info@timelyessay.com");
										message.setFromName("info@timelyessay.com");
										try{
										message.setHtml("<p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Dear "+client.f_name+",</span></p><p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Thank you for choosing Timely Essay for your professional academic writing services. </span></p><p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Your Login credentials are as follows:</span></p><p style='padding-left: 120px;'><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Email: <strong>"+client.email+"</strong></span></p><p style='padding-left: 120px;'><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Password: <strong>"+password+"</strong></span></p><p>&nbsp;</p><p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Kind Regards,</span></p><p><span style='font-size: 10pt; font-family: arial,helvetica,sans-serif;'>Your Friends at Timely Essay.</span></p><p>&nbsp;</p>");
										message.setAutoText(true);
										ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
										MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
										recipient.setEmail(client.email);
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
		
		public void sendClientResetPasswordMail(final String email, final String token){
			Thread t = new Thread() {
						public void run() {
										MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");
										MandrillMessage message = new MandrillMessage();
										message.setSubject("Timely Essay Password Reset");
										message.setFromEmail("admin@timelyessay.com");
										message.setFromName("admin@timelyessay.com");
										try{
								String  html="";										
											html+="<p>Hello,</p>";
											html+="<p>&nbsp;</p>";
											html+="<p>We recently received a password reset request for your timelyessay.com account.</p>";
											html+="<p>Please click&nbsp;<strong><a href='http://www.timelyessay.com/reset-password?token="+token+"'";
											html+="target='_blank'>here</a></strong> or visit the link below on your browser to reset your password.</p>";
											html+="<p><a href='http://www.timelyessay.com/reset-password?token="+token+"' target='_blank'><strong>http://www.timelyessay.com/reset-password?token="+token+"</strong></a></p>";
											html+="<p>&nbsp;</p>";
											html+="<p>Kind Regards,</p>";
											html+="<p>Your Friends at TimelyEssay.</p>";
											html+="<p>&nbsp;</p>";
											html+="<p>&nbsp;</p>";
											html+="<p>&nbsp;</p>";
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
					};
					t.start();
		}
		
		public void sendPasswordChangedMail(final String email){
						Thread t = new Thread() {
						public void run() {
										MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");
										MandrillMessage message = new MandrillMessage();
										message.setSubject("Timely Essay Password Changed.");
										message.setFromEmail("admin@timelyessay.com");
										message.setFromName("admin@timelyessay.com");
										try{
								String  html="";										
											html+="<p>Hello,</p>";
											html+="<p>&nbsp;</p>";
											html+="<p>Your timelyessay.com password was recently changed.</p>";
											html+="If this wasn't you, please contact Timely Essay Admin via admin@timelyessay.com.";
											html+="<p>&nbsp;</p>";
											html+="<p>Kind Regards,</p>";
											html+="<p>TimelyEssay Admin.</p>";
											html+="<p>&nbsp;</p>";
											html+="<p>&nbsp;</p>";
											html+="<p>&nbsp;</p>";
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
					};
					t.start();
		
		}

		public void sendGeneralMail(String email, String msg, String subject){
					MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");
					MandrillMessage message = new MandrillMessage();
					message.setSubject(subject);
					message.setFromEmail("support@timelyessay.com");
					message.setFromName("support@timelyessay.com");
					try{
					message.setHtml(msg);
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
		
		public String getClientInvitationEmailString(String code, String name){
			return "<p><span style='font-family: arial black,avant garde;'>Hello,</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>I recently came across <a href='www.timelyessay.com/neworder'>TimelyEssay.com.</></span></p>"+
													"<p><span style='font-family: arial black,avant garde;'> They provide excellent writing services in all academic fields, and have exceptional customer support. </span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>I am pretty sure you will enjoy their services. <strong>Use the promocode <b>"+code+"</b> and get a "+(Utilities.FIRST_ORDER_DISCOUNT*100)+"% discount</strong>. </span></p>"+
													"<p>&nbsp;</p>"+
													"<p><span style='font-family: arial black,avant garde;'>Cheers,</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>"+name+".</span></p>";
		}
}
 
