package pl.edu.agh.automerger;

import pl.edu.agh.automerger.config.RepositoryConfigurationBean;
import pl.edu.agh.automerger.core.Automerger;
import pl.edu.agh.automerger.core.config.RepositoryConfiguration;
import pl.edu.agh.automerger.core.Mailer;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

/**
 * EJB implementation of Automerger class.
 */
@Stateless
public class AutomergerBean extends Automerger {

  @EJB
  private RepositoryConfigurationBean repoConfigBean;

  @EJB
  private MailSender mailSender;

  /**
   * {@inheritDoc}
   */
  @Override
  protected RepositoryConfiguration getRepositoryConfiguration() {
    return repoConfigBean;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Mailer getMailer() {
    return mailSender;
  }

  /**
   * Periodically performs a merge operation and handles it's result.
   */
  @Schedule(persistent = false, hour = "*", minute = "0", second = "0")
  public void merge() {
    super.merge();
  }

}
