package pl.edu.agh.automerger.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.edu.agh.automerger.core.config.MailingConfiguration;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.IOException;
import java.util.Properties;

/**
 * EJB implementation of MailingConfiguration class.
 */
@Singleton
@Startup
public class MailingConfigurationBean extends MailingConfiguration {

  private static final String FILE_PATH = "mailing.properties";

  private final Logger logger = LogManager.getLogger();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void loadConfiguration(final Properties properties) {
    logger.info("Loading mailing configuration");
    try {
      properties.load(this.getClass().getClassLoader().getResourceAsStream(FILE_PATH));
    }
    catch (IOException e) {
      logger.error(e);
    }
  }

}
