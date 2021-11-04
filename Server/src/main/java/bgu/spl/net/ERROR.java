package bgu.spl.net;

import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class ERROR implements Command <User> {
    private short OP;
    public ERROR (short OP){this.OP = OP;}
    @Override
    public Serializable execute(User arg) {
        return null;
    }
    @Override
    public short getOpCode() {
        return OP;
    }
    @Override
    public String getOptional() {
        return null;
    }
}
