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

public class Packages {
	final static byte RPCValueType_PACKAGE				= 0;
	final static byte RPCValueType_UINTEGER				= 1;	// var unsigned integer
	final static byte RPCValueType_SINTEGER				= 2;	// var signed integer
	final static byte RPCValueType_64BITS				= 3;	// fix 64 bits, only for double
	final static byte RPCValueType_32BITS				= 4;	// fix 32 bits, only for float
	final static byte RPCValueType_BYTES				= 5;	// with one UINT for length then data
	final static byte RPCValueType_COLLECTION			= 6;	// a collection with a number of values, it may contain anything. End with package end
	final static byte RPCValueType_COMPOSITE			= 7;	// a package, end with package end
	
	final static byte RPCPackageID_END				= 0;		// package end
	final static byte RPCPackageID_START			= 1;		// with one byte version, end with package end
	final static byte RPCPackageID_EXTEND			= 2;		// subclass, a new package, end with package end
	
	
	DynamicByteBuffer _buffer;
	
	public Packages() {
		_buffer = new DynamicByteBuffer(32);
	}
	
	
}




















