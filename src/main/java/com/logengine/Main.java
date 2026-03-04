package com.logengine;

import com.logengine.model.LogEntry;
import com.logengine.reader.SegmentReader;
import com.logengine.storage.SegmentManager;

import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        SegmentManager manager = new SegmentManager(Path.of("logs"), 200); // 1 MB segment size
        for (int i = 0; i < 10; i++) {
            manager.append(
                    new LogEntry(
                            System.currentTimeMillis(),
                            "INFO",
                            "Log message number " + i
                    )
            );
        }
        manager.flush();
        System.out.println("\nReading logs:\n");

        for (var segment : manager.getSegments()) {
            List<LogEntry> entries =
                    SegmentReader.readAll(segment.getFilePath());

            entries.forEach(e ->
                    System.out.println(e.getTimestamp() + " " + e.getLogLevel() + " " + e.getMessage())
            );
        }

        long start = System.currentTimeMillis() - 1000; // last 1 second
        long end = System.currentTimeMillis();

        var segments = manager.getSegmentsByTimeRange(start, end);
        for (var segment : segments) {
            var entries = SegmentReader.readByTimeRange(
                    segment.getFilePath(),
                    start,
                    end
            );

            entries.forEach(System.out::println);
        }
    }
}
