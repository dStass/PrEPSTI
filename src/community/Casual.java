package community;

import agent.* ;
import java.util.logging.Level;


class Casual extends Relationship {
    // Probability of breakup() in a given cycle.
    static double breakupProbability = 1.0 ;
    
    // Probability of sexual encounter in a given cycle.
    static double encounterProbability = 1.0 ;
    

    protected Casual() 
    {
        super() ;
    }
    

    protected Casual(Agent agent1, Agent agent2) 
    {
        super(agent1,agent2) ;
    }
    


    /*********************************************************************
     * Casual relationship ends at end of cycle 
     * @return True  
     *********************************************************************/
    protected boolean breakup() 
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
        return breakupProbability ;
    }
    
    

}
