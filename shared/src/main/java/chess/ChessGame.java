package chess;

import java.util.Vector;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;
    public ChessGame() {
        board = new ChessBoard();
        teamTurn = TeamColor.WHITE;
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
        return piece.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
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
        throw new RuntimeException("Not implemented");
        /** Clone board for every move
         * Get all potential moves
         */
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
