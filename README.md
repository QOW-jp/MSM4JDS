# MSM4JDS (MinecraftServerManager4Java DefaultSettings)

## About

MSM4JDS : Minecraft Server Manager 4 Java Default Settings は、Minecraft Java Edition サーバーまたは Minecraft Bedrock
Edition サーバーを管理するための Java プログラムです。
使用するには、設定を管理するための msm4j.qon と jmux.qon を記述し、公式 Minecraft ウェブページからサーバーファイルをダウンロードする必要があります。

## Requirements

Java 17 or later

## Getting started

| 項目      | 詳細               |
|---------|------------------|
| OS      | Ubuntu 22.04 LTS |
| Edition | Java             |

## cmd

以下のコマンドでそれぞれ実行できます。

#### start_server.sh

サーバー起動コマンド

```
#!/bin/bash
java -jar MSM4JDS-1.0.0-JMUXCommand.jar jmux.qon msm4j.qon ENABLE 1
```

#### stop_server.sh

サーバー一時停止コマンド

```
#!/bin/bash
java -jar MSM4JDS-1.0.0-MSM4JCommand.jar msm4j.qon stop
```

#### backup_server.sh

サーバーバックアップコマンド

```
#!/bin/bash
java -jar MSM4JDS-1.0.0-MSM4JCommand.jar msm4j.qon BACKUP
```

#### exit_server.sh

サーバー停止コマンド

```
#!/bin/bash
java -jar MSM4JDS-1.0.0-JMUXCommand.jar jmux.qon msm4j.qon EXIT 1
```

#### control_server.sh

サーバーコマンド

```
#!/bin/bash
java -jar MSM4JDS-1.0.0-MSM4JCommand.jar msm4j.qon "op Player"
```

## config files

上のcmdを実行するために以下の2種類のコンフィグファイルを記述する必要があります。

#### msm4j.qon

```shell
#qon file 4 msm4j config v1.7.0
#Requirements qon4j v1.1.0 or later
#Minecraft Edition [java,bedrock,cmd]
edition=java
#Home path
home-dir=/home/user/Desktop/minecraft/java/default
#Server path
server-path=$(home-dir)/server.jar
#Webhook url (Only when any notification config is enabled)
webhook-url=https//discord.com/api/webhooks/???
#Log config
log{
    #Enable log
    enable=true
    #log title
    title=log
    #Time formats that conform to DateTimeFormatter
    time-format=yyyy年MM月dd日HH時mm分ss秒
    #Extension
    extension=.log
    #Log directory
    directory=$(home-dir)/run_log
}
#JVM config
jvm-args{
    #JVM argument
    before[
        -Xmx8G
        -XX:+UnlockExperimentalVMOptions
        -XX:+UseZGC
        -XX:ZUncommitDelay=50
        -XX:+AlwaysPreTouch
    ]
    #Java program argument
    after[
        nogui
    ]
}
#Notification config
notification{
    #Webhook url
    webhook-url=$(webhook-url)
    #Enable notifications when attempting to start up/quit
    server-wave=false
    #Enable notifications when startup/quit is successful
    server-status=true
    #Enable notification when log in/out
    log-in-out=true
    #player name index
    log-in-out-index=3
    #Time formats that conform to DateTimeFormatter
    time-format=HH:mm:ss
}
#Control config
control{
    #Enable control
    enable=true
    #Enable bind IP
    bind-ip=true
    #Server IP
    server-ip=localhost
    #Client IP (Only when bind-ip=true)
    client-ip=localhost
    #Server port
    port=9999
    #Enable auto port
    auto-porting=true
    #Server port temp file path
    port-temp=$(home-dir)/msm4j_port.temp
    #Communication protocol
    byte-size=1024
    #Protocol ID
    protocol-id=msm4j@$(home-dir)
}
#qon file 4 backup->msm4jmux config v1.6.0
#Requirements qon4j v1.1.0 or later
#Backup config
backup{
    #Enable backup
    enable=true
    #Backup file title
    title=MC_SERVER_BK
    #Time formats that conform to DateTimeFormatter
    time-format=yyyy年MM月dd日HH時mm分ss秒
    #Backup directory
    directory=$(home-dir)/server-backups
    #stop server delay
    delay=10
    #Notification comment
    comment=This server will stop for backup after $(delay) seconds.
    #Backup target files path
    backup-files-path[
        $(home-dir)/world
        $(home-dir)/server.properties
        $(home-dir)/whitelist.json
        $(home-dir)/usercache.json
    ]
    #Webhook config
    webhook{
        #Enable Webhook on Backup
        enable=true
        #Webhook url
        webhook-url=$(webhook-url)
        #Enable notifications when attempting to start backup
        start=true
        #Enable notifications when backup is finished
        finish=true
        #Time formats that conform to DateTimeFormatter
        time-format=HH:mm:ss
    }
}
```

#### jmux.qon

```shell
#qon file 4 jmux config v1.4.0
#Requirements qon4j v1.1.0 or later
#Home path
home-dir=/home/user/Desktop/minecraft/java/default
#Server IP
server-ip=localhost
#Server port
port=9999
#Enable auto port
auto-porting=true
#Server port temp file path
port-temp=$(home-dir)/jmux_port.temp
#Enable bind IP
bind-ip=true
#Client IP (Only when bind-ip=true)
client-ip=localhost
#Protocol ID
protocol-id=jmux@$(home-dir)
```