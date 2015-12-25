package p2p._app.visibility;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import p2p._app.map.Map;
import p2p._app.map.Region;

@SuppressWarnings("serial")
public class Visibility implements Serializable {

	private VisibilityCell[][] cells;
	private int cellSize; // meters
	private int maxRange; // meters

	private void writeObject(ObjectOutputStream out) throws IOException {

		out.writeInt(cellSize);
		out.writeInt(maxRange);
		out.writeInt(cells.length);
		out.writeInt(cells[0].length);
		
		for(int col = 0; col < cells.length; col++) {
			for(int row = 0; row < cells[col].length; row++) {

				out.writeObject(cells[col][row].getRegion());
			}
		}
		
		for(int col = 0; col < cells.length; col++) {
			for(int row = 0; row < cells.length; row++) {

				VisibilityCell cell = cells[col][row];
				
				out.writeInt(cell.getPvs().size());
				
				for(VisibilityCell pvsCell : cell.getPvs()) {
					
					out.writeObject(pvsCell.getRegion());
				}
			}
		}		
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

		cellSize = in.readInt();
		maxRange = in.readInt();
		
		cells = new VisibilityCell[in.readInt()][in.readInt()];
		
		for(int col = 0; col < cells.length; col++) {
			for(int row = 0; row < cells.length; row++) {

				Region region = (Region) in.readObject();
				cells[col][row] = new VisibilityCell(region.getX(), region.getY(), region.getSize()); 
			}
		}
		
		for(int col = 0; col < cells.length; col++) {
			for(int row = 0; row < cells.length; row++) {

				VisibilityCell cell = cells[col][row];
			
				int pvsSize = in.readInt();
				
				while(pvsSize-- > 0) {

					Region region = (Region) in.readObject(); 
					cell.addToPvs(cells[region.getX() / cellSize][region.getY() / cellSize]);
				}
			}
		}	
	}
	
	private void calculateForPosision(Map map, int sX, int sY) {

		Horizon horizon = new Horizon();
		VisibilityCell cell = cells[sX / cellSize][sY / cellSize];

		for (int range = 1; range < maxRange; range++) {

			int xMin = Math.max(sX - range, 0);
			int xMax = Math.min(sX + range, map.getWidth() - 1);
			int yMin = Math.max(sY - range, 0);
			int yMax = Math.min(sY + range, map.getWidth() - 1);

			// north, south
			for (int x = xMin; x <= xMax; x++) {

				calculateForLine(map, horizon, cell, sX, sY, x, yMin);
				calculateForLine(map, horizon, cell, sX, sY, x, yMax);
			}

			// west, east
			for (int y = yMin; y <= yMax; y++) {

				calculateForLine(map, horizon, cell, sX, sY, xMin, y);
				calculateForLine(map, horizon, cell, sX, sY, xMax, y);
			}
		}
	}

	private void calculateForLine(Map map, Horizon horizon, VisibilityCell cell, int sX, int sY, int tX, int tY) {

		if (sX != tX || sY != tY) {
			
			int dX = tX - sX;
			int dY = tY - sY;
			
			if (((dX * dX) + (dY * dY) <= (maxRange * maxRange)) && horizon.update(getSector(map, sX, sY, tX, tY))) {
				
				int row = tX / cellSize;
				int col = tY / cellSize;
				
				cell.addToPvs(cells[col][row]);
			}
		}
	}

	private Sector getSector(Map map, double sX, double sY, int tX, int tY) {

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

	public static Visibility calculateDummy(Map map, int cellSize, int visibilityRange) {
		
		Visibility visibility = new Visibility();
		
		visibility.cellSize = cellSize;
		visibility.maxRange = visibilityRange;

		int colNum = map.getWidth() / cellSize;
		int rowNum = map.getHeight() / cellSize;

		visibility.cells = new VisibilityCell[colNum][rowNum];

		for (int col = 0; col < colNum; col++) {
			for (int row = 0; row < rowNum; row++) {
				
				visibility.cells[col][row] = new VisibilityCell(col * cellSize, row * cellSize, cellSize);
			}
		}
		
		for (int col = 0; col < colNum; col++) {
			for (int row = 0; row < rowNum; row++) {
				
				int rangeInCells = visibilityRange / cellSize;
				
				for(int range = 1; range < rangeInCells; range++) {
					
					for(int tCol = col - range; tCol <= col + range; tCol++) {
						for(int tRow = row - range; tRow <= row + range; tRow++) {
					
							if(tCol >= 0 &&  tCol <colNum && tRow >= 0 && tRow < rowNum) {
								
								visibility.cells[col][row].addToPvs(visibility.cells[tCol][tRow]);
							}
						}
					}
				}
			}
		}

		return visibility;
	}
	
	public static Visibility calculate(Map map, int cellSize, int visibilityRange) {

		Visibility visibility = new Visibility();
		
		visibility.cellSize = cellSize;
		visibility.maxRange = visibilityRange;

		int colNum = map.getWidth() / cellSize;
		int rowNum = map.getHeight() / cellSize;

		visibility.cells = new VisibilityCell[colNum][rowNum];

		for (int col = 0; col < colNum; col++) {
			for (int row = 0; row < rowNum; row++) {
				
				visibility.cells[col][row] = new VisibilityCell(col * cellSize, row * cellSize, cellSize);
			}
		}
		
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {

				visibility.calculateForPosision(map, x, y);
			}
		}
		
		return visibility;
	}
	
	public int getCellSize() {
		
		return cellSize;
	}

	public int getMaxRange() {
		
		return maxRange;
	}
	
	public VisibilityCell getCell(int col, int row) {
		
		return cells[col][row];
	}
	
	public VisibilityCell getCellForPos(int x, int y) {
		
		return cells[x / cellSize][y / cellSize];
	}
}
