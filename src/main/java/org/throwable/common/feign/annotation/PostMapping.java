package org.throwable.common.feign.annotation;

import java.lang.annotation.*;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/10/13 16:38
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostMapping {

    String url();

    String mediaType() default "application/json";

    boolean decodeSlash() default true;
}
