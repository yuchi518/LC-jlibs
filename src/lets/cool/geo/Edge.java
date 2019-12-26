package lets.cool.geo;

public class Edge<T> {
    final public T a, b;

    public Edge(T a, T b) {
        this.a = a;
        this.b = b;
    }

    public T getA() {
        return a;
    }

    public T getB() {
        return b;
    }

    @Override
    public int hashCode() {
        int a_h = a.hashCode();
        int b_h = b.hashCode();
        if (a_h < b_h) {
            return (a_h * 13) ^ b_h;
        } else {
            return (b_h * 13) ^ a_h;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Edge) {
            Edge other = (Edge)obj;
            return (other.a.equals(this.a) && other.b.equals(this.b)) ||
                    (other.a.equals(this.b) && other.b.equals(this.a));
        }
        return false;
    }
}
