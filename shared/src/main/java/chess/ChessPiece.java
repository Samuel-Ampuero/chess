package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.Vector;

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
        if (piece.type == PieceType.PAWN){

        } else if (piece.type == PieceType.KNIGHT){

        } else if (piece.type == PieceType.ROOK){

        } else if (piece.type == PieceType.BISHOP){
            return bishopPiece(board, myPosition, piece);
        } else if (piece.type == PieceType.KING){

        } else if (piece.type == PieceType.QUEEN){

        }
        return null;
    }

    public Collection<ChessMove> bishopPiece(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Vector<ChessMove> possibleMoves = new Vector<>();
        //System.out.println("FOUND"); //testing
        //System.out.printf("Position = %d, %d\nPossibilities:\n", myPosition.getRow(), myPosition.getColumn()); //testing
        int tempX = myPosition.getRow();
        int tempY = myPosition.getColumn();
            while (true){
                tempX++;
                tempY++;

                if (tempX <= 8 && tempY <= 8 && tempX > 0 && tempY > 0 &&
                        (board.getPiece(new ChessPosition(tempX,tempY)) == null || board.getPiece(new ChessPosition(tempX,tempY)).pieceColor != piece.pieceColor)){
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(tempX,tempY),null));
                    //System.out.printf("%d, %d\n", tempX, tempY);
                    if (board.getPiece(new ChessPosition(tempX,tempY)) != null) {
                        if (board.getPiece(new ChessPosition(tempX, tempY)).pieceColor == ChessGame.TeamColor.BLACK && piece.pieceColor == ChessGame.TeamColor.WHITE) {
                            break;
                        } else if (board.getPiece(new ChessPosition(tempX, tempY)).pieceColor == ChessGame.TeamColor.WHITE && piece.pieceColor == ChessGame.TeamColor.BLACK) {
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
        tempX = myPosition.getRow();
        tempY = myPosition.getColumn();
        while (true){
            tempX--;
            tempY++;

            if (tempX <= 8 && tempY <= 8 && tempX > 0 && tempY > 0 &&
                    (board.getPiece(new ChessPosition(tempX,tempY)) == null || board.getPiece(new ChessPosition(tempX,tempY)).pieceColor != piece.pieceColor)){
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(tempX,tempY),null));
                //System.out.printf("%d, %d\n", tempX, tempY);
                if (board.getPiece(new ChessPosition(tempX,tempY)) != null) {
                    if (board.getPiece(new ChessPosition(tempX, tempY)).pieceColor == ChessGame.TeamColor.BLACK && piece.pieceColor == ChessGame.TeamColor.WHITE) {
                        break;
                    } else if (board.getPiece(new ChessPosition(tempX, tempY)).pieceColor == ChessGame.TeamColor.WHITE && piece.pieceColor == ChessGame.TeamColor.BLACK) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
        tempX = myPosition.getRow();
        tempY = myPosition.getColumn();
        while (true){
            tempX--;
            tempY--;

            if (tempX <= 8 && tempY <= 8 && tempX > 0 && tempY > 0 &&
                    (board.getPiece(new ChessPosition(tempX,tempY)) == null || board.getPiece(new ChessPosition(tempX,tempY)).pieceColor != piece.pieceColor)){
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(tempX,tempY),null));
                //System.out.printf("%d, %d\n", tempX, tempY);
                if (board.getPiece(new ChessPosition(tempX,tempY)) != null) {
                    if (board.getPiece(new ChessPosition(tempX, tempY)).pieceColor == ChessGame.TeamColor.BLACK && piece.pieceColor == ChessGame.TeamColor.WHITE) {
                        break;
                    } else if (board.getPiece(new ChessPosition(tempX, tempY)).pieceColor == ChessGame.TeamColor.WHITE && piece.pieceColor == ChessGame.TeamColor.BLACK) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
        tempX = myPosition.getRow();
        tempY = myPosition.getColumn();
        while (true){
            tempX++;
            tempY--;

            if (tempX <= 8 && tempY <= 8 && tempX > 0 && tempY > 0 &&
                    (board.getPiece(new ChessPosition(tempX,tempY)) == null || board.getPiece(new ChessPosition(tempX,tempY)).pieceColor != piece.pieceColor)){
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(tempX,tempY),null));
               //System.out.printf("%d, %d\n", tempX, tempY);
                if (board.getPiece(new ChessPosition(tempX,tempY)) != null) {
                    if (board.getPiece(new ChessPosition(tempX, tempY)).pieceColor == ChessGame.TeamColor.BLACK && piece.pieceColor == ChessGame.TeamColor.WHITE) {
                        break;
                    } else if (board.getPiece(new ChessPosition(tempX, tempY)).pieceColor == ChessGame.TeamColor.WHITE && piece.pieceColor == ChessGame.TeamColor.BLACK) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
        return possibleMoves;
    }

}
