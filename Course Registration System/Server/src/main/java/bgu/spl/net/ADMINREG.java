package bgu.spl.net;

import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class ADMINREG implements Command <User> {
    private Database DB = Database.getInstance();
    private String userName;
    private String password;
    private short opCode;

    public ADMINREG (String userName , String password, short opCode){
        this.userName = userName;
        this.password = password;
        this.opCode = opCode;
    }
    @Override
    public Serializable execute(User arg) {
        if (arg == null) {
            Admin a = DB.fitUserNameToAdmin(userName);
            Student b = DB.fitUserNameToStudent(userName);
            if (a != null | b != null)
                return new ERROR(opCode);
            else {
                DB.addAdmin(new Admin(userName, password));
                return new ACK(opCode, null);
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
}
