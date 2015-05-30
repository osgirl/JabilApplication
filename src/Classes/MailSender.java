/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;


import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailSender {
    
    String M_HOST,M_USER,M_PASSWORD,M_PORT,A_ADDRESS;
    
	public boolean send(String[] to, String subject, String text) { //bylo jeszcze static
		//System.out.println("send mail: " + to);
		try {
			M_HOST = PropertiesClass.props.M_HOST;
			M_USER = PropertiesClass.props.M_USER;
                        M_PASSWORD = PropertiesClass.props.M_PASSWORD ; //encrypted password
                        M_PORT = PropertiesClass.props.M_PORT ;
                        A_ADDRESS = PropertiesClass.props.A_ADDRESS ;
			Properties props = System.getProperties();
                        StringBuffer msg = new StringBuffer();
			props.put("mail.smtp.host", M_HOST);
			props.put("mail.smtp.user", M_USER);
			props.put("mail.smtp.password", Cipher.mDecrypt(M_PASSWORD,msg));
			props.put("mail.smtp.port", M_PORT); // 587 is the port number of yahoo mail
			props.put("mail.smtp.auth", "true");

			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(M_USER));

			InternetAddress[] to_address = new InternetAddress[to.length];
			int i = 0;
			// To get the array of addresses
			while (i<to.length) {
				to_address[i] = new InternetAddress(to[i]);
				i++;
			}
			//System.out.println(Message.RecipientType.TO);
			i = 0;
			while (i< to_address.length) {
				message.addRecipient(Message.RecipientType.TO, to_address[i]);
				i++;
			}
			message.setSubject(subject);
			message.setText(text+"\n"+A_ADDRESS);
			Transport transport = session.getTransport("smtp");
			transport.connect(M_HOST, M_USER, M_PASSWORD);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			return true;
		} catch (Exception e) {
			System.out.println("this is the error in MailSender: " + e);
			return false;
		}
	}

	
}

