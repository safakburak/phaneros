package p2p.visibility.nuv.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class Map implements Serializable {

	private int x;
	private int y;
	private BufferedImage data;

	public Map(String path) {

		this.x = 0;
		this.y = 0;
		
		load(path);
	}

	public Map(int x, int y, BufferedImage data) {

		this.x = x;
		this.y = y;
		this.data = data;
	}
	
	public Map getMapPart(int x, int y, int w, int h) {
		
		return new Map(x, y, data.getSubimage(x, y, w, h));
	}
	
	public void fillRandom(double fillRate, int stepSize) {
		
		Graphics g = data.getGraphics();
		
		for(int x = 0; x < data.getWidth(); x += stepSize) {
			for(int y = 0; y < data.getHeight(); y += stepSize) {
				
				if(Math.random() <= fillRate) {

					g.setColor(new Color(120, 120, 120));
					g.fillRect(x, y, 10, 10);
				}
			}	
		}
	}
	
	public void save(String path) {

		FileOutputStream outputStream;
		
		try {
			
			outputStream = new FileOutputStream(path);
			ImageIO.write(data, "png", outputStream);
			outputStream.flush();
			outputStream.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public void load(String path) {

		try {
			
			data = ImageIO.read(new FileInputStream(path));
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public int getX() {
		
		return x;
	}
	
	public int getY() {
		
		return y;
	}
	
	public int getWidth() {

		return data.getWidth();
	}

	public int getHeight() {

		return data.getHeight();
	}
	
	public BufferedImage getData() {
		
		return data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Map other = (Map) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	public int getHeightAt(int x, int y) {
		
		return data.getData().getSample(x, y, 0);
	}
}
