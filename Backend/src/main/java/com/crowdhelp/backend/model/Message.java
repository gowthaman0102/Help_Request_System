package com.crowdhelp.backend.model;

public class Message {
    private int id;
    private int requestId;
    private int senderId;
    private int receiverId;
    private String senderName;
    private String content;
    private String timestamp;

    public Message() {}

    public Message(int requestId, int senderId, int receiverId, String content) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}

