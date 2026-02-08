package com.santos.spring_rabbitmq.gateway;

import com.santos.spring_rabbitmq.entity.EmailEntity;
import com.santos.spring_rabbitmq.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailGateway {

    public void sendEmail(EmailEntity emailEntity) {
        ThreadUtil.sleepSeconds(2); // Simulating email sending delay
    }
}
