package p2p.timer;

import java.util.ArrayList;
import java.util.Iterator;

import actionsim.AbstractNodeListener;
import actionsim.core.Action;
import actionsim.core.Node;

public class Timer extends AbstractNodeListener {

	private ArrayList<Task> tasks = new ArrayList<Task>();
	private ArrayList<Task> newTasks = new ArrayList<Task>();

	public Timer(Node node) {

		node.addNodeListener(this);
	}

	public void delay(TimedAction action, float delay) {

		if (delay == 0) {

			action.act(0);

		} else {

			newTasks.add(new Task(action, 0, delay));
		}
	}

	public void repeat(TimedAction action, float period, float delay) {

		if (delay == 0) {

			action.act(0);
		}

		if (period != 0 || delay != 0) {

			newTasks.add(new Task(action, period, delay));
		}
	}

	public void repeat(TimedAction action, float period) {

		repeat(action, period, period);
	}

	@Override
	public void onStep(Action[] completedActions, float deltaTime) {

		tasks.addAll(newTasks);
		newTasks.clear();

		Iterator<Task> itr = tasks.iterator();

		while (itr.hasNext()) {

			Task task = itr.next();
			if (task.step(deltaTime)) {

				itr.remove();
			}
		}
	}

	private class Task {

		private TimedAction action;
		private float period;
		private float time;

		public Task(TimedAction action, float period, float delay) {

			this.action = action;
			this.period = period;
			this.time = period - delay;
		}

		public boolean step(float time) {

			this.time += time;

			boolean acted = false;

			if (this.time >= this.period) {

				this.time -= this.period;
				action.act(period);

				acted = true;
			}

			return period == 0 && acted;
		}
	}
}
