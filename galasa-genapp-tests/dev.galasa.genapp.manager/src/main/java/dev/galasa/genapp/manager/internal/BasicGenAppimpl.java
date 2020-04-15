package dev.galasa.genapp.manager.internal;

import dev.galasa.genapp.manager.IBasicGenApp;

public class BasicGenAppimpl implements IBasicGenApp {

    private String applid;

    public BasicGenAppimpl(String applId) {
        this.applid = applId;
    }

    @Override
    public String getApplId() {
        return this.applid;
    }

}