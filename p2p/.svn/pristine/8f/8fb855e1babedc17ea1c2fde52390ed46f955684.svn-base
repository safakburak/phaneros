package p2p.cache;

import java.util.concurrent.ConcurrentLinkedQueue;

import p2p.patch.Patch;

public class LruDisposer extends Disposer
{
	private ConcurrentLinkedQueue<Patch> mPatchList= new ConcurrentLinkedQueue<Patch>();
	
	@Override
	public void onPatchFetch(Patch patch) 
	{
		if(patch != null)
		{
			if(mPatchList.contains(patch))
			{
				mPatchList.remove(patch);
			}
			
			mPatchList.add(patch);
		}
	}
	
	@Override
	public void check() 
	{
		while(mPatchCache.size() > mPatchCache.getMaxSize())
		{
			Patch toBeDisposed = mPatchList.poll();
			
			mPatchCache.dispose(toBeDisposed);
		}
	}
}
