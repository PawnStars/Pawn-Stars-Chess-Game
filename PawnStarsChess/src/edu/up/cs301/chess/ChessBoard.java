package edu.up.cs301.chess;

import java.io.Serializable;
import java.util.ArrayList;

import android.R;
import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
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
	
	// Paints for each element drawn on the board
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
	
	// the highlighted tiles on the screen
	private boolean[][] selectedTiles = new boolean[ChessGameState.BOARD_WIDTH][ChessGameState.BOARD_HEIGHT];
	
	private int[] selectedLoc = new int[]{-1,-1};

	// true if the board is flipped upside down
	// false if not
	private boolean flipped = false;
	
	//the size of a single tile
	private float[] tileSize = new float[]{0,0};
	
	private ArrayList <ChessPiece> whiteTakenPieces = new ArrayList<ChessPiece>();
	private ArrayList <ChessPiece> blackTakenPieces = new ArrayList<ChessPiece>();
	
	private ArrayList <Integer> whiteTakenIndex = new ArrayList<Integer>();
	private ArrayList <Integer> blackTakenIndex = new ArrayList<Integer>();

	
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
		
		for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
		{
			for(int j=0;j<ChessGameState.BOARD_WIDTH;j++)
			{
				float left = j*tileSize[0];
				float top = i*tileSize[1];
				float right = (j+1)*tileSize[0];
				float bottom = (i+1)*tileSize[1];
				if(flipped)
				{
					int newI = ChessGameState.BOARD_WIDTH-1-i;
					top = newI*tileSize[1];
					bottom = (newI+1)*tileSize[1];
				}
				
				Paint currentColor = null;
				if(selectedLoc != null && selectedLoc[1] == i && selectedLoc[0] == j)
				{
					// Draw the selected tile
					currentColor = selectColor;
				}
				else if(selectedTiles != null && selectedTiles[i][j] == true)
				{
					// Draw the highlighted tiles
					currentColor =  highlightColor;
				}
				else if((i%2) == (j%2))
				{
					// Draw tiles
					currentColor = tileColor;
				}
				if(currentColor != null)
				{
					canvas.drawRect(left, top, right, bottom, currentColor);
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
		if(pieceMap != null)
		{
			// Draw each piece in its respective color
			for(int i=0;i<ChessGameState.BOARD_HEIGHT;i++)
			{
				for(int j=0;j<ChessGameState.BOARD_WIDTH;j++)
				{
					ChessPiece piece = pieceMap[i][j];
					if(piece != null && piece.isAlive())
					{
						float x = j*tileSize[0];
						float y = (int)((i+0.80)*tileSize[1]);
						if(flipped)
						{
							int newI = ChessGameState.BOARD_WIDTH-1-i;
							y = (int)((newI+0.80)*tileSize[1]);
						}
						int type = piece.getType();
						String typeStr = whitePieceStrs[piece.getType()];
						String fillTypeStr = blackPieceStrs[piece.getType()];
						if(type >= 0 && type < whitePieceStrs.length)
						{
							if(piece.isWhite())
							{
								canvas.drawText(fillTypeStr, x, y, whitePieceColor);
								canvas.drawText(typeStr, x, y, blackPieceColor);
							}
							else
							{
								canvas.drawText(fillTypeStr, x, y, blackPieceColor);
							}
						}
						
					}
				}
			
			}
		}
		
		//draw the grave yard for black pieces
		if (blackTakenPieces != null && blackTakenPieces.size() > 0) {
			
			//Goes through the taken black array and draws them on the board
			for (int k = 0; k < blackTakenPieces.size(); k++) {
				if (blackTakenPieces.get(k) != null) {
					
					int type = blackTakenPieces.get(k).getType();
					String str = blackPieceStrs[type];

					if (type >= 0 && type < blackPieceStrs.length) {

						//sets the x and y location of the piece 
						int x = (int) (tileSize[1] * 8);
						int y = (int) (tileSize[0]);
						x += (k * tileSize[1]);

						//Goes to a new row of taken pieces
						if (x > canvas.getWidth() - 100) {
							y = (int) (tileSize[0] * 2);
							x = (int) ((int) (tileSize[1] * 8) + ((k - 4) * tileSize[1]));
							
							//Goes to a new row of taken pieces
							if (x > canvas.getWidth() - 100) {
								y = (int) (tileSize[0] * 3);
								x = (int) ((int) (tileSize[1] * 8) + ((k - 8) * tileSize[1]));
								
								//Goes to a new row of taken pieces
								if (x > canvas.getWidth() - 100) {
									y = (int) (tileSize[0] * 4);
									x = (int) ((int) (tileSize[1] * 8) + ((k - 12) * tileSize[1]));
								}
							}
						}

						canvas.drawText(str, x, y, blackPieceColor);
					}
				}
			}
		}
		
		//draw the grave yard for white pieces
		if (whiteTakenPieces != null && whiteTakenPieces.size() > 0) {
			
			//Goes through the taken white array and draws them on the board
			for (int k = 0; k < whiteTakenPieces.size(); k++) {
				if (whiteTakenPieces.get(k) != null) {
					
					int type = whiteTakenPieces.get(k).getType();
					String str = blackPieceStrs[type];
					String str2 = whitePieceStrs[type];

					if (type >= 0 && type < whitePieceStrs.length) {
						
						//sets the x and y location of the piece 
						int x = (int) ((tileSize[1] * 8));
						int y = (int) tileSize[0];
						y += (tileSize[0] * 4);
						x += (k * tileSize[1]);
						
						//Goes to a new row of taken pieces
						if (x > canvas.getWidth() - 100) {
							y = (int) tileSize[0];
							y += (int) (tileSize[0] * 5);
							x = (int) ((int) (tileSize[1] * 8) + ((k - 4) * tileSize[1]));
							
							//Goes to a new row of taken pieces
							if (x > canvas.getWidth() - 100) {
								y = (int) tileSize[0];
								y += (int) (tileSize[0] * 6);
								x = (int) ((int) (tileSize[1] * 8) + ((k - 8) * tileSize[1]));

								//Goes to a new row of taken pieces
								if (x > canvas.getWidth() - 100) {
									y = (int) tileSize[0];
									y += (int) (tileSize[0] * 7);
									x = (int) ((int) (tileSize[1] * 8) + ((k - 12) * tileSize[1]));
								}
							}
						}
						canvas.drawText(str, x, y, whitePieceColor);
						canvas.drawText(str2, x, y, blackPieceColor);
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
		blackPieceColor.setStyle(Paint.Style.FILL);
		blackPieceColor.setColor(0xFF000000);//black
		highlightColor.setColor(0xAA32CD32);//faded green
		selectColor.setColor(0xFF00FF00);//blue
		setZOrderOnTop(true);
		SurfaceHolder holder = getHolder();
		holder.setFormat(PixelFormat.TRANSPARENT);
		
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
			return; //error
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
		newSelectedLoc[0] = j;
		newSelectedLoc[1] = i;
		
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
	
	
	
	public boolean isFlipped() {
		return flipped;
	}

	/**
	 * Returns an array of size 2 containing the size of each tile on the board
	 * @return
	 */
	public float[] getTileSize()
	{
		return tileSize;
	}
	
	/**
	 * This method adds the white taken pieces to an ArrayList and 
	 * makes sure there are no duplicate pieces 
	 * 
	 * @param piece: The piece that is taken 
	 * @param index: The index of the piece in the original array 
	 */
	public void setWhiteTakenPiece(ChessPiece piece, int index) {
		if(whiteTakenIndex.contains(index)){
			return;
		}
		whiteTakenIndex.add(index);
		whiteTakenPieces.add(piece);
	}
	
	/**
	 * 
	 * @return WhiteTakenPieces ArrayList
	 */
	public ArrayList<ChessPiece> getWhiteTakenPieces() {
		return whiteTakenPieces;
	}

	/**
	 * This method adds the black taken pieces to an ArrayList and 
	 * makes sure there are no duplicate pieces 
	 * 
	 * @param piece: The piece that is taken 
	 * @param index: The index of the piece in the original array 
	 */
	public void setBlackTakenPiece(ChessPiece piece, int index) {
		if(blackTakenIndex.contains(index)){
			return;
		}
		blackTakenIndex.add(index);
		blackTakenPieces.add(piece);
	}
	
	/**
	 * 
	 * @return BlackTakenPieces ArrayList
	 */
	public ArrayList<ChessPiece> getBlackTakenPieces() {
		return blackTakenPieces;
	}
}
