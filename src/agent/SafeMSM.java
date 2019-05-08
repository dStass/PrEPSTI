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
    
    /** Probability of joining a Group Sex Event if invited. */
    static double JOIN_GSE_PROBABILITY = 0.0 ;
    
    public SafeMSM(int startAge){
        super(startAge) ;
    }
    
    
    /**
     * Decides probabilistically whether MSM chooses to use a condom in a given encounter.
     * Choice is based on properties of msm and Agent partner.
     * TODO: Different probabilityUseCondom according to prepStatus?
     * TODO: Should there be subset that always uses a condom?
     * @param relationshipClazzName
     * @param partner
     * @return true if condom is to be used, false otherwise
     */
    @Override
    protected boolean chooseCondom(String relationshipClazzName, Agent agentPartner) 
    {
        MSM partner = (MSM) agentPartner ;
        LOGGER.severe("SafeMSM.chooseCondom() called") ;
        if (getStatusHIV())
            if (!getAntiViralStatus())
                return true ;
        else if (!getPrepStatus())
        {
            if (!partner.getDiscloseStatusHIV()) // Partner doesn't disclose
                return true ; 
            if (partner.getStatusHIV() && !partner.getAntiViralStatus()) // Partner HIV +ve without antivirals
                return true ;
        }
        return (RAND.nextDouble() < probabilityUseCondom ) ;  //TODO: Should there be subset who always use?
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
