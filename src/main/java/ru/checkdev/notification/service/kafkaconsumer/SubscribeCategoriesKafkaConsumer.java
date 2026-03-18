package ru.checkdev.notification.service.kafkaconsumer;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.SubscribeCategory;
import ru.checkdev.notification.service.SubscribeCategoryService;

@Service
@AllArgsConstructor
public class SubscribeCategoriesKafkaConsumer {
    private final SubscribeCategoryService service;

    @KafkaListener(topics = "notification-add-subscr-category")
    public void toAddSubscribeCategory(SubscribeCategory subscribeCategory) {
        service.save(subscribeCategory);
    }
    @KafkaListener(topics = "notification-delete-subscr-category")
    public void toDeleteSubscribeCategory(SubscribeCategory subscribeCategory) {
        service.delete(subscribeCategory);
    }
}
