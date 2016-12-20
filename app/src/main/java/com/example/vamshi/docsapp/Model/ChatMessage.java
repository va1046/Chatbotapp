package com.example.vamshi.docsapp.Model;

import com.orm.SugarRecord;

/**
 * Created by vamshi on 20-12-2016.
 */

public class ChatMessage extends SugarRecord/* implements Parcelable */ {

    public boolean left;
    public String Message;
    public boolean offline;

    public ChatMessage() {
    }

    public ChatMessage(boolean left, String message,boolean offline) {
        this.left = left;
        this.Message = message;
        this.offline = offline;
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

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    //    public static final Parcelable.Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
//        public ChatMessage createFromParcel(Parcel source) {
//            ChatMessage mChat = new ChatMessage();
//            mChat.left = source.readByte() != 0;
//            mChat.Message = source.readString();
//
//            return mChat;
//        }
//
//        public ChatMessage[] newArray(int size) {
//            return new ChatMessage[size];
//        }
//    };
//
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeByte((byte) (left ? 1 : 0));
//        parcel.writeString(getMessage());
//    }
//
//    private void readFromParcel(Parcel in) {
//        left = in.readByte() != 0;
//        Message = in.readString();
//    }
}
