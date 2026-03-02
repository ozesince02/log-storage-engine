package com.logengine.model;

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

    public String serialize() {
        return timestamp + "|" + logLevel + "|" + message + "\n";
    }

    // TODO: we use timestamp|logLevel|message format for serialization for now, but we will upgrade this later
    public static LogEntry deserialize(String logLine) {
        String[] parts = logLine.split("\\|", 3);
        return new LogEntry(Long.parseLong(parts[0]), parts[1], parts[2]);
    }
}
