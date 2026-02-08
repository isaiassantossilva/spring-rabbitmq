package com.santos.spring_rabbitmq.dto;

import lombok.Data;

@Data
public class EmailDTO {

    private String recipient;
    private String subject;
    private String body;
}
