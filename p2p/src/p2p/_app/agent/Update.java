package p2p._app.agent;

public class Update {

	private String id;
	private int x;
	private int y;
	
	public Update(String id, int x, int y) {
		
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	public String getId() {
		
		return id;
	}
	
	public int getX() {
		
		return x;
	}
	
	public int getY() {
		
		return y;
	}
}
