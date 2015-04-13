package edu.up.cs301.chess;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;

import android.util.Log;

import edu.up.cs301.chess.actions.*;
import edu.up.cs301.chess.engine.MoveGenerator;
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
	private ChessPiece[][] pieces;
	
	//The array of pieces for each player
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
		pieces = new ChessPiece[MAX_PLAYERS][NUM_PIECES];
		player1Pieces = pieces[0];
		player2Pieces = pieces[1];
	
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
			player1Pieces[i].setLocation(loc1.clone());
			player2Pieces[i] = new ChessPiece(ChessPiece.PAWN, !player1IsWhite);
			player2Pieces[i].setLocation(loc2.clone());
			
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
		
		pieces = new ChessPiece[MAX_PLAYERS][NUM_PIECES];
		pieces[0] = player1Pieces;
		pieces[1] = player2Pieces;
		
		for(int i=0;i<MAX_PLAYERS;i++)
		{
			for(int j=0;j<NUM_PIECES;j++)
			{
				int[] loc = pieces[i][j].getLocation();
				if(!outOfBounds(loc))
				{
					pieceMap[loc[0]][loc[1]] = pieces[i][j];
				}
			}
		}
		
		//copy each move
		moveList = new ArrayDeque<ChessMoveAction>();
		Iterator<ChessMoveAction> it = orig.getMoveList().iterator();
		while(it.hasNext())
		{
			ChessMoveAction move = it.next();
			ChessMoveAction newMove = new ChessMoveAction(move.getPlayer(),move);
			moveList.add(newMove);
		}
		
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
		
		player1Material = orig.getPlayer1Material();
		player2Material = orig.getPlayer2Material();
		
		player1PawnMaterial = orig.getPlayer1PawnMaterial();
		player2PawnMaterial = orig.getPlayer2PawnMaterial();
		
		lastCapture = orig.getLastCapture();
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
		if(!Arrays.deepEquals(player1Pieces, comp.getPlayer1Pieces()))
			return false;
		if(!Arrays.deepEquals(player2Pieces, comp.getPlayer2Pieces()))
			return false;
		
		// Check if all primitive instance variables are equals
		if(player1Points != comp.getPlayer1Points()) return false;
		if(player2Points != comp.getPlayer2Points()) return false;
		
		if(whoseTurn != comp.isWhoseTurn()) return false;
		
		if(player1InCheck != comp.isPlayer1InCheck()) return false;

		if(player2InCheck != comp.isPlayer2InCheck()) return false;

		if(isGameOver != comp.isGameOver()) return false;

		if(canCastle != comp.getCanCastle()) return false;//TODO fix this

		if(player1IsWhite != comp.getPlayer1Color()) return false;
		
		//TODO Check if moveList is equal
		
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
		//Do not apply the move if the game is over or there is no move.
		if(act == null || isGameOver)
		{
			return false;
		}
		if(act instanceof DrawAction)
		{
			isGameOver = true;
			player1Won = true;
			player2Won = true;
			return true;
		}
		if(act instanceof ChooseColorAction)
		{
			ChooseColorAction action = (ChooseColorAction)act;
			player1IsWhite = action.isWhichColor();
			return true;
		}
		if(act instanceof SelectUpgradeAction)
		{
			SelectUpgradeAction action = (SelectUpgradeAction)act;
			return applySelectUpgradeAction(action);
		}
		if(act instanceof ChessMoveAction)
		{
			ChessMoveAction move = (ChessMoveAction)act;
			ChessPlayer player = ((ChessPlayer)act.getPlayer());
			ChessPiece takenPiece = move.getTakenPiece();
			ChessPiece whichPiece = move.getWhichPiece();
			boolean foundTaken = false;
			boolean foundWhich = false;
			
			//check if it is this player's turn
			if(player != null && player.isPlayer1() == whoseTurn)
			{
				//Statemate
				if(lastCapture > MAX_MOVES_SINCE_CAPTURE)
				{
					isGameOver = true;
					player1Won = false;
					player2Won = false;
					return true;
				}
				
				for(int i=0;i<MAX_PLAYERS;i++)
				{
					for(int j=0;j<NUM_PIECES;j++)
					{
						ChessPiece p = pieces[i][j];
						if(p.equals(takenPiece))
						{
							//remove the piece from the board
							takenPiece = p;
							foundTaken = true;
						}
						//find the piece to be moved
						if(p.equals(whichPiece))
						{
							whichPiece = p;
							foundWhich = true;
						}
					}
				}
				
				if(takenPiece == null)
				{
					lastCapture++;
				}
				
				if(foundWhich && whichPiece != null)
				{
					//Move the piece
					int[] oldLoc = whichPiece.getLocation().clone();
					int[] newLoc = move.getNewPos().clone();
					
					//If a piece was taken, kill it and remove its reference
					if(foundTaken && takenPiece != null)
					{
						int[] takenLoc = takenPiece.getLocation();
						if(!outOfBounds(takenLoc))
						{
							takenPiece.kill();
							pieceMap[takenLoc[0]][takenLoc[1]] = null;
							lastCapture = 0;
						}
					}
					
					//If the new position is a valid position, make the move.
					if(!outOfBounds(newLoc) && !outOfBounds(oldLoc))
					{
						whichPiece.move(newLoc);
						ChessPiece movedPiece = new ChessPiece(whichPiece);
						pieceMap[newLoc[0]][newLoc[1]] = movedPiece;
						pieceMap[oldLoc[0]][oldLoc[1]] = null;
						moveList.add(move);
					}
					
					//Check if the game is over or if a player is in check.
					boolean p1CanTakeKing = MoveGenerator.canTakeKing(this, true);
					boolean p2CanTakeKing = MoveGenerator.canTakeKing(this, false);
					
					for(int i=0;i<MAX_PLAYERS;i++)
					{
						for(int j=0;j<NUM_PIECES;j++)
						{
							ChessPiece p = pieces[i][j];
							if(p.getType() == ChessPiece.KING)
							{
								if(!p.isAlive())
								{
									if(p.isWhite() && player1IsWhite)
									{
										player1Won = true;
									}
									else
									{
										player2Won = true;
									}
								}
							}
						}
					}
					
					//Someone is in check
					if(p1CanTakeKing && !whoseTurn)
					{
						player2InCheck = true;
					}
					if(p2CanTakeKing && whoseTurn)
					{
						player1InCheck = true;
					}
					
					//Someone is in checkmate
					if(player1Won || player2Won)
					{
						isGameOver = true;
					}
					
					whoseTurn = !whoseTurn;
					
					return true;
				}
				else//Could not find the piece to move
				{
					return false;
				}
			}
			else//Could not find player or not the player's turn
			{
				return false;
			}
		}
		else//Cannot handle whatever move was given.
		{
			return false;
		}
	}
	
	private boolean applySelectUpgradeAction(SelectUpgradeAction action)
	{
		ChessPiece upgradePiece = action.getPiece();
		for(int i=0;i<MAX_PLAYERS;i++)
		{
			for(int j=0;j<NUM_PIECES;j++)
			{
				ChessPiece p = pieces[i][j];
				if(p.equals(upgradePiece))
				{
					p.setType(action.getType());
					return true;
				}
			}
		}
		return false;//didn't find the piece
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
		rtnVal+="Score: "+player1Points+"\n";
		
		//List the moves made so far
		rtnVal += "Moves: ";
		for(ChessMoveAction move:moveList)
		{
			rtnVal+=move.toString()+",";
		}
		
		//Print the board
		rtnVal +="\nState\n";
		for(int i=0;i<BOARD_HEIGHT;i++)
		{
			for(int j=0;j<BOARD_WIDTH;j++)
			{
				if(pieceMap[i][j] != null)
				{
					//print the character corresponding to the chess piece
					rtnVal+="["+pieceMap[i][j].toCharacter()+"]";
				}
				else
				{
					//empty tiles on the board
					rtnVal+="[_]";
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
		//Check for null
		if(loc == null)
		{
			return true;
		}
		if(loc.length != 2)
		{
			return true;
		}
		
		//Check for coordinates outside the pieceMap
		if(loc[0] < 0 || loc[0] >= ChessGameState.BOARD_HEIGHT)
		{
			return true;
		}
		if(loc[1] < 0 || loc[1] >= ChessGameState.BOARD_WIDTH)
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

	public int getLastCapture() {
		return lastCapture;
	}
}
