package com.simpleoj.mqreceiver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SubmissionReceiverTest {
    @Autowired
    private SubmissionReceiver submissionReceiver;

    @Test
    void handleSubmitMsg() {
        submissionReceiver.handleSubmitMsg("8");

    }
}