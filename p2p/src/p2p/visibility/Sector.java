package p2p.visibility;

class Sector {

	private Heading start;
	private Heading end;
	private Elevation elev;

	public Sector(double start, double end, double elev) {

		this.start = new Heading(start);
		this.end = new Heading(end);
		this.elev = new Elevation(elev);
	}

	public Sector(Heading start, Heading end, Elevation elev) {

		this.start = start;
		this.end = end;
		this.elev = elev;
	}

	public Heading getStart() {

		return start;
	}

	public void setStart(Heading start) {

		this.start = start;
	}

	public Heading getEnd() {

		return end;
	}

	public void setEnd(Heading end) {

		this.end = end;
	}

	public Elevation getElev() {

		return elev;
	}

	public void setElev(Elevation elev) {

		this.elev = elev;
	}

	/**
	 * @param angle
	 * @return if angle is in [start, end)
	 */
	public boolean contains(Heading angle, boolean isLeftClosed) {

		if (end.gt(start)) {

			if (isLeftClosed) {

				return angle.gte(start) && angle.lt(end);

			} else {

				return angle.gt(start) && angle.lt(end);
			}
		} else {

			if (isLeftClosed) {

				return (angle.gte(start) && angle.lt(Heading.MAX)) || (angle.gte(Heading.MIN) && angle.lt(end));

			} else {

				return (angle.gt(start) && angle.lt(Heading.MAX)) || (angle.gt(Heading.MIN) && angle.lt(end));
			}
		}
	}

	@Override
	public String toString() {

		return "[" + start.getValue() + ", " + end.getValue() + ") : " + elev.getValue();
	}
}
