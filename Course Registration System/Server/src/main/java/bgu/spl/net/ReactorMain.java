package bgu.spl.net;

import bgu.spl.net.srv.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ReactorMain {
    public static void main(String[] args) throws UnknownHostException{
        Database.getInstance().initialize("Courses.txt");
        int port = Integer.parseInt(args[0]);
        int numOfThreads = Integer.parseInt(args[1]);
        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println("System IP Address : " + localhost.getHostAddress());
        Server.reactor(numOfThreads, port, () -> new BGRSProtocol(), () -> new OperationEncoderDecoder()).serve();
    }
}
