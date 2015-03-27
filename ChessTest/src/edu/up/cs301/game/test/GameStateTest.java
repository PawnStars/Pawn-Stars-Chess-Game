package edu.up.cs301.game.test;

import edu.up.cs301.chess.*;
//import edu.up.cs301.pawnStarsChess.ChessPiece;
import junit.framework.Assert;
import android.test.AndroidTestCase;


public class GameStateTest extends AndroidTestCase {

	public void testConstructor() throws Throwable {
		ChessGameState initState = new ChessGameState(true);
		Assert.assertTrue(initState.getPlayer1Pieces().length == ChessGameState.NUM_PIECES);
		Assert.assertTrue(initState.getPlayer2Pieces().length == ChessGameState.NUM_PIECES);
		
		boolean[][] castle = initState.getCanCastle();
		for(int i=0;i<2;i++)
		{
			for(int j=0;j<2;j++)
			{
				Assert.assertTrue(castle[i][j] == true);
			}
		}
		
		Assert.assertTrue(castle.length == 2);
		Assert.assertTrue(castle[0].length == 2);

		
		Assert.assertTrue(initState.isWhoseTurn() == initState.getPlayer1Color());
		
		
		Assert.assertTrue(initState.getPlayer1Points() == 0);
		Assert.assertTrue(initState.getPlayer2Points() == 0);
		
		Assert.assertTrue(initState.isPlayer1InCheck() == false);
		Assert.assertTrue(initState.isPlayer2InCheck() == false);
		Assert.assertTrue(initState.isGameOver() == false);
	}
	
	public void testCopyConstructor() throws Throwable {
		ChessGameState initState = new ChessGameState(true);
		ChessGameState newState = new ChessGameState(initState);
		
		Assert.assertTrue(initState.equals(newState));
		
	}
	
	public void testPiecePlacement() throws Throwable{
		ChessGameState initState = new ChessGameState(true);
		ChessPiece[][] layout = initState.getPieceMap();
		int[] pieces = { ChessPiece.ROOK, ChessPiece.KNIGHT, ChessPiece.BISHOP,
				ChessPiece.KING, ChessPiece.QUEEN, ChessPiece.BISHOP,
				ChessPiece.KNIGHT, ChessPiece.ROOK };
		for(int i=0;i<ChessGameState.BOARD_WIDTH;i++)
		{
			if(layout[ChessGameState.BOARD_HEIGHT-3][i] != null)
			{
				Assert.assertTrue(layout[ChessGameState.BOARD_HEIGHT-3][i].getType() == ChessPiece.PAWN);
				Assert.assertTrue(layout[ChessGameState.BOARD_HEIGHT-3][i].isWhite() == true);
			}
			if(layout[ChessGameState.BOARD_HEIGHT-2][i] != null)
			{
				Assert.assertTrue(layout[ChessGameState.BOARD_HEIGHT-2][i].getType() == pieces[i]);
				Assert.assertTrue(layout[ChessGameState.BOARD_HEIGHT-2][i].isWhite() == true);
			}
			if(layout[0][i] != null)
			{
				Assert.assertTrue(layout[0][i].isWhite() == false);
				Assert.assertTrue(layout[0][i].getType() == pieces[i]);
			}
			if(layout[1][i] != null)
			{
				Assert.assertTrue(layout[1][i].isWhite() == false);
				Assert.assertTrue(layout[1][i].getType() == ChessPiece.PAWN);
			}
			
		}
		
	}
	
	public void testBoardSize() throws Throwable {
		
		ChessGameState initState = new ChessGameState(true);
		
		ChessPiece[][] layout = initState.getPieceMap();
		
		Assert.assertTrue(layout.length == ChessGameState.BOARD_HEIGHT);
		Assert.assertTrue(layout[0].length == ChessGameState.BOARD_WIDTH);
	}
	
	public void testMovePiece() throws Throwable {
		ChessGameState initState = new ChessGameState(true);
		//initState.setBoardFlipped(true);
		//Assert.assertTrue(initState.isBoardFlipped());
	}
	
}