package ui;

import chess.ChessBoard;
import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ChessBoardUI extends EscapeSequences {
    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);;
    private ChessGame chessGame;
    private ChessBoard chessBoard;

    public ChessBoardUI(ChessGame game){
        chessGame = game;
        chessBoard = game.getBoard();
    }

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        var chess = new ChessGame();
        chess.getBoard().resetBoard();
        ChessBoardUI test = new ChessBoardUI(chess);
        test.createWhiteChessBoard();
    }

    public void createWhiteChessBoard(){
        out.print(ERASE_SCREEN);

        String[] letters = {"\u2002\u2009a\u2009\u2002", "\u2002\u2009b\u2009\u2002", "\u2002\u2009c\u2009\u2002", "\u2002\u2009d\u2009\u2002", "\u2002\u2009e\u2009\u2002", "\u2002f\u2009\u2002", "\u2009\u2009\u2009g\u2009\u2002", "\u2009\u2009\u2009h\u2009\u2002"};
        printHeaders(letters);
        printChessBoard();
        printHeaders(letters);
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
        out.println();
    }

    public void printChessBoard(){
        for (int i = 8; i > 0; i--){
            for (int j = 0; j < 10; j++){
                if (j == 0 || j == 9) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                    out.print(SET_TEXT_COLOR_BLACK);
                    out.print("\u2002\u2009" + i + "\u2009\u2002");
                }
                else {
                    if (i % 2 == 0){
                        if (j % 2 == 1) {
                            out.print(SET_BG_COLOR_WHITE);
                            out.print(EMPTY);
                        } else {
                            out.print(SET_BG_COLOR_BLACK);
                            out.print(EMPTY);
                        }
                    } else {
                        if (j % 2 == 0) {
                            out.print(SET_BG_COLOR_WHITE);
                            out.print(EMPTY);
                        } else {
                            out.print(SET_BG_COLOR_BLACK);
                            out.print(EMPTY);
                        }
                    }

                }
            }
            out.println();
        }
    }

}
