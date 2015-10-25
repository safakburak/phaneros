package actionsim.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private static Logger instance;
	
	private PrintStream stream;
	
	public Logger(PrintStream stream) {
		
		this.stream = stream;
	}
	
	public void println(String message) {
		
		this.stream.println(message);
	} 
	
	public static void init() {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		
		File logFile = new File(dateFormat.format(new Date()) + ".log");
		
		try {
			
			logFile.createNewFile();
			instance = new Logger(new PrintStream(new FileOutputStream(logFile)));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
			init(System.out);
		}
		
	}
	
	public static void init(PrintStream stream) {
		
		instance = new Logger(stream);
	}
	
	public static void log(String message) {
		
		instance.println(message);
	}
	
	public static void log(Object obj) {
		
		instance.println(obj.toString());
	}
}
