package actionsim.core;

public interface Configuration {

	public Float getStepLength();

	public Float getConnectionCost();

	public Float getDisconnectionCost();

	public Float getUploadBandwidth(Node node);

	public Float getDownloadBandwidth(Node node);

	public Float getCpuBudget(Node node);
}
