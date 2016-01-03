package p2p.geometry;

import java.util.ArrayList;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import p2p.geometry.delaunay.Delaunay;
import p2p.geometry.primitives.Point;
import p2p.geometry.primitives.Triangle;

public class Test {

	public static void main(String[] args) {
		
		int pointCount = 1000;
		
		ArrayList<Point> points = new ArrayList<Point>(pointCount);
		
		while(pointCount-- > 0) {
			
			points.add(new Point(Math.random() * 1024, Math.random() * 1024));
		}
		
		long t = System.nanoTime();
		
//		ArrayList<Triangle> triangles = Delaunay.randomizedIncremental(points);
//		
//		Multimap<Point, Point> graph = HashMultimap.create();
//		
//		for(Triangle triangle : triangles) {
//			
//			graph.put(triangle.getA(), triangle.getB());
//			graph.put(triangle.getA(), triangle.getC());
//			
//			graph.put(triangle.getB(), triangle.getA());
//			graph.put(triangle.getB(), triangle.getC());
//			
//			graph.put(triangle.getC(), triangle.getA());
//			graph.put(triangle.getC(), triangle.getB());
//		}
//		
//		for(Point point : graph.get(points.get(150))) {
//			
//			System.out.println(point);
//		}
		
		for(Point p1 : points) {
			for(Point p2 : points) {

				p1.dist(p2);
			}
		}
		
		System.out.println((System.nanoTime() - t) / 1000000.0);
	}
	
}
