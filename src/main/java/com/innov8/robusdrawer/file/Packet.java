package com.innov8.robusdrawer.file;

import static com.innov8.robusdrawer.file.PacketState.NONE;

public class Packet {
    private String message;
    private PacketState state;

    public Packet(String message, PacketState state) {
        this.message = message;
        this.state = state;
    }

    public Packet(String message) {
        this(message, NONE);
    }

    public int getSize() {
        return message.length();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PacketState getState() {
        return state;
    }

    public void setState(PacketState state) {
        this.state = state;
    }
}
