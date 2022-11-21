package cz.cvut.fel.model;

import cz.cvut.fel.output.stringifier.DiagramVisitor;

public interface DiagramComponent {
    String accept(DiagramVisitor visitor);
}
