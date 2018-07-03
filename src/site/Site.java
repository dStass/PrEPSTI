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

    static Random RAND = new Random() ;
    
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
    
    // Constants that vary according to Site subclass
    /** Probability of initial gonorrhoea infection in any Site except Urethra.
     * Urethra start uninfected
     */ 
    static double INITIAL = 0.03 ;

    /** Probability of positive symptomatic status if infected. */
    static double SYMPTOMATIC_PROBABILITY = 0.5 ;

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
        if (RAND.nextDouble() < INITIAL ) 
        {
            infectedStatus = 1 ;
            infectionTime = RAND.nextInt(getInfectionDuration()) ; // + getInfectionDuration() ;
            return true ;
        }
        return false ;
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
            chooseSymptomatic() ;
            return true ;
        }
        return false ;
    }

    /**
     * Choose probability that Site is symptomatic and then invoke 
     * setSymptomatic() to choose.
     * @return 
     */
    private boolean chooseSymptomatic()
    {
        return setSymptomatic(getSymptomaticProbability()) ;
    }

    /**
     * Set whether Site is symptomatic given the symptomaticProbability.
     * Allow to specify that Site must or must not be symptomatic.
     * @param symptomaticProbability
     * @return 
     */
    public boolean setSymptomatic(double symptomaticProbability)
    {
        symptomatic = (RAND.nextDouble() < symptomaticProbability) ;
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
    
    public int progressInfection()
    {
        infectionTime-- ;
        if (infectionTime == 0)
            clearInfection() ;
        return infectionTime ;
    }
    
    /**
     * Randomly chooses how long an infection lasts, assuming it is asymptomatic.
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
    public int setSymptomaticDuration()
    {
        int infectionDuration = getSymptomaticDuration() ;
        int distributionMean = infectionDuration/2 ;
        infectionTime = ((int) new GammaDistribution(distributionMean,1).sample()) + distributionMean ;
        return infectionTime ;
    }
    
    /**
     * Mean duration of symptomatic infections.
     * @return 
     */
    abstract protected int getSymptomaticDuration() ;
    
}
