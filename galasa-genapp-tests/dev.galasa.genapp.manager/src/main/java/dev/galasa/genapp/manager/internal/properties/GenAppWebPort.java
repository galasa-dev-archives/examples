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

/**
 * General Application (CB12) Http Port CPS Property
 * 
 * @galasa.cps.property
 * 
 * @galasa.name genapp.instance.[GENAPPNAME].web.port
 * 
 * @galasa.description Provides the exposed Http-port of the CICS-region with GenApp
 * 
 * @galasa.required No
 * 
 * @galasa.default 12345
 * 
 * @galasa.valid_values Any valid TCP Port number
 * 
 * @galasa.examples 
 * <code>genapp.instance.[GENAPPNAME].web.port=54321</code>
 * 
 * @galasa.extra
 * This allows the manager to interact with CB12 through an HTTP-client with JSON requests and responses.
 * CB12 allows for a limited amount of functionality through its exposed port compared to 3270 interaction.
 */
public class GenAppWebPort extends CpsProperties{
    public static int get(@NotNull String instance)
            throws ConfigurationPropertyStoreException, GenAppManagerException {
        String webport = getStringWithDefault(GenAppPropertiesSingleton.cps(), "12345", "instance", "webnet.port",instance);
        return Integer.parseInt(webport);
    }
}