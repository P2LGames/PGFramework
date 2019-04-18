package annotations;

import java.lang.annotation.*;

/**
 * Annotation to mark a getter and the fields it corresponds to
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Getter {
    String fieldName();
}
