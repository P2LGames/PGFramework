package entity;

import command.StringCommandDefault;

/**
 * An example of a concrete implementation of the entity class
 */
public class TestEntity extends GenericEntity {
    /**
     * This constructor will initialize the defaults for any entity of this type
     *
     */
    public TestEntity() {
        super();
        try {
            this.makeDefault("talk", StringCommandDefault.class.getMethod("getString"));
        } catch (Exception e) {
            System.out.println("Unable to create testEntity\n" + e.getMessage());
        }
    }

    public TestEntity(String entityID) {
        super(entityID);
        try {
            this.makeDefault("talk", StringCommandDefault.class.getMethod("getString"));
        } catch (Exception e) {
            System.out.println("Unable to create testEntity\n" + e.getMessage());
        }
    }
}
