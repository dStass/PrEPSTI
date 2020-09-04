/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PRSP.PrEPSTI.reporter.presenter;

import PRSP.PrEPSTI.reporter.* ;
//import community.Community ;

import java.util.ArrayList ;
//import java.util.Arrays;
import java.util.HashMap;
import java.util.Collections ;
//import java.lang.reflect.*;
import java.util.logging.Level;

/**
 * Class to plot data from EncounterReporter
 * @author MichaelWalker
 */


public class EncounterPresenter extends Presenter {
    
    //static private String[] simNames = new String[] {"serosort2Pop10000Cycles2190"} ; //,"from2007symptom9gPop40000Cycles5840"} ;
    //static String[] simNames = new String[] {"newSortCasual2aPop40000Cycles730"} ; //,"seek68bPop40000Cycles1825","seek68cPop40000Cycles1825","seek68dPop40000Cycles1825"} ; //,"seek53ePop40000Cycles1825",
      //      "seek53fPop40000Cycles1825","seek53gPop40000Cycles1825","seek53hPop40000Cycles1825","seek53iPop40000Cycles1825","seek53jPop40000Cycles1825"} ;
    //static String[] simNames = new String[] {"to2017seek71aPop40000Cycles5475","to2017seek65bPop40000Cycles5475","to2017seek65cPop40000Cycles5475","to2017seek65dPop40000Cycles5475","to2017seek65ePop40000Cycles5475",
    static String[] simNames ;
    
    private EncounterReporter reporter ;
    
    public static void main(String[] args)
    {
        //String chartTitle = "infections_of_PrEP_users" ; // args[1] ;
        //String chartTitle = "proportion_of_Agents_had_discordant_CLAI" ; // args[1] ;
        //String chartTitle = "proportion_of_Agents_had_CLAI" ; // args[1] ;
        //String chartTitle = "condom use universal" ;
        //String chartTitle = "new infections" ;
        //String chartTitle = "incidence_rate (per 100 MSM)" ;
        String chartTitle = "site to site transmission" ;
        //String chartTitle = "protection" ; // args[1] ;
        //String chartTitle = "condom_coverage" ; // args[1] ;
        //String reportFileName = "output/untouchable/" ; // args[2] ;
        String reportFileName = "output/test/" ; // args[2] ;
        //String reportFileName = "output/prePrEP/" ; // args[2] ;
        //String reportFileName = "output/prep/" ; // args[2] ;
        //String reportFileName = "output/year2007/" ; // args[2] ;
        //String reportFileName = "output/year2010/" ; // args[2] ;
        //String reportFileName = "output/year2012/" ; // args[2] ;
        // LOGGER.info(chartTitle) ;
        String[] siteNames  = new String[] {"Pharynx","Rectum","Urethra"} ;
        
        String prefix = "to2019u60gradual49" ;
        String suffix = "Pop40000Cycles1460" ;
        //String prefix = "to2019u60gradual49" ;
        //String suffix = "Pop40000Cycles5475" ;
        ArrayList<String> simNameList = new ArrayList<String>() ;
        //String letter0 = "" ;
        for (String letter0 : new String[] {"a","b","c","d","e","f","g","h","i","j"})
            for (String letter1 : new String[] {"a","b","c","d","e"})
                simNameList.add(prefix + letter0 + letter1 + suffix) ;
        
        simNames = simNameList.toArray(new String[] {}) ;
        
        String simName = "Qibin1p0aPop40000Cycles1460" ;
        //String simName = simNames[0] ;

        EncounterPresenter encounterPresenter = new EncounterPresenter(simName,chartTitle,reportFileName) ;
        //encounterPresenter.plotYearsCondomUseReport(6,2012) ;
        //encounterPresenter.plotCondomUse();
        //encounterPresenter.plotProtection() ;
        //encounterPresenter.plotTransmissionsPerCycle(siteNames);
        //encounterPresenter.plotFinalTransmissions(siteNames);
        //encounterPresenter.plotFinalIncidenceRecord(siteNames, 0, Reporter.DAYS_PER_YEAR) ;
        //encounterPresenter.plotSortedFinalIncidenceRecord(siteNames, 0, Reporter.DAYS_PER_YEAR,"statusHIV") ;
        //encounterPresenter.plotCumulativeAgentTransmissionReport() ;
        //encounterPresenter.plotIncidenceYears(siteNames, 13, 2019, "") ;
        //encounterPresenter.plotNumberCondomlessYears(3, 0, 0, 2017, new String[] {"Casual","Regular","Monogomous"}) ;
        //encounterPresenter.plotNumberCondomlessReport(0, 6, 0, new String[] {"Casual","Regular","Monogomous"}) ;
        //encounterPresenter.plotPercentAgentCondomlessReport(new String[] {"Casual","Regular","Monogomous"}, 0, 6, 0, "", false,"") ;
        //encounterPresenter.plotYearsCondomUseReport(13, 2019, new String[] {PROPORTION}) ;
        //encounterPresenter.plotPercentAgentCondomlessReport(new String[] {"Casual","Regular","Monogomous"}, 0, 6, 0, "statusHIV", true) ; 
        //encounterPresenter.plotPercentAgentCondomlessYears(new String[] {"Casual","Regular","Monogomous"}, 3, 2017, "statusHIV", false, "statusHIV") ; 
        //encounterPresenter.plotNumberAgentTransmissionReport("statusHIV") ;
        encounterPresenter.plotFromSiteToSite(siteNames, 730) ; // (new String[] {"Rectum","Urethra"});
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
     * Plots the number of times an infection has been transmitted from each site
     * TODO: use Agent.getSiteNames() to automatically generate Site names
     * @param siteNames (String[]) names of body Sites to consider
     */
    public void plotTransmittingSites(String[] siteNames)
    {
        // HashMap to be plotted
        HashMap<Comparable<?>,Number> transmittingSites = new HashMap<Comparable<?>,Number>() ;
        for (String name : siteNames)
            transmittingSites.put(name, 0) ;

        // For counting infected Sites
        int count ;
        
        // To record whether given Site was responsible for transmission
        ArrayList<Comparable<?>> infectedStatus ;
        
        ArrayList<String> transmissionReport = reporter.prepareTransmissionReport() ;
        
        for (String report : transmissionReport)
            for (String name : siteNames)
            {
                infectedStatus = Reporter.EXTRACT_ALL_VALUES(name, report, 0) ;
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
     * @param startCycle - The cycle to start counting transmission from.
     */
    public void plotFromSiteToSite(String[] siteNames, int startCycle)
    {
        // HashMap to be plotted
        // (String) key has format infectedsiteToReceivingsite
        HashMap<Comparable<?>,Number> fromSiteToSiteReport = reporter.prepareFromSiteToSiteReport(siteNames, startCycle) ;
      // logger.log(level.info,"{0}",fromSiteToSiteReport) ;
        plotHashMap("Site to Site","transmissions",fromSiteToSiteReport) ;        
    }
    
    /**
     * Plots the number of Agents responsible for the given number of transmissions.
     */
    public void plotNumberAgentTransmissionReport()
    {
        HashMap<Comparable,Number> numberAgentTransmissionReport = reporter.prepareNumberAgentTransmissionReport() ;
        
        plotSpline("Number of transmissions","No of agents",numberAgentTransmissionReport) ;
    }
    
    /**
     * Plots the number of Agents responsible for the given number of transmissions
     * after sorting them for sortingProperty.
     */
    public void plotNumberAgentTransmissionReport(String sortingProperty)
    {
        HashMap<Comparable,HashMap<Comparable,Number>> numberAgentTransmissionReport = reporter.prepareNumberAgentTransmissionReport(sortingProperty) ;
      // logger.log(level.info, "{0}", numberAgentTransmissionReport);
        
        //(HashMap) unsortedKey maps to (Number[]) values in order determined by 
        // looping through keySet.
        HashMap<Comparable<?>,Number[]> plotHashMap = prepareSortedHashMap(numberAgentTransmissionReport) ;
      // logger.log(level.info, "{0}", plotHashMap);
        
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
        HashMap<Comparable,Number> cumulativeAgentTransmissionReport = reporter.prepareCumulativeAgentTransmissionReport() ;
        
        plotSpline("Cumulative number of transmissions","No of agents",cumulativeAgentTransmissionReport) ;
    }
    
    /**
     * Plots the incidence during the final backMonths months and backDays days 
     * of the simulation.
     * @param siteNames
     * @param backMonths
     * @param backDays 
     */
    public void plotFinalIncidenceRecord(String[] siteNames, int backMonths, int backDays)
    {
        int endCycle = reporter.getMaxCycles() ;
        String finalIncidenceRecord = reporter.prepareFinalIncidenceRecord(siteNames, 0, backMonths, backDays, endCycle) ;
        //HashMap<Object,Number> finalIncidenceRecord = reporter.prepareFinalIncidenceRecord(siteNames, 0, backMonths, backDays, endCycle) ;
        ArrayList<String> siteList = Reporter.IDENTIFY_PROPERTIES(finalIncidenceRecord) ;
        
        String[] finalSiteNames = new String[siteList.size()] ;
        for (int siteIndex = 0 ; siteIndex < siteList.size() ; siteIndex++ )
            finalSiteNames[siteIndex] = siteList.get(siteIndex) ;
        
      // logger.log(level.info, "{0}", finalIncidenceRecord);
        
        callPlotChartDefault(finalSiteNames, EncounterReporter.INCIDENCE, "Site", finalIncidenceRecord) ;
        //plotValues("incidence", finalIncidenceRecord) ;        
    }
    
    /**
     * Plots the incidence during the final backMonths months and backDays days 
     * of the simulation.
     * @param siteNames
     * @param backMonths
     * @param backDays 
     */
    public void plotSortedFinalIncidenceRecord(String[] siteNames, int backMonths, int backDays, String sortingProperty)
    {
        int endCycle = reporter.getMaxCycles() ;
        String finalIncidenceRecord = reporter.prepareSortedFinalIncidenceRecord(siteNames, 0, backMonths, backDays, endCycle, sortingProperty) ;
        //HashMap<Object,Number> finalIncidenceRecord = reporter.prepareFinalIncidenceRecord(siteNames, 0, backMonths, backDays, endCycle) ;
            
      // logger.log(level.info, "{0}", finalIncidenceRecord);
        //plotHashMapString(finalIncidenceRecord, "incidence", "site", siteNames) ;        
        multiPlotValues(finalIncidenceRecord, EncounterReporter.INCIDENCE, "Site") ;
    }
    
    
    
    /**
     * Plots bar chart showing incidence at each Site for each of the last backYears
     * years counting back from lastYear.
     * @param siteNames
     * @param backYears
     * @param lastYear 
     * @param sortingProperty 
     */
    public void plotIncidenceYears(String[] siteNames, int backYears, int lastYear, String sortingProperty)
    {
        HashMap<Comparable<?>,String> incidenceRecordYears = new HashMap<Comparable<?>,String>() ;
        //reporter.prepareYearsIncidenceReport(siteNames, backYears, lastYear) ;
        ArrayList<HashMap<Comparable<?>,String>> reports = new ArrayList<HashMap<Comparable<?>,String>>() ;
        
        for (String simulation : simNames)
        {
            EncounterReporter encounterReporter = new EncounterReporter(simulation,reporter.getFolderPath()) ;
            HashMap<Comparable<?>,String> report = encounterReporter.prepareYearsIncidenceReport(siteNames, backYears, lastYear, sortingProperty) ;
            Reporter.CLEAR_REPORT_LIST() ; 
            reports.add((HashMap<Comparable<?>,String>) report.clone()) ;
            Reporter.DUMP_OUTPUT(EncounterReporter.INCIDENCE,simulation,reporter.getFolderPath(),report);
            //LOGGER.info(report.toString());
        }
        Reporter.WRITE_CSV_DISTRIBUTION(reports, EncounterReporter.INCIDENCE, simNames[0], "output/prep/") ;
        incidenceRecordYears = Reporter.PREPARE_MEAN_HASHMAP_REPORT(reports,"year","INCIDENCE",simNames[0]) ;
        
      // logger.log(level.info, "{0}", incidenceRecordYears);
        String[] scoreNames = Reporter.IDENTIFY_PROPERTIES(incidenceRecordYears.get(lastYear)).toArray(new String[0]) ;
        for (String property : scoreNames)
        {
            ArrayList<String> simNameList = new ArrayList<String>() ;
            Collections.addAll(simNameList, simNames) ;
            Reporter.MULTI_WRITE_CSV(simNameList, "year", property, "Incidence", reporter.getFolderPath()) ;
        }
        
        plotHashMapString(incidenceRecordYears,EncounterReporter.INCIDENCE,"year",scoreNames) ;
    }
    
    
    /**
     * Plots bar chart showing prevalence of all siteNames and total prevalence.
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
        HashMap<Comparable<?>,Number> finalTransmissionsRecord = reporter.prepareFinalTransmissionReport(siteNames) ;
            
      // logger.log(level.info, "{0}", finalTransmissionsRecord);
        plotHashMap("Sites","prevalence",finalTransmissionsRecord) ;        
    }
    
    /**
     * Plots bar chart showing prevalence of requested siteNames and total prevalence.
     * @param siteName 
     */
    public void plotFinalTransmissions(String siteName)
    {
        plotFinalTransmissions(new String[] {siteName}) ;
    }
    
    
    
    /**
     * Plots the number of transmissions in a given cycle.
     * @param siteName
     */
    public void plotTransmissionsPerCycle(String siteName)
    {
        ArrayList<String> nbTransmissionReport = new ArrayList<String>() ;
        //reporter.prepareTransmissionCountReport(siteName) ;
        
        ArrayList<ArrayList<String>> reports = new ArrayList<ArrayList<String>>() ;
        for (String simulation : simNames)
        {
            EncounterReporter encounterReporter = new EncounterReporter(simulation,reporter.getFolderPath()) ;
            ArrayList<String> transmissionReport = encounterReporter.prepareTransmissionCountReport(siteName) ;
            
            reports.add((ArrayList<String>) transmissionReport.clone()) ;
        }
        nbTransmissionReport = Reporter.AVERAGED_REPORT(reports,"transmission") ;
        
        plotCycleValue("transmission", nbTransmissionReport) ;
    }
    
    /**
     * Plots the number of transmissions in a given cycle for each siteName.
     * @param siteNames
     */
    public void plotTransmissionsPerCycle(String[] siteNames)
    {
        ArrayList<ArrayList<String>> nbTransmissionsReports = new ArrayList<ArrayList<String>>() ;
        
        for (String siteName : siteNames)
        {
            ArrayList<ArrayList<String>> reports = new ArrayList<ArrayList<String>>() ;
            for (String simulation : simNames)
            {
                EncounterReporter encounterReporter = new EncounterReporter(simulation,reporter.getFolderPath()) ;
                reports.add(encounterReporter.prepareTransmissionCountReport(siteName)) ;
            }
            nbTransmissionsReports.add(Reporter.AVERAGED_REPORT(reports,"transmission")) ;
        }
        
        multiPlotCycleValue("transmission",nbTransmissionsReports,siteNames) ;
        
    }

    /**
     * Plots the proportion of opportunities for condom use for which a condom is 
     * actually used in a given cycle.
     */
    public void plotCondomUse()
    {
        ArrayList<ArrayList<String>> condomReports = new ArrayList<ArrayList<String>>() ;
        for (String simName : simNames)
        {
            EncounterReporter encounterReporter = new EncounterReporter(simName,reporter.getFolderPath()) ;
            condomReports.add(encounterReporter.prepareCondomUseReport()) ;
            //coplotNames.add("prevalence") ;
        }
        ArrayList<String> meanCondomReport ;
        if (simNames.length > 1)
            meanCondomReport = Reporter.AVERAGED_REPORT(condomReports, PROPORTION) ;
        else
            meanCondomReport = condomReports.get(0) ;

        plotCycleValue(PROPORTION, meanCondomReport) ;
    }
    
    /**
     * Plots condom use on a year-by-year basis.
     * @param backYears
     * @param lastYear 
     */
    private void plotYearsCondomUseReport(int backYears, int lastYear, String[] reportProperties) 
    {
        ArrayList<HashMap<Comparable<?>,String>> reports = new ArrayList<HashMap<Comparable<?>,String>>() ;
        HashMap<Comparable<?>,String> hashMapReport = new HashMap<Comparable<?>,String>() ;
        for (String simulation : simNames)
        {
            EncounterReporter encounterReporter = new EncounterReporter(simulation,reporter.getFolderPath()) ;
            ArrayList<String> report = encounterReporter.prepareYearsCondomUseRecord(backYears, lastYear) ;
            Reporter.CLEAR_REPORT_LIST() ; 
            hashMapReport = Reporter.ARRAY_TO_HASHMAP(report, lastYear) ;
            reports.add((HashMap<Comparable<?>,String>) hashMapReport.clone()) ;
            Reporter.DUMP_OUTPUT("condomUse",simulation,reporter.getFolderPath(),report);
        }
        //Reporter.WRITE_CSV_DISTRIBUTION(reports, "at-risk incidence-rate", simNames[0], "output/prep/") ;
        
        HashMap<Comparable<?>,String> yearsCondomUseReport = Reporter.PREPARE_MEAN_HASHMAP_REPORT(reports,"year","condomUse",simNames[0]) ;
      // logger.log(level.info, "{0}", yearsCondomUseReport) ;
        plotHashMapString(yearsCondomUseReport,"","year",reportProperties) ;
    }
    
    /**
     * Plots condom use on a year-by-year basis.
     * @param backYears
     * @param lastYear 
     */
    private void plotYearsDisclosureReport(int backYears, int lastYear, String[] reportProperties) 
    {
        ArrayList<HashMap<Comparable<?>,String>> reports = new ArrayList<HashMap<Comparable<?>,String>>() ;
        HashMap<Comparable<?>,String> hashMapReport = new HashMap<Comparable<?>,String>() ;
        for (String simulation : simNames)
        {
            EncounterReporter encounterReporter = new EncounterReporter(simulation,reporter.getFolderPath()) ;
            HashMap<Comparable<?>,String> report = encounterReporter.prepareYearsDisclosureReport(backYears, lastYear) ;
            Reporter.CLEAR_REPORT_LIST() ; 
            reports.add((HashMap<Comparable<?>,String>) report.clone()) ;
            Reporter.DUMP_OUTPUT("condomUse",simulation,reporter.getFolderPath(),report);
        }
        //Reporter.WRITE_CSV_DISTRIBUTION(reports, "at-risk incidence-rate", simNames[0], "output/prep/") ;
        
        HashMap<Comparable<?>,String> yearsCondomUseReport = Reporter.PREPARE_MEAN_HASHMAP_REPORT(reports,"year","condomUse",simNames[0]) ;
      // logger.log(level.info, "{0}", yearsCondomUseReport) ;
        plotHashMapString(yearsCondomUseReport,"","year",reportProperties) ;
    }
    
    public void plotProtection()
    {
        PopulationReporter populationReporter = new PopulationReporter(applicationTitle,reporter.getFolderPath()) ;
        ArrayList<String> census = populationReporter.prepareBirthReport() ;
        //LOGGER.log(Level.INFO, "{0}", census);
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
        ArrayList<String> protectionReport = reporter.prepareProtectionReport(census) ;
        
        multiPlotCycleValue(practices, protectionReport) ;
    }
    
    private void plotMeanProtection()
    {
        ArrayList<String> practices = new ArrayList<String>() ;
        practices.add("condomOnly") ;
        practices.add("onlySeroPosition") ;
        //practices.add("onlySeroSort") ;
        practices.add("condomSeroPosition") ;
        //practices.add("condomSeroSort") ;
        practices.add("unprotected") ;
        //LOGGER.log(Level.INFO, "{0}", practices);
        
        ArrayList<String> meanProtectionReport ;
        ArrayList<ArrayList<String>> protectionList = new ArrayList<ArrayList<String>>() ;
        
        PopulationReporter populationReporter = new PopulationReporter(applicationTitle,reporter.getFolderPath()) ;
        ArrayList<String> census = populationReporter.prepareBirthReport() ;
        protectionList.add(reporter.prepareProtectionReport(census)) ;
        
        if (simNames.length > 1)
            meanProtectionReport = Reporter.AVERAGED_REPORT(protectionList, practices) ;
        else
            meanProtectionReport = protectionList.get(0) ;

        multiPlotCycleValue(practices, meanProtectionReport) ;
    }
            

    /**
     * Produces a scatter plot of which Agents were infected by which other Agents.
     */
    public void plotAgentToAgent()
    {
        HashMap<Comparable<?>,ArrayList<Comparable<?>>> transmittingAgentsReport = reporter.prepareAgentToAgentRecord() ;
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
    public void plotPercentAgentCondomlessReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, String concordanceName, boolean concordant, String sortingProperty)
    {
        int endCycle = reporter.getMaxCycles() ;
        
        plotPercentAgentCondomlessReport(relationshipClassNames, backYears, backMonths, backDays, endCycle, concordanceName, concordant, sortingProperty) ;
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
    public void plotPercentAgentCondomlessReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle, String concordanceName, boolean concordant, String sortingProperty)
    {
        String percentAgentCondomlessReport 
                = reporter.preparePercentAgentCondomlessReport(relationshipClassNames, backYears, backMonths, backDays, endCycle, concordanceName, concordant, sortingProperty) ;
        
        if (!concordanceName.isEmpty())
            chartTitle += "_" + concordanceName + "_" + String.valueOf(concordant) ;
        
        // LOGGER.info(percentAgentCondomlessReport.toString()) ;
        multiPlotValues(percentAgentCondomlessReport, "percentage engaged in CLAI","Class of Relationships") ;
    }
    
    public void plotPercentAgentCondomlessYears(String[] relationshipClassNames, int backYears, int lastYear, String concordanceName, boolean concordant, String sortingProperty)
    {
        HashMap<Comparable<?>,String> percentAgentCondomlessYears 
                = reporter.preparePercentAgentCondomlessYears(relationshipClassNames, backYears, lastYear, concordanceName, concordant, sortingProperty) ;
        
      // logger.log(level.info, "{0}", percentAgentCondomlessYears) ;
        //Reporter.WRITE_CSV(percentAgentCondomlessYears, "year", relationshipClassNames, "discordant_relationships", simNames[0], reporter.getFolderPath()) ;
        String[] legend = Reporter.IDENTIFY_PROPERTIES(percentAgentCondomlessYears.get(lastYear)).toArray(new String[0]) ;
        plotHashMapString(percentAgentCondomlessYears,"percentage engaged in CLAI","year", legend) ;
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
        HashMap<Object,String> numberCondomlessReport 
                = reporter.prepareNumberCondomlessReport(backYears, backMonths, backDays, relationshipClazzNames) ;
        // HashMap<Object,Number[]> 
        //ArrayList<String> legend = Reporter.IDENTIFY_PROPERTIES(numberCondomlessReport.values().iterator().next()) ;
        //plotHashMapString("condom use",relationshipClazzNames,numberCondomlessReport) ;
        
        plotHashMapString(numberCondomlessReport,"condom_use","",relationshipClazzNames) ;
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
        //HashMap<Object,HashMap<Object,Number[]>> 
        HashMap<Comparable,HashMap<Object,String>> 
    numberCondomlessYears = reporter.prepareNumberCondomlessYears(relationshipClazzNames, backYears, backMonths, backDays, lastYear) ;
        
        HashMap<Comparable<?>,Number[]> yearlyReport = new HashMap<Comparable<?>,Number[]>() ;
        
        String[] condomStati = new String[] {"always","not_always","no_AI"} ;
        
        String[] scoreNames = new String[3*relationshipClazzNames.length] ;
        
        for (Comparable yearKey : numberCondomlessYears.keySet())
        {
            HashMap<Object,String> numberCondomlessRelationship = numberCondomlessYears.get(yearKey) ;
            Number[] scores = new Number[scoreNames.length] ;
            for (int relationshipIndex = 0 ; relationshipIndex < relationshipClazzNames.length ; relationshipIndex++ )
            {
                String relationshipClazz = relationshipClazzNames[relationshipIndex] ;
                String condomlessRecord = numberCondomlessRelationship.get(relationshipClazz) ;
                for (int statusIndex = 0 ; statusIndex < condomStati.length ; statusIndex++ )
                {
                    String status = condomStati[statusIndex] ;
                    scores[statusIndex] = Double.valueOf(Reporter.EXTRACT_VALUE(status, condomlessRecord)) ;
                }
                // Copy Number[] from numberCondomlessYears.get(yearKey).get(relationshipClazz) to scores
                // System.arraycopy(numberCondomlessYears.get(yearKey).get(relationshipClazz), 0, scores, 3 * relationshipIndex, condomStati.length);
            }
            yearlyReport.put(yearKey, (Number[]) scores.clone()) ;    
        }
        
        int index = 0 ;
        for (String relationshipClazz : relationshipClazzNames)
        {
            for (String status : condomStati)
            {
                scoreNames[index] = relationshipClazz + GROUP + status ;
                index++ ;
            }
        }
        
        plotHashMap("Year", scoreNames, yearlyReport) ;
    }
    
    /**
     * plotAgentToAgentNetwork() draws network of Agents infecting other Agents .
     */
    public void plotAgentToAgentNetwork()
    {
        HashMap<Comparable<?>,HashMap<Comparable<?>,ArrayList<Comparable<?>>>> transmittingAgentsReport = reporter.prepareAgentToAgentReport() ;
        HashMap<Comparable<?>,HashMap<Comparable<?>,ArrayList<Comparable<?>>>> invertedTransmittingAgentsReport 
                = Reporter.INVERT_HASHMAP_HASHMAP(transmittingAgentsReport) ;
        
        ArrayList<HashMap<Comparable<?>,ArrayList<Comparable<?>>>> plottingAgentsReport = new ArrayList<HashMap<Comparable<?>,ArrayList<Comparable<?>>>>() ;
        for (int cycle = 0 ; cycle < invertedTransmittingAgentsReport.keySet().size() ; cycle++ )
        {
            if (invertedTransmittingAgentsReport.keySet().contains(cycle))
            {
                plottingAgentsReport.add(invertedTransmittingAgentsReport.get(cycle)) ;
                //LOGGER.log(Level.INFO, "{0} {1}", new Object[] {cycle,invertedTransmittingAgentsReport.get(cycle)});
            }
            else
                plottingAgentsReport.add(new HashMap<Comparable<?>,ArrayList<Comparable<?>>>()) ;
        }
        //LOGGER.log(Level.INFO, "{0}", transmittingAgentsReport);
        plotNetwork("cycle", "agentId", plottingAgentsReport) ;    // (HashMap<Number,HashMap<Number,ArrayList<Number>>>) 
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
        // LOGGER.info("prepareReceiveSortPrepStatusReport");
        HashMap<Comparable<?>,HashMap<Comparable<?>,ArrayList<Comparable<?>>>> receiveSortPrepStatusReport 
                = reporter.prepareReceiveSortPrepStatusReport(value) ;
        // LOGGER.log(Level.INFO, "{0}", receiveSortPrepStatusReport);
        HashMap<Comparable<?>,HashMap<Comparable<?>,ArrayList<Comparable<?>>>> invertedPrepStatusReport 
                = SortReporter.INVERT_HASHMAP_HASHMAP(receiveSortPrepStatusReport) ;
        // LOGGER.info("prepareTransmissionCountReport");
        ArrayList<ArrayList<Comparable<?>>> nbTransmissionReport 
                = reporter.prepareReceiveCountReport(invertedPrepStatusReport) ;
        // LOGGER.log(Level.INFO, "{0}", nbTransmissionReport);
        // LOGGER.info("plotCycleValue");
        plotEventsPerCycle("nbTransmissions", nbTransmissionReport) ;
    }
    
    
    
}
