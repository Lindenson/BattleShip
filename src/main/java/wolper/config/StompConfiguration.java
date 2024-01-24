package wolper.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.SimpleBrokerRegistration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@ComponentScan("wolper.controller")
@EnableWebSocketMessageBroker
public class StompConfiguration implements WebSocketMessageBrokerConfigurer {
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/data").setAllowedOriginPatterns("*");
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
         SimpleBrokerRegistration simpleBrokerRegistration = registry.enableSimpleBroker("/queue", "/topic");
         registry.setApplicationDestinationPrefixes("/app");
         simpleBrokerRegistration.setTaskScheduler(messageBrokerSockJsTaskScheduler());
         long [] hbeat = {4000, 4000};
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


