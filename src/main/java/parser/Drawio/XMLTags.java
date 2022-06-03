package parser.Drawio;

public enum XMLTags {
    XML_TAG("mxCell"),
    PARENT_ATTRIBUTE("parent"),
    EDGE_ATTRIBUTE("edge"),
    NAME_ATTRIBUTE("value"),
    STYLE_ATTRIBUTE("style"),
    ID_ATTRIBUTE("id"),
    SOURCE_ATTRIBUTE("source"),
    TARGET_ATTRIBUTE("target"),
    VERTEX_ATTRIBUTE("vertex");

    private final String value;
    private XMLTags(String XMLatribute){
        this.value = XMLatribute;
    }

    public String getValue() {
        return value;
    }
}
