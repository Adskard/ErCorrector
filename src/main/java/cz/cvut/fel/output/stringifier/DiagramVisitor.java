package cz.cvut.fel.output.stringifier;

import cz.cvut.fel.model.*;

/**
 * Visitor interface for Diagram classes
 * @author Adam Skarda
 */
public interface DiagramVisitor {
    String visit(Entity entity);
    String visit(Relationship relationship);
    String visit(Attribute attribute);
    String visit(Edge edge);
    String visit(Composite composite);
}
