package bgu.spl.net;
import bgu.spl.net.impl.rci.Command;
import java.io.Serializable;

public class COURSESTAT implements Command<User> {
    private Database DB = Database.getInstance();
    private int courseNum;
    private short opCode;

    public COURSESTAT(int courseNum, short optCode) {
        this.courseNum = courseNum;
        this.opCode = optCode;
    }

    @Override
    public Serializable execute(User arg) {
            if (arg != null) {
                Admin a = DB.fitUserNameToAdmin(arg.getUsername());
                Course c = DB.fitCourseNumToCourse(courseNum);
                if (c != null & a != null)
                    return new ACK(opCode, DB.printCourseStatus(c));
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

