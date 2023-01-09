package com.chat.chat.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Group {
    @Builder.Default
    String id = UUID.randomUUID().toString();
    String name;
}
