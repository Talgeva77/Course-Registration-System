package bgu.spl.net;

import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class MYCOURSES implements Command<User> {
    private Database DB = Database.getInstance();
    private short opCode;
    public MYCOURSES (short opCode){
        this.opCode = opCode;
    }
    @Override
    public Serializable execute(User arg) {
        if (arg != null) {
            Student s = DB.fitUserNameToStudent(arg.getUsername());
            if (s != null)
                return new ACK(opCode, DB.myCourses(arg.getUsername()));
            else
                return new ERROR(opCode);
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
