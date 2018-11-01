/**
 * 
 */
package agent;

import reporter.Reporter ;

import java.util.logging.Level;
import site.* ;

import java.lang.reflect.*;
import org.apache.commons.math3.distribution.* ;
        
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
    
    /** Names of Sites for MSM */ 
    static public String[] SITE_NAMES = new String[] {"rectum","urethra","pharynx"} ;
    
    /** The maximum number of Regular Relationships an agent may be willing to sustain. */
    static int MAX_RELATIONSHIPS = 4 ;
    
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
    
    // Age-specific death rates (deaths per 1000 per year) from ABS.Stat
    static double RISK_60 = 8.0 ;
    static double RISK_55 = 5.5 ;
    static double RISK_50 = 3.5 ;
    static double RISK_45 = 2.2 ;
    static double RISK_40 = 1.4 ; 
    static double RISK_35 = 0.9 ;
    static double RISK_30 = 0.7 ;
    static double RISK_25 = 0.6 ;
    static double RISK_20 = 0.6 ;
    static double RISK_15 = 0.4 ;

    /**
     * The fraction by which the probability for consenting to Casual Relationships
     * is multiplied every additional year after the age of 30. 
     */
    
    
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
    /** Probability of accepting proposed Casual Relationship */
    private double consentCasualProbability = RAND.nextDouble() * 5/12 ; //* 0.999999 + 0.000001  ;
    
    /** Status for HIV infection. */
    private boolean statusHIV ;
    /** Whether currently being treated with antiretrovial medication. */
    private boolean antiViralStatus ;
    /** Whether discloses HIV +ve status. */
    private boolean discloseStatusHIV ;
    /** Whether currently taking PrEP. */
    private boolean prepStatus ;
	
    /** Transmission probabilities per sexual contact from Urethra to Rectum */
    static double URETHRA_TO_RECTUM = 0.07 ; 
    /** Transmission probabilities sexual contact from Urethra to Pharynx. */
    static double URETHRA_TO_PHARYNX = 0.05 ; // 0.15 ; // 0.32 ; 
    /** Transmission probabilities sexual contact from Rectum to Urethra. */ 
    static double RECTUM_TO_URETHRA = 0.005 ; // 0.06 ; // 0.12 ;
    /** Transmission probabilities sexual contact from Rectum to Pharynx. */
    static double RECTUM_TO_PHARYNX = 0.03 ; // 0.025 ; // 0.05 ;
    /** Transmission probabilities sexual contact in Pharynx to Urethra intercourse. */
    static double PHARYNX_TO_URETHRA = 0.01 ; // 0.022 ; // 0.043 ; 
    /** Transmission probabilities sexual contact in Pharynx to Rectum intercourse. */
    static double PHARYNX_TO_RECTUM = 0.03 ; // 0.05 ; // 0.1 ; // 
    /** Transmission probabilities sexual contact in Pharynx to Pharynx intercourse (kissing). */
    static double PHARYNX_TO_PHARYNX = 0.04 ;
    /** Transmission probabilities sexual contact in Urethra to Urethra intercourse (docking). */
    static double URETHRA_TO_URETHRA = 0.001 ; // 0.002 ; // 0.004 ;
    /** Transmission probabilities sexual contact in Rectum to Rectum intercourse. */
    static double RECTUM_TO_RECTUM = 0.003 ; // 0.007 ; // .013 ; 
    
    /** The probability of screening in a given cycle with statusHIV true. */
    static double SCREEN_PROBABILITY_HIV_POSITIVE = 0.0029 ;
    
    /** The probability of screening in a given cycle with statusHIV false 
     * when not on PrEP.
     */
    static double SCREEN_PROBABILITY_HIV_NEGATIVE = 0.0012 ;
    
    /** The number of cycles between screenings for MSM on PrEP. */
    //static int SCREENCYCLE = 92 ;

    
    /** The number of msm invited to any given orgy. */
    static public int GROUP_SEX_EVENT_SIZE = 7 ;

    /** The number of orgies in the community during a given cycle. */
    //int ORGY_NUMBER = 4 ;
    
    /** Probability of joining an orgy if invited. */
    static double JOIN_GSE_PROBABILITY = 6.35 * Math.pow(10,-4) ;
    
    
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
        String probabilityString = infectedSite.getSite().toUpperCase() + "_TO_" + clearSite.getSite().toUpperCase() ;
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
    public static Site[] chooseSites(Agent agent0, Agent agent1, String relationshipClazzName)
    {
        return chooseSites((MSM) agent0, (MSM) agent1, relationshipClazzName) ;
    }
    
    /**
     * Choose sites for sexual contact, implementing seropositioning
     * if required. If so, Urethra of positive statusHIV msm is never chosen if 
     * couple is serodiscordant. 
     * Current Relationship Class is of no consequence.
     * @param msm0
     * @param msm1
     * @return (Site[]) Sites of sexual contact for msm0, msm1, respectively.
     */
    public static Site[] chooseSites(MSM msm0, MSM msm1)
    {
        return chooseSites(msm0, msm1, "") ;
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
    public static Site[] chooseSites(MSM msm0, MSM msm1, String relationshipClazzName)
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
     * @param startAge - age of MSM at sexual 'birth'.
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
     * @param startAge - Age of MSM at sexual 'birth'
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
    final void initStatus(int startAge)
    {
        //requireDiscloseStatusHIV = (rand.nextDouble() < probabilityRequireDiscloseHIV) ;
        statusHIV = (RAND.nextDouble() < getProportionHIV()) ;

        // Sets antiViral status, ensuring it is true only if statusHIV is true
        setAntiViralStatus(RAND.nextDouble() < getAntiviralProbability()) ;

        // Randomly set PrEP status, ensuring it is true only if statusHIV is true
        initPrepStatus(RAND.nextDouble() < getProbabilityPrep()) ;
        
        
        // Initialises infectedStatus at beginning of simulation, 
        //ensuring consistency with Site.infectedStatus
        if (startAge < 0)    // MSM generated at outset, represent initial population
            initInfectedStatus() ;

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
            seroSortCasual = false ;
            seroSortRegular = false ;
            seroSortMonogomous = false ;
            seroPosition = false ;
        }
    }

    /**
     * Initialises MSM infectedStatus while ensuring consistency with 
     * Site.infectedStatus .
     */
    /*final public void initInfectedStatus()
    {
        boolean infected = false ;    //  getInfectedStatus() ;
        for (Site site : sites)
        {
            infected = infected || site.initialiseInfection() ;
            setSymptomatic(site) ;
        }
        setInfectedStatus(infected) ;
    }*/
       
    /**
     * Adds "prepStatus", "statusHIV" to censusFieldNames
     * @return (String[]) Names of MSM fields of relevance to a census.
     */
    @Override
    protected String[] getCensusFieldNames()
    {
        String[] baseCensusNames = super.getCensusFieldNames() ;
        String[] censusNames = new String[baseCensusNames.length + 1] ;
        System.arraycopy(baseCensusNames, 0, censusNames, 0, baseCensusNames.length);
        censusNames[baseCensusNames.length] = "prepStatus" ;
        return censusNames ;
    }

    /**
     * 
     * @return (String) describing traits of Agent.
     */
    @Override
    public String getCensusReport()
    {
        String censusReport = super.getCensusReport() ;
        censusReport += Reporter.addReportProperty("prepStatus", prepStatus) ;
        censusReport += Reporter.addReportProperty("statusHIV", statusHIV) ;
        censusReport += Reporter.addReportProperty("seroSortCasual", seroSortCasual) ;
        censusReport += Reporter.addReportProperty("seroSortRegular", seroSortRegular) ;
        censusReport += Reporter.addReportProperty("seroSortMonogomous", seroSortMonogomous) ;
        censusReport += Reporter.addReportProperty("seroPosition", seroPosition) ;
        return censusReport ;
    }
    /**
     * Should generate Site[] and not call Site[] MSM.sites to avoid error/complications
     * of ensuring that MSM.sites is updated with every call to setSitename().
     * @return (Site[]) of Sites belonging to this MSM
     */
    @Override
    public Site[] getSites()
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
            if (index < 1)
                return rectum ;
            if (index < 2)
                return urethra ;
            else 
                return pharynx ;
	}
	
    /**
     * Used when choosing Site for sexual encounter where one Site has already 
     * been chosen.
     * @return random choice of rectum, urethra or pharynx
     */
    @Override
    protected Site chooseSite(Site site)
    {
        if (site.getSite().equals(RECTUM))
        {
            int index = RAND.nextInt(6) ;
            if (index < 3) 
                return urethra ;
            else if (index < 5) 
                return pharynx ;
            else 
                return rectum ;
        }
        else if (site.getSite().equals(PHARYNX))
        {
            int index = RAND.nextInt(7) ;
            if (index < 3)
                return pharynx ;
            if (index < 6) 
                return urethra ;
            return rectum ;
        }
        else    // if (site.getSite().equals(URETHRA))
        {
            int index = RAND.nextInt(6) ;
            if (index < 3)
                return pharynx ;
            if (index < 5) 
                return rectum ;
            return urethra ;
        }
    }
    
    /**
     * Used when choosing Site other than Rectum for sexual encounter when Rectum 
     * has already been chosen.
     * @return random choice of urethra or pharynx
     */
    protected Site chooseNotRectumSite() 
    {
        int index = RAND.nextInt(4) ;
        if (index < 3)
            return urethra ;
        return pharynx ;
    }

    /**
     * Used when choosing Site other than Rectum for sexual encounter when Site
     * other than Rectum has already been chosen.
     * @return random choice of rectum, urethra or pharynx
     */
    protected Site chooseNotRectumSite(Site site) 
    {
        int index ;
        if (URETHRA.equals(site.getSite()))
        {
            index = RAND.nextInt(6) ;
            if (index < 5)
                return pharynx ;
            return urethra ;
        }
        index = RAND.nextInt(4) ;
        if (index < 3)
            return pharynx ;
        return urethra ;
    }

    /**
     * Used when choosing Site other than Urethra for sexual encounter when Urethra 
     * has already been chosen.
     * @return random choice of rectum or pharynx
     */
    protected Site chooseNotUrethraSite() 
    {
        int index = RAND.nextInt(4) ;
        if (index > 0)
            return rectum ;
        return pharynx ;
    }

    /**
     * 
     * @return (Site) rectum
     */
    public Rectum getRectum()
    {
        return rectum ;
    }

    /**
     * Setter of Site rectum.
     * @param rectum 
     */
    protected void setRectum(Rectum rectum)
    {
        this.rectum = rectum ;
    }
    
    public Urethra getUrethra()
    {
        return urethra ;		
    }

    protected void setUrethra(Urethra urethra)
    {
        this.urethra = urethra ;
    }
    
    public Pharynx getPharynx()
    {
        return pharynx ;
    }

    protected void setPharynx(Pharynx pharynx)
    {
        this.pharynx = pharynx ;
    }
    
    /**
     * 
     * @return (boolean) HIV status
     */
    public boolean getStatusHIV()
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

    public boolean getAntiViralStatus()
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
    }

    public boolean getPrepStatus()
    {
        return prepStatus ;
    }
    
    /**
     * Initialise prepStatus and set up screenCycle and screenTime accordingly.
     * screenCycle is initiated here because it is prepStatus dependent.
     * @param prep 
     */
    private void initPrepStatus(boolean prep)
    {
        setPrepStatus(prep) ;
        initScreenCycle() ;
        
    }
    
    /**
     * Initialises screenCycle from a Gamma distribution to determine how often 
     * an MSM is screened, and then starts the cycle in a random place so that 
     * not every MSM gets screened at the same time.
     */
    private void initScreenCycle()
    {
        if (getPrepStatus())
            setScreenCycle(((int) new GammaDistribution(31,1).sample()) + 61) ;
        else
        {
            int firstScreenCycle = (int) (new GammaDistribution(7,55).sample() * 499.0/333.0) ; // factor correcting for different year
            setScreenCycle(firstScreenCycle) ;  // 49.9% screen within a year 2016
            /*if (statusHIV)
                setScreenCycle(((int) new GammaDistribution(6,55).sample())) ;  // 65% screen within a year
            else
                setScreenCycle(((int) new GammaDistribution(8,55).sample())) ;  // 35% screen within a year
            */
        }
        // Randomly set timer for first STI screen 
        setScreenTime(RAND.nextInt(getScreenCycle())) ;
    }
    
    
    public void reinitScreenCycle(int year) throws Exception
    {
        // Screening frequency compared to 2016 figure. per 1000 in last year.
        double testBase = 499 ;
        // Frequencies, given by per 1000 per year, from 2007-2016
        double[] testRates = new double[] {333,340,398,382,383,382,391,419,445,499} ;
        double ratio = testBase/testRates[year] ;
        int newScreenCycle = (int) ratio*getScreenCycle() ;
        
        setScreenCycle(newScreenCycle) ;
    }
    
    /**
     * Allow for re-initialisation of prepStatus during simulation while 
     * initPrepStatus() remains private.
     * @param prep 
     */
    public void reinitPrepStatus(boolean prep)
    {
        initPrepStatus(prep) ;
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
     * Called to adjust condom use.
     */
    @Override
    public void adjustCondomUse()
    {
        adjustProbabilityUseCondom() ;
    }
    
    /**
     * Adjusts probabilityUseCondom to reflect behavioural trends
     */
    abstract public void adjustProbabilityUseCondom() ;

    /**
     * Whether to enter a proposed relationship of class relationshipClazz .
     * Currently according to whether in a monogomous relationship and 
     * the number of relationships already entered compared to promiscuity.
     * 
     * @param relationshipClazzName - name relationship subclass
     * @param partner - agent for sharing proposed relationship
     * @return true if accept and false otherwise
     */
    @Override
    public boolean consent(String relationshipClazzName, Agent partner)
    {
        if (getSeroSort(relationshipClazzName))
            if (!String.valueOf(getStatusHIV()).equals(declareStatus()))
                return false ;
        return super.consent(relationshipClazzName, partner) ;
    }
    
    @Override
    protected boolean consentCasual(Agent partner)
    {
        return (RAND.nextDouble() < consentCasualProbability) ;
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
    
    abstract double getProportionHIV();
    
    final private double getAntiviralProbability()
    {
        return PROBABILITY_ANTIVIRAL ;
    }
    
    abstract double getProbabilityDiscloseHIV() ;
    
    public double getProbabilityPrep() 
    {
        return PROBABILITY_PREP ;
    }
    
    protected double getProbabilitySeroSort(boolean hivStatus)
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_SERO_SORT ;
        return PROBABILITY_NEGATIVE_SERO_SORT ;
    }

        protected double getProbabilitySeroSortCasual(boolean hivStatus)
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_CASUAL_SERO_SORT ;
        return PROBABILITY_NEGATIVE_CASUAL_SERO_SORT ;
    }

    protected double getProbabilitySeroSortRegular(boolean hivStatus)
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_REGULAR_SERO_SORT ;
        return PROBABILITY_NEGATIVE_REGULAR_SERO_SORT ;
    }

    protected double getProbabilitySeroSortMonogomous(boolean hivStatus)
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_MONOGOMOUS_SERO_SORT ;
        return PROBABILITY_NEGATIVE_MONOGOMOUS_SERO_SORT ;
    }

    protected double getProbabilitySeroPosition(boolean hivStatus) 
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_SERO_POSITION ;
        return PROBABILITY_NEGATIVE_SERO_POSITION ;
    }

    /**
     * Decides probabilistically whether MSM chooses to use a condom in a given encounter.
     * Choice is based on type of Relationship and properties of msm
     * @param relationshipClazzName
     * @param msm
     * @return true if condom is to be used, false otherwise
     */
    @Override
    abstract protected boolean chooseCondom(String relationshipClazzName, Agent msm);
    
    /**
     * 
     * @return (int) the number of orgies in a MSM community per cycle
     */
    //@Override
    //public int getOrgyNumber()
    {
        //return ORGY_NUMBER ;
    }
    
    /**
     * 
     * @return (int) number of MSM invited to join any given orgy
     */
    @Override
    public int getGroupSexEventSize()
    {
        return GROUP_SEX_EVENT_SIZE ;
    }

    /**
     * 
     * @return (double) the probability of MSM joining an orgy when invited
     */
    @Override
    abstract public double getJoinGroupSexEventProbability();
    
    /**
     * Probability of MSM screening on that day. Depends on prepStatus and statusHIV.
     * @param args
     * @return for PrEP users, 1.0 if cycle multiple of screenCycle, 0.0 otherwise
     *     for non-PrEP users, random double between 0.0 and 1.0 .
     */    
    @Override
    public double getScreenProbability(String[] args)
    {
            // Find current cycle
        //    int cycle = Integer.valueOf(args[0]) ;
            
        // Countdown to next STI screen
        decrementScreenTime() ;

        // Is it time for a regular screening?
        if ( getScreenTime() < 0)
        {
            setScreenTime(getScreenCycle()) ;
            return 1.0 ;
        }
        else
            return 0.0 ;
    }
    
        /**
     * Based on data from the Australian Bureau of Statistics for NSW male population
     * (http://stat.data.abs.gov.au/).
     * @return (double) probability of dying during any cycle depending on the 
     * properties, here age, of the Agent.
     */
    @Override
    protected double getRisk()
    {
        double risk ;
        int msmAge = getAge() ;
        if (msmAge > 65)
            return 1 ;
        else if (msmAge > 60)
            risk = RISK_60 ;
        else if (msmAge > 55) 
            risk = RISK_55 ;
        else if (msmAge > 50) 
            risk = RISK_50 ;
        else if (msmAge > 45) 
            risk = RISK_45 ;
        else if (msmAge > 40) 
            risk = RISK_40 ;
        else if (msmAge > 35) 
            risk = RISK_35 ;
        else if (msmAge > 30) 
            risk = RISK_30 ;
        else if (msmAge > 25)
            risk = RISK_25 ;
        else if (msmAge > 20)
            risk = RISK_20 ;
        else 
            risk = RISK_15 ;
        double noRiskPower = 1.0/DAYS_PER_YEAR ;
        
        double noRisk = Math.pow((1 - risk/1000),1.0/DAYS_PER_YEAR) ;
        return 1 - noRisk ;
    }
    
    /**
     * Incorporate the effects of aging on sexual activity.
     * @return 
     */
    @Override
    protected String ageEffects() 
    {
        return "" ;
    
    }
        /*String report = "" ;
        consentCasualProbability *= PROMISCUITY_FRACTION ;
        report += Reporter.addReportProperty("consentCasualProbability", consentCasualProbability) ;
        return super.ageEffects() + report ;
    }*/
}
