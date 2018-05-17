/**
 * 
 */
package agent;

import java.util.logging.Level;
import site.* ;

import java.lang.reflect.*;

/**
 * @author Michael Walker
 *
 */
abstract public class MSM extends Agent {
	
    // The maximum number of relationships an agent may be willing to sustain
    // static int maxRelationships = 20;
    
    /** Site name of Rectum */
    static String RECTUM = "Rectum" ;
    /** Site name of Rectum */
    static String URETHRA = "Urethra" ;
    /** Site name of Rectum */
    static String PHARYNX = "Pharynx" ;
    
    /** The probability of disclosing HIV status if HIV positive */
    static double PROBABILITY_DISCLOSE_POSITIVE_HIV = 0.40 ;
    /** The probability of disclosing HIV status if HIV negative */
    static double PROBABILITY_DISCLOSE_NEGATIVE_HIV = 0.35 ;
    /** Probability of serosorting if HIV positive (2016) */
    static double PROBABILITY_POSITIVE_SERO_SORT = 0.461 ;
    /** Probability of serosorting if HIV negative (2016) */
    static double PROBABILITY_NEGATIVE_SERO_SORT = 0.518 ;
    /** Probability of serosorting in Casual Relationship if HIV positive */
    static double PROBABILITY_POSITIVE_CASUAL_SERO_SORT = 0.431 ;
    /** Probability of serosorting in Casual Relationship if HIV negative */
    static double PROBABILITY_NEGATIVE_CASUAL_SERO_SORT = 0.485 ;
    /** Probability of serosorting in Regular Relationship if HIV positive */
    static double PROBABILITY_POSITIVE_REGULAR_SERO_SORT = 0.274 ;
    /** Probability of serosorting in Regular Relationship if HIV negative */
    static double PROBABILITY_NEGATIVE_REGULAR_SERO_SORT = 0.712 ;
    /** Probability of serosorting in Regular Relationship if HIV positive */
    static double PROBABILITY_POSITIVE_MONOGOMOUS_SERO_SORT 
            = PROBABILITY_POSITIVE_REGULAR_SERO_SORT ;
    /** Probability of serosorting in Regular Relationship if HIV negative */
    static double PROBABILITY_NEGATIVE_MONOGOMOUS_SERO_SORT 
            = PROBABILITY_NEGATIVE_REGULAR_SERO_SORT ;
    /** Probability of sero-positioning if HIV positive */
    static double PROBABILITY_POSITIVE_SERO_POSITION = 0.237 ;
    /** Probability of sero-positioning if HIV negative */
    static double PROBABILITY_NEGATIVE_SERO_POSITION = 0.217 ;
    /** The probability of being on antivirals, given positive HIV status */
    static double PROBABILITY_ANTIVIRAL = 0.85 ;
    /** The probability of being on PrEP, given negative HIV status */
    static double PROBABILITY_PREP = 0.14 ;
    /** Probability of accepting seropositive partner on antiVirals, given 
     * seroSort or seroPosition if HIV positive */
    static double PROBABILITY_POSITIVE_ACCEPT_ANTIVIRAL = 0.5 ;
    /** Probability of accepting seropositive partner on antiVirals, given 
     * seroSort or seroPosition if HIV negative */
    static double PROBABILITY_NEGATIVE_ACCEPT_ANTIVIRAL = 0.5 ;
    
    /** Potential infection site Rectum */
    private Rectum rectum = new Rectum() ;
    /** Potential infection site Urethra */
    private Urethra urethra = new Urethra() ;
    /** Potential infection site Pharynx */
    private Pharynx pharynx = new Pharynx() ;
    /** Array of infection Sites */
    private Site[] sites = {rectum,urethra,pharynx} ;

    /** Whether MSM serosorts, ie match for statusHIV. */
    private boolean seroSort ;
    /** Whether MSM serosorts in Casual Relationship, ie match for statusHIV. */
    private boolean seroSortCasual ;
    /** Whether MSM serosorts in Regular Relationship, ie match for statusHIV. */
    private boolean seroSortRegular ;
    /** Whether MSM serosorts in Monogomous Relationship, ie match for statusHIV. */
    private boolean seroSortMonogomous ;
    /** Whether MSM seropositions, +ve statusHIV never inserts to -ve statusHIV. */
    private boolean seroPosition ;
    /** Given seroSort or seroPosition, whether being on antiviral is sufficient. */
    private boolean acceptAntiViral ;

    /** Status for HIV infection. */
    private boolean statusHIV ;
    /** Whether currently being treated with antiretrovial medication. */
    private boolean antiViralStatus ;
    /** Whether discloses HIV +ve status. */
    private boolean discloseStatusHIV ;
    /** Whether currently taking PrEP. */
    private boolean prepStatus ;
    /** Cycles remaining until next STI screen if on PrEP. */
    private int screenTime ;
	
    /** Transmission probabilities from Urethra to Rectum */
    static double URETHRARECTUM = 0.8 ;
    /** Transmission probabilities from Urethra to Pharynx. */
    static double URETHRAPHARYNX = 0.7 ;
    /** Transmission probabilities from Rectum to Urethra. */ 
    static double RECTUMURETHRA = 0.3 ;
    /** Transmission probabilities from Rectum to Pharynx. */
    static double RECTUMPHARYNX = 0.1 ;
    /** Transmission probabilities in Pharynx to Urethra intercourse. */
    static double PHARYNXURETHRA = 0.2 ;
    /** Transmission probabilities in Pharynx to Rectum intercourse. */
    static double PHARYNXRECTUM = 0.2 ;
    /** Transmission probabilities in Pharynx to Pharynx intercourse (kissing). */
    static double PHARYNXPHARYNX = 0.4 ;
    /** Transmission probabilities in Urethra to Urethra intercourse (cockfighting). */
    static double URETHRAURETHRA = 0.1 ;
    /** Transmission probabilities in Rectum to Rectum intercourse. */
    static double RECTUMRECTUM = 0.2 ;
    
    /** The number of cycles between screenings for MSM on PrEP. */
    static int SCREENCYCLE = 92 ;

    /** SCREENCYCLE getter(). */
    public static int getSCREENCYCLE()
    {
        return SCREENCYCLE ;
    }

    /** The number of Agents invited to any given orgy. */
    int ORGY_SIZE = 8 ;

    /** The number of orgies in the community during a given cycle. */
    int ORGY_NUMBER = 4 ;
    
    
    /**
     * 
     * @param infectedAgent
     * @param clearAgent
     * @param infectionStatus
     * @param infectedSite
     * @param clearSite
     * @return infectProbability (double) the probability of infection of clearSite
     */
    public static double getInfectProbability(Agent infectedAgent, Agent clearAgent, int infectionStatus,
    		Site infectedSite, Site clearSite)
    {
    	double infectProbability = -1.0 ;
        String probabilityString = infectedSite.getSite().toUpperCase() + clearSite.getSite().toUpperCase() ;
        try
        {
            infectProbability = MSM.class.getDeclaredField(probabilityString).getDouble(null) ;
        }
        catch ( Exception e )
        {
            LOGGER.log(Level.SEVERE, "{0} : {1}", new Object[]{e.getClass().getName(), e.getLocalizedMessage()});
            return -1.0 ;
        }
    	return infectProbability ;
    }
    
    /**
     * Choose sites for sexual contact, implementing seropositioning
     * if required. If so, Urethra of positive statusHIV msm is never chosen if 
     * couple is serodiscordant. 
     * Also check if either MSM refrains from anal intercourse in Casual Relationships.
     * @param agent0
     * @param agent1
     * @param relationshipClazzName
     * @return (Site[]) Sites of sexual contact for agent0, agent1, respectively.
     */
    public static Site[] chooseSites(Agent agent0, Agent agent1)    //, String relationshipClazzName)
    {
        return chooseSites((MSM) agent0, (MSM) agent1) ;
    }
    
    /**
     * Choose sites for sexual contact, implementing seropositioning
     * if required. If so, Urethra of positive statusHIV msm is never chosen if 
     * couple is serodiscordant. 
     * Also check if either MSM refrains from anal intercourse in Casual Relationships.
     * @param msm0
     * @param msm1
     * @param relationshipClazzName
     * @return (Site[]) Sites of sexual contact for msm0, msm1, respectively.
     */
    public static Site[] chooseSites(MSM msm0, MSM msm1)    //, String relationshipClazzName)
    {
        if (msm0.seroPosition || msm1.seroPosition)
        {
            if (msm0.statusHIV != msm1.statusHIV)
            {
                Site site0 ;
                Site site1 ;
                if (msm0.statusHIV)
                {
                    site0 = msm0.chooseNotUrethraSite() ;
                    site1 = msm1.chooseSite(site0) ;
                }
                else    // msm1.statusHIV == true
                {
                    site1 = msm1.chooseNotUrethraSite() ;
                    site0 = msm0.chooseSite(site1) ;
                }
                return new Site[] {site0,site1} ;
            }
        }
        Site site0 = msm0.chooseSite() ;
        Site site1 = msm1.chooseSite(site0) ;
        return new Site[] {site0,site1} ;
    }
    
    	
    // Odds of an MSM being safeMSM
    static int SAFE_ODDS = 68 ;
    // Odds of an MSM being riskyMSM
    static int RISKY_ODDS = 32 ;
    // Sum of safeOdds and riskyOdds
    static int TOTAL_ODDS = RISKY_ODDS + SAFE_ODDS ;
    	
    /**
     * Choose whether MSM is RiskyMSM or SafeMSM
     * @startAge - age of MSM at sexual 'birth'.
     * @return - one of subclass RiskyMSM or SafeMSM
     */
    public static MSM birthMSM(int startAge)
    {
        Class clazz ;
        int choice = RAND.nextInt(TOTAL_ODDS) ;
    	if (choice < SAFE_ODDS)
    		clazz = SafeMSM.class ;
        else 
            clazz = RiskyMSM.class ;
        try
        {
            return (MSM) clazz.getConstructor(int.class).newInstance(startAge);
        }
        catch ( Exception e )
        {
            LOGGER.log(Level.SEVERE, "{0} {1}", new Object[]{e.getClass().getCanonicalName(), clazz.getCanonicalName()});
        }
        return new SafeMSM(-1) ;
    }
   

    /**
     * 
     * Specifies Agent subclass Men having Sex with Men. Necessary to call super.constructor()
     * @startAge - Age of MSM at sexual 'birth'
     */

    public MSM(int startAge) 
    {
        super(startAge) ;
        initStatus(startAge) ;
    }

    /**
     * Initialises status' at construction of MSM. 
     * Ensures that those MSM who come out during simulation are initially
     * HIV free (statusHIV == false).
     */
    final private void initStatus(int startAge)
    {
        //requireDiscloseStatusHIV = (rand.nextDouble() < probabilityRequireDiscloseHIV) ;
        statusHIV = (RAND.nextDouble() < getProbabilityHIV()) ;

        // Sets antiViral status, ensuring it is true only if statusHIV is true
        setAntiViralStatus(RAND.nextDouble() < getAntiviralProbability()) ;

        // Randomly set PrEP status, ensuring it is true only if statusHIV is true
        initPrepStatus(RAND.nextDouble() < getProbabilityPrep()) ;
        

        if (startAge < 0)    // MSM generated at outset, represent initial population
        {
            // Initialises infectedStatus, ensuring consistency with Site.infectedStatus
            initInfectedStatus() ;
        }            
        else if (false)   // TODO: Assume HIV free on sexual birth?
        {
            setStatusHIV(false) ;
            //clearInfected() ;
        }

        // Sets whether disclosesHIV, allowing for statusHIV
        double probabilityDiscloseHIV = getProbabilityDiscloseHIV() ;
        discloseStatusHIV = (RAND.nextDouble() < probabilityDiscloseHIV) ;

        if (discloseStatusHIV)
        {
            seroSortCasual = ((RAND.nextDouble() < getProbabilitySeroSortCasual(statusHIV)/probabilityDiscloseHIV) && discloseStatusHIV) ;
            seroSortRegular = ((RAND.nextDouble() < getProbabilitySeroSortRegular(statusHIV)/probabilityDiscloseHIV) && discloseStatusHIV) ;
            seroSortMonogomous = ((RAND.nextDouble() < getProbabilitySeroSortMonogomous(statusHIV)/probabilityDiscloseHIV) && discloseStatusHIV) ;
            seroPosition = ((RAND.nextDouble() < getProbabilitySeroPosition(statusHIV)/probabilityDiscloseHIV) && discloseStatusHIV) ;
        }
        else    // Cannot seroSort or SeroPosition without disclosing statusHIV
        {
            seroSort = false ;
            seroPosition = false ;
        }
    }

    /**
     * Initialises MSM infectedStatus while ensuring consistency with 
     * Site.infectedStatus .
     */
    final protected void initInfectedStatus()
    {
        boolean infected = false ;    //  getInfectedStatus() ;
        for (Site site : sites)
        {
            infected = infected || site.initialiseInfection() ;
            setSymptomatic(site) ;
        }
        setInfectedStatus(infected) ;
    }
        
    /**
     * Should generate Site[] and not call Site[] MSM.sites to avoid error/complications
     * of ensuring that MSM.sites is updated with every call to setSitename().
     * @return (Site[]) of Sites belonging to this MSM
     */
    @Override
    protected Site[] getSites()
    {
        return new Site[] {rectum,urethra,pharynx} ;
    }
        
    /**
     * Used when choosing Site for sexual encounter
     * @return random choice of rectum, urethra or pharynx
     */
    @Override
    protected Site chooseSite()
	{
            int index = RAND.nextInt(3) ;
            switch (index) 
            { 
                case 0:
                    return rectum ;
                case 1: 
                    return urethra ;
                default: 
                    return pharynx ;
            }
	}
	
    @Override
    protected Site chooseSite(Site site)
    {
        if (site.getSite().equals(RECTUM))
        {
            int index = RAND.nextInt(2) ;
            if (index == 0) return urethra ;
            else return pharynx ;
        }
        else
        {
            return chooseSite() ;
        }
    }
    
    

    protected Site chooseNotRectumSite() 
    {
        int index = RAND.nextInt(2) ;
        if (index == 0)
            return urethra ;
        return pharynx ;
    }

    protected Site chooseNotRectumSite(Site site) 
    {
        if (PHARYNX.equals(site.getSite()))
            return urethra ;
        return pharynx ;
    }

    protected Site chooseNotUrethraSite() 
    {
        int index = RAND.nextInt(2) ;
        if (index == 0)
            return rectum ;
        return pharynx ;
    }

    protected Rectum getRectum()
    {
        return rectum ;
    }

    protected void setRectum(Rectum rectum)
    {
        this.rectum = rectum ;
    }
    
    protected Urethra getUrethra()
    {
        return urethra ;		
    }

    protected void setUrethra(Urethra urethra)
    {
        this.urethra = urethra ;
    }
    
    protected Pharynx getPharynx()
    {
        return pharynx ;
    }

    protected void setPharynx(Pharynx pharynx)
    {
        this.pharynx = pharynx ;
    }
    
    protected boolean getStatusHIV()
    {
        return statusHIV ;
    }
    
    /**
     * Setter of statusHIV.
     * Also ensures that prepStatus and antiViralStatus do not have
     * inappropriate values.
     * @param status 
     */
    public void setStatusHIV(boolean status)
    {
        statusHIV = status ;
        if (status)
            setPrepStatus(false) ;
        else
            setAntiViralStatus(false) ;
    }

    public boolean getSeroSort(String relationshipClazzName)    //, Boolean status)
    {
        Boolean serosort  = false ;
        String returnString = "seroSort" + relationshipClazzName ;
        try
        {
            serosort = MSM.class.getDeclaredField(returnString).getBoolean(this) ;
        }
        catch ( Exception e )
        {
            LOGGER.log(Level.SEVERE, "{0} {1}", new Object[] {returnString, e.getClass().getSimpleName()});
        }
        return serosort ;
    }
    
    /**
     * Setter of seroSort, used for unit testing
     * @param sort 
     */
    public void setSeroSort(boolean sort)
    {
        discloseStatusHIV = (discloseStatusHIV || sort) ;
        seroSort = sort ;
    }

    public boolean getSeroPosition()
    {
        return seroPosition ;
    }
    
    /**
     * Setter of seroPosition. Used for unit testing.
     * Changes discloseStatus to true if (position == true)
     * @param position 
     */
    public void setSeroPosition(boolean position)
    {
        discloseStatusHIV = (discloseStatusHIV || position) ;
        seroPosition = position ;
    }

    protected boolean getAntiViralStatus()
    {
        return antiViralStatus ;
    }
    
    /**
     * Setter of antiViralStatus. 
     * Will only set it to true if statusHIV is true.
     * @param status 
     */
    public void setAntiViralStatus(boolean status)
    {
        antiViralStatus = status && statusHIV ;
    }

    /**
     * 
     * @return true if MSM discloses statusHIV, false otherwise
     */
    final protected boolean getDiscloseStatusHIV()
    {
        return discloseStatusHIV ;
    }
    
    /**
     * Setter of discloseStatusHIV. Used for unit testing
     * @param disclose 
     */
    public void setDiscloseStatusHIV(boolean disclose)
    {
        discloseStatusHIV = disclose ;
        return ;
    }

    protected boolean getPrepStatus()
    {
        return prepStatus ;
    }
    
    private void initPrepStatus(boolean prep)
    {
        setPrepStatus(prep) ;
        
        // Randomly set timer for first STI screen if on PrEP
        if (prepStatus)
            screenTime = RAND.nextInt(SCREENCYCLE) ;
    }
    /**
     * Setter of prepStatus. Used for unit testing
     * @param prep 
     */
    public void setPrepStatus(boolean prep)
    {
        prepStatus = prep && (!statusHIV) ;
    }

    /**
     * Whether to enter a proposed relationship of class relationshipClazz .
     * Currently according to whether in a monogomous relationship and 
     * the number of relationships already entered compared to promiscuity.
     * 
     * @param relationshipClazzName - name relationship subclass
     * @param partner - agent for sharing proposed relationship
     * @return true if accept and false otherwise
     */
    public boolean consent(String relationshipClazzName, Agent partner)
    {
        if (getSeroSort(relationshipClazzName))
            if (!String.valueOf(getStatusHIV()).equals(declareStatus()))
                return false ;
        return super.consent(relationshipClazzName, partner) ;
    }
    
    /**
     * How would the MSM respond if asked to disclose their statusHIV
     * @return String representation of statusHIV if (discloseStatusHIV), otherwise 'none'
     */
    @Override
    public String declareStatus()
    {
        if (discloseStatusHIV)
            return Boolean.toString(statusHIV) ;
        return NONE ;
    }

    @Override
    abstract int getMaxRelationships();
    
    abstract double getProbabilityHIV() ;
    
    final private double getAntiviralProbability()
    {
        return PROBABILITY_ANTIVIRAL ;
    }
    
    abstract double getProbabilityDiscloseHIV() ;
    
    protected double getProbabilityPrep() 
    {
        return PROBABILITY_PREP ;
    }
    
    protected double getProbabilitySeroSort(Boolean hivStatus)
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_SERO_SORT ;
        return PROBABILITY_NEGATIVE_SERO_SORT ;
    }

        protected double getProbabilitySeroSortCasual(Boolean hivStatus)
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_CASUAL_SERO_SORT ;
        return PROBABILITY_NEGATIVE_CASUAL_SERO_SORT ;
    }

    protected double getProbabilitySeroSortRegular(Boolean hivStatus)
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_REGULAR_SERO_SORT ;
        return PROBABILITY_NEGATIVE_REGULAR_SERO_SORT ;
    }

    protected double getProbabilitySeroSortMonogomous(Boolean hivStatus)
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_MONOGOMOUS_SERO_SORT ;
        return PROBABILITY_NEGATIVE_MONOGOMOUS_SERO_SORT ;
    }

    protected double getProbabilitySeroPosition(Boolean hivStatus) 
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_SERO_POSITION ;
        return PROBABILITY_NEGATIVE_SERO_POSITION ;
    }

    /**
     * Decides probabilistically whether MSM chooses to use a condom in a given encounter.
     * Choice is based on type of Relationship and properties of msm
     * @param msm
     * @param relationshipName
     * @return true if condom is to be used, false otherwise
     */
    @Override
    abstract protected boolean chooseCondom(String relationshipClazzName, Agent msm);
    
    /**
     * 
     * @return (int) the number of orgies in a MSM community per cycle
     */
    @Override
    public int getOrgyNumber()
    {
        return ORGY_NUMBER ;
    }
    
    /**
     * 
     * @return (int) number of MSM invited to join any given orgy
     */
    public int getOrgySize()
    {
        return ORGY_SIZE ;
    }

    /**
     * 
     * @return (double) the probability of MSM joining an orgy when invited
     */
    abstract public double getJoinOrgyProbability() ;
    
    /**
     * Probability of MSM screening on that day. Same as Agent unless prepStatus true
     * @param args
     * @return for PrEP users, 1.0 if cycle multiple of screenCycle, 0.0 otherwise
     *     for non-PrEP users, random double between 0.0 and 1.0
     */    
    @Override
    public double getScreenProbability(String[] args)
    {
        if (prepStatus)
        {
            // Find current cycle
            int cycle = Integer.valueOf(args[0]) ;
            
            // Countdown to next STI screen
            screenTime-- ;
            
            // Those on antivirals test every three months (92 days) on a given day
            if ( screenTime == 0)
            {
                screenTime = SCREENCYCLE ;
                return 1.0 ;
            }
            else
                return 0.0 ;
        }
        return super.getScreenProbability(args) ;
    }
    
   
}
