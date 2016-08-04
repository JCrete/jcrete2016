package org.jcrete.bitterjava8.visitor;

import java.util.Objects;

/**
 * A File is a leaf and doesn't contain children.
 *
 * @author ikost
 */
@org.jpatterns.gof.VisitorPattern.ConcreteElement
public class File extends Node {

    private StringBuilder contents = new StringBuilder();

    public File(String name) {
        super(name);
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
        final File other = (File) obj;
        if (!this.contents.toString().equals(other.contents.toString())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(this.contents.toString());
        return hash;
    }

    public void addContents(String cont) {
        contents.append(cont);
        size = contents.length();
    }

    public void setContents(String cont) {
        contents = new StringBuilder(cont);
        size = contents.length();
    }

    public String getContents() {
        return contents.toString();
    }

}
