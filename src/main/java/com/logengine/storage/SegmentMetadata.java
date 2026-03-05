package com.logengine.storage;

import java.io.*;
import java.nio.file.Path;

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

    public void save(Path metaPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaPath.toFile()))) {
            writer.write(minTimestamp + "\n");
            writer.write(maxTimestamp + "\n");
        }
    }

    public static SegmentMetadata load(Path metaPath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(metaPath.toFile()))) {
            long minTimestamp = Long.parseLong(reader.readLine());
            long maxTimestamp = Long.parseLong(reader.readLine());
            return new SegmentMetadata(minTimestamp, maxTimestamp);
        }
    }
}
