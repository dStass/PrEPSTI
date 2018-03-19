/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent;

/**
 *
 * @author Michael Walker
 */
public class RiskyMSM extends MSM
{
    // probability of choosing each Relationship subclass
    // RiskyMSM taken to have more Casual Relationships and less Monogomous
    // odds of Monogomous, Regular and Casual
    int monogomousOdds = rand.nextInt(10) ;
    int regularOdds = 2 * rand.nextInt(10 - monogomousOdds) ;
    int casualOdds = 5 * (10 - monogomousOdds - regularOdds) ;

    // TODO: Check that these fields and not those of the MSM superclass
    //are used by MSM.initStatus()
    
    // The maximum number of relationships an agent may be willing to sustain
    static int maxRelationships = 25;
    
    // Associated probabilities for the above
    static double probabilityRequireDiscloseHIV = 0.1 ;
    static double probabilityHIV = 0.1 ;
    static double antiViralProbability = 0.5 ;
    static double probabilityDiscloseHIV = 0.2 ;
    static double probabilityPrep = 0.1 ;

    public RiskyMSM(int startAge){
        super(startAge) ;
    }
    
}
