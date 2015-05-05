package pl.edu.agh.automerger.mail;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by wawek on 18.04.15.
 */
@Singleton
public class MailSender {

    private final Logger LOGGER = Logger.getLogger("MailSender");

    private static final String SENDER_ADDRESS = "automerger@gmail.com";
    private static final String USERNAME = "automerger@gmail.com";
    private static final String PASSWORD = "Zaq12345@!";
    private static final String SUBJECT = "Automerger conflict occurred";
    private static final String CONTENT = "See details";

    @Asynchronous
    public void sendTo(String recipient) {

        LOGGER.info("Preparing mail for conflict author");
        final Session session = prepareAuthenticatedSession();

        try {
            final Message message = prepareMessage(session, recipient);
            Transport.send(message);
            LOGGER.info("Mail successfully sent");
        } catch (MessagingException e) {
            LOGGER.info("A MessagingException occurred while sending mail");
            throw new RuntimeException(e);
        }
    }

    private Session prepareAuthenticatedSession() {

        final Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });
    }

    private Message prepareMessage(final Session session, final String recipient) throws MessagingException {

        final Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SENDER_ADDRESS));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(SUBJECT);
        message.setText(CONTENT);
        return message;
    }

}
