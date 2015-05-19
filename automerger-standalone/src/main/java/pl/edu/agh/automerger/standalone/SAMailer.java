package pl.edu.agh.automerger.standalone;

import pl.edu.agh.automerger.core.Mailer;
import pl.edu.agh.automerger.core.config.MailingConfiguration;
import pl.edu.agh.automerger.standalone.config.SAMailingConfiguration;

/**
 * Standalone implementation of Mailer class.
 */
class SAMailer extends Mailer {

  private MailingConfiguration mailingConfiguration;

  /**
   * {@inheritDoc}
   */
  @Override
  protected MailingConfiguration getMailingConfiguration() {
    if (mailingConfiguration == null) {
      mailingConfiguration = new SAMailingConfiguration();
    }
    return mailingConfiguration;
  }

}
