package in.hernandez.graphcompositor.pubsubreading;

import in.hernandez.graphcompositor.graph.TweetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

public class PubSubMessageHandler implements MessageHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TweetRepository tweetRepository;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {

        String messagePayloadRawJson = new String((byte[]) message.getPayload());
        logger.info("Message arrived! Payload: " + messagePayloadRawJson);

        try {
            Status status = TwitterObjectFactory.createStatus(messagePayloadRawJson);
            tweetRepository.add(status);
            BasicAcknowledgeablePubsubMessage originalMessage =
                    message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
            if (originalMessage != null) {
                originalMessage.ack();
            } else {
                logger.error("message couldn't be acked, HEADERS: " + GcpPubSubHeaders.ORIGINAL_MESSAGE);
            }
        } catch (Exception e) {
            //TODO: catch TwitterException
            e.printStackTrace();
        }
    }
}
