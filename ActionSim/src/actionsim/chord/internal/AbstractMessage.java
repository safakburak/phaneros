package actionsim.chord.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import actionsim.chord.ChordId;

public class AbstractMessage {

	private ArrayList<ChordId> hopHistory = new ArrayList<ChordId>();

	public void addHop(ChordId id) {

		hopHistory.add(id);
	}

	public void addHops(List<ChordId> ids) {

		hopHistory.addAll(ids);
	}

	public int getHopCount() {

		return hopHistory.size();
	}

	public ChordId getLastHop() {

		return hopHistory.isEmpty() ? null : hopHistory.get(hopHistory.size() - 1);
	}

	public ArrayList<ChordId> getHopHistory() {
		return hopHistory;
	}

	@Override
	public String toString() {

		String result = getClass().getSimpleName() + "\n";
		result += "hops: ";

		for (ChordId id : hopHistory) {

			result += id.getKeyText() + "(" + id.getHexText() + ") ";
		}

		result += "\n";

		for (Field field : getClass().getDeclaredFields()) {

			field.setAccessible(true);

			try {
				result += field.getName() + ": " + field.get(this) + "\n";
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		return result;
	}
}
