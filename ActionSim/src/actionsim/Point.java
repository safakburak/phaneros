package actionsim;

public class Point {

	public float x;
	public float y;

	public Point() {

		x = 0;

		y = 0;
	}

	public Point(float x, float y) {

		this.x = x;
		this.y = y;
	}

	public Point(Point p) {

		this(p.x, p.y);
	}

}
