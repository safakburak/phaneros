package actionsim.core;

public class MessageReceiveAction extends Action {

	private Message message;
	
	public MessageReceiveAction(Message message) {

		super(0);
		
		this.message = message;
	}

	@Override
	protected void run() {
		
	}

	public Message getMessage() {
		
		return message;
	}
}
