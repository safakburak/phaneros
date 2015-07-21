package planet.test.scribe;

import java.util.Iterator;

import planet.commonapi.Application;
import planet.commonapi.Network;
import planet.commonapi.Node;
import planet.commonapi.exception.InitializationException;
import planet.generic.commonapi.GenericApp;
import planet.generic.commonapi.factory.GenericFactory;
import planet.scribe.Scribe;
import planet.scribe.ScribeContent;
import planet.scribe.ScribeImpl;
import planet.scribe.Topic;
import planet.simulate.NetworkSimulator;
import planet.test.TestNames;
import planet.util.Properties;

/**
 * @author Ruben Mondejar <Ruben.Mondejar@estudiants.urv.es>
 */

public class ScribePeerTest extends GenericApp
{

	// Simulator
	private static Network network = null;

	/**
	 * Constructor
	 * 
	 */
	public ScribePeerTest() throws InitializationException
	{
		// arguments: properties file, application level, events, results,
		// serialization
		super("../conf/master.properties", TestNames.SCRIBE_SCRIBEPEERTEST, true, false, false, false);

		try
		{
			network = GenericFactory.buildNetwork();
			
			network.stabilize();
			
			Iterator it = network.iterator();
			
			while (it.hasNext())
			{
				Node node = (Node) it.next();
				
				Scribe scribe = new ScribeImpl("Scribe"); 
				
				node.registerApplication((Application) scribe, "Scribe");
				
				scribe.subscribe(new Topic(GenericFactory.buildKey("seed")), new ScribeApplication("Client"));
			}
			
		} 
		catch (InitializationException e)
		{
			System.out.println("Occur an exception in initialization of " + this.getClass().getName() + ": " + e.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * Per enviar un event Scribe
	 */
	public void sendEvent()
	{
		ScribeTestMessage textMissatge = new ScribeTestMessage("Hello World!");
		
		Scribe scribe = (Scribe) network.getRandomApplication("Scribe");
		
		try
		{
			scribe.publish(new Topic(GenericFactory.buildKey("seed")), textMissatge);
		} 
		catch (InitializationException e)
		{
			e.printStackTrace();
		}
		
		for(int i = 0; i < 10; i++)
		{
			network.simulate();
		}
	}

	/**
	 * Metode principal: rep els arguments i inicialitza el peer servidor
	 * 
	 * @param args
	 * @throws InitializationException 
	 */
	public static void main(String args[]) throws InitializationException
	{
		ScribePeerTest c = new ScribePeerTest();
		
		c.sendEvent();
	}

	class ScribeTestMessage implements ScribeContent
	{
		String message;

		public ScribeTestMessage(String message)
		{
			this.message = message;
		}

		public String toString()
		{
			return message;
		}

	}
}
