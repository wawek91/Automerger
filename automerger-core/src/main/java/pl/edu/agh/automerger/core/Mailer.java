package pl.edu.agh.automerger.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.edu.agh.automerger.core.config.MailingConfiguration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Handles conflict information e-mails sending.
 */
public abstract class Mailer {

  private static final String SMTP_AUTH = "mail.smtp.auth";
  private static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
  private static final String SMTP_HOST = "mail.smtp.host";
  private static final String SMTP_PORT = "mail.smtp.port";

  private final Logger logger = LogManager.getLogger();

  private MailingConfiguration mailConfig;

  public Mailer() {
    mailConfig = getMailingConfiguration();
  }

  /**
   * Sends an information e-mail to the given recipient.
   */
  protected void sendTo(String recipient) {
    logger.info("Preparing mail for conflict author");
    final Session session = prepareAuthenticatedSession();

    try {
      final Message message = prepareMessage(session, recipient);
      Transport.send(message);
      logger.info("Mail successfully sent");
    } catch (MessagingException e) {
      logger.info("A MessagingException occurred while sending mail");
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns an authenticated session object.
   */
  private Session prepareAuthenticatedSession() {
    final Properties props = new Properties();
    props.put(SMTP_AUTH, mailConfig.getSmtpAuthSetting());
    props.put(SMTP_STARTTLS_ENABLE, mailConfig.getSmtpStarttlsSetting());
    props.put(SMTP_HOST, mailConfig.getSmtpHost());
    props.put(SMTP_PORT, mailConfig.getSmtpPort());

    return Session.getInstance(props,
        new Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(mailConfig.getUsername(), mailConfig.getPassword());
          }
        });
  }

  /**
   * Returns a proper MailingConfiguration object.
   */
  protected abstract MailingConfiguration getMailingConfiguration();

  /**
   * Prepares Message object filled with proper data.
   */
  private Message prepareMessage(final Session session, final String recipient) throws MessagingException {
    final Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(mailConfig.getMessageSender()));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
    message.setSubject(mailConfig.getMessageSubject());
    message.setText(mailConfig.getMessageContent());
    return message;
  }

}
