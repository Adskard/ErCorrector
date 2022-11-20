package corrector.checker;

import corrector.configuration.ConfigExtractor;
import corrector.configuration.value.ConfigValue;
import corrector.configuration.value.NaryRelationshipConfigValue;
import corrector.configuration.value.QuantityConfigValue;
import corrector.configuration.value.UsageConfigValue;
import corrector.defect.BasicDefect;
import corrector.defect.Defect;
import corrector.struct.CardinalityPair;
import corrector.struct.HierarchyPair;
import enums.Cardinality;
import enums.DefectType;

import lombok.Getter;
import lombok.extern.java.Log;
import model.*;
import model.Diagram;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 *
 * @author Adam Skarda
 */
@Log
@Getter
public class DefectChecker {

    private final List<Defect> defects = new LinkedList<>();
    private final Diagram diagram;
    private final ConfigExtractor extractor;

    public DefectChecker(Diagram diagram, ConfigExtractor extractor){
        this.diagram = diagram;
        this.extractor = extractor;
    }

    /**
     *
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
    }

    /**
     * Checks for presence of assignment specific defects
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
     * Checks for recursive relationships in diagram.
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
     * Checks diagram for weak entities and their count.
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
     * Checks structured attribute usage in diagram.
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
     * Checks diagram for entities with multiple identifiers.
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
     * Checks diagram for n-ary relationships.
     * N-ary relationship is between 3 or more entities.
     */
    private void checkNaryRelationshipCount(){
        log.fine("Check n-ary relationships");

        DefectType defectType = DefectType.N_ARY_RELATIONSHIP_COUNT;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            QuantityConfigValue q = (QuantityConfigValue) extractor.getConfigValue(defectType);
            NaryRelationshipConfigValue value = (NaryRelationshipConfigValue) extractor.getConfigValue(defectType);
            defects.add(QuantityDefectChecker.checkNaryRelationshipCount(diagram, defectType, value));
        }
        catch (RuntimeException ex){
            log.log(Level.WARNING, "Error counting n-ary relationships!", ex);
        }
    }


    /**
     * Checks hierarchy count.
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
     * Checks attribute count.
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
     * Checks relationship count.
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
     * Checks cardinality usage on multivalued attributes
     */
    private void checkMultivaluedAttributeCardinalityUsage(){
        log.fine("Checking multivalued attribute cardinality usages");

        DefectType defectType = DefectType.MULTIVALUED_ATTRIBUTE_CARDINALITY_USAGE;

        if (!extractor.isEnabledInConfig(defectType)) return;

        try{
            UsageConfigValue<Cardinality> value = (UsageConfigValue<Cardinality>) extractor.getConfigValue(defectType);
            defects.add(UsageDefectChecker.checkMultivaluedAttributeCardinality(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking cardinalities of multivalued attributes!", ex);
        }
    }

    /**
     * Checks types of cardinalities used and their respective count.
     */
    private void checkCardinalityUsage(){
        log.fine("Checking cardinality usage");

        DefectType defectType = DefectType.CARDINALITY_TYPE_USAGE;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            UsageConfigValue<Cardinality> values = (UsageConfigValue<Cardinality>) extractor.getConfigValue(defectType);

            Defect defect = UsageDefectChecker.checkCardinality(diagram, defectType, values);
            defects.add(defect);
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error during cardinality usage checking!", ex);
        }
    }


    /**
     * Checks hierarchy usage in diagram. How many were used, which types were used.
     */
    private void checkHierarchyUsage(){
        log.log(Level.FINE, "Checking hierarchy pairs");

        DefectType defectType = DefectType.HIERARCHY_USAGE;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            UsageConfigValue<HierarchyPair> values = (UsageConfigValue<HierarchyPair>) extractor
                    .getConfigValue(defectType);

            Defect defect = UsageDefectChecker.checkHierarchy(diagram, defectType, values);
            defects.add(defect);
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error during hierarchy pair usage checking!", ex);
        }
    }


    /**
     * Checks for cardinality pair usage..
     */
    private void checkCardinalityPairUsage(){
        log.log(Level.FINE, "Checking cardinality pairs");

        DefectType defectType = DefectType.CARDINALITY_PAIR_USAGE;

        if(!extractor.isEnabledInConfig(defectType)) return;

        try{
            UsageConfigValue<CardinalityPair> values = (UsageConfigValue<CardinalityPair>) extractor.getConfigValue(defectType);

            Defect defect = UsageDefectChecker.checkCardinalityPairs(diagram, defectType, values);
            defects.add(defect);
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error during cardinality pair usage checking!", ex);
        }
    }



    /**
     * For finding diagram connectivity. Diagram should have only one component.
     * It achieves this by doing a DFS and comparing visited nodes to diagram vertices.
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
     * For finding out if every non-weak entity has an identifier
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
     * Checks if all attributes with cardinality connection are proper multivalued attributes
     * i.e. not cardinality 1..1.
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
     * Weak entity must be connected to relationship with non-weak entity with 1..1 cardinality
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
     * Entity - Relationship connections
     */
    private void checkCardinalities(){
        log.log(Level.FINE, "Checking connection cardinalities");

        DefectType defectType = DefectType.CARDINALITIES_PRESENT;

        if(!extractor.isEnabledInConfig(defectType))   return;

        try{
            ConfigValue value = extractor.getConfigValue(defectType);
            defects.add(BasicDefectChecker.checkCardinalities(diagram, defectType, value));
        }
        catch(RuntimeException ex){
            log.log(Level.WARNING, "Error checking presence of cardinalities on connections!", ex);
        }
    }

    /**
     * For finding out if there are duplicate entity and relationship names
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
     * For finding out if every vertex is named
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
