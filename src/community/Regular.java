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

}
