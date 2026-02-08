package com.santos.spring_rabbitmq.controller;

import com.santos.spring_rabbitmq.dto.EmailDTO;
import com.santos.spring_rabbitmq.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping
    public void sendEmail(@RequestBody EmailDTO emailDTO) {
        this.emailService.sendToQueue(emailDTO);
    }
}
