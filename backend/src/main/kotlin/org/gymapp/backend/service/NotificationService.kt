package org.gymapp.backend.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.gymapp.backend.model.GymMember
import org.springframework.stereotype.Service

@Service
class NotificationService {

    fun sendNotification(token: String, title: String, body: String) {
        val notification: Notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build()

        val message: Message = Message.builder()
            .setToken(token)
            .setNotification(notification)
            .build()

        val response = FirebaseMessaging.getInstance().send(message)
        println("Sent message: $response")
    }

    fun sendNotifications(tokens: List<String>, title: String, body: String) {
        for (token in tokens) {
            sendNotification(token, title, body)
        }
    }

    fun notifyParticipants(participants: MutableList<GymMember>, title: String, body: String) {
        print(participants.toString())
        val tokens = participants.map { it.gymUser.user.fcmToken ?: "" }
        sendNotifications(tokens, title, body)
    }
}