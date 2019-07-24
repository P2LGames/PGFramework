package annotations;

import java.lang.annotation.*;

/**
 * Annotation for denoting a function that maps to a command
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String commandName();
    int id();
}
