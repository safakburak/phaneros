package p2p.visibility.nuv.app;

import javax.swing.JFrame;

import p2p.visibility.nuv.agent.Renderer;
import p2p.visibility.nuv.map.Map;
import p2p.visibility.nuv.map.Patch;

public class TestRenderer {

	public static void main(String[] args) {
		
		Patch world = new Patch("world.png");
		Map map = new Map(world, 32, 32);
		
		final Renderer renderer = new Renderer();

		renderer.addPatch(map.getPatch(0, 0));
		renderer.addPatch(map.getPatch(1, 0));
		renderer.addPatch(map.getPatch(0, 1));
		renderer.addPatch(map.getEmptyPatch(1, 1));
		
		
		
		JFrame frame = new JFrame();
		frame.setContentPane(renderer.getPanel());
		frame.setSize(500, 500);
		frame.setVisible(true);
		
		new Thread(new Runnable() {
			public void run() {
				
				while(true) {
					
					renderer.render();
					
					try {
						
						Thread.sleep(100);
						
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
