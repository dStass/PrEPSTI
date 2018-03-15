package community;

import agent.* ;


public class Regular extends Relationship {
    private double breakupProbability = 0.3 ;
    
    private double encounterProbability = 0.7 ;
    

	public Regular(Agent agent1, Agent agent2) {
		super(agent1,agent2) ;
	}

}
