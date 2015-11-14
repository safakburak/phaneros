package p2p.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	public final static int TRACE	= 4;
	public final static int INFO	= 3;
	public final static int WARNING	= 2;
	public final static int ERROR	= 1;
	
	private static Logger instance;
	
	
	private PrintStream stream;
	
	private int level;
	
	public Logger(PrintStream stream, int level) {
		
		this.stream = stream;
		this.level = level;
	}
	
	public void println(String message) {
		
		this.stream.println(message);
	} 
	
	public static void init(int level) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		
		File logFile = new File("log/" + dateFormat.format(new Date()) + ".log");
		
		try {
			
			logFile.createNewFile();
			instance = new Logger(new PrintStream(new FileOutputStream(logFile)), level);
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
			init(System.out, level);
		}
		
	}
	
	public static void init(PrintStream stream, int level) {
		
		instance = new Logger(stream, level);
	}
	
	public static void log(String message, int level) {
		
		if(level <= instance.level) {
			
			instance.println(message);
		}
	}
	
	public static void log(Object obj, int level) {

		if(level <= instance.level) {

			instance.println(obj.toString());
		}
	}
	
	public static void log(String message) {
		
		log(message, INFO);
	}
	
	public static void log(Object obj) {
		
		log(obj, INFO);
	}
}
