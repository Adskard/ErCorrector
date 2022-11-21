package cz.cvut.fel.output.stringifier;

import cz.cvut.fel.model.*;

public interface DiagramVisitor {
    String visit(Entity entity);
    String visit(Relationship relationship);
    String visit(Attribute attribute);
    String visit(Connection connection);
    String visit(Composite composite);
}
