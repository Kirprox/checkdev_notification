package ru.checkdev.notification.service.kafkaconsumer;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.checkdev.notification.dto.WisherApprovedDTO;
import ru.checkdev.notification.service.NotificationMessagesService;

@Service
@AllArgsConstructor
public class NotificationWisherKafkaConsumer {
    private final NotificationMessagesService notificationMessagesService;

    @KafkaListener(topics = "notification-approved-wisher")
    public void sendMessageApprovedWisher(
            @RequestBody WisherApprovedDTO wisherApprovedNotifyDTO) {
        notificationMessagesService.sendApprovedNotification(wisherApprovedNotifyDTO);
    }
}
