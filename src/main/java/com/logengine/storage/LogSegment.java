package com.logengine.storage;

import com.logengine.model.LogEntry;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents a single physical log file on disk.
 * Handles appends and tracks timestamp ranges for indexing.
 */
public class LogSegment {
    private final Path filePath;
    private final BufferedWriter writer;
    public long size;
    private long minTimestamp = Long.MAX_VALUE;
    private long maxTimestamp = Long.MIN_VALUE;

    public LogSegment(Path filePath) throws IOException {
        this.filePath = filePath;
        this.writer = new BufferedWriter(new FileWriter(filePath.toFile(), true));
        this.size = filePath.toFile().length();
    }

    /**
     * Appends a log entry to this segment's file and updates internal metrics.
     * Thread-safe.
     */
    public synchronized void append(LogEntry logEntry) throws IOException {
        String serialized = logEntry.serialize();
        writer.write(serialized);
        writer.flush();
        size += serialized.getBytes().length;
        minTimestamp = Math.min(minTimestamp, logEntry.getTimestamp());
        maxTimestamp = Math.max(maxTimestamp, logEntry.getTimestamp());
    }

    public long getSize() {
        return size;
    }

    public void flush() throws IOException {
        writer.flush();
    }

    /**
     * Closes the file writer and persists time-range metadata for this segment.
     */
    public void close() throws IOException {
        writer.close();
        Path metaPath = Path.of(filePath.toString() + ".meta");
        SegmentMetadata meta = new SegmentMetadata(minTimestamp, maxTimestamp);
        meta.save(metaPath);
    }

    public Path getFilePath() {
        return filePath;
    }

    public long getMinTimestamp() {
        return minTimestamp;
    }

    public long getMaxTimestamp() {
        return maxTimestamp;
    }

    /**
     * Updates segment-level metadata. Useful when recovering existing segments.
     */
    public void setMetadata(long minTimestamp, long maxTimestamp) {
        this.minTimestamp = minTimestamp;
        this.maxTimestamp = maxTimestamp;
    }
}
