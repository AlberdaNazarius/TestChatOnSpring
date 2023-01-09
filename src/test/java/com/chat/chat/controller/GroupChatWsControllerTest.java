package com.chat.chat.controller;

import com.chat.chat.MessageType;
import com.chat.chat.config.WebSocketConfig;
import com.chat.chat.model.GroupChatMessage;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.chat.chat.controller.GroupChatWsController.*;


@Log4j2
@RunWith(SpringJUnit4ClassRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GroupChatWsControllerTest {

    private WebClient client;

    @Value("${local.server.port}")
    private int port;

    @BeforeAll
    void setup() throws Exception {

        RunStopFrameHandler runStopFrameHandler = new RunStopFrameHandler(new CompletableFuture<>());
        String wsURL = "ws://localhost:" + port + WebSocketConfig.REGISTRY;
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSession stompSession = stompClient
                .connectAsync(wsURL, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        client = WebClient.builder()
                .stompClient(stompClient)
                .stompSession(stompSession)
                .handler(runStopFrameHandler)
                .build();
    }

    @AfterAll
    void tearDown() {
        if (client.getStompSession().isConnected()) {
            client.getStompSession().disconnect();
            client.getStompClient().stop();
        }
    }

   @Test
    void shouldSendMessage() throws ExecutionException, InterruptedException {
        String message = "Message";
        String id = "123";
        StompSession stompSession = client.getStompSession();
        RunStopFrameHandler handler = client.getHandler();

        stompSession.subscribe(convertFetchGroupMassages(id), handler);
        stompSession.send("/app" + convertSendMessage(id), GroupChatMessage.builder()
                .sender("Sender")
                .content(message)
                .type(MessageType.CHAT)
                .build()
        );

        GroupChatMessage chatMessage = (GroupChatMessage) handler.future.get();
        Assertions.assertEquals(message, chatMessage.getContent());

    }

    @Test
    void newUser() {
    }

    List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    @Data
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    class RunStopFrameHandler implements StompFrameHandler {

        CompletableFuture<Object> future;

        @Override
        public @NonNull Type getPayloadType(StompHeaders stompHeaders) {
            log.info(stompHeaders.toString());
            return GroupChatMessage.class;
        }

        @Override
        public void handleFrame(@NonNull StompHeaders stompHeaders, Object o) {
            log.info(o);
            future.complete(o);
            future = new CompletableFuture<>();
        }
    }

    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class WebClient {
        WebSocketStompClient stompClient;
        StompSession stompSession;
        String sessionToken;
        RunStopFrameHandler handler;
    }
}