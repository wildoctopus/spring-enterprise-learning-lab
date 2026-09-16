package com.example.lifecycle.concurrency;

import java.util.List;

/** Deliberately broken value object used to demonstrate aliasing. */
public final class BadImmutableUserProfile {

    private final List<String> roles;

    public BadImmutableUserProfile(List<String> roles) {
        this.roles = roles;
    }

    public List<String> roles() {
        return roles;
    }
}