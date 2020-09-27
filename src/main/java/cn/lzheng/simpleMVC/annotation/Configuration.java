package cn.lzheng.simpleMVC.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {

    String controllerSrc();

    String dispatcherUrl() default "/";

    String staticPath() default "";

    String suffix() default ".jsp";

}
