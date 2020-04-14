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
import dev.galasa.genapp.manager.IAccount;
import dev.galasa.genapp.manager.internal.GenAppManagerField;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@GenAppManagerField
@ValidAnnotatedFields({ IAccount.class })
public @interface Account {

    boolean existing() default true;

    int userID() default 1;

}
