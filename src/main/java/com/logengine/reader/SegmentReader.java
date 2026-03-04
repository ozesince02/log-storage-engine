package com.logengine.reader;

import com.logengine.model.LogEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SegmentReader {
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
