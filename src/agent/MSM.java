/**
 * 
 */
package agent;

//import static agent.Agent.LOGGER;
import reporter.Reporter ;

import java.util.logging.Level;
import site.* ;
import agent.* ;
        
import java.lang.reflect.*;
import java.util.ArrayList ;
import java.util.Arrays;
import java.util.HashMap ;
import java.util.Collection ;
import java.util.stream.IntStream;
import org.apache.commons.math3.distribution.* ;
import reporter.PopulationReporter;
import static reporter.Reporter.AGENTID;
import reporter.ScreeningReporter;
        
/**
 * @author Michael Walker
 *
 */
public class MSM extends Agent {
	
    // The maximum number of relationships an agent may be willing to sustain
    // static int maxRelationships = 20;
    
    /** Site name of Rectum */
    static String RECTUM = "Rectum" ;
    /** Site name of Rectum */
    static String URETHRA = "Urethra" ;
    /** Site name of Rectum */
    static String PHARYNX = "Pharynx" ;
    
    /** Names of Sites for MSM */ 
    static public String[] SITE_NAMES = new String[] {"Rectum","Urethra","Pharynx"} ;
    
    /** The maximum number of Regular Relationships an agent may be willing to sustain. */
    static int MAX_RELATIONSHIPS = 3 ;
    
    /** The probability of positive HIV status */
    static double PROPORTION_HIV = 0.092 ;
    
    /** The probability of disclosing HIV status if HIV positive */
    static double PROBABILITY_DISCLOSE_POSITIVE_HIV =  0.286 ; // 2010 VALUE // 0.2 ; // 
    /** The probability of disclosing HIV status if HIV negative */
    static double PROBABILITY_DISCLOSE_NEGATIVE_HIV = 0.239 ; // 2010 VALUE // 0.18 ; // 
    /** Probability of serosorting if HIV positive (2017) */
    static double PROBABILITY_POSITIVE_SERO_SORT = 0.59 ;
    /** Probability of serosorting if HIV negative (2017) */
    static double PROBABILITY_NEGATIVE_SERO_SORT = 0.45 ;
    /** Probability of serosorting in Casual Relationship if HIV positive */
    static double PROBABILITY_POSITIVE_CASUAL_SERO_SORT = 0.40 ; // 0.431 ;
    /** Probability of serosorting in Casual Relationship if HIV negative */
    static double PROBABILITY_NEGATIVE_CASUAL_SERO_SORT = 0.5 ; // 0.485 ;
    /** Probability of serosorting in Regular Relationship if HIV positive */
    static double PROBABILITY_POSITIVE_REGULAR_SERO_SORT = 0.5 ; // 0.274 ;
    /** Probability of serosorting in Regular Relationship if HIV negative */
    static double PROBABILITY_NEGATIVE_REGULAR_SERO_SORT = 0.5 ; // 0.712 ;
    /** Probability of serosorting in Regular Relationship if HIV positive */
    static double PROBABILITY_POSITIVE_MONOGOMOUS_SERO_SORT 
            = PROBABILITY_POSITIVE_REGULAR_SERO_SORT ;
    /** Probability of serosorting in Regular Relationship if HIV negative */
    static double PROBABILITY_NEGATIVE_MONOGOMOUS_SERO_SORT 
            = PROBABILITY_NEGATIVE_REGULAR_SERO_SORT ;
    /** Probability of sero-positioning if HIV positive */
    static double PROBABILITY_POSITIVE_SERO_POSITION = 0.25 ;
    /** Probability of sero-positioning if HIV negative */
    static double PROBABILITY_NEGATIVE_SERO_POSITION = 0.154 ;
    /** The probability of being on antivirals, given positive HIV status */
    static double PROBABILITY_ANTIVIRAL = 0.689 ; // 2010 value // 0.532 ; // 
    /** 
     * Adjusts the probability of accepting a Casual relationship.
     * Value chosen to match the GCPS.
     */
    static double ADJUST_CASUAL_CONSENT = 0.7 ;
    
    /**
     * Used to change the value of PROBABILITY_ANTIVIRAL
     * @param antiViral 
     */
    static void SET_PROBABILITY_ANTIVIRAL(double antiViral)
    {
        PROBABILITY_ANTIVIRAL = antiViral ;
    }
    
    /**
     * Alters the probability of being on antiViral medication with undetectable
     * virus blood concentration from year to year. If the probability increases 
     * then only MSM not on antiviral medication will change, and vice versa if the probability
     * decreases.
     * @param year 
     */
    static protected void REINIT_PROBABILITY_ANTIVIRAL(ArrayList<Agent> agentList, int year) throws Exception
    {
        if (year == 0)
            return ;
        
        double[] probabilityAntiViral = new double[] {0.532, 0.706, 0.735, 0.689, 0.706, 0.802, 0.766, 0.830, 0.818, 0.854} ;
        double newProbability = probabilityAntiViral[year] ;
        double oldProbability = probabilityAntiViral[year-1] ;
        double changeProbability ;
        MSM msm ;
        for (Agent agent : agentList)
        {
            msm = (MSM) agent ;
            
            if (newProbability > oldProbability)
            {
                if (msm.discloseStatusHIV)
                    return ;
                changeProbability = (newProbability - oldProbability)/(1 - oldProbability) ;
            }
            else    // Probability of being on antiViral medication decreases.
            {
                if (!msm.discloseStatusHIV)
                    return ;
                changeProbability = (oldProbability - newProbability)/oldProbability ;
            }
            newProbability *= changeProbability ;

            msm.setAntiViralStatus(RAND.nextDouble() < newProbability) ;
        }
    }
    
    /**
     * Resets the probability of adjusting discloseStatusHIV according to changing 
     * disclose probabilities each year.
     * Probabilities taken from Table 9 of ARTB 2017.
     * @param year
     * @throws Exception 
     */
    static protected void REINIT_PROBABILITY_DISCLOSURE_HIV(ArrayList<Agent> agentList, int year) throws Exception
    {
        // Go from 2007
        double newDiscloseProbability ;
        double oldDiscloseProbability ;
        double changeProbability ;
        //if (statusHIV)
        double[] positiveDiscloseProbability = new double[] {0.201,0.296,0.327,0.286,0.312,0.384,0.349,0.398,0.430,0.395} ;
        //else
        double[] negativeDiscloseProbability = new double[] {0.175,0.205,0.218,0.239,0.229,0.249,0.236,0.295,0.286,0.352} ;
        
        double positiveNewDiscloseProbability = positiveDiscloseProbability[year] ;
        double positiveOldDiscloseProbability = positiveDiscloseProbability[year-1] ;
        
        double negativeNewDiscloseProbability = negativeDiscloseProbability[year] ;
        double negativeOldDiscloseProbability = negativeDiscloseProbability[year-1] ;
        
        for (Agent agent : agentList)
        {
            MSM msm = (MSM) agent ;
            if (msm.statusHIV)
            {
                newDiscloseProbability = positiveNewDiscloseProbability ;
                oldDiscloseProbability = positiveOldDiscloseProbability ;
            }
            else
            {
                newDiscloseProbability = negativeNewDiscloseProbability ;
                oldDiscloseProbability = negativeOldDiscloseProbability ;
            }
            
            // Do not change against the trend
            if (newDiscloseProbability > oldDiscloseProbability)
            {
                if (msm.discloseStatusHIV)
                    continue ;
                changeProbability = (newDiscloseProbability - oldDiscloseProbability)/(1 - oldDiscloseProbability) ;
            }
            else    // if less likely to disclose
            {
                if (!msm.discloseStatusHIV)
                    continue ;
                changeProbability = (oldDiscloseProbability - newDiscloseProbability)/oldDiscloseProbability ;
            }
            msm.initSeroStatus(newDiscloseProbability * changeProbability) ;
        }
    }
    
    /**
     * Resets the probability of Risky vs Safe behaviour according to the year.
     * Rates taken from GCPS 2011 Table 16, 2014 Table 15, 
     */
    static public void REINIT_RISK_ODDS(ArrayList<Agent> agentList, int year) throws Exception
    {
        if (year == 0)
            return ;
        // Go from 2007, ARTB (Table 9, 2014) (Table 11, 2017)
        // Year-by-year rates of UAIC 
        int[] riskyOdds = new int[] {321,327,378,361,337,360,357,375,388,482,482} ;
        //int[] riskyOdds = new int[] {365,360,355,350,345,340,335,330,325,320} ;
        // Year-by-year rates of non-UAIC 
        // 2013- Table 11 2017, 2007-2012 Table 9 2014 * .7
        //int[] safeOdds = new int[] {679,673,622,639,663,640,643,625,622,518,518} ;
        int[] safeOdds = new int[] {475,471,435,447,464,448,443,445,421,398,398} ;
        //int[] safeOdds = new int[] {430,435,440,445,450,455,460,465,470,475} ;
        // Ratios .403 , .410 , .465 , .447 , .421 , .446 , .446 , .457 , .480 , .548
        SAFE_ODDS = safeOdds[year] ;
        RISKY_ODDS = riskyOdds[year] ;
        
        int totalOdds = SAFE_ODDS + RISKY_ODDS ;
        int lastRisky = riskyOdds[year-1] ;
        int lastTotal = safeOdds[year-1] + lastRisky ;
        double riskyProbability = ((double) RISKY_ODDS)/totalOdds ;
        double lastProbability = ((double) lastRisky)/lastTotal ;
        double changeProbability ;
        
        boolean moreRisky = (lastProbability < riskyProbability) ;
        double adjustProbabilityUseCondom = lastProbability/riskyProbability ;
        
        // Compensates for allowing only change in one direction.
        if (moreRisky) 
            changeProbability = (riskyProbability - lastProbability)/(1-lastProbability) ;
        else
            changeProbability = riskyProbability/lastProbability ; //(lastProbability - riskyProbability)/lastProbability ;
                                                                    // see comments below
        
        //riskyProbability *= changeProbability ;
        //double riskyProbabilityPositive = riskyProbability ; //* HIV_RISKY_CORRELATION ;
        //double riskyProbabilityNegative = riskyProbability ; //* (1.0 - PROPORTION_HIV * HIV_RISKY_CORRELATION)/(1.0 - PROPORTION_HIV) ;
        
        MSM msm ;
        for (Agent agent : agentList)
        {
            msm = (MSM) agent ;
            msm.scaleProbabilityUseCondom(adjustProbabilityUseCondom) ;
            
            if (moreRisky) 
            {
                if (msm.getRiskyStatus()) // if risky already
                    continue ;    // we don't change it
                msm.setRiskyStatus(RAND.nextDouble() < changeProbability) ;
            }
            else    // riskyProbability has gone down
            {
                if (!msm.getRiskyStatus()) // if safe already
                    continue ;    // we don't change it
                // equivalent to correct calculation: RAND > (1 - changeProbability)
                msm.setRiskyStatus(RAND.nextDouble() < changeProbability) ; 
            }
        }
    }
    
    /** The probability of being on PrEP, given negative HIV status */
    static double PROBABILITY_PREP = 0.0 ; // 0.14 ;
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
    
    /** Odds of choosing pharynx for sexual contact. */
    private int choosePharynx = 3 ; // RAND.nextInt(3) + 1  ;
    /** Odds of choosing rectum for sexual contact. */
    private int chooseRectum = 3 ; // RAND.nextInt(3) ;
    /** Odds of choosing rectum for sexual contact. */
    private int chooseUrethra = 3 ; // RAND.nextInt(3) ;
    

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
    private double consentCasualProbability ; // = RAND.nextDouble() * 5/12 ; //* 0.999999 + 0.000001  ;
    
    /** Status for HIV infection. */
    private boolean statusHIV ;
    /** Whether currently being treated with antiretroviral medication. */
    private boolean antiViralStatus ;
    /** Whether discloses HIV +ve status. */
    private boolean discloseStatusHIV ;
    /** Whether currently taking PrEP. */
    private boolean prepStatus ;
    /** Whether MSM is Risky, Safe otherwise. */
    private boolean riskyStatus ;
    
    /** Transmission probabilities per sexual contact from Urethra to Rectum */
    static double URETHRA_TO_RECTUM = 0.95 ; // 0.100 ;  0.25 ; //
    /** Transmission probabilities sexual contact from Urethra to Pharynx. */
    static double URETHRA_TO_PHARYNX = 0.20 ; // 0.060 ; // 0.035 ; // 0.15 ;
    /** Transmission probabilities sexual contact from Rectum to Urethra. */
    static double RECTUM_TO_URETHRA = 0.003 ; // 0.020 ; // 0.008 ; 0.010 ; //
    /** Transmission probabilities sexual contact from Rectum to Pharynx. */
    static double RECTUM_TO_PHARYNX = 0.030 ;
    /** Transmission probabilities sexual contact in Pharynx to Urethra intercourse. */
    static double PHARYNX_TO_URETHRA = 0.0005 ; // 0.001 ;
    /** Transmission probabilities sexual contact in Pharynx to Rectum intercourse. */
    static double PHARYNX_TO_RECTUM = 0.045 ; // 0.030 ; // 0.0100 ;
    /** Transmission probabilities sexual contact in Pharynx to Pharynx intercourse (kissing). */
    static double PHARYNX_TO_PHARYNX = 0.035 ; // 0.030 ; // 0.052 ;
    /** Transmission probabilities sexual contact in Urethra to Urethra intercourse (docking). */
    static double URETHRA_TO_URETHRA = 0.0001 ; // 0.0001 ; // 0.005 ;
    /** Transmission probabilities sexual contact in Rectum to Rectum intercourse. */
    static double RECTUM_TO_RECTUM = 0.001 ;

    
    /** Transmission probabilities per sexual contact from Urethra to Rectum */
    //static double URETHRA_TO_RECTUM = 0.95 ; // 0.100 ;  0.25 ; // 
    /** Transmission probabilities sexual contact from Urethra to Pharynx. */
    //static double URETHRA_TO_PHARYNX = 0.05 ; // 0.060 ; // 0.035 ; // 0.15 ; 
    /** Transmission probabilities sexual contact from Rectum to Urethra. */ 
    //static double RECTUM_TO_URETHRA = 0.005 ; 
    /** Transmission probabilities sexual contact from Rectum to Pharynx. */
    //static double RECTUM_TO_PHARYNX = 0.0050 ;
    /** Transmission probabilities sexual contact in Pharynx to Urethra intercourse. */
    //static double PHARYNX_TO_URETHRA = 0.0005 ; // 0.001 ;
    /** Transmission probabilities sexual contact in Pharynx to Rectum intercourse. */
    //static double PHARYNX_TO_RECTUM = 0.020 ; // 0.030 ; // 0.0100 ; 
    /** Transmission probabilities sexual contact in Pharynx to Pharynx intercourse (kissing). */
    //static double PHARYNX_TO_PHARYNX = 0.05 ; // 0.030 ; // 0.052 ; 
    /** Transmission probabilities sexual contact in Urethra to Urethra intercourse (docking). */
    //static double URETHRA_TO_URETHRA = 0.005 ; // 0.0001 ; // 0.005 ; 
    /** Transmission probabilities sexual contact in Rectum to Rectum intercourse. */
    //static double RECTUM_TO_RECTUM = 0.001 ;
    
    /** The probability of screening in a given cycle with statusHIV true. */
    static double SCREEN_PROBABILITY_HIV_POSITIVE = 0.0029 ;
    
    /** The probability of screening in a given cycle with statusHIV false 
     * when not on PrEP.
     */
    static double SCREEN_PROBABILITY_HIV_NEGATIVE = 0.0012 ;
    
    /** The number of cycles between screenings for MSM on PrEP. */
    //static int SCREENCYCLE = 92 ;

    
    /** The number of MSM invited to any given group-sex event. */
    static public int GROUP_SEX_EVENT_SIZE = 7 ;

    /** The number of orgies in the community during a given cycle. */
    //int ORGY_NUMBER = 4 ;
    
    /** Probability of joining a group-sex event if invited. */
    static double JOIN_GSE_PROBABILITY = 6.35 * Math.pow(10,-4) ;
    
    
    public static void RESET_INFECT_PROBABILITIES(int index)
    {
        HashMap<String[],double[]> transmissionMap = new HashMap<String[], double[]>() ;
        transmissionMap.put(new String[] {URETHRA, RECTUM},new double[] {0.028,0.032}) ;
        transmissionMap.put(new String[] {RECTUM, URETHRA},new double[] {0.026,0.028}) ;
        transmissionMap.put(new String[] {PHARYNX, RECTUM},new double[] {0.024,0.024}) ;
        transmissionMap.put(new String[] {PHARYNX, URETHRA},new double[] {0.024,0.024}) ;
        transmissionMap.put(new String[] {URETHRA, PHARYNX},new double[] {0.024,0.024}) ;
        
        int nbKeys = transmissionMap.size() ;
        int nbValues = transmissionMap.values().iterator().next().length ;
        int maxIndex = (int) Math.pow(nbValues,nbKeys) ;
        index = Math.floorMod(index, maxIndex) ;
        
        int keyIndex = Math.floorDiv(index,nbValues) ;
        int valueIndex = Math.floorMod(index, nbValues) ;
        //String siteNames[] = new String[] {URETHRA,RECTUM,PHARYNX} ;
        String[] keyArray ;
        String[] key = (String[]) transmissionMap.keySet().toArray()[(int) Math.floorMod(index,nbKeys)] ;
        {
                {
                    // Implement using 
                }
            }
    }
    
    /**
     * Allows changing of SITE_TO_SITE transmission probabilities.
     * @param infectedSiteName
     * @param clearSiteName
     * @param transmission 
     */
    public static void SET_INFECT_PROBABILITY(String infectedSiteName, String clearSiteName, double transmission)
    {
        String probabilityString = infectedSiteName.toUpperCase() + "_TO_" + clearSiteName.toUpperCase() ;
        try
        {
            MSM.class.getDeclaredField(probabilityString).setDouble(null,transmission) ;
        }
        catch ( Exception e )
        {
            LOGGER.log(Level.SEVERE, "{0} : {1}", new Object[]{e.getClass().getName(), e.getLocalizedMessage()});
        }
    }
    
    /**
     * Earlier versions of theis Meth had parameters Agent infectedAgent, Agent clearAgent,
     * @param infectedAgent
     * @param clearAgent
     * @param infectionStatus
     * @param infectedSite
     * @param clearSite
     * @return infectProbability (double) the probability of infection of clearSite
     */
    public static double GET_INFECT_PROBABILITY(Site infectedSite, Site clearSite)
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
    public static Site[] CHOOSE_SITES(Agent agent0, Agent agent1, String relationshipClazzName)
    {
        return CHOOSE_SITES((MSM) agent0, (MSM) agent1, relationshipClazzName) ;
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
    public static Site[] CHOOSE_SITES(MSM msm0, MSM msm1)
    {
        return CHOOSE_SITES(msm0, msm1, "") ;
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
    public static Site[] CHOOSE_SITES(MSM msm0, MSM msm1, String relationshipClazzName)
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
    
    	
    // Odds of a MSM having anal intercourse safely (consistent condom use)
    static int SAFE_ODDS = 447 ; // 475 ; // 
    // Odds of an MSM being riskyMSM
    static int RISKY_ODDS = 361 ; //  321 ; // 
    // Sum of safeOdds and riskyOdds
    static int TOTAL_ODDS = RISKY_ODDS + SAFE_ODDS ;
//        int[] safeOdds = new int[] {475,471,435,447,464,448,443,445,421,398,398} ;
//        int[] riskyOdds = new int[] {321,327,378,361,337,360,357,375,388,482,482} ;

    /** 
     * Describes correlation between statusHIV and riskyStatus.
     * Must be less than 1/PROPORTION_HIV OR initRiskiness() fails.
     */
    static double HIV_RISKY_CORRELATION = 2.0 ; // 1.0 ;	
    
    /**
     * Choose whether MSM is RiskyMSM or SafeMSM
     * @param startAge - age of MSM at sexual 'birth'.
     * @return - one of subclass RiskyMSM or SafeMSM
     */
    public static MSM BIRTH_MSM(int startAge)
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
     * Specifies Agent subclass Men having Sex with Men. Necessary to call super()
     * @param startAge - Age of MSM at sexual 'birth'
     */
    public MSM(int startAge) 
    {
        super(startAge) ;
        initStatus() ;
        // Cannot be called in super() because sites is not initiated yet.
        initInfectedStatus(startAge) ;
        initConsentCasualProbability() ;
        
        // Choose tops, 1/5
        //if (RAND.nextInt(5) > 0)
          //  chooseRectum = 0 ;
        // Choose bottoms, 4/5 * 3/4
        //else if (RAND.nextInt(4) > 0)
          //  chooseUrethra = 0 ;
    }

    /**
     * Initialises status' at construction of MSM. 
     * Ensures that those MSM who come out during simulation are initially
     * HIV free (statusHIV == false).
     */
    final void initStatus()
    {
        //requireDiscloseStatusHIV = (rand.nextDouble() < probabilityRequireDiscloseHIV) ;
        statusHIV = (RAND.nextDouble() < getProportionHIV()) ;

        // Sets antiViral status, ensuring it is true only if statusHIV is true
        setAntiViralStatus(RAND.nextDouble() < getAntiviralProbability()) ;

        // Randomly set PrEP status, ensuring it is true only if statusHIV is true
        initPrepStatus(RAND.nextDouble() < getProbabilityPrep()) ;
        
        
        initRiskiness() ;
        
        // Initialises infectedStatus at beginning of simulation, 
        //ensuring consistency with Site.infectedStatus
        //  initInfectedStatus(startAge) ;    // MSM generated at outset, represent initial population

        // Sets whether disclosesHIV, allowing for statusHIV
        double probabilityDiscloseHIV = getProbabilityDiscloseHIV() ;
        // Sero -sorting and -positioning status'
        initSeroStatus(probabilityDiscloseHIV) ;
        
    }

    /**
     * Initialises riskyStatus and probabilityUseCondom according to RISKY_ODDS, SAFE_ODDS
     * Risky or Safe behaviour correlated with HIV status
     * Keep overall risky behaviour the same
     */
    final void initRiskiness()
    {
        int totalOdds = SAFE_ODDS + RISKY_ODDS ;
        double riskyProbability = ((double) RISKY_ODDS)/totalOdds ;
        if (statusHIV)
            riskyProbability *= HIV_RISKY_CORRELATION ;
        else
            riskyProbability *= (1.0 - PROPORTION_HIV * HIV_RISKY_CORRELATION)/(1.0 - PROPORTION_HIV) ;
        
        probabilityUseCondom = sampleGamma(4, 0.1, 1) ; // Gamma2 * (1 - riskyProbability) * RAND.nextDouble() ;
        if (probabilityUseCondom > 1)
            probabilityUseCondom = 1 ;
        
        riskyStatus = (RAND.nextDouble() < riskyProbability) ;
    } 
    
    /**
     * Chooses discloseStatusHIV according to probabilityDiscloseHIV and then 
     * chooses sero- Sort/Position parameters accordingly.
     * @param probabilityDiscloseHIV 
     */
    final void initSeroStatus(double probabilityDiscloseHIV)
    {
        discloseStatusHIV = (RAND.nextDouble() < probabilityDiscloseHIV) ;
        if (discloseStatusHIV)
        {
            seroSortCasual = (RAND.nextDouble() < getProbabilitySeroSortCasual(statusHIV)) ;
            seroSortRegular = (RAND.nextDouble() < getProbabilitySeroSortRegular(statusHIV)) ;
            seroSortMonogomous = (RAND.nextDouble() < getProbabilitySeroSortMonogomous(statusHIV)) ;
            seroPosition = (RAND.nextDouble() < getProbabilitySeroPosition(statusHIV)) ;
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
     * Initialises consentCasualProbability using data from EPIC.
     * Specifically the variable 'mensex' indicating the number of
     * male partners in the past 3 months at Baseline. The proportions for 
     * each range are
     *   0        1        2-5      6-10    11-20    21-50    51-100     100+ 
     * 0.00311  0.02715  0.31857  0.30353  0.22172  0.10170  0.019716  0.00450
     * 
     * We take the weighted mean of the first two columns, and use a Poisson 
     * distribution to choose in the 100+ range. Otherwise find the daily probability 
     * associated with the extrema of each range and choose with a Uniform distribution.
     */
    final void initConsentCasualProbability()
    {
        double consentProbability = 0 ;
        int[] lowerBounds = new int[] {0,1,2,6,11,21,51,100} ;
        double[] proportions = new double[] {0.00311, 0.02715, 0.31857, 0.30353, 0.22172, 0.10170, 0.01972, 0.00450} ;
        double[] cumulative = new double[] {0.00311, 0.00311, 0.00311, 0.00311, 0.00311, 0.00311, 0.00311, 0.00311} ;
        // Now loop over proportions at each cumulIndex to fill out cumulative Array.
        for (int cumulIndex = 1 ; cumulIndex < cumulative.length ; cumulIndex++ )
            for (int propIndex = 1 ; propIndex <= cumulIndex ; propIndex++ )
                cumulative[cumulIndex] += proportions[propIndex] ;
        
        // Choose range
        double rangeChoice = RAND.nextDouble() ;
        int rangeIndex = 0 ;
        for (int cumulIndex = 1 ; cumulIndex < cumulative.length ; cumulIndex++ )
        {
            if (rangeChoice < cumulative[cumulIndex])
            {
                rangeIndex = cumulIndex ;
                break ;
            }
        }
        
        if (rangeIndex < 2) // 0 or 1 partners
        {
            consentProbability = Math.max(0.0,RAND.nextDouble() * proportions[1] 
                    - RAND.nextDouble() * proportions[0])/92.0 ;
        }
        else if (rangeIndex == (proportions.length - 1)) // 100+ partners
        {
            // Poisson distribution
            //int nbPartners = 100 + ((int) new PoissonDistribution(10.0).sample()) ;
            //consentProbability = nbPartners/92.0 ;
            consentProbability = 1.0 ;
        }
        else
        {
            // Choose in range, take 92 days in 3 months
            double lowerProb = lowerBounds[rangeIndex]/92.0 ;
            double upperProb = (lowerBounds[rangeIndex+1] - 1)/92.0 ;
            consentProbability = RAND.doubles(lowerProb, upperProb).iterator().nextDouble() ;
        }
        //double adjustConsent = 0.8 ;
        consentCasualProbability = Math.sqrt(ADJUST_CASUAL_CONSENT * consentProbability/2.0) ;
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
            chooseSymptomatic(site) ;
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
        // Agent census names
        String[] baseCensusNames = super.getCensusFieldNames() ;
        String[] censusNames = new String[baseCensusNames.length + 2] ;
        System.arraycopy(baseCensusNames, 0, censusNames, 0, baseCensusNames.length);
        
        // Uniquely MSM census names
        censusNames[baseCensusNames.length] = "prepStatus" ;
        censusNames[baseCensusNames.length + 1] = "statusHIV" ;
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
        censusReport += Reporter.ADD_REPORT_PROPERTY("prepStatus", prepStatus) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("statusHIV", statusHIV) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("discloseStatusHIV", discloseStatusHIV) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("seroSortCasual", seroSortCasual) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("seroSortRegular", seroSortRegular) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("seroSortMonogomous", seroSortMonogomous) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("seroPosition", seroPosition) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("riskyStatus", riskyStatus) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("antiViralStatus", antiViralStatus) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("consentCasualProbability", consentCasualProbability) ;
        
        for (Site site : sites)
            censusReport += site.getCensusReport() ;
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
        return sites ; // new Site[] {rectum,urethra,pharynx} ;
    }
        
    /**
     * Used when choosing Site for sexual encounter
     * @return random choice of rectum, urethra or pharynx
     */
    @Override
    protected Site chooseSite()
	{
            int chooseTotal = chooseUrethra + chooseRectum + choosePharynx ;
            int index = RAND.nextInt(chooseTotal) ;
            if (index < chooseRectum)
                return rectum ;
            if (index < (chooseRectum + chooseUrethra))
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
            int chooseTotal = chooseUrethra + choosePharynx + chooseRectum ;
            int index = RAND.nextInt(chooseTotal) ;
            if (index < choosePharynx)    // choosePharynx = 3
                return pharynx ;
            if (index < (choosePharynx + chooseUrethra))   // chooseUrethra = 3
                return urethra ;
            return rectum ;
        }
        else    // if (site.getSite().equals(URETHRA))
        {
            int index = RAND.nextInt(10) ;
            if (index < 3)
                return pharynx ;
            if (index < 9) 
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
        if (index < 2)
            return urethra ;
        return pharynx ;
    }

    /**
     * Used when choosing Site other than Rectum for sexual encounter when Site
     * other than Rectum has already been chosen.
     * @param site
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

    @Override
    public void setRiskyStatus(boolean risky)
    {
        riskyStatus = risky ;
    }
    
    /**
     * Getter of seroSort variables, specific to Class of Relationship.
     * @param relationshipClazzName
     * @return 
     */
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

    /**
     * Setter of seroSortRegular
     * @param sort 
     */
    public void setSeroSortRegular(boolean sort)
    {
        seroSortRegular = sort ;
    }

    /**
     * Setter of seroSortCasual
     * @param sort 
     */
    public void setSeroSortCasual(boolean sort)
    {
        seroSortCasual = sort ;
    }

    /**
     * Setter of seroSort
     * @param sort 
     */
    public void setSeroSortMonogomous(boolean sort)
    {
        seroSortMonogomous = sort ;
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
    
    /**
     * Setter of consentCasualProbability.
     * @param casualProbability 
     */
    public void setConsentCasualProbability(double casualProbability)
    {
        consentCasualProbability = casualProbability ;
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
     * Getter for riskyStatus.
     * @return 
     */
    public boolean getRiskyStatus()
    {
        return riskyStatus ;
    }
    
    /**
     * Initialise prepStatus and set up screenCycle and screenTime accordingly.
     * @param prep 
     */
    private void initPrepStatus(boolean prep)
    {
        setPrepStatus(prep) ;
    }
    
    /**
     * Initialises screenCycle from a Gamma distribution to determine how often 
     * an MSM is screened, and then starts the cycle in a random place so that 
     * not every MSM gets screened at the same time.
     * @param rescale
     */
    @Override
    protected void initScreenCycle(double rescale)
    {
        if (getPrepStatus())
            setScreenCycle(((int) new GammaDistribution(31,1).sample()) + 61) ;
        else
        {
            //int firstScreenCycle = (int) new GammaDistribution(7,55).sample() ; 
            //setScreenCycle(firstScreenCycle) ;  // 49.9% screen within a year 2016
            if (statusHIV)
                setScreenCycle(sampleGamma(6,71,rescale)) ;  // 41% screen within a year
            else
                setScreenCycle(sampleGamma(6,85.5,rescale)) ;  // 26% screen within a year
            
        }
        // Randomly set timer for first STI screen 
        setScreenTime(RAND.nextInt(getScreenCycle()) + 1) ;
    }
    
    /**
     * Initialises screenCycle from a Gamma distribution to determine how often 
     * an MSM is screened, and then starts the cycle in a random place so that 
     * not every MSM gets screened at the same time.
     */
    protected void initScreenCycle_Site()
    {
        for (Site site : getSites())
            site.initScreenCycle(statusHIV, prepStatus,1) ;
    }
    
    /**
     * Adjusts per year the screening period.
     * @param year
     * @throws Exception 
     */
    @Override
    public void reinitScreenCycle(int year) throws Exception
    {
        if (year == 0)
            return ;
        
        // Go from 2007
        // Tests, given by per 1000 per year, from 2007-2016
        // Table 17 ARTB 2016
        double[] testRates = new double[] {333,340,398,382,383,382,391,419,445,499} ;
        double testBase ;
        //testBase = testRates[0] ;
        testBase = testRates[year-1] ;
        
        double ratio = testBase/testRates[year] ;
        int newScreenCycle = (int) Math.ceil(ratio * getScreenCycle()) ;
        setScreenCycle(newScreenCycle) ;

        /*double ratio = testBase/testRates[year] ;
        
        // Do not reinitialise MSM on Prep
        initScreenCycle(ratio) ;*/
    }
    
    /**
     * Resets the probability of Risky vs Safe behaviour according to the year.
     * Rates taken from GCPS 2011 Table 16, 2014 Table 15, 
     */
    public void reinitRiskOdds(int year) throws Exception
    {
        if (year == 0)
            return ;
        // Go from 2007, ARTB (Table 9, 2014) (Table 11, 2017)
        // Year-by-year rates of UAIC 
        int[] riskyOdds = new int[] {321,327,378,361,337,360,357,375,388,482,482} ;
        // Year-by-year rates of non-UAIC 
        // 2013- Table 11 2017, 2007-2012 Table 9 2014 * .7
        //int[] safeOdds = new int[] {679,673,622,639,663,640,643,625,622,518,518} ;
        int[] safeOdds = new int[] {475,471,435,447,464,448,443,445,421,398,398} ;
        // Ratios .403 , .410 , .465 , .447 , .421 , .446 , .446 , .457 , .480 , .548
        SAFE_ODDS = safeOdds[year] ;
        RISKY_ODDS = riskyOdds[year] ;
        //else
        {
            int totalOdds = SAFE_ODDS + RISKY_ODDS ;
            int lastRisky = riskyOdds[year-1] ;
            int lastOdds = safeOdds[year-1] + lastRisky ;
            double riskyProbability = ((double) RISKY_ODDS)/totalOdds ;
            double lastProbability = lastRisky/lastOdds ;
            double changeProbability ;
            
            if (lastProbability < riskyProbability) 
            {
                if (!getRiskyStatus())
                    changeProbability = (riskyProbability - lastProbability)/(1-lastProbability) ;
                else    // if risky already
                    return ;    // we don't change it
            }
            else    // riskyProbability has gone down
            {
                if (getRiskyStatus())
                    changeProbability = (lastProbability - riskyProbability)/lastProbability ;
                else    // if already safe
                    return ;    // we don't change it
            }
            riskyProbability *= changeProbability ;

            // Allow for correlation between statusHIV and Risky behaviour
            if (statusHIV)
                riskyProbability *= HIV_RISKY_CORRELATION ;
            else
                riskyProbability *= (1.0 - PROPORTION_HIV * HIV_RISKY_CORRELATION)/(1.0 - PROPORTION_HIV) ;

            riskyStatus = (RAND.nextDouble() < riskyProbability) ;
        }
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
     * Resets the probability of adjusting discloseStatusHIV according to changing 
     * disclose probabilities each year.
     * Probabilities taken from Table 9 of ARTB 2017.
     * @param year
     * @throws Exception 
     */
    public void reinitProbablityDiscloseHIV(int year) throws Exception
    {
        if (year == 0)
            return ;
        // Go from 2007
        double[] discloseProbability ;
        if (statusHIV)
            discloseProbability = new double[] {0.201,0.296,0.327,0.286,0.312,0.384,0.349,0.398,0.430,0.395} ;
        else
            discloseProbability = new double[] {0.175,0.205,0.218,0.239,0.229,0.249,0.236,0.295,0.286,0.352} ;
        
        double newDiscloseProbability = discloseProbability[year] ;
        double oldDiscloseProbability = discloseProbability[year-1] ;
        double changeProbability ;
        if (newDiscloseProbability > oldDiscloseProbability)
        {
            if (discloseStatusHIV)
                return ;
            changeProbability = (newDiscloseProbability - oldDiscloseProbability)/(1 - oldDiscloseProbability) ;
        }
        else    // if less likely to disclose
        {
            if (!discloseStatusHIV)
                return ;
            changeProbability = (oldDiscloseProbability - newDiscloseProbability)/oldDiscloseProbability ;
        }
        initSeroStatus(newDiscloseProbability * changeProbability) ;
    }
    
    /**
     * Alters the probability of being on antiViral medication with undetectable
     * virus blood concentration from year to year. If the probability increases 
     * then only MSM not on antiviral medication will change, and vice versa if the probability
     * decreases.
     * @param year 
     */
    public void reinitProbabilityAntiViral(int year) 
    {
        if (year == 0)
            return ;
        
        double[] probabilityAntiViral = new double[] {0.532, 0.706, 0.735, 0.689, 0.706, 0.802, 0.766, 0.830, 0.818, 0.854} ;
        double newProbability = probabilityAntiViral[year] ;
        double oldProbability = probabilityAntiViral[year-1] ;
        double changeProbability ;
        if (newProbability > oldProbability)
        {
            if (discloseStatusHIV)
                return ;
            changeProbability = (newProbability - oldProbability)/(1 - oldProbability) ;
        }
        else    // Probability of being on antiViral medication decreases.
        {
            if (!discloseStatusHIV)
                return ;
            changeProbability = (oldProbability - newProbability)/oldProbability ;
        }
        newProbability *= changeProbability ;
        
        setAntiViralStatus(RAND.nextDouble() < newProbability) ;
    }
    
    /**
     * Setter of prepStatus.
     * screenCycle is initiated here because it is prepStatus dependent.
     * @param prep 
     */
    public void setPrepStatus(boolean prep)
    {
        prepStatus = prep && (!statusHIV) ;
        initScreenCycle(382.0/333.0) ;    // Rescale for 2010
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
    @Override
    public boolean consent(String relationshipClazzName, Agent partner)
    {
        if (getSeroSort(relationshipClazzName))
            if (!String.valueOf(getStatusHIV()).equals(declareStatus()))
                return false ;
        return super.consent(relationshipClazzName, partner) ;
    }
    
    /**
     * 
     * @param partner
     * @return (boolean) whether to enter proposed Casual Relationship.
     */
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

    /**
     * getter() for MAX_RELATIONSHIPS.
     * @return MAX_RELATIONSHIPS
     */
    @Override
    protected int getMaxRelationships()
    {
        return MAX_RELATIONSHIPS ;
    }

        /**
     * getter() of static PROPORTION_HIV.
     * @return (double) The proportion of MSM who are HIV positive.
     */
    protected double getProportionHIV()
    { 
        return PROPORTION_HIV ;
    }
    

    final private double getAntiviralProbability()
    {
        return PROBABILITY_ANTIVIRAL ;
    }
    
    /**
     * HIV positive MSM are more likely to disclose the statusHIV
     * @return (Double) probability of disclosing statusHIV
     */
    protected double getProbabilityDiscloseHIV()
    {
        if (getStatusHIV())
            return PROBABILITY_DISCLOSE_POSITIVE_HIV ;
        return PROBABILITY_DISCLOSE_NEGATIVE_HIV ;
    }
    
    
    public double getProbabilityPrep() 
    {
        return PROBABILITY_PREP ;
    }
    
    public double getProbabilitySeroSort(boolean hivStatus)
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_SERO_SORT ;
        return PROBABILITY_NEGATIVE_SERO_SORT ;
    }

    public double getProbabilitySeroSortCasual(boolean hivStatus)
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_CASUAL_SERO_SORT ;
        return PROBABILITY_NEGATIVE_CASUAL_SERO_SORT ;
    }

    public double getProbabilitySeroSortRegular(boolean hivStatus)
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_REGULAR_SERO_SORT ;
        return PROBABILITY_NEGATIVE_REGULAR_SERO_SORT ;
    }

    public double getProbabilitySeroSortMonogomous(boolean hivStatus)
    {
        if (hivStatus)
            return PROBABILITY_POSITIVE_MONOGOMOUS_SERO_SORT ;
        return PROBABILITY_NEGATIVE_MONOGOMOUS_SERO_SORT ;
    }

    public double getProbabilitySeroPosition(boolean hivStatus) 
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
    //@Override
    //abstract protected boolean chooseCondom(String relationshipClazzName, Agent msm);
    
    
    /**
     * Decides probabilistically whether MSM chooses to use a condom in a given encounter.
     * RiskyMSM choose use strategies other than condoms
     * @param partner
     * @param agentPartner
     * @return true if condom is to be used, false otherwise
     */
    @Override
    protected boolean chooseCondom(String relationshipClazzName, Agent agentPartner) 
    {
        //if (2 < 0)
          //  return (RAND.nextDouble() < probabilityUseCondom ) ;
        MSM partner = (MSM) agentPartner ;
        if (riskyStatus)
        {
            String partnerDisclosure = partner.declareStatus() ;
            //Boolean partnerSeroPosition = partner.getSeroPosition() ;

            // Not if on PrEP
            if (getPrepStatus())
                if (RAND.nextDouble() > probabilityUseCondom)    // '>' intended
                    return false ;

            if (getSeroSort(relationshipClazzName))    // might use condom when serodiscordance or nondisclosure
            {
                if (String.valueOf(getStatusHIV()).equals(partnerDisclosure)) 
                    return false ;
                /*
                {
                    if (RAND.nextDouble() < probabilityUseCondom ) 
                        return true;
                    if (!(getStatusHIV()) || (partner.getPrepStatus())) // !getPrepStatus() || 
                        return (RAND.nextDouble() < probabilityUseCondom ) ;
                    else if (!getStatusHIV() && !partner.getAntiViralStatus())
                        return (RAND.nextDouble() < probabilityUseCondom ) ;
                }*/
            }
            if (getSeroPosition())
                if (!(NONE.equals(partnerDisclosure)))  // maybe if partner does not disclose
                    return false; // (RAND.nextDouble() < probabilityUseCondom ) ;
            return (RAND.nextDouble() < probabilityUseCondom ) ;
        }
        else    // if not risky
        {
            //if (2 > 0)
              //  return true ;
            //LOGGER.severe("Safe Agent:" + String.valueOf(getAgentId()));
            if (getStatusHIV())
                if (!getAntiViralStatus())
                    return true ;
            else if (!getPrepStatus())
            {
                if (!partner.getDiscloseStatusHIV()) // Partner doesn't disclose
                    return true ; 
                if (partner.getStatusHIV() && !partner.getAntiViralStatus()) // Partner HIV +ve without supressed viral load
                    return true ;
            }
            return (RAND.nextDouble() < probabilityUseCondom ) ;  //TODO: Should there be subset who always use?
        }
    }
    
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
    public double getJoinGroupSexEventProbability()
    {
        if (riskyStatus)
            return 1.0/(3 * 184) ; // RiskyMSM.JOIN_GSE_PROBABILITY ;
        return 0.0 ; // SafeMSM.JOIN_GSE_PROBABILITY ;
    }
    
    /**
     * Probability of MSM screening on that day. Depends on prepStatus and statusHIV.
     * @param args
     * @return for PrEP users, 1.0 if cycle multiple of screenCycle, 0.0 otherwise
     *     for non-PrEP users, random double between 0.0 and 1.0 .
     */    
    @Override
    public double getScreenProbability(Object[] args)
    {
            // Find current cycle
        //    int cycle = Integer.valueOf(args[0]) ;
            
        // Countdown to next STI screen
        decrementScreenTime() ;

        // Is it time for a regular screening?
        if ( getScreenTime() < 0)
        {
            setScreenTime(getScreenCycle()) ;
            return 1.1 ;
        }
        else
            return -0.1 ;
    }
    
    /**
     * Call getScreenProbability for each Site and choose to screen if 
     * any Site call chooses to screen. 
     * @param args
     * @return Probability of screening. Values outside of range 0 - 1 used 
     * to protect against roundoff error.
     */
    public double chooseScreen(Object[] args)
    {
        for (Site site : getSites())
        {
            if (site.getScreenProbability(args)> 0 )
                return 1.1 ;
        }
        return -0.1 ; 
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
        report += Reporter.ADD_REPORT_PROPERTY("consentCasualProbability", consentCasualProbability) ;
        return super.ageEffects() + report ;
    }*/
    
    static public void SET_ADJUST_CASUAL_CONSENT(double adjustCasual)
    {
        ADJUST_CASUAL_CONSENT = adjustCasual ;
    }
    
    static public void TEST_SET_INFECT_PROBABILITY()
    {
        String[] siteNames = new String[] {URETHRA,RECTUM,PHARYNX} ;
        try
        {
            double transmission ;
            String probabilityString ;
            for (String infectedSiteName : siteNames)
                for (String clearSiteName : siteNames)
                {
                    transmission = RAND.nextDouble() ;
                    SET_INFECT_PROBABILITY(infectedSiteName, clearSiteName, transmission) ;
                    probabilityString = infectedSiteName.toUpperCase() + "_TO_" + clearSiteName.toUpperCase() ;
        assert(MSM.class.getDeclaredField(probabilityString).getDouble(null) == transmission) : probabilityString + "not correctly assigned" ;
                }
        }
        catch ( Exception e )
        {
            LOGGER.log(Level.SEVERE, "{0} : {1}", new Object[]{e.getClass().getName(), e.getLocalizedMessage()});
        }
    	
    }
            
}
