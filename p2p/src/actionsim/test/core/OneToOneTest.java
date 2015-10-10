package actionsim.test.core;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import actionsim.core.Message;
import actionsim.core.Node;
import actionsim.core.Simulation;
import actionsim.core.SimulationConfiguration;

public class OneToOneTest{

	
	
	@Test
	public void testConnection() throws InterruptedException {

		SimulationConfiguration.connectionCost = 250;
		
		Simulation simulation = new Simulation();
		
		Node n1 = simulation.createNode();
		Node n2 = simulation.createNode();
		
		n1.setCpuBudget(100);
		n1.setBandwidth(100);
		
		n1.requestConnectionTo(n2);

		simulation.iterate(1);
		
		assertTrue(n1.isConnectedTo(n2) == false);
		
		simulation.iterate(1);
		
		assertTrue(n1.isConnectedTo(n2) == false);
		
		simulation.iterate(1);
		
		assertTrue(n1.isConnectedTo(n2) == true);

		Node n3 = simulation.createNode();
		Node n4 = simulation.createNode();
		
		Message testMessage = new Message(n1, n2, 300);
		
		n1.send(testMessage);
		
		simulation.iterate(2);
		
	}
}
