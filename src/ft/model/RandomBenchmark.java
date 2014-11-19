/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import com.google.common.io.ByteStreams;

public class RandomBenchmark {
	
	public static final int ITERATIONS = 10_000_000;
	
	private static int runBenchmark(Class<? extends Board> boardClass) {
		
		
		int gCount = 0;
		long pCount = 0;
		final int total = ITERATIONS;
		final int step = total / 10; 
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
			game.start();
			gCount++;
			pCount += game.plieCount();
		
		}
		long endTime = System.currentTimeMillis();
		int mps = (int) (pCount / ((float) (endTime - oStartTime) / 1000));
		
		return mps;
		
		
	}
	
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
