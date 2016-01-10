package p2p.stats;

public class Metric {

	private String name;

	private long sampleCount;

	public Metric(String name) {

		this.name = name;
	}

	public void sample() {

		sampleCount++;
	}

	public void report() {

		System.out.println(name + ": " + sampleCount);
	}
}
