package p2p.patchfetcher;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import p2p.data.IntPair;
import p2p.patch.Patch;

public class DiskPatchSource implements PatchSource {

	@Override
	public Patch getPatch(IntPair patchPos) 
	{
		Patch patch = null;
		
		try 
		{
			FileInputStream fileInputStream = new FileInputStream("../data/simple/patch_" + patchPos.getX() + "_" + patchPos.getY());
			
			ObjectInputStream in = new ObjectInputStream(fileInputStream);
			
			patch = (Patch) in.readObject();
			
			in.close();
		} 
		catch (Exception exception) 
		{
			exception.printStackTrace();
		}
		
		return patch;
	}
}
