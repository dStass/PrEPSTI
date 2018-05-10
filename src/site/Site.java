/**
 * 
 */
package site;

import java.util.Random ;
/**
 * @author Michael Walker
 *
 *Refers to Site of body at which infection takes place
 */
public class Site {
    // Name of subClass of Site
    final private String site ;

    static Random RAND = new Random() ;

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
    private boolean symptomatic = false ;
    
    
    // Constants that vary according to Site subclass
    // Probability of initial gonorrhoea infection 
    static double INITIAL = 0.3 ;

    // Probability of positive symptomatic status if infected
    static double SYMPTOMATIC_PROBABILITY = 0.5 ;

    // Probability of site transmitting infection, if all other probabilities unity
    //static double TRANSMIT = 0.5 ;

    // Probability of site becoming infected, if all other probabilities unity
    //static double RECEIVE = 0.5 ;
    
    // Probability of treatment being successful
    static double TREATMENT_PROBABILITY = 0.95 ;
    
	
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
     * TODO: May change back to private when details of multiple STIs are worked out.
     * @return true if site initially infected, false otherwise
     */
    public boolean initialiseInfection()
    {
        //infectedStatus = 0 ;
        //symptomatic = false ;

        // Decide initial infection status
        return receiveInfection(INITIAL) ;
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
            chooseSymptomatic() ;
            return true ;
        }
        return false ;
    }

    private boolean chooseSymptomatic()
    {
        double probability = RAND.nextDouble() ;
        symptomatic = (probability < getSymptomaticProbability()) ;
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
     * @return true if treatment successful and false otherwise.
     */
    public boolean treat()
    {
            if (RAND.nextDouble() < TREATMENT_PROBABILITY )   //getTreatmentProbability() )
            {
                clearInfection() ;
                return true ;
            }
            return false ;
    }

    /**
     * 
     * @return Probability of treatment for an STI being successful
     */
    protected double getTreatmentProbability()
    {
        return TREATMENT_PROBABILITY ;
    }

    public void clearInfection()
    {
        infectedStatus = 0 ;
        symptomatic = false ;
    }

    public int getInfectedStatus()
    {
        return infectedStatus ;
    }

    public boolean getSymptomatic()
    {
        return symptomatic ;
    }
}
