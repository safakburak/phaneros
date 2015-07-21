package p2p.network.inhouse.socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import p2p.application.messages.Introduction;
import p2p.application.messages.Subscription;
import p2p.network.IEndPoint;
import p2p.network.IMessage;

public class MessageServer implements IMessageEndPointListener
{
	private ServerSocket mServerSocket;
	
	private Vector<MessageEndPoint> mEndPoints = new Vector<MessageEndPoint>();
	
	private ConcurrentHashMap<String, Vector<IEndPoint>> mSubscriptions = new ConcurrentHashMap<String, Vector<IEndPoint>>();
	
	private ConcurrentHashMap<String, IEndPoint> mEndPointAdressBook = new ConcurrentHashMap<String, IEndPoint>();
	
	public MessageServer() 
	{
		new Thread(new Runnable() {
			@Override
			public void run() 
			{
				try 
				{
					mServerSocket = new ServerSocket(5555);
					
					while(true)
					{
						Socket socket = mServerSocket.accept();
						
						MessageEndPoint endPoint = new MessageEndPoint(socket, MessageServer.this);
						
						mEndPoints.add(endPoint);
					}
				} 
				catch (Exception exception) 
				{
					exception.printStackTrace();
				}
			}
		}, "Accepting Thread").start();
	}

	@Override
	public void onReceive(IEndPoint endPoint, MessageEnvelope envelope) 
	{
		IMessage message = envelope.getMessage();
		
		if(message instanceof Introduction)
		{
			mEndPointAdressBook.put(envelope.getSourceId(), endPoint);
		}
		else if(message instanceof Subscription)
		{
			Subscription subscriptionMessage = (Subscription) message;
			
			String topic = subscriptionMessage.getTopic();
			
			if(subscriptionMessage.isSubscribe())
			{
				if(!mSubscriptions.containsKey(topic))
				{
					mSubscriptions.put(topic, new Vector<IEndPoint>());
				}
				
				mSubscriptions.get(topic).add(endPoint);
			}
			else
			{
				if(mSubscriptions.containsKey(topic))
				{
					mSubscriptions.get(topic).remove(endPoint);
				}
			}
		}
		else
		{
			if(envelope.getDestinationId() == null)
			{
				if(envelope.getTopic() != null)
				{
					Vector<IEndPoint> endPoints = mSubscriptions.get(envelope.getTopic());
					
					if(endPoints != null)
					{
						int targetIndex = 0;
						
						for(targetIndex = 0; targetIndex < endPoints.size(); targetIndex++)
						{
							MessageEndPoint target = (MessageEndPoint) endPoints.get(targetIndex);
							
							if(target != endPoint)
							{
								target.send(envelope);
							}
						}
					}
				}
			}
			else
			{
				MessageEndPoint destination = (MessageEndPoint) mEndPointAdressBook.get(envelope.getDestinationId());
				
				if(destination != null)
				{
					destination.send(envelope);
				}
			}
			
		}
	}
}
