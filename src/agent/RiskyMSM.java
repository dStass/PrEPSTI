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
    /** The maximum number of Regular Relationships an agent may be willing to sustain. */
    static int MAX_RELATIONSHIPS = MSM.MAX_RELATIONSHIPS ;
    
    //static double probabilityRequireDiscloseHIV = 0.5 ;
    
    // The probability of positive HIV status
    static double PROPORTION_HIV = 0.092 ;
    
    // The probability of disclosing HIV status if HIV positive
    static double PROBABILITY_DISCLOSE_POSITIVE_HIV = 0.20 ; // 0.40 ;
    // The probability of disclosing HIV status if HIV negative
    static double PROBABILITY_DISCLOSE_NEGATIVE_HIV = 0.18 ; // 0.35 ;
    
    /** 
     * Probability of joining an orgy if invited.
     * Assumed one third of RiskyMSM average once every six months.
     */
    static double JOIN_GSE_PROBABILITY = 1.0/(3 * 184) ;
    
    /** 
     * Probability of using condom regardless of what other strategies are available.
     */
    private double probabilityUseCondom = RAND.nextDouble() ;

    public RiskyMSM(int startAge){
        super(startAge) ;
    }
    
    /**
     * getter for MAX_RELATIONSHIPS.
     * @return MAX_RELATIONSHIPS
     */
    @Override
    protected int getMaxRelationships()
    {
        return MAX_RELATIONSHIPS ;
    }
    
    /**
     *
     * @return (double) The proportion of RiskyMSM who are HIV positive.
     */
    @Override
    protected double getProportionHIV()
    { 
        return PROPORTION_HIV ;
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
        //Boolean partnerSeroPosition = ((MSM) partner).getSeroPosition() ;
        
        // Not if on PrEP
        if (getPrepStatus())
            return false ;
        
        if (getSeroSort(relationshipClazzName))    // might use condom when serodiscordance or nondisclosure
        {
            if (!(getStatusHIV() == Boolean.getBoolean(partnerDisclosure))) 
            {
                if (RAND.nextDouble() < probabilityUseCondom ) 
                    return true;
                if (getStatusHIV() && !((MSM)partner).getPrepStatus()) // !getPrepStatus() || 
                    return (RAND.nextDouble() < probabilityUseCondom ) ;
                else if (!getStatusHIV() && ((MSM)partner).getPrepStatus())
                    return (RAND.nextDouble() < probabilityUseCondom ) ;
            }
        }
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
    public double getJoinGroupSexEventProbability()
    {
        return JOIN_GSE_PROBABILITY ;
    }
    
    /**
     * Adjusts probabilityUseCondom to reflect behavioural trends
     */
    @Override
    public void adjustProbabilityUseCondom()
    {
        probabilityUseCondom *= RAND.nextDouble() ;
    }
    
    /**
     * Setter for probabilityUseCondom.
     * @param useCondom 
     */
    @Override
    public void setProbabilityUseCondom(double useCondom)
    {
        probabilityUseCondom = useCondom ;
    }
    
    /**
     * Called to adjust the changing condom use over time.
     */
    /*@Override
    public void adjustCondomUse()
    {
        probabilityUseCondom = 0.0 ;
    }*/
    
}
