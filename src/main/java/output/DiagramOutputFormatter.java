package output;

import model.*;
import output.stringifier.DiagramComponentStringifier;

public class DiagramOutputFormatter {

    private static final String relationshipSeparator = "\n";
    private static final String entitySeparator = "\n";
    private static final DiagramComponentStringifier stringifier = new DiagramComponentStringifier();

    public static String stringifyDiagram(Diagram diagram){
        return "====================LEGEND========================\n" +
                getLegend() +
                "\n====================Entities======================\n" +
                stringifyAllEntities(diagram) +
                "\n====================Relationships=================\n" +
                stringifyAllRelationships(diagram);
    }

    private static String getLegend(){
        String entityLegend = stringifier.getEntityFormat().apply("entity");
        String attributeLegend = stringifier.getAttributeListFormat().apply("attributes");
        String relationshipLegend = stringifier.getRelationshipFormat().apply("relationship");
        String cardinalityLegend = stringifier.getCardinalityFormat().apply("cardinality");
        String compositeLegend = stringifier.getCompositeFormat().apply("composite identifier");

        String keySignifier = String.format("%s - marks a simple key attribute",
                stringifier.getKeySignifier());

        String entitySignifier = String.format("%s - marks a weak entity",
                stringifier.getWeakEntitySignifier());

        String relationshipSignifier = String.format("%s - marks relationship member of composite key",
                stringifier.getRelationshipMemberSignifier());

        return String.format("%s %s %s\n%s %s\n%s\n%s\n%s\n",
                entityLegend, attributeLegend, relationshipLegend,
                cardinalityLegend, compositeLegend,
                keySignifier, entitySignifier, relationshipSignifier);
    }

    private static String stringifyAllRelationships(Diagram diagram){
        StringBuilder builder = new StringBuilder();

        diagram.getVertices().stream()
                .filter(DataClass::isRelationship)
                .map(dataClass -> (Relationship) dataClass)
                .forEach(relationship ->
                        builder.append(String.format("%s%s",
                                relationship.accept(stringifier), relationshipSeparator)));

        return builder.toString();
    }

    private static String stringifyAllEntities(Diagram diagram){
        StringBuilder builder = new StringBuilder();

        diagram.getEntities()
                .forEach(entity -> builder.append(String.format("%s%s",
                        entity.accept(stringifier), entitySeparator)));

        return builder.toString();
    }

}
