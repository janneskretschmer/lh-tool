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

import de.lh.tool.domain.model.PasswordChangeToken;
import de.lh.tool.domain.model.User;
import de.lh.tool.service.entity.interfaces.MailService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class MailServiceImpl implements MailService {

	private static final String SENDER_NAME = "LDC Baugruppe 5";
	private static final String FOOTER = "p.s. Das ist eine automatisch generierte Mail, bitte antworte nicht darauf. Bei Fragen wende dich bitte an ...";

	private Properties properties;

	@Value("${mail.smtp.host}")
	private String host;

	@Value("${mail.smtp.username}")
	private String username;

	@Value("${mail.smtp.password}")
	private String password;

	@Value("${mail.smtp.tlsEnabled}")
	private boolean tlsEnabled;

	public MailServiceImpl() {
		properties = new Properties();
		properties.put("mail.smtp.socketFactory.port", "465");
		if (tlsEnabled) {
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", "465");
	}

	@Override
	public void sendNewLocalCoordinatorMail(User user) {
		if (user != null) {
			if (user.getEmail() != null) {
				StringBuilder text = new StringBuilder("Lieber Bruder ").append(user.getLastName()).append(
						",\n\nes wurde für dich ein Account auf lh-tool.de angelegt. Diese Webseite hilft dir als lokalen Koordinator, den Bedarf an Helfern zu verwalten.\n")
						.append("Bitte rufe folgenden Link auf, um ein Passwort zu setzen. Anschließend kannst du Accounts für alle Brüder und Schwestern erstellen, welche geeignet sind, bei der Baustelle mitzuhelfen.\n\n")
						.append("TODO: Link generieren" + user.getPasswordChangeToken())
						.append("\n\nVielen Dank für deine Bereitschaft. Wir wünschen dir Jehovas Segen bei deiner Aufgabe.\n\nIn brüderlicher Liebe\n")
						.append(SENDER_NAME).append("\n\n").append(FOOTER);
				sendMail(user.getEmail(), "Account bei lh-tool.de", text.toString());
				if (log.isInfoEnabled()) {
					log.info("Mail for local coordinator " + user.getFirstName() + " " + user.getLastName()
							+ " sent to " + user.getEmail());
				}
			} else {
				if (log.isInfoEnabled()) {
					log.warn("Mail for local coordinator " + user.getFirstName() + " " + user.getLastName()
							+ " with id " + user.getId() + " not sent");
				}
			}
		}
	}

	@Override
	public void sendNewPublisherMail(User user) {
		if (user != null) {
			if (user.getEmail() != null) {
				StringBuilder text = new StringBuilder(User.Gender.FEMALE.equals(user.getGender()) ? "Liebe Schwester "
						: "Lieber Bruder ").append(user.getLastName()).append(
								",\n\nes wurde für dich ein Account auf lh-tool.de angelegt. Auf dieser Webseite kannst du dich als Helfer bei der Baustelle an deinem Saal bewerben.\n")
								.append("Bitte rufe folgenden Link auf, um ein Passwort zu setzen. Anschließend kannst du angeben, an welchen Tagen es dir möglich wäre mitzuhelfen.\n\n")
								.append("TODO: Link generieren" + user.getPasswordChangeToken())
								.append("\n\nVielen Dank für deine Bereitschaft. Wir wünschen dir Jehovas Segen.\n\nIn brüderlicher Liebe\n")
								.append(SENDER_NAME).append("\n\n").append(FOOTER);
				sendMail(user.getEmail(), "Account bei lh-tool.de", text.toString());
				if (log.isInfoEnabled()) {
					log.info("Mail for local coordinator " + user.getFirstName() + " " + user.getLastName()
							+ " sent to " + user.getEmail());
				}
			} else {
				if (log.isInfoEnabled()) {
					log.warn("Mail for local coordinator " + user.getFirstName() + " " + user.getLastName()
							+ " with id " + user.getId() + " not sent");
				}
			}
		}
	}

	@Override
	public void sendPwResetMail(User user, PasswordChangeToken passwordChangeToken) {
		// Preliminary implementation:
		if (user != null && passwordChangeToken != null && user.getEmail() != null) {
			// FIXME Harcoded URL prefix
			String url = "http://localhost:8080/lh-tool/web/changepw?uid=" + user.getId() + "&token="
					+ passwordChangeToken.getToken();
			String text = "Link um dein Passwort zurückzusetzen:\n" + url;
			sendMail(user.getEmail(), "[LH-Tool] Passwort zurücksetzen", text);
		}
	}

	private void sendMail(String toEmailAddress, String subject, String messageText) {
		properties.put("mail.smtp.host", host);
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(SENDER_NAME + "<" + username + ">"));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress));
			msg.setSubject(subject);
			msg.setText(messageText);
			Transport.send(msg);
		} catch (Exception e) {
			log.warn("Email to " + toEmailAddress + " could not be sent", e);
		}
	}
}
