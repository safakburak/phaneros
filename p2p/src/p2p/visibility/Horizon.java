package p2p.visibility;

class Horizon {

	private Sector[] sectors = new Sector[10000];

	private int length;

	public Horizon() {

		sectors[0] = new Sector(Angle.MIN, Angle.MAX, new Angle(0));
		length = 1;
	}

	public boolean update(Sector s) {

		long t = System.nanoTime();

		if (s.getStart().gt(s.getEnd())) {

			boolean r1 = update(new Sector(s.getStart(), Angle.MAX, s.getElev()));
			boolean r2 = update(new Sector(Angle.MIN, s.getEnd(), s.getElev()));

			return r1 || r2;
		}

		boolean updated = false;

		int start = findIndex(s.getStart(), 0, length);

		for (int i = start; i < length; i++) {

			Sector d = sectors[i];

			if (s.getElev().gt(d.getElev())) { // güncelleme olacak

				if (s.getStart().equals(d.getStart())) { // başlangıçlar ayni

					if (s.getEnd().equals(d.getEnd())) { // tam üstüste geldi

						d.setElev(s.getElev());

						updated = true;
						break;

					} else if (s.getEnd().lt(d.getEnd())) { // bitiş içerde
															// kaldı

						System.arraycopy(sectors, i, sectors, i + 1, length - i);
						length++;

						sectors[i + 1] = new Sector(s.getEnd(), d.getEnd(), d.getElev());
						d.setElev(s.getElev());
						d.setEnd(s.getEnd());

						updated = true;
						break;

					} else { // diğer sektöre DEVAM

						d.setElev(s.getElev());
						s.setStart(d.getEnd());

						updated = true;
					}

				} else { // başlangıç içerde kaldı. bölüp DEVAM et.

					System.arraycopy(sectors, i, sectors, i + 1, length - i);
					length++;

					sectors[i + 1] = new Sector(s.getStart(), d.getEnd(), d.getElev());
					d.setEnd(s.getStart());

					updated = true;
				}

			} else { // güncelleme olmayacak

				if (s.getEnd().lte(d.getEnd())) { // bitiş içerde kaldı

					break;

				} else { // sonraki sektöre devam

					s.setStart(d.getEnd());
				}
			}
		}

		if (length > 1) {

			for (int i = length - 1; i > start; i--) {

				if (sectors[i].getElev().equals(sectors[i - 1].getElev())) {

					sectors[i].setStart(sectors[i - 1].getStart());
					System.arraycopy(sectors, i, sectors, i - 1, length - i);
					length--;
				}
			}
		}

		return updated;
	}

	private int findIndex(Angle angle, int start, int end) {

		int index = start + (end - start) / 2;

		Sector sector = sectors[index];

		if (sector.contains(angle, true)) {

			return index;
		}

		if (angle.lt(sector.getStart())) {

			return findIndex(angle, start, index);

		} else {

			return findIndex(angle, index + 1, end);
		}
	}

	@Override
	public String toString() {

		String result = "";

		for (int i = 0; i < length; i++) {

			result += sectors[i].toString() + "\n";
		}

		return result;
	}

	public static void main(String[] args) {

		Horizon horizon = new Horizon();
		System.out.println(horizon);

		horizon.update(new Sector(60, 120, 10));
		System.out.println(horizon);

		horizon.update(new Sector(50, 70, 20));
		System.out.println(horizon);

		horizon.update(new Sector(80, 100, 30));
		System.out.println(horizon);

		horizon.update(new Sector(20, 150, 30));
		System.out.println(horizon);

		horizon.update(new Sector(0, 360, 0));
		System.out.println(horizon);

		horizon.update(new Sector(0, 360, 100));
		System.out.println(horizon);

		horizon.update(new Sector(300, 60, 200));
		System.out.println(horizon);

		horizon.update(new Sector(355, 350, 205));
		System.out.println(horizon);

		System.out.println(horizon.update(new Sector(355, 350, 205)));
		System.out.println(horizon.update(new Sector(355, 350, 205)));

		System.out.println(horizon.update(new Sector(355, 5, 300)));
		System.out.println(horizon.update(new Sector(45, 90, 350)));

	}
}
