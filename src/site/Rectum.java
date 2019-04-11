/**
 * 
 */
package site;

import org.apache.commons.math3.distribution.GammaDistribution;

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
    static int INFECTION_DURATION = 365 ; // 183 ;
    
    static int SYMPTOMATIC_DURATION = 5 ;
    
    /**
     * Probability of seeking treatment in a given cycle if infected with 
     * gonorrhoea.
     */
    static double TREATMENT_PROBABILITY = 0.4 ; // 0.3 ;
    
    /** Days between asymptomatic STI screens . */
    private int screenCycle ;
    
    /** Cycles remaining until next STI screen. */
    private int screenTime ;

    /** Are rectal infections symptomatic. */
    private double symptomaticProbability ;
    
    /**
     * 
     */
    public Rectum() 
    {
            super() ;
            initSymptomaticProbability() ;
    }

    /**
     * Decides probability of rectal infections being symptomatic for this Agent.
     * Some are always, some never, and some are random with mean probability.
     */
    private void initSymptomaticProbability()
    {
        if (RAND.nextDouble() < 1.0)
            symptomaticProbability = SYMPTOMATIC_PROBABILITY ;
        else    // Whether infections are symptomatic depends on the Agent.
        {
            if (RAND.nextDouble() < SYMPTOMATIC_PROBABILITY)
                symptomaticProbability = 1.0 ;
            else
                symptomaticProbability = 0.0 ;
        }
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
            setScreenCycle(sampleGamma(31,1,rescale) + 61) ;  // ((int) new GammaDistribution(31,1).sample())
        else
        {
            
            if (statusHIV)
                setScreenCycle(sampleGamma(7,60,rescale)) ; // (20,18.3,rescale)) ;  // (16,23,rescale)) ;  // (((int) new GammaDistribution(10,37).sample())) ;  // 52.2% screen within a year
            else
                setScreenCycle(sampleGamma(8,61,rescale)) ; // setScreenCycle(sampleGamma(20,19.3,rescale)) ;  // (6,69,rescale)) ;  // (((int) new GammaDistribution(9,53).sample())) ;  // 43.6% screen within a year
            
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
     * @return The probability of an infection at this Site causing symptoms
     */        
    protected double getSymptomaticProbability()
    {
        return symptomaticProbability ; // SYMPTOMATIC_PROBABILITY ;
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
    @Override
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
    
    /** screenTime setter(). */
    @Override
    public void setScreenTime(int time)
    {
        screenTime = time ;
    }

    /** screenTime getter().
     * @return  
     */
    @Override
    public int getScreenTime()
    {
        return screenTime ;
    }

    /** 
     * screenCycle setter().
     * @param screen 
     */
    public void setScreenCycle(int screen)
    {
        screenCycle = screen ;
    }

    /** screenCycle getter().
     * @return screenCycle 
     */
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
            testRates = new double[] {522,493,539,610,625,605,614,691} ;
        else
            testRates = new double[] {436,459,461,444,484,467,511,514} ;
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
