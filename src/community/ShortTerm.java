package community;

public class ShortTerm extends Relationship {
    private double breakupProbability = 0.3 ;
    
    private double encounterProbability = 0.7 ;
    

	public ShortTerm(Agent agent1, Agent agent2) {
		super(agent1,agent2) ;
	}

}
