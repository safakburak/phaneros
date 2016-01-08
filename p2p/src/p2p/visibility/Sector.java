package p2p.visibility;

class Sector {

	private Angle start;
	private Angle end;
	private Angle elev;

	public Sector(double start, double end, double elev) {

		this.start = new Angle(start);
		this.end = new Angle(end);
		this.elev = new Angle(elev);
	}

	public Sector(Angle start, Angle end, Angle elev) {

		this.start = start;
		this.end = end;
		this.elev = elev;
	}

	public Angle getStart() {

		return start;
	}

	public void setStart(Angle start) {

		this.start = start;
	}

	public Angle getEnd() {

		return end;
	}

	public void setEnd(Angle end) {

		this.end = end;
	}

	public Angle getElev() {

		return elev;
	}

	public void setElev(Angle elev) {

		this.elev = elev;
	}

	/**
	 * @param angle
	 * @return if angle is in [start, end)
	 */
	public boolean contains(Angle angle, boolean isLeftClosed) {

		if (end.gt(start)) {

			if (isLeftClosed) {

				return angle.gte(start) && angle.lt(end);

			} else {

				return angle.gt(start) && angle.lt(end);
			}
		} else {

			if (isLeftClosed) {

				return (angle.gte(start) && angle.lt(Angle.MAX)) || (angle.gte(Angle.MIN) && angle.lt(end));

			} else {

				return (angle.gt(start) && angle.lt(Angle.MAX)) || (angle.gt(Angle.MIN) && angle.lt(end));
			}
		}
	}

	@Override
	public String toString() {

		return "[" + start.getValue() + ", " + end.getValue() + ") : " + elev.getValue();
	}
}
