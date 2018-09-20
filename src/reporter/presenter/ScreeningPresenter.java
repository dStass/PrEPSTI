/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter.presenter;


import java.util.ArrayList ;
import java.util.HashMap;

import community.Community ;
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
    static public String NOTIFICATION = "incidence" ;  // "treated" ;
    
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
        //String simName = "testPop30000Cycles500" ; // Community.NAME_ROOT ; // "introPrepCalibration48Pop40000Cycles7000" ; // args[0] ;
        String simName = "NoPrepCalibration33Pop40000Cycles3000" ; // "DecliningCondoms3Pop40000Cycles5000" ; // "NoPrepCalibration86Pop40000Cycles5000" ; // "introPrepCalibration49Pop40000Cycles5000" ; // "NoPrepSetting01Pop40000Cycles5000" ; // 
        String chartTitle = NOTIFICATION ; // args[1] ;
        String reportFileName = "output/test/" ; // args[2] ;

        ScreeningPresenter screeningPresenter = new ScreeningPresenter(simName,chartTitle,reportFileName) ;

        //String[] simNames = new String[] {"test2Pop30000Cycles500","test3Pop30000Cycles500","test4Pop30000Cycles500"} ;
        //String[] simNames = new String[] {"NoPrepCalibration86bPop40000Cycles5000","NoPrepCalibration86cPop40000Cycles5000","NoPrepCalibration86dPop40000Cycles5000"} ;

        //screeningPresenter.coplotPrevalence(simNames) ;

        //screeningPresenter.plotIncidencePerCycle(new String[] {"Rectum"});
        //screeningPresenter.multiPlotScreening(new Object[] {SYMPTOMATIC,new String[] {"Pharynx","Rectum","Urethra"}});
        //screeningPresenter.multiPlotScreening(new Object[] {"prevalence","coprevalence",new String[] {"Pharynx","Rectum"},new String[] {"Urethra","Rectum"},"prevalence",new String[] {"Pharynx","Rectum","Urethra"}});
        //screeningPresenter.plotIncidencePerCycle("Pharynx") ;
        //screeningPresenter.plotFinalSymptomatic(new String[] {"Pharynx","Rectum","Urethra"}) ;
        //screeningPresenter.plotFinalPrevalences(new String[] {"Pharynx","Rectum","Urethra"}) ;
        //screeningPresenter.plotFinalIncidence(new String[] {"Pharynx","Rectum","Urethra"}) ;
        screeningPresenter.plotNotificationPerCycle() ;    


        //String methodName = args[3] ;
        //Method method = EncounterPresenter.class.getMethod(methodName) ;

        //method.invoke(encounterPresenter, (Object[]) Arrays.copyOfRange(args,4,args.length)) ;
        
    }
    
    
    
    /**
     * Plots bar chart showing prevalence of all siteNames and total prevalence.
     * TODO: Read (String[]) siteNames from metadata.
     * @param siteNames 
     */
    public void plotFinalIncidence()
    {
        //String siteNames = reporter.getMetaDatum("Community.SITE_NAMES") ;
        //ArrayList<String> sitesList = Arrays(siteNames) ;
        
        plotFinalIncidence(new String[] {"Pharynx","Rectum","Urethra"}) ;
    }
    
    /**
     * Plots bar chart showing prevalence of requested siteNames and total prevalence.
     * @param siteNames 
     */
    public void plotFinalIncidence(String[] siteNames)
    {
        HashMap<Object,Number> finalIncidenceRecord = reporter.prepareFinalIncidenceRecord(siteNames) ;
        
        
        plotHashMap("Sites","prevalence",finalIncidenceRecord) ;        
    }
    
    /**
     * Plots bar chart showing prevalence of all siteNames and total prevalence.
     * TODO: Read (String[]) siteNames from metadata.
     * @param siteNames 
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
     * TODO: Read (String[]) siteNames from metadata.
     * @param siteNames 
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
    public void plotIncidencePerCycle()
    {
        plotIncidencePerCycle("") ;
    }
    
    /**
     * Plots site-specific STI incidence pere head of population over time.
     * @param siteName 
     */
    public void plotIncidencePerCycle(String siteName)
    {
        ArrayList<Object> incidenceReport = reporter.prepareIncidenceReport(siteName) ;
        
        plotCycleValue("incidence", incidenceReport) ;
    }
    
    
    public void plotIncidencePerCycle(String[] siteNames)
    {
        ArrayList<ArrayList<Object>> incidenceReports = new ArrayList<ArrayList<Object>>() ;
        
        for (String siteName : siteNames)
            incidenceReports.add(reporter.prepareIncidenceReport(siteName)) ;
        
        multiPlotCycleValue("incidence", incidenceReports, siteNames) ;
    }
    
    
    /**
     * Plots STI incidents per head of population over time.
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
        ArrayList<Object> incidenceReport = reporter.prepareIncidenceReport(siteName) ;
        LOGGER.log(Level.INFO, "{0}", incidenceReport);
        plotCycleValue(NOTIFICATION, incidenceReport) ;
    }
    
    
    public void plotNotificationPerCycle(String[] siteNames)
    {
        ArrayList<ArrayList<Object>> incidenceReports = new ArrayList<ArrayList<Object>>() ;
        
        for (String siteName : siteNames)
            incidenceReports.add(reporter.prepareIncidenceReport(siteName)) ;
        
        multiPlotCycleValue(NOTIFICATION, incidenceReports, siteNames) ;
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
            legendArray[i] = legend.get(i) ;
        multiPlotCycleValue(multiPlotNames,multiPlotReports,legendArray) ;
        
    }
    
    
//            (boolean plotPrevalence, boolean plotSymptomatic, boolean plotCoPrevalence, boolean plotCoSymptomatic, 
//            boolean plotRectum, boolean plotUrethra, boolean plotPharynx)
//            boolean plotPharynxSymptomPrevalence, boolean plotIncidence, boolean plotRectumIncidence, boolean plotUrethraIncidents, 
//            boolean plotPharynxIncidents)

    /**
     * Plots prevalence from different saved simulations on same axes.
     * @param simNames 
     */
    public void coplotPrevalence(String[] simNames)
    {
        ArrayList<ArrayList<Object>> prevalenceReportList = new ArrayList<ArrayList<Object>>() ;
        //ArrayList<String> coplotNames = new ArrayList<String>() ;
        ArrayList<String> legend = new ArrayList<String>() ;
        
        // Include this Reporter
        prevalenceReportList.add(reporter.preparePrevalenceReport()) ;
        int nbCycles = Integer.valueOf(reporter.getMetaDatum("Community.MAX_CYCLES")) ;
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
                cycleValue += Double.valueOf(Reporter.extractValue(PREVALENCE,String.valueOf(prevalenceReport.get(cycle)))) ;
            cycleString = Reporter.addReportProperty(PREVALENCE, cycleValue/nbReports) ;
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
        prevalenceRecordList.put(reporter.getSimName(),Double.valueOf(Reporter.extractValue(PREVALENCE, finalRecord))) ;
        legend.add(reporter.getSimName()) ;
        
        // Add Reporters corresponding to simNames
        for (String simName : simNames)
        {
            ScreeningReporter screeningReporter = new ScreeningReporter(simName,reporter.getMetaDatum("Community.FILE_PATH")) ;
            prevalenceReport = screeningReporter.preparePrevalenceReport() ;
            reportSize = prevalenceReport.size() ;
            finalRecord = (String) prevalenceReport.get(reportSize) ;
            prevalenceRecordList.put(simName,Double.valueOf(Reporter.extractValue(PREVALENCE, finalRecord))) ;
            legend.add(simName) ;
        }
        //LOGGER.log(Level.INFO, "{0}", prevalenceReport);
        String[] legendArray =  new String[legend.size()] ;
        for (int i = 0 ; i < legendArray.length ; i++ )
            legendArray[i] = legend.get(i) ;
        plotHashMap("simulation", PREVALENCE, prevalenceRecordList) ;
    
    }
    
}
