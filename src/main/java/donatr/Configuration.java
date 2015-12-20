package donatr;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.configuration.*;

@Log4j2
public class Configuration extends CompositeConfiguration {
	public Configuration() {
		addConfiguration(new SystemConfiguration());
		addConfiguration(new EnvironmentConfiguration());
		try {
			addConfiguration(new PropertiesConfiguration("application.properties"));
		} catch (final ConfigurationException e) {
			log.warn(e.getMessage());
		}
	}
}
