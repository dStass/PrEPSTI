/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter.presenter;

import reporter.* ;
import community.Community ;

import java.util.ArrayList ;
import java.util.HashMap;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Class to plot data from EncounterReporter
 * @author MichaelWalker
 */


public class EncounterPresenter extends Presenter {
    
    static private String[] simNames 
            = new String[] {"NoPrepCalibration74aPop40000Cycles5000"} ; //,"NoPrepCalibration74bPop40000Cycles5000","NoPrepCalibration74cPop40000Cycles5000"} ;
    
    private EncounterReporter reporter ;
    
    public static void main(String[] args)
    {
        String simName = "TestPop40000Cycles100" ; // Community.NAME_ROOT ; // "introPrepCalibration48Pop40000Cycles7000" ; // args[0] ;
        //String simName = "NoPrepCalibration74aPop40000Cycles5000" ; // "NoPrepCalibration86Pop40000Cycles5000" ; // "introPrepCalibration48Pop40000Cycles7000" ; // args[0] ;
        //String simName = "neutral_calibration2Pop40000Cycles4000" ;
        //String simName = "RelationshipCalibrationPop40000Cycles200" ; // "NoPrepCalibration86Pop40000Cycles5000" ; // "introPrepCalibration48Pop40000Cycles7000" ; // args[0] ;
        //String simName = "AllSexualContactsPop40000Cycles1200" ;
        //String chartTitle = "infections_of_PrEP_users" ; // args[1] ;
        //String chartTitle = "proportion_of_Agents_had_CLAI" ; // args[1] ;
        String chartTitle = "transmissions" ;
        //String chartTitle = "incidence_rate" ;
        //String chartTitle = "condom_use_in_AI" ; // args[1] ;
        String reportFileName = "output/test/" ; // args[2] ;
        LOGGER.info(chartTitle) ;
        String[] siteNames  = new String[] {"Pharynx","Rectum","Urethra"} ;
        

        EncounterPresenter encounterPresenter = new EncounterPresenter(simName,chartTitle,reportFileName) ;
        //encounterPresenter.plotCondomUse();
        //encounterPresenter.plotProtection() ;
        //encounterPresenter.plotTransmissionsPerCycle(siteNames);
        //encounterPresenter.plotCumulativeAgentTransmissionReport() ;
        encounterPresenter.plotTransmissionsPerCycle(siteNames);
        //encounterPresenter.plotIncidenceYears(siteNames, 7, 2017) ;
        //encounterPresenter.plotNumberCondomlessYears(3, 0, 0, 2017, new String[] {"Casual","Regular","Monogomous"}) ;
        //encounterPresenter.plotNumberCondomlessReport(0, 6, 0, new String[] {"Casual","Regular","Monogomous"}) ;
        //encounterPresenter.plotPercentAgentCondomlessReport(new String[] {"Casual","Regular","Monogomous"}, 0, 6, 0, "", false) ;
        //encounterPresenter.plotPercentAgentCondomlessReport(new String[] {"Casual","Regular","Monogomous"}, 0, 6, 0, "statusHIV", true) ; 
        //encounterPresenter.plotPercentAgentCondomlessYears(new String[] {"Casual","Regular","Monogomous"}, 3, 6, 0, 2017, "", false) ; 
        //encounterPresenter.plotNumberAgentTransmissionReport("statusHIV") ;
        //encounterPresenter.plotFromSiteToSite(new String[] {"Rectum","Urethra","Pharynx"});
        //encounterPresenter.plotReceiveSortPrepStatusReport("true") ;

        //String methodName = args[3] ;
        //Method method = EncounterPresenter.class.getMethod(methodName) ;

        //method.invoke(encounterPresenter, (Object[]) Arrays.copyOfRange(args,4,args.length)) ;
    }
    
    public EncounterPresenter()
    {
        super() ;   
    }
    
    public EncounterPresenter(String applicationTitle, String chartTitle)
    {
        super(applicationTitle, chartTitle);
    }
    
    public EncounterPresenter(String simName, String chartTitle, String reportFilePath)
    {
        super(simName,chartTitle) ;
        setReporter(new EncounterReporter(simName,reportFilePath)) ;
    }
    
    public EncounterPresenter(String applicationTitle, String chartTitle, EncounterReporter reporter)
    {
        super(applicationTitle,chartTitle,reporter) ;
        setReporter(reporter) ;
    }
    
    /**
     * Overrides super.setReporter() because reporter is now PopulationReporter
     * @param reporter 
     */
    public void setReporter(EncounterReporter reporter)
    {
        this.reporter = reporter ;
    }

    /**
     * Generates HashMap whose keys are Site names (String) and values the
     * number of times an infection has been transmitted from that site
     * TODO: use Agent.getSiteNames() to automatically generate Site names
     * @param siteNames (String[]) names of body Sites to consider
     */
    public void plotTransmittingSites(String[] siteNames)
    {
        // HashMap to be plotted
        HashMap<Object,Number> transmittingSites = new HashMap<Object,Number>() ;
        for (String name : siteNames)
            transmittingSites.put(name, 0) ;

        // For counting infected Sites
        int count ;
        
        // To record whether given Site was responsible for transmission
        ArrayList<Object> infectedStatus ;
        
        ArrayList<String> transmissionReport = reporter.prepareTransmissionReport() ;
        
        for (String report : transmissionReport)
            for (String name : siteNames)
            {
                infectedStatus = Reporter.extractAllValues(name, report, 0) ;
                count = 0 ;
                for (Object site : infectedStatus)
                    count += Integer.valueOf((String) site) ;
                count += transmittingSites.get(name).intValue() ;
                transmittingSites.put(name, count) ;
            }
        
        plotHashMap("Site","Ongoing transmissions",transmittingSites) ;
        
    }
    
    /**
     * Plots the number of site-specific transmissions
     * @param siteNames - The Sites to be considered in this plot.
     */
    public void plotFromSiteToSite(String[] siteNames)
    {
        // HashMap to be plotted
        // (String) key has format infectedsiteToReceivingsite
        HashMap<Object,Number> fromSiteToSiteReport = reporter.prepareFromSiteToSiteReport(siteNames) ;
        
        plotHashMap("Site to Site","transmissions",fromSiteToSiteReport) ;        
    }
    
    /**
     * Plots the number of Agents responsible for the given number of transmissions.
     */
    public void plotNumberAgentTransmissionReport()
    {
        HashMap<Object,Number> numberAgentTransmissionReport = reporter.prepareNumberAgentTransmissionReport() ;
        
        plotSpline("Number of transmissions","No of agents",numberAgentTransmissionReport) ;
    }
    
    /**
     * Plots the number of Agents responsible for the given number of transmissions
     * after sorting them for sortingProperty.
     */
    public void plotNumberAgentTransmissionReport(String sortingProperty)
    {
        HashMap<Object,HashMap<Object,Number>> numberAgentTransmissionReport = reporter.prepareNumberAgentTransmissionReport(sortingProperty) ;
        LOGGER.log(Level.INFO, "{0}", numberAgentTransmissionReport);
        
        //(HashMap) unsortedKey maps to (Number[]) values in order determined by 
        // looping through keySet.
        HashMap<Object,Number[]> plotHashMap = prepareSortedHashMap(numberAgentTransmissionReport) ;
        LOGGER.log(Level.INFO, "{0}", plotHashMap);
        
        String[] legend = new String[numberAgentTransmissionReport.keySet().size()] ;
        int nameIndex = 0 ;
        for (Object scoreName : numberAgentTransmissionReport.keySet())
        {
            legend[nameIndex] = sortingProperty + " " + scoreName.toString() ;
            nameIndex++ ;
        }
        
        plotSpline("Number of transmissions","Number of Agents",plotHashMap,legend) ;
    }
    
    /**
     * Plots the number of Agents responsible for at least the given number of transmissions.
     */
    public void plotCumulativeAgentTransmissionReport()
    {
        HashMap<Object,Number> cumulativeAgentTransmissionReport = reporter.prepareCumulativeAgentTransmissionReport() ;
        
        plotSpline("Cumulative number of transmissions","No of agents",cumulativeAgentTransmissionReport) ;
    }
    
    /**
     * Plots bar chart showing incidence at each Site for each of the last backYears
     * years counting back from lastYear.
     * @param siteNames
     * @param backYears
     * @param lastYear 
     */
    public void plotIncidenceYears(String[] siteNames, int backYears, int lastYear)
    {
        HashMap<Object,Number[]> incidenceRecordYears = new HashMap<Object,Number[]>() ;
        //reporter.prepareYearsIncidenceRecord(siteNames, backYears, lastYear) ;
        ArrayList<HashMap<Object,Number[]>> reports = new ArrayList<HashMap<Object,Number[]>>() ;
        
        for (String simulation : simNames)
        {
            EncounterReporter encounterReporter = new EncounterReporter(simulation,reporter.getFolderPath()) ;
            HashMap<Object,Number[]> report 
                    = encounterReporter.prepareYearsIncidenceRecord(siteNames, backYears, lastYear) ;
            reports.add((HashMap<Object,Number[]>) report.clone()) ;
        }
        incidenceRecordYears = Reporter.averagedHashMapReport(reports) ;
        
        String[] scoreNames = new String[siteNames.length + 1] ;
        for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
            scoreNames[siteIndex] = siteNames[siteIndex] ;
        scoreNames[siteNames.length] = "all__" ;
        
        plotHashMap("Year", scoreNames, incidenceRecordYears) ;
    }
    
    
    /**
     * Plots bar chart showing prevalence of all siteNames and total prevalence.
     * TODO: Read (String[]) siteNames from metadata.
     * @param siteNames 
     */
    public void plotFinalTransmissions()
    {
        //String siteNames = reporter.getMetaDatum("Community.SITE_NAMES") ;
        //ArrayList<String> sitesList = Arrays(siteNames) ;
        
        plotFinalTransmissions(new String[] {"Pharynx","Rectum","Urethra"}) ;
    }
    
    /**
     * Plots bar chart showing prevalence of requested siteNames and total prevalence.
     * @param siteNames 
     */
    public void plotFinalTransmissions(String[] siteNames)
    {
        HashMap<Object,Number> finalTransmissionsRecord = reporter.prepareFinalTransmissionsRecord(siteNames) ;
        
        
        plotHashMap("Sites","prevalence",finalTransmissionsRecord) ;        
    }
    
    
    
    /**
     * Plots the number of transmissions in a given cycle.
     */
    public void plotTransmissionsPerCycle(String siteName)
    {
        ArrayList<Object> nbTransmissionReport = new ArrayList<Object>() ;
        //reporter.prepareTransmissionCountReport(siteName) ;
        
        ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>() ;
        for (String simulation : simNames)
        {
            EncounterReporter encounterReporter = new EncounterReporter(simulation,reporter.getFolderPath()) ;
            ArrayList<Object> transmissionReport = encounterReporter.prepareTransmissionCountReport(siteName) ;
            
            reports.add((ArrayList<Object>) transmissionReport.clone()) ;
        }
        nbTransmissionReport = Reporter.averagedReport(reports,"transmission") ;
        
        plotCycleValue("transmission", nbTransmissionReport) ;
    }
    
    /**
     * Plots the number of transmissions in a given cycle for each siteName.
     */
    public void plotTransmissionsPerCycle(String[] siteNames)
    {
        ArrayList<ArrayList<Object>> nbTransmissionsReports = new ArrayList<ArrayList<Object>>() ;
        
        for (String siteName : siteNames)
        {
            ArrayList<ArrayList<Object>> reports = new ArrayList<ArrayList<Object>>() ;
            for (String simulation : simNames)
            {
                EncounterReporter encounterReporter = new EncounterReporter(simulation,reporter.getFolderPath()) ;
                reports.add(encounterReporter.prepareTransmissionCountReport(siteName)) ;
            }
            nbTransmissionsReports.add(Reporter.averagedReport(reports,"transmission")) ;
        }
        
        multiPlotCycleValue("transmission",nbTransmissionsReports,siteNames) ;
        
    }

    /**
     * Plots the proportion of opportunities for condom use for which a condom is 
     * actually used in a given cycle.
     */
    public void plotCondomUse()
    {
        ArrayList<Object> condomUseReport = reporter.prepareCondomUseReport() ;
        
        plotCycleValue("proportion", condomUseReport) ;
    }
    
    
    public void plotProtection()
    {
        PopulationReporter populationReporter = new PopulationReporter(applicationTitle,Community.FILE_PATH) ;
        LOGGER.info("prepareBirthReport()");
        ArrayList<String> census = populationReporter.prepareBirthReport() ;
        LOGGER.log(Level.INFO, "{0}", census);
        plotProtection(census) ;
    }
    
    public void plotProtection(ArrayList<String> census)
    {
        ArrayList<String> practices = new ArrayList<String>() ;
        practices.add("condomOnly") ;
        practices.add("onlySeroPosition") ;
        //practices.add("onlySeroSort") ;
        practices.add("condomSeroPosition") ;
        //practices.add("condomSeroSort") ;
        practices.add("unprotected") ;
        //LOGGER.log(Level.INFO, "{0}", practices);
        plotProtection(census, practices) ;
    }
    
    /**
     * Plots the combinations of protection-related practices used as a 
     * proportion of condom-relevant contacts per cycle.
     * @param census
     * @param practices 
     */
    public void plotProtection(ArrayList<String> census, ArrayList<String> practices)
    {
        ArrayList<Object> protectionReport = reporter.prepareProtectionReport(census) ;
        
        multiPlotCycleValue(practices, protectionReport) ;
    }
            

    /**
     * Produces a scatter plot of which Agents were infected by which other Agents.
     */
    public void plotAgentToAgent()
    {
        HashMap<Object,ArrayList<Object>> transmittingAgentsReport = reporter.prepareAgentToAgentRecord() ;
        plotHashMapScatter("infectious agent", "receiving agent", transmittingAgentsReport ) ;
    }
    
    /**
     * Plots the percent of Agents who have been involved in each subclass of Relationship 
     * who have partaken in condomless anal intercourse within those Relationships.
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays 
     * @param concordanceName 
     * @param concordant 
     */
    public void plotPercentAgentCondomlessReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, String concordanceName, boolean concordant)
    {
        int endCycle = reporter.getMaxCycles() ;
        
        plotPercentAgentCondomlessReport(relationshipClassNames, backYears, backMonths, backDays, endCycle, concordanceName, concordant) ;
    }
    
    /**
     * Plots the percent of Agents who have been involved in each subclass of Relationship 
     * who have partaken in condomless anal intercourse within those Relationships.
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays 
     * @param endCycle 
     * @param concordanceName 
     * @param concordant 
     */
    public void plotPercentAgentCondomlessReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle, String concordanceName, boolean concordant)
    {
        HashMap<Object,Number> percentAgentCondomlessReport 
                = reporter.preparePercentAgentCondomlessReport(relationshipClassNames, backYears, backMonths, backDays, endCycle, concordanceName, concordant) ;
        
        if (!concordanceName.isEmpty())
            chartTitle += "_" + concordanceName + "_" + String.valueOf(concordant) ;
        plotHashMap("Relationship class","percentage engaged in CLAI",percentAgentCondomlessReport) ;
    }
    
    public void plotPercentAgentCondomlessYears(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int lastYear, String concordanceName, boolean concordant)
    {
        HashMap<Object,Number[]> percentAgentCondomlessYears 
                = reporter.preparePercentAgentCondomlessYears(relationshipClassNames, backYears, backMonths, backDays, lastYear, concordanceName, concordant) ;
        
        plotHashMap("Year", relationshipClassNames, percentAgentCondomlessYears) ;
    }
    /**
     * Plots the proportion of Agents who have either always or not always used a 
     * condom during anal sex, or who never had anal sex during the given time period.
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param relationshipClazzNames 
     */
    public void plotNumberCondomlessReport(int backYears, int backMonths, int backDays, String[] relationshipClazzNames)
    {
        HashMap<Object,Number[]> numberCondomlessReport 
                = reporter.prepareNumberCondomlessReport(backYears, backMonths, backDays, relationshipClazzNames) ;
        plotHashMap("condom use",relationshipClazzNames,numberCondomlessReport) ;
    }
    
    /**
     * Year-by-year plot of proportion of Agents who have either always or not always used a 
     * condom during anal sex, or who never had anal sex during the given time period.
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param lastYear
     * @param relationshipClazzNames 
     */
    public void plotNumberCondomlessYears(int backYears, int backMonths, int backDays, int lastYear, String[] relationshipClazzNames)
    {
        HashMap<Object,HashMap<Object,Number[]>> 
    numberCondomlessYears = reporter.prepareNumberCondomlessYears(relationshipClazzNames, backYears, backMonths, backDays, lastYear) ;
        
        HashMap<Object,Number[]> yearlyReport = new HashMap<Object,Number[]>() ;
        
        String[] condomStati = new String[] {"always","not_always","no_AI"} ;
        
        String[] scoreNames = new String[3*relationshipClazzNames.length] ;
        
        for (Object yearKey : numberCondomlessYears.keySet())
        {
            Number[] scores = new Number[scoreNames.length] ;
            for (int relationshipIndex = 0 ; relationshipIndex < relationshipClazzNames.length ; relationshipIndex++ )
            {
                String relationshipClazz = relationshipClazzNames[relationshipIndex] ;
                // Copy Number[] from numberCondomlessYears.get(yearKey).get(relationshipClazz) to scores
                System.arraycopy(numberCondomlessYears.get(yearKey).get(relationshipClazz), 0, scores, 3 * relationshipIndex, condomStati.length);
            }
            yearlyReport.put(yearKey, (Number[]) scores.clone()) ;    
        }
        
        int index = 0 ;
        for (String relationshipClazz : relationshipClazzNames)
            for (String status : condomStati)
            {
                scoreNames[index] = relationshipClazz + GROUP + status ;
                index++ ;
            }
        
        plotHashMap("Year", scoreNames, yearlyReport) ;
    }
    
    /**
     * plotAgentToAgentNetwork() draws network of Agents infecting other Agents .
     */
    public void plotAgentToAgentNetwork()
    {
        HashMap<Object,HashMap<Object,ArrayList<Object>>> transmittingAgentsReport = reporter.prepareAgentToAgentReport() ;
        HashMap<Object,HashMap<Object,ArrayList<Object>>> invertedTransmittingAgentsReport 
                = Reporter.invertHashMapHashMap(transmittingAgentsReport) ;
        
        ArrayList<HashMap<Object,ArrayList<Object>>> plottingAgentsReport = new ArrayList<HashMap<Object,ArrayList<Object>>>() ;
        for (int cycle = 0 ; cycle < invertedTransmittingAgentsReport.keySet().size() ; cycle++ )
        {
            if (invertedTransmittingAgentsReport.keySet().contains(cycle))
            {
                plottingAgentsReport.add(invertedTransmittingAgentsReport.get(cycle)) ;
                //LOGGER.log(Level.INFO, "{0} {1}", new Object[] {cycle,invertedTransmittingAgentsReport.get(cycle)});
            }
            else
                plottingAgentsReport.add(new HashMap<Object,ArrayList<Object>>()) ;
        }
        //LOGGER.log(Level.INFO, "{0}", transmittingAgentsReport);
        callPlotNetwork("cycle", "agentId", plottingAgentsReport) ;    // (HashMap<Number,HashMap<Number,ArrayList<Number>>>) 
    }
    
    /*
    Everything below here are conditional plots.
    */
    
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
                = reporter.prepareReceiveCountReport(invertedPrepStatusReport) ;
        LOGGER.log(Level.INFO, "{0}", nbTransmissionReport);
        LOGGER.info("plotCycleValue");
        plotEventsPerCycle("nbTransmissions", nbTransmissionReport) ;
    }
    
    
    
}
