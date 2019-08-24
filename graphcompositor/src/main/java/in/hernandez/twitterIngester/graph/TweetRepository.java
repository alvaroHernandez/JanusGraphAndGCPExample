package in.hernandez.twitterIngester.graph;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import twitter4j.Status;

@Repository
public class TweetRepository {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private JanusGraph graph;

    public TweetRepository(JanusGraphConnector janusGraphConnector){
        this.graph = janusGraphConnector.getGraph();
    }

    public void add(Status tweet){
        Vertex userNode = graph.addVertex("User");
        userNode.property("name",tweet.getUser().getName());

        Vertex tweetNode = graph.addVertex("Tweet");
        tweetNode.property("createdAt",tweet.getCreatedAt());
        tweetNode.property("text",tweet.getText());

        userNode.addEdge("posts",tweetNode);

        graph.tx().commit();
        logger.info("tweet added to graph = " + tweet);
    }
}
