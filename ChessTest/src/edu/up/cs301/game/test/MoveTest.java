 package edu.up.cs301.game.test;

import edu.up.cs301.chess.*;
import edu.up.cs301.game.infoMsg.BindGameInfo;
import android.test.AndroidTestCase;


public class MoveTest extends AndroidTestCase {

	/**
	 * Test that pawns can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testPawns() throws Throwable {
		ChessPiece pawn = new ChessPiece(ChessPiece.PAWN,true);
		ChessPiece pawn2 = new ChessPiece(pawn);
		ChessPiece pawn3 = new ChessPiece(ChessPiece.PAWN,false);
		ChessPiece pawn4 = new ChessPiece(pawn3);
		ChessPiece[][] map = {
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,pawn3,null,null,null,null,null},
				{null,null,null,pawn4,null,null,null,null},
				{pawn,null,pawn2,null,null,null,null,null},
		};
		ChessGameState state = new ChessGameState(true);
		state.setPieceMap(map);
		boolean[][] stateMoves1 = state.getPawnMoves(0, 7, pawn);
		boolean[][] stateMoves2 = state.getPawnMoves(2, 7, pawn2);
		boolean[][] moves1 = {
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{true ,false,false,false,false,false,false,false},
				{true ,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
		};
		boolean[][] moves2 = {
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,true ,true ,false,false,false,false},
				{false,false,false,false,false,false,false,false},
		};
		for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
		{
			for(int j=0;j<ChessGameState.BOARD_HEIGHT;j++)
			{
				assertTrue(stateMoves1[i][j] == moves1[i][j]);
				assertTrue(stateMoves2[i][j] == moves2[i][j]);
			}
		}
	}
	
	/**
	 * Test that rooks can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testRooks() throws Throwable {
		ChessPiece rook = new ChessPiece(ChessPiece.ROOK,true);
		ChessPiece rook2 = new ChessPiece(rook);
		ChessPiece rook3 = new ChessPiece(ChessPiece.ROOK,false);
		ChessPiece rook4 = new ChessPiece(rook3);
		ChessPiece[][] map = {
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,rook,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,rook3,null,null,rook4,null,null},
				{null,null,null,null,null,null,null,null},
				{rook2,null,null,null,null,null,null,null},
		};
		ChessGameState state = new ChessGameState(true);
		state.setPieceMap(map);
		boolean[][] stateMoves1 = state.getRookMoves(3, 3, rook);
		boolean[][] stateMoves2 = state.getRookMoves(0, 7, rook2);
		
		boolean[][] moves1 = {
				{true ,false,false,false,false,false,true ,false},
				{false,true ,false,false,false,true ,false,false},
				{false,false,true ,false,true ,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,true ,false,true ,false,false,false},
				{false,true ,false,false,false,true ,false,false},
				{true ,false,false,false,false,false,true ,false},
				{false,false,false,false,false,false,false,true },
		};
		
		boolean[][] moves2 = {
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
		};
		
		for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
		{
			for(int j=0;j<ChessGameState.BOARD_HEIGHT;j++)
			{
				assertTrue(stateMoves1[i][j] == moves1[i][j]);
				assertTrue(stateMoves2[i][j] == moves2[i][j]);
			}
		}
	}
	
	/**
	 * Test that knights can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testKnights() throws Throwable {
		//TODO implement
	}
	
	/**
	 * Test that bishops can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testBishops() throws Throwable {
		//TODO implement
	}
	
	/**
	 * Test that the king can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testKing() throws Throwable {
		//TODO implement
	}
	
	/**
	 * Test that the queen can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testQueen() throws Throwable {
		//TODO implement
	}
	
	/**
	 * Test that the smart AI can beat the dumb AI
	 * @throws Throwable
	 */
	public void testAI() throws Throwable {
		
		//Make a dummy game
		ChessLocalGame game = new ChessLocalGame();
		ChessGameState state = new ChessGameState(true);
		
		//Initialize players
		ChessComputerPlayer1 smartPlayer = new ChessComputerPlayer1("Hawking",
				ChessComputerPlayer1.TAKE_PIECES);
		ChessComputerPlayer1 dumbPlayer = new ChessComputerPlayer1("Peter",
				ChessComputerPlayer1.RANDOM);
		game.start(new ChessPlayer[]{smartPlayer,dumbPlayer});
		smartPlayer.start();
		dumbPlayer.start();
		smartPlayer.sendInfo(new BindGameInfo(game, 0));
		smartPlayer.sendInfo(new BindGameInfo(game, 1));
		smartPlayer.sendInfo(state);
		dumbPlayer.sendInfo(state);
		//Make the players make moves until someone wins
		while(!state.isGameOver())
		{
			state.applyMove(smartPlayer.makeMove());
			state.applyMove(dumbPlayer.makeMove());
		}
		
		//Make sure the smart player wins
		assertTrue(state.isPlayer1Won());
	}
	
	
}