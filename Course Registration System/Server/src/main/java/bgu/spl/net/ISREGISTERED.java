package bgu.spl.net;

import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class ISREGISTERED implements Command<User> {
    private Database DB = Database.getInstance();
    private int courseNum;
    private short opCode;

    public ISREGISTERED(int courseNum, short opCode) {
        this.courseNum = courseNum;
        this.opCode = opCode;
    }

    @Override
    public Serializable execute(User arg) {
        if (arg != null) {
            Student s = DB.fitUserNameToStudent(arg.getUsername());
            if (DB.isRegister(s, DB.fitCourseNumToCourse(courseNum)))
                return new ACK(opCode, "REGISTERED");
            else
                return new ACK(opCode, "NOT REGISTERED");
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
