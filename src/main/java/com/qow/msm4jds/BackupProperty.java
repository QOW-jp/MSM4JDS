package com.qow.msm4jds;

import com.qow.util.Property;
import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;

import java.util.Arrays;

public class BackupProperty extends Property {
    public BackupProperty(QONObject backup) throws NoSuchKeyException {
        this();
        putMap("enable", backup.get("enable"));
        putMap("title", backup.get("title"));
        putMap("time-format", backup.get("time-format"));
        putMap("directory", backup.get("directory"));
        putMap("delay", backup.get("delay"));
        putMap("comment", backup.get("comment"));
        putMap("backup-files-path", Arrays.toString(backup.getQONArray("backup-files-path").list()));

        QONObject webhook = backup.getQONObject("webhook");
        putMap("webhook_enable", webhook.get("enable"));
        putMap("webhook_webhook-url", webhook.get("webhook-url"));
        putMap("webhook_start", webhook.get("start"));
        putMap("webhook_finish", webhook.get("finish"));
        putMap("webhook_time-format", webhook.get("time-format"));
    }

    public BackupProperty() {
        super();
        addTargetKey("enable");
        addTargetKey("title");
        addTargetKey("time-format");
        addTargetKey("directory");
        addTargetKey("delay");
        addTargetKey("comment");
        addTargetKey("backup-files-path");

        addTargetKey("webhook_enable");
        addTargetKey("webhook_webhook-url");
        addTargetKey("webhook_start");
        addTargetKey("webhook_finish");
        addTargetKey("webhook_time-format");
    }
}
