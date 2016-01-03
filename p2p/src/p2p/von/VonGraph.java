package p2p.von;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import p2p.common.AbstractAgent;
import p2p.geometry.delaunay.Delaunay;
import p2p.geometry.primitives.Point;
import p2p.geometry.primitives.Triangle;

public class VonGraph {

	private VonAgent agent;
	
	private ArrayList<Triangle> triangles;
	
	private Set<VonAgent> enclosing;
	
	public VonGraph(List<AbstractAgent> agents) {
		
		update(agents);
	}
	
	public VonGraph(VonAgent agent) {
		
		this.agent = agent;
	}
	
	public void update(List<AbstractAgent> agents) {

		ArrayList<Point> points = new ArrayList<Point>();
		
		for(AbstractAgent agent : agents) {
			
			Point point = new Point(agent.getX(), agent.getY());
			point.setMarker(agent);
			points.add(point);
		}
		
		if(agent != null && agents.contains(agent) == false) {
			
			Point point = new Point(agent.getX(), agent.getY());
			point.setMarker(agent);
			points.add(point);
		}

		triangles = Delaunay.randomizedIncremental(points);
		
		if(agent != null) {
			
			enclosing = getEnclosing(agent);
		}
	}

	public Set<VonAgent> getEnclosing(VonAgent agent) {
		
		Set<VonAgent> result = new HashSet<VonAgent>();
		
		for(Triangle triangle : triangles) {
			
			if(triangle.getA().getMarker() == agent) {
				
				result.add((VonAgent) triangle.getB().getMarker());
				result.add((VonAgent) triangle.getC().getMarker());
				
			} else if(triangle.getB().getMarker() == agent) {
				
				result.add((VonAgent) triangle.getA().getMarker());
				result.add((VonAgent) triangle.getC().getMarker());
				
			} else if(triangle.getC().getMarker() == agent) {
				
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
