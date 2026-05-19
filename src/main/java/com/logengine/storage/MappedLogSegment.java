package com.logengine.storage;

import com.logengine.model.BinaryLogRecord;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * Represents a segment of log data mapped into memory for efficient I/O operations.
 * Uses Memory Mapped Files (mmap) to provide high-performance reading and writing.
 */
public class MappedLogSegment {
    private final Path filePath;
    private final int capacity;
    private final MappedByteBuffer mappedBuffer;
    private final FileChannel channel;
    private int writeOffset = 0;

    public MappedLogSegment(Path filePath, int capacity) throws IOException {
        this.filePath = filePath;
        this.capacity = capacity;
        RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "rw");
        file.setLength(capacity);
        this.channel = file.getChannel();
        this.mappedBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, capacity);
    }

    /**
     * Appends a log record to the end of this segment.
     *
     * @param record The log record to append.
     * @return true if the record was appended, false if there is insufficient capacity.
     * @throws IOException If an I/O error occurs during serialization or writing.
     */
    public synchronized boolean append(BinaryLogRecord record) throws IOException {
        ByteBuffer recordBuffer = record.serialize();
        int recordSize = recordBuffer.remaining();
        if (writeOffset + recordSize > capacity) {
            return false;
        }
        mappedBuffer.position(writeOffset);
        mappedBuffer.put(recordBuffer);
        writeOffset+=recordSize;
        return true;
    }

    /**
     * Persists any changes in the memory-mapped buffer to the underlying storage device.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void flush() throws IOException {
        mappedBuffer.force();
    }

    public int getWriteOffset() {
        return writeOffset;
    }

    public int getCapacity() {
        return capacity;
    }

    public Path getFilePath() {
        return filePath;
    }

    /**
     * Flushes any pending changes and closes the underlying file channel.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void close() throws IOException {
        flush();
        channel.close();
    }
}
