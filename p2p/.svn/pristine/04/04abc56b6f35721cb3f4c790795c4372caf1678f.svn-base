package p2p.network.planetsim;

import java.util.ArrayList;
import java.util.HashMap;

import p2p.application.messages.PatchQuery;
import p2p.application.messages.PatchRequest;
import p2p.application.messages.PositionUpdate;
import p2p.log.Stats;
import p2p.network.IEndPoint;
import p2p.network.IForwardListener;
import p2p.network.IMessage;
import p2p.network.IMessageListener;
import planet.commonapi.Message;
import planet.commonapi.NodeHandle;
import planet.commonapi.exception.InitializationException;
import planet.generic.commonapi.factory.GenericFactory;
import planet.scribe.IHopController;
import planet.scribe.IHopListener;
import planet.scribe.Scribe;
import planet.scribe.ScribeClient;
import planet.scribe.ScribeContent;
import planet.scribe.Topic;

public class PSimEndPoint implements IEndPoint, ScribeClient 
{
	private IForwardListener mForwardDecider;
	
	private Scribe mScribe;

	private String mId;
	
	private HashMap<Class<?>, ArrayList<IMessageListener<IMessage>>> mListeners = new HashMap<Class<?>, ArrayList<IMessageListener<IMessage>>>();
	
	public PSimEndPoint(String id, Scribe scribe)
	{
		mId = id;
		mScribe = scribe;
		
		subscribe(mId);
		
		mScribe.setHopListener(new IHopListener() {
			
			@Override
			public void onRequestHop() 
			{
				Stats.incrementRequestHopCount();
			}
			
			@Override
			public void onQueryHop() 
			{
				Stats.incrementQueryHopCount();
			}
			
			@Override
			public void onPositionUpdateHop() 
			{
				Stats.incrementPositionUpdateHopCount();
			}
		});
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
	public void subscribe(String topicName) 
	{
		try
		{
			mScribe.subscribe(new Topic(GenericFactory.buildKey(topicName)), this);
		} 
		catch (InitializationException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void unsubscribe(String topicName) 
	{
		try
		{
			mScribe.unsubscribe(new Topic(GenericFactory.buildKey(topicName)), this);
		} 
		catch (InitializationException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void castMessage(String topicName, IMessage message) 
	{
		try
		{
			mScribe.publish(new Topic(GenericFactory.buildKey(topicName)), new ScribeEnvelope(mId, message));
		} 
		catch (InitializationException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(String dest, IMessage message) 
	{
		castMessage(dest, message);
	}

	@Override
	public boolean anycast(Topic topic, ScribeContent content) 
	{
		return true;
	}

	@Override
	public void deliver(Topic topic, ScribeContent content) 
	{
		if (content instanceof ScribeEnvelope)
		{
			ScribeEnvelope envelope = (ScribeEnvelope) content;
			
			ArrayList<IMessageListener<IMessage>> listeners = mListeners.get(envelope.getMessage().getClass());
			
			if(listeners != null)
			{
				for(IMessageListener<IMessage> listener : listeners)
				{
					listener.onMessage(envelope.getMessage(), envelope.getSource());
				}
			}
		}
	}

	@Override
	public void childAdded(Topic topic, NodeHandle child) 
	{
		
	}

	@Override
	public void childRemoved(Topic topic, NodeHandle child) 
	{
		
	}

	@Override
	public void subscribeFailed(Topic topic) 
	{
	}
	
	private class ScribeEnvelope implements ScribeContent, Message
	{
		private String mSource;
		
		private IMessage mMessage;
		
		public ScribeEnvelope(String source, IMessage message) 
		{
			mSource = source;
			mMessage = message;
		}
		
		public String getSource()
		{
			return mSource;
		}
		
		public IMessage getMessage()
		{
			return mMessage;
		}
		
		public boolean isQuery()
		{
			return mMessage instanceof PatchQuery;
		}
		
		public boolean isRequest()
		{
			return mMessage instanceof PatchRequest;
		}
		
		public boolean isPositionUpdate()
		{
			return mMessage instanceof PositionUpdate;
		}
	}
	
	@Override
	public void setForwardDecider(IForwardListener decider) 
	{
		mForwardDecider = decider;
		
		mScribe.setHopController(new IHopController() {
			
			@Override
			public boolean isHopAllowed(ScribeContent message) 
			{
				if(message instanceof ScribeEnvelope)
				{
					ScribeEnvelope envelope = (ScribeEnvelope) message;
					
					if(mForwardDecider.forwardAllowed(envelope.getSource(),  envelope.getMessage()) == false) 
					{
						return false;
					}
				}
				
				return true;
			}
		});
	}
}
