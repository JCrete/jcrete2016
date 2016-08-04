package org.jcrete.bitterjava8.visitor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ikost
 */
public class FileTest {

    private File file;
    public static final String lorem = "Lorem ipsum dolor rutur amet.\n"
            + "Integer id dui sed odio imperd feugiat et nec ipsum.\n"
            + "Ut rutrum massa non ligula facilisis in ullamcorper purus dapibus.";

    @Before
    public void setUp() {
        file = new File("junk");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of equals method, of class File.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        File f = new File("junk");
        assertTrue(file.equals(f));
        f.setContents(lorem);
        assertFalse(file.equals(f));
        file.setContents(lorem);
        assertTrue(file.equals(f));
        File f1 = new File("yankee");
        assertFalse(file.equals(f1));
    }

    /**
     * Test of hashCode method, of class File.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        File f = new File("junk");
        assertEquals(file.hashCode(), f.hashCode());
        f.setContents(lorem);
        assertFalse(file.hashCode() == f.hashCode());
        file.setContents(lorem);
        assertEquals(file.hashCode(), f.hashCode());
        File f1 = new File("yankee");
        assertFalse(file.hashCode() == f1.hashCode());
    }

    /**
     * Test of contents methods, of class File.
     */
    @Test
    public void test() {
        System.out.println("testFile");
        assertTrue(Node.class.isAssignableFrom(file.getClass()));
        assertTrue(file.isLeaf());
        assertEquals("junk", file.getName());
        assertEquals("", file.getContents());
        assertEquals(0, file.getSize());
        assertEquals(" junk 0", file.toString());
        file.setContents(lorem);
        assertEquals(lorem, file.getContents());
        assertEquals(lorem.length(), file.getSize());
        assertEquals(" junk " + lorem.length(), file.toString());
        file.setContents("");
        assertEquals("", file.getContents());
        assertEquals(0, file.getSize());
        assertEquals(" junk 0", file.toString());
    }
    
   /**
     * Test of null name, of class File.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        System.out.println("testNullName");
        File test = new File(null);
    }

    /**
     * Test of empty name, of class File.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyName() {
        System.out.println("testEmptyName");
        File test = new File("");
    }

    /**
     * Test of empty name, of class File.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptySetName() {
        System.out.println("testEmptySetName");
        File test = new File("f");
        test.setName("");
    }

    /**
     * Test of null name, of class File.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullSetName() {
        System.out.println("testNullSetName");
        File test = new File("f");
        test.setName(null);
    }
}
