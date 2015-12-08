package p2p.visibility.nuv.visibility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import p2p.visibility.nuv.map.Map;
import p2p.visibility.nuv.visibility.horizon.Horizon;
import p2p.visibility.nuv.visibility.horizon.Sector;

public class Visibility implements Serializable {

	private Map map;
	private int cellSize; // meters
	private Cell[][] cells;
	private int maxRange; // meters

	public Visibility(Map map, int cellSize, int visibilityRange) {

		this.map = map;
		this.cellSize = cellSize;
		this.maxRange = visibilityRange;

		int colNum = map.getWidth() / cellSize;
		int rowNum = map.getHeight() / cellSize;

		cells = new Cell[colNum][rowNum];

		for (int col = 0; col < colNum; col++) {
			for (int row = 0; row < rowNum; row++) {
				cells[col][row] = new Cell(col * cellSize, row * cellSize, cellSize, cellSize);
			}
		}
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

		for (int range = 1; range < maxRange; range++) {

			int xMin = Math.max(sX - range, 0);
			int xMax = Math.min(sX + range, map.getWidth() - 1);
			int yMin = Math.max(sY - range, 0);
			int yMax = Math.min(sY + range, map.getWidth() - 1);

			// north, south
			for (int x = xMin; x <= xMax; x++) {

				calculateForLine(horizon, cell, sX, sY, x, yMin);
				calculateForLine(horizon, cell, sX, sY, x, yMax);
			}

			// west, east
			for (int y = yMin; y <= yMax; y++) {

				calculateForLine(horizon, cell, sX, sY, xMin, y);
				calculateForLine(horizon, cell, sX, sY, xMax, y);
			}
		}
	}

	private void calculateForLine(Horizon horizon, Cell cell, int sX, int sY, int tX, int tY) {

		if (sX != tX || sY != tY) {
			
			int dX = tX - sX;
			int dY = tY - sY;
			
			if (((dX * dX) + (dY * dY) <= (maxRange * maxRange)) && horizon.update(getSector(sX, sY, tX, tY))) {
				
				int row = tX / cellSize;
				int col = tY / cellSize;
				
				cell.addToPvs(cells[col][row]);
			}
		}
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
			outputStream.writeObject(this);
			outputStream.flush();
			outputStream.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public static Visibility load(String path) {

		Visibility result = null;
		
		try {

			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path));
			result = (Visibility) inputStream.readObject();
			inputStream.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
		
		return result;
	}

	public int getCellSize() {

		return cellSize;
	}

	public static void main(String[] args) {

		Visibility visibility = new Visibility(new Map("world.png").getMapPart(0, 0, 100, 100), 20, 2);
		visibility.calculate();
		
		visibility.save("deneme.vis");
		
		visibility = Visibility.load("deneme.vis");
	}
}
