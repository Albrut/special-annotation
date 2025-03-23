package kg.nurtelecom.specialtask.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that a method parameter should be populated
 * with data extracted from the HTTP request based on the specified mappings.
 * This allows declarative extraction of request parameters, headers, path variables, etc.,
 * into a model object.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindRequest {

}
