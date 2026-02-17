package com.qow.msm4jds;

import com.qow.util.Property;
import com.qow.util.UntrustedPropertyException;
import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

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

    protected static JMUXProperty getProperty(String path) {
        try {
            JMUXProperty property = new JMUXProperty(new QONObject(new File(path)));
            property.parse();
            return property;
        } catch (IOException | UntrustedQONException | NoSuchKeyException | UntrustedPropertyException e) {
            System.err.println("Not Available JMUXProperty.");
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
