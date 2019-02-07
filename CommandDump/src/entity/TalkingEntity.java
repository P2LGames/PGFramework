package entity;

import command.StringCommandDefault;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * An example of a concrete implementation of the
 */
public class TalkingEntity extends GenericCommandEntity {
    /**
     * This constructor will initialize the defaults for any entity of this type
     *
     */
    public TalkingEntity() {
        super();
        try {
            this.makeDefault("talk", StringCommandDefault.class.getMethod("getString")); } catch (Exception e) {
            System.out.println("Unable to create TalkingEntity\n" + e.getMessage());
        }
    }

    public TalkingEntity(String entityID) {
        super(entityID);
        try {
            this.makeDefault("talk", StringCommandDefault.class.getMethod("getString"));
        } catch (Exception e) {
            System.out.println("Unable to create testEntity\n" + e.getMessage());
        }
    }
}
