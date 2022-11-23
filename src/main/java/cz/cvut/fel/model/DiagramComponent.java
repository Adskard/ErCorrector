package cz.cvut.fel.model;

import cz.cvut.fel.output.stringifier.DiagramVisitor;

/**
 * Interface DiagramComponent is used to group members of a diagram
 */
public interface DiagramComponent {
    String accept(DiagramVisitor visitor);
}
