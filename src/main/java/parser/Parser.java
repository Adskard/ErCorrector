package parser;

import model.Diagram;
import org.w3c.dom.Document;

public interface Parser {
    Diagram parse(Document document);
}
