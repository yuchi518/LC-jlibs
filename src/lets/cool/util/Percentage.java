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

public class Percentage {

	protected double total;
	protected double value;
	protected double degree, amplify;
	protected int percentage, lastPercentage;
	protected String format;
	
	public Percentage() {
		this(0);
	}
	
	public Percentage(int degree) {
		this(0, degree);
	}
	
	public Percentage(double total) {
		this(total, 1);
	}
	
	/**
	 * 
	 * @param total
	 * @param degree degree=1 means the percentage is ddd.d format,
	 *  degree=0 means the percentage is ddd.dd format.
	 *  degree should be between -1 to 5. (dd0.0 ~ ddd.ddddd)
	 */
	public Percentage(double total, int degree) {
		reset(total, degree);
	}
	
	public Percentage reset(int degree) {
		return reset(0, degree);
	}
	
	public Percentage reset(double total) {
		return reset(total, 1);
	}
	
	/**
	 * Reset to reuse.
	 * @param total
	 * @param degree
	 * @return
	 */
	synchronized public Percentage reset(double total, int degree) {
		this.total = total;
		this.value = 0;
		this.percentage = 0;
		this.lastPercentage = 0;
		this.degree = Math.pow(10, degree);
		this.amplify = 100 * this.degree;
		this.format = degree<=0?"%3.0f%%":("%0" + (2+degree) + "."+degree+"f%%");
		return this;
	}
	
	synchronized public void setTotal(double total) {
		this.total = total;
		if (total!=0) percentage = (int)(value * amplify / total);
	}
	
	synchronized public double total() {
		return total;
	}
	
	synchronized public void setValue(double value) {
		this.value = value;
		if (total!=0) percentage = (int)(value * amplify / total);
	}
	
	synchronized public double value() {
		return value;
	}
	
	/**
	 * Gap between total() & value()
	 * @return
	 */
	synchronized public double gap() {
		return total-value;
	}
	
	/**
	 * This value should be 0 ~ 100.
	 * @return
	 */
	synchronized public double percentage() {
		return percentage/degree;
	}
	
	/**
	 * Use this to detect percentage changing.
	 * After call toString, this value will change to be false.
	 * @return
	 */
	synchronized public boolean isChanged() {
		return lastPercentage!=percentage;
	}
	
	/**
	 * Return toString() if isChanged() is true and change to false.
	 * Return null if isChanged() if false.
	 * @return
	 */
	synchronized public String changedString() {
		return isChanged()?this.toString():null;
	}
	
	synchronized public double addTotal(double total) {
		this.setTotal(this.total+total);
		return this.total;
	}
	
	/**
	 * Increase total by 1.
	 * @return
	 */
	synchronized public double incTotal() {
		this.setTotal(this.total+1);
		return this.total;
	}
	
	synchronized public double addValue(double value) {
		this.setValue(this.value+value);
		return this.value;
	}
	
	/**
	 * Increase value by 1.
	 * @return
	 */
	synchronized public double incValue() {
		this.setValue(this.value+1);
		return this.value;
	}
	

	/**
	 * After call toString, isChanged = !isChanged
	 */
	@Override
	synchronized public String toString() {
		lastPercentage = percentage;
		return total==0?"NAV.-":String.format(format, percentage/degree);
	}
	
	/*public String toDetailString() {
		return String.format("%3.1f%% left=%f", percentage/10.0);
	}*/
	
}




















