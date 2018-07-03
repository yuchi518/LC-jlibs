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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemoryPrinter {
	public static void printMemory(Logger log, Level level, String info, final byte[] memory, int offset, int length, int address)
	{
		//log.
		if (log.isLoggable(level)) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			MemoryPrinter.printMemory(memory, offset, length, address, output);
			
			log.log(level, "{0}\n{1}", new Object[]{info, output.toString()});
		}
	}
	
	
	public static void printMemory(final byte[] memory, int offset, int length, int address, OutputStream output)
	{
		long dt = System.currentTimeMillis();
		PrintStream out;
		
		out = new PrintStream(output);
		String addressFormater;
		//NSCharacterSet *lcs = [NSCharacterSet letterCharacterSet];
		
		//address = (int)(Math.random()*1000);  // test different address
		
		if (length < 0) length = memory==null?0:(memory.length-offset);
		
		int mark = (address&0x0f);
		if (mark!=0) {
			address -= mark;
			length += mark;
		}
		

		if (address+length> 0x0000FFFF) {
			addressFormater = "%08X  ";
					
			out.print("           0  1  2  3  4  5  6  7    8  9  A  B  C  D  E  F\n");
		} else {
			addressFormater = "%04X  ";
			
			out.print("       0  1  2  3  4  5  6  7    8  9  A  B  C  D  E  F\n");
		}
		
		for (int i=0; i<length; ) {
			out.printf(addressFormater,address+i);
			
			for (int j=0; j<16; j++) {
				if (i+j>=mark && j+i<length) {
					out.printf("%02X ", memory[i+j-mark+offset]);
				} else {
					out.print("   ");
				}
				
				if (j==7) {
					out.print("  ");
				}
			}
			
			out.print(" ");
			
			for (int j=0; j<16; j++) {
				if (i+j>=mark && j+i<length) {
					byte c = memory[i+j-mark+offset];
					out.printf("%c", (c>=32&&c<=126)?c:'.');
				} else {
					out.print(" ");
				}
			}
			
			out.println();
			i+=16;
			if ((i!=length) && (i%256)==0) out.println();
		}
		
		if ((length%16)!=0) out.println();
		out.println(((System.currentTimeMillis()-dt)/1000.0) + "s");
	}
}


























