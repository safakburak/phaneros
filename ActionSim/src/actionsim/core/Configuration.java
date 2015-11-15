package actionsim.core;

public interface Configuration {

	public float getStepLength();
	
	public float getConnectionCost();
	
	public float getDisconnectionCost();
	
	public float getDefaultBandwidth();
	
	public float getDefaultCpuBudget();
}
