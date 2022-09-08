package com.smart.Service;

import org.springframework.stereotype.Service;


	import java.net.PasswordAuthentication;
	import java.util.Properties;

	import javax.mail.Authenticator;
	import javax.mail.Message;
	import javax.mail.MessagingException;
	import javax.mail.Session;
	import javax.mail.Transport;
	import javax.mail.internet.InternetAddress;
	import javax.mail.internet.MimeMessage;

	import org.springframework.stereotype.Service;



	@Service
	public class EmailService {
		
		public boolean sendEmail(String subject,String message,String to) {
			
			
			boolean send=false;
			
			String host="smtp.gmail.com";
			
			String from="alamashraf356@gmail.com";
			Properties properties=System.getProperties();
			
			System.out.println("PROPERTIES"+properties);
			
			
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", "465");
			properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.auth","true");
			
			Session session=Session.getInstance(properties,new Authenticator() {

				@Override
				protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
					
					return new javax.mail.PasswordAuthentication("alamashraf356@gmail.com","gulzar@123");
				}
		
			});
			
			session.setDebug(true);
			
			MimeMessage mimemessage=new MimeMessage(session);
			
			try {
				
				//from email
				mimemessage.setFrom(from);
				
				//adding recipient to message
				mimemessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				
				//adding subject to message
				
				mimemessage.setSubject(subject);
				
				
				//adding text message
				
				mimemessage.setContent(message,"text/html");
				
				//send the message using transport class
				
				Transport.send(mimemessage);
				
				send=true;
				
				System.out.println("------------Email sent Successfully-----------------");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return send;
			
		}

	}



