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

package lets.cool.shpf.rc;

import com.seisw.util.geom.Poly;
import lets.cool.shpf.RecordContent;
import lets.cool.util.DynamicByteBuffer;

public class PointRC extends RecordContent {

    double x, y;

    public PointRC(int recordNumber, byte[] dataWithNoCopy) {
        super(recordNumber, dataWithNoCopy);
    }

    @Override
    public int shapeType() {
        return 1;
    }

    @Override
    public void parse() {
        x = input.getLEDouble();
        y = input.getLEDouble();
    }

    @Override
    public byte[] optimizedData() {
        if (optimizedData == null) {
            DynamicByteBuffer buff = new DynamicByteBuffer(rawData.length / 2);

            buff.putSignedVarLong((long) (x * BASE));
            buff.putSignedVarLong((long) (y * BASE));

            optimizedData = buff.toBytesBeforeCurrentPosition();
        }
        return  optimizedData;
    }

    @Override
    public Poly poly() { throw new UnsupportedOperationException("Not implement yet."); }

}
