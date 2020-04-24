/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
package dev.galasa.genapp.manager;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import dev.galasa.framework.spi.ValidAnnotatedFields;
import dev.galasa.genapp.manager.IGenApp;
import dev.galasa.genapp.manager.internal.GenAppManagerField;

/**
 * General Application (CB12) 
 * 
 * @galasa.annotation
 * 
 * @galasa.description The <code>{@literal @}GenApp</code> annotation requests Galasa to provision an instance of GenApp that provides the original GenApp functionality in JAVA
 * 
 * @galasa.examples 
 * <code>{@literal @}GenApp<br>
 * public IGenApp genapp;<br>
 * </code>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@GenAppManagerField
@ValidAnnotatedFields({ IGenApp.class })
public @interface GenApp {

}