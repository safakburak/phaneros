package p2p.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import actionsim.core.Simulation;
import actionsim.core.SimulationListener;
import p2p.common.AbstractAgent;
import p2p.map.Map;
import p2p.map.Region;
import p2p.map.World;
import p2p.visibility.VisibilityCell;

@SuppressWarnings("serial")
public class Renderer extends JPanel {

	@SuppressWarnings("unused")
	private Simulation simulation;

	private IRenderable renderable;

	private double zoom = 1;

	private Point pan = new Point(0, 0);

	private BufferedImage frontBuffer;
	private BufferedImage backBuffer;

	@SuppressWarnings("unused")
	private KeyManager keyManager;

	private World world;

	private ArrayList<AbstractAgent> allAgents;
	
	public Renderer(World world, Simulation simulation, IRenderable renderable, ArrayList<AbstractAgent> allAgents) {

		this.simulation = simulation;
		this.renderable = renderable;
		this.world = world;
		this.allAgents = allAgents;

		keyManager = new KeyManager(simulation, this);

		simulation.addListener(new SimulationListener() {

			@Override
			public void onStep(float deltaTime, long step) {

				render();
			}
		});

		JFrame frame = new JFrame(renderable.getId());
		frame.setContentPane(this);
		frame.setSize(500, 500);
		frame.setVisible(true);

		setDoubleBuffered(true);
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		synchronized (frontBuffer) {

			g.drawImage(frontBuffer, 0, 0, null);
		}
	}

	public void render() {

		Graphics2D g2D = (Graphics2D) backBuffer.getGraphics();

		g2D.setColor(Color.white);
		g2D.fillRect(0, 0, getWidth(), getHeight());

		AffineTransform transform = g2D.getTransform();

		g2D.translate(getWidth() / 2, getHeight() / 2);
		g2D.scale(zoom, zoom);
		g2D.translate(-getWidth() / 2, -getHeight() / 2);
		g2D.translate(pan.x, pan.y);

		int cellSize = world.getVisibility().getCellSize();
		int rowCount = world.getVisibility().getRowCount();
		int colCount = world.getVisibility().getColCount();
		int width = cellSize * colCount;
		int height = cellSize * rowCount;

		g2D.setColor(Color.darkGray);
		g2D.setStroke(new BasicStroke(0.5f));

		for (int row = 0; row <= rowCount; row++) {

			g2D.drawLine(0, row * cellSize, width, row * cellSize);
		}

		for (int col = 0; col <= rowCount; col++) {

			g2D.drawLine(col * cellSize, 0, col * cellSize, height);
		}

		for (Map patch : renderable.getPatches()) {

			g2D.drawImage(patch.getData(), patch.getX(), patch.getY(), null);
		}

		g2D.setColor(Color.orange.darker());
		for (AbstractAgent agent : allAgents) {

			g2D.fillRect(agent.getX(), agent.getY(), 1, 1);
		}
		
		g2D.setColor(Color.green);
		for (Point p : renderable.getAgents()) {

			g2D.fillRect(p.x, p.y, 1, 1);
		}

		g2D.setColor(new Color(255, 255, 0, 50));
		for (VisibilityCell cell : renderable.getPvs()) {

			Region region = cell.getRegion();
			g2D.fillRect(region.getX(), region.getY(), region.getSize(), region.getSize());
		}
		
		g2D.setColor(Color.red);
		g2D.fillRect(renderable.getX(), renderable.getY(), 1, 1);

		double r = world.getVisibility().getMaxRange();
		g2D.drawOval((int) (renderable.getX() - r), (int) (renderable.getY() - r), (int) (r * 2), (int) (r * 2));

		g2D.setTransform(transform);

		synchronized (frontBuffer) {

			BufferedImage temp = frontBuffer;
			frontBuffer = backBuffer;
			backBuffer = temp;
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				repaint();
			}
		});
	}

	public void zoomIn() {

		zoom = zoom * 2;

		render();
	}

	public void zoomOut() {

		zoom = zoom / 2;

		render();
	}

	public void pan(int dX, int dY) {

		pan.translate(dX, dY);

		render();
	}

	public void reset() {

		zoom = 1;
		pan.setLocation(0, 0);

		render();
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {

		super.setBounds(x, y, width, height);

		frontBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
}
