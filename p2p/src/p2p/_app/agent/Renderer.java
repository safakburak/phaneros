package p2p._app.agent;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import p2p._app.map.Map;

@SuppressWarnings("serial")
public class Renderer extends JPanel {

	private Agent agent;

	public Renderer(Agent agent) {
		
		this.agent = agent;
	}
	
	@Override
	public void paint(Graphics g) {

		g.clearRect(0, 0, getWidth(), getHeight());

		for (Map patch : agent.getCache().getPatches()) {

			if (patch.getData() == null) {

				g.setColor(Color.yellow);
				g.fillRect(patch.getX(), patch.getY(), patch.getWidth(), patch.getHeight());

			} else {

				g.drawImage(patch.getData(), patch.getX(), patch.getY(), null);
			}
		}
		
		g.setColor(Color.red);
		g.fillRect(agent.getX(), agent.getY(), 1, 1);
	}
}
