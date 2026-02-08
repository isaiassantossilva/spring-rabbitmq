package com.santos.spring_rabbitmq.config;

import com.santos.spring_rabbitmq.config.property.QueueProperty;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    @ConfigurationProperties(prefix = "rabbitmq.queues.emails")
    public QueueProperty emailQueueProperty() {
        return new QueueProperty();
    }

    @Bean
    public Queue emailQueue(QueueProperty emailQueueProperty) {
        return QueueBuilder.durable(emailQueueProperty.getName()).build();
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
