package p2p.von;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import p2p.common.AbstractAgent;
import p2p.geometry.delaunay.Delaunay;
import p2p.geometry.primitives.Point;
import p2p.geometry.primitives.Triangle;

public class VonGraph {

	private VonAgent agent;

	private ArrayList<Triangle> triangles;

	private Set<VonAgent> enclosing;

	@SuppressWarnings("rawtypes")
	public VonGraph(List<AbstractAgent> agents) {

		update(agents);
	}

	public VonGraph(VonAgent agent) {

		this.agent = agent;
	}

	public void update(HashMap<VonAgent, Point> knownAgents) {

		ArrayList<Point> points = new ArrayList<Point>();

		for (Entry<VonAgent, Point> entry : knownAgents.entrySet()) {

			if (entry.getKey() != agent) {

				Point point = new Point(entry.getValue().getX(), entry.getValue().getY());
				point.setMarker(entry.getKey());
				points.add(point);
			}
		}

		Point point = new Point(agent.getX(), agent.getY());
		point.setMarker(agent);
		points.add(point);

		try {

			triangles = Delaunay.randomizedIncremental(points);

		} catch (Exception exception) {

			triangles = Delaunay.bruteForce(points);
		}

		enclosing = getEnclosing(agent);
	}

	@SuppressWarnings("rawtypes")
	public void update(List<AbstractAgent> agents) {

		ArrayList<Point> points = new ArrayList<Point>();

		for (AbstractAgent agent : agents) {

			Point point = new Point(agent.getX(), agent.getY());
			point.setMarker(agent);
			points.add(point);
		}

		triangles = Delaunay.randomizedIncremental(points);
	}

	public Set<VonAgent> getEnclosing(VonAgent agent) {

		Set<VonAgent> result = new HashSet<VonAgent>();

		for (Triangle triangle : triangles) {

			if (triangle.getA().getMarker() == agent) {

				result.add((VonAgent) triangle.getB().getMarker());
				result.add((VonAgent) triangle.getC().getMarker());

			} else if (triangle.getB().getMarker() == agent) {

				result.add((VonAgent) triangle.getA().getMarker());
				result.add((VonAgent) triangle.getC().getMarker());

			} else if (triangle.getC().getMarker() == agent) {

				result.add((VonAgent) triangle.getA().getMarker());
				result.add((VonAgent) triangle.getB().getMarker());

			}
		}

		return result;
	}

	public Set<VonAgent> getEnclosing() {

		return enclosing;
	}
}
