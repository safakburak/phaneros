package p2p.application.loop;

import java.util.Vector;

public class Loop 
{
	private static Vector<ILoopListener> sClientList = new Vector<ILoopListener>();
	
	public static void setTimer(ILoopListener client)
	{
		sClientList.add(client);
	}
	
	public static void start()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				while(true)
				{
					for (ILoopListener client : sClientList) 
					{
						client.loopCallback(100);
					}
				}
			}
		}).start();
	}
}
