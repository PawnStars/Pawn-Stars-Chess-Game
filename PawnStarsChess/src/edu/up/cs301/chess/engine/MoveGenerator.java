package edu.up.cs301.chess.engine;

import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import edu.up.cs301.chess.ChessGameState;
import edu.up.cs301.chess.ChessPiece;
import edu.up.cs301.chess.ChessPlayer;
import edu.up.cs301.chess.actions.ChessMoveAction;
import edu.up.cs301.chess.actions.PawnMove;

/**
 * This generates a list of moves that the AI will use to make a move.
 * .
 * @author Allison Liedtke
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @version April 2015
 *
 */
public class MoveGenerator {
	
	/**
	 * Returns an array containing the moves a player can make
	 * 
	 * @param state the current ChessGameState
	 * @param player the current player
	 * @return an array of moves
	 */
	public static ChessMoveAction[] getPossibleMoves(ChessGameState state, ChessPlayer player, boolean color)
	{
		//null check
		if(state == null)
		{
			return null;
		}
		
		//Contain each array of moves in a 2d array
		ChessMoveAction[][] moveList2d = new ChessMoveAction[ChessGameState.NUM_PIECES][];
		
		//get the pieces that can move
		ChessPiece[] pieces;
		if(state.isWhoseTurn())//player 1's turn
		{
			pieces = state.getPlayer1Pieces();
		}
		else//player 2's turn
		{
			pieces = state.getPlayer2Pieces();
		}
		for(int i=0;i<ChessGameState.NUM_PIECES;i++)
		{
			if(pieces[i].isAlive())
			{
				/*
				 * get all possible moves the player can make
				 * including ones that do not protect the king
				 */
				
				ChessMoveAction[] newActions = getPieceMoves(state, pieces[i], player, color, false);
				moveList2d[i] = newActions;
			}
		}
		
		
		//Calculate the length of the new array
		int length = 0;
		for(int i=0;i<ChessGameState.NUM_PIECES;i++)
		{
			if(moveList2d[i] != null)
			{
				length += moveList2d[i].length;
			}
		}
		
		//Add every move into the array
		ChessMoveAction[] moveList = new ChessMoveAction[length];
		int c=0;
		for(int i=0;i<ChessGameState.NUM_PIECES;i++)
		{
			if(moveList2d[i] != null)
			{
				for(int j=0;j<moveList2d[i].length;j++)
				{
					if(moveList2d[i][j] != null)
					{
						moveList[c++] = moveList2d[i][j];
					}
				}
			}
		}
		
		//remove the moves that would get the king captured
		ChessMoveAction[] moves = removeIllegalMoves(state, moveList, color);
		
		return moves;
	}
	
	public static ChessMoveAction[] getEvasions(ChessGameState state, ChessPlayer player)
	{
		return null;
	}
	
	public static ChessMoveAction[] getCapturesAndChecks(ChessGameState state, ChessPlayer player)
	{
		return null;
	}
	
	public static ChessMoveAction[] getCaptures(ChessGameState state, ChessPlayer player)
	{
		return null;
	}
	
	public static boolean isInCheck(ChessGameState state, ChessPlayer player)
	{
		return false;
	}
	
	public static boolean givesCheck(ChessGameState state, ChessPlayer player, ChessMoveAction move)
	{
		return false;
	}
	
	/**
	 * Returns true if the king can be taken in the next move
	 * @param state
	 * @param player
	 * @return
	 */
	public static boolean canTakeKing(ChessGameState state, boolean isPlayer1)
	{
		ChessPiece[] moveablePieces;
		boolean moveColor;
		if(state.isWhoseTurn() && isPlayer1)//player 1's turn
		{
			moveablePieces = state.getPlayer1Pieces().clone();
			moveColor = state.isPlayer1IsWhite();
		}
		else if(!state.isWhoseTurn() && !isPlayer1)//player 2's turn
		{
			moveablePieces = state.getPlayer2Pieces().clone();
			moveColor = !state.isPlayer1IsWhite();
		}
		else
		{
			return false;
		}
		if(moveablePieces != null)
		{
			for(ChessPiece p: moveablePieces)
			{
				ChessPiece copyOfP = new ChessPiece(p);
				ChessMoveAction[] moves = getPieceMoves(state,copyOfP,null,!moveColor,false);
				if(moves != null)
				{
					for(ChessMoveAction move: moves)
					{
						if(move != null && move.getTakenPiece() != null)
						{
							ChessPiece piece = move.getTakenPiece();
							if(piece.getType() == ChessPiece.KING)
							{
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
	 * Returns true if the king can be taken in the next move
	 * @param state
	 * @param player
	 * @return
	 */
	public static boolean willTakeKing(ChessGameState state, boolean isPlayer1)
	{
		//Get all the moves that won't get the king captured
		ChessMoveAction[] possibleMoves = getPossibleMoves(state,null,true);

		//If there are none, you lose
		return possibleMoves.length == 0;
	}
	
	/**
	 * Returns an array of ChessMoveActions that a given piece can make
	 * @param state
	 * @param piece
	 * @param currPlayer
	 * @param color
	 * @param legal
	 * @return an array of moves
	 */
	public static ChessMoveAction[] getPieceMoves(ChessGameState state,
			ChessPiece piece, ChessPlayer player, boolean color, boolean legal)
	{
		//Check for null pointers
		if(state == null || piece == null)
		{
			return null;
		}
		
		int type = piece.getType();
		int[] loc = piece.getLocation();
		if(type == ChessPiece.INVALID || loc == null)
		{
			return null;
		}
		
		//Get the locations the piece can move to
		boolean[][] possibleLocs = state.getPossibleMoves(piece);
		
		//Add a move for each location it can more to
		ArrayList<ChessMoveAction> moveList = new ArrayList<ChessMoveAction>();
		for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
		{
			for(int j=0;j<ChessGameState.BOARD_WIDTH;j++)
			{
				if(possibleLocs[i][j])
				{
					int[] newLoc = new int[]{i,j};
					//addMove(state,piece,moveList,newLoc,player,color);
					ChessPiece taken = state.getPieceMap()[newLoc[0]][newLoc[1]];
					moveList.add(new ChessMoveAction(player, piece, newLoc, taken));
				}
			}
		}
		
		//Convert to an array
		ChessMoveAction[] rtnVal = moveList.toArray(new ChessMoveAction[moveList.size()]);
		if(legal)
		{
			rtnVal =  removeIllegalMoves(state, rtnVal,color);
		}
		/*String debugMsg = "";
		debugMsg+="Possible moves: ";
		for(ChessMoveAction move:rtnVal)
		{
			debugMsg +=move.toString()+" ";
		}
		Log.d("Move Generator", debugMsg);*/
		return rtnVal;
	}
	
	/**
	 * Add a move to an ArrayList given a piece to move, its location,
	 * and the color of the player
	 * @param state
	 * @param piece
	 * @param moveList
	 * @param newLoc
	 * @param player
	 * @return true if this move takes a piece
	 */
	/*private static boolean addMove(ChessGameState state,
			ChessPiece piece, ArrayList<ChessMoveAction> moveList,
			int[] newLoc, ChessPlayer player, boolean color)
	{
		//null checks
		if(newLoc == null || newLoc.length != 2)
		{
			return false;
		}
		if(state == null || state.getPieceMap() == null)
		{
			return false;
		}
		if(moveList == null)
		{
			return false;
		}
		if(piece == null)
		{
			return false;
		}
		//check for out of bounds
		if(ChessGameState.outOfBounds(newLoc))
		{
			return false;
		}
		//check for a move to the same square
		if(Arrays.equals(newLoc,piece.getLocation()))
		{
			return false;
		}
		
		ChessPiece taken = state.getPieceMap()[newLoc[0]][newLoc[1]];
		
		//space is occupied
		if(taken != null)
		{
			if(taken.isWhite() != color)
			{
				//add a move if it can take a piece
				moveList.add(new ChessMoveAction(player, piece, newLoc, taken));
			}
			return true;
		}
		else //unoccupied
		{
			moveList.add(new ChessMoveAction(player, piece, newLoc, taken));
			return false;
		}
	}*/

	/**
	 * You must defend yourself from check threats, so this function removes
	 * all moves that do not protect the king.
	 * 
	 * @param state
	 * @param moves
	 * @return a list of legal moves
	 */
	public static ChessMoveAction[] removeIllegalMoves(ChessGameState state,
			ChessMoveAction[] moves, boolean color)
	{
		
		ChessGameState[] nextStates = new ChessGameState[moves.length];
		
		// Apply each move to a new game state
		for(int i=0;i<moves.length;i++)
		{
			ChessGameState copy = new ChessGameState(state);
			copy.applyMove(moves[i]);
			nextStates[i] = copy;
		}
		
		// Find which states allow the king to be taken
		int numRemoved = 0;
		for(int i=0;i<nextStates.length;i++)
		{
			if(canTakeKing(nextStates[i], color == state.isPlayer1IsWhite()))
			{
				moves[i] = null;
				numRemoved++;
			}
		}
		
		// Remove moves that take your own pieces
		for(int i=0;i<moves.length;i++)
		{
			if(moves[i] != null)
			{
				if(moves[i].getTakenPiece() != null)
				{
					if(moves[i].getTakenPiece().isWhite() == color)
					{
						moves[i] = null;
						numRemoved++;//TODO not sure if this is ever executed
					}
				}
			}
		}
		
		// Make a new array with the null moves removed
		int j=0;
		ChessMoveAction[] legalMoves = new ChessMoveAction[moves.length-numRemoved];
		
		for(int i=0;i<moves.length;i++)
		{
			if(moves[i] != null)
			{
				legalMoves[j++] = moves[i];
			}
		}
		
		return legalMoves;
	}
}
