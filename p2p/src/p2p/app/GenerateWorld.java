package p2p.app;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import p2p.map.Atlas;
import p2p.map.World;
import p2p.util.Persist;
import p2p.visibility.Visibility;

public class GenerateWorld {

	private static void randomFixedRange() {

		try {

			Atlas atlas = new Atlas(ImageIO.read(new File("data/map/random.png")));
			Visibility visibility = Visibility.calculate(atlas, 16, 50);
			World world = new World(atlas, visibility);

			Persist.save(world, "data/world/random_range.world");

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		randomFixedRange();

	}
}
