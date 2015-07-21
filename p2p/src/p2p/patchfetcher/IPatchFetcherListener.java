package p2p.patchfetcher;

import p2p.patch.Patch;

public interface IPatchFetcherListener 
{
	public void onComplete(Patch patch);
}
