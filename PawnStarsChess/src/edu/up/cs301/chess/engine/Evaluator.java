package edu.up.cs301.chess.engine;

import edu.up.cs301.chess.ChessGameState;
import edu.up.cs301.chess.actions.ChessMoveAction;

/**
 * This evaluates a ChessGameState and calculates its score
 * to be used internally and/or externally
 * .
 * @author Allison Liedtke
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @version March 2015
 *
 */
public class Evaluator {
	//TODO replace with 
	private final int pawnVal = 92;
	private final int rookVal = 593;
	private final int bishopVal = 385;
	private final int knightVal = 385;
	private final int queenVal = 1244;
	private final int kingVal = 9900;
	
	private final int worth[] = {kingVal,queenVal,rookVal,bishopVal,knightVal,pawnVal};
	
	// Exact piece table values from Stockfish AI
	/** Piece/square table for king during middle game. */
	private static final int[] kt1b = { -22,-35,-40,-40,-40,-40,-35,-22,
		                                -22,-35,-40,-40,-40,-40,-35,-22,
		                                -25,-35,-40,-45,-45,-40,-35,-25,
		                                -15,-30,-35,-40,-40,-35,-30,-15,
		                                -10,-15,-20,-25,-25,-20,-15,-10,
		                                  4, -2, -5,-15,-15, -5, -2,  4,
		                                 16, 14,  7, -3, -3,  7, 14, 16,
		                                 24, 24,  9,  0,  0,  9, 24, 24 };

    /** Piece/square table for king during end game. */
	private static final int[] kt2b = {  0,  8, 16, 24, 24, 16,  8,  0,
		                                 8, 16, 24, 32, 32, 24, 16,  8,
		                                16, 24, 32, 40, 40, 32, 24, 16,
		                                24, 32, 40, 48, 48, 40, 32, 24,
		                                24, 32, 40, 48, 48, 40, 32, 24,
		                                16, 24, 32, 40, 40, 32, 24, 16,
		                                 8, 16, 24, 32, 32, 24, 16,  8,
		                                 0,  8, 16, 24, 24, 16,  8,  0 };

    /** Piece/square table for pawns during middle game. */
	private static final int[] pt1b = {  0,  0,  0,  0,  0,  0,  0,  0,
		                                 8, 16, 24, 32, 32, 24, 16,  8,
		                                 3, 12, 20, 28, 28, 20, 12,  3,
		                                -5,  4, 10, 20, 20, 10,  4, -5,
		                                -6,  4,  5, 16, 16,  5,  4, -6,
		                                -6,  4,  2,  5,  5,  2,  4, -6,
		                                -6,  4,  4,-15,-15,  4,  4, -6,
		                                 0,  0,  0,  0,  0,  0,  0,  0 };

    /** Piece/square table for pawns during end game. */
	private static final int[] pt2b = {   0,  0,  0,  0,  0,  0,  0,  0,
		                                 25, 40, 45, 45, 45, 45, 40, 25,
		                                 17, 32, 35, 35, 35, 35, 32, 17,
		                                  5, 24, 24, 24, 24, 24, 24,  5,
		                                 -9, 11, 11, 11, 11, 11, 11, -9,
		                                -17,  3,  3,  3,  3,  3,  3,-17,
		                                -20,  0,  0,  0,  0,  0,  0,-20,
		                                  0,  0,  0,  0,  0,  0,  0,  0 };

    /** Piece/square table for knights during middle game. */
	private static final int[] nt1b = { -53,-42,-32,-21,-21,-32,-42,-53,
		                                -42,-32,-10,  0,  0,-10,-32,-42,
		                                -21,  5, 10, 16, 16, 10,  5,-21,
		                                -18,  0, 10, 21, 21, 10,  0,-18,
		                                -18,  0,  3, 21, 21,  3,  0,-18,
		                                -21,-10,  0,  0,  0,  0,-10,-21,
		                                -42,-32,-10,  0,  0,-10,-32,-42,
		                                -53,-42,-32,-21,-21,-32,-42,-53 };

    /** Piece/square table for knights during end game. */
	private static final int[] nt2b = { -56,-44,-34,-22,-22,-34,-44,-56,
		                                -44,-34,-10,  0,  0,-10,-34,-44,
		                                -22,  5, 10, 17, 17, 10,  5,-22,
		                                -19,  0, 10, 22, 22, 10,  0,-19,
		                                -19,  0,  3, 22, 22,  3,  0,-19,
		                                -22,-10,  0,  0,  0,  0,-10,-22,
		                                -44,-34,-10,  0,  0,-10,-34,-44,
		                                -56,-44,-34,-22,-22,-34,-44,-56 };

    /** Piece/square table for bishops during middle game. */
	private static final int[] bt1b = {  0,  0,  0,  0,  0,  0,  0,  0,
		                                 0,  4,  2,  2,  2,  2,  4,  0,
		                                 0,  2,  4,  4,  4,  4,  2,  0,
		                                 0,  2,  4,  4,  4,  4,  2,  0,
		                                 0,  2,  4,  4,  4,  4,  2,  0,
		                                 0,  3,  4,  4,  4,  4,  3,  0,
		                                 0,  4,  2,  2,  2,  2,  4,  0,
		                                -5, -5, -7, -5, -5, -7, -5, -5 };

    /** Piece/square table for bishops during middle game. */
	private static final int[] bt2b = {  0,  0,  0,  0,  0,  0,  0,  0,
		                                 0,  2,  2,  2,  2,  2,  2,  0,
		                                 0,  2,  4,  4,  4,  4,  2,  0,
		                                 0,  2,  4,  4,  4,  4,  2,  0,
		                                 0,  2,  4,  4,  4,  4,  2,  0,
		                                 0,  2,  4,  4,  4,  4,  2,  0,
		                                 0,  2,  2,  2,  2,  2,  2,  0,
		                                 0,  0,  0,  0,  0,  0,  0,  0 };

    /** Piece/square table for queens during middle game. */
	private static final int[] qt1b = { -10, -5,  0,  0,  0,  0, -5,-10,
		                                 -5,  0,  5,  5,  5,  5,  0, -5,
		                                  0,  5,  5,  6,  6,  5,  5,  0,
		                                  0,  5,  6,  6,  6,  6,  5,  0,
		                                  0,  5,  6,  6,  6,  6,  5,  0,
		                                  0,  5,  5,  6,  6,  5,  5,  0,
		                                 -5,  0,  5,  5,  5,  5,  0, -5,
		                                -10, -5,  0,  0,  0,  0, -5,-10 };

    /** Piece/square table for rooks during middle game. */
	private static final int[] rt1b = {  8, 11, 13, 13, 13, 13, 11,  8,
		                                22, 27, 27, 27, 27, 27, 27, 22,
		                                 0,  0,  0,  0,  0,  0,  0,  0,
		                                 0,  0,  0,  0,  0,  0,  0,  0,
		                                -2,  0,  0,  0,  0,  0,  0, -2,
		                                -2,  0,  0,  2,  2,  0,  0, -2,
		                                -3,  2,  5,  5,  5,  5,  2, -3,
		                                 0,  3,  5,  5,  5,  5,  3,  0 };
	/**
	 * Evaluates a game state and assigns each player a score
	 */
	public static final int evalulate(ChessGameState state)
	{
		int score = 0;
		
		score += pieceSquareEval(state);
        score += pawnBonus(state);
        score += tradeBonus(state);
        score += castleBonus(state);
        score += rookBonus(state);
        score += bishopEval(state, score);
        score += threatBonus(state);
        score += kingSafety(state);
        score = endGameEval(state, score);
        //TODO implement score setting correctly
        state.setPlayer1Points(score);
        state.setPlayer2Points(-score);
        
        return score;
	}

	/**
	 * Inputs any knowledge that would help end game situations
	 * @param state
	 * @param score
	 * @return score
	 */
	private static int endGameEval(ChessGameState state, int score) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Protect the king at all costs
	 * @param state
	 * @return score
	 */
	private static int kingSafety(ChessGameState state) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * See what pieces can be taken and reflect that in the score
	 * @param state
	 * @return score
	 */
	private static int threatBonus(ChessGameState state) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Add the reach of the bishops into the calculation
	 * @param state
	 * @param score
	 * @return score
	 */
	private static int bishopEval(ChessGameState state, int score) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Add the reach of the rooks into the calculation
	 * @param state
	 * @param score
	 * @return score
	 */
	private static int rookBonus(ChessGameState state) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Add the benefit of being able to castle into the calculation
	 * @param state
	 * @param score
	 * @return score
	 */
	private static int castleBonus(ChessGameState state) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Make trades favorable when you have more pieces
	 * and only trade pawns when you have less
	 * @param state
	 * @param score
	 * @return score
	 */
	private static int tradeBonus(ChessGameState state) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Take pawn islands and formations into account
	 * @param state
	 * @return score
	 */
	private static int pawnBonus(ChessGameState state) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Compute score using piece tables and the worth of each piece
	 * @param state
	 * @return score
	 */
	private static int pieceSquareEval(ChessGameState state) {
		// TODO Auto-generated method stub
		return 0;
	}
}
