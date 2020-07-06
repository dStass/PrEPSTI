/**
 * 
 */
package PRSP.PrEPSTI.agent;

//import static agent.Agent.LOGGER;
import PRSP.PrEPSTI.community.Relationship;
import PRSP.PrEPSTI.configloader.ConfigLoader;
import PRSP.PrEPSTI.reporter.Reporter ;

import java.util.logging.Level;
import PRSP.PrEPSTI.site.* ;
        
import java.lang.reflect.*;
import java.util.ArrayList ;
import java.util.Arrays;
import java.util.HashMap ;
import java.util.Collection ;
import java.util.Collections;
import java.util.stream.IntStream;
import org.apache.commons.math3.distribution.* ;
import PRSP.PrEPSTI.reporter.PopulationReporter;
import static PRSP.PrEPSTI.reporter.Reporter.AGENTID;
import PRSP.PrEPSTI.reporter.ScreeningReporter;
        
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
    // The proportion of HIV-positives whose viral load is undetectable, given positive HIV status, see REINIT_PROPORTION_UNDETECTABLE
    //static double[] PROPORTION_UNDETECTABLE = {0.566, 0.647, 0.701, 0.723, 0.747, 0.816, 0.734, 0.808,0.856, 0.847, 0.918} ;
    
    /**
     * Coordinates the reinitialisation of Agent parameters when they change 
     * year-by-year.
     * @param agentList
     * @param year
     * @return 
     */
    static public String REINIT(ArrayList<Agent> agentList, int year) 
    {
    	StringBuilder sbReport = new StringBuilder() ; // "" ;
        //boolean successful = true ;
        String change = "change" ;
        String methodName = "" ;
        try
        {
            methodName = "undetectable" ;
            sbReport.append(Reporter.ADD_REPORT_PROPERTY(change, methodName)) ;
            sbReport.append(REINIT_PROPORTION_UNDETECTABLE(agentList, year)) ;
            
            methodName = "disclosure" ;
            REINIT_PROBABILITY_DISCLOSURE_HIV(agentList, year) ;
            
            methodName = "riskinessCasual" ;
            sbReport.append(Reporter.ADD_REPORT_PROPERTY(change, methodName)) ;
            sbReport.append(REINIT_RISK_CASUAL(agentList, year)) ;
            
            methodName = "riskinessRegular" ;
            sbReport.append(Reporter.ADD_REPORT_PROPERTY(change, methodName)) ;
            sbReport.append(REINIT_RISK_REGULAR(agentList, year)) ;
            
            methodName = "trust_undetectable" ;
            sbReport.append(Reporter.ADD_REPORT_PROPERTY(change, methodName)) ;
            sbReport.append(REINIT_TRUST_UNDETECTABLE(agentList, year)) ;
            
            methodName = "trust_prep" ;
            sbReport.append(Reporter.ADD_REPORT_PROPERTY(change, methodName)) ;
            sbReport.append(REINIT_TRUST_PREP(agentList, year)) ;
            
            // Needs to be called after MSM.REINIT() specifically MSM.REINIT_RISK_ODDS()
            // due to its updating prepStatus.
            //methodName = "screen" ;
            //report += Reporter.ADD_REPORT_PROPERTY(change, methodName) ;
            //report += REINIT_SCREEN_CYCLE(agentList, year) ;
            //                                                             
            //REINIT_USE_GSN(agentList, year) ;
        }
        catch ( Exception e )
        {
            LOGGER.severe(e.toString() + " in method " + methodName) ;
            //return false ;
        }
        return sbReport.toString() ;
    }
    
    /** The proportion of HIV-positives whose viral load is undetectable, given positive HIV status */
    static double[] PROPORTION_UNDETECTABLE = {0.566, 0.647, 0.701, 0.723, 0.747, 0.816, 0.734, 0.808,    // 2007-2014 
            0.856, 0.847, 0.918} ;    // 2015-2018
    
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
    	StringBuilder sbReport = new StringBuilder() ; // "" ;
        if (year == 0)
            return sbReport.toString() ;
        
        // years 2007 onwards
        //double[] probabilityUndetectable = new double[] {0.566, 0.647, 0.701, 0.723, 0.747, 0.816, 0.734, 0.808, 
          //  0.856, 0.847, 0.918, 0.918, 0.918} ;
        // years 2007-2009
        // 0.532, 0.706, 0.735, 
        
        //if (year >= PROPORTION_UNDETECTABLE.length)
          //  year = PROPORTION_UNDETECTABLE.length - 1 ;
        
        double newProbability = GET_YEAR(PROPORTION_UNDETECTABLE,year) ;
        double oldProbability = GET_YEAR(PROPORTION_UNDETECTABLE,year-1) ;
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
                    sbReport.append(Reporter.ADD_REPORT_PROPERTY(String.valueOf(msm.getAgentId()), newStatus)) ;
                    continue ;
                }
            }
            else    // Probability of being on antiViral medication decreases.
            {
                if (msm.undetectableStatus)
                {
                    changeProbability = newProbability/oldProbability ; // (oldProbability - newProbability)/oldProbability ;
                    // equivalent to correct calculation: RAND > (1 - changeProbability)
                    newStatus = RAND.nextDouble() < changeProbability ;
                    msm.setUndetectableStatus(newStatus) ;
                    sbReport.append(Reporter.ADD_REPORT_PROPERTY(String.valueOf(msm.getAgentId()), newStatus)) ;
                    continue ;
                }
            }
            //newProbability *= changeProbability ;
        }

        return sbReport.toString() ;
    }
    
 // Start from year 2012
    static double[] POSITIVE_TRUST_UNDETECTABLE= new double[] {0.0,0.483,0.772,    // 2012 to 2014
        0.695, 0.720, 0.757, 0.830, 0.727                                   // 2015 to 2019
    //,0.830, 0.865, 0.900, 0.935, 0.970, 1.0                               // 2020 to 2025
    } ;
    static double[] NEGATIVE_TRUST_UNDETECTABLE = new double[] {0.0,0.106,0.094,   // 2012 to 2014
        0.129, 0.154, 0.203, 0.231, 0.194                                   // 2015 to 2019
        //,0.231, 0.265, 0.300, 0.335, 0.370, 0.405, 0.440                  // 2020 to 2025
        } ;
    
    
    /**
     * Alters the willingness to trust U=U as protection against HIV on a year-by-year
     * basis.
     * Data taken from GCPS: Table 21 in GCPS 2017, Table 19 in 2019
     * Where they conflict the later report is taken.
     * @param agentList (ArrayList) List of Agents to undergo parameter change.
     * @param year (int) Year of simulation starting from year zero.
     */
    static protected String REINIT_TRUST_UNDETECTABLE(ArrayList<Agent> agentList, int year) 
    {
    	StringBuilder sbReport = new StringBuilder() ; // "" ;
        
        // Trust is zero until 2013, which is year 6.
        if (year < 6)
            return sbReport.toString() ;
        
        //int undetectableYear = year ;
        //if (undetectableYear >= PROPORTION_UNDETECTABLE.length)
          //  undetectableYear = PROPORTION_UNDETECTABLE.length - 1 ;
        double lastUndetectable = GET_YEAR(PROPORTION_UNDETECTABLE,year - 1) ; 
        double undetectableProportion = GET_YEAR(PROPORTION_UNDETECTABLE,year) ;
        
        //int discloseYear = year ;
        //if (discloseYear >= NEGATIVE_DISCLOSE_PROBABILITY.length)
          //      discloseYear = NEGATIVE_DISCLOSE_PROBABILITY.length - 1 ;
        double negativeDiscloseProportion = GET_YEAR(NEGATIVE_DISCLOSE_PROBABILITY,year) ;
        double lastDiscloseProportion = GET_YEAR(NEGATIVE_DISCLOSE_PROBABILITY,year - 1) ;


        year -= 5 ;    // Start from year 2012
        
        //int positiveYear = year ;
        //int negativeYear = year ;
        //if (positiveYear >= positiveTrustUndetectable.length)
          //  positiveYear = positiveTrustUndetectable.length - 1 ;
        //if (negativeYear >= negativeTrustUndetectable.length)
          //  negativeYear = negativeTrustUndetectable.length - 1 ;

        double positiveLastProbability = GET_YEAR(POSITIVE_TRUST_UNDETECTABLE,year - 1) ;
        double positiveTrustProbability = GET_YEAR(POSITIVE_TRUST_UNDETECTABLE,year) ;
        double negativeLastProbability = GET_YEAR(NEGATIVE_TRUST_UNDETECTABLE,year - 1) ;
        double negativeTrustProbability = GET_YEAR(NEGATIVE_TRUST_UNDETECTABLE,year) ;
        
        double changeProbability ; 
        
        for (Agent agent : agentList)
        {
            MSM msm = (MSM) agent ;
            
            double lastProbability ;
            double trustProbability ; 
            
            if (msm.statusHIV)
            {
                if (!msm.undetectableStatus)    // Only the undetectable trust being undetectable
                	continue ;

                lastProbability = positiveLastProbability/lastUndetectable ;
                trustProbability = positiveTrustProbability/undetectableProportion ;
            }
            else    // msm HIV negative
            {
                if (!msm.discloseStatusHIV)  // Only those who disclose can know their partner's undetectableStatus
                    continue ;
                lastProbability = negativeLastProbability/lastDiscloseProportion  ;
                trustProbability = negativeTrustProbability/negativeDiscloseProportion  ;
            }
            
            if (trustProbability > lastProbability)
            {
                if (msm.trustUndetectable)
                    continue ;
                changeProbability = (trustProbability - lastProbability)/(1 - lastProbability) ;
                msm.setTrustUndetectable(RAND.nextDouble() < changeProbability);
                if (msm.trustUndetectable)    // if trustPrep has changed
                    sbReport.append(Reporter.ADD_REPORT_PROPERTY(String.valueOf(msm.getAgentId()), TRUE)) ;
            }
            else
            {
                if (!msm.trustUndetectable)
                    continue ;
                changeProbability = trustProbability/lastProbability ; // (lastProbability - trustProbability)/lastProbability ;
                // equivalent to correct calculation: RAND > (1 - changeProbability)
                msm.setTrustUndetectable(RAND.nextDouble() < changeProbability) ; 
                if (!msm.trustUndetectable)    // if trustPrep has changed
                    sbReport.append(Reporter.ADD_REPORT_PROPERTY(String.valueOf(msm.getAgentId()), FALSE)) ;
            }
        }
        return sbReport.toString() ;
    }
    
    /** Proportion of HIV-positives willing to trust that PrEP will protect their HIV-negative partner */
    static double[] POSITIVE_TRUST_PREP = new double[] {0.0, 0.336, 0.467, 0.360} ;    // 2016 to 2019
    
    // Currently redundant as currently not modelling HIV status unknown or distrust
    /** Proportion of HIV-negatives willing to trust that PrEP ensures that their partner is HIV-negative */
    static double[] NEGATIVE_TRUST_PREP = new double[] {0.0, 0.346, 0.491, 0.532} ;    // 2016 to 2019
    
    /**
     * Alters the willingness to trust partner's PrEP to protect them from HIV on a year-by-year
     * basis.
     * Data taken from GCPS: Table 19 in GCPS 2019
     * @param agentList (ArrayList) List of Agents to undergo parameter change.
     * @param year (int) Year of simulation starting from year zero.
     */
    static protected String REINIT_TRUST_PREP(ArrayList<Agent> agentList, int year) 
    {
    	StringBuilder sbReport = new StringBuilder() ; //  "" ;
        
        // Trust is zero until 2016 inclusive, which is year 9.
        if (year < 10)
            return sbReport.toString() ;
        year -= 9 ;
        
        
        //if (year >= positiveTrustPrep.length)
          //  year = positiveTrustPrep.length - 1 ;
        
        double positiveLastProbability = GET_YEAR(POSITIVE_TRUST_PREP,year - 1) ;
        double positiveTrustProbability = GET_YEAR(POSITIVE_TRUST_PREP,year) ;
        double negativeLastProbability = GET_YEAR(NEGATIVE_TRUST_PREP,year - 1) ;
        double negativeTrustProbability = GET_YEAR(NEGATIVE_TRUST_PREP ,year) ;
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
                if (msm.trustPrep)
                    continue ;
                changeProbability = (trustProbability - lastProbability)/(1 - lastProbability) ;
                msm.setTrustPrep(RAND.nextDouble() < changeProbability);
                if (msm.trustPrep)    // if trustPrep has changed
                    sbReport.append(Reporter.ADD_REPORT_PROPERTY(String.valueOf(msm.getAgentId()), TRUE)) ;
            }
            else
            {
                if (!msm.trustPrep)
                    continue ;
                changeProbability = trustProbability/lastProbability ; // (lastProbability - trustProbability)/lastProbability ;
                // equivalent to correct calculation: RAND > (1 - changeProbability)
                msm.setTrustPrep(RAND.nextDouble() < changeProbability) ; 
                if (!msm.trustPrep)    // if trustPrep has changed
                    sbReport.append(Reporter.ADD_REPORT_PROPERTY(String.valueOf(msm.getAgentId()), FALSE)) ;
            }
        }
        return sbReport.toString() ;
    }
    
    static double[] POSITIVE_DISCLOSE_PROBABILITY = new double[] {0.201,0.296,0.327,0.286,0.312,0.384,0.349,0.398,
            0.430,0.395,0.461,0.461,0.461    // to 2019 
            //,0.496,0.531,0.566,0.601,0.636,0.671    // from 2020 to 2025 
            } ;
    static double[] NEGATIVE_DISCLOSE_PROBABILITY = new double[] {0.175,0.205,0.218,0.239,0.229,0.249,0.236,0.295,
            0.286,0.352,0.391,0.391,0.391
            //,0.426,0.461,0.496,0.531,0.566,0.601    // from 2020 to 2025
            } ;
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
    	StringBuilder sbReport = new StringBuilder() ; // "" ;
        // Go from 2007
        double newDiscloseProbability ;
        double oldDiscloseProbability ;
        double changeProbability ;
        
        double negativeNewDiscloseProbability = GET_YEAR(NEGATIVE_DISCLOSE_PROBABILITY,year) ;
        double negativeOldDiscloseProbability = GET_YEAR(NEGATIVE_DISCLOSE_PROBABILITY,year-1) ;

        double positiveNewDiscloseProbability = GET_YEAR(POSITIVE_DISCLOSE_PROBABILITY,year) ;
        double positiveOldDiscloseProbability = GET_YEAR(POSITIVE_DISCLOSE_PROBABILITY,year-1) ;
        
        
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
                changeProbability = newDiscloseProbability/oldDiscloseProbability ; // (oldDiscloseProbability - newDiscloseProbability)/oldDiscloseProbability ;
                // equivalent to correct calculation: RAND > (1 - changeProbability)
                msm.setDiscloseStatusHIV(RAND.nextDouble() < changeProbability);
                //msm.initSeroStatus(changeProbability) ;
            }
            
        }
        return sbReport.toString() ;
    }
    
    static double[] NEW_RISKY_CASUAL = new double[] {290,293,369,345,331,340,364,350,362,409,520,566,566} ;
    static double[] NEW_SAFE_CASUAL = new double[] {468,514,471,501,469,465,444,473,440,424,307,264,264        
    		//    ,259,254,249,244,239,234    // decreasing Safe 2020 to 2025
            //,269,274,279,284,289,294    // increasing Safe 2020 to 2025
        } ;
    
    /**
     * Resets the probability of Risky vs Safe behaviour within Casual Relationships 
     * according to the year.
     * Rates taken from GCPS 2011 Table 16, 2014 Table 15, 
     * @param agentList (ArrayList) List of Agents to undergo parameter change.
     * @param year (int) Year of simulation, starting from zero.
     * @return (String) report of changes made to MSM.probabilityUseCondom .
     * @throws java.lang.Exception (Exception)
     */
    static protected String REINIT_RISK_CASUAL(ArrayList<Agent> agentList, int year) throws Exception
    {
    	StringBuilder sbReport = new StringBuilder() ; // "" ;
        if (year == 0)
            return sbReport.toString() ;
        // GCPS (Table 15, 2011) (Table 14, 2013) (Table 16, 2017-18)
        // Year-by-year rates of UAIC 
        double[] newRiskyCasual = new double[] {290,293,369,345,331,340,364,350,362,409,520,566,566} ;
        double[] newSafeCasual = new double[] {468,514,471,501,469,465,444,473,440,424,307,264,264        
        		//    ,259,254,249,244,239,234    // decreasing Safe 2020 to 2025
                //,269,274,279,284,289,294    // increasing Safe 2020 to 2025
            } ;

        
        //if (year >= newRiskyCasual.length)
          //  year = newRiskyCasual.length - 1 ;
        // newRiskyProportions         {.38,.36,.44,.41,.41,.42,.45,.43,.45,.49,.63} ;
        // total_odds                 {758,807,840,846,800,805,808,823,802,831,827}
        SAFE_ODDS_CASUAL = GET_YEAR(newSafeCasual,year) ;
        RISKY_ODDS_CASUAL = GET_YEAR(newRiskyCasual,year) ;
        
        double totalOdds = SAFE_ODDS_CASUAL + RISKY_ODDS_CASUAL ;
        
        double lastRisky = GET_YEAR(newRiskyCasual,year-1) ;
        double lastSafe = GET_YEAR(newSafeCasual,year-1) ;
        double lastTotal = lastSafe + lastRisky ;
        double riskyProbability = (RISKY_ODDS_CASUAL)/totalOdds ;
        double safeProbability = (SAFE_ODDS_CASUAL)/totalOdds ;
        double lastProbabilityRisk = (lastRisky)/lastTotal ;
        double lastProbabilitySafe = (lastSafe)/lastTotal ;
        double changeProbability ;
        
        boolean moreRisky = (lastProbabilityRisk < riskyProbability) ;
        double adjustProbabilityUseCondom = safeProbability/lastProbabilitySafe ; // SAFE_ODDS/newSafeOdds[year-1] ;
        double screeningRatio = Agent.TEST_RATES[0]/GET_YEAR(Agent.TEST_RATES,year - 1) ;
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
                    msm.reInitScreenCycle(1.0,false) ;
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
                    msm.reInitScreenCycle(screeningRatio,false) ;
                }
            }
            sbReport.append(Reporter.ADD_REPORT_PROPERTY(String.valueOf(msm.getAgentId()), record.toString())) ;
            record.clear() ;
        }
        return sbReport.toString() ;
    }
    
    static double[] NEW_RISKY_REGULAR = new double[] {568,540,538,604,493,513,503,520,576,557,618,650,685} ;
    static double[] NEW_SAFE_REGULAR = new double[] {300,300,300,296,279,247,257,248,239,203,157,131,108
            // ,103,98,93,88,83,78    // decreasing Safe 2020 to 2025
            //,113,118,123,128,133,138    // increasing Safe 2020 to 2025
        } ;

    
    /**
     * Resets the probability of Risky vs Safe behaviour within Regular Relationships 
     * according to the year.
     * Rates taken from GCPS 2011 Table 16, 2014 Table 15, 
     * @param agentList (ArrayList) List of Agents to undergo parameter change.
     * @param year (int) Year of simulation, starting from zero.
     * @return (String) report of changes made to MSM.probabilityUseCondom .
     * @throws java.lang.Exception (Exception)
     */
    static protected String REINIT_RISK_REGULAR(ArrayList<Agent> agentList, int year) throws Exception
    {
        StringBuilder sbReport = new StringBuilder() ; // "" ;
        if (year == 0)
            return sbReport.toString() ;
        // GCPS (Table 15, 2011) (Table 14, 2013) (Table 16, 2017-18)
        // Year-by-year rates of UAIC     // 538?
        double[] newRiskyRegular = new double[] {568,540,538,604,493,513,503,520,576,557,618,650,685} ;
        double[] newSafeRegular = new double[] {300,300,300,296,279,247,257,248,239,203,157,131,108
                // ,103,98,93,88,83,78    // decreasing Safe 2020 to 2025
                //,113,118,123,128,133,138    // increasing Safe 2020 to 2025
            } ;

        
        //if (year >= newRiskyRegular.length)
          //  year = newRiskyRegular.length - 1 ;
        // newRiskyProportions         {.38,.36,.44,.41,.41,.42,.45,.43,.45,.49,.63} ;
        // total_odds                 {758,807,840,846,800,805,808,823,802,831,827}
        SAFE_ODDS_REGULAR = GET_YEAR(newSafeRegular,year) ;
        RISKY_ODDS_REGULAR = GET_YEAR(newRiskyRegular,year) ;
        
        double totalOdds = SAFE_ODDS_REGULAR + RISKY_ODDS_REGULAR ;
        double lastRisky = GET_YEAR(newRiskyRegular,year-1) ;
        double lastSafe = GET_YEAR(newSafeRegular,year-1) ;
        double lastTotal = lastSafe + lastRisky ;
        double riskyProbability = RISKY_ODDS_REGULAR/totalOdds ;
        double safeProbability = SAFE_ODDS_REGULAR/totalOdds ;
        double lastProbabilityRisk = lastRisky/lastTotal ;
        double lastProbabilitySafe = lastSafe/lastTotal ;
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
            sbReport.append(Reporter.ADD_REPORT_PROPERTY(String.valueOf(msm.getAgentId()), record.toString())) ;
            record.clear() ;
        }
        return sbReport.toString() ;
    }
    
    /**
     * Resets the probability of MSM using a GSN year-by-year.
     * Values taken from GCPS "Where men met their male sex partners in the six months
     * prior to the survey. (Table 20 2013, Table 10 2018)
     * @param agentList (ArrayList) List of Agents to undergo parameter change.
     * @param year (int) Year of simulation, starting from zero.
     */
//    static protected void REINIT_USE_GSN(ArrayList<Agent> agentList, int year)
//    {
//        if (year == 0)
//            return ;
//        double[] gsnArray = new double[] {0.0,0.0,0.0,0.0, 22.9, 31.5, 36.1, 41.9, 46.0, 49.5, 48.8, 50.8} ;
//        
//        double probabilityGSN = gsnArray[year] ;
//        if (probabilityGSN == 0.0)
//            return ;
//        double lastProbabilityGSN = gsnArray[year - 1] ;
//        
//        boolean moreGSN = ( lastProbabilityGSN < probabilityGSN ) ;
//        if (lastProbabilityGSN == 0.0)
//            for (Agent agent : agentList)
//                agent.setUseGSN(RAND.nextDouble() < probabilityGSN) ;
//        else
//        {
//            double changeProbability ;
//
//            for (Agent agent : agentList)
//            {
//                // Compensates for allowing only change in one direction.
//                if (moreGSN) 
//                {
//                    changeProbability = (probabilityGSN - lastProbabilityGSN)/(1-lastProbabilityGSN) ;
//                }
//                else
//                    changeProbability = probabilityGSN/lastProbabilityGSN ;
//
//
//                if (moreGSN) 
//                {
//                    if (agent.useGSN) // if using GSN already
//                        continue ;    // we don't change it
//                    agent.setUseGSN(RAND.nextDouble() < changeProbability) ;
//                }
//                else    // riskyProbability has gone down
//                {
//                    if (!agent.useGSN)// if not using GSN already
//                        continue ;    // we don't change it
//                    // equivalent to correct calculation: RAND > (1 - changeProbability)
//                    agent.setUseGSN(RAND.nextDouble() < changeProbability) ; 
//                }
//            }
//        }
//    }
    
    /**
     * Resets the probability of consenting to a Casual relationship according to 
     * the year and the HIV status of each MSM.
     * Rates taken from GCPS 2011 Table 16, 2014 Table 15, 
     * @param agentList (ArrayList) List of Agents to undergo parameter change.
     * @param year (int) Year of simulation, starting from zero.
     */
    static protected String REINIT_CONSENT_CASUAL_PROBABILITY(ArrayList<Agent> agentList, int year)
    {
        StringBuilder sbReport = new StringBuilder() ; // "" ;
        double[] positiveProbabilities = new double[] {1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0} ;
        double[] negativeProbabilities = new double[] {1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0} ;
        
        double consentProbability = 1.0 ;
        for (Agent agent : agentList)
        {
            MSM msm = (MSM) agent ;
            if (msm.getStatusHIV())
                consentProbability = msm.consentCasualProbability * GET_YEAR(positiveProbabilities,year) ;
            else
                consentProbability = msm.consentCasualProbability * GET_YEAR(negativeProbabilities,year) ;
            msm.setConsentCasualProbability(consentProbability);
            // May change to msm.rescaleConsentCasualProbability(consentStatus[year]/consentStatus[year-1]) ;
        }
        return sbReport.toString() ;
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
        
        return 1.0 - GET_YEAR(proportionAbstain,year) ;
    }
    
    /**
     * Brings newly-born Agent parameters up-to-date
     * @return (boolean) true if successful, false otherwise
     */
    @Override
    public boolean update(int year)
    {
    	// DISCLOSE_PROBABILITY
        if (statusHIV)
        	setDiscloseStatusHIV(RAND.nextDouble() < GET_YEAR(POSITIVE_DISCLOSE_PROBABILITY,year)) ;
        else
        	setDiscloseStatusHIV(RAND.nextDouble() < GET_YEAR(NEGATIVE_DISCLOSE_PROBABILITY,year)) ;
                
                
    	// Undetectable status if HIV positive
    	double probabilityUndetectable = GET_YEAR(PROPORTION_UNDETECTABLE,year) ;
    	if (statusHIV)
                setUndetectableStatus(RAND.nextDouble() < probabilityUndetectable) ;
    	
    	// TRUST_UNDETECTABLE
    	if (year >= 6)
    	{
            int trustUndetectableYear = year - 5 ;
            
            double negativeDiscloseProportion = GET_YEAR(NEGATIVE_DISCLOSE_PROBABILITY,year) ;    // year and not trustUndetectableYear

            double trustProbability ; 
            
            if (statusHIV)
            {
                if (undetectableStatus)    // Only the undetectable trust being undetectable
                {
                  trustProbability = GET_YEAR(POSITIVE_TRUST_UNDETECTABLE,trustUndetectableYear)/probabilityUndetectable ;
                  setTrustUndetectable(RAND.nextDouble() < trustProbability);
                }
            }
            else    // msm HIV negative
            {
                if (discloseStatusHIV)  // Only those who disclose can know their partner's undetectableStatus
                {
                    trustProbability = GET_YEAR(NEGATIVE_TRUST_UNDETECTABLE,trustUndetectableYear)/negativeDiscloseProportion ;
                    setTrustUndetectable(RAND.nextDouble() < trustProbability);
                }
            }
            
            // RISKY_REGULAR
            double risky = GET_YEAR(NEW_RISKY_REGULAR,year) ;
            double safe = GET_YEAR(NEW_SAFE_REGULAR,year) ;
            double total = safe + risky ;
            double initRisky = GET_YEAR(NEW_RISKY_REGULAR,0) ;
            double initSafe = GET_YEAR(NEW_SAFE_REGULAR,0) ;
            double initTotal = initSafe + initRisky ;
            double riskyProbability = risky/total ;
            double safeProbability = safe/total ;
            
            double hivFactor = GET_HIV_RISKY_CORRELATION(statusHIV) ;
            if (RAND.nextDouble() < riskyProbability * hivFactor) 
                riskyStatusRegular = true ;
            
            scaleProbabilityUseCondomRegular((safe * initTotal)/(initSafe * total)) ;
            
            
            // RISKY_CASUAL
            risky = GET_YEAR(NEW_RISKY_CASUAL,year) ;
            safe = GET_YEAR(NEW_SAFE_CASUAL,year) ;
            total = safe + risky ;
            initRisky = GET_YEAR(NEW_RISKY_CASUAL,0) ;
            initSafe = GET_YEAR(NEW_SAFE_CASUAL,0) ;
            initTotal = initSafe + initRisky ;
            riskyProbability = risky/total ;
            safeProbability = safe/total ;
            
            hivFactor = GET_HIV_RISKY_CORRELATION(statusHIV) ;
            if (RAND.nextDouble() < riskyProbability * hivFactor) 
                riskyStatusCasual = true ;
            
            scaleProbabilityUseCondomCasual((safe * initTotal)/(initSafe * total)) ;
            
            if (riskyStatusCasual && (!statusHIV))
                reinitPrepStatus(year, riskyProbability * hivFactor) ;
        }

    	return super.update(year) ;
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
        float t1 = System.nanoTime();
        String report = "" ;
        StringBuilder sbReport = new StringBuilder();

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

                    Relationship relationship = Relationship.GET_RELATIONSHIP_FROM_CLASS_NAME(relationshipClazzName);
                    if (relationship == null) 
                    	LOGGER.severe(relationshipClazzName);
                    else 
                    	sbReport.append(relationship.addAgents(msm0, msm1));

                    /*try
                    {
                        relationshipClazz = Class.forName("PRSP.PrEPSTI.community." + relationshipClazzName) ;
                        Relationship relationship = (Relationship) relationshipClazz.newInstance() ;
                        // report += relationship.addAgents(msm0, msm1);
                        sbReport.append(relationship.addAgents(msm0, msm1));
                    }
                    catch( Exception e )
                    {
                        LOGGER.severe(e.toString()) ;
                    }
                    */
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
                        relationshipClazz = Class.forName("PRSP.PrEPSTI.community." + relationshipClazzName) ;
                        Relationship relationship = (Relationship) relationshipClazz.getDeclaredConstructor().newInstance() ;
                        //incrementNbRelationships() ;
                        // report += relationship.addAgents(msm0, msm1);
                        sbReport.append(relationship.addAgents(msm0, msm1));
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
        // System.out.println(report.length() + " time=" + String.valueOf((System.nanoTime()-t1) / 1000000000f));
        report = sbReport.toString();
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
    static double URETHRA_TO_RECTUM = 0.95 ; //  0.85 ; 
    /** Transmission probabilities sexual contact from Urethra to Pharynx. */
    static double URETHRA_TO_PHARYNX = 0.35 ; // 0.25 ; // 0.50 ; 
    /** Transmission probabilities sexual contact from Rectum to Urethra. */
    static double RECTUM_TO_URETHRA = 0.010 ; // 0.009 ; // 0.015 ; // 0.010 ;
    /** Transmission probabilities sexual contact from Rectum to Pharynx. */
    static double RECTUM_TO_PHARYNX = 0.025 ; // 0.023 ; 
    /** Transmission probabilities sexual contact in Pharynx to Urethra intercourse. */
    static double PHARYNX_TO_URETHRA = 0.004 ; // 0.005 ; // .005 ; // 0.010 ; 
    /** Transmission probabilities sexual contact in Pharynx to Rectum intercourse. */
    static double PHARYNX_TO_RECTUM = 0.025 ; // 0.020 ; 0.020 ; // 0.020 ; 
    /** Transmission probabilities sexual contact in Pharynx to Pharynx intercourse (kissing). */
    static double PHARYNX_TO_PHARYNX = 0.065 ; // 0.075 // 0.040 ;
    /** Transmission probabilities sexual contact in Urethra to Urethra intercourse (docking). */
    static double URETHRA_TO_URETHRA = 0.001 ; // 0.001 ; // 0.001 ; // 0.020 ; 
    /** Transmission probabilities sexual contact in Rectum to Rectum intercourse. */
    static double RECTUM_TO_RECTUM = 0.001 ; // 0.001 ; // 0.003 ; // 0.020 ;

    /** The probability of screening in a given cycle with statusHIV true. */
    static double SCREEN_PROBABILITY_HIV_POSITIVE = 0.0029 ;
    
    /** The probability of screening in a given cycle with statusHIV false 
     * when not on PrEP.
     */
    static double SCREEN_PROBABILITY_HIV_NEGATIVE = 0.0012 ;
    
    /** The number of cycles between screenings for MSM on PrEP. */
    //static int SCREENCYCLE = 92 ;

    
    /** The number of MSM invited to any given group-sex event. */
    static public int GROUP_SEX_EVENT_SIZE;

    /** The number of orgies in the community during a given cycle. */
    //int ORGY_NUMBER = 4 ;
    
    /** Probability of joining a group-sex event if invited. */
    static double JOIN_GSE_PROBABILITY = 6.35 * Math.pow(10,-4) ;


    // hashmap with key = method name, value = hashmap that contains
    // variable names and its literal value as a String
    // static public HashMap<String, HashMap> METHOD_CONFIG;
    
    /**
     * Allows changing of SITE_TO_SITE transmission probabilities.
     * @param index
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

    static double SAFE_ODDS_CASUAL = 468 ;
    // Odds of an MSM being riskyMSM
    static double RISKY_ODDS_CASUAL = 290 ;
    // Sum of safeOdds and riskyOdds
    static double TOTAL_ODDS_CASUAL = RISKY_ODDS_CASUAL + SAFE_ODDS_CASUAL ;
    
    static double SAFE_ODDS_REGULAR = 300 ;
    // Odds of an MSM being riskyMSM
    static double RISKY_ODDS_REGULAR = 568 ;
    // Sum of safeOdds and riskyOdds
    static double TOTAL_ODDS_REGULAR = RISKY_ODDS_REGULAR + SAFE_ODDS_REGULAR ;


    /** 
     * Describes correlation between statusHIV and riskyStatus.
     * Must be less than 1/PROPORTION_HIV OR initRiskiness() fails.
     */
    public static double HIV_RISKY_CORRELATION; // 2.0 ; // 1.0 ;	
    
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
    	initStatus(0) ;
    }
    
    /**
     * Initialises status' at construction of MSM. 
     * Ensures that those MSM who come out during simulation are initially
     * HIV free (statusHIV == false).
     */
    final void initStatus(int year)
    {
        //requireDiscloseStatusHIV = (rand.nextDouble() < probabilityRequireDiscloseHIV) ;
        statusHIV = (RAND.nextDouble() < getProportionHIV()) ;

        // Sets antiViral status, ensuring it is true only if statusHIV is true
        setUndetectableStatus(RAND.nextDouble() < getProportionUndetectable(year)) ;

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
     * Initialises riskyStatus and probabilityUseCondom, probabilityUseCondomCasual,
     * probabilityUseCondomRegular according to RISKY_ODDS, SAFE_ODDS
     * Risky or Safe behaviour correlated with HIV status
     * Keep overall risky behaviour the same
     */
    final void initRiskiness()
    {
        int totalOdds = SAFE_ODDS + RISKY_ODDS ;
        double riskyProbability = ((double) RISKY_ODDS)/totalOdds ;
        riskyProbability *= GET_HIV_RISKY_CORRELATION(statusHIV) ;
        
        probabilityUseCondom = RAND.nextDouble() ; // sampleGamma(4, 0.1, 1) ; // Gamma2 * (1 - riskyProbability) * RAND.nextDouble() ;
        
        double totalOddsCasual = SAFE_ODDS_CASUAL + RISKY_ODDS_CASUAL ;
        double riskyProbabilityCasual = (RISKY_ODDS_CASUAL)/totalOddsCasual ;
        riskyProbabilityCasual *= GET_HIV_RISKY_CORRELATION(statusHIV) ;
        
        probabilityUseCondomCasual = RAND.nextDouble() ; // sampleGamma(4, 0.1, 1) ; // Gamma2 * (1 - riskyProbability) * RAND.nextDouble() ;
        
        double totalOddsRegular = SAFE_ODDS_REGULAR + RISKY_ODDS_REGULAR ;
        double riskyProbabilityRegular = (RISKY_ODDS_REGULAR)/totalOddsRegular ;
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
        censusReport += Reporter.ADD_REPORT_PROPERTY("probabilityUseCondom", probabilityUseCondom) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("probabilityUseCondomCasual", probabilityUseCondomCasual) ;
        censusReport += Reporter.ADD_REPORT_PROPERTY("probabilityUseCondomRegular", probabilityUseCondomRegular) ;
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
     // String returnString = "seroSort" + relationshipClazzName ;
        switch(relationshipClazzName) {
            case "Casual":
                serosort = getSeroSortCasual();
                break;
            case "Regular":
                serosort = getSeroSortRegular();
                break;
            case "Monogomous":
                serosort = getSeroSortMonogomous();
                break;
            default:
                LOGGER.severe(relationshipClazzName);
                break;
        }

        // try
        // {
        //     serosort = MSM.class.getDeclaredField(returnString).getBoolean(this) ;
        // }
        // catch ( Exception e )
        // {
        //     LOGGER.log(Level.SEVERE, "{0} {1}", new Object[] {returnString, e.getClass().getSimpleName()});
        // }
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
    protected int reInitScreenCycle(double rescale)
    {
    	return reInitScreenCycle(rescale, true) ;
    }
    
    /**
     * Initialises screenCycle from a Gamma distribution to determine how often 
     * an MSM is screened, and then starts the cycle in a random place so that 
     * not every MSM gets screened at the same time.
     * @param rescale - The factor to rescale screenCycle by
     * @param ignorePrep - Whether to reInit PrEP users
     */
    protected int reInitScreenCycle(double rescale, boolean ignorePrep)
    {
        // For easily testing the effects of the PrEP screening regime
        boolean checkPrepStatus = ConfigLoader.getMethodVariableBoolean("msm", "reInitScreenCycle", "checkPrepStatus");
        

        if (getPrepStatus())
        {
        	if (ignorePrep || !checkPrepStatus)    // ignorePrep for ordinary simulations where PrEP users are rescaled separately
        		return -1 ;
            if (checkPrepStatus)
                setScreenCycle((sampleGamma(31,1,1)) + 61) ;
        }
        else
        {
            //int firstScreenCycle = (int) new GammaDistribution(7,55).sample() ; 
            //setScreenCycle(firstScreenCycle) ;  // 49.9% screen within a year 2016
            if (statusHIV)
                setScreenCycle(sampleGamma(6,71,rescale)) ;  // 41% screen within a year
            else
                setScreenCycle(sampleGamma(6,85.5,rescale)) ;  // 26% screen within a year
            
        }
        // Randomly set timer for next STI screen 
        setScreenTime(RAND.nextInt(getScreenCycle()) + 1) ;   //Timing begins at next screening
        
        return getScreenCycle() ;
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
     * Taken from GCPS Table 30 2017, Table 26 2019.
     * Where they disagree the later value is taken.
     * 
     * @param year (int) Which year of the simulation we are switching to.
     * @param riskyProbability (double) The proportion of MSM with riskyStatus true.
     * @return (boolean) Whether or not the MSM is now on PrEP.
     */
    public boolean reinitPrepStatus(int year, double riskyProbability)
    {
        //PrEP use begins in 2013 (after 6 years)
        if (year < 6)
            return false ;
        year -= 6 ;
        
        // Values up to 2018
        //double[] prepProbabilityArray = new double[] {0.011,0.014,0.014,0.039,0.139,0.204,0.204} ;
        // Most recent
        double[] prepProbabilityArray = new double[] {0.011,0.014,0.017,0.049,0.167,0.239,0.310  // 2013 to 2019
            //    ,0.39,0.46,0.53,0.60,0.67,0.74    // 2020 to 2025
        } ;
        if (year >= prepProbabilityArray.length)
            year = prepProbabilityArray.length - 1 ;
        
        boolean prep = prepStatus ;
        if (riskyStatusCasual && (!statusHIV))
        {
            double prepProbability = prepProbabilityArray[year] / riskyProbability ;
            setPrepStatus(RAND.nextDouble() < prepProbability) ;
        }
        else
            setPrepStatus(false) ;
        
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
     * @param relationshipClazzName (String) The Class of Relationship being considered.
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
     * @param partner (Agent) The Agent with whom a Casual Relationship is being considered.
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
    

    final private double getProportionUndetectable(int index)
    {
    	if (index >= PROPORTION_UNDETECTABLE.length)
    		index = PROPORTION_UNDETECTABLE.length - 1 ;
        return PROPORTION_UNDETECTABLE[index] ;
    }
    
    /**
     * HIV positive MSM are more likely to disclose the statusHIV
     * @return (Double) probability of disclosing statusHIV
     */
    protected double getProbabilityDiscloseHIV()
    {
        if (getStatusHIV())
            return POSITIVE_DISCLOSE_PROBABILITY[0] ;
        return NEGATIVE_DISCLOSE_PROBABILITY[0] ;
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
     * Risky MSM are more likely to use strategies other than condoms.
     * @param relationshipClazzName (String) The name of the Class of Relationship within which this encounter is happening.
     * @param agentPartner (Agent) the sexual partner with whom a condom might be used.
     * @return (boolean) true if condom is to be used, false otherwise
     */
    @Override
    protected boolean chooseCondom(String relationshipClazzName, Agent agentPartner) 
    {
        //boolean testCondom = false ;
        //if (testCondom)
          //  return (RAND.nextDouble() < probabilityUseCondom ) ;
        
        boolean localRiskyStatus ; 
        double localProbabilityUseCondom ;
        if ("Casual".equals(relationshipClazzName))
        {
            localRiskyStatus = riskyStatusCasual ;
            localProbabilityUseCondom = probabilityUseCondomCasual ;
        }
        else    // Could do a separate if for Regular and Monogomous, but they are the same
        {
            localRiskyStatus = riskyStatusRegular ;
            localProbabilityUseCondom = probabilityUseCondomRegular ;
        }
    
        MSM partner = (MSM) agentPartner ;
        if (localRiskyStatus)
        {
            //Boolean partnerSeroPosition = partner.getSeroPosition() ;

            // Not if on PrEP or using U=U
            if (prepStatus)
                return false ;
            
            if (undetectableStatus && trustUndetectable)
                return false ;
            
            if (partner.discloseStatusHIV || discloseStatusHIV)
            {
                if (statusHIV == partner.statusHIV)
                    return false ;
                
                if (partner.prepStatus)
                    return false ;
                
                if (partner.statusHIV)
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
            
            if (undetectableStatus && trustUndetectable)
                return (RAND.nextDouble() < localProbabilityUseCondom ) ;  
            
            if (partner.discloseStatusHIV || discloseStatusHIV)
            {
                if (statusHIV == partner.statusHIV) 
                    return (RAND.nextDouble() < localProbabilityUseCondom ) ;
                else if (partner.statusHIV)
                {
                    if ((partner.undetectableStatus) && (trustUndetectable))
                        return (RAND.nextDouble() < localProbabilityUseCondom ) ;
                }
                else if (partner.prepStatus)    // partner HIV negative
                    return (RAND.nextDouble() < localProbabilityUseCondom ) ;
            }
            
            return true ;
            
            //return (RAND.nextDouble() < localProbabilityUseCondom ) ;  //TODO: Should there be subset who always use?
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
     * Probability of MSM screening on that day. Binary output according to whether 
     * the screening cycle has completed. 
     * @param args (Object[])
     * @return (double) users, 1.1 if screening is due and -0.1 if not. Extra 0.1
     *     to guard against round-off errors.
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
     * @param args (Object[])
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
        //double noRiskPower = 1.0/DAYS_PER_YEAR ;
        
        double noRisk = Math.pow((1 - risk/1000),1.0/DAYS_PER_YEAR) ;
        return 1 - noRisk ;
    }
    
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
