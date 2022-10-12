package corrector;

import lombok.extern.java.Log;
import model.*;

import java.util.logging.Level;

/**
 * The BasicCorrector class and its methods are used
 */
@Log
public class BasicCorrector {

    private final Diagram diagram;

    public BasicCorrector(Diagram diagram){
        this.diagram = diagram;
    }

    public void findDefects(){
        checkCardinalities();
        checkDiagramComponent();
        checkEntityIdentification();
        checkWeakEntities();
    }

    /**
     * For finding diagram connectivity. Diagram should have only one component.
     */
    private void checkDiagramComponent(){
        log.log(Level.FINE, "Checking diagram component");
    }

    /**
     * For finding out if every non-weak entity has an identifier
     */
    private void checkEntityIdentification(){
        log.log(Level.FINE, "Checking entity identification");
    }

    /**
     * For finding out if weak entities are properly identified by their composite key.
     */
    private void checkWeakEntities(){
        log.log(Level.FINE, "Checking weak entities");
    }

    /**
     * For finding out if cardinalities are present where they should be.
     * Entity - Relationship connections
     */
    private void checkCardinalities(){
        log.log(Level.FINE, "Checking connection cardinalities");
    }
}
