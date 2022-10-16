package model;

/**
 * Interface implemented by possible keys in Entity-Relationship diagrams
 */
public interface Key {
    default boolean isSimple(){
        return this instanceof Attribute;
    }
}
