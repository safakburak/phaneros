package p2p.cache;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import p2p.constants.Constants;
import p2p.data.IntPair;
import p2p.patch.Patch;


public class Cache implements Iterable<Patch>
{
	private ConcurrentHashMap<IntPair, Patch> mMap;
	
	private Disposer mDisposer;
	
	private int mMaxSize;
	
	public Cache(int maxSize, Disposer disposer) 
	{
		mMaxSize = maxSize;
		
		mMap = new ConcurrentHashMap<IntPair, Patch>();
		
		mDisposer = disposer;
		
		if(mDisposer != null)
		{
			mDisposer.setPatchCache(this);
		}
	}
	
	public Patch getPatch(IntPair patchPos)
	{
		Patch result = mMap.get(patchPos);

		if(mDisposer != null)
		{
			mDisposer.onPatchFetch(result);
		}
		
		return result; 
	}
	
	public Patch getPatchForPosition(IntPair cellPos)
	{
		Patch result = mMap.get(new IntPair(cellPos.getX() / Constants.PATCH_SIZE, cellPos.getY() / Constants.PATCH_SIZE));
		
		if(mDisposer != null)
		{
			mDisposer.onPatchFetch(result);
		}
		
		return result;
	}
	
	public void put(Patch patch)
	{
		if(patch != null)
		{
			mMap.put(patch.getPos(), patch);
			
			if(mDisposer != null)
			{
				mDisposer.onPatchFetch(patch);
				mDisposer.check();
			}
		}
	}

	@Override
	public Iterator<Patch> iterator() 
	{
		return mMap.values().iterator();
	}
	
	public void dispose(Patch patch)
	{
		if(patch != null)
		{
			mMap.remove(patch.getPos());
		}
	}

	public int size() 
	{
		return mMap.size();
	}
	
	public int getMaxSize()
	{
		return mMaxSize;
	}
}
