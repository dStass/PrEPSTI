/**
 * 
 */
package community;

import agent.* ;

import com.sun.media.jfxmedia.logging.Logger;

/**
 * @author MichaelWalker
 *
 */
public class Monogomous extends Relationship {
    static double breakupProbability = 0.1 ;
    

    // Part of how the number of sexual encounters is determined.
    // TODO: Finalise the form of how this is handled
    static double encounterProbability = 0.6 ;


    // Logger
    java.util.logging.Logger logger = java.util.logging.Logger.getLogger("longTerm") ;

    /**
     * 
     */
    public Monogomous() {
            super() ;
    }

    /**
     * 
     */
    public Monogomous(Agent agent1, Agent agent2) {
            super(agent1,agent2) ;
            logger.info("Relationship type " + getRelationship());
    }

    /**
     * TODO: Finalise the form of how this is handled  
     * @return the int number of sexual contacts for a given encounter
     */
    protected int chooseNbContacts()
    {
    	return rand.nextInt(3) + 1 ;
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
