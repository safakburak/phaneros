package p2p.log;

import java.util.HashMap;

import p2p.application.loop.ILoopListener;
import p2p.application.loop.Loop;
import p2p.data.IntPair;

public class Stats 
{
	private static HashMap<String, Integer> sQuerySend = new HashMap<String, Integer>();
	private static HashMap<String, Integer> sQueryReceive = new HashMap<String, Integer>();
	private static HashMap<String, Integer> sQueryForward = new HashMap<String, Integer>();
	private static HashMap<String, Integer> sQueryHit = new HashMap<String, Integer>();
	private static HashMap<String, Integer> sQueryHitForwarded = new HashMap<String, Integer>();
	private static HashMap<String, Integer> sQueryMiss = new HashMap<String, Integer>();
	private static HashMap<String, Integer> sRequestSend = new HashMap<String, Integer>();
	private static HashMap<String, Integer> sRequestReceive = new HashMap<String, Integer>();
	private static HashMap<String, Integer> sRequestHit = new HashMap<String, Integer>();
	private static HashMap<String, Integer> sRequestHitForwarded = new HashMap<String, Integer>();
	private static HashMap<String, Integer> sRequestMiss = new HashMap<String, Integer>();
	
	private static HashMap<String, Integer> sPositionUpdateSent = new HashMap<String, Integer>();
	private static HashMap<String, Integer> sPositionUpdateReceived = new HashMap<String, Integer>();
	
	private static long sReportingPeriod = 1000; // ms in simulation time 
	private static long sSeconds = 0;
	
	private static Long sQueryHopCount = 0l;
	private static Long sRequestHopCount = 0l;
	private static Long sPositionUpdateHopCount = 0l;
	
	public static void start()
	{
		Loop.setTimer(new ILoopListener() {
			
			private long mSinceLastReport = 0;
			
			@Override
			public void loopCallback(long deltaTime) 
			{
				mSinceLastReport += deltaTime;
				
				if(mSinceLastReport >= sReportingPeriod)
				{
					mSinceLastReport -= sReportingPeriod;
					
					sSeconds++;
					
					report(sSeconds);
				}
			}
		});
	}
	
	public static void positionUpdateSent(String sender)
	{
		increment(sPositionUpdateSent, sender);
	}
	
	public static void positionUpdateReceived(String receiver)
	{
		increment(sPositionUpdateReceived, receiver);
	}
	
	public static void incrementQueryHopCount()
	{
		synchronized (sQueryHopCount) 
		{
			sQueryHopCount++;
		}
	}
	
	public static void incrementRequestHopCount()
	{
		synchronized (sRequestHopCount) 
		{
			sRequestHopCount++;
		}
	}
	
	public static void incrementPositionUpdateHopCount() 
	{
		synchronized (sPositionUpdateHopCount) 
		{
			sPositionUpdateHopCount++;
		}
	}
	
	public static void querySent(String source, String destination, IntPair query)
	{
		increment(sQuerySend, source);
	}
	
	public static void queryForwarded(String forwarder, String source, String requester, String destination, IntPair query)
	{
		increment(sQueryForward, forwarder);
	}
	
	public static void queryReceived(String receiver, String source, String requester, IntPair query)
	{
		increment(sQueryReceive, receiver);
	}
	
	public static void queryHit(String receiver, String source, String requester, IntPair query, boolean isForwarded)
	{
		increment(sQueryHit, receiver);
		
		if(isForwarded)
		{
			increment(sQueryHitForwarded, receiver);
		}
	}
	
	public static void queryMiss(String receiver, String source, String requester, IntPair query)
	{
		increment(sQueryMiss, receiver);
	}
	
	public static void requestSent(String source, String destination, IntPair query)
	{
		increment(sRequestSend, source);
	}
	
	public static void requestReceived(String receiver, String requester, IntPair query)
	{
		increment(sRequestReceive, receiver);
	}
	
	public static void requestHit(String receiver, String requester, IntPair query, boolean isForwarded)
	{
		increment(sRequestHit, receiver);
		
		if(isForwarded)
		{
			increment(sRequestHitForwarded, receiver);
		}
	}
	
	public static void requestMiss(String receiver, String requester, IntPair query)
	{
		increment(sRequestMiss, receiver);
	}
	
	private static void increment(HashMap<String, Integer> map, String key)
	{
		synchronized (map) 
		{
			if(map.containsKey(key) == false)
			{
				map.put(key, 0);
			}
			
			map.put(key, map.get(key) + 1);
		}
	}
	
	private static long getTotal(HashMap<String, Integer> map, String prefix)
	{
		long total = 0;
		
		if(prefix == null)
		{
			for(int sum : map.values())
			{
				total += sum;
			}
		}
		else
		{
			for(String name : map.keySet())
			{
				if(name.startsWith(prefix))
				{
					total += map.get(name);
				}
			}
		}
		
		return total;
	}
	
	public static void report(long sec)
	{
		Logger.log("Query SEND: " + getTotal(sQuerySend, null));
		Logger.log("Query RECEIVE: " + getTotal(sQueryReceive, null));
		Logger.log("Query FORWARD: " + getTotal(sQueryForward, null));
		Logger.log("Query HIT: " + getTotal(sQueryHit, null));
		Logger.log("Query HIT FORWARD: " + getTotal(sQueryHitForwarded, null));
		Logger.log("Query MISS: " + getTotal(sQueryMiss, null));
		
		Logger.log("Request SEND: " + getTotal(sRequestSend, null));
		Logger.log("Request RECEIVE: " + getTotal(sRequestReceive, null));
		Logger.log("Request HIT: " + getTotal(sRequestHit, null));
		Logger.log("Request HIT FORWARD: " + getTotal(sRequestHitForwarded, null));
		Logger.log("Request MISS: " + getTotal(sRequestMiss, null));
		
		Logger.log("AVG Query Hop: " + (((double)sQueryHopCount) / ((double)getTotal(sQueryReceive, null))));
		Logger.log("AVG Request Hop: " + (((double)sRequestHopCount) / ((double)getTotal(sRequestReceive, null))));
		
		Logger.log("SERVER response: " + getTotal(sRequestReceive, "seed"));

		Logger.log("Position Update SEND: " + getTotal(sPositionUpdateSent, null));
		Logger.log("Position Update RECEIVE: " + getTotal(sPositionUpdateReceived, null));
		Logger.log("AVG Position Update Hop: " + (((double)sPositionUpdateHopCount) / ((double)getTotal(sPositionUpdateReceived, null))));
		
		Logger.log("-----------------------------" + sec + "-------------------------------------");
	}
	
}
