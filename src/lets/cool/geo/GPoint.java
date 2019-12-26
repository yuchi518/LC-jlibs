package lets.cool.geo;

import lets.cool.util.MathUtil;

import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 * Geometry point.
 */
public interface GPoint {
    double getX();
    double getY();

    default Point2D.Double toPoint2D() {
        return (this instanceof Point2D.Double) ? (Point2D.Double)this : new Point2D.Double(getX(), getY());
    }

    default boolean equalsLocation(GPoint pp) {
        return getX()==pp.getX() && getY()==pp.getY();
    }

    class Impl extends Point2D.Double implements GPoint {
        public Impl(double x, double y) {
            super(x, y);
        }
    }

    class Virtual implements GPoint {
        @Override
        public double getX() {
            throw new IllegalStateException("Should not access virtual point.");
        }

        @Override
        public double getY() {
            throw new IllegalStateException("Should not access virtual point.");
        }

        /*@Override
        public boolean equalsLocation(PolygonPoint pp) {
            return (pp ins);
        }*/
    }


    // colinear: 在幾何學中，共線是指點在空間中的一種關係，表示一系列點落在同一條直線上的性質

    /**
     * Given three 'colinear' points p, q, r, the function checks if
     * point q lies on line segment 'pr' 
     */
    static boolean onSegment(GPoint p, GPoint q, GPoint r)
    {
        if (q.getX() <= Math.max(p.getX(), r.getX()) && q.getX() >= Math.min(p.getX(), r.getX()) &&
                q.getY() <= Math.max(p.getY(), r.getY()) && q.getY() >= Math.min(p.getY(), r.getY()))
            return true;

        return false;
    }

    /**
     * To find orientation of ordered triplet (p, q, r).
     * The function returns following values
     * 0 --> p, q and r are colinear
     * 1 --> Clockwise
     * -1 --> Counterclockwise
     */
    static int orientation(GPoint p, GPoint q, GPoint r)
    {
        // See https://www.geeksforgeeks.org/orientation-3-ordered-points/ 
        // for details of below formula. 
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) -
                (q.getX() - p.getX()) * (r.getY() - q.getY());

        if (val == 0) return 0; // colinear 

        return (val > 0)? 1: -1; // clock or counterclock wise
    }

    static int orientation(Iterator<GPoint> closedLinesIter) {
        GPoint a = closedLinesIter.next();
        GPoint b = closedLinesIter.next();
        GPoint c = closedLinesIter.next();

        if (!closedLinesIter.hasNext()) {
            return orientation(a, b, c);
        } else {
            GPoint first = a;
            int ori = orientation(a, b, c);
            int pos = ori > 0 ? 1 : 0;
            int neg = ori < 0 ? 1 : 0;
            int eq = ori == 0 ? 1 : 0;

            while (closedLinesIter.hasNext()) {
                a = b;
                b = c;
                c = closedLinesIter.next();
                ori = orientation(a, b, c);
                if (ori > 0) pos++;
                else if (ori < 0) neg++;
                else eq++;
            }

            if (!c.equalsLocation(first)) {
                a = b;
                b = c;
                c = first;
                ori = orientation(a, b, c);
                if (ori > 0) pos++;
                else if (ori < 0) neg++;
                else eq++;
            }

            return Integer.compare(pos, neg);
        }
    }

    /**
     * This function returns true if line segment 'p1q1' and 'p2q2' intersect.
     * ref: https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
     */
    static boolean intersects(GPoint p1, GPoint q1, GPoint p2, GPoint q2)
    {
        // Find the four orientations needed for general and 
        // special cases 
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        // General case 
        if (o1 != o2 && o3 != o4)
            return true;

        // Special Cases 
        // p1, q1 and p2 are colinear and p2 lies on segment p1q1 
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;

        // p1, q1 and q2 are colinear and q2 lies on segment p1q1 
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;

        // p2, q2 and p1 are colinear and p1 lies on segment p2q2 
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;

        // p2, q2 and q1 are colinear and q1 lies on segment p2q2 
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;

        return false; // Doesn't fall in any of the above cases 
    }

    /**
     *
     */
    static GPoint intersectionOfTwoLines(GPoint A, GPoint B, GPoint C, GPoint D)
    {
        // Line AB represented as a1x + b1y = c1
        double a1 = B.getY() - A.getY();
        double b1 = A.getX() - B.getX();
        double c1 = a1*(A.getX()) + b1*(A.getY());

        // Line CD represented as a2x + b2y = c2
        double a2 = D.getY() - C.getY();
        double b2 = C.getX() - D.getX();
        double c2 = a2*(C.getX())+ b2*(C.getY());

        double determinant = a1*b2 - a2*b1;

        if (determinant == 0)
        {
            // The lines are parallel. This is simplified
            // by returning a pair of NaN
            return new Impl(Double.NaN, Double.NaN);
        }
        else
        {
            // TODO: 如何處理加減乘除後多餘的尾數，目前直接進位到小數點第十四位
            double x = MathUtil.round((b2 * c1 - b1 * c2)/determinant, 14);
            double y = MathUtil.round((a1*c2 - a2*c1)/determinant, 14);
            return new Impl(x, y);
        }
    }

    static double distanceBetweenPoints(GPoint A, GPoint B)
    {
        return Math.sqrt(Math.pow(A.getX()-B.getX(), 2) + Math.pow(A.getY()-B.getY(), 2));
    }

    /**
     *
     * @param A
     * @param B
     * @return
     */
    static double distancePow2BetweenPoints(GPoint A, GPoint B)
    {
        return (Math.pow(A.getX()-B.getX(), 2) + Math.pow(A.getY()-B.getY(), 2));
    }
}
