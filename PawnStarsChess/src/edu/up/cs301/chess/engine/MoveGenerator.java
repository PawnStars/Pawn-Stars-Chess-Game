package edu.up.cs301.chess.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

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
 * @version March 2015
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
		if(state == null || player == null)
		{
			return null;
		}
		ArrayList<ChessMoveAction[]> moveList2d = new ArrayList<ChessMoveAction[]>();
		//player 1
		if(state.isPlayer1IsWhite() == true && player.isWhite() == true)
		{
			if(state.isWhoseTurn())//true if player 1's turn
			{
				ChessPiece[] pieces = state.getPlayer1Pieces();
				for(int i=0;i<ChessGameState.NUM_PIECES;i++)
				{
					if(pieces[i].isAlive())
					{
						//get all possible moves the player can make
						//including ones that do not protect the king
						ChessMoveAction[] newActions = getPieceMoves(state, pieces[i], player, player.isWhite(), false);
						moveList2d.add(newActions);
					}
				}
			}
		}
		else //player 2
		{
			if(!state.isWhoseTurn())
			{
				ChessPiece[] pieces = state.getPlayer1Pieces();
				for(int i=0;i<ChessGameState.NUM_PIECES;i++)
				{
					if(pieces[i].isAlive())
					{
						ChessMoveAction[] newActions = getPieceMoves(state, pieces[i], player, player.isWhite(), false);
						moveList2d.add(newActions);
					}
				}
			}
		}
		//Calculate the length of the new array
		int length = 0;
		for(ChessMoveAction[] actions: moveList2d)
		{
			length+= actions.length;
		}
		
		//Add every move into the array
		ChessMoveAction[] moveList = new ChessMoveAction[length];
		int i=0;
		for(ChessMoveAction[] actions: moveList2d)
		{
			for(ChessMoveAction action:actions)
			{
				moveList[i++] = action;
			}
		}
		
		//remove the moves that would get the king captured
		return removeIllegalMoves(state, moveList, player);
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
	public static boolean canTakeKing(ChessGameState state, ChessPlayer player)
	{
		ChessPiece[] moveablePieces;
		boolean moveColor;
		if(state.isWhoseTurn())//player 1's turn
		{
			moveablePieces = state.getPlayer1Pieces();
			moveColor = state.getPlayer1Color();
		}
		else//player 2's turn
		{
			moveablePieces = state.getPlayer2Pieces();
			moveColor = !state.getPlayer1Color();
		}
		for(ChessPiece p: moveablePieces)
		{
			ChessMoveAction[] moves = getPieceMoves(state,p,player,moveColor,false);
			if(moves != null)
			{
				for(ChessMoveAction move: moves)
				{
					if(move != null && move.getTakenPiece() != null &&
							move.getTakenPiece().getType() == ChessPiece.KING)
					{
						return true;
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
				
				int[] newLoc = loc;
				
				if(piece.isWhite())
				{
					newLoc[0] += i;
				}
				else
				{
					newLoc[0] -= i;
				}
				
				if(!ChessGameState.outOfBounds(newLoc))
				{
					ChessPiece taken = state.getPieceMap()[newLoc[0]][newLoc[1]];
					
					//can't take a piece vertically
					if(taken == null)
					{
						moveList.add(new ChessMoveAction(currPlayer, piece, newLoc, taken));
					}
				}
			}
			int[][] attackLoc;
			if(piece.isWhite()==state.isPlayer1IsWhite())//player 1
			{
				attackLoc = new int[][]{
						{loc[0]+1,loc[1]-1},
						{loc[0]+1,loc[1]+1}
				};
			}
			else//player 2
			{
				attackLoc = new int[][]{
						{loc[0]-1,loc[1]-1},
						{loc[0]-1,loc[1]+1}
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
				int[] newLoc = {i,loc[1]};
				
				boolean done = addMove(state,piece,moveList,newLoc,currPlayer,currPlayer.isWhite());
				if(done == true)
				{
					break;
				}
			}
			
			//horizontal to the left
			for(int i=loc[0]-1;i>=0;i--)
			{
				int[] newLoc = {i,loc[1]};
				
				boolean done = addMove(state,piece,moveList,newLoc,currPlayer,currPlayer.isWhite());
				if(done == true)
				{
					break;
				}
			}
			
			//vertical going up
			for(int i=loc[0]+1;i<ChessGameState.BOARD_HEIGHT;i++)
			{
				int[] newLoc = {loc[0],i};
				
				boolean done = addMove(state,piece,moveList,newLoc,currPlayer,currPlayer.isWhite());
				if(done == true)
				{
					break;
				}
			}
			
			//vertical going down
			for(int i=loc[0]-1;i>=0;i--)
			{
				int[] newLoc = {loc[0],i};
				
				boolean done = addMove(state,piece,moveList,newLoc,currPlayer,currPlayer.isWhite());
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
					System.out.println("["+newLoc[i][0]+"]"+"["+newLoc[i][1]+"]");
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
				boolean done = addMove(state,piece,moveList,newLoc,currPlayer,currPlayer.isWhite());
				if(done == true)
				{
					break;
				}
			}
			
			//top left
			for(newLoc[0]=loc[0]-1,newLoc[1]=loc[1]+1;!ChessGameState.outOfBounds(newLoc);newLoc[0]--,newLoc[1]++)
			{
				boolean done = addMove(state,piece,moveList,newLoc,currPlayer,currPlayer.isWhite());
				if(done == true)
				{
					break;
				}
			}
			
			//bottom right
			for(newLoc[0]=loc[0]+1,newLoc[1]=loc[1]-1;!ChessGameState.outOfBounds(newLoc);newLoc[0]++,newLoc[1]--)
			{
				boolean done = addMove(state,piece,moveList,newLoc,currPlayer,currPlayer.isWhite());
				if(done == true)
				{
					break;
				}
			}
			
			//bottom left
			for(newLoc[0]=loc[0]-1,newLoc[1]=loc[1]-1;!ChessGameState.outOfBounds(newLoc);newLoc[0]--,newLoc[1]--)
			{
				boolean done = addMove(state,piece,moveList,newLoc,currPlayer,currPlayer.isWhite());
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
						addMove(state,piece,moveList,new int[]{i,j},currPlayer,currPlayer.isWhite());
					}
				}
			}
		}
		ChessMoveAction[] rtnVal = moveList.toArray(new ChessMoveAction[moveList.size()]);
		
		if(legal)
		{
			return removeIllegalMoves(state, rtnVal,currPlayer);
		}
		else
		{
			return rtnVal;
		}
		
	}
	
	/**
	 * Add a move to an ArrayList given a piece to move, its location,
	 * and the color of the player
	 * @param state
	 * @param piece
	 * @param moveList list of moves that have been made since the start of the game
	 * @param newLoc
	 * @param player
	 * @return true if successful
	 */
	private static boolean addMove(ChessGameState state,
			ChessPiece piece, ArrayList<ChessMoveAction> moveList,
			int[] newLoc, ChessPlayer player, boolean color)
	{
		if(newLoc == null || newLoc.length != 2 )
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
		}
		return false;
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
		Stack<Integer> removeList = new Stack<Integer>();
		for(int i=0;i<nextStates.length;i++)
		{
			if(canTakeKing(nextStates[i], currPlayer))
			{
				removeList.push(Integer.valueOf(i));
			}
		}
		
		// Remove the last elements first so the indices don't change
		ArrayList<ChessMoveAction> moveList = new ArrayList<ChessMoveAction>(Arrays.asList(moves));
		while(!removeList.isEmpty())
		{
			moveList.remove(removeList.pop().intValue());
		}
		
		// Make into normal array
		ChessMoveAction[] legalMoves = moveList.toArray(new ChessMoveAction[moveList.size()]);
		
		return legalMoves;
	}
}
