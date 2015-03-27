package edu.up.cs301.chess;

import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;


/**
* A computer-version of the human player.  It generates a list of moves
* it can make and picks one according to its intelligence level. This
* computer player does display the game as it is progressing, so if
* there is no human player on the device, this player will display a GUI
* that displays the board as the game is being played.
* 
* @author 
* @version
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
	
	// the most recent game state, as given to us by the ChessLocalGame
	private ChessGameState currentGameState = null;
	
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
     * callback method--game's state has changed
     * 
     * @param info
     * 		the information (presumably containing the game's state)
     */
	@Override
	protected void receiveInfo(GameInfo info) {
		// perform superclass behavior
		super.receiveInfo(info);
		
		Log.i("computer player", "receiving");
		
		// if there is no game, ignore
		if (game == null) {
			return;
		}
		else if (info instanceof ChessGameState) {
			// if we indeed have a counter-state, update the GUI
			currentGameState = (ChessGameState)info;
			updateDisplay();
		}
	}
	
	
	/** 
	 * sets the counter value in the text view
	 *  */
	private void updateDisplay() {
		// if the guiHandler is available, set the new counter value
		// in the counter-display widget, doing it in the Activity's
		// thread.
		if (guiHandler != null) {
			guiHandler.post(
					new Runnable() {
						public void run() {
						if (currentGameState != null) {
							board.setPieceMap(currentGameState.getPieceMap());
							player1Score.setText(currentGameState.getPlayer1Points());
							player2Score.setText(currentGameState.getPlayer2Points());
						}
					}});
		}
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
		
		// if the state is non=null, update the display
		if (currentGameState != null) {
			updateDisplay();
		}
	}
}
