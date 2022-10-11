package formatter;

import enums.Cardinality;
import model.*;

public class SimpleDiagramFormat {
    public static String stringifyDiagram(Diagram diagram){
        StringBuilder string = new StringBuilder("");

        string.append("Relationships\n");
        string.append("==================================================\n");
        string.append("* - marks a simple key attribute\n");
        string.append("$ - marks a weak entity\n");
        diagram.getVertices().stream().filter((dataClass)-> dataClass instanceof Relationship)
                .map(dataClass -> (Relationship) dataClass)
                .forEach(relationship -> string.append(stringifyRelationship(relationship) + "\n"));

        string.append("Composite keys\n");
        string.append("==================================================\n");
        string.append("# - marks relationship member of composite key\n");
        diagram.getComposites().stream().forEach(composite -> string.append(stringifyComposite(composite) + "\n"));

        return string.toString();
    }


    public static String stringifyAttribute(Attribute attribute){
        return String.format("%s%s", attribute.getIsKey() ? "*" : "", attribute.getName());
    }

    public static String stringifyRelationship(Relationship relationship){
        StringBuilder builder = new StringBuilder(relationship.getName());
        builder.append("\n");

        relationship.getConnections().stream()
                .filter(connection -> connection.getTarget() instanceof Entity)
                .forEach(connection -> {
                    builder.append("\t");
                    builder.append(
                            String.format("%s - %s",
                                    connection.getCardinality(),
                                    stringifyEntity((Entity)connection.getTarget())));
                    builder.append("\n");
                });

        return builder.toString();
    }

    public static String stringifyEntity(Entity entity){
        StringBuilder builder = new StringBuilder("");
        builder.append(String.format("[%s%s]-", entity.getIsWeak() ? "$" : "", entity.getName()));

        String sep = ", ";
        builder.append(" Attributes{");
        entity.getConnections().stream()
                .filter(connection -> connection.getSource() instanceof Attribute)
                .forEach(connection -> {
                            builder.append(connection.getCardinality().equals(Cardinality.NO_CARDINALITY)
                                    ? "" : String.format("(%s)", connection.getCardinality()));
                            builder.append(stringifyAttribute((Attribute) connection.getSource()) + sep);
                        }
                );

        //delete last separation string
        builder.delete(builder.length()-2, builder.length());
        builder.append("}");
        return builder.toString();
    }

    public static String stringifyComposite(Composite composite){
        StringBuilder builder = new StringBuilder(
                String.format("Composite key id=%s identifies [%s]",
                        composite.getId(),
                        composite.getEntity().getName()));
        builder.append("\n\t[");

        composite.getCompositeMembers().values().forEach(dataClass -> builder.append(
                String.format("%s%s, ",
                        dataClass instanceof Relationship ? "#" : "",
                        dataClass.getName()
                        )));
        builder.delete(builder.length()-2, builder.length());
        builder.append("]");

        return builder.toString();
    }
}
