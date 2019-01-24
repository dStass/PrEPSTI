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
    //static double INITIAL = 0.3 ;

    // Probability of positive symptomatic status if infected
    static double SYMPTOMATIC_PROBABILITY = 0.0 ;
    
    /** Probability of Site being infected initially */
    static double INITIAL = 0.01 ;

    /**
     * Duration of gonorrhoea infection in Pharynx, 12 weeks.
     */
    static int INFECTION_DURATION = 12 * 7 ;
    
    /**
     * Mean duration of symptomatic gonorrhoea infection in Pharynx, 12 days.
     */
    static int SYMPTOMATIC_DURATION = 12 ;
    
    /**
     * Probability of seeking treatment in a given cycle if infected with 
     * gonorrhoea.
     */
    static double TREATMENT_PROBABILITY = 0.4 ; // 0.3 ;
    
    

    /**
     * 
     */
    public Pharynx() 
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
     * @return The probability of an infection at this Site causing symptoms.
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
