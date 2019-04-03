/**
 * 
 */
package site;

import org.apache.commons.math3.distribution.GammaDistribution;
import static site.Site.RAND;


/**
 * @author MichaelWalker
 *
 */
public class Pharynx extends Site {

    // Constants that vary according to Site subclass
    /** Probability of initial gonorrhoea infection */
    static double INITIAL = 0.02 ;

    /** Probability of positive symptomatic status if infected */
    static double SYMPTOMATIC_PROBABILITY = 0.0 ;
    
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
    
    /** Days between asymptomatic STI screens . */
    private int screenCycle = 160 ;
    
    /** Cycles remaining until next STI screen. */
    private int screenTime ;

    

    /**
     * 
     */
    public Pharynx() 
    {
            super() ;
    }

    /**
     * Initialises screenCycle from a Gamma distribution to determine how often 
     * Rectum is screened, and then starts the cycle in a random place so that 
     * not every MSM screens his Rectum at the same time.
     */
    public void initScreenCycle(boolean statusHIV, boolean prepStatus)
    {
        if (prepStatus)
            setScreenCycle(((int) new GammaDistribution(31,1).sample()) + 61) ;
        else
        {
            
            if (statusHIV)
                setScreenCycle(((int) new GammaDistribution(5,74).sample())) ;  // 54.6% screen within a year
            else
                setScreenCycle(((int) new GammaDistribution(5,81).sample())) ;  // 46.9% screen within a year
            
        }
        // Randomly set timer for first STI screen 
        setScreenTime(RAND.nextInt(getScreenCycle())) ;
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

    @Override
    public void setScreenTime(int time) {
        screenTime = time ;
    }

    @Override
    public int getScreenTime() {
        return screenTime ;
    }

    @Override
    public void setScreenCycle(int screen) {
        screenCycle = screen ;
    }

    @Override
    public int getScreenCycle() {
        return screenCycle ;
    }

    /**
     * Adjusts per year the screening period.
     * @param year
     * @throws Exception 
     */
    public void reinitScreenCycle(int year) throws Exception
    {
        // Go from 2007
        // Frequencies, given by per 1000 per year, from 2007-2016
        // Table 17 ARTB 2016
        double[] testRates = new double[] {333,340,398,382,383,382,391,419,445,499} ;
        double testBase ;
        if (year == 0)
            testBase = testRates[0] ;
        else
            testBase = testRates[year - 1] ;
        
        double ratio = testBase/testRates[year] ;
        int newScreenCycle = (int) Math.ceil(ratio * getScreenCycle()) ;
        setScreenCycle(newScreenCycle) ;
    }
    
    
    
}
