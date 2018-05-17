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
    
    // The probability of disclosing HIV status if HIV positive
    static double PROBABILITY_DISCLOSE_POSITIVE_HIV = 0.40 ;
    // The probability of disclosing HIV status if HIV negative
    static double PROBABILITY_DISCLOSE_NEGATIVE_HIV = 0.35 ;
    // probability of using condom even when other strategies not available
    private double probabilityUseCondom = RAND.nextDouble() ;
    
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
     * RiskyMSM choose use strategies other than condoms
     * @param partner
     * @return true if condom is to be used, false otherwise
     */
    @Override
    protected boolean chooseCondom(String relationshipClazzName, Agent partner) 
    {
        String partnerDisclosure = partner.declareStatus() ;
        Boolean partnerSeroPosition = ((MSM) partner).getSeroPosition() ;
        if (getSeroSort(relationshipClazzName))    // might use condom when serodiscordance or nondisclosure
            if (!(getStatusHIV() == Boolean.getBoolean(partnerDisclosure))) 
                return (RAND.nextDouble() < probabilityUseCondom ) ;
        if (getSeroPosition())
            if (NONE.equals(partnerDisclosure))  // maybe if partner does not disclose
                return (RAND.nextDouble() < probabilityUseCondom ) ;
        return false ;
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
