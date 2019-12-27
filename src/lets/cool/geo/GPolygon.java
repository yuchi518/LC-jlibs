package lets.cool.geo;

import lets.cool.util.logging.Logr;

import java.awt.geom.Path2D;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class GPolygon<T extends GPoint> implements Iterable<T> {
    final protected static Logr log = Logr.logger();

    public enum PolygonPart {
        LEFT,
        RIGHT,
        //BOTH = 3,         // not supported

        // for mac os coordinate system, origin point (0,0) is in left-bottom corner.
        //LEFT_MACOS    = LEFT,
        //RIGHT_MACOS   = RIGHT,
        // for ios coordinate system, origin point (0,0) is in left-top corner
        //LEFT_IOS      = RIGHT,
        //RIGHT_IOS     = LEFT,
    }

    static public <T extends GPoint> GPolygon<T> create() {
        return new GPolygon<>();
    }

    static public <T extends GPoint> GPolygon<T> create(T ... points) {
        return new GPolygon<>(Arrays.asList(points));
    }

    static public <T extends GPoint> GPolygon<T> create(List<T> pointsList) {
        return new GPolygon<>(pointsList);
    }

    protected List<T> points;

    protected GPolygon() {
        points = new ArrayList<>();
    }

    protected GPolygon(List<T> pointsList) {
        points = pointsList;
    }

    public int count() {
        return points.size();
    }

    public T firstPoint() {
        return points.get(0);
    }

    public T lastPoint() {
        return points.get(points.size()-1);
    }

    public List<T> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public void appendPoint(T point) {
        points.add(point);
    }

    public void removeLastPoint() {
        points.remove(points.size()-1);
    }

    public void insertPoint(T point, int index) {
        points.add(index, point);
    }

    public List<GPolygon<T>> split(PolygonPart part, List<T> line) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator() {
        return points.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        points.forEach(action);
    }

    public <V extends T> GPolygon<V> castAll(Function<? super T, ? extends V> action) {
        points.replaceAll(item -> action.apply(item));
        return (GPolygon<V>)this;
    }

    public Path2D toPath() {
        return toPath(true, null);
    }

    public Path2D toPath(boolean forceToClose, Function<? super T, ? extends T> converter) {
        if (converter == null)
            converter = v -> v;

        Path2D path = new Path2D.Double();
        Iterator<T> iter = points.iterator();
        if (iter.hasNext()) {
            T p = converter.apply(iter.next());
            path.moveTo(p.getX(), p.getY());
            while (iter.hasNext()) {
                p = converter.apply(iter.next());
                path.lineTo(p.getX(), p.getY());
            }
            if (forceToClose) {
                path.closePath();
            }
        }
        return path;
    }
}
