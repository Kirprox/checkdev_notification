package ru.checkdev.notification.service.kafkaconsumer;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.dto.FeedbackNotificationDTO;
import ru.checkdev.notification.service.NotificationMessagesService;
@AllArgsConstructor
@Service
public class FeedbackNotificationKafkaConsumer {
    private final NotificationMessagesService notificationMessagesService;

    @KafkaListener(topics = "feedback-notification")
    public void sendFeedbackNotification(FeedbackNotificationDTO feedbackNotification){
        notificationMessagesService.sendFeedbackNotification(feedbackNotification);
    }
}
