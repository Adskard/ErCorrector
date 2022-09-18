package parser.Drawio;

public enum XMLTags {
    CELL("mxCell"),
    PARENT_ATTRIBUTE("parent"),
    EDGE_ATTRIBUTE("edge"),
    NAME_ATTRIBUTE("value"),
    STYLE_ATTRIBUTE("style"),
    ID_ATTRIBUTE("id"),
    SOURCE_ATTRIBUTE("source"),
    TARGET_ATTRIBUTE("target"),
    VERTEX_ATTRIBUTE("vertex");

    private final String value;
    XMLTags(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
