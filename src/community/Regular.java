package community;

import agent.* ;

import java.util.logging.Level;


public class Regular extends Relationship {
    
    /** The number of current Regular Relationships. */
    static int NB_REGULAR = 0 ;
    
    /** Probability of breakup() in a given cycle. */
    static public double BREAKUP_PROBABILITY = 0.005 ;
    
    /** Probability of sexual encounter in a given cycle. */
    static double ENCOUNTER_PROBABILITY = 0.6 ; // 0.4 ; // 0.7 ;
    
    public Regular()
    {
        super() ;
        NB_REGULAR++ ;
    }

    public Regular(Agent agent1, Agent agent2) {
        super(agent1,agent2) ;
        NB_REGULAR++ ;
    }

    /**
     * The probability of any sexual contact in any cycle.
     * @return static ENCOUNTER_PROBABILITY
     */
    @Override
    protected double getEncounterProbability()
    {
        return ENCOUNTER_PROBABILITY ;
    }
    
    /**
     * Since breakupProbability is static, this getter allows it to be called
     * from the Relationship base class
     * @return (double) the probability of a relationship ending in a given cycle
     */
    @Override
    public double getBreakupProbability()
    {
        return BREAKUP_PROBABILITY ;
    }
    
    /**
     * 
     * @return (int) The current number of Regular Relationships.
     */
    public int getNbRegular()
    {
        return NB_REGULAR ;
    }
    
    
    protected void diminishNbRegular()
    {
        NB_REGULAR-- ;
    }

}
