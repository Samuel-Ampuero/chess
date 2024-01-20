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
            return pawnPiece(board,myPosition,piece);
        } else if (piece.type == PieceType.KNIGHT){
            return knightPiece(board,myPosition,piece);
        } else if (piece.type == PieceType.ROOK){
            return rookPiece(board,myPosition,piece);
        } else if (piece.type == PieceType.BISHOP){
            return bishopPiece(board, myPosition, piece);
        } else if (piece.type == PieceType.KING){
            return kingPiece(board, myPosition, piece);
        } else if (piece.type == PieceType.QUEEN){
            return queenPiece(board, myPosition, piece);
        }
        return null;
    }

    private void isPossibleMove(int x,int y, ChessBoard board, ChessPosition myPosition, ChessPiece piece, Vector<ChessMove> possibleMoves){
        int tempX = x;
        int tempY = y;
        if (tempX <= 8 && tempY <= 8 && tempX > 0 && tempY > 0 &&
                (board.getPiece(new ChessPosition(tempX,tempY)) == null || board.getPiece(new ChessPosition(tempX,tempY)).pieceColor != piece.pieceColor)){
            possibleMoves.add(new ChessMove(myPosition, new ChessPosition(tempX,tempY),null));
            System.out.printf("%d, %d\n", tempX, tempY);
        }
    }
    private void isPromotionMove(int x,int y, ChessBoard board, ChessPosition myPosition, ChessPiece piece, Vector<ChessMove> possibleMoves,ChessPiece.PieceType promotionPiece){
        int tempX = x;
        int tempY = y;
        if (tempX <= 8 && tempY <= 8 && tempX > 0 && tempY > 0 &&
                (board.getPiece(new ChessPosition(tempX,tempY)) == null || board.getPiece(new ChessPosition(tempX,tempY)).pieceColor != piece.pieceColor)){
            possibleMoves.add(new ChessMove(myPosition, new ChessPosition(tempX,tempY),promotionPiece));
            System.out.printf("%d, %d\n", tempX, tempY);
        }
    }
    private Vector<ChessMove> bishopPiece(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
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
    private Vector<ChessMove> kingPiece(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        Vector<ChessMove> possibleMoves = new Vector<>();
        isPossibleMove(myPosition.getRow() + 1,myPosition.getColumn(),board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow() + 1,myPosition.getColumn() + 1,board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow(),myPosition.getColumn() + 1,board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow() - 1,myPosition.getColumn() + 1,board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow() - 1,myPosition.getColumn(),board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow() - 1,myPosition.getColumn() - 1,board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow(),myPosition.getColumn() - 1,board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow() + 1,myPosition.getColumn() - 1,board,myPosition,piece,possibleMoves);

        return possibleMoves;
    }
    private Vector<ChessMove> knightPiece(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        Vector<ChessMove> possibleMoves = new Vector<>();
        isPossibleMove(myPosition.getRow() + 2,myPosition.getColumn() + 1,board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow() + 2,myPosition.getColumn() - 1,board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow() - 2,myPosition.getColumn() + 1,board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow() - 2,myPosition.getColumn() - 1,board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow() - 1,myPosition.getColumn() + 2,board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow() + 1,myPosition.getColumn() + 2,board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow() - 1,myPosition.getColumn() - 2,board,myPosition,piece,possibleMoves);
        isPossibleMove(myPosition.getRow() + 1,myPosition.getColumn() - 2,board,myPosition,piece,possibleMoves);
        return possibleMoves;
    }
    private Vector<ChessMove> pawnPiece(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        Vector<ChessMove> possibleMoves = new Vector<>();

        if (piece.pieceColor == ChessGame.TeamColor.WHITE){
            if (myPosition.getRow() == 2){
                if(board.getPiece(new ChessPosition(myPosition.getRow() + 1,myPosition.getColumn())) == null) {
                    isPossibleMove(myPosition.getRow() + 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves);
                    if(board.getPiece(new ChessPosition(myPosition.getRow() + 2,myPosition.getColumn())) == null){
                        isPossibleMove(myPosition.getRow() + 2,myPosition.getColumn(),board,myPosition,piece,possibleMoves);
                    }
                }
            } else if (myPosition.getRow() < 8){
                if (myPosition.getRow() == 7){
                    isPromotionMove(myPosition.getRow() + 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves, PieceType.QUEEN);
                    isPromotionMove(myPosition.getRow() + 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves, PieceType.ROOK);
                    isPromotionMove(myPosition.getRow() + 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves, PieceType.KNIGHT);
                    isPromotionMove(myPosition.getRow() + 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves, PieceType.BISHOP);
                } else {
                    if(board.getPiece(new ChessPosition(myPosition.getRow() + 1,myPosition.getColumn())) == null) {
                        isPossibleMove(myPosition.getRow() + 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves);
                    }
                    if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)) != null &&
                            board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)).pieceColor == ChessGame.TeamColor.BLACK) {
                        isPossibleMove(myPosition.getRow() + 1, myPosition.getColumn() + 1, board, myPosition, piece, possibleMoves);
                    }
                    if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)) != null &&
                            board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)).pieceColor == ChessGame.TeamColor.BLACK) {
                        isPossibleMove(myPosition.getRow() + 1, myPosition.getColumn() - 1, board, myPosition, piece, possibleMoves);
                    }
                }
            }
        } else if (piece.pieceColor == ChessGame.TeamColor.BLACK){
            if (myPosition.getRow() == 7){
                if(board.getPiece(new ChessPosition(myPosition.getRow() - 1,myPosition.getColumn())) == null){
                    isPossibleMove(myPosition.getRow() - 1,myPosition.getColumn(),board,myPosition,piece,possibleMoves);
                    if(board.getPiece(new ChessPosition(myPosition.getRow() - 2,myPosition.getColumn())) == null) {
                        isPossibleMove(myPosition.getRow() - 2, myPosition.getColumn(), board, myPosition, piece, possibleMoves);
                    }
                }
            } else if (myPosition.getRow() > 1){
                if (myPosition.getRow() == 2){
                    isPromotionMove(myPosition.getRow() - 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves,PieceType.BISHOP);
                    isPromotionMove(myPosition.getRow() - 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves,PieceType.ROOK);
                    isPromotionMove(myPosition.getRow() - 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves,PieceType.KNIGHT);
                    isPromotionMove(myPosition.getRow() - 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves,PieceType.QUEEN);
                } else {
                    if(board.getPiece(new ChessPosition(myPosition.getRow() - 1,myPosition.getColumn())) == null) {
                        isPossibleMove(myPosition.getRow() - 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves);
                    }
                    if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)) != null &&
                            board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)).pieceColor == ChessGame.TeamColor.WHITE) {
                        isPossibleMove(myPosition.getRow() - 1, myPosition.getColumn() - 1, board, myPosition, piece, possibleMoves);
                    }
                    if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)) != null &&
                            board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)).pieceColor == ChessGame.TeamColor.WHITE) {
                        isPossibleMove(myPosition.getRow() - 1, myPosition.getColumn() + 1, board, myPosition, piece, possibleMoves);
                    }
                }
            }
        }

        return possibleMoves;
    }
    private Vector<ChessMove> rookPiece(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        Vector<ChessMove> possibleMoves = new Vector<>();
        //System.out.println("FOUND"); //testing
        //System.out.printf("Position = %d, %d\nPossibilities:\n", myPosition.getRow(), myPosition.getColumn()); //testing
        int tempX = myPosition.getRow();
        int tempY = myPosition.getColumn();
        while (true){
            tempX++;

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
    private Vector<ChessMove> queenPiece(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        Vector<ChessMove> possibleMoves = new Vector<>();
        possibleMoves = bishopPiece(board, myPosition, piece);
        possibleMoves.addAll(rookPiece(board, myPosition, piece));
        return possibleMoves;
    }

}
