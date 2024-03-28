package wolper.messaging.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;
import java.util.stream.IntStream;


@Configuration
@EnableRabbit
public class RabbitMqStompConfig implements WebSocketMessageBrokerConfigurer {

    private final String topic = "infoExchange";
    private final String queue = "infoExchange";

    @Bean
    Queue queue() {
        return new Queue(queue, true);
    }

    @Bean
    TopicExchange topic() {
        return new TopicExchange(topic);
    }


    @Bean
    Declarables bindings() {
        List<Queue> queues = List.of(queue());
        List<TopicExchange> topics = List.of(topic());
        assert (queues.size() == topics.size());
        List<Binding> bindings = IntStream.range(0, topics.size())
                .mapToObj(it -> BindingBuilder.bind(queues.get(it)).to(topics.get(it)).with("#")).toList();
        return new Declarables(bindings);
    }
}
