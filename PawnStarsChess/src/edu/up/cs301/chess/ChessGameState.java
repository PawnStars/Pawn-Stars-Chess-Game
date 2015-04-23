package edu.up.cs301.chess;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

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
	public static final int BOARD_WIDTH = 8; // Number of spaces wide
	public static final int BOARD_HEIGHT = 8; // Number of spaces tall

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

	// Keep track of total worth of each piece as the game progresses:
	private int player1Material;
	private int player2Material;

	// Keep track of pawn worth of each piece as the game progresses:
	private int player1PawnMaterial;
	private int player2PawnMaterial;

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

	private int[][] canEnPassant;

	private boolean canDraw;

	/*
	 * Keep track of which player is white: True if player 1 is white and player
	 * 2 is black, False if player 1 is black and player 2 is white
	 */
	private boolean player1IsWhite;

	// The stack containing all of the moves applied so far to this game state
	private ArrayDeque<ChessMoveAction> moveList;

	private Vector<int[][]> pieceMapHistory;

	/*
	 * The number of moves since the last capture. Can be used to indicate a
	 * stalemate.
	 */
	private int lastCapture;

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
		pieceMapHistory = new Vector<int[][]>();
		canCastle = new boolean[MAX_PLAYERS][2];
		canEnPassant = new int[MAX_PLAYERS][BOARD_WIDTH];
		canDraw = false;

		// Give each player a pawn of the appropriate color:
		for (int i = 0; i < BOARD_WIDTH; ++i) {
			int[] loc1 = new int[] { BOARD_HEIGHT - 2, i };
			int[] loc2 = new int[] { 1, i };

			// swap locations
			if (!player1IsWhite) {
				int[] temp = loc1;
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
		int[] pieces = new int[] { ChessPiece.ROOK, ChessPiece.KNIGHT,
				ChessPiece.BISHOP, ChessPiece.QUEEN, ChessPiece.KING,
				ChessPiece.BISHOP, ChessPiece.KNIGHT, ChessPiece.ROOK };

		// Puts non-pawn pieces into the piecemap
		for (int i = 0; i < BOARD_WIDTH; i++) {
			int[] loc1 = { BOARD_HEIGHT - 1, i };
			int[] loc2 = { 0, i };

			// swap locations
			if (!player1IsWhite) {
				int[] temp = loc1;
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
			int[] loc = piece.getLocation();// TODO not sure if equals is
											// necessary
			if (!outOfBounds(loc) && piece.isAlive()
					&& piece.equals(orig.getPieceMap()[loc[0]][loc[1]])) {
				pieceMap[loc[0]][loc[1]] = piece;
			}
		}

		for (ChessPiece piece : player2Pieces) {
			int[] loc = piece.getLocation();
			if (!outOfBounds(loc) && piece.isAlive()
					&& piece.equals(orig.getPieceMap()[loc[0]][loc[1]])) {
				pieceMap[loc[0]][loc[1]] = piece;
			}
		}
		// Copy the piece map history
		pieceMapHistory = new Vector<int[][]>();
		pieceMapHistory.ensureCapacity(orig.getPieceMapHistory().capacity());

		Iterator<int[][]> it1 = orig.getPieceMapHistory().iterator();
		while (it1.hasNext()) {
			int[][] pieces = new int[BOARD_WIDTH][BOARD_HEIGHT];
			int[][] oldPieces = it1.next();
			for (int i = 0; i < BOARD_HEIGHT; i++) {
				pieces[i] = new int[BOARD_WIDTH];
				System.arraycopy(oldPieces[i], 0, pieces[i], 0, BOARD_WIDTH);
			}
			pieceMapHistory.add(pieces);
		}

		// Copy the move list
		moveList = new ArrayDeque<ChessMoveAction>();
		Iterator<ChessMoveAction> it2 = orig.getMoveList().iterator();
		while (it2.hasNext()) {
			moveList.add(it2.next().clone());
			// TODO make sure the moves are copied
		}
		originalCall2 = orig.originalCall2;

		updateCanCastle();
		updateCanEnPassant();
		// canCastle = orig.getCanCastle();
		// canEnPassant = orig.getCanEnPassant();
		// TODO copy these arrays

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

		isGameOver = orig.isGameOver();

		player1Points = orig.getPlayer1Points();
		player2Points = orig.getPlayer2Points();

		player1Material = orig.getPlayer1Material();
		player2Material = orig.getPlayer2Material();

		player1PawnMaterial = orig.getPlayer1PawnMaterial();
		player2PawnMaterial = orig.getPlayer2PawnMaterial();

		// To prevent recursive function definitions:
		originalCall = orig.originalCall;
		// originalCall2 = orig.originalCall2;
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

			// Check for a draw:
			/*
			 * if(lastCapture > MAX_MOVES_SINCE_CAPTURE) { isGameOver = true;
			 * player1Won = false; player2Won = false; return true; } else {
			 */
			return movePiece(move);
			// }
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
	 * @param loc
	 * @return true if the points are in bounds, false if not
	 */
	public static boolean outOfBounds(int[] loc) {
		if (loc == null) {
			return true;
		}
		if (loc.length != 2) {
			return true;
		}
		return outOfBounds(loc[1], loc[0]);
	}

	public static boolean outOfBounds(int x, int y) {
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
	 * Get the total worth of player 1's pieces
	 * 
	 * @return player1Material
	 */
	public int getPlayer1Material() {
		return player1Material;
	}

	/**
	 * Sets the total worth of player 1's pieces
	 * 
	 * @param player1Material
	 */
	public void setPlayer1Material(int player1Material) {
		this.player1Material = player1Material;
	}

	/**
	 * Get the total worth of player 2's pieces
	 * 
	 * @return player1Material
	 */
	public int getPlayer2Material() {
		return player2Material;
	}

	/**
	 * Sets the total worth of player 2's pieces
	 * 
	 * @param player2Material
	 */
	public void setPlayer2Material(int player2Material) {
		this.player2Material = player2Material;
	}

	/**
	 * Get the total worth of player 1's pawns
	 * 
	 * @return player1PawnMaterial
	 */
	public int getPlayer1PawnMaterial() {
		return player1PawnMaterial;
	}

	/**
	 * Sets the total worth of player 1's pawns
	 * 
	 * @param player1PawnMaterial
	 */
	public void setPlayer1PawnMaterial(int player1PawnMaterial) {
		this.player1PawnMaterial = player1PawnMaterial;
	}

	/**
	 * Get the total worth of player 2's pawns
	 * 
	 * @return player2PawnMaterial
	 */
	public int getPlayer2PawnMaterial() {
		return player2PawnMaterial;
	}

	/**
	 * Sets the total worth of player 2's pawns
	 * 
	 * @param player2PawnMaterial
	 */
	public void setPlayer2PawnMaterial(int player2PawnMaterial) {
		this.player2PawnMaterial = player2PawnMaterial;
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
	public Vector<int[][]> getPieceMapHistory() {
		return pieceMapHistory;
	}

	/**
	 * Gets an int array describing which tiles can do an En Passant and if it
	 * is right or left.
	 * 
	 * @return
	 */
	public int[][] getCanEnPassant() {
		return canEnPassant;
	}

	/**
	 * 
	 * @param piece
	 * @return
	 */
	public boolean[][] getPossibleMoves(ChessPiece piece) {
		boolean[][] moves = null;

		if (piece == null || !piece.isAlive()) {
			return null;// something bad happened
		}

		// Get coordinates of the piece in the piecemap:
		int[] location = piece.getLocation();

		if (outOfBounds(location)) {
			return null;
		}

		int xLocation = location[1];
		int yLocation = location[0];

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
			moves = getPawnMoves(xLocation, yLocation, piece, isInCheck);
			break;
		case ChessPiece.KNIGHT:
			moves = getKnightMoves(xLocation, yLocation, piece, isInCheck);
			break;
		case ChessPiece.BISHOP:
			moves = getBishopMoves(xLocation, yLocation, piece, isInCheck);
			break;
		case ChessPiece.QUEEN:
			moves = getQueenMoves(xLocation, yLocation, piece, isInCheck);
			break;
		case ChessPiece.KING:
			if (originalCall2) {
				isKingMove = true;
				originalCall2 = false;
				moves = getKingMoves(xLocation, yLocation, piece, isInCheck);
				originalCall2 = true;
			}
			break;
		case ChessPiece.ROOK:
			moves = getRookMoves(xLocation, yLocation, piece, isInCheck);
			break;
		}

		if (!isKingMove) {
			// See if the player is in check, and update
			// moves appropriately if they are:
			if (isInCheck && originalCall) {
				originalCall = false;
				// Traverse through the valid moves, and see
				// if they will stop the king from being in check:
				for (int row = 0; row < BOARD_WIDTH; ++row) {
					for (int col = 0; col < BOARD_HEIGHT; ++col) {
						if (moves != null) {
							if (moves[row][col] == true) {
								int[] pieceLocation = { row, col };
								if (!this.willSaveKing(pieceLocation, piece)) {
									moves[row][col] = false;
								}
							}
						}
					}
				}
				originalCall = true;
			}
		}

		return moves;

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
	private boolean originalCall = true;

	public boolean[][] getPawnMoves(int xLocation, int yLocation,
			ChessPiece piece, boolean isInCheck) {
		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];

		// See if the squares in front are taken:
		int j = 0;
		int i = 0;
		if (piece.isWhite()) {// piece is white
			j = yLocation - 1;
			i = xLocation;
			if (yLocation - 1 >= 0) {
				if (this.pieceMap[j][i] == null) {
					moves[j][i] = true;

					if (piece.getHasMoved() == false) {
						j--;
						if (!outOfBounds(i, j)) {
							if (this.pieceMap[j][i] == null) {
								moves[j][i] = true;
							}
						}
					}
				}
			}

			// See if the pawn can attack from its current location:
			j = yLocation - 1;
			i = xLocation - 1;
			if (!outOfBounds(i, j)) {
				if (this.pieceMap[j][i] != null
						&& this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}

			j = yLocation - 1;
			i = xLocation + 1;
			if (!outOfBounds(i, j)) {
				if (this.pieceMap[j][i] != null
						&& this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}
			if (canEnPassant != null) {
				if (canEnPassant[1][xLocation] == PawnMove.LEFT_EN_PASSANT) {
					moves[xLocation - 1][yLocation + 1] = true;
				}
				if (canEnPassant[1][xLocation] == PawnMove.RIGHT_EN_PASSANT) {
					moves[xLocation + 1][yLocation + 1] = true;
				}
			}

		} else// player 2 is white
		{
			j = yLocation + 1;
			i = xLocation;
			if (!outOfBounds(i, j)) {
				if (this.pieceMap[j][i] == null) {
					moves[j][i] = true;
					j++;
					// See if the squares in front are taken:
					if (piece.getHasMoved() == false) {
						if (!outOfBounds(i, j)) {
							if (this.pieceMap[j][i] == null) {
								moves[j][i] = true;
							}
						}
					}
				}
			}

			// See if the pawn can attack from its current location:
			j = yLocation + 1;
			i = xLocation - 1;
			if (!outOfBounds(i, j)) {
				if (this.pieceMap[j][i] != null
						&& this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}

			j = yLocation + 1;
			i = xLocation + 1;
			if (!outOfBounds(i, j)) {
				if (this.pieceMap[j][i] != null
						&& this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}

			if (canEnPassant != null) {
				if (canEnPassant[1][xLocation] == PawnMove.LEFT_EN_PASSANT) {
					moves[xLocation - 1][yLocation - 1] = true;
				}
				if (canEnPassant[1][xLocation] == PawnMove.RIGHT_EN_PASSANT) {
					moves[xLocation + 1][yLocation - 1] = true;
				}
			}
		}

		return moves;
	}

	/**
	 * Checks to see if the move will save the king
	 */
	public boolean willSaveKing(int[] newLocation, ChessPiece piece) {
		// Get all the pieces currently attacking the king:
		ChessPiece[] pieces = this.getAttackingPieces();

		// See if the move saves the king from being attacked:
		ChessGameState stateCopy = new ChessGameState(this);
		int x = newLocation[0];
		int y = newLocation[1];
		int oldX = piece.getLocation()[0];
		int oldY = piece.getLocation()[1];
		stateCopy.pieceMap[oldX][oldY] = null;
		// Hack a move for the state:
		if (stateCopy.pieceMap[x][y] != null) {
			// This means we are taking a piece:
			ChessPiece takenPiece = stateCopy.pieceMap[x][y];
			// Remove it from the board:
			takenPiece.kill();
		}
		stateCopy.pieceMap[x][y] = piece;

		pieces = stateCopy.getAttackingPieces();
		if (pieces == null) {
			return true;
		} else {
			pieces = null;
			return false;
		}
	}

	/**
	 * Returns all pieces currently attacking the king
	 * 
	 */

	private ChessPiece[] getAttackingPieces() {
		ArrayList<ChessPiece> attackingPieces = new ArrayList<ChessPiece>();
		// TODO make sure this is getting the correct king
		ChessPiece king = this.getKing(this.whoseTurn);

		// Find all pieces of opposite color, and see if their valid
		// moves would kill the king:
		ChessPiece[] pieces = new ChessPiece[NUM_PIECES];
		if (whoseTurn) {
			pieces = this.getPlayer2Pieces();
		} else {
			pieces = this.getPlayer1Pieces();
		}

		// Traverse each piece:
		for (ChessPiece piece : pieces) {
			// Get the possible moves for that piece:
			boolean[][] moves = this.getPossibleMoves(piece);
			if (moves != null) {
				for (int i = 0; i < BOARD_WIDTH; ++i) {
					for (int j = 0; j < BOARD_HEIGHT; ++j) {
						// Check to see if one of the possible
						// moves can kill the king:
						if (moves[i][j] == true) {
							int[] location = { i, j };
							if (location[0] == king.getLocation()[0]
									&& location[1] == king.getLocation()[1]) {
								// If it can, add the piece to the list:
								attackingPieces.add(piece);
							}
						}
					}
				}
			}
		}

		if (attackingPieces.isEmpty()) {
			return null;
		} else {
			ChessPiece[] arr = new ChessPiece[attackingPieces.size()];
			for (int i = 0; i < attackingPieces.size(); ++i) {
				arr[i] = attackingPieces.get(i);
			}
			attackingPieces = null;
			return arr;
		}

	}

	/**
	 * Returns all pieces currently attacking the king
	 * 
	 */

	private ChessPiece[] getAttackingPieces(ChessPiece king) {
		ArrayList<ChessPiece> attackingPieces = new ArrayList<ChessPiece>();

		// Find all pieces of opposite color, and see if their valid
		// moves would kill the king:
		ChessPiece[] pieces = new ChessPiece[NUM_PIECES];
		if (whoseTurn) {
			pieces = this.getPlayer2Pieces();
		} else {
			pieces = this.getPlayer1Pieces();
		}

		// Traverse each piece:
		for (ChessPiece piece : pieces) {
			// Get the possible moves for that piece:
			boolean[][] moves = this.getPossibleMoves(piece);
			if (moves != null) {
				for (int i = 0; i < BOARD_WIDTH; ++i) {
					for (int j = 0; j < BOARD_HEIGHT; ++j) {
						// Check to see if one of the possible
						// moves can kill the king:
						if (moves[i][j] == true) {
							int[] location = { i, j };
							if (location[0] == king.getLocation()[0]
									&& location[1] == king.getLocation()[1]) {
								// If it can, add the piece to the list:
								attackingPieces.add(piece);
							}
						}
					}
				}
			}
		}

		if (attackingPieces.isEmpty()) {
			return null;
		} else {
			ChessPiece[] arr = new ChessPiece[attackingPieces.size()];
			for (int i = 0; i < attackingPieces.size(); ++i) {
				arr[i] = attackingPieces.get(i);
			}
			attackingPieces = null;
			return arr;
		}

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
	public boolean[][] getKnightMoves(int xLocation, int yLocation,
			ChessPiece piece, boolean isInCheck) {
		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];
		// Pass by reference into checkKnightSpot...

		// Check up two right one (or right one up two)
		checkKnightSpot(piece, xLocation + 1, yLocation - 2, moves, isInCheck);

		// Check down two right one (or right one two down):
		checkKnightSpot(piece, xLocation + 1, yLocation + 2, moves, isInCheck);

		// Check up two left one (or left one up two)
		checkKnightSpot(piece, xLocation - 1, yLocation - 2, moves, isInCheck);

		// Check down two left one (or left one two down):
		checkKnightSpot(piece, xLocation - 1, yLocation + 2, moves, isInCheck);

		// Check left two one up (or one up two left):
		checkKnightSpot(piece, xLocation - 2, yLocation - 1, moves, isInCheck);

		// Check left two one down (or one down two left):
		checkKnightSpot(piece, xLocation - 2, yLocation + 1, moves, isInCheck);

		// Check right two one down:
		checkKnightSpot(piece, xLocation + 2, yLocation - 1, moves, isInCheck);

		// Check right two one up:
		checkKnightSpot(piece, xLocation + 2, yLocation + 1, moves, isInCheck);

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
			boolean[][] moves, boolean isInCheck) {
		if (!outOfBounds(xLocation, yLocation)) {
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
			ChessPiece piece, boolean isInCheck) {
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
	public boolean originalCall2 = true;

	public boolean[][] getKingMoves(int xLocation, int yLocation,
			ChessPiece piece, boolean isInCheck) {

		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];

		for (int i = xLocation - 1; i <= xLocation + 1; i++) {
			for (int j = yLocation - 1; j <= yLocation + 1; j++) {
				if ((i != xLocation || j != yLocation)
						&& outOfBounds(i, j) == false) {
					if (pieceMap[j][i] == null
							|| pieceMap[j][i].isWhite() != piece.isWhite()) {
						moves[j][i] = true;

						// See if the move will put you into check:
						ChessGameState stateCopy = new ChessGameState(this);
						stateCopy.pieceMap[xLocation][yLocation] = null;
						// Hack a move for the state:
						if (stateCopy.pieceMap[j][i] != null) {
							// This means we are taking a piece:
							ChessPiece takenPiece = stateCopy.pieceMap[j][i];
							// Remove it from the board:
							takenPiece.kill();
						}
						ChessPiece pieceCopy = new ChessPiece(piece);
						int [] location = {j,i};
						pieceCopy.setLocation(location);
						stateCopy.pieceMap[j][i] = piece;
						stateCopy.originalCall2 = false;
						ChessPiece[] pieces = stateCopy.getAttackingPieces(pieceCopy);
						if (pieces != null) {
							moves[j][i] = false;

						}
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
	public boolean[][] getRookMoves(int xLocation, int yLocation,
			ChessPiece piece, boolean isInCheck) {
		// TODO think about how to do this more succinctly...
		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];
		int i = xLocation + 1;
		int j = yLocation;

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
		i = xLocation - 1;
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
		j = yLocation + 1;

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
		j = yLocation - 1;

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

		// Castling
		ChessPiece[] kings = findPieces(ChessPiece.KING,
				(piece.isWhite() == player1IsWhite));
		if (kings != null && kings.length != 0 && kings[0] != null) {
			int[] kingLoc = kings[0].getLocation();

			if (xLocation == 0 && yLocation == BOARD_HEIGHT - 1
					&& canCastle[1][0]) // Player 1, Left
			{
				moves[kingLoc[0]][kingLoc[1]] = true;
			} else if (xLocation == 0 && yLocation == 0 && canCastle[0][0]) // Player
																			// 2,
																			// Left
			{
				moves[kingLoc[0]][kingLoc[1]] = true;
			} else if (xLocation == BOARD_WIDTH - 1
					&& yLocation == BOARD_HEIGHT - 1 && canCastle[1][1]) // Player
																			// 1,
																			// Right
			{
				moves[kingLoc[0]][kingLoc[1]] = true;
			} else if (xLocation == BOARD_WIDTH - 1 && yLocation == 0
					&& canCastle[0][1]) // Player 2, Right
			{
				moves[kingLoc[0]][kingLoc[1]] = true;
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
	public boolean[][] getQueenMoves(int xLocation, int yLocation,
			ChessPiece piece, boolean isInCheck) {

		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];

		boolean[][] rookMoves = getRookMoves(xLocation, yLocation, piece,
				isInCheck);
		boolean[][] bishopMoves = getBishopMoves(xLocation, yLocation, piece,
				isInCheck);
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
		// TODO find bugs
		// Assume the move was not made
		boolean success = false;

		ChessPiece piece = findPiece(act.getWhichPiece());
		if (piece == null || !piece.isAlive()) {
			return false;
		}

		int[] location = act.getNewPos();
		ChessPiece takenPiece = pieceMap[location[0]][location[1]];// findPiece(act.getTakenPiece());
		int[] newPos = act.getNewPos();

		int newYPos = newPos[0];
		int newXPos = newPos[1];

		int oldYPos = piece.getLocation()[0];
		int oldXPos = piece.getLocation()[1];

		boolean[][] validMoves = getPossibleMoves(piece);

		if (act instanceof RookMove) {
			// Castling
			int type = ((RookMove) act).getType();
			if (type == RookMove.CASTLE_LEFT || type == RookMove.CASTLE_RIGHT) {
				boolean isPlayer1 = (act.getWhichPiece().isWhite() == player1IsWhite);
				int firstRank;
				ChessPiece king = getKing(isPlayer1);
				int newKingX = 0;
				int newRookX = 0;
				int canCastleX = 0;
				int canCastleY;

				// Find the king, current row, and if the player is in check
				if (isPlayer1)// player 1, on bottom
				{
					firstRank = BOARD_HEIGHT - 1;
					canCastleY = 1;
				} else// player 2, on top
				{
					firstRank = 0;
					canCastleY = 0;
				}

				if (type == RookMove.CASTLE_LEFT) {
					newRookX = 2;
					newKingX = 1;
					canCastleX = 0;
				}
				if (type == RookMove.CASTLE_RIGHT) {
					newRookX = 5;
					newKingX = 6;
					canCastleX = 1;
				}

				if (canCastle[canCastleY][canCastleX]) {
					int[] kingLoc = king.getLocation();
					int[] rookLoc = piece.getLocation();

					king.move(new int[] { firstRank, newKingX });
					piece.move(new int[] { firstRank, newRookX });

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
		if (validMoves[newYPos][newXPos] && !success) {
			// If the move is normal and valid, apply the move

			// Capture
			if (takenPiece != null) {
				// update the scores:
				updateScores(takenPiece);
				int[] loc = takenPiece.getLocation();
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

			// Check if any of the players are in check
			isInCheck();

			// Check if the players can castle
			updateCanCastle();

			// Check if any pawns can en passant
			updateCanEnPassant();

			// UpdateCanDraw();

			// Check if the king is being taken
			if (takenPiece != null && takenPiece.getType() == ChessPiece.KING) {
				// game over, don't do anything
				return true;
			} else {
				lastCapture++;
			}
		}
		return success;
	}

	/**
	 * Updates the score when a piece is taken
	 */
	private void updateScores(ChessPiece takenPiece) {
		if (takenPiece != null) {
			int tP = takenPiece.getType();
			// add points based on type
			if (whoseTurn == true) {
				if (tP == ChessPiece.KING) {
					this.player1Points += 10;
					// player1Score.setText(player1Points + "");
				}
				if (tP == ChessPiece.QUEEN) {
					this.player1Points += 9;
					// player1Score.setText(player1Points + "");

				}
				if (tP == ChessPiece.BISHOP) {
					this.player1Points += 3;
					// player1Score.setText(player1Points + "");

				}
				if (tP == ChessPiece.ROOK) {
					this.player1Points += 5;
					// player1Score.setText(player1Points + "");

				}
				if (tP == ChessPiece.KNIGHT) {
					this.player1Points += 3;
					// player1Score.setText(player1Points + "");

				}
				if (tP == ChessPiece.PAWN) {
					this.player1Points += 1;
					// player1Score.setText(player1Points + "");

				}
			} else {
				if (tP == ChessPiece.KING) {
					this.player2Points += 10;
				}
				// player1Score.setText(player1Points + "");

				if (tP == ChessPiece.QUEEN) {
					this.player2Points += 9;
					// player1Score.setText(player1Points + "");

				}
				if (tP == ChessPiece.BISHOP) {
					this.player2Points += 3;
					// player1Score.setText(player1Points + "");

				}
				if (tP == ChessPiece.ROOK) {
					this.player2Points += 5;
					// player1Score.setText(player1Points + "");

				}
				if (tP == ChessPiece.KNIGHT) {
					this.player2Points += 3;
					// player1Score.setText(player1Points + "");

				}
				if (tP == ChessPiece.PAWN) {
					this.player2Points += 1;
					// player1Score.setText(player1Points + "");

				}
			}
		}
	}

	/**
	 * Sees if each player is in check
	 * 
	 * @return
	 */
	public boolean isInCheck() {
		// The king that currently can move
		ChessPiece king = getKing(whoseTurn);
		if (king == null) {
			return false;
		}
		
		
		if (!king.isAlive()) {
			isGameOver = true;
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
		int[] kingLoc = king.getLocation();
		if (outOfBounds(kingLoc)) {
			return false;
		}
		int y = kingLoc[0];
		int x = kingLoc[1];

		// Get the pieces that can attack next
		ChessPiece[] attackPieces;
		if (whoseTurn) {
			attackPieces = player2Pieces;
		} else {
			attackPieces = player1Pieces;
		}

		// See what tiles each piece can attack
		for (int i = 0; i < NUM_PIECES; i++) {
			boolean[][] tempAttacked = getPossibleMoves(attackPieces[i]);
			if (tempAttacked != null && tempAttacked[y][x] == true) {
				// A piece can attack a king if you don't do anything
				player1InCheck = whoseTurn;
				player2InCheck = !whoseTurn;
				moveList.getLast().setMakesCheck(true);

				return true;
			}
		}

		//TODO: fix this code
		if ((player1InCheck || player2InCheck) && this.getPossibleMoves(king).length == 0) {
			isGameOver = true;
		}
		
		
		// Otherwise, no one is in check
		player1InCheck = false;
		player2InCheck = false;
		return false;
	}

	public void updateCanEnPassant() {
		canEnPassant = new int[MAX_PLAYERS][BOARD_WIDTH];
		for (int i = 0; i < BOARD_WIDTH; i++) {
			for (int j = 0; j < MAX_PLAYERS; j++) {
				int fifthRank;
				if (j == 1)// player 1
				{
					fifthRank = 3;
				} else// player 2
				{
					fifthRank = 4;
				}
				if (moveList.peekLast() instanceof PawnMove) {
					PawnMove lastMove = (PawnMove) moveList.getLast();
					/*Log.d("game state",
							"last move double jump: "
									+ (lastMove.getType() == PawnMove.FIRST_MOVE)
									+ " other color: "
									+ (lastMove.getWhichPiece().isWhite() != player1IsWhite));*/

					// the last move had to be a double jump from the opponent
					if (lastMove.getType() == PawnMove.FIRST_MOVE
							&& lastMove.getWhichPiece().isWhite() == player1IsWhite) {
						int[] pos = lastMove.getNewPos();
						if (pos[0] == fifthRank) {
							// The pawns are on the same rank
							if (pos[1] == i + 1) {
								// The pawns are adjacent to each other
								canEnPassant[j][i] = PawnMove.RIGHT_EN_PASSANT;
							}
							if (pos[1] == i - 1) {
								// The pawns are adjacent to each other
								canEnPassant[j][i] = PawnMove.LEFT_EN_PASSANT;
							}
						}
					}
				}
			}
		}
		/*String msg = "";
		for (int j = 0; j < MAX_PLAYERS; j++) {
			msg += "player " + j;
			for (int i = 0; i < BOARD_WIDTH; i++) {
				msg += " " + canEnPassant[j][i];
			}
		}
		Log.d("game state", msg);*/
	}

	/**
	 * Checks if the players can still castle for their next turn or not
	 */
	private void updateCanCastle() {
		int firstRank = 4;
		boolean check = false;

		// Assume no one can castle
		canCastle = new boolean[2][2];

		// String msg = "";

		for (int y = 0; y < canCastle.length; y++) {
			// Find the rooks, kings, and empty spaces required for castling
			// for each player

			ChessPiece[] attackPieces;
			boolean[][] attackedSquares = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
			boolean player1 = (y == 1);
			ChessPiece king = getKing(player1);
			if (player1)// player 1
			{
				firstRank = BOARD_HEIGHT - 1;// the rook and king's initial row
				check = player1InCheck;
				attackPieces = player2Pieces;
			} else // player 2
			{
				firstRank = 0;// the rook and king's initial row
				check = player2InCheck;
				attackPieces = player1Pieces;
			}
			ChessPiece[] rooks = new ChessPiece[] { pieceMap[firstRank][0],
					pieceMap[firstRank][BOARD_WIDTH - 1] };

			// find the squares that can be attacked in the first rank
			for (int i = 0; i < NUM_PIECES; i++) {
				boolean[][] tempAttackedSquares = getPossibleMoves(attackPieces[i]);

				if (tempAttackedSquares != null) {
					for (int j = 0; j < BOARD_WIDTH; j++) {
						attackedSquares[firstRank][j] |= tempAttackedSquares[firstRank][j];
					}
				}
			}

			xLoop: for (int x = 0; x < canCastle[0].length; x++) {
				// msg += "\nX:"+x+" Y:"+y;

				if ((rooks[x] == null || !rooks[x].isAlive()
						|| rooks[x].getHasMoved() || rooks[x].getType() != ChessPiece.ROOK)
						|| (king == null || king.getHasMoved()) || check) {
					/*
					 * According to the rules of chess, there are special
					 * conditions when castling is not allowed. If the rook is
					 * not in the right spot, or it moved, or the piece found is
					 * not a rook, or the king was not found, or the king moved,
					 * or the king is in check, then the player may not castle.
					 */
					/*
					 * if(rooks[x] == null) msg += " null rooks["+x+"]";
					 * if(rooks[x] != null && !rooks[x].isAlive()) msg +=
					 * " rook dead"; if(rooks[x] != null &&
					 * rooks[x].getHasMoved()) msg += " rook moved"; if(rooks[x]
					 * != null && rooks[x].getType() != ChessPiece.ROOK) msg +=
					 * " rook not a rook"; if(king == null) msg +=
					 * " king not found"; if(king != null && king.getHasMoved())
					 * msg += " king moved"; if(check) msg += " in check";
					 */
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

				// Check for empty spaces or attacked spaces between the rook
				// and king
				for (int i = minSpace + 1; i < maxSpace; i++) {
					if ((pieceMap[firstRank][i] != null)
							|| (attackedSquares[firstRank][i] == true)) {
						/*
						 * msg+=" Square X:"+i; if(pieceMap[firstRank][i] !=
						 * null) msg += " was occupied";
						 * if(attackedSquares[firstRank][i] == true) msg+=
						 * " was attacked";
						 */
						continue xLoop;
					}
				}
				// msg+=" can castle";
				// If it reached this point, the player can castle
				canCastle[y][x] = true;
			}

		}
		// Log.d("game state",msg);

	}

	private void UpdateCanDraw() {
		// Once you can claim a draw, it stays that way
		if (!canDraw) {
			// fifty move rule
			if (lastCapture >= MAX_MOVES_SINCE_CAPTURE) {
				canDraw = true;
				return;
			}

			// convert the piece map into an array of ints for better storage
			int[][] pieceMapAsInt = new int[BOARD_HEIGHT][BOARD_WIDTH];
			for (int i = 0; i < BOARD_WIDTH; i++) {
				for (int j = 0; j < BOARD_HEIGHT; j++) {
					if (pieceMap[j][i] != null) {
						pieceMapAsInt[j][i] = pieceMap[j][i].getType();
					}
				}
			}
			pieceMapHistory.add(pieceMapAsInt);

			// threefold repetition rule
			Iterator<int[][]> it = pieceMapHistory.iterator();
			int matches = 0;
			while (it.hasNext()) {
				if (Arrays.deepEquals(it.next(), pieceMapAsInt)) {
					// found a state equivalent to the one right now
					matches++;
				}
				if (matches >= MAX_REPETITION) {
					canDraw = true;
					return;
				}
			}
		}
	}

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
		// TODO find a better solution to find the king
		if (player1) {
			return player1Pieces[12];
		} else {
			return player2Pieces[12];
		}
	}
	
	public String toFEN()
	{
		String fen = "";
		int i = 0;
		if(!player1IsWhite)
		{
			i = BOARD_HEIGHT-1;
		}
		while(i >= 0 && i < BOARD_HEIGHT)
		{
			int numEmpty = 0;
			for (int j = 0; j < BOARD_WIDTH; j++)
			{
				if(pieceMap[i][j] == null)
				{
					numEmpty++;
				}
				else
				{
					int type = pieceMap[i][j].getType();
					if(numEmpty > 0) {
						fen += numEmpty;
					}
					numEmpty = 0;
					String pieceChar = "";
					if(type == ChessPiece.PAWN) {
						pieceChar = "p";
					}
					else if(type == ChessPiece.QUEEN) {
						pieceChar = "q";
					}
					else if(type == ChessPiece.KING) {
						pieceChar = "k";
					}
					else if(type == ChessPiece.ROOK) {
						pieceChar = "r";
					}
					else if(type == ChessPiece.BISHOP) {
						pieceChar = "b";
					}
					else if(type == ChessPiece.KNIGHT) {
						pieceChar = "k";
					}
					
					if(pieceMap[i][j].isWhite())
					{
						pieceChar = pieceChar.toUpperCase(Locale.US);
					}
					
					fen+=pieceChar;
				}
			}
			
			if(numEmpty > 0) {
				fen += numEmpty;
			}
			
			if(player1IsWhite)
			{
				i++;
			}
			else
			{
				i--;
			}
			//not the last row
			if(i != -1 || i != BOARD_HEIGHT)
			{
				fen += "/";
			}
		}
		if(player1IsWhite == whoseTurn)
		{
			fen +=" w ";
		}
		else
		{
			fen +=" b ";
		}
		if(!canCastle[0][0] && !canCastle[0][1] && !canCastle[1][0] && !canCastle[1][1])
		{
			fen += "-";
		}
		if(player1IsWhite)
		{
			if(canCastle[0][0]) {
				fen += "q";//player 2 left
			}
			if(canCastle[0][1]) {
				fen += "k";//player 2 right
			}
			if(canCastle[1][0]) {
				fen += "Q";//player 1 left
			}
			if(canCastle[1][1]){
				fen += "K"; //player 1 right
			}
		}
		else
		{
			if(canCastle[0][0]) {
				fen += "Q";//player 2 left
			}
			if(canCastle[0][1]) {
				fen += "K";//player 2 right
			}
			if(canCastle[1][0]) {
				fen += "q";//player 1 left
			}
			if(canCastle[1][1]){
				fen += "k"; //player 1 right
			}
		}
		boolean enPassant = false;
		if(moveList.peekLast() instanceof PawnMove)
		{
			PawnMove lastMove = (PawnMove)moveList.peekLast();
			if(lastMove.getType() == PawnMove.FIRST_MOVE)
			{
				int[] location = lastMove.getNewPos();
				fen += " ";
				fen += (char)(97+location[1]);
				fen += ChessGameState.BOARD_HEIGHT-location[0];
				fen += " ";
				enPassant = true;
			}
		}
		if(!enPassant)
		{
			fen += " - ";
		}
		fen += lastCapture;
		fen += " ";
		fen += moveList.size();
		//Log.d("game state","fen: "+fen);
		return fen;
	}
}
