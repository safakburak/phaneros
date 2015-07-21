package p2p.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;

import p2p.application.peer.Peer;
import p2p.cache.Cache;
import p2p.constants.Constants;
import p2p.data.IntPair;
import p2p.patch.Patch;
import p2p.visibility.VisibilityMap;

public class Renderer
{
	private static final int cellDisplaySize = 7;
	
	private JFrame mFrame;
	
	private ImagePanel mPanel;
	
	private Cache mPatchCache;
	
	private Timer mRenderingTimer;
	
	private VisibilityMap mVisibility;
	
	private String mId;

	private Peer mOwner;
	
	public Renderer(Peer owner, String id, Cache patchCache, VisibilityMap visibility) 
	{
		mOwner = owner;
		
		mId = id;
		
		mPatchCache = patchCache;
		mVisibility = visibility;

		mPanel = new ImagePanel();
		
		mFrame = new JFrame();
		mFrame.setSize(500, 500);
		mFrame.setVisible(true);
		mFrame.setContentPane(mPanel);
		mFrame.setTitle(mId);
		
		mRenderingTimer = new Timer();
		
		mRenderingTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() 
			{
				render();
			}
		}, 1000, 100);
		
		mPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				mOwner.isUserControl = mOwner.isUserControl;
			}
		});
	}

	public void render() 
	{
		IntPair position = mOwner.currentPos; 
		
		int x = position.getX();
		int y = position.getY();
		
		Graphics2D g = (Graphics2D) mPanel.getBufferContext();
		
		if(g == null)
		{
			return;
		}
		
		g.setColor(Color.white);
		g.fillRect(0, 0, mFrame.getContentPane().getWidth(), mFrame.getContentPane().getHeight());
		
		AffineTransform oldTransform = g.getTransform();
		
		g.translate(-x * cellDisplaySize, -y * cellDisplaySize);
		g.translate(mFrame.getContentPane().getWidth() / 2, mFrame.getContentPane().getHeight() / 2);
		
		
		ArrayList<IntPair> pvs = mVisibility.get(mOwner.currentPatch);
		
		for(Patch patch : mPatchCache)
		{
			renderPatch(g, patch, pvs.contains(patch.getPos()));
		}
		
		for(IntPair patchPos : mVisibility.get(mOwner.currentPatch))
		{
			if(mPatchCache.getPatch(patchPos) == null)
			{
				renderPatchPlaceHolder(g, patchPos);
			}
		}
		
		g.setColor(Color.red);
		g.fillOval(x * cellDisplaySize, y * cellDisplaySize, cellDisplaySize, cellDisplaySize);
		
		g.setColor(Color.blue);
		
		ConcurrentHashMap<String, IntPair> peers = mOwner.peerPoses;
		
		for(String key : peers.keySet())
		{
			IntPair peerPosition = peers.get(key);
			
			if (peerPosition != null)
			{
				g.fillOval(peerPosition.getX() * cellDisplaySize, peerPosition.getY() * cellDisplaySize, cellDisplaySize, cellDisplaySize);
			}
		}
		
		g.setTransform(oldTransform);
		
		mPanel.toggleBuffer();
	}
	
	private void renderPatch(Graphics2D g, Patch patch, boolean isPvsPatch)
	{
		
		if(isPvsPatch)
		{
			g.setColor(Color.green.darker());
		}
		else
		{
			g.setColor(Color.gray.brighter());
		}
		
		AffineTransform oldTransform = g.getTransform();
		
		g.translate(patch.getPos().getX() * Constants.PATCH_SIZE  * cellDisplaySize, 
				patch.getPos().getY() * Constants.PATCH_SIZE  * cellDisplaySize);
		
		g.drawRect(0, 0, 
				Constants.PATCH_SIZE * cellDisplaySize, 
				Constants.PATCH_SIZE * cellDisplaySize);
		
		int[][] data = patch.getData();
		
		for(int x = 0; x < Constants.PATCH_SIZE; x++)
		{
			for(int y = 0; y < Constants.PATCH_SIZE; y++)
			{
				if(data[x][y] == 1)
				{
					g.fillRect(x * cellDisplaySize, y * cellDisplaySize, cellDisplaySize, cellDisplaySize);
				}
			}
		}
		
		g.setTransform(oldTransform);
	}
	
	private void renderPatchPlaceHolder(Graphics2D g, IntPair patchPos)
	{
		AffineTransform oldTransform = g.getTransform();
		
		g.translate(patchPos.getX() * Constants.PATCH_SIZE  * cellDisplaySize, 
				patchPos.getY() * Constants.PATCH_SIZE  * cellDisplaySize);
		
		g.setColor(Color.yellow);
		g.fillRect(0, 0, 
				Constants.PATCH_SIZE * cellDisplaySize, 
				Constants.PATCH_SIZE * cellDisplaySize);
		
		g.setColor(Color.gray);
		g.drawRect(0, 0, 
				Constants.PATCH_SIZE * cellDisplaySize, 
				Constants.PATCH_SIZE * cellDisplaySize);
		
		g.setTransform(oldTransform);
	}
}
