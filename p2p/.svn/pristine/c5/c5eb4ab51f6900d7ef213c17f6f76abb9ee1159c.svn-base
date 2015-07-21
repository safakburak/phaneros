package p2p.cache;

import p2p.patch.Patch;


public abstract class Disposer 
{
	protected Cache mPatchCache;

	public void setPatchCache(Cache cache) 
	{
		mPatchCache = cache;
	}
	
	public abstract void onPatchFetch(Patch patch);
	
	public abstract void check();
}
