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

	public Region getRegion() {

		return region;
	}

	public int getRelative(int x, int y) {

		return atlas.get(x + region.getX(), y + region.getY());
	}

	public int getAbsolute(float x, float y) {

		return atlas.get((int) x, (int) y);
	}

	public Image getImage() {

		return atlas.getImage().getSubimage(getX(), getY(), getSize(), getSize());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((region == null) ? 0 : region.hashCode());
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
		Tile other = (Tile) obj;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		return true;
	}

}
