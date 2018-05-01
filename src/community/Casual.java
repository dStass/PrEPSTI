package community;

import agent.* ;
import java.util.logging.Level;


class Casual extends Relationship {
    
    /** The number of current Casual Relationships. */
    static int NB_CASUAL = 0 ;
    
    /** Probability of breakup() in a given cycle. */
    static double BREAKUP_PROBABILITY = 1.0 ;
    
    /** Probability of sexual encounter in a given cycle. */
    static double ENCOUNTER_PROBABILITY = 1.0 ;
    

    protected Casual() 
    {
        super() ;
        NB_CASUAL++ ;
    }
    

    protected Casual(Agent agent1, Agent agent2) 
    {
        super(agent1,agent2) ;
        NB_CASUAL++ ;
    }
    


    /*********************************************************************
     * Casual relationship ends at end of cycle 
     * @return True  
     *********************************************************************/
    @Override
    final protected boolean breakup() 
    {
    	// Casual Relationship always ends immediately
        for (Agent agent : getAgents())
            agent.leaveRelationship(this) ;  
        
        return true ;
    }

    /**
     * Since breakupProbability is static, this getter allows it to be called
     * from the Relationship base class.
     * Probability redundant in Casual subclass since breakup always returns true,
     * But this is for consistency and future-proofing
     * @return (double) the probability of a relationship ending in a given cycle
     */
    @Override
    protected double getBreakupProbability()
    {
        return BREAKUP_PROBABILITY ;
    }
    
    /**
     * 
     * @return (int) The current number of Casual Relationships.
     */
    public int getNbCasual()
    {
        return NB_CASUAL ;
    }
    
    
    protected void diminishNbCasual()
    {
        NB_CASUAL-- ;
    }

}
