package edu.tuc.bsvs.rockpaperscissors.server;


import edu.tuc.bsvs.rockpaperscissors.helper.Identifier;

import java.io.Serializable;

/**
 * Created by Smadback on 22.05.2016.
 */
public class Packet implements Serializable {

    private Identifier identifier = null;
    private Object value = null;
    private int senderId = 0;

    public Packet(Identifier identifier, Object value) {
        this.identifier = identifier;
        this.value = value;
    }

    public Packet(Identifier identifier, Object value, int senderId) {
        this.identifier = identifier;
        this.value = value;
        this.senderId = senderId;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Object getValue() {
        return value;
    }

    public int getSenderId() {
        return senderId;
    }
}
