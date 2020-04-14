/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
package dev.galasa.genapp.manager.internal.properties;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import dev.galasa.framework.spi.IConfigurationPropertyStoreService;
import dev.galasa.genapp.manager.GenAppManagerException;

@Component(service = GenAppPropertiesSingleton.class, immediate = true)
public class GenAppPropertiesSingleton {
    private static GenAppPropertiesSingleton  INSTANCE;

    private IConfigurationPropertyStoreService cps;

    @Activate
    public void activate() {
        INSTANCE = this;
    }

    @Deactivate
    public void deacivate() {
        INSTANCE = null;
    }

    public static IConfigurationPropertyStoreService cps() throws GenAppManagerException {
        if (INSTANCE != null) {
            return INSTANCE.cps;
        }

        throw new GenAppManagerException("Attempt to access manager CPS before it has been initialised");
    }

    public static void setCps(IConfigurationPropertyStoreService cps) throws GenAppManagerException {
        if (INSTANCE != null) {
            INSTANCE.cps = cps;
            return;
        }

        throw new GenAppManagerException("Attempt to set manager CPS before instance created");
    }
}