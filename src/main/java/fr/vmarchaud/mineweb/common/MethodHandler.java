package fr.vmarchaud.mineweb.common;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface MethodHandler {
    int inputs() default 0;
    
    Class[] types() default { Void.class };
}
