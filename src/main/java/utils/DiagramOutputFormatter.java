package utils;

import model.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiagramOutputFormatter {

    private static final Function<String, String> entityFormat = (name)->String.format("[%s]", name);
    private static final Function<String, String> attributeFormat = (name)->String.format("(%s)", name);
    private static final Function<String, String> relationshipFormat = (name)->String.format("<%s>", name);
    private static final Function<String, String> cardinalityFormat = (name)->name.equals("") ? "" : String.format("{%s}", name);

    private static final String attributeSeparator = ", ";
    private static final String compositeMemberSeparator = ", ";

    private static final List<Attribute> visitedAttributes = new LinkedList<>();

    public static String stringifyDiagram(Diagram diagram){
        return "====================LEGEND========================\n" +
                "[entity] (attributes) <relationship>\n" +
                "{cardinality} |Composite identifier|\n" +
                "* - marks a simple key attribute\n" +
                "$ - marks a weak entity\n" +
                "# - marks relationship member of composite key\n" +
                "\n====================Entities======================\n" +
                stringifyAllEntities(diagram) +
                "\n====================Relationships=================\n" +
                stringifyAllRelationships(diagram);
    }

    public static String stringifyAttribute(Attribute attribute){

        return (attribute.getIsKey() ? "*" : "") +
                attribute.getName() +
                stringifyAttributeList(attribute);
    }

    public static String stringifyAllRelationships(Diagram diagram){
        StringBuilder builder = new StringBuilder();

        diagram.getVertices().stream()
                .filter(DataClass::isRelationship)
                .map(dataClass -> (Relationship) dataClass)
                .forEach(relationship -> builder.append(String.format("%s\n",stringifyRelationship(relationship))));

        return builder.toString();
    }

    public static String stringifyRelationship(Relationship relationship){
        StringBuilder builder = new StringBuilder(relationshipFormat.apply(relationship.getName()));
        builder.append("\n");

        relationship.getConnections().stream()
                .filter(connection -> connection.getOtherParticipant(relationship).isEntity())
                .forEach(connection -> {
                    builder.append("\t");
                    builder.append(
                            String.format("%s - %s - Description: %s",
                                    cardinalityFormat.apply(connection.getCardinality().getValue()),
                                    entityFormat.apply(connection.getOtherParticipant(relationship).getName()),
                                    Arrays.toString(connection.getDescription().toArray())));
                    builder.append("\n");
                });

        String attributes = stringifyAttributeList(relationship);
        if(!attributes.equals("")){
            builder.append("\tAttributes: ");
            builder.append(attributes);
        }


        return builder.toString();
    }

    public static String stringifyEntity(Entity entity){
        StringBuilder builder = new StringBuilder();
        builder.append(entityFormat.apply((entity.getIsWeak() ? "$" : "") + entity.getName()));


        List<Generalization> hierarchy =  entity.getConnections().stream()
            .filter(Connection::isGeneralization)
            .filter(connection -> connection.isSource(entity))
            .map(connection -> (Generalization)connection)
            .collect(Collectors.toList());
        if(!hierarchy.isEmpty()){
            builder.append("\n\tAncestor: ");
            hierarchy.forEach(generalization -> builder.append(String.format("[%s]{%s, %s}",
                    generalization.getTarget().getName(),
                    generalization.getCoverage(),
                    generalization.getDisjointness())));
        }


        builder.append("\n\tAttributes: ");
        builder.append(stringifyAttributeList(entity));

        List<Composite> composites = entity.getKeys().stream()
                .filter(key -> !key.isSimple())
                .map(key -> (Composite) key)
                .collect(Collectors.toList());

        if(!composites.isEmpty()){
            builder.append("\n\tComposite keys:");
        }
        composites.forEach(composite -> {
            builder.append("\n\t\t|");
            composite.getCompositeMembers().values().forEach(
                    dataClass -> builder.append(
                            String.format("%s%s%s",
                                    dataClass.isRelationship() ? "#" : "",
                                    dataClass.getName(),
                                    compositeMemberSeparator
                            )));
        });

        if(!composites.isEmpty()){
            builder.delete(builder.length()-2, builder.length());
            builder.append("|");
        }


        return builder.toString();
    }

    public static String stringifyAllEntities(Diagram diagram){
        StringBuilder builder = new StringBuilder();

        diagram.getEntities()
                .forEach(entity -> builder.append(String.format("%s\n",stringifyEntity(entity))));

        return builder.toString();
    }

    public static String stringifyAttributeList(DataClass dataClass){
        StringBuilder builder = new StringBuilder();
        List<Connection> attributeConnections = dataClass.getConnections().stream()
                .filter(connection -> connection.getOtherParticipant(dataClass).isAttribute())
                .filter(connection -> {
                    Attribute attribute = (Attribute) connection.getOtherParticipant(dataClass);
                    if(visitedAttributes.contains(attribute)){
                        return false;
                    }
                    else{
                        visitedAttributes.add(attribute);
                        return true;
                    }
                })
                .collect(Collectors.toList());

        if(attributeConnections.isEmpty()){
            return "";
        }

        attributeConnections.forEach(connection -> {
                            Attribute attribute = (Attribute) (connection.getOtherParticipant(dataClass));
                            builder.append(cardinalityFormat.apply(connection.getCardinality().getValue()));
                            builder.append(String.format("%s%s",stringifyAttribute(attribute),attributeSeparator));
                        }
                );

        //delete last separation string
        builder.delete(builder.length()-2, builder.length());
        return attributeFormat.apply(builder.toString());
    }
}
