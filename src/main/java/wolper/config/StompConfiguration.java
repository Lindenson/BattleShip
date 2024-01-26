package wolper.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.SimpleBrokerRegistration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class StompConfiguration implements WebSocketMessageBrokerConfigurer {

    public static final int HEARTBEAT_TIME_MS = 20000;

    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/data");
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
         SimpleBrokerRegistration simpleBrokerRegistration = registry.enableSimpleBroker("/topic");
         registry.setApplicationDestinationPrefixes("/app");
         simpleBrokerRegistration.setTaskScheduler(messageBrokerSockJsTaskScheduler());
         long [] hbeat = {HEARTBEAT_TIME_MS, HEARTBEAT_TIME_MS};
         simpleBrokerRegistration.setHeartbeatValue(hbeat);


    }

    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler messageBrokerSockJsTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("SockJS-");
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        return scheduler;
    }
}


