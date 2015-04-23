package edu.up.cs301.chess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.res.AssetManager;
import android.os.Handler;
import android.util.Log;
import edu.up.cs301.chess.actions.ChessMoveAction;
import edu.up.cs301.chess.actions.DrawAction;
import edu.up.cs301.chess.engine.MoveGenerator;
import edu.up.cs301.chess.engine.UCIInterface;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.Tickable;
    
/**
 * A computer-version of a chess player.
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version April 2015
 */
public class ChessComputerPlayer1 extends GameComputerPlayer implements ChessPlayer, Tickable {
	
	public static final int RANDOM = 0;
	public static final int TAKE_PIECES = 1; 
	
	public static final int CRITTER = 9; 
	public static final int STOCKFISH = 10; 
	/*
	 * The intelligence of the AI
	 * A value of MAX_INTELLIGENCE corresponds to a deeper search
	 * for the best move and selecting the best one. Lower values
	 * correspond to a shallower search and a lower inclination to
	 * pick the best move.
	 */
    protected int smart;
    
    // The current game state according to this player
    protected ChessGameState gameState;
    
    //true if this player is white, false if not
    //protected boolean isWhite;
    
    private ArrayList<String> engines;
    
    //The interface between the chess engine executable and java
    private UCIInterface client;
    
    //the time to wait before sending a move
    private int waitTime;
    
    protected GameMainActivity activity;
    
    private Handler engineHandler = new Handler();
    
    private ChessPlayer player;
    
    private GameAction chosenMove;
    
	/**
     * Constructor for objects of class CounterComputerPlayer1
     * 
     * @param name
     * 		the player's name
     */
    public ChessComputerPlayer1(String name, int intelligence) {
        // invoke superclass constructor
        super(name);
        smart = intelligence;
        waitTime = 5000;
        engines = new ArrayList<String>();
        player = this;
        
        
        /*
         * Pick white or black randomly. The game state will
         * decide which player's choice goes through depending
         * on how fast they initialize the state.
         */
        
        //isWhite = Math.random() > 0.5;
    }
    
    /**
     * callback method--game's state has changed
     * 
     * @param info
     * 		the information (presumably containing the game's state)
     */
	@Override
	protected void receiveInfo(GameInfo info) {
		
		Log.i("computer player", "receiving");
		
		// if there is no game, ignore
		if (game == null) {
			Log.d("computer player", "no game");
			return;
		}
		else if (info instanceof ChessGameState) {
			ChessGameState newState = (ChessGameState)info;
			
			//make sure the state was changed
			if(newState.equals(gameState))
			{
				Log.d("computer player", "equal game state");
				return;
			}
			else
			{
				gameState = newState;
				Log.d("computer player",gameState.toString());
				//not sure if we need to apply move
				GameAction move = makeMove();
				Log.d("computer player", "Sending this move: "+move);
				if(gameState.applyMove(move))
				{
					game.sendAction(move);
				}
				else
				{
					Log.d("computer player", "Error when applying this move: "+move);
				}
			}
		}
		else
		{
			Log.d("computer player", "error");
		}
	}
	
	/**
	 * Generates a move according to the AI's intelligence
	 * level.
	 */
	public GameAction makeMove()
	{
		//Log.d("computer player", "trying to make a move");
		
		if(gameState == null || gameState.isWhoseTurn() != isPlayer1())
		{
			return null;
		}
		
		long start = System.currentTimeMillis();
		ChessGameState newState = new ChessGameState(gameState);
		chosenMove = null;
		if(smart > 1)
		{
			String FEN = gameState.toFEN();
			
			if(FEN != null && !FEN.equals(""))
			{
				// initialize and connect to engine
				
				final Runnable getBestMove = new Runnable() {
					@Override
					public void run()
					{
						//client.sendCommand("uci");//the list of commands
						//TODO stop the engine from playing as your color
						if (client != null && client.startEngine())
						{
							String FEN = gameState.toFEN();
							String bestMove = client.getBestMove(FEN,waitTime);
						
							Log.d("computer player","engine's best move:"+bestMove);
							chosenMove = ChessMoveAction.moveTextToAction(gameState, player, bestMove);
							
							// stop the engine
							client.stopEngine();
						}
					}
				};
				if(engineHandler != null)
				{
					engineHandler.post(getBestMove);
				}
			}
		}
		if(smart == RANDOM || smart == TAKE_PIECES || chosenMove == null)
		{
			//Get all the possible moves
			ChessMoveAction[] possibleActions = MoveGenerator.getPossibleMoves(newState, this, isWhite());
			
			//Check if the move generator found any possible moves
			if(possibleActions != null && possibleActions.length > 0)
			{
				//scramble the order of the moves so it won't be predictable
				for(int i=0;i<possibleActions.length;i++)
				{
					int randomIndex = (int) (Math.random()*(possibleActions.length));
					ChessMoveAction temp = possibleActions[i];
					
					possibleActions[i] = possibleActions[randomIndex];
					possibleActions[randomIndex] = temp;
				}
				
				for(int i=0;i<possibleActions.length;i++)
				{
					System.out.print(possibleActions[i].toString()+", ");
					ChessMoveAction tempMove = new ChessMoveAction(this,possibleActions[i]);
					if(tempMove != null)
					{
						chosenMove = tempMove;
						if(smart == RANDOM)
						{
							//Apply any valid non-null move
							break;
						}
						if(smart >= TAKE_PIECES)
						{
							//Do any move that would result in taking a piece if it exists
							if(tempMove.getTakenPiece() != null)
							{
								break;
							}
						}
					}
				}
			}
			//Make sure the player is included as a reference
			chosenMove = new ChessMoveAction(this,(ChessMoveAction)chosenMove);

		}
		
		//wait the remaining amount of time
		if((System.currentTimeMillis()-start) < waitTime)
		{
			try
			{
				Thread.sleep(waitTime+start-System.currentTimeMillis());
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		//send the new game state if it worked
		return chosenMove;
	}

	/**
	 * Returns the color of this player as a boolean.
	 * 
	 * @return true if this color is white,
	 * 		   false if not.
	 */
	public boolean isWhite() {
		
		return (isPlayer1() == gameState.isPlayer1IsWhite());
	}

	/**
	 * Returns true if this player is player 1 in the game state.
	 * 
	 * @return boolean true if this player is player 1
	 */
	public boolean isPlayer1() {
		return playerNum == 0;//return isPlayer1;
	}

	public int getPlayerID() {
		return playerNum;
	}

	/**
	 * How to respond when a player asks for a draw
	 */
	public void askDraw(String msg)
	{
		if(smart < 10)
		{
			DrawAction act = new DrawAction(this,isWhite(),true);
			game.sendAction(act);
		}
		//Do not accept if the computer is smart
	}
	
	@Override
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
		
		Log.d("computer player","setting computer as gui");
		
		// remember who our activity is
		activity = act;
		
		engineHandler = new Handler();
		
		//copy assets
		if(smart > 1)
		{
			copyAssets();
			String engine = "";
			for(String str: engines)
			{
				if(str.contains("stockfish") && smart == STOCKFISH)
		        {
		        	engine = str;
		        }
				else if(str.contains("critter") && smart == CRITTER)
		        {
		        	engine = str;
		        }
			}
			client = new UCIInterface(engine);
		}
	}
	
	private void copyAssets()
	{
	    AssetManager assetManager = activity.getAssets();
	    String[] files = null;
	    try
	    {
	    	//get all of the files in assets
	        files = assetManager.list("");
	    }
	    catch (IOException e)
	    {
	        Log.e("tag", "Failed to get asset file list.", e);
	    }
	    for(String filename : files)
	    {
	        InputStream in = null;
	        OutputStream out = null;
	        try
	        {
				File outFile = new File(activity.getCacheDir(),filename);
				
				//only copy relevant files
				boolean isEngine = filename.contains("engine");
				if(isEngine || filename.contains("."))
				{
					if(!outFile.exists())
					{
						in = assetManager.open(filename);
						out = new FileOutputStream(outFile);
						copyFile(in, out);
						Log.d("computer player", "copied asset file: " + outFile.getAbsolutePath());
					}
					outFile.setReadable(true);
					outFile.setWritable(true);
					outFile.setExecutable(true);
					if(isEngine)
					{
						engines.add(outFile.getAbsolutePath());
						Log.d("computer player", "engine: " + outFile.getAbsolutePath());
					}
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
	                	Log.e("tag", "Failed to close source "+filename, e);
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
	                	Log.e("tag", "Failed to close copy", e);
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
