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
 * General Application (CB12) Zos Image CPS Property
 * 
 * @galasa.cps.property
 * 
 * @galasa.name genapp.instance.[GENAPPNAME].zos.image
 * 
 * @galasa.description Provides a pointer towards the zos image cps properties
 * 
 * @galasa.required No
 * 
 * @galasa.default GENAPP
 * 
 * @galasa.valid_values Any valid Zos image name
 * 
 * @galasa.examples 
 * <code>genapp.instance.[GENAPPNAME].zos.image=GENAPPLOCAL</code>
 * 
 * @galasa.extra
 * The GenApp manager takes the assumption that you have set up the Zos Image CPS properties correctly, because this manager is dependant on Zos and Zos3270. <br>
 * This CPS property acts as a pointer towards your set up Zos image properties, so that you still are able to have multiple zos images.
 */
public class GenAppZosImage extends CpsProperties{
    public static String get(@NotNull String instance)
            throws ConfigurationPropertyStoreException, GenAppManagerException {
        return getStringWithDefault(GenAppPropertiesSingleton.cps(), "GENAPP", "instance", "zos.image", instance);
    }
}