package pl.edu.agh.automerger.standalone.config;

import pl.edu.agh.automerger.core.config.MailingConfiguration;

import java.io.IOException;
import java.util.Properties;

/**
 * Standalone implementation of MailingConfiguration class.
 */
public class SAMailingConfiguration extends MailingConfiguration {

  // .properties config file name
  private static final String FILE_PATH = "mailing.properties";

  /**
   * {@inheritDoc}
   */
  @Override
  protected void loadConfiguration(final Properties properties) {
    try {
      properties.load(SAMailingConfiguration.class.getClassLoader().getResourceAsStream(FILE_PATH));
    }
    catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

}
