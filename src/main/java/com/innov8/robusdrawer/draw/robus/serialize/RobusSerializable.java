package com.innov8.robusdrawer.draw.robus.serialize;

import com.innov8.robusdrawer.exception.DeserializationFailedException;

import java.lang.reflect.Field;

public interface RobusSerializable {
    String serialize();
    void deserialize(String value) throws DeserializationFailedException;
}
