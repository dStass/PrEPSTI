/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package community.community;

import agent.* ;
import site.* ;
import reporter.Reporter ;
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
        
        MSM msm0 = new RiskyMSM(20) ;
        for (Site site : msm0.getSites())
        {
            if ("Urethra".equals(site.getSite()))
                msm0.receiveInfection(1.1,site) ;
            else
                site.treat();
        }
        
        MSM msm1 = new SafeMSM(30) ;
        for (Site site : msm1.getSites())
        {
            if ("Urethra".equals(site.getSite()))
                msm0.receiveInfection(1.1,site) ;
            else
                site.treat();
        }
        
        String result = instance.testEncounter();
        
        System.out.println(result);
        
        if (result.indexOf("Urethra") < 0)
            assertEquals(-1,result.indexOf("transmission:")) ;
        else
            assert(!Reporter.extractAllValues("Urethra", result, 0).contains("0")) ;
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getReport method, of class Relationship.
     */
    @Test
    public void testGetReport() {
        System.out.println("getReport");
        Relationship instance = new Relationship();
        String expResult = "";
        String result = instance.getRecord();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
