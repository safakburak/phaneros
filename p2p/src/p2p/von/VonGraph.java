package p2p.von;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import p2p.common.AbstractAgent;
import p2p.geometry.delaunay.Delaunay_Triangulation;
import p2p.geometry.delaunay.Point_dt;
import p2p.geometry.delaunay.Triangle_dt;

public class VonGraph {

	private VonAgent agent;

	private ArrayList<Triangle_dt> triangles = new ArrayList<Triangle_dt>();

	private Set<VonAgent> enclosing;

	@SuppressWarnings("rawtypes")
	public VonGraph(List<AbstractAgent> agents) {

		update(agents);
	}

	public VonGraph(VonAgent agent) {

		this.agent = agent;
	}

	public void update(HashMap<VonAgent, Point> knownAgents) {

		Point_dt[] points = new Point_dt[knownAgents.size() + 1];

		int pIndex = 0;
		for (Entry<VonAgent, Point> entry : knownAgents.entrySet()) {

			if (entry.getKey() != agent) {

				points[pIndex] = new Point_dt(entry.getValue().getX(), entry.getValue().getY());
				points[pIndex].marker = entry.getKey();
				pIndex++;
			}
		}

		points[pIndex] = new Point_dt(agent.getX(), agent.getY());
		points[pIndex].marker = agent;

		Delaunay_Triangulation triangulation = new Delaunay_Triangulation(points);

		Iterator<Triangle_dt> itr = triangulation.trianglesIterator();

		triangles.clear();

		while (itr.hasNext()) {

			triangles.add(itr.next());
		}

		enclosing = getEnclosing(agent);
	}

	@SuppressWarnings("rawtypes")
	public void update(List<AbstractAgent> agents) {

		Point_dt[] points = new Point_dt[agents.size()];

		int pIndex = 0;
		for (AbstractAgent agent : agents) {

			points[pIndex] = new Point_dt(agent.getX(), agent.getY());
			points[pIndex].marker = (VonAgent) agent;
			pIndex++;
		}

		Delaunay_Triangulation triangulation = new Delaunay_Triangulation(points);

		Iterator<Triangle_dt> itr = triangulation.trianglesIterator();

		triangles.clear();

		while (itr.hasNext()) {

			triangles.add(itr.next());
		}
	}

	public Set<VonAgent> getEnclosing(VonAgent agent) {

		Set<VonAgent> result = new HashSet<VonAgent>();

		for (Triangle_dt triangle : triangles) {

			if (triangle.p1() != null && triangle.p1().marker == agent) {

				if (triangle.p2() != null) {
					result.add((VonAgent) triangle.p2().marker);
				}

				if (triangle.p3() != null) {
					result.add((VonAgent) triangle.p3().marker);
				}
			} else if (triangle.p2() != null && triangle.p2().marker == agent) {

				if (triangle.p1() != null) {
					result.add((VonAgent) triangle.p1().marker);
				}

				if (triangle.p3() != null) {
					result.add((VonAgent) triangle.p3().marker);
				}
			} else if (triangle.p3() != null && triangle.p3().marker == agent) {

				if (triangle.p1() != null) {
					result.add((VonAgent) triangle.p1().marker);
				}

				if (triangle.p2() != null) {
					result.add((VonAgent) triangle.p2().marker);
				}
			}
		}

		return result;
	}

	public Set<VonAgent> getEnclosing() {

		return enclosing;
	}
}
