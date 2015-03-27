package edu.up.cs301.chess;

import edu.up.cs301.chess.ChessGameState;
import edu.up.cs301.chess.ChessPiece;

/**
 * This represents a single chess piece on the board. It stores its
 * color, what type of piece it is, if it is on the board,
 * and it it has moved before.
 * .
 * @authors Allison Liedtke
 * 		    Anthony Donaldson
 * 		    Derek Schumacher
 * 			Scott Rowland
 * @version March 2015
 *
 */
public class ChessPiece {
	
	//The names for what each Piece can be
	public static final int PAWN = 0;
	public static final int ROOK = 1;
	public static final int BISHOP = 2;
	public static final int KNIGHT = 3;
	public static final int QUEEN = 4;
	public static final int KING = 5;
	
	//Keeps track of what color piece is and if they have moved
	private boolean isWhite;
	private boolean hasMoved;
	
	//Keep track of if piece is alive/dead:
	private boolean isAlive;
	
	//Indicates which type of piece it is.
	private int type = -1; 
	
	//TODO implement location
	//Indicates where the piece is on the board
	private int[] location;
	/**
	 * Ctor
	 * 
	 * @param type: Constant used to indicate which piece
	 * @param isWhite: true if the piece is white
	 */
	public ChessPiece (int type, boolean isWhite) {
		//Make sure type is valid:
		if (type < 6 && type >= 0) {
			this.type = type;
		}
		
		this.hasMoved = false;
		this.isWhite = isWhite;
		this.isAlive = true;
	}
	
	/**
	 * Returns whether or not this piece is alive
	 * 
	 * @return true if the piece is alive (has not been taken)
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	/**
	 * Returns the type of this piece as an int
	 * 
	 * @return the type of this piece represented as an int
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Returns true if this piece is white, false if black
	 * 
	 * @return true if this ChessPiece is white and
	 * 		   false if it is black
	 */
	public boolean isWhite()
	{
		return isWhite;
	}
	
	/**
	 * Returns whether or not this piece has moved before
	 * 
	 * @return true if this piece moved before and
	 * 		   false if it has not moved
	 */
	public boolean getHasMoved()
	{
		return hasMoved;
	}
	
	/**
	 * Set this piece as dead
	 */
	public void kill()
	{
		isAlive = false;
	}
	
	/**
	 * Sets hasMoved to true and sets a new location
	 * @param the new location of the piece
	 */
	public void move(int[] newLoc)
	{
		hasMoved = true;
		location = newLoc;
	}
	
	/**
	 * Returns the location
	 * @return the location of the piece
	 */
	public int[] getLocation() {
		return location;
	}

	/**
	 * Change the type of this ChessPiece in the case of promotion
	 * @param type
	 */
	public void setType(int type)
	{
		this.type = type;
	}
	
	/**
	 * Makes a deep copy of this ChessPiece
	 * @return a copy of this ChessPiece
	 */
	public ChessPiece copy()
	{
		ChessPiece newPiece = new ChessPiece(type,isWhite);
		return newPiece;
	}
	
	/**
	 * Does a deep copy of a 2d array of ChessPieces
	 * 
	 * @param an 8x8 array
	 * @return a deep copy of the 2d array
	 */
	public static ChessPiece[][] copyPieceMap(ChessPiece[][] map)
	{
		if(map == null)
		{
			return null;
		}
		ChessPiece[][] newMap = new ChessPiece[ChessGameState.BOARD_WIDTH][ChessGameState.BOARD_HEIGHT];
		for(int i=0;i<ChessGameState.BOARD_WIDTH;i++)
		{
			for(int j=0;j<ChessGameState.BOARD_HEIGHT;j++)
			{
				if(map[i][j] != null)
				{
					newMap[i][j] = map[i][j].copy();
				}
			}
		}
		return newMap;
	}
	
	/**
	 * Does a deep copy of an array of ChessPieces and puts the new pieces in the map
	 * 
	 * @param an array of length 16
	 * @return a deep copy of the array
	 */
	public static ChessPiece[] copyPieceList(ChessPiece[][] map, ChessPiece[] list)
	{
		if(list.length != ChessGameState.NUM_PIECES)
			return null;
		ChessPiece[] newList = new ChessPiece[ChessGameState.NUM_PIECES];
		
		//make copy of the list
		for(int k=0;k<ChessGameState.NUM_PIECES;k++)
		{
			if(list[k] != null)
			{
				newList[k] = list[k].copy();
			}
		}
		
		//replace the ones in the map
		for(int i=0;i<ChessGameState.BOARD_WIDTH;i++)
		{
			for(int j=0;j<ChessGameState.BOARD_HEIGHT;j++)
			{
				if(map[i][j] != null)
				{
					for(int k=0;k<ChessGameState.NUM_PIECES;k++)
					{
						if(list[k] != null)
						{
							if(list[k].equals(map[i][j]))
							{
								map[i][j] = list[k];
							}
						}
					}
				}
			}
		}
		
		return newList;
	}
	
	/**
	 * Checks if two arrays of ChessPieces are equal to one another.
	 * It does a shallow and deep comparison.
	 * 
	 * @param array1 first array
	 * @param array2 second array
	 * @return true if the two arrays of ChessPieces are equivalent and
	 * 		   false if they are not
	 */
	public static boolean pieceArrayEquals(ChessPiece[] array1, ChessPiece[] array2)
	{
		// Check for the same reference
		if(array1 == array2)
			return true;
		
		// Check for one being null and another not
		if(array1 == null && array2 != null)
			return false;
		if(array2 == null && array1 != null)
			return false;
		
		// Check if the arrays have the right length
		if(array1.length != ChessGameState.NUM_PIECES)
			return false;
		
		if(array2.length != ChessGameState.NUM_PIECES)
			return false;
		
		//check the board for equivalent pieces in each array
		for (int i = 0; i < ChessGameState.BOARD_WIDTH; ++i)
		{
			if(array1[i] != null)
			{
				if(array2[i] != null)
				{
					//both not null
					if(!(array1[i].equals(array2[i])))
						return false;
				}
				else
				{
					//one null one not
					return false;
				}
			}
			else
			{
				if(array2[i] != null)
				{
					//one null one not
					return false;
				}
			}
		}

		return true;
	}
	
	/**
	 * Checks if two 2d arrays of ChessPieces are equal to one another.
	 * It does a shallow and deep comparison.
	 * @param map1 first 2d array
	 * @param map2 second 2d array
	 * @return true if the two 2d arrays of ChessPieces are equivalent and
	 * 		   false if they are not
	 */
	public static boolean pieceMapEquals(ChessPiece[][] map1, ChessPiece[][] map2)
	{
		// Check for the same reference
		if(map1 == map2)
			return true;
		
		// Check for one being null and another not
		if(map1 == null && map2 != null)
			return false;
		if(map2 == null && map1 != null)
			return false;
		
		// Check if the arrays have the right dimensions
		if(map1.length != ChessGameState.BOARD_HEIGHT)
			return false;
		if(map1[0].length != ChessGameState.BOARD_WIDTH)
			return false;
		
		if(map2.length != ChessGameState.BOARD_HEIGHT)
			return false;
		if(map2[0].length != ChessGameState.BOARD_WIDTH)
			return false;
		
		// Check the board for equivalent pieces in each pieceMap
		for (int row = 0; row < ChessGameState.BOARD_WIDTH; ++row)
		{
			for (int col = 0; col < ChessGameState.BOARD_HEIGHT; ++col)
			{
				if(map1[row][col] != null)
				{
					if(map2[row][col] != null)
					{
						//both not null
						if(!(map1[row][col].equals(map2[row][col])))
							return false;
					}
					else
					{
						//one null, one not
						return false;
					}
				}
				else
				{
					if(map2[row][col] != null)
					{
						//one null, one not
						return false;
					}
					//both null
				}
			}
		}

		// Reaches the end if it passed all the tests
		return true;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		// Cast to ChessPiece if it is a ChessPiece
		if (getClass() != obj.getClass())
			return false;
		ChessPiece other = (ChessPiece) obj;
		
		// Compare instance variables
		if (hasMoved != other.hasMoved)
			return false;
		if (isAlive != other.isAlive)
			return false;
		if (isWhite != other.isWhite)
			return false;
		if (type != other.type)
			return false;
		
		return true;
	}
}
