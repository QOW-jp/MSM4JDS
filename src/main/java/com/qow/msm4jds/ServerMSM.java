package com.qow.msm4jds;

import com.qow.jmux.Command;
import com.qow.jmux.JMUX;

import java.io.IOException;

public class ServerMSM extends JMUX{
    public ServerMSM(int port, byte[] protocolID) throws IOException {
        super(port,protocolID);
    }

    public ServerMSM(int port, byte[] protocolID, String clientIp) throws IOException {
        super(port,protocolID,clientIp);
    }

    public void start(String msm4jPath) {
        int tokenID = 1;
        addToken(new MSMToken(tokenID, msm4jPath));
        System.out.print("start JMUX : ");
        System.out.println(enable());
        System.out.print("ENABLE : ");
        System.out.println(command(Command.ENABLE, tokenID));
        waitForServer();
        System.exit(3);
    }
}