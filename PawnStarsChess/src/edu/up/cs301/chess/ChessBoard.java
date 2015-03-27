package edu.up.cs301.chess;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceView;
import android.util.AttributeSet;

/**
 * A subclass of the SurfaceView class that draws a square chessboard
 * on the screen with an 8x8 array of pieces.
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version March 2015
 *
 */
public class ChessBoard extends SurfaceView
{
	// colors for each element drawn on the board
	private Paint tileColor = new Paint();
	private Paint highlightColor = new Paint();
	private Paint boardColor = new Paint();
	private Paint textColor = new Paint();
	private Paint blackPieceColor = new Paint();
	private Paint whitePieceColor = new Paint();
	
	// The unicode characters for chess pieces
	// outline of the characters
	private static String whitePieceStrs[] = {"\u2654","\u2655","\u2656","\u2657","\u2658","\u2659"};
	
	// filled in characters
	private static String blackPieceStrs[] = {"\u265A","\u265B","\u265C","\u265D","\u265E","\u265F"};
	
	// an 8x8 array of the pieces on the board
	private ChessPiece pieceMap[][];
	
	//TODO implement the instance variables below
	
	// the highlighted tiles on the screen
	private boolean[][] selectedTiles;
	
	private int[] selectedLoc;

	// true if the board is flipped upside down
	// false if not
	private boolean flipped = false;
	
	/**
	 * constructor
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public ChessBoard(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}
	
	/**
	 * constructor
	 * @param context
	 * @param attrs
	 */
	public ChessBoard(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}
	
	/**
	 * constructor
	 * @param context
	 */
	public ChessBoard(Context context)
	{
		super(context);
		init();
	}

	/**
	 * Draws the current state of the board
	 * @param canvas the canvas to draw on
	 */
	@Override
	public void onDraw(Canvas canvas)
	{
		// Make sure the board is square by only drawing
		// as wide or tall as its smallest dimension
		float dimensions = 0;
		if(getHeight()<getWidth())
		{
			dimensions = getHeight();
		}
		else
		{
			dimensions = getWidth();
		}
		//TODO implement a graveyard where there is room
		
		// The size of each tile
		float incrX = dimensions/ChessGameState.BOARD_HEIGHT;
		float incrY = dimensions/ChessGameState.BOARD_WIDTH;
		
		// Set each piece to the size of a tile
		textColor.setTextSize(incrX/2);
		whitePieceColor.setTextSize(incrX);
		blackPieceColor.setTextSize(incrX);
		
		// Margins for the chess notation text
		float marginSize = (float)(textColor.getTextSize()*0.66);
		
		// Draw a background behind everything
		canvas.drawRect(0, 0, dimensions, dimensions, boardColor);
		
		for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
		{
			for(int j=0;j<ChessGameState.BOARD_WIDTH;j++)
			{
				if((i%2) == (j%2))
				{
					// Draw tiles
					canvas.drawRect(i*incrX, j*incrY, (i+1)*incrX, (j+1)*incrY, tileColor);
				}
				if(selectedTiles[i][j] == true && selectedTiles != null)
				{
					// Draw the highlighted tiles
					canvas.drawRect(i*incrX, j*incrY, (i+1)*incrX, (j+1)*incrY, highlightColor);
				}
			}
			
			// Draw the chess notation text vertically
			canvas.drawText(""+(i+1), 0, (int)((i+0.66)*incrX), textColor);
		}
		
		// Draw the chess notation text horizontally
		for(int j=0;j<ChessGameState.BOARD_WIDTH;j++)
		{
			// uses arithmetic to turn 0,1,...,8 to A,B,...,H
			canvas.drawText(String.valueOf((char)(65+j)), (int)((j+0.33)*incrY), marginSize, textColor);
		}
		
		// Draw each piece in its respective color
		if(pieceMap != null)
		{
			for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
			{
				for(int j=0;j<ChessGameState.BOARD_WIDTH;j++)
				{
					if(pieceMap[i][j] != null)
					{
						if(pieceMap[i][j].isWhite())
						{
							canvas.drawText(whitePieceStrs[pieceMap[i][j].getType()], j*incrX, (int)((i+0.80)*incrY), whitePieceColor);
						}
						else
						{
							canvas.drawText(blackPieceStrs[pieceMap[i][j].getType()], j*incrX, (int)((i+0.80)*incrY), blackPieceColor);
						}
					}
				}
			}
		}
		//TODO implement flipped,selectedloc
	}
	
	/**
	 * Initializes paints for the board, tiles, and pieces
	 */
	protected void init()
	{
		//various colors
		boardColor.setColor(0xFF510700);//yellowish
		tileColor.setColor(0xFFEFD284);//brownish
		textColor.setColor(Color.RED);//red
		whitePieceColor.setColor(Color.WHITE);//white
		blackPieceColor.setColor(Color.BLACK);//black
		highlightColor.setColor(0xAA32CD32);//faded green
		
		// highlighted tiles
		selectedTiles = new boolean[ChessGameState.BOARD_WIDTH][ChessGameState.BOARD_HEIGHT];
		
		// the board is upside down if true
		flipped = false;
	}
	
	/**
	 * Gives the board a new 8x8 array of ChessPieces and draws it
	 * @param 2d array of every piece on the board
	 */
	public void setPieceMap(ChessPiece[][] pieceMap) {
		this.pieceMap = pieceMap;
		invalidate();
	}

	/**
	 * Gives the board a new 8x8 array of booleans and draws on
	 * the corresponding true tiles
	 * @param 2d array of every piece on the board
	 */
	public void setSelectedTiles(boolean[][] selectedTiles) {
		this.selectedTiles = selectedTiles;
		invalidate();
	}

	/** 
	 * Sets the new location where the player touched
	 * @param selectedLoc
	 */
	public void setSelectedLoc(int[] selectedLoc) {
		this.selectedLoc = selectedLoc;
	}

	/**
	 * Flips the board on the screen
	 */
	public void flipBoard()
	{
		flipped = !flipped;
		invalidate();
	}
	
	
}