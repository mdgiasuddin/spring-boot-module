package com.example.module.rabbitmqproducer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String DIRECT_EXCHANGE = "direct.exchange";
    public static final String DIRECT_QUEUE = "direct.queue";
    public static final String DIRECT_ROUTING_KEY = "direct.routing.key";

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);

        return template;
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }

    @Bean(name = "directQueue")
    public Queue directQueue() {
        return QueueBuilder
                .durable(DIRECT_QUEUE)
                .quorum()
                .build();
    }

    @Bean(name = "directBinding")
    public Binding directBinding(Queue directQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(directQueue)
                .to(exchange)
                .with(DIRECT_ROUTING_KEY);
    }

}
