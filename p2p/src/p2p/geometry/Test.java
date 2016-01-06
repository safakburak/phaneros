package p2p.geometry;

import java.util.ArrayList;

import p2p.geometry.delaunay.Delaunay;
import p2p.geometry.primitives.Point;

public class Test {

	public static void main(String[] args) {

		// int pointCount = 1000;
		//
		// ArrayList<Point> points = new ArrayList<Point>(pointCount);
		//
		// while(pointCount-- > 0) {
		//
		// points.add(new Point(Math.random() * 1024, Math.random() * 1024));
		// }
		//
		// long t = System.nanoTime();
		//
		//// ArrayList<Triangle> triangles =
		// Delaunay.randomizedIncremental(points);
		////
		//// Multimap<Point, Point> graph = HashMultimap.create();
		////
		//// for(Triangle triangle : triangles) {
		////
		//// graph.put(triangle.getA(), triangle.getB());
		//// graph.put(triangle.getA(), triangle.getC());
		////
		//// graph.put(triangle.getB(), triangle.getA());
		//// graph.put(triangle.getB(), triangle.getC());
		////
		//// graph.put(triangle.getC(), triangle.getA());
		//// graph.put(triangle.getC(), triangle.getB());
		//// }
		////
		//// for(Point point : graph.get(points.get(150))) {
		////
		//// System.out.println(point);
		//// }
		//
		// for(Point p1 : points) {
		// for(Point p2 : points) {
		//
		// p1.dist(p2);
		// }
		// }
		//
		// System.out.println((System.nanoTime() - t) / 1000000.0);

		ArrayList<Point> points = new ArrayList<Point>();

		// points.add(new Point(621.0, 166.0));
		// points.add(new Point(612.0, 185.0));
		// points.add(new Point(612.0, 190.0));
		// points.add(new Point(579.0, 202.0));
		// points.add(new Point(612.0, 227.0));
		// points.add(new Point(602.0, 150.0));
		// points.add(new Point(584.0, 160.0));
		// points.add(new Point(554.0, 191.0));
		// points.add(new Point(592.0, 184.0));
		// points.add(new Point(600.0, 186.0));

		points.add(new Point(291.0, 422.0));
		points.add(new Point(352.0, 440.0));
		points.add(new Point(307.0, 492.0));
		points.add(new Point(337.0, 491.0));
		points.add(new Point(334.0, 458.0));
		points.add(new Point(305.0, 429.0));
		points.add(new Point(307.0, 432.0));
		points.add(new Point(283.0, 482.0));
		points.add(new Point(278.0, 481.0));
		points.add(new Point(343.0, 446.0));
		points.add(new Point(307.0, 455.0));

		int n = 100;

		while (n-- > 0) {

			Delaunay.randomizedIncremental(points);
		}
	}

}
