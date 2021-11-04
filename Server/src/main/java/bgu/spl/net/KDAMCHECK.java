package bgu.spl.net;

import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class KDAMCHECK implements Command <User> {
    private Database DB = Database.getInstance();
    private int courseNum;
    private short opCode;

    public KDAMCHECK(int courseNum, short opCode){
        this.opCode = opCode;
        this.courseNum  = courseNum;
    }

    public Serializable execute(User arg) { // we dont use arg here, maybe a problem
        return new ACK(opCode, DB.getKdamByNum(courseNum));
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
