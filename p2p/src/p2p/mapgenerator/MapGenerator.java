package p2p.mapgenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import p2p.constants.Constants;
import p2p.patch.Patch;


public class MapGenerator 
{
	public MapGenerator() 
	{
		
	}
	
	private Patch generatePatch(int x, int y)
	{
		Patch patch = new Patch(x, y);
		
		int[][] data = new int[Constants.PATCH_SIZE][Constants.PATCH_SIZE];
		
		for(int dataX = 0; dataX < Constants.PATCH_SIZE; dataX++)
		{
			for(int dataY = 0; dataY < Constants.PATCH_SIZE; dataY++)
			{
				if(Math.random() > 0.7)
				{
					data[dataX][dataY] = 1;
				}
				else
				{
					data[dataX][dataY] = 0;
				}
			}
		}

		patch.setData(data);
		
		return patch;
	}
	
	public void generate(String outputPath, int horPatchCount, int verPatchCount) 
	{
		File outDir = new File(outputPath);
		outDir.mkdirs();
		
		for(int x = 0; x < horPatchCount; x++)
		{
			for(int y = 0; y < verPatchCount; y++)
			{
				Patch patch = generatePatch(x, y);
				
				try 
				{
					String fileName = outDir.getAbsolutePath() + "/" + "patch_" + x + "_" + y;

					ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(fileName));
					
					outStream.writeObject(patch);
					
					outStream.flush();
					outStream.close();
				} 
				catch (Exception exception) 
				{
					exception.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) 
	{
		MapGenerator dataGenerator = new MapGenerator();
		
		dataGenerator.generate("../data/simple", 100, 100);
	}
}
