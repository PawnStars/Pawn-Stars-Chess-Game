package edu.up.cs301.chess.engine;

import edu.up.cs301.chess.ChessGameState;
import edu.up.cs301.chess.ChessPiece;
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
	private static final int pawnVal = 92;
	private static final int rookVal = 593;
	private static final int bishopVal = 385;
	private static final int knightVal = 385;
	private static final int queenVal = 1244;
	private static final int kingVal = 9900;
	
	private static final int worth[] = {pawnVal,rookVal,bishopVal,knightVal,queenVal,kingVal};
	
	// Exact piece table values from Stockfish AI
	/** Piece/square table for king during middle game. */
	private static final int[][] kingMidTbP1 = 
	{
		{-22,-35,-40,-40,-40,-40,-35,-22},
        {-22,-35,-40,-40,-40,-40,-35,-22},
        {-25,-35,-40,-45,-45,-40,-35,-25},
        {-15,-30,-35,-40,-40,-35,-30,-15},
        {-10,-15,-20,-25,-25,-20,-15,-10},
        {  4, -2, -5,-15,-15, -5, -2,  4},
        { 16, 14,  7, -3, -3,  7, 14, 16},
        { 24, 24,  9,  0,  0,  9, 24, 24}
    };

    /** Piece/square table for king during end game. */
	private static final int[][] kingEndTbP1 = {  
		{ 0,  8, 16, 24, 24, 16,  8,  0},
		{ 8, 16, 24, 32, 32, 24, 16,  8},
		{16, 24, 32, 40, 40, 32, 24, 16},
		{24, 32, 40, 48, 48, 40, 32, 24},
		{24, 32, 40, 48, 48, 40, 32, 24},
		{16, 24, 32, 40, 40, 32, 24, 16},
		{ 8, 16, 24, 32, 32, 24, 16,  8},
		{ 0,  8, 16, 24, 24, 16,  8,  0}
    };

    /** Piece/square table for pawns during middle game. */
	private static final int[][] pawnMidTbP1 = {  
		{ 0,  0,  0,  0,  0,  0,  0,  0},
	    { 8, 16, 24, 32, 32, 24, 16,  8},
	    { 3, 12, 20, 28, 28, 20, 12,  3},
	    {-5,  4, 10, 20, 20, 10,  4, -5},
	    {-6,  4,  5, 16, 16,  5,  4, -6},
	    {-6,  4,  2,  5,  5,  2,  4, -6},
	    {-6,  4,  4,-15,-15,  4,  4, -6},
	    { 0,  0,  0,  0,  0,  0,  0,  0}
	};

    /** Piece/square table for pawns during end game. */
	private static final int[][] pawnEndTbP1 = {   
		{  0,  0,  0,  0,  0,  0,  0,  0},
        { 25, 40, 45, 45, 45, 45, 40, 25},
        { 17, 32, 35, 35, 35, 35, 32, 17},
    	{  5, 24, 24, 24, 24, 24, 24,  5},
        { -9, 11, 11, 11, 11, 11, 11, -9},
        {-17,  3,  3,  3,  3,  3,  3,-17},
        {-20,  0,  0,  0,  0,  0,  0,-20},
    	{  0,  0,  0,  0,  0,  0,  0,  0}
	};

    /** Piece/square table for knights during middle game. */
	private static final int[][] knightMidTbP1 = 
	{
		{-53,-42,-32,-21,-21,-32,-42,-53},
        {-42,-32,-10,  0,  0,-10,-32,-42},
        {-21,  5, 10, 16, 16, 10,  5,-21},
        {-18,  0, 10, 21, 21, 10,  0,-18},
        {-18,  0,  3, 21, 21,  3,  0,-18},
        {-21,-10,  0,  0,  0,  0,-10,-21},
        {-42,-32,-10,  0,  0,-10,-32,-42},
        {-53,-42,-32,-21,-21,-32,-42,-53}
	};

    /** Piece/square table for knights during end game. */
	private static final int[][] knightEndTbP1 = 
	{
		{-56,-44,-34,-22,-22,-34,-44,-56},
        {-44,-34,-10,  0,  0,-10,-34,-44},
        {-22,  5, 10, 17, 17, 10,  5,-22},
        {-19,  0, 10, 22, 22, 10,  0,-19},
        {-19,  0,  3, 22, 22,  3,  0,-19},
        {-22,-10,  0,  0,  0,  0,-10,-22},
        {-44,-34,-10,  0,  0,-10,-34,-44},
        {-56,-44,-34,-22,-22,-34,-44,-56}
	};

    /** Piece/square table for bishops during middle game. */
	private static final int[][] bishopMidTbP1 =
	{  
		{ 0,  0,  0,  0,  0,  0,  0,  0},
        { 0,  4,  2,  2,  2,  2,  4,  0},
        { 0,  2,  4,  4,  4,  4,  2,  0},
        { 0,  2,  4,  4,  4,  4,  2,  0},
        { 0,  2,  4,  4,  4,  4,  2,  0},
        { 0,  3,  4,  4,  4,  4,  3,  0},
        { 0,  4,  2,  2,  2,  2,  4,  0},
        {-5, -5, -7, -5, -5, -7, -5, -5} 
	};

    /** Piece/square table for bishops during end game. */
	private static final int[][] bishopEndTbP1 = 
	{  
		{0,  0,  0,  0,  0,  0,  0,  0},
	    {0,  2,  2,  2,  2,  2,  2,  0},
	    {0,  2,  4,  4,  4,  4,  2,  0},
	    {0,  2,  4,  4,  4,  4,  2,  0},
	    {0,  2,  4,  4,  4,  4,  2,  0},
	    {0,  2,  4,  4,  4,  4,  2,  0},
	    {0,  2,  2,  2,  2,  2,  2,  0},
	    {0,  0,  0,  0,  0,  0,  0,  0}
	};

    /** Piece/square table for queens during middle game. */
	private static final int[][] queenMidTbP1 = 
	{
		{-10, -5,  0,  0,  0,  0, -5,-10},
		{ -5,  0,  5,  5,  5,  5,  0, -5},
		{  0,  5,  5,  6,  6,  5,  5,  0},
		{  0,  5,  6,  6,  6,  6,  5,  0},
		{  0,  5,  6,  6,  6,  6,  5,  0},
		{  0,  5,  5,  6,  6,  5,  5,  0},
		{ -5,  0,  5,  5,  5,  5,  0, -5},
		{-10, -5,  0,  0,  0,  0, -5,-10} 
	};

    /** Piece/square table for rooks during middle game. */
	private static final int[][] rookMidTbP1 = 
	{  
		{ 8, 11, 13, 13, 13, 13, 11,  8},
		{22, 27, 27, 27, 27, 27, 27, 22},
		{ 0,  0,  0,  0,  0,  0,  0,  0},
		{ 0,  0,  0,  0,  0,  0,  0,  0},
		{-2,  0,  0,  0,  0,  0,  0, -2},
		{-2,  0,  0,  2,  2,  0,  0, -2},
		{-3,  2,  5,  5,  5,  5,  2, -3},
		{ 0,  3,  5,  5,  5,  5,  3,  0} 
	};
	
	private static final int[][][] midGameTableP1 = {pawnMidTbP1,rookMidTbP1,bishopMidTbP1,knightMidTbP1,queenMidTbP1,kingMidTbP1};
	private static final int[][][] endGameTableP1 = {pawnEndTbP1,rookMidTbP1,bishopEndTbP1,knightEndTbP1,queenMidTbP1,kingEndTbP1};
	
	//copies of player 1's tables reversed
	private static final int[][][] midGameTableP2 = new int[ChessPiece.NUM_TYPES][][];
	private static final int[][][] endGameTableP2 = new int[ChessPiece.NUM_TYPES][][];
	
	// make a reverse copy of this 3d array for player 2
	static
	{
		for(int i=0;i<ChessPiece.NUM_TYPES;i++)
		{
			for(int j=0;j<ChessGameState.BOARD_HEIGHT;j++)
			{
				for(int k=0;k<ChessGameState.BOARD_WIDTH;k++)
				{
					midGameTableP2[i][j][j] = 
						midGameTableP1[i][ChessGameState.BOARD_HEIGHT-1-j][ChessGameState.BOARD_WIDTH-1-k];
					
					endGameTableP2[i][j][j] = 
							endGameTableP1[i][ChessGameState.BOARD_HEIGHT-1-j][ChessGameState.BOARD_WIDTH-1-k];
				}
			}
		}
	}
	final static int midGamePawnMat = queenVal + 2*rookVal + 2*bishopVal;
	//end game/mid game doesn't matter much for rooks
	final static int midGameKnightMat = queenVal + 2*rookVal + bishopVal + knightVal + 6*pawnVal;
	//end game/mid game doesn't matter much for bishops
	//end game/mid game doesn't matter much for queens
	final static int midGameKingMat = queenVal + 2*rookVal + 2*bishopVal;
	
	final static int[] midGameMaterial = {midGamePawnMat,0,midGameKnightMat,0,0,midGameKingMat};
	//final int t1 = qV + 2 * rV + 2 * bV;//higher limit
    //final int t2 = rV;//lower limit
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
		
		int score = 0;
		//Compute worth of each alive piece
		
		int p1Material = 0;
		int p2Material = 0;
		
		int p1MaterialPawns = 0;
		int p2MaterialPawns = 0;
		
		for(ChessPiece p: state.getPlayer1Pieces())
		{
			if(p.isAlive())
			{
				int type = p.getType();
				p1Material += worth[type];
				if(type == ChessPiece.PAWN)
				{
					p1MaterialPawns += worth[type];
				}
			}
		}
		for(ChessPiece p: state.getPlayer2Pieces())
		{
			if(p.isAlive())
			{
				int type = p.getType();
				p2Material += worth[type];
				if(type == ChessPiece.PAWN)
				{
					p2MaterialPawns += worth[type];
				}
			}
		}
		state.setPlayer1Material(p1Material);
		state.setPlayer2Material(p2Material);
		
		for(ChessPiece p: state.getPlayer1Pieces())
		{
			if(p.isAlive())
			{
				int type = p.getType();
				int[] loc = p.getLocation();
				
				int score1 = midGameTableP1[type][loc[0]][loc[1]];
				int score2 = endGameTableP1[type][loc[0]][loc[1]];
				//TODO finish
				//if()
				
				
			}
		}
		for(ChessPiece p: state.getPlayer2Pieces())
		{
			if(p.isAlive())
			{
				int type = p.getType();
				int[] loc = p.getLocation();
				//TODO finish this side
			}
		}
		
		score = score + p1Material - p2Material;
		
		
		return score;
	}
	
	/**
     * Interpolate between (x1,y1) and (x2,y2).
     * If x < x1, return y1, if x > x2 return y2.
     * Otherwise, use linear interpolation.
     * 
     * Used to get a score from a piece table when
     * it is not clear if it is end game or mid game.
     */
    static final int interpolate(int x, int x1, int y1, int x2, int y2) {
        if (x > x2) {
            return y2;
        } else if (x < x1) {
            return y1;
        } else {
            return (x - x1) * (y2 - y1) / (x2 - x1) + y1;
        }
    }
}
