package 数据结构和算法.算法.图;

import 数据结构和算法.数据结构.hash.HashMap;
import 数据结构和算法.数据结构.map.Map;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author like
 * @date 2021-01-05 11:09
 * @contactMe 980650920@qq.com
 * @description
 */
public class ListGraph<V, E> implements Graph<V, E> {
    private Map<V, Vertex<V, E>> vertices = new HashMap<>();
    private Set<Edge<V, E>> edges = new HashSet<>();

    @Override
    public void print() {
        vertices.traversal((key, value) -> {
            System.out.println(key);
            System.out.println("==={out}===");
            System.out.println(value.fromEdges);
            System.out.println("==={in}===");
            System.out.println(value.toEdges);
            System.out.println("=================");
            return false;
        });
        edges.forEach(System.out::println);
    }

    @Override
    public int verticesSize() {
        return vertices.size();
    }

    @Override
    public int edgesSize() {
        return edges.size();
    }

    @Override
    public void addVertex(V v) {
        if (vertices.containsKey(v)) return;
        vertices.put(v, new Vertex<>(v));
    }

    @Override
    public void addEdge(V from, V to) {
        addEdge(from, to, null);
    }

    @Override
    public void addEdge(V from, V to, E weight) {
        // 1.保证from，to两个顶点都存在
        Vertex<V, E> fV = vertices.get(from);
        Vertex<V, E> tV = vertices.get(to);
        if (fV == null) {
            fV = new Vertex<>(from);
            vertices.put(from, fV);
        }
        if (tV == null) {
            tV = new Vertex<>(to);
            vertices.put(to, tV);
        }
        // 2.添加边
        Edge<V, E> e = new Edge<>(fV, tV, weight);
        if (fV.fromEdges.remove(e)) {   // 如果这个边存在就删除
            tV.toEdges.remove(e);
            edges.remove(e);
        }
        fV.fromEdges.add(e);
        tV.toEdges.add(e);
        edges.add(e);
    }

    @Override
    public void removeVertex(V v) {

    }

    @Override
    public void removeEdge(V from, V to) {

    }

    /**
     * 顶点
     */
    public static class Vertex<V, E> {
        V value;
        /** 以当前顶底为起始的边 **/
        Set<Edge<V, E>> fromEdges = new HashSet<>();
        /** 以当前顶底为终点的边 **/
        Set<Edge<V, E>> toEdges = new HashSet<>();

        public Vertex(V value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return value == null ? 0 : value.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Vertex)) return false;
            Vertex<?, ?> vertex = (Vertex<?, ?>) o;
            return Objects.equals(value, vertex.value);// && Objects.equals(outEdges, vertex.outEdges) && Objects.equals(intEdges, vertex.intEdges)
        }

        @Override
        public String toString() {
            return value == null ? "null" : value.toString();
        }
    }

    /**
     * 边缘
     */
    public static class Edge<V, E> {
        /** 当前边的起点 **/
        Vertex<V, E> from;
        /** 当前边的终点 **/
        Vertex<V, E> to;
        E weight;

        public Edge(Vertex<V, E> from, Vertex<V, E> to, E weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public int hashCode() {
            return from.hashCode() * 31 + to.hashCode();
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge)) return false;
            Edge<?, ?> edge = (Edge<?, ?>) o;
            return Objects.equals(from, edge.from) && Objects.equals(to, edge.to);//  && Objects.equals(weight, edge.weight)
        }

        @Override
        public String toString() {
            return "Edge{" +
                    "from=" + from +
                    ", to=" + to +
                    ", weight=" + weight +
                    '}';
        }
    }
}
