/**
 * 
 */
package agent;

import site.* ;

/**
 * @author Michael Walker
 *
 */
public class MSM extends Agent {
	
    // The maximum number of relationships an agent may be willing to sustain
    static int maxRelationships = 20;
    
    // Potential infection sites
    private Rectum rectum ;
    private Penis penis ;
    private Pharynx pharynx ;

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
	
    // Associated probabilities for the above
    static double probabilitySeroSort = 0.4 ;
    static double probabilitySeroPosition = 0.4 ;
    static double probabilityRequireDiscloseHIV = 0.05 ;
    static double probabilityHIV = 0.05 ;
    static double antiViralProbability = 0.7 ;
    static double probabilityDiscloseHIV = 0.8 ;
    static double probabilityPrep = 0.2 ;

    // Transmission probabilities fromsiteTosite
    static double PENISRECTUM = 0.8 ;
    static double PENISPHARYNX = 0.7 ;
    static double RECTUMPENIS = 0.3 ;
    static double RECTUMPHARYNX = 0.1 ;
    static double PHARYNXPENIS = 0.2 ;
    static double PHARYNXRECTUM = 0.2 ;
    
    // How often do MSM on PrEP get screened
    private static int screenCycle = 92 ;
    // getter() for screenCycle
    public static int getScreenCycle()
    {
        return screenCycle ;
    }

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
    	double infectProbability = 0.0 ;
    	if ("penis".equals(infectedSite.getSite()))
    	{
    		if ("rectum".equals(clearSite.getSite())) infectProbability = PENISRECTUM ;
    		else if ("pharynx".equals(clearSite.getSite())) infectProbability = PENISPHARYNX ;
    	}
    	else if ("rectum".equals(clearSite.getSite())) 
    	{
    		if ("penis".equals(clearSite.getSite())) infectProbability = RECTUMPENIS ;
    		else if ("pharynx".equals(clearSite.getSite())) infectProbability = RECTUMPHARYNX ;
    	}
    	else    // "pharynx" == clearSite.getSite()
    	{
    		if ("penis".equals(clearSite.getSite())) infectProbability = PHARYNXPENIS ;
    		else infectProbability = PHARYNXRECTUM ;    // "rectum" == clearSite.getSite()
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
    static int safeOdds = 5 ;
    // Odds of an MSM being riskyMSM
    static int riskyOdds = 5 ;
    
    /**
     * Choose whether MSM is RiskyMSM or SafeMSM
     * @return Class - one of subclass RiskyMSM or SafeMSM
     */
    public static Class chooseMSM()
    {
    	int totalOdds = riskyOdds + safeOdds ;
    	int choice = rand.nextInt(totalOdds) ;
    	if (choice < safeOdds)
    		return SafeMSM.class ;
    	return RiskyMSM.class ;
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
        private void initStatus()
        {
            //requireDiscloseStatusHIV = (rand.nextDouble() < probabilityRequireDiscloseHIV) ;
            statusHIV = (rand.nextDouble() < probabilityHIV) ;
            if (statusHIV)
            {
                antiViralStatus = (rand.nextDouble() < antiViralProbability) ;
                prepStatus = false ;
            }
            else
            {
                prepStatus = (rand.nextDouble() < probabilityPrep) ;
                antiViralStatus = false ;
            }
            discloseStatusHIV = (rand.nextDouble() < probabilityDiscloseHIV) ;
            if (discloseStatusHIV)
            {
        	seroSort = ((rand.nextDouble() < probabilitySeroSort) && discloseStatusHIV) ;
                seroPosition = ((rand.nextDouble() < probabilitySeroPosition) && discloseStatusHIV) ;
            }
            else    // Cannot seroSort or SeroPosition without disclosing statusHIV
            {
                seroSort = false ;
                seroPosition = false ;
            }
            return ;
        }

	/**
         * Adds Sites rectum, penis, and pharynx
         */
        @Override
	protected void setSites() {
		rectum = new Rectum() ;
		penis = new Penis() ;
		pharynx = new Pharynx() ;
		
		// Assign sites to Sites[] sites[]
		Site[] sites = {penis,rectum,pharynx} ;
		setSites(sites) ;
		return ;
	}
	
    /**
     * Used when choosing Site for sexual encounter
     * @return randomly choice of rectum, penis or pharynx
     */
    protected Site chooseSite()
	{
            int index = rand.nextInt(3) ;
            if (index == 0) 
                return rectum ;
            else if (index == 1) 
                return penis ;
            else 
                return pharynx ;
	}
	
    protected Site chooseSite(Site site)
    {
        if (site.getSite().equals("rectum"))
        {
            int index = rand.nextInt(2) ;
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
        int index = rand.nextInt(2) ;
        if (index == 0)
            return rectum ;
        return pharynx ;
    }

    protected Rectum getRectum()
    {
        return rectum ;
    }

    protected Penis getPenis()
    {
        return penis ;		
    }

    protected Pharynx getPharynx()
    {
        return pharynx ;
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
     * TODO: FIXME: Find way to pass relevant partner information to
     * consent()
     * @param relationshipClazzName
     * @param agent
     * @return 
     */
    @Override
    public boolean consent(String relationshipClazzName, Agent agent)
    {
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
            if ( Math.floorMod(cycle, screenCycle) == 0)
                return 1.0 ;
            else
                return 0.0 ;
        }
        return super.getScreenProbability(args) ;
    }

}
