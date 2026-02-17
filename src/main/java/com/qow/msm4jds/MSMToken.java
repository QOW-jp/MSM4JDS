package com.qow.msm4jds;

import com.qow.jmux.Token;
import com.qow.minecraft.server.*;
import com.qow.util.UntrustedPropertyException;
import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

public class MSMToken extends Token {
    private final String qonPath;
    private MinecraftServerManager4J msManager;

    public MSMToken(int tokenID, String qonPath) {
        super(tokenID);
        this.qonPath = qonPath;
    }

    @Override
    public void start() {
        MSM4JProperty msm4JProperty;
        BackupProperty backupProperty;

        try {
            System.out.print("Loading MSM4J QON config file : ");
            QONObject qonObject = new QONObject(new File(qonPath));
            System.out.println("Succeeded");

            System.out.print("Loading MSM4J Property : ");
            msm4JProperty = new MSM4JProperty(qonObject);
            msm4JProperty.parse();
            System.out.println("Succeeded");

            System.out.print("Loading Backup Property : ");
            backupProperty = new BackupProperty(qonObject.getQONObject("backup"));
            backupProperty.parse();
            System.out.println("Succeeded");
        } catch (NoSuchKeyException | UntrustedPropertyException | UntrustedQONException | IOException e) {
            System.out.println("Failed");
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            CommandRule rule = new PrivateRule(backupProperty);

            msManager = new MinecraftServerManager4J(msm4JProperty, rule);
            Runtime.getRuntime().addShutdownHook(new Thread(msManager::killProcess));

            CommandControllerServer ccs = msManager.getCommandControllerServer();

            System.out.println("Launch MSM4J : " + msManager.start());
            System.out.println("Launch CommandControllerServer : " + ccs.start());
        } catch (MinecraftEditionException | DisabledException | IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        System.out.println("Closing MSM4J");
        try {
            msManager.command("stop");
            int exitCode = msManager.waitFor();
            msManager.getCommandControllerServer().stop();
            System.out.println("Server exit code = " + exitCode);
        } catch (InterruptedException | IOException | DisabledException ignored) {
        }
    }
}