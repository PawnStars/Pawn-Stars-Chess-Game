package edu.up.cs301.chess.actions;

import java.io.Serializable;
import java.util.Vector;

import android.util.Log;
import edu.up.cs301.chess.ChessGameState;
import edu.up.cs301.chess.ChessPiece;
import edu.up.cs301.chess.ChessPlayer;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * A ChessMoveAction is an action that is a "move" in the game.
 * A piece or pieces are moved on the board and may take opponent pieces.
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version April 2015
 */
public class ChessMoveAction extends GameAction implements Serializable{
	
	// to satisfy the serializable interface
	private static final long serialVersionUID = 2806249547013L;
	
	//The piece moved by the player
	protected ChessPiece whichPiece;
	
	//The piece to be taken
	protected ChessPiece takenPiece;
	
	//The new location of the selected piece
	protected byte[] newPos;
	
	//The old location of the selected piece
	protected byte[] oldPos;
	
	protected boolean makesCheck;
	
	protected boolean makesCheckmate;
	
	//TODO could make it possible to undo a turn with this info and the last position
	
	/**
	 * Constructor for the ChessMoveAction class.
	 * 
	 * @param player
	 *            the player making the move
	 * @param whichPlayer
	 *            value to initialize which player is white
	 */
	public ChessMoveAction(GamePlayer player, ChessPiece whichPiece, byte[] newPos, ChessPiece takenPiece)
	{
		super(player);
		
		if(takenPiece != null)
		{
			this.takenPiece = new ChessPiece(takenPiece);
		}
		else
		{
			this.takenPiece = null;
		}
		
		this.newPos = new byte[2];
		this.oldPos = new byte[2];
		if(whichPiece != null)
		{
			this.whichPiece = new ChessPiece(whichPiece);
			if(whichPiece.getLocation().length == 2)
			{
				System.arraycopy(whichPiece.getLocation(), 0, this.oldPos, 0, 2);
			}
		}
		else
		{
			this.whichPiece = null;
		}
		if(newPos != null && newPos.length == 2)
		{
			System.arraycopy(newPos, 0, this.newPos, 0, 2);
		}
		
		
		makesCheck = false;
		makesCheckmate = false;
	}

	/**
	 * Copy constructor for adding a player to an invalid ChessMoveAction
	 * @param player
	 * @param action
	 */
	public ChessMoveAction(GamePlayer player, ChessMoveAction action) {
		super(player);
		
		if(takenPiece != null)
		{
			this.takenPiece = new ChessPiece(takenPiece);
		}
		else
		{
			this.takenPiece = null;
		}
		//TODO: this caused a null pointer exception... 
		//may need to fix after we implement checkmate properly
		newPos = new byte[2];
		oldPos = new byte[2];
		if(action != null)
		{
			if(action.getWhichPiece() != null)
			{
				this.whichPiece = new ChessPiece(action.getWhichPiece());
			}
			
			System.arraycopy(action.getNewPos(), 0, newPos, 0, 2);
			System.arraycopy(action.getOldPos(), 0, oldPos, 0, 2);
			makesCheck = action.isMakesCheck();
			makesCheckmate = action.isMakesCheckmate();
		}
	}

	/**
	 * Gets the piece to be moved
	 * @return ChessPiece the piece that will move
	 */
	public ChessPiece getWhichPiece()
	{
		return whichPiece;
	}

	/**
	 * Gets the piece to be taken
	 * @return ChessPiece the piece that will be captured
	 */
	public ChessPiece getTakenPiece() {
		return takenPiece;
	}

	/**
	 * Gets the new location of the piece
	 * @return an int array of size 2 with the new position of the ChessPiece
	 */
	public byte[] getNewPos() {
		return newPos;
	}
	
	public byte[] getOldPos() {
		return oldPos;
	}

	public boolean isMakesCheck() {
		return makesCheck;
	}

	public void setMakesCheck(boolean makesCheck) {
		this.makesCheck = makesCheck;
	}

	public boolean isMakesCheckmate() {
		return makesCheckmate;
	}

	public void setMakesCheckmate(boolean makesCheckmate) {
		this.makesCheckmate = makesCheckmate;
	}

	/**
	 * Convert the move into standard chess notation for debugging.
	 */
	@Override
	public String toString()
	{
		String msg = "";
		
		if(whichPiece != null)
		{
			byte pieceType = whichPiece.getType();
			if(pieceType == ChessPiece.QUEEN)
			{
				msg += "Q";
			}
			if(pieceType == ChessPiece.KING)
			{
				msg += "K";
			}
			if(pieceType == ChessPiece.ROOK)
			{
				msg += "R";
			}
			if(pieceType == ChessPiece.BISHOP)
			{
				msg += "B";
			}
			if(pieceType == ChessPiece.KNIGHT)
			{
				msg += "N";
			}
			if(takenPiece != null)
			{
				msg += "x";
			}
			msg += (char)(97+oldPos[1]);
			msg += ChessGameState.BOARD_HEIGHT-oldPos[0];
			msg += (char)(97+newPos[1]);
			msg += ChessGameState.BOARD_HEIGHT-newPos[0];
			
			if(makesCheck)
			{
				msg += "+";
			}
			else if(makesCheckmate)
			{
				msg += "#";
			}
		}
		return msg;
	}
	
	/**
	 * Makes a copy of the ChessMoveAction
	 */
	public ChessMoveAction clone()
	{
		return new ChessMoveAction(null,this);
	}
	
	/**
	 * Converts algebraic chess notation into a ChessMoveAction
	 * @param state the current ChessGameState
	 * @param player the player who will send the move
	 * @param text the chess move in algebraic chess notation
	 * @return a corresponding ChessMoveAction
	 */
	public static ChessMoveAction moveTextToAction(ChessGameState state,ChessPlayer player, String text)
	{
		if(state == null || text == null || text.equals("(none)") || text.equals(""))
		{
			return null;
		}
		
		//get rid of check/checkmate info
		char lastChar = text.charAt(text.length()-1);
		if(lastChar == '+')
		{
			text = text.substring(0, text.length()-2);
		}
		else if(lastChar == '#')
		{
			text = text.substring(0, text.length()-2);
		}
		
		//check for special moves and get rid of that info from the string
		boolean enPassant = false;
		boolean rightCastle = false;
		boolean leftCastle = false;
		byte promotion = -1;
		if(text.contains("e.p."))//en passant
		{
			enPassant = true;
			text = text.replace("e.p.", "");
		}
		else if(text.contains("/"))//promotion
		{
			char type = text.charAt(text.indexOf('/')+1);
			if(type == 'Q') {
				promotion = ChessPiece.QUEEN;
			} else if(type == 'R') {
				promotion = ChessPiece.ROOK;
			} else if(type == 'N') {
				promotion = ChessPiece.KNIGHT;
			} else if(type == 'B') {
				promotion = ChessPiece.BISHOP;
			} else {
				Log.wtf("move parser","invalid promotion char "+type);
				return null;
			}
		}
		else if(text.contains("0-0"))//castling
		{
			if(text.contains("0-0-0"))
			{
				leftCastle = true;
			}
			else
			{
				rightCastle = true;
			}
		}
		
		byte oldX = -1;
		byte oldY = -1;
		byte newX = -1;
		byte newY = -1;
		if(text.length() >= 2)
		{
			newX = (byte) (text.charAt(text.length()-2)-97);
			newY = (byte) (ChessGameState.BOARD_HEIGHT-text.charAt(text.length()-1)+48);
			text = text.substring(0, text.length()-2);
			Log.d("move parser","move text without end pos:"+text);
		}
		else
		{
			Log.wtf("move parser","not enough info for end pos: "+text);
		}
		
		ChessPiece newTakenPiece = null;
		if(text.contains("x"))
		{
			//get rid of capture notation because it isn't necessary
			text = text.replace("x","");
			newTakenPiece = state.getPieceMap()[newY][newX];
		}
		
		//get all the pieces that can move to the right spot
		byte[] newLoc = new byte[]{newY,newX};
		Vector<ChessPiece> whichPieces = new Vector<ChessPiece>();

		if(!ChessGameState.outOfBounds(newX, newY))
		{
			//add each piece manually
			ChessPiece[] movingPieces = null;
			boolean[][][] locs = null;
			if(state.isWhoseTurn()) {
				movingPieces = state.getPlayer1Pieces();
				locs = state.getPlayer1Moves();
			} else {
				movingPieces = state.getPlayer2Pieces();
				locs = state.getPlayer2Moves();
			}
			for(int i=0;i<ChessGameState.NUM_PIECES;i++)
			{
				if(locs[i][newY][newX])
				{
					whichPieces.add(movingPieces[i]);
				}
			}
		}
		//now we have a list of pieces that can move to the right spot
		
		//see which type of piece to move
		int pieceType = -1;
		if(!text.isEmpty() && Character.isUpperCase(text.charAt(0)))
		{
			char pieceChar = text.charAt(0);
			if(pieceChar == 'Q') {
				pieceType = ChessPiece.QUEEN;
				Log.d("move parser","moving a queen");
			} else if(pieceChar == 'R') {
				pieceType = ChessPiece.ROOK;
				Log.d("move parser","moving a rook");
			} else if(pieceChar == 'N') {
				pieceType = ChessPiece.KNIGHT;
				Log.d("move parser","moving a knight");
			} else if(pieceChar == 'B') {
				pieceType = ChessPiece.BISHOP;
				Log.d("move parser","moving a bishop");
			} else if(pieceChar == 'K') {
				pieceType = ChessPiece.KING;
				Log.d("move parser","moving a king");
			} else {
				Log.wtf("move parser","invalid chess piece type char "+text.charAt(0));
				return null;
			}
			//remove the first character describing which piece to move
			text = text.substring(1);
		}
		else
		{
			//no piece character implies pawn
			pieceType = ChessPiece.PAWN;
			Log.d("move parser","moving a pawn");
		}
		
		//now the text should only contain the rank or file of which piece to move
		
		//find out any additional info about the piece's starting position if needed
		Log.d("move parser","move text without end pos and piece type:"+text);
		
		//get the row or column the right piece is at
		for(char c: text.toCharArray())
		{
			int x = c-97;
			int y = ChessGameState.BOARD_HEIGHT-c+48;
			
			if(x >= 0 && x < ChessGameState.BOARD_HEIGHT && oldX == -1) {
				//if c wasn't a file, it will be out of bounds
				oldX = (byte) x;
			} else if(y >= 0 && y < ChessGameState.BOARD_HEIGHT && oldY == -1) {
				//if c wasn't a rank, it will be out of bounds
				oldY = (byte) y;
			}
		}
		
		String coords = "x:"+oldX+" y:"+oldY+" newX:"+newX+" newY:"+newY;
		
		//figure out which piece in the vector should move
		ChessPiece newWhichPiece = null;
		if(!ChessGameState.outOfBounds(oldX, oldY) && state.getPieceMap()[oldY][oldX] != null)
		{
			newWhichPiece = state.getPieceMap()[oldY][oldX];
		}
		else if(whichPieces.isEmpty())
		{
			Log.wtf("move parser","did not get any pieces that can attack at "+coords);
			return null;
		}
		else if(whichPieces.size() == 1)
		{
			newWhichPiece = whichPieces.firstElement();
		}
		else if(whichPieces.size() > 1)//there are multiple pieces this could be
		{
			for(ChessPiece p:whichPieces)
			{
				byte[] loc = p.getLocation();
				
				//the piece must be the right type if it was specified
				if(pieceType == p.getType() || pieceType == ChessPiece.PAWN)
				{
					newWhichPiece = p;
					
					//see how many coords have to match
					if(oldX != -1 && oldY != -1) { //need to match rank and file
						if(oldY == loc[0] && oldX == loc[1]) {
							break;
						}
					} else if(oldX != -1) { //need to match file
						if(oldX == loc[1]) {
							break;
						}
					} else if(oldY != -1) { //need to match rank
						if(oldY == loc[0]) {
							break;
						}
					} else { //doesn't need to match anything, but have to choose from more than 1 piece
						Log.wtf("move parser","could not narrow down any pieces at "+coords);
					}
				}
			}
		}
		//TODO make sure special moves work
		
		if(!ChessGameState.outOfBounds(newLoc))
		{
			if(promotion != -1)//promotion
			{
				PawnMove move = new PawnMove(player,newWhichPiece,newLoc,newTakenPiece,PawnMove.PROMOTION);
				move.setNewType(promotion);
				return move;
			}
			if(enPassant)//en passant
			{
				int dy = 0;
				if(state.isWhoseTurn()) {
					dy = 1;
				} else {
					dy = -1;
				}
				byte[] loc = state.getCanEnPassant();
				newTakenPiece = state.getPieceMap()[loc[0]+dy][loc[1]];//TODO see if this is necessary*/
				if(newTakenPiece != null && newTakenPiece.getType() == ChessPiece.PAWN)
				{
					return new PawnMove(player,newWhichPiece,newLoc,newTakenPiece,PawnMove.EN_PASSANT);
				}
				else
				{
					Log.d("computer player"," cannot en passant at "+coords);
					return null;
				}
			}
			if((leftCastle || rightCastle))//castling
			{
				byte moveType;
				int x;
				int y;
				
				if(state.isWhoseTurn()) {
					y = ChessGameState.BOARD_HEIGHT;
				} else {
					y = 0;
				}
				
				if(leftCastle) { //left
					moveType = RookMove.CASTLE_LEFT;
					x = 0;
				} else { //right
					moveType = RookMove.CASTLE_RIGHT;
					x = ChessGameState.BOARD_WIDTH-1;
				}
				newWhichPiece = state.getPieceMap()[y][x];
				
				return new RookMove(player, newWhichPiece, newLoc, null, moveType);
			}
			//normal move
			return new ChessMoveAction(player,newWhichPiece,newLoc,newTakenPiece);
		}
		else
		{
			Log.d("computer player","invalid new coords:"+coords);
			return null;
		}
	}
}//class CounterMoveAction
