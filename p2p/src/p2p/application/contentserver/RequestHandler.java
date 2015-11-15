package p2p.application.contentserver;

import p2p.application.messages.PatchAvailable;
import p2p.application.messages.PatchEnvelope;
import p2p.application.messages.PatchQuery;
import p2p.application.messages.PatchRequest;
import p2p.cache.Cache;
import p2p.data.Region;
import p2p.network.IEndPoint;
import p2p.network.IMessageListener;
import p2p.patch.Patch;
import p2p.patchfetcher.PatchFetcher;
import p2p.stats.Stats;

public class RequestHandler
{
	private IEndPoint mEndPoint;
	
	private PatchFetcher mPatchFetcher;
	
	private Cache mPatchCache;
	
	private Region mResRegion;
	
	public RequestHandler(IEndPoint endPoint, Cache patchCache, PatchFetcher patchFetcher, Region resRegion) 
	{
		mEndPoint = endPoint;
		
		mPatchCache = patchCache;
		
		mPatchFetcher = patchFetcher;
		
		mResRegion = resRegion;
		
		mEndPoint.subscribe("seed");
		
		mEndPoint.addMessageListener(PatchQuery.class, new IMessageListener<PatchQuery>() {
			@Override
			public void onMessage(PatchQuery query, String source) 
			{
				if(mResRegion == null || mResRegion.contains(query.getPatchPos().getX(), query.getPatchPos().getY()))
				{
					Stats.queryReceived("seed", source, query.getRequesterId(), query.getPatchPos());
					
					Patch patch = mPatchCache.getPatch(query.getPatchPos());
					
					if(patch == null)
					{
						Stats.queryMiss("seed", source, query.getRequesterId(), query.getPatchPos());
						mPatchFetcher.fetch(query.getPatchPos());
					}
					else
					{
						Stats.queryHit("seed", source, query.getRequesterId(), query.getPatchPos(), false);
						PatchAvailable patchAvailable = new PatchAvailable(patch.getPos(), false);
						
						mEndPoint.sendMessage(query.getRequesterId(), patchAvailable);
					}
				}
			}
		});
		
		mEndPoint.addMessageListener(PatchRequest.class, new IMessageListener<PatchRequest>() {
			@Override
			public void onMessage(PatchRequest request, String source) 
			{
				Stats.requestReceived("seed", source, request.getPatchPos());
				
				Patch patch = mPatchCache.getPatch(request.getPatchPos());
				
				if(patch == null)
				{
					Stats.requestMiss("seed", source, request.getPatchPos());
				}
				else
				{
					Stats.requestHit("seed", source, request.getPatchPos(), false);
					PatchEnvelope patchEnvelope = new PatchEnvelope(patch);
					mEndPoint.sendMessage(source, patchEnvelope);
				}
			}
		});
	}
}
