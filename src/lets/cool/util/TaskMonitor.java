/*
 * LC-jlibs, lets.cool java libraries
 * Copyright (C) 2015-2018 Yuchi Chen (yuchi518@gmail.com)

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation. For the terms of this
 * license, see <http://www.gnu.org/licenses>.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package lets.cool.util;

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
	
	public TaskMonitor start() {
		if (_cnt++ > 0) return this;
		
		long total = rt.totalMemory();
		long free = rt.freeMemory();
		//long max = rt.maxMemory();
		long used = total - free;
		
		lastUsedMemory = used;
		lastUsedTime = System.currentTimeMillis();
		
		return this;
	}
	
	public TaskMonitor stop() {
		if (--_cnt > 0) return this;
		
		long total = rt.totalMemory();
		long free = rt.freeMemory();
		//long max = rt.maxMemory();
		long used = total - free;
		
		usedMemory += used-lastUsedMemory;
		elapsedTime += System.currentTimeMillis()-lastUsedTime;
		
		return this;
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











