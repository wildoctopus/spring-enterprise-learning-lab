package com.example.lifecycle.concurrency;

import java.util.List;
import java.util.Objects;

/**
 * Immutable value object: final class, final fields, no setters, and a
 * defensive copy of mutable input. Sharing it between threads is safe because
 * its state cannot change after construction.
 */
public final class ImmutableUserProfile {

    private final String userId;
    private final String displayName;
    private final List<String> roles;

    public ImmutableUserProfile(String userId, String displayName, List<String> roles) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.roles = List.copyOf(roles);
    }

    public String userId() {
        return userId;
    }

    public String displayName() {
        return displayName;
    }

    public List<String> roles() {
        return roles;
    }

    public ImmutableUserProfile withDisplayName(String newDisplayName) {
        // An update returns a new object rather than mutating this one.
        return new ImmutableUserProfile(userId, newDisplayName, roles);
    }
}
