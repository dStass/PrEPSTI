/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter.presenter;

import java.lang.reflect.* ;
import java.util.ArrayList ;
import java.util.Arrays;
import java.util.HashMap ;
import java.util.logging.Level;

import reporter.Reporter ;
import reporter.SortReporter ;
import reporter.EncounterReporter ;
import static reporter.presenter.Presenter.LOGGER;


/**
 *
 * @author MichaelWalker
 */
public class SortPresenter extends Presenter {
    
    private SortReporter reporter ;
    
    public static void main(String[] args) // "test1","encounter","population","fileName","test1","plotReceiveSortPrepStatusReport","false"
    {
        String simName = "NoPrepCalibration61Pop40000Cycles7000" ; // args[0] ;
        String chartTitle = "relationships_in_past_ten_years" ; // args[1] ;
        String reportFileName = "../output/test/" ; // args[2] ;
        SortPresenter sortPresenter = new SortPresenter(simName,chartTitle,reportFileName,"infection","relationship") ;
        //encounterPresenter.plotCondomUse();
        sortPresenter.plotSortPrevalence(10,5) ;

    }
    
    public SortPresenter()
    {
        super() ;
    }
    
    public SortPresenter(String applicationTitle, String chartTitle)
    {
        super(applicationTitle, chartTitle);
    }
    
    public SortPresenter(String simName, String chartTitle, String fileName, String unsortedName, String sortingName)
    {
        super(simName,chartTitle) ;
        applicationTitle = simName ;
        setReporter(new SortReporter(simName,fileName,unsortedName,sortingName)) ;
    }
    
    public SortPresenter(String applicationTitle, String chartTitle, SortReporter reporter)
    {
        super(applicationTitle,chartTitle, reporter) ;
        setReporter(reporter) ;
    }
    
    /**
     * Overrides super.setReporter() because reporter is now PopulationReporter
     * @param reporter 
     */
    public void setReporter(SortReporter reporter)
    {
        this.reporter = reporter ;
    }

    /**
     * Plots the number of infections received by Agents with PrEP status,
     * given by (boolean) value, per cycle.
     * @param value 
     */
    public void plotReceiveSortPrepStatusReport(String value)
    {
        LOGGER.info("prepareReceiveSortPrepStatusReport");
        HashMap<Object,HashMap<Object,ArrayList<Object>>> receiveSortPrepStatusReport 
                = reporter.prepareReceiveSortPrepStatusReport(value) ;
        LOGGER.log(Level.INFO, "{0}", receiveSortPrepStatusReport);
        HashMap<Object,HashMap<Object,ArrayList<Object>>> invertedPrepStatusReport 
                = SortReporter.invertHashMapHashMap(receiveSortPrepStatusReport) ;
        LOGGER.info("prepareTransmissionCountReport");
        ArrayList<ArrayList<Object>> nbTransmissionReport 
                = ((EncounterReporter) reporter.getUnsortedReporter()).prepareReceiveCountReport(invertedPrepStatusReport) ;
        LOGGER.log(Level.INFO, "{0}", nbTransmissionReport);
        LOGGER.info("plotCycleValue");
        plotEventsPerCycle("nbTransmissions", nbTransmissionReport) ;
    }
    
    /**
     * Plots the prevalence of STI among MSM with partnerCount new Relationships in the 
     * past backYears years.
     */
    public void plotSortPrevalence(int partnerCount, int backYear)
    {
        HashMap<Object,Number[]> prevalenceSortCount = new HashMap<Object,Number[]>() ;
        
        ArrayList<String> prevalenceSortReport = new ArrayList<String>() ;
        String prevalenceSortReportEntry ; // = new ArrayList<Object>() ;
        ArrayList<Double> prevalences = new ArrayList<Double>() ;
        ArrayList<Integer> populations = new ArrayList<Integer>() ;
        double populationRatio ; 
        //int population ;
                
        String prevalenceSortRecord ;
        double prevalence ;
        int totalPopulation = 0 ;
        
        String[] scoreNames = new String[] {"prevalence","nonprevalence"} ;
        
        prevalenceSortReport = reporter.prepareSortPrevalenceReport(partnerCount, backYear) ;
        for (int partnerIndex = 0 ; partnerIndex <= partnerCount ; partnerIndex++ )
        {
            prevalenceSortReportEntry = prevalenceSortReport.get(partnerIndex) ;
            if (!prevalenceSortReportEntry.isEmpty())
            {
                prevalences.add(Double.valueOf(Reporter.extractValue("prevalence", prevalenceSortReportEntry))) ;
                int population = Integer.valueOf(Reporter.extractValue("population", prevalenceSortReportEntry));
                populations.add(population) ;
            }
            else 
                break ;
        }
        
        totalPopulation = populations.get(0) ;
                
        // Normalise entries according to proportion of population with given number of previous partners
        // All agents with two or more new partners also had one or more, so we normalise by this figure.
        // Adjust number of loops according to when/whether previous loop executed break.
        for (int nbPartner = 1 ; nbPartner < populations.size() ; nbPartner++ )
        {
            int index = nbPartner - 1 ;
            // Normalise by this.
            populationRatio = ((double) populations.get(index))/totalPopulation ;
            
            prevalence = prevalences.get(index) ;
            prevalence = prevalence * populationRatio ;
            prevalenceSortCount.put(nbPartner, new Number[] {prevalence, populationRatio - prevalence}) ;
        }
        plotHashMapNumber("nbPartners",scoreNames, prevalenceSortCount) ;
    }
    
    /**
     * Plots the mean number of relationships entered by an Agent by a given age.
     */
    public void plotAgeNumberEnteredRelationshipRecord()
    {
        HashMap<Object,Double> ageNumberEnteredRelationshipRecord 
                = reporter.prepareAgeNumberEnteredRelationshipRecord() ;
        
        plotHashMapDouble("age","nbPartners", ageNumberEnteredRelationshipRecord ) ;
    }
    
}
