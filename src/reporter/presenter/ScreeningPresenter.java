/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter.presenter;


import java.util.ArrayList ;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set ;
import java.util.Collection ;

//import community.Community ;
//import java.util.Arrays;
import java.util.logging.Level;
import reporter.Reporter ;
import reporter.ScreeningReporter ;
import static reporter.presenter.Presenter.LOGGER;

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
    
    public ScreeningPresenter()
    {
        super() ;   
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

    //static String[] simNames = new String[] {"seekPop40000Cycles730"} ;
    static String[] simNames = new String[] {"seek1cPop40000Cycles5475","seek1dPop40000Cycles5475","seek1ePop40000Cycles5475",
        "seek1fPop40000Cycles5475","seek1gPop40000Cycles5475","seek1hPop40000Cycles5475","seek1iPop40000Cycles5475","seek1jPop40000Cycles5475"} ;
      //  "adjustCondom75Holt3fPop40000Cycles4380","adjustCondom75Holt3gPop40000Cycles4380","adjustCondom75Holt3hPop40000Cycles4380","adjustCondom75Holt3iPop40000Cycles4380","adjustCondom75Holt3jPop40000Cycles4380"} ;
    //static private String[] simNames = new String[] {"noAntiviralPostHolt3aPop40000Cycles5475"} ; // ,"noAntiviralPostHolt3bPop40000Cycles5475","noAntiviralPostHolt3cPop40000Cycles5475","noAntiviralPostHolt3dPop40000Cycles5475","noAntiviralPostHolt3ePop40000Cycles5475",
        //"noAntiviralPostHolt3fPop40000Cycles5475","noAntiviralPostHolt3gPop40000Cycles5475","noAntiviralPostHolt3hPop40000Cycles5475","noAntiviralPostHolt3iPop40000Cycles5475","noAntiviralPostHolt3jPop40000Cycles5475"} ;
    //static String[] simNames = new String[] {"noGSNpostHolt3aPop40000Cycles5475"} ; //,"noGSNpostHolt3bPop40000Cycles5475","noGSNpostHolt3cPop40000Cycles5475","noGSNpostHolt3dPop40000Cycles5475","noGSNpostHolt3ePop40000Cycles5475",
      //  "noGSNpostHolt3fPop40000Cycles5475","noGSNpostHolt3gPop40000Cycles5475","noGSNpostHolt3hPop40000Cycles5475","noGSNpostHolt3iPop40000Cycles5475","noGSNpostHolt3jPop40000Cycles5475"} ;
    //static String[] simNames = new String[] {"from2007holt2aPop40000Cycles2190","from2007holt2bPop40000Cycles2190","from2007holt2cPop40000Cycles2190"} ;
    //static String[] simNames = new String[] {"from2007wild2bPop40000Cycles5840"} ;
    //static String[] simNames = new String[] {"pholtaPop40000Cycles1460"} ; //,"pholtbPop40000Cycles1460"} ; //
    
    public static void main(String[] args)
    {
        //String simName =  "adjust2009contact96aPop40000Cycles4380" ;
        //String simName = "sameScreen29aPop40000Cycles3190" ;
        //String simName = "Year2007Commence5fPop40000Cycles2500" ;
        //String simName = "safeContact99aPop40000Cycles4000" ;
        //String simName = "to2012max3sameScreen34cPop40000Cycles4380" ;
        //String simName = "to2014agentAdjust29aPop40000Cycles4920" ;
        //String simName = "testRebootPop40000Cycles730" ;
        String simName = simNames[0] ;
        
        boolean unique = false ;
        int notifications = -1 ; 
        String chartTitle ;
        if (unique && (notifications == 1))
            chartTitle = "unique " ;
        else
            chartTitle = "" ;
        if (notifications == 0)
        {
            chartTitle += "notification-rate" ;
            //chartTitle += "condom use scaledown in 2009" ;
        } //
        else if (notifications == 1)
            chartTitle += "incidence" ;
        else
        {
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
        String reportFileName = "output/prep/" ; // args[2] ;
        //String reportFileName = "output/prePrEP/" ; // args[2] ;
        //String reportFileName = "output/test/" ; // args[2] ;
        //String reportFileName = "output/reverse/" ; // args[2] ;
        //String reportFileName = "output/year2012/" ; // args[2] ;
        //String reportFileName = "output/year2010/" ; // args[2] ;
        //String reportFileName = "output/year2007/" ; // args[2] ;

        LOGGER.info(chartTitle) ;
        
        ScreeningPresenter screeningPresenter = new ScreeningPresenter(simName,chartTitle,reportFileName) ;

        String[] siteNames  = new String[] {"Pharynx","Rectum","Urethra"} ;
        //String[] simNames = new String[] {"test2Pop30000Cycles500","test3Pop30000Cycles500","test4Pop30000Cycles500"} ;
        //String[] testArray[] = Arrays.asList(siteNames).subList(0,0)
        
        
        //String[] simNames = new String[] {"to2014max3contactII34aPop40000Cycles5920","to2014max3contactII34bPop40000Cycles5920","to2014max3contactII34cPop40000Cycles5920","to2014max3contactII34dPop40000Cycles5920","to2014max3contactII34ePop40000Cycles5920","to2014max3contactII34fPop40000Cycles5920","to2014max3contactII34gPop40000Cycles5920","to2014max3contactII34hPop40000Cycles5920","to2014max3contactII34iPop40000Cycles5920","to2014max3contactII34jPop40000Cycles5920"} ;
        //String[] simNames = new String[] {"max3contacts54ePop40000Cycles1000","max3contacts54fPop40000Cycles1000"} ; // "max3contact54aPop40000Cycles4000","max3contact54bPop40000Cycles4000","max3contact54cPop40000Cycles4000","max3contact54dPop40000Cycles4000","max3contact34gPop40000Cycles4000","max3contact34hPop40000Cycles4000","max3contact34iPop40000Cycles4000","max3contact34jPop40000Cycles4000"} ;
            //"to2014agentScreen29hPop40000Cycles4920","to2014agentScreen29iPop40000Cycles4920","to2014agentScreen29jPop40000Cycles4920"} ;
        //String[] simNames = new String[] {"max3contacts13bPop40000Cycles1000","max3contacts13aPop40000Cycles1250"} ;
        
        //screeningPresenter.coplotPrevalence(simNames) ;
        //screeningPresenter.plotSiteMeanPrevalence(siteNames,simNames) ;
        
        //screeningPresenter.plotNumberAgentTestingReport(0, 6, 0) ;
        //screeningPresenter.plotNumberAgentTreatedReport(2, 0, 0,"prepStatus",5) ;

        //screeningPresenter.multiPlotScreening(new Object[] {"prevalence","coprevalence",new String[] {"Pharynx","Rectum"},new String[] {"Urethra","Rectum"},"prevalence",new String[] {"Pharynx","Rectum","Urethra"}});
        //screeningPresenter.multiPlotScreening(new Object[] {"prevalence","prevalence",new String[] {"Pharynx","Rectum","Urethra"}});
        //screeningPresenter.plotIncidencePerCycle(siteNames) ;
        //screeningPresenter.plotNotificationsPerCycle(siteNames) ;
        //screeningPresenter.plotSitePrevalence(siteNames) ;
        //screeningPresenter.plotSiteSymptomPrevalence(siteNames) ;
        //screeningPresenter.plotPrevalence(siteNames) ;
        //screeningPresenter.plotPrevalenceYears(siteNames,11,2017) ;
        //screeningPresenter.plotSortedPrevalenceYears(siteNames,11,2017,"statusHIV") ;
        //screeningPresenter.plotFinalSymptomatic(new String[] {"Pharynx","Rectum","Urethra"}) ;
        //screeningPresenter.plotFinalPrevalences(new String[] {"Pharynx","Rectum","Urethra"}) ;
        //screeningPresenter.plotSortedFinalPrevalences(new String[] {"Pharynx","Rectum","Urethra"}, "statusHIV") ;
        //screeningPresenter.plotFinalNotifications(new String[] {"Pharynx","Rectum","Urethra"}, unique, 0, Reporter.DAYS_PER_YEAR, notifications) ;
        //screeningPresenter.plotSortedFinalNotifications(new String[] {"Pharynx","Rectum","Urethra"}, unique, 0, 0, Reporter.DAYS_PER_YEAR, "statusHIV") ; 
        screeningPresenter.plotSortedNotificationsYears(siteNames, unique, 11, 2017, "statusHIV") ;
        //screeningPresenter.plotNotificationsYears(siteNames,5,2014) ;    // siteNames,5,4) ;    // new String[] {"all"} 
        //screeningPresenter.plotPositivityYears(siteNames, unique, 8, 2014) ;
        //screeningPresenter.plotNotificationPerCycle() ;    
        //screeningPresenter.plotSiteProportionSymptomatic(siteNames) ;


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
    public void plotSortedNotificationsYears(String[] siteNames, boolean unique, int backYears, int lastYear, String sortingProperty)
    {
        HashMap<Object,String> plotSortedNotifications = new HashMap<Object,String>() ;
        
        String[] siteAllNames = new String[siteNames.length+1] ;
        siteAllNames[0] = "all" ;
        // = Arrays.copyOf(siteNames, siteNames.length + 1) ;
        System.arraycopy(siteNames, 0, siteAllNames, 1, siteNames.length);
        ArrayList<HashMap<Object,String>> reports = new ArrayList<HashMap<Object,String>>() ;
        
        // Get sorted Report
        for (String simulation: simNames)
        {
            ScreeningReporter screeningReporter = new ScreeningReporter(simulation,reporter.getFolderPath()) ;
            reports.add(screeningReporter.prepareSortedYearsNotificationsRecord(siteNames, backYears, lastYear, sortingProperty)) ;
        }
        HashMap<Object,String> sortedNotificationsYears //= reports.get(0) ;
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

        Reporter.WRITE_CSV_STRING(plotSortedNotifications, "year", title, simNames[0], Reporter.REPORT_FOLDER) ;
        plotHashMapString(plotSortedNotifications,title,"year",legend) ;
    }
    
    /**
     * Plots bar chart showing incidence at each Site for each of the last backYears
     * years counting back from lastYear.
     * @param siteNames
     * @param backYears
     * @param lastYear 
     */
    public void plotNotificationsYears(String[] siteNames, int backYears, int lastYear)
    {
        //HashMap<Object,Number[]> notificationsRecordYears = reporter.prepareYearsNotificationsRecord(siteNames, backYears, lastYear) ;
        HashMap<Object,String> notificationsYearsPlot ; //= new HashMap<Object,String>() ;
        
        String[] siteAllNames = new String[siteNames.length+1] ;
        siteAllNames[0] = "all" ;
        // = Arrays.copyOf(siteNames, siteNames.length + 1) ;
        System.arraycopy(siteNames, 0, siteAllNames, 1, siteNames.length);
        
        ArrayList<HashMap<Object,String>> reports = new ArrayList<HashMap<Object,String>>() ;
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
    
    /**
     * Plots bar chart showing incidence at each Site for each of the last backYears
     * years counting back from lastYear.
     * @param siteNames
     * @param unique
     * @param backYears
     * @param lastYear 
     */
    public void plotPositivityYears(String[] siteNames, boolean unique, int backYears, int lastYear)
    {
        HashMap<Object,String> positivityRecordYears = reporter.prepareYearsPositivityRecord(siteNames, unique, backYears, lastYear) ;    // Number[]
        
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
    public void plotFinalNotifications(String[] siteNames, boolean unique, int backMonths, int backDays, int outcome)
    {
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
        LOGGER.log(Level.INFO, "{0}", reports);
        meanNotificationsRecord = Reporter.PREPARE_MEAN_REPORT(reports) ;
        
        
        /**
        for (String property : Reporter.IDENTIFY_PROPERTIES(notificationsRecord))
        {
            finalNotificationsRecord.put(property, Double.valueOf(Reporter.EXTRACT_VALUE(property, notificationsRecord))) ;
            TODO: sEPARATE NOTIFICATION FROM POSITIVITY ;
        }
        LOGGER.log(Level.INFO, "{0}", finalNotificationsRecord); */
        //String[] yLabels = new String[] {"incidence","positivity"} ;
        LOGGER.info(meanNotificationsRecord);
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
    public void plotSortedFinalNotifications(String[] siteNames, boolean unique, int backYears, int backMonths, int backDays, String sortingProperty)
    {
        String finalNotificationsRecord = "" ;
        HashMap<Object,String> notificationsRecord = reporter.prepareSortedFinalNotificationsRecord(siteNames, unique, backYears, backMonths, backDays, 0, sortingProperty) ;
        LOGGER.log(Level.INFO, "{0}", notificationsRecord);
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
            LOGGER.info(siteName);
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
        LOGGER.log(Level.INFO, "{0}", finalNotificationsRecord);
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
        LOGGER.log(Level.INFO,"{0} {1}", new Object[] {sortedFinalNotificationsRecord.keySet(),String.valueOf(legendLength)});
        
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
        LOGGER.log(Level.INFO,"{2} {0} {1}", new Object[] {legendList,sortedFinalNotificationsReport,legend.length}) ;
        multiPlotCycleValue(legendList,sortedFinalNotificationsReport,legend) ;
        * */
    }
    
    /**
     * Plots bar chart showing incidence at each Site for each of the last backYears
     * years counting back from lastYear.
     * @param siteNames
     * @param backYears
     * @param lastYear 
     */
    public void plotSortedPrevalenceYears(String[] siteNames, int backYears, int lastYear, String sortingProperty)
    {
        //HashMap<Object,Number[]> 
        HashMap<Object,String>  prevalenceRecordYears = reporter.prepareYearsPrevalenceRecord(siteNames, backYears, lastYear, sortingProperty) ;
        String[] siteAllNames = Arrays.copyOf(siteNames, siteNames.length + 1) ;
        siteAllNames[siteNames.length] = "all" ;
        LOGGER.log(Level.INFO, "{0}", prevalenceRecordYears.get(2017));
        plotHashMapString(prevalenceRecordYears,PREVALENCE,"Year", siteAllNames) ;
    }
    
    /**
     * Plots bar chart showing incidence at each Site for each of the last backYears
     * years counting back from lastYear.
     * @param siteNames
     * @param backYears
     * @param lastYear 
     */
    public void plotPrevalenceYears(String[] siteNames, int backYears, int lastYear)
    {
        //HashMap<Object,Number[]> 
        HashMap<Object,String>  prevalenceRecordYears = reporter.prepareYearsPrevalenceRecord(siteNames, backYears, lastYear, "") ;
        String[] siteAllNames = Arrays.copyOf(siteNames, siteNames.length + 1) ;
        siteAllNames[siteNames.length] = "all" ;
        LOGGER.log(Level.INFO, "{0}", prevalenceRecordYears);
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
    public void plotFinalPrevalences(String[] siteNames)
    {
        //HashMap<Object,Number> 
        String finalPrevalencesRecord = reporter.prepareFinalPrevalencesRecord(siteNames) ;
        LOGGER.info(finalPrevalencesRecord);
        
        plotValues(PREVALENCE,finalPrevalencesRecord) ;        
    }
    
    /**
     * Plots bar chart showing prevalence of requested siteNames and total prevalence.
     * @param siteNames 
     * @param sortingProperty 
     */
    public void plotSortedFinalPrevalences(String[] siteNames, String sortingProperty)
    {
        //HashMap<Object,Number> 
        String finalPrevalencesRecord = reporter.prepareFinalPrevalencesSortedRecord(siteNames, sortingProperty) ;
        
        LOGGER.log(Level.INFO, "{0}", finalPrevalencesRecord) ;
                
        plotValues(PREVALENCE,finalPrevalencesRecord) ;        
    }
    
    /**
     * Plots bar chart showing prevalence of all siteNames and total prevalence.
     */
    public void plotFinalSymptomatic()
    {
        //String siteNames = reporter.getMetaDatum("Community.SITE_NAMES") ;
        //ArrayList<String> sitesList = Arrays(siteNames) ;
        
        plotFinalSymptomatic(new String[] {"Pharynx","Rectum","Urethra"}) ;
    }
    
    /**
     * Plots bar chart showing symptomatic prevalence of requested siteNames and in total.
     * @param siteNames 
     */
    public void plotFinalSymptomatic(String[] siteNames)
    {
        HashMap<Object,Number> finalSymptomaticRecord = reporter.prepareFinalSymptomaticRecord(siteNames) ;
        
        
        plotHashMap("Sites","symptomatic",finalSymptomaticRecord) ;        
    }
    
    /**
     * Plots the population prevalence of STI over time (cycles).
     */
    public void plotPrevalence()
    {
        ArrayList<Object> prevalenceReport = reporter.preparePrevalenceReport() ;
        //LOGGER.log(Level.INFO, "{0}", prevalenceReport);
        plotCycleValue("prevalence", prevalenceReport) ;
    }
    
    /**
     * Plots the population prevalence of STI over time (cycles).
     */
    public void plotPrevalence(String[] siteNames)
    {
        ArrayList<Object> prevalenceReport = reporter.prepareCompletePrevalenceReport(siteNames) ;
        //LOGGER.log(Level.INFO, "{0}", prevalenceReport);
        ArrayList<String> siteNamesAll = new ArrayList<String>(Arrays.asList(siteNames)) ;
        siteNamesAll.add(0,ALL) ;
        LOGGER.log(Level.INFO, "{0}",  prevalenceReport.get(prevalenceReport.size() - 1));
        multiPlotChart(siteNamesAll, prevalenceReport, "prevalence") ;
    }
    
    /**
     * Plots the population prevalence of symptomatic STI over time (cycles).
     */
    public void plotSymptomPrevalence()
    {
        ArrayList<Object> symptomaticReport = reporter.preparePrevalenceReport() ;
        
        plotCycleValue("symptomatic", symptomaticReport) ;
    }
    
    /**
     * Plots the proportion of Agents with an STI that are symptomatic over time (cycles).
     */
    public void plotProportionSymptomatic()
    {
        ArrayList<Object> symptomaticReport = reporter.preparePrevalenceReport() ;
        plotCycleValue("proportion", symptomaticReport) ;
    }
    
    /**
     * Plots the population prevalence of infected siteName over time (cycles).
     * @param siteName - Name of Site to plot for.
     */
    public void plotSitePrevalence(String siteName)
    {
        ArrayList<Object> prevalenceReport = reporter.preparePrevalenceReport(siteName) ;
        
        plotCycleValue("prevalence", prevalenceReport) ;
    }
    
    /**
     * Co-plots the population prevalence of infected siteNames over time (cycles).
     * @param siteNames - Array of Sites to plot for.
     */
    public void plotSitePrevalence(String[] siteNames)
    {
        ArrayList<ArrayList<Object>> prevalenceReports = new ArrayList<ArrayList<Object>>() ;
        
        for (String siteName : siteNames)
            prevalenceReports.add(reporter.preparePrevalenceReport(siteName)) ;
        
        multiPlotCycleValue("prevalence", prevalenceReports, siteNames) ;
    }
    
    /**
     * Plots the population prevalence of symptomatic siteName over time (cycles).
     * @param siteName - Name of Site to plot for.
     */
    public void plotSiteSymptomPrevalence(String siteName)
    {
        ArrayList<Object> symptomaticReport = reporter.preparePrevalenceReport(siteName) ;
        
        plotCycleValue("symptomatic", symptomaticReport) ;
    }
    
    /**
     * Co-plots the population prevalence of symptomatic siteNames over time (cycles).
     * @param siteNames - Array of Sites to plot for.
     */
    public void plotSiteSymptomPrevalence(String[] siteNames)
    {
        ArrayList<ArrayList<Object>> symptomaticReports = new ArrayList<ArrayList<Object>>() ;
        
        for (String siteName : siteNames)
            symptomaticReports.add(reporter.preparePrevalenceReport(siteName)) ;
        
        multiPlotCycleValue("symptomatic", symptomaticReports, siteNames) ;
    }
    
    /**
     * Plots the proportion of Agents with an infected siteName that are symptomatic over time (cycles).
     * @param siteName - Name of Site to plot for.
     */
    public void plotSiteProportionSymptomatic(String siteName)
    {
        ArrayList<Object> symptomaticReport = reporter.preparePrevalenceReport(siteName) ;
        plotCycleValue("proportion", symptomaticReport) ;
    }
    
    /**
     * Plots the proportion of Agents that are symptomatic at each siteName over time (cycles).
     * @param siteNames - Name of Sites to plot for.
     */
    public void plotSiteProportionSymptomatic(String[] siteNames)
    {
        ArrayList<ArrayList<Object>> symptomaticReports = new ArrayList<ArrayList<Object>>() ;
        
        for (String siteName : siteNames)
            symptomaticReports.add(reporter.preparePrevalenceReport(siteName)) ;
        
        multiPlotCycleValue("proportion", symptomaticReports, siteNames) ;
    
    }
    
    /**
     * Plots the population prevalence of coinfected siteNames over time (cycles).
     * @param siteNames (String[]) - Name of coinfected Sites to plot for.
     */
    public void plotSiteCoPrevalence(String[] siteNames)
    {
        ArrayList<Object> coprevalenceReport = reporter.prepareCoPrevalenceReport(siteNames) ;
        
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
    public void plotNotificationsPerCycle(String siteName)
    {
        ArrayList<Object> notificationsReport = reporter.prepareNotificationsReport(siteName) ;
        
        plotCycleValue("notifications", notificationsReport) ;
    }
    
    /**
     * Plots site-specific STI incidence pere head of population over time.
     * @param siteNames 
     */
    public void plotNotificationsPerCycle(String[] siteNames)
    {
        ArrayList<ArrayList<Object>> notificationsReports = new ArrayList<ArrayList<Object>>() ;
        
        for (String siteName : siteNames)
        {
            notificationsReports.add(reporter.prepareNotificationsReport(siteName)) ;
            LOGGER.log(Level.INFO, "{0}", notificationsReports.get(notificationsReports.size()-1)) ;
        }
        
        multiPlotCycleValue("notification", notificationsReports,siteNames) ;
    }
    
    
    public void plotIncidencePerCycle(String[] siteNames)
    {
        ArrayList<ArrayList<Object>> notificationReports = new ArrayList<ArrayList<Object>>() ;
        
        for (String siteName : siteNames)
            notificationReports.add(reporter.prepareNotificationsReport(siteName)) ;
        LOGGER.log(Level.INFO, "{0}", notificationReports);
        multiPlotCycleValue("incidence", notificationReports, siteNames) ;
    }
    
    
    /**
     * Plots STI notifications per head of population over time.
     */
    public void plotNotificationPerCycle()
    {
        plotNotificationPerCycle("") ;
    }
    
    /**
     * Plots site-specific STI incidence pere head of population over time.
     * @param siteName 
     */
    public void plotNotificationPerCycle(String siteName)
    {
        ArrayList<Object> incidenceReport = reporter.prepareNotificationsReport(siteName) ;
        LOGGER.log(Level.INFO, "{0}", incidenceReport);
        plotCycleValue(NOTIFICATION, incidenceReport) ;
    }
    
    
    public void plotNotificationPerCycle(String[] siteNames)
    {
        ArrayList<ArrayList<Object>> incidenceReports = new ArrayList<ArrayList<Object>>() ;
        
        for (String siteName : siteNames)
            incidenceReports.add(reporter.prepareNotificationsReport(siteName)) ;
        
        multiPlotCycleValue(NOTIFICATION, incidenceReports, siteNames) ;
    }
    
    /**
     * Plots the proportion of Agents who have had a given number of tests in the 
     * given time period.
     * @param backYears
     * @param backMonths
     * @param backDays 
     */
    public void plotNumberAgentTestingReport(int backYears, int backMonths, int backDays)
    {
        HashMap<Object,Number> numberAgentTestingReport 
                = reporter.prepareNumberAgentTestingReport(backYears, backMonths, backDays) ;
    
        String yLabel = "proportion of Agents in last " + GET_TIME_PERIOD_STRING(backYears, backMonths, backDays) ;
        
        plotHashMap("Number of tests", yLabel, numberAgentTestingReport) ;
    }
    
    /**
     * Plots the proportion of Agents who have had a given number of treatments in the 
     * given time period.
     * @param backYears
     * @param backMonths
     * @param backDays 
     */
    public void plotNumberAgentTreatedReport(int backYears, int backMonths, int backDays)
    {
        HashMap<Object,Number> numberAgentTreatedReport 
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
    public void plotNumberAgentTreatedReport(int backYears, int backMonths, int backDays, String sortingProperty, int maxNumber )
    {
        HashMap<Object,Number[]> numberAgentTreatedReport 
                = reporter.prepareNumberAgentTreatedReport(backYears, backMonths, backDays, sortingProperty, maxNumber) ;
    
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
        ArrayList<ArrayList<Object>> multiPlotReports = new ArrayList<ArrayList<Object>>() ;
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
            ArrayList<Object> plotReport = multiPlotReports.get(i) ;
            LOGGER.log(Level.INFO, "{0} {1}", new Object[] {legend.get(i),plotReport.get(plotReport.size() - 1)}) ;
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
    public void plotSiteMeanPrevalence(String[] siteNames, String[] simNames)
    {
        ArrayList<ArrayList<Object>> prevalenceReports = new ArrayList<ArrayList<Object>>() ;
        
        /*ArrayList<String> allSimNames = (ArrayList<String>) Arrays.asList(simNames) ;
        if (!allSimNames.contains(reporter.getSimName()))
            allSimNames.add(reporter.getSimName()) ;
        */
        
        int nbReports = simNames.length ;
        int nbCycles = reporter.getMaxCycles() ;
        
        for (String siteName : siteNames)
        {
            ArrayList<ArrayList<Object>> siteReports = new ArrayList<ArrayList<Object>>() ;
            for (String simName : simNames)
            {
                ScreeningReporter screeningReporter = new ScreeningReporter(simName,reporter.getFolderPath()) ;
                siteReports.add(screeningReporter.preparePrevalenceReport(siteName)) ;
                //coplotNames.add("prevalence") ;
            }
            ArrayList<Object> meanPrevalenceReport = new ArrayList<Object>() ;
        
            for (int cycle = 0 ; cycle < nbCycles ; cycle++)
            {
                double cycleValue = 0.0 ;
                String cycleString ;
                for (ArrayList<Object> siteReport : siteReports)
                    cycleValue += Double.valueOf(Reporter.EXTRACT_VALUE(PREVALENCE,String.valueOf(siteReport.get(cycle)))) ;
                cycleString = Reporter.ADD_REPORT_PROPERTY(PREVALENCE, cycleValue/nbReports) ;
                meanPrevalenceReport.add(cycleString) ;
            }
            prevalenceReports.add((ArrayList<Object>) meanPrevalenceReport.clone()) ;
        }
        
        // Total prevalence
        ArrayList<ArrayList<Object>> siteReports = new ArrayList<ArrayList<Object>>() ;
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
            for (ArrayList<Object> siteReport : siteReports)
                cycleValue += Double.valueOf(Reporter.EXTRACT_VALUE(PREVALENCE,String.valueOf(siteReport.get(cycle)))) ;
            cycleString = Reporter.ADD_REPORT_PROPERTY(PREVALENCE, cycleValue/nbReports) ;
            meanPrevalenceReport.add(cycleString) ;
        }
        prevalenceReports.add(0,(ArrayList<Object>) meanPrevalenceReport.clone()) ;
        
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
        ArrayList<ArrayList<Object>> prevalenceReportList = new ArrayList<ArrayList<Object>>() ;
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
        ArrayList<Object> meanPrevalenceReport = new ArrayList<Object>() ;
        int nbReports = prevalenceReportList.size() ;
        for (int cycle = 0 ; cycle < nbCycles ; cycle++)
        {
            double cycleValue = 0.0 ;
            String cycleString ;
            for (ArrayList<Object> prevalenceReport : prevalenceReportList)
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
        HashMap<Object,Number> prevalenceRecordList = new HashMap<Object,Number>() ;
        ArrayList<String> legend = new ArrayList<String>() ;
        int reportSize ;
        String finalRecord ;
        
        // Include this Reporter
        ArrayList<Object> prevalenceReport = reporter.preparePrevalenceReport() ;
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
    
}
