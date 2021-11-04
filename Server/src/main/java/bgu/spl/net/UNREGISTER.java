package bgu.spl.net;
import bgu.spl.net.impl.rci.Command;
import java.io.Serializable;

public class UNREGISTER implements Command<User> {
    private Database DB = Database.getInstance();
    private int courseNum;
    private short opCode;


    public UNREGISTER (int courseNum,short opCode){
        this.courseNum = courseNum;
        this.opCode = opCode;
    }

    @Override
    public Serializable execute(User arg) {
        if (arg != null) {
            Student s = DB.fitUserNameToStudent(arg.getUsername());
            Course c = DB.fitCourseNumToCourse(courseNum);
            if (s != null && DB.unregisterToCourse(s, c))
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

