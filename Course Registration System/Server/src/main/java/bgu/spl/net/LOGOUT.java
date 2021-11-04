package bgu.spl.net;

import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class LOGOUT implements Command<User> {
    private short opCode;
    private Database DB = Database.getInstance();

    public  LOGOUT (short opCode){ this.opCode = opCode;}
    @Override
    public Serializable execute(User arg) {
        if(arg != null) {
            DB.logout(arg);
            return new ACK(opCode, null);
        }
        return new ERROR(opCode);
    }
    @Override
    public short getOpCode() {
        return opCode;
    }
    @Override
    public String getOptional() {
        return null;
    }
}
