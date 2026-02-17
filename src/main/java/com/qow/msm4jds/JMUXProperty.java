package com.qow.msm4jds;

import com.qow.util.Property;
import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;

public class JMUXProperty extends Property {
    public JMUXProperty(QONObject jmux) throws NoSuchKeyException {
        this();
        putMap("server-ip", jmux.get("server-ip"));
        putMap("port", jmux.get("port"));
        putMap("auto-porting", jmux.get("auto-porting"));
        putMap("port-temp", jmux.get("port-temp"));
        putMap("bind-ip", jmux.get("bind-ip"));
        putMap("client-ip", jmux.get("client-ip"));
        putMap("protocol-id", jmux.get("protocol-id"));
    }

    public JMUXProperty() {
        super();
        addTargetKey("server-ip");
        addTargetKey("port");
        addTargetKey("auto-porting");
        addTargetKey("port-temp");
        addTargetKey("bind-ip");
        addTargetKey("client-ip");
        addTargetKey("protocol-id");
    }
}
