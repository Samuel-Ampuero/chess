package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

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

    private void isPossibleMove(int x,int y, ChessBoard board, ChessPosition myPosition, ChessPiece piece, ArrayList<ChessMove> possibleMoves){
        int tempX = x;
        int tempY = y;
        if (tempX <= 8 && tempY <= 8 && tempX > 0 && tempY > 0 &&
                (board.getPiece(new ChessPosition(tempX,tempY)) == null || board.getPiece(new ChessPosition(tempX,tempY)).pieceColor != piece.pieceColor)){
            possibleMoves.add(new ChessMove(myPosition, new ChessPosition(tempX,tempY),null));
        }
    }
    private void isPromotionMove(int x,int y, ChessBoard board, ChessPosition myPosition, ChessPiece piece, ArrayList<ChessMove> possibleMoves){
        int tempX = x;
        int tempY = y;
        if (tempX <= 8 && tempY <= 8 && tempX > 0 && tempY > 0 &&
                (board.getPiece(new ChessPosition(tempX,tempY)) == null || board.getPiece(new ChessPosition(tempX,tempY)).pieceColor != piece.pieceColor)){
            possibleMoves.add(new ChessMove(myPosition, new ChessPosition(tempX,tempY),PieceType.BISHOP));
            possibleMoves.add(new ChessMove(myPosition, new ChessPosition(tempX,tempY),PieceType.QUEEN));
            possibleMoves.add(new ChessMove(myPosition, new ChessPosition(tempX,tempY),PieceType.KNIGHT));
            possibleMoves.add(new ChessMove(myPosition, new ChessPosition(tempX,tempY),PieceType.ROOK));
        }
    }
    private ArrayList<ChessMove> bishopPiece(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int tempX = myPosition.getRow();
        int tempY = myPosition.getColumn();
        while (true){
            tempX++;
            tempY++;
            if (diagonalCheck(board, myPosition, piece, possibleMoves, tempX, tempY)) break;
        }
        tempX = myPosition.getRow();
        tempY = myPosition.getColumn();
        while (true){
            tempX--;
            tempY++;

            if (diagonalCheck(board, myPosition, piece, possibleMoves, tempX, tempY)) break;
        }
        tempX = myPosition.getRow();
        tempY = myPosition.getColumn();
        while (true){
            tempX--;
            tempY--;

            if (diagonalCheck(board, myPosition, piece, possibleMoves, tempX, tempY)) break;
        }
        tempX = myPosition.getRow();
        tempY = myPosition.getColumn();
        while (true){
            tempX++;
            tempY--;

            if (diagonalCheck(board, myPosition, piece, possibleMoves, tempX, tempY)) break;
        }
        return possibleMoves;
    }

    private boolean diagonalCheck(ChessBoard board, ChessPosition myPosition, ChessPiece piece, ArrayList<ChessMove> possibleMoves, int tempX, int tempY) {
        if (tempX <= 8 && tempY <= 8 && tempX > 0 && tempY > 0 &&
                (board.getPiece(new ChessPosition(tempX,tempY)) == null || board.getPiece(new ChessPosition(tempX,tempY)).pieceColor != piece.pieceColor)){
            possibleMoves.add(new ChessMove(myPosition, new ChessPosition(tempX,tempY),null));
            if (board.getPiece(new ChessPosition(tempX,tempY)) != null) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    private ArrayList<ChessMove> kingPiece(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
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
    private ArrayList<ChessMove> knightPiece(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
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
    private ArrayList<ChessMove> pawnPiece(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();

        if (piece.pieceColor == ChessGame.TeamColor.WHITE){
            if (myPosition.getRow() == 2){
                if(board.getPiece(new ChessPosition(myPosition.getRow() + 1,myPosition.getColumn())) == null) {
                    isPossibleMove(myPosition.getRow() + 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves);
                    if(board.getPiece(new ChessPosition(myPosition.getRow() + 2,myPosition.getColumn())) == null){
                        isPossibleMove(myPosition.getRow() + 2,myPosition.getColumn(),board,myPosition,piece,possibleMoves);
                    }
                }
                whitePawnPromotion(board, myPosition, piece, possibleMoves);
            } else if (myPosition.getRow() < 8){
                if (myPosition.getRow() == 7){
                    isPromotionMove(myPosition.getRow() + 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves);
                    if(myPosition.getColumn() <= 7) {
                        if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)) != null &&
                                board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)).pieceColor == ChessGame.TeamColor.BLACK) {
                            isPromotionMove(myPosition.getRow() + 1, myPosition.getColumn() + 1, board, myPosition, piece, possibleMoves);
                        }
                    }
                    if(myPosition.getColumn() > 1) {
                        if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)) != null &&
                                board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)).pieceColor == ChessGame.TeamColor.BLACK) {
                            isPromotionMove(myPosition.getRow() + 1, myPosition.getColumn() - 1, board, myPosition, piece, possibleMoves);
                        }
                    }
                } else {
                    if(board.getPiece(new ChessPosition(myPosition.getRow() + 1,myPosition.getColumn())) == null) {
                        isPossibleMove(myPosition.getRow() + 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves);
                    }
                    whitePawnPromotion(board, myPosition, piece, possibleMoves);
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
                blackPawnPromotion(board, myPosition, piece, possibleMoves);
            } else if (myPosition.getRow() > 1){
                if (myPosition.getRow() == 2){
                    isPromotionMove(myPosition.getRow() - 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves);
                    if(myPosition.getColumn() > 1) {
                        if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)) != null &&
                                board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)).pieceColor == ChessGame.TeamColor.WHITE) {
                            isPromotionMove(myPosition.getRow() - 1, myPosition.getColumn() - 1, board, myPosition, piece, possibleMoves);
                        }
                    }
                    if(myPosition.getColumn() <= 7) {
                        if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)) != null &&
                                board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)).pieceColor == ChessGame.TeamColor.WHITE) {
                            isPromotionMove(myPosition.getRow() - 1, myPosition.getColumn() + 1, board, myPosition, piece, possibleMoves);
                        }
                    }
                } else {
                    if(board.getPiece(new ChessPosition(myPosition.getRow() - 1,myPosition.getColumn())) == null) {
                        isPossibleMove(myPosition.getRow() - 1, myPosition.getColumn(), board, myPosition, piece, possibleMoves);
                    }
                    blackPawnPromotion(board, myPosition, piece, possibleMoves);
                }
            }
        }

        return possibleMoves;
    }

    private void blackPawnPromotion(ChessBoard board, ChessPosition myPosition, ChessPiece piece, ArrayList<ChessMove> possibleMoves) {
        if(myPosition.getColumn() > 1) {
            if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)) != null &&
                    board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)).pieceColor == ChessGame.TeamColor.WHITE) {
                isPossibleMove(myPosition.getRow() - 1, myPosition.getColumn() - 1, board, myPosition, piece, possibleMoves);
            }
        }
        if(myPosition.getColumn() <= 7) {
            if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)) != null &&
                    board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)).pieceColor == ChessGame.TeamColor.WHITE) {
                isPossibleMove(myPosition.getRow() - 1, myPosition.getColumn() + 1, board, myPosition, piece, possibleMoves);
            }
        }
    }

    private void whitePawnPromotion(ChessBoard board, ChessPosition myPosition, ChessPiece piece, ArrayList<ChessMove> possibleMoves) {
        if(myPosition.getColumn() <= 7) {
            if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)) != null &&
                    board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)).pieceColor == ChessGame.TeamColor.BLACK) {
                isPossibleMove(myPosition.getRow() + 1, myPosition.getColumn() + 1, board, myPosition, piece, possibleMoves);
            }
        }
        if(myPosition.getColumn() > 1) {
            if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)) != null &&
                    board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)).pieceColor == ChessGame.TeamColor.BLACK) {
                isPossibleMove(myPosition.getRow() + 1, myPosition.getColumn() - 1, board, myPosition, piece, possibleMoves);
            }
        }
    }

    private ArrayList<ChessMove> rookPiece(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int tempX = myPosition.getRow();
        int tempY = myPosition.getColumn();
        while (true){
            tempX++;

            if (diagonalCheck(board, myPosition, piece, possibleMoves, tempX, tempY)) break;
        }
        tempX = myPosition.getRow();
        tempY = myPosition.getColumn();
        while (true){
            tempX--;

            if (diagonalCheck(board, myPosition, piece, possibleMoves, tempX, tempY)) break;
        }
        tempX = myPosition.getRow();
        tempY = myPosition.getColumn();
        while (true){
            tempY++;

            if (diagonalCheck(board, myPosition, piece, possibleMoves, tempX, tempY)) break;
        }
        tempX = myPosition.getRow();
        tempY = myPosition.getColumn();
        while (true){
            tempY--;

            if (diagonalCheck(board, myPosition, piece, possibleMoves, tempX, tempY)) break;
        }
        return possibleMoves;
    }
    private ArrayList<ChessMove> queenPiece(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        possibleMoves = bishopPiece(board, myPosition, piece);
        possibleMoves.addAll(rookPiece(board, myPosition, piece));
        return possibleMoves;
    }

}
