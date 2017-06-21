package actionsim.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Logger {

	public static int TRACE = 0;
	public static int INFO = 1;

	private static PrintStream stream;
	private static int level;

	public static void init(int level) {

		File file = new File("log");
		file.mkdirs();

		try {

			init(new PrintStream(new File("log/" + System.currentTimeMillis() + ".log")), level);

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}

	public static void init(PrintStream stream, int level) {

		Logger.stream = stream;
		Logger.level = level;
	}

	public static void log(String string) {

		log(string, INFO);
	}

	public static void log(String string, int level) {

		if (level >= Logger.level) {

			stream.println(string);
		}
	}

	public static void log(Object obj, int level) {

		log(obj.toString(), level);
	}

	public static void setLevel(int level) {

		Logger.level = level;
	}
}
