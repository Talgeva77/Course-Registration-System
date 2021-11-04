package bgu.spl.net;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class BGRSProtocol implements MessagingProtocol <Command> {
    private boolean shouldTerminate  = false;
    User user;

    public BGRSProtocol () { user = null; }

    public Command process(Command msg){
        Serializable output = msg.execute(user);
        if (output instanceof ACK) {
            if (msg.getOpCode() == 4)
                user = null;
            if (msg.getOpCode() == 3)
                user = ((LOGIN) msg).getUser();
        }
        return (Command) output;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
