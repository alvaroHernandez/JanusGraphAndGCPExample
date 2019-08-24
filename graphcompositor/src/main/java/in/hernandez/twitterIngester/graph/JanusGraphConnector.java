package in.hernandez.twitterIngester.graph;

import org.janusgraph.core.JanusGraph;

public class JanusGraphConnector  {

    private JanusGraph graph;

    public JanusGraphConnector(JanusGraph graph){
        this.graph = graph;
    }

    JanusGraph getGraph() {
        return this.graph;
    }
}
