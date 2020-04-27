/**
 * 
 */
package PRSP.PrEPSTI.community;

import PRSP.PrEPSTI.agent.* ;


/**
 * @author MichaelWalker
 *
 */
public class Monogomous extends Relationship {
    
    /** The number of current Monogomous Relationships. */
    static int NB_MONOGOMOUS = 0 ;
    
    /** Probability of breakup() in a given cycle. */
    public static double BREAKUP_PROBABILITY = 0.0001 ;
    

    // Part of how the number of sexual encounters is determined.
    static double ENCOUNTER_PROBABILITY = 0.2 ; // 0.6 ;


    // Logger
    java.util.logging.Logger logger = java.util.logging.Logger.getLogger("longTerm") ;

    /**
     * 
     */
    public Monogomous() {
        super() ;
        NB_MONOGOMOUS++ ;
    }

    /**
     * 
     */
    public Monogomous(Agent agent1, Agent agent2) {
        super(agent1,agent2) ;
        NB_MONOGOMOUS++ ;
    }

    /**
     * The probability of any sexual contact in any cycle.
     * @return 
     */
    @Override
    protected double getEncounterProbability()
    {
        return ENCOUNTER_PROBABILITY ;
    }
    
    /**
     * TODO: Finalise the form of how this is handled  
     * @return the int number of sexual contacts for a given encounter
     */
    protected int chooseNbContacts()
    {
    	return RAND.nextInt(3) + 1 ;
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
     * @return (int) The current number of Monogomous Relationships.
     */
    public int getNbMonogomous()
    {
        return NB_MONOGOMOUS ;
    }
    
    
    protected void diminishNbMonogomous()
    {
        NB_MONOGOMOUS-- ;
    }
    
    
}
