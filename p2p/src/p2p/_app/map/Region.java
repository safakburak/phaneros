package p2p._app.map;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Region implements Serializable {

	private int x;
	private int y;
	private int size;
	
	public Region(int x, int y, int size) {
		
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		
		return x;
	}
	
	public int getY() {
		
		return y;
	}
	
	public int getSize() {
		
		return size;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	@Override
	public String toString() {
		
		return "Region(" + x + ", " + y + ")";
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
		Region other = (Region) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
