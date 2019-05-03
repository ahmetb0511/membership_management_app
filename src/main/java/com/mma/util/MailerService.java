package com.mma.util;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailerService {

	private Logger log = LoggerFactory.getLogger(MailerService.class);

	@Autowired
	private JavaMailSender mailSender;

	public void send(String to, String from, String subject, String mailMsg) {
		MimeMessage mail = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mail, true);
			helper.setTo(to);			       
			helper.setFrom((from != null && !from.isEmpty()) ? from : "mma@gmail.com");
			helper.setSubject(subject);		      
			helper.setText(mailMsg);		        
			mailSender.send(mail);
			log.info("New message is sent to: " + to);
		} catch (MessagingException e) {
			log.error(e.getMessage(), e);
		}
	}
}
