 package edu.up.cs301.game.test;

import edu.up.cs301.chess.*;
import edu.up.cs301.chess.actions.ChessMoveAction;
import edu.up.cs301.chess.engine.Search;
import junit.framework.Assert;
import android.test.AndroidTestCase;


public class MoveTest extends AndroidTestCase {

	/**
	 * Test that pawns can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testPawns() throws Throwable {
	
	}
	
	/**
	 * Test that rooks can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testRooks() throws Throwable {
	
	}
	
	/**
	 * Test that knights can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testKnights() throws Throwable {
	
	}
	
	/**
	 * Test that bishops can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testBishops() throws Throwable {
	
	}
	
	/**
	 * Test that the king can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testKing() throws Throwable {
	
	}
	
	/**
	 * Test that the queen can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testQueen() throws Throwable {
	
	}
	
	/**
	 * Test that the smart AI can beat the dumb AI
	 * @throws Throwable
	 */
	public void testAI() throws Throwable {
		ChessGameState newState = new ChessGameState(true);
		ChessPlayer player = new ChessComputerPlayer1("Bob", Search.MAX_INTELLIGENCE);
		ChessMoveAction bestMove = Search.findMove(player, newState, Search.MAX_INTELLIGENCE);
		System.out.println(bestMove.toString());
		newState.applyMove(bestMove);
		
	}
	
	
}