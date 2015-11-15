package p2p.application.peer.components;

import java.util.ArrayList;

import p2p.application.messages.PatchAvailable;
import p2p.application.messages.PatchEnvelope;
import p2p.application.messages.PatchQuery;
import p2p.application.messages.PatchRequest;
import p2p.application.peer.Peer;
import p2p.cache.Cache;
import p2p.data.IntPair;
import p2p.network.IEndPoint;
import p2p.network.IMessageListener;
import p2p.patch.Patch;
import p2p.stats.Stats;
import p2p.visibility.VisibilityMap;

public class RequestTracker
{
	private Peer mOwner;
	
	private IEndPoint mEndPoint;
	
	private Cache mPatchCache;
	
	private VisibilityMap mVisibilityMap;
	
	public RequestTracker(Peer owner, IEndPoint endPoint, Cache patchCache, VisibilityMap visibilityMap) 
	{
		mOwner = owner;
		
		mEndPoint = endPoint;
		
		mPatchCache = patchCache;
		
		mVisibilityMap = visibilityMap;
		
		mEndPoint.addMessageListener(PatchQuery.class, new IMessageListener<PatchQuery>() {
			@Override
			public void onMessage(PatchQuery query, String source) 
			{
				Stats.queryReceived(mOwner.getId(), source, query.getRequesterId(), query.getPatchPos());
				
				Patch patch = mPatchCache.getPatch(query.getPatchPos()); 
				
				if(patch == null)
				{
					Stats.queryMiss(mOwner.getId(), source, query.getRequesterId(), query.getPatchPos());
					
					if(query.isForwarded() == false)
					{
						//aranan objenin durduğu yerin PVS'i mesajı zaten aldı. tekrar göndermiyoruz.
						ArrayList<IntPair> exclude = mVisibilityMap.get(query.getPatchPos());
						
						for(IntPair pvsPatch : mVisibilityMap.get(mOwner.currentPatch))
						{
							if(exclude.contains(pvsPatch) == false)
							{
								String topic = "content_" + pvsPatch.getX() + "_" + pvsPatch.getY();
								
								PatchQuery queryForwarded = new PatchQuery(query.getRequesterId(), query.getPatchPos(), true);
								
								mEndPoint.castMessage(topic, queryForwarded);
								
								Stats.queryForwarded(mOwner.getId(), source, query.getRequesterId(), topic, query.getPatchPos());
								
								break;
							}
						}
					}	
				}
				else
				{
					Stats.queryHit(mOwner.getId(), source, query.getRequesterId(), query.getPatchPos(), query.isForwarded());
					
					PatchAvailable patchAvailable = new PatchAvailable(query.getPatchPos(), query.isForwarded());
					
					mEndPoint.sendMessage(query.getRequesterId(), patchAvailable);
				}
			}
		});
		
		mEndPoint.addMessageListener(PatchRequest.class, new IMessageListener<PatchRequest>() {
			@Override
			public void onMessage(PatchRequest request, String source) 
			{
				Stats.requestReceived(mOwner.getId(), source, request.getPatchPos());
				
				Patch patch = mPatchCache.getPatch(request.getPatchPos());
				
				if(patch == null)
				{
					Stats.requestMiss(mOwner.getId(), source, request.getPatchPos());
				}
				else
				{
					Stats.requestHit(mOwner.getId(), source, request.getPatchPos(), request.isForwarded());
					
					PatchEnvelope patchEnvelope = new PatchEnvelope(patch);
					
					mEndPoint.sendMessage(source, patchEnvelope);
				}
			}
		});
	}
}
