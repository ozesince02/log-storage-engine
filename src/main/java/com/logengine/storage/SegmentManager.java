package com.logengine.storage;

import com.logengine.model.LogEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SegmentManager {
    private final Path logDir;
    private final long maxSegmentSize;
    private final List<LogSegment> segments = new ArrayList<>();
    private LogSegment activeSegment;
    private int segmentCounter = 0;

    public SegmentManager(Path logDir, long maxSegmentSize) throws IOException {
        this.logDir = logDir;
        this.maxSegmentSize = maxSegmentSize;
        if (!Files.exists(logDir)) {
            Files.createDirectories(logDir);
        }

        loadExistingSegments();
        if (segments.isEmpty()) {
            createNewSegment();
        }
    }

    public synchronized void append(LogEntry logEntry) throws IOException {
        if (activeSegment.getSize() + logEntry.serialize().getBytes().length > maxSegmentSize) {
            createNewSegment();
        }
        activeSegment.append(logEntry);
    }

    public void createNewSegment() throws IOException {
        if (activeSegment != null) {
            activeSegment.flush();
            activeSegment.close();
        }
        String fileName = String.format("segment_%05d.log", segmentCounter++);
        Path segmentPath = logDir.resolve(fileName);
        activeSegment = new LogSegment(segmentPath);
        segments.add(activeSegment);
        System.out.println("Created new segment " + activeSegment);
    }

    public void flush() throws IOException {
        if (activeSegment != null) {
            activeSegment.flush();
        }
    }

    public void close() throws IOException {
        if (activeSegment != null) {
            activeSegment.close();
        }
    }

    public List<LogSegment> getSegments() {
        return segments;
    }

    public List<LogSegment> getSegmentsByTimeRange(long startTime, long endTime) {
        List<LogSegment> result = new ArrayList<>();
        for (LogSegment segment : segments) {
            if (segment.getMaxTimestamp() < startTime || segment.getMinTimestamp() > endTime) {
                continue; // skip segments that are completely outside the time range
            }
            result.add(segment);
        }
        return result;
    }

    private void rebuildMetadata(LogSegment segment) throws IOException {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        try (BufferedReader reader = new BufferedReader(new FileReader(segment.getFilePath().toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LogEntry entry = LogEntry.deserialize(line);
                min = Math.min(min, entry.getTimestamp());
                max = Math.max(max, entry.getTimestamp());
            }
        }
        segment.setMetadata(min, max);
        SegmentMetadata metadata = new SegmentMetadata(min, max);
        metadata.save(Path.of(segment.getFilePath().toString() + ".meta"));
    }

    private void loadExistingSegments() throws IOException {
        var files = Files.list(logDir)
                .filter(p -> p.toString().endsWith(".log"))
                .sorted()
                .toList();

        for (Path file: files) {
            LogSegment segment = new LogSegment(file);
            Path metaPath = Path.of(file.toString()+".meta");
            if (Files.exists(metaPath)) {
                SegmentMetadata metadata = SegmentMetadata.load(metaPath);
                segment.setMetadata(metadata.getMinTimestamp(), metadata.getMaxTimestamp());
            } else {
                rebuildMetadata(segment);
            }
            segments.add(segment);
            segmentCounter++;
        }

        if (!segments.isEmpty()) {
            activeSegment = segments.getLast();
        }
    }
}
