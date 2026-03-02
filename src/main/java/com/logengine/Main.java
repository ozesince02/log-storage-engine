package com.logengine;

import com.logengine.model.LogEntry;
import com.logengine.storage.SegmentManager;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        SegmentManager manager = new SegmentManager(Path.of("logs"), 1_000_000); // 1 MB segment size
        for (int i = 0; i < 20; i++) {
            manager.append(
                    new LogEntry(
                            System.currentTimeMillis(),
                            "INFO",
                            "Log message number " + i
                    )
            );
        }
        manager.flush();
    }
}
