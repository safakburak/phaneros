package p2p.visibility.nuv.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

public class Patch {

	private int x;
	private int y;
	private int width;
	private int height;
	private BufferedImage data;

	public Patch(String path) {

		this.x = 0;
		this.y = 0;
		
		loadMap(path);
	}

	public Patch(int x, int y, int width, int height) {

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Patch(int x, int y, BufferedImage data) {

		this.x = x;
		this.y = y;
		this.data = data;
		this.width = data.getWidth();
		this.height = data.getHeight();
	}
	
	public Patch getPart(int x, int y, int w, int h) {
		
		return new Patch(x, y, data.getSubimage(x, y, w, h));
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
	
	public void saveMap(String path) {

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
	
	public void loadMap(String path) {

		try {
			
			data = ImageIO.read(new FileInputStream(path));
			width = data.getWidth();
			height = data.getHeight();
			
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

		return width;
	}

	public int getHeight() {

		return height;
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
		Patch other = (Patch) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
