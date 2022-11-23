package cz.cvut.fel.grading.defect;

import cz.cvut.fel.output.stringifier.DefectVisitor;

/**
 * Interface specifying visitor accept or host methods.
 * @author Adam Skarda
 */
public interface Host {

    /**
     * Visitor method for stringifying this class
     *
     * @param defectVisitor Visitor that stringifies this class
     * @return String output of visitor visit() method
     */
    String accept(DefectVisitor defectVisitor);
}
