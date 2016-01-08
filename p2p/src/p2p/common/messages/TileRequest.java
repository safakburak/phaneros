package p2p.common.messages;

public class TileRequest {

	private int x;
	private int y;
	
	public TileRequest(int x, int y) {
		
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		
		return x;
	}
	
	public int getY() {
		
		return y;
	}
}
