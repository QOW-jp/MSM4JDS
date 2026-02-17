package com.qow.msm4jds;

import com.qow.minecraft.server.CommandRule;
import com.qow.minecraft.server.ProcessManager;
import com.qow.util.Webhook;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrivateRule extends CommandRule {
    BackupProperty backup;

    public PrivateRule(BackupProperty backup) {
        super();

        this.backup = backup;
    }

    public synchronized void backup() throws IOException {
        if (!Boolean.parseBoolean(backup.get("enable"))) {
            System.err.println("backupable is false");
            return;
        }

        ProcessManager pm = getProcessManager();

        boolean noPlayer = getPlayerList().isEmpty();
        boolean wasEnableServer = pm.getServerStatus();
        if (wasEnableServer) {
            pm.requestStopServer(!noPlayer ? Integer.parseInt(backup.get("delay")) : 0, backup.get("comment"));
            try {
                pm.waitForProcess();
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }

        //Webhook
        boolean webhook = Boolean.parseBoolean(backup.get("webhook_enable"));
        if (webhook) {
            boolean start = Boolean.parseBoolean(backup.get("webhook_start"));
            if (start) {
                SimpleDateFormat sdf4wh = new SimpleDateFormat(backup.get("webhook_time-format"));
                new Webhook(backup.get("webhook_webhook-url"), "BACKUP NOW " + sdf4wh.format(new Date()), Color.YELLOW);
            }
        }

        String targetPath = backup.get("backup-files-path");
        String[] backupFilePaths = targetPath.substring(1, targetPath.length() - 1).split(",\\s*");

        SimpleDateFormat sdf = new SimpleDateFormat(backup.get("time-format"));
        String archivedFileName = backup.get("directory") + "/" + backup.get("title") + "_" + sdf.format(new Date()) + ".tar.zst";

        //親ディレクトリ作成
        Path archivedFilePath = Paths.get(archivedFileName);
        Path p = archivedFilePath.getParent();
        Files.createDirectories(p);

        //ファイル圧縮
        try {
            List<Path> targets = new ArrayList<>();
            for (String path : backupFilePaths) {
                targets.add(Path.of(path));
            }
            new Backup(targets, archivedFilePath);
        } catch (IOException e) {
            System.err.println("failed to archive.");
            System.out.println(e.getMessage());
        }

        //Webhook
        if (webhook) {
            boolean finish = Boolean.parseBoolean(backup.get("webhook_finish"));
            if (finish) {
                SimpleDateFormat sdf4wh = new SimpleDateFormat(backup.get("webhook_time-format"));
                new Webhook(backup.get("webhook_webhook-url"), "FINISHED BACKUP " + sdf4wh.format(new Date()), Color.YELLOW);
            }
        }

        if (wasEnableServer && !noPlayer) {
            pm.start();
        } else {
            System.exit(2);
        }
    }

    @Override
    public void listeningCommandLine(String line) {

    }

    @Override
    public boolean listeningCommandServer(String line) {
        return switch (line) {
            case "BACKUP" -> {
                try {
                    backup();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
                yield false;
            }
            default -> true;
        };
    }
}