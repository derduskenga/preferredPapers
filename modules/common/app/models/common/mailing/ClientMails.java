package models.common.mailing;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import com.microtripit.mandrillapp.lutung.view.MandrillUserInfo;

import java.io.IOException;
import play.Logger;

public class ClientMails{
		MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");

		public void sendTrialMessage(){
				try{
					MandrillMessage message = new MandrillMessage();
					message.setSubject("Hello World!");
					
					message.setHtml("<h1>Hi pal!</h1><br />Really, I'm just saying hi!");
					message.setAutoText(true);
					message.setFromEmail("info@timelyessay.com");
					message.setFromName("Timely Essay");
					// add recipients
					ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
					MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
					recipient.setEmail("wambua.sam@gmail.com");
					recipient.setName("Sam Wambua");
					recipients.add(recipient);
					//recipient = new Recipient();
					//recipient.setEmail("terrybull@yetanotherdomain.com");
					//recipients.add(recipient);
					message.setTo(recipients);
					message.setPreserveRecipients(false);
					//ArrayList<String> tags = new ArrayList<String>();
					//tags.add("test");
					//tags.add("helloworld");
					//message.setTags(tags);
					// ... add more message details if you want to!
					// then ... send
					MandrillMessageStatus[] messageStatusReports = mandrillApi
							.messages().send(message, false); 
							
					} catch (MandrillApiError mandrillApiError) {
							mandrillApiError.printStackTrace();
					} catch (IOException e) {
							e.printStackTrace();
					}
			}
			
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
													"<p><span style='font-family: arial black,avant garde;'>I am pretty sure you will enjoy their services. <strong>Use the promocode {INSERT_CODE_HERE} and get a 18% discount</strong>. </span></p>"+
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
			
			public void sendRegisteredClientFirstEmail(String email){
					MandrillApi mandrillApi = new MandrillApi("qB6SN2vnpORIxTIjmlIoFA");
					MandrillMessage message = new MandrillMessage();
					message.setSubject("Check Out This Service.");
					message.setFromEmail("sales@timelyessay.com");
					message.setFromName("sales@timelyessay.com");
					try{
					message.setHtml("<p><span style='font-family: arial black,avant garde;'>Hello,</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>I recently came across TimelyEssay.com.</span></p>"+
													"<p><span style='font-family: arial black,avant garde;'> They provide excellent writing services in all academic fields, and have exceptional customer support. </span></p>"+
													"<p><span style='font-family: arial black,avant garde;'>I am pretty sure you will enjoy their services. <strong>Use the promocode {INSERT_CODE_HERE} and get a 18% discount</strong>. </span></p>"+
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
}
