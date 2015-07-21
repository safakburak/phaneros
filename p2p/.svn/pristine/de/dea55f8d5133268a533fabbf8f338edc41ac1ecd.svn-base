package p2p.patchfetcher;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import p2p.cache.Cache;
import p2p.data.IntPair;
import p2p.patch.Patch;

public class PatchFetcher
{
	private Cache mPatchCache;
	
	private ConcurrentLinkedQueue<IntPair> mLoadQueue = new ConcurrentLinkedQueue<IntPair>();
	
	private PatchSource mPatchSource;
	
	private ArrayList<Thread> mFetchingThreads = new ArrayList<Thread>();
	
	public PatchFetcher(Cache patchCache, PatchSource patchSource) 
	{
		mPatchCache = patchCache;
		
		mPatchSource = patchSource;
		
		for (int i = 0; i < 10; i++)
		{
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() 
				{
					while(true)
					{
						IntPair patchPos = mLoadQueue.poll();
						
						if (patchPos == null)
						{
							synchronized (mLoadQueue) 
							{
								try 
								{
									mLoadQueue.wait();
								} 
								catch (InterruptedException e) 
								{
									e.printStackTrace();
								}
							}
						}
						else
						{
							Patch patch = mPatchSource.getPatch(patchPos);
							mPatchCache.put(patch);
						}
					}
				}
			});
			
			mFetchingThreads.add(thread);
			
			thread.start();
		}
	}
	
	public void fetch(IntPair patchPos)
	{
		if(patchPos.getX() >= 0 && patchPos.getY() >=0)
		{
			if(mPatchCache.getPatch(patchPos) == null)
			{
				if(!mLoadQueue.contains(patchPos))
				{
					mLoadQueue.add(patchPos);

					synchronized (mLoadQueue) 
					{
						mLoadQueue.notify();
					}
				}
			}
		}
	}
}
