/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter.presenter;

//import java.lang.reflect.Method;
import reporter.* ;
//import community.Community ;

import java.util.ArrayList ;
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
    
    public static void main(String[] args)
    {
        //String simName = "NoPrepCalibration22Pop40000Cycles500" ; // "NoPrepCalibration86Pop40000Cycles5000" ; // "introPrepCalibration49Pop40000Cycles5000" ; // "NoPrepSetting01Pop40000Cycles5000" ; // 
        //String simName = "NoBurninPop40000Cycles8000" ;
        String simName = "TestPop40000Cycles100" ; // "testPlotCondomUsePop4000Cycles500" ; // args[0] ;
        //String simName = "RelationshipCalibration74Pop40000Cycles100" ; // "testPlotCondomUsePop4000Cycles500" ; // args[0] ;
        //String chartTitle = "Nb_Agents_had_given_relationships" ; // args[1] ;
        //String chartTitle = "cumulative_relationships" ; // args[1] ;
        //String chartTitle = "mean_nb_relationships" ;
        String chartTitle = "breakups" ;
        //String chartTitle = "agents_entered_relationships" ;
        String reportFileName = "output/test/" ; // args[2] ;
        
        LOGGER.info(chartTitle) ;
        LOGGER.info(simName) ;
        String[] relationshipClazzNames = new String[] {"Regular","Monogomous","Casual"} ; // "Casual","Regular","Monogomous"
        RelationshipPresenter relationshipPresenter = new RelationshipPresenter(simName,chartTitle,reportFileName) ;
        relationshipPresenter.plotBreakupsPerCycle() ;
        //relationshipPresenter.plotCumulativeRelationshipGaps() ;
        //relationshipPresenter.plotCumulativeRelationships(10, new String[] {"Casual","Regular","Monogomous"}, 0, 6, 0) ;
        //relationshipPresenter.plotCumulativeRelationshipLengths() ;
        //relationshipPresenter.plotRelationshipCumulativeTransmissions() ;
        //relationshipPresenter.plotMeanNumberRelationshipsReport(relationshipClazzNames);
        //relationshipPresenter.plotAgentRelationshipsMeanYears(relationshipClazzNames, 3, 6, 0, 2017) ;
        //relationshipPresenter.plotAgentRelationshipsMean(new String[] {"Casual","Regular","Monogomous"}, 0, 6, 0) ;
        //relationshipPresenter.plotRelationshipLength() ;
        //relationshipPresenter.plotRecentRelationshipsReport(relationshipClazzNames,0,6,0) ;
        //relationshipPresenter.plotNumberAgentsEnteredRelationship(new String[] {"Casual","Regular","Monogomous"}, 0, 6, 0) ;
        //relationshipPresenter.plotNumberAgentsEnteredRelationshipYears(new String[] {"Casual","Regular","Monogomous"}, 2, 6, 0, 2017) ;
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
        ArrayList<ArrayList<Object>> relationshipCommenceReport = reporter.prepareRelationshipCommenceReport() ;
        ArrayList<ArrayList<Object>> submitReport = new ArrayList<ArrayList<Object>>() ;
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
        ArrayList<ArrayList<Object>> relationshipCommenceReport = reporter.prepareRelationshipCommenceReport(relationshipClazzes) ;
        ArrayList<ArrayList<Object>> submitReport = new ArrayList<ArrayList<Object>>() ;
        for (int index = 1 ; index < relationshipCommenceReport.size() ; index++)
            submitReport.add(relationshipCommenceReport.get(index)) ;
        plotEventsPerCycle("New relationships",submitReport) ;
        
    }

    /**
     * Find and plot the number of new Relationships as a function of time/cycle
     */
    public void plotBreakupsPerCycle()
    {
        ArrayList<ArrayList<Object>> relationshipBreakupReport = reporter.prepareRelationshipBreakupReport() ;
        ArrayList<ArrayList<Object>> submitReport = new ArrayList<ArrayList<Object>>() ;
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
        HashMap<Object,Number[]> numberAgentsEnteredRelationshipYears
        = reporter.prepareNumberAgentsEnteredRelationshipYears(relationshipClassNames, backYears, backMonths, backDays, lastYear) ;

        plotHashMap("Year", relationshipClassNames, numberAgentsEnteredRelationshipYears) ;
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
        HashMap<Object,Number> numberAgentsEnteredRelationshipReport 
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
    public void plotAgentRelationshipsMean(String[] relationshipClassNames, int backYears, int backMonths, int backDays)
    {
        //(HashMap) relationshipClassName maps to mean number of
        // Relationships of given class per agentId involved in during given time period).
        HashMap<Object,Number> agentRelationshipsMean
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
        HashMap<Object,Number[]> agentRelationshipsMeanYears
            = reporter.prepareAgentRelationshipsMeanYears(relationshipClassNames, backYears, backMonths, backDays, lastYear) ; 
        
        //String timePeriod = GET_TIME_PERIOD_STRING(backYears, backMonths, backDays) ;
        
        plotHashMap("Year", relationshipClassNames, agentRelationshipsMeanYears) ;
    }
    
    /**
     * Plot the number of relationships that lasted a given time
     */
    public void plotRelationshipLength()
    {
        HashMap<Object,Number> relationshipLengthReport = reporter.prepareLengthAtBreakupReport() ;
        
        // Comment out if Casual Relationships are to be included
        // relationshipLengthReport.remove(1) ;
        
        plotHashMap("Length", "Length of relationships", relationshipLengthReport ) ;
    }

    public void plotCumulativeRelationshipLengths()
    {
        HashMap<Object,Number> cumulativeRelationshipLengthReport = reporter.prepareCumulativeLengthReport() ;
        
        plotSpline("Cumulative length distribution","Number of relationships",cumulativeRelationshipLengthReport) ;
    }
    
    /**
     * Plot how many agentIds have more had how many or more Relationships
     * @param relationshipClassNames 
     */
    public void plotCumulativeRelationships(int nbRelationships, String[] relationshipClassNames, int backYears, int backMonths, int backDays)
    {
        HashMap<Object,Number> output = new HashMap<Object,Number>() ;
        
        // A snapshot of how many agentIds have more had how many or more Relationships
        HashMap<Object,HashMap<Object,Number>> cumulativeRelationshipRecord 
                = reporter.prepareCumulativeRelationshipRecord(nbRelationships, relationshipClassNames, backYears, backMonths, backDays) ;
        
        for (Object className : cumulativeRelationshipRecord.keySet())
            output.put(className, cumulativeRelationshipRecord.get(className).get(nbRelationships)) ;
        
        plotHashMap("Class of Relationships","Number of Agents",output) ;
    }
    
    /**
     * Plot how many agentIds have more had how many or more Relationships
     * @param relationshipClassNames 
     */
    public void plotCumulativeRelationships(String[] relationshipClassNames, int backYears, int backMonths, int backDays)
    {
        // A snapshot of how many agentIds have more had how many or more Relationships
        HashMap<Object,HashMap<Object,Number>> cumulativeRelationshipRecord 
                = reporter.prepareCumulativeRelationshipRecord(-1,relationshipClassNames, backYears, backMonths, backDays) ;
        
        HashMap<Object,Number[]> invertedHashMap 
                = Reporter.INVERT_HASHMAP_LIST(cumulativeRelationshipRecord,relationshipClassNames) ;
        
        plotSpline("Cumulative number of partners","Number of agents",invertedHashMap,relationshipClassNames) ;
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
        HashMap<Object,HashMap<Object,Number>> recentRelationshipsReport 
                = reporter.prepareRecentRelationshipsReport(relationshipClassNames, backYears, backMonths, backDays) ;
        
        HashMap<Object,Number[]> invertedHashMap 
                = Reporter.INVERT_HASHMAP_LIST(recentRelationshipsReport,relationshipClassNames) ;
        
        String timePeriod = String.valueOf(backYears) + "Y" 
                + String.valueOf(backMonths) + "M" 
                + String.valueOf(backDays) + "D" ;
        
        plotSpline("partners in " + timePeriod,"Number of Agents",invertedHashMap, relationshipClassNames) ;
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
        ArrayList<ArrayList<Object>> invertedReport = new ArrayList<ArrayList<Object>>() ;
        for (String relationshipClassName : relationshipClassNames)
        {
            ArrayList<String> record = new ArrayList<String>() ;
            for (HashMap<Object,String> report : meanNumberRelationshipsReport)
            {
                record.add(report.get(relationshipClassName)) ;
            }
            invertedReport.add((ArrayList<Object>) record.clone()) ;
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
        HashMap<Object,Number> agentGapReport = reporter.prepareAgentGapReport() ;
        
        plotHashMap("Agent Ids","Days between relationships",agentGapReport) ;
    }
    
    
    public void plotCumulativeRelationshipGaps()
    {
        HashMap<Object,Number> cumulativeRelationshipGapRecord = reporter.prepareRelationshipCumulativeGapRecord() ;
        
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
        HashMap<Object,Number> relationshipCumulativeTransmissionReport
        = reporter.prepareRelationshipCumulativeTransmissionReport(encounterReporter) ;
        
        plotSpline("Number of Transmissions","Cumulative relationships",relationshipCumulativeTransmissionReport) ;
    }
            
            
}
