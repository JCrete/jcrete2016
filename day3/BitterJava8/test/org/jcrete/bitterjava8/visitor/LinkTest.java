package org.jcrete.bitterjava8.visitor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author ikost
 */
public class LinkTest {

    private Link linkToFile, linkToDirectory;
    private File file;
    private Directory dir;

    @Before
    public void setUp() {
        file = new File("junk");
        linkToFile = new Link(file);
        dir = new Directory("dir");
        linkToDirectory = new Link(dir);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getSubject method, of class Link.
     */
    @Test
    public void testGetSubject() {
        System.out.println("getSubject");
        assertEquals(file, linkToFile.getSubject());
        assertEquals(dir, linkToDirectory.getSubject());
    }

    /**
     * Test of getSize method, of class Link.
     */
    @Test
    public void testGetSize() {
        System.out.println("getSize");
        assertEquals(0, linkToDirectory.getSize());
        assertEquals(0, linkToFile.getSize());
    }

    /**
     * Test of add/remove methods, of class Link.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddRemove() {
        System.out.println("add/remove");
        linkToDirectory.add(file);
        assertEquals(file, linkToDirectory.getChildren("junk").get(0));
        assertEquals(0, linkToDirectory.getSize());
        linkToDirectory.remove(file);
        assertNull(linkToDirectory.getChildren("junk").get(0));
        assertEquals(0, linkToDirectory.getSize());
        linkToFile.add(dir);   // UnsupportedOperationException
        assertEquals(0, linkToFile.getSize());
        assertNull(linkToFile.getChildren("dir").get(0));
    }

    /**
     * Test of toString method, of class Link.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals("ld dir 0", linkToDirectory.toString());
        assertEquals("l junk 0", linkToFile.toString());
    }

}
