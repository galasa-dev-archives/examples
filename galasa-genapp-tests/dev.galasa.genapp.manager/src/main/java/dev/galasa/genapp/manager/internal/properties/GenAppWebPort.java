/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
package dev.galasa.genapp.manager.internal.properties;

import dev.galasa.framework.spi.ConfigurationPropertyStoreException;
import dev.galasa.framework.spi.cps.CpsProperties;
import dev.galasa.genapp.manager.GenAppManagerException;

import javax.validation.constraints.NotNull;

public class GenAppWebPort extends CpsProperties{
    public static int get(@NotNull String instance)
            throws ConfigurationPropertyStoreException, GenAppManagerException {
        String webport = getStringWithDefault(GenAppPropertiesSingleton.cps(), "12345", "instance", "webnet.port",instance);
        return Integer.parseInt(webport);
    }
}