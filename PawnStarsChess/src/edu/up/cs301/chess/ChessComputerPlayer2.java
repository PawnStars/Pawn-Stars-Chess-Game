package edu.up.cs301.chess;

import edu.up.cs301.chess.actions.ChooseColorAction;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import android.app.Activity;
import android.os.Handler;
import android.widget.TextView;


/**
* A computer-version of the human player.  It generates a list of moves
* it can make and picks one according to its intelligence level. This
* computer player does display the game as it is progressing, so if
* there is no human player on the device, this player will display a GUI
* that displays the board as the game is being played.
* 
* @author Anthony Donaldson
* @author Derek Schumacher
* @author Scott Rowland
* @author Allison Liedtke
* @version March 2015
*/
public class ChessComputerPlayer2 extends ChessComputerPlayer1 {
	
	/*
	 * instance variables
	 */
	
	//the score TextViews for each player
	private TextView player1Score;
	private TextView player2Score;
	
	//The chessboard to draw on
	private ChessBoard board;
	
	// If this player is running the GUI, the activity (null if the player is
	// not running a GUI).
	private Activity activityForGui = null;
	
	// If this player is running the GUI, the handler for the GUI thread (otherwise
	// null)
	private Handler guiHandler = null;
	
	/**
	 * constructor
	 * 
	 * @param name
	 * 		the player's name
	 */
	public ChessComputerPlayer2(String name, int intelligence) {
		super(name, intelligence);
	}
	
	/** 
	 * sets the counter value in the text view
	 *  */
	private void updateDisplay() {
		// if the guiHandler is available, set the new counter value
		// in the counter-display widget, doing it in the Activity's
		// thread.
		/*if (guiHandler != null) {
			guiHandler.post(
					new Runnable() {
						public void run() {
						if (gameState != null) {
							board.setPieceMap(gameState.getPieceMap());
							player1Score.setText(gameState.getPlayer1Points());
							player2Score.setText(gameState.getPlayer2Points());
						}
					}});
		}*/
		board.setPieceMap(gameState.getPieceMap());
		player1Score.setText(""+gameState.getPlayer1Points());
		player2Score.setText(""+gameState.getPlayer2Points());
	}
	
	/**
	 * Tells whether we support a GUI
	 * 
	 * @return
	 * 		true because we support a GUI
	 */
	public boolean supportsGui() {
		return true;
	}
	
	/**
	 * callback method--our player has been chosen/rechosen to be the GUI,
	 * called from the GUI thread.
	 * 
	 * @param a
	 * 		the activity under which we are running
	 */
	@Override
	public void setAsGui(GameMainActivity act) {
		
		// remember who our activity is
		this.activityForGui = act;
		
		// remember the handler for the GUI thread
		this.guiHandler = new Handler();
		
		// Load the layout resource for the our GUI's configuration
		activityForGui.setContentView(R.layout.chess_human_player);

		// Find the score TextViews
		this.player1Score =
				(TextView) activityForGui.findViewById(R.id.player1ScoreTextView);
		this.player2Score =
				(TextView) activityForGui.findViewById(R.id.player2ScoreTextView);
		
		// Find the board
		board = (ChessBoard) activityForGui.findViewById(R.id.gameBoardSurfaceView);
		
		//50/50 chance to be white or black
		setWhite(Math.random() > 0.5);
		
		// if the state is non=null, update the display
		if (gameState != null) {
			ChooseColorAction action = new ChooseColorAction(this,isWhite);
			if(game instanceof ChessLocalGame)
			{
				ChessLocalGame chessGame = (ChessLocalGame)game;
				chessGame.makeMove(action);
			}
			updateDisplay();
		}
	}
}
