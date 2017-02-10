package actionsim.core;

public interface Configuration {

	public float getStepLength();

	public float getConnectionCost();

	public float getDisconnectionCost();

	public float getBandwidth(Node node);

	public float getCpuBudget(Node node);
}
