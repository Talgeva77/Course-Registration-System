package bgu.spl.net;
import bgu.spl.net.impl.rci.Command;
import java.io.Serializable;

public class STUDENTSTAT implements Command<User> {
    private Database DB = Database.getInstance();
    private String username;
    private short opCode;

    public STUDENTSTAT (String username, short optCode) {
        this.username = username;
        this.opCode = optCode;
    }

    @Override
    public Serializable execute(User arg) {
        if (arg != null) {
            Admin a = DB.fitUserNameToAdmin(arg.getUsername());
            Student s = DB.fitUserNameToStudent(username);
            if (s != null & a != null)
                return new ACK(opCode, DB.printStudentStatus(s));
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

