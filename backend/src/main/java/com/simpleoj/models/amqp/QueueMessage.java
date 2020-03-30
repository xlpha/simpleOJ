package com.simpleoj.models.amqp;

import lombok.Data;

@Data
public class QueueMessage {
    private Long id;

    public QueueMessage(Long id) {
        this.id = id;
    }
}
