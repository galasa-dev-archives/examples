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
 * General Application (CB12) CICS ApplID CPS Property
 * 
 * @galasa.cps.property
 * 
 * @galasa.name genapp.instance.[GENAPPNAME].cics.applID
 * 
 * @galasa.description Provides the Application ID of the CICS region with GenApp installed
 * 
 * @galasa.required Yes - The ApplID is necessary to be able to work with the GenApp instance
 * 
 * @galasa.default None
 * 
 * @galasa.valid_values A valid Application ID for the desired CICS-region
 * 
 * @galasa.examples 
 * <code>genapp.instance.[GENAPPNAME].cics.applID=CICPY01D<br>
 * </code>
 * 
 * @galasa.extra
 * The GenApp manager makes the assumption that you have an instance of GenApp (CB12) installed inside a CICS-region, where <br>
 * you have a user available with the necessary RACF-permissions.
 * */
public class GenAppCicsApplID extends CpsProperties{
    public static String get(@NotNull String instance)
            throws ConfigurationPropertyStoreException, GenAppManagerException {
        return getStringNulled(GenAppPropertiesSingleton.cps(), "instance", "cics.applID",instance);
    }
}