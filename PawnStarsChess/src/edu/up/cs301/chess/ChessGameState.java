package edu.up.cs301.chess;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import android.util.Log;

import edu.up.cs301.chess.actions.*;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameState;

/**
 * This contains the state for a Chess board. The state consists of an array of
 * the pieces on the board, the points each player has, whose turn it is, if
 * player 1 is in check, and player 1's color.
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
	public static final byte BOARD_WIDTH = 8; // Number of spaces wide
	public static final byte BOARD_HEIGHT = 8; // Number of spaces tall

	public static final int MAX_PLAYERS = 2; // Number of players in a game

	// Number of moves without a capture until a draw happens
	private static final int MAX_MOVES_SINCE_CAPTURE = 50;

	// Number of times the board can be in the same state until a draw happens
	private static final int MAX_REPETITION = 3;

	// to satisfy Serializable interface
	private static final long serialVersionUID = 7737493762369851826L;

	/*
	 * Represents the board. Each piece is represented by its own character. A
	 * null character means no piece is there. Player 1 is given the "bottom" of
	 * the array.
	 */
	private ChessPiece[][] pieceMap;

	// Arrays to keep track of which pieces are alive/dead:
	private ChessPiece[] player1Pieces;
	private ChessPiece[] player2Pieces;

	// Keep track of current score as the game progresses:
	private int player1Points;
	private int player2Points;

	/*
	 * Keep track of whose turn it is. True means it is player 1's turn, false
	 * means it is player 2's turn.
	 */
	private boolean whoseTurn;

	// Keep track if a player is in check
	private boolean player1InCheck;
	private boolean player2InCheck;

	// Keep track of when the game is over and who won
	private boolean isGameOver;

	private boolean player1Won;
	private boolean player2Won;

	// Keep track of which players can castle left or castle right
	private boolean[][] canCastle;

	// Keep track of the location where an en passant can happen
	private byte[] canEnPassant;
	
	// Keep track of when a player can claim a draw
	private boolean canDraw;

	/*
	 * Keep track of which player is white: True if player 1 is white and player
	 * 2 is black, False if player 1 is black and player 2 is white
	 */
	private boolean player1IsWhite;

	// The stack containing all of the moves applied so far to this game state
	private ArrayDeque<ChessMoveAction> moveList;

	private Vector<String> pieceMapHistory;

	/*
	 * The number of moves since the last capture. Can be used to indicate a
	 * stalemate.
	 */
	private int lastCapture;
	
	private boolean[][][] player1Moves;
	private boolean[][][] player2Moves;

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
		lastCapture = 0;
		moveList = new ArrayDeque<ChessMoveAction>();
		pieceMapHistory = new Vector<String>();
		canCastle = new boolean[MAX_PLAYERS][2];
		canEnPassant = new byte[]{-1,-1};
		canDraw = false;

		// Give each player a pawn of the appropriate color:
		for (int i = 0; i < BOARD_WIDTH; ++i) {
			byte[] loc1 = new byte[] { BOARD_HEIGHT - 2, (byte) i };
			byte[] loc2 = new byte[] { 1, (byte) i };

			// swap locations
			if (!player1IsWhite) {
				byte[] temp = loc1;
				loc1 = loc2;
				loc2 = temp;
			}
			// add the pieces to the player's list
			player1Pieces[i] = new ChessPiece(ChessPiece.PAWN, player1IsWhite);
			player1Pieces[i].setLocation(loc1);
			player2Pieces[i] = new ChessPiece(ChessPiece.PAWN, !player1IsWhite);
			player2Pieces[i].setLocation(loc2);

			// add to the piece map
			pieceMap[loc1[0]][loc1[1]] = player1Pieces[i];
			pieceMap[loc2[0]][loc2[1]] = player2Pieces[i];
		}

		// Give each player the remaining pieces of the appropriate color:
		byte[] pieces = new byte[] { ChessPiece.ROOK, ChessPiece.KNIGHT,
				ChessPiece.BISHOP, ChessPiece.QUEEN, ChessPiece.KING,
				ChessPiece.BISHOP, ChessPiece.KNIGHT, ChessPiece.ROOK };

		// Puts non-pawn pieces into the piecemap
		for (int i = 0; i < BOARD_WIDTH; i++) {
			byte[] loc1 = { BOARD_HEIGHT - 1, (byte) i };
			byte[] loc2 = { 0, (byte) i };

			// swap locations
			if (!player1IsWhite) {
				byte[] temp = loc1;
				loc1 = loc2;
				loc2 = temp;
			}

			player1Pieces[i + BOARD_WIDTH] = new ChessPiece(pieces[i],
					player1IsWhite);
			player1Pieces[i + BOARD_WIDTH].setLocation(loc1);

			player2Pieces[i + BOARD_WIDTH] = new ChessPiece(pieces[i],
					!player1IsWhite);
			player2Pieces[i + BOARD_WIDTH].setLocation(loc2);

			pieceMap[loc1[0]][loc1[1]] = player1Pieces[i + BOARD_WIDTH];
			pieceMap[loc2[0]][loc2[1]] = player2Pieces[i + BOARD_WIDTH];
		}
		
		updateMoves(true);
	}

	/**
	 * 
	 * copy constructor; makes a copy of the original object
	 * 
	 * @param orig
	 *            original game state being copied
	 */
	public ChessGameState(ChessGameState orig) {

		// Sets all variables equal to the original ChessGameState

		// Creates copies of the pieces and puts them in the board
		pieceMap = new ChessPiece[BOARD_WIDTH][BOARD_HEIGHT];
		player1Pieces = ChessPiece.copyPieceList(orig.getPlayer1Pieces());
		player2Pieces = ChessPiece.copyPieceList(orig.getPlayer2Pieces());

		// Place the pieces in the board
		for (ChessPiece piece : player1Pieces) {
			byte[] loc = piece.getLocation();
			
			if (!outOfBounds(loc) && piece.isAlive()) {
				pieceMap[loc[0]][loc[1]] = piece;
			}
		}

		for (ChessPiece piece : player2Pieces) {
			byte[] loc = piece.getLocation();
			if (!outOfBounds(loc) && piece.isAlive()) {
				pieceMap[loc[0]][loc[1]] = piece;
			}
		}
		// Copy the piece map history
		pieceMapHistory = new Vector<String>();
		pieceMapHistory.ensureCapacity(orig.getPieceMapHistory().capacity());

		Iterator<String> it1 = orig.getPieceMapHistory().iterator();
		while (it1.hasNext()) {
			pieceMapHistory.add(it1.next());
		}

		// Copy the move list
		moveList = new ArrayDeque<ChessMoveAction>();
		Iterator<ChessMoveAction> it2 = orig.getMoveList().iterator();
		while (it2.hasNext()) {
			moveList.add(it2.next().clone());
		}
		
		canCastle = copyCastle(orig.getCanCastle());
		
		canEnPassant = new byte[2];
		System.arraycopy(orig.getCanEnPassant(), 0, canEnPassant, 0, 2);
		
		//copy generated move 3d arrays
		player1Moves = copyPlayerMoves(orig.getPlayer1Moves());
		player2Moves = copyPlayerMoves(orig.getPlayer2Moves());
		
		// Primitive values do not need to be copied
		player1Points = orig.getPlayer1Points();
		player2Points = orig.getPlayer2Points();

		whoseTurn = orig.isWhoseTurn();

		player1InCheck = orig.isPlayer1InCheck();
		player2InCheck = orig.isPlayer2InCheck();

		isGameOver = orig.isGameOver();

		canDraw = orig.isCanDraw();

		player1IsWhite = orig.isPlayer1IsWhite();

		player1InCheck = orig.isPlayer1InCheck();
		player2InCheck = orig.isPlayer2InCheck();

		player1Points = orig.getPlayer1Points();
		player2Points = orig.getPlayer2Points();
		
	}

	private boolean[][][] copyPlayerMoves(boolean[][][] moves)
	{
		boolean[][][] newMoves = new boolean[NUM_PIECES][BOARD_HEIGHT][BOARD_WIDTH];
		if(moves != null)
		{
			for(int i = 0;i<NUM_PIECES;i++)
			{
				if(moves[i] != null)
				{
					for(int j = 0;j<BOARD_HEIGHT;j++)
					{
						if(moves[i][j] != null)
						{
							for(int k = 0;k<BOARD_HEIGHT;k++)
							{
								newMoves[i][j][k] = moves[i][j][k];
							}
						}
					}
				}
			}
		}
		
		return newMoves;
	}
	
	private boolean[][] copyCastle(boolean[][] orig) {
		if(orig == null)
		{
			return null;
		}
		else
		{
			boolean[][] canCastle = new boolean[MAX_PLAYERS][2];
			canCastle[0][0] = orig[0][0];
			canCastle[0][1] = orig[0][1];
			canCastle[1][0] = orig[1][0];
			canCastle[1][1] = orig[1][1];
			return canCastle;
		}
	}

	/**
	 * Checks to see if the ChessGameState passed in is equal to the original
	 * ChessGameState. If so, return true; otherwise, return false.
	 * 
	 * @param comp
	 *            the ChessGameState to compare this with
	 * @return true if the two ChessGameStates have the same instance variables
	 *         false if ChessGameStates are not the same
	 */
	public boolean equals(Object obj) {
		// Check for being the same reference
		if (this == obj)
			return true;

		// Check for null
		if (obj == null)
			return false;

		// Check if obj is a ChessGameState
		if (getClass() != obj.getClass())
			return false;

		ChessGameState comp = (ChessGameState) obj;
		ChessPiece[][] otherPieceMap = comp.getPieceMap();

		// Check if the two 2d arrays are equal
		if (!ChessPiece.pieceMapEquals(pieceMap, otherPieceMap))
			return false;

		// Check if the player's piece arrays are equal
		if (!ChessPiece
				.pieceArrayEquals(player1Pieces, comp.getPlayer1Pieces()))
			return false;
		if (!ChessPiece
				.pieceArrayEquals(player2Pieces, comp.getPlayer2Pieces()))
			return false;

		// Check if all primitive instance variables are equals
		if (player1Points != comp.getPlayer1Points())
			return false;
		if (player2Points != comp.getPlayer2Points())
			return false;

		if (whoseTurn != comp.isWhoseTurn())
			return false;

		if (player1InCheck != comp.isPlayer1InCheck())
			return false;

		if (player2InCheck != comp.isPlayer2InCheck())
			return false;

		if (isGameOver != comp.isGameOver())
			return false;

		if (!Arrays.deepEquals(canCastle, comp.getCanCastle()))
			return false;

		if (player1IsWhite != comp.isPlayer1IsWhite())
			return false;

		return true;
	}

	private boolean fromApplyMove = false;

	/**
	 * Applies a move to the game state
	 * 
	 * @param move
	 *            the move to be applied to this game state
	 * @return true if successful, false if not
	 */
	public boolean applyMove(GameAction act) {
		if (act == null) {
			return false;
		}

		// See if user has requested a draw (tie)
		if (act instanceof DrawAction) {
			DrawAction drawAct = (DrawAction) act;
			if (drawAct.isAccepted() || canDraw) {
				isGameOver = true;
				player2Won = true;
				player1Won = true;
			}
			return true;
		}

		// See if user has moved a piece
		if (act instanceof ChessMoveAction) {
			ChessMoveAction move = (ChessMoveAction) act;
			boolean retVal = movePiece(move);
			// See if anyone won:
			//TODO make sure this is necessary
			// TODO: fix this code: implement game winning conditions
			ChessPiece player1King = this.getKing(true);
			ChessPiece player2King = this.getKing(false);

			if (this.isPlayer1InCheck() && whoseTurn && !fromApplyMove) {
				fromApplyMove = true;
				boolean[][] arr = this.getPossibleMoves(player1King, false);
				isGameOver = true;
				this.player1Won = false;
				this.player2Won = true;
				for (int i = 0; i < BOARD_WIDTH; ++i) {
					for (boolean element : arr[i]) {
						if (element) {
							isGameOver = false;
							this.player1Won = true;
							this.player2Won = false;
						}
					}
				}
			} else if (this.isPlayer2InCheck() && !whoseTurn && !fromApplyMove) {
				fromApplyMove = true;
				boolean[][] arr = this.getPossibleMoves(player2King, false);
				isGameOver = true;
				this.player2Won = false;
				this.player1Won = true;
				for (int i = 0; i < BOARD_WIDTH; ++i) {
					for (boolean element : arr[i]) {
						if (element) {
							isGameOver = false;
							this.player2Won = true;
							this.player1Won = false;
						}
					}
				}
			}

			if (isGameOver) {
				Log.i("GAME STATE: ", "______GAME IS OVER ______");
			}

			return retVal;
		} else {
			return false;
		}
	}

	/**
	 * Convert the game state into a readable chess board
	 */
	@Override
	public String toString() {
		String rtnVal = "";

		String turn = "";
		if (player1IsWhite == whoseTurn)// white's turn
		{
			turn = "White";
		}
		if (player1IsWhite != whoseTurn)// black's turn
		{
			turn = "Black";
		}

		rtnVal += "Turn: " + turn + "\n";
		/*
		 * if(canCastle != null) { rtnVal+="P2Castle L:"+canCastle[0][0]
		 * +" R:"+canCastle[0][1]+"\n"; rtnVal+="P1Castle L:"+canCastle[1][0]
		 * +" R:"+canCastle[1][1]+"\n"; }
		 */
		rtnVal += "Moves: ";

		Iterator<ChessMoveAction> it = moveList.iterator();
		for (int i = 1; it.hasNext(); i++) {
			rtnVal += i + ". " + it.next();
			if (it.hasNext()) {
				rtnVal += " " + it.next() + " ";
			}
		}

		rtnVal += "\nState\n";
		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				if (pieceMap[i][j] != null) {
					rtnVal += pieceMap[i][j].toCharacter();
				} else {
					rtnVal += "  ";
				}
			}
			rtnVal += "\n";
		}

		return rtnVal;
	}

	/**
	 * Checks if an array contains points that are within the board;
	 * 
	 * @param location
	 * @return true if the points are in bounds, false if not
	 */
	public static boolean outOfBounds(byte[] location) {
		if (location == null) {
			return true;
		}
		if (location.length != 2) {
			return true;
		}
		return outOfBounds(location[1], location[0]);
	}

	public static boolean outOfBounds(byte x, byte y) {
		if (y < 0 || y >= ChessGameState.BOARD_HEIGHT) {
			return true;
		}
		if (x < 0 || x >= ChessGameState.BOARD_WIDTH) {
			return true;
		}
		return false;
	}

	/*
	 * The following methods are getters and setters for all necessary variables
	 * in the class
	 */

	
	
	/**
	 * 
	 * @return pieceMap an 8x8 array of ChessPieces that represent the positions
	 *         of each piece on the board
	 */
	public ChessPiece[][] getPieceMap() {
		return pieceMap;
	}

	/**
	 * @return a 3d array containing all of the squares where player 1's pieces can move
	 */
	public boolean[][][] getPlayer1Moves() {
		return player1Moves;
	}

	/**
	 * @return a 3d array containing all of the squares where player 2's pieces can move
	 */
	public boolean[][][] getPlayer2Moves() {
		return player2Moves;
	}

	/**
	 * 
	 * @param pieceMap
	 *            the 8x8 array of ChessPieces that represent the positions of
	 *            each piece on the board
	 */
	public void setPieceMap(ChessPiece[][] pieceMap) {
		this.pieceMap = pieceMap;
	}

	/**
	 * 
	 * @return player1Pieces an array of player 1's ChessPieces of length
	 *         NUM_PIECES(16)
	 */
	public ChessPiece[] getPlayer1Pieces() {
		return player1Pieces;
	}

	/**
	 * 
	 * @param player1Pieces
	 *            an array of player 1's ChessPieces of length NUM_PIECES(16)
	 */
	public void setPlayer1Pieces(ChessPiece[] player1Pieces) {
		this.player1Pieces = player1Pieces;
	}

	/**
	 * 
	 * @return player1Pieces an array of player 2's ChessPieces of length
	 *         NUM_PIECES(16)
	 */
	public ChessPiece[] getPlayer2Pieces() {
		return player2Pieces;
	}

	/**
	 * 
	 * @param player2Pieces
	 *            an array of player 2's ChessPieces of length NUM_PIECES(16)
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
	 * @param player1Points
	 *            player 1's score
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
	 * @param player2Points
	 *            player 2's score
	 */
	public void setPlayer2Points(int player2Points) {
		this.player2Points = player2Points;
	}

	/**
	 * 
	 * @return true if it is player 1's turn and false if it is player 2's turn
	 */
	public boolean isWhoseTurn() {
		return whoseTurn;
	}

	/**
	 * 
	 * @param whoseTurn
	 *            true if it is player 1's turn and false if it is player 2's
	 *            turn
	 */
	public void setWhoseTurn(boolean whoseTurn) {
		this.whoseTurn = whoseTurn;
	}

	/**
	 * 
	 * @return true if player1 is in check and false if player1 is not in check
	 */
	public boolean isPlayer1InCheck() {
		return player1InCheck;
	}

	/**
	 * 
	 * @param isCheck
	 *            true if player1 is in check and false if player1 is not in
	 *            check
	 */
	public void setPlayer1InCheck(boolean player1InCheck) {
		this.player1InCheck = player1InCheck;
	}

	/**
	 * 
	 * @return true if player2 is in check and false if player2 is not in check
	 */
	public boolean isPlayer2InCheck() {
		return player2InCheck;
	}

	/**
	 * 
	 * @param isCheck
	 *            true if player2 is in check and false if player2 is not in
	 *            check
	 */
	public void setPlayer2InCheck(boolean player2InCheck) {
		this.player2InCheck = player2InCheck;
	}

	/**
	 * 
	 * @return true if the game is over due to a king being in checkmate and
	 *         false if the game is continuing
	 */
	public boolean isGameOver() {
		return isGameOver;
	}

	/**
	 * 
	 * @param isGameOver
	 *            true if the game is over due to a king being in checkmate and
	 *            false if the game is continuing
	 */
	public void setGameOver(boolean isGameOver) {
		this.isGameOver = isGameOver;
	}

	/**
	 * 
	 * @return a 2x2 array of booleans that represent whether or not castling
	 *         can occur to the left and right for each player
	 */
	public boolean[][] getCanCastle() {
		return canCastle;
	}

	/**
	 * 
	 * @return true if player1 is white and and player2 is black false if
	 *         player1 is black and player2 is white
	 */
	public boolean isPlayer1IsWhite() {
		return player1IsWhite;
	}

	/**
	 * 
	 * @param player1IsWhite
	 *            true if player1 is white and and player2 is black false if
	 *            player1 is black and player2 is white
	 */
	public void setPlayer1IsWhite(boolean player1IsWhite) {
		this.player1IsWhite = player1IsWhite;
	}

	/**
	 * 
	 * @param color
	 *            true if player1 is white and and player2 is black false if
	 *            player1 is black and player2 is white
	 */
	public void setPlayer1Color(boolean color) {
		player1IsWhite = color;
	}

	/**
	 * Returns the stack containing all of the moves applied to this game state
	 * so far.
	 * 
	 * @return
	 */
	public ArrayDeque<ChessMoveAction> getMoveList() {
		return moveList;
	}

	/**
	 * Returns true if a player can claim a draw
	 * 
	 * @return
	 */
	public boolean isCanDraw() {
		return canDraw;
	}

	/**
	 * Returns true if player 1 won the game
	 * 
	 * @return player1Won
	 */
	public boolean isPlayer1Won() {
		return player1Won;
	}

	public void setPlayer1Won(boolean player1Won) {
		this.player1Won = player1Won;
	}

	/**
	 * Returns true if player 2 won the game
	 * 
	 * @return player2Won
	 */
	public boolean isPlayer2Won() {
		return player2Won;
	}

	/**
	 * Sets player 2 as the winner or loser
	 * 
	 * @param player2Won
	 *            true if player 2 won the game
	 */
	public void setPlayer2Won(boolean player2Won) {
		this.player2Won = player2Won;
	}

	/**
	 * Gets a vector containing all of the previous piece configurations on the
	 * board
	 * 
	 * @return
	 */
	public Vector<String> getPieceMapHistory() {
		return pieceMapHistory;
	}

	/**
	 * Gets an int array describing which tiles can do an En Passant and if it
	 * is right or left.
	 * 
	 * @return
	 */
	public byte[] getCanEnPassant() {
		return canEnPassant;
	}
	
	/**
	 * Generates a 2d array of bits containing all of the tiles that a piece can move to
	 * @param piece
	 * @return
	 */
	/*public boolean[][] getPossibleMoves(ChessPiece piece,boolean legal) {
		boolean[][] moves = null;

		if (piece == null || !piece.isAlive()) {
			return null;// something bad happened
		}

		// Get coordinates of the piece in the piecemap:
		byte[] location = piece.getLocation();

		if (outOfBounds(location)) {
			return null;
		}

		byte xLocation = location[1];
		byte yLocation = location[0];

		switch (piece.getType()) {
		case ChessPiece.PAWN:
			moves = getPawnMoves(xLocation, yLocation, piece);
			break;
		case ChessPiece.KNIGHT:
			moves = getKnightMoves(xLocation, yLocation, piece);
			break;
		case ChessPiece.BISHOP:
			moves = getBishopMoves(xLocation, yLocation, piece);
			break;
		case ChessPiece.QUEEN:
			moves = getQueenMoves(xLocation, yLocation, piece);
			break;
		case ChessPiece.KING:
			moves = getKingMoves(xLocation, yLocation, piece, legal);
			break;
		case ChessPiece.ROOK:
			moves = getRookMoves(xLocation, yLocation, piece);
			break;
		}

		if (piece.getType() != ChessPiece.KING) {
			// See if the player is in check, and update
			// moves appropriately if they are:
			if (legal) {
				// Traverse through the valid moves, and see
				// if they will stop the king from being in check:
				for (int row = 0; row < BOARD_WIDTH; ++row) {
					for (int col = 0; col < BOARD_HEIGHT; ++col) {
						if (moves != null) {
							if (moves[row][col] == true) {
								byte[] pieceLocation = { (byte) row, (byte) col };
								if (!this.willSaveKing(pieceLocation, piece)) {
									moves[row][col] = false;
								}
							}
						}
					}
				}

				//TODO: check to see if the pieces move will put the king in check
				
				
			}
		}
		return moves;
	}*/
	public boolean[][] getPossibleMoves(ChessPiece piece, boolean fromIsInCheck) {
		boolean[][] moves = null;

		if (piece == null || !piece.isAlive()) {
			return null;// something bad happened
		}

		// Get coordinates of the piece in the piecemap:
		byte[] location = piece.getLocation();

		if (outOfBounds(location)) {
			return null;
		}

		byte xLocation = location[1];
		byte yLocation = location[0];

		// First, check to see if the king is in check
		boolean isInCheck = false;
		if (whoseTurn && this.player1InCheck) {
			isInCheck = true;
		} else if ((whoseTurn == false) && this.player2InCheck) {
			isInCheck = true;
		}

		boolean isKingMove = false;

		switch (piece.getType()) {
		case ChessPiece.PAWN:
			moves = getPawnMoves(xLocation, yLocation, piece);
			break;
		case ChessPiece.KNIGHT:
			moves = getKnightMoves(xLocation, yLocation, piece);
			break;
		case ChessPiece.BISHOP:
			moves = getBishopMoves(xLocation, yLocation, piece);
			break;
		case ChessPiece.QUEEN:
			moves = getQueenMoves(xLocation, yLocation, piece);
			break;
		case ChessPiece.KING:
			isKingMove = true;
			moves = getKingMoves(xLocation, yLocation, piece, isInCheck);
			break;
		case ChessPiece.ROOK:
			moves = getRookMoves(xLocation, yLocation, piece);
			break;
		}

		if (!fromIsInCheck) {
			if (isKingMove) {
				// Remove the moves that would put the king in check
				moves = removeInvalidKingMoves(piece, moves);
			} else {
				// Remove the moves that would either put the king in check,
				// or that would fail to save the king from being in check
				// if they are in check already
				moves = removeDangerousMoves(piece, moves);
			}

		}

		return moves;

	}

	private boolean[][] removeInvalidKingMoves(ChessPiece piece,
			boolean[][] moves) {
		// Iterate through all the moves
		for (int i = 0; i < BOARD_HEIGHT; ++i) {
			for (int j = 0; j < BOARD_WIDTH; ++j) {
				if (moves[i][j] == true) {
					// Make a copy of the piece:
					ChessPiece kingCopy = new ChessPiece(piece);

					// Make a copy of the current game state
					ChessGameState stateCopy = new ChessGameState(this);

					// Fake the move:
					this.fakeMove(kingCopy, stateCopy, (byte)i, (byte)j, true);

					// Save current piece map
					ChessPiece[][] pieceMap = this.getPieceMap();

					// Update piece map with the fake data, so getPossibleMoves
					// will work:
					this.pieceMap = stateCopy.pieceMap;

					// See if that makes the king in check
					if (stateCopy.isInCheck()) {
						// If it does, remove that move:
						if (this.isWhoseTurn() && stateCopy.player1InCheck) {
							moves[i][j] = false;
						} else if (!this.isWhoseTurn()
								&& stateCopy.player2InCheck) {
							moves[i][j] = false;
						}
					}

					// Restore the move of the current game state:
					this.pieceMap = pieceMap;
				}
			}
		}

		return moves;
	}

	private void fakeMove(ChessPiece piece, ChessGameState stateCopy, byte i,
			byte j, boolean isKing) {
		int xLocation = piece.getLocation()[0];
		int yLocation = piece.getLocation()[1];
		stateCopy.pieceMap[yLocation][xLocation] = null;

		if (stateCopy.pieceMap[i][j] != null) {
			stateCopy.pieceMap[i][j].kill();
		}
		stateCopy.pieceMap[i][j] = piece;
		if (this.whoseTurn && isKing) {
			stateCopy.player1Pieces[12] = piece;
		} else if (isKing) {
			stateCopy.player2Pieces[12] = piece;
		}
		byte[] location = { i, j };
		piece.setLocation(location);
	}

	private boolean[][] removeDangerousMoves(ChessPiece piece, boolean[][] moves) {
		for (int i = 0; i < BOARD_WIDTH; ++i) {
			for (int j = 0; j < BOARD_HEIGHT; ++j) {
				if (moves[i][j]) {
					// If the king is in check, remove all moves that would not
					// save the king
					if (this.isInCheck()) {
						byte[] location = { (byte) i, (byte) j };
						if (!this.willSaveKing(location, piece)) {
							moves[i][j] = false;
						}
					}

					// Also, remove moves if they would put the king in check:
					ChessPiece pieceCopy = new ChessPiece(piece);
					ChessGameState stateCopy = new ChessGameState(this);

					// Fake the move
					this.fakeMove(pieceCopy, stateCopy, (byte)i, (byte)j, false);

					stateCopy.isInCheck();

					if ((whoseTurn && stateCopy.isPlayer1InCheck())
							|| (!whoseTurn && stateCopy.isPlayer2InCheck())) {
						moves[i][j] = false;
					}
				}
			}
		}

		return moves;
	}
	
		
		
	
	/**
	 * Gets the tiles the piece can move to
	 * @param piece
	 * @return
	 */
	public boolean[][] getSavedPossibleMoves(ChessPiece piece)
	{
		if(whoseTurn)
		{
			for(int i=0;i<NUM_PIECES;i++)
			{
				if(player1Pieces[i].equals(piece))
				{
					return player1Moves[i];
				}
			}
		}
		else
		{
			for(int i=0;i<NUM_PIECES;i++)
			{
				if(player2Pieces[i].equals(piece))
				{
					return player2Moves[i];
				}
			}
		}
		return null;
	}

	/**
	 * Checks the possible spots a pawn can move to
	 * 
	 * @param xLocation
	 *            x-coordinate of selected piece
	 * @param yLocation
	 *            y-coordinate of selected piece
	 * @param piece
	 *            currently selected piece
	 * @return a 2-D array representing the spaces the given pawn can move. True
	 *         means the piece can move there, false means it can not move
	 *         there.
	 */
	public boolean[][] getPawnMoves(byte xLocation, byte yLocation,
			ChessPiece piece) {
		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];

		
		byte i = xLocation;
		byte dy; // the direction the pawn is going
		if (piece.isWhite()) {
			dy = -1;
		}
		else {
			dy = 1;
		}
		byte j = yLocation;
		
		// See if the squares in front are taken:
		j += dy;
		if (j >= 0 && j < BOARD_HEIGHT) {
			if (this.pieceMap[j][i] == null) {
				moves[j][i] = true;

				if (piece.getHasMoved() == false) {
					j += dy;
					if (!outOfBounds(i, j)) {
						if (this.pieceMap[j][i] == null) {
							moves[j][i] = true;
						}
					}
				}
			}
		}

		// See if the pawn can attack from its current location:
		j = yLocation;
		j += dy;
		i += 1;
		if (!outOfBounds(i, j)) {
			if(pieceMap[j][i] != null && pieceMap[j][i].isWhite() != piece.isWhite())
			{
				moves[j][i] = true;
			} else if(canEnPassant != null && canEnPassant[0] == j && canEnPassant[1] == i) {
				byte[] loc = moveList.peekLast().getNewPos();
				if(loc[0] == yLocation && Math.abs(loc[1]-xLocation) == 1) {
					moves[loc[0]][loc[1]] = true;
				}
			}
		}

		i -= 2;
		if (!outOfBounds(i, j)) {
			if(pieceMap[j][i] != null && pieceMap[j][i].isWhite() != piece.isWhite())
			{
				moves[j][i] = true;
			} else if(canEnPassant != null && canEnPassant[0] == j && canEnPassant[1] == i) {
				byte[] loc = moveList.peekLast().getNewPos();
				if(loc[0] == yLocation && Math.abs(loc[1]-xLocation) == 1) {
					moves[loc[0]][loc[1]] = true;
				}
			}
		}
		
		return moves;
	}

	/**
	 * Checks to see if the move will save the king
	 */
	public boolean willSaveKing(byte[] newLocation, ChessPiece piece) {
		// See if the move saves the king from being attacked:
		ChessGameState stateCopy = new ChessGameState(this);
		
		byte x = newLocation[1];
		byte y = newLocation[0];
		byte oldX = piece.getLocation()[1];
		byte oldY = piece.getLocation()[0];
		ChessPiece whichPiece = stateCopy.findPiece(piece);
		
		//moves the piece
		stateCopy.getPieceMap()[oldY][oldX] = null;
		stateCopy.pieceMap[y][x] = whichPiece;
		whichPiece.move(newLocation);
		
		//takes a piece
		if (stateCopy.pieceMap[y][x] != null) {
			// This means we are taking a piece:
			ChessPiece takenPiece = stateCopy.pieceMap[y][x];
			// Remove it from the board:
			takenPiece.kill();
		}
		
		stateCopy.setWhoseTurn(!whoseTurn);
		
		byte[] kingLoc = getKing(whoseTurn).getLocation();
		
		if (stateCopy.isAttacked(kingLoc)) {
			return false;
		} else {
			return true;
		}
	}


	public ChessPiece[] getAttackingPieces(byte[] loc) {
		ChessPiece[] attackingPieces = new ChessPiece[NUM_PIECES];

		// Find all pieces of opposite color, and see if their valid
		// moves would kill the king:
		ChessPiece[] pieces = new ChessPiece[NUM_PIECES];
		if (whoseTurn) {
			pieces = this.getPlayer2Pieces();
		} else {
			pieces = this.getPlayer1Pieces();
		}
		int index = 0;// Keeps track of array index for us
		// Traverse each piece:
		for (ChessPiece piece : pieces) {
			// Get the possible moves for that piece:
			boolean[][] moves = getPossibleMoves(piece,false);
			if (moves != null) {
				
				for (int i = 0; i < BOARD_HEIGHT; ++i) {
					for (int j = 0; j < BOARD_WIDTH; ++j) {
						// Check to see if one of the possible
						// moves can kill the king:
						if (moves[i][j] == true) {
							if (i == loc[0] && j == loc[1]) {
								// If it can, add the piece to the list:
								attackingPieces[index++] = piece;
							}
						}
					}
				}
			}
		}
		if(index == 0)
		{
			return null;
		}
		// Package up the array nicely (no extra spaces)
		ChessPiece[] arr = new ChessPiece[index];
		for (int i = 0; i < index; ++i) {
				arr[i] = attackingPieces[i];
		}

		if (arr != null && arr[0] != null) {
			return arr;
		} else {
			return null;
		}
	}
	
	public boolean isAttacked(byte[] loc) {
		// Find all pieces of opposite color, and see if their valid
		// moves would kill the king:
		ChessPiece[] pieces = new ChessPiece[NUM_PIECES];
		if (whoseTurn) {
			pieces = this.getPlayer1Pieces();
		} else {
			pieces = this.getPlayer2Pieces();
		}
		// Traverse each piece:
		for (ChessPiece piece : pieces) {
			// Get the possible moves for that piece:
			boolean[][] moves = getPossibleMoves(piece,false);
			if (moves != null) {
				
				for (int i = 0; i < BOARD_HEIGHT; ++i) {
					for (int j = 0; j < BOARD_WIDTH; ++j) {
						// Check to see if one of the possible
						// moves can kill the king:
						if (moves[i][j] == true) {
							if (i == loc[0] && j == loc[1]) {
								// If it can, add the piece to the list:
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
	 * Checks the possible spots a knight can move to
	 * 
	 * @param xLocation
	 *            x-coordinate of selected piece
	 * @param yLocation
	 *            y-coordinate of selected piece
	 * @param piece
	 *            currently selected piece
	 * @return 2D array representing the board. True means the piece can move
	 *         there false means it can not move there
	 */
	public boolean[][] getKnightMoves(byte xLocation, byte yLocation,
			ChessPiece piece) {
		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];
		// Pass by reference into checkKnightSpot...

		// Check up two right one (or right one up two)
		checkKnightSpot(piece, xLocation + 1, yLocation - 2, moves);

		// Check down two right one (or right one two down):
		checkKnightSpot(piece, xLocation + 1, yLocation + 2, moves);

		// Check up two left one (or left one up two)
		checkKnightSpot(piece, xLocation - 1, yLocation - 2, moves);

		// Check down two left one (or left one two down):
		checkKnightSpot(piece, xLocation - 1, yLocation + 2, moves);

		// Check left two one up (or one up two left):
		checkKnightSpot(piece, xLocation - 2, yLocation - 1, moves);

		// Check left two one down (or one down two left):
		checkKnightSpot(piece, xLocation - 2, yLocation + 1, moves);

		// Check right two one down:
		checkKnightSpot(piece, xLocation + 2, yLocation - 1, moves);

		// Check right two one up:
		checkKnightSpot(piece, xLocation + 2, yLocation + 1, moves);

		return moves;
	}

	/**
	 * Alters moves array to determine which location knight can move to
	 * 
	 * @param xLocation
	 *            x-coordinate to be checked
	 * @param yLocation
	 *            y-coordinate to be checked
	 * @param moves
	 *            array to be modified
	 */
	public void checkKnightSpot(ChessPiece piece, int xLocation, int yLocation,
			boolean[][] moves) {
		if (!outOfBounds((byte)xLocation, (byte)yLocation)) {
			// See if the spot is taken:
			if (this.pieceMap[yLocation][xLocation] == null) {
				moves[yLocation][xLocation] = true;
			} else if (this.pieceMap[yLocation][xLocation].isWhite() != piece
					.isWhite()) {
				// If the pieces are different colors, the knight can move to
				// that spot (and take the piece)
				moves[yLocation][xLocation] = true;
			}
		}
	}

	/**
	 * Determines the position a bishop can move to.
	 * 
	 * @param xLocation
	 *            of the current piece
	 * @param yLocation
	 *            of the current piece
	 * @param piece
	 *            of interest
	 * @return 2-D array, true means the piece can move there
	 */
	public boolean[][] getBishopMoves(int xLocation, int yLocation,
			ChessPiece piece) {
		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];

		// TODO think about how to do this more succinctly...
		// Check northwest direction:
		int i = xLocation - 1;
		int j = yLocation - 1;

		while (i >= 0 && j >= 0 && this.pieceMap[j][i] == null) {
			moves[j][i] = true;
			i--;
			j--;
		}
		if (i >= 0 && j >= 0) {
			if (this.pieceMap[j][i] != null) {
				if (this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}
		}

		// Check northeast direction:
		i = xLocation + 1;
		j = yLocation - 1;
		while (i < BOARD_WIDTH && j >= 0 && this.pieceMap[j][i] == null) {
			moves[j][i] = true;
			i++;
			j--;
		}
		if (i < BOARD_WIDTH && j >= 0) {
			if (this.pieceMap[j][i] != null) {
				if (this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}
		}

		// Check southwest direction:
		i = xLocation - 1;
		j = yLocation + 1;
		while (i >= 0 && j < BOARD_HEIGHT && this.pieceMap[j][i] == null) {
			moves[j][i] = true;
			i--;
			j++;
		}
		if (i >= 0 && j < BOARD_HEIGHT) {
			if (this.pieceMap[j][i] != null) {
				if (this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}
		}

		// Check southeast direction:
		i = xLocation + 1;
		j = yLocation + 1;
		while (i < BOARD_WIDTH && j < BOARD_HEIGHT
				&& this.pieceMap[j][i] == null) {
			moves[j][i] = true;
			i++;
			j++;
		}
		if (i < BOARD_WIDTH && j < BOARD_HEIGHT) {
			if (this.pieceMap[j][i] != null) {
				if (this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}
		}

		return moves;
	}

	/**
	 * Determines the position the queen can move to.
	 * 
	 * @param xLocation
	 *            of the current piece
	 * @param yLocation
	 *            of the current piece
	 * @param piece
	 *            of interest
	 * @return 2-D array, true means the piece can move there
	 */

	public boolean[][] getKingMoves(byte xLocation, byte yLocation,
			ChessPiece piece, boolean legal) {

		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];

		for (byte i = (byte) (xLocation - 1); i <= xLocation + 1; i++) {
			for (byte j = (byte) (yLocation - 1); j <= yLocation + 1; j++) {
				if ((i != xLocation || j != yLocation)
						&& outOfBounds(i, j) == false) {
					if (pieceMap[j][i] == null
							|| pieceMap[j][i].isWhite() != piece.isWhite()) {
						moves[j][i] = true;

						/*if(legal)
						{
							// See if the move will put you into check:
							ChessGameState stateCopy = new ChessGameState(this);
							ChessPiece pieceCopy = stateCopy.findPiece(piece);
							
							stateCopy.pieceMap[xLocation][yLocation] = null;
							// Hack a move for the state:
							if (stateCopy.pieceMap[j][i] != null) {
								// This means we are taking a piece:
								ChessPiece takenPiece = stateCopy.pieceMap[j][i];
								// Remove it from the board:
								takenPiece.kill();
							}
							
							byte[] location = { j, i };
							pieceCopy.setLocation(location);
							stateCopy.pieceMap[j][i] = pieceCopy;
							stateCopy.setWhoseTurn(!whoseTurn);
							if (stateCopy.isAttacked(location)) {
								moves[j][i] = false;
	
							}
						}*/
					}
				}
			}
		}
		return moves;
	}

	/**
	 * Determines the position the Rook can move to.
	 * 
	 * @param xLocation
	 *            of the current piece
	 * @param yLocation
	 *            of the current piece
	 * @param piece
	 *            of interest
	 * @return 2-D array, true means the piece can move there
	 */
	public boolean[][] getRookMoves(byte xLocation, byte yLocation,
			ChessPiece piece) {
		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];
		byte i = (byte) (xLocation + 1);
		byte j = yLocation;

		// check to the EAST
		while (!outOfBounds(i, j) && this.pieceMap[j][i] == null) {
			moves[j][i] = true;
			i++;
		}
		if (!outOfBounds(i, j)) {
			if (this.pieceMap[j][i] != null) {
				if (this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}
		}
		// check to the west
		i = (byte) (xLocation - 1);
		j = yLocation;

		while (!outOfBounds(i, j) && this.pieceMap[j][i] == null) {
			moves[j][i] = true;
			i--;
		}
		if (!outOfBounds(i, j)) {
			if (this.pieceMap[j][i] != null) {
				if (this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}
		}

		// check south

		i = xLocation;
		j = (byte) (yLocation + 1);

		while (!outOfBounds(i, j) && this.pieceMap[j][i] == null) {
			moves[j][i] = true;
			j++;
		}
		if (!outOfBounds(i, j)) {
			if (this.pieceMap[j][i] != null) {
				if (this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}
		}
		// check to the NORTH

		i = xLocation;
		j = (byte) (yLocation - 1);

		while (!outOfBounds(i, j) && this.pieceMap[j][i] == null) {
			moves[j][i] = true;
			j--;
		}
		if (!outOfBounds(i, j)) {
			if (this.pieceMap[j][i] != null) {
				if (this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}
		}

		if(canCastle != null)
		{
			// Castling
			ChessPiece[] kings = findPieces(ChessPiece.KING,
					(piece.isWhite() == player1IsWhite));
			if (kings != null && kings.length != 0 && kings[0] != null) {
				byte[] kingLoc = kings[0].getLocation();
	
				if (xLocation == 0 && yLocation == BOARD_HEIGHT - 1
						&& canCastle[1][0]) { // Player 1, Left
					moves[kingLoc[0]][kingLoc[1]] = true;
				} else if (xLocation == 0 && yLocation == 0 && canCastle[0][0]) { // Player
																				// 2,
					moves[kingLoc[0]][kingLoc[1]] = true;						// Left
				} else if (xLocation == BOARD_WIDTH - 1
						&& yLocation == BOARD_HEIGHT - 1 && canCastle[1][1]) { // Player
																				// 1,
					moves[kingLoc[0]][kingLoc[1]] = true;						// Right
				} else if (xLocation == BOARD_WIDTH - 1 && yLocation == 0
						&& canCastle[0][1]) // Player 2, Right
				{
					moves[kingLoc[0]][kingLoc[1]] = true;
				}
			}
		}

		return moves;
	}

	/**
	 * Determines the position the Rook can move to.
	 * 
	 * @param xLocation
	 *            of the current piece
	 * @param yLocation
	 *            of the current piece
	 * @param piece
	 *            of interest
	 * @return 2-D array, true means the piece can move there
	 */
	public boolean[][] getQueenMoves(byte xLocation, byte yLocation,
			ChessPiece piece) {

		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];

		boolean[][] rookMoves = getRookMoves(xLocation, yLocation, piece);
		boolean[][] bishopMoves = getBishopMoves(xLocation, yLocation, piece);
		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				moves[i][j] = rookMoves[i][j] || bishopMoves[i][j];
			}
		}

		return moves;
	}

	/**
	 * Attempts to move a piece.
	 * 
	 * @param act
	 *            game action
	 * @return true if the move was valid, false otherwise
	 */
	public boolean movePiece(ChessMoveAction act) {

		if (act == null) {
			return false;
		}
		
		// Assume the move was not made
		boolean success = false;

		ChessPiece piece = findPiece(act.getWhichPiece());
		if (piece == null || !piece.isAlive()) {
			return false;
		}

		byte[] location = act.getNewPos();
		ChessPiece takenPiece = pieceMap[location[0]][location[1]];// findPiece(act.getTakenPiece());
		byte[] newPos = act.getNewPos();

		byte newYPos = newPos[0];
		byte newXPos = newPos[1];

		byte oldYPos = piece.getLocation()[0];
		byte oldXPos = piece.getLocation()[1];

		boolean[][] validMoves = getSavedPossibleMoves(piece);

		if (act instanceof RookMove) {
			// Castling
			int type = ((RookMove) act).getType();
			if (type == RookMove.CASTLE_LEFT || type == RookMove.CASTLE_RIGHT) {
				boolean isPlayer1 = (act.getWhichPiece().isWhite() == player1IsWhite);
				byte firstRank;
				ChessPiece king = getKing(isPlayer1);
				byte newKingX = 0;
				byte newRookX = 0;
				byte canCastleX = 0;
				byte canCastleY;

				// Find the king, current row, and if the player is in check
				if (isPlayer1) {// player 1, on bottom
					firstRank = BOARD_HEIGHT - 1;
					canCastleY = 1;
				} else {// player 2, on top
					firstRank = 0;
					canCastleY = 0;
				} if (type == RookMove.CASTLE_LEFT) {
					newRookX = 2;
					newKingX = 1;
					canCastleX = 0;
				} if (type == RookMove.CASTLE_RIGHT) {
					newRookX = 5;
					newKingX = 6;
					canCastleX = 1;
				}

				if (canCastle[canCastleY][canCastleX]) {
					byte[] kingLoc = king.getLocation();
					byte[] rookLoc = piece.getLocation();

					king.move(new byte[] { firstRank, newKingX });
					piece.move(new byte[] { firstRank, newRookX });

					pieceMap[firstRank][newKingX] = king;
					pieceMap[firstRank][newRookX] = piece;

					pieceMap[kingLoc[0]][kingLoc[1]] = null;
					pieceMap[rookLoc[0]][rookLoc[1]] = null;

					success = true;
				}
			}
		}
		if (act instanceof PawnMove) {
			// promotion
			PawnMove pawnAct = (PawnMove) act;
			if (pawnAct.getType() == PawnMove.PROMOTION) {
				piece.setType(pawnAct.getNewType());
			}
		}
		if (validMoves != null && validMoves[newYPos][newXPos] && !success) {
			// If the move is normal and valid, apply the move

			// Capture
			if (takenPiece != null) {
				// update the scores:
				updateScores(takenPiece);
				byte[] loc = takenPiece.getLocation();
				takenPiece.kill();
				pieceMap[loc[0]][loc[1]] = null;
				lastCapture = 0;

			} else {
				lastCapture++;
			}

			// Move the piece on the pieceMap and in the piece
			piece.move(newPos);
			pieceMap[oldYPos][oldXPos] = null;
			pieceMap[newYPos][newXPos] = piece;

			success = true;
		}
		if (success) {

			// Switch turns
			whoseTurn = !whoseTurn;

			// Update the move list
			moveList.add(act);

			updateMoves(true);
			// Check if the players can castle and
			// calculate where each piece can move
			updateCanCastle();
			
			// Check if any pawns can en passant
			updateCanEnPassant();
			
			// Check if any of the players are in check
			isInCheck();

			// Check if a player is allowed to declare a draw
			updateCanDraw();
		}
		else
		{
			updateMoves(true);
		}
		return success;
	}
	
	
	/**
	 * Generates the possible moves for each piece on the board
	 * @param legal
	 */
	public void updateMoves(boolean legal) {
		player1Moves = new boolean[NUM_PIECES][BOARD_HEIGHT][BOARD_WIDTH];
		player2Moves = new boolean[NUM_PIECES][BOARD_HEIGHT][BOARD_WIDTH];
		
		//Find the moves each piece can make
		for (int i = 0; i < NUM_PIECES; i++) {
			boolean[][] tempMoves = getPossibleMoves(player1Pieces[i],legal);
			if(tempMoves != null) {
				player1Moves[i] = tempMoves;
			}
			
			tempMoves = getPossibleMoves(player2Pieces[i],legal);
			if(tempMoves != null) {
				player2Moves[i] = tempMoves;
			}
		}
	}

	/**
	 * Updates the score when a piece is taken
	 */
	private void updateScores(ChessPiece takenPiece) {
		if (takenPiece != null) {
			int tP = takenPiece.getType();
			int points = 0;

			// add points based on type
			if (tP == ChessPiece.KING) {
				points += 10;
			}
			if (tP == ChessPiece.QUEEN) {
				points += 9;
			}
			if (tP == ChessPiece.BISHOP) {
				points += 3;
			}
			if (tP == ChessPiece.ROOK) {
				points += 5;
			}
			if (tP == ChessPiece.KNIGHT) {
				points += 3;
			}
			if (tP == ChessPiece.PAWN) {
				points += 1;
			}
			
			if(whoseTurn) {
				player1Points += points;
			} else {
				player2Points += points;
			}
		}
	}

	/**
	 * Sees if each player is in check
	 * 
	 * @return
	 */
	public boolean isInCheck() {
		//not in check by default
		player1InCheck = false;
		player2InCheck = false;
		
		// The king that currently can move
		ChessPiece king = getKing(whoseTurn);
		if (king == null) {
			return false;
		}
		
		

		if (!king.isAlive()) {
			isGameOver = true;
			if (moveList.size() != 0) {
				if (moveList.getLast() instanceof ChessMoveAction) {
					moveList.getLast().setMakesCheckmate(true);
				}

				// The king belongs to player 1
				if (king.isWhite() == player1IsWhite) {
					player2Won = true;
				} else// The king belongs to player 2
				{
					player1Won = true;
				}
			}
			return true;
		}
		byte[] kingLoc = king.getLocation();
		if (outOfBounds(kingLoc)) {
			return false;
		}
		int y = kingLoc[0];
		int x = kingLoc[1];
		
		boolean[][][] attackSquares;
		if (whoseTurn) {
			attackSquares = player2Moves;
		} else {
			attackSquares = player1Moves;
		}

		// See what tiles each piece can attack
		for (int i = 0; i < NUM_PIECES; i++) {
			boolean[][] tempAttacked = attackSquares[i];
			if (tempAttacked != null && tempAttacked[y][x] == true) {
				// A piece can attack a king if you don't do anything
				player1InCheck = whoseTurn;
				player2InCheck = !whoseTurn;
				if (!moveList.isEmpty()) {
					moveList.getLast().setMakesCheck(true);
				}
			}
		}

		//TODO: fix this code
		
		if (player1InCheck) {
			isGameOver = true;
			for(int i=0;i<NUM_PIECES;i++)
			{
				for(int j=0;j<BOARD_HEIGHT;j++)
				{
					for(int k=0;k<BOARD_WIDTH;k++)
					{
						if(player1Moves[i][j][k])
						{
							//if a valid move can be made, the game isn't over
							isGameOver = false;
							return true;
						}
					}
				}
			}
			if(isGameOver)
			{
				player2Won = true;
			}
		}
		if (player2InCheck) {
			isGameOver = true;
			for(int i=0;i<NUM_PIECES;i++)
			{
				for(int j=0;j<BOARD_HEIGHT;j++)
				{
					for(int k=0;k<BOARD_WIDTH;k++)
					{
						if(player2Moves[i][j][k])
						{
							isGameOver = false;
							return true;
						}
					}
				}
			}
			if(isGameOver)
			{
				player1Won = true;
			}
		}
		return false;
	}

	/**
	 * Calculates the position where a pawn would go to do an en passant
	 */
	public void updateCanEnPassant() {
		canEnPassant = new byte[]{-1,-1};
		for (int i = 0; i < BOARD_WIDTH; i++) {

			if (moveList.peekLast() instanceof PawnMove) {
				PawnMove lastMove = (PawnMove) moveList.getLast();

				// the last move had to be a double jump from the opponent
				if (lastMove.getType() == PawnMove.FIRST_MOVE) {
					canEnPassant[1] = lastMove.getNewPos()[1];
					if(whoseTurn)
					{
						canEnPassant[0] = (byte) (lastMove.getNewPos()[0]-1);
					}
					else
					{
						canEnPassant[0] = (byte) (lastMove.getNewPos()[0]+1);
					}
				}
			}
		}
		// Log.d("game state","can en passant x:"+canEnPassant[1]+" y:"+canEnPassant[0]);
	}

	/**
	 * Finds and saves the locations where each piece can move. It also
	 * checks if the players can still castle for their next turn or not
	 */
	
	private void updateCanCastle() {
		
		int firstRank = 4;
		boolean check = false;

		// Assume no one can castle
		canCastle = new boolean[2][2];

		for (int y = 0; y < canCastle.length; y++) {
			// Find the rooks, kings, and empty spaces required for castling
			// for each player

			ChessPiece[] attackPieces;
			boolean[][] attackedSquares = null;
			boolean player1 = (y == 1);
			ChessPiece king = getKing(player1);
			if (player1) {// player 1
				firstRank = BOARD_HEIGHT - 1;// the rook and king's initial row
				check = player1InCheck;
				attackPieces = player2Pieces;
			} else {// player 2
				firstRank = 0;// the rook and king's initial row
				check = player2InCheck;
				attackPieces = player1Pieces;
			}
			ChessPiece[] rooks = new ChessPiece[] { pieceMap[firstRank][0],
					pieceMap[firstRank][BOARD_WIDTH - 1] };

			xLoop:
			for (int x = 0; x < canCastle[0].length; x++) {
				/*
				 * According to the rules of chess, there are special
				 * conditions when castling is not allowed.
				 */
				if (rooks[x] == null || !rooks[x].isAlive()) {
					//make sure the piece is alive
					continue xLoop;
				} if(rooks[x].getHasMoved() || rooks[x].getType() != ChessPiece.ROOK) {
					//make sure the rook array has a rook that didn't move
					continue xLoop;
				} if(king == null || king.getHasMoved() || king.getType() != ChessPiece.KING) {
					//make sure the king is a king that hasn't moved
					continue xLoop;
				} if(check) {
					//make sure the king isn't in check
					continue xLoop;
				}

				// Find the bounds between the squares to check
				int minSpace = rooks[x].getLocation()[1];
				int maxSpace = king.getLocation()[1];
				if (minSpace > maxSpace) {
					int temp = minSpace;
					minSpace = maxSpace;
					maxSpace = temp;
				}
				
				// Check for taken spaces between the rook
				// and king
				for (int i = minSpace + 1; i < maxSpace; i++) {
					if (pieceMap[firstRank][i] != null) {
						continue xLoop;
					}
				}
				
				//Check for attacked squares last because it is expensive to do
				if(attackedSquares == null)
				{
					attackedSquares = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
					for (int i = 0; i < NUM_PIECES; i++) {
						boolean[][] tempAttackedSquares = getSavedPossibleMoves(attackPieces[i]);
						if (tempAttackedSquares != null) {
							for (int j = 0; j < BOARD_WIDTH; j++) {
								attackedSquares[firstRank][j] |= tempAttackedSquares[firstRank][j];
							}
						}
					}
				}
				
				// Check for attacked spaces between the rook
				// and king
				for (int i = minSpace + 1; i < maxSpace; i++) {
					if (attackedSquares[firstRank][i] == true) {
						continue xLoop;
					}
				}
				// If it reached this point, the player can castle
				canCastle[y][x] = true;
				
				byte[] kingLoc = king.getLocation();
				
				if(!outOfBounds(kingLoc))
				{
					//add in castling move
					for(int i=0;i<NUM_PIECES;i++)
					{
						if(player1Pieces[i] == rooks[x])
						{
							player1Moves[i][kingLoc[0]][kingLoc[1]] = true;
						}
						if(player2Pieces[i] == rooks[x])
						{
							player2Moves[i][kingLoc[0]][kingLoc[1]] = true;
						}
					}
				}
			}
		}
	}

	private void updateCanDraw() {
		// Once you can claim a draw, it stays that way
		if (!canDraw) {
			// fifty move rule
			if (lastCapture >= MAX_MOVES_SINCE_CAPTURE) {
				canDraw = true;
				return;
			}

			// convert the piece map into an array of ints for better storage
			String fen = toFEN();

			// threefold repetition rule
			Iterator<String> it = pieceMapHistory.iterator();
			int matches = 0;
			while (it.hasNext()) {
				if (it.next().equals(fen)) {
					// found a state equivalent to the one right now
					matches++;
				}
				if (matches >= MAX_REPETITION) {
					canDraw = true;
					return;
				}
			}

			pieceMapHistory.add(fen);
		}
	}

	/**
	 * Gets all the pieces of a type and color
	 * @param type
	 * @param isPlayer1
	 * @return
	 */
	public ChessPiece[] findPieces(int type, boolean isPlayer1) {
		ChessPiece[] pieces = new ChessPiece[NUM_PIECES];
		int i = 0;
		if (isPlayer1) {
			for (ChessPiece p : player1Pieces) {
				if (p.getType() == type && p.isAlive()) {
					pieces[i++] = p;
				}
			}
		} else {
			for (ChessPiece p : player2Pieces) {
				if (p.getType() == type && p.isAlive()) {
					pieces[i++] = p;
				}
			}
		}

		ChessPiece[] rtnPieceArray = new ChessPiece[i];
		for (int j = 0; j < i; j++) {
			rtnPieceArray[j] = pieces[j];
		}
		return rtnPieceArray;
	}

	/**
	 * Finds the a chess piece in the game state equal to the param
	 * 
	 * @param piece
	 * @return
	 */
	public ChessPiece findPiece(ChessPiece piece) {
		// TODO this doesn't work all the time
		for (ChessPiece p : getPlayer1Pieces()) {
			if (p.equals(piece)) {
				return p;
			}
		}

		for (ChessPiece p : getPlayer2Pieces()) {
			if (p.equals(piece)) {
				return p;
			}
		}
		return null;
	}

	public ChessPiece getKing(boolean player1) {
		
		ChessPiece[] kings = findPieces(ChessPiece.KING,player1);
		if(kings.length > 0 && kings[0] != null)
		{
			return kings[0];
		}
		
		//if that didn't work, try using the index where it is supposed to be
		
		if (player1) {
			return player1Pieces[12];
		} else {
			return player2Pieces[12];
		}
	}

	public String toFEN() {
		String fen = "";
		int i = 0;
		if (!player1IsWhite) {
			i = BOARD_HEIGHT - 1;
		}
		while (i >= 0 && i < BOARD_HEIGHT) {
			int numEmpty = 0;
			for (int j = 0; j < BOARD_WIDTH; j++) {
				if (pieceMap[i][j] == null) {
					numEmpty++;
				} else {
					int type = pieceMap[i][j].getType();
					if (numEmpty > 0) {
						fen += numEmpty;
					}
					numEmpty = 0;
					String pieceChar = "";
					if (type == ChessPiece.PAWN) {
						pieceChar = "p";
					} else if (type == ChessPiece.QUEEN) {
						pieceChar = "q";
					} else if (type == ChessPiece.KING) {
						pieceChar = "k";
					} else if (type == ChessPiece.ROOK) {
						pieceChar = "r";
					} else if (type == ChessPiece.BISHOP) {
						pieceChar = "b";
					} else if (type == ChessPiece.KNIGHT) {
						pieceChar = "n";
					}

					if (pieceMap[i][j].isWhite()) {
						pieceChar = pieceChar.toUpperCase(Locale.US);
					}

					fen += pieceChar;
				}
			}

			if (numEmpty > 0) {
				fen += numEmpty;
			}

			if (player1IsWhite) {
				i++;
			} else {
				i--;
			}
			// not the last row
			if (i != -1 || i != BOARD_HEIGHT) {
				fen += "/";
			}
		}
		if (player1IsWhite == whoseTurn) {
			fen += " w ";
		} else {
			fen += " b ";
		}
		if (!canCastle[0][0] && !canCastle[0][1] && !canCastle[1][0]
				&& !canCastle[1][1]) {
			fen += "-";
		}
		if (player1IsWhite) {
			if (canCastle[0][0]) {
				fen += "q";// player 2 left
			} if (canCastle[0][1]) {
				fen += "k";// player 2 right
			} if (canCastle[1][0]) {
				fen += "Q";// player 1 left
			} if (canCastle[1][1]) {
				fen += "K"; // player 1 right
			}
		} else {
			if (canCastle[0][0]) {
				fen += "Q";// player 2 left
			} if (canCastle[0][1]) {
				fen += "K";// player 2 right
			} if (canCastle[1][0]) {
				fen += "q";// player 1 left
			} if (canCastle[1][1]) {
				fen += "k"; // player 1 right
			}
		}

		if (canEnPassant[0] == 0 && canEnPassant[1] == 0) {
			fen += " - ";
		} else {
			fen += " ";
			fen += (char) (97 + canEnPassant[1]);
			fen += ChessGameState.BOARD_HEIGHT - canEnPassant[0];
			fen += " ";
		}

		fen += lastCapture / 2;
		fen += " ";
		fen += moveList.size();

		return fen;
	}
}
