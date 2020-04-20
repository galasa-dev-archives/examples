package dev.galasa.genapp.manager;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface AreasTested {

    String[] areas();

}