package ru.checkdev.notification.service.kafkaconsumer;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.dto.CancelInterviewNotificationDTO;
import ru.checkdev.notification.dto.InterviewNotifyDTO;
import ru.checkdev.notification.dto.WisherDismissedDTO;
import ru.checkdev.notification.dto.WisherNotifyDTO;
import ru.checkdev.notification.service.InnerMessageService;
import ru.checkdev.notification.service.MessagesGenerator;
import ru.checkdev.notification.service.NotificationMessage;
import ru.checkdev.notification.service.UserTelegramService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class NotificationInterviewKafkaConsumer {
    private final UserTelegramService userTelegramService;
    private final InnerMessageService innerMessageService;
    private final NotificationMessage<UserTelegram, String, InnerMessage> notificationMessage;
    private final MessagesGenerator messagesGenerator;

    @KafkaListener(topics = "notification-topic")
    public void sendMessageSubscribeTopic(InterviewNotifyDTO interviewNotifyDTO) {
        List<UserTelegram> usersTopic = userTelegramService
                .findAllByTopicIdAndUserIdNot(interviewNotifyDTO.getTopicId(),
                        interviewNotifyDTO.getSubmitterId());
        var message = messagesGenerator.getMessageSubscribeTopic(interviewNotifyDTO);
        notificationMessage.sendMessage(usersTopic, message);
    }

    @KafkaListener(topics = "notification-participate")
    public void sendMessageSubmitterInterview(WisherNotifyDTO wisherNotifyDTO) {
        var message = messagesGenerator.getMessageParticipateWisher(wisherNotifyDTO);
        InnerMessage innerMessage = InnerMessage.of()
                .userId(wisherNotifyDTO.getSubmitterId())
                .text(message)
                .created(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .read(false)
                .interviewId(wisherNotifyDTO.getInterviewId())
                .build();
        innerMessageService.saveMessage(innerMessage);
        userTelegramService
                .findByUserId(wisherNotifyDTO.getSubmitterId())
                .ifPresent(
                        tg -> notificationMessage.sendMessage(tg, message)
                );
    }

    @KafkaListener(topics = "notification-cancel-interview")
    public void sendMessageCancelInterview(CancelInterviewNotificationDTO cancelInterviewDTO) {
        var message = messagesGenerator.getMessageCancelInterview(cancelInterviewDTO);
        InnerMessage innerMessage = InnerMessage.of()
                .userId(cancelInterviewDTO.getUserId())
                .text(message)
                .created(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .read(false)
                .interviewId(cancelInterviewDTO.getInterviewId())
                .build();
        CompletableFuture.supplyAsync(() -> innerMessageService.saveMessage(innerMessage));
        userTelegramService
                .findByUserId(cancelInterviewDTO.getUserId())
                .ifPresent(
                        tg -> notificationMessage.sendMessage(tg, message)
                );
    }

    @KafkaListener(topics = "notification-participant-isdismissed")
    public void sendMessageCancelInterview(List<WisherDismissedDTO> wisherDtoList) {
        wisherDtoList.parallelStream().forEach(wisher -> {
                    var message = messagesGenerator.getMessageDismissedWisher(wisher);
                    InnerMessage innerMessage = InnerMessage.of()
                            .userId(wisher.getUserId())
                            .text(message)
                            .created(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                            .read(false)
                            .interviewId(wisher.getInterviewId())
                            .build();
                    CompletableFuture.supplyAsync(() -> innerMessageService.saveMessage(innerMessage));
                    userTelegramService
                            .findByUserId(wisher.getUserId())
                            .ifPresent(
                                    tg -> notificationMessage.sendMessage(tg, message)
                            );
                }
        );
    }
}

