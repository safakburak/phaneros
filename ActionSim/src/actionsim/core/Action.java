package actionsim.core;

public abstract class Action {

	private float cpuCost;
	
	public Action(float cpuCost) {
		
		this.cpuCost = cpuCost;
	}
	
	public float getCpuCost() {
		
		return cpuCost;
	}
	
	protected abstract void run();
}
