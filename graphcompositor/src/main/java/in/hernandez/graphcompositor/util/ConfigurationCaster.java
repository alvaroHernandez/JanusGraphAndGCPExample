package in.hernandez.graphcompositor.util;

import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration2.Configuration;

import java.util.Iterator;

public class ConfigurationCaster {
    public static DefaultConfigurationBuilder castConfiguration(Configuration configuration) {
        DefaultConfigurationBuilder castedConfiguration = new DefaultConfigurationBuilder();
        Iterator<String> keys = configuration.getKeys();
        for (; keys.hasNext();){
            String key = keys.next();
            castedConfiguration.addProperty(key,configuration.getString(key));
        }
        return castedConfiguration;
    }
}
