package edu.up.cs301.game.test;

import edu.up.cs301.chess.*;
import edu.up.cs301.chess.actions.ChessMoveAction;
import edu.up.cs301.chess.engine.MoveGenerator;
//import edu.up.cs301.pawnStarsChess.ChessPiece;
import junit.framework.Assert;
import android.test.AndroidTestCase;


public class GameStateTest extends AndroidTestCase {

	public void testConstructor() throws Throwable {
		ChessGameState initState = new ChessGameState(true);
		Assert.assertTrue(initState.getPlayer1Pieces().length == ChessGameState.NUM_PIECES);
		Assert.assertTrue(initState.getPlayer2Pieces().length == ChessGameState.NUM_PIECES);

		
		Assert.assertTrue(initState.isWhoseTurn() == initState.isPlayer1IsWhite());
		
		
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
				ChessPiece.QUEEN, ChessPiece.KING, ChessPiece.BISHOP,
				ChessPiece.KNIGHT, ChessPiece.ROOK };
		for(int i=0;i<ChessGameState.BOARD_WIDTH;i++)
		{
			if(layout[ChessGameState.BOARD_HEIGHT-2][i] != null)
			{
				String pieceMsg = "Testing: "+layout[ChessGameState.BOARD_HEIGHT-2][i].toString();
				Assert.assertTrue(pieceMsg,layout[ChessGameState.BOARD_HEIGHT-2][i].getType() == ChessPiece.PAWN);
				Assert.assertTrue(pieceMsg,layout[ChessGameState.BOARD_HEIGHT-2][i].isWhite() == true);
			}
			if(layout[ChessGameState.BOARD_HEIGHT-1][i] != null)
			{
				String pieceMsg = "Testing: "+layout[ChessGameState.BOARD_HEIGHT-1][i].toString();
				Assert.assertTrue(pieceMsg,layout[ChessGameState.BOARD_HEIGHT-1][i].getType() == pieces[i]);
				Assert.assertTrue(pieceMsg,layout[ChessGameState.BOARD_HEIGHT-1][i].isWhite() == true);
			}
			if(layout[0][i] != null)
			{
				String pieceMsg = "Testing: "+layout[0][i].toString();
				Assert.assertTrue(pieceMsg,layout[0][i].isWhite() == false);
				Assert.assertTrue(pieceMsg,layout[0][i].getType() == pieces[i]);
			}
			if(layout[1][i] != null)
			{
				String pieceMsg = "Testing: "+layout[1][i].toString();
				Assert.assertTrue(pieceMsg,layout[1][i].isWhite() == false);
				Assert.assertTrue(pieceMsg,layout[1][i].getType() == ChessPiece.PAWN);
			}
			
		}
		
	}
	
	public void testBoardSize() throws Throwable {
		
		ChessGameState initState = new ChessGameState(true);
		
		ChessPiece[][] layout = initState.getPieceMap();
		
		Assert.assertTrue(layout.length == ChessGameState.BOARD_HEIGHT);
		Assert.assertTrue(layout[0].length == ChessGameState.BOARD_WIDTH);
	}
	
	/**
	 * Moves a piece and tests if it is applied
	 * 
	 * @throws Throwable
	 */
	public void testMovePiece() throws Throwable {
		ChessGameState initState = new ChessGameState(true);
		
		//Get one of the possible moves
		ChessMoveAction[] actions = MoveGenerator.getPossibleMoves(initState, null, true);
		
		int randIndex = (int)(Math.random()*actions.length);
		ChessMoveAction act = actions[randIndex];
		initState.applyMove(act);
		
		//Check that the piece was moved
		for(ChessPiece p:initState.getPlayer1Pieces())
		{
			if(p.equals(act.getWhichPiece()))
			{
				Assert.assertTrue(p.getLocation()[0] == act.getNewPos()[0]);
				Assert.assertTrue(p.getLocation()[1] == act.getNewPos()[1]);
				return;
			}
		}
		
	}
	
}