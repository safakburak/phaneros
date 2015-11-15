package p2p.application.peer.components;

import actionsim.log.Logger;
import p2p.application.peer.IPeerComponent;
import p2p.application.peer.Peer;
import p2p.cache.Cache;
import p2p.constants.Constants;
import p2p.data.IntPair;
import p2p.patch.Patch;


public class MovementController implements IPeerComponent
{
	private Cache mPatchCache;
	
	private long mSinceLastWalk = 0;
	
	private int mDeltaX = 0;
	private int mDeltaY = 0;
	private Peer mOwner;
	
	public MovementController(Peer owner, Cache patchCache) 
	{
		mOwner = owner;
		
		mPatchCache = patchCache;
		
//		setUpUserControl();
	}
	
	@Override
	public void update(long deltaTime) 
	{
		mSinceLastWalk += deltaTime;
		
		if(mSinceLastWalk > 250)
		{
			mSinceLastWalk -= 250;
		
			if(mOwner.isUserControl == false)
			{
				step(deltaTime);
			}
		}
	}
	
//	private void setUpUserControl()
//	{
//		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
//			@Override
//			public void eventDispatched(AWTEvent event) 
//			{
//				if(event instanceof KeyEvent)
//				{
//					KeyEvent evt = (KeyEvent) event;
//					
//					if(evt.getID() == KeyEvent.KEY_PRESSED)
//					{
//						if(mOwner.isUserControl == true)
//						{
//							int keyCode = evt.getKeyCode();
//							
//							int deltaX = 0;
//							int deltaY = 0;
//							
//							if(keyCode == KeyEvent.VK_W)
//							{
//								deltaX = 0;
//								deltaY = -1;
//							}
//							else if(keyCode == KeyEvent.VK_S)
//							{
//								deltaX = 0;
//								deltaY = 1;
//							} 
//							else if(keyCode == KeyEvent.VK_A)
//							{
//								deltaX = -1;
//								deltaY = 0;
//							} 
//							else if(keyCode == KeyEvent.VK_D)
//							{
//								deltaX = 1;
//								deltaY = 0;
//							} 
//							else if(keyCode == KeyEvent.VK_Q)
//							{
//								deltaX = -1;
//								deltaY = -1;
//							}
//							else if(keyCode == KeyEvent.VK_E)
//							{
//								deltaX = 1;
//								deltaY = -1;
//							} 
//							else if(keyCode == KeyEvent.VK_Z)
//							{
//								deltaX = -1;
//								deltaY = 1;
//							} 
//							else if(keyCode == KeyEvent.VK_X)
//							{
//								deltaX = 1;
//								deltaY = 1;
//							} 
//							
//							IntCoords newCellPos = new IntCoords(mOwner.currentPos.getX() + deltaX, mOwner.currentPos.getY() + deltaY);
//							
//							IntCoords newPatchPos = new IntCoords(newCellPos.getX() / Constants.PATCH_SIZE, newCellPos.getY() / Constants.PATCH_SIZE);
//							
//							if(newCellPos.getX() > 0 && newCellPos.getY() > 0)
//							{
//								Patch patch = mPatchCache.getPatch(newPatchPos);
//								
//								if(patch != null && patch.getData(newCellPos) == 0)
//								{
//									mOwner.currentPos = newCellPos;
//									mOwner.currentPatch = newPatchPos;
//								}
//							}
//						}
//					}
//				}
//			}
//		}, AWTEvent.KEY_EVENT_MASK);
//	}
	
	private void step(long deltaTime)
	{
		IntPair oldPos = mOwner.currentPos;
		
		Patch oldPatch = mPatchCache.getPatchForPosition(oldPos);
		
		if(oldPatch == null)
		{
			mDeltaX = 0;
			mDeltaY = 0;
		}
		else
		{
			IntPair newPos = new IntPair(oldPos.getX() + mDeltaX, oldPos.getY() + mDeltaY);
			IntPair newPatch = new IntPair(newPos.getX() / Constants.PATCH_SIZE, newPos.getY() / Constants.PATCH_SIZE);
			
			if(newPos.getX() < 0 || newPos.getY() < 0 
					|| (mDeltaX == 0 && mDeltaY == 0) 
					|| newPatch.getX() >= Constants.PATCH_COUNT
					|| newPatch.getY() >= Constants.PATCH_COUNT)
			{
				randomize(oldPos);
			}
			else
			{
				mOwner.targetPatch = newPatch; 
				
				Patch newPatchData = mPatchCache.getPatch(newPatch);
				
				if(newPatchData != null)
				{
					if (newPatchData.getData(newPos) == 0)
					{
						mOwner.currentPos = newPos;
						mOwner.currentPatch = new IntPair(newPos.getX() / Constants.PATCH_SIZE, newPos.getY() / Constants.PATCH_SIZE);
					}
					else
					{
						randomize(oldPos);
					}
				}
			}
		}
	}
	
	private void randomize(IntPair oldPos)
	{
		mDeltaX = randomStep();
		mDeltaY = randomStep();
		
		while(oldPos.getX() + mDeltaX < 0 
				|| oldPos.getY() + mDeltaY < 0 
				|| (mDeltaX == 0 && mDeltaY == 0))
		{
			mDeltaX = randomStep();
			mDeltaY = randomStep();
		}
	}
	
	private int randomStep()
	{
		double random = Math.random();
		
		if(random > 0.66)
		{
			return 1;
		}
		else if(random < 0.33)
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
}
