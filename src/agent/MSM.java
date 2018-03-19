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
    private boolean requireDiscloseStatusHIV ;
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
     * @return String which decides subclass.getSimpleName()
     */
    public static String chooseMSM()
    {
    	int totalOdds = riskyOdds + safeOdds ;
    	int choice = rand.nextInt(totalOdds) ;
    	if (choice < safeOdds)
    		return "SafeMSM" ;
    	return "RiskyMSM" ;
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
            seroSort = (rand.nextDouble() < probabilitySeroSort) ;
            seroPosition = (rand.nextDouble() < probabilitySeroPosition) ;
            requireDiscloseStatusHIV = (rand.nextDouble() < probabilityRequireDiscloseHIV) ;
            statusHIV = (rand.nextDouble() < probabilityHIV) ;
            antiViralStatus = (rand.nextDouble() < antiViralProbability) ;
            discloseStatusHIV = (rand.nextDouble() < probabilityDiscloseHIV) ;
            prepStatus = (rand.nextDouble() < probabilityPrep) ;
	    return ;
        }

	/* (non-Javadoc)
	 * @see community.Agent#setSites()
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
        
        public boolean getSeroSort()
        {
            return seroSort ;
        }
        
        public boolean getSeroPosition()
        {
            return seroPosition ;
        }
        
        private boolean getAntiViralStatus()
        {
            return antiViralStatus ;
        }
        
        private boolean getDiscloseStatusHIV()
        {
            return discloseStatusHIV ;
        }
        
        private boolean getPrepStatus()
        {
            return prepStatus ;
        }
        
        public String discloseStatus()
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
            String partnerDisclosure = partner.discloseStatus() ;
            Boolean partnerSeroPosition = partner.getSeroPosition() ;
            if (seroSort)
            {
                if ("none".equalsIgnoreCase(partnerDisclosure))
                    return false ;
                else
                    return (statusHIV == Boolean.getBoolean(partnerDisclosure)) ;
            }
            if (seroPosition)
            {
                if ("none".equalsIgnoreCase(partnerDisclosure))
                    return false ;
                else 
                    return partnerSeroPosition ;
            }
            return true ;
        }
        
        /**
         * Override and call in subclass if details about possible partners are 
         * needed.
         * @param relationshipClazzName
         * @return String[] args of relationshipClazzName and other Properties 
         * relevant to deciding consent()
         */
        public String[] consentArgs(String relationshipClazzName) 
        {
            String[] consentArgs = {relationshipClazzName,discloseStatus(),String.valueOf(seroSort)} ;
            return consentArgs ;
        }
        
	
        
    @Override
	public double getScreenProbability(String[] args)
	{
            if (prepStatus && (! statusHIV))
            {
                int cycle = Integer.valueOf(args[0]) ;
                // Those on antivirals test every three months (92 days) on a given day
                if ( Math.floorMod(cycle, 92) == 0)
                    return 1.0 ;
                else
                    return 0.0 ;
            }
            return super.getScreenProbability(args) ;
	}

}
