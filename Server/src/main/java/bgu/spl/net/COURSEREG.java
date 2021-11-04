package bgu.spl.net;
import bgu.spl.net.impl.rci.Command;
import java.io.Serializable;

public class COURSEREG implements Command<User> {
    private Database DB = Database.getInstance();
    private int courseNum;
    private short opCode;


    public COURSEREG(int courseNum, short optCode) {
        this.courseNum = courseNum;
        this.opCode = optCode;
    }

    @Override
    public Serializable execute(User arg) {
        if (arg != null) {
            Student s = DB.fitUserNameToStudent(arg.getUsername());
            Course c = DB.fitCourseNumToCourse(courseNum);
            if (DB.courseExists(c) && DB.availableSeats(c)) {
                if (s != null && DB.didKdam(s, c) && s.logIn) {
                    DB.registerToCourse(s, c);
                    return new ACK(opCode, null);
                }
            }
        }
    return new ERROR(opCode);
}

    public short getOpCode() {
        return opCode;
    }

    @Override
    public String getOptional() {
        return null;
    }
}
