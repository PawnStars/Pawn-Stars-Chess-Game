package edu.up.cs301.chess;

import edu.up.cs301.chess.actions.*;
import edu.up.cs301.chess.engine.MoveGenerator;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.MessageBox;
import android.app.Activity;
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
 * A GUI of a chess player. The GUI displays the current state of the
 * chessboard. It allows the player to select a piece and move it. The
 * player can also quit, flip the board, ask for a draw, and confirm moves.
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version April 2015
 */
public class ChessHumanPlayer extends GameHumanPlayer implements ChessPlayer, OnClickListener, OnTouchListener {

	/* instance variables */
	
	public static final int[] types = {
		ChessPiece.QUEEN,
		ChessPiece.KNIGHT,
		ChessPiece.ROOK,
		ChessPiece.BISHOP
	};
	
	// The TextView the displays the current counter value
	private TextView player1Score;
	private TextView player2Score;
	
	// The buttons at the side of the screen
	private Button quitButton;
	private Button flipButton;
	private Button drawButton;
	private Button confirmButton;
	
	//The board that draws each piece
	private ChessBoard board;
	
	// the most recent game state, as given to us by the CounterLocalGame
	private ChessGameState state;
	
	// the android activity that we are running
	private GameMainActivity activity;
	
	//true if this player is white, false if this player is black
	private boolean isWhite = true;
	
	//true if the user is holding down, false if not
	private boolean down;
	
	//the last piece the player touched
	protected ChessPiece lastPieceSelected;
	
	//the valid locations for a move using the lastPieceSelected
	private boolean[][] validLocs;
	
	private Vibrator vibrator;
	
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
		player1Score.setText(""+state.getPlayer1Points());
		player2Score.setText(""+state.getPlayer2Points());
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
			DrawAction act = new DrawAction(this,isWhite());
			
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
		Log.d("human player",state.toString());
		updateDisplay();
		//Log.d("human player", "state updated:"+state);
	}
	
	/**
	 * callback method--our game has been chosen/rechosen to be the GUI,
	 * called from the GUI thread
	 * 
	 * @param activity
	 * 		the activity under which we are running
	 */
	public void setAsGui(GameMainActivity activity) {
		
		// remember the activity
		this.activity = activity;
		
	    // Load the layout resource for our GUI
		activity.setContentView(R.layout.chess_human_player);
		
		// Set listeners for each button
		drawButton = (Button)activity.findViewById(R.id.drawButton);
		drawButton.setOnClickListener(this);
		confirmButton = (Button)activity.findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(this);
		flipButton = (Button)activity.findViewById(R.id.flipBoardButton);
		flipButton.setOnClickListener(this);
		quitButton = (Button)activity.findViewById(R.id.resignButton);
		quitButton.setOnClickListener(this);
		
		// Find the score TextViews
		this.player1Score =
				(TextView) activity.findViewById(R.id.player1ScoreTextView);
		this.player2Score =
				(TextView) activity.findViewById(R.id.player2ScoreTextView);
		
		// Find the board
		board = (ChessBoard)activity.findViewById(R.id.gameBoardSurfaceView);
		
		board.setOnTouchListener(this);
		
		vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
		
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
				setWhite(true);
				startGame();
			}
		};
		android.content.DialogInterface.OnClickListener blackListener =
				new android.content.DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) {
				setWhite(false);
				startGame();
			}
		};
		
		//make a dialog to ask which color the human wants to be
		MessageBox.popUpChoice(colorQuestion, whiteLabel, blackLabel, whiteListener, blackListener, activity);
		
		if(!isPlayer1())
		{
			board.flipBoard();
		}
	}
	/**
	 * Returns true if it is white
	 * @return true 
	 */
	public boolean isWhite()
	{
		return isWhite;
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
				if(state.isWhoseTurn() != isPlayer1())
				{
					return false;
				}
				
				float[] tileSize = board.getTileSize();
				int tileX;
				int tileY;
				int[] selectedLoc;
				
				//translate the points if the board is flipped
				if(board.isFlipped())
				{
					tileX = (int) (event.getX()/tileSize[0]);
					tileY = ChessGameState.BOARD_WIDTH-1-(int) (event.getY()/tileSize[1]);
					selectedLoc = new int[]{tileY,tileX};
				}
				else
				{
					tileX = (int) (event.getX()/tileSize[0]);
					tileY = (int) (event.getY()/tileSize[1]);
					selectedLoc = new int[]{tileY,tileX};
				}
				
				ChessPiece pieceSelected = null;
				
				// Make sure it is within bounds
				if(ChessGameState.outOfBounds(selectedLoc))
				{
					return false;
				}
				if(state == null || state.getPieceMap() == null)
				{
					return false;
				}
				
				down = false;
				
				if(state.getPieceMap()[tileY][tileX] != null)
				{
					pieceSelected = new ChessPiece(state.getPieceMap()[tileY][tileX]);
				}
				
				//clear the selected locations first
				board.setSelectedTiles(null);
				
				//make the selected location invalid
				board.setSelectedLoc(-1, -1);
				
				//selected the same piece twice
				if(lastPieceSelected != null && lastPieceSelected.equals(pieceSelected))
				{
					lastPieceSelected = null;
					return true;
				}
				
				//selected a piece of the same color different from the last one
				if(pieceSelected != null && pieceSelected.isWhite() == isWhite() && !pieceSelected.equals(lastPieceSelected))
				{
					//reset the valid locations

					//pass selected tile to ChessBoard
					board.setSelectedLoc(tileY,tileX);
					
					// Highlight valid moves for the player:
					validLocs = state.getPossibleMoves(pieceSelected);
					
					//TODO check for illegal moves
					board.setSelectedTiles(validLocs);
					
					lastPieceSelected = pieceSelected;
					return true;
				}
				
				/*
				 * Didn't select a tile with a piece on it or it is of a different color,
				 * so the last piece could possibly move here.
				 */
				if(pieceSelected == null || pieceSelected.isWhite() != isWhite())
				{
					//Is a valid location to move the piece to
					if(validLocs != null && validLocs[tileY][tileX] == true)
					{
						//Create and apply the move
						ChessMoveAction move = null;
						ChessPiece takenPiece = state.getPieceMap()[tileY][tileX];
						if(lastPieceSelected.getType() == ChessPiece.PAWN)
						{
							//TODO implement special moves
							move = new PawnMove(this, lastPieceSelected, selectedLoc,
									takenPiece,PawnMove.NONE);
						}
						else if(lastPieceSelected.getType() == ChessPiece.ROOK)
						{
							//TODO implement special moves
							move = new RookMove(this, lastPieceSelected, selectedLoc,
									takenPiece,RookMove.NONE);
						}
						else
						{
							move = new ChessMoveAction(this,
									lastPieceSelected, selectedLoc,takenPiece);
						}
						state.applyMove(move);//TODO not sure if necessary
						game.sendAction(move);
					}
					
					/*
					 * The user either tried to make an invalid move or made
					 * a move, so clear piece selections.
					 */
					lastPieceSelected = null;
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
	 * Sets this player as white or black
	 * @param true for white and false for black.
	 */
	public void setWhite(boolean isWhite) {
		this.isWhite = isWhite;
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
	}

	public void selectUpgrade() {
		CharSequence upgradeChoices[] = new CharSequence[] {"Queen", "Knight", "Rook", "Bishop"};
		
		//TODO fix
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
		builder.show();
	}
	
	public SelectUpgradeAction doSelectAction(int type)
	{
		SelectUpgradeAction newAct = new SelectUpgradeAction(this, this.lastPieceSelected, types[type]);
		game.sendAction(newAct);
		return null;
	}
	
	public void vibrate(int time)
	{
		vibrator.vibrate(time);
	}
}// class CounterHumanPlayer

