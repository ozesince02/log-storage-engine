package com.logengine.storage;

import com.logengine.model.LogEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WriteBuffer {
    private final ArrayBlockingQueue<LogEntry> queue;
    private final SegmentManager segmentManager;
    private final int batchSize;
    private final int flushIntervalMs;

    public WriteBuffer(int capacity, SegmentManager segmentManager, int batchSize, int flushIntervalMs) {
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.segmentManager = segmentManager;
        this.batchSize = batchSize;
        this.flushIntervalMs = flushIntervalMs;
        startBackgroundWriter();
    }

    public void append(LogEntry logEntry) throws InterruptedException {
        queue.put(logEntry); // blocks if the queue is full
    }

    private void startBackgroundWriter() {
        Thread writerThread = new Thread(() -> {
            List<LogEntry> batch = new ArrayList<>();
            long lastFlush = System.currentTimeMillis();
            while (true) {
                try {
                    LogEntry entry = queue.poll(50, TimeUnit.MILLISECONDS); // non-blocking poll
                    if (entry != null) {
                       batch.add(entry);
                    }
                    long now = System.currentTimeMillis();
                    if (batch.size() >= batchSize || now - lastFlush >= flushIntervalMs) {
                        flush(batch);
                        batch.clear();
                        lastFlush = now;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        writerThread.setDaemon(true);
        writerThread.start();
    }

    private void flush(List<LogEntry> batch) throws IOException {
        for (LogEntry entry : batch) {
            segmentManager.append(entry);
        }
        segmentManager.flush();
    }
}
