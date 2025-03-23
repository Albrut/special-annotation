package kg.nurtelecom.specialtask.annotation.annotations;


import kg.nurtelecom.specialtask.annotation.enums.TypeOfDataRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the type of data to be extracted from an HTTP request.
 * Used to annotate method parameters to indicate the source of the data
 * (e.g., header, query parameters, request body, etc.).
 *
 * <p>By default, {@link TypeOfDataRequest#BODY} is used, meaning
 * the data will be extracted from the request body.</p>
 *
 * @see TypeOfDataRequest
 */

@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestType {
    TypeOfDataRequest value() default TypeOfDataRequest.BODY;
    String key() default "";
}
