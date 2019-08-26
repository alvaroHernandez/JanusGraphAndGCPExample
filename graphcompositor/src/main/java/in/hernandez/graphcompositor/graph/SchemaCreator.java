package in.hernandez.graphcompositor.graph;

import in.hernandez.graphcompositor.util.ConfigurationCaster;
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
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;

@Component
public class SchemaCreator {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public SchemaCreator(@Value("${init-schema}") Boolean initSchema, ResourceLoader resourceLoader) throws ConfigurationException, IOException {
        if(initSchema) {
            Configurations configs = new Configurations();
            Configuration config = configs.properties(resourceLoader.getResource("classpath:conf/remote-graph.properties").getFile());
            JanusGraph graph = JanusGraphFactory.open(ConfigurationCaster.castConfiguration(config));
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
