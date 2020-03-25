/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PRSP.PrEPSTI.reporter.presenter;

//import java.lang.reflect.Method;
import PRSP.PrEPSTI.reporter.* ;
//import community.Community ;

import java.util.ArrayList ;
import java.util.Arrays;
//import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * For presenting results from the RelationshipReporter
 * Methods typically relevant to birth, death, and appropriate ages
 * @author MichaelWalker
 */
public class RelationshipPresenter extends Presenter{
    
    private RelationshipReporter reporter ;
    
    //static String simName = "u60symp41bPop40000Cycles730" ;
    //static String simName = "relationshipLT2aPop40000Cycles730" ;
    static String simName = "casual1p00Pop40000Cycles730" ;
    //static String simName = "noGSNpostHolt3aPop40000Cycles5475" ; // "testPlotCondomUsePop4000Cycles500" ; // args[0] ;
        
    public static void main(String[] args)
    {
        //String chartTitle = "Nb_Agents_had_given_relationships" ; // args[1] ;
        String chartTitle = "cumulative_relationships" ; // args[1] ;
        //String chartTitle = "mean_nb_relationships" ;
        //String chartTitle = "breakups" ;
        //String chartTitle = "agents_in_relationships" ;
        //String chartTitle = "agents_entered_relationships" ;
        
        String reportFileName = "output/test/" ; // args[2] ;
        //String reportFileName = "output/year2007/" ; // args[2] ;
        //String reportFileName = "output/year2010/" ; // args[2] ;
        //String reportFileName = "output/prep/" ; // args[2] ;
        
        LOGGER.info(chartTitle) ;
        LOGGER.info(simName) ;
        String[] relationshipClazzNames = new String[] {"Casual","Regular","Monogomous"} ; // "Casual","Regular","Monogomous"
        RelationshipPresenter relationshipPresenter = new RelationshipPresenter(simName,chartTitle,reportFileName) ;
        //relationshipPresenter.plotBreakupsPerCycle() ;
        //relationshipPresenter.plotCumulativeRelationshipGaps() ;
        //relationshipPresenter.plotCumulativeRelationships("Casual",0, 6, 0) ;
        //relationshipPresenter.plotCumulativeRelationships(new String[] {"Casual","Regular","Monogomous"}, 0, 6, 0) ;
        relationshipPresenter.plotCumulativeRelationships(1, new String[] {"Casual","Regular","Monogomous"}, 0, 6, 0) ;
        //relationshipPresenter.plotCumulativeRelationshipLengths() ;
        //relationshipPresenter.plotRelationshipCumulativeTransmissions() ;
        //relationshipPresenter.plotMeanNumberRelationshipsReport(relationshipClazzNames);
        //relationshipPresenter.plotAgentRelationshipsMeanYears(relationshipClazzNames, 3, 6, 0, 2017) ;
        //relationshipPresenter.plotAgentRelationshipsMean(new String[] {"Casual","Regular","Monogomous"}, 0, 6, 0) ;
        //relationshipPresenter.plotRelationshipLength() ;
        //relationshipPresenter.plotRecentRelationshipsReport(relationshipClazzNames,0,6,0) ;
        //relationshipPresenter.plotNumberRelationships(new String[] {"Casual","Regular","Monogomous"}, 0, 6, 0) ;
        //relationshipPresenter.plotNumberAgentsEnteredRelationship(new String[] {"Casual","Regular","Monogomous"}, 0, 6, 0) ;
        //relationshipPresenter.plotNumberAgentsEnteredRelationshipYears(new String[] {"Casual"}, 10, 6, 0, 2017) ;
    }
    
    public RelationshipPresenter()
    {
        super() ;   
    }
    
    public RelationshipPresenter(String simName, String chartTitle, String fileName)
    {
        super(simName, chartTitle);
        applicationTitle = simName ;
        setReporter(new RelationshipReporter(simName,fileName)) ;
    }
    
    public RelationshipPresenter(String applicationTitle, String chartTitle, RelationshipReporter reporter)
    {
        super(applicationTitle,chartTitle,reporter) ;
        setReporter(reporter) ;
    }
    
    /**
     * Overrides super.setReporter() because reporter is now RelationshipReporter
     * @param reporter 
     */
    public void setReporter(RelationshipReporter reporter)
    {
        this.reporter = reporter ;
    }

    /**
     * Find and plot the number of new Relationships as a function of time/cycle
     * The first cycle is left out to accomodate burn-in.
     */
    public void plotNewRelationshipsPerCycle()
    {
        ArrayList<ArrayList<Comparable>> relationshipCommenceReport = reporter.prepareRelationshipCommenceReport() ;
        ArrayList<ArrayList<Comparable>> submitReport = new ArrayList<ArrayList<Comparable>>() ;
        for (int index = 1 ; index < relationshipCommenceReport.size() ; index++)
            submitReport.add(relationshipCommenceReport.get(index)) ;
        plotEventsPerCycle("New relationships",submitReport) ;
        
    }

    /**
     * Find and plot the number of new Relationships as a function of time/cycle
     * The first cycle is left out to accomodate burn-in.
     */
    public void plotNewRelationshipsPerCycle(String[] relationshipClazzes)
    {
        ArrayList<ArrayList<Comparable>> relationshipCommenceReport = reporter.prepareRelationshipCommenceReport(relationshipClazzes) ;
        ArrayList<ArrayList<Comparable>> submitReport = new ArrayList<ArrayList<Comparable>>() ;
        for (int index = 1 ; index < relationshipCommenceReport.size() ; index++)
            submitReport.add(relationshipCommenceReport.get(index)) ;
        plotEventsPerCycle("New relationships",submitReport) ;
        
    }

    /**
     * Find and plot the number of new Relationships as a function of time/cycle
     */
    public void plotBreakupsPerCycle()
    {
        ArrayList<ArrayList<Comparable>> relationshipBreakupReport = reporter.prepareRelationshipBreakupReport() ;
        ArrayList<ArrayList<Comparable>> submitReport = new ArrayList<ArrayList<Comparable>>() ;
        for (int index = 2 ; index < relationshipBreakupReport.size() ; index++)
            submitReport.add(relationshipBreakupReport.get(index)) ;
        
        plotEventsPerCycle("Breakups",submitReport) ;
        
    }

    /**
     * Plots how many Agents had entered into each given relationshipClassName during
     * given time. Does not indicate number of relationships per Agent.
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays 
     */
    private void plotNumberAgentsEnteredRelationshipYears(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int lastYear)
    {
        HashMap<Comparable,Number[]> numberAgentsEnteredRelationshipYears
        = reporter.prepareNumberAgentsEnteredRelationshipYears(relationshipClassNames, backYears, backMonths, backDays, lastYear) ;

        plotHashMap("Year", relationshipClassNames, numberAgentsEnteredRelationshipYears) ;
    }

    /**
     * Plots the proportion of Agents involved in each of the Relationship classes 
     * in relationshipClassNames.
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle 
     */
    private void plotNumberRelationships(String[] relationshipClassNames, int backYears, int backMonths, int backDays)
    {
        int maxCycles = reporter.getMaxCycles() ;
        
        plotNumberRelationships(relationshipClassNames, backYears, backMonths, backDays, maxCycles) ;
    }
    

    /**
     * Plots the proportion of Agents involved in each of the Relationship classes 
     * in relationshipClassNames.
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle 
     */
    private void plotNumberRelationships(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle)
    {
        HashMap<Comparable,Number> proportionRelationshipsReport 
                = reporter.prepareProportionRelationshipsReport(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        
        plotHashMap("class of relationship","proportion of agents",proportionRelationshipsReport) ;
    }
    
    /**
     * Plots how many Agents had entered into each given relationshipClassName during
     * given time. Does not indicate number of relationships per Agent.
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays 
     */
    private void plotNumberAgentsEnteredRelationship(String[] relationshipClassNames, int backYears, int backMonths, int backDays)
    {
        HashMap<Comparable,Number> numberAgentsEnteredRelationshipReport 
        = reporter.prepareNumberAgentsEnteredRelationshipReport(relationshipClassNames, backYears, backMonths, backDays) ;
    
        String timePeriod = GET_TIME_PERIOD_STRING(backYears, backMonths, backDays) ;
        
        plotHashMap("Class of Relationships","Proportion of Agents in last " + timePeriod, numberAgentsEnteredRelationshipReport) ;
    }
    
    /**
     * Plots the mean number of relationships per Agent of each given class during 
     * the specified period.
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays 
     */
    public void plotAgentRelationshipsMean(String[] relationshipClassNames, int backYears, int backMonths, int backDays, String sortingProperty)
    {
        //(HashMap) relationshipClassName maps to mean number of
        // Relationships of given class per agentId involved in during given time period).
        String agentRelationshipsMean
            = reporter.prepareAgentRelationshipsMean(relationshipClassNames, backYears, backMonths, backDays, sortingProperty) ;
        
        LOGGER.info(agentRelationshipsMean.toString());
        String timePeriod = GET_TIME_PERIOD_STRING(backYears, backMonths, backDays) ;
        
        //String[] legend = new String[relationshipClassNames.length * agentRelationshipsMean.size()] ;
        
        /*int legendIndex = 0 ;
        ArrayList<String> properties = Reporter.IDENTIFY_PROPERTIES(agentRelationshipsMean) ;
        for (String relationshipClassName : relationshipClassNames)
        {
            for (String keyValue : properties)
            {
                legend[legendIndex] = relationshipClassName + GROUP + keyValue ;
                legendIndex++ ;
            }
        }*/
        multiPlotValues(agentRelationshipsMean, "Relationships per Agent for last " + timePeriod,"Class of Relationships") ;
        //plotHashMapString(agentRelationshipsMean,"Relationships per Agent for last " + timePeriod,"Class of Relationships",legend) ;
    }
    
    /**
     * Plots the mean number of relationships per Agent of each given class during 
     * the specified period.
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays 
     */
    public void plotAgentRelationshipsMean(String[] relationshipClassNames, int backYears, int backMonths, int backDays)
    {
        //(HashMap) relationshipClassName maps to mean number of
        // Relationships of given class per agentId involved in during given time period).
        HashMap<Comparable,Number> agentRelationshipsMean
            = reporter.prepareAgentRelationshipsMean(relationshipClassNames, backYears, backMonths, backDays) ;
        
        String timePeriod = GET_TIME_PERIOD_STRING(backYears, backMonths, backDays) ;
        
        plotHashMap("Class of Relationships","Relationships per Agent for last " + timePeriod, agentRelationshipsMean) ;
    }
    
    /**
     * Plots the mean number of relationships per Agent of each given class during 
     * backMonths months and backDays days for backYears years counting back from
     * last Year
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays 
     */
    private void plotAgentRelationshipsMeanYears(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int lastYear) 
    {
        HashMap<Comparable,Number[]> agentRelationshipsMeanYears
            = reporter.prepareAgentRelationshipsMeanYears(relationshipClassNames, backYears, backMonths, backDays, lastYear) ; 
        
        //String timePeriod = GET_TIME_PERIOD_STRING(backYears, backMonths, backDays) ;
        
        plotHashMap("Year", relationshipClassNames, agentRelationshipsMeanYears) ;
    }
    
    /**
     * Plot the number of relationships that lasted a given time
     */
    public void plotRelationshipLength()
    {
        HashMap<Comparable,Number> relationshipLengthReport = reporter.prepareLengthAtBreakupReport() ;
        
        // Comment out if Casual Relationships are to be included
        relationshipLengthReport.remove(1) ;
        
        //Object maxSize = Collections.max(new HashSet(relationshipLengthReport.keySet())) ;
        
        plotHashMap("Length", "Length of relationships", relationshipLengthReport ) ;
    }

    public void plotCumulativeRelationshipLengths()
    {
        HashMap<Comparable,Number> cumulativeRelationshipLengthReport = reporter.prepareCumulativeLengthReport() ;
        
        plotSpline("Cumulative length distribution","Number of relationships",cumulativeRelationshipLengthReport) ;
    }
    
    /**
     * Plot the proportion of agentIds who have had how many or more Relationships
     * @param relationshipClassNames 
     */
    public void plotCumulativeRelationships(int nbRelationships, String[] relationshipClassNames, int backYears, int backMonths, int backDays)
    {
        HashMap<Comparable,Number> output = new HashMap<Comparable,Number>() ;
        
        // A snapshot of how many agentIds have more had how many or more Relationships
        HashMap<Comparable,HashMap<Comparable,Number>> cumulativeRelationshipRecord 
                = reporter.prepareCumulativeRelationshipRecord(nbRelationships, relationshipClassNames, backYears, backMonths, backDays) ;
        
        Number outputEntry ;
        for (Comparable className : cumulativeRelationshipRecord.keySet())
        {
            outputEntry = cumulativeRelationshipRecord.get(className).get(nbRelationships) ;
            if (outputEntry == null)
                outputEntry = 0 ;
            output.put(className, outputEntry) ;
        }
        
        LOGGER.log(Level.INFO, "{0}", output) ;
        
        plotHashMap("Class of Relationships","Number of Agents",output) ;
    }
    
    /**
     * Plot how many agentIds have more had how many or more Relationships
     * @param relationshipClassNames 
     */
    public void plotCumulativeRelationships(String[] relationshipClassNames, int backYears, int backMonths, int backDays)
    {
        // A snapshot of how many agentIds have more had how many or more Relationships
        HashMap<Comparable,HashMap<Comparable,Number>> cumulativeRelationshipRecord 
                = reporter.prepareCumulativeRelationshipRecord(-1,relationshipClassNames, backYears, backMonths, backDays) ;
        
        HashMap<Comparable,Number[]> invertedHashMap 
                = Reporter.INVERT_HASHMAP_LIST(cumulativeRelationshipRecord,relationshipClassNames) ;
        
        plotSpline("Cumulative number of partners","Number of agents",invertedHashMap,relationshipClassNames) ;
    }
    
    /**
     * Plot how many agentIds have more had how many or more relationshipClassName 
     * Relationships
     * @param relationshipClassName 
     * @param backYears 
     * @param backMonths 
     * @param backDays 
     */
    public void plotCumulativeRelationships(String relationshipClassName, int backYears, int backMonths, int backDays)
    {
        String[] relationshipClassNames ;
        if (relationshipClassName.isEmpty())
        {
            relationshipClassNames = new String[] {"Regular","Monogomous","Casual"} ;
            relationshipClassName = "total" ;
        } 
        else
            relationshipClassNames = new String[] {relationshipClassName} ;
        // A snapshot of how many agentIds have more had how many or more Relationships
        HashMap<Comparable,HashMap<Comparable,Number>> cumulativeRelationshipRecord 
                = reporter.prepareCumulativeRelationshipRecord(-1, relationshipClassNames, backYears, backMonths, backDays) ;
        
        //HashMap<Object,Number[]> invertedHashMap 
          //      = Reporter.INVERT_HASHMAP_LIST(cumulativeRelationshipRecord, new String[] {relationshipClassName}) ;
        LOGGER.log(Level.INFO, "{0}", cumulativeRelationshipRecord) ;
        
        plotHashMap("Cumulative number of partners","Number of agents",
                binHashMap(cumulativeRelationshipRecord.get(relationshipClassName),"Nb_of_partners")) ; 
    }
    
    /**
     * Plots how many Agents had how many partners in last given backYears years, 
     * backMonths months and backDays days.
     * @param backYears
     * @param backMonths
     * @param backDays 
     */
    public void plotRecentRelationshipsReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays)
    {
        HashMap<Comparable,HashMap<Comparable,Number>> recentRelationshipsReport 
                = reporter.prepareRecentRelationshipsReport(relationshipClassNames, backYears, backMonths, backDays) ;
        
        String[] relationshipClassNewNames = Arrays.copyOf(relationshipClassNames,relationshipClassNames.length + 1) ;
        relationshipClassNewNames[relationshipClassNames.length] = "total" ;
        //recentRelationshipsReport.remove("total") ;
        
        HashMap<Comparable,Number[]> invertedHashMap 
                = Reporter.INVERT_HASHMAP_LIST(recentRelationshipsReport,relationshipClassNewNames) ;
        LOGGER.info(invertedHashMap.toString());
        
        for (int index = 0 ; index < relationshipClassNewNames.length ; index++ )
            relationshipClassNewNames[index] = relationshipClassNewNames[index] + GROUP + relationshipClassNewNames[index] ;
                
        String timePeriod = String.valueOf(backYears) + " years " 
                + String.valueOf(backMonths) + " months " 
                + String.valueOf(backDays) + " days " ;
        
        HashMap<Comparable,Number[]> binnedReport = binHashMap(invertedHashMap, relationshipClassNewNames) ;
        LOGGER.info(reporter.getFolderPath());
        Reporter.WRITE_CSV(binnedReport, chartTitle, relationshipClassNewNames, "nb_Relationships", simName, reporter.getFolderPath()) ;
        plotHashMap("partners in " + timePeriod,relationshipClassNewNames,binnedReport) ;
    }
    
    /**
     * Plots the mean number of Relationships per Agent over time.
     * @param relationshipClassNames
     */
    public void plotMeanNumberRelationshipsReport(String[] relationshipClassNames)
    {
        // (ArrayList) records of mean number of each Relationship class per Agent
        ArrayList<HashMap<Object,String>> meanNumberRelationshipsReport 
                = reporter.prepareMeanNumberRelationshipsReport(relationshipClassNames) ;
        LOGGER.log(Level.INFO, "{0}", meanNumberRelationshipsReport);
        //ArrayList<ArrayList<Object>> invertedReport = Reporter.INVERT_ARRAY_HASHMAP(meanNumberRelationshipsReport,relationshipClassNames) ;
        //LOGGER.log(Level.INFO, "{0}", invertedReport);
        ArrayList<ArrayList<String>> invertedReport = new ArrayList<ArrayList<String>>() ;
        for (String relationshipClassName : relationshipClassNames)
        {
            ArrayList<String> record = new ArrayList<String>() ;
            for (HashMap<Object,String> report : meanNumberRelationshipsReport)
            {
                record.add(report.get(relationshipClassName)) ;
            }
            invertedReport.add((ArrayList<String>) record.clone()) ;
        }
        LOGGER.log(Level.INFO, "{0}", invertedReport);
        multiPlotCycleValue("meanNb",invertedReport,relationshipClassNames) ;
    }
    
    /**
     * Plots gap between last two Relationships, ie cycle of commencement for last 
     * minus cycle of breakup for second-last Relationship. A negative gap indicates
     * overlap.
     * TODO: Implement as cumulative plot.
     */
    public void plotAgentGapReport()
    {
        HashMap<Comparable,Number> agentGapReport = reporter.prepareAgentGapReport() ;
        
        plotHashMap("Agent Ids","Days between relationships",agentGapReport) ;
    }
    
    
    public void plotCumulativeRelationshipGaps()
    {
        HashMap<Comparable,Number> cumulativeRelationshipGapRecord = reporter.prepareRelationshipCumulativeGapRecord() ;
        
        plotHashMap("Length of relationship gap","Cumulative distribution",cumulativeRelationshipGapRecord) ;
    }
    
    /**
     * plots the number of Relationships responsible for the given number or more
     * of transmissions.
     */
    public void plotRelationshipCumulativeTransmissions()
    {
        EncounterReporter encounterReporter 
                = new EncounterReporter(reporter.getSimName(), reporter.getFolderPath()) ;
        HashMap<Comparable,Number> relationshipCumulativeTransmissionReport
        = reporter.prepareRelationshipCumulativeTransmissionReport(encounterReporter) ;
        
        LOGGER.log(Level.INFO, "{0}", relationshipCumulativeTransmissionReport) ;
        
        plotSpline("Number of Transmissions","Cumulative relationships",relationshipCumulativeTransmissionReport) ;
    }
            
            
}
