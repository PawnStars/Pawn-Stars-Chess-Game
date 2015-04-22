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
		boolean[][] stateMoves1 = state.getPawnMoves(0, 7, pawn,false);
		boolean[][] stateMoves2 = state.getPawnMoves(2, 7, pawn2,false);
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
				assertTrue("Moves 1 X:"+j+"Y:"+i,stateMoves1[i][j] == moves1[i][j]);
				assertTrue("Moves 2 X:"+j+"Y:"+i,stateMoves2[i][j] == moves2[i][j]);
			}
		}
	}
	
	/**
	 * Test that rooks can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testRooks() throws Throwable {
		//White rooks
		ChessPiece rook = new ChessPiece(ChessPiece.ROOK,true);
		ChessPiece rook2 = new ChessPiece(rook);
		ChessPiece rook3 = new ChessPiece(rook);
		ChessPiece rook4 = new ChessPiece(rook);
		
		//Black rooks
		ChessPiece rook5 = new ChessPiece(ChessPiece.ROOK,false);
		ChessPiece rook6 = new ChessPiece(rook5);
		ChessPiece[][] map = {
				{null,rook4,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,rook,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{rook5,rook2,null,null,null,null,rook6,null},
				{null,rook3,null,null,null,null,null,null},
		};
		ChessGameState state = new ChessGameState(true);
		state.setPieceMap(map);
		boolean[][] stateMoves1 = state.getRookMoves(3, 3, rook,false);
		boolean[][] stateMoves2 = state.getRookMoves(1, 6, rook2,false);
		
		boolean[][] moves1 = {
				{false,false,false,true ,false,false,false,false},
				{false,false,false,true ,false,false,false,false},
				{false,false,false,true ,false,false,false,false},
				{true ,true ,true ,false,true ,true ,true ,true },
				{false,false,false,true ,false,false,false,false},
				{false,false,false,true ,false,false,false,false},
				{false,false,false,true ,false,false,false,false},
				{false,false,false,true ,false,false,false,false},
		};
		
		boolean[][] moves2 = {
				{false,false,false,false,false,false,false,false},
				{false,true ,false,false,false,false,false,false},
				{false,true ,false,false,false,false,false,false},
				{false,true ,false,false,false,false,false,false},
				{false,true ,false,false,false,false,false,false},
				{false,true ,false,false,false,false,false,false},
				{true ,false,true ,true ,true ,true ,true ,false},
				{false,false,false,false,false,false,false,false},
		};
		
		for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
		{
			for(int j=0;j<ChessGameState.BOARD_HEIGHT;j++)
			{
				assertTrue("Moves 1 X:"+j+"Y:"+i,stateMoves1[i][j] == moves1[i][j]);
				assertTrue("Moves 2 X:"+j+"Y:"+i,stateMoves2[i][j] == moves2[i][j]);
			}
		}
	}
	
	/**
	 * Test that knights can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testKnights() throws Throwable {
		//White knights
		ChessPiece knight = new ChessPiece(ChessPiece.KNIGHT,true);
		ChessPiece knight2 = new ChessPiece(knight);
		ChessPiece knight3 = new ChessPiece(knight);
		//Black knights
		ChessPiece knight4 = new ChessPiece(ChessPiece.KNIGHT,false);
		ChessPiece[][] map = {
				{null   ,null   ,null   ,null  ,null,null,null,null},
				{null   ,null   ,null   ,null  ,null,null,null,null},
				{null   ,null   ,null   ,null  ,null,null,null,null},
				{null   ,null   ,null   ,knight,null,null,null,null},
				{null   ,null   ,null   ,null  ,null,null,null,null},
				{null   ,knight4,null   ,null  ,null,null,null,null},
				{null   ,null   ,knight3,null  ,null,null,null,null},
				{knight2,null   ,null   ,null  ,null,null,null,null},
		};
		ChessGameState state = new ChessGameState(true);
		state.setPieceMap(map);
		boolean[][] stateMoves1 = state.getKnightMoves(3, 3, knight,false);
		boolean[][] stateMoves2 = state.getKnightMoves(0, 7, knight2,false);
		
		boolean[][] moves1 = {
				{false,false,false,false,false,false,false,false},
				{false,false,true ,false,true ,false,false,false},
				{false,true ,false,false,false,true ,false,false},
				{false,false,false,false,false,false,false,false},
				{false,true ,false,false,false,true ,false,false},
				{false,false,true ,false,true ,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
		};
		
		boolean[][] moves2 = {
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,true ,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
		};
		
		for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
		{
			for(int j=0;j<ChessGameState.BOARD_HEIGHT;j++)
			{
				assertTrue("Moves 1 X:"+j+"Y:"+i,stateMoves1[i][j] == moves1[i][j]);
				assertTrue("Moves 2 X:"+j+"Y:"+i,stateMoves2[i][j] == moves2[i][j]);
			}
		}
	}
	
	/**
	 * Test that bishops can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testBishops() throws Throwable {
		ChessPiece bish = new ChessPiece(ChessPiece.BISHOP,true);
		ChessPiece bish2 = new ChessPiece(bish);
		ChessPiece bish3 = new ChessPiece(bish);
		ChessPiece bish4 = new ChessPiece(ChessPiece.BISHOP,false);
		ChessPiece[][] map = {
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,bish,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,bish4,null,bish3,null,null,null,null},
				{null,null,bish2,null,null,null,null,null},
		};
		ChessGameState state = new ChessGameState(true);
		state.setPieceMap(map);
		boolean[][] stateMoves1 = state.getBishopMoves(3, 3, bish,false);
		boolean[][] stateMoves2 = state.getBishopMoves(2, 7, bish2,false);
		
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
				{false,true ,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
		};
		
		for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
		{
			for(int j=0;j<ChessGameState.BOARD_HEIGHT;j++)
			{
				assertTrue("Moves 1 X:"+j+"Y:"+i,stateMoves1[i][j] == moves1[i][j]);
				assertTrue("Moves 2 X:"+j+"Y:"+i,stateMoves2[i][j] == moves2[i][j]);
			}
		}
	}
	
	/**
	 * Test that the king can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testKing() throws Throwable {
		ChessPiece king = new ChessPiece(ChessPiece.KING,true);
		ChessPiece king2 = new ChessPiece(king);
		ChessPiece king3 = new ChessPiece(king);
		
		ChessPiece king4 = new ChessPiece(ChessPiece.KING,false);
		ChessPiece[][] map = {
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,king,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,null,null,null,null,null,null,null},
				{null,king3,null,null,null,null,null,null},
				{king2,king4,null,null,null,null,null,null},
		};
		ChessGameState state = new ChessGameState(true);
		state.setPieceMap(map);
		boolean[][] stateMoves1 = state.getKingMoves(3, 3, king,false);
		boolean[][] stateMoves2 = state.getKingMoves(0, 7, king2,false);
		
		boolean[][] moves1 = {
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,true ,true ,true ,false,false,false},
				{false,false,true ,false,true ,false,false,false},
				{false,false,true ,true ,true ,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
		};
		
		boolean[][] moves2 = {
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{false,false,false,false,false,false,false,false},
				{true ,false,false,false,false,false,false,false},
				{false,true ,false,false,false,false,false,false},
		};
		
		for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
		{
			for(int j=0;j<ChessGameState.BOARD_HEIGHT;j++)
			{
				assertTrue("Moves 1 X:"+j+"Y:"+i,stateMoves1[i][j] == moves1[i][j]);
				assertTrue("Moves 2 X:"+j+"Y:"+i,stateMoves2[i][j] == moves2[i][j]);
			}
		}
	}
	
	/**
	 * Test that the queen can move correctly under different circumstances
	 * @throws Throwable
	 */
	public void testQueen() throws Throwable {
		//White queens
		ChessPiece queen = new ChessPiece(ChessPiece.QUEEN,true);
		ChessPiece queen2 = new ChessPiece(queen);
		ChessPiece queen3 = new ChessPiece(queen);
		
		//Black queens
		ChessPiece queen5 = new ChessPiece(ChessPiece.QUEEN,false);
		ChessPiece[][] map = {
				{null,null,null,null ,null,null  ,null  ,null},
				{null,null,null,null ,null,null  ,queen2,null},
				{null,null,null,null ,null,queen5,queen3,null},
				{null,null,null,queen,null,null  ,null  ,null},
				{null,null,null,null ,null,null  ,null  ,null},
				{null,null,null,null ,null,null  ,null  ,null},
				{null,null,null,null ,null,null  ,null  ,null},
				{null,null,null,null ,null,null  ,null  ,null},
		};
		ChessGameState state = new ChessGameState(true);
		state.setPieceMap(map);
		boolean[][] stateMoves1 = state.getQueenMoves(3, 3, queen,false);
		boolean[][] stateMoves2 = state.getQueenMoves(6, 1, queen2,false);
		
		boolean[][] moves1 = {
				{true ,false,false,true ,false,false,true ,false},
				{false,true ,false,true ,false,true ,false,false},
				{false,false,true ,true ,true ,false,false,false},
				{true ,true ,true ,false,true ,true ,true ,true },
				{false,false,true ,true ,true ,false,false,false},
				{false,true ,false,true ,false,true ,false,false},
				{true ,false,false,true ,false,false,true ,false},
				{false,false,false,true ,false,false,false,true },
		};
		
		boolean[][] moves2 = {
				{false,false,false,false,false,true ,true ,true },
				{true ,true ,true ,true ,true ,true ,false,true },
				{false,false,false,false,false,true ,false,true },
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
				assertTrue("Moves 1 X:"+j+"Y:"+i,stateMoves1[i][j] == moves1[i][j]);
				assertTrue("Moves 2 X:"+j+"Y:"+i,stateMoves2[i][j] == moves2[i][j]);
			}
		}
	}
	
	public void testCastling()
	{
		//TODO implement method
	}
	
	public void testEnPassant()
	{
		//TODO implement method
	}
	
	/**
	 * Test that the smart AI can beat the dumb AI
	 * @throws Throwable
	 */
	public void testAI() throws Throwable {
		//TODO fix crashes
		//Make a dummy game
		ChessLocalGame game = new ChessLocalGame();
		ChessGameState state = new ChessGameState(true);
		
		//Initialize players
		ChessComputerPlayer1 smartPlayer = new ChessComputerPlayer1("Hawking",
				ChessComputerPlayer1.TAKE_PIECES);
		ChessComputerPlayer1 dumbPlayer = new ChessComputerPlayer1("Peter",
				ChessComputerPlayer1.RANDOM);
		
		game.start(new ChessPlayer[]{smartPlayer,dumbPlayer});
		
		smartPlayer.sendInfo(new BindGameInfo(game, 0));
		dumbPlayer.sendInfo(new BindGameInfo(game, 1));
		
		smartPlayer.sendInfo(state);
		dumbPlayer.sendInfo(state);
		//Make the players make moves until someone wins
		while(!state.isGameOver())
		{
			state.applyMove(smartPlayer.makeMove());
			state.applyMove(dumbPlayer.makeMove());
		}
		
		//Make sure the smart player wins
		assertTrue("",state.isPlayer1Won());
	}
	
	
}