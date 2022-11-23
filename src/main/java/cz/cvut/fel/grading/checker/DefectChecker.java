package cz.cvut.fel.grading.checker;

import cz.cvut.fel.grading.configuration.ConfigExtractor;
import cz.cvut.fel.grading.configuration.value.*;
import cz.cvut.fel.grading.defect.Defect;
import cz.cvut.fel.enums.DefectType;

import lombok.Getter;
import lombok.extern.java.Log;
import cz.cvut.fel.model.Diagram;

import java.util.*;
import java.util.logging.Level;

/**
 * Class for Diagram Defect checking based on Configuration.
 * Used for Diagram error checking and Task/assignment checking.
 * These checks are represented by the Defect class.
 *
 * @author Adam Skarda
 */
@Log
@Getter
public class DefectChecker {

    private final List<Defect> defects = new LinkedList<>();
    private final Diagram diagram;
    private final ConfigExtractor extractor;

    /**
     * Basic constructor
     * @param diagram Diagram to be checked for defects
     * @param extractor Configuration for used defects
     */
    public DefectChecker(Diagram diagram, ConfigExtractor extractor){
        this.diagram = diagram;
        this.extractor = extractor;
    }

    /**
     * Searches for presence of defects in diagram
     * @return List of checked defects
     */
    public List<Defect> findDefects(){
        defects.clear();
        findBasicDefects();
        findAssignmentSpecificDefects();
        return defects;
    }

    /**
     * Checks for presence of basic ER modeling defects
     */
    private void findBasicDefects(){
        checkIllegalMultivaluedAttribute();
        checkCardinalities();
        checkDiagramComponent();
        checkAllEntityIds();
        checkWeakEntities();
        checkNamedVertices();
        checkDuplicateNames();
        checkDuplicateAttributes();
        checkHierarchyAnnotation();
    }

    /**
     * Checks for presence of assignment/task specific defects
     */
    private void findAssignmentSpecificDefects(){
        checkMultivaluedAttributeCount();
        checkMultivaluedAttributeCardinalityUsage();
        checkEntityCount();
        checkRelationshipCount();
        checkAttributeCount();
        checkWeakEntityCount();
        checkHierarchyCount();
        checkHierarchyUsage();
        checkCompositeIdentifierCount();
        checkMultipleIdentifierCount();
        checkNaryRelationshipCount();
        checkRecursiveRelationshipCount();
        checkStructuredAttributeCount();
        checkCardinalityUsage();
        checkCardinalityPairUsage();
    }


    /**
     * Checks presence of multivalued attributes in diagram and their count.
     * Adds the resulting QuantityDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkMultivaluedAttributeCount(){
        log.fine("Checking multivalued attribute count");

        DefectType defectType = DefectType.MULTIVALUED_ATTRIBUTE_COUNT;

        if(!extractor.isEnabledInConfig(defectType)){
            return;
        }

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkMultivaluedAttributeCount(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking multivalued attribute count!", ex);
        }
    }

    /**
     * Checks number of used entities in diagram.
     * Adds the resulting QuantityDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkEntityCount(){
        log.fine("Checking entity count");

        DefectType defectType = DefectType.ENTITY_COUNT;

        if (!extractor.isEnabledInConfig(defectType)) return;

        try {
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkEntityCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error during entity counting!", ex);
        }
    }

    /**
     * Checks for number of used recursive relationships in diagram.
     * Adds the resulting QuantityDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkRecursiveRelationshipCount(){
        log.fine("Checking recursive relationship count");

        DefectType defectType = DefectType.RECURSIVE_RELATIONSHIP_COUNT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkRecursiveRelationshipCount(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking recursive relationship count!", ex);
        }

    }

    /**
     * Checks diagram for number of weak entities.
     * Adds the resulting QuantityDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkWeakEntityCount(){
        log.fine("Checking weak entity count");

        DefectType defectType = DefectType.WEAK_ENTITY_COUNT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkWeakEntityCount(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking weak entity count!", ex);
        }
    }

    /**
     * Checks diagram for the number of composite identifiers.
     * Adds the resulting QuantityDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkCompositeIdentifierCount(){
        log.fine("Checking composite identification count");
        DefectType defectType = DefectType.COMPOSITE_ID_COUNT;
        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkCompositeCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting composite identifiers!", ex);
        }
    }

    /**
     * Checks for number of structured attributes in diagram.
     * Adds the resulting QuantityDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkStructuredAttributeCount(){
        log.fine("Checking structured attribute count");
        DefectType defectType = DefectType.STRUCTURED_ATTRIBUTE_COUNT;
        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkStructuredAttributeCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting structured attributes!", ex);
        }
    }

    /**
     * Checks diagram for number of entities with multiple identifiers.
     * Adds the resulting QuantityDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkMultipleIdentifierCount(){
        log.fine("Checking entity with multiple identifiers count");
        DefectType defectType = DefectType.MULTIPLE_ID_COUNT;
        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkMultipleIdentificationsCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting entities with multiple identifiers!", ex);
        }
    }

    /**
     * Checks diagram for number of n-ary relationships.
     * N-ary relationship is defined by the number of edges to entities.
     * So relationship with 3 edges to entities is ternary and so on.
     * The minimum number of edges comprising n-ary relationship can be configured.
     * Adds the resulting QuantityDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkNaryRelationshipCount(){
        log.fine("Check n-ary relationships");

        DefectType defectType = DefectType.N_ARY_RELATIONSHIP_COUNT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            NaryRelationshipConfigValue value = (NaryRelationshipConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkNaryRelationshipCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting n-ary relationships!", ex);
        }
    }


    /**
     * Checks the number of hierarchies in diagram.
     * Adds the resulting QuantityDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkHierarchyCount(){
        log.fine("Checking hierarchy count");

        DefectType defectType = DefectType.HIERARCHY_COUNT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkHierarchyCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting hierarchies!", ex);
        }
    }

    /**
     * Checks for number of attributes in diagram.
     * Adds the resulting QuantityDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkAttributeCount(){
        log.fine("Checking attribute count");

        DefectType defectType = DefectType.ORDINARY_ATTRIBUTE_COUNT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkAttributeCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting attributes!", ex);
        }
    }

    /**
     * Checks for number of relationships.
     * Adds the resulting QuantityDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkRelationshipCount(){
        log.fine("Checking relationship count");

        DefectType defectType = DefectType.RELATIONSHIP_COUNT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue value = (QuantityConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkRelationshipCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting relationships!", ex);
        }
    }

    /**
     * Checks cardinality usage on multivalued attributes.
     * That means checking if all expected cardinalities
     * were used on multivalued attribute connection.
     * Expected cardinalities are specified in configuration.
     * Adds the resulting UsageDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkMultivaluedAttributeCardinalityUsage(){
        log.fine("Checking multivalued attribute cardinality usages");

        DefectType defectType = DefectType.MULTIVALUED_ATTRIBUTE_CARDINALITY_USAGE;

        if (!extractor.isEnabledInConfig(defectType)) return;

        try{
            CardinalityUsageConfigValue value = (CardinalityUsageConfigValue) extractor.getConfigValue(defectType);
            defects.add(UsageDefectChecker.checkMultivaluedAttributeCardinality(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking cardinalities of multivalued attributes!", ex);
        }
    }

    /**
     * Checks types of cardinalities used on relationship connections.
     * That means checking if all expected cardinalities
     * were used on multivalued attribute edge.
     * Adds the resulting UsageDefect into the List of defects.
     * Expected cardinalities are specified in configuration.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkCardinalityUsage(){
        log.fine("Checking cardinality usage");

        DefectType defectType = DefectType.CARDINALITY_TYPE_USAGE;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            CardinalityUsageConfigValue values = (CardinalityUsageConfigValue) extractor.getConfigValue(defectType);

            Defect defect = UsageDefectChecker.checkCardinality(diagram, defectType, values);
            defects.add(defect);
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error during cardinality usage checking!", ex);
        }
    }



    /**
     * Checks types of hierarchy usage in diagram.
     * That means uses of HierarchyPair Coverage and Disjointness.
     * Expected HierarchyPairs are specified in configuration.
     * Adds the resulting UsageDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkHierarchyUsage(){
        log.log(Level.FINE, "Checking hierarchy pairs");

        DefectType defectType = DefectType.HIERARCHY_USAGE;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            HierarchyPairUsageConfigValue values = (HierarchyPairUsageConfigValue) extractor
                    .getConfigValue(defectType);

            Defect defect = UsageDefectChecker.checkHierarchy(diagram, defectType, values);
            defects.add(defect);
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error during hierarchy pair usage checking!", ex);
        }
    }


    /**
     * Checks for cardinality pair usage in diagram.
     * That means pairs of cardinalities connecting two entities through a relationship.
     * Expected CardinalityPairs are specified in configuration.
     * Adds the resulting UsageDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkCardinalityPairUsage(){
        log.log(Level.FINE, "Checking cardinality pairs");

        DefectType defectType = DefectType.CARDINALITY_PAIR_USAGE;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            CardinalityPairUsageConfigValue values = (CardinalityPairUsageConfigValue) extractor.getConfigValue(defectType);

            Defect defect = UsageDefectChecker.checkCardinalityPairs(diagram, defectType, values);
            defects.add(defect);
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error during cardinality pair usage checking!", ex);
        }
    }


    /**
     * For finding out if all generalizations have valid Coverage and Disjointness
     */
    private void checkHierarchyAnnotation(){
        log.log(Level.FINE, "Checking hierarchy annotation");

        DefectType defectType = DefectType.HIERARCHY_ANNOTATED;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            ConfigValue value = extractor.getConfigValue(defectType);
            defects.add(BasicDefectChecker.checkHierarchyAnnotation(diagram, defectType,value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Exception during hierarchy annotation checking", ex);
        }
    }

    /**
     * For finding out if diagram is in one component.
     * Adds the resulting BasicDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkDiagramComponent(){
        log.log(Level.FINE, "Checking diagram component");

        DefectType defectType = DefectType.ONE_COMPONENT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            ConfigValue value = extractor.getConfigValue(defectType);
            defects.add(BasicDefectChecker.checkDiagramComponent(diagram, defectType,value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Exception during diagram component checking", ex);
        }
    }

    /**
     * For finding out if every non-weak entity has an identifier.
     * An identifier can be both simple attribute or composite.
     * Adds the resulting BasicDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkAllEntityIds(){
        log.log(Level.FINE, "Checking entity identification");

        DefectType defectType = DefectType.EVERY_ENTITY_IDENTIFIED;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            ConfigValue value = extractor.getConfigValue(defectType);
            defects.add(BasicDefectChecker.checkAllEntityIds(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Exception during all entity identification checking checking", ex);
        }
    }

    /**
     * Checks if all multivalued attributes with cardinality on their Edge are proper multivalued attributes
     * i.e. checking that the edge cardinality is not 1..1.
     * Adds the resulting BasicDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkIllegalMultivaluedAttribute(){
        log.fine("Checking multivalued attribute legality");

        DefectType defectType = DefectType.MULTIVALUED_ATTRIBUTE_ILLEGAL_CARDINALITY;

        if(!extractor.isEnabledInConfig(defectType))   return;

        try{
            ConfigValue value = extractor.getConfigValue(defectType);
            defects.add(BasicDefectChecker.checkMultivaluedAttributes(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking legality of multivalued attributes!", ex);
        }
    }

    /**
     * For finding out if weak entities are properly identified by their composite key.
     * Composite key must be comprised of connection to a relationship with 1..1 cardinality
     * and an attribute.
     * Adds the resulting BasicDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkWeakEntities(){
        log.log(Level.FINE, "Checking weak entities");

        DefectType defectType = DefectType.WEAK_ENTITY_IDENTIFIED;

        if(!extractor.isEnabledInConfig(defectType))   return;

        try{
            ConfigValue value = extractor.getConfigValue(defectType);
            defects.add(BasicDefectChecker.checkWeakEntities(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking identification of weak entities!", ex);
        }

    }

    /**
     * For finding out if cardinalities are present where they should be.
     * i.e. every Entity - Relationship edge.
     * Adds the resulting BasicDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkCardinalities(){
        log.log(Level.FINE, "Checking edge cardinalities");

        DefectType defectType = DefectType.CARDINALITIES_PRESENT;

        if(!extractor.isEnabledInConfig(defectType))   return;

        try{
            ConfigValue value = extractor.getConfigValue(defectType);
            defects.add(BasicDefectChecker.checkCardinalities(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking presence of cardinalities on edges!", ex);
        }
    }

    /**
     * For finding out if there are duplicate entity and relationship names in diagram.
     * Adds the resulting BasicDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkDuplicateNames(){
        log.log(Level.FINE, "Checking duplicate names");

        DefectType defectType = DefectType.NO_DUPLICATE_NAMES;

        if(!extractor.isEnabledInConfig(defectType))   return;

        try{
            ConfigValue value = extractor.getConfigValue(defectType);
            defects.add(BasicDefectChecker.checkDuplicateNames(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking duplicate names!", ex);
        }
    }

    /**
     * For finding duplicate attribute names on one Entity or Relationship
     * Adds the resulting BasicDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkDuplicateAttributes(){
        log.log(Level.FINE, "Checking duplicate attributes");

        DefectType defectType = DefectType.NO_DUPLICATE_ATTRIBUTES;

        if(!extractor.isEnabledInConfig(defectType))   return;

        try{
            ConfigValue value = extractor.getConfigValue(defectType);
            defects.add(BasicDefectChecker.checkDuplicateAttributes(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking duplicate attributes!", ex);
        }
    }

    /**
     * For finding out if every vertex is named.
     * Adds the resulting BasicDefect into the List of defects.
     * Can be enabled or disabled in configuration.
     * @see ConfigExtractor
     */
    private void checkNamedVertices(){
        log.log(Level.FINE, "Checking named vertices");

        DefectType defectType = DefectType.NAMED_VERTICES;

        if(!extractor.isEnabledInConfig(defectType))   return;

        try{
            ConfigValue value = extractor.getConfigValue(defectType);
            defects.add(BasicDefectChecker.checkNamedVertices(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking named vertices!", ex);
        }
    }

}
