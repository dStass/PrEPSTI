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
        String simName = "NoPrepCalibration39Pop40000Cycles20000" ; // "NoPrepCalibration86Pop40000Cycles5000" ; // "introPrepCalibration49Pop40000Cycles5000" ; // "NoPrepSetting01Pop40000Cycles5000" ; // 
        //String simName = "testPop30000Cycles500" ; // "testPlotCondomUsePop4000Cycles500" ; // args[0] ;
        String chartTitle = "Nb_Agents_had_given_relationships" ; // args[1] ;
        //String chartTitle = "cumulative_relationships" ; // args[1] ;
        String reportFileName = "output/test/" ; // args[2] ;
        LOGGER.info(chartTitle) ;
        RelationshipPresenter relationshipPresenter = new RelationshipPresenter(simName,chartTitle,reportFileName) ;
        //relationshipPresenter.plotCumulativeRelationshipGaps() ;
        //relationshipPresenter.plotCumulativeRelationships(new String[] {"Casual","Regular","Monogomous"}) ;
        //relationshipPresenter.plotCumulativeRelationshipLengths() ;
        //relationshipPresenter.plotRelationshipCumulativeTransmissions() ;
        relationshipPresenter.plotMeanNumberRelationshipsReport(new String[] {"Casual","Regular","Monogomous"});
        //relationshipPresenter.plotRelationshipLength() ;
        //relationshipPresenter.plotNumberAgentsEnteredRelationship(new String[] {"Casual","Regular","Monogomous"}, 0, 6, 0) ;
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
    private void plotNumberAgentsEnteredRelationship(String[] relationshipClassNames, int backYears, int backMonths, int backDays)
    {
        HashMap<Object,Number> numberAgentsEnteredRelationshipReport 
        = reporter.prepareNumberAgentsEnteredRelationshipReport(relationshipClassNames, backYears, backMonths, backDays) ;
    
        String timePeriod = String.valueOf(backYears) + "Y" 
                + String.valueOf(backMonths) + "M" 
                + String.valueOf(backDays) + "D" ;
        
        
        plotHashMap("Class of Relationships","Number of Agents in last " + timePeriod, numberAgentsEnteredRelationshipReport) ;
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
    
    public void plotCumulativeRelationships(String[] relationshipClassNames)
    {
        // A snapshot of how many agentIds have more had how many or more Relationships
        HashMap<Object,HashMap<Object,Number>> cumulativeRelationshipRecord 
                = reporter.prepareCumulativeRelationshipRecord(relationshipClassNames) ;
        
        HashMap<Object,Number[]> invertedHashMap 
                = Reporter.invertHashMapList(cumulativeRelationshipRecord,relationshipClassNames) ;
        
        plotSpline("Cumulative number of partners","Number of agents",invertedHashMap,relationshipClassNames) ;
    }
    
    /**
     * Plots how many Agents had many partners in last given backYears years, 
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
                = Reporter.invertHashMapList(recentRelationshipsReport,relationshipClassNames) ;
        
        String timePeriod = String.valueOf(backYears) + "Y" 
                + String.valueOf(backMonths) + "M" 
                + String.valueOf(backDays) + "D" ;
        
        plotSpline("partners in " + timePeriod,"Number of Agents",invertedHashMap, relationshipClassNames) ;
    }
    
    /**
     * Plots the mean number of Relationships per Agent over time.
     */
    public void plotMeanNumberRelationshipsReport(String[] relationshipClassNames)
    {
        ArrayList<HashMap<Object,Number>> meanNumberRelationshipsReport 
                = reporter.prepareMeanNumberRelationshipsReport(relationshipClassNames) ;
        
        ArrayList<ArrayList<Object>> invertedReport = Reporter.invertArrayHashMap(meanNumberRelationshipsReport,relationshipClassNames) ;
        
        multiPlotCycleValue("Mean number of partners",invertedReport,relationshipClassNames) ;
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
