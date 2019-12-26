package lets.cool.geo;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lets.cool.util.logging.Logr;

import java.util.*;

/**
 * 增加 Edge 不一定表示總是"擴展" Polygon 的範圍，也可能會有"切除"的效果，
 * 端看 Edge 所在位置。
 * @param <T> Point type for two endpoints of edge.
 */
public class EdgePolygon<T> {
    final protected static Logr log = Logr.logger();

    private Multimap<T, Edge<T>> polygon;
    private Set<Edge<T>> edges;

    public EdgePolygon() {
        //polygon = LinkedListMultimap.create(); //debug insert order
        polygon = HashMultimap.create();
        edges = new HashSet<>();
    }

    public void addEdge(Edge<T> edge) {
        if (edges.contains(edge)) {
            edges.remove(edge);
            polygon.remove(edge.a, edge);
            polygon.remove(edge.b, edge);
        } else {
            edges.add(edge);
            polygon.put(edge.a, edge);
            polygon.put(edge.b, edge);
        }
    }

    public List<List<T>> toClosedWay() {
        ArrayList<List<T>> listlist = new ArrayList<>();

        Set<Edge<T>> clonedEdges = new HashSet<>(edges);

        while (clonedEdges.size() > 0) {
            ArrayList<T> list = new ArrayList<>();
            Edge<T> edge = clonedEdges.iterator().next();
            list.add(edge.a);
            list.add(edge.b);
            T first = edge.a;
            T last = edge.b;
            clonedEdges.remove(edge);

            while (true) {
                Collection<Edge<T>> collection = polygon.get(last);
                if ((collection.size() % 2) != 0) {
                    polygon.entries().forEach(entry -> log.warnf("%s %s", entry.getKey(), entry.getValue()));
                    log.exception("Not x2 edges at one point.");
                }
                Edge<T> nextEdge = null;
                for (Edge<T> eg : collection) {
                    if (!eg.equals(edge) && clonedEdges.contains(eg)) {
                        nextEdge = eg;
                        break;
                    }
                }

                if (nextEdge == null) {
                    polygon.entries().forEach(entry -> {
                        log.warnf("%s %s", entry.getKey(), entry.getValue());
                    });
                    log.exception("Is not a closed way ?????");
                    //return new ArrayList<>();
                }

                if (clonedEdges.size() == 0) {
                    log.exception("closedEdges is empty, not possible.");
                }
                clonedEdges.remove(nextEdge);
                if (nextEdge.a.equals(last)) {
                    last = nextEdge.b;
                } else {
                    last = nextEdge.a;
                }

                if (first.equals(last))
                    break;

                list.add(last);
                edge = nextEdge;
            }

            listlist.add(list);
        }

        return listlist;
    }
}

