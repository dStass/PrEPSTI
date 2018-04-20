/**
 * 
 */
package site;


/**
 * @author MichaelWalker
 *
 */
public class Pharynx extends Site {

    // Constants that vary according to Site subclass
    // Probability of initial gonorrhoea infection 
    static double INITIAL = 0.3 ;

    // Probability of positive symptomatic status if infected
    static double SYMPTOMATIC_PROBABILITY = 0.05 ;

    // Probability of site transmitting infection, if all other probabilities unity
    static double TRANSMIT = 0.5 ;

    // Probability of site becoming infected, if all other probabilities unity
    static double RECEIVE = 0.7 ;
     

    /**
     * 
     */
    public Pharynx() 
    {
            super() ;
    }

    /**
     * 
     * @return The probability of an infection at this Site causing symptoms
     */        
    protected double getSymptomaticProbability()
    {
        return SYMPTOMATIC_PROBABILITY ;
    }

    
}
