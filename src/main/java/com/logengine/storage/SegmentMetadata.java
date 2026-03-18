package com.logengine.storage;

import java.io.*;
import java.nio.file.Path;

/**
 * Persistence model for segment-level metrics (timestamp boundaries).
 * Stored as companion .meta files on disk to avoid scanning large logs.
 */
public class SegmentMetadata {
    private long minTimestamp;
    private long maxTimestamp;

    public SegmentMetadata(long minTimestamp, long maxTimestamp) {
        this.minTimestamp = minTimestamp;
        this.maxTimestamp = maxTimestamp;
    }

    public long getMinTimestamp() {
        return minTimestamp;
    }

    public long getMaxTimestamp() {
        return maxTimestamp;
    }

    /**
     * Persists min and max timestamps to a .meta file on disk.
     */
    public void save(Path metaPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaPath.toFile()))) {
            writer.write(minTimestamp + "\n");
            writer.write(maxTimestamp + "\n");
        }
    }

    /**
     * Loads time-range metadata from a previously saved .meta file.
     */
    public static SegmentMetadata load(Path metaPath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(metaPath.toFile()))) {
            long minTimestamp = Long.parseLong(reader.readLine());
            long maxTimestamp = Long.parseLong(reader.readLine());
            return new SegmentMetadata(minTimestamp, maxTimestamp);
        }
    }
}
