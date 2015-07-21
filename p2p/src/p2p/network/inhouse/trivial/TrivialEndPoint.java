package p2p.network.inhouse.trivial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import p2p.application.loop.Loop;
import p2p.application.loop.ILoopListener;
import p2p.network.IEndPoint;
import p2p.network.IMessage;
import p2p.network.IForwardListener;
import p2p.network.IMessageListener;

public class TrivialEndPoint implements IEndPoint 
{
	private static HashMap<String, TrivialEndPoint> sEndPoints = new HashMap<String, TrivialEndPoint>();
	private static HashMap<String, ArrayList<TrivialEndPoint>> sTopics = new HashMap<String, ArrayList<TrivialEndPoint>>();
	
	static
	{
		Loop.setTimer(new ILoopListener() {
			@Override
			public void loopCallback(long deltaTime) 
			{
				step();
			}
		});
	}
	
	public static void step()
	{
		for(TrivialEndPoint endPoint : sEndPoints.values())
		{
			endPoint.processIncoming();
		}
		
		for(TrivialEndPoint endPoint : sEndPoints.values())
		{
			endPoint.processOutgoing();
		}
	}
	
	private String mId;
	
	private Queue<TrivialEnvelope> mIncoming = new ConcurrentLinkedQueue<TrivialEnvelope>();
	private Queue<TrivialEnvelope> mOutgoing = new ConcurrentLinkedQueue<TrivialEnvelope>();

	private HashMap<Class<?>, ArrayList<IMessageListener<IMessage>>> mListeners = new HashMap<Class<?>, ArrayList<IMessageListener<IMessage>>>();
	
	public TrivialEndPoint(String id) 
	{
		mId = id;
		
		subscribe(mId);
		
		sEndPoints.put(mId, this);
	}
	
	@Override
	public void addMessageListener(Class<?> messageClass, IMessageListener listener) 
	{
		if(mListeners.containsKey(messageClass) == false)
		{
			mListeners.put(messageClass, new ArrayList<IMessageListener<IMessage>>());
		}
		
		mListeners.get(messageClass).add(listener);
	}

	@Override
	public void subscribe(String topic) 
	{
		if(sTopics.containsKey(topic) == false)
		{
			sTopics.put(topic, new ArrayList<TrivialEndPoint>());
		}
		
		sTopics.get(topic).add(this);
	}

	@Override
	public void unsubscribe(String topic) 
	{
		if(sTopics.containsKey(topic))
		{
			sTopics.get(topic).remove(this);
		}
	}

	@Override
	public void castMessage(String topic, IMessage message) 
	{
		TrivialEnvelope envelope = new TrivialEnvelope();
		envelope.source = mId;
		envelope.topic = topic;
		envelope.message = message;
		
		mOutgoing.add(envelope);
	}

	@Override
	public void sendMessage(String dest, IMessage message) 
	{
		TrivialEnvelope envelope = new TrivialEnvelope();
		envelope.source = mId;
		envelope.destination = dest;
		envelope.message = message;
		
		mOutgoing.add(envelope);
	}
	
	private void processIncoming()
	{
		while(!mIncoming.isEmpty())
		{
			TrivialEnvelope envelope = mIncoming.poll();
			
			ArrayList<IMessageListener<IMessage>> listeners = mListeners.get(envelope.message.getClass());
			
			if(listeners != null)
			{
				for(IMessageListener<IMessage> listener : listeners)
				{
					listener.onMessage(envelope.message, envelope.source);
				}
			}
		}
	}
	
	private void processOutgoing()
	{
		while(!mOutgoing.isEmpty())
		{
			TrivialEnvelope envelope = mOutgoing.poll();
			
			if (envelope.destination != null)
			{
				TrivialEndPoint dest = sEndPoints.get(envelope.destination);
				dest.mIncoming.add(envelope);
			}
			
			if(envelope.topic != null)
			{
				ArrayList<TrivialEndPoint> dests = sTopics.get(envelope.topic);
				
				if(dests != null)
				{
					for(TrivialEndPoint dest : dests)
					{
						dest.mIncoming.add(envelope);
					}
				}
			}
			
		}
	}

	@Override
	public void setForwardDecider(IForwardListener controller) 
	{
		
	}
}
