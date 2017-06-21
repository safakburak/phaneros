package actionsim.core;

public class DefaultConfiguration implements Configuration {

	@Override
	public Float getStepLength() {

		return 100f;
	}

	@Override
	public Float getConnectionCost() {

		return 0f;
	}

	@Override
	public Float getDisconnectionCost() {

		return 0f;
	}

	@Override
	public Float getUploadBandwidth(Node node) {

		return null;
	}

	@Override
	public Float getDownloadBandwidth(Node node) {

		return null;
	}

	@Override
	public Float getCpuBudget(Node node) {

		return null;
	}
}
