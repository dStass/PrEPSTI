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
    int monogomousOdds = RAND.nextInt(10) ;
    int regularOdds = 2 * RAND.nextInt(10 - monogomousOdds) ;
    int casualOdds = 5 * (10 - monogomousOdds - regularOdds) ;

    // The maximum number of relationships an agent may be willing to sustain
    static int MAX_RELATIONSHIPS = 25;
    
    //static double probabilityRequireDiscloseHIV = 0.5 ;
    
    // The probability of positive HIV status
    static double PROBABILITY_HIV = 0.02 ;
    
    // The probability of being on antivirals, given positive HIV status
    static double ANTIVIRAL_PROBABILITY = 0.9 ;
    
    // The probability of disclosing HIV status
    static double PROBABILITY_DISCLOSE_HIV = 0.4 ;
    
    // The probability of being on PrEP, given negative HIV status
    static double PROBABILITY_PREP = 0.5 ;

    // The probability of serosorting
    static double PROBABILITY_SERO_SORT = 0.4 ;
    // The probability of seropositioning
    static double PROBABILITY_SERO_POSITION = 0.4 ;
    
    //static double probabilityRequireDiscloseHIV = 0.05 ;
    
    
    // probability of joining an orgy if invited
    static double JOIN_ORGY_PROBABILITY = 0.4 ;
    
    public RiskyMSM(int startAge){
        super(startAge) ;
    }
    
    
    protected int getMaxRelationships()
    {
        return MAX_RELATIONSHIPS ;
    }
    
    protected double getProbabilityHIV()
    { 
        return PROBABILITY_HIV ;
    }
    
    protected double getAntiviralProbability()
    {
        return ANTIVIRAL_PROBABILITY ;
    }
    
    protected double getProbabilityDiscloseHIV()
    {
        return PROBABILITY_DISCLOSE_HIV ;
    }
    
    protected double getProbabilityPrep()
    {
        return PROBABILITY_PREP ;
    }
    
    protected double getProbabilitySeroSort()
    {
        return PROBABILITY_SERO_SORT ;
    }
    
    protected double getProbabilitySeroPosition()
    {
        return PROBABILITY_SERO_POSITION ;
    }

    /**
     * 
     * @return (double) the probability of MSM joining an orgy when invited
     */
    public double getJoinOrgyProbability()
    {
        return JOIN_ORGY_PROBABILITY ;
    }
    
}
