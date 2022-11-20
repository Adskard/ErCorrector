package model;

import output.stringifier.DiagramVisitor;

public interface DiagramComponent {
    String accept(DiagramVisitor visitor);
}
