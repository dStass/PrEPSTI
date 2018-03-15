package community;

import agent.* ;

class Casual extends Relationship {
    private double breakupProbability = 1.0 ;
    
    private double encounterProbability = 1.0 ;
    
    

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


}
