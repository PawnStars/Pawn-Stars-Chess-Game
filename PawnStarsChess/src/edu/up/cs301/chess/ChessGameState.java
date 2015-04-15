package edu.up.cs301.chess;

import java.util.ArrayDeque;
import java.util.Arrays;

import edu.up.cs301.chess.actions.*;
import edu.up.cs301.game.actionMsg.GameAction;
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
	
	public static final int MAX_PLAYERS = 2;
	private static final int MAX_MOVES_SINCE_CAPTURE = 50;
	
	// to satisfy Serializable interface
	private static final long serialVersionUID = 7737493762369851826L;
	
	
	/*
	 * Represents the board. Each piece is represented by its own character.
	 * A null character means no piece is there. Player 1 is given the
	 * "bottom" of the array.
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
	 * Keep track of whose turn it is.
	 * True means it is player 1's turn, 
	 * false means it is player 2's turn.
	 */
	private boolean whoseTurn;

	// Keep track if a king is in check
	private boolean player1InCheck;
	private boolean player2InCheck;

	// Keep track of when the game is over and who won
	private boolean isGameOver;

	private boolean player1Won;
	private boolean player2Won;

	// Keep track of which players can castle left or castle right
	private boolean[][] canCastle;

	/*
	 * Keep track of which player is white:
	 * True if player 1 is white,
	 * False if player 2 is white
	 */
	private boolean player1IsWhite; 
	
	/*
	 * The index of the two players.
	 * Player 1 is at element 0 and player 2 is at element 1.
	 */
	private int[] playerIdx = new int[2];
	
	// The stack containing all of the moves applied so far to this game state
	private ArrayDeque<ChessMoveAction> moveList;
	
	/*
	 * The number of moves since the last capture.
	 * Can be used to indicate a stalemate.
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
		
		// Give each player a pawn of the appropriate color:
		for (int i = 0; i < BOARD_WIDTH; ++i) {
			int[] loc1 = new int[]{BOARD_HEIGHT-2,i};
			int[] loc2 = new int[]{1,i};
			
			//swap locations
			if(!player1IsWhite)
			{
				int[] temp = loc1;
				loc1 = loc2;
				loc2 = temp;
			}
			//add the pieces to the player's list
			player1Pieces[i] = new ChessPiece(ChessPiece.PAWN, player1IsWhite);
			player1Pieces[i].setLocation(loc1);
			player2Pieces[i] = new ChessPiece(ChessPiece.PAWN, !player1IsWhite);
			player2Pieces[i].setLocation(loc2);
			
			//add to the piece map
			pieceMap[loc1[0]][loc1[1]] = player1Pieces[i];
			pieceMap[loc2[0]][loc2[1]] = player2Pieces[i];
		}

		// Give each player the remaining pieces of the appropriate color:
		int[] pieces = new int[]{
				ChessPiece.ROOK,
				ChessPiece.KNIGHT,
				ChessPiece.BISHOP,
				ChessPiece.KING,
				ChessPiece.QUEEN,
				ChessPiece.BISHOP,
				ChessPiece.KNIGHT,
				ChessPiece.ROOK
		};
		
		//Puts non-pawn pieces into the piecemap
		for (int i = 0; i < BOARD_WIDTH; i++) {
			int[] loc1 = {BOARD_HEIGHT-1,i};
			int[] loc2 = {0,i};
			
			//swap locations
			if(!player1IsWhite)
			{
				int[] temp = loc1;
				loc1 = loc2;
				loc2 = temp;
			}
			
			player1Pieces[i+BOARD_WIDTH] = new ChessPiece(pieces[i], player1IsWhite);
			player1Pieces[i+BOARD_WIDTH].setLocation(loc1);
			
			player2Pieces[i+BOARD_WIDTH] = new ChessPiece(pieces[i],!player1IsWhite);
			player2Pieces[i+BOARD_WIDTH].setLocation(loc2);
			
			pieceMap[loc1[0]][loc1[1]] = player1Pieces[i+BOARD_WIDTH];
			pieceMap[loc2[0]][loc2[1]] = player2Pieces[i+BOARD_WIDTH];
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
		
		//Creates copies of the pieces and puts them in the board
		pieceMap = new ChessPiece[BOARD_WIDTH][BOARD_HEIGHT];
		player1Pieces = ChessPiece.copyPieceList(orig.getPlayer1Pieces());
		player2Pieces = ChessPiece.copyPieceList(orig.getPlayer2Pieces());
		
		for(ChessPiece piece:player1Pieces)
		{
			int[] loc = piece.getLocation();
			if(!outOfBounds(loc))
			{
				pieceMap[loc[0]][loc[1]] = piece;
			}
		}
		
		for(ChessPiece piece:player2Pieces)
		{
			int[] loc = piece.getLocation();
			if(!outOfBounds(loc))
			{
				pieceMap[loc[0]][loc[1]] = piece;
			}
		}
		
		moveList = orig.getMoveList().clone();//TODO clone the elements
		
		//Primitive values do not need to be copied
		player1Points = orig.getPlayer1Points();
		player2Points = orig.getPlayer2Points();

		whoseTurn = orig.isWhoseTurn();

		player1InCheck = orig.isPlayer1InCheck();
		player2InCheck = orig.isPlayer2InCheck();

		isGameOver = orig.isGameOver();

		canCastle = orig.getCanCastle();

		player1IsWhite = orig.getPlayer1Color();
		
		player1InCheck = orig.isPlayer1InCheck();
		player2InCheck = orig.isPlayer2InCheck();
		
		isGameOver = orig.isGameOver();
		
		player1Points = orig.getPlayer1Points();
		player2Points = orig.getPlayer2Points();
		
		//valid = orig.isValid();
		
		player1Material = orig.getPlayer1Material();
		player2Material = orig.getPlayer2Material();
		
		player1PawnMaterial = orig.getPlayer1PawnMaterial();
		player2PawnMaterial = orig.getPlayer2PawnMaterial();
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
	
	/**
	 * Applies a move to the game state
	 * @param move the move to be applied to this game state
	 * @return true if successful, 
	 * 		   false if not
	 */
	public boolean applyMove(GameAction act)
	{
		if(act == null)
		{
			return false;
		}
		//See if user has requested a draw (tie)
		if(act instanceof DrawAction)
		{
			DrawAction drawAct = (DrawAction)act;
			if(drawAct.getPlayer1().isPlayer1())
			{
				player2Won = true;
				player1Won = true;
			}
			
			//TODO implement
			return true;
		}
		
		if(act instanceof SelectUpgradeAction)
		{
			SelectUpgradeAction selectAct = (SelectUpgradeAction) act;
			ChessPiece piece = selectAct.getPiece();
			for(ChessPiece p:player1Pieces)
			{
				if(p.equals(piece))
				{
					piece = p;
					break;
				}
			}
			
			for(ChessPiece p:player2Pieces)
			{
				if(p.equals(piece))
				{
					piece = p;
					break;
				}
			}
			
			piece.setType(selectAct.getType());
		}
		
		//See if user has changed a piece
		if(act instanceof ChessMoveAction)
		{
			ChessMoveAction move = (ChessMoveAction)act;
			
			//Check for stalemate:
			if(lastCapture > MAX_MOVES_SINCE_CAPTURE)
			{
				isGameOver = true;
				player1Won = false;
				player2Won = false;
				return false;
			}
			moveList.add(move);
			
			return movePiece(move);
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Convert the game state into a readable chess board
	 */
	@Override
	public String toString()
	{
		String rtnVal = "";
		
		String turn = "";
		if(player1IsWhite == whoseTurn)//white's turn
		{
			turn = "White";
		}
		if(player1IsWhite != whoseTurn)//black's turn
		{
			turn = "Black";
		}
		
		rtnVal+="Turn: "+turn+"\n";
		
		rtnVal += "Moves: ";
		for(ChessMoveAction move:moveList)
		{
			rtnVal+=move.toString()+", ";
		}
		
		rtnVal +="State\n";
		for(int i=0;i<BOARD_HEIGHT;i++)
		{
			for(int j=0;j<BOARD_WIDTH;j++)
			{
				if(pieceMap[i][j] != null)
				{
					rtnVal+=pieceMap[i][j].toString();
				}
				else
				{
					rtnVal+="[ ]";
				}
			}
			rtnVal+="\n";
		}
		
		return rtnVal;
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
			return true;
		}
		if(loc.length != 2)
		{
			return true;
		}
		return outOfBounds(loc[1],loc[0]);
	}
	
	public static boolean outOfBounds(int x, int y)
	{
		if(y < 0 || y >= ChessGameState.BOARD_HEIGHT)
		{
			return true;
		}
		if(x < 0 || x >= ChessGameState.BOARD_WIDTH)
		{
			return true;
		}
		return false;
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
	 * Get the total worth of player 1's pieces
	 * @return player1Material
	 */
	public int getPlayer1Material() {
		return player1Material;
	}

	/**
	 * Sets the total worth of player 1's pieces
	 * @param player1Material
	 */
	public void setPlayer1Material(int player1Material) {
		this.player1Material = player1Material;
	}

	/**
	 * Get the total worth of player 2's pieces
	 * @return player1Material
	 */
	public int getPlayer2Material() {
		return player2Material;
	}

	/**
	 * Sets the total worth of player 2's pieces
	 * @param player2Material
	 */
	public void setPlayer2Material(int player2Material) {
		this.player2Material = player2Material;
	}
	
	/**
	 * Get the total worth of player 1's pawns
	 * @return player1PawnMaterial
	 */
	public int getPlayer1PawnMaterial() {
		return player1PawnMaterial;
	}

	/**
	 * Sets the total worth of player 1's pawns
	 * @param player1PawnMaterial
	 */
	public void setPlayer1PawnMaterial(int player1PawnMaterial) {
		this.player1PawnMaterial = player1PawnMaterial;
	}

	/**
	 * Get the total worth of player 2's pawns
	 * @return player2PawnMaterial
	 */
	public int getPlayer2PawnMaterial() {
		return player2PawnMaterial;
	}

	/**
	 * Sets the total worth of player 2's pawns
	 * @param player2PawnMaterial
	 */
	public void setPlayer2PawnMaterial(int player2PawnMaterial) {
		this.player2PawnMaterial = player2PawnMaterial;
	}
	
	public boolean isPlayer1Won() {
		return player1Won;
	}

	public void setPlayer1Won(boolean player1Won) {
		this.player1Won = player1Won;
	}

	public boolean isPlayer2Won() {
		return player2Won;
	}

	public void setPlayer2Won(boolean player2Won) {
		this.player2Won = player2Won;
	}
	/**
	 * @return the player1Idx
	 */
	public int getPlayer1Idx() {
		return playerIdx[1];
	}

	/**
	 * @return the player2Idx
	 */
	public int getPlayer2Idx() {
		return playerIdx[1];
	}
	
	/**
	 * 
	 * @param piece
	 * @return
	 */
	public boolean[][] getPossibleMoves(ChessPiece piece) {
		boolean[][] moves = null;// = new boolean[BOARD_WIDTH][BOARD_HEIGHT];

		if (piece == null)
			return null;// something bad happened

		//TODO check for illegal moves
		// Get coordinates of the piece in the piecemap:
		int[] location = piece.getLocation();
		int xLocation = location[1];
		int yLocation = location[0];

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
			moves = getKingMoves(xLocation, yLocation, piece);
			break;
		case ChessPiece.ROOK:
			moves = getRookMoves(xLocation, yLocation, piece);
			break;
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
	public boolean[][] getPawnMoves(int xLocation, int yLocation,
			ChessPiece piece) {
		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];
		if (piece.isWhite()) {
			if (yLocation - 1 >= 0) {
				if (this.pieceMap[yLocation - 1][xLocation] == null) {
					moves[yLocation - 1][xLocation] = true;
				}
			}
			// See if the squares in front are taken:
			if (piece.getHasMoved() == false) {
				if (yLocation - 2 >= 0) {
					if (this.pieceMap[yLocation - 2][xLocation] == null) {
						moves[yLocation - 2][xLocation] = true;
					}
				}
			}

			// See if the pawn can attack from its current location:
			if (xLocation - 1 >= 0 && yLocation - 1 >= 0) {
				if (this.pieceMap[yLocation - 1][xLocation - 1] != null
						&& this.pieceMap[yLocation - 1][xLocation - 1].isWhite() != piece.isWhite()) {
					moves[yLocation - 1][xLocation - 1] = true;
				}
			}

			if (xLocation + 1 < BOARD_WIDTH && yLocation - 1 >= 0) {
				if (this.pieceMap[yLocation - 1][xLocation + 1] != null
						&& this.pieceMap[yLocation - 1][xLocation + 1].isWhite() != piece.isWhite()) {
					moves[yLocation - 1][xLocation + 1] = true;
				}
			}
		} 
		else
		{
			if (yLocation + 1 < BOARD_HEIGHT) {
				if (this.pieceMap[yLocation + 1][xLocation] == null) {
					moves[yLocation + 1][xLocation] = true;
				}
			}
			// See if the squares in front are taken:
			if (piece.getHasMoved() == false) {
				if (yLocation + 2 >= 0) {
					if (this.pieceMap[yLocation + 2][xLocation] == null) {
						moves[yLocation + 2][xLocation] = true;
					}
				}
			}

			// See if the pawn can attack from its current location:
			if (xLocation - 1 >= 0 && yLocation + 1 < BOARD_HEIGHT) {
				if (this.pieceMap[yLocation + 1][xLocation - 1] != null
						&& this.pieceMap[yLocation + 1][xLocation - 1].isWhite() != piece.isWhite()) {
					moves[yLocation + 1][xLocation - 1] = true;
				}
			}

			if (xLocation + 1 < BOARD_WIDTH && yLocation + 1 < BOARD_HEIGHT) {
				if (this.pieceMap[yLocation + 1][xLocation + 1] != null
						&&  this.pieceMap[yLocation + 1][xLocation + 1].isWhite() != piece.isWhite()) {
					moves[yLocation + 1][xLocation + 1] = true;
				}
			}
		}
		return moves;
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
	private void checkKnightSpot(ChessPiece piece, int xLocation,
			int yLocation, boolean[][] moves) {
		if (yLocation >= 0 && yLocation < BOARD_HEIGHT && xLocation >= 0
				&& xLocation < BOARD_WIDTH) {
			// See if the spot is taken:
			if (this.pieceMap[yLocation][xLocation] == null) {
				moves[yLocation][xLocation] = true;
			} else if (this.pieceMap[yLocation][xLocation].isWhite() != piece.isWhite()) {
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
	private boolean[][] getBishopMoves(int xLocation, int yLocation,
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
		while (i > 0 && j < BOARD_HEIGHT && this.pieceMap[j][i] == null) {
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
	private boolean[][] getKingMoves(int xLocation, int yLocation,
			ChessPiece piece) {
		
		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];

		for(int i = xLocation-1;i<=xLocation+1;i++)
		{
			for(int j = yLocation-1;j<=yLocation+1;j++)
			{
				if(i != xLocation && j != yLocation && !outOfBounds(i,j))
				{
					if(pieceMap[j][i] == null || pieceMap[j][i].isWhite() != piece.isWhite())
					{
						moves[j][i] = true;
					}
				}
			}
		}

		return moves;
	}

	/**
	 * Determines the postition the Rook can move to.
	 * 
	 * @param xLocation
	 *            of the current piece
	 * @param yLocation
	 *            of the current piece
	 * @param piece
	 *            of interest
	 * @return 2-D array, true means the piece can move there
	 */
	private boolean[][] getRookMoves(int xLocation, int yLocation,
			ChessPiece piece) {
		// TODO think about how to do this more succinctly...
		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];
		int i = xLocation + 1;
		int j = yLocation;

		// check to the EAST
		while (i < BOARD_WIDTH && j < BOARD_WIDTH
				&& this.pieceMap[j][i] == null) {
			moves[j][i] = true;
			i++;
		}
		if (i < BOARD_WIDTH && j >= 0) {
			if (this.pieceMap[j][i] != null) {
				if (this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}
		}
		// check to the west
		i = xLocation - 1;
		j = yLocation;

		while (i >= 0 && j < BOARD_WIDTH && this.pieceMap[j][i] == null) {
			moves[j][i] = true;
			i--;
		}
		if (i >= 0 && j >= 0) {
			if (this.pieceMap[j][i] != null) {
				if (this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}
		}

		// check south

		i = xLocation;
		j = yLocation + 1;

		while (i >= 0 && j < BOARD_WIDTH && this.pieceMap[j][i] == null) {
			moves[j][i] = true;
			j++;
		}
		if (i < BOARD_WIDTH && j < BOARD_HEIGHT) {
			if (this.pieceMap[j][i] != null) {
				if (this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}
		}
		// check to the NORTH

		i = xLocation;
		j = yLocation - 1;

		while (j >= 0 && i < BOARD_WIDTH && this.pieceMap[j][i] == null) {
			moves[j][i] = true;
			j--;
		}
		if (i >= 0 && j >= 0) {
			if (this.pieceMap[j][i] != null) {
				if (this.pieceMap[j][i].isWhite() != piece.isWhite()) {
					moves[j][i] = true;
				}
			}
		}
		return moves;
	}

	/**
	 * Determines the postition the Rook can move to.
	 * 
	 * @param xLocation
	 *            of the current piece
	 * @param yLocation
	 *            of the current piece
	 * @param piece
	 *            of interest
	 * @return 2-D array, true means the piece can move there
	 */
	private boolean[][] getQueenMoves(int xLocation, int yLocation,
			ChessPiece piece) {
		// TODO think about how to do this more succinctly...
		boolean[][] moves = new boolean[BOARD_WIDTH][BOARD_HEIGHT];
		// check to the east
		int i = xLocation + 1;
		int j = yLocation;
		if (i < BOARD_WIDTH) {
			if (this.pieceMap[j][i] == null
					|| this.pieceMap[j][i].isWhite() != piece.isWhite()) {
				moves[j][i] = true;
			}
		}

		// check southeast
		i = xLocation + 1;
		j = yLocation + 1;
		if (i < BOARD_WIDTH && j < BOARD_HEIGHT) {
			if (this.pieceMap[j][i] == null
					|| this.pieceMap[j][i].isWhite() != piece.isWhite()) {
				moves[j][i] = true;
			}
		}

		// check south
		i = xLocation;
		j = yLocation + 1;
		if (i < BOARD_WIDTH && j < BOARD_HEIGHT) {
			if (this.pieceMap[j][i] == null
					|| this.pieceMap[j][i].isWhite() != piece.isWhite()) {
				moves[j][i] = true;
			}
		}

		// check southwest

		i = xLocation - 1;
		j = yLocation + 1;
		if (i >= 0 && j < BOARD_HEIGHT) {
			if (this.pieceMap[j][i] == null
					|| this.pieceMap[j][i].isWhite() != piece.isWhite()) {
				moves[j][i] = true;
			}
		}

		// check west
		i = xLocation - 1;
		j = yLocation;
		if (i >= 0) {
			if (this.pieceMap[j][i] == null
					|| this.pieceMap[j][i].isWhite() != piece.isWhite()) {
				moves[j][i] = true;
			}
		}

		// check northWest
		i = xLocation - 1;
		j = yLocation - 1;
		if (i >= 0 && j >= 0) {
			if (this.pieceMap[j][i] == null
					|| this.pieceMap[j][i].isWhite() != piece.isWhite()) {
				moves[j][i] = true;
			}
		}

		// check north
		i = xLocation;
		j = yLocation - 1;
		if (j >= 0) {
			if (this.pieceMap[j][i] == null
					|| this.pieceMap[j][i].isWhite() != piece.isWhite()) {
				moves[j][i] = true;
			}
		}

		// check northEast
		i = xLocation + 1;
		j = yLocation - 1;
		if (i < BOARD_WIDTH && j >= 0) {
			if (this.pieceMap[j][i] == null
					|| this.pieceMap[j][i].isWhite() != piece.isWhite()) {
				moves[j][i] = true;
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
		// make sure the new location the piece is moving to is valid:
		int[] position = act.getNewPos();
		int newYPos = position[0];
		int newXPos = position[1];

		ChessPiece piece = act.getWhichPiece();
		int oldYPos = piece.getLocation()[0];
		int oldXPos = piece.getLocation()[1];
		
		//Get the version of the piece in this state
		for(ChessPiece p:player1Pieces)
		{
			if(p.equals(piece))
			{
				piece = p;
				break;
			}
		}
		
		for(ChessPiece p:player2Pieces)
		{
			if(p.equals(piece))
			{
				piece = p;
				break;
			}
		}

		boolean[][] validMoves = this.getPossibleMoves(piece);

		if(pieceMap[newYPos][newXPos] != null)
		{
			pieceMap[newYPos][newXPos].kill();
			lastCapture = 0;
		}
		else
		{
			lastCapture++;
		}
		
		if(act instanceof PawnMove)
		{
			/*PawnMove pawnAct = (PawnMove) act;
			if(pawnAct.getType() == PawnMove.PROMOTION)
			{
				//TODO 
			}*/
		}
		if (validMoves[newYPos][newXPos] == true)
		{
			// If the move is valid, apply the move and return true
			pieceMap[newXPos][newYPos] = piece;
			pieceMap[oldXPos][oldYPos] = null;
			
			piece.move(position);
			whoseTurn = !whoseTurn;
			return true;
		}
		else
		{
			// do nothing and return false
			return false;
		}
	}
}
