package pl.edu.agh.automerger.standalone.config;

import pl.edu.agh.automerger.core.config.RepositoryConfiguration;

import java.io.IOException;
import java.util.Properties;

/**
 * Standalone implementation of RepositoryConfiguration class.
 */
public class SARepositoryConfiguration extends RepositoryConfiguration {

  // .properties config file name
  private static final String FILE_PATH = "repository.properties";

  /**
   * {@inheritDoc}
   */
  @Override
  protected void loadConfiguration(final Properties properties) {
    try {
      properties.load(SARepositoryConfiguration.class.getClassLoader().getResourceAsStream(FILE_PATH));
    }
    catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

}
