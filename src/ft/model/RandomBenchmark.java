package ft.model;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import com.google.common.io.ByteStreams;

public class RandomBenchmark {
	
	public static void main(String[] args) {
		
		int gCount = 0;
		long pCount = 0;
		final int step = 1_000_000; 
		final int total = 100_000_000;
		long startTime = System.currentTimeMillis();
		long oStartTime = System.currentTimeMillis();
		PrintStream nullStream = new PrintStream(ByteStreams.nullOutputStream());
		
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALY);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');
		
		
		while (gCount < total) {
			
			if (gCount % step == 0 && gCount != 0) {
				long endTime = System.currentTimeMillis();
				int gps = (int) (step / ((float) (endTime - startTime) / 1000));
				String progress = gCount + " of " + total;
				System.out.println(progress + " : " + formatter.format(gps) + " gps");
				startTime = System.currentTimeMillis();
			}
			
			
		
			Game game = new Game(new Player[]{new ComputerPlayer(new RandomStrategy())
							, new ComputerPlayer(new RandomStrategy())}
							, nullStream, false);
			game.start();
			gCount++;
			pCount += game.plieCount();
		
		}
		long endTime = System.currentTimeMillis();
		System.out.println(formatter.format(
						(int) (total / ((float) (endTime - oStartTime) / 1000))) 
						+ " gps (games per second)");
		
		int mps = (int) (pCount / ((float) (endTime - oStartTime) / 1000));
		System.out.println(formatter.format(mps) + " mps (moves per second");
		
	}

}
