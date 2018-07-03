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

public class PolyLineRC extends RecordContent {

    public PolyLineRC(int recordNumber, byte[] dataWithNoCopy) {
        super(recordNumber, dataWithNoCopy);
    }

    @Override
    public int shapeType() {
        return 3;
    }

    @Override
    public void parse() {
        throw new UnsupportedOperationException("Not implement yet.");
    }

    @Override
    public byte[] optimizedData() {
        throw new UnsupportedOperationException("Not implement yet.");
    }

    @Override
    public Poly poly() { throw new UnsupportedOperationException("Not implement yet."); }

}
