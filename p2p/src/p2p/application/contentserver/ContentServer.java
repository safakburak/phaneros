package p2p.application.contentserver;

import p2p.cache.LruDisposer;
import p2p.cache.Cache;
import p2p.data.Region;
import p2p.network.EndPointFactory;
import p2p.network.IEndPoint;
import p2p.patchfetcher.DiskPatchSource;
import p2p.patchfetcher.PatchFetcher;

public class ContentServer 
{
	private Cache mPatchCache;
	
	private PatchFetcher mPatchFetcher;
	
	private IEndPoint mEndPoint;
	
	private String mId;
	
	private RequestHandler mRequestHandler;
	
	private Region mResRegion = null;
	
	public ContentServer(String id, Region resRegion) 
	{
		mId = id;
		mResRegion = resRegion;
		
		mEndPoint = EndPointFactory.createEndPoint(mId);
	}
	
	public void start() {
		
		mPatchCache = new Cache(1000, new LruDisposer());
		
		mPatchFetcher = new PatchFetcher(mPatchCache, new DiskPatchSource());
		
		mRequestHandler = new RequestHandler(mEndPoint, mPatchCache, mPatchFetcher, mResRegion);
	}
}
