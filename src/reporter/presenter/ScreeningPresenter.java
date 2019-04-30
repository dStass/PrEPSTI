/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter.presenter;


import java.util.ArrayList ;
import java.util.Arrays;
import java.util.HashMap;

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
    static public String NOTIFICATION = "notification" ;  // "treated" ;
    
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

    public static void main(String[] args)
    {
        //String simName = "corrP1Screen16ePop40000Cycles4920" ; 
        //String simName = "test1aPop4000Cycles1500" ; 
        //String simName =  "agent3screenjPop40000Cycles4000" ;
        //String simName =  "fixProgress6aPop40000Cycles750" ;
        //String simName = "sameAntiViral16aPop40000Cycles2000" ;
        //String simName = "Year2007Commence5fPop40000Cycles2500" ;
        String simName = "to2014agent29aPop40000Cycles3920" ;
        //String simName = "to2012allTest1aPop40000Cycles2690" ;
        //String simName = "allTest2cPop40000Cycles1500" ;
        
        boolean unique = false ;
        int notifications = 0 ; 
        String chartTitle ;
        if (unique && (notifications == 1))
            chartTitle = "unique " ;
        else
            chartTitle = "" ;
        if (notifications == 0)
            chartTitle += "notifications" ; //
        else if (notifications == 1)
            chartTitle += "positivity" ;
        else
        {
            chartTitle += "mean_prevalence" 
            //chartTitle += "multi-site prevalence" 
        
        + "" ;
        }
        //String chartTitle = "proportion_symptomatic" ;
        //String chartTitle = "site-specific symptomatic" ; // args[1] ;
        //String chartTitle = "testing_6_months" ; // args[1] ;
        //String chartTitle = "infections_past_2years_PrEP" ; // args[1] ;
        String reportFileName = "output/prePrEP/" ; // args[2] ;
        //String reportFileName = "output/test/" ; // args[2] ;
        //String reportFileName = "output/reverse/" ; // args[2] ;
        //String reportFileName = "output/year2012/" ; // args[2] ;
        //String reportFileName = "output/year2007/" ; // args[2] ;

        LOGGER.info(chartTitle) ;
        
        ScreeningPresenter screeningPresenter = new ScreeningPresenter(simName,chartTitle,reportFileName) ;

        String[] siteNames  = new String[] {"Pharynx","Rectum","Urethra"} ;
        //String[] simNames = new String[] {"test2Pop30000Cycles500","test3Pop30000Cycles500","test4Pop30000Cycles500"} ;
        //String[] testArray[] = Arrays.asList(siteNames).subList(0,0)
        
        //String[] simNames = new String[] {"to2014agentScreen29aPop40000Cycles4920","to2014agentScreen29bPop40000Cycles4920","to2014agentScreen29cPop40000Cycles4920","to2014agentScreen29dPop40000Cycles4920","to2014agentScreen29ePop40000Cycles4920","to2014agentScreen29fPop40000Cycles4920","to2014agentScreen29gPop40000Cycles4920",
            //"to2014agentScreen29hPop40000Cycles4920","to2014agentScreen29iPop40000Cycles4920","to2014agentScreen29jPop40000Cycles4920"} ;
        //String[] simNames = new String[] {"agent3ScreenaPop40000Cycles4000","agent3ScreenbPop40000Cycles4000","agent3screencPop40000Cycles4000","agent3screendPop40000Cycles4000","agent3screenePop40000Cycles4000","agent3screenfPop40000Cycles4000","agent3screengPop40000Cycles4000","agent3screenhPop40000Cycles4000","agent3screeniPop40000Cycles4000","agent3screenjPop40000Cycles4000"} ;
        //String[] simNames = new String[] {"IntroPrepCalibration74acycle6000Pop40000Cycles12000","IntroPrepCalibration74bcycle6000Pop40000Cycles12000","IntroPrepCalibration74ccycle6000Pop40000Cycles12000"} ;

        //screeningPresenter.coplotPrevalence(simNames) ;
        //screeningPresenter.plotSiteMeanPrevalence(siteNames,simNames) ;
        
        //screeningPresenter.plotNumberAgentTestingReport(0, 6, 0) ;
        //screeningPresenter.plotNumberAgentTreatedReport(2, 0, 0,"prepStatus",5) ;

        //screeningPresenter.multiPlotScreening(new Object[] {"prevalence","coprevalence",new String[] {"Pharynx","Rectum"},new String[] {"Urethra","Rectum"},"prevalence",new String[] {"Pharynx","Rectum","Urethra"}});
        //screeningPresenter.multiPlotScreening(new Object[] {"prevalence","prevalence",new String[] {"Pharynx","Rectum","Urethra"}});
        //screeningPresenter.plotNotificationsPerCycle(siteNames) ;
        //screeningPresenter.plotSitePrevalence(siteNames) ;
        //screeningPresenter.plotSiteMeanPrevalence(siteNames, simNames) ;
        //screeningPresenter.plotFinalSymptomatic(new String[] {"Pharynx","Rectum","Urethra"}) ;
        //screeningPresenter.plotFinalPrevalences(new String[] {"Pharynx","Rectum","Urethra"}) ;
        //screeningPresenter.plotFinalNotifications(new String[] {"Pharynx","Rectum","Urethra"}, unique, 0, Reporter.DAYS_PER_YEAR, notifications) ;
        screeningPresenter.plotNotificationsYears(new String[] {"all"},8,2014) ;
        //screeningPresenter.plotPositivityYears(siteNames, unique, 8, 2014) ;
        //screeningPresenter.plotNotificationPerCycle() ;    
        //screeningPresenter.plotSiteProportionSymptomatic(siteNames) ;


        //String methodName = args[3] ;
        //Method method = EncounterPresenter.class.getMethod(methodName) ;

        //method.invoke(encounterPresenter, (Object[]) Arrays.copyOfRange(args,4,args.length)) ;
        
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
        HashMap<Object,Number[]> notificationsRecordYears = reporter.prepareYearsNotificationsRecord(siteNames, backYears, lastYear) ;
        
        plotHashMap("Year", siteNames, notificationsRecordYears) ;
    }
    
    /**
     * Plots bar chart showing incidence at each Site for each of the last backYears
     * years counting back from lastYear.
     * @param siteNames
     * @param backYears
     * @param lastYear 
     */
    public void plotPositivityYears(String[] siteNames, boolean unique, int backYears, int lastYear)
    {
        HashMap<Object,Number[]> positivityRecordYears = reporter.prepareYearsPositivityRecord(siteNames, unique, backYears, lastYear) ;
        plotHashMap("Year", siteNames, positivityRecordYears) ;
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
     * @param backMonths 
     * @param backDays 
     * @param outcome 
     */
    public void plotFinalNotifications(String[] siteNames, boolean unique, int backMonths, int backDays, int outcome)
    {
        // [0] for positivity
        HashMap<Object,Number> finalNotificationsRecord = new HashMap<Object,Number>() ;
        HashMap<Object,Number[]> notificationsRecord = reporter.prepareFinalNotificationsRecord(siteNames, unique, backMonths, backDays) ;
        for (Object key : notificationsRecord.keySet())
        {
            finalNotificationsRecord.put(key, notificationsRecord.get(key)[outcome]) ;
        }
        LOGGER.log(Level.INFO, "{0}", finalNotificationsRecord);
        String[] yLabels = new String[] {"incidence","positivity"} ;
        plotHashMap("Sites",yLabels[outcome],finalNotificationsRecord) ;        
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
        HashMap<Object,Number[]> prevalenceRecordYears = reporter.prepareYearsPrevalenceRecord(siteNames, backYears, lastYear) ;
        
        plotHashMap("Year", siteNames, prevalenceRecordYears) ;
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
        HashMap<Object,Number> finalPrevalencesRecord = reporter.prepareFinalPrevalencesRecord(siteNames) ;
        
        
        plotHashMap("Sites","prevalence",finalPrevalencesRecord) ;        
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
        
        multiPlotCycleValue("notifications", notificationReports, siteNames) ;
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
                    multiPlotNames.add((String) option) ;
                    legend.add("all" + "_" + ((String) option)) ;  
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
                            multiPlotNames.add((String) option) ;
                            legend.add(((String) option) + "_" + siteName) ;  
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
        for (int index = 0 ; index < siteNames.length ; index++ )
            legend[index+1] = siteNames[index] ;
        
        Reporter.WRITE_CSV(prevalenceReports, legend, PREVALENCE, "NoPrepCalibration51bPop40000Cycles4000", "data_files/");
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
