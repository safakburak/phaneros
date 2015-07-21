package p2p.network.planetsim;

import java.util.ArrayList;
import java.util.Iterator;

import p2p.application.loop.ILoopListener;
import p2p.application.loop.Loop;
import planet.commonapi.Application;
import planet.commonapi.Network;
import planet.commonapi.Node;
import planet.commonapi.exception.InitializationException;
import planet.generic.commonapi.GenericApp;
import planet.generic.commonapi.factory.GenericFactory;
import planet.scribe.Scribe;
import planet.scribe.ScribeImpl;
import planet.test.TestNames;

public class PSimNetwork extends GenericApp 
{
	private static PSimNetwork sInstance = null;
	
	private Network mNetwork;
	private ArrayList<Scribe> mAvaiableScribes = new ArrayList<Scribe>();
	
	private PSimNetwork() throws InitializationException
	{
		super("../conf/master.properties", TestNames.SCRIBE_SCRIBEPEERTEST, true, false, false, false);
		
		mNetwork = GenericFactory.buildNetwork();
		
		mNetwork.stabilize();
		
		@SuppressWarnings("rawtypes")
		Iterator nodeItr = mNetwork.iterator();
		
		while(nodeItr.hasNext())
		{
			Node node = (Node) nodeItr.next();
			
			Scribe scribe = new ScribeImpl("Scribe");
			
			node.registerApplication((Application) scribe, "Scribe");
			
			mAvaiableScribes.add(scribe);
		}
	}
	
	private void update()
	{
		for(int i = 0; i < 10; i++)
		{
			mNetwork.simulate();
		}
	}
	
	private PSimEndPoint createEndPoint(String id)
	{
		if(mAvaiableScribes.size() > 0)
		{
			return new PSimEndPoint(id, mAvaiableScribes.remove(0));
		}
		
		return null;
	}
	
	public static void initialize()
	{
		try
		{
			sInstance = new PSimNetwork();
		} 
		catch (InitializationException e)
		{
			e.printStackTrace();
		}
		
		Loop.setTimer(new ILoopListener() {
			@Override
			public void loopCallback(long deltaTime)
			{
				sInstance.update();
			}
		});
	}
	
	public static PSimEndPoint getEndPoint(String id)
	{
		return sInstance.createEndPoint(id);
	}
}
