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

import lets.cool.util.logging.Logr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskMonitor {

	static Map<String, TaskMonitor> _monitors = new HashMap<>();

	public static TaskMonitor monitor(String name) {
		
		synchronized(_monitors) {
			TaskMonitor monitor = _monitors.get(name);
			if (monitor==null) {
				monitor = new TaskMonitor(name);
				_monitors.put(name, monitor);
			}
			return monitor;
		}
	}
	
	public static Collection<TaskMonitor> allMonitors() {
		return _monitors.values();
	}

	public enum UpdateEvent {
		NONE(0), PROGRESS(1), MEMORY(2), BOTH(3);

		final public int flag;
		UpdateEvent(int flag) {
			this.flag = flag;
		}

		public boolean isProgressUpdated() {
			return (this.flag & PROGRESS.flag) != 0;
		}

		public boolean isMemoryUpdated() {
			return (this.flag & MEMORY.flag) != 0;
		}
	}
	
	final public String name;
	final public String upperName;
	final private UsedMemoryPercentage memory;
	final private Percentage progress;

	private long usedMemory;
	private long elapsedTime;
	private int _cnt=0;

	private long lastUsedMemory;
	private long lastUsedTime;

	public TaskMonitor(String name) {
		this.name = name;
		this.upperName = name.toUpperCase();
		this.memory = new UsedMemoryPercentage();
		this.progress = new Percentage(1);
	}
	
	public TaskMonitor start() {
		if (_cnt++ > 0) return this;
		
		long used = memory.usedMemory();
		
		lastUsedMemory = used;
		lastUsedTime = System.currentTimeMillis();
		
		return this;
	}
	
	public TaskMonitor stop() {
		if (--_cnt > 0) return this;
		
		long used = memory.usedMemory();
		
		usedMemory += used-lastUsedMemory;
		elapsedTime += System.currentTimeMillis()-lastUsedTime;
		
		return this;
	}

	public long elapsedTime() {
		return (_cnt == 0) ? elapsedTime : elapsedTime + (System.currentTimeMillis() - lastUsedTime);
	}

	public String elapsedTimeText() {
		return HumanReadableText.elapsedTime(this.elapsedTime());
	}

	public String elapsedTimeDetailText() {
		return HumanReadableText.elapsedTimeDetail(this.elapsedTime());
	}

	public UpdateEvent checkEvent() {
		if (progress.isChanged()) {
			return memory.update() ? UpdateEvent.BOTH : UpdateEvent.PROGRESS;
		} else {
			return memory.update() ? UpdateEvent.MEMORY : UpdateEvent.NONE;
		}
	}

	public UpdateEvent update() {
		return memory.update() ? UpdateEvent.MEMORY : UpdateEvent.NONE;
	}

	public UpdateEvent updateIncrementalProgress(double value) {
		progress.addValue(value);
		return checkEvent();
	}

	public UpdateEvent updateIncrementalProgress(double value, double total) {
		progress.addValue(value);
		progress.addTotal(total);
		return checkEvent();
	}

	public UpdateEvent updateProgress(double value) {
		progress.setValue(value);
		return checkEvent();
	}

	public UpdateEvent updateProgress(double value, double total) {
		progress.setValue(value);
		progress.setTotal(total);
		return checkEvent();
	}

	public void resetProgressTotal(double progressTotal) {
		progress.reset(progressTotal, 1);
	}

	public double memoryPercentage() {
		return memory.percentage();
	}

	public double progressPercentage() {
		return progress.percentage();
	}

	public String memoryUsage() {
		return memory.usageText();
	}

	public String progressText() {
		return progress.toString();
	}
	
	public String toString() {
		return String.format("[%s]-%s(%s), Mem: %s",
				upperName, this.elapsedTimeDetailText(), progressText(), memoryUsage()/*usedMemory==0 ? "NAV" : HumanReadableText.byteCount(usedMemory)*/);
	}

	public String usageText() {
		return String.format("[%s]-%s(%s), Mem: %s",
				upperName, elapsedTimeText(), progressText(), memoryUsage());
	}

	// ====
	private long progressShownTime = 0L;
	private double progressShownValue = 0.0;
	public void logIfNecessary(Logr log) {
		UpdateEvent event = checkEvent();
		long dT = System.currentTimeMillis() - progressShownTime;
		double dP = progressPercentage() - progressShownValue;
		if (event.isProgressUpdated() && (progress.isCompleted() || (dP >= 10 && dT >= 10 * 1000) || (dP >= 1 && dT >= 60 * 1000))) {
			log.info(usageText());
			progressShownTime = System.currentTimeMillis();
			progressShownValue = progressPercentage();
		}
	}

	public boolean logIfNecessaryOrMemoryUsageAbovePercentage(Logr log, double percentage) {
		memory.update();
		if (this.memoryPercentage() > percentage) {
			log.warn(usageText());
			return true;
		} else {
			logIfNecessary(log);
		}
		return false;
	}
}











