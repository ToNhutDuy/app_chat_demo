package com.example.demochat.model;

import com.google.firebase.Timestamp;

public class ChatMessageModel {
    private String message; // Nội dung tin nhắn
    private String senderId; // ID của người gửi
    private String senderImageUrl; // URL hình ảnh người gửi (nếu có)
    private Timestamp timestamp; // Thời gian gửi tin nhắn

    // Constructor mặc định (cần thiết cho Firebase Firestore)
    public ChatMessageModel() {
    }

    // Constructor đầy đủ
    public ChatMessageModel(String message, String senderId, String senderImageUrl, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.senderImageUrl = senderImageUrl;
        this.timestamp = timestamp;
    }

    // Getter và Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderImageUrl() {
        return senderImageUrl;
    }

    public void setSenderImageUrl(String senderImageUrl) {
        this.senderImageUrl = senderImageUrl;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    // Xác định tin nhắn có được gửi bởi người dùng hiện tại không
    public boolean isSentByCurrentUser(String currentUserId) {
        return senderId != null && senderId.equals(currentUserId);
    }
}
