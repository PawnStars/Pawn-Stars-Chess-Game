package edu.up.cs301.chess.engine;

import java.util.ArrayList;

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
	public static ChessMoveAction[] getPossibleMoves(ChessGameState state,
			ChessPlayer player)
	{
		//null check
		if(state == null || player == null)
		{
			return null;
		}
		//going to contain each array of moves in a 2d array
		ChessMoveAction[][] moveList2d = new ChessMoveAction[ChessGameState.NUM_PIECES][];
		
		if(player.isPlayer1())//player 1
		{
			ChessPiece[] pieces = state.getPlayer1Pieces();
			for(int i=0;i<ChessGameState.NUM_PIECES;i++)
			{
				if(pieces[i].isAlive())
				{
					/*
					 * get all possible moves the player can make
					 * including ones that do not protect the king
					 */
					ChessMoveAction[] newActions = getPieceMoves(state, pieces[i], player, player.isWhite(), false);
					moveList2d[i] = newActions;
				}
			}
		}
		else //player 2
		{
			ChessPiece[] pieces = state.getPlayer2Pieces();
			for(int i=0;i<ChessGameState.NUM_PIECES;i++)
			{
				if(pieces[i].isAlive())
				{
					ChessMoveAction[] newActions = getPieceMoves(state, pieces[i], player, player.isWhite(), false);
					moveList2d[i] = newActions;
				}
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
		ChessMoveAction[] moves = removeIllegalMoves(state, moveList, player);
		
		//TODO scramble the order of the moves??
		//remove the moves that would get the king captured
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
	 * Returns an array of ChessMoveActions that a given piece can make
	 * @param state
	 * @param piece
	 * @param currPlayer
	 * @param color
	 * @param legal
	 * @return an array of moves
	 */
	public static ChessMoveAction[] getPieceMoves(ChessGameState state,
			ChessPiece piece, ChessPlayer currPlayer, boolean color, boolean legal)
	{
		//check for null pointers
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
		
		ArrayList<ChessMoveAction> moveList = new ArrayList<ChessMoveAction>();
		
		if(type == ChessPiece.PAWN)
		{
			//add the horizontal moves
			int numVertMoves;
			if(piece.getHasMoved() == false)
			{
				numVertMoves = 2;
			}
			else
			{
				numVertMoves = 1;
			}
			for(int i=1;i<=numVertMoves;i++)
			{
				
				int[] newLoc = loc.clone();
				if(piece.isWhite())//TODO make sure this works
				{
					newLoc[0] -= i;
				}
				else
				{
					newLoc[0] += i;
				}
				
				if(!ChessGameState.outOfBounds(newLoc))
				{
					ChessPiece taken = state.getPieceMap()[newLoc[0]][newLoc[1]];
					
					//can't take a piece vertically
					if(taken == null)
					{
						moveList.add(new ChessMoveAction(currPlayer, piece, newLoc, taken));
					}
					else
					{
						i=100;
						//break out of the loop somehow
						//dont want to move to the place behind a piece
					}
				}
			}
			
			/*
			 * Initialize to an invalid location so it does not get turned into a move 
			 * unless player 1 or player 2 can make the move.
			 * 
			 * */
			int[][] attackLoc = new int[][]{ChessPiece.INVALID_LOCATION,ChessPiece.INVALID_LOCATION};
			if(piece.isWhite() && state.isPlayer1IsWhite())//player 1
			{
				attackLoc = new int[][]{
						{loc[0]-1,loc[1]-1},
						{loc[0]-1,loc[1]+1}
				};
			}
			else if(!piece.isWhite() && !state.isPlayer1IsWhite())//player 2
			{
				attackLoc = new int[][]{
						{loc[0]+1,loc[1]-1},
						{loc[0]+1,loc[1]+1}
				};
			}
			
			//add the left and right capture moves
			for(int i=0;i<PawnMove.NUM_PAWN_ATTACKS_NORMAL;i++)
			{
				if(!ChessGameState.outOfBounds(attackLoc[i]))
				{
					//TODO not sure if this part is right
					ChessPiece taken = state.getPieceMap()[attackLoc[i][0]][attackLoc[i][1]];
					
					//it can take a piece of a different color
					if(taken != null && taken.isWhite() != color)
					{
						moveList.add(new ChessMoveAction(currPlayer, piece, attackLoc[i], taken));
					}
				}
			}
		}
		else if(type == ChessPiece.ROOK || type == ChessPiece.QUEEN)
		{
			
			//horizontal to the right
			for(int i=loc[0]+1;i<ChessGameState.BOARD_HEIGHT;i++)
			{
				int[] newLoc = new int[2];
				newLoc[0] = i;
				newLoc[1] = loc[1];
				
				boolean done = addMove(state,piece,moveList,newLoc,currPlayer,color);
				if(done == true)
				{
					break;
				}
			}
			
			//horizontal to the left
			for(int i=loc[0]-1;i>=0;i--)
			{
				int[] newLoc = new int[2];
				newLoc[0] = i;
				newLoc[1] = loc[1];
				
				boolean done = addMove(state,piece,moveList,newLoc,currPlayer,color);
				if(done == true)
				{
					break;
				}
			}
			
			//vertical going up
			for(int i=loc[0]+1;i<ChessGameState.BOARD_HEIGHT;i++)
			{
				int[] newLoc = new int[2];
				newLoc[0] = loc[0];
				newLoc[1] = i;
				
				boolean done = addMove(state,piece,moveList,newLoc,currPlayer,color);
				if(done == true)
				{
					break;
				}
			}
			
			//vertical going down
			for(int i=loc[0]-1;i>=0;i--)
			{
				int[] newLoc = new int[2];
				newLoc[0] = loc[0];
				newLoc[1] = i;
				
				boolean done = addMove(state,piece,moveList,newLoc,currPlayer,color);
				if(done == true)
				{
					break;
				}
			}
		}
		else if(type == ChessPiece.KNIGHT)
		{
			int[][] newLoc = {
					{loc[0]+1,loc[1]+2},//top-mid right
					{loc[0]+2,loc[1]+1},//top right
					
					{loc[0]+1,loc[1]-2},//bottom-mid right
					{loc[0]+2,loc[1]-1},//bottom right
					
					{loc[0]-1,loc[1]+2},//top-mid left
					{loc[0]-2,loc[1]+1},//top left
					
					{loc[0]-1,loc[1]-2},//bottom-mid left
					{loc[0]-2,loc[1]-1}//bottom left
			};
			
			//iterate through each possible knight move
			for(int i=0;i<newLoc.length;i++)
			{
				if(!ChessGameState.outOfBounds(newLoc[i]))
				{
					ChessPiece taken = state.getPieceMap()[newLoc[i][0]][newLoc[i][1]];
					
					//space is occupied
					if(taken != null)
					{
						if(taken.isWhite() != color)
						{
							//add a move only if the tile is empty or 
							moveList.add(new ChessMoveAction(currPlayer, piece, newLoc[i], taken));
						}
					}
					else //unoccupied
					{
						moveList.add(new ChessMoveAction(currPlayer, piece, newLoc[i], taken));
					}
				}
			}
		}
		else if(type == ChessPiece.BISHOP || type == ChessPiece.QUEEN)
		{
			int[] newLoc = new int[2];
			
			//top right
			for(newLoc[0]=loc[0]+1,newLoc[1]=loc[1]+1;!ChessGameState.outOfBounds(newLoc);newLoc[0]++,newLoc[1]++)
			{
				int[] newPieceLoc = newLoc.clone();
				boolean done = addMove(state,piece,moveList,newPieceLoc,currPlayer,color);
				if(done == true)
				{
					break;
				}
			}
			
			//top left
			for(newLoc[0]=loc[0]-1,newLoc[1]=loc[1]+1;!ChessGameState.outOfBounds(newLoc);newLoc[0]--,newLoc[1]++)
			{
				int[] newPieceLoc = newLoc.clone();
				boolean done = addMove(state,piece,moveList,newPieceLoc,currPlayer,color);
				if(done == true)
				{
					break;
				}
			}
			
			//bottom right
			for(newLoc[0]=loc[0]+1,newLoc[1]=loc[1]-1;!ChessGameState.outOfBounds(newLoc);newLoc[0]++,newLoc[1]--)
			{
				int[] newPieceLoc = newLoc.clone();
				boolean done = addMove(state,piece,moveList,newPieceLoc,currPlayer,color);
				if(done == true)
				{
					break;
				}
			}
			
			//bottom left
			for(newLoc[0]=loc[0]-1,newLoc[1]=loc[1]-1;!ChessGameState.outOfBounds(newLoc);newLoc[0]--,newLoc[1]--)
			{
				int[] newPieceLoc = newLoc.clone();
				boolean done = addMove(state,piece,moveList,newPieceLoc,currPlayer,color);
				if(done == true)
				{
					break;
				}
			}
		}
		else if(type == ChessPiece.KING)
		{
			//iterate through the locations surrounding the king
			for(int i=loc[0]-1;i<=loc[0]+1;i++)
			{
				for(int j=loc[1]-1;j<=loc[1]+1;j++)
				{
					if(i != loc[0] && j != loc[1])
					{
						addMove(state,piece,moveList,new int[]{i,j},currPlayer,color);
					}
				}
			}
		}
		ChessMoveAction[] rtnVal = moveList.toArray(new ChessMoveAction[moveList.size()]);
		
		if(legal)
		{
			rtnVal =  removeIllegalMoves(state, rtnVal,currPlayer);
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
	private static boolean addMove(ChessGameState state,
			ChessPiece piece, ArrayList<ChessMoveAction> moveList,
			int[] newLoc, ChessPlayer player, boolean color)
	{
		if(newLoc == null || newLoc.length != 2)
		{
			return false;
		}
		if(ChessGameState.outOfBounds(newLoc))
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
		int[] newPieceLoc = newLoc.clone();
		ChessPiece taken = state.getPieceMap()[newLoc[0]][newLoc[1]];
		
		//space is occupied
		if(taken != null)
		{
			if(taken.isWhite() != color)
			{
				//add a move if it can take a piece
				moveList.add(new ChessMoveAction(player, piece, newPieceLoc, taken));
			}
			return true;
		}
		else //unoccupied
		{
			moveList.add(new ChessMoveAction(player, piece, newPieceLoc, taken));
			return false;
		}
	}

	/**
	 * You must defend yourself from check threats, so this function removes
	 * all moves that do not protect the king.
	 * 
	 * @param state
	 * @param moves
	 * @return a list of legal moves
	 */
	public static ChessMoveAction[] removeIllegalMoves(ChessGameState state,
			ChessMoveAction[] moves, ChessPlayer currPlayer)
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
			if(canTakeKing(nextStates[i], !currPlayer.isPlayer1()))
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
					if(currPlayer != null)
					{
						if(moves[i].getTakenPiece().isWhite() == currPlayer.isWhite())
						{
							moves[i] = null;
							numRemoved++;
						}
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
