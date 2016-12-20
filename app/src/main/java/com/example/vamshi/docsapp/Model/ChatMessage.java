package com.example.vamshi.docsapp.Model;

/**
 * Created by vamshi on 20-12-2016.
 */

public class ChatMessage {

    public boolean left;
    public String Message;

    public ChatMessage(boolean left, String message) {
        this.left = left;
        Message = message;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
