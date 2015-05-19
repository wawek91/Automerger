package pl.edu.agh.automerger.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.edu.agh.automerger.core.config.RepositoryConfiguration;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.IOException;
import java.util.Properties;

/**
 * EJB implementation of RepositoryConfiguration class.
 */
@Singleton
@Startup
public class RepositoryConfigurationBean extends RepositoryConfiguration {

  private static final String FILE_PATH = "repository.properties";

  private final Logger logger = LogManager.getLogger();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void loadConfiguration(final Properties properties) {
    logger.info("Loading repository configuration");
    try {
      properties.load(this.getClass().getClassLoader().getResourceAsStream(FILE_PATH));
    }
    catch (IOException e) {
      logger.error(e);
    }
  }

}
