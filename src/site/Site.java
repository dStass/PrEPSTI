/**
 * 
 */
package site;

import java.util.Random ;
        
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.distribution.GammaDistribution ;

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
     * @return  
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
    
    
    //LOGGER
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("relationship") ;


    // Array of available infection status'
    static String[] STATES = new String[] {"clear","gonorrhoea"} ;

    /** Infection indices given by log_2 of pure infectStatus. */
    static int GONORRHOEA = 0 ;
    //static int SYPHILIS = 1 ;
    //static int CHLAMYDIA = 2 ;
	
    // Initial infection, possibly NoInfection
	// TODO: Implement Infection class instead of 0,1 infectStatus
    //private Infection infection ;
    private int infectedStatus = 0 ; 
    
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
    static int MIN_INCUBATION = 2 ;
    
    /** Range of incubation periods. */
    static int RANGE_INCUBATION = 4 ;

    // Probability of site transmitting infection, if all other probabilities unity
    //static double TRANSMIT = 0.5 ;

    // Probability of site becoming infected, if all other probabilities unity
    //static double RECEIVE = 0.5 ;
    
    /** Probability of treatment being sought and successful if symptomatic */
    static double TREATMENT_PROBABILITY = 0.5 ;
    
	
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
            infectedStatus = 1 ;
            infectionTime = RAND.nextInt(getInfectionDuration()) ; // + getInfectionDuration() ;
            return true ;
        }
        return false ;
    }
    
    abstract public void initScreenCycle(boolean statusHIV, boolean prepStatus) ;
    
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
    public String getSite()
    {
        return site ;
    }

    public boolean receiveInfection(double transmitProbability)
    {
        if (RAND.nextDouble() < transmitProbability )
        {
            infectedStatus = 1 ;
            infectionTime = getInfectionDuration() ;
            
            // Select whether symptomatic
            if (chooseSymptomatic())
                chooseIncubationTime() ;
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
     * Set whether Site is symptomatic given the symptomaticProbability.
     * @param symptomaticProbability
     * @return 
     */
    public boolean chooseSymptomatic(double symptomaticProbability)
    {
        symptomatic = (RAND.nextDouble() < symptomaticProbability) ;
        return symptomatic ;
    }
    
    /**
     * Setter of symptomatic
     * @param siteSymptomatic
     * @return 
     */
    public boolean setSymptomatic(boolean siteSymptomatic)
    {
        symptomatic = siteSymptomatic ;
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
            {
                clearInfection() ;
                return true ;
            }
            //return false ;
    }

    /**
     * 
     * @return Probability of treatment for an STI being successful
     */
    abstract protected double getTreatmentProbability() ;
    

    public void clearInfection()
    {
        infectedStatus = 0 ;
        symptomatic = false ;
        infectionTime = 0 ;
        incubationTime = 0 ;
        resetScreenTime() ;
    }

    public int getInfectedStatus()
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
    
    public boolean progressInfection()
    {
        infectionTime-- ;
        incubationTime-- ;
        if (infectionTime == 0)
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
    
    /** screenTime setter().
     * @param time */
    abstract public void setScreenTime(int time);

    /** screenTime getter().
     * @return  
     */
    abstract public int getScreenTime() ;

    /** 
     * screenCycle setter().
     * @param screen 
     */
    abstract public void setScreenCycle(int screen) ;

    /** screenCycle getter().
     * @return screenCycle 
     */
    abstract public int getScreenCycle() ;
    
    protected void decrementScreenTime()
    {
        setScreenTime((getScreenTime() - 1)) ;
    }

    /**
     * Probability of MSM screening on that day. Depends on prepStatus and statusHIV.
     * @param args
     * @return for PrEP users, 1.0 if cycle multiple of screenCycle, 0.0 otherwise
     *     for non-PrEP users, random double between 0.0 and 1.0 .
     */    
    public double getScreenProbability(String[] args)
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
            return 1.0 ;
        }
        else
            return 0.0 ;
    }
    
    /**
     * Randomly chooses how long an infection lasts, assuming it is untreated.
     * @return Randomly chosen from Gamma Distribution from half mean cutoff.
     */
    public int setInfectionDuration()
    {
        int infectionDuration = getInfectionDuration() ;
        int distributionMean = infectionDuration/2 ;
        infectionTime = ((int) new GammaDistribution(distributionMean,1).sample()) + distributionMean ;
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
        incubationTime = MIN_INCUBATION + RAND.nextInt(RANGE_INCUBATION) - 1 ; // (int) new GammaDistribution(distributionMean,1).sample()) + distributionMean ;
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
     * Mean duration of symptomatic infections.
     * @return 
     */
    abstract protected int getSymptomaticDuration() ;
    
    /**
     * Adjusts per year the screening period.
     * @param year
     * @throws Exception 
     */
    abstract public void reinitScreenCycle(int year) throws Exception ;
    
    
}
