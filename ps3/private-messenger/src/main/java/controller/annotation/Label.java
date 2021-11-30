package controller.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Label {
    String name();
    String description() default "";
    String prefix() default "--";
    boolean required() default true;
}
