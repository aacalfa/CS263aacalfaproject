package cs263aacalfaproject.cs263aacalfaproject;

import java.io.*;
import java.util.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.*;
import javax.servlet.http.*;

import java.security.*;

import com.google.appengine.api.users.*;

import java.io.IOException;

//The Worker servlet should be mapped to the "/worker" URL.
public class Worker extends HttpServlet {
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Get parameters
		String name = request.getParameter("name");
		String emailtext = request.getParameter("emailtext");
		String emailAddress = request.getParameter("emailaddr");
		
		// Send email
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("aacalfa@gmail.com", name));
			//msg.setFrom(new InternetAddress(emailAddress, name));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					"aacalfa@gmail.com", "Andre"));
			msg.setSubject("Strategy Board: New feedback arrived!");
			msg.setText(emailAddress + " wrote: " + emailtext);
			Transport.send(msg);

		} catch (AddressException e) {
			// ...
		} catch (MessagingException e) {
			// ...
		}
	}
}