# Log Storage Engine

A lightweight, educational Java project that demonstrates how a **segment-based log storage engine** works.

It writes `LogEntry` records to append-only segment files, rolls over to a new segment when size limits are reached, and reads entries back from disk.

## What this project does

- Appends log entries in a simple text format.
- Splits data across segment files (for example: `segment_00000.log`, `segment_00001.log`).
- Flushes data to disk and supports reading all entries from each segment.
- Provides a small runnable demo in `src/main/java/com/logengine/Main.java`.

## Project structure

- `src/main/java/com/logengine/model/LogEntry.java` - log record model + serialize/deserialize.
- `src/main/java/com/logengine/storage/LogSegment.java` - low-level append and file handling for one segment.
- `src/main/java/com/logengine/storage/SegmentManager.java` - segment lifecycle and rollover logic.
- `src/main/java/com/logengine/reader/SegmentReader.java` - reads entries back from segment files.
- `src/main/java/com/logengine/Main.java` - demo runner.
- `logs/` - generated segment files.

## Prerequisites

- Java (project is currently configured with compiler source/target `25` in `pom.xml`)
- Maven 3.9+

## Build and run

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass=com.logengine.Main
```

If `exec:java` is not available in your setup, run the class from your IDE (JetBrains) using `com.logengine.Main`.

## Data format

Each log line is currently stored as:

```text
timestamp|logLevel|message
```

Example:

```text
1709550000000|INFO|Log message number 1
```

## How it works (quick flow)

1. `Main` creates a `SegmentManager` with a max segment size.
2. `SegmentManager.append(...)` writes to the active `LogSegment`.
3. When a segment reaches size limit, `SegmentManager` creates a new segment file.
4. `SegmentReader.readAll(...)` parses each line back into `LogEntry` objects.

## Current limitations

- Durability and crash safety are basic (no explicit fsync strategy).
- Serialization format is intentionally simple and not schema/version aware.
- No compaction, filtering, querying, archival, or replication yet.

## Roadmap ideas

Based on current TODO notes:

- Improve durability and crash recovery.
- Evolve `LogEntry` representation and serialization format.
- Add compaction/rotation and log-level filtering.
- Add querying/search, archival/backup, and replication features.

---

This repository is a good starting point for learning storage engine fundamentals before moving to production-grade features.
