package in.hernandez.graphcompositor.pubsubreading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class PubSubInputConfig {
    private static final String SUBSCRIPTION_NAME = "example";
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(MessageChannel pubsubInputChannel, PubSubTemplate pubSubTemplate){
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, SUBSCRIPTION_NAME);
        adapter.setOutputChannel(pubsubInputChannel);
        adapter.setAckMode(AckMode.MANUAL);
        return adapter;
    }

    @Bean
    public PubSubMessageHandler twitterMessageHandler(){
        return new PubSubMessageHandler();
    }

    @Bean
    public MessageChannel pubsubInputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "pubsubInputChannel")
    public MessageHandler messageReceiver(PubSubMessageHandler pubSubMessageHandler) {
        return pubSubMessageHandler;
    }
}
