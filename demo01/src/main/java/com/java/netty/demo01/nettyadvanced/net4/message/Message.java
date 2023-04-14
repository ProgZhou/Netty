package com.java.netty.demo01.nettyadvanced.net4.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Message implements Serializable {

    public static Class<?> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    private int sequenceId;

    private int messageType;

    public abstract int getMessageType();

    public static final int LoginRequestMessage = 0;
    public static final int LoginResponseMessage = 1;
    public static final int ChatRequestMessage = 2;
    public static final int ChatResponseMessage = 3;
    public static final int GroupCreateRequestMessage = 4;
    public static final int GroupCreateResponseMessage = 5;
    public static final int GroupJoinRequestMessage = 6;
    public static final int GroupJoinResponseMessage = 7;
    public static final int GroupQuitRequestMessage = 8;
    public static final int GroupQuitResponseMessage = 9;
    public static final int GroupChatRequestMessage = 10;
    public static final int GroupChatResponseMessage = 11;
    public static final int GroupMembersRequestMessage = 12;
    public static final int GroupMembersResponseMessage = 13;
    private static final Map<Integer, Class<?>> messageClasses = new HashMap<>();

    static {
        messageClasses.put(LoginRequestMessage, com.java.netty.demo01.nettyadvanced.net4.message.LoginRequestMessage.class);
        messageClasses.put(LoginResponseMessage, com.java.netty.demo01.nettyadvanced.net4.message.LoginResponseMessage.class);
        messageClasses.put(ChatRequestMessage, com.java.netty.demo01.nettyadvanced.net4.message.ChatRequestMessage.class);
        messageClasses.put(ChatResponseMessage, com.java.netty.demo01.nettyadvanced.net4.message.ChatResponseMessage.class);
        messageClasses.put(GroupCreateRequestMessage, com.java.netty.demo01.nettyadvanced.net4.message.GroupCreateRequestMessage.class);
        messageClasses.put(GroupCreateResponseMessage, com.java.netty.demo01.nettyadvanced.net4.message.GroupCreateResponseMessage.class);
        messageClasses.put(GroupJoinRequestMessage, com.java.netty.demo01.nettyadvanced.net4.message.GroupJoinRequestMessage.class);
        messageClasses.put(GroupJoinResponseMessage, com.java.netty.demo01.nettyadvanced.net4.message.GroupJoinResponseMessage.class);
        messageClasses.put(GroupQuitRequestMessage, com.java.netty.demo01.nettyadvanced.net4.message.GroupQuitRequestMessage.class);
        messageClasses.put(GroupQuitResponseMessage, com.java.netty.demo01.nettyadvanced.net4.message.GroupQuitResponseMessage.class);
        messageClasses.put(GroupChatRequestMessage, com.java.netty.demo01.nettyadvanced.net4.message.GroupChatRequestMessage.class);
        messageClasses.put(GroupChatResponseMessage, com.java.netty.demo01.nettyadvanced.net4.message.GroupChatResponseMessage.class);
        messageClasses.put(GroupMembersRequestMessage, com.java.netty.demo01.nettyadvanced.net4.message.GroupMembersRequestMessage.class);
        messageClasses.put(GroupMembersResponseMessage, com.java.netty.demo01.nettyadvanced.net4.message.GroupMembersResponseMessage.class);
    }
}
