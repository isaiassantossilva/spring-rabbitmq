package com.santos.spring_rabbitmq.listener;

import com.santos.spring_rabbitmq.dto.EmailDTO;
import com.santos.spring_rabbitmq.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailListener {

    private final EmailService emailService;

    @RabbitListener(queues = "${rabbitmq.queues.emails.name}", concurrency = "${rabbitmq.queues.emails.concurrency}")
    public void onReceiveEmail(EmailDTO emailDTO) {
        this.emailService.sendToRecipient(emailDTO);
    }
}
