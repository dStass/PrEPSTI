/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PRSP.PrEPSTI.reporter.presenter;

import PRSP.PrEPSTI.configloader.ConfigLoader;

import java.util.ArrayList ;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Collections;

//import community.Community ;
//import java.util.Arrays;
import java.util.logging.Level;
import PRSP.PrEPSTI.reporter.Reporter ;
import static PRSP.PrEPSTI.reporter.Reporter.MULTI_WRITE_CSV;
import PRSP.PrEPSTI.reporter.ScreeningReporter ;
import static PRSP.PrEPSTI.reporter.presenter.Presenter.LOGGER;

/**
 *
 * @author Michael Walker
 */
public class ScreeningPresenter extends Presenter {
    
    private ScreeningReporter reporter ; 
    
    static public String INCIDENCE = "incidence" ;
    static public String PREVALENCE = "prevalence" ;
    static public String COPREVALENCE = "coprevalence" ;
    static public String SYMPTOMATIC = "symptomatic" ;
    static public String URETHRA = "Urethra" ;
    static public String RECTUM = "Rectum" ;
    static public String PHARYNX = "Pharynx" ;
    static public String ALL = "all" ;
    static public String NOTIFICATION = "notification" ;  // "treated" ;
    static public String POSITIVITY = "positivity" ;  // "treated" ;
    
    public ScreeningPresenter() {
        super();
    }

    public ScreeningPresenter(String applicationTitle, String chartTitle)
    {
        super(applicationTitle, chartTitle);
    }
    
    public ScreeningPresenter(String applicationTitle, String chartTitle, ScreeningReporter reporter)
    {
        super(applicationTitle,chartTitle,reporter) ;
        setReporter(reporter) ;
    }
    
    public ScreeningPresenter(String simName, String chartTitle, String reportFilePath)
    {
        super(simName,chartTitle) ;
        setReporter(new ScreeningReporter(simName,reportFilePath)) ;
    }
    
    
    
    /**
     * Overrides super.setReporter() because reporter is now PopulationReporter
     * @param reporter 
     */
    public void setReporter(ScreeningReporter reporter)
    {
        this.reporter = reporter ;
    }

    // static String[] simNames = new String[] {"REPORT_APop2500Cycles2920", "REPORT_BPop2500Cycles2920", "REPORT_CPop2500Cycles2920", "REPORT_DPop2500Cycles2920", "REPORT_EPop2500Cycles2920", "REPORT_FPop2500Cycles2920"} ; //, "to2017newSort17baPop40000Cycles5110","to2017newSort17caPop40000Cycles5110","to2017newSort17daPop40000Cycles5110","to2017newSort17eaPop40000Cycles5110",
    static String[] simNames = new String[] {"REPORT_A", "REPORT_B", "REPORT_C", "REPORT_D", "REPORT_E", "REPORT_F", "REPORT_G", "REPORT_H", "REPORT_I", "REPORT_J"};
    // static String[] simNames = new String[] {"REPORT_A", "REPORT_B", "REPORT_C"};


    
    //      "to2017newSort17faPop40000Cycles5110", "to2017newSort17gaPop40000Cycles5110","to2017newSort17haPop40000Cycles5110","to2017newSort17iaPop40000Cycles5110","to2017newSort17jaPop40000Cycles5110"} ;
            //"from2007seek57fPop40000Cycles5475","from2007seek57gPop40000Cycles5475","from2007seek57hPop40000Cycles5475","from2007seek57iPop40000Cycles5475","from2007seek57jPop40000Cycles5475"} ;
    //static String[] simNames = new String[] {"newSortaPop40000Cycles1825","seek68bPop40000Cycles1825","seek68cPop40000Cycles1825","seek68dPop40000Cycles1825"} ; // ,"seek53ePop40000Cycles1825",
      //      "seek53fPop40000Cycles1825","seek53gPop40000Cycles1825","seek53hPop40000Cycles1825","seek53iPop40000Cycles1825","seek53jPop40000Cycles1825"} ;
    // static String[] testSimNames = new String[] {"riskyIncidence_all_from2020to2025prep0p5cycleParams33aaPop40000Cycles2190", "riskyIncidence_all_from2020to2025prep1p0cycleParams33aaPop40000Cycles2190", "riskyIncidence_all_from2020to2025prep0p75cycleParams33aaPop40000Cycles2190" };
    static String[] testSimNames = new String[] {"csv1", "csv2", "csv3"};
    
    public static void main(String[] args)
    {

        ConfigLoader.load();
        
        // String prefix = "to2025UeqUto2019oldParams33" ;
        // String suffix = "Pop40000Cycles2190" ;
        // ArrayList<String> simNameList = new ArrayList<String>() ;
        // String letter0 = "" ;
        // //for (String letter0 : new String[] {"a","b","c","d","e","f","g","h","i","j"})
        //     for (String letter1: new String[] {"Ebi"}) //,"Dbc","Eje","Hji","Jce","Iae","Iad","Cfi","Fjc","Chh","Bci","Dhj","Bhi","Ibe","Keg","Kjc","Kbh","Fag","Jad","Bfd","Idg","Keh","Ggb","Dee","Ghh","Dac","Dgd","Fab","Hdh","Ibg","Fcc","Ghd","Hfa","Fci","Ifd","Gfd","Hje","Eei","Hhb","Aah","Gdh","Bjh","Cbf","Dcg","Ifc","Kej","Ajc","Fii","Hfb","Cdd"} ;
        //         simNameList.add(prefix + letter0 + letter1 + suffix) ;

        // String folderPath = "/scratch/is14/mw7704/prepsti/output/to2025/" ;

        //         String prefix = "old1p5Params26" ;
        //         //String prefix = "to2019fix23" ;
        //         String suffix = "Pop40000Cycles6570" ;
        //         ArrayList<String> simNameList = new ArrayList<String>() ;
        //         simNameList.add("rebootPop20000Cycles1825") ;
        //         //String letter0 = "" ;
        // //        for (String letter0 : new String[] {"a","b","c","d","e","f","g","h","i","j"})
        // //            for (String letter1 : new String[] {"a","b","c","d","e"})
        // //                simNameList.add(prefix + letter0 + letter1 + suffix) ;
        
        //         simNames = simNameList.toArray(new String[] {}) ;
        
        //String simName = "rebootPop20000Cycles1825" ;
        //String simName = "Qibin1p0aPop40000Cycles1460" ;
        //String simName = "to2017newSort17aaPop40000Cycles5110" ;
        //String simName = "to2012max3sameScreen34cPop40000Cycles4380" ;
        //String simName = "to2014agentAdjust29aPop40000Cycles4920" ;
        String simName = simNames[0] ;
        
        boolean unique = false ;
        int notifications = 1 ; 
        String chartTitle = "" ;
        if (unique && (notifications == 1))
        chartTitle = "unique " ;
        if (notifications == 0)
        {
            chartTitle += "notification-rate" ;
            //chartTitle += "condom use scaledown in 2009" ;
        } //
        else if (notifications == 1)
        chartTitle += "incidence" ;
        else
        {
            //chartTitle += "screening rate"
            //chartTitle += "testing rate"
            //chartTitle += "mean_prevalence" 
            chartTitle += "multi-site prevalence" 
            //chartTitle += "multi-site symptomatic"
            
            + "" ;
        }
        //String chartTitle = "proportion_symptomatic" ;
        //String chartTitle = "site-specific symptomatic" ; // args[1] ;
        //String chartTitle = "testing_6_months" ; // args[1] ;
        //String chartTitle = "infections_past_2years_PrEP" ; // args[1] ;
        //String reportFileName = "output/untouchable/" ; // args[2] ;
        //String reportFileName = "output/prep/" ; // args[2] ;
        //String reportFileName = "output/prePrEP/" ; // args[2] ;
        //String reportFileName = "output/to2025/" ; // args[2] ;
        //String reportFileName = "output/year2012/" ; // args[2] ;
        //String reportFileName = "output/year2010/" ; // args[2] ;
        //String reportFileName = "output/year2007/" ; // args[2] ;
        // String reportFileName = folderPath ;  
        
        
        String reportFileName = "output/" ; // args[2] ;
        // reportFileName = "reports/";
        ScreeningPresenter screeningPresenter = new ScreeningPresenter(simName,chartTitle,reportFileName) ;

        // // set information of plots based on whether we are plotting years
        // if (ScreeningPresenter.PLOT_YEARS) {
        //     screeningPresenter.setDrawPoints(true);
        //     screeningPresenter.setDrawError(true);
        // }

        // // set graphical info for plotting days
        // else {
        //     screeningPresenter.setDrawPoints(false);
        //     screeningPresenter.setDrawError(false);
        // }


        //Reporter.MULTI_WRITE_CSV(simNameList, "year", "Urethra_true", "riskyIncidence", screeningPresenter.reporter.getFolderPath()) ;
        
        String[] siteNames  = new String[] {"Pharynx","Rectum","Urethra"} ;
        //String[] testArray[] = Arrays.asList(siteNames).subList(0,0)
        
        
        //String[] simNames = new String[] {"to2014max3contactII34aPop40000Cycles5920","to2014max3contactII34bPop40000Cycles5920","to2014max3contactII34cPop40000Cycles5920","to2014max3contactII34dPop40000Cycles5920","to2014max3contactII34ePop40000Cycles5920","to2014max3contactII34fPop40000Cycles5920","to2014max3contactII34gPop40000Cycles5920","to2014max3contactII34hPop40000Cycles5920","to2014max3contactII34iPop40000Cycles5920","to2014max3contactII34jPop40000Cycles5920"} ;
        //String[] simNames = new String[] {"max3contacts54ePop40000Cycles1000","max3contacts54fPop40000Cycles1000"} ; // "max3contact54aPop40000Cycles4000","max3contact54bPop40000Cycles4000","max3contact54cPop40000Cycles4000","max3contact54dPop40000Cycles4000","max3contact34gPop40000Cycles4000","max3contact34hPop40000Cycles4000","max3contact34iPop40000Cycles4000","max3contact34jPop40000Cycles4000"} ;
            //"to2014agentScreen29hPop40000Cycles4920","to2014agentScreen29iPop40000Cycles4920","to2014agentScreen29jPop40000Cycles4920"} ;
        //String[] simNames = new String[] {"max3contacts13bPop40000Cycles1000","max3contacts13aPop40000Cycles1250"} ;
        
        //screeningPresenter.coplotPrevalence(simNames) ;
        //screeningPresenter.plotSiteMeanPrevalence(siteNames,simNames) ;
        
        // screeningPresenter.plotNumberAgentTestingReport(0, 6, 0) ;
        //screeningPresenter.plotNumberAgentTreatedReport(2, 0, 0,"prepStatus",5) ;

        //screeningPresenter.multiPlotScreening(new Object[] {"prevalence","coprevalence",new String[] {"Pharynx","Rectum"},new String[] {"Urethra","Rectum"},"prevalence",new String[] {"Pharynx","Rectum","Urethra"}});
        //screeningPresenter.multiPlotScreening(new Object[] {"prevalence","prevalence",new String[] {"Pharynx","Rectum","Urethra"}});
        //screeningPresenter.plotIncidencePerCycle(siteNames) ;
        // screeningPresenter.plotFinalAtRiskIncidentsRecord(siteNames,0,"statusHIV") ;

        // screeningPresenter.plotYearsAtRiskIncidenceReport(siteNames, 5, 2019, "statusHIV") ;  // !! line chart here

        // screeningPresenter.plotYearsBeenTestedReport(13, 0, 0, 2019, "statusHIV") ;
        //screeningPresenter.plotNotificationsPerCycle(siteNames) ;
        // screeningPresenter.plotSitePrevalence(siteNames) ;
        // screeningPresenter.plotSiteSymptomPrevalence(siteNames) ;
        // screeningPresenter.plotPrevalence(siteNames) ;  // points on plot may not make sense here?
        // screeningPresenter.plotPrevalenceYears(siteNames,5,2025) ;
        // screeningPresenter.plotSortedPrevalenceYears(siteNames, 2 ,2019,"statusHIV") ;
        // screeningPresenter.plotFinalSymptomatic(new String[] {"Pharynx","Rectum","Urethra"}) ;
        //screeningPresenter.plotFinalPrevalences(new String[] {"Pharynx","Rectum","Urethra"}) ;
        //screeningPresenter.plotSortedFinalPrevalences(new String[] {"Pharynx","Rectum","Urethra"}, "statusHIV") ;
        //screeningPresenter.plotFinalNotifications(new String[] {"Pharynx","Rectum","Urethra"}, unique, 0, Reporter.DAYS_PER_YEAR, notifications) ;
        //screeningPresenter.plotSortedFinalNotifications(new String[] {"Pharynx","Rectum","Urethra"}, unique, 0, 0, Reporter.DAYS_PER_YEAR, "statusHIV") ; 
        // screeningPresenter.plotSortedNotificationsYears(siteNames, unique, 2, 2019, "statusHIV") ;
        //screeningPresenter.plotNotificationsYears(siteNames,13,2019) ;   
        //screeningPresenter.plotPositivityYears(siteNames, unique, 8, 2014) ;
        //screeningPresenter.plotNotificationPerCycle() ;    
        // screeningPresenter.plotSiteProportionSymptomatic(siteNames) ;


        screeningPresenter.plotIntervalMeansFromCSVFileNames(testSimNames);


        //String methodName = args[3] ;
        //Method method = EncounterPresenter.class.getMethod(methodName) ;

        //method.invoke(encounterPresenter, (Object[]) Arrays.copyOfRange(args,4,args.length)) ;

        
        
    }
    
    /**
     * Plots year-by-year report with Agents sorted according to sortingProperty.
     * @param siteNames
     * @param unique
     * @param backYears
     * @param lastYear
     * @param sortingProperty 
     */
    public void plotSortedNotificationsYears(String[] siteNames, boolean unique, int backYears, int lastYear, String sortingProperty) {
        
        // set new title
        this.chartTitle = "Sorted Notifications Years";
        
        String[] siteAllNames = new String[siteNames.length+1] ;
        siteAllNames[0] = "all" ;
        System.arraycopy(siteNames, 0, siteAllNames, 1, siteNames.length);
        ArrayList<HashMap<Comparable,String>> reports = new ArrayList<HashMap<Comparable,String>>() ;
        
        // Get sorted Report
        for (String simulation: simNames)
        {
            ScreeningReporter screeningReporter = new ScreeningReporter(simulation,reporter.getFolderPath()) ;
            HashMap<Comparable,String> report = screeningReporter.prepareSortedYearsNotificationsRecord(siteNames, backYears, lastYear, sortingProperty) ;
            reports.add((HashMap<Comparable,String>) report.clone()) ;
            Reporter.CLEAR_REPORT_LIST();
            Reporter.DUMP_OUTPUT(GENERATE_SORTED_LABEL("notification",sortingProperty),simulation,reporter.getFolderPath(),report);
        }
        HashMap<Comparable,String> sortedNotificationsYears //= reports.get(0) ;
                = Reporter.PREPARE_MEAN_HASHMAP_REPORT(reports) ;
        
        // Generate legend and multiPlotNames
        String[] legend = new String[siteAllNames.length * sortedNotificationsYears.size()] ;
        ArrayList<String> multiPlotNames = new ArrayList<String>() ;
        int legendIndex = 0 ;
        for (Object sortingValue : sortedNotificationsYears.keySet())
        {
            multiPlotNames.add(sortingValue.toString()) ;
            for (String site : siteAllNames)
            {
                legend[legendIndex] = site.concat(GROUP).concat(sortingValue.toString()) ;
                legendIndex++ ;
            }
        }
        
        String title = NOTIFICATION ;
        if (!sortingProperty.isEmpty())
            title += "_" + sortingProperty ;

        Reporter.WRITE_CSV_STRING(sortedNotificationsYears, "year", title, simNames[0], Reporter.REPORT_FOLDER) ;
        plotHashMapString(sortedNotificationsYears,title,"year",legend) ;
    }
    
    /**
     * Plots bar chart showing incidence at each Site for each of the last backYears
     * years counting back from lastYear.
     * @param siteNames
     * @param backYears
     * @param lastYear 
     */
    public void plotNotificationsYears(String[] siteNames, int backYears, int lastYear) {
        
        // set new title
        this.chartTitle = "Notifications Years";
        
        //HashMap<Object,Number[]> notificationsRecordYears = reporter.prepareYearsNotificationsRecord(siteNames, backYears, lastYear) ;
        HashMap<Comparable,String> notificationsYearsPlot ; //= new HashMap<Object,String>() ;
        
        String[] siteAllNames = new String[siteNames.length+1] ;
        siteAllNames[0] = "all" ;
        // = Arrays.copyOf(siteNames, siteNames.length + 1) ;
        System.arraycopy(siteNames, 0, siteAllNames, 1, siteNames.length);
        
        ArrayList<HashMap<Comparable,String>> reports = new ArrayList<HashMap<Comparable,String>>() ;
        for (String simulation : simNames)
        {
            ScreeningReporter screeningReporter = new ScreeningReporter(simulation,reporter.getFolderPath()) ;
            reports.add(screeningReporter.prepareYearsNotificationsRecord(siteNames, backYears, lastYear)) ;
        }
        notificationsYearsPlot = Reporter.PREPARE_MEAN_HASHMAP_REPORT(reports, "year", NOTIFICATION, simNames[0]) ;
        //notificationsRecordYears = Reporter.AVERAGED_HASHMAP_REPORT(reports) ;
        
        //LOGGER.log(Level.INFO, "{0},{1},{2}", new Object[] {notificationsRecordYears.get(2007)[0],notificationsRecordYears.get(2007)[1],notificationsRecordYears.get(2007)[2]});
        /**
        // Put report in appropriate, text-based form
        String yearlyEntry = "" ;
        int firstYear = lastYear - backYears + 1 ;
        for (int year = firstYear ; year <= lastYear ; year++)
        {
            //Number[] notifications = notificationsRecordYears.get(year) ;
            for (int siteIndex = 0 ; siteIndex < siteAllNames.length ; siteIndex++ )
            {
                String siteName = siteAllNames[siteIndex] ;
                yearlyEntry += Reporter.ADD_REPORT_PROPERTY(siteName, notifications[siteIndex]) ;
            }
            notificationsYearsPlot.put(year,yearlyEntry) ;
            // Reset for next year
            yearlyEntry = "" ;
        }*/
        
        plotHashMapString(notificationsYearsPlot,NOTIFICATION,"year",siteAllNames) ;
        //plotHashMap("Year", siteAllNames, notificationsRecordYears) ;
    }
    
    public void plotYearsTestingRateReport(int backYears, int backMonths, int backDays, int lastYear, String sortingProperty){
        
        // set new title
        this.chartTitle = "Years Testing Rate Report";
        
        ArrayList<HashMap<Comparable,String>> reports = new ArrayList<HashMap<Comparable,String>>() ;
        String reportOutput ;
        String label ;
        
        for (String simulation : simNames)
        {
            ScreeningReporter screeningReporter = new ScreeningReporter(simulation,reporter.getFolderPath()) ;
            HashMap<Comparable,HashMap<Object,Number>> numberReport = screeningReporter.prepareYearsTestingRateReport(backYears, lastYear, sortingProperty) ;
            ArrayList<Comparable> sortedYears = new ArrayList<Comparable>(numberReport.keySet()) ; 
            Collections.sort(sortedYears) ;
            HashMap<Comparable,String> stringReport = new HashMap<Comparable,String>() ;
            for (Comparable year : numberReport.keySet())
            {
                reportOutput = "" ;
                HashMap<Object,Number> valueReport = numberReport.get(year) ;
                for (Object sortingValue : valueReport.keySet())
                {
                    label = "test-rate" ;
                    if (!"".equals(sortingValue))
                        label += "__" + sortingValue.toString() ;
                    reportOutput += Reporter.ADD_REPORT_PROPERTY(label,valueReport.get(sortingValue)) ;
                }
                stringReport.put(year, reportOutput) ;
            }
            
            Reporter.CLEAR_REPORT_LIST() ; 
            reports.add((HashMap<Comparable,String>) stringReport.clone()) ;
            Reporter.DUMP_OUTPUT(GENERATE_SORTED_LABEL("test-rate",sortingProperty),simulation,reporter.getFolderPath(),stringReport);
        }
        
        HashMap<Comparable,String> yearsBeenTestedReport = Reporter.PREPARE_MEAN_HASHMAP_REPORT(reports,"year","test-rate",simNames[0]) ;
        
        String yLabel = "Test-rate in last " + GET_TIME_PERIOD_STRING(backYears, backMonths, backDays) ;
//        
        plotHashMapString(yearsBeenTestedReport,yLabel,"year",new String[] {""}) ;
    }
    
    
    /**
     * Plots bar chart showing incidence at each Site for each of the last backYears
     * years counting back from lastYear.
     * @param siteNames
     * @param unique
     * @param backYears
     * @param lastYear 
     */
    public void plotPositivityYears(String[] siteNames, boolean unique, int backYears, int lastYear) {
        
        // set new title
        this.chartTitle = "Positivity Years";
        
        HashMap<Comparable,String> positivityRecordYears = reporter.prepareYearsPositivityRecord(siteNames, unique, backYears, lastYear) ;    // Number[]
        
        String[] siteAllNames = new String[siteNames.length+1] ;
        siteAllNames[0] = "all" ;
        // = Arrays.copyOf(siteNames, siteNames.length + 1) ;
        System.arraycopy(siteNames, 0, siteAllNames, 1, siteNames.length);
        
        plotHashMapString(positivityRecordYears, "Notification-rate", "Year", siteAllNames) ;
    }
    
    /**
     * Plots bar chart showing notifications of all siteNames and total incidence.
     * @param outcome
     */
    public void plotFinalNotifications(int outcome)
    {
        //ArrayList<String> sitesList = Arrays(siteNames) ;
        
        plotFinalNotifications(new String[] {"Pharynx","Rectum","Urethra"}, false, 6, 0, outcome) ;
    }
    
    /**
     * Plots bar chart showing incidence of requested siteNames and total 
     * outcome == 0 : incidence,
     * outcome == 1 : positivity
     * @param siteNames 
     * @param unique 
     * @param backMonths 
     * @param backDays 
     * @param outcome 
     */
    public void plotFinalNotifications(String[] siteNames, boolean unique, int backMonths, int backDays, int outcome) {
        
        // set new title
        this.chartTitle = "Final Notifications";
        
        // [0] for positivity
        //HashMap<Object,Number> finalNotificationsRecord = new HashMap<Object,Number>() ;
        String finalNotificationsRecord ; // = "" ;
        String meanNotificationsRecord ;
        //HashMap<Object,Number[]> notificationsRecord = reporter.prepareFinalNotificationsRecord(siteNames, unique, backMonths, backDays) ;
        String scoreName = "" ;
        
        ArrayList<String> reports = new ArrayList<String>() ;
        for (String simulation : simNames)
        {
            ScreeningReporter screeningReporter = new ScreeningReporter(simulation,reporter.getFolderPath()) ;
            String record =screeningReporter.prepareFinalNotificationsRecord(siteNames, unique, backMonths, backDays) ;
            int posIndex = record.indexOf(POSITIVITY) ;
            if (outcome == 0)
            {
                int notLength = NOTIFICATION.length() + 1 ;
                finalNotificationsRecord = record.substring(notLength, posIndex) ;
                scoreName = NOTIFICATION ;
            }
            else //    outcome == 1
            {
                int posLength = POSITIVITY.length() + 1 ;
                finalNotificationsRecord = record.substring(posIndex + posLength) ;
                scoreName = POSITIVITY ;
            }
            reports.add(finalNotificationsRecord) ;
        }
      // logger.log(level.info, "{0}", reports);
        meanNotificationsRecord = Reporter.PREPARE_MEAN_REPORT(reports) ;
        
        
        /**
        for (String property : Reporter.IDENTIFY_PROPERTIES(notificationsRecord))
        {
            finalNotificationsRecord.put(property, Double.valueOf(Reporter.EXTRACT_VALUE(property, notificationsRecord))) ;
            TODO: sEPARATE NOTIFICATION FROM POSITIVITY ;
        }
      // logger.log(level.info, "{0}", finalNotificationsRecord); */
        //String[] yLabels = new String[] {"incidence","positivity"} ;
        // LOGGER.info(meanNotificationsRecord);
        multiPlotValues(meanNotificationsRecord,scoreName,"Site") ;
        //plotHashMap("Sites",yLabels[outcome],finalNotificationsRecord) ;        
    }
    
    /**
     * Plots site-specific notifications for different values of sortingProperty
     * @param siteNames
     * @param unique
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param sortingProperty 
     */
    public void plotSortedFinalNotifications(String[] siteNames, boolean unique, int backYears, int backMonths, int backDays, String sortingProperty) {
        
        // set new title
        this.chartTitle = "Sorted Final Notifications";
        
        String finalNotificationsRecord = "" ;
        HashMap<Object,String> notificationsRecord = reporter.prepareSortedFinalNotificationsRecord(siteNames, unique, backYears, backMonths, backDays, 0, sortingProperty) ;
      // logger.log(level.info, "{0}", notificationsRecord);
        //HashMap<Object,Number[]> finalNotificationsRecord = new HashMap<Object,Number[]>() ;
        //HashMap<Object,HashMap<Object,Number>> notificationsRecord = reporter.prepareSortedFinalNotificationsRecord(siteNames, unique, backYears, backMonths, backDays, 0, sortingProperty) ;
        
        int nbSortingValues = notificationsRecord.keySet().size() ;
        String[] scoreNames = new String[nbSortingValues] ;
        ArrayList<String> scoreNamesList = new ArrayList<String>() ;
        
        ArrayList<String> propertiesList = Reporter.IDENTIFY_PROPERTIES(notificationsRecord.values().iterator().next()) ;
        String newSiteName ;
        String siteValue ;
        for (String siteName : propertiesList )
        {
            // LOGGER.info(siteName);
            //String sortedNotifications = "" ; // Reporter.ADD_REPORT_LABEL(siteName) ; // new Number[nbSortingValues] ;
            //Number[] sortedNotifications = new Number[nbSortingValues] ;
            int sortedIndex = 0 ;
            boolean scoreNamesConstructed = !scoreNamesList.isEmpty() ;
            //for (int sortedIndex = 0 ; sortedIndex < nbSortingValues ; sortedIndex++ )
            for (Object sortingValue : notificationsRecord.keySet())
            {
                // Combine siteName with sorting value
                newSiteName = siteName.concat(GROUP).concat(sortingValue.toString()) ;
                siteValue = Reporter.EXTRACT_VALUE(siteName, notificationsRecord.get(sortingValue)) ;
                finalNotificationsRecord += Reporter.ADD_REPORT_PROPERTY(newSiteName, siteValue) ;
                if (!scoreNamesConstructed)
                {
                    scoreNames[sortedIndex] = String.valueOf(sortingValue).concat(GROUP).concat(sortingValue.toString()) ; // concat(sortingProperty) ; // 
                    //scoreNamesList.add(String.valueOf(sortingValue)) ;
                }
                sortedIndex++ ;
            }
            //finalNotificationsRecord.put(key, sortedNotifications) ;
        }
        ArrayList<String> legend = Reporter.IDENTIFY_PROPERTIES(finalNotificationsRecord) ;
      // logger.log(level.info, "{0}", finalNotificationsRecord);
        multiPlotValues(finalNotificationsRecord,NOTIFICATION,"Sites") ;
        //plotHashMap("Sites",scoreNames,finalNotificationsRecord) ;        
        
        /**
        HashMap<Object,HashMap<Object,Number>> sortedFinalNotificationsRecord = reporter.prepareSortedFinalNotificationsRecord(siteNames, unique, backYears, backMonths, backDays, 0, sortingProperty) ;
        HashMap<Object,Number> finalNotificationsRecord = new HashMap<Object,Number>() ;
        ArrayList<ArrayList<Object>> sortedFinalNotificationsReport = new ArrayList<ArrayList<Object>>() ;
        
        String[] allSiteNames = new String[siteNames.length + 1] ;
        for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
            allSiteNames[siteIndex] = siteNames[siteIndex] ;
        allSiteNames[siteNames.length] = "all" ;
        
        ArrayList<String> scoreNames = new ArrayList<String>() ;
        ArrayList<String> legendList = new ArrayList<String>() ; 
        int legendLength = (siteNames.length + 1) * sortedFinalNotificationsRecord.keySet().size() ;
      // logger.log(level.info,"{0} {1}", new Object[] {sortedFinalNotificationsRecord.keySet(),String.valueOf(legendLength)});
        
        for (Object sortingValue : sortedFinalNotificationsRecord.keySet())
        {
            ArrayList<Object> notificationRecord = new ArrayList<Object>() ;
            finalNotificationsRecord = sortedFinalNotificationsRecord.get(sortingValue) ;
            legendList.add(String.valueOf(sortingValue)) ;
            boolean scoreNamesConstructedYet = scoreNames.isEmpty() ;
            for (int siteIndex = 0 ; siteIndex < allSiteNames.length ; siteIndex++ )
            {
                notificationRecord.add(finalNotificationsRecord.get(allSiteNames[siteIndex])) ;
                if (!scoreNamesConstructedYet)
                    scoreNames.add(allSiteNames[siteIndex]) ;
                //legend[legendBaseIndex + siteIndex] = String.valueOf(sortingValue) + "_" + allSiteNames[siteIndex] ;
            }
            sortedFinalNotificationsReport.add((ArrayList<Object>) notificationRecord.clone()) ;
        }
        String[] legend = legendList.toArray(new String[legendList.size()]) ; // new String[legendLength] ;
      // logger.log(level.info,"{2} {0} {1}", new Object[] {legendList,sortedFinalNotificationsReport,legend.length}) ;
        multiPlotCycleValue(legendList,sortedFinalNotificationsReport,legend) ;
        * */
    }
    
    /**
     * Plots bar chart showing incidence at each Site for each of the last backYears
     * years counting back from lastYear.
     * @param siteNames
     * @param backYears
     * @param lastYear 
     * @param sortingProperty 
     */
    public void plotSortedPrevalenceYears(String[] siteNames, int backYears, int lastYear, String sortingProperty) {
        
        // set new title
        this.chartTitle = "Sorted Prevalence Years";
        
        ArrayList<HashMap<Comparable,String>> reports = new ArrayList<HashMap<Comparable,String>>() ;
        for (String simulation: simNames)
        {
            ScreeningReporter screeningReporter = new ScreeningReporter(simulation,reporter.getFolderPath()) ;
            HashMap<Comparable,String> report = screeningReporter.prepareYearsPrevalenceRecord(siteNames, backYears, lastYear, sortingProperty) ;
            reports.add((HashMap<Comparable,String>) report.clone()) ;
            Reporter.CLEAR_REPORT_LIST();
            Reporter.DUMP_OUTPUT(GENERATE_SORTED_LABEL("prevalence",sortingProperty),simulation,reporter.getFolderPath(),report);
        }
        HashMap<Comparable,String> prevalenceRecordYears = Reporter.PREPARE_MEAN_HASHMAP_REPORT(reports) ;
        String[] siteAllNames = (String[]) Reporter.IDENTIFY_PROPERTIES(prevalenceRecordYears.get(lastYear)).toArray(new String[0]) ; // Arrays.copyOf(siteNames, siteNames.length + 1) ;
      // logger.log(level.info, "{0}", prevalenceRecordYears.get(lastYear));
        plotHashMapString(prevalenceRecordYears,PREVALENCE,"Year", siteAllNames) ;
    }
    
    /**
     * Plots bar chart showing incidence at each Site for each of the last backYears
     * years counting back from lastYear.
     * @param siteNames
     * @param backYears
     * @param lastYear 
     */
    public void plotPrevalenceYears(String[] siteNames, int backYears, int lastYear) {
        
        // set new title
        this.chartTitle = "Prevalence Years";
        
        //HashMap<Object,Number[]> 
        HashMap<Comparable,String>  prevalenceRecordYears = reporter.prepareYearsPrevalenceRecord(siteNames, backYears, lastYear, "") ;
        String[] siteAllNames = Arrays.copyOf(siteNames, siteNames.length + 1) ;
        siteAllNames[siteNames.length] = "all" ;
      // logger.log(level.info, "{0}", prevalenceRecordYears);
        plotHashMapString(prevalenceRecordYears,PREVALENCE,"Year", siteAllNames) ;
    }
    
    /**
     * Plots bar chart showing prevalence of all siteNames and total prevalence.
     */
    public void plotFinalPrevalences()
    {
        //String siteNames = reporter.getMetaDatum("Community.SITE_NAMES") ;
        //ArrayList<String> sitesList = Arrays(siteNames) ;
        
        plotFinalPrevalences(new String[] {"Pharynx","Rectum","Urethra"}) ;
    }
    
    /**
     * Plots bar chart showing prevalence of requested siteNames and total prevalence.
     * @param siteNames 
     */
    public void plotFinalPrevalences(String[] siteNames){
        
        // set new title
        this.chartTitle = "Final Prevalences";
        
        //HashMap<Object,Number> 
        String finalPrevalencesRecord = reporter.prepareFinalPrevalencesRecord(siteNames) ;
        // LOGGER.info(finalPrevalencesRecord);
        
        plotValues(PREVALENCE,finalPrevalencesRecord) ;        
    }
    
    /**
     * Plots bar chart showing prevalence of requested siteNames and total prevalence.
     * @param siteNames 
     * @param sortingProperty 
     */
    public void plotSortedFinalPrevalences(String[] siteNames, String sortingProperty) {
        
        // set new title
        this.chartTitle = "Sorted Final Prevalences";
        
        //HashMap<Object,Number> 
        String finalPrevalencesRecord = reporter.prepareFinalPrevalencesSortedRecord(siteNames, sortingProperty) ;
        
      // logger.log(level.info, "{0}", finalPrevalencesRecord) ;
                
        //plotValues(PREVALENCE,finalPrevalencesRecord) ;        
        multiPlotValues(finalPrevalencesRecord,PREVALENCE,"Sites") ;
    }
    
    /**
     * Plots bar chart showing prevalence of all siteNames and total prevalence.
     */
    public void plotFinalSymptomatic(){
        
        //String siteNames = reporter.getMetaDatum("Community.SITE_NAMES") ;
        //ArrayList<String> sitesList = Arrays(siteNames) ;
        
        plotFinalSymptomatic(new String[] {"Pharynx","Rectum","Urethra"}) ;
    }
    
    /**
     * Plots bar chart showing symptomatic prevalence of requested siteNames and in total.
     * @param siteNames 
     */
    public void plotFinalSymptomatic(String[] siteNames) {
        
        // set new title
        this.chartTitle = "Final Symptomatic";

        HashMap<Comparable,Number> finalSymptomaticRecord = reporter.prepareFinalSymptomaticRecord(siteNames) ;
        
        
        plotHashMap("Sites","symptomatic",finalSymptomaticRecord) ;        
    }
    
    /**
     * Plots the population prevalence of STI over time (cycles).
     */
    public void plotPrevalence()
    {
        ArrayList<String> prevalenceReport = reporter.preparePrevalenceReport() ;
        //LOGGER.log(Level.INFO, "{0}", prevalenceReport);
        plotCycleValue("prevalence", prevalenceReport) ;
    }
    
    /**
     * Plots the population prevalence of STI over time (cycles).
     */
    public void plotPrevalence(String[] siteNames) {
        
        // set new title
        this.chartTitle = "Prevalence";
        setDrawPoints(false);
        
        ArrayList<Object> prevalenceReport = reporter.prepareCompletePrevalenceReport(siteNames) ;
        //LOGGER.log(Level.INFO, "{0}", prevalenceReport);
        ArrayList<String> siteNamesAll = new ArrayList<String>(Arrays.asList(siteNames)) ;
        siteNamesAll.add(0,ALL) ;
      // logger.log(level.info, "{0}",  prevalenceReport.get(prevalenceReport.size() - 1));
        multiPlotChart(siteNamesAll, prevalenceReport, "prevalence") ;
    }
    
    /**
     * Plots the population prevalence of symptomatic STI over time (cycles).
     */
    public void plotSymptomPrevalence() {
        
        // set new title
        this.chartTitle = "Symptom Prevalence";

        ArrayList<String> symptomaticReport = reporter.preparePrevalenceReport() ;
        
        plotCycleValue("symptomatic", symptomaticReport) ;
    }
    
    /**
     * Plots the proportion of Agents with an STI that are symptomatic over time (cycles).
     */
    public void plotProportionSymptomatic() {
        
        // set new title
        this.chartTitle = "Proportion Symptomatic";

        ArrayList<String> symptomaticReport = reporter.preparePrevalenceReport() ;
        plotCycleValue("proportion", symptomaticReport) ;
    }
    
    /**
     * Plots the population prevalence of infected siteName over time (cycles).
     * @param siteName - Name of Site to plot for.
     */
    public void plotSitePrevalence(String siteName) {
        
        // set new title
        this.chartTitle = "Site Prevalence";

        ArrayList<String> prevalenceReport = reporter.preparePrevalenceReport(siteName) ;
        
        plotCycleValue("prevalence", prevalenceReport) ;
    }
    
    /**
     * Co-plots the population prevalence of infected siteNames over time (cycles).
     * @param siteNames - Array of Sites to plot for.
     */
    public void plotSitePrevalence(String[] siteNames) {
        
        // set new title
        this.chartTitle = "Site Prevalence";
        setDrawPoints(false);


        ArrayList<ArrayList<String>> prevalenceReports = new ArrayList<ArrayList<String>>() ;
        
        for (String siteName : siteNames)
            prevalenceReports.add(reporter.preparePrevalenceReport(siteName)) ;
        
        //LOGGER.info(prevalenceReports.toString());
        multiPlotCycleValue("prevalence", prevalenceReports, siteNames) ;
    }
    
    /**
     * Plots the population prevalence of symptomatic siteName over time (cycles).
     * @param siteName - Name of Site to plot for.
     */
    public void plotSiteSymptomPrevalence(String siteName) {
        
        // set new title
        this.chartTitle = "Site Symptom Prevalence";

        ArrayList<String> symptomaticReport = reporter.preparePrevalenceReport(siteName) ;
        
        plotCycleValue("symptomatic", symptomaticReport) ;
    }
    
    /**
     * Co-plots the population prevalence of symptomatic siteNames over time (cycles).
     * @param siteNames - Array of Sites to plot for.
     */
    public void plotSiteSymptomPrevalence(String[] siteNames) {
        
        // set new title
        this.chartTitle = "Site Symptom Prevalence";

        ArrayList<ArrayList<String>> symptomaticReports = new ArrayList<ArrayList<String>>() ;
        
        for (String siteName : siteNames)
            symptomaticReports.add(reporter.preparePrevalenceReport(siteName)) ;
        
        multiPlotCycleValue("symptomatic", symptomaticReports, siteNames) ;
    }
    
    /**
     * Plots the proportion of Agents with an infected siteName that are symptomatic over time (cycles).
     * @param siteName - Name of Site to plot for.
     */
    public void plotSiteProportionSymptomatic(String siteName) {
        
        // set new title
        this.chartTitle = "Site Proportion Symptomatic";

        ArrayList<String> symptomaticReport = reporter.preparePrevalenceReport(siteName) ;
        plotCycleValue("proportion", symptomaticReport) ;
    }
    
    /**
     * Plots the proportion of Agents that are symptomatic at each siteName over time (cycles).
     * @param siteNames - Name of Sites to plot for.
     */
    public void plotSiteProportionSymptomatic(String[] siteNames) {
        
        // set new title
        this.chartTitle = "Site Proportion Symptomatic";

        ArrayList<ArrayList<String>> symptomaticReports = new ArrayList<ArrayList<String>>() ;
        
        for (String siteName : siteNames)
            symptomaticReports.add(reporter.preparePrevalenceReport(siteName)) ;
        
        multiPlotCycleValue("proportion", symptomaticReports, siteNames) ;
    
    }
    
    /**
     * Plots the population prevalence of coinfected siteNames over time (cycles).
     * @param siteNames (String[]) - Name of coinfected Sites to plot for.
     */
    public void plotSiteCoPrevalence(String[] siteNames) {
        
        // set new title
        this.chartTitle = "Site Co-Prevalence";

        ArrayList<String> coprevalenceReport = reporter.prepareCoPrevalenceReport(siteNames) ;
        
        plotCycleValue("coprevalence", coprevalenceReport) ;
    }
    
    /**
     * Plots STI incidents per head of population over time.
     */
    public void plotNotificationsPerCycle()
    {
        ScreeningPresenter.this.plotNotificationsPerCycle("") ;
    }
    
    /**
     * Plots site-specific STI incidence pere head of population over time.
     * @param siteName 
     */
    public void plotNotificationsPerCycle(String siteName) {
        
        // set new title
        this.chartTitle = "Notifications Per Cycle";

        ArrayList<String> notificationsReport = reporter.prepareNotificationsReport(siteName) ;
        
        plotCycleValue("notifications", notificationsReport) ;
    }
    
    /**
     * Plots site-specific STI incidence pere head of population over time.
     * @param siteNames 
     */
    public void plotNotificationsPerCycle(String[] siteNames) {
        
        // set new title
        this.chartTitle = "Notifications Per Cycle";

        ArrayList<ArrayList<String>> notificationsReports = new ArrayList<ArrayList<String>>() ;
        
        for (String siteName : siteNames)
        {
            notificationsReports.add(reporter.prepareNotificationsReport(siteName)) ;
          // logger.log(level.info, "{0}", notificationsReports.get(notificationsReports.size()-1)) ;
        }
        
        multiPlotCycleValue("notification", notificationsReports,siteNames) ;
    }
    
    
    public void plotIncidencePerCycle(String[] siteNames) {
        
        // set new title
        this.chartTitle = "Incidence Per Cycle";

        ArrayList<ArrayList<String>> notificationReports = new ArrayList<ArrayList<String>>() ;
        
        for (String siteName : siteNames)
            notificationReports.add(reporter.prepareNotificationsReport(siteName)) ;
      // logger.log(level.info, "{0}", notificationReports);
        multiPlotCycleValue("incidence", notificationReports, siteNames) ;
    }
    
    
    /**
     * Plots STI notifications per head of population over time.
     */
    public void plotNotificationPerCycle() {
         plotNotificationPerCycle("") ;
    }
    
    /**
     * Plots site-specific STI incidence pere head of population over time.
     * @param siteName 
     */
    public void plotNotificationPerCycle(String siteName) {
        
        // set new title
        this.chartTitle = "Notification Per Cycle";

        ArrayList<String> incidenceReport = reporter.prepareNotificationsReport(siteName) ;
      // logger.log(level.info, "{0}", incidenceReport);
        plotCycleValue(NOTIFICATION, incidenceReport) ;
    }
    
    
    public void plotNotificationPerCycle(String[] siteNames) {
        
        // set new title
        this.chartTitle = "Notification Per Cycle";

        ArrayList<ArrayList<String>> incidenceReports = new ArrayList<ArrayList<String>>() ;
        
        for (String siteName : siteNames)
            incidenceReports.add(reporter.prepareNotificationsReport(siteName)) ;
        
        multiPlotCycleValue(NOTIFICATION, incidenceReports, siteNames) ;
    }
    
    public void plotFinalAtRiskIncidentsRecord(String[] siteNames, int backYears, String sortingProperty) {
        
        // set new title
        this.chartTitle = "Final At Risk Incidents Record";

        String atRiskIncidentsRecord = reporter.prepareFinalAtRiskIncidentsRecord(siteNames, backYears, sortingProperty) ;
        // LOGGER.info(atRiskIncidentsRecord) ;
        multiPlotValues(atRiskIncidentsRecord,INCIDENCE,"Site") ;
    }
    
    /**
     * Generates a year-by-year plot of incidence-at-risk over backYears years up until 
     * the year lastYear. If a sortingProperty is given then the plots are sorted according 
     * to the values of sortingProperty.
     * @param siteNames
     * @param backYears
     * @param lastYear
     * @param sortingProperty 
     */
    public void plotYearsAtRiskIncidenceReport(String[] siteNames, int backYears, int lastYear, String sortingProperty) {
        
        // set new title
        this.chartTitle = "Years At Risk Incidents Report";

        HashMap<Comparable,String> atRiskIncidenceReport ;
               // = reporter.prepareYearsAtRiskIncidenceReport(siteNames, backYears, lastYear, sortingProperty) ;
        ArrayList<HashMap<Comparable,String>> reports = new ArrayList<HashMap<Comparable,String>>() ;
        String reportName = GENERATE_SORTED_LABEL("riskyIncidence",sortingProperty) ;
//        if (!sortingProperty.isEmpty())
//            reportName += "_" + sortingProperty ;
        
        for (String simulation : simNames)
        {
            // LOGGER.info(simulation);
            ScreeningReporter screeningReporter = new ScreeningReporter(simulation,reporter.getFolderPath()) ;
            HashMap<Comparable,String> report = screeningReporter.prepareYearsAtRiskIncidenceReport(siteNames, backYears, lastYear, sortingProperty) ;
            Reporter.CLEAR_REPORT_LIST() ; 
            reports.add((HashMap<Comparable,String>) report.clone()) ;
            Reporter.DUMP_OUTPUT(reportName,simulation,reporter.getFolderPath(),report);
            // LOGGER.info("@@@@@@ AT RISK CI HM  REPORT" + sortingProperty + "\n" + reports.toString());
        }
        //Reporter.WRITE_CSV_DISTRIBUTION(reports, "at-risk incidence-rate", simNames[0], "output/prep/") ;

        // arRiskIncidenceReport with ONLY mean:
        atRiskIncidenceReport = Reporter.PREPARE_MEAN_HASHMAP_REPORT(reports,"year","INCIDENCE",simNames[0]) ;
        // ArrayList<String[]> 


        //Reporter.WRITE_CSV(percentAgentCondomlessYears, "year", relationshipClassNames, "discordant_relationships", simNames[0], reporter.getFolderPath()) ;
        String[] legend = Reporter.IDENTIFY_PROPERTIES(atRiskIncidenceReport.get(lastYear)).toArray(new String[0]) ;
        
        // keep all the csv file names without extensions
        ArrayList<String> fileNames = new ArrayList<String>();

        HashMap<String, HashMap> propertyToYAndRange = new HashMap<String, HashMap>();

        for (int legendIndex = 0 ; legendIndex < legend.length ; legendIndex++ )
        {
            String property = legend[legendIndex] ;
            ArrayList<String> simNameList = new ArrayList<String>() ;
            Collections.addAll(simNameList, simNames) ;

            Reporter.MULTI_WRITE_CSV(simNameList, "year", property, reportName, reporter.getFolderPath()) ;

            String fileName = reportName + "_" + property + "_" + simNameList.get(0);
            fileNames.add(fileName);

            HashMap<Comparable, String[]> readCSV = Reporter.READ_CSV_STRING(fileName, reporter.getFolderPath(), 1);
            HashMap<String, String[]> yAndRange = Reporter.extractYValueAndRange(readCSV);
            propertyToYAndRange.put(property, yAndRange);
        }
        
        // // set drawing information
        // setDrawError(true);
        // setErrorType(SHADED_REGION);

        plotShadedHashMapStringCI(propertyToYAndRange,INCIDENCE,"year", legend) ;
    }
    
    public void plotNumberAgentTestingReport(int backYears, int backMonths, int backDays) {
        
        // set new title
        this.chartTitle = "Number Agent Testing Report";

        HashMap<Comparable,Number> numberAgentTestingReport 
                = reporter.prepareNumberAgentTestingReport(backYears, backMonths, backDays) ;
    
        String yLabel = "proportion of Agents in last " + GET_TIME_PERIOD_STRING(backYears, backMonths, backDays) ;
        
        plotHashMap("Number of tests", yLabel, numberAgentTestingReport) ;
    }
    
    /**
     * Plots the proportion of Agents, sorted according to sortingProperty if specified,
     * which have been tested in the past backYears years, backMonths months and 
     * backDays days.
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param lastYear
     * @param sortingProperty 
     */
    public void plotYearsBeenTestedReport(int backYears, int backMonths, int backDays, int lastYear, String sortingProperty) {
        
        // set new title
        this.chartTitle = "Years been tested";

        ArrayList<HashMap<Comparable,String>> reports = new ArrayList<HashMap<Comparable,String>>() ;
        String reportOutput ;
        String label ;
        
        for (String simulation : simNames)
        {
            ScreeningReporter screeningReporter = new ScreeningReporter(simulation,reporter.getFolderPath()) ;
            HashMap<Comparable,HashMap<Object,Number>> numberReport = screeningReporter.prepareYearsBeenTestedReport(backYears, backMonths, backDays, lastYear, sortingProperty) ;
            ArrayList<Comparable> sortedYears = new ArrayList<Comparable>(numberReport.keySet()) ; 
            Collections.sort(sortedYears) ;
            HashMap<Comparable,String> stringReport = new HashMap<Comparable,String>() ;
            for (Comparable year : numberReport.keySet())
            {
                reportOutput = "" ;
                HashMap<Object,Number> valueReport = numberReport.get(year) ;
                for (Object sortingValue : valueReport.keySet())
                {
                    label = PROPORTION ;
                    if (!"".equals(sortingValue))
                        label += "__" + sortingValue.toString() ;
                    reportOutput += Reporter.ADD_REPORT_PROPERTY(label,valueReport.get(sortingValue)) ;
                }
                stringReport.put(year, reportOutput) ;
            }
            
            Reporter.CLEAR_REPORT_LIST() ; 
            reports.add((HashMap<Comparable,String>) stringReport.clone()) ;
            Reporter.DUMP_OUTPUT(GENERATE_SORTED_LABEL("testing",sortingProperty),simulation,reporter.getFolderPath(),stringReport);
        }
        
        HashMap<Comparable,String> yearsBeenTestedReport = Reporter.PREPARE_MEAN_HASHMAP_REPORT(reports,"year","testing",simNames[0]) ;
        
        String yLabel = "proportion of Agents in last " + GET_TIME_PERIOD_STRING(backYears, backMonths, backDays) ;
//        
        plotHashMapString(yearsBeenTestedReport,yLabel,"year",new String[] {""}) ;
    }
    
    /**
     * Plots the proportion of Agents who have had a given number of treatments in the 
     * given time period.
     * @param backYears
     * @param backMonths
     * @param backDays 
     */
    public void plotNumberAgentTreatedReport(int backYears, int backMonths, int backDays) {
        
        // set new title
        this.chartTitle = "Number Agent Treated Report";

        HashMap<Comparable,Number> numberAgentTreatedReport 
                = reporter.prepareNumberAgentTreatedReport(backYears, backMonths, backDays) ;
    
        String yLabel = "proportion of Agents in last " + GET_TIME_PERIOD_STRING(backYears, backMonths, backDays) ;
        
        plotHashMap("Number of infections", yLabel, numberAgentTreatedReport) ;
    }
    
    /**
     * Plots the proportion of Agents who have had a given number of treatments in the 
     * given time period, sorted according to the sortingProperty. Numbers higher than
     * maxNumber are absorbed into the classification "maxNumber+".
     * @param backYears
     * @param backMonths
     * @param backDays 
     * @param sortingProperty 
     * @param maxNumber 
     */
    public void plotNumberAgentTreatedReport(int backYears, int backMonths, int backDays, String sortingProperty, int maxNumber ) {
        
        // set new title
        this.chartTitle = "Number Agent Treated Report";

        HashMap<Comparable,Number[]> numberAgentTreatedReport 
                = reporter.prepareNumberAgentTreatedReport(backYears, backMonths, backDays, sortingProperty, maxNumber) ;
        // LOGGER.info(numberAgentTreatedReport.toString());
    
        //String yLabel = "proportion of Agents in last " + GET_TIME_PERIOD_STRING(backYears, backMonths, backDays) ;
        String[] yLabels = new String[] {"-ve".concat(GROUP),"+ve".concat(GROUP)} ;
        
        plotHashMap("Number of infections", yLabels, numberAgentTreatedReport) ;
    }
    
    /**
     * Generates above plots and puts them on the same set of axes with a legend.
     * @param options ArrayList of plot names accompanied by String[] of parameters
     */
    public void multiPlotScreening(Object[] options)
    {
        ArrayList<ArrayList<String>> multiPlotReports = new ArrayList<ArrayList<String>>() ;
        ArrayList<String> multiPlotNames = new ArrayList<String>() ;
        ArrayList<String> legend = new ArrayList<String>() ;
        String legendEntry ;
        Object option ;
        Object nextOption ;
        String[] siteNames ;
        
        for (int argIndex = 0 ; argIndex < options.length ; argIndex++ )
        {
            option = options[argIndex] ;
            if (COPREVALENCE.equals(option))
            {
                nextOption = options[argIndex + 1] ;
                while (nextOption.getClass().getSimpleName().equals("String[]"))
                {
                    siteNames = (String[]) nextOption ;
                    multiPlotReports.add(reporter.prepareCoPrevalenceReport(siteNames)) ;
                    multiPlotNames.add(COPREVALENCE) ;
                    argIndex++ ;
                    
                    legendEntry = "" ;
                    for (String siteName : siteNames)
                        legendEntry += siteName + "_"  ;
                    legend.add(legendEntry + "coprevalence") ; //.substring(0, legendEntry.length() - 1)) ;  // Leave out last "_"
                    if (argIndex == options.length-1)
                        break ;
                    nextOption = options[argIndex + 1] ;
                }
            }

            if (PREVALENCE.equals(option) || SYMPTOMATIC.equals(option))
            {
                // Total prevalence since not followed by String[] of siteNames
                if (argIndex == options.length-1 || options[argIndex + 1].getClass().getSimpleName().equals("String"))
                {
                    multiPlotReports.add(reporter.preparePrevalenceReport()) ;
                    multiPlotNames.add(option.toString()) ;
                    legend.add("all" + "_" + option.toString()) ;  
                }
                else // if siteNames specified by following argument(s)
                {
                    nextOption = (String[]) options[argIndex + 1] ;
                    while (nextOption.getClass().getSimpleName().equals("String[]"))
                    {
                        siteNames = (String[]) nextOption ;
                        for (String siteName : siteNames)
                        {
                            multiPlotReports.add(reporter.preparePrevalenceReport(siteName)) ;
                            multiPlotNames.add(option.toString()) ;
                            legend.add(siteName + "_" + option.toString()) ;  
                        }
                        argIndex++ ;
                        if (argIndex == options.length - 1)
                            break ;
                        nextOption = options[argIndex + 1] ;
                    }
                }
            }
        }
        String[] legendArray =  new String[legend.size()] ;
        for (int i = 0 ; i < legendArray.length ; i++ )
        {
            legendArray[i] = legend.get(i) ;
            ArrayList<String> plotReport = multiPlotReports.get(i) ;
          // logger.log(level.info, "{0} {1}", new Object[] {legend.get(i),plotReport.get(plotReport.size() - 1)}) ;
        }
        multiPlotCycleValue(multiPlotNames,multiPlotReports,legendArray) ;
        
    }
    
    
//            (boolean plotPrevalence, boolean plotSymptomatic, boolean plotCoPrevalence, boolean plotCoSymptomatic, 
//            boolean plotRectum, boolean plotUrethra, boolean plotPharynx)
//            boolean plotPharynxSymptomPrevalence, boolean plotIncidence, boolean plotRectumIncidence, boolean plotUrethraIncidents, 
//            boolean plotPharynxIncidents)

    /**
     * Co-plots the population prevalence of infected siteNames over time (cycles).
     * @param siteNames - Array of Sites to plot for.
     * @param simNames
     */
    public void plotSiteMeanPrevalence(String[] siteNames, String[] simNames) {
        
        // set new title
        this.chartTitle = "Site Mean Prevalence";

        ArrayList<ArrayList<String>> prevalenceReports = new ArrayList<ArrayList<String>>() ;
        
        /*ArrayList<String> allSimNames = (ArrayList<String>) Arrays.asList(simNames) ;
        if (!allSimNames.contains(reporter.getSimName()))
            allSimNames.add(reporter.getSimName()) ;
        */
        
        int nbReports = simNames.length ;
        int nbCycles = reporter.getMaxCycles() ;
        
        for (String siteName : siteNames)
        {
            ArrayList<ArrayList<String>> siteReports = new ArrayList<ArrayList<String>>() ;
            for (String simName : simNames)
            {
                ScreeningReporter screeningReporter = new ScreeningReporter(simName,reporter.getFolderPath()) ;
                siteReports.add(screeningReporter.preparePrevalenceReport(siteName)) ;
                //coplotNames.add("prevalence") ;
            }
            ArrayList<String> meanPrevalenceReport = new ArrayList<String>() ;
        
            for (int cycle = 0 ; cycle < nbCycles ; cycle++)
            {
                double cycleValue = 0.0 ;
                String cycleString ;
                for (ArrayList<String> siteReport : siteReports)
                    cycleValue += Double.valueOf(Reporter.EXTRACT_VALUE(PREVALENCE,String.valueOf(siteReport.get(cycle)))) ;
                cycleString = Reporter.ADD_REPORT_PROPERTY(PREVALENCE, cycleValue/nbReports) ;
                meanPrevalenceReport.add(cycleString) ;
            }
            // LOGGER.info(siteName + " " + meanPrevalenceReport.get(meanPrevalenceReport.size() - 1)) ;
            prevalenceReports.add((ArrayList<String>) meanPrevalenceReport.clone()) ;
        }
        
        // Total prevalence
        ArrayList<ArrayList<String>> siteReports = new ArrayList<ArrayList<String>>() ;
        for (String simName : simNames)
        {
            ScreeningReporter screeningReporter = new ScreeningReporter(simName,reporter.getFolderPath()) ;
            siteReports.add(screeningReporter.preparePrevalenceReport()) ;
            //coplotNames.add("prevalence") ;
        }
        ArrayList<Object> meanPrevalenceReport = new ArrayList<Object>() ;

        for (int cycle = 0 ; cycle < nbCycles ; cycle++)
        {
            double cycleValue = 0.0 ;
            String cycleString ;
            for (ArrayList<String> siteReport : siteReports)
                cycleValue += Double.valueOf(Reporter.EXTRACT_VALUE(PREVALENCE,String.valueOf(siteReport.get(cycle)))) ;
            cycleString = Reporter.ADD_REPORT_PROPERTY(PREVALENCE, cycleValue/nbReports) ;
            meanPrevalenceReport.add(cycleString) ;
        }
        // LOGGER.info("prevalence " + meanPrevalenceReport.get(meanPrevalenceReport.size() - 1)) ;
        prevalenceReports.add(0,(ArrayList<String>) meanPrevalenceReport.clone()) ;
        
        String[] legend = new String[siteNames.length + 1] ;
        legend[0] = "total" ;
        System.arraycopy(siteNames, 0, legend, 1, siteNames.length);
        
        Reporter.WRITE_CSV(prevalenceReports, legend, PREVALENCE, simNames[0], "data_files/");
        multiPlotCycleValue("prevalence", prevalenceReports, legend) ;
    }
    
    /**
     * Plots prevalence from different saved simulations on same axes together 
     * with their mean.
     * @param simNames 
     */
    public void coplotPrevalence(String[] simNames)
    {
        ArrayList<ArrayList<String>> prevalenceReportList = new ArrayList<ArrayList<String>>() ;
        //ArrayList<String> coplotNames = new ArrayList<String>() ;
        ArrayList<String> legend = new ArrayList<String>() ;
        
        // Include this Reporter
        prevalenceReportList.add(reporter.preparePrevalenceReport()) ;
        int nbCycles = reporter.getMaxCycles() ;
        legend.add(reporter.getSimName()) ;
        
        // Add Reporters corresponding to simNames
        for (String simName : simNames)
        {
            ScreeningReporter screeningReporter = new ScreeningReporter(simName,reporter.getFolderPath()) ;
            prevalenceReportList.add(screeningReporter.preparePrevalenceReport()) ;
            //coplotNames.add("prevalence") ;
            legend.add(simName) ;
        }
        
        // Find mean of reports
        ArrayList<String> meanPrevalenceReport = new ArrayList<String>() ;
        int nbReports = prevalenceReportList.size() ;
        for (int cycle = 0 ; cycle < nbCycles ; cycle++)
        {
            double cycleValue = 0.0 ;
            String cycleString ;
            for (ArrayList<String> prevalenceReport : prevalenceReportList)
                cycleValue += Double.valueOf(Reporter.EXTRACT_VALUE(PREVALENCE,String.valueOf(prevalenceReport.get(cycle)))) ;
            cycleString = Reporter.ADD_REPORT_PROPERTY(PREVALENCE, cycleValue/nbReports) ;
            meanPrevalenceReport.add(cycleString) ;
        }
        prevalenceReportList.set(0,meanPrevalenceReport) ;
        legend.set(0,"mean") ;

        //LOGGER.log(Level.INFO, "{0}", prevalenceReport);
        String[] legendArray =  new String[legend.size()] ;
        for (int i = 0 ; i < legendArray.length ; i++ )
            legendArray[i] = legend.get(i) ;
        multiPlotCycleValue("prevalence", prevalenceReportList, legendArray) ;
    }
    
    /**
     * Plots coprevalence at given simNames
     * @param simNames 
     */
    public void coplotFinalPrevalence(String[] simNames)
    {
        HashMap<Comparable,Number> prevalenceRecordList = new HashMap<Comparable,Number>() ;
        ArrayList<String> legend = new ArrayList<String>() ;
        int reportSize ;
        String finalRecord ;
        
        // Include this Reporter
        ArrayList<String> prevalenceReport = reporter.preparePrevalenceReport() ;
        reportSize = prevalenceReport.size() ;
        finalRecord = (String) prevalenceReport.get(reportSize) ;
        prevalenceRecordList.put(reporter.getSimName(),Double.valueOf(Reporter.EXTRACT_VALUE(PREVALENCE, finalRecord))) ;
        legend.add(reporter.getSimName()) ;
        
        // Add Reporters corresponding to simNames
        for (String simName : simNames)
        {
            ScreeningReporter screeningReporter = new ScreeningReporter(simName,reporter.getFolderPath()) ;
            prevalenceReport = screeningReporter.preparePrevalenceReport() ;
            reportSize = prevalenceReport.size() ;
            finalRecord = (String) prevalenceReport.get(reportSize) ;
            prevalenceRecordList.put(simName,Double.valueOf(Reporter.EXTRACT_VALUE(PREVALENCE, finalRecord))) ;
            legend.add(simName) ;
        }
        //LOGGER.log(Level.INFO, "{0}", prevalenceReport);
        String[] legendArray =  new String[legend.size()] ;
        for (int i = 0 ; i < legendArray.length ; i++ )
            legendArray[i] = legend.get(i) ;
        plotHashMap("simulation", PREVALENCE, prevalenceRecordList) ;
    
    }

    /**
     * Extracts information from a range of .csv files
     * where each .csv file is a property
     * used primarily for testing purposes
     * @param fileNames
     */
    public void plotIntervalMeansFromCSVFileNames(String[] fileNames) {
        HashMap<String, HashMap> propertyToYAndRange = new HashMap<String, HashMap>();

        for (int i = 0; i < fileNames.length; ++i) {
            String property = fileNames[i];
            String fileName = fileNames[i];
            HashMap<Comparable, String[]> readCSV = Reporter.READ_CSV_STRING(fileName, reporter.getFolderPath(), 1);
            
            int VALUES_TO_ADD = 3; // y-value, lower, upper
            int yValueIndex = 0;
            int lowerIndex = 1;
            int upperIndex = 2;

            for (Comparable keyCmp : readCSV.keySet()) {
                String[] values = readCSV.get(keyCmp);
                String[] to_add = Reporter.generateMedianAndRangeArrayFromValuesArray(values);

                String[] newValues = new String[values.length + VALUES_TO_ADD];
                newValues[yValueIndex] = to_add[yValueIndex];
                newValues[lowerIndex] = to_add[lowerIndex];
                newValues[upperIndex] = to_add[upperIndex];
                for (int j = 0; j < values.length; ++j)
                    newValues[VALUES_TO_ADD+j] = values[j];
                readCSV.put(keyCmp, newValues); 
            }
            
            HashMap<String, String[]> yAndRange = Reporter.extractYValueAndRange(readCSV);
            propertyToYAndRange.put(property, yAndRange);
        }

        plotShadedHashMapStringCI(propertyToYAndRange, "Y", "Year", fileNames);
    }
    
}
