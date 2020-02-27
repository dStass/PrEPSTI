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
public class Urethra extends Site {

    // Constants that vary according to Site subclass
    /** Probability of initial gonorrhoea infection */
    static double INITIAL = 0.004 ; // 0.004 ; //

    // Probability of positive symptomatic status if infected
    static double SYMPTOMATIC_PROBABILITY = 0.60 ; // 0.90 ; // 
    
    /**
     * Duration of gonorrhoea infection in Urethra.
     * Proper value unknown as almost always treated. 
     * Value for Rectum taken.
     */
    static int INFECTION_DURATION = 84 ; //  Matched to Pharynx // 183 ;
    
    
    /** Days between asymptomatic STI screens . */
    private int screenCycle = 92 ;
    
    /** Cycles remaining until next STI screen. */
    private int screenTime ;

    
    /**
     * 
     */
    public Urethra() 
    {
        super() ;
    }
    
    /**
     * Initialises screenCycle from a Gamma distribution to determine how often 
     * Rectum is screened, and then starts the cycle in a random place so that 
     * not every MSM screens his Rectum at the same time.
     * @param statusHIV
     * @param prepStatus
     */
    @Override
    public void initScreenCycle(boolean statusHIV, boolean prepStatus, double rescale)
    {
        if (prepStatus)
            setScreenCycle(sampleGamma(31,1,rescale) + 61) ;
        else
        {
            
            if (statusHIV)
                setScreenCycle(sampleGamma(4,87,rescale)) ;  // (((int) new GammaDistribution(6,58).sample())) ;  // 60.4% screen within first year
            else
                setScreenCycle(sampleGamma(3,128,rescale)) ;  // (((int) new GammaDistribution(4,94).sample())) ;  // 54.2% screen within first year
            
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
     * @return The probability of an infection at this Site causing symptoms
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
     * setter for screenTime.
     * @param time 
     */
    @Override
    public void setScreenTime(int time) 
    {
        screenTime = time ;
    }

    @Override
    public int getScreenTime() 
    {
        return screenTime ;
    }

    @Override
    public void setScreenCycle(int screen) 
    {
        screenCycle = screen ;
    }

    @Override
    public int getScreenCycle() 
    {
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
            testRates = new double[] {604,606,625,669,716,748,742,754} ;
        else
            testRates = new double[] {542,576,567,563,589,591,603,626} ;
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
