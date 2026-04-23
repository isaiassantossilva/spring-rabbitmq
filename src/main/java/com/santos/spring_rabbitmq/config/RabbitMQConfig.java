package com.santos.spring_rabbitmq.config;

import com.santos.spring_rabbitmq.config.property.QueueProperty;
import org.springframework.amqp.core.*;
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
    public Queue emailQueue(QueueProperty props) {
        return QueueBuilder.durable(props.getName())
                .withArgument("x-dead-letter-exchange", "emails.dlx")
                .withArgument("x-dead-letter-routing-key", "emails.dlq")
                .build();
    }

    @Bean
    public DirectExchange emailDlx() {
        return new DirectExchange("emails.dlx");
    }

    @Bean
    public Queue emailDlq() {
        return QueueBuilder.durable("emails.dlq").build();
    }

    @Bean
    public Binding dlqBinding(Queue emailDlq, DirectExchange emailDlx) {
        return BindingBuilder.bind(emailDlq).to(emailDlx).with("emails.dlq");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
