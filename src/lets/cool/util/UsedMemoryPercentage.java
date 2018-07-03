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






