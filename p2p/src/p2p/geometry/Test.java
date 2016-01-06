package p2p.geometry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import p2p.geometry.delaunay.Delaunay_Triangulation;
import p2p.geometry.delaunay.Point_dt;
import p2p.geometry.delaunay.Triangle_dt;

public class Test {

	public static void main(String[] args) {

		int pointCount = 1000;

		Point_dt[] points = new Point_dt[pointCount];

		int pIndex = 0;
		while (pIndex < pointCount) {

			points[pIndex] = new Point_dt((int) (Math.random() * 1024), (int) (Math.random() * 1024));

			points[pIndex].marker = pIndex;

			pIndex++;
		}

		long t = System.nanoTime();

		Delaunay_Triangulation triangulation = new Delaunay_Triangulation(points);

		Iterator<Triangle_dt> itr = triangulation.trianglesIterator();

		Set<Point_dt> outpoints = new HashSet<Point_dt>();

		while (itr.hasNext()) {

			Triangle_dt tri = itr.next();

			if (tri.p1() != null) {

				outpoints.add(tri.p1());
			}
			if (tri.p2() != null) {

				outpoints.add(tri.p2());
			}
			if (tri.p3() != null) {

				outpoints.add(tri.p3());
			}
		}

		System.out.println((System.nanoTime() - t) / 1000000.0);

		System.out.println(outpoints.size());

		for (Point_dt p : outpoints) {

			if (p.marker == null) {
				System.out.println("SIÇTIIIK");
			}
		}
	}
}
