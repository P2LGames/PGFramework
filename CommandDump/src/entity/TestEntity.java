package entity;

import command.InputCommandDefault;
import command.StringCommandDefault;
import command.parameter.Input;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * An example of a concrete implementation of the entity class
 */
public class TestEntity extends Entity {
    /**
     * This constructor will initialize the defaults for any entity of this type
     *
     */
    public TestEntity() {
        super();
        try {
            this.makeDefault("talk", StringCommandDefault.class);
            this.makeDefault("input", InputCommandDefault.class, Input.class);
        } catch (Exception e) {
            System.out.println("Fatal Error");
        }
    }

    public TestEntity(String entityID) {
        super(entityID);
        try {
            this.replaceConstructor("talk", StringCommandDefault.class.getConstructor());
            this.replaceConstructor("input", InputCommandDefault.class.getDeclaredConstructor(Input.class));
            this.getParameterClassNames().put("input", Input.class);
        } catch (Exception e) {
            System.out.println("Fatal Error");
        }
    }

    public TestEntity(String entityID, Map<String, Constructor> constructorInstances, Map<String, Class> parameterClassNames) {
        super(entityID, constructorInstances, parameterClassNames);
    }
}
