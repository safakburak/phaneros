package p2p.map;

import java.awt.Image;

public class Tile {

	private Atlas atlas;

	private Region region;

	public Tile(Atlas atlas, int x, int y, int size) {

		this.atlas = atlas;

		region = new Region(x, y, size);
	}

	public int getX() {

		return region.getX();
	}

	public int getY() {

		return region.getY();
	}

	public int getSize() {

		return region.getSize();
	}

	public int getRelative(int x, int y) {

		return atlas.get(x + region.getX(), y + region.getY());
	}

	public int getAbsolute(int x, int y) {

		return atlas.get(x, y);
	}

	public Image getImage() {

		return atlas.getImage().getSubimage(getX(), getY(), getSize(), getSize());
	}

	// public boolean contains(int x, int y) {
	//
	// return x >= this.x && y >= this.y && x < (this.x + getWidth()) && y <
	// (this.y + getHeight());
	// }

	@Override
	public int hashCode() {

		return region.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tile other = (Tile) obj;
		if (getX() != other.getX())
			return false;
		if (getX() != other.getY())
			return false;
		return true;
	}
}
