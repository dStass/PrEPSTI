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
    /** 
     * Probability of joining a Group Sex Event if invited.
     * Assumed one third of RiskyMSM average once every six months.
     */
    static double JOIN_GSE_PROBABILITY = 1.0/(3 * 184) ;
    
    public RiskyMSM(int startAge){
        super(startAge) ;
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
    
}
