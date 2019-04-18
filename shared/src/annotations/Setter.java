package annotations;

import java.lang.annotation.*;

/**
 * Annotation to mark a setter. Holds the name of the field and the type of that field
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Setter {
    String fieldName();
    Class<?> type();
}
