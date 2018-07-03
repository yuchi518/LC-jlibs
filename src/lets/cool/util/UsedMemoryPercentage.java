package lets.cool.util;

public class UsedMemoryPercentage extends Percentage {

	Runtime rt;
	
	public UsedMemoryPercentage() {
		this(1);
	}

	public UsedMemoryPercentage(int degree) {
		super(degree);
		rt = Runtime.getRuntime();
		setTotal(rt.maxMemory());
		update();
	}

	/**
	 * Call to update current used memory percentage.
	 * @return Return isChanged() value.
	 */
	synchronized public boolean update() {
		setValue(usedMemory());
		return isChanged();
	}
	
	public long maxMemory() {
		return rt.maxMemory();
	}
	
	public long freeMemory() {
		return rt.freeMemory();
	}

	public long usedMemory() {
		return rt.totalMemory()-rt.freeMemory();
	}
	
	public long totalMemory() {
		return rt.totalMemory();
	}

}






