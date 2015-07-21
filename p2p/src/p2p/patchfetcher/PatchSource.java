package p2p.patchfetcher;

import p2p.data.IntPair;
import p2p.patch.Patch;


public interface PatchSource 
{
	public Patch getPatch(IntPair coords);
}
