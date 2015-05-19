package pl.edu.agh.automerger.core.config;

import java.util.Properties;

/**
 * Base configuration class.
 */
abstract class Configuration {

  private final Properties properties = new Properties();

  public Configuration() {
    loadConfiguration(properties);
  }

  /**
   * Fills Properties object with .properties file content.
   */
  protected abstract void loadConfiguration(final Properties properties);

  /**
   * Returns property value for given property name.
   */
  protected String getProperty(final String key) {
    return properties.getProperty(key);
  }

}
