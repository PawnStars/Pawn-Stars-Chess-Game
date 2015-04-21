package edu.up.cs301.game.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.*;
import edu.up.cs301.chess.*;
import edu.up.cs301.game.R;

/**
 * Tests the GUI to make sure the buttons appear for any screen size
 * and they carry out their purpose on the GUI
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version March 2015
 *
 */
public class GuiTest extends ActivityInstrumentationTestCase2<ChessMainActivity>{

	// The main activity
	private ChessMainActivity act;
	
	// The buttons at the side of the screen
	//private Button confirmButton;
	private Button quitButton;
	private Button flipButton;
	private Button drawButton;
	
	// The chessboard that draws each piece
	private ChessBoard board;
	
	// The main layout
	private View layout;
	
	// Position of the layout on the screen in pixels
	private int layoutLocation[] = { 0, 0 };
	
	// Size of the layout
	int width;
	int height;
	
	public GuiTest() {
		super(ChessMainActivity.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		act = this.getActivity();
		this.setActivityInitialTouchMode(false);
		
		Button startButton = (Button)act.findViewById(R.id.playGameButton);
		TouchUtils.clickView(this, startButton);
		
		layout = (View)act.findViewById(R.id.top_gui_layout);
		
		drawButton = (Button)act.findViewById(R.id.drawButton);
		flipButton = (Button)act.findViewById(R.id.flipBoardButton);
		quitButton = (Button)act.findViewById(R.id.resignButton);
		
		board = (ChessBoard)act.findViewById(R.id.gameBoardSurfaceView);
		
		layout.getLocationOnScreen(layoutLocation);
		
		width = layout.getWidth();
		height = layout.getHeight();
		
		//Remove focus from the popup
		TouchUtils.clickView(this, drawButton);
	}
	
	public void testDrawButton() {
		// Get the size of the button
		float btnWidth = drawButton.getWidth();
		float btnHeight = drawButton.getHeight();
		
		// Find the button's coords
		int buttonLocation[] = { 0, 0 };
		drawButton.getLocationOnScreen(buttonLocation);
		
		// Check if it is all in the screen
		assertTrue("Checking button 1 x-bounds: ",
				layoutLocation[0] + width > buttonLocation[0] + btnWidth);
		assertTrue("Checking button 1 y-bounds: ",
				layoutLocation[1] + height > buttonLocation[1] + btnHeight);
		
		TouchUtils.clickView(this, drawButton);
		//TODO check if the draw button does what it is supposed to do
	}
	
	/**
	 * Tests that the quit button is visible on the screen
	 * for any given screen size
	 */
	public void testQuitButton() {
		// Get the size of the button
		float btnWidth = quitButton.getWidth();
		float btnHeight = quitButton.getHeight();
		
		// Find the button's coords
		int buttonLocation[] = { 0, 0 };
		quitButton.getLocationOnScreen(buttonLocation);
		
		// Check if it is all in the screen
		assertTrue("Checking button 1 x-bounds: ",
				layoutLocation[0] + width > buttonLocation[0] + btnWidth);
		assertTrue("Checking button 1 y-bounds: ",
				layoutLocation[1] + height > buttonLocation[1] + btnHeight);
		
		TouchUtils.clickView(this, quitButton);
		//TODO check if quit really does end the game
	}
	/**
	 * Tests that the flip board button is on the screen
	 * and flips the board when pressed
	 */
	public void testFlipButton() {
		// Get the size of the button
		float btnWidth = flipButton.getWidth();
		float btnHeight = flipButton.getHeight();
		
		// Find the button's coords
		int buttonLocation[] = { 0, 0 };
		flipButton.getLocationOnScreen(buttonLocation);
		
		// Check if it is all in the screen
		assertTrue("Checking button 1 x-bounds: ",
				layoutLocation[0] + width > buttonLocation[0] + btnWidth);
		assertTrue("Checking button 1 y-bounds: ",
				layoutLocation[1] + height > buttonLocation[1] + btnHeight);
		
		boolean wasFlipped = board.isFlipped();
		TouchUtils.clickView(this, flipButton);
		boolean isFlipped = board.isFlipped();
		
		assertTrue("The board did not flip", wasFlipped!=isFlipped);
		//TODO check if flipping the board works
	}
	
	public void testPromotion()
	{
		
	}
	
}
