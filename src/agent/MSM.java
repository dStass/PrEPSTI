/**
 * 
 */
package agent;

//import static agent.Agent.LOGGER;
import community.Relationship;
import reporter.Reporter ;

import java.util.logging.Level;
import site.* ;
        
import java.lang.reflect.*;
import java.util.ArrayList ;
import java.util.Arrays;
import java.util.HashMap ;
import java.util.Collection ;
import java.util.Collections;
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
    static double PROBABILITY_DISCLOSE_POSITIVE_HIV =  0.2 ; // 0.286 ; // 2010 VALUE // 0.2 ; // 
    /** The probability of disclosing HIV status if HIV negative */
    static double PROBABILITY_DISCLOSE_NEGATIVE_HIV = 0.18 ; // 0.239 ; // 2010 VALUE // 
    /** Probability of serosorting if HIV positive (2017) */
    static double PROBABILITY_POSITIVE_SERO_SORT = 0.59 ;
    /** Probability of serosorting if HIV negative (2017) */
    static double PROBABILITY_NEGATIVE_SERO_SORT = 0.45 ;
    /** Probability of serosorting in Casual Relationship if HIV positive */
    static double PROBABILITY_POSITIVE_CASUAL_SERO_SORT = 0.630 ; // 0.431 ;
    // .630, .630, .630, .630, .630, .710, .586, .673, .510, .550, .341
    /** Probability of serosorting in Casual Relationship if HIV negative */
    static double PROBABILITY_NEGATIVE_CASUAL_SERO_SORT = 0.513 ; // 0.485 ;
    // .513, .513, .513, .513, .513, .579, .480, .474, .547, .520, .485
    /** Probability of serosorting in Regular Relationship if HIV positive */
    static double PROBABILITY_POSITIVE_REGULAR_SERO_SORT = 0.358 ;
    // .358, .397, .344, .397, .378, .495, .404, .347, .408, .374, .274
    /** Probability of serosorting in Regular Relationship if HIV negative */
    static double PROBABILITY_NEGATIVE_REGULAR_SERO_SORT = 0.473 ;
    // .473, .618, .643, .515, .744, .763, .720, .731, .709, .712, .701
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
    static double PROPORTION_ANTIVIRAL = 0.566 ;
    
    /**
     * Used to change the value of PROPORTION_ANTIVIRAL
     * @param antiViral 
     */
    static void SET_PROBABILITY_ANTIVIRAL(double antiViral)
    {
        PROPORTION_ANTIVIRAL = antiViral ;
    }
    
    /**
     * Coordinates the reinitialisation of Agent parameters when they change 
     * year-by-year.
     * @param agentList
     * @param year
     * @return 
     */
    static public String REINIT(ArrayList<Agent> agentList, int year) 
    {
        String report = "" ;
        //boolean successful = true ;
        String change = "change" ;
        String methodName = "" ;
        try
        {
            methodName = "undetectable" ;
            report += Reporter.ADD_REPORT_PROPERTY(change, methodName) ;
            report += REINIT_PROPORTION_UNDETECTABLE(agentList, year) ;
            
            methodName = "disclosure" ;
            REINIT_PROBABILITY_DISCLOSURE_HIV(agentList, year) ;
            
            methodName = "riskinessCasual" ;
            report += Reporter.ADD_REPORT_PROPERTY(change, methodName) ;
            report += REINIT_RISK_CASUAL(agentList, year) ;
            
            methodName = "riskinessRegular" ;
            report += Reporter.ADD_REPORT_PROPERTY(change, methodName) ;
            report += REINIT_RISK_REGULAR(agentList, year) ;
            
            methodName = "trust_undetectable" ;
            REINIT_TRUST_UNDETECTABLE(agentList, year) ;
            //REINIT_USE_GSN(agentList, year) ;
        }
        catch ( Exception e )
        {
            LOGGER.severe(e.toString() + " in method " + methodName) ;
            //return false ;
        }
        return report ;
    }
    
    /**
     * Alters the probability of being on antiViral medication with undetectable
     * virus blood concentration from year to year. If the probability increases 
     * then only MSM not on antiviral medication will change, and vice versa if the probability
     * decreases.
     * Taken from Table 17 ARTB 2017 and Table 16 ARTB 2018.
     * @param agentList (ArrayList) List of Agents to undergo parameter change.
     * @param year (int) Year of simulation, starting from 0.
     * @throws java.lang.Exception 
     */
    static protected String REINIT_PROPORTION_UNDETECTABLE(ArrayList<Agent> agentList, int year) throws Exception
    {
        String report = "" ;
        if (year == 0)
            return report ;
        
        // years 2007 onwards
        //double[] probabilityAntiViral = new double[] {0.532, 0.706, 0.735, 0.689, 0.706, 0.802, 0.766, 0.830, 
        // 0.818, 0.854, 0.890, 0.890, 0.890} ;
        double[] probabilityUndetectable = new double[] {0.566, 0.647, 0.701, 0.723, 0.747, 0.816, 0.734, 0.808, 
            0.856, 0.847, 0.918, 0.918, 0.918} ;
        // years 2007-2009
        // 0.532, 0.706, 0.735, 
        
        if (year >= probabilityUndetectable.length)
            year = probabilityUndetectable.length - 1 ;
        
        double newProbability = probabilityUndetectable[year] ;
        double oldProbability = probabilityUndetectable[year-1] ;
        double changeProbability ;
        boolean newStatus ;
        MSM msm ;
        for (Agent agent : agentList)
        {
            msm = (MSM) agent ;
            if (!msm.statusHIV)
                continue ;
            
            if (newProbability > oldProbability)
            {
                if (!msm.undetectableStatus)
                {
                    changeProbability = (newProbability - oldProbability)/(1 - oldProbability) ;
                    newStatus = RAND.nextDouble() < changeProbability ;
                    msm.setUndetectableStatus(newStatus) ;
                    report += Reporter.ADD_REPORT_PROPERTY(String.valueOf(msm.getAgentId()), newStatus) ;
                    continue ;
                }
            }
            else    // Probability of being on antiViral medication decreases.
            {
                if (msm.undetectableStatus)
                {
                    changeProbability = (oldProbability - newProbability)/oldProbability ;
                    newStatus = RAND.nextDouble() < changeProbability ;
                    msm.setUndetectableStatus(newStatus) ;
                    report += Reporter.ADD_REPORT_PROPERTY(String.valueOf(msm.getAgentId()), newStatus) ;
                    continue ;
                }
            }
            //newProbability *= changeProbability ;
        }

        return report ;
    }
    
    /**
     * Alters the willingness to trust U=U as protection against HIV on a year-by-year
     * basis.
     * FIXME: Implement reporting of changes.
     * Data taken from GCPS: Table 21 in GCPS 2017, Table 20 in 2018
     * @param agentList (ArrayList) List of Agents to undergo parameter change.
     * @param year (int) Year of simulation starting from year zero.
     */
    static protected String REINIT_TRUST_UNDETECTABLE(ArrayList<Agent> agentList, int year) 
    {
        String report = "" ;
        double[] positiveTrustUndetectable = new double[] {0.0,0.0,0.0,0.0,0.0,0.0,0.483,0.772,
            0.692, 0.742, 0.804, 0.853, 0.853} ;
        double[] negativeTrustUndetectable = new double[] {0.0,0.0,0.0,0.0,0.0,0.0,0.106,0.094,
            0.129, 0.157, 0.203, 0.231, 0.231} ;
        
        if (year >= positiveTrustUndetectable.length)
            year = positiveTrustUndetectable.length - 1 ;
        
        double positiveLastProbability = positiveTrustUndetectable[year - 1] ;
        double positiveTrustProbability = positiveTrustUndetectable[year] ;
        double negativeLastProbability = negativeTrustUndetectable[year - 1] ;
        double negativeTrustProbability = negativeTrustUndetectable[year] ;
        double changeProbability ; 
        
        for (Agent agent : agentList)
        {
            MSM msm = (MSM) agent ;
            
            double lastProbability = 1.0 ; 
            double trustProbability = 1.0 ;
            
            if (msm.statusHIV)
            {
                lastProbability = positiveLastProbability ;
                trustProbability = positiveTrustProbability ;
            }
            else
            {
                lastProbability = negativeLastProbability ;
                trustProbability = negativeTrustProbability ;
            }
            
            if (trustProbability > lastProbability)
            {
                if (msm.trustUndetectable)
                    continue ;
                changeProbability = (trustProbability - lastProbability)/(1 - lastProbability) ;
            }
            else
            {
                if (!msm.trustUndetectable)
                    continue ;
                changeProbability = (lastProbability - trustProbability)/lastProbability ;
            }
        }
        return report ;
    }
    
    /**
     * Resets the probability of discloseStatusHIV according to changing 
     * disclose probabilities each year.
     * FIXME: Implement reporting of changes.
     * Probabilities taken from Table 9 of ARTB 2017
     * and Table 8 of ARTB 2018.
     * @param agentList (ArrayList) List of Agents to undergo parameter change.
     * @param year (int) Year of simulation, starting from zero.
     */
    static protected String REINIT_PROBABILITY_DISCLOSURE_HIV(ArrayList<Agent> agentList, int year) //throws Exception
    {
        String report = "" ;
        // Go from 2007
        double newDiscloseProbability ;
        double oldDiscloseProbability ;
        double changeProbability ;
        //if (statusHIV)
        double[] positiveDiscloseProbability = new double[] {0.201,0.296,0.327,0.286,0.312,0.384,0.349,0.398,
            0.430,0.395,0.461,0.461,0.461} ;
        //else
        double[] negativeDiscloseProbability = new double[] {0.175,0.205,0.218,0.239,0.229,0.249,0.236,0.295,
            0.286,0.352,0.391,0.391,0.391} ;
        // 2007 - 2009
        if (year >= positiveDiscloseProbability.length)
            year = positiveDiscloseProbability.length - 1 ;
        // positive 0.201,0.296,0.327,    negative 0.175,0.205,0.218,
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
                msm.setDiscloseStatusHIV(RAND.nextDouble() < changeProbability);
            }
            else    // if less likely to disclose
            {
                if (!msm.discloseStatusHIV)
                    continue ;
                changeProbability = (oldDiscloseProbability - newDiscloseProbability)/oldDiscloseProbability ;
                msm.setDiscloseStatusHIV(RAND.nextDouble() > changeProbability);
                //msm.initSeroStatus(changeProbability) ;
            }
            
        }
        return report ;
    }
    
    /**
     * Resets the probability of Risky vs Safe behaviour according to the year.
     * Rates taken from GCPS 2011 Table 16, 2014 Table 15, 
     * @param agentList (ArrayList) List of Agents to undergo parameter change.
     * @param year (int) Year of simulation, starting from zero.
     * @return (String) report of changes made to MSM.probabilityUseCondom .
     * @throws java.lang.Exception (Exception)
     */
    static protected String REINIT_RISK_CASUAL(ArrayList<Agent> agentList, int year) throws Exception
    {
        String report = "" ;
        if (year == 0)
            return report ;
        // GCPS (Table 15, 2011) (Table 14, 2013) (Table 16, 2017-18)
        // Year-by-year rates of UAIC 
        int[] newRiskyCasual = new int[] {290,293,369,345,331,340,364,350,362,409,520,566,566} ;
        int[] newSafeCasual = new int[] {468,514,471,501,469,465,444,473,440,424,307,264,264} ;
        
        if (year >= newRiskyCasual.length)
            year = newRiskyCasual.length - 1 ;
        // newRiskyProportions         {.38,.36,.44,.41,.41,.42,.45,.43,.45,.49,.63} ;
        // total_odds                 {758,807,840,846,800,805,808,823,802,831,827}
        SAFE_ODDS_CASUAL = newSafeCasual[year] ;
        RISKY_ODDS_CASUAL = newRiskyCasual[year] ;
        
        int totalOdds = SAFE_ODDS_CASUAL + RISKY_ODDS_CASUAL ;
        int lastRisky = newRiskyCasual[year-1] ;
        int lastSafe = newSafeCasual[year-1] ;
        int lastTotal = lastSafe + lastRisky ;
        double riskyProbability = ((double) RISKY_ODDS_CASUAL)/totalOdds ;
        double safeProbability = ((double) SAFE_ODDS_CASUAL)/totalOdds ;
        double lastProbabilityRisk = ((double) lastRisky)/lastTotal ;
        double lastProbabilitySafe = ((double) lastSafe)/lastTotal ;
        double changeProbability ;
        
        boolean moreRisky = (lastProbabilityRisk < riskyProbability) ;
        double adjustProbabilityUseCondom = safeProbability/lastProbabilitySafe ; // SAFE_ODDS/newSafeOdds[year-1] ;
        double screeningRatio = Agent.TEST_RATES[0]/Agent.TEST_RATES[year - 1] ;
                                                                    // see comments below
        
        //riskyProbability *= changeProbability ;
        //double riskyProbabilityPositive = riskyProbability ; //* HIV_RISKY_CORRELATION ;
        //double riskyProbabilityNegative = riskyProbability ; //* (1.0 - PROPORTION_HIV * HIV_RISKY_CORRELATION)/(1.0 - PROPORTION_HIV) ;
        double hivFactor ;
        HashMap<String,String> record = new HashMap<String,String>() ;
        MSM msm ;
        for (Agent agent : agentList)
        {
            msm = (MSM) agent ;
            
            // Compensates for allowing only change in one direction.
            if (moreRisky) 
            {
                hivFactor = GET_HIV_RISKY_CORRELATION(msm.statusHIV) ;
                changeProbability = hivFactor * (riskyProbability - lastProbabilityRisk)/(1-lastProbabilityRisk * hivFactor) ;
            }
            else
                changeProbability = riskyProbability/lastProbabilityRisk ; //(lastProbability - riskyProbability)/lastProbability ;

        
            record.put("probabilityUseCondomCasual",String.valueOf(msm.scaleProbabilityUseCondomCasual(adjustProbabilityUseCondom))) ;
            boolean currentRisky = msm.riskyStatusCasual ;
            if (moreRisky) 
            {
                if (!msm.getRiskyStatusCasual()) // if risky already we don't change it
                    msm.setRiskyStatusCasual(RAND.nextDouble() < changeProbability) ;
                
                // Record changes
                if (msm.riskyStatusCasual != currentRisky)
                    record.put("riskyStatus", String.valueOf(msm.riskyStatusCasual)) ;
                hivFactor = GET_HIV_RISKY_CORRELATION(msm.statusHIV) ;
                if (msm.reinitPrepStatus(year, riskyProbability * hivFactor))
                {
                    record.put("prepStatus", String.valueOf(msm.prepStatus)) ;
                    msm.reInitScreenCycle(1.0) ;
                }
            }
            else    // riskyProbability has gone down
            {
                if (msm.getRiskyStatusCasual()) // if safe already we don't change it
                {
                    // equivalent to correct calculation: RAND > (1 - changeProbability)
                    msm.setRiskyStatusCasual(RAND.nextDouble() < changeProbability) ; 
                }
                
                // Record changes
                if (msm.riskyStatusCasual != currentRisky)
                    record.put("riskyStatus", String.valueOf(msm.riskyStatusCasual)) ;
                hivFactor = GET_HIV_RISKY_CORRELATION(msm.statusHIV) ;
                if (msm.reinitPrepStatus(year, riskyProbability * hivFactor))
                {
                    record.put("prepStatus", String.valueOf(msm.prepStatus)) ;
                    msm.reInitScreenCycle(screeningRatio) ;
                }
            }
            report += Reporter.ADD_REPORT_PROPERTY(String.valueOf(msm.getAgentId()), record.toString()) ;
            record.clear() ;
        }
        return report ;
    }
    
    /**
     * Resets the probability of Risky vs Safe behaviour according to the year.
     * Rates taken from GCPS 2011 Table 16, 2014 Table 15, 
     * @param agentList (ArrayList) List of Agents to undergo parameter change.
     * @param year (int) Year of simulation, starting from zero.
     * @return (String) report of changes made to MSM.probabilityUseCondom .
     * @throws java.lang.Exception (Exception)
     */
    static protected String REINIT_RISK_REGULAR(ArrayList<Agent> agentList, int year) throws Exception
    {
        String report = "" ;
        if (year == 0)
            return report ;
        // GCPS (Table 15, 2011) (Table 14, 2013) (Table 16, 2017-18)
        // Year-by-year rates of UAIC 
        int[] newRiskyRegular = new int[] {538,540,538,604,493,513,503,520,576,557,618,650,685} ;
        int[] newSafeRegular = new int[] {300,300,300,296,279,247,257,248,239,203,157,131,108} ;
        
        if (year >= newRiskyRegular.length)
            year = newRiskyRegular.length - 1 ;
        // newRiskyProportions         {.38,.36,.44,.41,.41,.42,.45,.43,.45,.49,.63} ;
        // total_odds                 {758,807,840,846,800,805,808,823,802,831,827}
        SAFE_ODDS_REGULAR = newSafeRegular[year] ;
        RISKY_ODDS_REGULAR = newRiskyRegular[year] ;
        
        int totalOdds = SAFE_ODDS_REGULAR + RISKY_ODDS_REGULAR ;
        int lastRisky = newRiskyRegular[year-1] ;
        int lastSafe = newSafeRegular[year-1] ;
        int lastTotal = lastSafe + lastRisky ;
        double riskyProbability = ((double) RISKY_ODDS_REGULAR)/totalOdds ;
        double safeProbability = ((double) SAFE_ODDS_REGULAR)/totalOdds ;
        double lastProbabilityRisk = ((double) lastRisky)/lastTotal ;
        double lastProbabilitySafe = ((double) lastSafe)/lastTotal ;
        double changeProbability ;
        
        boolean moreRisky = (lastProbabilityRisk < riskyProbability) ;
        double adjustProbabilityUseCondom = safeProbability/lastProbabilitySafe ; // SAFE_ODDS/newSafeOdds[year-1] ;
        //double screeningRatio = Agent.TEST_RATES[0]/Agent.TEST_RATES[year - 1] ;
                                                                    // see comments below
        
        //riskyProbability *= changeProbability ;
        //double riskyProbabilityPositive = riskyProbability ; //* HIV_RISKY_CORRELATION ;
        //double riskyProbabilityNegative = riskyProbability ; //* (1.0 - PROPORTION_HIV * HIV_RISKY_CORRELATION)/(1.0 - PROPORTION_HIV) ;
        double hivFactor ;
        HashMap<String,String> record = new HashMap<String,String>() ;
        MSM msm ;
        for (Agent agent : agentList)
        {
            msm = (MSM) agent ;
            
            // Compensates for allowing only change in one direction.
            if (moreRisky) 
            {
                hivFactor = GET_HIV_RISKY_CORRELATION(msm.statusHIV) ;
                changeProbability = hivFactor * (riskyProbability - lastProbabilityRisk)/(1-lastProbabilityRisk * hivFactor) ;
            }
            else
                changeProbability = riskyProbability/lastProbabilityRisk ; //(lastProbability - riskyProbability)/lastProbability ;

        
            record.put("probabilityUseCondomRegular",String.valueOf(msm.scaleProbabilityUseCondomRegular(adjustProbabilityUseCondom))) ;
            boolean currentRisky = msm.riskyStatusRegular ;
            if (moreRisky) 
            {
                if (!msm.getRiskyStatusRegular()) // if risky already we don't change it
                    msm.setRiskyStatusRegular(RAND.nextDouble() < changeProbability) ;
                
                // Record changes
                if (msm.riskyStatusRegular != currentRisky)
                    record.put("riskyStatus", String.valueOf(msm.riskyStatusRegular)) ;
                hivFactor = GET_HIV_RISKY_CORRELATION(msm.statusHIV) ;
//                if (msm.reinitPrepStatus(year, riskyProbability * hivFactor))
//                {
//                    record.put("prepStatus", String.valueOf(msm.prepStatus)) ;
//                    msm.reInitScreenCycle(1.0) ;
//                }
            }
            else    // riskyProbability has gone down
            {
                if (msm.getRiskyStatusRegular()) // if safe already we don't change it
                {
                    // equivalent to correct calculation: RAND > (1 - changeProbability)
                    msm.setRiskyStatusRegular(RAND.nextDouble() < changeProbability) ; 
                }
                
                // Record changes
                if (msm.riskyStatusRegular != currentRisky)
                    record.put("riskyStatus", String.valueOf(msm.riskyStatusRegular)) ;
                hivFactor = GET_HIV_RISKY_CORRELATION(msm.statusHIV) ;
//                if (msm.reinitPrepStatus(year, riskyProbability * hivFactor))
//                {
//                    record.put("prepStatus", String.valueOf(msm.prepStatus)) ;
//                    msm.reInitScreenCycle(screeningRatio) ;
//                }
            }
            report += Reporter.ADD_REPORT_PROPERTY(String.valueOf(msm.getAgentId()), record.toString()) ;
            record.clear() ;
        }
        return report ;
    }
    
    /**
     * Resets the probability of MSM using a GSN year-by-year.
     * Values taken from GCPS "Where men met their male sex partners in the six months
     * prior to the survey. (Table 20 2013, Table 10 2018)
     * @param agentList (ArrayList) List of Agents to undergo parameter change.
     * @param year (int) Year of simulation, starting from zero.
     */
    static protected void REINIT_USE_GSN(ArrayList<Agent> agentList, int year)
    {
        if (year == 0)
            return ;
        double[] gsnArray = new double[] {0.0,0.0,0.0,0.0, 22.9, 31.5, 36.1, 41.9, 46.0, 49.5, 48.8, 50.8} ;
        
        double probabilityGSN = gsnArray[year] ;
        if (probabilityGSN == 0.0)
            return ;
        double lastProbabilityGSN = gsnArray[year - 1] ;
        
        boolean moreGSN = ( lastProbabilityGSN < probabilityGSN ) ;
        if (lastProbabilityGSN == 0.0)
            for (Agent agent : agentList)
                agent.setUseGSN(RAND.nextDouble() < probabilityGSN) ;
        else
        {
            double changeProbability ;

            for (Agent agent : agentList)
            {
                // Compensates for allowing only change in one direction.
                if (moreGSN) 
                {
                    changeProbability = (probabilityGSN - lastProbabilityGSN)/(1-lastProbabilityGSN) ;
                }
                else
                    changeProbability = probabilityGSN/lastProbabilityGSN ;


                if (moreGSN) 
                {
                    if (agent.useGSN) // if using GSN already
                        continue ;    // we don't change it
                    agent.setUseGSN(RAND.nextDouble() < changeProbability) ;
                }
                else    // riskyProbability has gone down
                {
                    if (!agent.useGSN)// if not using GSN already
                        continue ;    // we don't change it
                    // equivalent to correct calculation: RAND > (1 - changeProbability)
                    agent.setUseGSN(RAND.nextDouble() < changeProbability) ; 
                }
            }
        }
    }
    
    /**
     * Resets the probability of consenting to a Casual relationship according to 
     * the year and the HIV status of each MSM.
     * Rates taken from GCPS 2011 Table 16, 2014 Table 15, 
     * @param agentList (ArrayList) List of Agents to undergo parameter change.
     * @param year (int) Year of simulation, starting from zero.
     */
    static protected String REINIT_CONSENT_CASUAL_PROBABILITY(ArrayList<Agent> agentList, int year)
    {
        String report = "" ;
        double[] positiveProbabilities = new double[] {1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0} ;
        double[] negativeProbabilities = new double[] {1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0} ;
        
        double consentProbability = 1.0 ;
        for (Agent agent : agentList)
        {
            MSM msm = (MSM) agent ;
            if (msm.getStatusHIV())
                consentProbability = msm.consentCasualProbability * positiveProbabilities[year] ;
            else
                consentProbability = msm.consentCasualProbability * negativeProbabilities[year] ;
            msm.setConsentCasualProbability(consentProbability);
            // May change to msm.rescaleConsentCasualProbability(consentStatus[year]/consentStatus[year-1]) ;
        }
        return report ;
    }
    
    /**
     * 
     * @param year
     * @param hivStatus
     * @return (double) The probability of an Agent being willing to have anal sex in 
     * any given year, assuming that changes over time, according to his HIV status.
     */
    static double PROPORTION_ANAL_SEX(int year, boolean hivStatus)
    {
        double[] proportionAbstain = new double[] {} ;
        
        
        if (hivStatus)
        {
            //Assuming undetectable viral load
            proportionAbstain = new double[] {0.146,0.154,0.177,0.186,0.169,
            0.179,0.198,0.210,0.204} ;
        }
        else
            proportionAbstain = new double[] {0.179,0.174,0.183,0.200,0.211,
        0.220,0.212,0.214} ;
        
        // from 2013 .200,0.180,0.191,0.178,0.172
        
        return 1.0 - proportionAbstain[year] ;
    }
    
    /**
     * Generate relationships among the availableAgentList. 
     * Serosorting MSM are treated first to ensure that they don't miss out unduly. 
     * Those entering Relationships are removed.
     * FIXME: Not robust against reordering of relationshipClazzNames
     * 
     * @return (String) report of Relationships generated
     */
    static public String GENERATE_RELATIONSHIPS(ArrayList<Agent> availableAgentList, String[] relationshipClazzNames)
    {
        String report = "" ;
        Class<?> relationshipClazz ;
    
        ArrayList<MSM> seroSortList ;
        
        for (String relationshipClazzName : relationshipClazzNames)
        {
            ArrayList<Agent> relationshipAgentList = MSM.SEEKING_AGENTS(availableAgentList,relationshipClazzName) ;
        
            seroSortList = new ArrayList<MSM>() ;
            
            // Sort seekers according to HIV and serosorting status
            for (Agent agent : relationshipAgentList) 
            {
                MSM msm = (MSM) agent ;
                if (msm.getSeroSort(relationshipClazzName))
                    seroSortList.add(msm) ;
            }

            for (int index0 = seroSortList.size() - 1 ; index0 >= 0 ; index0-- )
            {
                MSM msm0 = seroSortList.get(index0) ;
                relationshipAgentList.remove(msm0) ;
                for (Agent agent : relationshipAgentList)
                {
                    MSM msm1 = (MSM) agent ;

                    // Check seroconcordance
                    if (msm1.statusHIV != msm0.statusHIV)
                        continue ;

                    // Have only one Relationship between two given MSM 
                    if (msm1.getCurrentPartnerIds().contains(msm0.getAgentId()))
                        continue ;

                    //seroSortList.remove(msm0) ;
                    relationshipAgentList.remove(agent) ;

                    availableAgentList.remove(msm0) ;
                    availableAgentList.remove(agent) ;

                    if (seroSortList.contains(msm1))  // Same MSM ArrayList
                    {
                        seroSortList.remove(msm1) ;
                        index0-- ;
                    }

                    try
                    {
                        relationshipClazz = Class.forName("community." + relationshipClazzName) ;
                        Relationship relationship = (Relationship) relationshipClazz.newInstance() ;
                        report += relationship.addAgents(msm0, msm1);
                    }
                    catch( Exception e )
                    {
                        LOGGER.severe(e.toString()) ;
                    }
                    break ;

                    // No longer available for other Relationships
                    //availableAgents.remove(agent0) ;
                    //availableAgents.remove(agent1) ;
                }
            }

            for (int index0 = relationshipAgentList.size() - 1 ; index0 > 0 ; index0--)
            {
                MSM msm0 = (MSM) relationshipAgentList.get(index0) ;
                for (int index1 = index0 - 1 ; index1 >= 0 ; index1-- )
                {
                    MSM msm1 = (MSM) relationshipAgentList.get(index1) ;

                    if (msm1.statusHIV != msm0.statusHIV) // First two ArrayList are serosorters
                        continue ;

                    // Have only one Relationship between two given MSM 
                    if (msm1.getCurrentPartnerIds().contains(msm0.getAgentId()))
                        continue ;

                    relationshipAgentList.remove(index0) ;
                    relationshipAgentList.remove(msm1) ;

                    availableAgentList.remove(msm0) ;
                    availableAgentList.remove(msm1) ;
                    index0-- ;

                    try
                    {
                        relationshipClazz = Class.forName("community." + relationshipClazzName) ;
                        Relationship relationship = (Relationship) relationshipClazz.newInstance() ;
                        //incrementNbRelationships() ;
                        report += relationship.addAgents(msm0, msm1);
                    }
                    catch( Exception e )
                    {
                        LOGGER.severe(e.toString()) ;
                    }
                    break ;

                    // No longer available for other Relationships
                    //availableAgents.remove(agent0) ;
                    //availableAgents.remove(agent1) ;
                }
            }

            
        }
        return report ;
    }
    
    /**
     * Returns an ArrayList of Agents seeking a relationshipClazzName Relationship.
     * 
     * @param agentList (ArrayList) List of Agents to be considered for specified Relationship Class.
     * @param relationshipClazzName (String) Name of Relationship sub-Class. 
     * @return 
     */
    static public ArrayList<Agent> SEEKING_AGENTS(ArrayList<Agent> agentList, String relationshipClazzName)
    {
        ArrayList<Agent> seekingAgentList = new ArrayList<Agent>() ;
        
        // Determine which Agents seek out which Relationship Class
        for (Agent agent : agentList)
        {
            MSM msm = (MSM) agent ;
            if (msm.seekRelationship(relationshipClazzName))
                seekingAgentList.add(msm) ;
        }
        //Collections.shuffle(seekingAgentList,RAND) ;
        
        return seekingAgentList ;    
    }
    
    /**
     * 
     * @param hivStatus
     * @return (double) Factor to adjust riskyProbability according to statusHIV.
     */
    static double GET_HIV_RISKY_CORRELATION(boolean hivStatus)
    {
        if (hivStatus)
            return HIV_RISKY_CORRELATION ;
        return (1.0 - PROPORTION_HIV * HIV_RISKY_CORRELATION)/(1.0 - PROPORTION_HIV) ;
    }
    
    /** The probability of being on PrEP, given negative HIV status */
    static double PROBABILITY_PREP = 0.0 ;
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
    /** Whether currently has an undetectable viral load given HIV +ve status */
    private boolean undetectableStatus ;
    /** Whether discloses HIV +ve status. */
    private boolean discloseStatusHIV ;
    /** Whether currently taking PrEP. */
    private boolean prepStatus ;
    /** Whether willing uses viral suppression as prophylaxis */
    private boolean trustUndetectable ;
    /** Whether trusts PrEP as prophylaxis */
    private boolean trustPrep ;
    /** Whether MSM is Risky, Safe otherwise. */
    private boolean riskyStatus ;
    /** Whether MSM is Risky within Casual Relationships, Safe otherwise. */
    private boolean riskyStatusCasual ;
    /** Whether MSM is Risky within Regular Relationships, Safe otherwise. */
    private boolean riskyStatusRegular ;
    
    /** Transmission probabilities per sexual contact from Urethra to Rectum */
    static double URETHRA_TO_RECTUM = 0.85 ; 
    /** Transmission probabilities sexual contact from Urethra to Pharynx. */
    static double URETHRA_TO_PHARYNX = 0.50 ; 
    /** Transmission probabilities sexual contact from Rectum to Urethra. */
    static double RECTUM_TO_URETHRA = 0.010 ;
    /** Transmission probabilities sexual contact from Rectum to Pharynx. */
    static double RECTUM_TO_PHARYNX = 0.025 ;
    /** Transmission probabilities sexual contact in Pharynx to Urethra intercourse. */
    static double PHARYNX_TO_URETHRA = 0.010 ; 
    /** Transmission probabilities sexual contact in Pharynx to Rectum intercourse. */
    static double PHARYNX_TO_RECTUM = 0.020 ; 
    /** Transmission probabilities sexual contact in Pharynx to Pharynx intercourse (kissing). */
    static double PHARYNX_TO_PHARYNX = 0.055 ; 
    /** Transmission probabilities sexual contact in Urethra to Urethra intercourse (docking). */
    static double URETHRA_TO_URETHRA = 0.020 ; 
    /** Transmission probabilities sexual contact in Rectum to Rectum intercourse. */
    static double RECTUM_TO_RECTUM = 0.020 ;

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
    
    /**
     * Allows changing of SITE_TO_SITE transmission probabilities.
     * @param infectedSiteName (String) Name of transmitting Site.
     * @param clearSiteName (String) Name of receiving Site.
     * @param transmission (double) New value for transmissionProbability.
     */
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
     * Allows setting of SITE_TO_SITE transmission probabilities.
     * @param infectedSiteName (String) Name of transmitting Site.
     * @param clearSiteName (String) Name of receiving Site.
     * @param transmission (double) New value for transmissionProbability.
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
     * 
     * @param infectedSite (Site) Infected Site capable of transmitting infection.
     * @param clearSite (Site) Infection-clear Site in danger of becoming infected.
     * @return infectProbability (double) the probability of infection of clearSite
     */
    public static double GET_INFECT_PROBABILITY(Site infectedSite, Site clearSite)
    {
    	double infectProbability = -1.0 ;
        String probabilityString = infectedSite.toString().toUpperCase() + "_TO_" + clearSite.toString().toUpperCase() ;
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
     * 
     * @return (String) Report specifying values of each transmissionProbability 
     * parameter.
     */
    public static String TRANSMISSION_PROBABILITY_REPORT()
    {
        String report = "" ;
        
        String[] SITENAMES = new String[] {"PHARYNX", "RECTUM", "URETHRA"} ;
        for (int siteIndex1 = 0 ; siteIndex1 < 3 ; siteIndex1++ )
            for (int siteIndex2 = 0 ; siteIndex2 < 3 ; siteIndex2++ )
            {
                String siteName1 = SITENAMES[siteIndex1] ;
                String siteName2 = SITENAMES[siteIndex2] ;
                String sitesTransmit = siteName1 + "_TO_" + siteName2 ;
                try
                {
                    double infectProbability = MSM.class.getDeclaredField(sitesTransmit).getDouble(null) ;
                    report += Reporter.ADD_REPORT_PROPERTY(sitesTransmit, infectProbability) ;
                }
                catch ( Exception e )
                {
                    LOGGER.log(Level.SEVERE, "{0} : {1}", new Object[]{e.getClass().getName(), e.getLocalizedMessage()});
                }
            }
        return report ;
    }
    /**
     * Choose sites for sexual contact, implementing seropositioning
     * if required. If so, Urethra of positive statusHIV msm is never chosen if 
     * couple is serodiscordant. 
     * Also check if either MSM refrains from anal intercourse in Casual Relationships.
     * @param agent0 (Agent) Participant in specified Relationship.
     * @param agent1 (Agent) Participant in specified Relationship.
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
     * @param msm0 (MSM) Participant in specified Relationship.
     * @param msm1 (MSM) Participant in specified Relationship.
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
     * @param msm0 (MSM) Participant in specified Relationship.
     * @param msm1 (MSM) Participant in specified Relationship.
     * @param relationshipClazzName (String) Name of Relationship sub-Class hosting 
     * the encounter.
     * @return (Site[]) Sites of sexual contact for msm0, msm1, respectively.
     */
    public static Site[] CHOOSE_SITES(MSM msm0, MSM msm1, String relationshipClazzName)
    {
        if (msm0.seroPosition && msm1.seroPosition)
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
    
    	
    static int SAFE_ODDS = 468 ;
    // Odds of an MSM being riskyMSM
    static int RISKY_ODDS = 290 ;
    // Sum of safeOdds and riskyOdds
    static int TOTAL_ODDS = RISKY_ODDS + SAFE_ODDS ;
//        int[] newSafeOdds = new int[] {468,514,471,501,469,465,444,473,440,424,307} ;
//       int[] newRiskyOdds = new int[] {290,293,369,345,331,340,364,350,362,409,520} ;

        static int SAFE_ODDS_CASUAL = 468 ;
    // Odds of an MSM being riskyMSM
    static int RISKY_ODDS_CASUAL = 290 ;
    // Sum of safeOdds and riskyOdds
    static int TOTAL_ODDS_CASUAL = RISKY_ODDS_CASUAL + SAFE_ODDS_CASUAL ;
    
    static int SAFE_ODDS_REGULAR = 300 ;
    // Odds of an MSM being riskyMSM
    static int RISKY_ODDS_REGULAR = 568 ;
    // Sum of safeOdds and riskyOdds
    static int TOTAL_ODDS_REGULAR = RISKY_ODDS_REGULAR + SAFE_ODDS_REGULAR ;


    /** 
     * Describes correlation between statusHIV and riskyStatus.
     * Must be less than 1/PROPORTION_HIV OR initRiskiness() fails.
     */
    static double HIV_RISKY_CORRELATION = 2.0 ; // 1.0 ;	
    
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
        setUndetectableStatus(RAND.nextDouble() < getAntiviralProbability()) ;

        // Randomly set PrEP status, ensuring it is true only if statusHIV is false
        // Now called within initRiskiness()
        //initPrepStatus() ;
        
        
        initRiskiness() ;
        
        trustUndetectable = false ;
        trustPrep = false ;
        
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
        riskyProbability *= GET_HIV_RISKY_CORRELATION(statusHIV) ;
        
        probabilityUseCondom = RAND.nextDouble() ; // sampleGamma(4, 0.1, 1) ; // Gamma2 * (1 - riskyProbability) * RAND.nextDouble() ;
        
        int totalOddsCasual = SAFE_ODDS_CASUAL + RISKY_ODDS_CASUAL ;
        double riskyProbabilityCasual = ((double) RISKY_ODDS_CASUAL)/totalOddsCasual ;
        riskyProbabilityCasual *= GET_HIV_RISKY_CORRELATION(statusHIV) ;
        
        probabilityUseCondomCasual = RAND.nextDouble() ; // sampleGamma(4, 0.1, 1) ; // Gamma2 * (1 - riskyProbability) * RAND.nextDouble() ;
        
        int totalOddsRegular = SAFE_ODDS_REGULAR + RISKY_ODDS_REGULAR ;
        double riskyProbabilityRegular = ((double) RISKY_ODDS_REGULAR)/totalOddsRegular ;
        riskyProbabilityRegular *= GET_HIV_RISKY_CORRELATION(statusHIV) ;
        
        probabilityUseCondomRegular = RAND.nextDouble() ; // sampleGamma(4, 0.1, 1) ; // Gamma2 * (1 - riskyProbability) * RAND.nextDouble() ;
        
        riskyStatus = (RAND.nextDouble() < riskyProbability) ;
        riskyStatusCasual = (RAND.nextDouble() < riskyProbabilityCasual) ;
        riskyStatusRegular = (RAND.nextDouble() < riskyProbabilityRegular) ;
        
        // Initialise PrEP status depending on adjusted riskyProbability.
        initPrepStatus(riskyProbability) ;
    } 
    
    /**
     * Chooses discloseStatusHIV according to probabilityDiscloseHIV and then 
     * chooses sero- Sort/Position parameters accordingly.
     * @param probabilityDiscloseHIV 
     */
    final void initSeroStatus(double probabilityDiscloseHIV)
    {
        discloseStatusHIV = (RAND.nextDouble() < probabilityDiscloseHIV) ;
        //if (discloseStatusHIV)
        {
            seroSortCasual = (RAND.nextDouble() < getProbabilitySeroSortCasual(statusHIV)) ;
            seroSortRegular = (RAND.nextDouble() < getProbabilitySeroSortRegular(statusHIV)) ;
            seroSortMonogomous = (RAND.nextDouble() < getProbabilitySeroSortMonogomous(statusHIV)) ;
            seroPosition = (RAND.nextDouble() < getProbabilitySeroPosition(statusHIV)) ;
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
     * 
     * For men who are not on PrEP the GCPS gives for the number of different partners
     * in the previous six months as
     *   0        1      2-10   11-50   51-99    100+
     * 0.1345   0.2301  0.4022  0.1761  0.0372  0.0200  
     * 
     * Previously we used the 2005 data from HIM, where the proportions
     * for each range are
     *   0       1-9      10+
     * 0.235    0.374    0.391
     */
    final void initConsentCasualProbability()
    {
        int[] lowerBounds ; 
        double[] proportions ;
        double[] cumulative ; 
        double consentProbability = 0 ;
        double timeAverage = 1 ;
        if (prepStatus)
        {
            lowerBounds = new int[] {0,1,2,6,11,21,51,100} ;
            proportions = new double[] {0.00311, 0.02715, 0.31857, 0.30353, 0.22172, 0.10170, 0.01972, 0.00450} ;
            cumulative = new double[] {0.00311, 0.00311, 0.00311, 0.00311, 0.00311, 0.00311, 0.00311, 0.00311} ;
            timeAverage = 92.0 ;
        }
        else
        {
            lowerBounds = new int[] {0,1,2,11,51,100} ;
            proportions = new double[] {0.1345, 0.2301, 0.4022, 0.1761, 0.0372, 0.02} ;
            cumulative = new double[] {0.1345, 0.1345, 0.1345, 0.1345, 0.1345, 0.1345} ;
            double offset1 = 0.15 ;
            cumulative[1] += offset1 ;
            timeAverage = 183.0 ;
        }
        // Now loop over proportions at each cumulIndex to fill out cumulative Array.
        for (int cumulIndex = 1 ; cumulIndex < cumulative.length ; cumulIndex++ )
            for (int propIndex = 1 ; propIndex <= cumulIndex ; propIndex++ )
                cumulative[cumulIndex] += proportions[propIndex] ;
        
        // Now take square root because both MSMs must consent
        //for (int cumulIndex = 0 ; cumulIndex < cumulative.length ; cumulIndex++ )
          //  cumulative[cumulIndex] = cumulative[cumulIndex] * cumulative[cumulIndex] ;
        
        // Choose range
        double rangeChoice = RAND.nextDouble() ;
        int rangeIndex = 0 ;
        for (int cumulIndex = 0 ; cumulIndex < cumulative.length ; cumulIndex++ )
        {
            if (rangeChoice < cumulative[cumulIndex])
            {
                rangeIndex = cumulIndex ;
                break ;
            }
        }
        
        if (rangeIndex == (proportions.length - 1)) // 100+ partners
        {
            double lowerProbability = lowerBounds[rangeIndex]/timeAverage ;
            if (lowerProbability > 1.0)
                consentProbability = 1.0 ;
            else
                consentProbability = RAND.doubles(lowerProbability, 1).iterator().nextDouble() ;
            
        }
        else
        {
            // Choose in range, take 92 days in 3 months
            double lowerProb = lowerBounds[rangeIndex]/timeAverage ;
            double upperProb = (lowerBounds[rangeIndex+1])/timeAverage ;
            if (upperProb <= lowerProb)    // for bins of width one, assumed to be clustered near zero
            {
                upperProb = lowerBounds[rangeIndex+1]/timeAverage ;
                //consentProbability =  Math.max(0.0,(lowerBounds[rangeIndex] + (RAND.nextDouble() - 0.5) )/timeAverage) ;
            }
            //else
                consentProbability = RAND.doubles(lowerProb, upperProb).iterator().nextDouble() ;
        }
        
        // Compensate for seroSorting
        /*if (seroSortCasual)
        {
            if (statusHIV)
                consentCasualProbability *= (1.0/PROPORTION_HIV) ;
            else    // !statusHIV 
                consentCasualProbability *= (1.0/(1.0 - PROPORTION_HIV)) ;
        }*/
        
        //double adjustConsent = 0.8 ;    
        // Logically /2.0 because two Agents in every Relationship, also requires sqrt, but better results without them.
        consentCasualProbability = consentProbability ;    // * ADJUST_CASUAL_CONSENT  ;
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
        censusReport += Reporter.ADD_REPORT_PROPERTY("riskyStatusCasual", riskyStatusCasual) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("riskyStatusRegular", riskyStatusRegular) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("undetectableStatus", undetectableStatus) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("trustUndetectable", trustUndetectable) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("trustPrep", trustPrep) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("consentCasualProbability", consentCasualProbability) ;
        
//        censusReport += Reporter.ADD_REPORT_PROPERTY("",) ;
//        censusReport += Reporter.ADD_REPORT_PROPERTY("",) ;
//        censusReport += Reporter.ADD_REPORT_PROPERTY("",) ;
        
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
     * @param site (Site) Site to choose in complement to.
     * @return random choice of rectum, urethra or pharynx
     */
    @Override
    protected Site chooseSite(Site site)
    {
        if (site.toString().equals(RECTUM))
        {
            int index = RAND.nextInt(6) ;
            if (index < 3) 
                return urethra ;
            else if (index < 5) 
                return pharynx ;
            else 
                return rectum ;
        }
        else if (site.toString().equals(PHARYNX))
        {
            int chooseTotal = chooseUrethra + choosePharynx + chooseRectum ;
            int index = RAND.nextInt(chooseTotal) ;
            if (index < choosePharynx)    // choosePharynx = 3
                return pharynx ;
            if (index < (choosePharynx + chooseUrethra))   // chooseUrethra = 3
                return urethra ;
            return rectum ;
        }
        else    // if (site.toString().equals(URETHRA))
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
     * @param site (Site) Non-rectal Site to choose in complement to.
     * @return random choice of rectum, urethra or pharynx
     */
    protected Site chooseNotRectumSite(Site site) 
    {
        int index ;
        if (URETHRA.equals(site.toString()))
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
     * @param rectum (Rectum)
     */
    protected void setRectum(Rectum rectum)
    {
        this.rectum = rectum ;
    }
    
    /**
     * Getter of Site urethra.
     * @return 
     */
    public Urethra getUrethra()
    {
        return urethra ;		
    }

    /**
     * Setter of Site urethra.
     * @param urethra (Urethra)
     */
    protected void setUrethra(Urethra urethra)
    {
        this.urethra = urethra ;
    }
    
    public Pharynx getPharynx()
    {
        return pharynx ;
    }

    /**
     * Setter for Pharynx.
     * @param pharynx (Pharynx) 
     */
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
     * Also ensures that prepStatus and undetectableStatus do not have
     * inappropriate values.
     * @param status (boolean)
     */
    public void setStatusHIV(boolean status)
    {
        statusHIV = status ;
        if (status)
            setPrepStatus(false) ;
        else
            setUndetectableStatus(false) ;
    }

    /**
     * Setter for riskyStatus.
     * @param risky (boolean) new value for riskyStatus.
     */
    @Override
    public void setRiskyStatus(boolean risky)
    {
        riskyStatus = risky ;
    }
    
    /**
     * Setter for riskyStatusCasual.
     * @param risky (boolean) new value for riskyStatusCasual.
     */
    @Override
    public void setRiskyStatusCasual(boolean risky)
    {
        riskyStatusCasual = risky ;
    }
    
    /**
     * Setter for riskyStatusRegular.
     * @param risky (boolean) new value for riskyStatusRegular.
     */
    @Override
    public void setRiskyStatusRegular(boolean risky)
    {
        riskyStatusRegular = risky ;
    }
    
    /**
     * Getter of seroSort variables, specific to Class of Relationship.
     * @param relationshipClazzName (String) Name of relationship subclass for 
     * which serosorting status is sought.
     * @return (boolean) Whether MSM uses serosorting for risk-reduction in relationships
     * of type relationshipClazzName.
     */
    public boolean getSeroSort(String relationshipClazzName)    //, Boolean status)
    {
        Boolean serosort = false ;
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
     * Getter of seroSortRegular
     * @return seroSortRegular (boolean) Whether MSM uses serosorting for risk-reduction in 
     * regular relationships.
     */
    public boolean getSeroSortRegular()
    {
        return seroSortRegular ;
    }

    /**
     * Getter of seroSortCasual
     * @return seroSortCasual (boolean) Whether MSM uses serosorting for risk-reduction in 
     * casual relationships.
     */
    public boolean getSeroSortCasual()
    {
        return seroSortCasual ;
    }

    /**
     * Getter of seroSortMonogomous
     * @return seroSortMonogomous (boolean) Whether MSM uses serosorting for risk-reduction in 
     * monogomous relationships.
     */
    public boolean getSeroSortMonogomous()
    {
        return seroSortMonogomous ;
    }

    /**
     * Setter of seroSort, used for unit testing
     * @param sort (boolean) Whether MSM uses serosorting for risk-reduction.
     */
    public void setSeroSort(boolean sort)
    {
        seroSort = sort ;
    }

    /**
     * Setter of seroSortRegular
     * @param sort (boolean) Whether MSM uses serosorting for risk-reduction in 
     * regular relationships.
     */
    public void setSeroSortRegular(boolean sort)
    {
        seroSortRegular = sort ;
    }

    /**
     * Setter of seroSortCasual
     * @param sort (boolean) Whether MSM uses serosorting for risk-reduction in 
     * casual relationships.
     */
    public void setSeroSortCasual(boolean sort)
    {
        seroSortCasual = sort ;
    }

    /**
     * Setter of seroSort
     * @param sort (boolean) Whether MSM uses serosorting for risk-reduction in 
     * monogomous relationships.
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
     * Setter of seroPosition. 
     * Changes discloseStatus to true if (position == true)
     * @param position (boolean) Whether MSM uses seropositioning for risk-reduction.
     */
    public void setSeroPosition(boolean position)
    {
        seroPosition = position ;
    }
    
    /**
     * Getter of consentCasualProbability.
     * @return (double) The probability of consenting to a Casual Relationship.
     */
    public double getConsentCasualProbability()
    {
        return consentCasualProbability ;
    }
    
    /**
     * Setter of consentCasualProbability.
     * @param casualProbability (double)
     */
    public void setConsentCasualProbability(double casualProbability)
    {
        consentCasualProbability = casualProbability ;
    }

    /**
     * Rescales consentCasualProbability according to scale
     * @param scale (double)
     */
    private void rescaleConsentCasualProbability(double scale)
    {
        consentCasualProbability *= scale ;
    }
    
    /**
     * Getter for undetectableStatus.
     * @return (boolean) undetectableStatus
     */
    public boolean getUndetectableStatus()
    {
        return undetectableStatus ;
    }
    
    /**
     * Setter of undetectableStatus. 
     * Will only set it to true if statusHIV is true.
     * @param status (boolean)
     */
    public void setUndetectableStatus(boolean status)
    {
        undetectableStatus = status && statusHIV ;
    }

    /**
     * Getter for trustUndetectable.
     * @return (boolean) trustUndetectable.
     */
    public boolean getTrustUndetectable()
    {
        return trustUndetectable ;
    }
    
    /**
     * Setter of trustUndetectable.
     * @param status (boolean)
     */
    public void setTrustUndetectable(boolean status)
    {
        trustUndetectable = status ;
    }

    /**
     * Getter for trustPrep.
     * @return (boolean) trustPrep.
     */
    public boolean getTrustPrep()
    {
        return trustPrep ;
    }
    
    /**
     * Setter of trustPrep.
     * @param status (boolean)
     */
    public void setTrustPrep(boolean status)
    {
        trustPrep = status ;
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
     * Setter of discloseStatusHIV. 
     * @param disclose (boolean) 
     */
    public void setDiscloseStatusHIV(boolean disclose)
    {
        discloseStatusHIV = disclose ;
    }

    /**
     * Getter of PrEP status
     * @return 
     */
    public boolean getPrepStatus()
    {
        return prepStatus ;
    }
    
    /**
     * Getter for riskyStatus.
     * @return 
     */
    @Override
    public boolean getRiskyStatus()
    {
        return riskyStatus ;
    }
    
    /**
     * Getter for riskyStatusCasual.
     * @return 
     */
    @Override
    public boolean getRiskyStatusCasual()
    {
        return riskyStatusCasual ;
    }
    
    /**
     * Getter for riskyStatusRegular.
     * @return 
     */
    @Override
    public boolean getRiskyStatusRegular()
    {
        return riskyStatusRegular ;
    }
    
    /**
     * Initialise prepStatus and set up screenCycle and screenTime accordingly.
     * @param prep 
     */
    private void initPrepStatus(double riskyProbability)
    {
        boolean prep = false ;
        //if (riskyStatus && (!statusHIV))
        {
          //  double prepProbability = getProbabilityPrep() / riskyProbability ;
            //prep = RAND.nextDouble() < prepProbability ;
        }
        setPrepStatus(prep) ;
        initScreenCycle(1.0) ; // (382.0/333.0) ;    // Rescale for 2010
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
            setScreenCycle((sampleGamma(31,1,1)) + 61) ;
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
     * @param rescale
     */
    @Override
    protected void reInitScreenCycle(double rescale)
    {
        if (getPrepStatus())
            setScreenCycle((sampleGamma(31,1,1)) + 61) ;
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
        setScreenTime(getScreenCycle()) ;
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
     * Allow for re-initialisation of prepStatus during simulation while 
     * initPrepStatus() remains private. 
     * @param year
     * @param riskyProbability
     */
    public boolean reinitPrepStatus(int year, double riskyProbability)
    {
        double[] prepProbabilityArray = new double[] {0.0,0.0,0.0,0.0,0.0,0.0,
            0.011,0.014,0.014,0.039,0.139,0.204,0.204} ;
        if (year >= prepProbabilityArray.length)
            year = prepProbabilityArray.length - 1 ;
        
        boolean prep = prepStatus ;
        if (riskyStatus && (!statusHIV))
        {
            double prepProbability = prepProbabilityArray[year] / riskyProbability ;
            setPrepStatus(RAND.nextDouble() < prepProbability) ;
        }
        
        return (prep != prepStatus) ;
    }
    
    /**
     * Setter of prepStatus.
     * screenCycle is initiated here because it is prepStatus dependent.
     * @param prep 
     */
    public void setPrepStatus(boolean prep)
    {
        prepStatus = prep && (!statusHIV) ;
    }
    
    /**
     * Whether to enter a proposed relationship of class relationshipClazz .
     * Currently according to whether in a Monogomous Relationship and 
     * the number of relationships already entered compared to concurrency.
     * 
     * @param relationshipClazzName - name relationship subclass
     * @param partner - agent for sharing proposed relationship
     * @return true if accept and false otherwise
     */
    @Override
    public boolean consent(String relationshipClazzName, Agent partner)
    {
        if (getSeroSort(relationshipClazzName))
            if (statusHIV != ((MSM) partner).statusHIV)
                return false ;
            //if (!String.valueOf(getStatusHIV()).equals(declareStatus()))
              //  return false ;
        return super.consent(relationshipClazzName, partner) ;
    }
    
    /**
     * FIXME: Find justifiable values for regularOdds and monogomousOdds
     * @param relationshipClazzName
     * @return (double) probability of MSM seeking out Relationship of class
     * relationshipClazzName.
     */
    @Override
    protected double seekRelationshipProbability(String relationshipClazzName)
    {
        if (CASUAL.equals(relationshipClazzName))
            return consentCasualProbability ;
        else if (REGULAR.equals(relationshipClazzName))
            return getRegularOdds() ;
        //else if (MONOGOMOUS.equals(relationshipClazzName))
        return getMonogomousOdds() ;
    }
    
    /**
     * Method to decide if MSM consents to a proposed Casual Relationship.
     * Affected by useGFN in such a way that MSM are more likely to accept if 
     * both partners use a GFN, with more active MSM more strongly affected than 
     * less active MSM.
     * @param partner
     * @return (boolean) whether to enter proposed Casual Relationship.
     */
    @Override
    protected boolean consentCasual(Agent partner)
    {
        double consentProbability = RAND.nextDouble() ;
        
        return (consentProbability < consentCasualProbability) ;
    }
    
    /**
     * How would the MSM respond if asked to disclose their statusHIV
     * @return String representation of statusHIV if (discloseStatusHIV), otherwise 'none'.
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
        return PROPORTION_ANTIVIRAL ;
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
     * Risky MSM are more likely to use strategies other than condoms.
     * @param relationshipClazzName
     * @param agentPartner
     * @return true if condom is to be used, false otherwise
     */
    @Override
    protected boolean chooseCondom(String relationshipClazzName, Agent agentPartner) 
    {
        //boolean testCondom = false ;
        //if (testCondom)
          //  return (RAND.nextDouble() < probabilityUseCondom ) ;
        
        boolean localRiskyStatus ; // = false ;
        double localProbabilityUseCondom ;
        if ("Casual".equals(relationshipClazzName))
        {
            localRiskyStatus = riskyStatusCasual ;
            localProbabilityUseCondom = probabilityUseCondomCasual ;
        }
        else
        {
            localRiskyStatus = riskyStatusRegular ;
            localProbabilityUseCondom = probabilityUseCondomRegular ;
        }
        //localProbabilityUseCondom *= 3 ;
        MSM partner = (MSM) agentPartner ;
        if (localRiskyStatus)
        {
            //Boolean partnerSeroPosition = partner.getSeroPosition() ;

            // Not if on PrEP or using U=U
            if (prepStatus)
                return false ;
            
            if (undetectableStatus && trustUndetectable)
                return false ;
            
            //if (useGSN && partner.useGSN) // && partner.riskyStatus))
                //return false ;

            if (partner.discloseStatusHIV || discloseStatusHIV)
            {
                if (statusHIV == partner.statusHIV)
                        return false ;
                if (partner.prepStatus && trustPrep)
                    return false ;
                    
                if (partner.undetectableStatus && trustUndetectable)
                    return false ;

                if (seroPosition && partner.seroPosition)
                    return false; // (RAND.nextDouble() < probabilityUseCondom ) ;
            }
            return (RAND.nextDouble() < localProbabilityUseCondom ) ;
        }
        else    // if not risky
        {
            //if (2 > 0)
              //  return true ;
            if (prepStatus)
                return (RAND.nextDouble() < localProbabilityUseCondom ) ;
            
            if (partner.discloseStatusHIV || discloseStatusHIV)
            {
                if (statusHIV == partner.statusHIV) 
                    return (RAND.nextDouble() < localProbabilityUseCondom ) ;
                else if (partner.statusHIV)
                {
                    if ((!partner.undetectableStatus) || (!trustUndetectable))
                        return true ;
                }
                else if (undetectableStatus && trustUndetectable)
                    return (RAND.nextDouble() < localProbabilityUseCondom ) ;  
                else if ((!partner.prepStatus) || (!trustPrep))    // partner HIV negative
                    return true ;
            }
            else
                return true ;
            
            return (RAND.nextDouble() < localProbabilityUseCondom ) ;  //TODO: Should there be subset who always use?
        }
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
        riskyStatusCasual = riskyStatus ;
        if (riskyStatusCasual)
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
