/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.agent;

import agent.Agent;
import agent.MSM;
//import agent.SafeMSM ;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import site.Urethra ;
import site.Pharynx ;
import site.Rectum ;
import site.Site ;

/**
 *
 * @author MichaelWalker
 */
public class MsmTest {
    
    public MsmTest() {
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

        // MSMs for testing
    MSM msm0 ;
    MSM msm1 ;
    
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("msmTest") ;
    
    public MsmTest(int startAge0, int startAge1)
    {
        msm0 = new MSM(startAge0) ; // BIRTH_MSM
        msm1 = new MSM(startAge1) ; // BIRTH_MSM
        int msmId0 = msm0.getAgentId() ;
        int msmId1 = msm1.getAgentId() ;
        assert (msmId1 == (msmId0 + 1)) : "msm0 Id:" + String.valueOf(msmId0) +
                "msm1 Id:" + String.valueOf(msmId1) ;
    }
    
    public void testDeclareStatus()
    {
        String declare0 ;
        String declare1 ;
        
        setStatusHIV(false,true) ;
        setDiscloseStatusHIV(true,false) ;
        
        declare1 = msm1.declareStatus() ;
        assert "none".equals(declare1) : "msm1.declareStatus() failed to yield 'none'" ;
        
        msm1.setDiscloseStatusHIV(true);
        declare0 = msm0.declareStatus() ;
        declare1 = msm1.declareStatus() ;
        assert "false".equals(declare0) : "msm0.declareStatus() failed to yield 'false'" ;
        assert "true".equals(declare1) : "msm1.declareStatus() failed to yield 'true'" ;
    }
    
    /**
     * Tests that consent is correctly given or refused, depending on Relationship
     * type, statusHIV, willingness to disclose (discloseStatusHIV), and serosort 
     * and seropositioning parameters.
     */
    public void testConsent()
    {
        boolean consent0 ;
        boolean consent1 ;
        String relationshipClazzName = "Regular" ;
        
        // Initialise status
        setStatusHIV(false,false) ;
        setDiscloseStatusHIV(false,false) ;
        setSeroSort(false,false) ;
        setSeroPosition(false,false) ;
        
        // Test 1
        consent0 = msm0.consent(relationshipClazzName, msm1) ;
        consent1 = msm1.consent(relationshipClazzName, msm0) ;
        assert (consent0 && consent1) : "msm.consent() yields 'false' when it should yield 'true'. Test 1" ;

        // Test 2        
        setSeroPosition(true,false);
        consent0 = msm0.consent(relationshipClazzName, msm1) ;
        consent1 = msm1.consent(relationshipClazzName, msm0) ;
        assert (!consent0) : "msm0.consent() yields 'true' when it should field 'false' Test 2" ;
        assert (consent1) : "msm1.consent() yields 'false' when it should yield 'true'. Test 2" ;
        
        // Test 3        
        // seroPosition(true,false) ;
        msm1.setStatusHIV(true) ;
        msm1.setDiscloseStatusHIV(true);
        consent0 = msm0.consent(relationshipClazzName, msm1) ;
        consent1 = msm1.consent(relationshipClazzName, msm0) ;
        assert (consent0) : "msm0.consent() yields 'false' when it should yield 'true'. Test 4" ;
        assert (consent1) : "msm1.consent() yields 'false' when it should yield 'true'. Test 4" ;
        
        // Test 5
        setSeroPosition(false,false) ;
        setSeroSort(true,false) ;
        // msm1.setStatusHIV(true) ;
        // msm1.discloseStatusHIV == true ;
        consent0 = msm0.consent(relationshipClazzName, msm1) ;
        consent1 = msm1.consent(relationshipClazzName, msm0) ;
        assert (! consent0) : "msm0.consent() yields 'true' when it should yield 'false'. Test 5" ;
        assert (consent1) : "msm1.consent() yields 'false' when it should yield 'true'. Test 5" ;
        
        // Test 6
        //msm0.setSeroPosition(false) ;
        //msm0.setSeroSort(true) ;
        setStatusHIV(false,false) ;
        msm1.setDiscloseStatusHIV(false) ;
        consent0 = msm0.consent(relationshipClazzName, msm1) ;
        consent1 = msm1.consent(relationshipClazzName, msm0) ;
        assert (! consent0) : "msm0.consent() yields 'true' when it should yield 'false'. Test 6" ;
        assert (consent1) : "msm1.consent() yields 'false' when it should yield 'true'. Test 6" ;
        
        // Test 7
        //msm0.setSeroPosition(false) ;
        msm0.setSeroSort(false) ;
        msm1.setStatusHIV(true) ;
        msm1.setDiscloseStatusHIV(false) ;
        consent0 = msm0.consent(relationshipClazzName, msm1) ;
        consent1 = msm1.consent(relationshipClazzName, msm0) ;
        assert (! consent0) : "msm0.consent() yields 'false' when it should yield 'true'. Test 7" ;
        assert (consent1) : "msm1.consent() yields 'false' when it should yield 'true'. Test 7" ;
        
        // Test 8
        //msm0.setSeroPosition(false) ;
        //msm0.setSeroSort(true) ;
        msm0.setStatusHIV(true);
        //msm1.setStatusHIV(true) ;
        msm1.setDiscloseStatusHIV(false) ;
        consent0 = msm0.consent(relationshipClazzName, msm1) ;
        consent1 = msm1.consent(relationshipClazzName, msm0) ;
        assert (! consent0) : "msm0.consent() yields 'true' when it should yield 'false'. Test 8" ;
        assert (consent1) : "msm1.consent() yields 'false' when it should yield 'true'. Test 8" ;
        
        // Test 9
        //msm0.setSeroPosition(false) ;
        //msm0.setSeroSort(true) ;
        //msm0.setStatusHIV(true);
        //msm1.setStatusHIV(true) ;
        setDiscloseStatusHIV(false,true) ;
        consent0 = msm0.consent(relationshipClazzName, msm1) ;
        consent1 = msm1.consent(relationshipClazzName, msm0) ;
        assert (consent0) : "msm0.consent() yields 'false' when it should yield 'true'. Test 8" ;
        assert (consent1) : "msm1.consent() yields 'false' when it should yield 'true'. Test 8" ;
        
        LOGGER.info("msmTest.testConsent() passed successfully") ;           
    }
    
    /**
     * Check that properties are properly adjusted when statusHIV is set.
     */
//    public void testSetStatus()
//    {
//        msm0.setStatusHIV(true);
//        assert (msm0.getStatusHIV()) : "msm0.getStatusHIV() should be 'true'. Test 1" ;
//        assert (!msm0.getPrepStatus()) : "msm0.getStatusHIV() should be 'false'. Test 2" ;
//        
//        msm1.setStatusHIV(false);
//        assert (!msm1.getStatusHIV()) : "msm1.getStatusHIV() should be 'false'. Test 3" ;
//        assert (!msm0.getAntiViralStatus()) : "msm1.getAntiViralStatus() should be 'false'. Test 4" ;
//    }
//    
    /**
     * Test that Sites are chosen with proper accounting for relevant factors
     * such as seroPositioning and statusHIV.
     * 
     * The (String) parameter in MSM.CHOOSE_SITES is the Class name of the 
 Relationship, which is currently unused.
     */
    public void testChooseSites()
    {
        setStatusHIV(true,false) ;
        
        msm0.setSeroPosition(true);
        Site[] sites = MSM.CHOOSE_SITES(msm0, msm1, "") ;
        
        assert (Urethra.class.isInstance(sites[0])) : "Site chosen for msm0 must not be Urethra." ;
    }
    
    /**
     * Method to set statusHIV of both MSMs for above tests.
     * @param statusHIV0
     * @param statusHIV1 
     */    
    private void setStatusHIV(boolean statusHIV0, boolean statusHIV1)
        {
            msm0.setStatusHIV(statusHIV0) ;
            msm1.setStatusHIV(statusHIV1) ;
        }

    /**
     * Method to set discloseStatusHIV of both MSMs for above tests
     * @param disclose0
     * @param disclose1 
     */
    private void setDiscloseStatusHIV(boolean disclose0, boolean disclose1)
    {
        msm0.setDiscloseStatusHIV(disclose0) ;
        msm1.setDiscloseStatusHIV(disclose1) ;
    }

    /** 
     * Method to set seroPosition of both MSMs for above tests.
     * @param position0
     * @param position1 
     */
    private void setSeroPosition(boolean position0, boolean position1)
    {
        msm0.setSeroPosition(position0) ;
        msm1.setSeroPosition(position1) ;
    }
    
    /**
     * Method to set seroSort of both MSMs for above tests
     * @param sort0
     * @param sort1 
     */
    private void setSeroSort(boolean sort0, boolean sort1)
    {
        msm0.setSeroSort(sort0) ;
        msm1.setSeroSort(sort1) ;
    }
    
//    public class SafeMSMImpl extends SafeMSM {
//
//        public SafeMSMImpl() {
//            super(0);
//        }
//
//        public int getMaxRelationships() {
//            return 0;
//        }
//
//        public double getProportionHIV() {
//            return 0.0;
//        }
//
//        public double getProbabilityDiscloseHIV() {
//            return 0.0;
//        }
//
//        public double getProbabilityPrep() {
//            return 0.0;
//        }
//
//        public boolean chooseCondom(Agent msm) {
//            return false;
//        }
//
//        public double getJoinOrgyProbability() {
//            return 0.0;
//        }
//    }
    
}
