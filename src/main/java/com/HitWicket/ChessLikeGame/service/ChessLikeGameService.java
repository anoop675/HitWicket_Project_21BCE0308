package com.HitWicket.ChessLikeGame.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.HitWicket.ChessLikeGame.model.ChessLikeGame;
@Service
public class ChessLikeGameService {

    private ChessLikeGame game;

    public ChessLikeGameService() {
        this.game = new ChessLikeGame();
    }

    public ChessLikeGame getGame() {
        return game;
    }

    public void initializeGame() {
        game = new ChessLikeGame();
    }

    public boolean processMove(String player, String piece, String moveCommand) {
        return game.processMove(player, piece, moveCommand);
    }

    public Map<String, Object> getBoardState() {
        return game.getBoardState();
    }
}
