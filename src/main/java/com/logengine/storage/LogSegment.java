package com.logengine.storage;

import com.logengine.model.LogEntry;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class LogSegment {
    private final Path filePath;
    private final BufferedWriter writer;
    public long size;

    public LogSegment(Path filePath) throws IOException {
        this.filePath = filePath;
        this.writer = new BufferedWriter(new FileWriter(filePath.toFile(), true));
        this.size = filePath.toFile().length();
    }

    public synchronized void append(LogEntry logEntry) throws IOException {
        String serialized = logEntry.serialize();
        writer.write(serialized);
        writer.flush();
        size += serialized.getBytes().length;
    }

    public long getSize() {
        return size;
    }

    public void flush() throws IOException {
        writer.flush();
    }

    public void close() throws IOException {
        writer.close();
    }

    public Path getFilePath() {
        return filePath;
    }
}
