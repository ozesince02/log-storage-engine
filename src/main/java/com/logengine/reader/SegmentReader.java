package com.logengine.reader;

import com.logengine.model.LogEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for reading log entries from segment files.
 * Provides full read and time-filtered read capabilities.
 */
public class SegmentReader {
    /**
     * Reads all log entries from a specific file.
     */
    public static List<LogEntry> readAll(Path filepath) throws IOException {
        List<LogEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                entries.add(LogEntry.deserialize(line));
            }
        }
        return entries;
    }

    /**
     * Reads log entries that fall within the given timestamp range.
     * Optimization: Terminates reading once the entry timestamp exceeds endTime,
     * assuming log entries in the file are chronologically sorted.
     */
    public static List<LogEntry> readByTimeRange(Path filepath, long startTime, long endTime) throws IOException {
        List<LogEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LogEntry entry = LogEntry.deserialize(line);
                if (entry.getTimestamp() >= startTime && entry.getTimestamp() <= endTime) {
                    entries.add(entry);
                }

                // stop early if the logs exceed the time range
                if (entry.getTimestamp() > endTime) {
                    break;
                }
            }
        }
        return entries;
    }
}
