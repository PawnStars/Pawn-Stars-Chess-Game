package edu.up.cs301.chess;

import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import android.view.View;
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
 * @version March 2015
 */
public class ChessHumanPlayer extends GameHumanPlayer implements ChessPlayer, OnClickListener {

	/* instance variables */
	
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
	
	// The rotation of the board in degrees
	private int boardRotation;
	
	// the most recent game state, as given to us by the CounterLocalGame
	private ChessGameState state;
	
	// the android activity that we are running
	private GameMainActivity activity;
	
	//TODO: implement isWhite
	private boolean isWhite;
	
	/**
	 * constructor
	 * 
	 * @param name
	 * 		the player's name
	 */
	public ChessHumanPlayer(String name) {
		super(name);
		boardRotation = 0;
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

		// Construct the action and send it to the game
		GameAction action = null;
		if (button.getId() == R.id.gameBoardSurfaceView) {
			//TODO Implement moves
		}
		else if (button.getId() == R.id.resignButton) {
			//TODO Implement quit
		}
		else if (button.getId() == R.id.drawButton) {
			//TODO Implement draw
		}
		else if (button.getId() == R.id.flipBoardButton) {
			
		}
		else {
			// something else was pressed: ignore
			return;
		}
		
		game.sendAction(action); // send action to the game
	}// onClick
	
	/**
	 * callback method when we get a message (e.g., from the game)
	 * 
	 * @param info
	 * 		the message
	 */
	@Override
	public void receiveInfo(GameInfo info) {
		// ignore the message if it's not a CounterState message
		if (!(info instanceof ChessGameState)) return;
		
		// update our state; then update the display
		this.state = (ChessGameState)info;
		updateDisplay();
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
		
		// if we have a game state, "simulate" that we have just received
		// the state from the game so that the GUI values are updated
		if (state != null) {
			receiveInfo(state);
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

}// class CounterHumanPlayer
