package edu.up.cs301.chess;

import java.util.Arrays;

import edu.up.cs301.chess.actions.*;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.MessageBox;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

/**
 * A GUI of a human chess player. The GUI displays the current state of the
 * chessboard. It allows the player to select a piece and move it. The
 * player can also quit, flip the board, and ask for a draw.
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version April 2015
 */
public class ChessHumanPlayer extends GameHumanPlayer implements ChessPlayer, OnClickListener, OnTouchListener {

	/* instance variables */
	
	private static final String upgradeChoices[] = new String[] {"\u2655 Queen", "\u2658 Knight", "\u2656 Rook", "\u2657 Bishop"};
	
	private static final int[] promotionTypes = {
		ChessPiece.QUEEN,
		ChessPiece.KNIGHT,
		ChessPiece.ROOK,
		ChessPiece.BISHOP
	};
	
	// The TextView the displays the current score 
	private TextView player1Score;
	private TextView player2Score;
	private TextView turnText;
	
	// The buttons at the side of the screen
	private Button quitButton;
	private Button flipButton;
	private Button drawButton;
	
	
	//The board that draws each piece
	private ChessBoard board;
	
	// the most recent game state, as given to us by the LocalGame
	private ChessGameState state;
	
	// the android activity that we are running
	private GameMainActivity activity;
	
	//true if this player is white, false if this player is black
	private boolean isWhite = isPlayer1();
	
	//true if the user is holding down, false if not
	private boolean down;
	
	//the last piece the player touched
	protected ChessPiece lastPieceSelected;
	
	//the valid locations for a move using the lastPieceSelected
	private boolean[][] validLocs;
	
	//The last move generated
	private ChessMoveAction move;
	
	/**
	 * constructor
	 * 
	 * @param name
	 * 		the player's name
	 */
	public ChessHumanPlayer(String name) {
		super(name);
	}

	/**
	 * Returns the GUI's top view object
	 * 
	 * @return
	 * 		the top object in the GUI's view hierarchy
	 */
	public View getTopView() {
		return activity.findViewById(R.id.top_gui_layout);
	}
	
	/**
	 * Sets the each player's score on the screen and updates the board
	 */
	protected void updateDisplay() {
		board.setPieceMap(state.getPieceMap());
		
		//update player points display
		player1Score.setText("" + state.getPlayer1Points());
		player2Score.setText("" + state.getPlayer2Points());
		
		//update turn indicator
		if(state.isWhoseTurn() == state.isPlayer1IsWhite()) {
			turnText.setText("Turn: White");
		} else {
			turnText.setText("Turn: Black");
		}
		
		// Goes through player 1's pieces to see what ones are dead/alive
		for (int i = 0; i < state.getPlayer1Pieces().length; i++) {
			if (state.getPlayer1Pieces()[i].isAlive() == false) {
				//Add the taken piece to either the white or black array in Board Class
				if (state.getPlayer1Pieces()[i].isWhite()){
					board.setWhiteTakenPiece(state.getPlayer1Pieces()[i], i);
				}
				else {
					board.setBlackTakenPiece(state.getPlayer1Pieces()[i], i);
				}
			}
		}

		// Goes through player 2's pieces to see what ones are dead/alive
		for (int j = 0; j < state.getPlayer2Pieces().length; j++) {
			if (state.getPlayer2Pieces()[j].isAlive() == false) {
				//Add the taken piece to either the white or black array in Board Class
				if (state.getPlayer2Pieces()[j].isWhite()){
					board.setWhiteTakenPiece(state.getPlayer2Pieces()[j], j);
				}
				else {
					board.setBlackTakenPiece(state.getPlayer2Pieces()[j], j);
				}
			}
		}
	}

	/**
	 * this method gets called when the user clicks on the GameBoard.
	 * 
	 * @param button
	 * 		the button that was clicked
	 */
	public void onClick(View button) {
		// if we are not yet connected to a game, ignore
		if (game == null) return;

		if (button.getId() == R.id.resignButton) {
			//make the activity think it received a back button
			KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK);
			activity.onKeyDown(KeyEvent.KEYCODE_BACK,event);
		}
		else if (button.getId() == R.id.drawButton) {
			DrawAction act = new DrawAction(this,isWhite(),false);
			
			game.sendAction(act);
		}
		else if (button.getId() == R.id.flipBoardButton) {
			board.flipBoard();
		}
		else {
			// something else was pressed: ignore
			return;
		}
	}// onClick
	
	/**
	 * callback method when we get a message (e.g., from the game)
	 * 
	 * @param info
	 * 		the message
	 */
	@Override
	public void receiveInfo(GameInfo info) {
		// ignore the message if it's not a ChessGameState message
		if (!(info instanceof ChessGameState))
		{
			return;
		}
		ChessGameState newState = (ChessGameState)info;
		
		if(newState.equals(state))
		{
			Log.d("human player", "equal game state");
			return;
		}
		
		// update our state; then update the display
		state = newState;
		
		//Log.d("human player",state.toString());
		updateDisplay();
	}
	
	/**
	 * callback method--our game has been chosen/rechosen to be the GUI,
	 * called from the GUI thread
	 * 
	 * @param activity
	 * 		the activity under which we are running
	 */
	@SuppressLint("ClickableViewAccessibility")
	public void setAsGui(GameMainActivity activity) {
		
		// remember the activity
		this.activity = activity;
		
	    // Load the layout resource for our GUI
		activity.setContentView(R.layout.chess_human_player);
		
		// Set listeners for each button
		drawButton = (Button)activity.findViewById(R.id.drawButton);
		drawButton.setOnClickListener(this);
		
		flipButton = (Button)activity.findViewById(R.id.flipBoardButton);
		flipButton.setOnClickListener(this);
		quitButton = (Button)activity.findViewById(R.id.resignButton);
		quitButton.setOnClickListener(this);
		
		// Find the score TextViews
		this.player1Score =
				(TextView) activity.findViewById(R.id.player1ScoreTextView);
		this.player2Score =
				(TextView) activity.findViewById(R.id.player2ScoreTextView);
		
		turnText = (TextView) activity.findViewById(R.id.turnTextView);
		
		// Find the board
		board = (ChessBoard)activity.findViewById(R.id.gameBoardSurfaceView);
		
		board.setOnTouchListener(this);
		
		//Ask which color the player wants to be.
		String colorQuestion =
				activity.getResources().getString(R.string.dialog_color_question);
		String whiteLabel =
				activity.getResources().getString(R.string.dialog_white_label);
		String blackLabel =
				activity.getResources().getString(R.string.dialog_black_label);
		
		android.content.DialogInterface.OnClickListener whiteListener =
				new android.content.DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) {
				isWhite = true;
				startGame();
			}
		};
		android.content.DialogInterface.OnClickListener blackListener =
				new android.content.DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) {
				isWhite = false;
				startGame();
			}
		};
		
		//make a dialog to ask which color the human wants to be
		MessageBox.popUpChoice(colorQuestion, whiteLabel, blackLabel, whiteListener, blackListener, activity);
	}
	/**
	 * Returns true if it is white
	 * @return true 
	 */
	public boolean isWhite()
	{
		return (isPlayer1() == state.isPlayer1IsWhite());
	}

	/**
	 * Handles touches for the ChessBoard
	 */
	public boolean onTouch(View v, MotionEvent event) {
		
		v.onTouchEvent(event);
		//only handle touches when it is your turn
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			down = true;
		}
		else if(event.getAction() == MotionEvent.ACTION_UP && down)
		{
			v.performClick();
			
			if(v == board)
			{
				//TODO sound feedback/vibration
				
				//check if it is your turn
				if(state == null || state.isWhoseTurn() != isPlayer1())
				{
					return false;
				}
				
				float[] tileSize = board.getTileSize();
				int tileX = (int) (event.getX()/tileSize[0]);
				int tileY;
				
				//translate the points if the board is flipped
				if(board.isFlipped())
				{
					tileY = ChessGameState.BOARD_WIDTH-1-((int) (event.getY()/tileSize[1]));
				}
				else
				{
					tileY = (int) (event.getY()/tileSize[1]);
				}
				//Log.d("human player","selected x:"+tileX+" y:"+tileY);
				int[] selectedLoc = new int[]{tileY,tileX};
				
				// Make sure it is within bounds
				if(ChessGameState.outOfBounds(selectedLoc))
				{
					return false;
				}
				if(state == null || state.getPieceMap() == null)
				{
					return false;
				}
				
				//Now that the error checks are done, it is safe to handle the touch
				down = false;
				
				
				ChessPiece pieceSelected = state.getPieceMap()[tileY][tileX];
				
				//selected the same piece twice in a row, so deselect it
				if(lastPieceSelected != null && lastPieceSelected.equals(pieceSelected))
				{
					lastPieceSelected = null;
					pieceSelected = null;
					board.setSelectedTiles(null);
					board.setSelectedLoc(-1, -1);
					
					return true;
				}
				
				move = null;
				
				//Is a valid location to move the piece to
				if(validLocs != null && validLocs[tileY][tileX] == true)
				{
					//Create and apply the move
					ChessPiece takenPiece = state.getPieceMap()[tileY][tileX];
					if(lastPieceSelected != null)
					{
						if(lastPieceSelected.getType() == ChessPiece.PAWN)
						{
							if(selectedLoc[0] == 0 || selectedLoc[0] == ChessGameState.BOARD_HEIGHT-1)
							{
								//Pawns only reach the edge when they are promoted
								
								//Only use the first move assigned
								if(move == null)
								{
									move = new PawnMove(this, lastPieceSelected, selectedLoc,
										takenPiece,PawnMove.PROMOTION);
									
									board.setSelectedTiles(null);
									board.setSelectedLoc(-1, -1);
									
									//let the select upgrade method make a move
									selectUpgrade();
									return true;
								}
								
							}
							else if(!lastPieceSelected.getHasMoved())
							{
								//First move
								if(move == null && takenPiece == null)
								{
									move = new PawnMove(this, lastPieceSelected, selectedLoc,
										takenPiece,PawnMove.FIRST_MOVE);
								}
							}
							else if(state.getMoveList().getLast() instanceof PawnMove)
							{
								PawnMove lastMove = (PawnMove)state.getMoveList().getLast();
								int[] newLoc = lastMove.getNewPos();
								
								//The last move was a double pawn jump
								if(lastMove.getType() == PawnMove.FIRST_MOVE)
								{
									//Is trying to do an en passant
									if(Math.abs(selectedLoc[1]-newLoc[1]) == 1 && takenPiece != null)
									{
										if(isPlayer1() && selectedLoc[0] == newLoc[0]-1 && move == null)
										{
											move = new PawnMove(this, lastPieceSelected, selectedLoc,
													takenPiece,PawnMove.EN_PASSANT);
										}
										if(!isPlayer1() && selectedLoc[0] == newLoc[0]+1 && move == null)
										{
											move = new PawnMove(this, lastPieceSelected, selectedLoc,
													takenPiece,PawnMove.EN_PASSANT);
										}
									}
								}
							}
							
							//Normal pawn move
							if(move == null)
							{
								move = new PawnMove(this, lastPieceSelected, selectedLoc,
										takenPiece,PawnMove.NONE);
							}
						}
						else if(lastPieceSelected.getType() == ChessPiece.ROOK)
						{
							ChessPiece king = state.getKing(isWhite() == state.isPlayer1IsWhite());
							
							//Castling
							if(king != null)
							{
								int[] kingLoc = king.getLocation();
								
								//selected the king
								if(Arrays.equals(selectedLoc,kingLoc))
								{
									int lastX = lastPieceSelected.getLocation()[1];
									int canCastleX = -1;
									int canCastleY;
									int type = RookMove.NONE;
									
									//last selected the left rook
									if(lastX == 0)
									{
										type = RookMove.CASTLE_LEFT;
										canCastleX = 0;
									}
									
									//last selected the right rook
									if(lastX == ChessGameState.BOARD_WIDTH-1)
									{
										type = RookMove.CASTLE_RIGHT;
										canCastleX = 1;
									}
									if(isPlayer1())
									{
										canCastleY = 1;
									}
									else
									{
										canCastleY = 0;
									}
									
									//Check if you can castle
									if(canCastleX != -1 && state.getCanCastle()[canCastleY][canCastleX] && move == null)
									{
										move = new RookMove(this, lastPieceSelected, selectedLoc,
												null,type);
									}
									else if(move == null)
									{
										//Normal rook move
										move = new RookMove(this, lastPieceSelected, selectedLoc,
												takenPiece,RookMove.NONE);
									}
								}
							}
							if(move == null)
							{
								//Normal rook move
								move = new RookMove(this, lastPieceSelected, selectedLoc,
											takenPiece,RookMove.NONE);
							}
						}
						if(move == null)
						{
							//Normal move
							move = new ChessMoveAction(this,
									lastPieceSelected, selectedLoc,takenPiece);
						}
						
						if(state.applyMove(move))
						{
							game.sendAction(move);
							updateDisplay();
						}
						Log.d("human player", "sending this move: "+move);
						
						//clear the highlighted tiles after a move
						board.setSelectedTiles(null);
						board.setSelectedLoc(-1, -1);
						lastPieceSelected = null;
						pieceSelected = null;
					}
				}
				else
				{
					//clear piece selections if the selected tile was invalid
					board.setSelectedTiles(null);
					board.setSelectedLoc(-1, -1);
					lastPieceSelected = null;
				}
				
				//selected a piece to see what tiles it can move to
				if(pieceSelected != null && !pieceSelected.equals(lastPieceSelected) && move == null)
				{
					if(pieceSelected.isWhite() == isWhite())
					{
						// Highlight valid moves for the player:
						validLocs = state.getSavedPossibleMoves(pieceSelected);
						board.setSelectedTiles(validLocs);
						board.setSelectedLoc(tileY,tileX);
						
						//Keep a reference to the piece
						lastPieceSelected = pieceSelected;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Returns true if this player is
	 * player 1 in the game state.
	 */
	public boolean isPlayer1() {
		return playerNum == 0;//return isPlayer1;
	}

	/**
	 * Returns this player's unique ID number.
	 */
	public int getPlayerID() {
		return playerNum;
	}
	
	public void startGame()
	{
		// if we have a game state, "simulate" that we have just received
		// the state from the game so that the GUI values are updated
		if (state != null)
		{
			ChooseColorAction act = new ChooseColorAction(this,isWhite);
			if(game instanceof ChessLocalGame)
			{
				ChessLocalGame chessGame = (ChessLocalGame)game;
				chessGame.makeMove(act);
			}
			updateDisplay();
		}
		// Flip the board if the human player chooses to be black
		if (!isPlayer1()) {
			board.flipBoard();
		}

		// Set the name text views:
		TextView player1View = (TextView) activity
				.findViewById(R.id.player1TextView);
		TextView player2View = (TextView) activity
				.findViewById(R.id.player2TextView);

		//TODO shorten names if necessary
		if(player1View != null)
		{
			player1View.setText(this.name);
		}
		if(player2View != null)
		{
			player2View.setText(this.allPlayerNames[1]);
		}
	}

	/**
	 * Prompts the user to choose a piece type for promotion
	 */
	public void selectUpgrade() {
		//The choices when making a promotion
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Pick a new piece.");
		
		android.content.DialogInterface.OnClickListener pieceListener =
				new android.content.DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) {
				doSelectAction(which);
			}
		};
		
		builder.setItems(upgradeChoices, pieceListener);
		builder.setCancelable(false);
		builder.show();
	}
	
	/**
	 * Sends a promotion action
	 * @param type
	 */
	public void doSelectAction(int type)
	{
		if(move instanceof PawnMove)
		{
			PawnMove pawnAct = (PawnMove)move;
			if(pawnAct.getType() == PawnMove.PROMOTION && lastPieceSelected != null)
			{
				//Trying to do a promotion and the action was not sent before
				pawnAct.setNewType(promotionTypes[type]);
				game.sendAction(pawnAct);
				
				lastPieceSelected = null;
			}
		}
	}

	/**
	 * Asks the player for a draw
	 */
	public void askDraw(String msg) {
		
		//The accept and decline button text
		String acceptLabel =
				activity.getResources().getString(R.string.accept);
		String declineLabel =
				activity.getResources().getString(R.string.decline);
		
		android.content.DialogInterface.OnClickListener acceptListener =
				new android.content.DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) {
				doDraw();
			}
		};
		
		//make a dialog to ask which color the human wants to be
		MessageBox.popUpChoice(msg, acceptLabel, declineLabel, acceptListener, null, activity);
	}
	
	/**
	 * Creates and sends a draw action that asks the other player
	 * if he/she wants to draw
	 */
	public void doDraw()
	{
		DrawAction act = new DrawAction(this,isWhite,true);
		game.sendAction(act);
	}
	
}// class CounterHumanPlayer

