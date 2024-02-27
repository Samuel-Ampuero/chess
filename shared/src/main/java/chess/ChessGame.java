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
    private boolean whiteRookKingSideMoved = false;
    private boolean whiteRookQueenSideMoved = false;
    private boolean blackRookKingSideMoved = false;
    private boolean blackRookQueenSideMoved = false;
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
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

    private ChessPosition getKingPosition(TeamColor teamColor){
        ChessPosition king = null;
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                if (board.getPiece(new ChessPosition(i,j)) != null
                        && board.getPiece(new ChessPosition(i,j)).getPieceType() == ChessPiece.PieceType.KING
                        && board.getPiece(new ChessPosition(i,j)).getTeamColor() == teamColor){
                    king = new ChessPosition(i,j);
                    break;
                }
            }
            if (king != null){
                break;
            }
        }
        return king;
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

        if (piece.getTeamColor() == TeamColor.BLACK
                && piece.getPieceType() == ChessPiece.PieceType.KING
                && !blackRookKingSideMoved && !blackKingMoved
                && board.getPiece(new ChessPosition(8,7)) == null
                && board.getPiece(new ChessPosition(8,6)) == null) {

            board.addPiece(new ChessPosition(8,6), piece);
            board.addPiece(startPosition, null);
            if (!isInCheck(piece.getTeamColor())) {
                board.addPiece(new ChessPosition(8,7), piece);
                board.addPiece(new ChessPosition(8,6), null);
                if (!isInCheck(piece.getTeamColor())) {
                    actualMoves.add(new ChessMove(startPosition, new ChessPosition(8,7),null));
                }
            }
            board.addPiece(startPosition, piece);
            board.addPiece(new ChessPosition(8,6), null);
            board.addPiece(new ChessPosition(8,7), null);
        }
        if (piece.getTeamColor() == TeamColor.BLACK
                && piece.getPieceType() == ChessPiece.PieceType.KING
                && !blackRookQueenSideMoved && !blackKingMoved
                && board.getPiece(new ChessPosition(8,2)) == null
                && board.getPiece(new ChessPosition(8,3)) == null
                && board.getPiece(new ChessPosition(8,4)) == null) {

            board.addPiece(new ChessPosition(8,4), piece);
            board.addPiece(startPosition, null);
            if (!isInCheck(piece.getTeamColor())) {
                board.addPiece(new ChessPosition(8,3), piece);
                board.addPiece(new ChessPosition(8,4), null);
                if (!isInCheck(piece.getTeamColor())) {
                    actualMoves.add(new ChessMove(startPosition, new ChessPosition(8,3),null));
                }
            }
            board.addPiece(startPosition, piece);
            board.addPiece(new ChessPosition(8,3), null);
            board.addPiece(new ChessPosition(8,4), null);
        }
        if (piece.getTeamColor() == TeamColor.WHITE
                && piece.getPieceType() == ChessPiece.PieceType.KING
                && !whiteRookKingSideMoved && !whiteKingMoved
                && board.getPiece(new ChessPosition(1,7)) == null
                && board.getPiece(new ChessPosition(1,6)) == null) {

            board.addPiece(new ChessPosition(1,6), piece);
            board.addPiece(startPosition, null);
            if (!isInCheck(piece.getTeamColor())) {
                board.addPiece(new ChessPosition(1,7), piece);
                board.addPiece(new ChessPosition(1,6), null);
                if (!isInCheck(piece.getTeamColor())) {
                    actualMoves.add(new ChessMove(startPosition, new ChessPosition(1,7),null));
                }
            }
            board.addPiece(startPosition, piece);
            board.addPiece(new ChessPosition(1,6), null);
            board.addPiece(new ChessPosition(1,7), null);
        }
        if (piece.getTeamColor() == TeamColor.WHITE
                && piece.getPieceType() == ChessPiece.PieceType.KING
                && !whiteRookQueenSideMoved && !whiteKingMoved
                && board.getPiece(new ChessPosition(1,2)) == null
                && board.getPiece(new ChessPosition(1,3)) == null
                && board.getPiece(new ChessPosition(1,4)) == null) {

            board.addPiece(new ChessPosition(1,4), piece);
            board.addPiece(startPosition, null);
            if (!isInCheck(piece.getTeamColor())) {
                board.addPiece(new ChessPosition(1,3), piece);
                board.addPiece(new ChessPosition(1,4), null);
                if (!isInCheck(piece.getTeamColor())) {
                    actualMoves.add(new ChessMove(startPosition, new ChessPosition(1,3),null));
                }
            }
            board.addPiece(startPosition, piece);
            board.addPiece(new ChessPosition(1,3), null);
            board.addPiece(new ChessPosition(1,4), null);        }
        return actualMoves;
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
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == TeamColor.BLACK
                && move.getEndPosition().equals(new ChessPosition(8,7))) {
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
            board.addPiece(new ChessPosition(8,6), board.getPiece(new ChessPosition(8,8)));
            board.addPiece(new ChessPosition(8,8),null);
            blackRookKingSideMoved = true;
            blackKingMoved = true;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == TeamColor.BLACK
                && move.getEndPosition().equals(new ChessPosition(8,3))) {
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
            board.addPiece(new ChessPosition(8, 4), board.getPiece(new ChessPosition(8, 1)));
            board.addPiece(new ChessPosition(8, 1), null);
            blackRookQueenSideMoved = true;
            blackKingMoved = true;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == TeamColor.WHITE
                && move.getEndPosition().equals(new ChessPosition(1,7))) {
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
            board.addPiece(new ChessPosition(1, 6), board.getPiece(new ChessPosition(1, 8)));
            board.addPiece(new ChessPosition(1, 8), null);
            whiteRookKingSideMoved = true;
            whiteKingMoved = true;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == TeamColor.WHITE
                && move.getEndPosition().equals(new ChessPosition(1,3))) {
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
            board.addPiece(new ChessPosition(1, 4), board.getPiece(new ChessPosition(1, 1)));
            board.addPiece(new ChessPosition(1, 1), null);
            whiteRookQueenSideMoved = true;
            whiteKingMoved = true;
        } else {
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
        }

        if (piece.getTeamColor() == TeamColor.BLACK
                && piece.getPieceType() == ChessPiece.PieceType.ROOK
                && move.getStartPosition().equals(new ChessPosition(8,8))){blackRookKingSideMoved = true;}
        if (piece.getTeamColor() == TeamColor.BLACK
                && piece.getPieceType() == ChessPiece.PieceType.ROOK
                && move.getStartPosition().equals(new ChessPosition(8,1))){blackRookQueenSideMoved = true;}
        if (piece.getTeamColor() == TeamColor.WHITE
                && piece.getPieceType() == ChessPiece.PieceType.ROOK
                && move.getStartPosition().equals(new ChessPosition(1,1))){whiteRookQueenSideMoved = true;}
        if (piece.getTeamColor() == TeamColor.BLACK
                && piece.getPieceType() == ChessPiece.PieceType.ROOK
                && move.getStartPosition().equals(new ChessPosition(1,8))){whiteRookKingSideMoved = true;}
        if (piece.getTeamColor() == TeamColor.BLACK
                && piece.getPieceType() == ChessPiece.PieceType.KING
                && move.getStartPosition().equals(new ChessPosition(8,5))){blackKingMoved = true;}
        if (piece.getTeamColor() == TeamColor.WHITE
                && piece.getPieceType() == ChessPiece.PieceType.KING
                && move.getStartPosition().equals(new ChessPosition(1,5))){whiteKingMoved = true;}

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
            ChessPosition king = getKingPosition(teamColor);
            if (king == null){return false;}

            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++){
                    ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                    if (piece != null && piece.getTeamColor() == TeamColor.BLACK){
                        if (checkEnemyMoves(king, i, j, piece)) return true;
                    }
                }
            }
        } else if (teamColor == TeamColor.BLACK){
            ChessPosition king = getKingPosition(teamColor);
            if (king == null){return false;}

            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++){
                    ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                    if (piece != null && piece.getTeamColor() == TeamColor.WHITE){
                        if (checkEnemyMoves(king, i, j, piece)) return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkEnemyMoves(ChessPosition king, int i, int j, ChessPiece piece) {
        Collection<ChessMove> moves = piece.pieceMoves(board,new ChessPosition(i,j));
        for (ChessMove move : moves){
            if (move.getEndPosition().getRow() == king.getRow()
                    && move.getEndPosition().getColumn() == king.getColumn()){
                return true;
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
            ChessPosition king = getKingPosition(teamColor);
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
            ChessPosition king = getKingPosition(teamColor);
            if (king == null){return false;}

            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++) {
                    if (board.getPiece(new ChessPosition(i,j)) != null
                            && board.getPiece(new ChessPosition(i,j)).getTeamColor() == TeamColor.BLACK
                            && board.getPiece(new ChessPosition(i,j)).getPieceType() != ChessPiece.PieceType.KING) {
                        Collection<ChessMove> tempMoves = validMoves(new ChessPosition(i, j));
                        for (ChessMove chessMove : tempMoves) {
                            ChessPiece temp = board.getPiece(chessMove.getEndPosition());
                            board.addPiece(chessMove.getEndPosition(), board.getPiece(chessMove.getStartPosition()));
                            board.addPiece(chessMove.getStartPosition(), null);
                            if (!isInCheck(TeamColor.BLACK)) {
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
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                if (board.getPiece(new ChessPosition(i,j)) != null
                        && board.getPiece(new ChessPosition(i,j)).getTeamColor() == teamColor){
                    Collection<ChessMove> moves = validMoves(new ChessPosition(i,j));
                    if (!moves.isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        whiteRookKingSideMoved = false;
        whiteRookQueenSideMoved = false;
        blackRookKingSideMoved = false;
        blackRookQueenSideMoved = false;
        whiteKingMoved = false;
        blackKingMoved = false;

        if (board.getPiece(new ChessPosition(1,1)) == null || board.getPiece(new ChessPosition(1,1)).getPieceType() != ChessPiece.PieceType.ROOK){
            whiteRookQueenSideMoved = true;
        }
        if (board.getPiece(new ChessPosition(1,8)) == null || board.getPiece(new ChessPosition(1,8)).getPieceType() != ChessPiece.PieceType.ROOK){
            whiteRookKingSideMoved = true;
        }
        if (board.getPiece(new ChessPosition(1,5)) == null || board.getPiece(new ChessPosition(1,5)).getPieceType() != ChessPiece.PieceType.KING){
            whiteKingMoved = true;
        }
        if (board.getPiece(new ChessPosition(8,1)) == null || board.getPiece(new ChessPosition(8,1)).getPieceType() != ChessPiece.PieceType.ROOK){
            blackRookQueenSideMoved = true;
        }
        if (board.getPiece(new ChessPosition(8,8)) == null || board.getPiece(new ChessPosition(8,8)).getPieceType() != ChessPiece.PieceType.ROOK){
            blackRookKingSideMoved = true;
        }
        if (board.getPiece(new ChessPosition(8,5)) == null || board.getPiece(new ChessPosition(8,5)).getPieceType() != ChessPiece.PieceType.KING){
            blackKingMoved = true;
        }
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
