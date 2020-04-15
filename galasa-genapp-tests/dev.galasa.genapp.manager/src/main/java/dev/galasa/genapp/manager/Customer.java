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
import dev.galasa.genapp.manager.ICustomer;
import dev.galasa.genapp.manager.internal.GenAppManagerField;

/**
 * Customer 
 * 
 * @galasa.annotation
 * 
 * @galasa.description The <code>{@literal @}Customer</code> annotation requests Galasa to provision a customer found in the VSAM of GenApp
 * 
 * @galasa.examples 
 * <code>{@literal @}Customer<br>
 * public ICustomer cust1;<br>
 * {@literal @}Customer(userID=1)<br>
 * public ICustomer cust2;<br>
 * </code>
 * 
 * @galasa.extra
 * This annotation will try and provide an existing Customer from the GenApp VSAM files, but if the given userID does not exist GenApp will create a new one in the VSAM
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@GenAppManagerField
@ValidAnnotatedFields({ ICustomer.class })
public @interface Customer {

    /**
     * This refers to the unique identifier of a customer within GenApp. 
     * 
     */
    int userID() default 0;

}
