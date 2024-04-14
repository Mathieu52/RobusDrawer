package com.innov8.robusdrawer.file;

public enum FileTransferFailureReason {
    // Indicates that the communication partner (e.g., another device) failed to respond within the expected time frame.
    TIMEOUT_WHILE_WAITING_FOR_RESPONSE,

    // Indicates that the file transfer was unsuccessful due to a high rate of packet loss during transmission.
    HIGH_PACKET_LOSS_RATE,

    // Both event occurred at the same time (Really unlikely to happen)
    TIMEOUT_WHILE_HIGH_PACKET_LOSS_RATE,

    // Indicates that the reason for the file transfer failure is unknown or unspecified.
    UNKNOWN_REASON,
}