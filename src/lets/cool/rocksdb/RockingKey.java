package lets.cool.rocksdb;

import java.util.Objects;

public abstract class RockingKey {

    public abstract byte[] toBytes();


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
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return Long.toString(id);
        }
    }

}
