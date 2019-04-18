package annotations;

import java.lang.annotation.*;

/**
 * Annotation to mark the method that sets the entity field in the default command class
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SetEntity {
}
