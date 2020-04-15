package dev.galasa.genapp.manager.internal;

import java.nio.charset.Charset;
import java.util.Random;

import dev.galasa.genapp.manager.IBasicGenApp;
/**
 * A Type of implementation that allows the capture of the application ID for running basic tests without the need of the manager
 */
public class BasicGenAppimpl implements IBasicGenApp {

    private String applid;
    private String address;
    private int port;

    public BasicGenAppimpl(String applId, String baseAddress, int port) {
        this.applid = applId;
        this.address = baseAddress;
        this.port = port;
    }

    @Override
    public String getApplId() {
        return this.applid;
    }

    @Override
    public String getBaseAddress() {
        return "http://" + this.address + ":" + this.port;
    }

    @Override
    public String provisionCustomerName() {
        return Integer.toHexString(new Random().nextInt()).substring(0, 6);
    }

}