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

import java.io.*;
import java.util.Iterator;

import lets.cool.shpf.rc.MultiPatchRC;
import lets.cool.shpf.rc.MultiPointMRC;
import lets.cool.shpf.rc.MultiPointRC;
import lets.cool.shpf.rc.MultiPointZRC;
import lets.cool.shpf.rc.NullRC;
import lets.cool.shpf.rc.PointMRC;
import lets.cool.shpf.rc.PointRC;
import lets.cool.shpf.rc.PointZRC;
import lets.cool.shpf.rc.PolyLineMRC;
import lets.cool.shpf.rc.PolyLineRC;
import lets.cool.shpf.rc.PolyLineZRC;
import lets.cool.shpf.rc.PolygonMRC;
import lets.cool.shpf.rc.PolygonRC;
import lets.cool.shpf.rc.PolygonZRC;
import lets.cool.util.InputStreamByteBuffer;
import lets.cool.util.Percentage;

public class ShapeFile implements Iterator<RecordContent> {
	
	final InputStreamByteBuffer input;
	final File shpFile;

	int fileCode, fileLength, version, shapeType;
	double Xmin, Ymin, Xmax, Ymax, Zmin, Zmax, Mmin, Mmax;
	public Percentage completedPercentage;

    public ShapeFile(File shpFile) throws FileNotFoundException {
        if (!shpFile.exists() || !shpFile.isFile()) {
            throw new FileNotFoundException();
        }
        this.input = new InputStreamByteBuffer(new java.io.FileInputStream(shpFile));
        this.shpFile = shpFile;
        this.loadFileHeader();
    }
	
	public ShapeFile(FileInputStream in) {
		this(new InputStreamByteBuffer(new BufferedInputStream(in)));
	}
	
	public ShapeFile(InputStreamByteBuffer input) {
		this.input = input;
        this.shpFile = null;
		this.loadFileHeader();
	}

    public String getName()
    {
        String name = shpFile.getName();
        if (name.toLowerCase().endsWith(".shp"))
            name = name.substring(0, name.length()-4);
        return name;
    }

	void loadFileHeader() {
		fileCode = input.getInt();		// == 9994
		input.getInt();					// unused == 0
		input.getInt();					// unused == 0
		input.getInt();					// unused == 0
		input.getInt();					// unused == 0
		input.getInt();					// unused == 0
		fileLength = input.getInt();		// 16-bit words
		version = input.getLEInt();			// 1000
		shapeType = input.getLEInt();		// shape type
		
		Xmin = input.getLEDouble();
		Ymin = input.getLEDouble();
		Xmax = input.getLEDouble();
		Ymax = input.getLEDouble();
		Zmin = input.getLEDouble();
		Zmax = input.getLEDouble();
		Mmin = input.getLEDouble();
		Mmax = input.getLEDouble();
		
		completedPercentage = new Percentage(fileLength, 1);
		completedPercentage.addValue(50);		// 100 bytes = 50 x 16-bits
		
	}
	
	public RecordContent loadRecord() {
		if (!input.hasRemaining()) return null;
		return next();
	}

	@Override
	public boolean hasNext() {
		return input.hasRemaining();
	}

	@Override
	public RecordContent next() {
		int recordNumber = input.getInt();
		int contentLength = input.getInt();		// 16-bit words
		
		
		//int startPosition = input.po
		
		int type = input.peekLEInt();
		byte b[] = input.getBytesWithFixedLength(contentLength*2);
		
		completedPercentage.addValue(4 + contentLength);
		
		switch(type) {
		case 0:
			return new NullRC(recordNumber, b);
		case 1:
			return new PointRC(recordNumber, b);
		case 3:
			return new PolyLineRC(recordNumber, b);
		case 5:
			return new PolygonRC(recordNumber, b);
		case 8:
			return new MultiPointRC(recordNumber, b);
		case 11:
			return new PointZRC(recordNumber, b);
		case 13:
			return new PolyLineZRC(recordNumber, b);
		case 15:
			return new PolygonZRC(recordNumber, b);
		case 18:
			return new MultiPointZRC(recordNumber, b);
		case 21:
			return new PointMRC(recordNumber, b);
		case 23:
			return new PolyLineMRC(recordNumber, b);
		case 25:
			return new PolygonMRC(recordNumber, b);
		case 28:
			return new MultiPointMRC(recordNumber, b);
		case 31:
			return new MultiPatchRC(recordNumber, b);
		}
		throw new RuntimeException("Not support shape type: " + type);
	}

	@Override
	public void remove() {
		
	}
}



























