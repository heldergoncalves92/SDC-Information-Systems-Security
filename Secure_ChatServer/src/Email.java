/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Gomes
 */
import java.math.BigInteger;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
import java.security.SecureRandom;

public class Email{
 
	public String sendEmail(String email, String user ) {
 
		final String username = "ssi.chatservice@gmail.com";
		final String password = "seguranca";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
            SecureRandom random = new SecureRandom();            
            String codigo = new BigInteger(130, random).toString(32).substring(0, 8);;
                        
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("ssi.chatservice@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(email));
			message.setSubject("Novo codigo");
			message.setText("Ola " + user 
				+ "\n\n O seu código é o " + codigo);
 
                        
			Transport.send(message);
 
			//System.out.println("Done");
                        return codigo;
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}