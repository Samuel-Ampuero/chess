package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame{
    private ChessBoard board;
    private TeamColor teamTurn;
    public ChessGame() {
        board = new ChessBoard();
        teamTurn = TeamColor.WHITE;
    }
    public ChessGame(ChessBoard board, TeamColor teamTurn) {
        this.board = board;
        this.teamTurn = teamTurn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null){
            return null;
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        ArrayList<ChessMove> actualMoves = new ArrayList<>();
        if(isInCheck(piece.getTeamColor())){
            for (ChessMove chessMove : moves){
                ChessPiece temp = board.getPiece(chessMove.getEndPosition());
                board.addPiece(chessMove.getEndPosition(), piece);
                board.addPiece(chessMove.getStartPosition(), null);
                if (!isInCheck(piece.getTeamColor())) {
                    actualMoves.add(chessMove);
                }
                board.addPiece(chessMove.getStartPosition(), piece);
                board.addPiece(chessMove.getEndPosition(), temp);
            }
            return actualMoves;
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (!validMoves.contains(move)){
            throw new InvalidMoveException("Not a valid move");
        }
        if(piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not the team's turn");
        }

        if (isInCheck(piece.getTeamColor())){
            board.addPiece(move.getEndPosition(),piece);
            if (isInCheck(piece.getTeamColor())){
                board.addPiece(move.getEndPosition(),null);
                throw new InvalidMoveException("King in danger");
            }
        }

        if(piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null){
            board.addPiece(move.getEndPosition(),new ChessPiece(piece.getTeamColor(),move.getPromotionPiece()));
            board.addPiece(move.getStartPosition(),null);
        } else {
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
        }

        if (teamTurn == TeamColor.WHITE){
            teamTurn = TeamColor.BLACK;
        } else if (teamTurn == TeamColor.BLACK){
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE){
            ChessPosition king = null;
            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++){
                    if (board.getPiece(new ChessPosition(i,j)) != null
                            && board.getPiece(new ChessPosition(i,j)).getPieceType() == ChessPiece.PieceType.KING
                            && board.getPiece(new ChessPosition(i,j)).getTeamColor() == TeamColor.WHITE){
                        king = new ChessPosition(i,j);
                        break;
                    }
                }
                if (king != null){
                    break;
                }
            }

            if (king == null){return false;}

            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++){
                    ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                    if (piece != null && piece.getTeamColor() == TeamColor.BLACK){
                        Collection<ChessMove> moves = piece.pieceMoves(board,new ChessPosition(i,j));
                        for (ChessMove move : moves){
                            if (move.getEndPosition().getRow() == king.getRow()
                                    && move.getEndPosition().getColumn() == king.getColumn()){
                                return true;
                            }
                        }
                    }
                }
            }
        } else if (teamColor == TeamColor.BLACK){
            ChessPosition king = null;
            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++){
                    if (board.getPiece(new ChessPosition(i,j)) != null
                            && board.getPiece(new ChessPosition(i,j)).getPieceType() == ChessPiece.PieceType.KING
                            && board.getPiece(new ChessPosition(i,j)).getTeamColor() == TeamColor.BLACK){
                        king = new ChessPosition(i,j);
                        break;
                    }
                }
                if (king != null){
                    break;
                }
            }

            if (king == null){return false;}

            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++){
                    ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                    if (piece != null && piece.getTeamColor() == TeamColor.WHITE){
                        Collection<ChessMove> moves = piece.pieceMoves(board,new ChessPosition(i,j));
                        for (ChessMove move : moves){
                            if (move.getEndPosition().getRow() == king.getRow()
                                    && move.getEndPosition().getColumn() == king.getColumn()){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        if (teamColor == TeamColor.WHITE){
            ChessPosition king = null;
            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++){
                    if (board.getPiece(new ChessPosition(i,j)) != null
                            && board.getPiece(new ChessPosition(i,j)).getPieceType() == ChessPiece.PieceType.KING
                            && board.getPiece(new ChessPosition(i,j)).getTeamColor() == TeamColor.WHITE){
                        king = new ChessPosition(i,j);
                        break;
                    }
                }
                if (king != null){
                    break;
                }
            }
            if (king == null){return false;}

            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++) {
                    if (board.getPiece(new ChessPosition(i,j)) != null
                            && board.getPiece(new ChessPosition(i,j)).getTeamColor() == TeamColor.WHITE
                            && board.getPiece(new ChessPosition(i,j)).getPieceType() != ChessPiece.PieceType.KING) {
                        Collection<ChessMove> tempMoves = validMoves(new ChessPosition(i, j));
                        for (ChessMove chessMove : tempMoves) {
                            ChessPiece temp = board.getPiece(chessMove.getEndPosition());
                            board.addPiece(chessMove.getEndPosition(), board.getPiece(chessMove.getStartPosition()));
                            board.addPiece(chessMove.getStartPosition(), null);
                            if (!isInCheck(TeamColor.WHITE)) {
                                board.addPiece(chessMove.getStartPosition(), board.getPiece(chessMove.getEndPosition()));
                                board.addPiece(chessMove.getEndPosition(), temp);
                                return false;
                            }
                            board.addPiece(chessMove.getStartPosition(), board.getPiece(chessMove.getEndPosition()));
                            board.addPiece(chessMove.getEndPosition(), temp);
                        }
                    }
                }
            }
        } else if (teamColor == TeamColor.BLACK){
            ChessPosition king = null;
            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++){
                    if (board.getPiece(new ChessPosition(i,j)) != null
                            && board.getPiece(new ChessPosition(i,j)).getPieceType() == ChessPiece.PieceType.KING
                            && board.getPiece(new ChessPosition(i,j)).getTeamColor() == TeamColor.BLACK){
                        king = new ChessPosition(i,j);
                        break;
                    }
                }
                if (king != null){
                    break;
                }
            }
            if (king == null){return false;}

            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++) {
                    if (board.getPiece(new ChessPosition(i,j)) != null
                            && board.getPiece(new ChessPosition(i,j)).getTeamColor() == TeamColor.BLACK
                            && board.getPiece(new ChessPosition(i,j)).getPieceType() != ChessPiece.PieceType.KING) {
                        Collection<ChessMove> tempMoves = validMoves(new ChessPosition(i, j));
                        for (ChessMove chessMove : tempMoves) {
                            board.addPiece(chessMove.getEndPosition(), board.getPiece(chessMove.getStartPosition()));
                            board.addPiece(chessMove.getStartPosition(), null);
                            if (!isInCheck(TeamColor.BLACK)) {
                                board.addPiece(chessMove.getStartPosition(), board.getPiece(chessMove.getEndPosition()));
                                board.addPiece(chessMove.getEndPosition(), null);
                                return false;
                            }
                            board.addPiece(chessMove.getStartPosition(), board.getPiece(chessMove.getEndPosition()));
                            board.addPiece(chessMove.getEndPosition(), null);
                        }
                    }
                }
            }

        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheckmate(teamColor)) {
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
