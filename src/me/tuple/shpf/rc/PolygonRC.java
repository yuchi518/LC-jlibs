package me.tuple.shpf.rc;

import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;
import com.seisw.util.geom.PolySimple;
import me.tuple.shpf.RecordContent;
import me.tuple.util.DynamicByteBuffer;

public class PolygonRC extends RecordContent {

    public double Xmin, Ymin, Xmax, Ymax;
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
        Xmin = input.getLEDouble();
        Ymin = input.getLEDouble();
        Xmax = input.getLEDouble();
        Ymax = input.getLEDouble();

        if (Xmin > Xmax) {
            double tmp = Xmin;
            Xmin = Xmax;
            Xmax = tmp;
            System.out.println("Swap Xmin & Xmax");
        }

        if (Ymin > Ymax) {
            double tmp = Ymin;
            Ymin = Ymax;
            Ymax = tmp;
            System.out.println("Swap YMin & YMax");
        }

        NumParts = input.getLEInt();
        NumPoints = input.getLEInt();

        Parts = new int[NumParts];
        for (int i = 0; i < NumParts; i++) Parts[i] = input.getLEInt();

        Points = new double[NumPoints * 2];
        for (int i = 0; i < NumPoints * 2;) {
            Points[i] = input.getLEDouble();
            /*if (Points[i]<Xmin || Points[i]>Xmax) {
                System.err.println("Out of boundary");
            }*/
            i++;
            Points[i] = input.getLEDouble();
            /*if (Points[i]<Ymin || Points[i]>Ymax) {
                System.err.println("Out of boundary");
            }*/
            i++;
        }

        if (input.hasRemaining()) throw new RuntimeException("Why still have data?");

        if (Xmin >= Xmax || Ymin >= Ymax)
            System.out.printf("Box (%f,%f,%f,%f) \n", Xmin, Ymin, Xmax, Ymax);

        ///optimizedData();
    }

    @Override
    public byte[] optimizedData() {
        if (optimizedData == null) {
            DynamicByteBuffer buff = new DynamicByteBuffer(rawData.length / 2);

            buff.putVarLong(this.shapeType());
            buff.putSignedVarLong((long) (Xmin * BASE));
            buff.putSignedVarLong((long) (Ymin * BASE));
            buff.putSignedVarLong((long) (Xmax * BASE) - (long) (Xmin * BASE));
            buff.putSignedVarLong((long) (Ymax * BASE) - (long) (Ymin * BASE));

            buff.putVarLong(NumParts);
            buff.putVarLong(NumPoints);

            int dI = 0;
            for (int i = 0; i < NumParts; i++) {
                buff.putSignedVarLong(Parts[i] - dI);
                dI = Parts[i];
            }

            long dX = (long) (Xmin * BASE), dY = (long) (Ymin * BASE);
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
        PolyDefault ps = new PolyDefault();

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
                        ps.xor(sp);
                        break;
                    }
                    else
                        sp.add(x, y);

                }
                else
                {
                    if (i>=Parts[j]*2) {
                        // do we make sure these polygons are holes?
                        ps.xor(sp);
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








