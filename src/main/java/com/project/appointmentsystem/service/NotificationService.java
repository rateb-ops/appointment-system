package com.project.appointmentsystem.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendStatusNotification(Long appointmentId, String newStatus, Long customerId) {

        String message = String.format("تحديث الموعد #%d: الحالة أصبحت %s", appointmentId, newStatus);

        System.out.println(">>> Sending WebSocket Notification: " + message);
        messagingTemplate.convertAndSend("/topic/updates", message);
    }
}