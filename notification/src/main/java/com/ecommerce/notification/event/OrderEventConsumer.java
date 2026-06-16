package com.ecommerce.notification.event;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Component
public class OrderEventConsumer {

  @RabbitListener(queues = "${rabbitmq.queue.name}")
  public void handleOrderEvent (Map<String, Object> orderEvent) {
    System.out.println("Received Order Event: " + orderEvent);
  }
}
