package edu.up.cs301.chess.engine;

import java.util.ArrayList;
import java.util.Arrays;

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
	
	// Exact piece table values/material values from Stockfish AI
	private static final int pawnVal = 92;
	private static final int rookVal = 593;
	private static final int bishopVal = 385;
	private static final int knightVal = 385;
	private static final int queenVal = 1244;
	private static final int kingVal = 9900;
	
	private static final int worth[] = {pawnVal,rookVal,bishopVal,knightVal,queenVal,kingVal};
	
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
	private static final int[][] kingMidTbP2 = new int[ChessGameState.BOARD_HEIGHT][ChessGameState.BOARD_WIDTH];
	private static final int[][] kingEndTbP2 = new int[ChessGameState.BOARD_HEIGHT][ChessGameState.BOARD_WIDTH];
	private static final int[][] pawnMidTbP2 = new int[ChessGameState.BOARD_HEIGHT][ChessGameState.BOARD_WIDTH];
	private static final int[][] pawnEndTbP2 = new int[ChessGameState.BOARD_HEIGHT][ChessGameState.BOARD_WIDTH];
	private static final int[][] knightMidTbP2 = new int[ChessGameState.BOARD_HEIGHT][ChessGameState.BOARD_WIDTH];
	private static final int[][] knightEndTbP2 = new int[ChessGameState.BOARD_HEIGHT][ChessGameState.BOARD_WIDTH];
	private static final int[][] bishopMidTbP2 = new int[ChessGameState.BOARD_HEIGHT][ChessGameState.BOARD_WIDTH];
	private static final int[][] bishopEndTbP2 = new int[ChessGameState.BOARD_HEIGHT][ChessGameState.BOARD_WIDTH];
	private static final int[][] queenMidTbP2 = new int[ChessGameState.BOARD_HEIGHT][ChessGameState.BOARD_WIDTH];
	private static final int[][] rookMidTbP2 = new int[ChessGameState.BOARD_HEIGHT][ChessGameState.BOARD_WIDTH];
	
	private static final int[][][] midGameTableP2 = new int[ChessPiece.NUM_TYPES][][];
	private static final int[][][] endGameTableP2 = new int[ChessPiece.NUM_TYPES][][];
	
	// make a reverse copy of these arrays for player 2
	static
	{
		for(int i=0;i<ChessPiece.NUM_TYPES;i++)
		{
			for(int j=0;j<ChessGameState.BOARD_HEIGHT;j++)
			{
				for(int k=0;k<ChessGameState.BOARD_WIDTH;k++)
				{
					int oppJ = ChessGameState.BOARD_HEIGHT-1-j;
					int oppK = ChessGameState.BOARD_WIDTH-1-k;
					midGameTableP2[i][j][j] = midGameTableP1[i][oppJ][oppK];
					
					endGameTableP2[i][j][j] = endGameTableP1[i][oppJ][oppK];
					if(i == ChessPiece.PAWN)
					{
						pawnMidTbP2[j][k] = pawnMidTbP1[oppJ][oppK];
						pawnEndTbP2[j][k] = pawnEndTbP1[oppJ][oppK];
					}
					else if(i == ChessPiece.ROOK)
					{
						rookMidTbP2[j][k] = rookMidTbP1[oppJ][oppK];
					}
					else if(i == ChessPiece.BISHOP)
					{
						bishopMidTbP2[j][k] = bishopMidTbP1[oppJ][oppK];
						bishopEndTbP2[j][k] = bishopEndTbP1[oppJ][oppK];
					}
					else if(i == ChessPiece.KNIGHT)
					{
						knightMidTbP2[j][k] = knightMidTbP1[oppJ][oppK];
						knightEndTbP2[j][k] = knightEndTbP1[oppJ][oppK];
					}
					else if(i==ChessPiece.QUEEN)
					{
						queenMidTbP2[j][k] = queenMidTbP1[oppJ][oppK];
					}
					else if(i==ChessPiece.KING)
					{
						kingMidTbP2[j][k] = kingMidTbP1[oppJ][oppK];
						kingEndTbP2[j][k] = kingEndTbP1[oppJ][oppK];
					}
				}
			}
		}
	}
	
	/*
	 * The mid game tables can be used for this piece when
	 * the player has this much material on the board.
	 * 
	 * Some pieces don't have values because their
	 * presence indicates it is not end game. They
	 * get a special calculation.
	 */
	final static int midGamePawnMat = queenVal + 2*rookVal + 2*bishopVal;
	final static int rookCalc = -1;
	final static int midGameKnightMat = queenVal + 2*rookVal + bishopVal + knightVal + 6*pawnVal;
	final static int bishopCalc = 0;
	final static int queenCalc = 0;
	final static int midGameUpperKingMat = queenVal + 2*rookVal + 2*bishopVal;
	
	final static int[] midGameMaterial = {
		midGamePawnMat,
		rookCalc,
		midGameKnightMat,
		bishopCalc,
		queenCalc,
		midGameUpperKingMat
	};
	
	
	final static int endGamePawnMat = rookVal;
	//It is not end game if there is a rook on the board.
	final static int endGameKnightMat = knightVal + 8*pawnVal;
	//It is not end game if there is a bishop on the board.
	//It is not end game if there is a queen on the board.
	final static int endGameKingMat = rookVal;
	
	final static int[] endGameMaterial = {
		endGamePawnMat,
		rookCalc,
		endGameKnightMat,
		bishopCalc,
		queenCalc,
		endGameKingMat
	};
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
	 * Add special consideration for bishops
	 * @param state
	 * @param score
	 * @return score
	 */
	private static int bishopEval(ChessGameState state, int oldScore) {
		
		int score = 0;
		
		//Calculate the number of bishops each player has
		int numP1Bishops = 0;
		int numP2Bishops = 0;
		
		//could have more than 2 bishops due to promotion
		ChessPiece[] p1Bishops = new ChessPiece[ChessGameState.NUM_PIECES];
		ChessPiece[] p2Bishops = new ChessPiece[ChessGameState.NUM_PIECES];
		
		boolean p1Queen = false;
		boolean p2Queen = false;
		
		for(ChessPiece piece: state.getPlayer1Pieces())
		{
			if(piece.isAlive())
			{
				if(piece.getType() == ChessPiece.BISHOP)
				{
					p1Bishops[numP1Bishops] = piece;
					numP1Bishops++;
				}
				if(piece.getType() == ChessPiece.QUEEN)
				{
					p1Queen = true;
				}
			}
		}
		for(ChessPiece piece: state.getPlayer2Pieces())
		{
			if(piece.isAlive())
			{
				if(piece.getType() == ChessPiece.BISHOP)
				{
					p2Bishops[numP2Bishops] = piece;
					numP2Bishops++;
				}
				if(piece.getType() == ChessPiece.QUEEN)
				{
					p2Queen = true;
				}
			}
		}
		
		//Do nothing if there are no more bishops
		if(numP1Bishops == 0 && numP2Bishops == 0)
		{
			return 0;
		}
		
		//bonus for both bishops being alive
		if (numP1Bishops == 2)
		{
			int numPawns = state.getPlayer1PawnMaterial()/pawnVal;
            score += 28 + (8 - numPawns) * 3;
        }
		if (numP2Bishops == 2)
		{
			int numPawns = state.getPlayer2PawnMaterial()/pawnVal;
            score -= 28 + (8 - numPawns) * 3;
        }
		
		//Penalty for being equal. We want to win, not draw.
		if(numP1Bishops == 1 && numP2Bishops == 1 )
		{
			int[] locP1Bishop = p1Bishops[0].getLocation();
			int[] locP2Bishop = p2Bishops[0].getLocation();
			
			boolean p1BishopLightSq = (locP1Bishop[0]%2 == locP1Bishop[1]%2);
			boolean p2BishopLightSq = (locP2Bishop[0]%2 == locP2Bishop[1]%2);
			if(p1BishopLightSq == p2BishopLightSq)
			{
				int p1ValMat = state.getPlayer1Material() - state.getPlayer1PawnMaterial();
				int p2ValMat = state.getPlayer2Material() - state.getPlayer2PawnMaterial();
				if(p1ValMat == p2ValMat)
				{
					int penalty = (oldScore + score) / 2;
		            int loMtrl = 2 * bishopVal;
		            int hiMtrl = 2 * (queenVal + rookVal + bishopVal);
		            int totalMat = p1ValMat+p2ValMat;
		            score -= interpolate(totalMat, loMtrl, penalty, hiMtrl, 0);
				}
			}
		}
		
		//special cases of having a trapped bishop
		ChessPiece[][] board = state.getPieceMap();
		
		//bishop at a7
		boolean bishopColor = board[1][0].isWhite();
		if(board[1][0] != null && board[1][0].getType() == ChessPiece.BISHOP)
		{
			//black pawn at b6
			if(board[2][1].getType() == ChessPiece.PAWN && board[2][1].isWhite() != bishopColor)
			{
				//black pawn at c7
				if(board[1][2].getType() == ChessPiece.PAWN && board[1][2].isWhite() != bishopColor)
				{
					if(bishopColor == state.getPlayer1Color())
					{
						score -= pawnVal * 3 / 2;
					}
					else
					{
						score += pawnVal * 3 / 2;
					}
				}
			}
		}
		
		//bishop at h7
		bishopColor = board[1][7].isWhite();
		if(board[1][7] != null && board[1][7].getType() == ChessPiece.BISHOP)
		{
			//enemy pawn at g6
			if(board[2][6] != null && board[2][1].getType() == ChessPiece.PAWN &&
					board[2][1].isWhite() != bishopColor)
			{
				//enemy pawn at f7
				if(board[1][5] != null && board[1][2].getType() == ChessPiece.PAWN &&
						board[1][2].isWhite() != bishopColor)
				{
					if(p1Queen && bishopColor == state.getPlayer1Color())
					{
						score -= pawnVal;
					}
					else
					{
						score -= pawnVal*3/2;
					}
				}
			}
		}
		//bishop at a2
		bishopColor = board[6][0].isWhite();
		if(board[6][0] != null && board[6][0].getType() == ChessPiece.BISHOP)
		{
			//black pawn at b3
			if(board[5][1].getType() == ChessPiece.PAWN && board[5][1].isWhite() != bishopColor)
			{
				//black pawn at c2
				if(board[6][2].getType() == ChessPiece.PAWN && board[6][2].isWhite() != bishopColor)
				{
					if(bishopColor == state.getPlayer1Color())
					{
						score -= pawnVal * 3 / 2;
					}
					else
					{
						score += pawnVal * 3 / 2;
					}
				}
			}
		}
		
		//bishop at h2
		bishopColor = board[6][7].isWhite();
		if(board[6][7] != null && board[6][7].getType() == ChessPiece.BISHOP)
		{
			//enemy pawn at g3
			if(board[5][6] != null && board[2][1].getType() == ChessPiece.PAWN &&
					board[5][6].isWhite() != bishopColor)
			{
				//enemy pawn at f2
				if(board[6][5] != null && board[1][2].getType() == ChessPiece.PAWN &&
						board[6][5].isWhite() != bishopColor)
				{
					//player 1's bishop
					if(bishopColor == state.getPlayer1Color())
					{
						if(p1Queen)
						{
							score -= pawnVal;
						}
						else
						{
							score -= pawnVal*3/2;
						}
					}
					else
					{
						if(p2Queen)
						{
							score += pawnVal;
						}
						else
						{
							score += pawnVal*3/2;
						}
					}
				}
			}
		}
		
		return score;
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
		boolean p1Left = state.getCanCastle()[1][0];
		boolean p1Right = state.getCanCastle()[1][1];
		
		boolean p2Left = state.getCanCastle()[0][0];
		boolean p2Right = state.getCanCastle()[0][1];
		
		if(state.isWhoseTurn() && (p1Left||p1Right))
		{
			//TODO finish
			return 0;
		}
		else if(!state.isWhoseTurn() && (p2Left||p2Right))
		{
			//TODO finish
			return 0;
		}
		else
		{
			return 0;
		}
	}

	/**
	 * See what pieces can be taken and reflect that in the score
	 * @param state
	 * @return score
	 */
	private static int threatBonus(ChessGameState state) {
		// TODO Auto-generated method stub
		int score = 0;
		for(ChessPiece piece:state.getPlayer1Pieces())
		{
			if(piece.isAlive())
			{
				ChessPiece[] vulnerable = attackedBy(state,piece);
				for(ChessPiece v:vulnerable)
				{
					score += worth[v.getType()];
				}
			}
		}
		for(ChessPiece piece:state.getPlayer2Pieces())
		{
			if(piece.isAlive())
			{
				ChessPiece[] vulnerable = attackedBy(state,piece);
				for(ChessPiece v:vulnerable)
				{
					score -= worth[v.getType()];
				}
			}
		}
		
		int totalMat = state.getPlayer1Material()+state.getPlayer2Material();
		
		score -= totalMat + totalMat * totalMat / queenVal;
        return score / 64;
	}
	
	/**
	 * Make trades favorable when you have more pieces
	 * and only trade pawns when you have less
	 * @param state
	 * @param score
	 * @return score
	 */
	private static int tradeBonus(ChessGameState state) {
		int p1Mat = state.getPlayer1Material();
		int p2Mat = state.getPlayer1Material();
		
		int p1PawnMat = state.getPlayer1PawnMaterial();
		int p2PawnMat = state.getPlayer2PawnMaterial();
		
        //positive if p1 is winning
        int deltaScore = p1Mat - p2Mat;
        int pBonus = 0;
        
        int x;
        if(deltaScore > 0)//use the winning player's pawn worth
        {
        	x = p1PawnMat;
        }
        else
        {
        	x = p2PawnMat;
        }
        //trade pawns in most cases
        pBonus += interpolate(x, 0, -30 * deltaScore / 100, 6 * pawnVal, 0);
        
        if(deltaScore > 0)//use the winning player's material worth
        {
        	x = p2Mat;
        }
        else
        {
        	x = p1Mat;
        }
        
        //trade more valuable pieces when you are winning
        int maxSigMaterial = queenVal + 2 * rookVal + 2 * bishopVal + 2 * knightVal;
        pBonus += interpolate(x, 0, 30 * deltaScore / 100, maxSigMaterial, 0);

        return pBonus;
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
				
				//use interpolation
				if(endGameMaterial[type] > 0)
				{
					int scoreMid = midGameTableP1[type][loc[0]][loc[1]];
					int scoreEnd = endGameTableP1[type][loc[0]][loc[1]];
					
					//the material of the pieces that matter
					int p2SigMat = p2Material-p2MaterialPawns;
					
					int p2MatUpper = midGameMaterial[type];
					int p2MatLower = endGameMaterial[type];
					
					score += interpolate(p2SigMat, p2MatLower, scoreEnd, p2MatUpper, scoreMid);
				}
				else if(endGameMaterial[type] == bishopCalc)
				{
					score += midGameTableP1[type][loc[0]][loc[1]];
				}
				else if(endGameMaterial[type] == queenCalc)
				{
					score += midGameTableP1[type][loc[0]][loc[1]];
				}
				else if(endGameMaterial[type] == rookCalc)
				{
					int numPawns = p2MaterialPawns/pawnVal;
					int scoreMid = midGameTableP1[type][loc[0]][loc[1]];
					
					//value of a rook depends on the number of opponent pawns
					score += scoreMid*Math.min(numPawns, 6)/6;
				}
				
			}
		}
		
		for(ChessPiece p: state.getPlayer2Pieces())
		{
			if(p.isAlive())
			{
				int type = p.getType();
				int[] loc = p.getLocation();
				
				//use interpolation
				if(endGameMaterial[type] > 0)
				{
					int scoreMid = midGameTableP2[type][loc[0]][loc[1]];
					int scoreEnd = endGameTableP2[type][loc[0]][loc[1]];
					
					//the material of the pieces that matter
					int p1SigMat = p1Material-p1MaterialPawns;
					
					int p1MatUpper = midGameMaterial[type];
					int p1MatLower = endGameMaterial[type];
					
					score -= interpolate(p1SigMat, p1MatLower, scoreEnd, p1MatUpper, scoreMid);
				}
				else if(endGameMaterial[type] == bishopCalc)
				{
					score -= midGameTableP2[type][loc[0]][loc[1]];
				}
				else if(endGameMaterial[type] == queenCalc)
				{
					score -= midGameTableP2[type][loc[0]][loc[1]];
					/*
					 * TODO implement an increase to the score depending on how many
					 * pieces the queen can capture
					 */
				}
				else if(endGameMaterial[type] == rookCalc)
				{
					int numPawns = p1MaterialPawns/pawnVal;
					int scoreMid = midGameTableP1[type][loc[0]][loc[1]];
					
					//value of a rook depends on the number of opponent pawns
					score -= scoreMid*Math.min(numPawns, 6)/6;
				}
			}
		}
		
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
    
    static final ChessPiece[] attackedBy(ChessGameState state,ChessPiece piece)
    {
    	//TODO finish
    	ArrayList<ChessPiece> inDanger = new ArrayList<ChessPiece>();
    	
    	ChessMoveAction[] moves = MoveGenerator.getPieceMoves(state, piece, null, piece.isWhite(), true);
    	for(ChessMoveAction move: moves)
    	{
    		ChessPiece takenPiece = move.getTakenPiece();
    		if(takenPiece != null)
    		{
    			inDanger.add(takenPiece);
    		}
    	}
    	
    	ChessPiece[] rtnVal = inDanger.toArray(new ChessPiece[inDanger.size()]);
		return rtnVal;
    }
}
