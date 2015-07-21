package p2p.patch;

import java.io.Serializable;

import p2p.constants.Constants;
import p2p.data.IntPair;

@SuppressWarnings("serial")
public class Patch implements Serializable
{
	private IntPair mPatchPos;
	
	private int[][] mData;
	
	public Patch(IntPair coords) 
	{
		mPatchPos = coords;
	}
	
	public Patch(int x, int y) 
	{
		mPatchPos = new IntPair(x, y);
	}

	public void setData(int[][] data)
	{
		mData = data;
	}
	
	public int[][] getData()
	{
		return mData;
	}
	
	public int getData(IntPair absCellPos)
	{
		return mData[absCellPos.getX() - mPatchPos.getX() * Constants.PATCH_SIZE][absCellPos.getY() - mPatchPos.getY() * Constants.PATCH_SIZE];
	}
	
	public IntPair getPos()
	{
		return mPatchPos;
	}
}
