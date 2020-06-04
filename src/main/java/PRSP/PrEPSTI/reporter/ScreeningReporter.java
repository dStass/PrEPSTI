/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PRSP.PrEPSTI.reporter;

import PRSP.PrEPSTI.agent.MSM;
import PRSP.PrEPSTI.community.Community;
import PRSP.PrEPSTI.site.Pharynx;
import PRSP.PrEPSTI.site.Rectum;
/**
 *
 * @author MichaelWalker
 */
import PRSP.PrEPSTI.site.Site;
import PRSP.PrEPSTI.site.Urethra;

import java.io.* ;
import java.util.ArrayList ;
import java.util.Arrays;
import java.util.Collections ;
import java.util.Comparator;
import java.util.HashSet ;
import java.util.Collections ;
import java.util.HashMap;
import java.util.logging.Level;

import java.lang.reflect.* ;


public class ScreeningReporter extends Reporter {

    static String INFECTED = "infected" ;
    static String SYMPTOMATIC = "symptomatic" ;
    static String TESTED = "tested" ;
    static String TREATED = "treated" ;
    static String NOTIFICATION = "notification" ;
    static String POSITIVITY = "positivity" ;
    
    static boolean WRITE_REPORT = true ;
    public ScreeningReporter()
    {
        
    }
    
    public ScreeningReporter(String simname, ArrayList<String> report) {
        super(simname, report);
        // TODO Auto-generated constructor stub
    }

    public ScreeningReporter(String simName, String reportFilePath)
    {
        super(simName,reportFilePath) ;
    }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param lastYear
     * @return Year-by-year report for backYears years on positivity on last day
     * of each year ending lastYear.
     */
    public HashMap<Comparable,String> 
        prepareYearsPositivityRecord(String[] siteNames, boolean unique, int backYears, int lastYear) 
        {
            //HashMap<Object,Number[]> positivityRecordYears = new HashMap<Object,Number[]>() ;
            HashMap<Comparable,String> positivityRecordYears = new HashMap<Comparable,String>() ;
            
            // Whether to save this Report to file
            boolean writeLocal = WRITE_REPORT ;
            // Do not save subreports
            WRITE_REPORT = false ;
            
            //Count from the last cycle of the simulation.
            int maxCycles = getMaxCycles() ;
            
            //HashMap<Object,Number[]> positivityRecord ;
            String positivityRecord = "" ;
            for (int year = 0 ; year < backYears ; year++ )
            {
                //Number[] yearlyPositivityRecord = new Number[siteNames.length] ;
               
                //endCycle = maxCycles - year * DAYS_PER_YEAR ;
                positivityRecord = prepareFinalNotificationsRecord(siteNames, unique, year, 0, DAYS_PER_YEAR, maxCycles);
               
                // Extract positivity
                int posLength = POSITIVITY.length() + 1 ;    // +1 is for ":"
                int posIndex = positivityRecord.indexOf(POSITIVITY) ;
                positivityRecord = positivityRecord.substring(posIndex + posLength) ;
            
                // [1] for positivity
                //for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
                  //  yearlyPositivityRecord[siteIndex] = positivityRecord.get(siteNames[siteIndex])[1] ;
                
                //positivityRecordYears.put(lastYear - year, (Number[]) yearlyPositivityRecord.clone()) ;
                
                positivityRecordYears.put(lastYear - year, positivityRecord) ;
            }
            if (writeLocal)
                WRITE_CSV_STRING(positivityRecordYears, "Year", "Positivity", simName, getFolderPath()) ;
            WRITE_REPORT = writeLocal ;
        
            return positivityRecordYears ;
        }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param lastYear
     * @return 
     */
    public HashMap<Comparable,String> 
        prepareYearsNotificationsRecord(String[] siteNames, int backYears, int lastYear) 
        {
            // Number[]
            return prepareYearsNotificationsRecord(siteNames, backYears, lastYear, new ArrayList<String>())  ;
        }
        
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param lastYear
     * @param sortedAgents
     * @return Year-by-year report for backYears years on notification on last day
     * of each year ending lastYear.
     */
    public HashMap<Comparable,String> 
        prepareYearsNotificationsRecord(String[] siteNames, int backYears, int lastYear, ArrayList<String> sortedAgents ) 
        {
            //HashMap<Object,String> notificationRecordYears = new HashMap<Object,String>() ;
            HashMap<Comparable,String> notificationsRecordYears = new HashMap<Comparable,String>() ;
            
            // Whether to save this Report to file
            boolean writeLocal = WRITE_REPORT ;
            // Do not save subreports
            WRITE_REPORT = false ;
            
            int maxCycles = getMaxCycles() ;
            
            String notificationsString ;
            String notificationsRecord ;
            //HashMap<Object,Number[]> notificationsRecord ;
            for (int year = 0 ; year < backYears ; year++ )
            {
                LOGGER.severe(String.valueOf(year));
                //Number[] yearlyNotificationsRecord = new Number[siteNames.length + 1] ;
                //String yearlyNotificationsString = "" ;
                //endCycle = maxCycles - year * DAYS_PER_YEAR ;
                notificationsRecord = prepareFinalNotificationsRecord(siteNames, false, year, 0, DAYS_PER_YEAR, maxCycles, sortedAgents);
                // [0] for positivity
                //for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
                  //  yearlyNotificationsRecord[siteIndex] = notificationsRecord.get(siteNames[siteIndex])[0] ;
                //yearlyNotificationsRecord[siteNames.length] = notificationsRecord.get("all")[0] ;
                int notLength = NOTIFICATION.length() + 1 ;    // +1 is for ":"
                int posIndex = notificationsRecord.indexOf(POSITIVITY) ;
                notificationsString = notificationsRecord.substring(notLength, posIndex) ;
                
                notificationsRecordYears.put(lastYear - year, notificationsString) ;
                //notificationRecordYears.put(lastYear - year, (Number[]) yearlyNotificationsRecord.clone()) ;
            }
            //if (writeLocal)
              //  WRITE_CSV(notificationRecordYears, "Year", siteNames, "yearlyNotifications", simName, getFolderPath()) ;
            WRITE_REPORT = writeLocal ;
            
            return notificationsRecordYears ;
        }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param lastYear
     * @param sortingProperty
     * @return 
     */
    public HashMap<Comparable,String> 
        prepareSortedYearsNotificationsRecord(String[] siteNames, int backYears, int lastYear, String sortingProperty)
        {
            HashMap<Comparable,String> sortedYearsNotificationsReport = new HashMap<Comparable,String>() ;
            HashMap<Object,HashMap<Comparable,String>> sortedYearsNotificationsRecord = new HashMap<Object,HashMap<Comparable,String>>() ;
            //HashMap<Object,HashMap<Object,Number[]>> sortedYearsNotificationsRecord = new HashMap<Object,HashMap<Object,Number[]>>() ;
            
            // Get Report of sortingValue mapping to agentIds
            PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
            HashMap<Object,ArrayList<String>> sortedAgentReport = populationReporter.agentIdSorted(sortingProperty) ;

            for (Object sortingValue : sortedAgentReport.keySet())
            {
              // logger.log(level.info, "value:{0} population:{1}", new Object[] {sortingValue,sortedAgentReport.get(sortingValue).size()});
                HashMap<Comparable,String> yearsNotificationsRecord = new HashMap<Comparable,String>() ; 
                yearsNotificationsRecord = prepareYearsNotificationsRecord(siteNames, backYears, lastYear, sortedAgentReport.get(sortingValue)) ;
                
                sortedYearsNotificationsRecord.put(sortingValue, (HashMap<Comparable,String>) yearsNotificationsRecord) ;
                //sortedYearsNotificationsRecord.put(sortingValue, (HashMap<Object,Number[]>) yearsNotificationsRecord.clone()) ;
            }
            
            // Put report in appropriate, text-based form
            String yearlyEntry ;
            String propertyEntry ;
            int firstYear = 1 + lastYear - backYears ;
            for (int year = firstYear ; year <= lastYear ; year++)
            {
                yearlyEntry = "" ;
                for (Object sortingValue : sortedYearsNotificationsRecord.keySet())
                {
                    String notifications = sortedYearsNotificationsRecord.get(sortingValue).get(year) ;
                    ArrayList<String> properties = IDENTIFY_PROPERTIES(notifications) ;
                    for (String property : properties)
                    {
                        propertyEntry = property.concat("__").concat(sortingValue.toString()) ;
                        yearlyEntry += Reporter.ADD_REPORT_PROPERTY(propertyEntry, Reporter.EXTRACT_VALUE(property,notifications)) ;
                    }
                }
                sortedYearsNotificationsReport.put(year, yearlyEntry) ;
            }
        
            return sortedYearsNotificationsReport ;
        }
        
    
    /**
     * 
     * @param siteNames
     * @param unique
     * @param backMonths
     * @param backDays
     * @return Records of final incidence for specified siteNames and in total.
     */
    public String prepareFinalNotificationsRecord(String[] siteNames, boolean unique, int backMonths, int backDays)
    {
        // HashMap<Object,Number[]>
        int endCycle = getMaxCycles() ;
        
        return prepareFinalNotificationsRecord(siteNames, unique, 0, backMonths, backDays, endCycle) ;
    }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return Records of final notifications for specified siteNames and in total.
     */
    public String prepareFinalNotificationsRecord(String[] siteNames, int backYears, int backMonths, int backDays, int endCycle)
    {
        //HashMap<Object,Number[]>
        return prepareFinalNotificationsRecord(siteNames, false, backYears, backMonths, backDays, endCycle) ;
    }
    
    /**
     * 
     * @param siteNames
     * @param unique  - Count one positive result per Agent. 
     * @param backYears
     * @param backMonths 
     * @param backDays 
     * @param notifications 
     * @param sortingProperty 
     * @return Records of final notifications for specified siteNames and in total.
     */
    //public ArrayList<ArrayList<Number>> prepareSortedFinalNotificationsRecord(String[] siteNames, boolean unique, int backYears, int backMonths, int backDays, int notifications, String sortingProperty)
    public HashMap<Object,String> prepareSortedFinalNotificationsRecord(String[] siteNames, boolean unique, int backYears, int backMonths, int backDays, int notifications, String sortingProperty)
    {
        //HashMap<Object,HashMap<Object,Number>>
        int endCycle = getMaxCycles() ;
        
        return prepareSortedFinalNotificationsRecord(siteNames, unique, backYears, backMonths, backDays, endCycle, notifications, sortingProperty) ;
    }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param endCycle
     * @param unique  - Count one positive result per Agent. 
     * @return Records of final notifications for specified siteNames and in total.
     */
    public HashMap<Object,String> prepareSortedFinalNotificationsRecord(String[] siteNames, boolean unique, int backYears, int backMonths, int backDays, int endCycle, int notifications, String sortingProperty)
    {
        HashMap<Object,String> sortedNotificationsReport = new HashMap<Object,String>() ;
        //HashMap<Object,HashMap<Object,Number>> sortedNotificationsReport = new HashMap<Object,HashMap<Object,Number>>() ;
        
        // Get Report of sortingValue mapping to agentIds
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        HashMap<Object,ArrayList<String>> sortedAgentReport = populationReporter.agentIdSorted(sortingProperty) ;
        
        for (Object sortingValue : sortedAgentReport.keySet())
        {
            // LOGGER.info(sortingValue.toString());
            //HashMap<Object,Number[]> finalNotificationsRecord = prepareFinalNotificationsRecord(siteNames,unique, backYears, backMonths, backDays, endCycle, sortedAgentReport.get(sortingValue)) ;
            String finalNotificationsRecord = prepareFinalNotificationsRecord(siteNames,unique, backYears, backMonths, backDays, endCycle, sortedAgentReport.get(sortingValue)) ;
            //HashMap<Object,Number> notificationsRecord = new HashMap<Object,Number>() ;
            //ArrayList<Number> notificationsList = new ArrayList<Number>() ;
            //for (Object site : finalNotificationsRecord.keySet())
              //  notificationsRecord.put(site, finalNotificationsRecord.get(site)[notifications]) ;
            
            //Finds notifications if notifications==0, otherwise finds positivity
            String finalRecord ;
            int notLength = NOTIFICATION.length() + 1 ;    // +1 is for ":"
            int posLength = POSITIVITY.length() + 1 ;    // +1 is for ":"
            int posIndex = finalNotificationsRecord.indexOf(POSITIVITY) ;
            if (notifications == 1)
                finalRecord = finalNotificationsRecord.substring(posIndex + posLength) ;
            else
                finalRecord = finalNotificationsRecord.substring(notLength, posIndex) ;
            
            sortedNotificationsReport.put(sortingValue, finalRecord) ;
            //sortedNotificationsReport.put(sortingValue, (HashMap<Object,Number>) notificationsRecord.clone()) ;
        }
        
        return sortedNotificationsReport ;
    }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param endCycle
     * @param unique  - Count one positive result per Agent. 
     * @return Records of final notifications for specified siteNames and in total.
     */
    public String prepareFinalNotificationsRecord(String[] siteNames, boolean unique, int backYears, int backMonths, int backDays, int endCycle)
    {
        //HashMap<Object,Number[]>
        return prepareFinalNotificationsRecord(siteNames, unique, backYears, backMonths, backDays, endCycle, new ArrayList<String>()) ;
    }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param endCycle
     * @param backMonths
     * @param backDays
     * @param unique  - Count one positive result per Agent if true. 
     * @param sortedAgents 
     * @return Records of final notifications for specified siteNames and in total.
     */
    public String prepareFinalNotificationsRecord(String[] siteNames, boolean unique, int backYears, int backMonths, int backDays, int endCycle, ArrayList<String> sortedAgents)
    {
        HashMap<Comparable,Number[]> finalNotifications = new HashMap<Comparable,Number[]>() ;
        String finalNotificationsString = ADD_REPORT_LABEL(NOTIFICATION) ;
        
        endCycle -= backYears * DAYS_PER_YEAR ;
        //double daysBetweenTests = 505 ;    // PROPORTION_HIV * 6 * 71 + (1-PROPORTION_HIV) * 6 * 85.5
        int notifications ;
        //double nbTests ;
        String record ;
        String positiveRecord ;
        //String finalIncidenceRecord ; // getFinalRecord() ;
        ArrayList<String> finalNotificationsReport = getBackCyclesReport(0, backMonths, backDays, endCycle) ;
        
        double population ;
        if (sortedAgents.isEmpty())    // if no sortingProperty specified take everyone
            population = getPopulation() ; // 15-64yo NSW males 3600000 ; // 
        else
        {
            ArrayList<Object> countedAgents = (ArrayList<Object>) sortedAgents.clone() ;
            PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
            ArrayList<String> agentsAliveReport = populationReporter.prepareAgentsAliveRecord(endCycle) ;
            countedAgents.retainAll(agentsAliveReport) ;
            population = countedAgents.size() ;
        }
        // LOGGER.info("population:" + String.valueOf(population));
        
        // Adjust for portion of year sampled //! and units of 100 person-years
        double denominator = ((double) getBackCycles(0,backMonths,backDays)*population)/(100*DAYS_PER_YEAR) ; // daysBetweenTests) ; //DAYS_PER_YEAR) ; // *population/100000
        for (String siteName : siteNames)
        {
            notifications = 0 ;
            ArrayList<Object> positiveAgents = new ArrayList<Object>() ;
            for (String finalIncidenceRecord : finalNotificationsReport)
            {
                //LOGGER.info(finalIncidenceRecord);
                String sortedRecord = "" ;
                if (sortedAgents.isEmpty())    // if no sortingProperty specified take everyone
                    sortedRecord = finalIncidenceRecord ;
                else    // take only those with value sortingValue for sortingProperty
                    sortedRecord = BOUNDED_STRING_FROM_ARRAY(AGENTID,sortedAgents,AGENTID,finalIncidenceRecord) ;
                
                // Count infected siteName
                record = BOUNDED_STRING_BY_CONTENTS(siteName,AGENTID,sortedRecord) ;    //  finalIncidenceRecord) ;  // 
                // Count Agents with positive tests
                positiveRecord = BOUNDED_STRING_BY_CONTENTS(TREATED,AGENTID,record) ;
                
                // Extract agentIds with positive result
                ArrayList<Comparable> positiveList = EXTRACT_ALL_VALUES(AGENTID,positiveRecord) ;
                notifications += positiveList.size() ;
                //notifications += COUNT_VALUE_INCIDENCE("treated","",record,0)[1] ;
                // Avoid double counting, ACCESS counts unique patient visits
                if (unique)
                    positiveList.removeAll(positiveAgents) ;
                positiveAgents.addAll(positiveList) ;
            }
            Number[] entry = new Number[] {notifications/denominator,positiveAgents.size()} ;
            finalNotifications.put(siteName,entry) ;
            finalNotificationsString += ADD_REPORT_PROPERTY(siteName, entry[0].doubleValue()) ;
        }
        notifications = 0 ;
        //nbTests = 0 ;
        ArrayList<Object> testedAgents = new ArrayList<Object>() ;
        ArrayList<Object> positiveAgents = new ArrayList<Object>() ;
        for (String finalNotificationsRecord : finalNotificationsReport)
        {
            String sortedRecord = "" ;
            if (sortedAgents.isEmpty())    // if no sortingProperty specified take everyone
                sortedRecord = finalNotificationsRecord ;
            else    // take only those with value sortingValue for sortingProperty
                sortedRecord = BOUNDED_STRING_FROM_ARRAY(AGENTID,sortedAgents,AGENTID,finalNotificationsRecord) ;

            notifications += COUNT_VALUE_INCIDENCE(TREATED,"",sortedRecord,0)[1] ;
            notifications += COUNT_VALUE_INCIDENCE(TESTED,TREATED,sortedRecord,0)[0] ;
            
            record = BOUNDED_STRING_BY_CONTENTS(TESTED,AGENTID,sortedRecord) ;
            ArrayList<Comparable> testedList = EXTRACT_ALL_VALUES(AGENTID,record) ;
            // Avoid double counting Agents
            if (unique)
                    testedList.removeAll(testedAgents) ;
            testedAgents.addAll(testedList) ;
            
            // Positive Agents
            record = BOUNDED_STRING_BY_CONTENTS(TREATED,AGENTID,finalNotificationsRecord) ;
            ArrayList<Comparable> positiveList = EXTRACT_ALL_VALUES(AGENTID,record) ;
            // Avoid double counting
            if (unique)
                    positiveList.removeAll(positiveAgents) ;
            positiveAgents.addAll(positiveList) ;
                
        }
        double nbTested = (double) testedAgents.size() ;
        double nbTreated = (double) positiveAgents.size() ;
        Number[] entry = new Number[] {notifications/denominator,nbTreated/nbTested} ;
        finalNotifications.put("all",entry) ;
        finalNotificationsString += ADD_REPORT_PROPERTY("all",entry[0].doubleValue()) ;
        finalNotificationsString += ADD_REPORT_LABEL(POSITIVITY) ;
        finalNotificationsString += ADD_REPORT_PROPERTY("all",entry[1].doubleValue()) ;
        
        // Correct siteName entries by nbTests
        for (String siteName : siteNames)
        {
            entry = finalNotifications.get(siteName) ;
            notifications = entry[1].intValue() ;
            entry[1] = (Integer) notifications/nbTested ;
            finalNotifications.put(siteName,entry) ;
            finalNotificationsString += ADD_REPORT_PROPERTY(siteName,entry[1].doubleValue()) ;
        }
        
        if (WRITE_REPORT)
            WRITE_CSV(finalNotifications, "Site", new String[] {"incidence",POSITIVITY}, "finalNotifications", simName, getFolderPath()) ;
        return finalNotificationsString ;
    }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param endCycle
     * @param backMonths
     * @param backDays
     * @param unique  - Count one positive result per Agent. 
     * @return Records of final notifications for specified siteNames and in total.
     */
    public HashMap<Comparable,Number[]> prepareFinalNotificationsRecord_Site(String[] siteNames, boolean unique, int backYears, int backMonths, int backDays, int endCycle)
    {
        HashMap<Comparable,Number[]> finalNotifications = new HashMap<Comparable,Number[]>() ;
        for (String siteName : siteNames)
            finalNotifications.put(siteName, new Number[] {0,0}) ;
        
        double population ;
        population = getPopulation() ; // Double.valueOf(getMetaDatum("Community.POPULATION")) ;
        
        String sortedRecord ;
        /**Sorting by statusHIV
        PopulationReporter populationReporter = new PopulationReporter(getMetaDatum("Community.NAME_ROOT"), getFolderPath()); 
        HashMap<Object,ArrayList<Object>> sortingReport = populationReporter.sortStatusHIV() ;
        ArrayList<Object> sortedAgents = sortingReport.get(TRUE) ;
        population = sortedAgents.size() ;
        
        */
        
        endCycle -= backYears * DAYS_PER_YEAR ;
        
        int notifications ;
        //double nbTests ;
        String record ;
        String testedString ;
        
        //String positiveRecord ;
        ArrayList<Object> positiveAgents ;
        ArrayList<Object> testedAgents ;
        
        ArrayList<String> testedRecordList ;
        //String finalIncidenceRecord ; // getFinalRecord() ;
        ArrayList<String> finalNotificationsReport = getBackCyclesReport(0, backMonths, backDays, endCycle) ;
        
        // Adjust for portion of year sampled //! and units of 100 person-years
        double denominator = ((double) getBackCycles(0,backMonths,backDays)*population)/(DAYS_PER_YEAR * 100) ; // *population/100000
        for (String siteName : siteNames)
        {
            notifications = 0 ;
            positiveAgents = new ArrayList<Object>() ;
            testedAgents = new ArrayList<Object>() ;
            
            for (String finalIncidenceRecord : finalNotificationsReport)
            {
                ArrayList<Object> testedList = new ArrayList<Object>() ;
                ArrayList<Object> positiveList = new ArrayList<Object>() ; 
                //sortedRecord = BOUNDED_STRING_FROM_ARRAY(AGENTID,sortedAgents,AGENTID,finalIncidenceRecord) ;
                // Count infected siteName
                record = BOUNDED_STRING_BY_CONTENTS(siteName,AGENTID,finalIncidenceRecord) ; // sortedRecord) ; // 
                // Count Agents with positive tests
                //positiveRecord = BOUNDED_STRING_BY_CONTENTS("treated",AGENTID,record) ;
                testedRecordList = EXTRACT_ARRAYLIST(record,AGENTID,TESTED) ;
                
                testedString = "" ;
                for (String testedRecord : testedRecordList)
                {
                    int siteIndex ; 
                    int oldIndex = 0 ;
                    int testedIndex = testedRecord.indexOf(TESTED) ;
                    //LOGGER.log(Level.INFO,"{0} {1}", new Object[] {siteName,testedRecord}) ;
                    while (testedIndex > 0)
                    {
                        testedString = testedRecord.substring(oldIndex, testedIndex);
                        siteIndex = -1 ;
                        //LOGGER.info(testedString) ;
                        // Determine indexOf tested siteName
                        for (String siteName2 : siteNames)
                            if (testedString.indexOf(siteName2) > siteIndex)
                                siteIndex = testedString.indexOf(siteName2) ;
                           
                        if (siteIndex >= 0)
                        {
                            testedString = testedString.substring(siteIndex) ;
                            if (testedString.startsWith(siteName))
                                break ;
                        }
                        // Prepare for next loop
                        oldIndex = testedIndex ;
                        testedIndex = testedRecord.indexOf(TESTED,testedIndex + 1) ;
                    }
                    // if siteName not tested 
                    if (testedIndex < 0)
                        continue ;
                    
                    String agentId = EXTRACT_VALUE(AGENTID,testedRecord) ;
                    testedList.add(agentId) ;
                    
                    // Extract agentIds with positive result
                    if (!(EXTRACT_VALUE(siteName,testedString).equals(CLEAR))) 
                        positiveList.add(agentId) ;
                }
                notifications += positiveList.size() ;
                //notifications += COUNT_VALUE_INCIDENCE("treated","",record,0)[1] ;
                
                // Avoid double counting, ACCESS counts unique patient visits
                if (unique)
                {
                    positiveList.removeAll(positiveAgents) ;
                    testedList.removeAll(testedAgents) ;
                }
                positiveAgents.addAll(positiveList) ;
                testedAgents.addAll(testedList) ;
            }
            //LOGGER.log(Level.INFO, "{0} {1}", new Object[] {denominator,testedAgents.size()});
            Number[] entry = new Number[] {notifications/denominator,((double) positiveAgents.size())/testedAgents.size()} ;
            finalNotifications.put(siteName,entry) ;
        }
        notifications = 0 ;
        //nbTests = 0 ;
        testedAgents = new ArrayList<Object>() ;
        positiveAgents = new ArrayList<Object>() ;
        for (String finalNotificationsRecord : finalNotificationsReport)
        {
            ArrayList<Comparable> testedList = new ArrayList<Comparable>() ;
            ArrayList<Comparable> positiveList = new ArrayList<Comparable>() ;
            
            notifications += COUNT_VALUE_INCIDENCE(TREATED,"",finalNotificationsRecord,0)[1] ;
            
            record = BOUNDED_STRING_BY_CONTENTS(TESTED,AGENTID,finalNotificationsRecord) ;
            testedList = EXTRACT_ALL_VALUES(AGENTID,record) ;
            // Avoid double counting Agents
            if (unique)
                    testedList.removeAll(testedAgents) ;
            testedAgents.addAll(testedList) ;
            
            // Positive Agents
            record = BOUNDED_STRING_BY_CONTENTS(TREATED,AGENTID,finalNotificationsRecord) ;
            positiveList = EXTRACT_ALL_VALUES(AGENTID,record) ;
            // Avoid double counting
            if (unique)
                    positiveList.removeAll(positiveAgents) ;
            positiveAgents.addAll(positiveList) ;
                
        }
        double nbTested = (double) testedAgents.size() ;
        double nbTreated = (double) positiveAgents.size() ;
        Number[] entry = new Number[] {notifications/denominator,nbTreated/nbTested} ;
        finalNotifications.put("all",entry) ;
        
        if (WRITE_REPORT)
            WRITE_CSV(finalNotifications, "Site", new String[] {"incidence","positivity"}, "finalNotifications", simName, getFolderPath()) ;
        return finalNotifications ;
    }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param lastYear
     * @param sortingProperty
     * @return Year-by-year report for backYears years on prevalence on last day
     * of each year ending lastYear.
     */
    public HashMap<Comparable,String> 
        prepareYearsPrevalenceRecord(String[] siteNames, int backYears, int lastYear, String sortingProperty) 
        {
            //HashMap<Object,Number[]> prevalenceRecordYears = new HashMap<Object,Number[]>() ;
            HashMap<Comparable,String> prevalenceRecordYears = new HashMap<Comparable,String>() ;
            
            int maxCycles = getMaxCycles() ;
            
            int endCycle ;
            String prevalenceRecord = "" ;
            //HashMap<Object,Number> prevalenceRecord ;
            for (int year = 0 ; year < backYears ; year++ )
            {
                //Number[] yearlyPrevalenceRecord = new Number[siteNames.length + 1] ;
               
                endCycle = maxCycles - year * DAYS_PER_YEAR ;
                if (sortingProperty.isEmpty())
                    prevalenceRecord += prepareFinalPrevalencesRecord(siteNames, endCycle);
                else
                    prevalenceRecord += prepareFinalPrevalencesSortedRecord(siteNames, sortingProperty, endCycle);
               
                //for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
                  //  yearlyPrevalenceRecord[siteIndex] = prevalenceRecord.get(siteNames[siteIndex]) ;
                //if (prevalenceRecord.containsKey("all"))
                  //  yearlyPrevalenceRecord[siteNames.length] = prevalenceRecord.get("all") ;
                
                prevalenceRecordYears.put(lastYear - year, prevalenceRecord) ;
                prevalenceRecord = "" ;
            }
            // LOGGER.info(prevalenceRecordYears.toString());
            
            return prevalenceRecordYears ;
        }
    
    /**
     * 
     * @param siteNames
     * @return Records of final prevalences for specified siteNames and in total.
     */
    public String prepareFinalPrevalencesRecord(String[] siteNames)
    {
        //HashMap<Object,Number>
        int endCycle = getMaxCycles() ;
        
        return prepareFinalPrevalencesRecord(siteNames, endCycle) ;
    }
    
    /**
     * 
     * @param siteNames
     * @param endCycle
     * @return Records of final prevalences for specified siteNames and in total.
     */
    public String prepareFinalPrevalencesRecord(String[] siteNames, int endCycle)
    {
        //HashMap<Object,Number> 
        String prevalencesRecord = "" ;    // = new HashMap<Object,Number>() ;
        double population = getPopulation() ;
        int prevalence ;
        // Number of times a Site is mentioned, regardless of infection status
        int[] mentions ;
        
        //LOGGER.log(Level.INFO, "{0}", endCycle);
        String finalPrevalenceRecord = getBackCyclesReport(0,0,1,endCycle).get(0) ; // getFinalRecord() ;
        
        
        // agentId maps to sortingProperty
        //if (sortingProperty.isEmpty())
        for (String siteName : siteNames)
        {
            // Count infected siteName
            mentions = COUNT_VALUE_INCIDENCE(siteName,CLEAR,finalPrevalenceRecord,0) ;
            prevalencesRecord += ADD_REPORT_PROPERTY(siteName,(mentions[1] - mentions[0])/population) ;
        }

        // Count Agents with any Site infected
        ArrayList<String> agentRecords = EXTRACT_ARRAYLIST(finalPrevalenceRecord,AGENTID) ;
        prevalence = 0 ;
        for (String record : agentRecords)
            for (String siteName : siteNames)
                if (record.contains(siteName))
                    if (!EXTRACT_VALUE(siteName,record).equals(CLEAR))
                    {
                        prevalence++ ;
                        break ;
                    }
        
        prevalencesRecord += ADD_REPORT_PROPERTY("all",prevalence/population) ;
        return prevalencesRecord ;
    }
 
    /**
     * 
     * @param siteNames
     * @param sortingProperty
     * @return Records of final prevalences for specified siteNames and in total,
     * sorted according to sortingProperty.
     */
    public String prepareFinalPrevalencesSortedRecord(String[] siteNames, String sortingProperty)
    {
        int endCycle = getMaxCycles() ;
        
        return prepareFinalPrevalencesSortedRecord(siteNames, sortingProperty, endCycle) ;
    }
    
 
    /**
     * 
     * @param siteNames
     * @param sortingProperty
     * @param endCycle
     * @return Records of final prevalences for specified siteNames and in total,
     * sorted according to sortingProperty.
     */
    public String prepareFinalPrevalencesSortedRecord(String[] siteNames, String sortingProperty, int endCycle)
    {
        //String finalPrevalencesSortedRecord = new HashMap<Object,String>() ;
        
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        HashMap<Object,ArrayList<String>> sortedAgentReport = populationReporter.agentIdSorted(sortingProperty) ;
        
        ArrayList<String> agentsAliveReport = populationReporter.prepareAgentsAliveRecord(endCycle) ;
            
        int[] sitePrevalences = new int[siteNames.length] ;
        
        double population ;
        String prevalencesRecord = "" ; 
        
        String finalPrevalenceRecord = getBackCyclesReport(0,0,1,endCycle).get(0) ; // getFinalRecord() ;
        
        for (Object key : sortedAgentReport.keySet())
        {
            ArrayList<String> sortedAgents = sortedAgentReport.get(key) ;
            /*if (sortingProperty.isEmpty())
                population = getPopulation() ; 
            else*/
            // Allow for deaths among selected Agents when determining the relevant population.
            ArrayList<Object> countedAgents = (ArrayList<Object>) sortedAgents.clone() ;
            countedAgents.retainAll(agentsAliveReport) ;
            population = countedAgents.size() ;
            //prevalencesRecord = "" ; 
            for (int index = 0 ; index < sitePrevalences.length ; index++ )
                sitePrevalences[index] = 0 ;
        
            // Count Agents with any Site infected
            ArrayList<String> agentRecords = EXTRACT_ARRAYLIST(finalPrevalenceRecord,AGENTID) ;
            int prevalence = 0 ;
            int sitePrevalence ;
            boolean incrementPrevalence ; 
            for (String record : agentRecords)
            {
                String agentId = EXTRACT_VALUE(AGENTID,record) ;
                if (sortedAgents.contains(agentId))
                {
                    incrementPrevalence = false ;
                    for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
                    {
                        String siteName = siteNames[siteIndex] ;
                        if (record.contains(siteName))
                            if (!EXTRACT_VALUE(siteName,record).equals(CLEAR))
                            {
                                //LOGGER.info(record);
                                sitePrevalence = sitePrevalences[siteIndex] ;
                                sitePrevalence++ ;
                                sitePrevalences[siteIndex] = sitePrevalence ;
                                incrementPrevalence = true ;
                            }
                    }
                    if (incrementPrevalence)
                        prevalence++ ;
                }
            }
            for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
            {
                String siteEntry = siteNames[siteIndex] + "_" + key.toString() ;
                prevalencesRecord += ADD_REPORT_PROPERTY(siteEntry, sitePrevalences[siteIndex]/population) ;
            }
            //LOGGER.log(Level.INFO, "{0} {1}", new Object[] {prevalence,population});
            prevalencesRecord += ADD_REPORT_PROPERTY("all" + "_" + key.toString(),prevalence/population) ;
            //LOGGER.info(prevalencesRecord);
            //finalPrevalencesSortedRecord.put(key, prevalencesRecord) ;
        }
        return prevalencesRecord ;
    }
    
    /**
     * 
     * @param siteNames
     * @return Records of final symptomatic prevalences for specified siteNames and in total.
     */
    public HashMap<Comparable,Number> prepareFinalSymptomaticRecord(String[] siteNames)
    {
        HashMap<Comparable,Number> finalSymptomaticRecords = new HashMap<Comparable,Number>() ;
        
        int symptomatic ;
        
        String finalSymptomaticRecord = getFinalRecord() ;
        
        double population = getPopulation() ; 
        for (String siteName : siteNames)
        {
            // Count infected siteName
            symptomatic = COUNT_VALUE_INCIDENCE(siteName,TRUE,finalSymptomaticRecord,0)[0];
            finalSymptomaticRecords.put(siteName,symptomatic/population) ;
        }
        
        // Count Agents with any Site symptomatic 
        ArrayList<String> agentRecords = EXTRACT_ARRAYLIST(finalSymptomaticRecord,AGENTID) ;
        symptomatic = 0 ;
        for (String record : agentRecords)
            for (String siteName : siteNames)
                if (COMPARE_VALUE(siteName, TRUE, record))
                {
                    symptomatic++ ;
                    break ;
                }
        finalSymptomaticRecords.put("all",symptomatic/population) ;
        
        return finalSymptomaticRecords ;
    }
    
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) Report where number of tests maps to number of Agents taking 
     * that many tests in given time frame.
     */
    public HashMap<Comparable,Number> prepareNumberAgentTestingReport(int backYears, int backMonths, int backDays)
    {
        int endCycle = getMaxCycles() ;
        
        return prepareNumberAgentTestingReport(backYears, backMonths, backDays, endCycle, new ArrayList<String>()) ;
    }
    
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @param sortedAgentIds
     * @return (HashMap) Report number of tests maps to number of Agents taking 
     * that many tests in given time frame.
     */
    public HashMap<Comparable,Number> prepareNumberAgentTestingReport(int backYears, int backMonths, int backDays, int endCycle, ArrayList<String> sortedAgentIds)
    {
        HashMap<Comparable,Number> numberAgentTestingReport = new HashMap<Comparable,Number>() ;
        
        // (HashMap) agentId maps to (ArrayList) of cycles in which Agent was tested.
        HashMap<Comparable,ArrayList<Comparable>> agentTestingReport 
                = prepareAgentTestingReport(backYears, backMonths, backDays, endCycle, sortedAgentIds) ;
        
        int population ;
        if (sortedAgentIds.isEmpty())
            population = getPopulation() ;
        else
            population = sortedAgentIds.size() ;
        
        Integer untested = population ;
        
        int nbTests ;
        for (ArrayList<Comparable> value : agentTestingReport.values())
        {
            nbTests = value.size() ;
            numberAgentTestingReport = INCREMENT_HASHMAP(nbTests,numberAgentTestingReport) ;
            untested-- ;
        }
        numberAgentTestingReport.put(0, untested.doubleValue()) ;
        
        for (Comparable tests : numberAgentTestingReport.keySet())
            numberAgentTestingReport.put(tests,(numberAgentTestingReport.get(tests))) ;
        
        return numberAgentTestingReport ;
    }
    
    /**
     * 
     * @param backMonths
     * @param backDays
     * @param backYears
     * @param lastYear
     * @return Year-by-year Report of the number of tests mapping to the number of Agents
     * who have had that many tests in each year
     */
    public HashMap<Comparable,Number[]> 
        prepareYearsNumberAgentTestingReport(int backYears, int backMonths, int backDays, int lastYear) 
        {
            HashMap<Comparable,Number[]> numberAgentTestingYears = new HashMap<Comparable,Number[]>() ;
            //HashMap<Object,Number[]> numberAgentTestingYears = new HashMap<Object,Number[]>() ;
            
            // Whether to save this Report to file
            boolean writeLocal = WRITE_REPORT ;
            // Do not save subreports
            WRITE_REPORT = false ;
            
            int maxCycles = getMaxCycles() ;
            int endCycle ;
            
            HashMap<Comparable,Number> numberAgentTestingRecord = new HashMap<Comparable,Number>() ;
            HashMap<String,Number> numberAgentStringRecord = new HashMap<String,Number>() ;
            for (int year = 0 ; year < backYears ; year++ )
            {
                endCycle = maxCycles - year * DAYS_PER_YEAR ;
                numberAgentTestingRecord = prepareNumberAgentTestingReport(1, 0, 0, endCycle, new ArrayList<String>());
                for (Comparable key : numberAgentTestingRecord.keySet())
                    numberAgentStringRecord.put(key.toString(), numberAgentTestingRecord.get(key)) ;
                
                Number[] yearlyNumberAgentRecord = new Number[numberAgentTestingRecord.keySet().size()] ;
               
                // Read through numbers of tests in keySet and convert to Number[]
                for (int numberIndex = 0 ; numberIndex < numberAgentTestingRecord.keySet().size() ; numberIndex++ )
                    yearlyNumberAgentRecord[numberIndex] = numberAgentTestingRecord.get(numberIndex) ;
                
                numberAgentTestingYears.put(lastYear - year, (Number[]) yearlyNumberAgentRecord.clone()) ;
            }
            if (writeLocal)
                WRITE_CSV(numberAgentTestingYears, "Year", (String[]) numberAgentStringRecord.keySet().toArray(new String[0]), "yearlyTests", simName, getFolderPath()) ;
            WRITE_REPORT = writeLocal ;
            
            return numberAgentTestingYears ;
        }
    
    /**
     * 
     * @param backYears
     * @param lastYear
     * @param sortingProperty
     * @return (HashMap) Year-by-year report of the number of tests per Agent per year,
     * where the Agents are sorted according to sorting property, if given.
     */
    public HashMap<Comparable,HashMap<Object,Number>> prepareYearsTestingRateReport(int backYears, int lastYear, String sortingProperty)
    {
        HashMap<Comparable,HashMap<Object,Number>> yearsTestingRateReport = new HashMap<Comparable,HashMap<Object,Number>>() ;
            
            int maxCycles = getMaxCycles() ;
            int endCycle ;
            
            for (int year = 0 ; year < backYears ; year++ )
            {  
                endCycle = maxCycles - year * DAYS_PER_YEAR ;
                
                yearsTestingRateReport.put(lastYear - year, prepareTestingRateReport(1,0,0,endCycle,sortingProperty)) ;
              
            }
        return yearsTestingRateReport ;
    }
    
    /**
     * Finds the testing rate over the backYears years, backMonths months and backDays days
     * leading up to day endCycle, with Agents sorted by sortingProperty. If sortingProperty 
     * is an empty String then the Agents remain unsorted.
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @param sortingProperty
     * @return (HashMap) where value of sortingProperty maps to (Number) of tests per Agent per year.
     */
    public HashMap<Object,Number> prepareTestingRateReport(int backYears, int backMonths, int backDays, int endCycle, String sortingProperty)
    {
        HashMap<Object,Number> testingRateReport = new HashMap<Object,Number>() ; 
        
        ArrayList<String> sortedAgentIds ;
        ArrayList<Object> sortingValues = new ArrayList<Object>() ;
        HashMap<Object,ArrayList<String>> sortedAgentReport = new HashMap<Object,ArrayList<String>>() ;
        if (sortingProperty.isEmpty())
            sortingValues.add("") ;
        else
        {
            PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
            sortedAgentReport = populationReporter.agentIdSorted(sortingProperty) ;
            sortingValues = new ArrayList<Object>(sortedAgentReport.keySet()) ;
        }
        
        for (Object value : sortingValues)
        {
            if (sortingProperty.isEmpty())    
                sortedAgentIds = new ArrayList<String>() ;
            else
                sortedAgentIds = sortedAgentReport.get(value) ;
            
            HashMap<Comparable,Number> numberAgentTestingReport = prepareNumberAgentTestingReport(backYears,backMonths, backDays, endCycle, sortedAgentIds) ;

            Double testedAgents = 0.0 ;
            Integer totalTests = 0 ;
            for (Comparable nbTests : numberAgentTestingReport.keySet())        
            {
                totalTests += Integer.valueOf(String.valueOf(nbTests)) ;
                testedAgents += numberAgentTestingReport.get(nbTests).intValue() ;
            }

            // per Agents per YEAR
            Double denominator = testedAgents * GET_BACK_CYCLES(backYears, backMonths, backDays, endCycle)/DAYS_PER_YEAR ;

            testingRateReport.put(value,totalTests/denominator) ;
        }
        
        return testingRateReport ;
    }
    

    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) Report number of tests maps to number of Agents taking 
     * that many or more tests in given time frame.
     */
    public Number[] prepareCumulativeAgentTestingReport(int backYears, int backMonths, int backDays, int endCycle)
    {
        return prepareCumulativeAgentTestingReport(backYears, backMonths, backDays, endCycle, new ArrayList<String>()) ;
    }
    
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) Report number of tests maps to number of Agents taking 
     * that many or more tests in given time frame.
     */
    public Number[] prepareCumulativeAgentTestingReport(int backYears, int backMonths, int backDays, int endCycle, ArrayList<String> sortedAgentIds)
    {
        Number[] cumulativeNumberAgentRecord ;
        HashMap<Comparable,Number> cumulativeAgentTestingRecord = new HashMap<Comparable,Number>() ;
        
        // (HashMap) number of tests maps to number of agentIds to have taken that many tests
        HashMap<Comparable,Number> agentTestingReport 
                = prepareNumberAgentTestingReport(backYears, backMonths, backDays, endCycle, sortedAgentIds) ;
        
        int population ;
        if (sortedAgentIds.isEmpty())
            population = getPopulation() ;
        else
            population = sortedAgentIds.size() ;
        Integer tested = 0 ;
        
        int maxKey = 0 ;
        for (Object key : agentTestingReport.keySet())
        {
            int keyInt = Integer.valueOf(key.toString()) ;
            if (keyInt > maxKey)
                maxKey = keyInt ;
        }
        
        for (int nbTests = maxKey ; nbTests > 0 ; nbTests-- )
        {
            if (agentTestingReport.containsKey(nbTests))
                tested += ((Number) agentTestingReport.get(nbTests)).intValue() ;
            // else
            //     LOGGER.info("No Agents have taken precisely " + String.valueOf(nbTests) + " tests.") ;
            cumulativeAgentTestingRecord.put(nbTests,tested) ;
        }
        if (tested != (population-agentTestingReport.get(0).intValue()))
            LOGGER.log(Level.SEVERE, "Test numbers don't tally. Population:{0} nbTested:{1}", new Object[] {population,tested}) ;
        
        cumulativeNumberAgentRecord = new Number[cumulativeAgentTestingRecord.keySet().size()] ;

        // Read through numbers of tests in keySet and convert to Number[]
        for (int numberIndex = 0 ; numberIndex < cumulativeAgentTestingRecord.keySet().size() ; numberIndex++ )
        {
            if (cumulativeAgentTestingRecord.containsKey(numberIndex+1))
                cumulativeNumberAgentRecord[numberIndex] = cumulativeAgentTestingRecord.get(numberIndex+1) ;
            else
                cumulativeNumberAgentRecord[numberIndex] = 0 ;
        }
        //sortedCumulativeAgentTestingRecord.put(sortingValue, (Number[]) cumulativeNumberAgentRecord.clone()) ;
        return cumulativeNumberAgentRecord ;
    }
        
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param lastYear
     * @return Year-by-year Report of the number of tests mapping to the number of Agents
     * who have had at least that many tests in each year.
     */
    public HashMap<Comparable,Number[]> 
        prepareYearsCumulativeAgentTestingReport(int backYears, int backMonths, int backDays, int lastYear) 
        {
            HashMap<Comparable,Number[]> desortedReport = new HashMap<Comparable,Number[]>() ;
            
            HashMap<Comparable,HashMap<Object,Number[]>> sortedReport = prepareYearsCumulativeAgentTestingReport(backYears, backMonths, backDays, lastYear, "") ;
            
            for (Comparable year : sortedReport.keySet())
                desortedReport.put(year, sortedReport.get(year).get(EMPTY)) ;
            
            return desortedReport ;
        }
        
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param lastYear
     * @param sortingProperty
     * @return Year-by-year Report of the number of tests mapping to the number of Agents
     * who have had at least that many tests in each year.
     */
    public HashMap<Comparable,HashMap<Object,Number[]>> 
        prepareYearsCumulativeAgentTestingReport(int backYears, int backMonths, int backDays, int lastYear, String sortingProperty) 
        {
            HashMap<Comparable,HashMap<Object,Number[]>> cumulativeAgentTestingYears = new HashMap<Comparable,HashMap<Object,Number[]>>() ;
            
            // Whether to save this Report to file
//            boolean writeLocal = WRITE_REPORT ;
//            // Do not save subreports
//            WRITE_REPORT = false ;
            
            int maxCycles = getMaxCycles() ;
            int endCycle ;
            
            // Sorted AgentId
            ArrayList<Object> sortingValues = new ArrayList<Object>() ;
            HashMap<Object,ArrayList<String>> sortedAgentIds = new HashMap<Object,ArrayList<String>>() ;
            PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
            if (sortingProperty.isEmpty())
                sortingValues.add("") ;

            ArrayList<String> agentIdSorted = new ArrayList<String>() ;
        
            
            Number[] cumulativeAgentTestingRecord = new Number[] {} ;
            for (int year = 0 ; year < backYears ; year++ )
            {
                HashMap<Object,Number[]> sortedCumulativeAgentTestingRecord = new HashMap<Object,Number[]>() ;
            
                endCycle = maxCycles - year * DAYS_PER_YEAR ;
                
                if (!sortingProperty.isEmpty())
                {
                    sortedAgentIds = populationReporter.agentIdSorted(sortingProperty,endCycle) ;
                    sortingValues = new ArrayList<Object>(sortedAgentIds.keySet()) ;
                }

                for (Object sortingValue : sortingValues)
                {
                    if (!sortingProperty.isEmpty())
                    {
                        agentIdSorted = sortedAgentIds.get(sortingValue) ;
                        //population = agentIdSorted.size() ;
                    }
                
                    cumulativeAgentTestingRecord = prepareCumulativeAgentTestingReport(1, 0, 0, endCycle, agentIdSorted) ;

                    sortedCumulativeAgentTestingRecord.put(sortingValue, (Number[]) cumulativeAgentTestingRecord.clone()) ;
                }
                cumulativeAgentTestingYears.put(lastYear - year, (HashMap<Object,Number[]>) sortedCumulativeAgentTestingRecord.clone()) ;
            }
            
//            if (writeLocal)
//            {
//                ArrayList<Comparable> scoreList = new ArrayList<Comparable>(cumulativeAgentTestingRecord.keySet()) ;
//                Collections.sort(scoreList) ;
//                String[] scoreNames = new String[scoreList.size()] ; 
//                for (int scoreIndex = 0 ; scoreIndex < scoreList.size() ; scoreIndex++ )
//                    scoreNames[scoreIndex] = scoreList.get(scoreIndex).toString() ;
//                    
//                WRITE_CSV(cumulativeAgentTestingYears, "Year", scoreNames, "yearlyTests", simName, getFolderPath()) ;
//            }
//            WRITE_REPORT = writeLocal ;
            
            return cumulativeAgentTestingYears ;
        }
    
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param lastYear
     * @return HashMap from year to proportion of Agents tested at least once during that year.
     */
    public HashMap<Comparable,Number> prepareYearsBeenTestedReport(int backYears, int backMonths, int backDays, int lastYear) 
    {
        HashMap<Comparable,Number> desortedReport = new HashMap<Comparable,Number>() ;
        
        HashMap<Comparable,HashMap<Object,Number>> sortedReport = prepareYearsBeenTestedReport(backYears, backMonths, backDays, lastYear, "") ;
        for (Comparable year : sortedReport.keySet())
            desortedReport.put(year, sortedReport.get(year).get(EMPTY)) ;
        
        return desortedReport ;
    }
    
    
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param lastYear
     * @param sortingProperty
     * @return HashMap from year to proportion of Agents tested at least once during that year.
     */
    public HashMap<Comparable,HashMap<Object,Number>> prepareYearsBeenTestedReport(int backYears, int backMonths, int backDays, int lastYear, String sortingProperty) 
    {
        HashMap<Comparable,HashMap<Object,Number>> yearsBeenTestedReport = new HashMap<Comparable,HashMap<Object,Number>>() ;
        
        int endCycle ;
        int maxCycle = getMaxCycles() ;
        
        int population = getPopulation() ;
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        
        ArrayList<String> agentIdSorted = new ArrayList<String>() ;
        ArrayList<Object> sortingValues = new ArrayList<Object>() ;
        sortingValues.add(EMPTY) ;
        
        HashMap<Object,ArrayList<String>> sortedAgentIds = new HashMap<Object,ArrayList<String>>() ;
        
        // Extract yearly values for one or more tests
        for (int year = 0 ; year < backYears ; year++ )
        {
            endCycle = maxCycle - year * DAYS_PER_YEAR ;
            
            if (!sortingProperty.isEmpty())
            {
                sortedAgentIds = populationReporter.agentIdSorted(sortingProperty,endCycle) ;
                sortingValues = new ArrayList<Object>(sortedAgentIds.keySet()) ;
            }

            HashMap<Object,Number[]> sortedCumulativeAgentTestingRecord = new HashMap<Object,Number[]>() ;
            HashMap<Object,Number> sortedBeenTestedRecord = new HashMap<Object,Number>() ; 
            //for (Object sortingValue : new Object[] {TRUE,FALSE})
            for (Object sortingValue : sortingValues)
            {
                if (!sortingProperty.isEmpty())
                {
                    agentIdSorted = sortedAgentIds.get(sortingValue) ;
                    ArrayList<Comparable> deadAgentIds = populationReporter.prepareAgentsDeadRecord(endCycle - year * DAYS_PER_YEAR) ;
                    agentIdSorted.removeAll(deadAgentIds) ;
                    population = agentIdSorted.size() ;
                }

                Number[] cumulativeAgentTestingRecord 
                    = prepareCumulativeAgentTestingReport(1, 0, 0, endCycle, agentIdSorted)  ;

                sortedCumulativeAgentTestingRecord.put(sortingValue, (Number[]) cumulativeAgentTestingRecord.clone()) ;
            
          // logger.log(level.info, "population:{0} report:{1}", new Object[] {population,sortedCumulativeAgentTestingRecord.get(sortingValue)[0]});
                sortedBeenTestedRecord.put(sortingValue, sortedCumulativeAgentTestingRecord.get(sortingValue)[0].doubleValue()/population) ;
            }
            yearsBeenTestedReport.put(lastYear - year, (HashMap<Object,Number>) sortedBeenTestedRecord.clone()) ;
        }
        //LOGGER.log(Level.INFO, "{0}", yearsBeenTestedReport);
        
        return yearsBeenTestedReport ;
    }
    
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) agentId maps to (ArrayList) of cycles in which Agent was tested.
     */
    public HashMap<Comparable,ArrayList<Comparable>> prepareAgentTestingReport(int backYears, int backMonths, int backDays)
    {
        int maxCycle = getMaxCycles() ;
        
        return prepareAgentTestingReport(backYears, backMonths, backDays, maxCycle) ;
    }
    
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return (HashMap) agentId maps to (ArrayList) of cycles in which Agent was tested.
     */
    public HashMap<Comparable,ArrayList<Comparable>> prepareAgentTestingReport(int backYears, int backMonths, int backDays, int endCycle)
    {
        return prepareAgentTestingReport(backYears, backMonths, backDays, endCycle, new ArrayList<String>()) ;
    }
    
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @param sortedAgentIds
     * @return (HashMap) agentId maps to (ArrayList) of cycles in which Agent was tested.
     */
    public HashMap<Comparable,ArrayList<Comparable>> prepareAgentTestingReport(int backYears, int backMonths, int backDays, int endCycle, ArrayList<String> sortedAgentIds)
    {
        HashMap<Comparable,ArrayList<Comparable>> agentTestingReport = new HashMap<Comparable,ArrayList<Comparable>>() ; 
        
        int backCycles = GET_BACK_CYCLES(backYears, backMonths, backDays, endCycle) ;
        int startCycle = endCycle - backCycles ;
        
        ArrayList<String> inputReport = getBackCyclesReport(backYears, backMonths, backDays, endCycle) ;
        String record ;
        String agentId ;
        for (int cycle = 0 ; cycle < backCycles ; cycle++ )
        {
            record = inputReport.get(cycle) ;
            
            ArrayList<String> agentReport = EXTRACT_ARRAYLIST(record,AGENTID,TESTED) ;
            if (!sortedAgentIds.isEmpty())
                for (int agentIndex = agentReport.size() - 1 ; agentIndex >= 0 ; agentIndex-- )
                {
                    String agentIdString = EXTRACT_VALUE(AGENTID,agentReport.get(agentIndex)) ;
                    if (!sortedAgentIds.contains(agentIdString)) 
                        agentReport.remove(agentIndex) ;
                }
            
            for (String agentRecord : agentReport)
            {
                agentId = EXTRACT_VALUE(AGENTID,agentRecord) ;
                agentTestingReport = (HashMap<Comparable,ArrayList<Comparable>>) UPDATE_HASHMAP(agentId, startCycle + cycle, agentTestingReport) ;
            }
        }
        return agentTestingReport ;
    }
    
    

    /**
     * FIXME: Correct order of category variables for ten or more treatments.
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param sortingProperty
     * @return (HashMap) Report number of treatments maps to number of Agents 
     * receiving that many treatments in given time frame for each value of sortingProperty.
     */
    public HashMap<Comparable,Number[]> 
        prepareNumberAgentTreatedReport(int backYears, int backMonths, int backDays, String sortingProperty, int maxNumber)
    {
        HashMap<Comparable,Number[]> sortedNumberAgentTreatedReport 
                = new HashMap<Comparable,Number[]>() ;
        
        // (HashMap) agentId maps to (ArrayList) of cycles in which Agent was tested.
        HashMap<Comparable,ArrayList<Comparable>> agentTreatedReport 
                = prepareAgentTreatedReport(new String[] {}, backYears, backMonths, backDays).get("all") ;
        
        // For sorting agentTreatedReport
        HashMap<Object,HashMap<Object,ArrayList<Comparable>>> sortedAgentTreatedReport 
                = new HashMap<Object,HashMap<Object,ArrayList<Comparable>>>() ;
        
        // Generate required spaces in label names
        //categoryEntry.sort(null);
        int totalDigits = (int) Math.log10(maxNumber) ; // (Integer.valueOf(String.valueOf(categoryEntry.get(categoryEntry.size() - 1))))) + 1 ;
        
        
        // Sort agentTreatedReport according to sortingProperty of Agents
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        int startCycle = getMaxCycles() - getBackCycles(backYears,backMonths, backDays) ;
        ArrayList<Comparable> agentsDeadRecord = populationReporter.prepareAgentsDeadRecord(startCycle) ;
        
        // agentId maps to sortingProperty
        HashMap<Object,Object> sortedAgentReport = populationReporter.sortedAgentIds(sortingProperty) ;
        //for (Object agentId : agentTreatedReport.keySet())
        for (Object agentId : sortedAgentReport.keySet())
        {
            if (agentsDeadRecord.contains(agentId))
                continue ;
            Object propertyKey = sortedAgentReport.get(agentId) ;
            // Create key if still needed
            if (!sortedAgentTreatedReport.containsKey(propertyKey))
                sortedAgentTreatedReport.put(propertyKey, new HashMap<Object,ArrayList<Comparable>>()) ;
            
            if (agentTreatedReport.containsKey(agentId))
                sortedAgentTreatedReport.get(propertyKey).put(agentId, agentTreatedReport.get(agentId)) ;
            else
                sortedAgentTreatedReport.get(propertyKey).put(agentId, new ArrayList<Comparable>()) ;
        }
        int nbKeys = sortedAgentTreatedReport.size() ;
        int sortedPopulation ;
        int nbTreatments ;
        int keyIndex = 0 ;
        String treatmentsString = "" ;
        String maxNumberString = String.valueOf(maxNumber).concat("+") ;
        for (Object propertyKey : sortedAgentTreatedReport.keySet())
        {
            HashMap<Comparable,Number> numberAgentTreatedReport = new HashMap<Comparable,Number>() ;
            
            HashMap<Object,ArrayList<Comparable>> sortedTreatedReport 
                    = sortedAgentTreatedReport.get(propertyKey) ;
            
            // Read in values corresponding to propertyKey
            sortedPopulation = sortedTreatedReport.size() ;
            for (ArrayList<Comparable> value : sortedTreatedReport.values())
            {
                nbTreatments = value.size() ;
                numberAgentTreatedReport = INCREMENT_HASHMAP(nbTreatments,numberAgentTreatedReport) ;
            }

            for (Comparable treatments : numberAgentTreatedReport.keySet())
                numberAgentTreatedReport.put(treatments,(numberAgentTreatedReport.get(treatments).doubleValue())/sortedPopulation) ;
        
            // Construct sortedNumberAgentTreatedReport
            for (Object treatments : numberAgentTreatedReport.keySet())
            {
                if ((Integer.valueOf(String.valueOf(treatments)) < maxNumber) || (maxNumber < 0))
                {
                    treatmentsString = String.valueOf(treatments) ;
                    int nbDigits = treatmentsString.length() ;
                    for (int digitIndex = nbDigits ; digitIndex < totalDigits ; digitIndex++ )
                        treatmentsString = " ".concat(treatmentsString) ;
                }
                else
                    treatmentsString = maxNumberString ;
                
                
                // Create keys where needed
                if (!sortedNumberAgentTreatedReport.containsKey(treatmentsString))
                {
                    sortedNumberAgentTreatedReport.put(treatmentsString, new Number[nbKeys]) ;
                    for (int index = 0 ; index < nbKeys ; index++ )
                        sortedNumberAgentTreatedReport.get(treatmentsString)[index] = 0 ;
                }
                
                // Enter values. maxNumber < 0 corresponds to no maximum.
                if (treatmentsString != maxNumberString) // (Integer.valueOf(String.valueOf(treatments)) < maxNumber) || maxNumber < 0)
                {
                    sortedNumberAgentTreatedReport.get(treatmentsString)[keyIndex] 
                        = numberAgentTreatedReport.get(treatments) ;
                }
                else
                {
                    sortedNumberAgentTreatedReport.get(maxNumberString)[keyIndex] 
                            = sortedNumberAgentTreatedReport.get(maxNumberString)[keyIndex].doubleValue()
                            + numberAgentTreatedReport.get(treatments).doubleValue() ;
                }
            }
            keyIndex++ ;
        }
        return sortedNumberAgentTreatedReport ;
    }
    
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) Report number of treatments maps to number of Agents 
     * receiving that many treatments in given time frame.
     */
    public HashMap<Comparable,Number> prepareNumberAgentTreatedReport(int backYears, int backMonths, int backDays)
    {
        HashMap<Comparable,Number> numberAgentTreatedReport = new HashMap<Comparable,Number>() ;
        
        // (HashMap) agentId maps to (ArrayList) of cycles in which Agent was tested.
        HashMap<Comparable,ArrayList<Comparable>> agentTreatedReport 
                = prepareAgentTreatedReport(new String[] {}, backYears, backMonths, backDays).get("all") ;
        
        int population = getPopulation() ;
        int untreated = population ;
        
        int nbTreatments ;
        for (ArrayList<Comparable> value : agentTreatedReport.values())
        {
            nbTreatments = value.size() ;
            numberAgentTreatedReport = INCREMENT_HASHMAP(nbTreatments,numberAgentTreatedReport) ;
            untreated-- ;
        }
        numberAgentTreatedReport.put(0, untreated) ;
        
        for (Object treatments : numberAgentTreatedReport.keySet())
            numberAgentTreatedReport.put(Integer.valueOf(String.valueOf(treatments)),(numberAgentTreatedReport.get(treatments).doubleValue())/population) ;
        
        return numberAgentTreatedReport ;
    }
 
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) agentId maps to (ArrayList) of cycles in which Agent was treated for STIs.
     */
    public HashMap<Object,HashMap<Comparable,ArrayList<Comparable>>> prepareAgentTreatedReport(String [] siteNames, int backYears, int backMonths, int backDays)
    {
        int endCycle = getMaxCycles() ;
        
        return prepareAgentTreatedReport(siteNames, backYears, backMonths, backDays, endCycle) ;
    }
 
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return (HashMap) agentId maps to (ArrayList) of cycles in which Agent was treated for STIs.
     */
    public HashMap<Object,HashMap<Comparable,ArrayList<Comparable>>> prepareAgentTreatedReport(String[] siteNames, int backYears, int backMonths, int backDays, int endCycle)
    {
        HashMap<Object,HashMap<Comparable,ArrayList<Comparable>>> agentTreatedReport = new HashMap<Object,HashMap<Comparable,ArrayList<Comparable>>>() ; 
        for (String siteName : siteNames)
            agentTreatedReport.put(siteName, new HashMap<Comparable,ArrayList<Comparable>>()) ; 
        agentTreatedReport.put("all", new HashMap<Comparable,ArrayList<Comparable>>()) ;
        
        int backCycles = GET_BACK_CYCLES(backYears, backMonths, backDays, endCycle) ;
        int startCycle = endCycle - backCycles ;
        ArrayList<String> inputReport = getBackCyclesReport(backYears, backMonths, backDays, endCycle) ;
        String record ;
        String agentId ;
        for (int cycle = 0 ; cycle < backCycles ; cycle++ )
        {
            record = inputReport.get(cycle) ;
            
            ArrayList<String> agentReport = EXTRACT_ARRAYLIST(record,AGENTID,TREATED) ;
            //LOGGER.info("agentReport.size:" + String.valueOf(agentReport.size()));
            for (String agentRecord : agentReport)
            {
                agentId = EXTRACT_VALUE(AGENTID,agentRecord) ;
                agentTreatedReport.put("all", UPDATE_HASHMAP(agentId, startCycle + cycle, agentTreatedReport.get("all"))) ;
                for (String siteName : siteNames)
                    if (agentRecord.contains(siteName))
                        agentTreatedReport.put(siteName, UPDATE_HASHMAP(agentId, startCycle + cycle, agentTreatedReport.get(siteName))) ;
            }
        }
        //LOGGER.info(agentTreatedReport.get("all").get("1034").toString());
        return agentTreatedReport ;
    }
 
    public HashMap<Comparable,String> prepareYearsAtRiskIncidenceReport(String[] relationshipClassNames, int backYears, int lastYear, String sortingProperty)
    {
        HashMap<Comparable,String> incidentRateReport = new HashMap<Comparable,String>() ;
        //HashMap<Object,Number[]> percentAgentCondomlessYears = new HashMap<Object,Number[]>() ;
    
        for (int year = 0 ; year < backYears ; year++ )
        {
            //LOGGER.info("backYears:" + String.valueOf(year));
            String yearlyNumberAgentsEnteredRelationship ;

            //endCycle = maxCycles - year * DAYS_PER_YEAR ;
            yearlyNumberAgentsEnteredRelationship 
                = prepareFinalAtRiskIncidentsRecord(relationshipClassNames, year, sortingProperty);

//                for (int classIndex = 0 ; classIndex < relationshipClassNames.length ; classIndex++ )
//                    yearlyNumberAgentsEnteredRelationship[classIndex] = percentAgentCondomlessRecord.get(relationshipClassNames[classIndex]) ;

            incidentRateReport.put(lastYear - year, yearlyNumberAgentsEnteredRelationship) ;
        }
        // LOGGER.info(incidentRateReport.toString()) ;

        return incidentRateReport ;
    }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @return atRiskIncidenceReport from year leading up to backYears years ago.
     */
    public String prepareFinalAtRiskIncidentsRecord(String[] siteNames, int backYears, String sortingProperty)
    {
        int endCycle = getMaxCycles() - DAYS_PER_YEAR * backYears ;
        
        return prepareAtRiskIncidenceReport(siteNames, 1, 0, 0, endCycle, sortingProperty) ;
    }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @param sortingProperty
     * @return Report of incidence calculated by number of positive tests following
     * negative tests, per time-at-risk being the time between such tests.
     */
    public String prepareAtRiskIncidenceReport(String[] siteNames, int backYears, int backMonths, int backDays, int endCycle, String sortingProperty)
    {
        String incidentRateReport = "" ;
        
        HashSet<Object> sortingProperties = new HashSet<Object>() ; //(Arrays.asList(new Object[] {""})) ;
        HashMap<Object,ArrayList<String>> sortedAgentsReport = new HashMap<Object,ArrayList<String>>() ;
        if (!sortingProperty.isEmpty())
        {
            PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
            sortedAgentsReport = populationReporter.agentIdSorted(sortingProperty,endCycle) ;
            Collections.addAll(sortingProperties,sortedAgentsReport.keySet().toArray()) ;
//            LOGGER.info(sortedAgentsReport.toString());
//            LOGGER.info(sortedAgentsReport.keySet().toString());
        }
        else
            sortingProperties.add("") ;
        //LOGGER.info(sortedAgentsReport.keySet().toString());
        //LOGGER.log(Level.INFO, "false:{0} true:{1}", new Object[] {sortedAgentsReport.get("false").size(),sortedAgentsReport.get("true").size()});
            
        HashMap<Comparable,ArrayList<Comparable>> agentTestingReport = prepareAgentTestingReport(backYears, backMonths, backDays, endCycle) ; 
        HashMap<Object,HashMap<Comparable,ArrayList<Comparable>>> agentTreatedReport = prepareAgentTreatedReport(siteNames, backYears, backMonths, backDays, endCycle) ; 
        //LOGGER.info(agentTreatedReport.get("all").keySet().toString()) ;
        
        HashMap<Object,HashMap<Object,ArrayList<Integer>>> timeAtRiskReport = prepareTimeAtRiskReport(agentTestingReport,agentTreatedReport) ;
        
        //int population = getPopulation() ;
        int daysAtRisk ;
        int nbIncidents ;
        for (Object sortingValue : sortingProperties)
        {
            for (Object siteName : agentTreatedReport.keySet())
            {
                daysAtRisk = 0 ;
                nbIncidents = 0 ;
                int intervalRisk ;

                HashMap<Comparable,ArrayList<Comparable>> treatedRecord = agentTreatedReport.get(siteName) ;
                    //LOGGER.info(treatedRecord.keySet().toString());
                HashMap<Object,ArrayList<Integer>> atRiskReport = timeAtRiskReport.get(siteName) ;

                // Sort agentIds by sortingProperty if one is given.
                ArrayList<Object> agentIdList = new ArrayList<Object>() ;
                Collections.addAll(agentIdList,atRiskReport.keySet().toArray()) ;
                if (!sortingValue.equals(""))
                    agentIdList.retainAll(sortedAgentsReport.get(sortingValue)) ;
                for (Object agentId : agentIdList)
                {
                    ArrayList<Comparable> agentTestingRecord = agentTestingReport.get(agentId.toString()) ;
                    ArrayList<Comparable> agentTreatedRecord = treatedRecord.get(agentId.toString()) ;
                    if (agentTreatedRecord == null)
                        agentTreatedRecord = new ArrayList<Comparable>() ;
                    if (!atRiskReport.containsKey(agentId))
                        continue ;
                    ArrayList<Integer> agentTimeAtRiskRecord = atRiskReport.get(agentId.toString()) ;
    //                LOGGER.info("testing " + agentId.toString() + agentTestingRecord.toString());
    //                LOGGER.info("treated " + agentTreatedRecord.toString()) ;
    //                LOGGER.info("risk " + agentTimeAtRiskRecord.toString()) ;
                    for (int index = 0 ; index < agentTimeAtRiskRecord.size() ; index ++ )
                    {
                        //LOGGER.info("agentTreatedRecord " + agentTreatedRecord.toString());
                        intervalRisk = agentTimeAtRiskRecord.get(index) ;
                        daysAtRisk += intervalRisk ;

                        // Count positive test only if preceded by a negative test
                        if (agentTreatedRecord == null)
                            continue ;
                        if ((intervalRisk > 0) && agentTreatedRecord.contains(agentTestingRecord.get(index))) // 
                            nbIncidents++ ;
                    }
                }

                // Rate of incidents per 100 MSM-years
                double incidentRate = (DAYS_PER_YEAR * 100.0 * nbIncidents)/(daysAtRisk) ;
                String propertyName = siteName.toString() ;
                if (!"".equals(sortingValue))
                    propertyName += "_" + sortingValue ;
                incidentRateReport += Reporter.ADD_REPORT_PROPERTY(propertyName, incidentRate) ;
            }
        }
        return incidentRateReport ;
    }
    
    /**
     * 
     * @return Report of days-at-risk for each test of each Agent. Days-at-risk 
     * is the number of days since the last positive test, unless the last test was positive 
     * in which case it is zero.
     */
    private HashMap<Object,HashMap<Object,ArrayList<Integer>>> prepareTimeAtRiskReport(HashMap<Comparable,ArrayList<Comparable>> agentTestingReport, 
            HashMap<Object,HashMap<Comparable,ArrayList<Comparable>>> agentTreatedReport)
    {
        HashMap<Object,HashMap<Object,ArrayList<Integer>>> timeAtRiskReport = new HashMap<Object,HashMap<Object,ArrayList<Integer>>>() ;
        
        Integer daysAtRisk ;
        // Cycle of previous test
        int previousTest ;
        // Require negative test result followed by a positive one
        boolean previousResult ;
        
        Integer day ;
        for (Object siteName : agentTreatedReport.keySet())
        {
            HashMap<Comparable,ArrayList<Comparable>> treatedReport = agentTreatedReport.get(siteName) ;
            ArrayList<Comparable> agentTreatedRecord = new ArrayList<Comparable>() ;
            HashMap<Object,ArrayList<Integer>> atRiskReport = new HashMap<Object,ArrayList<Integer>>() ;
            //LOGGER.info(treatedReport.toString()) ;
            for (Object agentId : agentTestingReport.keySet())
            {
                ArrayList<Object> atRiskRecord = new ArrayList<Object>() ;

                ArrayList<Comparable> agentTestingRecord = agentTestingReport.get(agentId) ;
                if (treatedReport.containsKey(agentId))
                    agentTreatedRecord = treatedReport.get(agentId) ;
                else
                    agentTreatedRecord = new ArrayList<Comparable>() ;
                //LOGGER.info(agentTreatedRecord.toString()) ;

                previousTest = 0 ;
                // Need to register a negative test before finding an incident.
                previousResult = true ;


                for (Object cycle : agentTestingRecord )
                {
                    day = Integer.valueOf(String.valueOf(cycle)) ;
                    //if (day == previousTest)
                      //  LOGGER.log(Level.SEVERE, "day:{3} agentId:{0} site:{1} record:{2}", new Object[] {agentId,siteName,agentTestingRecord,day});

                    if (!previousResult)    // If last test positive
                        daysAtRisk = day - previousTest ;    // Add days-at-risk to agentRecord
                    else
                        daysAtRisk = 0 ;

                    atRiskRecord.add(daysAtRisk) ;

                    // Prepare for next loop
                    previousTest = day ;

                    previousResult = agentTreatedRecord.contains(cycle) ;
                }
                atRiskReport.put(agentId,(ArrayList<Integer>) atRiskRecord.clone()) ;
            }
            timeAtRiskReport.put(siteName, (HashMap<Object,ArrayList<Integer>>) atRiskReport.clone()) ;
        }
        
        return timeAtRiskReport ;
    }
    
    /**
     * 
     * @return (ArrayList) indicating the total prevalence, prevalence of 
     * symptomatic infection, and proportion of symptomatic infection in each report cycle.
     */
    public ArrayList<String> preparePrevalenceReport()
    {
        ArrayList<String> prevalenceReport = new ArrayList<String>() ;
        
        int population = getPopulation() ; // Integer.valueOf(getMetaDatum("Community.POPULATION")) ;
        int nbInfected ;
        int nbSymptomatic ;
        String entry ;
        String siteStatus ;
        String[] siteNames = MSM.SITE_NAMES ;
        boolean agentInfected ;
        
        /*for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
        {
            String siteName = siteNames[siteIndex] ;
            siteNames[siteIndex] = siteName.substring(0,1).toUpperCase() 
                    + siteName.substring(1) ;
        }*/

        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
            for (String record : input)
            {
                nbSymptomatic = 0 ;
                ArrayList<String> infections = EXTRACT_ARRAYLIST(record,AGENTID) ;
                nbInfected = 0 ; // infections.size() ;
                for (String infection : infections)
                {
                    agentInfected = false ;
                    for (String siteName : MSM.SITE_NAMES)
                    {
                        siteStatus = EXTRACT_VALUE(siteName,infection) ;
                        if (siteStatus.equals(TRUE))
                        {
                            agentInfected = true ;
                            nbSymptomatic++ ;
                            break ;
                        }
                        else if (siteStatus.equals(FALSE))
                            agentInfected = true ;
                    }
                    if (agentInfected)
                        nbInfected++ ;
                }

                //LOGGER.info(record) ;
                entry = ADD_REPORT_PROPERTY("prevalence",((double) nbInfected)/population) ;
                entry += ADD_REPORT_PROPERTY("symptomatic",((double) nbSymptomatic)/population) ;
                entry += ADD_REPORT_PROPERTY("proportion",((double) nbSymptomatic)/nbInfected) ;

                prevalenceReport.add(entry) ;
            }
            
        return prevalenceReport ;
    }
    
    /**
     * 
     * @param siteNames
     * @return (ArrayList) Report of (String) records giving prevalence values 
     * for each given siteName and overall.
     */
    public ArrayList<Object> prepareCompletePrevalenceReport(String[] siteNames)
    {
        ArrayList<Object> completePrevalenceReport = new ArrayList<Object>() ;
        
        int population = getPopulation() ;
        String entry ;
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport())
            for (String record : input)
            {
                entry = ADD_REPORT_LABEL("all") ;
                entry += preparePrevalenceRecord(siteNames, record, population) ;
                for (String siteName : siteNames)
                {
                    entry += ADD_REPORT_LABEL(siteName) ;
                    entry += preparePrevalenceRecord(siteName, record, population) ;
                }
                completePrevalenceReport.add(entry) ;
            }
        return completePrevalenceReport ;
    }
    
    /**
     * 
     * @param siteName
     * @return (ArrayList) indicating the total prevalence, prevalence of 
     * symptomatic infection, and proportion of symptomatic infection at site given 
     * by siteName in each report cycle.
     */
    public ArrayList<String> preparePrevalenceReport(String siteName) 
    {
        ArrayList<String> sitePrevalenceReport = new ArrayList<String>() ;
        
        int population = getPopulation() ; // Integer.valueOf(getMetaDatum("Community.POPULATION")) ;
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
            for (String record : input)
                sitePrevalenceReport.add(preparePrevalenceRecord(siteName, record, population)) ;
         
        return sitePrevalenceReport ;
    }
    
    private String preparePrevalenceRecord(String siteName, String record, double population)
    {
        int[] nbSymptomatic = COUNT_VALUE_INCIDENCE(siteName,TRUE,record,0) ;

        String entry = ADD_REPORT_PROPERTY("prevalence",((double) nbSymptomatic[1])/population) ;
        entry += ADD_REPORT_PROPERTY("symptomatic",((double) nbSymptomatic[0])/population) ;
        entry += ADD_REPORT_PROPERTY("proportion",((double) nbSymptomatic[0])/nbSymptomatic[1]) ;

        return entry ;
    }
    
    private String preparePrevalenceRecord(String[] siteNames, String record, double population)
    {
        boolean agentInfected ;
        String siteStatus ;
        String entry = "" ;
        
//        for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
//            {
//                String siteName = siteNames[siteIndex] ;
//                siteNames[siteIndex] = siteName.substring(0,1).toUpperCase() 
//                        + siteName.substring(1) ;
//            }
//        
        int nbSymptomatic = 0 ;
        ArrayList<String> infections = EXTRACT_ARRAYLIST(record,AGENTID) ;
        int nbInfected = 0 ; // infections.size() ;
        for (String infection : infections)
        {
            agentInfected = false ;
            for (String siteName : siteNames)
            {
                siteStatus = EXTRACT_VALUE(siteName,infection) ;
                if (siteStatus.equals(TRUE))
                {
                    agentInfected = true ;
                    nbSymptomatic++ ;
                    break ;
                }
                else if (siteStatus.equals(FALSE))
                    agentInfected = true ;
            }
            if (agentInfected)
                nbInfected++ ;
        }

        //LOGGER.info(record) ;
        entry = ADD_REPORT_PROPERTY("prevalence",((double) nbInfected)/population) ;
        entry += ADD_REPORT_PROPERTY("symptomatic",((double) nbSymptomatic)/population) ;
        entry += ADD_REPORT_PROPERTY("proportion",((double) nbSymptomatic)/nbInfected) ;

        return entry ;
    }
    
    /**
     * 
     * @param siteName
     * @return (ArrayList) indicating the total prevalence, prevalence of 
     * symptomatic infection, and proportion of symptomatic infection at site given 
     * by siteName in each report cycle.
     */
    public ArrayList<Object> preparePrevalenceReport_site(String siteName) 
    {
        ArrayList<Object> sitePrevalenceReport = new ArrayList<Object>() ;
        
        int nbClear ;
        int[] nbSymptomatic ;
        int prevalence ;
        String entry ;
        double population = Double.valueOf(getMetaDatum("Community.POPULATION")) ;
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
        {
            for (String record : input)
            {
                nbSymptomatic = COUNT_VALUE_INCIDENCE(siteName,TRUE,record,0) ;
                nbClear = COUNT_VALUE_INCIDENCE(siteName,CLEAR,record,0)[0] ;

    //            if (nbSymptomatic[0] == nbSymptomatic[1])
    //                LOGGER.info(record);

                prevalence = nbSymptomatic[1] - nbClear ;
                entry = ADD_REPORT_PROPERTY("prevalence",prevalence/population) ;
                entry += ADD_REPORT_PROPERTY("symptomatic",(nbSymptomatic[0])/population) ;
                entry += ADD_REPORT_PROPERTY("proportion",((double) nbSymptomatic[0])/prevalence) ;
                sitePrevalenceReport.add(entry) ;
            }
            
        }
        return sitePrevalenceReport ;
    }
    
    /**
     * 
     * @param siteNames
     * @return ArrayList of coprevalence of coninfection of Sites named in siteNames.
     */
    public ArrayList<String> prepareCoPrevalenceReport(String[] siteNames) 
    {
        ArrayList<String> siteCoPrevalenceReport = new ArrayList<String>() ;
        
        int population = getPopulation() ; // Integer.valueOf(getMetaDatum("Community.POPULATION")) ;
        
        Double coprevalence ;
        String entry ;
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport())
        {
            for (String record : input)
            {
                entry = record ;
                for (String siteName : siteNames)
                {
                    entry = BOUNDED_STRING_BY_VALUE(siteName,TRUE,AGENTID,entry) 
                            + BOUNDED_STRING_BY_VALUE(siteName,FALSE,AGENTID,entry) ;
                }
                if (entry.isEmpty())
                    coprevalence = 0.0 ;
                else 
                    coprevalence = (Double.valueOf(COUNT_VALUE_INCIDENCE(AGENTID,"",entry,0)[1]))/population ;
                siteCoPrevalenceReport.add("coprevalence:" + String.valueOf(coprevalence) + " ") ;
                //LOGGER.info("coprevalence:" + String.valueOf(coprevalence) + " ");
            }
        }
        return siteCoPrevalenceReport ;
    }
    
    /** 
     * 
     * @return Report indicating the number and per population of incidents
     * of infection.
     */
    public ArrayList<String> prepareNotificationsReport()
    {
        return prepareNotificationsReport("") ;
    }
    
    public ArrayList<String> prepareNotificationsReport(String siteName)
    {
        ArrayList<String> notificationsReport = new ArrayList<String>() ;
        
        int notifications ;
        double rate ;
        int population = getPopulation() ;
        String output ;
        
        // Loop through Reporter input files 
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
            for (String record : input)
            {
                if (!siteName.isEmpty())
                    record = BOUNDED_STRING_BY_VALUE(siteName,TRUE,AGENTID,record) 
                            + BOUNDED_STRING_BY_VALUE(siteName,FALSE,AGENTID,record) ;
                
                notifications = COUNT_VALUE_INCIDENCE("treated","",record,0)[1];
                rate = ((double) notifications)/population;

                output = Reporter.ADD_REPORT_PROPERTY("notification", notifications) ;
                output += Reporter.ADD_REPORT_PROPERTY("rate", rate) ;

                notificationsReport.add(output) ;
            }
        return notificationsReport ;
    }

    /**
     * Prepare a report consisting of all agents id as key
     * this maps to a HashMap containing the three sites Rectum, Urethra, Pharynx
     * each site will contain its record status
     * @param endCycle
     * @return
     */
    public HashMap<String, HashMap<String, String>> prepareRawAgentSiteReport(int endCycle, HashSet<String> agentIdSet) {
        String[] siteNames = Site.getAvailableSites();

        ArrayList<String> screeningBackCycles = this.getBackCyclesReport(0, 0, 1, endCycle);
        
        // extract site data
        String lastCycle = screeningBackCycles.get(screeningBackCycles.size() - 1);
        HashMap<String, String> lastCycleHashMap = SPLIT_RECORD_BY_PROPERTY("agentId", lastCycle);        
        HashMap<String, String> infectiousAgentsHashMap = new HashMap<String, String>();
        for (String agentId : lastCycleHashMap.keySet()) {
            if (lastCycleHashMap.get(agentId).contains("tested:clear")) continue;
            infectiousAgentsHashMap.put(agentId, lastCycleHashMap.get(agentId));
        }
        
        HashMap<String, HashMap<String, String>> returnReport = new HashMap<String, HashMap<String, String>> ();
        
        // populate report with empty strings - all sites for all agents clear
        for (String agentId : agentIdSet) {
            HashMap<String, String> siteHashMap = new HashMap<String, String>();
            for (String site : siteNames) siteHashMap.put(site, "");
            returnReport.put(agentId, siteHashMap);
        }

        // modify internal hashmap with infectious agents
        for (String agentId : infectiousAgentsHashMap.keySet()) {
            String agentInfectiousRecord = infectiousAgentsHashMap.get(agentId);
            for (String site : siteNames) {
                // if the site is infectious
                if (agentInfectiousRecord.contains(site)) {

                    // extract correct values
                    String symptomatic = EXTRACT_VALUE(site, agentInfectiousRecord);
                    String[] infectionAndIncubation = extractInfectionAndIncubationTimeFromBackCycles(agentId, site, symptomatic, endCycle);  // TODO backcycles
                    String infectionTime = infectionAndIncubation[0];
                    String incubationTime = infectionAndIncubation[1]; // TODO work out incubation time

                    // build the new record
                    String newSiteRecord = Reporter.ADD_REPORT_PROPERTY("symptomatic", symptomatic) ;
                    newSiteRecord += Reporter.ADD_REPORT_PROPERTY("infectionTime", infectionTime);
                    newSiteRecord += Reporter.ADD_REPORT_PROPERTY("incubationTime", incubationTime);
                    newSiteRecord = newSiteRecord.substring(0, newSiteRecord.length() - 1);
                    
                    // replace with the new record
                    returnReport.get(agentId).put(site, newSiteRecord);
                }
            }
        }
        
        return returnReport;
    }

    /**
     * loop backwards over screen report back cycles to extract infectionTime and incubationTime
     * @param screeningBackCycles
     * @param agentId
     * @param site
     * @param symptomatic
     * @return
     */
    private String[] extractInfectionAndIncubationTimeFromBackCycles(String agentId, String site, String symptomatic, int endCycle) {
        ArrayList<String> screeningBackCycles = this.getBackCyclesReport(0, 0, endCycle, endCycle);

        int foundCycle = screeningBackCycles.size() - 1;
        for (int i = screeningBackCycles.size() - 1; i >= 0; --i) {
            foundCycle -= 1;
            String screenCycleRecord = screeningBackCycles.get(i);
            HashMap<String, String> screenedAgents = SPLIT_RECORD_BY_PROPERTY(AGENTID, screenCycleRecord);

            // if agent is not screened or when screened, the given site is non-infectious, the agent is cleared
            if (!screenedAgents.containsKey(agentId) || !screenedAgents.get(agentId).contains(site)) break;
        }
        foundCycle += 1;
        int infectionDuration = 0;
        int incubationDuration = 0;

        switch (site) {
            case "Pharynx":
                Pharynx pharynx = new Pharynx();
                infectionDuration = pharynx.getInfectionDuration();
                incubationDuration = pharynx.chooseIncubationTime();
                break;
            case "Rectum":
                Rectum rectum = new Rectum();
                infectionDuration = rectum.getInfectionDuration();
                incubationDuration = rectum.chooseIncubationTime();
                break;
            case "Urethra":
                Urethra urethra = new Urethra();
                infectionDuration = urethra.getInfectionDuration();
                incubationDuration = urethra.chooseIncubationTime();
                break;
        }

        // infectionDuration = (new Class.forName("PRSP.PrEPSTI.site"+site).getDeclaredConstructor().newInstance()).getInfectionDuration() ;

        int infectionTime = infectionDuration - ((screeningBackCycles.size() - 1) - foundCycle);
        int incubationTime = Math.max(0, incubationDuration - ((screeningBackCycles.size() - 1) - foundCycle));

        // return
        String[] infectionAndIncubation = new String[2];
        infectionAndIncubation[0] = String.valueOf(infectionTime);
        infectionAndIncubation[1] = String.valueOf(incubationTime);
        return infectionAndIncubation;
    }

    /**
     * Returns a HashMap report where the key = agentId, value = site record for reboot
     * ending at a given cycle
     * @param endCycle
     * @param agentIdSet
     * @return
     */
    public HashMap<String, String> prepareAgentSiteReport(int endCycle, HashSet<String> agentIdSet) {
        String[] siteNames = Site.getAvailableSites();

        HashMap<String, HashMap<String, String>> rawReport = prepareRawAgentSiteReport(endCycle, agentIdSet);

        HashMap<String, String> returnReport = new HashMap<String, String>();
        for (String agentId : agentIdSet) {
            String agentRecord = "";
            for (String site : siteNames) {
                String siteRecord = rawReport.get(agentId).get(site);
                agentRecord += Reporter.ADD_REPORT_PROPERTY("Site",site);
                if (siteRecord.length() > 0) agentRecord += siteRecord + " ";
            }
            returnReport.put(agentId, agentRecord);
        }

        return returnReport;
    }
    
}
