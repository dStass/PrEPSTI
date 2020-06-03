/**
 * 
 */
package PRSP.PrEPSTI.site;

import java.util.Random ;
        
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.distribution.GammaDistribution ;
import PRSP.PrEPSTI.reporter.Reporter;

/**
 * @author Michael Walker
 *
 *Refers to Site of body at which infection takes place
 */
abstract public class Site {
    // Name of subClass of Site
    final private String site ;

    /** Generate and record Random number seed. */
    static long RANDOM_SEED = System.nanoTime() ;
    static Random RAND = new Random(RANDOM_SEED) ;
    
    /** 
     * getter for RANDOM_SEED.
     * @return (long) The static RANDOM_SEED.
     */
    static public final long GET_RANDOM_SEED()
    {
        return RANDOM_SEED ;
    }
    
    static public void SET_RAND(long seed)
    {
        RANDOM_SEED = seed ;
        RAND = new Random(RANDOM_SEED) ;
    }
    
    /**
     * Generates seed for random number generator to use upon reboot.
     * @return (long) seed for random number generation
     */
    static public final long GET_REBOOT_SEED()
    {
        return RAND.nextLong() ;
    }
    
    
    //LOGGER
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("site") ;


    // Array of available infection status'
    static String[] STATES = new String[] {"clear","gonorrhoea"} ;

    static private String[] SITE_NAMES = new String[] {"Rectum", "Urethra", "Pharynx"};

    /** Infection indices given by log_2 of pure infectStatus. */
    static int GONORRHOEA = 0 ;
    //static int SYPHILIS = 1 ;
    //static int CHLAMYDIA = 2 ;
	
    // Initial infection, possibly NoInfection
	// TODO: Implement Infection class instead of 0,1 infectStatus
    //private Infection infection ;
    private boolean infectedStatus = false ; 
    
    /** Whether the site has a symptomatic infection. 
     * Cannot be true unless infectedStatus is true.
     * symptomatic == symptomatic && infectedStatus
     */
    private boolean symptomatic = false ;
    
    /** The length of an ongoing infection.
     * Equals zero unless infectedStatus == true
     */
    private int infectionTime = 0 ;
    
    /** 
     * Days left of incubation period.
     * Equals zero unless infectedStatus == true
     */
    private int incubationTime = 0 ;
    
    // Constants that vary according to Site subclass
    /** Probability of initial gonorrhoea infection in any Site except Urethra.
     * Urethra start uninfected
     */ 
    static double INITIAL = 0.03 ;

    /** Probability of positive symptomatic status if infected. */
    static double SYMPTOMATIC_PROBABILITY = 0.5 ;
    
    /** Minimum incubation period. */
    static int MIN_INCUBATION = 7 ; //2 ;
    
    /** Range of incubation periods. */
    static int RANGE_INCUBATION = 5 ;

    /** Probability of treatment being sought and successful if symptomatic */
    //static double TREATMENT_PROBABILITY = 0.5 ;
    
	
    /**
     * 
     */
    public Site() 
    {
        //initialiseInfection() ;    //TODO: Reinstate when details of multiple
        // STIs are sorted out.
        Class<?> clazz = this.getClass() ;
        site = clazz.asSubclass(clazz).getSimpleName() ;
    }

    /**
     * Called once to initialise infection status.
     * Chooses infectionTime from uniform distribution.
     * TODO: May change back to private when details of multiple STIs are worked out.
     * @return true if site initially infected, false otherwise
     */
    public boolean initialiseInfection()
    {
        if (RAND.nextDouble() < getInfectedProbability() )
        {
            infectedStatus = true ;
            infectionTime = RAND.nextInt(getInfectionDuration()) ;
            
            // Initiate infections as asymptomatic
            //chooseSymptomatic() ; // Do not want chooseIncubationTime() for initialisation
            return true ;
        }
        return false ;
    }
    
    /**
     * Initialises screenCycle from a Gamma distribution to determine how often 
     * Rectum is screened, and then starts the cycle in a random place so that 
     * not every Agent screens their Rectum at the same time.
     * @param statusHIV (boolean) Whether Agent has HIV.
     * @param prepStatus (boolean) Whether Agent is in PrEP.
     * @param rescale (double) Factor by which GammaDistribution is rescaled.
     */
    abstract public void initScreenCycle(boolean statusHIV, boolean prepStatus, double rescale) ;
    
    /**
     * Returns a sample from a Gamma Distribution specified by the parameters shape
     * and scale * rescale. The rescale parameter allows easy adjustment of the 
     * distribution from year-to-year.
     * @param shape (double) Shape parameter of GammaDistribution
     * @param scale (double) Scale parameter of GammaDistribution
     * @param rescale (double) Factor by which GammaDistribution is rescaled.
     * Used to change probability distribution in response to yearly data.
     * @return (int) A sample from a Gamma Distribution 
     */
    protected int sampleGamma(double shape, double scale, double rescale)
    {
        return (int) new GammaDistribution(shape,scale * rescale).sample() ;
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
     * @return String name of Site subClass
     */
    @Override
    public String toString()
    {
        return site ;
    }

    public boolean receiveInfection(double transmitProbability)
    {
        if (RAND.nextDouble() < transmitProbability )
        {
            infectedStatus = true ;
            infectionTime = getInfectionDuration() ;
            
            // Select whether symptomatic an set incubationTime if so.
            chooseSymptomatic() ;
            //if ()
              //  chooseIncubationTime() ;
            return true ;
        }
        return false ;
    }
    
    /**
     * Choose probability that Site is symptomatic and then invoke 
 chooseSymptomatic() to choose.
     * @return 
     */
    private boolean chooseSymptomatic()
    {
        return chooseSymptomatic(getSymptomaticProbability()) ;
    }

    /**
     * Probabilitistically chooses whether Site is symptomatic.
     * @param symptomaticProbability (double) The probability of choosing the Site to be symptomatic.
     * @return (boolean) Whether the site is now symptomatic.
     */
    public boolean chooseSymptomatic(double symptomaticProbability)
    {
        return setSymptomatic(RAND.nextDouble() < symptomaticProbability) ;
    }
    
    /**
     * Setter of symptomatic
     * @param siteSymptomatic (boolean) Whether the site is now symptomatic.
     * @return (boolean) The new value for symptomatic.
     */
    public boolean setSymptomatic(boolean siteSymptomatic)
    {
        symptomatic = siteSymptomatic ;
        if (symptomatic)
            chooseIncubationTime() ;
        return symptomatic ;
    }
    
    /**
     * 
     * @return The probability of an infection causing symptoms
     */        
    protected double getSymptomaticProbability()
    {
        return SYMPTOMATIC_PROBABILITY ;
    }

    /**
     * Invoked when Agent is symptomatic. Call each site.treat(). If all treatments successful, call clearSymptomatic()
     * @return true if all sites successfully treated, false otherwise
     */
    public boolean treatSymptomatic()
    {
        if (!(incubationTime < 0)) 
            return false ;
        
        return treat() ;
    }

    /**
     * Probabilistically treat infection at Site
     * @return true if treatment sought and successful, false otherwise.
     */
    public boolean treat()
    {
        //if (RAND.nextDouble() < getTreatmentProbability() )
        clearInfection() ;
        return true ;
        //return false ;
    }

    /**
     * 
     * getter for treatmentProbability, 
     * the probability of treatment for an STI being successful
     */
    //abstract protected double getTreatmentProbability() ;
    

    public void clearInfection()
    {
        infectedStatus = false ;
        symptomatic = false ;
        infectionTime = 0 ;
        incubationTime = 0 ;
        //resetScreenTime() ;
    }

    public boolean getInfectedStatus()
    {
        return infectedStatus ;
    }

    public boolean getSymptomatic()
    {
        return symptomatic ;
    }
    
    public int getInfectionTime()
    {
        return infectionTime ;
    }
    
    public void setInfectionTime(int time)
    {
        infectionTime = time ;
    }
    
    /**
     * Adjusts infectionTime and incubationTime by one day
     * @return true if infection has cleared and false otherwise
     */
    public boolean progressInfection()
    {
        infectionTime-- ;
        if (infectionTime < 0)
        {
            clearInfection() ;
            return true ;
        }
        return false ;
    }
    
    private void resetScreenTime()
    {
        setScreenTime(getScreenCycle()) ;
    }
    
    /**
     * setter for screenTime
     * @param time (int) Cycles remaining until the next screening.
     */
    abstract public void setScreenTime(int time);

    /** 
     * screenTime getter().
     * @return (int) The  number of cycles between screenings.
     */
    abstract public int getScreenTime() ;

    /** 
     * screenCycle setter().
     * @param screen (int) The number of cycles between screenings
     */
    abstract public void setScreenCycle(int screen) ;

    /** 
     * screenCycle getter().
     * @return screenCycle 
     */
    abstract public int getScreenCycle() ;
    
    protected void decrementScreenTime()
    {
        setScreenTime((getScreenTime() - 1)) ;
    }

    /**
     * Probability of MSM screening on that day. Depends on prepStatus and statusHIV.
     * @param args (Object[])
     * @return for PrEP users, 1.0 if cycle multiple of screenCycle, 0.0 otherwise
     *     for non-PrEP users, random double between 0.0 and 1.0 .
     */    
    public double getScreenProbability(Object[] args)
    {
            // Find current cycle
        //    int cycle = Integer.valueOf(args[0]) ;
            
        // Countdown to next STI screen
        decrementScreenTime() ;

        // Is it time for a regular screening?
        if ( getScreenTime() < 0)
        {
            //LOGGER.log(Level.INFO,"{0} {1}", new Object[] {getScreenTime(),getScreenCycle()}) ;
            setScreenTime(getScreenCycle()) ;
            return 1.1 ;
        }
        else
            return -0.1 ;
    }
    
    /**
     * Randomly chooses how long an infection lasts, assuming it is untreated.
     * @return Randomly chosen from Gamma Distribution from half mean cutoff.
     */
    public int applyInfectionDuration()
    {
        infectionTime = RAND.nextInt(getInfectionDuration() - 1) + 1 ;
        infectedStatus = true ;
        return infectionTime ;
    }
    
    /**
     * Mean duration of asymptomatic infections.
     * @return 
     */
    abstract protected int getInfectionDuration() ;
    
    /**
     * Randomly chooses how long an infection lasts, assuming it is asymptomatic.
     * @return Randomly chosen from Gamma Distribution from half mean cutoff.
     */
    public int chooseIncubationTime()
    {
        incubationTime = MIN_INCUBATION ; //+ RAND.nextInt(RANGE_INCUBATION) ; // (int) new GammaDistribution(distributionMean,1).sample()) + distributionMean ;
        return incubationTime ;
    }
    
    /**
     * Getter for days left in incubation period.
     * @return incubationTime
     */
    public int getIncubationTime()
    {
        return incubationTime ;
    }
    
    /**
     * Getter for days left in incubation period.
     * 
     * @param incubationPeriod
     */
    public void setIncubationTime(int incubationPeriod)
    {
        incubationTime = incubationPeriod ;
    }
    
    /**
     * Adjusts per year the screening period.
     * @param year
     * @param hivStatus
     * @throws Exception 
     */
    abstract public void reinitScreenCycle(int year, boolean hivStatus) throws Exception ;
    
    /**
     * 
     * @return (String) giving values of the Site's important properties.
     */
    public String getCensusReport()
    {
        String censusReport = Reporter.ADD_REPORT_PROPERTY("Site",toString()) ;
        //censusReport += Reporter.ADD_REPORT_PROPERTY("screenCycle",getScreenCycle()) ;
        //censusReport += Reporter.ADD_REPORT_PROPERTY("screenTime",getScreenTime()) ;
        if (infectedStatus)
        {
            // Order is important, symptomatic must be before all other infection-related properties.
            censusReport += Reporter.ADD_REPORT_PROPERTY("symptomatic",symptomatic) ;
            censusReport += Reporter.ADD_REPORT_PROPERTY("infectionTime",infectionTime) ;
            censusReport += Reporter.ADD_REPORT_PROPERTY("incubationTime",incubationTime) ;
        }
        
        return censusReport ;
    }

    public static String[] getAvailableSites() {
        return Site.SITE_NAMES;
    }
}
