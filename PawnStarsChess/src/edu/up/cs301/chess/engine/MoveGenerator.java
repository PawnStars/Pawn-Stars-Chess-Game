package edu.up.cs301.chess.engine;

import java.util.ArrayList;

import edu.up.cs301.chess.ChessGameState;
import edu.up.cs301.chess.ChessPiece;
import edu.up.cs301.chess.ChessPlayer;
import edu.up.cs301.chess.actions.ChessMoveAction;
import edu.up.cs301.chess.actions.PawnMove;
import edu.up.cs301.chess.actions.RookMove;

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
				
				ChessMoveAction[] newActions = getPieceMoves(state, new ChessPiece(pieces[i]), player, color, false);
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
			moveablePieces = state.getPlayer1Pieces();
			moveColor = state.isPlayer1IsWhite();
		}
		else if(!state.isWhoseTurn() && !isPlayer1)//player 2's turn
		{
			moveablePieces = state.getPlayer2Pieces();
			moveColor = !state.isPlayer1IsWhite();
		}
		else
		{
			return false;
		}
		
		if(moveablePieces != null)
		{
			//See what moves each piece can make
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
							
							//If there is a move that results in the king being taken
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
		byte[] loc = piece.getLocation();
		if(type == ChessPiece.INVALID || loc == null)
		{
			return null;
		}
		
		//Get the locations the piece can move to
		boolean[][] possibleLocs = state.getSavedPossibleMoves(piece);
		
		if(possibleLocs == null)
		{
			return null;
		}
		int y1 = piece.getLocation()[0];
		
		//Add a move for each location it can more to
		ArrayList<ChessMoveAction> moveList = new ArrayList<ChessMoveAction>();
		for(byte i=0;i<ChessGameState.BOARD_HEIGHT;i++)
		{
			for(byte j=0;j<ChessGameState.BOARD_WIDTH;j++)
			{
				if(possibleLocs[i][j])
				{
					byte[] newLoc = new byte[]{i,j};
					
					ChessPiece taken = state.getPieceMap()[i][j];
					
					//special moves
					if(piece.getType() == ChessPiece.PAWN)
					{
						if(Math.abs(y1-i) == 2)
						{
							//vertical distance is 2
							moveList.add(new PawnMove(player, piece, newLoc, taken,PawnMove.FIRST_MOVE));
						}
						else if(taken == null && Math.abs(i-state.getCanEnPassant()[1]) == 1 
								&& j == state.getCanEnPassant()[0])
						{
							//the piece is moving horizontally, but not on another piece
							//and it is on the same rank as a double jumped pawn
							
							newLoc = state.getCanEnPassant();
							//en passants happen around the middle of the board, so they do not happen at (0,0)
							taken = state.getPieceMap()[newLoc[0]][newLoc[1]];
							moveList.add(new PawnMove(player, piece, newLoc, taken,PawnMove.EN_PASSANT));
						}
						else if(i == ChessGameState.BOARD_HEIGHT-1 || i == 0)
						{
							PawnMove pawnAct = new PawnMove(player, piece, newLoc, taken,PawnMove.PROMOTION);
							
							//automatically choose queen
							pawnAct.setNewType(ChessPiece.QUEEN);
							moveList.add(pawnAct);
						}
						else
						{
							moveList.add(new PawnMove(player, piece, newLoc, taken,PawnMove.NONE));
						}
					}
					else if(piece.getType() == ChessPiece.ROOK)
					{
						if(taken != null && taken.getType() == ChessPiece.KING && taken.isWhite() == piece.isWhite())
						{
							//castling
							byte moveType = 0;
							if(piece.getLocation()[1] == 0)
							{
								moveType = RookMove.CASTLE_LEFT;
							}
							else if(piece.getLocation()[1] == ChessGameState.BOARD_WIDTH-1)
							{
								moveType = RookMove.CASTLE_RIGHT;
							}
							else
							{
								continue;//unhandled behavior
							}
							moveList.add(new RookMove(player, piece, newLoc, null, moveType));
						}
						else
						{
							moveList.add(new RookMove(player, piece, newLoc, taken,RookMove.NONE));
						}
					}
					else
					{
						moveList.add(new ChessMoveAction(player, piece, newLoc, taken));
					}
				}
			}
		}
		
		//Convert to an array
		ChessMoveAction[] rtnVal = moveList.toArray(new ChessMoveAction[moveList.size()]);
		/*if(legal)
		{
			rtnVal =  removeIllegalMoves(state, rtnVal,color);
		}*/
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
