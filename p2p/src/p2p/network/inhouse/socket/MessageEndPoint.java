package p2p.network.inhouse.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import p2p.application.messages.Introduction;
import p2p.application.messages.Subscription;
import p2p.network.IEndPoint;
import p2p.network.IMessage;
import p2p.network.IForwardListener;
import p2p.network.IMessageListener;

public class MessageEndPoint implements IEndPoint
{
	private ObjectInputStream mInputStream;
	private ObjectOutputStream mOutputStream;
	
	private ConcurrentLinkedQueue<MessageEnvelope> mIncomingMessages = new ConcurrentLinkedQueue<MessageEnvelope>();
	private ConcurrentLinkedQueue<MessageEnvelope> mOutgoingMessages = new ConcurrentLinkedQueue<MessageEnvelope>();
	
	private ConcurrentHashMap<Class<?>, Vector<IMessageListener>> mListeners = new ConcurrentHashMap<Class<?>, Vector<IMessageListener>>(); 
	
	private IMessageEndPointListener mEnvelopeListener;
	
	private String mId;
	
	public MessageEndPoint(String id)
	{
		mId = id;
		
		subscribe(mId);
		
		try 
		{
			Socket socket = new Socket("localhost", 5555);
			
			initialize(socket, false);
		} 
		catch (Exception e) 
		{
			System.out.println(mId + " cannot connect to messaging server.");
		}
	}
	
	public MessageEndPoint(Socket socket, IMessageEndPointListener envelopeListener) 
	{
		mEnvelopeListener = envelopeListener;
		
		initialize(socket, true);
	}

	private void initialize(Socket socket, boolean isServer) 
	{
		try 
		{
			if(isServer)
			{
				mInputStream = new ObjectInputStream(socket.getInputStream());
				mOutputStream = new ObjectOutputStream(socket.getOutputStream());
			}
			else
			{
				mOutputStream = new ObjectOutputStream(socket.getOutputStream());
				mInputStream = new ObjectInputStream(socket.getInputStream());
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() 
			{
				while(true)
				{
					try 
					{
						MessageEnvelope envelope = (MessageEnvelope) mInputStream.readObject();
						
						mIncomingMessages.add(envelope);
						
						synchronized (mIncomingMessages) 
						{
							mIncomingMessages.notifyAll();
						}
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() 
			{
				while(true)
				{
					MessageEnvelope envelope = mOutgoingMessages.poll();
					
					if(envelope == null)
					{
						synchronized (mOutgoingMessages) 
						{
							try 
							{
								mOutgoingMessages.wait();
							} 
							catch (InterruptedException e) 
							{
								e.printStackTrace();
							}
						}
					}
					else
					{
						try 
						{
							mOutputStream.writeObject(envelope);
							mOutputStream.flush();
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() 
			{
				while(true)
				{
					MessageEnvelope envelope = mIncomingMessages.poll();
					
					if(envelope == null)
					{
						synchronized (mIncomingMessages) 
						{
							try 
							{
								mIncomingMessages.wait();
							} 
							catch (InterruptedException e) 
							{
								e.printStackTrace();
							}
						}
					}
					else
					{
						Vector<IMessageListener> listeners = mListeners.get(envelope.getMessage().getClass());
						
						if(listeners != null)
						{
							for (IMessageListener listener : listeners) 
							{
								listener.onMessage(envelope.getMessage(), envelope.getSourceId());
							}
						}
						
						if(mEnvelopeListener != null)
						{
							mEnvelopeListener.onReceive(MessageEndPoint.this, envelope);
						}
					}
				}
			}
		}).start();
		
		MessageEnvelope envelope = new MessageEnvelope(mId, new Introduction());
		send(envelope);
	}
	
	public void castMessage(String topic, IMessage message)
	{
		MessageEnvelope envelope = new MessageEnvelope(mId, message);
		
		envelope.setTopic(topic);
		
		send(envelope);
	}
	
	public void sendMessage(String destination, IMessage message)
	{
		MessageEnvelope envelope = new MessageEnvelope(mId, message);
		
		envelope.setDestination(destination);
		
		send(envelope);
	}
	
	public void send(MessageEnvelope envelope)
	{
		mOutgoingMessages.add(envelope);
		
		synchronized (mOutgoingMessages) 
		{
			mOutgoingMessages.notifyAll();
		}
	}
	
	public void subscribe(String topic)
	{
		MessageEnvelope envelope = new MessageEnvelope(mId, new Subscription(topic, true));
		send(envelope);
	}
	
	public void unsubscribe(String topic)
	{
		MessageEnvelope envelope = new MessageEnvelope(mId, new Subscription(topic, false));
		send(envelope);
	}

	@Override
	public void addMessageListener(Class<?> messageClass, IMessageListener listener) 
	{
		if(!mListeners.containsKey(messageClass))
		{
			mListeners.put(messageClass, new Vector<IMessageListener>());
		}
		
		mListeners.get(messageClass).add(listener);
	}

	@Override
	public void setForwardDecider(IForwardListener controller) 
	{
		
	}
}
