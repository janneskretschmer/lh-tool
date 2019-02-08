package de.lh.tool.service.entity.impl;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.lh.tool.service.entity.interfaces.MailService;

@Service
public class MailServiceImpl implements MailService {

	private Properties properties;

	@Value("${mail.smtp.host}")
	private String host;

	@Value("${mail.smtp.username}")
	private String username;

	@Value("${mail.smtp.password}")
	private String password;

	public MailServiceImpl() {
		properties = new Properties();
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", "465");
	}

	@Override
	public void test() {
		System.out.println("Host: " + host);
		sendMail(username, password, username, "jannes.kretschmer@gmx.de", "Test", "sers, das ist ein Test xD");
	}

	private void sendMail(String username, String password, String fromEmailAddress, String toEmailAddress,
			String subject, String messageText) {
		properties.put("mail.smtp.host", host);
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(fromEmailAddress));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress));
			msg.setSubject(subject);
			msg.setText(messageText);
			Transport.send(msg);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
