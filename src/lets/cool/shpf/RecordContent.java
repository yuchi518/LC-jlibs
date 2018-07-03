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

package lets.cool.shpf;

import com.seisw.util.geom.Poly;
import lets.cool.util.DynamicByteBuffer;

public abstract class RecordContent {
    final public static long BASE = 1000000000L; // Use to covert double to long

    final public int recordNumber;
	final public byte rawData[];
	final protected DynamicByteBuffer input;
	public byte optimizedData[]=null;
	
	protected RecordContent(int recordNumber, byte dataWithNoCopy[]) {
		this.recordNumber = recordNumber;
		this.rawData = dataWithNoCopy;
		input = new DynamicByteBuffer(dataWithNoCopy, true);
		
		int type = input.getLEInt(); 
		if (type != shapeType()) {
			throw new RuntimeException("Type is not match, " + type + " != " + shapeType());
		}
	}
	
	public abstract int shapeType();
	
	// not include first four byte (shape type)
	public abstract void parse();
	
	public abstract byte[] optimizedData();

    public abstract Poly poly();

	@Override
	public int hashCode() {
		return recordNumber;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordContent other = (RecordContent) obj;
		if (recordNumber != other.recordNumber)
			return false;
		return true;
	}
	
	
}






















