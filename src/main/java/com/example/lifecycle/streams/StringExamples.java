package com.example.lifecycle.streams;

public final class StringExamples {

    private StringExamples() {
    }

    public static StringComparison compare(String left, String right) {
        return new StringComparison(left == right, left.equals(right));
    }

    public static String buildWithStringBuilder(String prefix, String value) {
        StringBuilder builder = new StringBuilder(prefix);
        builder.append(value);
        return builder.toString();
    }

    public static String buildWithStringBuffer(String prefix, String value) {
        StringBuffer buffer = new StringBuffer(prefix);
        buffer.append(value);
        return buffer.toString();
    }

    public record StringComparison(boolean sameReference, boolean equalValue) {
    }
}