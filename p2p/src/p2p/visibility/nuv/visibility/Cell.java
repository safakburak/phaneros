package p2p.visibility.nuv.visibility;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class Cell implements Serializable {

	private int x;
	private int y;
	private int width;
	private int height;
	
	private Set<Cell> pvs = new HashSet<Cell>();
	
	public Cell(int x, int y, int width, int height) {
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public int getX() {
		
		return x;
	}
	
	public int getY() {
		
		return y;
	}
	
	public int getWidth() {
		
		return width;
	}
	
	public int getHeight() {
		
		return height;
	}
	
	public Set<Cell> getPvs() {
		
		return pvs;
	}
	
	public void addToPvs(Cell cell) {
		
		pvs.add(cell);
	}
}
