package p2p.visibility;

class Heading {
	public static Heading MIN = new Heading(0);
	public static Heading MAX = new Heading(360);

	private int value;

	public Heading(double value) {
		this.value = (int) (value * 100);
	}

	@Override
	public boolean equals(Object obj) {

		return obj instanceof Heading && this.value == ((Heading) obj).value;
	}

	public boolean lt(Heading angle) {
		return value < angle.value;
	}

	public boolean gt(Heading angle) {
		return value > angle.value;
	}

	public boolean lte(Heading angle) {
		return value <= angle.value;
	}

	public boolean gte(Heading angle) {
		return value >= angle.value;
	}

	public double getValue() {
		return value / 100.0;
	}

	public Heading add(Heading angle) {
		return new Heading(this.value + angle.value);
	}

	public Heading sub(Heading angle) {
		return new Heading(this.value - angle.value);
	}
}
