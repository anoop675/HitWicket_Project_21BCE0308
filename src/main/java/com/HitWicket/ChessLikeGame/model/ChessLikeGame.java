package com.HitWicket.ChessLikeGame.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChessLikeGame {

    private static final int BOARD_SIZE = 5;

    private char[][] board;
    private Map<String, Character> pieces;
    private String currentPlayer;
    private boolean gameOver;

    public ChessLikeGame() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        pieces = new HashMap<>();
        currentPlayer = "A";
        gameOver = false;
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = ' ';
            }
        }
    }

    public void placePieces(String player, Map<String, Integer[]> initialPositions) {
        for (Map.Entry<String, Integer[]> entry : initialPositions.entrySet()) {
            String piece = entry.getKey();
            Integer[] position = entry.getValue();
            int row = position[0];
            int col = position[1];

            if (isValidPosition(row, col) && board[row][col] == ' ') {
                board[row][col] = piece.charAt(0);
                pieces.put(player + "-" + piece, board[row][col]);
            } else {
                throw new IllegalArgumentException("Invalid position or cell occupied");
            }
        }
    }

    public boolean processMove(String player, String piece, String moveCommand) {
        if (gameOver) throw new IllegalStateException("Game is over");

        if (!player.equals(currentPlayer)) return false;

        Character pieceChar = pieces.get(player + "-" + piece);
        if (pieceChar == null) return false;

        int[] move = parseMoveCommand(moveCommand);
        if (move == null) return false;

        Integer[] position = findPiecePosition(player, piece);
        if (position == null) return false;

        int fromRow = position[0];
        int fromCol = position[1];
        int toRow = fromRow + move[0];
        int toCol = fromCol + move[1];

        if (!isValidMove(pieceChar, fromRow, fromCol, toRow, toCol)) return false;

        char targetCell = board[toRow][toCol];
        if (targetCell != ' ' && targetCell != pieceChar) {
            pieces.remove(getPieceKeyByPosition(toRow, toCol));
        }

        board[toRow][toCol] = pieceChar;
        board[fromRow][fromCol] = ' ';
        pieces.put(player + "-" + piece, pieceChar);

        if (checkGameOver()) {
            gameOver = true;
        }

        currentPlayer = currentPlayer.equals("A") ? "B" : "A";

        return true;
    }

    private boolean isValidMove(Character pieceChar, int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidPosition(toRow, toCol)) return false;

        if (pieceChar == 'P') {
            // Pawn move validation
            return Math.abs(toRow - fromRow) <= 1 && Math.abs(toCol - fromCol) <= 1;
        } else if (pieceChar == 'H') {
            // Hero move validation
            return Math.abs(toRow - fromRow) <= 2 && Math.abs(toCol - fromCol) <= 2;
        }

        return true;
    }

    private int[] parseMoveCommand(String moveCommand) {
        switch (moveCommand) {
            case "L": return new int[]{0, -1};
            case "R": return new int[]{0, 1};
            case "F": return new int[]{-1, 0};
            case "B": return new int[]{1, 0};
            case "FL": return new int[]{-1, -1};
            case "FR": return new int[]{-1, 1};
            case "BL": return new int[]{1, -1};
            case "BR": return new int[]{1, 1};
            default: return null;
        }
    }

    private Integer[] findPiecePosition(String player, String piece) {
        char pieceChar = pieces.get(player + "-" + piece);
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == pieceChar) {
                    return new Integer[]{row, col};
                }
            }
        }
        return null;
    }

    private String getPieceKeyByPosition(int row, int col) {
        for (Map.Entry<String, Character> entry : pieces.entrySet()) {
            String key = entry.getKey();
            Character value = entry.getValue();
            Integer[] position = findPiecePositionByChar(value);
            if (position != null && position[0] == row && position[1] == col) {
                return key;
            }
        }
        return null;
    }

    private Integer[] findPiecePositionByChar(char pieceChar) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == pieceChar) {
                    return new Integer[]{row, col};
                }
            }
        }
        return null;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }
    
    public Map<String, Object> getBoardState() {
        Map<String, Object> state = new HashMap<>();
        state.put("board", convertBoardToList());
        state.put("currentPlayer", currentPlayer);
        state.put("gameOver", gameOver);
        return state;
    }
    
    private List<List<Character>> convertBoardToList() {
        List<List<Character>> boardList = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            List<Character> row = new ArrayList<>();
            for (int j = 0; j < BOARD_SIZE; j++) {
                row.add(board[i][j]);
            }
            boardList.add(row);
        }
        return boardList;
    }

    private boolean checkGameOver() {
        Set<String> players = new HashSet<>();
        players.add("A");
        players.add("B");

        for (String player : players) {
            boolean hasPiecesLeft = false;
            for (Map.Entry<String, Character> entry : pieces.entrySet()) {
                if (entry.getKey().startsWith(player + "-")) {
                    hasPiecesLeft = true;
                    break;
                }
            }
            if (!hasPiecesLeft) {
                return true;
            }
        }

        return false;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
