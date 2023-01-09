package com.chat.chat.model;

import com.chat.chat.MessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupChatMessage {
    String sender;
    String content;
    String date;
    MessageType type;
}
