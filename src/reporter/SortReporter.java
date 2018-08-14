/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter;

import agent.MSM;
import community.Community;
import reporter.* ;
import reporter.EncounterReporter ;
//import reporter.ScreeningReporter ;
import reporter.RelationshipReporter ;
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
/**
     * @param simName
     * @param fileName 
     */
    public SortReporter(String simName, String reportFilePath, String unsortedName, String sortingName)
    {
        String unsortedClassName = "" ;
        if ("infection".equals(unsortedName))
            unsortedClassName = "Screening" ;
        else
            unsortedClassName = unsortedName.substring(0,1).toUpperCase() + unsortedName.substring(1).toLowerCase() ;
        unsortedClassName = "reporter." + unsortedClassName + "Reporter" ;
        String sortingClassName = "" ;
        if ("infection".equals(sortingName))
            sortingClassName = "Screening" ;
        else
            sortingClassName = sortingName.substring(0,1).toUpperCase() + sortingName.substring(1).toLowerCase() ;
        sortingClassName = "reporter." + sortingClassName + "Reporter" ;
        
        try
        {
            Class<?> unsortedReporterClazz = Class.forName(unsortedClassName) ;
            Class<?> sortingReporterClazz = Class.forName(sortingClassName) ;
            this.unsortedReporter = (Reporter) unsortedReporterClazz.newInstance() ;
            unsortedReporter.initReporter(simName, reportFilePath);
            this.sortingReporter = (Reporter) sortingReporterClazz.newInstance() ;
            sortingReporter.initReporter(simName, reportFilePath);
        }
        catch ( Exception e )
        {
            LOGGER.info(e.toString()) ;
        }
        
    }
    
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
    
    /**
     * 
     * @return HashMap of age to mean number of Relationships entered into by 
     * that age.
     */
    public HashMap<Object,Number> prepareAgeNumberEnteredRelationshipRecord()
    {
        HashMap<Object,Number> ageNumberEnteredRelationshipRecord 
                = new HashMap<Object,Number>() ;

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
     * @param backYears
     * @return prevalence report including only those Agents with the given number of
     * new Relationships in the last backYears
     */
    public ArrayList<String> prepareSortPrevalenceReport(int partnerCount, int backYears)
    {
        ArrayList<String> prevalenceReport = new ArrayList<String>() ;
        int daysPerYear = 365 ;
        int maxCycles = Community.MAX_CYCLES ;
        int population ;
        int nbInfected ;
        int nbSymptomatic ;
        String entry ;
        
        // SITE_NAMES from MSM.java with case corrected
        String[] siteNames = MSM.SITE_NAMES ;
        for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
        {
            String siteName = siteNames[siteIndex] ;
            siteNames[siteIndex] = siteName.substring(0,1).toUpperCase() 
                    + siteName.substring(1) ;
        }
        
        ArrayList<Object> agentIdArray = new ArrayList<Object>() ; 
        
        // FIXME: Only considers prevalences in final cycle.
        int recordIndex = maxCycles- 1 ;
        String record = unsortedReporter.getFinalRecord() ;
        
        // More than Nb new Relationships -> agentIds
        ArrayList<ArrayList<Object>> screenSortAgentNewPartnersReport 
            = prepareScreenSortAgentNewPartnersReport(partnerCount, backYears, recordIndex) ;

        for (int partnerIndex = partnerCount ; partnerIndex >= 0 ; partnerIndex--)
        {
            // Want prevalence only among agents with given number of partners.
            agentIdArray.addAll(screenSortAgentNewPartnersReport.get(partnerIndex)) ;
            population = agentIdArray.size() ;
            nbInfected = 0 ;
            nbSymptomatic = 0 ;
            // Determine nb of infected
            ArrayList<String> infections = extractArrayList(record,AGENTID) ;
            // Determine nb of symptomatic infected
            for (String infection : infections)
            {
                if (!agentIdArray.contains(extractValue(AGENTID,infection))) 
                        continue ;
                nbInfected++ ;
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
                entry += addReportProperty("population",population) ;
            }
            else
            {
                entry = addReportProperty("prevalence",0.0) ;
                entry += addReportProperty("symptomatic",0.0) ;
                entry += addReportProperty("proportion",0.0) ;
                entry += addReportProperty("population",0) ;
            }

            prevalenceReport.add(0,entry) ;
        }
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
        HashMap<Object,Number> agentCommenceCount = new HashMap<Object,Number>() ;

        // Modify backYears if necessary so you don't go past beginning of simulation
        if (daysPerYear*backYears > recordIndex)
            backYears = recordIndex/daysPerYear ;
        //for (int cyclesBack = daysPerYear*backYears ; cyclesBack > recordIndex ; cyclesBack = daysPerYear * backYears )
          //  backYears-- ;
        
        // TODO: Avoid multiple double looping over recent years
        // Count new relationships for each Agent over past backYears years
        for (int cycle = 0 ; cycle < backYears*daysPerYear ; cycle++)
            for (Object agentId : sortingReport.get(recordIndex-cycle))
                agentCommenceCount = incrementHashMap(agentId,agentCommenceCount) ;

        // Which Agents have had more than Array_position new Relationships in last backYears
        for (Object agentKey : agentCommenceCount.keySet())    
        {
            int relationshipCount = Integer.valueOf(String.valueOf(agentCommenceCount.get(agentKey))) ;
            if (relationshipCount <= partnerCount)
                screenSortAgentNewPartnersReport.get(relationshipCount-1).add(agentKey) ;
            else  // Had partnerCount or more new partners
                screenSortAgentNewPartnersReport.get(partnerCount).add(agentKey) ;
        }
        return screenSortAgentNewPartnersReport ;
    }
}
