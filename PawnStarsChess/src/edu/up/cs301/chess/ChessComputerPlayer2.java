package edu.up.cs301.chess;

import edu.up.cs301.chess.actions.ChessMoveAction;
import edu.up.cs301.chess.actions.ChooseColorAction;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;
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
* @version April 2015
*/
public class ChessComputerPlayer2 extends ChessComputerPlayer1 {

	private static final int MAX_NAME_LENGTH = 12;
	/*
	 * instance variables
	 */

	// the score TextViews for each player
	private TextView player1Score;
	private TextView player2Score;
	
	private TextView player1View;
	private TextView player2View;
	private TextView turnText;

	// The chessboard to draw on
	private ChessBoard board;

	// If this player is running the GUI, the handler for the GUI thread
	// (otherwise
	// null)
	private Handler guiHandler = null;
	
	/**
	 * constructor
	 * 
	 * @param name
	 *            the player's name
	 */
	public ChessComputerPlayer2(String name, int intelligence) {
		super(name, intelligence);
	}

	/**
	 * Updates the ChessBoard and score textviews
	 * */
	private void updateDisplay() {
		// if the guiHandler is available, set the new counter value
		// in the counter-display widget, doing it in the Activity's
		// thread.

		final Runnable update = new Runnable() {
			@Override
			public void run() {

				if (gameState != null) {
					board.setPieceMap(gameState.getPieceMap());
					player1Score.setText(""+gameState.getPlayer1Points());
					player2Score.setText(""+gameState.getPlayer2Points());
					
					//select the tile the computer moved to
					if(chosenMove instanceof ChessMoveAction)
					{
						byte[] loc = ((ChessMoveAction)chosenMove).getNewPos();
						board.setSelectedLoc(loc[0],loc[1]);
					}
					
					if(player1View != null && name != null)
					{
						String msg = name;
						if(msg.length() > MAX_NAME_LENGTH)
						{
							msg = msg.substring(0, MAX_NAME_LENGTH);
						}
						player1View.setText(msg);
					}
					if(player2View != null && allPlayerNames != null)
					{
						if(allPlayerNames.length >1 && allPlayerNames[1] != null) {
							String msg = allPlayerNames[1];
							if(msg.length() > MAX_NAME_LENGTH)
							{
								msg = msg.substring(0, MAX_NAME_LENGTH);
							}
							player2View.setText(msg);
						}
						
					}
					
					if(gameState.isWhoseTurn() == gameState.isPlayer1IsWhite()) {
						turnText.setText("Turn: White");
					} else {
						turnText.setText("Turn: Black");
					}
					
					// Goes through player 1's pieces to see what ones are dead/alive
					player1Score.setText("" + gameState.getPlayer1Points());
					player2Score.setText("" + gameState.getPlayer2Points());

					// Goes through player 1's pieces to see what ones are
					// dead/alive
					for (int i = 0; i < gameState.getPlayer1Pieces().length; i++) {
						if (gameState.getPlayer1Pieces()[i].isAlive() == false) {
							// Add the taken piece to either the white or black
							// array in Board Class
							if (gameState.getPlayer1Pieces()[i].isWhite()) {
								board.setWhiteTakenPiece(
										gameState.getPlayer1Pieces()[i], i);
							} else {
								board.setBlackTakenPiece(
										gameState.getPlayer1Pieces()[i], i);
							}
						}
					}

					// Goes through player 2's pieces to see what ones are
					// dead/alive
					for (int j = 0; j < gameState.getPlayer2Pieces().length; j++) {
						if (gameState.getPlayer2Pieces()[j].isAlive() == false) {
							// Add the taken piece to either the white or black
							// array in Board Class
							if (gameState.getPlayer2Pieces()[j].isWhite()) {
								board.setWhiteTakenPiece(
										gameState.getPlayer2Pieces()[j], j);
							} else {
								board.setBlackTakenPiece(
										gameState.getPlayer2Pieces()[j], j);
							}
						}
					}
				}
			}
		};
		if (guiHandler != null) {
			guiHandler.post(update);
		}
	}

	/**
	 * Tells whether we support a GUI
	 * 
	 * @return true because we support a GUI
	 */
	public boolean supportsGui() {
		return true;
	}

	/**
	 * callback method--our player has been chosen/rechosen to be the GUI,
	 * called from the GUI thread.
	 * 
	 * @param a
	 *            the activity under which we are running
	 */
	@Override
	public void setAsGui(GameMainActivity act) {
		super.setAsGui(act);

		// remember the handler for the GUI thread
		this.guiHandler = new Handler();

		// Load the layout resource for the our GUI's configuration
		act.setContentView(R.layout.chess_human_player);

		this.player1Score = (TextView) act.findViewById(R.id.player1ScoreTextView);
		this.player2Score = (TextView) act.findViewById(R.id.player2ScoreTextView);

		board = (ChessBoard) act.findViewById(R.id.gameBoardSurfaceView);

		if (gameState != null && game != null) {
			ChooseColorAction action = new ChooseColorAction(this, isWhite());
			game.sendAction(action);
			updateDisplay();
		}
		
		// Set the name text views:
		player1View = (TextView) act.findViewById(R.id.player1TextView);
		player2View = (TextView) act.findViewById(R.id.player2TextView);
		
		turnText = (TextView) activity.findViewById(R.id.turnTextView);
		
		//TODO shorten names if necessary
	}

	@Override
	public void receiveInfo(GameInfo info) {
		super.receiveInfo(info);
		updateDisplay();
	}
}
