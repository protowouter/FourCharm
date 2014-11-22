/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package test.java.benchmark;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import main.java.model.ComputerPlayer;
import main.java.model.Game;
import main.java.model.Player;
import main.java.model.ai.RandomStrategy;
import main.java.model.board.BinaryBoard;
import main.java.model.board.Board;
import main.java.model.board.ReferenceBoard;

import com.google.common.io.ByteStreams;


/**
 * Class for perfoming an benchmark of the BinaryBoard.
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class RandomBenchmark {
	
	private RandomBenchmark() {
		
	}
	
	/**
	 * Amount of iterations of the benchmark.
	 */
	public static final int ITERATIONS = 10_000_000;
	
	public static final int STEP_PERCENTAGE = 10;
	
	private static int runBenchmark(Class<? extends Board> boardClass) {
		
		
		int gCount = 0;
		long pCount = 0;
		final int total = ITERATIONS;
		final int step = total / STEP_PERCENTAGE; 
		long oStartTime = System.currentTimeMillis();
		PrintStream nullStream = new PrintStream(ByteStreams.nullOutputStream());
		
		
		
		
		while (gCount < total + 1) {
			
			if (gCount % step == 0 && gCount != 0) {
				float percent = ((float) gCount / (float) total) * 100;
				int intPercent = (int) percent;
				System.out.println(intPercent + "%");
			}
		
			Game game = new Game(boardClass, new Player[]{new ComputerPlayer(new RandomStrategy())
							, new ComputerPlayer(new RandomStrategy())}
							, nullStream, false);
			game.play();
			gCount++;
			pCount += game.plieCount();
		
		}
		long endTime = System.currentTimeMillis();
		
		return (int) (pCount / ((float) (endTime - oStartTime) / 1000));
		
		
	}
	
	
	/**
	 * Run the benchmark.
	 * @param args N/A
	 */
	public static void main(String[] args) {
		
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALY);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');
		
		System.out.println("Running Benchmark with " 
						+ formatter.format(ITERATIONS) + " iterations");
		
		System.out.println("\n\n" + BinaryBoard.class.getSimpleName() + ":");
		int binMps = runBenchmark(BinaryBoard.class);
		System.out.println("\n\n" + ReferenceBoard.class.getSimpleName() + ":");
		int refMps = runBenchmark(ReferenceBoard.class);
		float speedup = (float) binMps / refMps;
		
		System.out.println("\n\n\nResults:\n-----------\n");
		
		System.out.println("BinaryBoard: " + formatter.format(binMps) 
						+ " mps (moves per second)");
		System.out.println("ReferenceBoard: " + formatter.format(refMps) 
						+ " mps (moves per second)");
		System.out.println("\nBinaryBoard is " + speedup 
						+ "x faster than the 2D array reference implementation");
		
	}
		
		

}
