package p2p._app.agent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				
				if(e.getID() == KeyEvent.KEY_PRESSED) {
					
					switch (e.getKeyCode()) {
					
					case KeyEvent.VK_A:
						zoom = zoom / 2;
						break;
						
					case KeyEvent.VK_Z:
						zoom = zoom * 2;
						break;
						
					case KeyEvent.VK_Q:
						zoom = 1;
						pan.setLocation(0, 0);
						break;
						
					case KeyEvent.VK_UP:
						pan.translate(0, 10);
						break;
						
					case KeyEvent.VK_DOWN:
						pan.translate(0, -10);
						break;
						
					case KeyEvent.VK_LEFT:
						pan.translate(10, 0);
						break;
						
					case KeyEvent.VK_RIGHT:
						pan.translate(-10, 0);
						break;
					}
				}
				
				return true;
			}
		});
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
}
