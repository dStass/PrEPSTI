/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package community.community;

import agent.* ;
import site.* ;
import reporter.Reporter ;
import community.* ;
import java.lang.reflect.InvocationTargetException;
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
     * @throws java.lang.Exception
     */
    @Test
    public void testEncounter() throws Exception{
        System.out.println("encounter");
        Relationship instance = new Relationship();
        String expResult = "";
        
        for (String checkSiteName : MSM.SITE_NAMES)
        {
            // MSMs both infected at Urethra only
            MSM msm0 = new RiskyMSM(20) ;
            for (Site site : msm0.getSites())
            {
                if (checkSiteName.equals(site.getSite()))
                    msm0.receiveInfection(1.1,site) ;    // Must receive infection, +0.1 margin against round-off
                else
                    site.treat();
            }

            MSM msm1 = new SafeMSM(30) ;
            for (Site site : msm1.getSites())
            {
                if (checkSiteName.equals(site.getSite()))
                    msm1.receiveInfection(1.1,site) ;    // Must receive infection, +0.1 margin against round-off
                else
                    site.treat();
            }

            String result = instance.testEncounter();

            System.out.println(result);

            if (!result.contains(checkSiteName))    // checkSiteName not mentioned in result
                assertEquals(-1,result.indexOf("transmission:")) ;    // implies no transmission
            else    // Urethra is mentioned in result
                assert(!Reporter.EXTRACT_ALL_VALUES(checkSiteName, result, 0).contains("0")) : "checkSiteName must be infected" ;
            assertEquals(expResult, result);
        }
    }
        
    @Test
    public void testRelationship() throws Exception
    {
        Agent agent0 = new SafeMSM(20) ;
        Agent agent1 = new SafeMSM(19) ;
        
        Casual casual = new Casual(agent0,agent1) ;
        
        assert(agent0.getCurrentRelationships().size() == 1) : "agent0 should have exactly one Relationship" ;
        assert(agent1.getCurrentRelationships().size() == 1) : "agent1 should have exactly one Relationship" ;
        
        assert(agent0.getCurrentRelationships().contains(casual)) : "agent0.currentRelationships should have (Casual) casual" ;
        assert(agent1.getCurrentRelationships().contains(casual)) : "agent1.currentRelationships should have (Casual) casual" ;
        
        assert(agent0.getCurrentPartnerIds().size() == 1) : "agent0 should have exactly one partner" ;
        assert(agent1.getCurrentPartnerIds().size() == 1) : "agent1 should have exactly one partner" ;
        
        assert(agent0.getCurrentPartnerIds().contains(agent1.getAgentId())) : "agent0 should have agent1 as partner" ;
        assert(agent1.getCurrentPartnerIds().contains(agent0.getAgentId())) : "agent0 should have agent1 as partner" ;
        
        agent0.leaveRelationship(casual) ;
        agent1.leaveRelationship(casual) ;
        
        assert(agent0.getCurrentRelationships().isEmpty()) : "agent0 should have no Relationships" ;
        assert(agent1.getCurrentRelationships().isEmpty()) : "agent1 should have no Relationships" ;
        assert(agent0.getCurrentPartnerIds().isEmpty()) : "agent0 should have no current partners" ;
        assert(agent1.getCurrentPartnerIds().isEmpty()) : "agent1 should have no current partners" ;
        
        // TODO: Sort out permissions to test Relaitonship.addAgents(agent0,agent1) 
    }
    

}
