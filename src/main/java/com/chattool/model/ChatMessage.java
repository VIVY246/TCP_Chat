package com.chattool.model;

public class ChatMessage {
    public String from;
    public String to;
    public String message;
    public String timestamp;

    // コンストラクタを追加
    public ChatMessage(String from, String to, String message, long timestamp) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.timestamp = String.valueOf(timestamp); // long を String に変換
    }
}