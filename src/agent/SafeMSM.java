/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent;

/**
 * 
 * @author MichaelWalker
 */
public class SafeMSM extends MSM{
    
    // TODO: Check that these fields and not those of the MSM superclass
    //are used by MSM.initStatus()
    
    // The maximum number of relationships an agent may be willing to sustain
    static int maxRelationships = 5;
    
    // Associated probabilities for the above
    static double probabilityRequireDiscloseHIV = 0.5 ;
    static double probabilityHIV = 0.02 ;
    static double antiViralProbability = 0.9 ;
    static double probabilityDiscloseHIV = 0.8 ;
    static double probabilityPrep = 0.5 ;

    public SafeMSM(int startAge){
        super(startAge) ;
    }
    
}
