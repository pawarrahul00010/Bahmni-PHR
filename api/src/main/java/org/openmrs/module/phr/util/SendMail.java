package org.openmrs.module.phr.util;

import java.util.Properties;  
import javax.mail.*;  
import javax.mail.internet.*;  

public class SendMail {

	String notify(String email, String subject, String msg){
		
		 String host="mail.javatpoint.com";  
		  final String user="sonoojaiswal@javatpoint.com";//change accordingly  
		  final String password="xxxxx";//change accordingly  
		    
		  String to=email;//change accordingly  
		  
		   //Get the session object  
		   Properties props = new Properties();  
		   props.put("mail.smtp.host",host);  
		   props.put("mail.smtp.auth", "true");  
		     
		   Session session = Session.getDefaultInstance(props,  
		    new javax.mail.Authenticator() {  
		      protected PasswordAuthentication getPasswordAuthentication() {  
		    return new PasswordAuthentication(user,password);  
		      }  
		    });  
		  
		   //Compose the message  
		    try {  
		     MimeMessage message = new MimeMessage(session);  
		     message.setFrom(new InternetAddress(user));  
		     message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));  
		     message.setSubject(subject);  
		     message.setText(msg);  
		       
		    //send the message  
		     Transport.send(message);  
		  
		     System.out.println("message sent successfully...");  
		   
		     } catch (MessagingException e) {e.printStackTrace();}  
		   
		return "message sent successfully...";
	}
}
