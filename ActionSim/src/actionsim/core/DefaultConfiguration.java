package actionsim.core;

public class DefaultConfiguration implements Configuration {

	@Override
	public float getStepLength() {
		
		return 100;
	}

	@Override
	public float getConnectionCost() {
		
		return 0;
	}

	@Override
	public float getDisconnectionCost() {
		
		return 0;
	}

	@Override
	public float getDefaultBandwidth() {
		
		return 0;
	}

	@Override
	public float getDefaultCpuBudget() {
		
		return 0;
	}
}
