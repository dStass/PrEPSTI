/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter.presenter;

import java.lang.reflect.Method;
import reporter.* ;
import community.Community ;

import java.util.ArrayList ;
import java.util.Arrays;
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
        String simName = "noPrepCalibration86Pop40000Cycles5000" ; // Community.NAME_ROOT ; // "testPlotCondomUsePop4000Cycles500" ; // args[0] ;
        String chartTitle = "relationships_of_length" ; // args[1] ;
        String reportFileName = "output/test/" ; // args[2] ;
        RelationshipPresenter relationshipPresenter = new RelationshipPresenter(simName,chartTitle,reportFileName) ;
        //relationshipPresenter.plotCumulativeRelationshipGaps() ;
        //relationshipPresenter.plotCumulativeRelationshipLengths() ;
        //relationshipPresenter.plotRelationshipCumulativeTransmissions() ;
        relationshipPresenter.plotRelationshipLength() ;
        
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
     * Plot the number of relationships that lasted a given time
     */
    public void plotRelationshipLength()
    {
        HashMap<Object,Number> relationshipLengthReport = reporter.prepareLengthAtBreakupReport() ;
        
        // Comment out if Casual Relationships are to be included
        relationshipLengthReport.remove(1) ;
        
        plotHashMap("Length", "Number of relationships", relationshipLengthReport ) ;
    }

    public void plotCumulativeRelationshipLengths()
    {
        HashMap<Object,Number> cumulativeRelationshipLengthReport = reporter.prepareCumulativeLengthReport() ;
        
        plotHashMap("Number of relationships","Cumulative length distribution",cumulativeRelationshipLengthReport) ;
    }
    
    public void plotCumulativeRelationships()
    {
        HashMap<Object,Number> cumulativeRelationshipRecord = reporter.prepareCumulativeRelationshipRecord() ;
        
        plotHashMap("Number of partners to date","Cumulative distribution",cumulativeRelationshipRecord) ;
    }
    
    public void plotMeanNumberRelationshipsReport()
    {
        ArrayList<Object> meanNumberRelationshipsReport = reporter.prepareMeanNumberRelationshipsReport() ;
        
        plotCycleValue("Mean number of partners",meanNumberRelationshipsReport) ;
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
        EncounterReporter encounterReporter = new EncounterReporter(Community.NAME_ROOT, Community.FILE_PATH) ;
        LOGGER.info(reporter.getSimName());
        HashMap<Object,Number> relationshipCumulativeTransmissionReport
        = reporter.prepareRelationshipCumulativeTransmissionReport(encounterReporter) ;
        
        plotHashMap("Number of Transmissions","Cumulative relationships",relationshipCumulativeTransmissionReport) ;
    }
            
            
}
