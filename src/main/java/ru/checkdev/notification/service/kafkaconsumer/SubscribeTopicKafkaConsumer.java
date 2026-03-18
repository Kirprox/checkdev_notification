package ru.checkdev.notification.service.kafkaconsumer;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.SubscribeTopic;
import ru.checkdev.notification.service.SubscribeTopicService;

@Service
@AllArgsConstructor
public class SubscribeTopicKafkaConsumer {
    private final SubscribeTopicService service;

    @KafkaListener(topics = "notification-add-subscr-topic")
    public void toAddSubscribeTopic(SubscribeTopic subscribetopic){
        service.save(subscribetopic);
    }

    @KafkaListener(topics = "notification-add-subscr-topic")
    public void toDeleteSubscribeTopic(SubscribeTopic subscribetopic){
        service.delete(subscribetopic);
    }
}
