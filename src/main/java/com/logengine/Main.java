package com.logengine;

import com.logengine.model.BinaryLogRecord;
import com.logengine.model.LogEntry;
import com.logengine.reader.SegmentReader;
import com.logengine.storage.SegmentManager;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.List;

/**
 * Entry point for the Log Storage Engine demonstration.
 * Shows usage of individual log record serialization and segment management.
 */
public class Main {
    public static void main(String[] args) throws Exception {
//        SegmentManager manager = new SegmentManager(Path.of("logs"), 200); // 1 MB segment size
//        for (int i = 0; i < 10; i++) {
//            manager.append(
//                    new LogEntry(
//                            System.currentTimeMillis(),
//                            "INFO",
//                            "Log message number " + i
//                    )
//            );
//        }
//        manager.flush();
//        System.out.println("\nReading logs:\n");
//
//        for (var segment : manager.getSegments()) {
//            List<LogEntry> entries =
//                    SegmentReader.readAll(segment.getFilePath());
//
//            entries.forEach(e ->
//                    System.out.println(e.getTimestamp() + " " + e.getLogLevel() + " " + e.getMessage())
//            );
//        }
//
//        long start = System.currentTimeMillis() - 1000; // last 1 second
//        long end = System.currentTimeMillis();
//
//        var segments = manager.getSegmentsByTimeRange(start, end);
//        for (var segment : segments) {
//            var entries = SegmentReader.readByTimeRange(
//                    segment.getFilePath(),
//                    start,
//                    end
//            );
//
//            entries.forEach(System.out::println);
//        }

        // Demonstrate binary serialization of a single log record
        BinaryLogRecord record =
                new BinaryLogRecord(System.currentTimeMillis(), (byte)3, "DB failed");

        // ByteBuffer is a container for data of a specific primitive type.
        // It provides methods for reading and writing data, and manages a 'position', 'limit', and 'capacity'.
        ByteBuffer buffer = record.serialize();

        BinaryLogRecord decoded =
                BinaryLogRecord.deserialize(buffer);

        System.out.println(decoded.getMessage());
    }
}
