package bgu.spl.net;

import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class LOGIN implements Command <User> {
    private Database DB = Database.getInstance();
    private String userName;
    private String password;
    private short opCode;
    private User user;

    public LOGIN(String userName, String password, short opCode){
        this.password = password;
        this.userName  = userName;
        this.opCode = opCode;
    }

    public Serializable execute(User arg) {
        if(arg == null) {
            Admin a = DB.fitUserNameToAdmin(userName);
            Student s = DB.fitUserNameToStudent(userName);
            if (s != null) {
                if (DB.studentLogIn(s, password)) {
                    user = s;
                    return new ACK(opCode, null);
                }
            } else if (a != null) {
                if (DB.adminLogIn(a, password)) {
                    user = a;
                    return new ACK(opCode, null);
                }
            }
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

    public User getUser() { return user; }
}
