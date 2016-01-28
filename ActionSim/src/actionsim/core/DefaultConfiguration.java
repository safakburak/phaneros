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
	public float getBandwidth(Node node) {

		return 0;
	}

	@Override
	public float getCpuBudget(Node node) {

		return 0;
	}

	@Override
	public int getOutboxCapacity(Node result) {

		return 0;
	}
}
