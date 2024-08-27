package com.HitWicket.ChessLikeGame.controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;

import com.HitWicket.ChessLikeGame.service.ChessLikeGameService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ChessLikeGameController extends TextWebSocketHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(ChessLikeGameController.class);

    @Autowired
    private ChessLikeGameService gameService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
    	
    	logger.info(message.getPayload().toString());
    	
        String payload = ((TextMessage) message).getPayload();
        Map<String, String> data = objectMapper.readValue(payload, Map.class);

        String type = data.get("type");
        switch (type) {
            case "initialize":
                handleInitialize(session);
                break;
            case "move":
                handleMove(session, data);
                break;
        }
    }

    private void handleInitialize(WebSocketSession session) throws IOException {
        gameService.initializeGame();
        sendBoardState(session);
    }

    private void handleMove(WebSocketSession session, Map<String, String> data) throws IOException {
        String player = data.get("player");
        String piece = data.get("piece");
        String move = data.get("move");

        boolean validMove = gameService.processMove(player, piece, move);

        if (validMove) {
            sendBoardState(session);
        } else {
            sendError(session, "Invalid move");
        }
    }

    private void sendBoardState(WebSocketSession session) throws IOException {
        String state = objectMapper.writeValueAsString(gameService.getBoardState());
        session.sendMessage(new TextMessage(state));
    }

    private void sendError(WebSocketSession session, String error) throws IOException {
        Map<String, String> errorMessage = Map.of("error", error);
        String errorJson = objectMapper.writeValueAsString(errorMessage);
        session.sendMessage(new TextMessage(errorJson));
    }
}