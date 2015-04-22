package edu.up.cs301.chess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.up.cs301.chess.actions.ChooseColorAction;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import android.app.Activity;
import android.content.res.AssetManager;
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
	 * Updates the ChessBoard and score textviews
	 *  */
	private void updateDisplay() {
		// if the guiHandler is available, set the new counter value
		// in the counter-display widget, doing it in the Activity's
		// thread.
		
		final Runnable update = new Runnable()
		{
			@Override
			public void run() {
				
				if(gameState != null)
				{
					board.setPieceMap(gameState.getPieceMap());
					player1Score.setText(""+gameState.getPlayer1Points());
					player2Score.setText(""+gameState.getPlayer2Points());
					
					// Goes through player 1's pieces to see what ones are dead/alive
					for (int i = 0; i < gameState.getPlayer1Pieces().length; i++) {
						if (gameState.getPlayer1Pieces()[i].isAlive() == false) {
							//Add the taken piece to either the white or black array in Board Class
							if (gameState.getPlayer1Pieces()[i].isWhite()){
								board.setWhiteTakenPiece(gameState.getPlayer1Pieces()[i], i);
							}
							else {
								board.setBlackTakenPiece(gameState.getPlayer1Pieces()[i], i);
							}
						}
					}
	
					// Goes through player 2's pieces to see what ones are dead/alive
					for (int j = 0; j < gameState.getPlayer2Pieces().length; j++) {
						if (gameState.getPlayer2Pieces()[j].isAlive() == false) {
							//Add the taken piece to either the white or black array in Board Class
							if (gameState.getPlayer2Pieces()[j].isWhite()){
								board.setWhiteTakenPiece(gameState.getPlayer2Pieces()[j], j);
							}
							else {
								board.setBlackTakenPiece(gameState.getPlayer2Pieces()[j], j);
							}
						}
					}
				}
			}
		};
		guiHandler.post(update);
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
		
		copyAssets();
		
		// if the state is non=null, update the display
		if(gameState != null && game != null)
		{
			updateDisplay();
			ChooseColorAction action = new ChooseColorAction(this,isWhite());
			game.sendAction(action);
		}
	}
	
	@Override
	protected void receiveInfo(GameInfo info) {
		super.receiveInfo(info);
		updateDisplay();
	}
	
	private void copyAssets()
	{
	    AssetManager assetManager = activityForGui.getAssets();
	    String[] files = null;
	    try
	    {
	        files = assetManager.list("");
	    }
	    catch (IOException e)
	    {
	        Log.e("tag", "Failed to get asset file list.", e);
	    }
	    for(String filename : files)
	    {
	    	if(filename.contains("engine"))
	    	{
		        InputStream in = null;
		        OutputStream out = null;
		        try
		        {
					File outFile = new File(activityForGui.getCacheDir(),filename);
					if(!outFile.exists())
					{
						in = assetManager.open(filename);
						out = new FileOutputStream(outFile);
						copyFile(in, out);
						Log.d("tag", "copied asset file: " + outFile.getAbsolutePath());
						outFile.setExecutable(true);
					}
		        }
		        catch(IOException e)
		        {
		            Log.e("tag", "Failed to copy asset file: " + filename, e);
		        }
		        finally
		        {
		            if(in != null)
		            {
		                try
		                {
		                    in.close();
		                }
		                catch (IOException e)
		                {
		                    // NOOP
		                }
		            }
		            if
		            (out != null)
		            {
		                try
		                {
		                    out.close();
		                }
		                catch (IOException e)
		                {
		                    // NOOP
		                }
		            }
		        }
	    	}
	    }
	}
	
	private void copyFile(InputStream in, OutputStream out) throws IOException
	{
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
}
