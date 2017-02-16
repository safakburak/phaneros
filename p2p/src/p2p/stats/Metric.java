package p2p.stats;

import java.util.HashMap;

import actionsim.log.Logger;

public class Metric {

	private String name;

	private long sampleCount;
	private double sampleSum;
	private double sampleMax = Long.MIN_VALUE;
	private double sampleMin = Long.MAX_VALUE;

	private HashMap<Object, Long> timeMark = new HashMap<Object, Long>();

	public void markTime(Object owner) {

		timeMark.put(owner, Stats.getTime());
	}

	public void sampleTime(Object owner) {

		sample(Stats.getTime() - timeMark.get(owner));
	}

	public Metric(String name) {

		this.name = name;
	}

	public void sample() {

		sample(1);
	}

	public void sample(double amount) {

		sampleCount++;
		sampleSum += amount;

		if (amount < sampleMin) {

			sampleMin = amount;
		}

		if (amount > sampleMax) {

			sampleMax = amount;
		}
	}

	public void report() {

		if (sampleCount > 0) {

			Logger.log(name + " COUNT: \t" + sampleCount);

			if (sampleCount != sampleSum || sampleMin != sampleMax) {

				Logger.log(name + " SUM: \t" + format(sampleSum));
				Logger.log(name + " AVG: \t" + format(sampleSum / sampleCount));
				Logger.log(name + " MIN: \t" + format(sampleMin));
				Logger.log(name + " MAX: \t" + format(sampleMax));
			}
		} else {

			Logger.log(name + ": \t NOT AVAILABLE");
		}

	}

	private String format(double value) {

		return String.format("%.2f", value).replace('.', ',');
	}
}
