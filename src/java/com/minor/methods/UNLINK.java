package com.minor.methods;

/**
 *
 * @author Vishesh
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.ws.rs.HttpMethod;

@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@HttpMethod(value = "UNLINK")
@Documented
public @interface UNLINK {
}
