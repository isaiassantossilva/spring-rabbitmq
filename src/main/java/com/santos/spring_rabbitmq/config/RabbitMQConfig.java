package com.santos.spring_rabbitmq.config;

import com.santos.spring_rabbitmq.config.property.QueueProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Configuration
public class RabbitMQConfig {

    private static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    private static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    private static final String DLX_SUFFIX = ".dlx";
    private static final String DLQ_SUFFIX = ".dlq";

    @Bean
    @ConfigurationProperties(prefix = "rabbitmq.queues.emails")
    public QueueProperty emailQueueProperty() {
        return new QueueProperty();
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper mapper) {
        return new JacksonJsonMessageConverter((JsonMapper) mapper);
    }

    @Bean
    public Queue emailQueue(QueueProperty props) {
        return QueueBuilder.durable(props.getName())
//                .withArgument(X_DEAD_LETTER_EXCHANGE, props.getName() + DLX_SUFFIX)
//                .withArgument(X_DEAD_LETTER_ROUTING_KEY, props.getName() + DLQ_SUFFIX)
                .deadLetterExchange(props.getName() + DLX_SUFFIX)
                .deadLetterRoutingKey(props.getName() + DLQ_SUFFIX)
                .build();
    }

    @Bean
    public DirectExchange emailDlx(QueueProperty props) {
        return new DirectExchange(props.getName() + DLX_SUFFIX);
    }

    @Bean
    public Queue emailDlq(QueueProperty props) {
        return QueueBuilder.durable(props.getName() + DLQ_SUFFIX).build();
    }

    @Bean
    public Binding dlqBinding(QueueProperty props, Queue emailDlq, DirectExchange emailDlx) {
        return BindingBuilder.bind(emailDlq).to(emailDlx).with(props.getName() + DLQ_SUFFIX);
    }

    @Bean
    public RabbitTemplateCustomizer rabbitTemplateCustomizer() {
        return template -> {
            template.setMandatory(true);

            template.setConfirmCallback((correlationData, ack, cause) -> {
                String correlationId = correlationData != null ? correlationData.getId() : null;

                if (ack) {
                    log.info("Publisher confirm ACK. correlationId={}", correlationId);
                } else {
                    log.error("Publisher confirm NACK. correlationId={} cause={}", correlationId, cause);
                }
            });

            template.setReturnsCallback(returned -> {
                log.error("Message returned: replyCode={}, replyText={}, exchange={}, routingKey={}, message={}",
                        returned.getReplyCode(), returned.getReplyText(), returned.getExchange(),
                        returned.getRoutingKey(), returned.getMessage());
            });
        };
    }

//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(messageConverter);
//        template.setMandatory(true);
//        return template;
//    }
//
//    @Bean
//    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
//        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
//        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
//        simpleRabbitListenerContainerFactory.setMessageConverter(messageConverter);
//        simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        simpleRabbitListenerContainerFactory.setPrefetchCount(10);
//        simpleRabbitListenerContainerFactory.setConcurrentConsumers(3);
//        simpleRabbitListenerContainerFactory.setMaxConcurrentConsumers(10);
//        return simpleRabbitListenerContainerFactory;
//    }
}
