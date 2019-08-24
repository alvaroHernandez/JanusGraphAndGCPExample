package in.hernandez.twitterIngester.stream.load;

import in.hernandez.twitterIngester.stream.consume.TwitterStreamConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;
import twitter4j.TwitterObjectFactory;

@Component
public class PubSubIngestionIngestionFlow {

    private Logger logger = LoggerFactory.getLogger(TwitterStreamConsumerConfig.class);

    @Autowired
    private PubSubIngestionConfig.PubsubOutputGateway messagingGateway;

    @Bean
    IntegrationFlow twitterFlow(MessageChannel twitterStreamOutputChannel, PubSubIngestionConfig.PubsubOutputGateway messagingGateway) {
        return IntegrationFlows.from(twitterStreamOutputChannel)
                .handle(
                        m ->
                            {
                                String tweetStatusAsJson = TwitterObjectFactory.getRawJSON(m.getPayload());
                                logger.info(tweetStatusAsJson);
                                messagingGateway.sendToPubsub(tweetStatusAsJson);
                            }
                )
                .get();
    }
}
