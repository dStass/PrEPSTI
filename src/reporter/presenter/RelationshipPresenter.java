/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter.presenter;

import java.lang.reflect.Method;
import reporter.* ;

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
        try
        {
        String methodName = args[0] ;
        System.out.println(methodName);
        Method method = RelationshipPresenter.class.getMethod(methodName) ;
        System.out.println(methodName);
        String simName = args[1] ;
        String chartTitle = args[2] ;
        String reportFileName = args[3] ;
        RelationshipPresenter relationshipPresenter = new RelationshipPresenter(simName,chartTitle,reportFileName) ;
        
        if (args.length>4)
            method.invoke(relationshipPresenter, (Object[]) Arrays.copyOfRange(args,4,args.length)) ;
        else 
            method.invoke(relationshipPresenter) ;
        }
        catch ( Exception e )
        {
            LOGGER.log(Level.SEVERE, "{0} {1}", new Object[] {e.toString(),e.getLocalizedMessage()});
        }
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
     */
    public void plotNewRelationshipsPerCycle()
    {
        ArrayList<ArrayList<Object>> relationshipCommenceReport = reporter.prepareRelationshipCommenceReport() ;
        
        // ArrayList<String> deathsPerCycle = prepareDeathsPerCycle() ;
        plotEventsPerCycle("New relationships",relationshipCommenceReport) ;
        
    }

    /**
     * Find and plot the number of new Relationships as a function of time/cycle
     */
    public void plotBreakupsPerCycle()
    {
        ArrayList<ArrayList<Object>> relationshipBreakupReport = reporter.prepareRelationshipBreakupReport() ;
        
        // ArrayList<String> deathsPerCycle = prepareDeathsPerCycle() ;
        
        plotEventsPerCycle("Breakups",relationshipBreakupReport) ;
        
    }

    
    /**
     * Plot the relationships that lasted a given time
     */
    public void plotRelationshipLength()
    {
        HashMap<Object,Integer> relationshipLengthReport = reporter.prepareLengthAtBreakupReport() ;
        
        // Comment out if Casual Relationships are to be included
        relationshipLengthReport.remove(1) ;
        
        plotHashMap("Length", "Number of relationships", relationshipLengthReport ) ;
    }

    public void plotCumulativeRelationshipLengths()
    {
        HashMap<Object,Integer> cumulativeRelationshipLengthReport = reporter.prepareCumulativeLengthReport() ;
        
        plotHashMap("Number of relationships","Cumulative length distribution",cumulativeRelationshipLengthReport) ;
    }
    
    public void plotCumulativeRelationships()
    {
        HashMap<Object,Integer> cumulativeRelationshipRecord = reporter.prepareCumulativeRelationshipRecord() ;
        
        plotHashMap("Number of partners to date","Cumulative distribution",cumulativeRelationshipRecord) ;
    }
    
    public void plotMeanNumberRelationshipsReport()
    {
        LOGGER.info("plotMeanNumberRelationshipReport()") ;
        ArrayList<Object> meanNumberRelationshipsReport = reporter.prepareMeanNumberRelationshipsReport() ;
        
        plotCycleValue("Mean number of partners",meanNumberRelationshipsReport) ;
    }
}
