package edu.up.cs301.chess.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.up.cs301.chess.ChessGameState;
import edu.up.cs301.chess.ChessPiece;
import edu.up.cs301.chess.ChessPlayer;
import edu.up.cs301.chess.actions.ChessMoveAction;

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
	
	public static final ChessMoveAction[] getPossibleMoves(ChessGameState state, ChessPlayer player)
	{
		//Set<ChessMoveAction> moveList = new HashSet<ChessMoveAction>();
		//player 1
		if(state.isPlayer1IsWhite() == true && player.isWhite() == true)
		{
			if(state.isWhoseTurn())
			{
				ChessPiece[] pieces = state.getPlayer1Pieces();
				for(int i=0;i<ChessGameState.NUM_PIECES;i++)
				{
					if(pieces[i].isAlive())
					{
						//TODO add the arrays together
					}
					
				}
				return null;
			}
		}
		else //player 2
		{
			if(!state.isWhoseTurn())
			{
				
				return null;
			}
		}
		return null;
	}
	
	public static final ChessMoveAction[] getEvasions(ChessGameState state, ChessPlayer player)
	{
		return null;
	}
	
	public static final ChessMoveAction[] getCapturesAndChecks(ChessGameState state, ChessPlayer player)
	{
		return null;
	}
	
	public static final ChessMoveAction[] getCaptures(ChessGameState state, ChessPlayer player)
	{
		return null;
	}
	
	public static final boolean isInCheck(ChessGameState state, ChessPlayer player)
	{
		return false;
	}
	
	public static final boolean givesCheck(ChessGameState state, ChessPlayer player, ChessMoveAction move)
	{
		return false;
	}
	
	public static final boolean canTakeKing(ChessGameState state, ChessPlayer player)
	{
		return false;
	}
	
	public static final ChessMoveAction[] getPieceMoves(ChessGameState state, ChessPiece piece, ChessPlayer player)
	{
		
		int type = piece.getType();
		
		ArrayList<ChessMoveAction> moveList = new ArrayList<ChessMoveAction>();
		
		int[] loc = piece.getLocation();
		
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
				// Don't need to check for out of bounds because of promotion
				ChessPiece taken = state.getPieceMap()[newLoc[0]][newLoc[1]];
				
				//can't take a piece like this
				if(taken == null)
				{
					moveList.add(new ChessMoveAction(player, piece, newLoc, taken));
				}
			}
			int[][] attackLoc;
			if(piece.isWhite())
			{
				attackLoc = new int[][]{
						{loc[0]-1,loc[1]+1},
						{loc[0]+1,loc[1]+1}
				};
			}
			else
			{
				attackLoc = new int[][]{
						{loc[0]-1,loc[1]-1},
						{loc[0]+1,loc[1]-1}
				};
			}
			
			//add the left and right capture moves
			for(int i=0;i<2;i++)
			{
				if(!ChessGameState.outOfBounds(attackLoc[i]))
				{
					//TODO not sure if this part is right
					ChessPiece taken = state.getPieceMap()[attackLoc[i][0]][attackLoc[i][1]];
					
					//it can take a piece of a different color
					if(taken != null && taken.isWhite() != player.isWhite())
					{
						moveList.add(new ChessMoveAction(player, piece, attackLoc[i], taken));
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
				
				boolean done = addMove(state,piece,moveList,newLoc,player);
				if(done == true)
				{
					break;
				}
			}
			
			//horizontal to the left
			for(int i=loc[0]-1;i>=0;i--)
			{
				int[] newLoc = {i,loc[1]};
				
				boolean done = addMove(state,piece,moveList,newLoc,player);
				if(done == true)
				{
					break;
				}
			}
			
			//vertical going up
			for(int i=loc[0]+1;i<ChessGameState.BOARD_HEIGHT;i++)
			{
				int[] newLoc = {loc[0],i};
				
				boolean done = addMove(state,piece,moveList,newLoc,player);
				if(done == true)
				{
					break;
				}
			}
			
			//vertical going down
			for(int i=loc[0]-1;i>=0;i--)
			{
				int[] newLoc = {loc[0],i};
				
				boolean done = addMove(state,piece,moveList,newLoc,player);
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
						if(taken.isWhite() != player.isWhite())
						{
							//add a move only if the tile is empty or 
							moveList.add(new ChessMoveAction(player, piece, newLoc[i], taken));
						}
					}
					else //unoccupied
					{
						moveList.add(new ChessMoveAction(player, piece, newLoc[i], taken));
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
				boolean done = addMove(state,piece,moveList,newLoc,player);
				if(done == true)
				{
					break;
				}
			}
			
			//top left
			for(newLoc[0]=loc[0]-1,newLoc[1]=loc[1]+1;!ChessGameState.outOfBounds(newLoc);newLoc[0]--,newLoc[1]++)
			{
				boolean done = addMove(state,piece,moveList,newLoc,player);
				if(done == true)
				{
					break;
				}
			}
			
			//bottom right
			for(newLoc[0]=loc[0]+1,newLoc[1]=loc[1]-1;!ChessGameState.outOfBounds(newLoc);newLoc[0]++,newLoc[1]--)
			{
				boolean done = addMove(state,piece,moveList,newLoc,player);
				if(done == true)
				{
					break;
				}
			}
			
			//bottom left
			for(newLoc[0]=loc[0]-1,newLoc[1]=loc[1]-1;!ChessGameState.outOfBounds(newLoc);newLoc[0]--,newLoc[1]--)
			{
				boolean done = addMove(state,piece,moveList,newLoc,player);
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
						addMove(state,piece,moveList,new int[]{i,j},player);
					}
				}
			}
		}
		
		return removeIllegalMoves(state, moveList.toArray(new ChessMoveAction[moveList.size()]));
	}
	
	/**
	 * 
	 * @param state
	 * @param piece
	 * @param moveList
	 * @param newLoc
	 * @param player
	 * @return
	 */
	private static boolean addMove(ChessGameState state,
			ChessPiece piece, ArrayList<ChessMoveAction> moveList,
			int[] newLoc, ChessPlayer player) {
		ChessPiece taken = state.getPieceMap()[newLoc[0]][newLoc[1]];
		
		//space is occupied
		if(taken != null)
		{
			if(taken.isWhite() != player.isWhite())
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
	public static final ChessMoveAction[] removeIllegalMoves(ChessGameState state, ChessMoveAction[] moves)
	{
		return moves;
	}
	
	
}
