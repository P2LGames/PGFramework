package entity;

import command.StringCommandDefault;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * An example of a concrete implementation of the
 */
public class TalkingEntity extends Entity {
    /**
     * This constructor will initialize the defaults for any entity of this type
     *
     */
    public TalkingEntity() {
        super();
        try {
            this.makeDefault("talk", StringCommandDefault.class); } catch (Exception e) {
            System.out.println("Fatal Error");
        }
    }

    public TalkingEntity(String entityID) {
        super(entityID);
        try {
            this.replaceConstructor("talk", StringCommandDefault.class.getConstructor());
        } catch (Exception e) {
            System.out.println("Fatal Error");
        }
    }

    public TalkingEntity(String entityID, Map<String, Constructor> constructorInstances, Map<String, Class> parameterClassNames) {
        super(entityID, constructorInstances, parameterClassNames);
    }
}
