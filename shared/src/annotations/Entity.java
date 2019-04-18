package annotations;

import java.lang.annotation.*;

/**
 * Annotation to mark an entity and the class that holds its default commands
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    Class<?> defaultCommands();
}
