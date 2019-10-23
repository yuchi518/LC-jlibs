package lets.cool.rocksdb;

import lets.cool.util.ByteUtil;
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

    public byte[] getBlob(RockingKey key) {
        return get(key.toBytes());
    }

    public void setBlob(byte[] key, byte[] blob) {
        put(key, blob);
    }

    public void setBlob(RockingKey key, byte[] blob) {
        put(key.toBytes(), blob);
    }

    public int setBlobAsync(byte[] key, byte[] blob) {
        AsyncBlob asb = new AsyncBlob(key, blob, false);
        return putAsync(asb);
    }

    public int setBlobAsync(RockingKey key, byte[] blob) {
        AsyncBlob asb = new AsyncBlob(key.toBytes(), blob, false);
        return putAsync(asb);
    }

    public void appendBlob(byte[] key, byte[] appendingBlob) {
        byte[] originBlob = get(key);
        if (originBlob == null) {
            put(key, appendingBlob);
        } else {
            byte[] blob = ByteUtil.concat(originBlob, appendingBlob);
            put(key, blob);
        }
    }

    public void appendBlob(RockingKey key, byte[] appendingBlob) {
        appendBlob(key.toBytes(), appendingBlob);
    }

    public int appendBlobAsync(byte[] key, byte[] appendingBlob) {
        AsyncBlob asb = new AsyncBlob(key, appendingBlob, true);
        return putAsync(asb);
    }

    public int appendBlobAsync(RockingKey key, byte[] appendingBlob) {
        AsyncBlob asb = new AsyncBlob(key.toBytes(), appendingBlob, true);
        return putAsync(asb);
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
