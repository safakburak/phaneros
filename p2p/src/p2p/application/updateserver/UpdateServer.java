package p2p.application.updateserver;

import p2p.application.messages.PatchUpdate;
import p2p.application.messages.PositionUpdate;
import p2p.network.EndPointFactory;
import p2p.network.IEndPoint;
import p2p.network.IMessageListener;

public class UpdateServer 
{
	private IEndPoint mEndPoint;
	
	private String mId;
	
	public UpdateServer(String id) 
	{
		mId = id;
		
		mEndPoint = EndPointFactory.createEndPoint(mId);

	}
	
	public void start() {
		

		mEndPoint.addMessageListener(PositionUpdate.class, new IMessageListener<PositionUpdate>() {
			@Override
			public void onMessage(PositionUpdate message, String source) 
			{
				mEndPoint.castMessage("updateServerBroadcast", message);
			}
		});
		
		mEndPoint.addMessageListener(PatchUpdate.class, new IMessageListener<PatchUpdate>() {
			@Override
			public void onMessage(PatchUpdate message, String source) 
			{
				mEndPoint.castMessage("updateServerBroadcast", message);
			}
		});
	}
}
