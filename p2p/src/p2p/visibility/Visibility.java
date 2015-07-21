package p2p.visibility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import p2p.constants.Constants;
import p2p.data.IntPair;
import p2p.patch.Patch;

public class Visibility 
{
	public static VisibilityMap sVisibilityMap = Visibility.loadVisibility("../data/simple/visibility.dat");
	
	private int[][] mData;
	private Horizon mHorizon;
	
	private HashMap<IntPair, ArrayList<IntPair>> mVisibility = new HashMap<IntPair, ArrayList<IntPair>>();
	
	public void calculate(String path, int horPatchCount, int verPatchCount)
	{
		mData = new int[horPatchCount * Constants.PATCH_SIZE][verPatchCount * Constants.PATCH_SIZE];
		
		for(int patchX = 0; patchX < horPatchCount; patchX++)
		{
			for(int patchY = 0; patchY < verPatchCount; patchY++)
			{
				try 
				{
					ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path + "/patch_" + patchX + "_" + patchY));
					
					Patch patch = (Patch) inputStream.readObject();
					
					for(int x = 0; x < Constants.PATCH_SIZE; x++)
					{
						for(int y = 0; y < Constants.PATCH_SIZE; y++)
						{
							mData[patchX * Constants.PATCH_SIZE + x][patchY * Constants.PATCH_SIZE + y] = patch.getData()[x][y];
						}	
					}
					
					inputStream.close();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				} 
			}
		}
		
		for(int seedX = 0; seedX < mData.length; seedX++)
		{
			for(int seedY = 0; seedY < mData.length; seedY++)
			{
				if(mData[seedX][seedY] == 0)
				{
					mHorizon = new Horizon();
					
					if(seedX == 0)
					{
						mHorizon.update(90, 270, 1);
					}
					
					if(seedX == mData.length)
					{
						mHorizon.update(270, 90, 1);
					}
					
					if(seedY == 0)
					{
						mHorizon.update(180, 360, 1);
					}
					
					if(seedY == mData.length)
					{
						mHorizon.update(0, 180, 1);
					}
					
					calculateForPos(seedX, seedY);
				}
			}
		}
	}
	
	private void calculateForPos(int seedX, int seedY)
	{
		for(int range = 1; range < 30; range++)
		{
			calculateForPosAndDistance(seedX, seedY, range);
			
			if(mHorizon.isClosed())
			{
				break;
			}
		}
	}
	
	private void calculateForPosAndDistance(int seedX, int seedY, int range)
	{
		for(int x = (seedX - range); x <= (seedX + range); x++)
		{
			for(int y = (seedY - range); y <= (seedY + range); y++)
			{
				if((x != seedX || y != seedY) 
						&& x >= 0 && x < mData.length 
						&& y >= 0 && y < mData[0].length)
				{
					testVisibility(seedX, seedY, x, y);
				}
			}
		}
	}
	
	private void testVisibility(int sourceX, int sourceY, int targetX, int targetY)
	{
		double centerX = sourceX + 0.5;
		double centerY = sourceY + 0.5;
		
		double[] hull = new double[2];
		
		calculateHull(new double[]{
			atan(targetY - centerY, targetX - centerX),
			atan(targetY - centerY, 1 + targetX - centerX),
			atan(1 + targetY - centerY, 1 + targetX - centerX),
			atan(1 + targetY - centerY, targetX - centerX)
		}, hull);
		
		boolean isVisible = mHorizon.update(hull[0], hull[1], 1);
		
		if(isVisible == true)
		{
			IntPair sourcePatchPos = new IntPair(sourceX / Constants.PATCH_SIZE, sourceY / Constants.PATCH_SIZE);

			IntPair targetPatchPos = new IntPair(targetX / Constants.PATCH_SIZE, targetY / Constants.PATCH_SIZE);
			
			if(!mVisibility.containsKey(sourcePatchPos))
			{
				mVisibility.put(sourcePatchPos, new ArrayList<IntPair>());
			}
			
			if(!mVisibility.get(sourcePatchPos).contains(targetPatchPos))
			{
				mVisibility.get(sourcePatchPos).add(targetPatchPos);
			}
		}
	}
	
	private double diff(double angle1, double angle2)
	{
		double diff = angle2 - angle1;
	
		if(diff < 0)
		{
			diff += 360;
		}
		
		if(diff > 180)
		{
			diff = 360 - diff;
		}
		
		return diff;
	}
	
	private void calculateHull(double[] angles, double[] result)
	{
		double a = angles[0];
		double b = angles[1];
		
		for(int i = 2; i < angles.length; i++)
		{
			if((diff(a, angles[i]) - diff(a,b)) > 0.001)
			{
				b = angles[i];
			}
			else if((diff(angles[i], b) - diff(a,b)) > 0.001)
			{
				a = angles[i];
			}
		}

		double diff = b - a;
		
		if(diff < 0)
		{
			diff += 360;
		}
		
		if(diff > 180)
		{
			result[0] = b;
			result[1] = a;
		}
		else
		{
			result[0] = a;
			result[1] = b;
		}
	}
	
	private double atan(double y, double x)
	{
		double result = Math.toDegrees(Math.atan2(y, x));
		
		if(result < 0)
		{
			result += 360;
		}
		
		return result;
	}
	
	public void save(String filePath)
	{
		try 
		{
			File file = new File(filePath);
			
			if(file.exists())
			{
				file.delete();
			}
			
			file.createNewFile();
			
			FileWriter writer = new FileWriter(file);
			
			
			for(IntPair key : mVisibility.keySet())
			{
				writer.write(key.getX() + "-" + key.getY() + ":");
				
				ArrayList<IntPair> visibleCells = mVisibility.get(key);
				
				int writeCount = 0;
				
				for(IntPair visible : visibleCells)
				{
					if(writeCount == 0)
					{
						writer.write(visible.getX() + "-" + visible.getY());
					}
					else
					{
						writer.write("," + visible.getX() + "-" + visible.getY());
					}
					
					writeCount++;
				}
				
				writer.write("\n");
			}
			
			writer.flush();
			writer.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static VisibilityMap loadVisibility(String path)
	{
		VisibilityMap visibility = new VisibilityMap();
		
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(path));
			
			while(true)
			{
				String line = reader.readLine();
				
				if(line == null)
				{
					break;
				}
				
				String[] tokens = line.split(":");
				
				String key = tokens[0];
				
				tokens = tokens[1].split(",");
				
				ArrayList<IntPair> visibles = new ArrayList<IntPair>();
				
				for(String visible : tokens)
				{
					String[] coordTokens = visible.split("-");
					
					visibles.add(new IntPair(Integer.parseInt(coordTokens[0]), Integer.parseInt(coordTokens[1])));
				}
				
				String[] coordTokens = key.split("-");
				
				visibility.put(new IntPair(Integer.parseInt(coordTokens[0]), Integer.parseInt(coordTokens[1])), visibles);
			}
			
			reader.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return visibility;
	}
}
