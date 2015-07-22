package p2p.application.peer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import p2p.application.loop.Loop;
import p2p.application.loop.ILoopListener;
import p2p.application.peer.components.FetchingManager;
import p2p.application.peer.components.MovementController;
import p2p.application.peer.components.RequestTracker;
import p2p.application.peer.components.SubscriptionManager;
import p2p.application.peer.components.UpdateManager;
import p2p.application.peer.components.UpdateTracker;
import p2p.cache.LruDisposer;
import p2p.cache.Cache;
import p2p.constants.Constants;
import p2p.data.IntPair;
import p2p.network.EndPointFactory;
import p2p.network.IEndPoint;
import p2p.renderer.Renderer;
import p2p.visibility.Visibility;
import p2p.visibility.VisibilityMap;


@SuppressWarnings("unused")
public class Peer 
{
	public String mId;
	
	public Boolean isUserControl;
	public IntPair currentPos;
	public IntPair currentPatch;
	public IntPair targetPatch;
	public ConcurrentHashMap<String, IntPair> peerPoses = new ConcurrentHashMap<String, IntPair>();
	
	private VisibilityMap mVisibility;
	
	private IEndPoint mEndPoint;
	
	private Cache mPatchCache;
	
	private FetchingManager mFetchingManager;
	private MovementController mMovementController;
	private UpdateManager mUpdateManager;
	private UpdateTracker mUpdateTracker;
	private SubscriptionManager mSubscriptionManager;
	private RequestTracker mRequestTracker;
	
	private Renderer mRenderer;
	
	public Peer(String id, boolean displayGui, boolean p2pContentDistribute, boolean p2pUpdates, boolean pvsUpdates) 
	{
		mId = id;
		
		mVisibility = Visibility.sVisibilityMap;
		
		mPatchCache = new Cache(20, new LruDisposer());
		
		int patchX = (int) (Math.random() * 10);
		int patchY = (int) (Math.random() * 10);
		
		currentPos = new IntPair(patchX * Constants.PATCH_SIZE, patchY * Constants.PATCH_SIZE);
		currentPatch = new IntPair(patchX, patchY);
		isUserControl = false;
		
		mEndPoint = EndPointFactory.createEndPoint(mId);
		
		mFetchingManager = new FetchingManager(this, mEndPoint, mPatchCache, mVisibility, p2pContentDistribute);
		
		if(displayGui)
		{
			mRenderer = new Renderer(this, mId, mPatchCache, mVisibility);
		}
		
		mMovementController = new MovementController(this, mPatchCache);
		
		mUpdateManager = new UpdateManager(this, mEndPoint, p2pUpdates, pvsUpdates);
		
		mUpdateTracker = new UpdateTracker(this, mEndPoint, mVisibility, p2pUpdates, pvsUpdates);
		
		mSubscriptionManager = new SubscriptionManager(this, mEndPoint, mVisibility);
		
		if(p2pContentDistribute == true)
		{
			mRequestTracker = new RequestTracker(this, mEndPoint, mPatchCache, mVisibility);
		}
		
		Loop.setTimer(new ILoopListener() {
			
			@Override
			public void loopCallback(long deltaTime) 
			{
				mFetchingManager.update(deltaTime);
				
				mMovementController.update(deltaTime);
				
				mSubscriptionManager.update(deltaTime);
				
				mUpdateManager.update(deltaTime);
			}
		});
	}

	public String getId() 
	{
		return mId;
	}
}