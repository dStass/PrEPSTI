/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package community.community;

import agent.Agent;
import community.Relationship;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author MichaelWalker
 */
public class RelationshipTest {
    
    public RelationshipTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of encounter method, of class Relationship.
     */
    @Test
    public void testEncounter() throws Exception {
        System.out.println("encounter");
        Relationship instance = new Relationship();
        String expResult = "";
        //String result = instance.encounter();
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getReport method, of class Relationship.
     */
    @Test
    public void testGetReport() {
        System.out.println("getReport");
        Relationship instance = new Relationship();
        String expResult = "";
        String result = instance.getReport();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
