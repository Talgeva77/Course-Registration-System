package bgu.spl.net;

import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class ACK implements Command <User> {
    private short OP;
    private String Optional;
    public ACK (short OP , String Optional){
        this.OP = OP;
        this.Optional = Optional;
    }
    @Override
    public Serializable execute(User arg) {
        return null;
    }

    public short getOpCode() {
        return OP;
    }

    @Override
    public String getOptional() {
        return Optional;
    }
}
