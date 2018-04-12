package community;

import agent.* ;

class Casual extends Relationship {
    static double breakupProbability = 1.0 ;
    
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
    	return true ;
    }

    /**
     * Since breakupProbability is static, this getter allows it to be called
     * from the Relationship base class.
     * Probability redundant in Casual subclass since breakup always returns true,
     * But this is for consistency and future-proofing
     * @return (double) the probability of a relationship ending in a given cycle
     */
    protected double getBreakupProbability()
    {
        return breakupProbability ;
    }
    
    

}
