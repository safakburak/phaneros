package p2p.visibility.nuv.agent;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import p2p.visibility.nuv.map.Patch;

public class Renderer {

	private List<Patch> patches = new ArrayList<Patch>();
	private JPanel panel = new JPanel();

	public void render() {

		if (panel == null) {

			return;
		}

		Graphics g = panel.getGraphics();

		g.clearRect(0, 0, panel.getWidth(), panel.getHeight());

		for (Patch patch : patches) {

			if (patch.getData() == null) {

				g.setColor(Color.yellow);
				g.fillRect(patch.getX(), patch.getY(), patch.getWidth(), patch.getHeight());

			} else {

				g.drawImage(patch.getData(), patch.getX(), patch.getY(), null);
			}
		}
	}

	public JPanel getPanel() {

		return panel;
	}

	public void addPatch(Patch patch) {
		
		if(patches.contains(patch) == false) {
			
			patches.add(patch);
		}
	}
	
	public void removePatch(Patch patch) {
		
		patches.remove(patch);
	}
}
