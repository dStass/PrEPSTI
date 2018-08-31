/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter;

import reporter.EncounterReporter ;
import community.Community ;
import community.Relationship ;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays ;
import java.util.HashMap ;
import java.util.Collections ;
import java.util.Collection ;
import java.util.logging.Level;


/**
 *
 * @author MichaelWalker
 */
public class RelationshipReporter extends Reporter {
    
    static String DEATH = "death" ;
    static String BIRTH = "birth" ;
    static String AGE = "age" ;
    static String RELATIONSHIP_ID = Relationship.RELATIONSHIP_ID ;
    
    public RelationshipReporter()
    {
        
    }
    
    public RelationshipReporter(String simName, ArrayList<String> report) 
    {
        super(simName, report);
    }

    /**
     * @param simName (String) Root of simulation name.
     * @param reportFilePath (String) Path to saved files.
     */
    public RelationshipReporter(String simName, String reportFilePath)
    {
        super(simName, reportFilePath) ;
    }
    
    /**
     * 
     * @return ArrayList of ArrayLists of (String) RelationshipIds of relationships
     * commenced in each cycle
     */
    public ArrayList<ArrayList<Object>> prepareRelationshipCommenceReport()
    {
        ArrayList<ArrayList<Object>> relationshipCommenceReport = new ArrayList<ArrayList<Object>>() ;
        
        ArrayList<String> commenceReport = prepareCommenceReport() ;
        
        for (int recordNb = 0 ; recordNb < commenceReport.size() ; recordNb++ )
        {
            String record = commenceReport.get(recordNb) ;
            //LOGGER.info(record);
            //int startIndex = indexOfProperty(RELATIONSHIP_ID,record) ;
            relationshipCommenceReport.add(extractAllValues(RELATIONSHIP_ID, record,0)) ;
        }
        return relationshipCommenceReport ;
    }
    
    /**
     * 
     * @return ArrayList of ArrayLists of (String) RelationshipIds of relationships
     * of class relationshipClazz commenced in each cycle
     */
    public ArrayList<ArrayList<Object>> prepareRelationshipCommenceReport(String[] relationshipClazzes)
    {
        ArrayList<ArrayList<Object>> relationshipCommenceReport = new ArrayList<ArrayList<Object>>() ;
        
        ArrayList<String> commenceReport = prepareCommenceReport() ;
        String filteredRecord ;
        
        for (int reportNb = 0 ; reportNb < commenceReport.size() ; reportNb++ )
        {
            String record = commenceReport.get(reportNb) ;
            
            // Include only selected Relationships 
            filteredRecord = "" ;
            for (String relationshipClazz : relationshipClazzes)
                filteredRecord += boundedStringByValue("relationship",relationshipClazz,RELATIONSHIP_ID,record) ;
            if (filteredRecord.isEmpty())
                filteredRecord = record ;
            //LOGGER.info(record);
            //int startIndex = indexOfProperty(RELATIONSHIP_ID,record) ;
            relationshipCommenceReport.add(extractAllValues(RELATIONSHIP_ID, filteredRecord,0)) ;
            //relationshipCommenceReport.add(extractAllValues(AGENTID1, record,0)) ;
        }
        return relationshipCommenceReport ;
    }
    
    /**
     * 
     * @return ArrayList of ArrayLists of (String) RelationshipIds of relationships
     * that break up in each cycle
     */
    public ArrayList<ArrayList<Object>> prepareRelationshipBreakupReport()
    {
        ArrayList<ArrayList<Object>> relationshipBreakupReport = new ArrayList<ArrayList<Object>>() ;
        
        ArrayList<String> breakupReport = prepareBreakupReport() ;
        
        for (int reportNb = 0 ; reportNb < breakupReport.size() ; reportNb++ )
        {
            String report = breakupReport.get(reportNb) ;
            relationshipBreakupReport.add(extractAllValues(RELATIONSHIP_ID, report,0)) ;
            //relationshipBreakupReport.add(extractAllValues(AGENTID1, record,0)) ;
        }
        return relationshipBreakupReport ;
    }
    
    /**
     * 
     * @return ArrayList of ArrayLists of (String) RelationshipIds of relationships
     * commenced in each cycle
     */
    public ArrayList<ArrayList<Object>> prepareAgentCommenceReport()
    {
        ArrayList<ArrayList<Object>> agentCommenceReport = new ArrayList<ArrayList<Object>>() ;
        
        ArrayList<String> commenceReport = prepareCommenceReport() ;
        
        for (int recordNb = 0 ; recordNb < commenceReport.size() ; recordNb++ )
        {
            String record = commenceReport.get(recordNb) ;
            //LOGGER.info(record);
            //int startIndex = indexOfProperty(RELATIONSHIP_ID,record) ;
            ArrayList<Object> agentCommenceRecord = extractAllValues(AGENTID0,record,0) ;
            agentCommenceRecord.addAll(extractAllValues(AGENTID1,record,0)) ;
            
            agentCommenceReport.add((ArrayList<Object>) agentCommenceRecord.clone()) ;
        }
        return agentCommenceReport ;
    }
    
    /**
     * 
     * @param encounterReporter
     * @return (HashMap) relationshipId maps to number of associated transmissions 
     */
    public HashMap<Object,Number> prepareRelationshipTransmissionReport(EncounterReporter encounterReporter)
    {
        HashMap<Object,Number> relationshipTransmissionReport = new HashMap<Object,Number>() ;
        
        ArrayList<String> encounterReport = encounterReporter.getFullInput() ;
        String encounterRecord ;
        String transmission = "transmission" ;
        ArrayList<String> encounterArray ; 
        ArrayList<Object> transmissionArray ; 
        String[] agentIds ;
        String[] encounterAgentIds ;  // For Agents in encounter
        
        HashMap<Object,String[]> relationshipAgentReport = prepareRelationshipAgentReport() ;
        
        // When did each Relationship commence?
        ArrayList<ArrayList<Object>> relationshipCommenceReport 
                = prepareRelationshipCommenceReport() ;
        // When did each Relationship break-up? Used for efficiency.
        ArrayList<ArrayList<Object>> relationshipBreakupReport 
                = prepareRelationshipBreakupReport() ;
        // relationshipId -> commencement cycle
        ArrayList<Object> currentRelationshipIds = new ArrayList<Object>() ;
        //for (boolean nextInput = true ; nextInput ; nextInput = updateReport())
        int nbCycles = encounterReport.size() ;
        //LOGGER.info(String.valueOf(nbCycles));
        for (int cycle = 0 ; cycle < nbCycles ; cycle++ )
        {
            for (Object relationshipId : relationshipCommenceReport.get(cycle))
                currentRelationshipIds.add(relationshipId) ;
            encounterRecord = encounterReport.get(cycle) ;
            encounterArray = extractArrayList(encounterRecord,transmission) ;
            for (String encounter : encounterArray)
            {
                transmissionArray = extractAllValues(transmission,encounter,0) ;
                if (transmissionArray.contains(TRUE))
                {
                    encounterAgentIds = extractAgentIds(encounter) ;
                    for (Object relationshipId : currentRelationshipIds)
                    {
                        String[] relationshipAgentIds = relationshipAgentReport.get(relationshipId) ;
                        if (relationshipAgentIds[0].equals(encounterAgentIds[0]) && relationshipAgentIds[1].equals(encounterAgentIds[1]))
                        {
                            relationshipTransmissionReport = incrementHashMap(relationshipId,relationshipTransmissionReport) ;
                            continue ;
                        }
                        if (relationshipAgentIds[1].equals(encounterAgentIds[0]) && relationshipAgentIds[0].equals(encounterAgentIds[1]))
                        {
                            relationshipTransmissionReport = incrementHashMap(relationshipId,relationshipTransmissionReport) ;
                            continue ;
                        }
                    }
                }
            }
            for (Object relationshipId : relationshipBreakupReport.get(cycle))
                currentRelationshipIds.remove(relationshipId) ;
            
        }
        return relationshipTransmissionReport ;
    }
    
    /**
     * 
     * @param encounterReporter
     * @return (HashMap) Number of Relationships responsible for a given number 
     * or more transmissions.
     */
    public HashMap<Object,Number> prepareRelationshipCumulativeTransmissionReport(EncounterReporter encounterReporter)
    {
        HashMap<Object,Number> cumulativeRelationshipTransmissionReport = new HashMap<Object,Number>() ;

        HashMap<Object,Number> relationshipTransmissionReport = prepareRelationshipTransmissionReport(encounterReporter) ;
        
        Collection<Number> relationshipTransmissionValues = relationshipTransmissionReport.values() ;
        
        int maxValue = 0 ;
        int intValue = 0 ;
        for (Number value : relationshipTransmissionValues)
        {
            intValue = value.intValue() ;
            if (intValue > maxValue)
                maxValue = intValue ;
        }
        //Collections.max(relationshipTransmissionValues) ;
        
        // To track how agentIds have had more than given Relationships
        int relationshipsOver = 0 ;
        
        for (int key = maxValue ; key > 0 ; key-- )
        {
            relationshipsOver += Collections.frequency(relationshipTransmissionValues,key) ;
            cumulativeRelationshipTransmissionReport.put(key, relationshipsOver) ;
        }
        
        return cumulativeRelationshipTransmissionReport ;
    }        
   
    
    /**
     * Finds last relationshipId entered into for each agentId, then finds last
     * relationshipId broken off that is different from the last entered into
     * @return (HashMap) agentId maps to cycle of last commencement minus that of last 
     * breakup.
     */
    public HashMap<Object,Number> prepareAgentGapReport()
    {
        HashMap<Object,Number> agentGapReport = new HashMap<Object,Number>() ;
        
        // Latest cycle for agentId commencing relationship
        HashMap<Object,Integer> agentLatestCommencement = new HashMap<Object,Integer>() ;
        // Latest cycle for agentId ending relationship different from latest commencement
        HashMap<Object,Integer> agentLatestBreakup = new HashMap<Object,Integer>() ;
        // relaitonshipId of last Relationship to commence for each agentId
        HashMap<Object,String> agentLastRelationship = new HashMap<Object,String>() ;
        
        ArrayList<String> commenceReport = prepareCommenceReport() ;
        ArrayList<String> breakupReport = prepareBreakupReport() ;
        HashMap<Object,String[]> relationshipAgentReport = prepareRelationshipAgentReport() ;
            
        
        String breakupRecord ;
        String relationshipId ;
        String[] agentIds ; 
                
        for (int index = commenceReport.size() - 1 ; index >= 0 ; index-- )
        {
            // Find last Relationship commencement
            String commenceRecord = commenceReport.get(index) ;
            ArrayList<String> relationshipArray = extractArrayList(commenceRecord,RELATIONSHIP_ID) ;
            for (String relationshipString : relationshipArray)
            {
                for (String propertyName : new String[] {AGENTID0,AGENTID1})
                {
                    Object agentId = extractValue(propertyName,relationshipString) ;
                    if (agentLatestCommencement.putIfAbsent(agentId, index) == null)
                        agentLastRelationship.put(agentId, extractValue(RELATIONSHIP_ID,relationshipString)) ;
                }
            }
            LOGGER.info(String.valueOf(index));
            // Find last Relationship breakup
            try
            {
                breakupRecord = breakupReport.get(index);
            }
            catch ( Exception e )
            {
                LOGGER.log(Level.INFO,"{0}", breakupReport) ;
                breakupRecord = breakupReport.get(index);
            }
            relationshipArray = extractArrayList(breakupRecord,RELATIONSHIP_ID) ;
            for (String relationshipString : relationshipArray)
            {
                relationshipId = extractValue(RELATIONSHIP_ID,relationshipString);
                agentIds = relationshipAgentReport.get(relationshipId) ;
                if (agentIds == null)  // TODO: Make unnecessary by saving and reading burn-in
                    continue ;
                for (String agentId : agentIds)
                    if (!relationshipId.equals(agentLastRelationship.get(agentId)))
                        agentLatestBreakup.putIfAbsent(agentId, index) ;
            }
            
        }
        
        // Find gap between relationships
        for (Object agentId : agentLatestBreakup.keySet())
            agentGapReport.put(agentId, agentLatestCommencement.get(agentId) - agentLatestBreakup.get(agentId)) ;
        
        return agentGapReport ;
    }
    
    /**
     * @return A snapshot of how many agentIds had gaps of a given magnitude 
     * or greater between their final two relationships
     */
    public HashMap<Object,Number> prepareRelationshipCumulativeGapRecord()
    {
        HashMap<Object,Number> cumulativeRelationshipGapRecord = new HashMap<Object,Number>() ;

        HashMap<Object,Number> agentGapReport = prepareAgentGapReport() ;
        
        Collection<Number> agentGapValues = agentGapReport.values() ;
        
        int intValue ;
        int maxValue = 0 ;
        for (Number value : agentGapValues) 
        {
            intValue = value.intValue() ;
            if (intValue > maxValue)
                maxValue = intValue ;
        }
        
        int minValue = maxValue ; 
        for (Number value : agentGapValues)
        {
            intValue = value.intValue() ;
            if (intValue < minValue)
                minValue = intValue ;
        }
        
        // To track how agentIds have had more than given Relationships
        int agentsOver = 0 ;
        
        for (int key = maxValue ; key > 0 ; key-- )
        {
            agentsOver += Collections.frequency(agentGapValues,key) ;
            cumulativeRelationshipGapRecord.put(key, agentsOver) ;
        }
        
        agentsOver = 0 ;
        for (int key = minValue ; key < 0 ; key++ )
        {
            agentsOver += Collections.frequency(agentGapValues,key) ;
            cumulativeRelationshipGapRecord.put(key, agentsOver) ;
        }
        
        return cumulativeRelationshipGapRecord ;
    }
    
    
    /**
     * 
     * @return ArrayList of ArrayLists of (String) AgentIds in a relationship
     * that broke up in a given cycle.
     */
    public ArrayList<ArrayList<String>> prepareAgentBreakupReport()
    {
        ArrayList<ArrayList<String>> agentBreakupReport = new ArrayList<ArrayList<String>>() ;
        ArrayList<String> agentBreakupRecord ;
        ArrayList<String> breakupReport = prepareBreakupReport() ;
        HashMap<Object,String[]> relationshipAgentIds = prepareRelationshipAgentReport() ;
        String record ;
        String relationshipId ;
        String[] agentIds ;
            
        for (int recordNb = 0 ; recordNb < breakupReport.size() ; recordNb++ )
        {
            agentBreakupRecord = new ArrayList<String>() ;
            record = breakupReport.get(recordNb);
            ArrayList<String> relationshipRecords = extractArrayList(RELATIONSHIP_ID,record) ;
            for (String relationship : relationshipRecords)
            {
                relationshipId = extractValue(RELATIONSHIP_ID,record);
                agentIds = extractAgentIds(record,0);
                agentBreakupRecord.addAll(Arrays.asList(agentIds));
            }
            agentBreakupReport.add((ArrayList<String>) agentBreakupRecord.clone()) ;
        }
        return agentBreakupReport ;
    }
    
    /**
     * 
     * @return (HashMap) relationshipId -> [agentIds]
     */
    private HashMap<Object,String[]> prepareRelationshipAgentReport()
    {
        HashMap<Object,String[]> relationshipAgentReport = new HashMap<Object,String[]>() ;
        
        ArrayList<String> commenceReport = prepareCommenceReport() ;
        ArrayList<String> relationshipRecords ;
        String relationshipId ;
        String[] agentIds = new String[2] ;
        
        for (String record : commenceReport)
        {
            relationshipRecords = extractArrayList(record,RELATIONSHIP_ID) ;
            for (String relationshipRecord : relationshipRecords)
            {
                relationshipId = extractValue(RELATIONSHIP_ID,relationshipRecord) ;
                agentIds = extractAgentIds(relationshipRecord,0) ;
                relationshipAgentReport.put(relationshipId, agentIds) ;
            }
        }
        return relationshipAgentReport ;
    }
    
    /**
     * 
     * @return (HashMap) length-at-breakup maps to number of Relationships of 
     * corresponding length
     */
    public HashMap<Object,Number> prepareLengthAtBreakupReport()
    {
        HashMap<Object,Number> lengthAtBreakupMap = new HashMap<Object,Number>() ;
        
        // relationshipId -> length of Relationship
        HashMap<Object,Integer> relationshipLengthReport = prepareRelationshipLengthReport() ;
        
        for (Object relationshipId : relationshipLengthReport.keySet())
        {
            int length = relationshipLengthReport.get(relationshipId) ;
            lengthAtBreakupMap = incrementHashMap(length,lengthAtBreakupMap) ;
        }
        
        return lengthAtBreakupMap ;
    }
    
    /**
     * 
     * @return (HashMap) key is String.valueOf(relationshipId) and value is the 
     * number of cycles the corresponding Relationship went for.
     */
    public HashMap<Object,Integer> prepareRelationshipLengthReport()
    {
        HashMap<Object,Integer> relationshipLengthMap = new HashMap<Object,Integer>() ;
        
        ArrayList<ArrayList<Object>> relationshipCommenceReport = prepareRelationshipCommenceReport() ;
        ArrayList<ArrayList<Object>> relationshipBreakupReport = prepareRelationshipBreakupReport() ;
        
        // Which Relationships commenced in cycle index
        for (int index = 0 ; index < relationshipCommenceReport.size() ; index++ )
        {
            ArrayList<Object> commenceRecord = relationshipCommenceReport.get(index) ;
            
            for (Object relationshipId : commenceRecord)
                relationshipLengthMap.put(relationshipId, -index) ;
        }
        for (int index = 0 ; index < (relationshipBreakupReport.size() ) ; index++ )
        {
            // key relationshipId must have commenced already, with value -ve start cycle
            ArrayList<Object> breakupRecord = relationshipBreakupReport.get(index) ;
            
            for (Object relationshipId : breakupRecord)
            {
                int commenceIndex = relationshipLengthMap.get(relationshipId) ;
                relationshipLengthMap.put(relationshipId, index + commenceIndex + 1) ;    // +1 because breakup is done in same cycle
            }
        }
        
        // RelationshipLengthMap < 0 for Relationships that are still ongoing at the end of the simulation.
        for (Object relationshipId : relationshipLengthMap.keySet())
            if (!(relationshipLengthMap.get(relationshipId) > 0))
            {
                int newValue = relationshipLengthMap.get(relationshipId) + Integer.valueOf(getMetaDatum("Community.MAX_CYCLES")) + 1 ;
                relationshipLengthMap.put(relationshipId, newValue) ;
            }
        
        return relationshipLengthMap ;
    }
    
    /**
     * 
     * @return Report of mean of minimum number of partners to date, so long as not zero.
     */
    public ArrayList<Object> prepareMeanCumulativeRelationshipReport()
    {
        ArrayList<Object> meanCumulativeRelationshipReport = new ArrayList<Object>() ;
        
        ArrayList<HashMap<Object,Integer>> agentsCumulativeRelationshipReport = prepareAgentsCumulativeRelationshipReport() ;
        
        for (HashMap<Object,Integer> record : agentsCumulativeRelationshipReport)
        {
            int sum = 0 ;
            for (Object agentId : record.keySet())
            {
                sum += record.get(agentId) ;
            }
            meanCumulativeRelationshipReport.add(((double) sum)/record.keySet().size()) ;
        }
        return meanCumulativeRelationshipReport ;
    }
    
    /**
     * @return A snapshot of how many agentIds have more had how many or more Relationships
     */
    public HashMap<Object,Number> prepareCumulativeLengthReport()
    {
        HashMap<Object,Number> cumulativeRelationshipLengthReport = new HashMap<Object,Number>() ;
        
        //TODO: Separate out action on individual RECORD
        //ArrayList<HashMap<Object,Integer>> agentsCumulativeRelationshipReport 
          //      = prepareAgentsCumulativeRelationshipReport() ;
        
        HashMap<Object,Number> lengthAtBreakupReport = prepareLengthAtBreakupReport() ;
        
        // Find maximum relationship length
        int maxValue = 0 ;
        int lengthValue ;
        for (Object lengthObject : lengthAtBreakupReport.keySet())
        {
            lengthValue = Integer.valueOf(String.valueOf(lengthObject)) ;
            if (lengthValue > maxValue)
                maxValue = lengthValue ;
        }
        
        int relationshipsUnder = 0 ;
        for (int lengthKey = maxValue ; lengthKey > 0 ; lengthKey-- )
        {
            if (lengthAtBreakupReport.containsKey(lengthKey))
                relationshipsUnder += (Integer) lengthAtBreakupReport.get(lengthKey) ;
            cumulativeRelationshipLengthReport.put(lengthKey, relationshipsUnder) ;
            LOGGER.info(String.valueOf(relationshipsUnder));
        }
        LOGGER.log(Level.INFO, "{0}", cumulativeRelationshipLengthReport) ;
        return cumulativeRelationshipLengthReport ;
    }
    
    /**
     * @return A snapshot of how many agentIds have more had how many or more Relationships
     */
    public HashMap<Object,Number> prepareCumulativeRelationshipRecord()
    {
        HashMap<Object,Number> cumulativeRelationshipRecord = new HashMap<Object,Number>() ;
        
        //TODO: Separate out action on individual RECORD
        ArrayList<HashMap<Object,Integer>> agentsCumulativeRelationshipReport 
                = prepareAgentsCumulativeRelationshipReport() ;
        
        HashMap<Object,Integer> agentsCumulativeRelationshipRecord 
                = agentsCumulativeRelationshipReport.get(agentsCumulativeRelationshipReport.size()-1) ;
        
        Collection<Integer> agentsCumulativeRelationshipValues 
                = agentsCumulativeRelationshipRecord.values() ;
        
        int maxValue = Collections.max(agentsCumulativeRelationshipValues) ;
        
        // To track how agentIds have had more than given Relationships
        int agentsOver = 0 ;
        
        for (int key = maxValue ; key > 0 ; key-- )
        {
            agentsOver += Collections.frequency(agentsCumulativeRelationshipValues,key) ;
            cumulativeRelationshipRecord.put(key, agentsOver) ;
        }
        return cumulativeRelationshipRecord ;
    }
    
    /**
     * 
     * @return Report with HashMap showing how many Relationships each agentId has entered into so far.
     */
    private ArrayList<HashMap<Object,Integer>> prepareAgentsCumulativeRelationshipReport()
    {
        ArrayList<HashMap<Object,Integer>> agentsCumulativeRelationshipReport = new ArrayList<HashMap<Object,Integer>>() ;
        
        // Keeps track of cumulative number of Relationships per agentId
        HashMap<Object,Integer> agentCumulativeRelationships = new HashMap<Object,Integer>() ;
        
        ArrayList<HashMap<Object,ArrayList<Object>>> agentsEnteredRelationshipReport 
                = prepareAgentsEnteredRelationshipReport() ;
        
        for (HashMap<Object,ArrayList<Object>> record : agentsEnteredRelationshipReport )
        {
            for (Object agentId : record.keySet())
            {
                int sumSoFar = 0 ;
                if (agentCumulativeRelationships.containsKey(agentId))
                    sumSoFar = agentCumulativeRelationships.get(agentId) ;
                agentCumulativeRelationships.put(agentId, record.get(agentId).size() + sumSoFar) ;
            }
            agentsCumulativeRelationshipReport.add(agentCumulativeRelationships) ;
        }
        return agentsCumulativeRelationshipReport ;
    }
    
    /**
     * 
     * @return (ArrayList) record of mean number of relationships 
     */
    public ArrayList<Object> prepareMeanNumberRelationshipsReport()
    {
        ArrayList<Object> meanNumberRelationshipsReport = new ArrayList<Object>() ;
        
        //ArrayList<Object> populationReport = (new PopulationReporter("",input)).preparePopulationReport() ;
        
        ArrayList<HashMap<Object,Integer>> agentNumberRelationshipsReport
                = prepareAgentNumberRelationshipsReport() ;
        int population = Integer.valueOf(getMetaDatum("Community.POPULATION")) ; // = Integer.valueOf(extractValue("Population", (String) populationReport.get(recordIndex))) ;

        for (int recordIndex = 0 ; recordIndex < agentNumberRelationshipsReport.size() ; recordIndex++ )
        {
            HashMap<Object,Integer> record = agentNumberRelationshipsReport.get(recordIndex) ;
            int sum = 0 ;
            //LOGGER.info(String.valueOf(record.keySet()));
            for (Object recordKey : record.keySet())
            {
            //Object[] agentsNumberRelationshipValues 
              //  = (Object[]) Arrays.asList(record.values()).toArray() ;
        
            
            // To track how many agentIds 
            //int agentsOver = agentsNumberRelationshipValues.size() ;
            
            
            //for (Object number : agentsNumberRelationshipValues )
                sum += record.get(recordKey) ; // Integer.valueOf(String.valueOf(number)) ;
            }
            //LOGGER.log(Level.INFO,"{3} SUM:{0} population:{1} {2}",new Object[] {sum,population,((double) sum)/population,recordIndex});
            
            meanNumberRelationshipsReport.add("Mean number of partners:" + String.valueOf(((double) sum)/population)) ;  // (Object) "Mean number of partners:" + 
        }
        return meanNumberRelationshipsReport ;
    }
    
    /**
     * First count the number of Relationships each Agent has entered up to now,
     * then subtract those which have broken up.
     * TODO: Redo to account for burn-in relationships.
     * @return Each record gives the number of current Relationships for each Agent.
     */
    public ArrayList<HashMap<Object,Integer>> prepareAgentNumberRelationshipsReport() 
    {
        ArrayList<HashMap<Object,Integer>> agentNumberRelationshipsReport 
                = new ArrayList<HashMap<Object,Integer>>() ;
        
        ArrayList<HashMap<Object,Integer>> agentEnterRelationshipsReport 
                = new ArrayList<HashMap<Object,Integer>>() ;
        ArrayList<HashMap<Object,Integer>> agentBreakupRelationshipsReport 
                = new ArrayList<HashMap<Object,Integer>>() ;
        
        ArrayList<HashMap<Object,ArrayList<Object>>> agentsEnteredRelationshipReport 
                = prepareAgentsEnteredRelationshipReport() ;
//        ArrayList<HashMap<Object,ArrayList<Object>>> agentsBreakupRelationshipReport 
//                = prepareAgentsBreakupRelationshipReport() ;
    
        ArrayList<String> breakupReport = prepareBreakupReport() ;
    
        HashMap<Object,ArrayList<Object>> agentRelationships = new HashMap<Object,ArrayList<Object>>() ;
        
        //LOGGER.log(Level.INFO, "{0}", agentsEnteredRelationshipReport);
        int reportsSize = agentsEnteredRelationshipReport.size() ;
        // Count Relationship entered per agentId
        HashMap<Object,Integer> agentRelationshipsCount = new HashMap<Object,Integer>() ;
        
        
        for (int enteredIndex = 0 ; enteredIndex < reportsSize ; enteredIndex++ )
        {
            // Formation of new Relationships
            HashMap<Object,ArrayList<Object>> enteredRecord = agentsEnteredRelationshipReport.get(enteredIndex) ;
            for (Object agentId : enteredRecord.keySet())
            {
                //LOGGER.info(String.valueOf(agentId)) ;
                // New Relationships
                int newTotal = enteredRecord.get(agentId).size() ;
                
                if (agentRelationshipsCount.containsKey(agentId))
                {
                    // Plus ones already formed
                    newTotal += agentRelationshipsCount.get(agentId) ;
                }
                agentRelationshipsCount.put(agentId, newTotal) ;
                //LOGGER.log(Level.INFO, "{0}", agentRelationshipsCount);
                
                for (Object relationshipId : enteredRecord.get(agentId))
                    agentRelationships = updateHashMap(relationshipId,agentId,agentRelationships) ;
            }
            HashMap<Object,Integer> enterAgentRelationshipsCount = (HashMap<Object,Integer>) agentRelationshipsCount.clone() ;
            agentEnterRelationshipsReport.add(enterAgentRelationshipsCount) ;
        }
        
        HashMap<Object,Number> agentBreakupsCount = new HashMap<Object,Number>() ;
        
        for (int breakupIndex = 0 ; breakupIndex < (reportsSize-1) ; breakupIndex++ )
        {
            // Formation of new Relationships
            String breakupRecord = breakupReport.get(breakupIndex) ; // agentsBreakupRelationshipReport.get(breakupIndex) ;
            //for (Object agentId : breakupRecord.keySet())
            for (Object relationshipId : extractAllValues("relationshipId",breakupRecord,0)) 
            {
                // Relationship breakups
                for (Object agentId : agentRelationships.get(relationshipId))
                {
                //int subtractTotal = breakupRecord.get(agentId).size() ;
                    agentBreakupsCount = incrementHashMap(agentId,agentBreakupsCount) ;
                
//                if (agentBreakupsCount.containsKey(agentId))
//                {
//                    // Plus ones already formed
//                    subtractTotal += agentBreakupsCount.get(agentId) ;
//                }
//                agentBreakupsCount.put(agentId, subtractTotal) ;
                }
            
            }
            agentBreakupRelationshipsReport.add((HashMap<Object,Integer>) agentBreakupsCount.clone()) ;
        }
        // Collate
        for (int breakupIndex = 0 ; breakupIndex < (reportsSize-1) ; breakupIndex++ )
        {
            HashMap<Object,Integer> numberRelationshipsCount = new HashMap<Object,Integer>() ;
            HashMap<Object,Integer> enterRecord = agentEnterRelationshipsReport.get(breakupIndex+1) ;
            HashMap<Object,Integer> breakupRecord = agentBreakupRelationshipsReport.get(breakupIndex) ;
            for (Object agentId : breakupRecord.keySet())
            {
//                if (!enterRecord.containsKey(agentId))
//                    continue ;  // Relationship entered during burn-in
                try
                {
                    numberRelationshipsCount.put(agentId, enterRecord.get(agentId) - breakupRecord.get(agentId)) ;
                }
                catch (Exception e)
                {
                    LOGGER.info(String.valueOf(agentId));
                    numberRelationshipsCount.put(agentId, enterRecord.get(agentId) - breakupRecord.get(agentId)) ;
                }
            }
            agentNumberRelationshipsReport.add((HashMap<Object,Integer>) numberRelationshipsCount.clone()) ;
        }
        return agentNumberRelationshipsReport ;
    }
    
    /**
     * Indicates which Agents were infected at which Sites for which cycles.
     * TODO: Adapt to multiple Report files
     * @param siteNames
     * @return HashMap key agentId, value HashMap key siteName value ArrayList of cycles when infected
     */
    private HashMap<Object,HashMap<Object,ArrayList<Object>>> prepareAgentInfectionReport(String[] siteNames)
    {
        HashMap<Object,HashMap<Object,ArrayList<Object>>> agentInfectionReport = new HashMap<Object,HashMap<Object,ArrayList<Object>>>() ;

        HashMap<Object,ArrayList<Integer>> siteInfectionReport ;

        for (int recordIndex = 0 ; recordIndex < input.size() ; recordIndex++ )
        {
            String record = input.get(recordIndex) ; 
            ArrayList<String> agentIdArray = extractArrayList(record,AGENTID) ;
            for ( String agentString : agentIdArray )
            {
                String agentId = extractValue(AGENTID,agentString) ;
                //siteInfectionReport = agentInfectionReport.get(agentId) ;
                for ( String siteName : siteNames )
                    if (record.indexOf(siteName) > 0 )
                        agentInfectionReport = updateHashMap(agentId,siteName,recordIndex,agentInfectionReport) ;
            }
        }
        return agentInfectionReport ;
    }
    
    /**
     * 
     * @return Each record is a HashMap indicating new relationshipIds for relevant (key) Agents
     */
    public ArrayList<HashMap<Object,ArrayList<Object>>> prepareAgentsEnteredRelationshipReport()
    {
        ArrayList<HashMap<Object,ArrayList<Object>>> agentsEnteredRelationshipReport = new ArrayList<HashMap<Object,ArrayList<Object>>>() ;
        
        ArrayList<String> commenceReport = prepareCommenceReport() ;
        
        HashMap<Object,ArrayList<Object>> commenceRelationshipRecord ;
        
        for (String record : commenceReport)
        {
            commenceRelationshipRecord = new HashMap<Object,ArrayList<Object>>();
            
            ArrayList<String> relationshipIdArray = extractArrayList(record,RELATIONSHIP_ID) ;
            for (String relationshipString : relationshipIdArray)
            {
                String relationshipIdValue = extractValue(RELATIONSHIP_ID,relationshipString) ;
                
                String[] agentIdValues = extractAgentIds(relationshipString) ;
                
                //String agentId0Value = extractValue(AGENTID0,relationshipString) ;
                commenceRelationshipRecord = updateHashMap(agentIdValues[0],relationshipIdValue,commenceRelationshipRecord) ;
                
                //String agentId1Value = extractValue(AGENTID1,relationshipString) ;
                commenceRelationshipRecord = updateHashMap(agentIdValues[1],relationshipIdValue,commenceRelationshipRecord) ;
            }
            
            agentsEnteredRelationshipReport.add(commenceRelationshipRecord) ;
            
        }
        return agentsEnteredRelationshipReport ;
    }
    
    /**
     * 
     * @return Each record is a HashMap indicating breakup of relationshipIds for relevant (key) agentIds
     */
    private ArrayList<HashMap<Object,ArrayList<Object>>> prepareAgentsBreakupRelationshipReport()
    {
        ArrayList<HashMap<Object,ArrayList<Object>>> agentsBreakupRelationshipReport = new ArrayList<HashMap<Object,ArrayList<Object>>>() ;
        
        ArrayList<String> breakupReport = prepareBreakupReport() ;
        
        HashMap<Object,ArrayList<Object>> breakupRelationshipRecord ;
        
        for (String record : breakupReport)
        {
            breakupRelationshipRecord = new HashMap<Object,ArrayList<Object>>();
            
            ArrayList<String> relationshipIdArray = extractArrayList(record,RELATIONSHIP_ID) ;
            for (String relationshipString : relationshipIdArray)
            {
                String relationshipIdValue = extractValue(RELATIONSHIP_ID,relationshipString) ;
                
                String agentId0Value = extractValue(AGENTID0,relationshipString) ;
                if (!agentId0Value.isEmpty())
                    breakupRelationshipRecord = updateHashMap(agentId0Value,relationshipIdValue,breakupRelationshipRecord,false) ;
                
                String agentId1Value = extractValue(AGENTID1,relationshipString) ;
                if (!agentId1Value.isEmpty())
                    breakupRelationshipRecord = updateHashMap(agentId1Value,relationshipIdValue,breakupRelationshipRecord,false) ;
            }
            
            agentsBreakupRelationshipReport.add(breakupRelationshipRecord) ;
            
        }
        return agentsBreakupRelationshipReport ;
    }
    
    
    private ArrayList<String> prepareBreakupReport()
    {
        return prepareBreakupReport(true) ;
    }
    
    /**
     * 
     * @return Report with breakup-relevant information from input records.
     */
    private ArrayList<String> prepareBreakupReport(boolean readBurnin)
    {
        ArrayList<String> breakupReport = new ArrayList<String>() ;
        
        String record ;
        int breakupIndex ;
        String valueString ;

        //Include burn-in Relationships
        ArrayList<String> inputString = new ArrayList<String>() ;
        if (readBurnin || !Relationship.BURNIN_BREAKUP.isEmpty())
            breakupReport.add("clear:") ;
        
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
            for (int recordNb = 0 ; recordNb < input.size() ; recordNb += outputCycle )
            {
                record = input.get(recordNb) ;
                record = record.substring(record.indexOf("clear:")) ;
                //LOGGER.log(Level.INFO, "{0} {1}", new Object[] {recordNb,record});

                breakupReport.add(record) ;
            }
        return breakupReport ;
    }
    
    /**
     * 
     * @return Report with commence-relevant information from input records.
     * Includes Relationships commenced during burn-in that breakup during the
     * simulation.
     */
    private ArrayList<String> prepareCommenceReport()
    {
        ArrayList<String> commenceReport = new ArrayList<String>() ;
        
        String record ;
        int agentIndex ;
        String valueString ;
        
        //Include burn-in Relationships
        ArrayList<String> inputString = new ArrayList<String>() ;
        //LOGGER.info(Relationship.BURNIN_COMMENCE) ;
        record = prepareBurninRecord() ;
        if (!record.isEmpty())
            inputString.add(record + "clear:") ;
        
        // Read in Relationship commencements from simulation.
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
            for (String inputRecord : input)
                inputString.add(inputRecord) ;
        

        for (int reportNb = 0 ; reportNb < inputString.size() ; reportNb += outputCycle )
        {
            record = inputString.get(reportNb) ;
            int relationshipIdIndex = indexOfProperty("relationshipId",record) ;
            int clearIndex = indexOfProperty("clear",record) ;
            if (relationshipIdIndex >= 0 && (relationshipIdIndex < clearIndex)) 
                commenceReport.add(record.substring(relationshipIdIndex,clearIndex)) ;
            else
                commenceReport.add("") ;
        }
        return commenceReport ;
    }
    
    /**
     * 
     * @return (String) replacement of Relationship.BURNIN_COMMENCE including only 
     * Relationships which have not broken up.
     */
    private String prepareBurninRecord()
    {
        
        String burninCommence = "" ;
        String burninCommenceStatic = Relationship.BURNIN_COMMENCE ;
        String burninBreakupStatic = Relationship.BURNIN_BREAKUP ;
        ArrayList<String> burninCommenceList = new ArrayList<String>() ;
        ArrayList<Object> burninBreakup = new ArrayList<Object>()  ;
        String relationshipId ;
        
        // Get relationshipIds commenced during burn-in
        if (burninBreakupStatic.isEmpty())    // If nothing in Relationship.BURNIN_COMMENCE
        {
            burninCommenceStatic = getMetaDatum("Relationship.BURNIN_COMMENCE") ;
            burninBreakupStatic = getMetaDatum("Relationship.BURNIN_BREAKUP") ;
        }
        burninCommenceList = extractArrayList(burninCommenceStatic,RELATIONSHIP_ID) ;
        burninBreakup = extractAllValues(RELATIONSHIP_ID,burninBreakupStatic,0) ;
        
        for (String relationshipEntry : burninCommenceList)
        {
            relationshipId = extractValue(RELATIONSHIP_ID,relationshipEntry) ;
            if (!burninBreakup.contains(relationshipId))
                burninCommence += relationshipEntry ;
        }
        
        //burninCommence += "clear:" ;
        return burninCommence ;
    }
    
}
