/**
 * 
 */
package site;

import com.sun.media.jfxmedia.logging.Logger;

/**
 * @author MichaelWalker
 *
 */
public class Urethra extends Site {

    // Constants that vary according to Site subclass
    // Probability of initial gonorrhoea infection 
    // static double INITIAL = 0.3 ;

    // Probability of positive symptomatic status if infected
    static double SYMPTOMATIC_PROBABILITY = 0.9 ;

    
    /**
     * 
     */
    public Urethra() 
    {
        super() ;
        Logger.logMsg(0, this.getSite());
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
