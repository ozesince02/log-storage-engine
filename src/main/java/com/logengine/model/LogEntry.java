package com.logengine.model;

/**
 * Simple text-based representation of a log entry.
 * Primarily used for delimiter-based serialization.
 */
public class LogEntry {
    private final long timestamp;
    private final String logLevel;
    private final String message;

    public LogEntry(long timestamp, String logLevel, String message) {
        this.timestamp = timestamp;
        this.logLevel = logLevel;
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Converts the LogEntry object to a pipe-delimited string format.
     */
    public String serialize() {
        return timestamp + "|" + logLevel + "|" + message + "\n";
    }

    /**
     * Parses a pipe-delimited string back into a LogEntry object.
     * Format: timestamp|logLevel|message
     */
    public static LogEntry deserialize(String logLine) {
        String[] parts = logLine.split("\\|", 3);
        return new LogEntry(Long.parseLong(parts[0]), parts[1], parts[2]);
    }
}
