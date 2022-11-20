package output.stringifier;

import model.*;

public interface DiagramVisitor {
    String visit(Entity entity);
    String visit(Relationship relationship);
    String visit(Attribute attribute);
    String visit(Connection connection);
    String visit(Composite composite);
}
