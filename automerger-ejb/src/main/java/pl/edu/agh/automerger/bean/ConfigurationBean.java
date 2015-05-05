package pl.edu.agh.automerger.bean;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by wawek on 18.04.15.
 */
@Singleton
@Startup
public class ConfigurationBean {

    private Logger logger = Logger.getLogger("ConfigurationBean");

    private static final String FILE_PATH = "";

    private Properties properties;

    @PostConstruct
    private void init() {
        logger.info("ConfigurationBean.init - invoked");
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(FILE_PATH);
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            logger.warning("ConfigurationBean.init - IOException" + e);
        }

    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
