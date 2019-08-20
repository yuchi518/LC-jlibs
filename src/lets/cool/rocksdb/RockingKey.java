package lets.cool.rocksdb;

import java.util.Arrays;
import java.util.Objects;

public abstract class RockingKey implements Comparable<RockingKey> {

    public abstract byte[] toBytes();

    @Override
    public int hashCode() {
        return Arrays.hashCode(toBytes());
    }

    @Override
    public boolean equals(Object obj) {
        return this==obj ||
                ((this.getClass() == obj.getClass()) && Arrays.equals(toBytes(), ((RockingKey) obj).toBytes()));
    }

    @Override
    public String toString() {
        return Arrays.toString(toBytes());
    }

    @Override
    public int compareTo(RockingKey o) {
        return Arrays.compare(toBytes(), o.toBytes());
    }

    /**
     * LongID 以 variable length 編碼，導致 bytes[] 的排序與 Long 的排序不同，須注意使用。
     */
    public static class LongID extends RockingKey {

        final public long id;

        public LongID(long l) {
            id = l;
        }

        public LongID(byte[] bytes) {
            id = RockingObject.longFromBytes(bytes);
        }

        @Override
        public byte[] toBytes() {
            return RockingObject.bytesFromLong(id);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LongID)) return false;
            LongID longID = (LongID) o;
            return id == longID.id;
        }

        @Override
        public int compareTo(RockingKey o) {
            if (o instanceof LongID) {
                return Long.compare(id, ((LongID)o).id);
            }
            return super.compareTo(o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return Long.toString(id);
        }
    }

    public static class BytesID extends RockingKey {

        final public byte[] id;

        public BytesID(byte[] id) {
            this(id, true);
        }
        public BytesID(byte[] id, boolean clone) {
            this.id = clone ? id.clone() : id;
        }

        @Override
        public byte[] toBytes() {
            return id;
        }
    }
}
