package pl.edu.agh.automerger;

import pl.edu.agh.automerger.config.MailingConfigurationBean;
import pl.edu.agh.automerger.core.Mailer;
import pl.edu.agh.automerger.core.config.MailingConfiguration;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;

/**
 * EJB implementation of Mailer class.
 */
@Singleton
public class MailSender extends Mailer {

  @EJB
  private MailingConfigurationBean mailConfig;

  /**
   * Sends conflict information e-mail to given recipient.
   */
  @Asynchronous
  public void sendTo(String recipient) {
    super.sendTo(recipient);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected MailingConfiguration getMailingConfiguration() {
    return mailConfig;
  }

}
