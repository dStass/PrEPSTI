/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter.presenter;

//import java.lang.reflect.* ;
import java.util.ArrayList ;
//import java.util.Arrays;
import java.util.HashMap ;
import java.util.logging.Level;

import reporter.Reporter ;
import reporter.SortReporter ;
//import reporter.EncounterReporter ;
import static reporter.presenter.Presenter.LOGGER;


/**
 *
 * @author MichaelWalker
 */
public class SortPresenter extends Presenter {
    
    private SortReporter reporter ;
    
    public static void main(String[] args) // "test1","encounter","population","fileName","test1","plotReceiveSortPrepStatusReport","false"
    {
        String simName = "NoPrepCalibration11Pop40000Cycles5000" ; // Community.NAME_ROOT ; // "testPlotCondomUsePop4000Cycles500" ; // args[0] ;
        String chartTitle = "incidence_past_year" ; // args[1] ;
        String reportFileName = "output/test/" ; // args[2] ;
        SortPresenter sortPresenter = new SortPresenter(simName,chartTitle,reportFileName,"encounter","relationship") ;
        sortPresenter.plotSortIncidence(5,1,1,0,0) ;    // nbRelationships, binSize, backYears, backMonths, backDays
        //SortPresenter sortPresenter = new SortPresenter(simName,chartTitle,reportFileName,"encounter","population") ;
        //sortPresenter.plotSortConcurrencyIncidence(3,2,0,0) ;    // nbRelationships, backYears, backMonths, backDays
        //SortPresenter sortPresenter = new SortPresenter(simName,chartTitle,reportFileName,"infection","relationship") ;
        //sortPresenter.plotSortPrevalence(5,1,1,0,0) ;    // nbRelationships, binSize, backYears, backMonths, backDays
        //SortPresenter sortPresenter = new SortPresenter(simName,chartTitle,reportFileName,"relationship","population") ;
        //sortPresenter.plotAgeNumberEnteredRelationshipRecord() ;
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
     * Plots the prevalence of STI among MSM with partnerCount new Relationships in the 
     * past backYears years.
     * @param partnerCount (int) the maximum number of new partners in the given time frame.
     * @param binSize (int) the range in partner number per bin.
     * @param backYear (int) the number of previous years to consider.
     * @param backMonths (int) plus the number of previous months to consider.
     * @param backDays (int) plus the number of previous days to consider.
     */
    public void plotSortPrevalence(int partnerCount, int binSize, int backYear, int backMonths, int backDays)
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
        int referencePopulation ;
        int population;
                
        String scoreName = "prevalence" ;
        int binNumber = (partnerCount + 1)/binSize ;
        if ((binNumber * binSize) < (partnerCount + 1))
            binNumber++ ;
        
        prevalenceSortReport = reporter.prepareSortPrevalenceReport(partnerCount, binSize, backYear, backMonths, backDays) ;
        for (int binIndex = 0 ; binIndex < prevalenceSortReport.size() ; binIndex++ )
        {
            prevalenceSortReportEntry = prevalenceSortReport.get(binIndex) ;
            if (!prevalenceSortReportEntry.isEmpty())
            {
                populations.add(Integer.valueOf(Reporter.extractValue("population", prevalenceSortReportEntry))) ;
                prevalences.add(Double.valueOf(Reporter.extractValue("prevalence", prevalenceSortReportEntry))) ;
            }
            else 
                break ;
        }
        
        referencePopulation = populations.get(0) ;    // Community.POPULATION ;
        
        // Normalise entries according to proportion of population with given number of previous partners
        // All agents with two or more new partners also had one or more, so we normalise by this figure.
        // Adjust number of loops according to when/whether previous loop executed break.
        String binLabel ;
        int totalDigits = ((int) Math.log10(partnerCount)) + 1 ;
        int nbDigits ;

        for (int binIndex = 0 ; binIndex < binNumber ; binIndex++ )
        {
            // Generate HashMap labels lowerCount-upperCount
            int nbPartners = binIndex * binSize ;
            
            // Pad with leading spaces for proper ordering
            if (nbPartners == 0)
                nbDigits = 1 ;
            else
                nbDigits = ((int) Math.log10(nbPartners)) + 1 ;
            binLabel = String.valueOf(nbPartners) ;
            for (int addSpace = nbDigits ; nbDigits < totalDigits ; nbDigits++ )
                binLabel = " ".concat(binLabel) ;
            
            if (binSize > 1)
            {
                if (binIndex == (binNumber - 1))    // Final bin
                {
                    if (partnerCount > nbPartners)    // more than last value in bin
                        binLabel += "-".concat(String.valueOf(partnerCount)) ;
                }
                else
                    binLabel += "-".concat(String.valueOf(nbPartners + binSize - 1)) ;
            }
            
            // Normalise by this.
            populationRatio = ((double) populations.get(binIndex))/referencePopulation ;
            
            prevalence = prevalences.get(binIndex) ;
            //prevalence = prevalence * populationRatio ;
            prevalenceSortCount.put(binLabel, new Number[] {populationRatio, prevalence}) ;
        }
        plotHashMapArea("nbPartners",scoreName, prevalenceSortCount) ;
    }
    
    /**
     * Plots the incidence of STI among MSM with partnerCount new Relationships in the 
     * past backYears years, backMonths months and backDays days.
     * @param partnerCount (int) the maximum number of new partners in the given time frame.
     * @param binSize (int) the range in partner number per bin.
     * @param backYears (int) the number of previous years to consider.
     * @param backMonths (int) plus the number of previous months to consider.
     * @param backDays (int) plus the number of previous days to consider.
     */
    public void plotSortIncidence(int partnerCount, int binSize, int backYears, int backMonths, int backDays)
    {
        HashMap<Object,Number[]> sortIncidenceReport = reporter.prepareSortIncidenceReport(partnerCount, binSize, backYears, backMonths, backDays) ;
        String scoreName = "incidence" ;
        
        plotHashMapArea("nbPartners",scoreName, sortIncidenceReport) ;
    }
    
    /**
     * Plots the incidence of STI among MSM with partnerCount new Relationships in the 
     * past backYears years, backMonths months and backDays days.
     * @param partnerCount (int) the maximum number of new partners in the given time frame.
     * @param binSize (int) the range in partner number per bin.
     * @param backYears (int) the number of previous years to consider.
     * @param backMonths (int) plus the number of previous months to consider.
     * @param backDays (int) plus the number of previous days to consider.
     */
    public void plotSortConcurrencyIncidence(int partnerCount, int backYears, int backMonths, int backDays)
    {
        HashMap<Object,Number[]> sortConcurrencyIncidenceReport = reporter.prepareSortConcurrencyIncidenceReport(partnerCount, backYears, backMonths, backDays) ;
        String scoreName = "incidence" ;
        LOGGER.log(Level.INFO, "{0}", sortConcurrencyIncidenceReport);
        plotHashMapArea("concurrency",scoreName, sortConcurrencyIncidenceReport) ;
    }
    
    /**
     * Plots the mean number of relationships entered by an Agent by a given age.
     */
    public void plotAgeNumberEnteredRelationshipRecord()
    {
        HashMap<Object,Number> ageNumberEnteredRelationshipRecord 
                = reporter.prepareAgeNumberEnteredRelationshipRecord() ;
        
        plotHashMap("age","nbPartners", regularBinHashMap(ageNumberEnteredRelationshipRecord,"nbPartners",5) ) ;
    }
    
}
