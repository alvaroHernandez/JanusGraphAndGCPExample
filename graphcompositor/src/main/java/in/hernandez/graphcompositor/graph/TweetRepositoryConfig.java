package in.hernandez.graphcompositor.graph;

import in.hernandez.graphcompositor.util.ConfigurationCaster;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class TweetRepositoryConfig {

    @Bean
    JanusGraphConnector janusGraphConnector() throws ConfigurationException {
        Configurations configs = new Configurations();
        org.apache.commons.configuration2.Configuration originalConfiguration = configs.properties(
                new File( this.getClass().getClassLoader().getResource("conf/remote-graph.properties").getPath()));
        DefaultConfigurationBuilder configuration = ConfigurationCaster.castConfiguration(originalConfiguration);
        JanusGraph graph = JanusGraphFactory.open(configuration);
        return new JanusGraphConnector(graph);
    }


}
