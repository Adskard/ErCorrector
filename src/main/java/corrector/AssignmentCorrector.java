package corrector;

import enums.Cardinality;
import enums.DefectType;
import lombok.extern.java.Log;
import model.*;
import model.Diagram;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 *
 * @author Adam Skarda
 */
@Log
public class AssignmentCorrector {

    private final List<Defect> defects = new LinkedList<>();

    private final Diagram diagram;
    private final Properties requirements;


    public AssignmentCorrector(Diagram diagram, Properties requirements){
        this.diagram = diagram;
        this.requirements = requirements;
    }
    /**
     * Finds defects based on configuration
     */
    public List<Defect> findDefects(){
        checkMultivaluedAttributes();
        checkEntityCount();
        return defects;
    }

    /**
     * Checks presence of multivalued attributes in diagram and their count.
     * @return number of multivalued attributes
     */
    private void checkMultivaluedAttributes(){
        log.fine("Checking multivalued attributes");
        DefectType defectType = DefectType.MULTIVALUED_ATTRIBUTE;
        String defaultValue = "1";
        try{
            Integer expectedCount = Integer.parseInt(requirements
                    .getProperty(defectType.getConfigKey(), defaultValue));

            Long actualCount = diagram.getEdges()
                    .stream()
                    .filter(connection -> connection.isAttributeConnection())
                    .filter(connection -> connection.hasCardinality())
                    .count();

            String info = String.format("Expected multivalued attributes: %s was %s", expectedCount, actualCount);
            if(actualCount < expectedCount){
                defects.add(new Defect(defectType, true, info));
            }
            else {
                defects.add(new Defect(defectType, false, info));
            }
        }
        catch(RuntimeException ex){
            log.warning(ex.getMessage());
        }
    }

    /**
     * Checks number of used entities in diagram.
     * @return number of entities
     */
    private void checkEntityCount(){
        log.fine("Checking entity count");
        DefectType defectType = DefectType.ENTITY_COUNT;
        String defaultValue = "2";
        try {
            Integer expectedCount = Integer.parseInt(requirements
                    .getProperty(defectType.getConfigKey(), defaultValue));

            Long actualCount = diagram.getVertices().stream()
                    .filter(dataClass -> dataClass.isEntity())
                    .count();

            String info = String.format("Expected entities: %s was %s", expectedCount, actualCount);
            if(actualCount < expectedCount){
                defects.add(new Defect(defectType, true, info));
            }
            else{
                defects.add(new Defect(defectType, false, info));
            }
        }
        catch (RuntimeException ex){
            log.warning(ex.getMessage());
        }
    }

    /**
     * Checks types of cardinalities used and their respective count.
     */
    private void checkUsedCardinalities(){

    }

    /**
     * Checks for recursive relationships in diagram.
     * @return number of unique recursive relationships present in diagram
     */
    private void checkRecursiveRelationship(){

    }

    /**
     * Checks diagram for weak entities and their count.
     * @return number of weak entities present
     */
    private void checkWeakEntities(){

    }

    /**
     * Checks diagram for the number of composite identifiers.
     * @return number of composite identifiers in diagram
     */
    private void checkCompositeIdentifiers(){
    }

    /**
     * Checks hierarchy usage in diagram. How many were used, which types were used.
     */
    private void checkHierarchyUsages(){

    }

    /**
     * Checks structured attribute usage in diagram.
     */
    private void checkStructuredAttributes(){

    }

    /**
     * Checks diagram for entities with multiple identifiers.
     */
    private void checkMultipleIdentifiers(){

    }

    /**
     * Checks diagram for n-ary relationships.
     * N-ary relationship is between 3 or more entities.
     */
    private void checkNaryRelationships(){

    }
}
