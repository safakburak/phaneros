package p2p.renderer;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import actionsim.core.Simulation;

public class KeyManager {

	private Simulation simulation;
	
	private Renderer renderer;

	public KeyManager(Simulation simulation, Renderer renderer) {
	
		this.simulation = simulation;
		this.renderer = renderer;
		
		bindKeys();
	}

	private void bindKeys() {
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				
				if(e.getID() == KeyEvent.KEY_PRESSED) {
					
					switch (e.getKeyCode()) {
					
						case KeyEvent.VK_A:
							
							if(renderer != null) {
								
								renderer.zoomOut();
							}
							break;
							
						case KeyEvent.VK_Z:
	
							if(renderer != null) {
								
								renderer.zoomIn();
							}
							break;
							
						case KeyEvent.VK_Q:
	
							if(renderer != null) {
								
								renderer.reset();
							}
							break;
							
						case KeyEvent.VK_UP:
	
							if(renderer != null) {
								
								renderer.pan(0, 10);
							}
							break;
							
						case KeyEvent.VK_DOWN:
							
							if(renderer != null) {
								
								renderer.pan(0, -10);
							}
							break;
							
						case KeyEvent.VK_LEFT:
	
							if(renderer != null) {
								
								renderer.pan(10, 0);
							}
							break;
							
						case KeyEvent.VK_RIGHT:
	
							if(renderer != null) {
								
								renderer.pan(-10, 0);
							}
							break;
						
						case KeyEvent.VK_SPACE:
							
							if(simulation != null) {
								
								simulation.togglePlay();
							}
							break;
					}
				}
				
				return true;
			}
		});
	}
}
