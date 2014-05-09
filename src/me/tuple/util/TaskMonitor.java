package me.tuple.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskMonitor {

	static Map<String, TaskMonitor> _monitors = new HashMap<>();
	static List<TaskMonitor> _orders = new ArrayList<>();
	static Runtime rt = Runtime.getRuntime();
	
	public static TaskMonitor monitor(String name) {
		
		synchronized(_monitors) {
			TaskMonitor monitor = _monitors.get(name);
			if (monitor==null) {
				monitor = new TaskMonitor(name);
				_monitors.put(name, monitor);
				_orders.add(monitor);
			}
			return monitor;
		}
		
	}
	
	
	public static Collection<TaskMonitor> allMonitors() {
		return _orders;
	}
	
	
	public String name;
	public long usedMemory;
	public long elapsedTime;
	int _cnt=0;
	
	protected long lastUsedMemory;
	protected long lastUsedTime;
	
	private TaskMonitor(String name) {
		this.name = name;
	}
	
	public void start() {
		if (_cnt++ > 0) return;
		
		long total = rt.totalMemory();
		long free = rt.freeMemory();
		//long max = rt.maxMemory();
		long used = total - free;
		
		lastUsedMemory = used;
		lastUsedTime = System.currentTimeMillis();
	}
	
	public void stop() {
		if (--_cnt > 0) return;
		
		long total = rt.totalMemory();
		long free = rt.freeMemory();
		//long max = rt.maxMemory();
		long used = total - free;
		
		usedMemory += used-lastUsedMemory;
		elapsedTime += System.currentTimeMillis()-lastUsedTime;
	}

	
	public String toString() {
		long total = rt.totalMemory();
		long free = rt.freeMemory();
		long max = rt.maxMemory();
		long used = total - free;
		return String.format("%s(%02d:%02d.%03d, %.1fMB used, %.1fMB total used, %.1fMB max)",
				name, elapsedTime/1000/60, elapsedTime/1000%60, elapsedTime%1000, usedMemory/1024/1024.0, used/1024/1024.0, max/1024/1024.0);
	}
	
}











