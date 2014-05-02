package me.tuple.util;

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
					out.printf("%02X ", memory[i+j-mark]);
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
					byte c = memory[i+j-mark];
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


























