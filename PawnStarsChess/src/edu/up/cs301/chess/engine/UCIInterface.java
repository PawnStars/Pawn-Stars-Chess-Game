package edu.up.cs301.chess.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.util.Log;

/**
 * A simple and efficient client to run Stockfish from Java
 * 
 * @author Rahul A R
 * 
 */
public class UCIInterface {

	private Process engineProcess;
	private BufferedReader processReader;
	private OutputStreamWriter processWriter;

	private String PATH;
	
	private boolean restart;
	
	public UCIInterface(String path)
	{
		PATH = path;
	}
	
	/**
	 * Starts the engine as a process and initializes it
	 * 
	 * @param None
	 * @return 
	 * @return True on success. False otherwise
	 */
	public boolean startEngine() {
		try {
			engineProcess = Runtime.getRuntime().exec(PATH);
			processReader = new BufferedReader(new InputStreamReader(
					engineProcess.getInputStream()));
			processWriter = new OutputStreamWriter(
					engineProcess.getOutputStream());
		} catch (Exception e) {
			restart = true;
			return false;
		}
		return true;
	}

	/**
	 * Takes in any valid UCI command and executes it
	 * 
	 * @param command
	 */
	public void sendCommand(String command) {
		try {
			processWriter.write(command + "\n");
			processWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
			restart = true;
		}
	}

	/**
	 * This is generally called right after 'sendCommand' for getting the raw
	 * output from the engine
	 * 
	 * @param waitTime
	 *            Time in milliseconds for which the function waits before
	 *            reading the output. Useful when a long running command is
	 *            executed
	 * @return Raw output from the engine
	 */
	public String getOutput(int waitTime) {
		
		StringBuffer buffer = new StringBuffer();
		try {
			Thread.sleep(waitTime);
			sendCommand("stop");//idk
			sendCommand("isready");
			
			while (true) {
				String text = processReader.readLine();
				if (text.equals("readyok"))
					break;
				else
					buffer.append(text + "\n");
			}
		} catch (Exception e) {
			restart = true;
			e.printStackTrace();
		}
		return buffer.toString();
	}

	/**
	 * This function returns the best move for a given position after
	 * calculating for 'waitTime' ms
	 * 
	 * @param fen
	 *            Position string
	 * @param waitTime
	 *            in milliseconds
	 * @return Best Move in PGN format
	 */
	public String getBestMove(String fen, int waitTime) {
		sendCommand("position fen " + fen);
		sendCommand("go");// movetime " + waitTime);
		String out = getOutput(waitTime + 20);
		if(out != null)
		{
			Log.d("uci interface",out);
			String[] outs = out.split("bestmove ");
			if(outs.length > 1)
			{
				out = outs[1];
				outs = out.split(" ");
				if(outs.length > 0)
				{
					return outs[0];
				}
			}
		}
		return null;
	}

	/**
	 * Stops the engine and cleans up before closing it
	 */
	public void stopEngine() {
		try {
			sendCommand("quit");
			processReader.close();
			processWriter.close();
		} catch (IOException e) {
			restart = true;
		}
		engineProcess.destroy();
	}

	/**
	 * Get a list of all legal moves from the given position
	 * 
	 * @param fen
	 *            Position string
	 * @return String of moves
	 */
	public String getLegalMoves(String fen) {
		sendCommand("position fen " + fen);
		sendCommand("d");
		return getOutput(0).split("Legal moves: ")[1];
	}

	/**
	 * Draws the current state of the chess board
	 * 
	 * @param fen
	 *            Position string
	 */
	public void drawBoard(String fen) {
		sendCommand("position fen " + fen);
		sendCommand("d");

		String[] rows = getOutput(0).split("\n");

		for (int i = 1; i < 18; i++) {
			System.out.println(rows[i]);
		}
	}

	/**
	 * Get the evaluation score of a given board position
	 * @param fen Position string
	 * @param waitTime in milliseconds
	 * @return evalScore
	 */
	public float getEvalScore(String fen, int waitTime) {
		sendCommand("position fen " + fen);
		sendCommand("go movetime " + waitTime);

		float evalScore = 0.0f;
		String[] dump = getOutput(waitTime + 20).split("\n");
		for (int i = dump.length - 1; i >= 0; i--) {
			if (dump[i].startsWith("info depth ")) {
				try {
				evalScore = Float.parseFloat(dump[i].split("score cp ")[1]
						.split(" nodes")[0]);
				} catch(Exception e) {
					evalScore = Float.parseFloat(dump[i].split("score cp ")[1]
							.split(" upperbound nodes")[0]);
				}
			}
		}
		return evalScore/100;
	}

	public boolean isRestart() {
		return restart;
	}
	
	
	
}
