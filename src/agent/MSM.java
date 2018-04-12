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
    
    // Site name of Rectum
    static String RECTUM = "Rectum" ;
    // Site name of Rectum
    static String PENIS = "Penis" ;
    // Site name of Rectum
    static String PHARYNX = "Pharynx" ;
    
    // Potential infection site Rectum
    private Rectum rectum = new Rectum() ;
    // Potential infection site Penis
    private Penis penis = new Penis() ;
    // Potential infection site Pharynx
    private Pharynx pharynx = new Pharynx() ;
    // Array of infection Sites
    private Site[] sites = {rectum,penis,pharynx} ;

    // Whether MSM serosorts, ie match for statusHIV
    private boolean seroSort ;
    // Whether MSM seropositions, +ve statusHIV never inserts to -ve statusHIV
    private boolean seroPosition ;
    // Whether MSM requires partner to discloseStatusHIV before sex
    //private boolean requireDiscloseStatusHIV ;
    // Status' for HIV infection, antiviral treatment if infected
    private boolean statusHIV ;
    // Whether currently being treated with antiretrovial medication
    private boolean antiViralStatus ;
    // Whether discloses HIV +ve status
    private boolean discloseStatusHIV ;
    // Whether currently taking PrEP
    private boolean prepStatus ;
	
    // Transmission probabilities from Penis to Rectum
    static double PENISRECTUM = 0.8 ;
    // Transmission probabilities from Penis to Pharynx
    static double PENISPHARYNX = 0.7 ;
    // Transmission probabilities from Rectum to Penis 
    static double RECTUMPENIS = 0.3 ;
    // Transmission probabilities from Rectum to Pharynx
    static double RECTUMPHARYNX = 0.1 ;
    // Transmission probabilities in Pharynx to Penis intercourse
    static double PHARYNXPENIS = 0.2 ;
    // Transmission probabilities in Pharynx to Rectum intercourse
    static double PHARYNXRECTUM = 0.2 ;
    // Transmission probabilities in Pharynx to Pharynx intercourse (kissing)
    static double PHARYNXPHARYNX = 0.4 ;
    // Transmission probabilities in Penis to Penis intercourse (cockfighting)
    static double PENISPENIS = 0.1 ;
    // Transmission probabilities in Rectum to Rectum intercourse
    static double RECTUMRECTUM = 0.2 ;
    
    // How often do MSM on PrEP get screened
    static int SCREENCYCLE = 92 ;

    // SCREENCYCLE getter()
    public static int getSCREENCYCLE()
    {
        return SCREENCYCLE ;
    }

        // The number of Agents invited to any given orgy
        int ORGY_SIZE = 8 ;
        
        // The number of orgies in the community in a given cycle
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
     * if required. If so, Penis of positive statusHIV msm is never chosen if 
     * couple is serodiscordant.
     * @param msm0
     * @param msm1
     * @return 
     */
    public static Site[] chooseSites(MSM msm0, MSM msm1)
    {
        if (msm0.seroPosition || msm1.seroPosition)
        {
            if (msm0.statusHIV != msm1.statusHIV)
            {
                Site site0 ;
                Site site1 ;
                if (msm0.statusHIV)
                {
                    site0 = msm0.chooseNotPenisSite() ;
                    site1 = msm1.chooseSite(site0) ;
                }
                else
                {
                    site1 = msm1.chooseNotPenisSite() ;
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
    static int SAFE_ODDS = 5 ;
    // Odds of an MSM being riskyMSM
    static int RISKY_ODDS = 5 ;
    // Sum of safeOdds and riskyOdds
    static int TOTAL_ODDS = RISKY_ODDS + SAFE_ODDS ;
    	
    /**
     * Choose whether MSM is RiskyMSM or SafeMSM
     * @return Class - one of subclass RiskyMSM or SafeMSM
     */
    public static Object birthMSM(int startAge)
    {
        Class clazz ;
        int choice = RAND.nextInt(TOTAL_ODDS) ;
    	if (choice < SAFE_ODDS)
    		clazz = SafeMSM.class ;
        else 
            clazz = RiskyMSM.class ;
        try
        {
            return clazz.getConstructor(int.class).newInstance(startAge);
        }
        catch ( Exception e )
        {
            LOGGER.log(Level.SEVERE, "{0} {1}", new Object[]{e.getClass().getCanonicalName(), clazz.getCanonicalName()});
        }
        return new Object() ;
    }
   

	/**
	 * 
     * Specifies Agent subclass Men having Sex with Men. Necessary to call super.constructor()
	*/
    
	public MSM(int startAge) 
        {
	    super(startAge) ;
            initStatus() ;
        }
        
        /**
         * Initialises status' at construction of MSM. Separated from constructor 
         * so that subclass static fields can be called
         * TODO: Check that inherited version calls correct static fields
         */
        final private void initStatus()
        {
            //requireDiscloseStatusHIV = (rand.nextDouble() < probabilityRequireDiscloseHIV) ;
            statusHIV = (RAND.nextDouble() < getProbabilityHIV()) ;
            if (statusHIV)
            {
                antiViralStatus = (RAND.nextDouble() < getAntiviralProbability()) ;
                prepStatus = false ;
            }
            else
            {
                prepStatus = (RAND.nextDouble() < getProbabilityPrep()) ;
                antiViralStatus = false ;
            }
            discloseStatusHIV = (RAND.nextDouble() < getProbabilityDiscloseHIV()) ;
            if (discloseStatusHIV)
            {
        	seroSort = ((RAND.nextDouble() < getProbabilitySeroSort()) && discloseStatusHIV) ;
                seroPosition = ((RAND.nextDouble() < getProbabilitySeroPosition()) && discloseStatusHIV) ;
            }
            else    // Cannot seroSort or SeroPosition without disclosing statusHIV
            {
                seroSort = false ;
                seroPosition = false ;
            }
            return ;
        }

    /**
     * Should generate Site[] and not call Site[] MSM.sites to avoid error/complications
     * of ensuring that MSM.sites is updated with every call to setSitename().
     * @return (Site[]) of Sites belonging to this MSM
     */
    @Override
    protected Site[] getSites()
    {
        return new Site[] {rectum,penis,pharynx} ;
    }
        
    /**
     * Used when choosing Site for sexual encounter
     * @return randomly choice of rectum, penis or pharynx
     */
    protected Site chooseSite()
	{
            int index = RAND.nextInt(3) ;
            if (index == 0) 
                return rectum ;
            else if (index == 1) 
                return penis ;
            else 
                return pharynx ;
	}
	
    protected Site chooseSite(Site site)
    {
        if (site.getSite().equals(RECTUM))
        {
            int index = RAND.nextInt(2) ;
            if (index == 0) return penis ;
            else return pharynx ;
        }
        else
        {
            return chooseSite() ;
        }
    }

    protected Site chooseNotPenisSite() 
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
    
    protected Penis getPenis()
    {
        return penis ;		
    }

    protected void setPenis(Penis penis)
    {
        this.penis = penis ;
    }
    
    protected Pharynx getPharynx()
    {
        return pharynx ;
    }

    protected void setPharynx(Pharynx pharynx)
    {
        this.pharynx = pharynx ;
    }
    
    private boolean getStatusHIV()
    {
        return statusHIV ;
    }
    
    /**
     * Setter of statusHIV, used for unit testing
     * @param status 
     */
    public void setStatusHIV(boolean status)
    {
        statusHIV = status ;
        return ;
    }

    public boolean getSeroSort()
    {
        return seroSort ;
    }
    
    /**
     * Setter of seroSort, used for unit testing
     * @param sort 
     */
    public void setSeroSort(boolean sort)
    {
        discloseStatusHIV = (discloseStatusHIV || sort) ;
        seroSort = sort ;
        return ;
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
        return ;
    }

    private boolean getAntiViralStatus()
    {
        return antiViralStatus ;
    }
    
    public void setAntiViralStatus(boolean status)
    {
        antiViralStatus = status ;
        return ;
    }

    private boolean getDiscloseStatusHIV()
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

    private boolean getPrepStatus()
    {
        return prepStatus ;
    }
    
    /**
     * Setter of prepStatus. Used for unit testing
     * @param prep 
     */
    public void setPrepStatus(boolean prep)
    {
        prepStatus = prep ;
        return ;
    }

    /**
     * How would the MSM respond if asked to disclose their statusHIV
     * @return String representation of statusHIV if (discloseStatusHIV), otherwise 'none'
     */
    public String declareStatus()
    {
        if (discloseStatusHIV)
            return Boolean.toString(statusHIV) ;
        return "none" ;
    }

    /**
     * Consent also affected by sero- Sorting and Position and
     * partners disclosure of statusHIV 
     * @param relationshipClazzName
     * @param agent
     * @return 
     */
    @Override
    public boolean consent(String relationshipClazzName, Agent agent)
    {
        // Agent superclass has own criteria
        if (! super.consent(relationshipClazzName, agent))
            return false;
        MSM partner = (MSM) agent ;
        String partnerDisclosure = partner.declareStatus() ;
        Boolean partnerSeroPosition = partner.getSeroPosition() ;
        if (seroSort)
        {
            // not if partner does not disclose
            if ("none".equalsIgnoreCase(partnerDisclosure))
                return false ;
            else    // check for serodiscordance
                return (statusHIV == Boolean.getBoolean(partnerDisclosure)) ;
        }
        if (seroPosition)
        {
            // not if partner does not disclose
            if ("none".equalsIgnoreCase(partnerDisclosure))
                return false ;
            else 
                return partnerSeroPosition ;
        }
        return true ;
    }
	
    abstract int getMaxRelationships() ;
    
    abstract double getProbabilityHIV() ;
    
    abstract double getAntiviralProbability() ;
    
    abstract double getProbabilityDiscloseHIV() ;
    
    abstract double getProbabilityPrep() ;
    
    abstract double getProbabilitySeroSort() ;

    abstract double getProbabilitySeroPosition() ;

    /**
     * 
     * @return (int) the number of orgies in a MSM community per cycle
     */
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
            int cycle = Integer.valueOf(args[0]) ;
            // Those on antivirals test every three months (92 days) on a given day
            if ( Math.floorMod(cycle, SCREENCYCLE) == 0)
                return 1.0 ;
            else
                return 0.0 ;
        }
        return super.getScreenProbability(args) ;
    }
    
    
}
