package cz.cvut.fel.output;

import cz.cvut.fel.model.Diagram;
import cz.cvut.fel.model.Relationship;
import cz.cvut.fel.output.stringifier.DiagramComponentStringifier;
import cz.cvut.fel.model.Vertex;

/**
 * Class DiagramOutputFormatter is used to make a Diagram Object
 * into a comprehensive string.
 *
 * @author Adam Skarda
 */
public class DiagramOutputFormatter {

    /**
     * Token separating relationships their listing
     */
    private static final String relationshipSeparator = "\n";

    /**
     * Token separating entities during their listing
     */
    private static final String entitySeparator = "\n";

    /**
     * For making strings out of diagram components
     */
    private static final DiagramComponentStringifier stringifier = new DiagramComponentStringifier();

    /**
     * Creates a comprehensive String describing a given diagram.
     *
     * @param diagram diagram to be made into a String
     * @return String describing the diagram
     */
    public static String stringifyDiagram(Diagram diagram){
        return "====================LEGEND========================\n" +
                getLegend() +
                "\n====================Entities======================\n" +
                stringifyAllEntities(diagram) +
                "\n====================Relationships=================\n" +
                stringifyAllRelationships(diagram);
    }

    /**
     * Creates a string which describes the legend (formatting)
     * of resulting diagram String.
     * Makes reading by user easier.
     * @return String legend
     */
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
                .filter(Vertex::isRelationship)
                .map(vertex -> (Relationship) vertex)
                .forEach(relationship ->
                        builder.append(String.format("%s%s",
                                relationship.accept(stringifier), relationshipSeparator)));

        return builder.toString();
    }

    /**
     * Lists all entities present in Diagram separated by entitySeparator token
     * @param diagram Diagram whose entities we want to list
     * @return String list of entities in a given Diagram
     */
    private static String stringifyAllEntities(Diagram diagram){
        StringBuilder builder = new StringBuilder();

        diagram.getEntities()
                .forEach(entity -> builder.append(String.format("%s%s",
                        entity.accept(stringifier), entitySeparator)));

        return builder.toString();
    }

}
