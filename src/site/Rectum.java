/**
 * 
 */
package site;

/**
 * @author MichaelWalker
 *
 */
public class Rectum extends Site {

    // Constants that vary according to Site subclass
    
    /** Probability of Site being infected initially */
    static double INITIAL = 0.02 ;
    
    // Probability of positive symptomatic status if infected
    static double SYMPTOMATIC_PROBABILITY = 0.15 ;

    /**
     * Duration of gonorrhoea infection in rectum, 6 months.
     */
    static int INFECTION_DURATION = 183 ;
    
    static int SYMPTOMATIC_DURATION = 5 ;
    
    /**
     * Probability of seeking treatment in a given cycle if infected with 
     * gonorrhoea.
     */
    static double TREATMENT_PROBABILITY = 0.4 ; // 0.3 ;
    
    /**
     * 
     */
    public Rectum() 
    {
            super() ;
    }

    /**
     * 
     * @return Probability of site being infected initially.
     */
    protected double getInfectedProbability()
    {
        return INITIAL ;
    }

    /**
     * 
     * @return The probability of an infection at this Site causing symptoms
     */        
    protected double getSymptomaticProbability()
    {
        return SYMPTOMATIC_PROBABILITY ;
    }

    
    @Override
    protected int getInfectionDuration()
    {
        return INFECTION_DURATION ;
    }
    
    /**
     * 
     * @return Mean duration of symptomatic infection (before treatment).
     */
    protected int getSymptomaticDuration()
    {
        return SYMPTOMATIC_DURATION ;
    }
    
    /**
     * 
     * @return Probability of treatment for an STI being sought and successful.
     */
    @Override
    protected double getTreatmentProbability()
    {
        return TREATMENT_PROBABILITY ;
    }

}
