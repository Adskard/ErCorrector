package cz.cvut.fel.output.stringifier;

import cz.cvut.fel.model.*;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class DiagramComponentStringifier is an implementation of the visitor patter on
 * DiagramComponents, whose goal is to create a String out of said component.
 *
 * @author Adam Skarda
 */
@Getter
public class DiagramComponentStringifier implements DiagramVisitor{

    /**
     * Formatting of entity name
     */
    private final Function<String, String> entityFormat = (name)->String.format("[%s]", name);

    /**
     * Formatting of list of attributes
     */
    private final Function<String, String> attributeListFormat = (name)->String.format("(%s)", name);

    /**
     * Formatting of a relationship name
     */
    private final Function<String, String> relationshipFormat = (name)->String.format("<%s>", name);

    /**
     * Formatting of Cardinalities
     */
    private final Function<String, String> cardinalityFormat = (name)->name.equals("") ? "" : String.format("{%s}", name);

    /**
     * Formatting of composite keys
     */
    private final Function<String, String> compositeFormat = (composite)->String.format("|%s|", composite);

    /**
     * Token signifying a key attribute
     */
    private final String keySignifier = "*";

    /**
     * Token signifying a weak entity
     */
    private final String weakEntitySignifier = "$";

    /**
     * Token signifying a relationship member of composite key
     */
    private final String relationshipMemberSignifier = "#";

    /**
     * Formatting of attributes
     */
    private final Function<Attribute, String> attributeNameFormat = (attribute) ->
            attribute.getIsKey() ? String.format("%s%s", keySignifier, attribute.getName()) : attribute.getName();

    /**
     * Formatting of entities
     */
    private final Function<Entity, String> entityNameFormat = (entity) ->
            entity.getIsWeak() ? String.format("%s%s", weakEntitySignifier, entity.getName()) : entity.getName();

    /**
     * Token separating attributes in attribute lists
     */
    private final String attributeSeparator = ", ";

    /**
     * Token separating composite members in composite keys
     */
    private final String compositeMemberSeparator = ", ";

    /**
     * For preventing recursion during listing of structured attributes.
     * Must be emptied between diagrams (or after each listing)
     */
    private final List<Attribute> visitedAttributes = new LinkedList<>();

    /**
     * Creates a string representing given Entity
     * @param entity entity to be stringified
     * @return String representing given Entity
     */
    @Override
    public String visit(Entity entity) {
        return stringifyEntity(entity);
    }

    /**
     * Creates a string representing given Relationship
     * @param relationship entity to be stringified
     * @return String representing given Relationship
     */
    @Override
    public String visit(Relationship relationship) {
        return stringifyRelationship(relationship);
    }

    /**
     * Creates a string representing given Attribute
     * @param attribute entity to be stringified
     * @return String representing given Attribute
     */
    @Override
    public String visit(Attribute attribute) {
        return stringifyAttribute(attribute);
    }

    /**
     * Creates a string representing given Connection
     * @param edge entity to be stringified
     * @return String representing given Connection
     */
    @Override
    public String visit(Edge edge) {
        return null;
    }

    /**
     * Creates a string representing given Composite
     * @param composite entity to be stringified
     * @return String representing given Composite
     */
    @Override
    public String visit(Composite composite) {
        return null;
    }

    /**
     * Creates a relationship string that lists its attributes and connected entities
     * with Cardinalities.
     * @param relationship diagram relationship
     * @return String representing given relationship
     */
    private String stringifyRelationship(Relationship relationship){
        StringBuilder builder = new StringBuilder();

        //empty visited attributes to prevent conflicts
        visitedAttributes.clear();


        builder.append(relationshipFormat.apply(relationship.getName()));
        builder.append("\n");

        relationship.getEdges().stream()
                .filter(edge -> edge.getOtherParticipant(relationship).isEntity())
                .forEach(edge -> {
                    builder.append("\t");
                    builder.append(
                            String.format("%s - %s - Description: %s",
                                    cardinalityFormat.apply(edge.getCardinality().getValue()),
                                    entityFormat.apply(edge.getOtherParticipant(relationship).getName()),
                                    Arrays.toString(edge.getDescription().toArray())));
                    builder.append("\n");
                });

        String attributes = stringifyAttributeList(relationship);


        if(!attributes.isEmpty()){
            builder.append("\tAttributes: ");
            builder.append(attributes);
            builder.append("\n");
        }

        return builder.toString();
    }

    /**
     * Creates an Attribute String with additional associated attributes
     * for structured attribute representation.
     * @param attribute diagram attribute
     * @return String representing given attribute
     */
    private String stringifyAttribute(Attribute attribute){
        return (attributeNameFormat.apply(attribute) +
                stringifyAttributeList(attribute));
    }

    /**
     * Creates a string representing a diagram Entity
     * with its attributes, ancestors in hierarchy and composite keys.
     * @param entity diagram entity
     * @return String representing given Entity
     */
    private String stringifyEntity(Entity entity){

        //empty visited attributes to prevent conflicts
        visitedAttributes.clear();

        return entityFormat.apply(entityNameFormat.apply(entity)) +
                stringifyAncestors(entity) +
                "\n\tAttributes: " +
                stringifyAttributeList(entity) +
                stringifyCompositeKeys(entity) +
                "\n";
    }

    /**
     * Creates a list of entity hierarchy ancestors with
     * associated coverage and disjointness.
     * @param entity diagram Entity with ancestors
     * @return String representation of entity ancestry
     */
    private String stringifyAncestors(Entity entity){
        StringBuilder builder = new StringBuilder();

        List<Generalization> hierarchy =  entity.getEdges().stream()
                .filter(Edge::isGeneralization)
                .filter(edge -> edge.isSource(entity))
                .map(edge -> (Generalization)edge)
                .collect(Collectors.toList());

        if(!hierarchy.isEmpty()){
            builder.append("\n\tAncestor: ");
            hierarchy.forEach(generalization -> builder.append(String.format("%s%s",
                    entityFormat.apply(generalization.getTarget().getName()),
                    cardinalityFormat.apply(String.format("%s%s%s",
                            generalization.getCoverage(),
                            attributeSeparator,
                            generalization.getDisjointness()))
                    )));
        }

        return builder.toString();
    }

    /**
     * Creates a string representing Entity Composite key
     * by listing them
     * @param entity Diagram entity with composite keys
     * @return String representing all entity composite keys
     */
    private String stringifyCompositeKeys(Entity entity){
        StringBuilder builder = new StringBuilder();
        List<Composite> composites = entity.getKeys().stream()
                .filter(key -> !key.isSimple())
                .map(key -> (Composite) key)
                .collect(Collectors.toList());

        if(!composites.isEmpty()){
            builder.append("\n\tComposite keys:");
        }

        composites.forEach(composite -> {
            builder.append("\n\t\t");
            builder.append(stringifyComposite(composite));
        });
        return builder.toString();
    }

    /**
     * Creates a string representing a composite key of an Entity.
     * Composite key is a list of its member attributes/relationships.
     *
     * @param composite Composite key of an Entity
     * @return String representing a composite key
     */
    private String stringifyComposite(Composite composite){
        StringBuilder compositeString = new StringBuilder();

        composite.getCompositeMembers().values().forEach(
                vertex -> compositeString.append(
                        String.format("%s%s%s",
                                vertex.isRelationship() ? relationshipMemberSignifier : "",
                                vertex.getName(),
                                compositeMemberSeparator
                        )));
        if(compositeString.length() <= 0){
            compositeString.delete(compositeString.length() - compositeMemberSeparator.length(),
                    compositeString.length());
        }

        return compositeFormat.apply(compositeString.toString());
    }

    /**
     * Creates a string representing a list of attribute list.
     * @param vertex diagram Vertex whose attributes we want to stringify
     * @return String of Vertex Attributes
     */
    private String stringifyAttributeList(Vertex vertex){
        StringBuilder builder = new StringBuilder();
        List<Edge> attributeEdges = vertex.getEdges().stream()
                .filter(edge -> edge.getOtherParticipant(vertex).isAttribute())
                .filter(edge -> {
                    Attribute attribute = (Attribute) edge.getOtherParticipant(vertex);
                    if(visitedAttributes.contains(attribute)){
                        return false;
                    }
                    else{
                        visitedAttributes.add(attribute);
                        return true;
                    }
                })
                .collect(Collectors.toList());

        if(attributeEdges.isEmpty()){
            return "";
        }

        attributeEdges.forEach(edge -> {
                    Attribute attribute = (Attribute) (edge.getOtherParticipant(vertex));
                    builder.append(cardinalityFormat.apply(edge.getCardinality().getValue()));
                    builder.append(String.format("%s%s",stringifyAttribute(attribute),attributeSeparator));
                }
        );

        //delete last separation string
        builder.delete(builder.length()-2, builder.length());
        return attributeListFormat.apply(builder.toString());
    }
}
