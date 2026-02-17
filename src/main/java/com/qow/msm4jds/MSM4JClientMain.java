package com.qow.msms4j;

import com.qow.minecraft.server.CommandControllerClient;
import com.qow.minecraft.server.MSM4JProperty;
import com.qow.minecraft.server.MinecraftServerManager4J;
import com.qow.net.ClosedServerException;
import com.qow.net.UntrustedConnectException;
import com.qow.util.UntrustedPropertyException;
import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MSM4JClientMain {
    public static void main(String[] args) {
        System.out.println("MSM4J");
        int length = 2;
        if (args.length != length) {
            System.err.println("args.length is not " + length);
            System.err.println("[msm4j.qon] [message]");
            System.exit(2);
        }

        String path = args[0];
        String message = args[1];

        MSM4JProperty msm4JProperty;
        try {
            msm4JProperty = new MSM4JProperty(new QONObject(new File(path)));
            msm4JProperty.parse();
        } catch (UntrustedQONException | NoSuchKeyException | IOException | UntrustedPropertyException e) {
            System.err.println("Not Available MSM4JProperty.");
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }

        String host = msm4JProperty.get("control_server-ip");
        byte[] protocolID = msm4JProperty.get("control_protocol-id").getBytes(StandardCharsets.UTF_8);
        int byteSize = Integer.parseInt(msm4JProperty.get("control_byte-size"));

        int msm4jPort = PortGetter.get("control_", msm4JProperty);
        if (msm4jPort == 0) throw new RuntimeException();

        CommandControllerClient ccc = new CommandControllerClient(host, msm4jPort, protocolID, byteSize);

        try {
            System.out.print(message + " : ");
            boolean success = ccc.command(message);
            System.out.println(success);
        } catch (ClosedServerException e) {
            if (message.equals("BACKUP")) {
                try {
                    QONObject qonObject = new QONObject(new File(path));
                    MSM4JProperty property = new MSM4JProperty(qonObject);
                    BackupProperty backupProperty = new BackupProperty(qonObject.getQONObject("backup"));

                    property.parse();
                    backupProperty.parse();

                    PrivateRule rule = new PrivateRule(backupProperty);
                    MinecraftServerManager4J msManager = new MinecraftServerManager4J(property, rule);

                    System.out.println("backup now");
                    rule.backup();
                    System.out.println("success");
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    System.exit(5);
                }
            } else {
                throw new RuntimeException(e);
            }
        } catch (UntrustedConnectException e) {
            throw new RuntimeException(e);
        }
    }
}
