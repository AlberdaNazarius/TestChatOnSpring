package com.chat.chat.controller;

import com.chat.chat.model.GroupChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GroupChatWsController {

    public static final String SEND_MESSAGE = "/groupChat/{id}/sendMessage";
    public static final String ADD_USER = "/groupChat/{id}/addUser";

    public static final String FETCH_GROUP_MASSAGES = "/topic/groupChat/{id}/messages";
    //public static final String FETCH_USER_UNSUBSCRIBE = "/topic/groupChat/{id}/unsubscribe";
    //public static final String FETCH_USER_LEAVE_GROUP_CHAT = "/topic/groupChat/{id}/user/leave";

    @MessageMapping(SEND_MESSAGE)
    @SendTo(FETCH_GROUP_MASSAGES)
    public GroupChatMessage sendMessage(@DestinationVariable("id") String id,
                                        @Payload GroupChatMessage webSocketChatMessage) {
        // TODO add to database
        return webSocketChatMessage;
    }

    @MessageMapping(ADD_USER)
    @SendTo(FETCH_GROUP_MASSAGES)
    public GroupChatMessage newUser(@DestinationVariable("id") String id,
                                    @Payload GroupChatMessage webSocketChatMessage,
                                    SimpMessageHeaderAccessor headerAccessor) {
        // TODO add to database
        // TODO nullPointerException
        headerAccessor.getSessionAttributes().put("username", webSocketChatMessage.getSender());
        return webSocketChatMessage;
    }

    public static String convertFetchGroupMassages (String  groupChatId) {
        return FETCH_GROUP_MASSAGES.replace("{id}", groupChatId);
    }

    public static String convertSendMessage (String groupChatId) {
        return SEND_MESSAGE.replace("{id}", groupChatId);
    }

//    @SubscribeMapping(FETCH_USER_LEAVE_GROUP_CHAT)
//    public User fetchUserLeaveGroupChat () {
//        return null;
//    }
}
