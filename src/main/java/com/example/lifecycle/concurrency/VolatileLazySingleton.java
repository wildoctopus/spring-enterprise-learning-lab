package com.example.lifecycle.concurrency;

/**
 * Educational lazy singleton using double-checked locking.
 *
 * volatile is essential: without it, another thread could observe a reference
 * before the constructor's writes are visible. Prefer the holder idiom or an
 * enum unless lazy construction has a measured benefit.
 */
public final class VolatileLazySingleton {

    private static volatile VolatileLazySingleton instance;

    private VolatileLazySingleton() {
    }

    public static VolatileLazySingleton getInstance() {
        VolatileLazySingleton current = instance;
        if (current == null) {
            synchronized (VolatileLazySingleton.class) {
                current = instance;
                if (current == null) {
                    current = new VolatileLazySingleton();
                    instance = current;
                }
            }
        }
        return current;
    }
}
