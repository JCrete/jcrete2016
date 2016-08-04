package org.jcrete.bitterjava8.visitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A directory consists of other nodes (i.e. files and/or directories).
 *
 * @author ikost
 */
@org.jpatterns.gof.VisitorPattern.ConcreteElement
public class Directory extends Node {

    private final List<Node> nodes = new LinkedList<>();

    public Directory(String name) {
        super(name);
    }

    @Override
    public void add(Node child) {
        if (child != null) {
            nodes.add(child);
            size += child.getSize();
        }
    }

    @Override
    public void remove(Node child) {
        if (child != null) {
            for (Node node : nodes) {
                if (child.equals(node)) {
                    size -= child.getSize();
                    nodes.remove(child);
                } else if (node instanceof Directory) {
                    node.remove(child);
                }
            }

        }
    }

    @Override
    public List<Node> getChildren(String name) {
        List<Node> children = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            for (Node child : nodes) {
                if (child.getName().equals(name)) {
                    children.add(child);
                } else if (child instanceof Directory) {
                    children.addAll(child.getChildren(name));
                }
            }
        }
        return children;
    }

    @Override
    public long getSize() {
        long total = 0;
        for (Node child : nodes) {
            total += child.getSize();
        }
        size = total;
        return size;
    }

    public int getNumberOfNodes() {
        return nodes.size();
    }

    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 67 * hash + Objects.hashCode(this.nodes);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final Directory other = (Directory) obj;
        if (!Objects.equals(this.nodes, other.nodes)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public String toString() {
        return "d" + super.toString();
    }

    /**
     * @return long listing of directories
     */
    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append("d").append(super.toString()).append("\n");
        Iterator<Node> iterator = iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString()).append("\n");
        }
        return sb.toString();
    }

}
