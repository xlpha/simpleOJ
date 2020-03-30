package com.simpleoj;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class SimpleOJApplicationTests {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("address", "chengdu");
        rabbitTemplate.convertAndSend("simpleOJ", "", map);
    }

    @Test
    void test2() {
        Object o = rabbitTemplate.receiveAndConvert("submission");
        Message receive = rabbitTemplate.receive();
        System.out.println(o);
    }

}
