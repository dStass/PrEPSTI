/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests.agentTest;

import agent.* ;

/**
 *
 * @author Michael Walker
 */
public class MsmTest {
    
    // MSMs for testing
    MSM msm0 ;
    MSM msm1 ;
    
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("msmTest") ;
    
    public MsmTest(int startAge0, int startAge1)
    {
        msm0 = new MSM(startAge0) ;
        msm1 = new MSM(startAge1) ;
        return ;
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
                
        return ;

    }
    
    public void testGetScreenProbability()
    {
        boolean testResult0 ;
        boolean testResult1 ;
        
        // Test for MSM on PrEP
        msm0.setPrepStatus(true) ;
        // always test on screening day
        int prepScreenCycle = 3 * MSM.getScreenCycle() ;
        String[] testArgs = {Integer.toString(prepScreenCycle)} ;
        assert (msm0.getScreenProbability(testArgs) == 1.0 ) : 
                "MSM on PrEP failed to screen on screening day" ;
        // never screen otherwise
        String[] testArgs1 = {Integer.toString(prepScreenCycle) + 1} ;
        assert (msm0.getScreenProbability(testArgs1) == 0.0 ) :
                "MSM on PrEP screened on non-screening day";
            
        // Non-PrEP users
        msm0.setPrepStatus(false);
        boolean testNonPrep = (0 < msm0.getScreenProbability(testArgs1)) ;
        testNonPrep = (testNonPrep && (msm0.getScreenProbability(testArgs1) < 1)) ;
        assert (testNonPrep) : "NonPrep MSM gave invalid screenProbability" ;
        
        return ;
        
        // Non-prep users 
        
    }
    
private void setStatusHIV(boolean statusHIV0, boolean statusHIV1)
    {
        msm0.setStatusHIV(statusHIV0) ;
        msm1.setStatusHIV(statusHIV1) ;
        return ;
    }

private void setDiscloseStatusHIV(boolean disclose0, boolean disclose1)
{
    msm0.setDiscloseStatusHIV(disclose0) ;
    msm1.setDiscloseStatusHIV(disclose1) ;
    return ;
}
    
    private void setSeroPosition(boolean position0, boolean position1)
    {
        msm0.setSeroPosition(position0) ;
        msm1.setSeroPosition(position1) ;
        return ;
    }
    
    private void setSeroSort(boolean sort0, boolean sort1)
    {
        msm0.setSeroSort(sort0) ;
        msm1.setSeroSort(sort1) ;
        return ;
    }
    
}
