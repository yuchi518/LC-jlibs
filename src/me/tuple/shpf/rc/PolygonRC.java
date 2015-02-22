package me.tuple.shpf.rc;

import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;
import com.seisw.util.geom.PolySimple;
import me.tuple.shpf.RecordContent;
import me.tuple.util.DynamicByteBuffer;

public class PolygonRC extends RecordContent {

    public double MinX, MinY, MaxX, MaxY;
    public int NumParts, NumPoints;
    public int Parts[];
    public double Points[];

    public PolygonRC(int recordNumber, byte[] dataWithNoCopy) {
        super(recordNumber, dataWithNoCopy);
    }

    @Override
    public int shapeType() {
        return 5;
    }

    @Override
    public void parse() {
        MinX = input.getLEDouble();
        MinY = input.getLEDouble();
        MaxX = input.getLEDouble();
        MaxY = input.getLEDouble();

        if (MinX > MaxX) {
            double tmp = MinX;
            MinX = MaxX;
            MaxX = tmp;
            System.out.println("Swap MinX & MaxX");
        }

        if (MinY > MaxY) {
            double tmp = MinY;
            MinY = MaxY;
            MaxY = tmp;
            System.out.println("Swap YMin & YMax");
        }

        NumParts = input.getLEInt();
        NumPoints = input.getLEInt();

        Parts = new int[NumParts];
        for (int i = 0; i < NumParts; i++) Parts[i] = input.getLEInt();

        Points = new double[NumPoints * 2];
        for (int i = 0; i < NumPoints * 2;) {
            Points[i] = input.getLEDouble();
            /*if (Points[i]<MinX || Points[i]>MaxX) {
                System.err.println("Out of boundary");
            }*/
            i++;
            Points[i] = input.getLEDouble();
            /*if (Points[i]<MinY || Points[i]>MaxY) {
                System.err.println("Out of boundary");
            }*/
            i++;
        }

        if (input.hasRemaining()) throw new RuntimeException("Why still have data?");

        if (MinX >= MaxX || MinY >= MaxY)
            System.out.printf("Box (%f,%f,%f,%f) \n", MinX, MinY, MaxX, MaxY);

        ///optimizedData();
    }

    @Override
    public byte[] optimizedData() {
        if (optimizedData == null) {
            DynamicByteBuffer buff = new DynamicByteBuffer(rawData.length / 2);

            buff.putVarLong(this.shapeType());
            buff.putSignedVarLong((long) (MinX * BASE));
            buff.putSignedVarLong((long) (MinY * BASE));
            buff.putSignedVarLong((long) (MaxX * BASE) - (long) (MinX * BASE));
            buff.putSignedVarLong((long) (MaxY * BASE) - (long) (MinY * BASE));

            buff.putVarLong(NumParts);
            buff.putVarLong(NumPoints);

            int dI = 0;
            for (int i = 0; i < NumParts; i++) {
                buff.putSignedVarLong(Parts[i] - dI);
                dI = Parts[i];
            }

            long dX = (long) (MinX * BASE), dY = (long) (MinY * BASE);
            for (int i = 0; i < NumPoints * 2; ) {
                buff.putSignedVarLong((long) (Points[i] * BASE) - dX);
                dX = (long) (Points[i] * BASE);
                i++;
                buff.putSignedVarLong((long) (Points[i] * BASE) - dY);
                dY = (long) (Points[i] * BASE);
                i++;

            }

            optimizedData = buff.toBytesBeforeCurrentPosition();

            //System.out.println(rawData.length + " vs. " + optimizedData.length);
        }
        return optimizedData;
    }

    @Override
    public Poly poly() {
        Poly ps = new PolyDefault();

        if (NumParts==0)
        {

        }
        else if (NumParts==1)
        {
            for (int i=0; ; ) {
                double x = Points[i++];
                double y = Points[i++];
                //System.out.print('.');

                if (i<NumPoints*2)
                    ps.add(x, y);
                else
                    break;
            }
        }
        else
        {
            int i=0;
            for (i=0; i<Parts[1]*2; ) {
                double x = Points[i++];
                double y = Points[i++];
                //System.out.print('.');

                if (i<NumPoints*2)
                    ps.add(x, y);
                else
                    break;
            }

            int j=2;
            Poly sp = new PolySimple();
            for (; ;)
            {
                double x = Points[i++];
                double y = Points[i++];
                //System.out.print('.');

                if (j>=NumParts)
                {
                    if (i>=NumPoints*2) {
                        //ps = ps.xor(sp);
                        sp.setIsHole(true);
                        ps.add(sp);
                        break;
                    }
                    else
                        sp.add(x, y);

                }
                else
                {
                    if (i>=Parts[j]*2) {
                        // do we make sure these polygons are holes?
                        //ps = ps.xor(sp);
                        sp.setIsHole(true);
                        ps.add(sp);
                        sp = new PolySimple();
                        j++;
                    }
                    else
                        sp.add(x, y);
                }
            }

        }

        return ps;
    }

}








