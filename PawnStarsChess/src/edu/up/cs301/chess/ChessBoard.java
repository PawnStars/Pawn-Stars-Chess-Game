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
	private Paint selectColor = new Paint();
	private Paint boardColor = new Paint();
	private Paint textColor = new Paint();
	private Paint blackPieceColor = new Paint();
	private Paint whitePieceColor = new Paint();
	
	// The unicode characters for chess pieces
	// outline of the characters
	public static final String whitePieceStrs[] = {"\u2654","\u2655","\u2656","\u2657","\u2658","\u2659"};
	
	// filled in characters
	public static final String blackPieceStrs[] = {"\u265A","\u265B","\u265C","\u265D","\u265E","\u265F"};
	
	// an 8x8 array of the pieces on the board
	private ChessPiece pieceMap[][];
	
	//TODO implement the instance variables below
	
	// the highlighted tiles on the screen
	private boolean[][] selectedTiles = new boolean[ChessGameState.BOARD_WIDTH][ChessGameState.BOARD_HEIGHT];
	
	private int[] selectedLoc = new int[]{-1,-1};

	// true if the board is flipped upside down
	// false if not
	private boolean flipped = false;
	
	private float[] tileSize = new float[2];
	
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
		tileSize[0] = dimensions/ChessGameState.BOARD_HEIGHT;
		tileSize[1] = dimensions/ChessGameState.BOARD_WIDTH;
		
		// Set each piece to the size of a tile
		textColor.setTextSize(tileSize[0]/2);
		whitePieceColor.setTextSize(tileSize[0]);
		blackPieceColor.setTextSize(tileSize[0]);
		
		// Margins for the chess notation text
		float marginSize = (float)(textColor.getTextSize()*0.66);
		
		// Draw a background behind everything
		canvas.drawRect(0, 0, dimensions, dimensions, boardColor);
		
		//TODO not sure the board is flipped correctly
		if(!flipped)
		{
			for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
			{
				for(int j=0;j<ChessGameState.BOARD_WIDTH;j++)
				{
					if(selectedLoc != null && selectedLoc[0] == i && selectedLoc[1] == j)
					{
						// Draw the selected tile
						canvas.drawRect(i*tileSize[0], j*tileSize[1], (i+1)*tileSize[0], (j+1)*tileSize[1], selectColor);
					}
					else if(selectedTiles != null && selectedTiles[j][i] == true)
					{
						// Draw the highlighted tiles
						canvas.drawRect(i*tileSize[0], j*tileSize[1], (i+1)*tileSize[0], (j+1)*tileSize[1], highlightColor);
					}
					else if((i%2) == (j%2))
					{
						// Draw tiles
						canvas.drawRect(i*tileSize[0], j*tileSize[1], (i+1)*tileSize[0], (j+1)*tileSize[1], tileColor);
					}
				}
				
				// Draw the chess notation text vertically
				canvas.drawText(""+(ChessGameState.BOARD_HEIGHT-i), 0, (int)((i+0.66)*tileSize[0]), textColor);
			}
			
			// Draw the chess notation text horizontally
			for(int j=0;j<ChessGameState.BOARD_WIDTH;j++)
			{
				// uses arithmetic to turn 0,1,...,8 to A,B,...,H
				canvas.drawText(String.valueOf((char)(65+j)), (int)((j+0.33)*tileSize[1]), marginSize, textColor);
			}
			
			// Draw each piece in its respective color
			if(pieceMap != null)
			{
				for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
				{
					for(int j=0;j<ChessGameState.BOARD_WIDTH;j++)
					{
						ChessPiece piece = pieceMap[i][j];
						if(piece != null && piece.isAlive())
						{
							float x = j*tileSize[0];
							float y = (int)((i+0.80)*tileSize[1]);
							int type = piece.getType();
							if(type >= 0 && type < whitePieceStrs.length)
							{
								if(piece.isWhite())
								{
									canvas.drawText(whitePieceStrs[piece.getType()], x, y, whitePieceColor);
								}
								else
								{
									canvas.drawText(blackPieceStrs[piece.getType()], x, y, blackPieceColor);
								}
							}
							
						}
					}
				}
			}
		}
		else
		{
			for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
			{
				for(int j=0;j<ChessGameState.BOARD_WIDTH;j++)
				{
					int newJ = ChessGameState.BOARD_WIDTH-j;
					float left = i*tileSize[0];
					float top = newJ*tileSize[1];
					float right = (i+1)*tileSize[0];
					float bottom = (newJ+1)*tileSize[1];
					if(selectedLoc != null && selectedLoc[0] == i && selectedLoc[1] == j 
							&& !ChessGameState.outOfBounds(selectedLoc))
					{
						// Draw the selected tile
						canvas.drawRect(left, top, right, bottom, selectColor);
					}
					else if(selectedTiles != null && selectedTiles[i][j] == true 
							&& !ChessGameState.outOfBounds(selectedLoc))
					{
						// Draw the highlighted tiles
						canvas.drawRect(left, top, right, bottom, highlightColor);
					}
					else if((i%2) == (j%2))
					{
						// Draw tiles
						canvas.drawRect(left, top, right, bottom, tileColor);
					}
				}
				
				// Draw the chess notation text vertically
				canvas.drawText(""+(ChessGameState.BOARD_HEIGHT-i), 0, (int)((i+0.66)*tileSize[0]), textColor);
			}
			
			// Draw the chess notation text horizontally
			for(int j=0;j<ChessGameState.BOARD_WIDTH;j++)
			{
				// uses arithmetic to turn 0,1,...,8 to A,B,...,H
				canvas.drawText(String.valueOf((char)(65+j)), (int)((j+0.33)*tileSize[1]), marginSize, textColor);
			}
			
			// Draw each piece in its respective color
			if(pieceMap != null)
			{
				for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
				{
					for(int j=0;j<ChessGameState.BOARD_WIDTH;j++)
					{
						int newI = ChessGameState.BOARD_WIDTH-i;
						ChessPiece piece = pieceMap[i][j];
						if(piece != null)
						{
							float x = j*tileSize[0];
							float y = (float) ((newI+0.8)*tileSize[1]);
							if(piece.isWhite())
							{
								canvas.drawText(whitePieceStrs[piece.getType()], x, y, whitePieceColor);
							}
							else
							{
								canvas.drawText(blackPieceStrs[piece.getType()], x, y, blackPieceColor);
							}
						}
					}
				}
			}
		}
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
		whitePieceColor.setColor(0xFFFFFFFF);//white
		blackPieceColor.setColor(0xFF000000);//black
		highlightColor.setColor(0xAA32CD32);//faded green
		selectColor.setColor(0xFF00FF00);//blue
	}
	
	/**
	 * Gives the board a new 8x8 array of ChessPieces and draws it
	 * @param 2d array of every piece on the board
	 */
	public void setPieceMap(ChessPiece[][] pieceMap) {
		if(pieceMap == null)
		{
			return;
		}
		if(pieceMap.length != ChessGameState.BOARD_HEIGHT || pieceMap[0].length != ChessGameState.BOARD_WIDTH)
		{
			return;
		}
		this.pieceMap = pieceMap;
		invalidate();
	}

	/**
	 * Gives the board a new 8x8 array of booleans and draws on
	 * the corresponding true tiles
	 * @param 2d array of every piece on the board
	 */
	public void setSelectedTiles(boolean[][] selectedTiles) {
		
		//clear the tiles
		if(selectedTiles == null)
		{
			this.selectedTiles = new boolean[ChessGameState.BOARD_HEIGHT][ChessGameState.BOARD_WIDTH];
			invalidate();
		}
		else if(selectedTiles.length != ChessGameState.BOARD_HEIGHT || selectedTiles[0].length != ChessGameState.BOARD_WIDTH)
		{
			//this is an error
			return;
		}
		else
		{
			this.selectedTiles = selectedTiles;
			invalidate();
		}
	}

	/** 
	 * Sets the new location where the player touched
	 * @param i the index of the row down the chess board
	 * 		  j the index of the columns across the chess board
	 */
	public void setSelectedLoc(int i,int j) {
		int[] newSelectedLoc = new int[2];
		newSelectedLoc[1] = i;
		newSelectedLoc[0] = j;
		selectedLoc = newSelectedLoc;
		invalidate();
	}

	/**
	 * Flips the board on the screen
	 */
	public void flipBoard()
	{
		flipped = !flipped;
		invalidate();
	}
	
	/**
	 * Returns an array of size 2 containing the size of each tile on the board
	 * @return
	 */
	public float[] getTileSize()
	{
		return tileSize;
	}
	
}
