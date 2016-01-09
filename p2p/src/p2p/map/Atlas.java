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
	private short[][] data;

	public Atlas(BufferedImage image) {

		this.image = image;
		fillData();
	}

	private void fillData() {

		data = new short[image.getWidth()][image.getHeight()];
		Raster raster = image.getData();

		for (int x = 0; x < data.length; x++) {

			data[x] = new short[image.getHeight()];

			for (int y = 0; y < data[x].length; y++) {

				data[x][y] = (short) raster.getSample(x, y, 0);
			}
		}
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

		fillData();
	}

	public int get(int x, int y) {

		return data[x][y];
	}

	private void writeObject(ObjectOutputStream out) throws IOException {

		ImageIO.write(image, "png", out);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

		image = ImageIO.read(in);
		fillData();
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
