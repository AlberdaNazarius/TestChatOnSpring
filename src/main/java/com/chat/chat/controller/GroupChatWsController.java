package com.chat.chat.controller;

import com.chat.chat.model.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class GroupChatWsController {

    public static final String SEND_MESSAGE = "/groupChat/{id}/sendMessage";
    public static final String ADD_USER = "/groupChat/{id}/addUser";
    public static final String GROUP_CHAT = "/topic/groupChat/{id}";

    @MessageMapping(SEND_MESSAGE)
    @SendTo(GROUP_CHAT)
    public ChatMessage sendMessage(@DestinationVariable("id") String id,
                                   @Payload ChatMessage webSocketChatMessage) {
        // TODO add to database
        return webSocketChatMessage;
    }

    @MessageMapping(ADD_USER)
    @SendTo(GROUP_CHAT)
    public ChatMessage newUser(@DestinationVariable("id") String id,
                               @Payload ChatMessage webSocketChatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // TODO add to database
        // TODO nullPointerException
        headerAccessor.getSessionAttributes().put("username", webSocketChatMessage.getSender());
        return webSocketChatMessage;
    }
}
