package org.jcrete.bitterjava8.visitor;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

/**
 * The common abstraction of {@code File} and {@code Directory}.
 *
 * @author ikost
 */
@org.jpatterns.gof.VisitorPattern.Element
public abstract class Node {

    protected long size = 0;
    protected String name = "New File-Directory";  // default name of file/directory
//    protected static Node current;

    protected Node(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
//        current = this;
    }

    public void add(Node child) {
        throw new UnsupportedOperationException("Not a directory.");
    }

    protected void remove(Node child) {
        throw new UnsupportedOperationException(child.name + " not found.");
    }

 /**
    * @param name
    * @return list of nodes with given name
    */
   protected List<Node> getChildren(String name) {
        throw new UnsupportedOperationException("Not a directory.");
    }

    /** @return size in kb */
    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
    }

//    public static Node getCurrent() {
//        return current;
//    }
    /**
     * 
     * @param is 
     * @throws java.lang.IllegalAccessException 
     */
    public void streamIn(InputStream is) throws IllegalAccessException {
        if (is != null /*&& isWritable()*/) {
            System.out.println("Write...");
            // doStreamIn(is);
        } else {
            throw new IllegalAccessException("You don't have write permission.");
        }
    }

    /**
     * 
     * @param os
     * @throws IllegalAccessException 
     */
    public void streamOut(OutputStream os) throws IllegalAccessException {
        if (os != null /*&& isReadable()*/) {
            System.out.println("Read...");
            // doStreamOut(os);
        } else {
            throw new IllegalAccessException("You don't have read permission.");
        }
    }

    public boolean isLeaf() {
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (this.size ^ (this.size >>> 32));
        hash = 79 * hash + Objects.hashCode(this.name);
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
        final Node other = (Node) obj;
        if (this.size != other.size) {
            return false;
        }
        if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return " " + name + " " + size;
    }

}
