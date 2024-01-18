package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> possibleMoves = new ArrayList<ChessMove>();

        if (piece.type == PieceType.PAWN){

        } else if (piece.type == PieceType.KNIGHT){

        } else if (piece.type == PieceType.ROOK){

        } else if (piece.type == PieceType.BISHOP){
            System.out.println("FOUND");
            System.out.printf("%d, %d", myPosition.getRow(), myPosition.getColumn());
//            while (true){
//                if ((myPosition.getRow()+1) <= 8 && (myPosition.getColumn()+1) <= 8 &&
//                        board.getPiece(new ChessPosition(myPosition.getRow()+1,myPosition.getColumn()+1)).pieceColor != piece.pieceColor){
//                    possibleMoves.add(new ChessMove(new ChessPosition(0,0), new ChessPosition(0,0),null));
//
//                }
//            }
        } else if (piece.type == PieceType.KING){

        } else if (piece.type == PieceType.QUEEN){

        }
        return possibleMoves;
    }
}
