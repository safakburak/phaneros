package p2p.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger 
{
	private static FileWriter sWriter;

	public static void start(String suffix)
	{
		try 
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			File file = new File("../log/" + dateFormat.format(new Date()) + "_" + suffix + ".txt");
			file.createNewFile();
			sWriter = new FileWriter(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void log(String msg)
	{
		try 
		{
			sWriter.write(msg + "\n");
			sWriter.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
