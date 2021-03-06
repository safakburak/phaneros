package p2p.renderer;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import actionsim.Point;
import actionsim.core.Simulation;
import actionsim.core.SimulationListener;
import p2p.common.AbstractAgent;
import p2p.map.Atlas;
import p2p.map.Region;
import p2p.map.Tile;
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

	@SuppressWarnings("rawtypes")
	private ArrayList<AbstractAgent> allAgents;

	private boolean isDrawAllAgents = true;

	private boolean isDrawWorldMap = true;

	private Atlas atlas;

	private Composite semiTransparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f);

	@SuppressWarnings("rawtypes")
	public Renderer(World world, Simulation simulation, IRenderable renderable, ArrayList<AbstractAgent> allAgents,
			Atlas atlas) {

		this.simulation = simulation;
		this.renderable = renderable;
		this.world = world;
		this.allAgents = allAgents;
		this.atlas = atlas;

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
		frame.setAlwaysOnTop(true);

		setDoubleBuffered(true);
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		synchronized (frontBuffer) {

			g.drawImage(frontBuffer, 0, 0, null);
		}
	}

	@SuppressWarnings("rawtypes")
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

		if (isDrawWorldMap) {

			Composite oldComposite = g2D.getComposite();

			g2D.setComposite(semiTransparent);
			g2D.drawImage(atlas.getImage(), 0, 0, null);

			g2D.setComposite(oldComposite);
		}

		for (Tile tile : renderable.getAvailableTiles()) {

			g2D.drawImage(tile.getImage(), tile.getX(), tile.getY(), null);
		}

		g2D.setColor(new Color(0, 0, 255, 100));
		for (VisibilityCell cell : renderable.getPvs()) {

			Region region = cell.getRegion();
			g2D.fillRect(region.getX(), region.getY(), region.getSize(), region.getSize());
		}

		g2D.setColor(Color.white);
		g2D.setStroke(new BasicStroke(0.5f));

		for (int row = 0; row <= rowCount; row++) {

			g2D.drawLine(0, row * cellSize, width, row * cellSize);
		}

		for (int col = 0; col <= rowCount; col++) {

			g2D.drawLine(col * cellSize, 0, col * cellSize, height);
		}

		g2D.setStroke(new BasicStroke(2f));

		if (isDrawAllAgents) {

			g2D.setColor(new Color(165, 42, 42));
			for (AbstractAgent agent : allAgents) {

				if (renderable != agent && renderable.isKnown(agent) == false) {

					drawAgent((int) (agent.getX() + 0.5), (int) (agent.getY() + 0.5), g2D);
				}
			}
		}

		g2D.setColor(Color.green);
		for (Point p : renderable.getAgents()) {

			drawAgent((int) (p.x + 0.5), (int) (p.y + 0.5), g2D);
		}

		g2D.setColor(Color.red);
		drawAgent(((int) (renderable.getX() + 0.5)), ((int) (renderable.getY() + 0.5)), g2D);

		double r = world.getVisibility().getMaxRange() + world.getVisibility().getCellSize();
		g2D.drawOval((int) (renderable.getX() - r), (int) (renderable.getY() - r), (int) (r * 2), (int) (r * 2));

		g2D.setColor(Color.magenta);
		r = world.getVisibility().getCellSize();

		List<Point> hotSpots = simulation.getHotSpots();

		if (hotSpots != null) {

			for (Point p : simulation.getHotSpots()) {

				g2D.fillOval((int) (p.x - r * 0.5), (int) (p.y - r * 0.5), (int) r, (int) r);
			}
		}

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

	private void drawAgent(int x, int y, Graphics2D g) {

		g.drawLine(x - 3, y - 3, x + 3, y + 3);
		g.drawLine(x - 3, y + 3, x + 3, y - 3);
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

		pan.x += dX;
		pan.y += dY;

		render();
	}

	public void reset() {

		zoom = 1;
		pan.x = 0;
		pan.y = 0;

		render();
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {

		super.setBounds(x, y, width, height);

		frontBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
}
