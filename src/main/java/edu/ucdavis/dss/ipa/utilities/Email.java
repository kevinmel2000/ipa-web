package edu.ucdavis.dss.ipa.utilities;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import edu.ucdavis.dss.ipa.config.SettingsConfiguration;

public class Email {
	private static final Logger log = LogManager.getLogger();

	/**
	 * Sends email if runningMode is production, else email is suppressed.
	 * 
	 * Use this to e-mail users.
	 * 
	 * @param recipientEmail address to send to
	 * @param messageBody body of the message
	 * @param messageSubject message subject
	 */
	public static boolean send(String recipientEmail, String messageBody, String messageSubject) {
		if (SettingsConfiguration.runningModeIsProduction()) {
			if (sendEmail(recipientEmail, messageBody, messageSubject, true) == false) {
				return false;
			}
			return true;
		}

		log.info("Suppressed email to '" + recipientEmail + "', subject '" + messageSubject + "' - Server is not in production mode");

		return true;
	}

	/**
	 * Sends email if runningMode is production or staging, else email is suppressed.
	 * 
	 * Use this to e-mail developers.
	 * 
	 * @param recipientEmail address to send to
	 * @param messageBody body of the message
	 * @param messageSubject message subject
	 */
	public static boolean reportException(String messageBody, String messageSubject) {
		String recipientEmail = "dssit-devs-exceptions@ucdavis.edu";

		if(SettingsConfiguration.runningModeIsProduction() || SettingsConfiguration.runningModeIsStaging()) {
			if (sendEmail(recipientEmail, messageBody, messageSubject, false) == false) {
				return false;
			}
			return true;
		}

		log.info("Suppressed emailing exception to '" + recipientEmail + "', subject '" + messageSubject + "' - Server is not in production or staging mode");

		return true;
	}

	private static boolean sendEmail(String recipientEmail, String messageBody, String messageSubject, boolean htmlMode) {
		String fromAddress = null;
		
		try {
			fromAddress = "no-reply@" + InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			fromAddress = "no-reply@unknown";
			log.error("Could not determine local hostname when sending e-mail. Exception:", e);
		}
		
		JavaMailSenderImpl sender = new JavaMailSenderImpl();

		sender.setHost(SettingsConfiguration.getEmailHost());
		sender.setPort(SettingsConfiguration.getEmailPort());

		Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.transport.protocol", SettingsConfiguration.getEmailProtocol());
		mailProperties.setProperty("mail.smtp.auth", SettingsConfiguration.getEmailAuth());
		mailProperties.setProperty("mail.debug", SettingsConfiguration.getEmailDebug());

		sender.setJavaMailProperties(mailProperties);

		if(htmlMode) {
			MimeMessage message = sender.createMimeMessage();
	
			try {
				MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
				helper.setTo(recipientEmail);
				helper.setFrom(fromAddress);
				helper.setSubject(messageSubject);
				helper.setText(messageBody, true);
	
				sender.send(message);
				log.info("Sending email to '" + recipientEmail + "', subject '" + messageSubject + "'");
			} catch (MailException e) {
				log.error("A MailException occurred while sending email to '" + recipientEmail + "'", e);
				return false;
			} catch (MessagingException e) {
				log.error("A MessagingException occurred while sending email to '" + recipientEmail + "'", e);

			}
	
			return true;
		} else {
			SimpleMailMessage message = new SimpleMailMessage();

			message.setTo(recipientEmail);
			message.setFrom(fromAddress);
			message.setSubject(messageSubject);
			message.setText(messageBody);
		
			try {
				sender.send(message);
				log.info("Sending email to '" + recipientEmail + "', subject '" + messageSubject + "'");
			} catch (MailException e) {
				log.error("A MailException occurred while sending email to '" + recipientEmail + "'", e);
				return false;
			}

			return true;
		}
	}
}
