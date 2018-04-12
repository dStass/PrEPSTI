package community;

import agent.* ;


public class Regular extends Relationship {
    static double breakupProbability = 0.3 ;
    
    static double encounterProbability = 0.7 ;
    
    public Regular()
    {
        super() ;
    }

    public Regular(Agent agent1, Agent agent2) {
            super(agent1,agent2) ;
    }

    /**
     * Since breakupProbability is static, this getter allows it to be called
     * from the Relationship base class
     * @return (double) the probability of a relationship ending in a given cycle
     */
    protected double getBreakupProbability()
    {
        return breakupProbability ;
    }
    
    
}
