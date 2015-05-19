package pl.edu.agh.automerger.core.config;

/**
 * Mailing configuration class.
 */
public abstract class MailingConfiguration extends Configuration {

  // is SMTP authentication enabled
  private static final String SMTP_AUTH = "smtp.auth";

  // is STARTTLS enabled
  private static final String SMTP_STARTTLS = "smtp.starttls.enable";

  // SMTP host name
  private static final String SMTP_HOST = "smtp.host";

  // SMTP port number
  private static final String SMTP_PORT = "smtp.port";

  // mail account username
  private static final String USERNAME = "account.username";

  // mail account password
  private static final String PASSWORD = "account.password";

  // message sender
  private static final String SENDER = "message.sender";

  // message subject
  private static final String SUBJECT = "message.subject";

  // message content
  private static final String CONTENT = "message.content";

  /**
   * Returns the SMTP authentication setting.
   */
  public String getSmtpAuthSetting() {
    return getProperty(SMTP_AUTH);
  }

  /**
   * Returns the SMTP STARTTLS setting.
   */
  public String getSmtpStarttlsSetting() {
    return getProperty(SMTP_STARTTLS);
  }

  /**
   * Returns the SMTP host name.
   */
  public String getSmtpHost() {
    return getProperty(SMTP_HOST);
  }

  /**
   * Returns the SMTP port number.
   */
  public String getSmtpPort() {
    return getProperty(SMTP_PORT);
  }

  /**
   * Returns the mail account username.
   */
  public String getUsername() {
    return getProperty(USERNAME);
  }

  /**
   * Returns the mail account password.
   */
  public String getPassword() {
    return getProperty(PASSWORD);
  }

  /**
   * Returns the message sender name.
   */
  public String getMessageSender() {
    return getProperty(SENDER);
  }

  /**
   * Returns the message subject.
   */
  public String getMessageSubject() {
    return getProperty(SUBJECT);
  }

  /**
   * Returns the message content.
   */
  public String getMessageContent() {
    return getProperty(CONTENT);
  }

}
