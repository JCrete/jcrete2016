package org.jcrete.bitterjava8.visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ikost
 */
@org.jpatterns.gof.VisitorPattern.ConcreteVisitor
public class FileSystem {

    private Node root;
    private final VisitorLambda<String> visitor = new VisitorLambda<>(); // Void throws void cannot be converted to Void

    public FileSystem(Node root) {
        if (root != null) {
            this.root = root;
        }
    }

    public Node getRoot() {
        return root;
    }

    public long getSize() {
        return getRoot().getSize();
    }

    /**
     * Finds zero or more nodes with the given name.
     *
     * @param name node to find
     * @param current current node
     * @return list of nodes found with this name
     */
    public List<Node> find(String name, Node current) {
        List<Node> nodesFound = new ArrayList<>();
        if (name != null && !name.isEmpty() && current != null) {
            if (name.equals(current.getName())) {
                nodesFound.add(current);
            }
            if (!current.isLeaf()) {
                nodesFound.addAll(((Directory) current).getChildren(name));
            }
        }
        return nodesFound;
    }

    /**
     * Creates a new directory, under parent. If intermediate directories do not
     * exist, they are created. Functionality similar to UNIX
     * {@code mkdir -p <path>} command.
     *
     * @param parent to add the new directories
     * @param path of the new directories to create; if intermediate directories
     * do not exist, they are created
     */
    public void mkdir(Directory parent, String path) {
        if (parent == null || path == null || path.isEmpty()) {
            return;
        }
        final int indexOfPathSeparator = path.indexOf("/");
        String subpath = (indexOfPathSeparator > -1) ? path.substring(indexOfPathSeparator) : path;
        if (subpath.isEmpty()) {
            parent.add(new Directory(path));
        } else {
            boolean finished = false;
            String name = (indexOfPathSeparator > -1) ? path.substring(0, indexOfPathSeparator) : path;
            if (name.isEmpty()) {
                name = path.substring(1);
                finished = true;
            } else if (indexOfPathSeparator == -1) {
                finished = true;
            }
            List<Node> children = find(name, parent);
            if (children.isEmpty()) {
                Node child = new Directory(name);
                parent.add(child);
                if (!finished) {
                    mkdir((Directory) child, subpath.substring(1));
                }
            } else {
                throw new IllegalArgumentException(name + " already exists.");
            }
        }
    }

    /**
     * Count the words of a text file.
     *
     * @param node a (text) file or a link to a text file to count the number of
     * words.
     */
    private long wc(String text) {
        long wc = 0;
        String[] lines = text.split("\n");
        for (String line : lines) {
            wc += line.split("\\s+").length;
        }
        return wc;
    }

    /**
     * Count the words of a text file (equivalent to Unix {@code wc} command).
     *
     * @param node a (text) file or a link to a (text) file to count the number
     * of words for.
     * @return word count of file
     * @throws UnsupportedOperationException
     */
    public String wc(Node node) {
        return visitor.when(File.class, file -> file.toString() + " has " + wc(file.getContents()) + " words")
                .when(Directory.class, dir -> {
                    throw new UnsupportedOperationException("Can't word count a directory.");
                })
                .when(Link.class, link -> wc(link.getSubject()))
                .visit(node);
    }

    /**
     * Displays the contents of a text file (equivalent to Unix {@code cat}
     * command).
     *
     * @param node to display its contents (a text file or a link to a text
     * file).
     * @return contents of file
     * @throws UnsupportedOperationException
     */
    public String cat(Node node) {
        return visitor.when(File.class, file -> file.toString() + "\n" + file.getContents())
                .when(Directory.class, dir -> {
                    throw new UnsupportedOperationException("Can't cat a directory.");
                })
                .when(Link.class, link -> cat(link.getSubject()))
                .visit(node);
    }

    /**
     * Lists a node (equivalent to Unix {@code ls} command).
     *
     * @param node to display its name and size and in case of a directory, its
     * children.
     * @return list of file/directory
     */
    public String list(Node node) {
        return visitor.when(File.class, file -> file.toString() + "\n")
                .when(Directory.class, dir -> dir.toLongString())
                .when(Link.class, link -> link.toString() + "\n")
                .visit(node);
    }

}
