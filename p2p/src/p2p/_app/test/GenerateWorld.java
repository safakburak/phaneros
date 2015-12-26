package p2p._app.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import p2p._app.common.Persist;
import p2p._app.map.Map;
import p2p._app.map.World;
import p2p._app.visibility.Visibility;

public class GenerateWorld {

	private static void randomFixedRange() {
		
		try {
			
			Map map = new Map(0, 0, ImageIO.read(new File("data/map/random.png")));
			Visibility visibility = Visibility.calculateDummy(map, 16, 50);
			World world = new World(map, visibility);
			
			Persist.save(world, "data/world/random_fixed_range.world");
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		randomFixedRange();
		
	}
}
