package com.example.smsreciever;

public interface MessageListener {
    void messageReceived(String sender, String body);
}