package org.jcrete.bitterjava8.visitor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ikost
 */
public class FileSystemTest {
    private FileSystem fs;

    @Before
    public void setUp() {
        Node root = new Directory("/");         // /
        fs = new FileSystem(root);
        Node bin = new Directory("bin");
        root.add(bin);                          // /bin
        Node home = new Directory("home");
        root.add(home);                         // /home
        Node tmp = new Directory("tmp");
        root.add(tmp);                          // /tmp
        final File ls = new File("ls");
        bin.add(ls);                            // /bin/ls
        home.add(new Directory("john"));        // /home/john
        home.add(new Directory("nick"));        // /home/nick
        File junk = new File("junk");
        junk.setContents(FileTest.lorem);
        tmp.add(junk);                          // /tmp/junk
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getRoot method, of class FileSystem.
     */
    @Test
    public void testGetRoot() {
        System.out.println("getRoot");
        Node root = fs.getRoot();
        assertNotNull(root);
        assertEquals("/", root.getName());
        assertEquals(FileTest.lorem.length(), root.getSize());
    }

    /**
     * Test of find method, of class FileSystem.
     */
    @Test
    public void testFind() {
        System.out.println("find");
        Node result = new Directory("john");
        assertEquals(result, fs.find("john", fs.getRoot()).get(0));
        assertEquals(result, fs.find("john", fs.find("home", fs.getRoot()).get(0)).get(0));
        Node ls = new File("ls");
        assertEquals(ls, fs.find("ls", fs.getRoot()).get(0));
    }

    /**
     * Test of mkdir method, of class FileSystem.
     */
    @Test
    public void testMkdir() {
        System.out.println("mkdir");
        fs.mkdir((Directory) fs.getRoot(), "test/sub1/sub2/");
        Node test = new Directory("test");
        Node sub1 = new Directory("sub1");
        Node sub2 = new Directory("sub2");
        sub1.add(sub2);
        test.add(sub1);
        assertEquals(test, fs.find("test", fs.getRoot()).get(0));
        assertEquals(sub1, fs.find("sub1", fs.getRoot()).get(0));
        assertEquals(sub2, fs.find("sub2", fs.getRoot()).get(0));
    }

    /**
     * Test of getSize method, of class FileSystem.
     */
    @Test
    public void testGetSize() {
        System.out.println("getSize");
        assertEquals(FileTest.lorem.length(), fs.getSize());
    }
    
       /**
     * Test of wc method, of class FileSystem.
     */
    @Test
    public void testWc() {
        System.out.println("wc");
        File file = (File) fs.find("junk", fs.getRoot()).get(0);
        assertEquals(file.toString() + " has 25 words", fs.wc(file));
    }

    /**
     * Test of cat method, of class FileSystem.
     */
    @Test
    public void testCat() {
        System.out.println("cat");
        File file = (File) fs.find("junk", fs.getRoot()).get(0);
        assertEquals(file.toString() + "\n" + FileTest.lorem, fs.cat(file));
    }

    /**
     * Test of list method, of class FileSystem.
     */
    @Test
    public void testList() {
        System.out.println("list");
        File file = (File) fs.find("junk", fs.getRoot()).get(0);
        assertEquals(file.toString() + "\n", fs.list(file));   
        Directory dir = (Directory) fs.find("john", fs.getRoot()).get(0);
        assertEquals(dir.toString() + "\n", fs.list(dir));        
    }    


}
