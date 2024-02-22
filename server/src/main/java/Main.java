import chess.*;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import model.AuthData;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        MemoryAuthDAO temp = new MemoryAuthDAO();

        try {
            AuthData cheese = temp.createAuth("cheese");
            temp.deleteAllAuths();

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}