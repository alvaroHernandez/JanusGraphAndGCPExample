package in.hernandez.twitterIngester.stream.consume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import twitter4j.*;

import java.util.List;

public class TwitterStreamConsumer extends MessageProducerSupport {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TwitterStream twitterStream;
    private TwitterStreamFilterQueryFactory twitterStreamFilterQueryFactory;

    private StatusListener statusListener;
    private FilterQuery filterQuery;

    private List<Long> usersIds;
    private List<String> searchTerms;

    public TwitterStreamConsumer(TwitterStream twitterStream, TwitterStreamFilterQueryFactory twitterStreamFilterQueryFactory, MessageChannel outputChannel) {
        this.twitterStream = twitterStream;
        this.twitterStreamFilterQueryFactory = twitterStreamFilterQueryFactory;
        setOutputChannel(outputChannel);
    }

    @Override
    protected void onInit(){
        super.onInit();
        statusListener = new StatusListener();
        filterQuery = twitterStreamFilterQueryFactory.buildQuery(searchTerms,usersIds);
    }

    @Override
    public void doStart() {
        twitterStream.addListener(statusListener);
        twitterStream.filter(filterQuery);
    }

    @Override
    public void doStop() {
        twitterStream.cleanUp();
        twitterStream.clearListeners();
    }

    public void setUsersIds(List<Long> usersIds) {
        this.usersIds = usersIds;
    }

    public void setSearchTerms(List<String> searchTerms) {
        this.searchTerms = searchTerms;
    }

    class StatusListener extends StatusAdapter {
        @Override
        public void onStatus(Status status) {
            sendMessage(MessageBuilder.withPayload(status).build());
        }
        @Override
        public void onException(Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        @Override
        public void onStallWarning(StallWarning warning) {
            logger.warn(warning.toString());
        }
    }
}
