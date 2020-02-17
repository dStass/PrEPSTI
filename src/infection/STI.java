/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infection;

import java.util.Arrays ;
import java.util.ArrayList ;
import java.util.HashMap ;
//import java.lang.reflect.* ;
import java.util.Random;
import reporter.Reporter;

/**
 *
 * @author MichaelWalker
 */
abstract public class STI {
    
    /**
     * Generates list if subClasses of STI. This Method should only need to be 
     * run once at the beginning of the simulation.
     */
    static public void GEN_STI_LIST()
    {
        if (!STI_LIST.isEmpty())
        {
            LOGGER.warning("This Method has been run once already!") ;
        }
        
        Class[] stiClasses = STI.class.getClasses() ;
        ArrayList<Class> stiList = new ArrayList<Class>() ;
        
        for (Class stiClass : stiClasses)
        {
            LOGGER.info(stiClass.getSimpleName()) ;
            if (STI.class.equals(stiClass.getSuperclass()))
                stiList.add(stiClass) ;
        }
        LOGGER.info(stiList.toString()) ;
        STI_LIST = stiList ;
    }
    
    /** ArrayList of specific subClasses of STI */
    static ArrayList<Class> STI_LIST ;
    
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("site") ;

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
    
    /** Generate and record Random number seed. */
    static long RANDOM_SEED = System.nanoTime() ;
    static Random RAND = new Random(RANDOM_SEED) ;
    
    /**
     * 
     * @return Probability of initial infection at site siteName.
     */
//    static public Double getInfectedProbability(String site)
//    {
//        return INITIAL_PREVALENCE.get(site) ;
//    }
    
    abstract public Double getInfectedProbability(String site) ;

//    static HashMap<String,Double> SYMPTOMATIC_PROBABILITY ;
//    
//    static HashMap<String[],Double> TRANSMISSION_PROBABILITY ;
//    
//    // Probability of transmission from Urethra to Urethra
//    static double URETHRA_URETHRA = 0.1 ;
//    // Probability of transmission from Urethra to Rectum
//    static double URETHRA_RECTUM = 0.8 ;
//    // Probability of transmission from Urethra to Pharynx
//    static double URETHRA_PHARYNX = 0.6 ;
//    // Probability of transmission from Rectum to Urethra
//    static double RECTUM_URETHRA = 0.5 ;
//    // Probability of transmission from Rectum to Rectum
//    static double RECTUM_RECTUM = 0.1 ;
//    // Probability of transmission from Rectum to Pharynx
//    static double RECTUM_PHARYNX = 0.2 ;
//    // Probability of transmission from Pharynx to Urethra
//    static double PHARYNX_URETHRA = 0.5 ;
//    // Probability of transmission from Pharynx to Rectum
//    static double PHARYNX_RECTUM = 0.5 ;
//    // Probability of transmission from Pharynx to Pharynx
//    static double PHARYNX_PHARYNX = 0.5 ;
//    
    /** Probability that condom use will block STI transmission. */
    static double CONDOM_EFFECT = 0.90 ;
    
    /** 
     * Probability of initial infection at each Site.
     */ 
//    static HashMap<String,Double> INITIAL_PREVALENCE ;
//
//    static HashMap<String,Integer> INFECTION_DURATION ;

    /** Minimum incubation period. */
    static int MIN_INCUBATION = 7 ; //2 ;
    
    /** Range of incubation periods. */
    static int RANGE_INCUBATION = 0 ;

    /** Index of infection in Site */
    int nbInfection ;
    
    /** Index of STI subClass */
    int stiIndex ;
    
    /** Whether this infection is symptomatic */
    boolean symptomatic ;
    
    /** Cycles remaining in incubation period */
    int incubationTime ; 
    
    /** Cycles remaining before STI clears itself */
    int infectionTime ;
    
    String siteName ;
    
    public void STI(String site)
    {
        if (STI_LIST.isEmpty())
            GEN_STI_LIST() ;
        stiIndex = STI_LIST.indexOf(this.getClass().asSubclass(this.getClass())) ;
        
        siteName = site ;
    }
    
    /**
     * Getter for nbInfection.
     * @return (int) Index of STI
     */
    public int getNbInfection()
    {
        return nbInfection ;
    }
    
    /**
     * Set nbInfection.
     * @param nb
     * @return Description INFECTION_NB or warning if nb != 0
     */
    public void setNbInfection(int nb)
    {
        nbInfection = nb ;
    }
    
    /**
     * Getter for stiIndex.
     * @return (int) Index of STI subClass
     */
    public int getStiIndex()
    {
        return stiIndex ;
    }
    
    /**
     * 
     * @return (double) Probability of Site siteName being symptomatic when infected
     */
//    public double getSymptomaticProbability()
//    {
//        return SYMPTOMATIC_PROBABILITY.get(siteName) ;
//    }
    
    abstract public double getSymptomaticProbability() ;
    
    /**
     * 
     * @return (double) Probability of transmission to Site toSite
     */ 
//    public double getTransmissionProbability(String toSite)
//    {
//        return TRANSMISSION_PROBABILITY.get(new String[] {siteName, toSite}) ;
//    }
    abstract double getTransmissionProbability(String toSite) ;
    
    /**
     * 
     * @return (Double) Probability of condom preventing STI transmission, if used
     */
    public double getCondomEffect()
    {
        return CONDOM_EFFECT ;
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
            return true ;
        
        return false ;
    }
    
    /**
     * Randomly chooses how long an infection lasts, assuming it is untreated.
     * @return Randomly chosen from Gamma Distribution from half mean cutoff.
     */
    public int applyInfectionDuration()
    {
        infectionTime = RAND.nextInt(getInfectionDuration() - 1) + 1 ;
        return infectionTime ;
    }
    
    /**
     * Mean duration of asymptomatic infections at Site siteName.
     * @return 
     */
//    protected int getInfectionDuration() 
//    {
//        return INFECTION_DURATION.get(siteName) ;
//    }
    abstract protected int getInfectionDuration() ;
    
    /**
     * 
     * @return (String) giving values of the Site's important properties.
     */
    public String getCensusReport()
    {
        String censusReport = Reporter.ADD_REPORT_PROPERTY(siteName,toString()) ;
        // Order is important, symptomatic must be before all other infection-related properties.
        censusReport += Reporter.ADD_REPORT_PROPERTY("symptomatic",symptomatic) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("infectionTime",infectionTime) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("incubationTime",incubationTime) ;
        
        return censusReport ;
    }
    /**
     * 
     * @return (String) Name of infection. 
     */
    public String toString()
    {
        return this.getClass().asSubclass(this.getClass()).getSimpleName() ;
    }
    
}
