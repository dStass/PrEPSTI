/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter;

import agent.MSM;
import community.Community;
import reporter.EncounterReporter ;
import reporter.PopulationReporter ;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.logging.Level;
import static reporter.Reporter.extractArrayList;

/**
 *
 * @author MichaelWalker
 */
public class SortReporter extends Reporter {
    
    Reporter sortingReporter ;
    Reporter unsortedReporter ;
    
    
        
    public SortReporter(String simname, Reporter unsortedReporter, Reporter sortingReporter) {
        super(simname,unsortedReporter.input) ;
        
        this.unsortedReporter = unsortedReporter ;
        this.sortingReporter = sortingReporter ;
    }

    /*public SortReporter(String simname, String reportFilePath)
    {
        reportFilePath = Community.FILE_PATH + "EncounterReport" + reportFilePath + ".txt" ;
        Reader reader = new Reader(simname,reportFilePath) ;
        input = reader.getFiledReport() ;
    }
    */
    
    public Reporter getUnsortedReporter()
    {
        return unsortedReporter ;
    }
    
    public Reporter getSortingReporter()
    {
        return sortingReporter ;
    }
    
    /**
     * TODO: Sort out \<Object\> vs \<String\> both here and related Methods.
     * @return HashMap of records sorted according to values.
     */
    public HashMap<Object,HashMap<Object,ArrayList<Object>>> prepareReceiveSortPrepStatusRecord(String[] values)
    {
        HashMap<Object,ArrayList<Object>> transmissionRecord = ((EncounterReporter) unsortedReporter).prepareAgentToAgentRecord() ;
        
        HashMap<Object,ArrayList<Object>> sortingReport = ((PopulationReporter) sortingReporter).sortPrepStatus() ;
        
        //String[] values = new String[] {TRUE, FALSE} ;
        
        return Reporter.sortRecord(transmissionRecord, sortingReport, values) ;
    }
    
    
    public HashMap<Object,HashMap<Object,ArrayList<Object>>>
        prepareReceiveSortPrepStatusReport(String value )
        {
            HashMap<Object,HashMap<Object,ArrayList<Object>>> outputHashMap 
                    = prepareReceiveSortPrepStatusReport(new String[] {value}).get(value) ;
            return outputHashMap ;
        }
    
    public HashMap<Object,HashMap<Object,HashMap<Object,ArrayList<Object>>>> 
        prepareReceiveSortPrepStatusReport(String[] values )
    {
        LOGGER.info("prepareAgentToAgentReport()");
        //EncounterReporter encounterReporter = new EncounterReporter(NONE, reporter.input) ;
        HashMap<Object,HashMap<Object,ArrayList<Object>>> transmissionReport = ((EncounterReporter) unsortedReporter).prepareAgentToAgentReport() ;
        //LOGGER.log(Level.INFO, "{0}", transmissionReport);
        LOGGER.info("sortPrepStatus()");
        //PopulationReporter populationReporter = new PopulationReporter(NONE, report) ;
        HashMap<Object,ArrayList<Object>> sortingReport = ((PopulationReporter) sortingReporter).sortPrepStatus() ;
        LOGGER.log(Level.INFO, "{0}", sortingReport);
        
        LOGGER.info("sortReport()");
        //String[] values = new String[] {TRUE, FALSE} ;
        return Reporter.sortReport(transmissionReport, sortingReport, values) ;
    }
        
        
    /**
     * 
     * @return HashMap of age to mean number of Relationships entered into at 
     * that age.
     */
    public HashMap<Object,Double> prepareAgeNumberEnteredRelationshipRecord()
    {
        HashMap<Object,Double> ageNumberEnteredRelationshipRecord 
                = new HashMap<Object,Double>() ;

        HashMap<Object,ArrayList<Object>> ageEnteredRelationshipRecord 
                = new HashMap<Object,ArrayList<Object>>() ;
        
        //TODO: key:agentId value: Age
        HashMap<Object,Integer> sortAgeRecord = ((PopulationReporter) sortingReporter).sortAgeRecord() ;
        
        ArrayList<HashMap<Object,ArrayList<Object>>> agentsEnteredRelationshipReport 
                = ((RelationshipReporter) unsortedReporter).prepareAgentsEnteredRelationshipReport() ;
        
        for (HashMap<Object,ArrayList<Object>> enteredRelationshipRecord : agentsEnteredRelationshipReport)
            for (Object agentId : enteredRelationshipRecord.keySet())
                ageEnteredRelationshipRecord =
                        updateHashMap(sortAgeRecord.get(agentId),enteredRelationshipRecord.get(agentId).size(),ageEnteredRelationshipRecord) ; 
        for (Object ageKey : ageEnteredRelationshipRecord.keySet())
        {
            ArrayList<Object> ageRecord = (ArrayList<Object>) ageEnteredRelationshipRecord.get(ageKey) ;
            int nbEntries = 0 ;
            for (Object agentId : sortAgeRecord.keySet())
                if (sortAgeRecord.get(agentId).equals(ageKey))
                    nbEntries++ ;
            // int nbEntries = ageRecord.size() ;
            int sum = 0 ;
            for (Object number : ageRecord)
                sum += (Integer) number ;
            double mean = ((double) sum)/nbEntries ;
            ageNumberEnteredRelationshipRecord.put(ageKey,mean) ;
        }
        return ageNumberEnteredRelationshipRecord ;
    }
    

    /**
     * 
     * @param partnerCount
     * @param backYear
     * @return prevalence report including only those Agents with the given number of
     * new Relationships in the past backYears
     */
    public ArrayList<Object> prepareSortPrevalenceReport(int partnerCount, int backYear)
    {
        ArrayList<Object> prevalenceReport = new ArrayList<Object>() ;
        int daysPerYear = 365 ;
        int maxCycles = input.size() ;
        int population = 0 ;
        int nbInfected ;
        int nbSymptomatic ;
        String entry ;
        
        String[] siteNames = MSM.SITE_NAMES ;
        for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
        {
            String siteName = siteNames[siteIndex] ;
            siteNames[siteIndex] = siteName.substring(0,1).toUpperCase() 
                    + siteName.substring(1) ;
        }
        
        ArrayList<Object> agentIdArray ; 
        
        for (int recordIndex = backYear*daysPerYear ; recordIndex < input.size() ; recordIndex++ ) // (String record : input)
        {
            String record = input.get(recordIndex) ;
            ArrayList<ArrayList<ArrayList<Object>>> screenSortAgentNewPartnersReport 
                = prepareScreenSortAgentNewPartnersReport(partnerCount, backYear, recordIndex) ;
            agentIdArray = screenSortAgentNewPartnersReport.get(backYear-1).get(partnerCount) ;
            population = agentIdArray.size() ;
            nbSymptomatic = 0 ;
            ArrayList<String> infections = extractArrayList(record,AGENTID) ;
            nbInfected = infections.size() ;
            for (String infection : infections)
            {
                if (!agentIdArray.contains(extractValue(AGENTID,infection))) 
                        continue ;
                for (String siteName : MSM.SITE_NAMES)
                    if (compareValue(siteName,TRUE,infection))
                    {
                        nbSymptomatic++ ;
                        break ;
                    }
            }
            //LOGGER.info(record) ;
            
            //population = Community.POPULATION ;
            
            entry = addReportProperty("prevalence",((double) nbInfected)/population) ;
            entry += addReportProperty("symptomatic",((double) nbSymptomatic)/population) ;
            entry += addReportProperty("proportion",((double) nbSymptomatic)/nbInfected) ;
            
            prevalenceReport.add(entry) ;
        }
        return prevalenceReport ;
    }
    
    /**
     * years back -> count -> agentIds
     * @param partnerCount
     * @param backYears
     * @return 
     */
    public ArrayList<ArrayList<ArrayList<Object>>> prepareScreenSortAgentNewPartnersReport(int partnerCount, int backYears, int recordIndex)
    {
        ArrayList<ArrayList<ArrayList<Object>>> screenSortAgentNewPartnersReport = new ArrayList<ArrayList<ArrayList<Object>>>() ;
        
        int daysPerYear = 365 ;
        
        ArrayList<ArrayList<Object>> sortingReport = ((RelationshipReporter) sortingReporter).prepareAgentCommenceReport() ;
        
        // number of partners -> agentIds
        ArrayList<ArrayList<Object>> commenceArray = new ArrayList<ArrayList<Object>>() ;
        for (int count = 0 ; count <= partnerCount ; count++ )
            commenceArray.add(new ArrayList<Object>()) ;
            
        // New partners per agentId
        HashMap<Object,Integer> agentCommenceCount = new HashMap<Object,Integer>() ;

        for (int count = 0 ; count <= partnerCount ; count++ )
            commenceArray.set(count, new ArrayList<Object>()) ;

        for (int year = 1 ; year <= backYears ; year++)
        {
            for (int cycle = (year-1)*daysPerYear ; cycle < year * daysPerYear ; cycle++)
                for (Object agentId : sortingReport.get(recordIndex-cycle))
                    agentCommenceCount = incrementHashMap(agentId,agentCommenceCount) ;
            for (Object agentKey : agentCommenceCount.keySet())    
            {
                int relationshipCount = agentCommenceCount.get(agentKey) ;
                if (relationshipCount < partnerCount)
                    commenceArray.get(relationshipCount).add(agentKey) ;
                else
                    commenceArray.get(partnerCount).add(agentKey) ;
            }
            screenSortAgentNewPartnersReport.add((ArrayList<ArrayList<Object>>) commenceArray.clone()) ;
        }
        return screenSortAgentNewPartnersReport ;
    }
}
