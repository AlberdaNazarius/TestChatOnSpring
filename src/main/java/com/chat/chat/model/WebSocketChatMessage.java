package com.chat.chat.model;

import com.chat.chat.MessageType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebSocketChatMessage {
    String sender;
    String content;
    String date;
    MessageType type;
}
