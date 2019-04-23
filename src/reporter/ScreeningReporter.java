/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter;

import agent.MSM;
import community.Community ;
/**
 *
 * @author MichaelWalker
 */

import java.io.* ;
import java.util.ArrayList ;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;


public class ScreeningReporter extends Reporter {

    static String INFECTED = "infected" ;
    static String SYMPTOMATIC = "symptomatic" ;
    static String TESTED = "tested" ;
    static String TREATED = "treated" ;
    
    static boolean writeReport = true ;
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
    public HashMap<Object,Number[]> 
        prepareYearsPositivityRecord(String[] siteNames, boolean unique, int backYears, int lastYear) 
        {
            HashMap<Object,Number[]> positivityRecordYears = new HashMap<Object,Number[]>() ;
            
            // Whether to save this Report to file
            boolean writeLocal = writeReport ;
            // Do not save subreports
            writeReport = false ;
            
            //Count from the last cycle of the simulation.
            int maxCycles = getMaxCycles() ;
            
            HashMap<Object,Number[]> positivityRecord ;
            for (int year = 0 ; year < backYears ; year++ )
            {
                Number[] yearlyPositivityRecord = new Number[siteNames.length] ;
               
                //endCycle = maxCycles - year * DAYS_PER_YEAR ;
                positivityRecord = prepareFinalNotificationsRecord(siteNames, unique, year, 0, DAYS_PER_YEAR, maxCycles);
               
                // [1] for positivity
                for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
                    yearlyPositivityRecord[siteIndex] = positivityRecord.get(siteNames[siteIndex])[1] ;
                
                positivityRecordYears.put(lastYear - year, (Number[]) yearlyPositivityRecord.clone()) ;
            }
            if (writeLocal)
                WRITE_CSV(positivityRecordYears, "Year", siteNames, "Positivity", simName, getFolderPath()) ;
            writeReport = writeLocal ;
        
            return positivityRecordYears ;
        }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param lastYear
     * @return Year-by-year report for backYears years on notification on last day
     * of each year ending lastYear.
     */
    public HashMap<Object,Number[]> 
        prepareYearsNotificationsRecord(String[] siteNames, int backYears, int lastYear) 
        {
            HashMap<Object,Number[]> notificationRecordYears = new HashMap<Object,Number[]>() ;
            
            int maxCycles = getMaxCycles() ;
            
            HashMap<Object,Number[]> notificationsRecord ;
            for (int year = 0 ; year < backYears ; year++ )
            {
                Number[] yearlyNotificationsRecord = new Number[siteNames.length] ;
               
                //endCycle = maxCycles - year * DAYS_PER_YEAR ;
                notificationsRecord = prepareFinalNotificationsRecord(siteNames, year, 0, DAYS_PER_YEAR, maxCycles);
               
                // [0] for positivity
                for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
                    yearlyNotificationsRecord[siteIndex] = notificationsRecord.get(siteNames[siteIndex])[0] ;
                
                notificationRecordYears.put(lastYear - year, (Number[]) yearlyNotificationsRecord.clone()) ;
            }
            
            return notificationRecordYears ;
        }
    
    /**
     * 
     * @param siteNames
     * @return Records of final incidence for specified siteNames and in total.
     */
    public HashMap<Object,Number[]> prepareFinalNotificationsRecord(String[] siteNames, boolean unique, int backMonths, int backDays)
    {
        int endCycle = getMaxCycles() ;
        
        return prepareFinalNotificationsRecord(siteNames, unique, 0, backMonths, backDays, endCycle) ;
    }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param endCycle
     * @return Records of final notifications for specified siteNames and in total.
     */
    public HashMap<Object,Number[]> prepareFinalNotificationsRecord(String[] siteNames, int backYears, int backMonths, int backDays, int endCycle)
    {
        return prepareFinalNotificationsRecord(siteNames, true, backYears, backMonths, backDays, endCycle) ;
    }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param endCycle
     * @param unique  - Count one positive result per Agent. 
     * @return Records of final notifications for specified siteNames and in total.
     */
    public HashMap<Object,Number[]> prepareFinalNotificationsRecord(String[] siteNames, boolean unique, int backYears, int backMonths, int backDays, int endCycle)
    {
        HashMap<Object,Number[]> finalNotifications = new HashMap<Object,Number[]>() ;
        
        endCycle -= backYears * DAYS_PER_YEAR ;
        //double daysBetweenTests = 505 ;    // PROPORTION_HIV * 6 * 71 + (1-PROPORTION_HIV) * 6 * 85.5
        int notifications ;
        //double nbTests ;
        String record ;
        String positiveRecord ;
        //String finalIncidenceRecord ; // getFinalRecord() ;
        ArrayList<String> finalNotificationsReport = getBackCyclesReport(0, backMonths, backDays, endCycle) ;
        
        double population = 3500000 ; // getPopulation() ; // 15-64yo NSW males 
        /**Sorting by statusHIV
        PopulationReporter populationReporter = new PopulationReporter(getMetaDatum("Community.NAME_ROOT"), getFolderPath()); 
        HashMap<Object,ArrayList<Object>> sortingReport = populationReporter.sortStatusHIV() ;
        ArrayList<Object> sortedAgents = sortingReport.get(TRUE) ;
        population = sortedAgents.size() ;
        */
        
        // Adjust for portion of year sampled //! and units of 100 person-years
        double denominator = ((double) getBackCycles(0,backMonths,backDays)*population)/(100000*DAYS_PER_YEAR) ; // daysBetweenTests) ; //DAYS_PER_YEAR) ; // *population/100000
        for (String siteName : siteNames)
        {
            notifications = 0 ;
            ArrayList<Object> positiveAgents = new ArrayList<Object>() ;
            for (String finalIncidenceRecord : finalNotificationsReport)
            {
                //sortedRecord = BOUNDED_STRING_FROM_ARRAY(AGENTID,sortedAgents,AGENTID,finalIncidenceRecord) ;
                
                // Count infected siteName
                record = BOUNDED_STRING_BY_CONTENTS(siteName,AGENTID,finalIncidenceRecord) ;  // sortedRecord) ;
                // Count Agents with positive tests
                positiveRecord = BOUNDED_STRING_BY_CONTENTS("treated",AGENTID,record) ;
                
                // Extract agentIds with positive result
                ArrayList<Object> positiveList = EXTRACT_ALL_VALUES(AGENTID,positiveRecord) ;
                notifications += positiveList.size() ;
                //notifications += COUNT_VALUE_INCIDENCE("treated","",record,0)[1] ;
                
                // Avoid double counting, ACCESS counts unique patient visits
                if (unique)
                    positiveList.removeAll(positiveAgents) ;
                positiveAgents.addAll(positiveList) ;
            }
            Number[] entry = new Number[] {notifications/denominator,positiveAgents.size()} ;
            finalNotifications.put(siteName,entry) ;
        }
        notifications = 0 ;
        //nbTests = 0 ;
        ArrayList<Object> testedAgents = new ArrayList<Object>() ;
        ArrayList<Object> positiveAgents = new ArrayList<Object>() ;
        for (String finalNotificationsRecord : finalNotificationsReport)
        {
            notifications += COUNT_VALUE_INCIDENCE("treated","",finalNotificationsRecord,0)[1] ;
            
            record = BOUNDED_STRING_BY_CONTENTS("tested",AGENTID,finalNotificationsRecord) ;
            ArrayList<Object> testedList = EXTRACT_ALL_VALUES(AGENTID,record) ;
            // Avoid double counting Agents
            if (unique)
                    testedList.removeAll(testedAgents) ;
            testedAgents.addAll(testedList) ;
            
            // Positive Agents
            record = BOUNDED_STRING_BY_CONTENTS("treated",AGENTID,finalNotificationsRecord) ;
            ArrayList<Object> positiveList = EXTRACT_ALL_VALUES(AGENTID,record) ;
            // Avoid double counting
            if (unique)
                    positiveList.removeAll(positiveAgents) ;
            positiveAgents.addAll(positiveList) ;
                
        }
        double nbTested = (double) testedAgents.size() ;
        double nbTreated = (double) positiveAgents.size() ;
        Number[] entry = new Number[] {notifications/denominator,nbTreated/nbTested} ;
        finalNotifications.put("all",entry) ;
        
        // Correct siteName entries by nbTests
        for (String siteName : siteNames)
        {
            entry = finalNotifications.get(siteName) ;
            notifications = entry[1].intValue() ;
            entry[1] = (Integer) notifications/nbTested ;
            finalNotifications.put(siteName,entry) ;
        }
        
        if (writeReport)
            WRITE_CSV(finalNotifications, "Site", new String[] {"incidence","positivity"}, "finalNotifications", simName, getFolderPath()) ;
        return finalNotifications ;
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
    public HashMap<Object,Number[]> prepareFinalNotificationsRecord_Site(String[] siteNames, boolean unique, int backYears, int backMonths, int backDays, int endCycle)
    {
        HashMap<Object,Number[]> finalNotifications = new HashMap<Object,Number[]>() ;
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
            ArrayList<Object> testedList = new ArrayList<Object>() ;
            ArrayList<Object> positiveList = new ArrayList<Object>() ;
            
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
        
        if (writeReport)
            WRITE_CSV(finalNotifications, "Site", new String[] {"incidence","positivity"}, "finalNotifications", simName, getFolderPath()) ;
        return finalNotifications ;
    }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param lastYear
     * @return Year-by-year report for backYears years on prevalence on last day
     * of each year ending lastYear.
     */
    public HashMap<Object,Number[]> 
        prepareYearsPrevalenceRecord(String[] siteNames, int backYears, int lastYear) 
        {
            HashMap<Object,Number[]> prevalenceRecordYears = new HashMap<Object,Number[]>() ;
            
            int maxCycles = getMaxCycles() ;
            
            int endCycle ;
            HashMap<Object,Number> prevalenceRecord ;
            for (int year = 0 ; year < backYears ; year++ )
            {
                Number[] yearlyPrevalenceRecord = new Number[siteNames.length] ;
               
                endCycle = maxCycles - year * DAYS_PER_YEAR ;
                prevalenceRecord = prepareFinalPrevalencesRecord(siteNames, endCycle);
               
                for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
                    yearlyPrevalenceRecord[siteIndex] = prevalenceRecord.get(siteNames[siteIndex]) ;
                
                prevalenceRecordYears.put(lastYear - year, (Number[]) yearlyPrevalenceRecord.clone()) ;
            }
            
            return prevalenceRecordYears ;
        }
    
    /**
     * 
     * @param siteNames
     * @return Records of final prevalences for specified siteNames and in total.
     */
    public HashMap<Object,Number> prepareFinalPrevalencesRecord(String[] siteNames)
    {
        int endCycle = getMaxCycles() ;
        
        return prepareFinalPrevalencesRecord(siteNames, endCycle) ;
    }
    
    /**
     * 
     * @param siteNames
     * @return Records of final prevalences for specified siteNames and in total.
     */
    public HashMap<Object,Number> prepareFinalPrevalencesRecord(String[] siteNames, int endCycle)
    {
        HashMap<Object,Number> finalPrevalencesRecord = new HashMap<Object,Number>() ;
        
        int prevalence ;
        // Number of times a Site is mentioned, regardless of infection status
        int[] mentions ;
        
        String finalPrevalenceRecord = getBackCyclesReport(0,0,1,endCycle).get(0) ; // getFinalRecord() ;
        
        double population = getPopulation() ; 
        for (String siteName : siteNames)
        {
            // Count infected siteName
            mentions = COUNT_VALUE_INCIDENCE(siteName,CLEAR,finalPrevalenceRecord,0) ;
            finalPrevalencesRecord.put(siteName,(mentions[1] - mentions[0])/population) ;
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
        finalPrevalencesRecord.put("all",prevalence/population) ;
        
        return finalPrevalencesRecord ;
    }
 
    /**
     * 
     * @param siteNames
     * @return Records of final symptomatic prevalences for specified siteNames and in total.
     */
    public HashMap<Object,Number> prepareFinalSymptomaticRecord(String[] siteNames)
    {
        HashMap<Object,Number> finalSymptomaticRecords = new HashMap<Object,Number>() ;
        
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
     * @return (HashMap) Report number of tests maps to number of Agents taking 
     * that many tests in given time frame.
     */
    public HashMap<Object,Number> prepareNumberAgentTestingReport(int backYears, int backMonths, int backDays)
    {
        HashMap<Object,Number> numberAgentTestingReport = new HashMap<Object,Number>() ;
        
        // (HashMap) agentId maps to (ArrayList) of cycles in which Agent was tested.
        HashMap<Object,ArrayList<Object>> agentTestingReport 
                = prepareAgentTestingReport(backYears, backMonths, backDays) ;
        
        int population = getPopulation() ;
        int untested = population ;
        
        int nbTests ;
        for (ArrayList<Object> value : agentTestingReport.values())
        {
            nbTests = value.size() ;
            numberAgentTestingReport = INCREMENT_HASHMAP(nbTests,numberAgentTestingReport) ;
            untested-- ;
        }
        numberAgentTestingReport.put(0, untested) ;
        
        for (Object tests : numberAgentTestingReport.keySet())
            numberAgentTestingReport.put(tests,(numberAgentTestingReport.get(tests).doubleValue())/population) ;
        
        return numberAgentTestingReport ;
    }
        
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) agentId maps to (ArrayList) of cycles in which Agent was tested.
     */
    public HashMap<Object,ArrayList<Object>> prepareAgentTestingReport(int backYears, int backMonths, int backDays)
    {
        HashMap<Object,ArrayList<Object>> agentTestingReport = new HashMap<Object,ArrayList<Object>>() ; 
        
        int maxCycles = getMaxCycles() ;
        int backCycles = GET_BACK_CYCLES(backYears, backMonths, backDays, maxCycles) ;
        int startCycle = maxCycles - backCycles ;
        
        ArrayList<String> inputReport = getBackCyclesReport(backYears, backMonths, backDays) ;
        String record ;
        String agentId ;
        for (int cycle = 0 ; cycle < backCycles ; cycle++ )
        {
            record = inputReport.get(cycle) ;
            
            ArrayList<String> agentReport = EXTRACT_ARRAYLIST(record,AGENTID,TESTED) ;
            for (String agentRecord : agentReport)
            {
                agentId = EXTRACT_VALUE(AGENTID,agentRecord) ;
                UPDATE_HASHMAP(agentId, startCycle + cycle, agentTestingReport) ;
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
    public HashMap<Object,Number[]> 
        prepareNumberAgentTreatedReport(int backYears, int backMonths, int backDays, String sortingProperty, int maxNumber)
    {
        HashMap<Object,Number[]> sortedNumberAgentTreatedReport 
                = new HashMap<Object,Number[]>() ;
        
        // (HashMap) agentId maps to (ArrayList) of cycles in which Agent was tested.
        HashMap<Object,ArrayList<Object>> agentTreatedReport 
                = prepareAgentTreatedReport(backYears, backMonths, backDays) ;
        
        // For sorting agentTreatedReport
        HashMap<Object,HashMap<Object,ArrayList<Object>>> sortedAgentTreatedReport 
                = new HashMap<Object,HashMap<Object,ArrayList<Object>>>() ;
        
        // Generate required spaces in label names
        //categoryEntry.sort(null);
        int totalDigits = (int) Math.log10(maxNumber) ; // (Integer.valueOf(String.valueOf(categoryEntry.get(categoryEntry.size() - 1))))) + 1 ;
        
        
        // Sort agentTreatedReport according to sortingProperty of Agents
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        int startCycle = getMaxCycles() - getBackCycles(backYears,backMonths, backDays) ;
        ArrayList<Object> agentsDeadRecord = populationReporter.prepareAgentsDeadRecord(startCycle) ;
        
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
                sortedAgentTreatedReport.put(propertyKey, new HashMap<Object,ArrayList<Object>>()) ;
            
            if (agentTreatedReport.containsKey(agentId))
                sortedAgentTreatedReport.get(propertyKey).put(agentId, agentTreatedReport.get(agentId)) ;
            else
                sortedAgentTreatedReport.get(propertyKey).put(agentId, new ArrayList<Object>()) ;
        }
        int nbKeys = sortedAgentTreatedReport.size() ;
        int sortedPopulation ;
        int nbTreatments ;
        int keyIndex = 0 ;
        String treatmentsString = "" ;
        String maxNumberString = String.valueOf(maxNumber).concat("+") ;
        for (Object propertyKey : sortedAgentTreatedReport.keySet())
        {
            HashMap<Object,Number> numberAgentTreatedReport = new HashMap<Object,Number>() ;
            
            HashMap<Object,ArrayList<Object>> sortedTreatedReport 
                    = sortedAgentTreatedReport.get(propertyKey) ;
            
            // Read in values corresponding to propertyKey
            sortedPopulation = sortedTreatedReport.size() ;
            for (ArrayList<Object> value : sortedTreatedReport.values())
            {
                nbTreatments = value.size() ;
                numberAgentTreatedReport = INCREMENT_HASHMAP(nbTreatments,numberAgentTreatedReport) ;
            }

            for (Object treatments : numberAgentTreatedReport.keySet())
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
    public HashMap<Object,Number> prepareNumberAgentTreatedReport(int backYears, int backMonths, int backDays)
    {
        HashMap<Object,Number> numberAgentTreatedReport = new HashMap<Object,Number>() ;
        
        // (HashMap) agentId maps to (ArrayList) of cycles in which Agent was tested.
        HashMap<Object,ArrayList<Object>> agentTreatedReport 
                = prepareAgentTreatedReport(backYears, backMonths, backDays) ;
        
        int population = getPopulation() ;
        int untreated = population ;
        
        int nbTreatments ;
        for (ArrayList<Object> value : agentTreatedReport.values())
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
    public HashMap<Object,ArrayList<Object>> prepareAgentTreatedReport(int backYears, int backMonths, int backDays)
    {
        HashMap<Object,ArrayList<Object>> agentTreatedReport = new HashMap<Object,ArrayList<Object>>() ; 
        
        int maxCycles = getMaxCycles() ;
        int backCycles = GET_BACK_CYCLES(backYears, backMonths, backDays, maxCycles) ;
        int startCycle = maxCycles - backCycles ;
        
        ArrayList<String> inputReport = getBackCyclesReport(backYears, backMonths, backDays) ;
        String record ;
        String agentId ;
        for (int cycle = 0 ; cycle < backCycles ; cycle++ )
        {
            record = inputReport.get(cycle) ;
            
            ArrayList<String> agentReport = EXTRACT_ARRAYLIST(record,AGENTID,TREATED) ;
            for (String agentRecord : agentReport)
            {
                agentId = EXTRACT_VALUE(AGENTID,agentRecord) ;
                UPDATE_HASHMAP(agentId, startCycle + cycle, agentTreatedReport) ;
            }
        }
        return agentTreatedReport ;
    }
 
    /**
     * 
     * @return (ArrayList) indicating the total prevalence, prevalence of 
     * symptomatic infection, and proportion of symptomatic infection in each report cycle.
     */
    public ArrayList<Object> preparePrevalenceReport()
    {
        ArrayList<Object> prevalenceReport = new ArrayList<Object>() ;
        
        int population = getPopulation() ; // Integer.valueOf(getMetaDatum("Community.POPULATION")) ;
        int nbInfected ;
        int nbSymptomatic ;
        String entry ;
        String siteStatus ;
        String[] siteNames = MSM.SITE_NAMES ;
        boolean agentInfected ;
        boolean nextInput = true ; 

        while (nextInput)
        {
        
            for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
            {
                String siteName = siteNames[siteIndex] ;
                siteNames[siteIndex] = siteName.substring(0,1).toUpperCase() 
                        + siteName.substring(1) ;
            }
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
            nextInput = updateReport() ;
        }
        return prevalenceReport ;
    }
    
    /**
     * 
     * @param siteName
     * @return (ArrayList) indicating the total prevalence, prevalence of 
     * symptomatic infection, and proportion of symptomatic infection at site given 
     * by siteName in each report cycle.
     */
    public ArrayList<Object> preparePrevalenceReport(String siteName) 
    {
        ArrayList<Object> sitePrevalenceReport = new ArrayList<Object>() ;
        
        int population = Integer.valueOf(getMetaDatum("Community.POPULATION")) ;
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
        {
            int[] nbSymptomatic ;
            String entry ;
            for (String record : input)
            {
                nbSymptomatic = COUNT_VALUE_INCIDENCE(siteName,TRUE,record,0) ;

    //            if (nbSymptomatic[0] == nbSymptomatic[1])
    //                LOGGER.info(record);


                entry = ADD_REPORT_PROPERTY("prevalence",((double) nbSymptomatic[1])/population) ;
                entry += ADD_REPORT_PROPERTY("symptomatic",((double) nbSymptomatic[0])/population) ;
                entry += ADD_REPORT_PROPERTY("proportion",((double) nbSymptomatic[0])/nbSymptomatic[1]) ;
                sitePrevalenceReport.add(entry) ;
            }
            
        }
        return sitePrevalenceReport ;
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
    public ArrayList<Object> prepareCoPrevalenceReport(String[] siteNames) 
    {
        ArrayList<Object> siteCoPrevalenceReport = new ArrayList<Object>() ;
        
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
    public ArrayList<Object> prepareNotificationsReport()
    {
        return prepareNotificationsReport("") ;
    }
    
    public ArrayList<Object> prepareNotificationsReport(String siteName)
    {
        ArrayList<Object> notificationsReport = new ArrayList<Object>() ;
        
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
    
}
