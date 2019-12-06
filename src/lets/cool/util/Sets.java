package lets.cool.util;

import java.util.Set;
import java.util.stream.Collectors;

public class Sets {

    public static <E> boolean intersects(Set<E> a, Set<E> b) {
        if (a==null || b==null) return false;

        if (a.size() > b.size()) {
            // swap
            Set<E> c = a;
            a = b;
            b = c;
        }
        // iterate small set to check large set
        for (E e: a) {
            if (b.contains(e)) return true;
        }
        return false;
    }

    public static <E> Set<E> intersection(Set<E> a, Set<E> b) {
        return a.stream().filter(b::contains).collect(Collectors.toSet());
    }
}
