package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public GameData updateGameData(String whiteUsername, String blackUsername, String gameName, ChessGame game){
        return new GameData(this.gameID(), whiteUsername, blackUsername, gameName, game);
    }
}
