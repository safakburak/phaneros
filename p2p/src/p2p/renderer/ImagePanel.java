package p2p.renderer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel 
{
	private Image mPrimaryImage;
	
	private Image mSecondaryImage;

	public Graphics getBufferContext()
	{
		if(mSecondaryImage == null)
		{
			return null;
		}
		else
		{
			return mSecondaryImage.getGraphics();
		}
	}
	
	public void toggleBuffer()
	{
		Image tmp = mPrimaryImage;
		mPrimaryImage = mSecondaryImage;
		mSecondaryImage = tmp;
		
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	{
		if(mPrimaryImage != null)
		{
			g.drawImage(mPrimaryImage, 
					0, 0, getWidth(), getHeight(), 
					0, 0, getWidth(), getHeight(), 
					null);
		}
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) 
	{
		super.setBounds(x, y, width, height);
		
		mPrimaryImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		mSecondaryImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
}
