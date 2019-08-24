package in.hernandez.twitterIngester.stream.consume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;
import twitter4j.TwitterStream;

import java.util.Arrays;

@Configuration
public class TwitterStreamConsumerConfig {

    private Logger logger = LoggerFactory.getLogger(TwitterStreamConsumerConfig.class);

    @Bean
    TwitterStream twitterStream(TwitterStreamFactory twitterStreamFactory) {
        return twitterStreamFactory.createInstance();
    }

    @Bean
    TwitterStreamFactory twitterStreamFactory() {
        return new TwitterStreamFactory();
    }

    @Bean
    MessageChannel twitterStreamOutputChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    TwitterStreamFilterQueryFactory filterQueryFactory(){
        return new TwitterStreamFilterQueryFactory();
    }

    @Bean
    TwitterStreamConsumer twitterMessageConsumer(TwitterStream twitterStream, TwitterStreamFilterQueryFactory twitterStreamFilterQueryFactory, MessageChannel twitterStreamOutputChannel) {
        TwitterStreamConsumer twitterStreamConsumer = new TwitterStreamConsumer(twitterStream, twitterStreamFilterQueryFactory,  twitterStreamOutputChannel);
        twitterStreamConsumer.setSearchTerms(Arrays.asList("java"));
        return twitterStreamConsumer;
    }
}
