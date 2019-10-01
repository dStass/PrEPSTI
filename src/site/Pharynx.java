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
    static double INITIAL = 0.045 ; // 0.005 ; 

    /** Probability of positive symptomatic status if infected */
    static double SYMPTOMATIC_PROBABILITY = 0.0 ; // 0.40 ;
    
    /**
     * Duration of gonorrhoea infection in Pharynx, 12 weeks.
     */
    static int INFECTION_DURATION = 7 * 12 ; // 12 ;
    
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
     * @param statusHIV
     * @param prepStatus
     * @param rescale
     */
    @Override
    public void initScreenCycle(boolean statusHIV, boolean prepStatus, double rescale)
    {
        if (prepStatus)
            setScreenCycle(sampleGamma(31,1,rescale) + 61) ;
        else
        {
            
            if (statusHIV)
                setScreenCycle(sampleGamma(5,74,rescale)) ;  // 54.6% screen within first year
            else
                setScreenCycle(sampleGamma(5,81,rescale)) ;  // 46.9% screen within first year
            
        }
        // Randomly set timer for first STI screen 
        setScreenTime(RAND.nextInt(getScreenCycle())) ;
    }
    
    /**
     * 
     * @return Probability of site being infected initially.
     */
    @Override
    protected double getInfectedProbability()
    {
        return INITIAL ;
    }

    /**
     * 
     * @return The probability of an infection at this Site causing symptoms.
     */        
    @Override
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
     * setter for screenTime
     * @param time 
     */
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
    @Override
    public void reinitScreenCycle(int year, boolean hivStatus) throws Exception
    {
        double[] testRates = new double[] {} ;
        double testBase ;
        // Go from 2007
        // Frequencies, given by per 1000 per year, from 2007-2016
        // Table 17 ARTB 2016
        if (hivStatus)
            testRates = new double[] {546,546,564,613,625,634,633,695} ;
        else
            testRates = new double[] {469,491,491,476,509,502,540,555} ;
        // year == 0 never calls this method
        //if (year == 0)
          //  testBase = testRates[0] ;
        //else
        testBase = testRates[0] ;
        
        double ratio = testBase/testRates[year] ;
        // Do not reinitialise MSM on Prep
        initScreenCycle(hivStatus,false,ratio) ;
    }    
    
    
}
