package p2p.map;

import java.io.Serializable;

import p2p.visibility.Visibility;

@SuppressWarnings("serial")
public class World implements Serializable {

	private Atlas atlas;
	private Visibility visibility;

	public World(Atlas atlas, Visibility visibility) {

		this.atlas = atlas;
		this.visibility = visibility;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public Atlas getAtlas() {

		return atlas;
	}

	public int getWidth() {

		return atlas.getWidth();
	}

	public int getHeight() {

		return atlas.getHeight();
	}
}
