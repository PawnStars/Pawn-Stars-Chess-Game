package edu.up.cs301.chess;

import java.util.ArrayDeque;
import java.util.Deque;

import edu.up.cs301.chess.actions.ChessMoveAction;
import edu.up.cs301.game.infoMsg.GameState;

/**
 * This contains the state for a Chess board. The state consists of an array
 * of the pieces on the board, the points each player has, whose turn it is,
 * if player 1 is in check, and player 1's color.
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version March 2015
 *
 */
public class ChessGameState extends GameState {
	// Constants:
	public static final int NUM_PIECES = 16; // Total number of pieces per side
	public static final int BOARD_WIDTH = 8; // Number of spaces wide
	public static final int BOARD_HEIGHT = 8; // Number of spaces tall

	// to satisfy Serializable interface
	private static final long serialVersionUID = 7737493762369851826L;

	// Represents the board. Each piece is represented by its own character.
	// A null character means no piece is there. Player 1 is given the "bottom"
	// of the array.
	private ChessPiece[][] pieceMap;

	// Arrays to keep track of which pieces are alive/dead:
	private ChessPiece[] player1Pieces;
	private ChessPiece[] player2Pieces;

	// Keep track of current score as pieces are taken:
	private int player1Points;
	private int player2Points;

	// Keep track of whose turn it is.
	// True means it is player 1's turn, false means it is player 2's turn.
	private boolean whoseTurn;

	// Keep track if a king is in check
	private boolean player1InCheck;
	private boolean player2InCheck;

	// Keep track of when the game is over
	private boolean isGameOver;

	// Keep track of which players can castle left or castle right
	private boolean[][] canCastle;

	// Keep track of which player is white:
	// True if player 1 is white,
	// False if player 2 is white
	private boolean player1IsWhite; 
	
	// The stack containing all of the moves applied so far to this game state
	private ArrayDeque<ChessMoveAction> moveList;
	

	/**
	 * constructor, initializing the ChessGameState to its initial state
	 * 
	 */
	public ChessGameState(boolean player1White) {
		pieceMap = new ChessPiece[BOARD_WIDTH][BOARD_HEIGHT];
		player1Pieces = new ChessPiece[NUM_PIECES];
		player2Pieces = new ChessPiece[NUM_PIECES];
	
		whoseTurn = player1White;
		player1IsWhite = player1White;
		player1InCheck = false;
		player2InCheck = false;
		isGameOver = false;
		player1Points = 0;
		player2Points = 0;
		moveList = new ArrayDeque<ChessMoveAction>();
		
		// Give each player a pawn of the appropriate color:
		for (int i = 0; i < BOARD_WIDTH; ++i) {
			player1Pieces[i] = new ChessPiece(ChessPiece.PAWN, player1IsWhite);
			player2Pieces[i] = new ChessPiece(ChessPiece.PAWN, !player1IsWhite);
		}

		// Give each payer the remaining pieces of the appropriate color:
		int[] pieces = { ChessPiece.ROOK, ChessPiece.KNIGHT, ChessPiece.BISHOP,
				ChessPiece.KING, ChessPiece.QUEEN, ChessPiece.BISHOP,
				ChessPiece.KNIGHT, ChessPiece.ROOK };
		for (int i = BOARD_WIDTH; i < NUM_PIECES; ++i) {
			player1Pieces[i] = new ChessPiece(pieces[i - BOARD_WIDTH],
					player1IsWhite);
			player2Pieces[i] = new ChessPiece(pieces[i - BOARD_WIDTH],
					!player1IsWhite);
		}
		
		//Put player 2's pieces on the board (in the piecemap):
		int index = player2Pieces.length - 1;
		for (int row = 0; row < 2; ++row) {
			for (int col = 0; col < BOARD_WIDTH; ++col) {
				pieceMap[row][col] = player2Pieces[index];
				index--;
			}
		}
		
		//Put player 1's pieces on the board (in the piecemap):
		index = player1Pieces.length - 1;
		for (int row = BOARD_HEIGHT-1; row > BOARD_HEIGHT-3 ; --row) {
			for (int col = 0; col < BOARD_WIDTH; ++col) {
				pieceMap[row][col] = player1Pieces[index];
				index--;
			}
		}
		
		//Sets all elements in canCastle to true
		canCastle = new boolean[2][2];
		for(int i=0;i<canCastle[0].length;i++)
		{
			for(int j=0;j<canCastle.length;j++)
			{
				canCastle[i][j] = true;
			}
		}
	}

	/**
	 * 
	 * copy constructor; makes a copy of the original object
	 * 
	 * @param orig
	 *            original game state being copied
	 */
	public ChessGameState(ChessGameState orig) {
		
		//Sets all variables equal to the original ChessGameState
		pieceMap = new ChessPiece[BOARD_WIDTH][BOARD_HEIGHT];

		//Creates copies of the pieces and puts them in the board
		
		player1Pieces = ChessPiece.copyPieceList(pieceMap,orig.getPlayer1Pieces());
		player2Pieces = ChessPiece.copyPieceList(pieceMap,orig.getPlayer2Pieces());
		//TODO place the pieces in the board
		
		player1Points = orig.getPlayer1Points();
		player2Points = orig.getPlayer2Points();

		whoseTurn = orig.isWhoseTurn();

		player1InCheck = orig.isPlayer1InCheck();
		player2InCheck = orig.isPlayer2InCheck();

		isGameOver = orig.isGameOver();

		canCastle = orig.getCanCastle();

		player1IsWhite = orig.getPlayer1Color();
	}

	
	
	/**
	 * Checks to see if the ChessGameState passed in is 
	 * equal to the original ChessGameState. If so, return 
	 * true; otherwise, return false. 
	 * @param comp the ChessGameState to compare this with
	 * @return true if the two ChessGameStates have the same instance variables
	 * 		   false if ChessGameStates are not the same
	 */
	public boolean equals(Object obj)
	{
		// Check for being the same reference
		if (this == obj)
			return true;
		
		// Check for null
		if (obj == null)
			return false;
		
		// Check if obj is a ChessGameState
		if (getClass() != obj.getClass())
			return false;
		
		ChessGameState comp = (ChessGameState)obj;
		ChessPiece[][] otherPieceMap = comp.getPieceMap();
		
		// Check if the two 2d arrays are equal
		if(!ChessPiece.pieceMapEquals(pieceMap, otherPieceMap))
			return false;
		
		// Check if the player's piece arrays are equal
		if(!ChessPiece.pieceArrayEquals(player1Pieces, comp.getPlayer1Pieces()))
			return false;
		if(!ChessPiece.pieceArrayEquals(player2Pieces, comp.getPlayer2Pieces()))
			return false;
		
		// Check if all primitive instance variables are equals
		if(player1Points != comp.getPlayer1Points()) return false;
		if(player2Points != comp.getPlayer2Points()) return false;
		
		if(whoseTurn != comp.isWhoseTurn()) return false;
		
		if(player1InCheck != comp.isPlayer1InCheck()) return false;

		if(player2InCheck != comp.isPlayer2InCheck()) return false;

		if(isGameOver != comp.isGameOver()) return false;

		if(canCastle != comp.getCanCastle()) return false;

		if(player1IsWhite != comp.getPlayer1Color()) return false;
		
		return true;
	}

	/*
	 * The following methods are getters and setters
	 * for all necessary variables in the class
	 */
	
	/**
	 * 
	 * @return pieceMap an 8x8 array of ChessPieces that represent
	 * 					the positions of each piece on the board
	 */
	public ChessPiece[][] getPieceMap() {
		return pieceMap;
	}

	/**
	 * 
	 * @param pieceMap the 8x8 array of ChessPieces that represent
	 * 					the positions of each piece on the board
	 */
	public void setPieceMap(ChessPiece[][] pieceMap) {
		this.pieceMap = pieceMap;
	}

	/**
	 * 
	 * @return player1Pieces an array of player 1's ChessPieces
	 * 						 of length NUM_PIECES(16)
	 */
	public ChessPiece[] getPlayer1Pieces() {
		return player1Pieces;
	}

	/**
	 * 
	 * @param player1Pieces  an array of player 1's ChessPieces
	 * 						 of length NUM_PIECES(16)
	 */
	public void setPlayer1Pieces(ChessPiece[] player1Pieces) {
		this.player1Pieces = player1Pieces;
	}

	/**
	 * 
	 * @return player1Pieces an array of player 2's ChessPieces
	 * 						 of length NUM_PIECES(16)
	 */
	public ChessPiece[] getPlayer2Pieces() {
		return player2Pieces;
	}

	/**
	 * 
	 * @param player2Pieces an array of player 2's ChessPieces
	 * 						of length NUM_PIECES(16)
	 */
	public void setPlayer2Pieces(ChessPiece[] player2Pieces) {
		this.player2Pieces = player2Pieces;
	}

	/**
	 * 
	 * @return player 1's score
	 */
	public int getPlayer1Points() {
		return player1Points;
	}

	/**
	 * 
	 * @param player1Points player 1's score
	 */
	public void setPlayer1Points(int player1Points) {
		this.player1Points = player1Points;
	}

	/**
	 * 
	 * @return player 2's score
	 */
	public int getPlayer2Points() {
		return player2Points;
	}

	/**
	 * 
	 * @param player2Points player 2's score
	 */
	public void setPlayer2Points(int player2Points) {
		this.player2Points = player2Points;
	}

	/**
	 * 
	 * @return true if it is player 1's turn and
	 * 		   false if it is player 2's turn
	 */
	public boolean isWhoseTurn() {
		return whoseTurn;
	}

	/**
	 * 
	 * @param whoseTurn true if it is player 1's turn and
	 * 		  			false if it is player 2's turn
	 */
	public void setWhoseTurn(boolean whoseTurn) {
		this.whoseTurn = whoseTurn;
	}
	
	/**
	 * 
	 * @return true if player1 is in check and
	 * 		   false if player1 is not in check
	 */
	public boolean isPlayer1InCheck() {
		return player1InCheck;
	}

	/**
	 * 
	 * @param isCheck true if player1 is in check and
	 * 		   		  false if player1 is not in check
	 */
	public void setPlayer1InCheck(boolean player1InCheck) {
		this.player1InCheck = player1InCheck;
	}

	/**
	 * 
	 * @return true if player2 is in check and
	 * 		   false if player2 is not in check
	 */
	public boolean isPlayer2InCheck() {
		return player2InCheck;
	}

	/**
	 * 
	 * @param isCheck true if player2 is in check and
	 * 		   		  false if player2 is not in check
	 */
	public void setPlayer2InCheck(boolean player2InCheck) {
		this.player2InCheck = player2InCheck;
	}

	/**
	 * 
	 * @return true if the game is over due to a king being in checkmate and
	 * 		   false if the game is continuing
	 */
	public boolean isGameOver() {
		return isGameOver;
	}

	/**
	 * 
	 * @param isGameOver true if the game is over due to a
	 * 					 king being in checkmate and
	 * 		   			 false if the game is continuing
	 */
	public void setGameOver(boolean isGameOver) {
		this.isGameOver = isGameOver;
	}
	
	/**
	 * 
	 * @return a 2x2 array of booleans that
	 * 		   represent whether or not castling
	 * 		   can occur to the left and right
	 * 		   for each player
	 */
	public boolean[][] getCanCastle() {
		return canCastle;
	}

	/**
	 * 
	 * @param canCastle a 2x2 array of booleans that
	 * 		   represent whether or not castling
	 * 		   can occur to the left and right
	 * 		   for each player
	 */
	public void setCanCastle(boolean[][] canCastle) {
		this.canCastle = canCastle;
	}

	/**
	 * 
	 * @return true if player1 is white and and player2 is black
	 * 		   false if player1 is black and player2 is white
	 */
	public boolean isPlayer1IsWhite() {
		return player1IsWhite;
	}

	/**
	 * 
	 * @param player1IsWhite true if player1 is white and and player2 is black
	 * 		   false if player1 is black and player2 is white
	 */
	public void setPlayer1IsWhite(boolean player1IsWhite) {
		this.player1IsWhite = player1IsWhite;
	}
	
	/**
	 * 
	 * @return true if player1 is white and and player2 is black
	 * 		   false if player1 is black and player2 is white
	 */
	public boolean getPlayer1Color() {
		return player1IsWhite;
	}
	
	/**
	 * 
	 * @param color true if player1 is white and and player2 is black
	 * 		   		false if player1 is black and player2 is white
	 */
	public void setPlayer1Color(boolean color)
	{
		player1IsWhite = color;
	}
	
	/**
	 * Returns the stack containing all of the moves applied to this game state so far.
	 * @return
	 */
	public ArrayDeque<ChessMoveAction> getMoveList() {
		return moveList;
	}
	
	/**
	 * Applies a move to the game state
	 * @param move the move to be applied to this game state
	 * @return true if successful, 
	 * 		   false if not
	 */
	public boolean applyMove(ChessMoveAction move)
	{
		return false;
	}
	
	/**
	 * Checks if an array contains points that are within the board;
	 * @param loc
	 * @return true if the points are in bounds, false if not
	 */
	public static boolean outOfBounds(int[] loc)
	{
		if(loc == null)
		{
			return false;
		}
		if(loc.length != 2)
		{
			return false;
		}
		if(loc[0] < 0 || loc[0] > ChessGameState.BOARD_HEIGHT)
		{
			return false;
		}
		if(loc[1] < 0 || loc[1] > ChessGameState.BOARD_WIDTH)
		{
			return false;
		}
		return true;
	}
}
