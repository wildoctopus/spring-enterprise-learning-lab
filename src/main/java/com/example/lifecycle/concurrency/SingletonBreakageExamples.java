package com.example.lifecycle.concurrency;

import java.lang.reflect.Constructor;

public final class SingletonBreakageExamples {

    private SingletonBreakageExamples() {
    }

    public static DatabaseClientSingleton createHolderInstanceWithReflection() {
        try {
            Constructor<DatabaseClientSingleton> constructor = DatabaseClientSingleton.class
                    .getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Reflection could not access the private constructor", exception);
        }
    }
}