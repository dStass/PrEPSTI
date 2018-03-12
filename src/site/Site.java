/**
 * 
 */
package site;

import java.util.Random ;
/**
 * @author MichaelWalker
 *
 *Refers to Site of body at which infection takes place
 */
public class Site {
	// Name of subClass of Site
	private String site ;
	
	static Random rand = new Random() ;
	
	// Array of available infection status'
	static String[] states = new String[] {"clear","gonorrhoea"} ;
	
	// infection indices given by log_2 of pure infectStatus
	static int GONORRHOEA = 0 ;
	//static int SYPHILIS = 1 ;
	//static int CHLAMYDIA = 2 ;
	
    // Initial infection, possibly NoInfection
	// TODO: Consider introducing Infection class instead of 0,1 infectStatus
    //private Infection infection ;
    private int infectStatus ; 
    private boolean symptomatic ;
    
    
    // Constants that vary according to Site subclass
	// Probability of initial gonorrhoea infection 
	static double INITIAL = 0.3 ;
	
	// Probability of positive symptomatic status if infected
	static double SYMPTOMATIC = 0.5 ;
	
	// Probability of site transmitting infection, if all other probabilities unity
	//static double TRANSMIT = 0.5 ;

	// Probability of site becoming infected, if all other probabilities unity
    //static double RECEIVE = 0.5 ;
    
    // Probability of successful treatment
    static double TREATMENT = 0.95 ;
    
	
	/**
	 * 
	 */
	public Site() 
	{
		initialiseInfection() ;
		Class<?> clazz = this.getClass() ;
		site = clazz.asSubclass(clazz).getSimpleName() ;
	}
	
	/**
	 * Called once to initialise infection status.
	 * @return true if site initially infected, false otherwise
	 */
	private boolean initialiseInfection()
	{
		infectStatus = 0 ;
	    symptomatic = false ;
	    
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
		if (rand.nextDouble() < transmitProbability )
		{
			infectStatus = 1 ;
			chooseSymptomatic() ;
			return true ;
		}
		return false ;
	}
	
	private boolean chooseSymptomatic()
	{
	    double probability = rand.nextDouble() ;
	    symptomatic = (probability < SYMPTOMATIC) ;
		return symptomatic ;
	}
	
	/**
	 * Probabilistically treat infection at Site
	 * @return true if treatment successful and false otherwise.
	 */
	public boolean treat()
	{
		if (rand.nextDouble() < TREATMENT )
		{
			clearInfection() ;
			return true ;
		}
		return false ;
	}
	
	private void clearInfection()
	{
		infectStatus = 0 ;
		symptomatic = false ;
		return ;
	}
	
	public int getInfectStatus()
	{
		return infectStatus ;
	}
	
	public boolean getSymptomatic()
	{
		return symptomatic ;
	}
}
