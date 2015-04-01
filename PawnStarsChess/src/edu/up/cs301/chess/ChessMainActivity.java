package edu.up.cs301.chess;

import java.util.ArrayList;

import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;

/**
 * This is the primary activity for Chess.
 * 
 * @author Allison Liedtke
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @version March 2015
 */
public class ChessMainActivity extends GameMainActivity {
	
	// the port number that this game will use when playing over the network
	private static final int PORT_NUMBER = 2244;

	/**
	 * Create the default configuration for this game:
	 * - one human player vs. one computer player
	 * - only 2 players
	 * - two kinds of computer player and one kind of human player available
	 * 
	 * @return
	 * 		the new configuration object, representing the default configuration
	 */
	@Override
	public GameConfig createDefaultConfig() {
		
		// Define the allowed player types
		ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();
		
		// a human player player type (player type 0)
		playerTypes.add(new GamePlayerType("Local Human Player") {
			public GamePlayer createPlayer(String name) {
				return new ChessHumanPlayer(name);
			}});
		
		// a computer player type (player type 1)
		playerTypes.add(new GamePlayerType("Computer Player") {
			public GamePlayer createPlayer(String name) {
				//0 means it is not smart
				return new ChessComputerPlayer1(name,0);
			}});
		
		// a computer player type (player type 2)
		playerTypes.add(new GamePlayerType("Computer Player (GUI)") {
			public GamePlayer createPlayer(String name) {
				return new ChessComputerPlayer2(name,0);
			}});
		
		playerTypes.add(new GamePlayerType("Computer Player (smart)") {
			public GamePlayer createPlayer(String name) {
				//1 means it is smart
				return new ChessComputerPlayer1(name,1);
			}});
		
		playerTypes.add(new GamePlayerType("Computer Player (smart GUI)") {
			public GamePlayer createPlayer(String name) {
				//1 means it is smart
				return new ChessComputerPlayer1(name,1);
			}});

		// Create a game configuration class for Chess:
		// - player types as given above
		// - from 1 to 2 players
		// - name of game is "Chess"
		// - port number as defined above
		GameConfig defaultConfig = new GameConfig(playerTypes, 2, 2, "Chess",
				PORT_NUMBER);

		// Add the default players to the configuration
		defaultConfig.addPlayer("Human", 0); // player 1: a human player
		defaultConfig.addPlayer("Computer", 1); // player 2: a computer player
		defaultConfig.addPlayer("Computer", 2); // player 2: a computer player
		defaultConfig.addPlayer("Computer", 3); // player 2: a computer player
		defaultConfig.addPlayer("Computer", 4); // player 2: a computer player
		
		// Set the default remote-player setup:
		// - player name: "Remote Player"
		// - IP code: (empty string)
		// - default player type: human player
		defaultConfig.setRemoteData("Remote Player", "", 0);
		
		// return the configuration
		return defaultConfig;
	}//createDefaultConfig

	/**
	 * create a local game
	 * 
	 * @return
	 * 		the local game, a counter game
	 */
	@Override
	public LocalGame createLocalGame() {
		return new ChessLocalGame();
	}

}
