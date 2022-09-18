package model;

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Diagram {
    private final List<DataClass> vertices = new LinkedList<>();
    private final List<Connection> edges = new LinkedList<>();

    public Diagram() {
    }

    public void addVertex(DataClass vert){
        vertices.add(vert);
    }

    public void addEdge(Connection edge){
        edges.add(edge);
    }

    public List<DataClass> getVertices() {
        return new LinkedList<>(vertices);
    }

    public List<Connection> getEdges() {
        return new LinkedList<>(edges);
    }

    public Optional<DataClass> findVertexById(String id){
        return vertices.stream().filter((vert) -> vert.getId().equals(id)).findAny();
    }

    public Optional<Connection> findEdgeById(String id){
        return edges.stream().filter((edge) -> edge.getId().equals(id)).findAny();
    }

    @Override
    public String toString() {
        return "Diagram{\n=======================\n"+
                "VERTICES\n"+
                "========================\n"+ Arrays.toString(vertices.toArray()) +
                "\n=======================\n"+
                "EDGES\n"+
                "========================\n"+
                Arrays.toString(edges.toArray()) +
                '}';
    }
}
