package p2p.visibility;

class Elevation {
	public static Elevation MIN = new Elevation(-90);
	public static Elevation MAX = new Elevation(90);
	public static Elevation ZERO = new Elevation(0);

	private int value;

	public Elevation(double value) {
		this.value = (int) (value * 100);
	}

	@Override
	public boolean equals(Object obj) {

		return obj instanceof Elevation && this.value == ((Elevation) obj).value;
	}

	public boolean lt(Elevation angle) {
		return value < angle.value;
	}

	public boolean gt(Elevation angle) {
		return value > angle.value;
	}

	public boolean lte(Elevation angle) {
		return value <= angle.value;
	}

	public boolean gte(Elevation angle) {
		return value >= angle.value;
	}

	public double getValue() {
		return value / 100.0;
	}

	public Elevation add(Elevation angle) {
		return new Elevation(this.value + angle.value);
	}

	public Elevation sub(Elevation angle) {
		return new Elevation(this.value - angle.value);
	}
}
