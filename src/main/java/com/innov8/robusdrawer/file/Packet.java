package com.innov8.robusdrawer.file;

import static com.innov8.robusdrawer.file.PacketState.NONE;

public class Packet {
    private String message;
    private PacketState state;

    private long time;

    public Packet(String message, PacketState state, long time) {
        setPacket(message, state, time);
    }
    public Packet(String message, PacketState state) {
        setPacket(message, state, System.currentTimeMillis());
    }

    public void setPacket(String message, PacketState state, long time) {
        this.message = message;
        this.state = state;
        this.time = time;
    }

    public void setPacket(String message, PacketState state) {
        setPacket(message, state, System.currentTimeMillis());
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
