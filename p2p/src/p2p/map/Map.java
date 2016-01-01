package p2p.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

import p2p.util.Persist;

@SuppressWarnings("serial")
public class Map implements Serializable {

	private int x;
	private int y;
	private BufferedImage data;

	public Map(int x, int y, BufferedImage data) {

		this.x = x;
		this.y = y;
		this.data = data;
	}

	public Map getMapPart(int x, int y, int w, int h) {

		return new Map(x, y, data.getSubimage(x, y, w, h));
	}

	public void fillRandom(double fillRate, int stepSize) {

		Graphics g = data.getGraphics();

		for (int x = 0; x < data.getWidth(); x += stepSize) {
			for (int y = 0; y < data.getHeight(); y += stepSize) {

				if (Math.random() <= fillRate) {

					g.setColor(new Color(120, 120, 120));
					g.fillRect(x, y, 10, 10);
				}
			}
		}
	}

	public int getX() {

		return x;
	}

	public int getY() {

		return y;
	}

	public int getWidth() {

		return data.getWidth();
	}

	public int getHeight() {

		return data.getHeight();
	}

	public BufferedImage getData() {

		return data;
	}

	public int getHeightAtRel(int x, int y) {

		return data.getData().getSample(x, y, 0);
	}
	
	public int getHeightAtAbs(int x, int y) {
		
		return data.getData().getSample(x - this.x, y - this.y, 0);
	}

	public boolean contains(int x, int y) {

		return x >= this.x && y >= this.y && x < (this.x + getWidth()) && y < (this.y + getHeight());
	}

	private void writeObject(ObjectOutputStream out) throws IOException {

		out.writeInt(x);
		out.writeInt(y);
		ImageIO.write(data, "png", out);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

		x = in.readInt();
		y = in.readInt();
		data = ImageIO.read(in);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Map other = (Map) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
