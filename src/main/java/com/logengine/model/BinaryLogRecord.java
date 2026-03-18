package com.logengine.model;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Represents a log record in binary format for efficient storage and transmission.
 * Uses ByteBuffer for serialization/deserialization.
 */
public class BinaryLogRecord {
    private final long timestamp;
    private final byte level;
    private final String message;

    public BinaryLogRecord(long timestamp, byte level, String message) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
    }

    /**
     * Serializes the record into a ByteBuffer.
     * The layout is: [4 bytes record size] [8 bytes timestamp] [1 byte level] [message bytes]
     * @return A ByteBuffer ready for reading (already flipped).
     */
    public ByteBuffer serialize() {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int recordSize = Long.BYTES + 1 + messageBytes.length;
        int totalSize = Integer.BYTES + recordSize; // 4 bytes for record size

        // ByteBuffer.allocate() creates a buffer in the JVM heap.
        ByteBuffer buffer = ByteBuffer.allocate(totalSize);

        // Methods like putInt, putLong, put write data and advance the 'position' index.
        buffer.putInt(recordSize);
        buffer.putLong(timestamp);
        buffer.put(level);
        buffer.put(messageBytes);

        // flip() is crucial: it sets the limit to current position and resets position to 0,
        // making the buffer ready to be read from the start.
        buffer.flip();
        return buffer;
    }

    /**
     * Reconstructs a BinaryLogRecord from its serialized ByteBuffer form.
     */
    public static BinaryLogRecord deserialize(ByteBuffer buffer) {
        // getInt(), getLong(), etc., read data from the current position and advance it.
        int recordSize = buffer.getInt();
        long timestamp = buffer.getLong();
        byte level = buffer.get();
        byte[] messageBytes = new byte[recordSize - Long.BYTES - 1];
        buffer.get(messageBytes);
        String message = new String(messageBytes, StandardCharsets.UTF_8);
        return new BinaryLogRecord(timestamp, level, message);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }
}
