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
    
    // The maximum number of relationships an agent may be willing to sustain
    static int MAX_RELATIONSHIPS = 5;
    
    // Associated probabilities for the above
    //static double probabilityRequireDiscloseHIV = 0.5 ;
    
    /** Fraction of SafeMSM with positive HIV status. */
    static double PROBABILITY_HIV = 0.02 ;
    
    /** Fraction of SafeMSM who disclose HIV status if HIV positive. */
    static double PROBABILITY_DISCLOSE_POSITIVE_HIV = 0.40 ;
    /** Fraction of SafeMSM who disclose HIV status if HIV negative. */
    static double PROBABILITY_DISCLOSE_NEGATIVE_HIV = 0.35 ;
    
    /** Probability of joining an orgy if invited. */
    static double JOIN_ORGY_PROBABILITY = 0.1 ;
    
    /** probability of using condom even when apparently safe (PrEP, TasP, etc) */
    private double probabilityUseCondom = RAND.nextDouble() ;
    
    private boolean casualAnalSex ;
    public SafeMSM(int startAge){
        super(startAge) ;
    }
    
    protected int getMaxRelationships()
    {
        return MAX_RELATIONSHIPS ;
    }
    
    /** Fraction of SafeMSM with positive HIV status. */
    protected double getProbabilityHIV()
    { 
        return PROBABILITY_HIV ;
    }
    
    
    /**
     * HIV positive MSM are more likely to disclose the statusHIV
     * @return (Double) probability of disclosing statusHIV
     */
    protected double getProbabilityDiscloseHIV()
    {
        if (getStatusHIV())
            return PROBABILITY_DISCLOSE_POSITIVE_HIV ;
        return PROBABILITY_DISCLOSE_NEGATIVE_HIV ;
    }
    
    /**
     * Decides probabilistically whether MSM chooses to use a condom in a given encounter.
     * Choice is based on properties of msm
     * @param partner
     * @return true if condom is to be used, false otherwise
     */
    @Override
    protected boolean chooseCondom(Agent partner) 
    {
        if (getStatusHIV())
            if (!getAntiViralStatus())
                return true ;
        else if (((MSM) partner).getDiscloseStatusHIV())
            if (((MSM) partner).getStatusHIV())
                if (!getPrepStatus())
                    return true ;
        else    // partner msm does not disclose
            return true ;
        return (RAND.nextDouble() < probabilityUseCondom ) ;  //TODO: Should there be subset who always use?
    }
    
    /**
     * 
     * @return (double) the probability of MSM joining an orgy when invited
     */
    @Override
    public double getJoinOrgyProbability()
    {
        return JOIN_ORGY_PROBABILITY ;
    }
    
    
}
