package dev.galasa.genapp.manager.internal;

import dev.galasa.genapp.manager.IBasicGenApp;
/**
 * A Type of implementation that allows the capture of the application ID for running basic tests without the need of the manager
 */
public class BasicGenAppimpl implements IBasicGenApp {

    private String applid;
    private String address;
    private int port;

    public BasicGenAppimpl(String applId, String baseAddress, int webnetPort) {
        this.applid = applId;
        this.address = baseAddress;
        this.port = webnetPort;
    }

    @Override
    public String getApplId() {
        return this.applid;
    }

    @Override
    public String getBaseAddress() {
        return this.address;
    }

    @Override
    public int getWebnetPort() {
        return this.port;
    }

}