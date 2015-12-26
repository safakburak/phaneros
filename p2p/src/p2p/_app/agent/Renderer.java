package p2p._app.agent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import p2p._app.map.Map;

@SuppressWarnings("serial")
public class Renderer extends JPanel {

	private Agent agent;

	private double zoom = 1;
	
	private Point pan = new Point(0, 0);
	
	public Renderer(Agent agent) {
		
		this.agent = agent;
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

		for (Map patch : agent.getCache().getPatches()) {

			if (patch.getData() == null) {

				g2D.setColor(Color.yellow);
				g2D.fillRect(patch.getX(), patch.getY(), patch.getWidth(), patch.getHeight());

			} else {

				g2D.drawImage(patch.getData(), patch.getX(), patch.getY(), null);
			}
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
