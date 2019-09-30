package lets.cool.rocksdb;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;

import java.io.File;
import java.util.function.Consumer;

public class RockingBlobsCache/*<TK extends RockingKey>*/ extends RockingCache {
    public RockingBlobsCache(File folder) {
        super(folder);
    }

    public RockingBlobsCache(File folder, Consumer<Options> optionsConsumer, boolean hardReadonly) {
        super(folder, optionsConsumer, hardReadonly);
    }

    protected RockingBlobsCache(RocksDB rDB, String name) {
        super(rDB, name, false);
    }

    protected RockingBlobsCache(RocksDB rDB, String name, boolean hardReadonly) {
        super(rDB, name, hardReadonly);
    }

    public void setBlob(byte[] key, byte[] blob) {
        put(key, blob);
    }

    public void setBlob(RockingKey key, byte[] blob) {
        put(key.toBytes(), blob);
    }

    public void setBlobAsync(byte[] key, byte[] blob) {
        AsyncBlob asb = new AsyncBlob(key, blob, false);
        putAsync(asb);
    }

    public void setBlobAsync(RockingKey key, byte[] blob) {
        AsyncBlob asb = new AsyncBlob(key.toBytes(), blob, false);
        putAsync(asb);
    }

    public void appendBlob(byte[] key, byte[] appendingBlob) {
        byte[] originBlob = get(key);
        if (originBlob == null) {
            put(key, appendingBlob);
        } else {
            byte[] blob = new byte[originBlob.length + appendingBlob.length];

            System.arraycopy(originBlob, 0, blob, 0, originBlob.length);
            System.arraycopy(appendingBlob, 0, blob, originBlob.length, appendingBlob.length);

            put(key, blob);
        }
    }

    public void appendBlob(RockingKey key, byte[] appendingBlob) {
        appendBlob(key.toBytes(), appendingBlob);
    }

    public void appendBlobAsync(byte[] key, byte[] appendingBlob) {
        AsyncBlob asb = new AsyncBlob(key, appendingBlob, true);
        putAsync(asb);
    }

    public void appendBlobAsync(RockingKey key, byte[] appendingBlob) {
        AsyncBlob asb = new AsyncBlob(key.toBytes(), appendingBlob, true);
        putAsync(asb);
    }


    private class AsyncBlob extends Async {
        final boolean appending;
        final byte[] key;
        final byte[] blob;

        AsyncBlob(byte[] key, byte[] blob, boolean appending) {
            this.key = key;
            this.blob = blob;
            this.appending = appending;
        }

        @Override
        protected void process() {
            if (appending) {
                appendBlob(key, blob);
            } else {
                setBlob(key, blob);
            }
        }

        @Override
        protected int size() {
            return 1;
        }
    }
}
