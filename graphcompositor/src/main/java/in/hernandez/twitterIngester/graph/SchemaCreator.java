package in.hernandez.twitterIngester.graph;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class SchemaCreator {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public SchemaCreator(@Value("${init-schema}") Boolean initSchema) throws ConfigurationException {
        if(initSchema) {
            Configurations configs = new Configurations();
            Configuration config = configs.properties(new File( this.getClass().getClassLoader().getResource("conf/remote-graph.properties").getPath()));
            JanusGraph graph = JanusGraphFactory.open((org.apache.commons.configuration.Configuration) config);
            JanusGraphManagement management = graph.openManagement();

            management.getOrCreateVertexLabel("User");
            management.getOrCreatePropertyKey("name");
            management.getOrCreateEdgeLabel("posts");
            management.getOrCreateVertexLabel("Tweet");
            management.getOrCreatePropertyKey("createdAt");
            management.getOrCreatePropertyKey("text");
            management.getOrCreateEdgeLabel("using");
            management.getOrCreateVertexLabel("Source");
            management.getOrCreatePropertyKey("url");

            logger.info("Schema created");

            management.commit();
            graph.close();
        }
    }
}
