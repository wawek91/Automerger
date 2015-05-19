package pl.edu.agh.automerger.standalone;

import pl.edu.agh.automerger.core.Automerger;
import pl.edu.agh.automerger.core.config.RepositoryConfiguration;
import pl.edu.agh.automerger.core.Mailer;
import pl.edu.agh.automerger.standalone.config.SARepositoryConfiguration;

/**
 * Standalone implementation of Automerger class.
 */
public class SAAutomerger extends Automerger {

  private RepositoryConfiguration repositoryConfiguration;

  private final Mailer mailer;

  public SAAutomerger() {
    mailer = new SAMailer();
  }

  /**
   * Executes a merging action.
   */
  public static void main(String[] args) {
    new SAAutomerger().merge();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RepositoryConfiguration getRepositoryConfiguration() {
    if (repositoryConfiguration == null) {
      repositoryConfiguration = new SARepositoryConfiguration();
    }
    return repositoryConfiguration;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Mailer getMailer() {
    return mailer;
  }

}
