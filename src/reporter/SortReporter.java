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

    /**
     * 
     * @param values
     * @return HashMap sorting values -> correspondingTransmissionReport
     */
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
     * @return HashMap of age to mean number of Relationships entered into by 
     * that age.
     */
    public HashMap<Object,Double> prepareAgeNumberEnteredRelationshipRecord()
    {
        HashMap<Object,Double> ageNumberEnteredRelationshipRecord 
                = new HashMap<Object,Double>() ;

        // key:age value:ArrayList of new Relationships for each Agent, sums to 
        //total number of new Relationships formed by that age.
        HashMap<Object,ArrayList<Object>> ageEnteredRelationshipRecord 
                = new HashMap<Object,ArrayList<Object>>() ;
        
        //key:agentId value: Age for final record in report
        HashMap<Object,Integer> sortAgeRecord = ((PopulationReporter) sortingReporter).sortAgeRecord() ;
        
        // Each record is a HashMap indicating new relationshipIds for relevant (key) Agents
        ArrayList<HashMap<Object,ArrayList<Object>>> agentsEnteredRelationshipReport 
                = ((RelationshipReporter) unsortedReporter).prepareAgentsEnteredRelationshipReport() ;
        
        for (HashMap<Object,ArrayList<Object>> enteredRelationshipRecord : agentsEnteredRelationshipReport)
            for (Object agentId : enteredRelationshipRecord.keySet())
                ageEnteredRelationshipRecord =
                        updateHashMap(sortAgeRecord.get(agentId),enteredRelationshipRecord.get(agentId).size(),ageEnteredRelationshipRecord) ; 
        
        for (Object ageKey : ageEnteredRelationshipRecord.keySet())
        {
            ArrayList<Object> ageRecord = (ArrayList<Object>) ageEnteredRelationshipRecord.get(ageKey) ;
            
            // Agents forming no Relationships and hence no contribution to ageRecord must still be counted.
            int nbEntries = 0 ;
            for (Object agentId : sortAgeRecord.keySet())
                if (sortAgeRecord.get(agentId).equals(ageKey))
                    nbEntries++ ;
            // int nbEntries = ageRecord.size() ;
            
            // Add new Relationships formed by that age
            int sum = 0 ;
            for (Object number : ageRecord)
                sum += (Integer) number ;
            
            // Take the mean per Agent
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
     * new Relationships in the last backYears
     */
    public ArrayList<Object> prepareSortPrevalenceReport(int partnerCount, int backYears)
    {
        ArrayList<Object> prevalenceReport = new ArrayList<Object>() ;
        int daysPerYear = 365 ;
        int maxCycles = input.size() ;
        int population ;
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
        int recordIndex = maxCycles- 1 ;
        String record = input.get(recordIndex) ;
        //LOGGER.info(record);
        ArrayList<ArrayList<Object>> screenSortAgentNewPartnersReport 
            = prepareScreenSortAgentNewPartnersReport(partnerCount, backYears, recordIndex) ;

        // Want prevalence only among agents with given number of partners.
        agentIdArray = screenSortAgentNewPartnersReport.get(partnerCount) ;
        population = agentIdArray.size() ;
        nbSymptomatic = 0 ;
        ArrayList<String> infections = extractArrayList(record,AGENTID) ;
        nbInfected = infections.size() ;
        for (String infection : infections)
        {
            if (!agentIdArray.contains(extractValue(AGENTID,infection))) 
                    continue ;
            for (String siteName : siteNames)
                if (compareValue(siteName,TRUE,infection))
                {
                    nbSymptomatic++ ;
                    break ;
                }
        }
        //LOGGER.info(record) ;

        //population = Community.POPULATION ;
        if (population > 0)
        {
            entry = addReportProperty("prevalence",((double) nbInfected)/population) ;
            entry += addReportProperty("symptomatic",((double) nbSymptomatic)/population) ;
            entry += addReportProperty("proportion",((double) nbSymptomatic)/nbInfected) ;
        }
        else
        {
            entry = addReportProperty("prevalence",0.0) ;
            entry += addReportProperty("symptomatic",0.0) ;
            entry += addReportProperty("proportion",0.0) ;
        }

        prevalenceReport.add(entry) ;
        return prevalenceReport ;
    }
    
    /**
     * 
     * @param partnerCount
     * @param backYears
     * @return count -> agentIds
     */
    public ArrayList<ArrayList<Object>> prepareScreenSortAgentNewPartnersReport(int partnerCount, int backYears, int recordIndex)
    {
        // number of partners -> agentIds
        ArrayList<ArrayList<Object>> screenSortAgentNewPartnersReport = new ArrayList<ArrayList<Object>>() ;
        for (int count = 0 ; count <= partnerCount ; count++ )
            screenSortAgentNewPartnersReport.add(new ArrayList<Object>()) ;
        
        int daysPerYear = 365 ;
        
        ArrayList<ArrayList<Object>> sortingReport = ((RelationshipReporter) sortingReporter).prepareAgentCommenceReport() ;
        
        // New partners per agentId
        HashMap<Object,Integer> agentCommenceCount = new HashMap<Object,Integer>() ;

        // Count new relationships for each Agent over past backYears years
        for (int cycle = 0 ; cycle < backYears*daysPerYear ; cycle++)
            for (Object agentId : sortingReport.get(recordIndex-cycle))
                agentCommenceCount = incrementHashMap(agentId,agentCommenceCount) ;

        // Which Agents have had Array_position+1 new Relationships in last backYears
        for (Object agentKey : agentCommenceCount.keySet())    
        {
            int relationshipCount = agentCommenceCount.get(agentKey) ;
            if (relationshipCount < partnerCount)
                screenSortAgentNewPartnersReport.get(relationshipCount).add(agentKey) ;
            else
                screenSortAgentNewPartnersReport.get(partnerCount).add(agentKey) ;
        }
        return screenSortAgentNewPartnersReport ;
    }
}
