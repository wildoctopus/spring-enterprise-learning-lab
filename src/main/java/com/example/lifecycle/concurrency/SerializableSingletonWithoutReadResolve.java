package com.example.lifecycle.concurrency;

import java.io.Serial;
import java.io.Serializable;

/** Deliberately broken: deserialization creates a second instance. */
public final class SerializableSingletonWithoutReadResolve implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final SerializableSingletonWithoutReadResolve INSTANCE =
            new SerializableSingletonWithoutReadResolve();

    private SerializableSingletonWithoutReadResolve() {
    }

    public static SerializableSingletonWithoutReadResolve getInstance() {
        return INSTANCE;
    }
}