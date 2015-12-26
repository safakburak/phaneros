package p2p._app.agent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;

import p2p._app.map.Map;
import p2p._app.map.Region;
import p2p._app.visibility.VisibilityCell;

@SuppressWarnings("serial")
public class Renderer extends JPanel {

	private Agent agent;

	private double zoom = 1;
	
	private Point pan = new Point(0, 0);
	
	public Renderer(Agent agent) {
		
		this.agent = agent;
		
		JFrame frame = new JFrame(agent.getName());
		frame.setContentPane(this);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {

		Graphics2D g2D = (Graphics2D) g;
		
		AffineTransform transform = g2D.getTransform();
		
		g2D.translate(getWidth() / 2, getHeight() / 2);
		g2D.scale(zoom, zoom);
		g2D.translate(-getWidth() / 2, -getHeight() / 2);
		g2D.translate(pan.x, pan.y);
		
		g2D.clearRect(0, 0, getWidth(), getHeight());

		for (VisibilityCell cell : agent.getPvs()) {
			
			Region region = cell.getRegion();
			
			g2D.setColor(Color.orange);
			g2D.fillRect(region.getX(), region.getY(), region.getSize(), region.getSize());
		}
		
		for (Map patch : agent.getCache().getPatches()) {

			g2D.drawImage(patch.getData(), patch.getX(), patch.getY(), null);
		}
		
		g2D.setColor(Color.red);
		g2D.fillRect(agent.getX(), agent.getY(), 1, 1);
		
		g2D.setTransform(transform);
	}
	
	public void zoomIn() {
		
		zoom = zoom * 2;
	}
	
	public void zoomOut() {
		
		zoom = zoom / 2;
	}
	
	public void pan(int dX, int dY) {
		
		pan.translate(dX, dY);
	}
	
	public void reset() {
		
		zoom = 1;
		pan.setLocation(0, 0);
	}
}
