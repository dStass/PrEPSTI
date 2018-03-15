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
	
	// Status' for HIV infection, antiviral treatment if infected
	private boolean statusHIV ;
        // Whether currently being treated with antiretrovial medication
	private boolean antiViralStatus ;
	// Whether discloses HIV +ve status
	private boolean discloseStatusHIV ;
        // Whether currently taking PrEP
        private boolean prepStatus ;
	
    // Associated probabilities for the above
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
	 * 
     * Specifies Agent subclass Men having Sex with MenNecessary to call super.constructor()
	 */
    
	public MSM(int startAge) {
		super(startAge) ;
                statusHIV = (rand.nextDouble() < probabilityHIV) ;
                antiViralStatus = (rand.nextDouble() < antiViralProbability) ;
                discloseStatusHIV = (rand.nextDouble() < probabilityDiscloseHIV) ;
                prepStatus = (rand.nextDouble() < probabilityPrep) ;
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
        
	public double getScreenProbability(String[] args)
	{
            if (antiViralStatus)
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
