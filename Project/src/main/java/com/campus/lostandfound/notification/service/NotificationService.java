package com.campus.lostandfound.notification.service;

import com.campus.lostandfound.notification.model.Notification;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationService {
    private final Map<String, Notification> notificationsById = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(100);

    public Notification sendNotification(String userId, String title, String message, String type, String referenceId) {
        String id = "NOTIF-" + counter.incrementAndGet();
        Notification notif = new Notification(id, userId, title, message, type, referenceId, false, Instant.now());
        notificationsById.put(id, notif);
        return notif;
    }

    public List<Notification> getNotificationsForUser(String userId) {
        return notificationsById.values().stream()
                .filter(n -> n.recipientUserId().equalsIgnoreCase(userId) || "ALL".equalsIgnoreCase(n.recipientUserId()))
                .sorted(Comparator.comparing(Notification::timestamp).reversed())
                .toList();
    }

    public void markAsRead(String id) {
        Notification n = notificationsById.get(id);
        if (n != null) {
            notificationsById.put(id, n.withRead(true));
        }
    }

    public List<Notification> getAllNotifications() {
        return notificationsById.values().stream()
                .sorted(Comparator.comparing(Notification::timestamp).reversed())
                .toList();
    }
}
