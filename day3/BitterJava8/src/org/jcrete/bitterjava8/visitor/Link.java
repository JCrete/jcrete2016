package org.jcrete.bitterjava8.visitor;

import java.util.List;

/**
 * A Link can point to both {@code File}s and {@code Directories}.
 *
 * @author ikost
 */
@org.jpatterns.gof.VisitorPattern.ConcreteElement
public class Link extends Node {

    private final Node subject;

    public Link(Node subject) {
        super(subject.getName());
        this.subject = subject;
    }

    public Node getSubject() {
        return subject;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public void add(Node child) {
        subject.add(child);
    }

    @Override
    public void remove(Node child) {
        subject.remove(child);
    }

    @Override
    public List<Node> getChildren(String name) {
        return subject.getChildren(name);
    }

    @Override
    public String toString() {
        return "l" + subject.toString();
    }

}
