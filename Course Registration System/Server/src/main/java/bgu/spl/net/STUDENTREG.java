package bgu.spl.net;
import bgu.spl.net.impl.rci.Command;


import java.io.Serializable;

public class STUDENTREG implements Command<User> {
    private Database DB = Database.getInstance();
    private short opCode;
    private String userName;
    private String password;


    public STUDENTREG(String userName, short opCode, String password) {
        this.opCode = opCode;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Serializable execute(User arg) {
        if (arg == null) {
            Admin a = DB.fitUserNameToAdmin(userName);
            Student s = new Student(userName, password);
            if (a == null & DB.studentRegisterToSystem(s))
                return new ACK(opCode, null);
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
