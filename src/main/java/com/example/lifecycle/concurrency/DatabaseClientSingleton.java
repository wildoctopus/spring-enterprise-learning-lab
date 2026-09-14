package com.example.lifecycle.concurrency;

/**
 * Thread-safe singleton using Java class initialization.
 *
 * This is usually preferable to hand-written lazy locking when one instance
 * must exist for the whole JVM and construction is cheap enough at startup.
 */
public final class DatabaseClientSingleton {

    private DatabaseClientSingleton() {
        // Private constructor prevents callers from creating arbitrary instances.
    }

    private static class Holder {
        // The JVM initializes this class once, safely, when getInstance() first runs.
        private static final DatabaseClientSingleton INSTANCE = new DatabaseClientSingleton();
    }

    public static DatabaseClientSingleton getInstance() {
        return Holder.INSTANCE;
    }
}
