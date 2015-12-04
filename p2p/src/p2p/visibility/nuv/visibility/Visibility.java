package p2p.visibility.nuv.visibility;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JPanel;

import p2p.visibility.nuv.map.Map;
import p2p.visibility.nuv.visibility.horizon.Horizon;
import p2p.visibility.nuv.visibility.horizon.Sector;

public class Visibility {

	private Map map;
	private int cellSize; // meters
	private Cell[][] cells;
	private int visibilityRange; // meters

	public Visibility(Map map, int cellSize, int visibilityRange) {

		this.map = map;
		this.cellSize = cellSize;
		this.visibilityRange = visibilityRange;

		int colNum = map.getWidth() / cellSize;
		int rowNum = map.getHeight() / cellSize;

		cells = new Cell[colNum][rowNum];
		
		for (int col = 0; col < colNum; col++) {
			for (int row = 0; row < rowNum; row++) {
				cells[col][row] = new Cell(col * cellSize, row * cellSize, cellSize, cellSize);
			}
		}
//		calculate();
	}

	private void calculate() {

		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {

				calculateForPosision(x, y);
			}
		}
	}

	private void calculateForPosision(int sX, int sY) {

		Horizon horizon = new Horizon();
		Cell cell = cells[sX / cellSize][sY / cellSize];

		for (int range = 0; range < visibilityRange; range++) {

			int xMin = Math.max(sX - range, 0);
			int xMax = Math.min(sX + range, map.getWidth() - 1);
			int yMin = Math.max(sY - range, 0);
			int yMax = Math.min(sY + range, map.getWidth() - 1);

			for (int y = yMin; y <= yMax; y++) {
				for (int x = xMin; x <= xMax; x++) {

					
					if ((x != sX || y != sY)
							&& (((x - sX) * (x - sX)) + ((y - sY) * (y - sY)) <= range * range)
							&& horizon.update(getSector(sX, sY, x, y))) {

						int row = x / cellSize;
						int col = y / cellSize;

						cell.addToPvs(cells[col][row]);
					}
				}
			}
		}
		
		System.out.println();
	}

	private Sector getSector(double sX, double sY, int tX, int tY) {

		double cX = sX + 0.5;
		double cY = sY + 0.5;
		
		ArrayList<Double> angles = new ArrayList<Double>();

		angles.add(atan(tY - cY, tX - cX));
		angles.add(atan(tY + 1 - cY, tX - cX));
		angles.add(atan(tY - cY, tX + 1 - cX));
		angles.add(atan(tY + 1 - sY, tX + 1 - sX));

		Collections.sort(angles);

		while (angularDistance(angles.get(0), angles.get(3)) > 180) {

			angles.add(0, angles.remove(3));
		}

		double distance = Double.MAX_VALUE;
		distance = Math.min(distance, (tY - cY) * (tY - cY) + (tX - cX) * (tX - cX));
		distance = Math.min(distance, (tY + 1 - cY) * (tY + 1 - cY) + (tX - cX) * (tX - cX));
		distance = Math.min(distance, (tY - cY) * (tY - cY) + (tX + 1 - cX) * (tX + 1 - cX));
		distance = Math.min(distance, (tY + 1 - cY) * (tY + 1 - cY) + (tX + 1 - cX) * (tX + 1 - cX));
		distance = Math.sqrt(distance);

		return new Sector(angles.get(0), angles.get(3), Math.toDegrees(Math.atan2(map.getHeightAt(tX, tY), distance)));
	}

	private double atan(double dY, double dX) {

		double result = Math.toDegrees(Math.atan2(dY, dX));

		while (result < 0) {

			result += 360;
		}

		return result;
	}

	private double angularDistance(double angle1, double angle2) {

		double result = angle2 - angle1;

		if (result < 0) {

			result += 360;
		}

		return result;
	}

	public void save(String path) {

		try {

			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path));
			outputStream.writeObject(cells);
			outputStream.flush();
			outputStream.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void load(String path) {

		try {

			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path));
			cells = (Cell[][]) inputStream.readObject();
			inputStream.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public int getCellSize() {

		return cellSize;
	}

	public static void main(String[] args) {

		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY);

		Graphics g = image.getGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 100, 100);
		
		g.setColor(new Color(100, 100, 100));
		g.drawLine(20, 10, 20, 20);
//		g.drawRect(5, 5, 10, 10);

		final Map map = new Map(0, 0, image);

		Visibility visibility = new Visibility(map, 10, 30);
		visibility.calculateForPosision(10, 10);
		
		JFrame frame = new JFrame();
		JPanel panel = new JPanel() {
			
			public void paint(Graphics g) {
				
				super.paint(g);
				g.drawImage(map.getData(), 0, 0, null);
			};
		}; 
		frame.setContentPane(panel);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}
