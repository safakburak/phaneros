package p2p.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

@SuppressWarnings("serial")
public class Atlas implements Serializable {

	private BufferedImage image;
	private Raster raster;

	public Atlas(BufferedImage image) {

		this.image = image;
		this.raster = image.getData();
	}

	public Tile getTile(int x, int y, int size) {

		return new Tile(this, x, y, size);
	}

	public void fillRandom(double fillRate, int stepSize) {

		Graphics g = image.getGraphics();

		for (int x = 0; x < image.getWidth(); x += stepSize) {
			for (int y = 0; y < image.getHeight(); y += stepSize) {

				if (Math.random() <= fillRate) {

					g.setColor(new Color(120, 120, 120));
					g.fillRect(x, y, 10, 10);
				}
			}
		}
	}

	public int get(int x, int y) {

		return raster.getSample(x, y, 0);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {

		ImageIO.write(image, "png", out);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

		image = ImageIO.read(in);
		raster = image.getData();
	}

	public int getWidth() {

		return image.getWidth();
	}

	public int getHeight() {

		return image.getHeight();
	}

	public BufferedImage getImage() {

		return image;
	}
}
