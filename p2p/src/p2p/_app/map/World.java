package p2p._app.map;

import java.io.Serializable;

import p2p._app.visibility.Visibility;

@SuppressWarnings("serial")
public class World implements Serializable {

	private Map map;
	private Visibility visibility;

	public World(Map map, Visibility visibility) {
		
		this.map = map;
		this.visibility = visibility;
	}

	public Visibility getVisibility() {
		return visibility;
	}
	
	public Map getMap() {
		
		return map;
	}
	
}
