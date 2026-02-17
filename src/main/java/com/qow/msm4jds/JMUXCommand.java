package com.qow.msm4jds;

import com.qow.jmux.Command;
import com.qow.jmux.JMUXClient;
import com.qow.minecraft.server.CommandControllerClient;
import com.qow.minecraft.server.MSM4JProperty;
import com.qow.net.ClosedServerException;
import com.qow.net.UntrustedConnectException;
import com.qow.util.UntrustedPropertyException;
import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static com.qow.jmux.Command.values;

public class JMUXCommand {
    public static void main(String[] args) {
        System.out.println("JMUXCommand");
        int length = 4;
        if (args.length != length) {
            System.err.println("[jmux.qon] [msm4j.qon] [command] [tokenID]");
            System.exit(2);
        }
        String jmuxPath = args[0];
        String msm4jPath = args[1];
        Command command = Command.valueOf(args[2]);
        int tokenID = Integer.parseInt(args[3]);

        System.out.println("JMUX Commands = " + Arrays.toString(values()));

        MSM4JProperty msm4JProperty;
        JMUXProperty jmuxProperty;

        try {
            System.out.print("Loading MSM4J Property : ");
            msm4JProperty = new MSM4JProperty(new QONObject(new File(msm4jPath)));
            msm4JProperty.parse();
            System.out.println("Succeeded");
        } catch (IOException | UntrustedQONException | NoSuchKeyException | UntrustedPropertyException e) {
            System.out.println("Failed");
            System.err.println("Not Available MSM4JProperty.");
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
        try {
            System.out.print("Loading JMUX Property : ");
            jmuxProperty = new JMUXProperty(new QONObject(new File(jmuxPath)));
            jmuxProperty.parse();
            System.out.println("Succeeded");
        } catch (IOException | UntrustedQONException | NoSuchKeyException | UntrustedPropertyException e) {
            System.out.println("Failed");
            System.err.println("Not Available JMUX Property.");
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }

        String host = jmuxProperty.get("server-ip");
        byte[] protocolID4jmux = jmuxProperty.get("protocol-id").getBytes(StandardCharsets.UTF_8);

        int jmuxPort = PortGetter.get(jmuxProperty);
        boolean jmuxAutoPorting = Boolean.parseBoolean(jmuxProperty.get("auto-porting"));

        System.out.println("Starting JMUX Client");
        JMUXClient jmuxClient = new JMUXClient(host, jmuxPort, protocolID4jmux);

        try {
            if (jmuxAutoPorting && jmuxPort == 0) {
                throw new ClosedServerException("Server is not activated.");
            }
            System.out.print("JMUX send [" + command + "] : ");
            boolean sendable = jmuxClient.send(command, tokenID);   //ClosedServerException UntrustedConnectException
            System.out.println(sendable);
            if (!sendable) {
                int msm4jPort = PortGetter.get("control_", msm4JProperty);

                byte[] protocolID4msm4j = msm4JProperty.get("control_protocol-id").getBytes(StandardCharsets.UTF_8);
                int byteSize = Integer.parseInt(msm4JProperty.get("control_byte-size"));
                CommandControllerClient ccc = new CommandControllerClient(host, msm4jPort, protocolID4msm4j, byteSize);

                if (msm4jPort == 0) throw new ClosedServerException("no server.");
                System.out.print("CommandControllerClient send [START]: ");
                System.out.println(ccc.command("START"));   //ClosedServerException UntrustedConnectException
            }
        } catch (UntrustedConnectException e) {
            System.err.println(e.getMessage());
            System.err.println("send protocol id. at: " + new String(e.getSendProtocolID(), StandardCharsets.UTF_8));
            System.err.println("receive protocol id. at: " + new String(e.getReceiveProtocolID(), StandardCharsets.UTF_8));
        } catch (ClosedServerException e) {
            System.err.println("Server not open");
            if (command == Command.ENABLE) {
                System.out.println("Starting JMUX Server");
                boolean bindIp = Boolean.parseBoolean(jmuxProperty.get("bind-ip"));
                if (jmuxAutoPorting) {
                    jmuxPort = 0;
                }
                System.out.println("JMUX auto porting : " + jmuxAutoPorting);
                System.out.println("JMUX bind IP : " + bindIp);

                ServerMSM sm;
                try {
                    if (bindIp) {
                        String clientIp = jmuxProperty.get("client-ip");
                        sm = new ServerMSM(jmuxPort, protocolID4jmux, clientIp);
                    } else {
                        sm = new ServerMSM(jmuxPort, protocolID4jmux);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                try (sm) {
                    if (jmuxAutoPorting) {
                        int activatedPort = sm.getLocalPort();
                        System.out.println("JMUX port : " + activatedPort);
                        File temp = new File(jmuxProperty.get("port-temp"));
                        Path parent = Path.of(temp.getParent());
                        System.out.print("Create port-temp file : ");
                        Files.createDirectories(parent);
                        try (FileWriter fw = new FileWriter(temp)) {
                            try (PrintWriter pw = new PrintWriter(new BufferedWriter(fw))) {
                                pw.println(activatedPort);
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        System.out.println("Succeeded");
                    }
                    System.out.println("Open JMUX Server");
                    sm.start(msm4jPath);
                    sm.waitForServer();
                    System.out.println("Closed JMUX Server");
                } catch (IOException ex) {
                    System.out.println("Failed");
                    throw new RuntimeException(ex);
                }
            }
        } finally {
            System.out.println("exit JMUXCommand");
        }
    }
}