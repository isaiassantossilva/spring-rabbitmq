package com.santos.spring_rabbitmq.service;

import com.santos.spring_rabbitmq.config.property.QueueProperty;
import com.santos.spring_rabbitmq.dto.EmailDTO;
import com.santos.spring_rabbitmq.entity.EmailEntity;
import com.santos.spring_rabbitmq.gateway.EmailGateway;
import com.santos.spring_rabbitmq.mapper.EmailMapper;
import com.santos.spring_rabbitmq.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailMapper emailMapper;
    private final EmailRepository emailRepository;
    private final RabbitTemplate rabbitTemplate;
    private final EmailGateway emailGateway;
    private final QueueProperty emailQueueProperty;

    public void sendToQueue(EmailDTO emailDTO) {
        log.info("Queueing email: {}", emailDTO);
        this.rabbitTemplate.convertAndSend(this.emailQueueProperty.getName(), emailDTO);
    }

    public void sendToRecipient(EmailDTO emailDTO) {
        log.info("Receiving email: {}", emailDTO);

        if (emailDTO.getRecipient() == null || emailDTO.getRecipient().isEmpty()) {
            log.error("Email recipient is missing: {}", emailDTO);
            throw new IllegalStateException("Email recipient is required");
        }

        EmailEntity emailEntity = this.emailMapper.toEmailEntity(emailDTO);
        this.emailRepository.save(emailEntity);

        log.info("Sending email: {}", emailEntity);

        try {
            this.emailGateway.sendEmail(emailEntity);
            emailEntity.setStatus(EmailEntity.Status.SENT);
            log.info("Email sent: {}", emailEntity);
        } catch (Exception e) {
            log.error("Email sent failed: {}", emailEntity, e);
            emailEntity.setStatus(EmailEntity.Status.FAILED);
        } finally {
            this.emailRepository.save(emailEntity);
        }
    }
}
