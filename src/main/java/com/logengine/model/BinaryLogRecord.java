package com.logengine.model;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BinaryLogRecord {
    private final long timestamp;
    private final byte level;
    private final String message;

    public BinaryLogRecord(long timestamp, byte level, String message) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
    }

    public ByteBuffer serialize() {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int recordSize = Long.BYTES + 1 + messageBytes.length;
        int totalSize = Integer.BYTES + recordSize; // 4 bytes for record size
        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        buffer.putInt(recordSize);
        buffer.putLong(timestamp);
        buffer.put(level);
        buffer.put(messageBytes);
        buffer.flip();
        return buffer;
    }

    public static BinaryLogRecord deserialize(ByteBuffer buffer) {
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
