package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChessBoardUI extends EscapeSequences {
    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private ChessGame chessGame;
    private ChessBoard chessBoard;
    private HashMap<Integer, List<Integer>> validMoves = new HashMap<>();

    public ChessBoardUI(ChessGame game){
        chessGame = game;
        chessBoard = game.getBoard();
    }

    public void createWhiteChessBoard(){
        out.print(ERASE_SCREEN);

        String[] letters = {"\u2002\u2009a\u2009\u2002", "\u2002\u2009b\u2009\u2002", "\u2002\u2009c\u2009\u2002", "\u2002\u2009d\u2009\u2002", "\u2002\u2009e\u2009\u2002", "\u2002f\u2009\u2002", "\u2002\u2009g\u2009\u2002", "\u2002h\u2009\u2002"};
        printHeaders(letters);
        printWhiteChessBoard();
        printHeaders(letters);
        out.print("\u001b[0m");
    }

    public void createBlackChessBoard(){
        out.print(ERASE_SCREEN);

        String[] letters = {"\u2002\u2009h\u2009\u2002", "\u2002\u2009g\u2009\u2002", "\u2002\u2009f\u2009\u2002", "\u2002\u2009e\u2009\u2002", "\u2002\u2009d\u2009\u2002", "\u2002c\u2009\u2002", "\u2002\u2009b\u2009\u2002", "\u2002a\u2009\u2002"};
        printHeaders(letters);
        printBlackChessBoard();
        printHeaders(letters);
        out.print("\u001b[0m");
    }

    public void whiteChessBoardHighlight(ChessPosition position){
        out.print(ERASE_SCREEN);

        for (var r :chessGame.validMoves(position)){
            validMoves.computeIfAbsent(r.getEndPosition().getRow(), k -> new ArrayList<>()).add(r.getEndPosition().getColumn());
        }

        String[] letters = {"\u2002\u2009a\u2009\u2002", "\u2002\u2009b\u2009\u2002", "\u2002\u2009c\u2009\u2002", "\u2002\u2009d\u2009\u2002", "\u2002\u2009e\u2009\u2002", "\u2002f\u2009\u2002", "\u2002\u2009g\u2009\u2002", "\u2002h\u2009\u2002"};
        printHeaders(letters);
        printWhiteChessBoardHighlight(position);
        printHeaders(letters);
        out.print("\u001b[0m");
    }

    public void blackChessBoardHighlight(ChessPosition position){
        out.print(ERASE_SCREEN);

        for (var r :chessGame.validMoves(position)){
            validMoves.computeIfAbsent(r.getEndPosition().getRow(), k -> new ArrayList<>()).add(r.getEndPosition().getColumn());
        }

        String[] letters = {"\u2002\u2009h\u2009\u2002", "\u2002\u2009g\u2009\u2002", "\u2002\u2009f\u2009\u2002", "\u2002\u2009e\u2009\u2002", "\u2002\u2009d\u2009\u2002", "\u2002c\u2009\u2002", "\u2002\u2009b\u2009\u2002", "\u2002a\u2009\u2002"};
        printHeaders(letters);
        printBlackChessBoardHighlight(position);
        printHeaders(letters);
        out.print("\u001b[0m");
    }

    public void printHeaders(String[] headers){
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        for (int i = 0; i < 10; i++){
            if (i == 0 || i == 9) {
                out.print(EMPTY);
            }
            else {
                out.print(headers[i - 1]);
            }
        }
        out.print(SET_BG_COLOR_BLACK);
        out.println();
    }

    public void printWhiteChessBoard(){
        for (int i = 8; i > 0; i--){
            for (int j = 0; j < 10; j++){
                if (j == 0 || j == 9) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                    out.print(SET_TEXT_COLOR_BLACK);
                    out.print("\u2002\u2009" + i + "\u2009\u2002");
                }
                else {
                    whiteChessSpace(i, j, SET_BG_COLOR_WHITE, SET_BG_COLOR_GREEN);

                }
            }
            out.print(SET_BG_COLOR_BLACK);
            out.println();
        }
    }

    public void printBlackChessBoard(){
        for (int i = 1; i <= 8; i++){
            for (int j = 9; j >= 0; j--){
                if (j == 0 || j == 9) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                    out.print(SET_TEXT_COLOR_BLACK);
                    out.print("\u2002\u2009" + i + "\u2009\u2002");
                }
                else {
                    blackChessSpace(i, j, SET_BG_COLOR_WHITE, SET_BG_COLOR_GREEN);
                }
            }
            out.print(SET_BG_COLOR_BLACK);
            out.println();
        }
    }

    public void printWhiteChessBoardHighlight(ChessPosition position){
        for (int i = 8; i > 0; i--){
            for (int j = 0; j < 10; j++){
                if (j == 0 || j == 9) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                    out.print(SET_TEXT_COLOR_BLACK);
                    out.print("\u2002\u2009" + i + "\u2009\u2002");
                }
                else {
                    if (i == position.getRow() && j == position.getColumn()) {
                        out.print(SET_BG_COLOR_YELLOW);
                        out.print(evaluatePiece(i, j));
                        continue;
                    }
                    if (validMoves.containsKey(i) && validMoves.get(i).contains(j)){
                        if (Objects.equals(evaluatePiece(i, j), EMPTY)) {
                            whiteChessSpace(i, j, SET_BG_COLOR_BLUE, SET_BG_COLOR_DARK_BLUE);
                        } else {
                            out.print(SET_BG_COLOR_RED);
                            out.print(evaluatePiece(i, j));
                        }
                        continue;
                    }

                    whiteChessSpace(i, j, SET_BG_COLOR_WHITE, SET_BG_COLOR_GREEN);

                }
            }
            out.print(SET_BG_COLOR_BLACK);
            out.println();
        }
    }

    private void whiteChessSpace(int i, int j, String setBgColorLighter, String setBgColorDarker) {
        if (i % 2 == 0){
            if (j % 2 == 1) {
                out.print(setBgColorLighter);
                out.print(evaluatePiece(i,j));
            } else {
                out.print(setBgColorDarker);
                out.print(evaluatePiece(i,j));
            }
        } else {
            if (j % 2 == 0) {
                out.print(setBgColorLighter);
                out.print(evaluatePiece(i,j));
            } else {
                out.print(setBgColorDarker);
                out.print(evaluatePiece(i,j));
            }
        }
    }

    public void printBlackChessBoardHighlight(ChessPosition position){
        for (int i = 1; i <= 8; i++){
            for (int j = 9; j >= 0; j--){
                if (j == 0 || j == 9) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                    out.print(SET_TEXT_COLOR_BLACK);
                    out.print("\u2002\u2009" + i + "\u2009\u2002");
                }
                else {
                    if (i == position.getRow() && j == position.getColumn()) {
                        out.print(SET_BG_COLOR_YELLOW);
                        out.print(evaluatePiece(i, j));
                        continue;
                    }

                    if (validMoves.containsKey(i) && validMoves.get(i).contains(j)){
                        if (Objects.equals(evaluatePiece(i, j), EMPTY)) {
                            blackChessSpace(i, j, SET_BG_COLOR_BLUE, SET_BG_COLOR_DARK_BLUE);
                        } else {
                            out.print(SET_BG_COLOR_RED);
                            out.print(evaluatePiece(i, j));
                        }
                        continue;
                    }

                    blackChessSpace(i, j, SET_BG_COLOR_WHITE, SET_BG_COLOR_GREEN);

                }
            }
            out.print(SET_BG_COLOR_BLACK);
            out.println();
        }
    }

    private void blackChessSpace(int i, int j, String lighterColor, String darkerColor) {
        if (i % 2 == 1){
            if (j % 2 == 0) {
                out.print(lighterColor);
                out.print(evaluatePiece(i,j));
            } else {
                out.print(darkerColor);
                out.print(evaluatePiece(i,j));
            }
        } else {
            if (j % 2 == 1) {
                out.print(lighterColor);
                out.print(evaluatePiece(i,j));
            } else {
                out.print(darkerColor);
                out.print(evaluatePiece(i,j));
            }
        }
    }

    private String evaluatePiece(int x, int y){
        ChessPiece piece = chessBoard.getPiece(new ChessPosition(x,y));
        if (piece == null){
            return EMPTY;
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
        {
            return switch (piece.getPieceType()){
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            };
        } else {
            return switch (piece.getPieceType()){
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
            };
        }

    }

}
