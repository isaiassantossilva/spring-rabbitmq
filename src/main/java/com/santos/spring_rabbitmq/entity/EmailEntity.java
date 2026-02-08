package com.santos.spring_rabbitmq.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@Entity(name = "Email")
@Table(name = "emails")
public class EmailEntity extends BaseEntity {

    @Column(name = "recipient")
    private String recipient;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body")
    private String body;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.PENDING;

    public enum Status {
        PENDING,
        SENT,
        FAILED
    }
}
