package output.stringifier;

import lombok.Getter;
import model.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class DiagramComponentStringifier implements DiagramVisitor{

    private final Function<String, String> entityFormat = (name)->String.format("[%s]", name);
    private final Function<String, String> attributeListFormat = (name)->String.format("(%s)", name);
    private final Function<String, String> relationshipFormat = (name)->String.format("<%s>", name);
    private final Function<String, String> cardinalityFormat = (name)->name.equals("") ? "" : String.format("{%s}", name);

    private final Function<String, String> compositeFormat = (composite)->String.format("|%s|", composite);

    private final String keySignifier = "*";

    private final String weakEntitySignifier = "$";

    private final String relationshipMemberSignifier = "#";

    private final Function<Attribute, String> attributeNameFormat = (attribute) ->
            attribute.getIsKey() ? String.format("%s%s", keySignifier, attribute.getName()) : attribute.getName();

    private final Function<Entity, String> entityNameFormat = (entity) ->
            entity.getIsWeak() ? String.format("%s%s", weakEntitySignifier, entity.getName()) : entity.getName();

    private final String attributeSeparator = ", ";
    private final String compositeMemberSeparator = ", ";
    private final List<Attribute> visitedAttributes = new LinkedList<>();

    @Override
    public String visit(Entity entity) {
        return stringifyEntity(entity);
    }

    @Override
    public String visit(Relationship relationship) {
        return stringifyRelationship(relationship);
    }

    @Override
    public String visit(Attribute attribute) {
        return stringifyAttribute(attribute);
    }

    @Override
    public String visit(Connection connection) {
        return null;
    }

    @Override
    public String visit(Composite composite) {
        return null;
    }

    private String stringifyRelationship(Relationship relationship){
        StringBuilder builder = new StringBuilder();

        builder.append(relationshipFormat.apply(relationship.getName()));
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
        if(!attributes.isEmpty()){
            builder.append("\tAttributes: ");
            builder.append(attributes);
            builder.append("\n");
        }

        return builder.toString();
    }

    private String stringifyAttribute(Attribute attribute){
        return (attributeNameFormat.apply(attribute) +
                stringifyAttributeList(attribute));
    }

    private String stringifyEntity(Entity entity){

        return entityFormat.apply(entityNameFormat.apply(entity)) +
                stringifyAncestors(entity) +
                "\n\tAttributes: " +
                stringifyAttributeList(entity) +
                stringifyCompositeKeys(entity) +
                "\n";
    }

    private String stringifyAncestors(Entity entity){
        StringBuilder builder = new StringBuilder();

        List<Generalization> hierarchy =  entity.getConnections().stream()
                .filter(Connection::isGeneralization)
                .filter(connection -> connection.isSource(entity))
                .map(connection -> (Generalization)connection)
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

    private String stringifyComposite(Composite composite){
        StringBuilder compositeString = new StringBuilder();

        composite.getCompositeMembers().values().forEach(
                dataClass -> compositeString.append(
                        String.format("%s%s%s",
                                dataClass.isRelationship() ? relationshipMemberSignifier : "",
                                dataClass.getName(),
                                compositeMemberSeparator
                        )));
        if(!compositeString.isEmpty()){
            compositeString.delete(compositeString.length() - compositeMemberSeparator.length(),
                    compositeString.length());
        }

        return compositeFormat.apply(compositeString.toString());
    }

    private String stringifyAttributeList(DataClass dataClass){
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
        return attributeListFormat.apply(builder.toString());
    }
}
