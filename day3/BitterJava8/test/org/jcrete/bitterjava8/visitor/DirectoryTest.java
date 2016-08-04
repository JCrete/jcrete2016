package org.jcrete.bitterjava8.visitor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ikost
 */
public class DirectoryTest {

    private Directory dir;

    @Before
    public void setUp() {
        dir = new Directory("test");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of add method, of class Directory.
     */
    @Test
    public void testDirectory() {
        System.out.println("directory");
        assertTrue(Node.class.isAssignableFrom(dir.getClass()));
        assertEquals("test", dir.getName());
        assertEquals(0, dir.getSize());
        assertEquals("d test 0", dir.toString());
    }

    /**
     * Test of add method, of class Directory.
     */
    @Test
    public void testAddRemove() {
        System.out.println("add/remove");
        // add a directory
        Directory dir1 = new Directory("dir1");
        dir.add(dir1);
        assertEquals(0, dir.getSize());
        assertEquals(0, dir1.getSize());
        assertEquals(dir1, dir.getChildren("dir1").get(0));
        assertEquals(1, dir.getNumberOfNodes());
        assertEquals("d test 0", dir.toString());
        // add a file
        File file = new File("junk");
        dir.add(file);
        assertEquals(0, dir.getSize());
        assertEquals(0, file.getSize());
        assertEquals(dir1, dir.getChildren("dir1").get(0));
        assertEquals(file, dir.getChildren("junk").get(0));
        assertEquals(2, dir.getNumberOfNodes());
        file.setContents(FileTest.lorem);
        assertEquals(FileTest.lorem.length(), dir.getSize());
        assertEquals(FileTest.lorem.length(), file.getSize());
        assertEquals("d test " + FileTest.lorem.length(), dir.toString());
        // remove the directory
        dir.remove(dir1);
        assertEquals(FileTest.lorem.length(), dir.getSize());
        assertEquals(1, dir.getNumberOfNodes());
        assertEquals(file, dir.getChildren("junk").get(0));
        assertTrue(dir.getChildren("dir1").isEmpty());
        assertEquals("d test " + FileTest.lorem.length(), dir.toString());
        // remove the file
        dir.remove(file);
        assertEquals(0, dir.getSize());
        assertEquals(0, dir.getNumberOfNodes());
        assertTrue(dir.getChildren("junk").isEmpty());
        assertTrue(dir.getChildren("dir1").isEmpty());        
        assertEquals("d test 0", dir.toString());
    }

    /**
     * Test of getSize method, of class Directory.
     */
    @Test
    public void testGetSize() {
        System.out.println("getSize");
        assertEquals(0, dir.getSize());
    }

    /**
     * Test of hashCode method, of class Directory.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        Directory dir1 = new Directory("test");
        assertEquals(dir1.hashCode(), dir.hashCode());
        Directory d1 = new Directory("d1");
        dir.add(d1);
        assertFalse(dir1.hashCode() == dir.hashCode());
        dir1.add(d1);
        assertEquals(dir1.hashCode(), dir.hashCode());
        File f = new File("f");
        f.setContents(FileTest.lorem);
        dir.add(f);
        assertFalse(dir1.hashCode() == dir.hashCode());
        dir1.add(f);
        assertEquals(dir1.hashCode(), dir.hashCode());         }

    /**
     * Test of equals method, of class Directory.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Directory dir1 = new Directory("test");
        assertEquals(dir1, dir);
        Directory d1 = new Directory("d1");
        dir.add(d1);
        assertFalse(dir1.equals(dir));
        dir1.add(d1);
        assertEquals(dir1, dir);
        File f = new File("f");
        f.setContents(FileTest.lorem);
        dir.add(f);
        assertFalse(dir1.equals(dir));
        dir1.add(f);
        assertEquals(dir1, dir);
    }

    /**
     * Test of isLeaf method, of class Directory.
     */
    @Test
    public void testIsLeaf() {
        System.out.println("isLeaf");
        assertFalse(dir.isLeaf());
    }
    
  /**
     * Test of null name, of class Directory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        System.out.println("testNullName");
        Directory test = new Directory(null);
    }

    /**
     * Test of empty name, of class Directory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyName() {
        System.out.println("testEmptyName");
        Directory test = new Directory("");
    }

    /**
     * Test of empty name, of class Directory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptySetName() {
        System.out.println("testEmptySetName");
        Directory test = new Directory("f");
        test.setName("");
    }

    /**
     * Test of null name, of class Directory.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullSetName() {
        System.out.println("testNullSetName");
        Directory test = new Directory("f");
        test.setName(null);
    }    

}
