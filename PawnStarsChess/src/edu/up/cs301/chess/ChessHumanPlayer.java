package edu.up.cs301.chess;

import edu.up.cs301.chess.actions.ChessMoveAction;
import edu.up.cs301.chess.actions.DrawAction;
import edu.up.cs301.chess.actions.PawnMoveAction;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.MessageBox;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

/**
 * A GUI of a chess player. The GUI displays the current state of the
 * chessboard. It allows the player to select a piece and move it. The player
 * can also quit, flip the board, ask for a draw, and confirm moves.
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version March 2015
 */
public class ChessHumanPlayer extends GameHumanPlayer implements ChessPlayer,
		OnClickListener, OnTouchListener {

	/* instance variables */

	// The TextView the displays the current counter value
	private TextView player1Score;
	private TextView player2Score;

	// The buttons at the side of the screen
	private Button quitButton;
	private Button flipButton;
	private Button drawButton;
	private Button confirmButton;

	// The board that draws each piece
	private ChessBoard board;

	// the most recent game state, as given to us by the CounterLocalGame
	private ChessGameState state;

	// the android activity that we are running
	private GameMainActivity activity;

	// TODO: implement isWhite
	private boolean isWhite = true;// ask for this somehow

	private boolean down;

	private ChessPiece lastPieceSelected;

	private boolean[][] validLocs = new boolean[ChessGameState.BOARD_HEIGHT][ChessGameState.BOARD_WIDTH];

	private boolean isPlayer1;

	protected boolean sentPlayerID;

	/**
	 * constructor
	 * 
	 * @param name
	 *            the player's name
	 */
	public ChessHumanPlayer(String name) {
		super(name);
	}

	/**
	 * Returns the GUI's top view object
	 * 
	 * @return the top object in the GUI's view hierarchy
	 */
	public View getTopView() {
		return activity.findViewById(R.id.top_gui_layout);
	}

	/**
	 * Sets the each player's score on the screen and updates the board
	 */
	protected void updateDisplay() {
		board.setPieceMap(state.getPieceMap());
		player1Score.setText("" + state.getPlayer1Points());
		player2Score.setText("" + state.getPlayer2Points());
	}

	/**
	 * this method gets called when the user clicks on the GameBoard.
	 * 
	 * @param button
	 *            the button that was clicked
	 */
	public void onClick(View button) {
		// if we are not yet connected to a game, ignore
		if (game == null)
			return;

		// Construct the action and send it to the game
		GameAction action = null;
		if (button.getId() == R.id.resignButton) {
			// make the activity think it received a back button
			KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_BACK);
			activity.onKeyDown(KeyEvent.KEYCODE_BACK, event);
		} else if (button.getId() == R.id.drawButton) {
			DrawAction act = new DrawAction(this, isWhite());
			ChessGameState newState = new ChessGameState(state);
			newState.applyMove(act);

			sendInfo(newState);
			state = newState;
		} else if (button.getId() == R.id.flipBoardButton) {

		} else {
			// something else was pressed: ignore
			return;
		}

		game.sendAction(action); // send action to the game
	}// onClick

	/**
	 * callback method when we get a message (e.g., from the game)
	 * 
	 * @param info
	 *            the message
	 */
	@Override
	public void receiveInfo(GameInfo info) {
		// ignore the message if it's not a ChessGameState message
		if (!(info instanceof ChessGameState)) {
			return;
		}
		ChessGameState newState = (ChessGameState) info;

		if (newState == null || newState.equals(state)) {
			return;
		}
		if (!sentPlayerID)
			sentPlayerID = newState.setPlayerInfo(this);
		// update our state; then update the display
		this.state = newState;
		updateDisplay();
	}

	/**
	 * callback method--our game has been chosen/rechosen to be the GUI, called
	 * from the GUI thread
	 * 
	 * @param activity
	 *            the activity under which we are running
	 */
	public void setAsGui(GameMainActivity activity) {

		// remember the activity
		this.activity = activity;

		// Load the layout resource for our GUI
		activity.setContentView(R.layout.chess_human_player);

		// Set listeners for each button
		drawButton = (Button) activity.findViewById(R.id.drawButton);
		drawButton.setOnClickListener(this);
		confirmButton = (Button) activity.findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(this);
		flipButton = (Button) activity.findViewById(R.id.flipBoardButton);
		flipButton.setOnClickListener(this);
		quitButton = (Button) activity.findViewById(R.id.resignButton);
		quitButton.setOnClickListener(this);

		// Find the score TextViews
		this.player1Score = (TextView) activity
				.findViewById(R.id.player1ScoreTextView);
		this.player2Score = (TextView) activity
				.findViewById(R.id.player2ScoreTextView);

		// Find the board
		board = (ChessBoard) activity.findViewById(R.id.gameBoardSurfaceView);

		board.setOnTouchListener(this);

		// Ask which color the player wants to be.
		String colorQuestion = activity.getResources().getString(
				R.string.dialog_color_question);
		String whiteLabel = activity.getResources().getString(
				R.string.dialog_white_label);
		String blackLabel = activity.getResources().getString(
				R.string.dialog_black_label);
		String pickColorTitle = activity.getResources().getString(
				R.string.dialog_title);

		android.content.DialogInterface.OnClickListener posListener = new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				setWhite(true);
			}
		};
		android.content.DialogInterface.OnClickListener negListener = new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				setWhite(false);
			}
		};

		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(colorQuestion);
		builder.setTitle(pickColorTitle);
		builder.setPositiveButton(whiteLabel, posListener);
		builder.setNegativeButton(blackLabel, negListener);
		AlertDialog alert = builder.create();
		alert.show();
		 
		// if we have a game state, "simulate" that we have just received
		// the state from the game so that the GUI values are updated
		if (state != null) {
			state.setPlayer1IsWhite(isPlayer1 == isWhite);
			sentPlayerID = state.setPlayerInfo(this);
			receiveInfo(state);

		}
	}

	/**
	 * Returns true if it is white
	 * 
	 * @return true
	 */
	public boolean isWhite() {
		return isWhite;
	}

	public boolean onTouch(View v, MotionEvent event) {
		v.onTouchEvent(event);

		// Respond only to a full tap (tap down then tap up):
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			down = true;
		} else if (event.getAction() == MotionEvent.ACTION_UP && down) {
			// Make sure game state and pieceMaps are valid:
			if (state == null || state.getPieceMap() == null) {
				return false;
			}

			// Check to see if the board was clicked
			if (v == board) {
				// Reset down boolean (to detect next "click")
				down = false;

				// TODO sound feedback/vibration
				// Get the tile the event corresponds to
				float[] tileSize = board.getTileSize();
				int tileX = (int) (event.getX() / tileSize[0]);
				int tileY = (int) (event.getY() / tileSize[1]);
				int[] selectedLoc = new int[2];
				selectedLoc[0] = tileY;
				selectedLoc[1] = tileX;

				// Make sure selected tile is within bounds
				if (ChessGameState.outOfBounds(selectedLoc)) {
					return false;
				}

				// See if the user wanted to move a piece:
				if (this.lastPieceSelected != null) {
					// Send a move action to the local game (will be ignored if
					// invalid move):
					if (this.lastPieceSelected.getType() == ChessPiece.PAWN) {
						PawnMoveAction move = new PawnMoveAction(this, lastPieceSelected, selectedLoc);
						game.sendAction(move);
					} else {
						ChessMoveAction move = new ChessMoveAction(this,
								lastPieceSelected, selectedLoc);
						game.sendAction(move);
					}
					this.lastPieceSelected = null;
				}

				// Get the selected piece from the game state:
				ChessPiece pieceSelected = state.getPieceMap()[tileY][tileX];

				if (pieceSelected != null) {
					// Check to see if piece is of the player's same color
					if (pieceSelected.isWhite() == this.isWhite) {
						// Pass selected tile to ChessBoard
						board.setSelectedLoc(tileY, tileX);
						// Highlight valid moves for the player:
						boolean[][] validMoves = state
								.getPossibleMoves(pieceSelected);

						if (validMoves != null && validMoves.length > 0) {
							board.setSelectedTiles(validMoves);
						}
					}

					// Update selected piece:
					this.lastPieceSelected = pieceSelected;
				}
			}
		}
		return true;
	}

	public boolean isPlayer1() {
		return isPlayer1;
	}

	public void setWhite(boolean isWhite) {
		this.isWhite = isWhite;
	}

	public void setPlayer1(boolean isPlayer1) {
		this.isPlayer1 = isPlayer1;
	}

	public int getPlayerID() {
		return playerNum;
	}

}// class CounterHumanPlayer

