package p2p.network;


public interface IEndPoint {

	public void addMessageListener(Class<?> messageClass, IMessageListener listener);
	
	public void subscribe(String topic);
	
	public void unsubscribe(String topic);
	
	public void castMessage(String topic, IMessage message);
	
	public void sendMessage(String dest, IMessage message);
	
	public void setForwardDecider(IForwardListener decider);
}
