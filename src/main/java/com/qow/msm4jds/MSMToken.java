package com.qow.msm4jds;

import com.qow.jmux.Token;
import com.qow.minecraft.server.*;
import com.qow.util.qon.QONObject;

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
        try {
            QONObject qonObject = new QONObject(new File(qonPath));
            MSM4JProperty property = new MSM4JProperty(qonObject);
            BackupProperty backupProperty = new BackupProperty(qonObject.getQONObject("backup"));

            property.parse();
            backupProperty.parse();

            CommandRule rule = new PrivateRule(backupProperty);
            msManager = new MinecraftServerManager4J(property, rule);
            Runtime.getRuntime().addShutdownHook(new Thread(msManager::killProcess));

            CommandControllerServer ccs = msManager.getCommandControllerServer();

            System.out.println("start MSM4J : " + msManager.start());
            System.out.println("start CommandControllerServer : " + ccs.start());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(5);
        }
    }

    @Override
    public void stop() {
        System.out.println("stop MSM4J");
        try {
            msManager.command("stop");
            int exitCode = msManager.waitFor();
            msManager.getCommandControllerServer().stop();
            System.out.println("server exit code " + exitCode);
        } catch (IOException | InterruptedException | DisabledException ignored) {
        }
    }
}