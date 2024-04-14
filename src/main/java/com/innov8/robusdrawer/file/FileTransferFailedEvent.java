package com.innov8.robusdrawer.file;

import javafx.event.Event;
import javafx.event.EventType;

// Define a custom event
public class FileTransferFailedEvent extends Event {
    private final FileTransferFailureReason reason;

    public FileTransferFailedEvent(FileTransferFailureReason reason) {
        super(EventType.ROOT);
        this.reason = reason;
    }

    public FileTransferFailureReason getReason() {
        return reason;
    }
}