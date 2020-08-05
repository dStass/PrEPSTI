/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PRSP.PrEPSTI.reporter;

import PRSP.PrEPSTI.community.Relationship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.logging.Level;

/**
 *
 * @author MichaelWalker
 */
public class RelationshipReporter extends Reporter {

    static String DEATH = "death";
    static String BIRTH = "birth";
    static String AGE = "age";
    static String RELATIONSHIP = "relationship";
    // static String RELATIONSHIP_ID = Relationship.RELATIONSHIP_ID ;
    static String TRANSMISSION = "transmission";
    static String TOTAL = "total";

    public RelationshipReporter() {

    }

    public RelationshipReporter(String simName, ArrayList<String> report) {
        super(simName, report);
    }

    /**
     * @param simName        (String) Root of simulation name.
     * @param reportFilePath (String) Path to saved files.
     */
    public RelationshipReporter(String simName, String reportFilePath) {
        super(simName, reportFilePath);
    }

    /**
     * 
     * @return ArrayList of ArrayLists of (String) RelationshipIds of relationships
     *         commenced in each cycle
     */
    public ArrayList<ArrayList<Comparable>> prepareRelationshipCommenceReport() {
        return prepareRelationshipCommenceReport("");
    }

    /**
     * 
     * @param relationshipClassName
     * @return ArrayList of ArrayLists of (String) RelationshipIds of relationships
     *         of type relationshipClassName commenced in each cycle. If
     *         relationshipClassName is an empty String then include all
     *         Relationships.
     */
    public ArrayList<ArrayList<Comparable>> prepareRelationshipCommenceReport(String relationshipClassName) {
        ArrayList<ArrayList<Comparable>> relationshipCommenceReport = new ArrayList<ArrayList<Comparable>>();

        ArrayList<String> commenceReport = (ArrayList<String>) getReport("commence", this); // prepareCommenceReport() ;
        // Restrict consideration to specified Relationship.class
        if (!relationshipClassName.isEmpty())
            commenceReport = FILTER_REPORT(RELATIONSHIP, relationshipClassName, RELATIONSHIPID, commenceReport);

        for (int recordNb = 0; recordNb < commenceReport.size(); recordNb++) {
            String record = commenceReport.get(recordNb);
            // LOGGER.info(relationshipId);
            // int startIndex = INDEX_OF_PROPERTY(RELATIONSHIPID,relationshipId) ;
            relationshipCommenceReport.add(EXTRACT_ALL_VALUES(RELATIONSHIPID, record, 0));
        }
        return relationshipCommenceReport;
    }

    /**
     * 
     * @param relationshipClazzes
     * @return ArrayList of ArrayLists of (String) RelationshipIds of relationships
     *         of class relationshipClazz commenced in each cycle
     */
    public ArrayList<ArrayList<Comparable>> prepareRelationshipCommenceReport(String[] relationshipClazzes) {
        ArrayList<ArrayList<Comparable>> relationshipCommenceReport = new ArrayList<ArrayList<Comparable>>();

        ArrayList<String> commenceReport = (ArrayList<String>) getReport("commence", this); // prepareCommenceReport() ;
        String filteredRecord;

        for (int reportNb = 0; reportNb < commenceReport.size(); reportNb++) {
            String record = commenceReport.get(reportNb);

            // Include only selected Relationships
            filteredRecord = "";
            for (String relationshipClazz : relationshipClazzes)
                filteredRecord += BOUNDED_STRING_BY_VALUE("relationship", relationshipClazz, RELATIONSHIPID, record);
            if (filteredRecord.isEmpty()) {
                relationshipCommenceReport.add(new ArrayList<Comparable>());
                continue;
            }
            // filteredRecord = relationshipId ;
            // LOGGER.info(relationshipId);
            // int startIndex = INDEX_OF_PROPERTY(RELATIONSHIPID,relationshipId) ;
            relationshipCommenceReport.add(EXTRACT_ALL_VALUES(RELATIONSHIPID, filteredRecord, 0));
            // relationshipCommenceReport.add(EXTRACT_ALL_VALUES(AGENTID1,
            // relationshipId,0)) ;
        }
        return relationshipCommenceReport;
    }

    /**
     * Prepares a HashMap representing relationships up to a specific cycle This is
     * done by getting a commencement report and breakup report Add new
     * relationships from the commencement report and remove those that exists in
     * breakup report
     * 
     * @param endCycle
     * @return HashMap with key = relationship ID, value = relationship record
     */
    public HashMap<Integer, String> prepareRelationshipRecordHashMap(int endCycle) {
        HashMap<Integer, String> relationshipReport = new HashMap<Integer, String>();

        // add commenced relationships to our relationshipReport
        ArrayList<String> commenceReport = prepareCommenceReport();
        ArrayList<String> breakupReport = prepareBreakupReport();

        // ArrayList<String> preparedCommenceReport = prepareCommenceReport();
        HashSet<String> endedRelationshipKeys = new HashSet<String>();

        for (int numReport = 0; numReport < endCycle; numReport++) {
            // extract all new relationship records
            String commenceRecord = commenceReport.get(numReport);
            HashMap<String, String> extractedCommenceReport = SPLIT_RECORD_BY_PROPERTY(RELATIONSHIPID, commenceRecord);
            // Set<String> extractedCommenceKeySet = extractedCommenceReport.keySet();
            // ArrayList<String> sortedKeys = new ArrayList<String>();
            // for (String s : extractedCommenceKeySet) sortedKeys.add(s);
            // Collections.sort(sortedKeys);
            
            // if (commenceRecord.contains("563793")) {
            //     LOGGER.info("REPORT: " + "563793" + " COMMENCED AT CYCLE:" + String.valueOf(numReport));
            // }
            for (String commenceId : extractedCommenceReport.keySet()) {
                relationshipReport.put(Integer.valueOf(commenceId), extractedCommenceReport.get(commenceId));
            }

            // remove breakups
            String breakupRecord = breakupReport.get(numReport);
            HashMap<String, String> extractedBreakupReport = SPLIT_RECORD_BY_PROPERTY(RELATIONSHIPID, breakupRecord);

            // add key to a set of relationships to remove
            for (String breakupId : extractedBreakupReport.keySet()) {
                endedRelationshipKeys.add(breakupId);
            }
        }

        Set<Integer> relationshipReportKeySet = new HashSet<Integer>(); 
        for (Integer key : relationshipReport.keySet()) {
            relationshipReportKeySet.add(key);
        }
        // for (String breakupId : endedRelationshipKeys) {
        //     Integer breakupIdInteger = Integer.valueOf(breakupId);
        //     if (relationshipReportKeySet.contains(breakupIdInteger)) {
        //         relationshipReport.remove(breakupIdInteger);
        //     }
        // }

        // remove broken up and casual relationships

        for (Integer relationshipId : relationshipReportKeySet) {
            String relationshipIdString = String.valueOf(relationshipId);
            String relationshipRecord = relationshipReport.get(relationshipId);

            if (endedRelationshipKeys.contains(relationshipIdString)) {
                relationshipReport.remove(relationshipId);
            } else if (EXTRACT_VALUE("relationship", relationshipRecord).equals("Casual")) {
                relationshipReport.remove(relationshipId);
            }
        }
        return relationshipReport;
    }


    /**
     * 
     * @return (ArrayList) of every Relationship to have ever broken up until backYears, backMonths, backDays
     * before cycle endCycle
     */
    public ArrayList<String> prepareRelationshipBreakupRecord()
    {
        ArrayList<String> relationshipBreakupRecord = new ArrayList<String>() ;
        
        String record ;
        
        // Loop through records in saved files
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
            for (int recordNb = 0 ; recordNb < input.size() ; recordNb += outputCycle )
            {
                record = input.get(recordNb) ;
                record = record.substring(record.indexOf("clear:")) ;
                //LOGGER.log(Level.INFO, "{0} {1}", new Object[] {recordNb,record});

                relationshipBreakupRecord.addAll(EXTRACT_ARRAYLIST(record,RELATIONSHIPID)) ;
            }
        return relationshipBreakupRecord ;
    }
    
    /**
     * 
     * @return ArrayList of ArrayLists of (String) RelationshipIds of relationships
     * that break up in each cycle
     */
    public ArrayList<ArrayList<Comparable>> prepareRelationshipBreakupReport()
    {
        ArrayList<ArrayList<Comparable>> relationshipBreakupReport = new ArrayList<ArrayList<Comparable>>() ;
        
        ArrayList<String> breakupReport = (ArrayList<String>) getReport("breakup",this) ; // prepareBreakupReport() ;
        
        for (int reportNb = 0 ; reportNb < breakupReport.size() ; reportNb++ )
        {
            String report = breakupReport.get(reportNb) ;
            relationshipBreakupReport.add(EXTRACT_ALL_VALUES(RELATIONSHIPID, report,0)) ;
            //relationshipBreakupReport.add(EXTRACT_ALL_VALUES(AGENTID1, relationshipId,0)) ;
        }
        return relationshipBreakupReport ;
    }
    
    /**
     * 
     * @return ArrayList of ArrayLists of (String) agentIds of Agents commencing 
     * relationships in each cycle.
     */
    public ArrayList<ArrayList<Comparable>> prepareAgentCommenceReport()
    {
        return prepareAgentCommenceReport(EMPTY,FALSE) ;
    }
    
    /**
     * 
     * @param sortingProperty
     * @param sortingValue
     * @return ArrayList of ArrayLists of (String) agentIds of Agents commencing 
     * relationships in each cycle.
     */
    public ArrayList<ArrayList<Comparable>> prepareAgentCommenceReport(String sortingProperty, Object sortingValue)
    {
        ArrayList<ArrayList<Comparable>> agentCommenceReport = new ArrayList<ArrayList<Comparable>>() ;
        
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        ArrayList<String> agentIds = populationReporter.agentIdSorted(sortingProperty).get(sortingValue) ;
        ArrayList<String> commenceReport = (ArrayList<String>) getReport("commence",this) ; //  prepareCommenceReport() ;
        
        for (int recordNb = 0 ; recordNb < commenceReport.size() ; recordNb++ )
        {
            String record = commenceReport.get(recordNb) ;
            //LOGGER.info(relationshipId);
            //int startIndex = INDEX_OF_PROPERTY(RELATIONSHIPID,relationshipId) ;
            ArrayList<Comparable> agentCommenceRecord = EXTRACT_ALL_VALUES(AGENTID0,record,0) ;
            agentCommenceRecord.addAll(EXTRACT_ALL_VALUES(AGENTID1,record,0)) ;
            // consider only Agents with correct sortingValue
            if (agentIds.isEmpty())
            agentCommenceRecord.retainAll(agentIds) ;
            agentCommenceReport.add((ArrayList<Comparable>) agentCommenceRecord.clone()) ;
        }
        return agentCommenceReport ;
    }
    
    /**
     * 
     * @param encounterReporter
     * @return (HashMap) relationshipId maps to number of associated transmissions 
     */
    public HashMap<Comparable,Number> prepareRelationshipTransmissionReport(EncounterReporter encounterReporter)
    {
        HashMap<Comparable,Number> relationshipTransmissionReport = new HashMap<Comparable,Number>() ;
        
        ArrayList<String> encounterReport = encounterReporter.getFullInput() ;
        String encounterRecord ;
        //String transmission = "transmission" ;
        ArrayList<String> encounterList ; 
        ArrayList<Object> transmissionList ; 
        String[] encounterAgentIds ;  // For Agents in encounter
        
        HashMap<Object,String[]> relationshipAgentReport 
                = (HashMap<Object,String[]>) getReport("relationshipAgent",this) ; // prepareRelationshipAgentReport() ;
        
        // When did each Relationship commence?
        ArrayList<ArrayList<Comparable>> relationshipCommenceReport 
                = prepareRelationshipCommenceReport() ;
        // When did each Relationship break-up? Used for efficiency.
        ArrayList<ArrayList<Comparable>> relationshipBreakupReport 
                = prepareRelationshipBreakupReport() ;
        
        // relationshipId -> commencement cycle
        // Use of currentRelationshipIds currently commented out.
        ArrayList<Object> currentRelationshipIds = new ArrayList<Object>() ;
        //for (boolean nextInput = true ; nextInput ; nextInput = updateReport())
        int nbCycles = encounterReport.size() ;
        // LOGGER.info("nbCycles:" + String.valueOf(nbCycles));
        for (int cycle = 0 ; cycle < nbCycles ; cycle++ )
        {
            for (Object relationshipId : relationshipCommenceReport.get(cycle))
                currentRelationshipIds.add(relationshipId) ;
            encounterRecord = encounterReport.get(cycle) ;
            encounterList = EXTRACT_ARRAYLIST(encounterRecord,"relationshipId",TRANSMISSION) ;
            //LOGGER.log(Level.INFO, "encounterList:{0}", encounterList);
            for (String encounter : encounterList)
            {
                //transmissionList = EXTRACT_ARRAY_LIST(TRANSMISSION,encounter,0) ;
                //transmissionList = EXTRACT_ALL_VALUES(TRANSMISSION,encounter,0) ;
                //LOGGER.log(Level.INFO, "transmissionList:{0}", transmissionList) ;
                //if (transmissionList.contains(TRUE))
                if (TRUE.equals(EXTRACT_VALUE(TRANSMISSION,encounter))) ;
                {
                    String relationshipId = EXTRACT_VALUE("relationshipId",encounter) ;
                    encounterAgentIds = relationshipAgentReport.get(relationshipId) ; // EXTRACT_AGENTIDS(encounter) ;
                  // logger.log(level.info, "encounterAgentIds", encounterAgentIds);
                    relationshipTransmissionReport = INCREMENT_HASHMAP(relationshipId,relationshipTransmissionReport) ;
                    /**
                     * for (Object relationshipId : currentRelationshipIds)
                    {
                        String[] relationshipAgentIds = relationshipAgentReport.get(relationshipId) ;
                        if (relationshipAgentIds[0].equals(encounterAgentIds[0]) && relationshipAgentIds[1].equals(encounterAgentIds[1]))
                        {
                            relationshipTransmissionReport = INCREMENT_HASHMAP(relationshipId,relationshipTransmissionReport) ;
                            continue ;
                        }
                        if (relationshipAgentIds[1].equals(encounterAgentIds[0]) && relationshipAgentIds[0].equals(encounterAgentIds[1]))
                        {
                            relationshipTransmissionReport = INCREMENT_HASHMAP(relationshipId,relationshipTransmissionReport) ;
                            continue ;
                        }
                    }
                }
            }
            for (Object relationshipId : relationshipBreakupReport.get(cycle))
                currentRelationshipIds.remove(relationshipId) ;
            */
                }
            }
        }
        return relationshipTransmissionReport ;
    }
    
    /**
     * 
     * @param encounterReporter
     * @return (HashMap) Number of Relationships responsible for a given number 
     * or more transmissions.
     */
    public HashMap<Comparable,Number> prepareRelationshipCumulativeTransmissionReport(EncounterReporter encounterReporter)
    {
        HashMap<Comparable,Number> cumulativeRelationshipTransmissionReport = new HashMap<Comparable,Number>() ;

        HashMap<Comparable,Number> relationshipTransmissionReport = prepareRelationshipTransmissionReport(encounterReporter) ;
      // logger.log(level.info, "{0}", relationshipTransmissionReport);
        
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
    public HashMap<Comparable,Number> prepareAgentGapReport()
    {
        HashMap<Comparable,Number> agentGapReport = new HashMap<Comparable,Number>() ;
        
        // Latest cycle for agentId commencing relationship
        HashMap<Object,Integer> agentLatestCommencement = new HashMap<Object,Integer>() ;
        // Latest cycle for agentId ending relationship different from latest commencement
        HashMap<Comparable,Integer> agentLatestBreakup = new HashMap<Comparable,Integer>() ;
        // relaitonshipId of last Relationship to commence for each agentId
        HashMap<Object,String> agentLastRelationship = new HashMap<Object,String>() ;
        
        ArrayList<String> commenceReport = (ArrayList<String>) getReport("commence",this) ; //  prepareCommenceReport() ;
        ArrayList<String> breakupReport = (ArrayList<String>) getReport("breakup",this) ; //  prepareBreakupReport() ;
        HashMap<Object,String[]> relationshipAgentReport = (HashMap<Object,String[]>) getReport("relationshipAgent",this) ; // prepareRelationshipAgentReport() ;
            
        
        String breakupRecord ;
        String relationshipId ;
        String[] agentIds ; 
                
        for (int index = commenceReport.size() - 1 ; index >= 0 ; index-- )
        {
            // Find last Relationship commencement
            String commenceRecord = commenceReport.get(index) ;
            ArrayList<String> relationshipArray = EXTRACT_ARRAYLIST(commenceRecord,RELATIONSHIPID) ;
            for (String relationshipString : relationshipArray)
            {
                for (String propertyName : new String[] {AGENTID0,AGENTID1})
                {
                    Object agentId = EXTRACT_VALUE(propertyName,relationshipString) ;
                    if (agentLatestCommencement.putIfAbsent(agentId, index) == null)
                        agentLastRelationship.put(agentId, EXTRACT_VALUE(RELATIONSHIPID,relationshipString)) ;
                }
            }
            // LOGGER.info(String.valueOf(index)+":");
            // Find last Relationship breakup
            try
            {
                breakupRecord = breakupReport.get(index);
                relationshipArray = EXTRACT_ARRAYLIST(breakupRecord,RELATIONSHIPID) ;
            }
            catch ( Exception e )
            {
              // logger.log(level.info,"{0}", breakupReport) ;
                assert(false) ;
            }
            for (String relationshipString : relationshipArray)
            {
                relationshipId = EXTRACT_VALUE(RELATIONSHIPID,relationshipString);
                agentIds = relationshipAgentReport.get(relationshipId) ;
                if (agentIds == null)  // TODO: Make unnecessary by saving and reading burn-in
                    continue ;
                for (String agentId : agentIds)
                    if (!relationshipId.equals(agentLastRelationship.get(agentId)))
                        agentLatestBreakup.putIfAbsent(agentId, index) ;
            }
            
        }
        
        // Find gap between relationships
        for (Comparable agentId : agentLatestBreakup.keySet())
            agentGapReport.put(agentId, agentLatestCommencement.get(agentId) - agentLatestBreakup.get(agentId)) ;
        
        return agentGapReport ;
    }
    
    /**
     * @return A snapshot of how many agentIds had gaps of a given magnitude 
     * or greater between their final two relationships
     */
    public HashMap<Comparable,Number> prepareRelationshipCumulativeGapRecord()
    {
        HashMap<Comparable,Number> cumulativeRelationshipGapRecord = new HashMap<Comparable,Number>() ;

        HashMap<Comparable,Number> agentGapReport = prepareAgentGapReport() ;
        
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
        ArrayList<String> breakupReport = (ArrayList<String>) getReport("breakup",this) ; //   prepareBreakupReport() ;
        HashMap<Object,String[]> relationshipAgentIds = (HashMap<Object,String[]>) getReport("relationshipAgent",this) ; // prepareRelationshipAgentReport() ;
        String record ;
        String relationshipId ;
        String[] agentIds ;
            
        for (int recordNb = 0 ; recordNb < breakupReport.size() ; recordNb++ )
        {
            agentBreakupRecord = new ArrayList<String>() ;
            record = breakupReport.get(recordNb);
            ArrayList<String> relationshipRecords = EXTRACT_ARRAYLIST(RELATIONSHIPID,record) ;
            for (String relationship : relationshipRecords)
            {
                relationshipId = EXTRACT_VALUE(RELATIONSHIPID,relationship);
                agentIds = relationshipAgentIds.get(relationshipId);
                agentBreakupRecord.addAll(Arrays.asList(agentIds));
            }
            agentBreakupReport.add((ArrayList<String>) agentBreakupRecord.clone()) ;
        }
        return agentBreakupReport ;
    }
    
    /**
     * 
     * @return (HashMap) relationshipId maps to [agentIds]
     */
    public HashMap<Object,String[]> prepareRelationshipAgentReport()
    {
        return prepareRelationshipAgentReport(false) ;
    }
    
    /**
     * 
     * @param ignoreBreakups (boolean) ignore Relationships which have broken up.
     * @return (HashMap) relationshipId maps to [agentIds]
     */
    public HashMap<Object,String[]> prepareRelationshipAgentReport(boolean ignoreBreakups)
    {
        // LOGGER.info("prepareRelationshipAgentReport()");
        HashMap<Object,String[]> relationshipAgentReport = new HashMap<Object,String[]>() ;
        
        Object getReportObject = getReport("commence", this);

        LOGGER.info("test => " + getReportObject.getClass().toString());
        ArrayList<String> commenceReport = (ArrayList<String>) getReport("commence",this) ; //  
        
        ArrayList<String> relationshipRecords ;
        String relationshipId ;
        String[] agentIds ; // = new String[2] ;
        
        String record = "" ;
        
        ArrayList<String> blacklist = new ArrayList<String>() ;
        if (ignoreBreakups)
            blacklist = prepareRelationshipBreakupRecord() ;
        
        //record = prepareBurninRecord() ;    // "0," + 
        //for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
        //LOGGER.log(Level.INFO, "{0}", commenceReport);
        for (String inputRecord : commenceReport)
        {
            //LOGGER.info(inputRecord);
            //inputString.add(inputRecord) ;
            //record = inputString.get(reportNb) ;
            int relationshipIdIndex = INDEX_OF_PROPERTY(RELATIONSHIPID,inputRecord) ;
            int clearIndex = INDEX_OF_PROPERTY("clear",inputRecord) ;
            if (clearIndex < 0)
                clearIndex = inputRecord.length() ;
            if (relationshipIdIndex >= 0 && (relationshipIdIndex < clearIndex)) 
                record += inputRecord.substring(relationshipIdIndex,clearIndex) ;
            else if (record.isEmpty())    // true unless contains burninRecord
                continue ;
                //LOGGER.info(record);

            relationshipRecords = EXTRACT_ARRAYLIST(record,RELATIONSHIPID) ;
            //LOGGER.log(Level.INFO, "{0}", relationshipRecords);
            for (String relationshipRecord : relationshipRecords)
            {
                relationshipId = EXTRACT_VALUE(RELATIONSHIPID,relationshipRecord) ;
                if (ignoreBreakups)
                    if (blacklist.contains(relationshipId))
                        continue ;
                agentIds = EXTRACT_AGENTIDS(relationshipRecord,0) ;
                relationshipAgentReport.put(relationshipId, agentIds) ;
            }

            record = "" ;
        }
        
        /*for (String record : commenceReport)
        {
            relationshipRecords = EXTRACT_ARRAYLIST(record,RELATIONSHIPID) ;
            for (String relationshipRecord : relationshipRecords)
            {
                relationshipId = EXTRACT_VALUE(RELATIONSHIPID,relationshipRecord) ;
                agentIds = EXTRACT_AGENTIDS(relationshipRecord,0) ;
                relationshipAgentReport.put(relationshipId, agentIds) ;
            }
        }*/
        return relationshipAgentReport ;
    }
    
    /**
     * 
     * @return (HashMap) length-at-breakup maps to number of Relationships of 
     * corresponding length
     */
    public HashMap<Comparable,Number> prepareLengthAtBreakupReport()
    {
        HashMap<Comparable,Number> lengthAtBreakupMap = new HashMap<Comparable,Number>() ;
        
        // relationshipId -> length of Relationship
        HashMap<Comparable,Integer> relationshipLengthReport = prepareRelationshipLengthReport() ;
        
        for (Object relationshipId : relationshipLengthReport.keySet())
        {
            int length = relationshipLengthReport.get(relationshipId) ;
            lengthAtBreakupMap = INCREMENT_HASHMAP(length,lengthAtBreakupMap) ;
        }
        
        return lengthAtBreakupMap ;
    }
    
    /**
     * 
     * @return (HashMap) key is String.valueOf(relationshipId) and value is the 
     * number of cycles the corresponding Relationship went for.
     */
    public HashMap<Comparable,Integer> prepareRelationshipLengthReport()
    {
        HashMap<Comparable,Integer> relationshipLengthMap = new HashMap<Comparable,Integer>() ;
        
        ArrayList<ArrayList<Comparable>> relationshipCommenceReport = prepareRelationshipCommenceReport() ;
        ArrayList<ArrayList<Comparable>> relationshipBreakupReport = prepareRelationshipBreakupReport() ;
        
        // Which Relationships commenced in cycle index
        for (int index = 0 ; index < relationshipCommenceReport.size() ; index++ )
        {
            ArrayList<Comparable> commenceRecord = relationshipCommenceReport.get(index) ;
            
            for (Comparable relationshipId : commenceRecord)
                relationshipLengthMap.put(relationshipId, -index) ;
        }
        for (int index = 0 ; index < (relationshipBreakupReport.size() ) ; index++ )
        {
            // key relationshipId must have commenced already, with value -ve start cycle
            ArrayList<Comparable> breakupRecord = relationshipBreakupReport.get(index) ;
            
            for (Comparable relationshipId : breakupRecord)
            {
                int commenceIndex = relationshipLengthMap.get(relationshipId) ;
                relationshipLengthMap.put(relationshipId, index + commenceIndex + 1) ;    // +1 because breakup is done in same cycle
            }
        }
        
        // RelationshipLengthMap < 0 for Relationships that are still ongoing at the end of the simulation.
        int nbCycles = Integer.valueOf(getMetaDatum("Community.MAX_CYCLES")) ;
        
        // Use of removeRelationships commented out, see below.
        ArrayList<Object> removeRelationships = new ArrayList<Object>() ;
        for (Comparable relationshipId : relationshipLengthMap.keySet())
            if (!(relationshipLengthMap.get(relationshipId) > 0))
            {
                int newValue = relationshipLengthMap.get(relationshipId) + nbCycles + 1 ;
                if (newValue > nbCycles)
                    removeRelationships.add(relationshipId) ;
                relationshipLengthMap.put(relationshipId, newValue) ;
            }
        
        // Comment this out to include relationships that last the whole simulation
        //for (Object relationshipId : removeRelationships)
          //  relationshipLengthMap.remove(relationshipId) ;
        
        return relationshipLengthMap ;
    }
    
    /**
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return Report of mean of minimum number of each subclass of Relationship 
     * to date.
     */
    public ArrayList<HashMap<Object,Object>> prepareMeanCumulativeRelationshipReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle)
    {
        ArrayList<HashMap<Object,Object>> meanCumulativeRelationshipReport 
                = new ArrayList<HashMap<Object,Object>>() ;
        
        HashMap<Object,Object> meanCumulativeRelationshipClassReport ;
        
        //int population = getPopulation() ; // Integer.valueOf(getMetaDatum("Community.POPULATION")) ;
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        int finalMaxAgentId = populationReporter.getMaxAgentId(endCycle) ;
        int earlyMaxAgentId = populationReporter.getMaxAgentId(endCycle - getBackCycles(backYears, backMonths, backDays)) ;
        double effectivePopulation = getPopulation() + finalMaxAgentId - earlyMaxAgentId ;
        
        ArrayList<HashMap<Object,HashMap<Object,Integer>>> agentsCumulativeRelationshipReport 
                = prepareAgentsCumulativeRelationshipReport(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        
        for (HashMap<Object,HashMap<Object,Integer>> record : agentsCumulativeRelationshipReport)
        {
            meanCumulativeRelationshipClassReport = new HashMap<Object,Object>() ;
            for (Object relationshipClassName : record.keySet())
            {
                int sum = 0 ;
                for (Object agentId : record.get(relationshipClassName).keySet())
                    sum += record.get(relationshipClassName).get(agentId) ;
                meanCumulativeRelationshipClassReport.put(relationshipClassName,((double) sum)/effectivePopulation) ; // relationshipId.keySet().size()) ;
            }
            meanCumulativeRelationshipReport.add((HashMap<Object,Object>) meanCumulativeRelationshipClassReport.clone()) ;
        }
        return meanCumulativeRelationshipReport ;
    }
    
    /**
     * TODO: Include length one and then plot on a log-scale.
     * @return A snapshot of how many agentIds have more had how many or more Relationships
     */
    public HashMap<Comparable,Number> prepareCumulativeLengthReport()
    {
        HashMap<Comparable,Number> cumulativeRelationshipLengthReport = new HashMap<Comparable,Number>() ;
        
        //TODO: Separate out action on individual RECORD
        //ArrayList<HashMap<Object,Integer>> agentsCumulativeRelationshipReport 
          //      = prepareAgentsCumulativeRelationshipReport() ;
        
        HashMap<Comparable,Number> lengthAtBreakupReport = prepareLengthAtBreakupReport() ;
        
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
        for (int lengthKey = maxValue ; lengthKey > 1 ; lengthKey-- )
        {
            if (lengthAtBreakupReport.containsKey(lengthKey))
                relationshipsUnder += (Integer) lengthAtBreakupReport.get(lengthKey) ;
            cumulativeRelationshipLengthReport.put(lengthKey, relationshipsUnder) ;
        }
        return cumulativeRelationshipLengthReport ;
    }
    
    /**
     * @param nbRelationships
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return A snapshot of what proportion of agentIds have more had how many 
     * or more Relationships, where each Relationship is assumed to be with a different 
     * partner.
     */
    public HashMap<Comparable,HashMap<Comparable,Number>> prepareCumulativeRelationshipRecord(int nbRelationships, String[] relationshipClassNames, int backYears, int backMonths, int backDays)
    {
        HashMap<Comparable,HashMap<Comparable,Number>> cumulativeRelationshipRecord 
                = new HashMap<Comparable,HashMap<Comparable,Number>>() ;
        for (String relationshipClassName : relationshipClassNames)
            cumulativeRelationshipRecord.put(relationshipClassName, new HashMap<Comparable,Number>()) ;
        
        //double population = getPopulation() ;
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        int finalMaxAgentId = populationReporter.getMaxAgentId() ;
        int earlyMaxAgentId = populationReporter.getMaxAgentId(getMaxCycles() - getBackCycles(backYears, backMonths, backDays)) ;
        double effectivePopulation = getPopulation() + finalMaxAgentId - earlyMaxAgentId ;
        
        //TODO: Separate out action on individual RECORD
        ArrayList<HashMap<Object,HashMap<Object,Integer>>> agentsCumulativeRelationshipReport 
                = prepareAgentsCumulativeRelationshipReport(relationshipClassNames, backYears, backMonths, backDays, getMaxCycles()) ;
        
        HashMap<Object,HashMap<Object,Integer>> agentsCumulativeRelationshipRecord 
                = agentsCumulativeRelationshipReport.get(agentsCumulativeRelationshipReport.size()-1) ;
        
        // Find cumulative number of all relationships. 
        HashMap<Object,Integer> totalRelationships = new HashMap<Object,Integer>() ;
        for (Object relationshipId : agentsCumulativeRelationshipRecord.keySet())
        {
            HashMap<Object,Integer> agentCumulativeRecord = agentsCumulativeRelationshipRecord.get(relationshipId) ;
            for (Object agentId : agentCumulativeRecord.keySet())
                totalRelationships = UPDATE_HASHMAP(agentId,agentCumulativeRecord.get(agentId),totalRelationships) ;
        }
        agentsCumulativeRelationshipRecord.put("total",totalRelationships) ;
        cumulativeRelationshipRecord.put("total", new HashMap<Comparable,Number>()) ;
            
        int minValue ;
        if (nbRelationships < 0)
            minValue = 1 ;
        else
            minValue = nbRelationships ;

        for (Object relationshipClassName : agentsCumulativeRelationshipRecord.keySet())
        {
            Collection<Integer> agentsCumulativeRelationshipValues 
                    = agentsCumulativeRelationshipRecord.get(relationshipClassName).values() ;

            Integer maxValue = Collections.max(agentsCumulativeRelationshipValues);
            if (maxValue == null)
                maxValue = 0 ;
            // To track how agentIds have had more than given Relationships
            int agentsOver = 0 ;

            for (int key = maxValue ; key >= minValue ; key-- )
            {
                agentsOver += Collections.frequency(agentsCumulativeRelationshipValues,key) ;
                cumulativeRelationshipRecord.get(relationshipClassName).put(key, agentsOver/effectivePopulation) ;
            }
        }
        //LOGGER.log(Level.INFO, "{0}", cumulativeRelationshipRecord);
        return cumulativeRelationshipRecord ;
    }
    
    /**
     * 
     * @return Report with relationshipClassName maps to 
     * (HashMap mapping agentId to the number of Relationships entered into so far).
     */
    private ArrayList<HashMap<Object,HashMap<Object,Integer>>> 
        prepareAgentsCumulativeRelationshipReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle)
    {
        ArrayList<HashMap<Object,HashMap<Object,Integer>>> agentsCumulativeRelationshipReport 
                = new ArrayList<HashMap<Object,HashMap<Object,Integer>>>() ;
        
        // Keeps track of cumulative number of Relationships per agentId
        HashMap<Object,HashMap<Object,Integer>> agentCumulativeRelationships 
                = new HashMap<Object,HashMap<Object,Integer>>() ;
        for (String relationshipClassName : relationshipClassNames)
            agentCumulativeRelationships.put(relationshipClassName, new HashMap<Object,Integer>()) ;
        
        //LOGGER.info("agentsEnteredRelationshipReport");
        ArrayList<HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>> agentsEnteredRelationshipReport 
                = prepareAgentsEnteredRelationshipReport(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        for (HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> record : agentsEnteredRelationshipReport )
        {
            for (Object relationshipClassName : relationshipClassNames)
            {
                for (Object agentId : record.get(relationshipClassName).keySet())
                {
                    int sumSoFar = 0 ;
                    if (agentCumulativeRelationships.get(relationshipClassName).containsKey(agentId))
                        sumSoFar = agentCumulativeRelationships.get(relationshipClassName).get(agentId) ;
                    agentCumulativeRelationships.get(relationshipClassName).put(agentId, record.get(relationshipClassName).get(agentId).size() + sumSoFar) ;
                }
            }
            agentsCumulativeRelationshipReport.add((HashMap<Object,HashMap<Object,Integer>>) agentCumulativeRelationships.clone()) ;
        }
        return agentsCumulativeRelationshipReport ;
    }
        
    /**
     * 
     * @param relationshipClassNames
     * @return (ArrayList) of Strings giving number of each RelationshipClazz in each
     * cycle.
     */
    private ArrayList<Object> prepareRelationshipNumberReport(String[] relationshipClassNames)
    {
        ArrayList<Object> relationshipNumberReport = new ArrayList<Object>() ;
        
        ArrayList<String> commenceReport = (ArrayList<String>) getReport("commence",this) ; //  prepareCommenceReport() ;
        ArrayList<String> breakupReport = (ArrayList<String>) getReport("breakup",this) ; //  prepareCommenceReport() ;
        
        ArrayList<Comparable> commenceRecord ;
        ArrayList<Comparable> breakupRecord ;
        String clazzName ;
        
        HashMap<Object,String> relationshipClazzReport 
                = (HashMap<Object,String>) getReport("relationshipClazz",this) ; // prepareRelationshipClazzReport() ;
        
        // tracks which relationshipIds from which relationshipClassName
        HashMap<String,ArrayList<Object>> openRelationships = new HashMap<String,ArrayList<Object>>() ;
        for (String className : relationshipClassNames)
            openRelationships.put(className, new ArrayList<Object>()) ;
        
        commenceRecord = EXTRACT_ALL_VALUES(RELATIONSHIPID,commenceReport.get(0)) ;
        for (Object relationshipId : commenceRecord)
        {
            clazzName = relationshipClazzReport.get(relationshipId) ;
            openRelationships.get(clazzName).add(EXTRACT_VALUE(RELATIONSHIPID,(String) relationshipId)) ;
        }
        String relationshipRecord = "" ;
        for (String className : relationshipClassNames)
            relationshipRecord += Reporter.ADD_REPORT_PROPERTY(className, openRelationships.get(className).size()) ;
        relationshipNumberReport.add(relationshipRecord) ;
        //LOGGER.log(Level.INFO,"length:{0} record:{1}", new Object[] {relationshipNumberReport.size(),relationshipRecord}) ;
        
        for (int breakupIndex = 0 ; breakupIndex < (breakupReport.size() - 1) ; breakupIndex++ )
        {
            relationshipRecord = "" ;
            
            breakupRecord = EXTRACT_ALL_VALUES(RELATIONSHIPID,breakupReport.get(breakupIndex)) ;
            for (Object relationshipId : breakupRecord)
                openRelationships.get(relationshipClazzReport.get(relationshipId)).remove(relationshipId) ;
            
            commenceRecord = EXTRACT_ALL_VALUES(RELATIONSHIPID,commenceReport.get(breakupIndex+1)) ;
            for (Object relationshipId : commenceRecord)
                openRelationships.get(relationshipClazzReport.get(relationshipId)).add(relationshipId) ;
            
            for (String className : relationshipClassNames)
                relationshipRecord += Reporter.ADD_REPORT_PROPERTY(className, openRelationships.get(className).size()) ;
            relationshipNumberReport.add(relationshipRecord) ;
        
        }
        return relationshipNumberReport ;
    }
    
    /**
     * 
     * @param relationshipClassNames
     * @return (ArrayList) records of mean number of each Relationship class per Agent
     */
    public ArrayList<HashMap<Object,String>> prepareMeanNumberRelationshipsReport(String[] relationshipClassNames)
    {
        ArrayList<HashMap<Object,String>> meanNumberRelationshipsReport 
                = new ArrayList<HashMap<Object,String>>() ;
        
        // (ArrayList) of Strings giving number of each RelationshipClazz in each cycle.
        ArrayList<Object> relationshipNumberReport = prepareRelationshipNumberReport(relationshipClassNames) ;
        
        HashMap<Object,Object> meanRelationshipClassReport ; // = new HashMap<Object,Object>() ;
        
        
        int population = getPopulation() ; 

        //for (int recordIndex = 0 ; recordIndex < agentNumberRelationshipsReport.size() ; recordIndex++ )
        //{
        for ( Object record : relationshipNumberReport) // .get(recordIndex) ;
        {
            meanRelationshipClassReport = new HashMap<Object,Object>() ;
            // Loop over Relationship classes
            for (String relationshipClassName : relationshipClassNames)
            {
                int sum = Integer.valueOf(EXTRACT_VALUE(relationshipClassName,(String) record)) ;
                double meanNb = 0.0 ;
                
                if (sum > 0)
                    meanNb = ((double) sum)/population ;
   
                meanRelationshipClassReport.put(relationshipClassName,ADD_REPORT_PROPERTY("meanNb",meanNb)) ;  // (Object) "Mean number of partners:" + 
            }
            //LOGGER.log(Level.INFO, "{0}", meanRelationshipClassReport) ;
            meanNumberRelationshipsReport.add((HashMap<Object,String>) meanRelationshipClassReport.clone()) ;
        }
        return meanNumberRelationshipsReport ;
    }
    
    
    /**
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) relationshipClassName maps to mean number of
     * Relationships of given class per agentId involved in during given time period).
     */
    public HashMap<Comparable,Number> 
        prepareAgentRelationshipsMean(String[] relationshipClassNames, int backYears, int backMonths, int backDays) 
        {
            int endCycle = getMaxCycles() ;
            return prepareAgentRelationshipsMean(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        }
    
    /**
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return (HashMap) relationshipClassName maps to mean number of
     * Relationships of given class per agentId involved in during given time period).
     */
    public HashMap<Comparable,Number> 
        prepareAgentRelationshipsMean(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle) 
    {
        HashMap<Comparable,Number> agentRelationshipsMean = new HashMap<Comparable,Number>() ;
        
        HashMap<Object,HashMap<Object,Integer>> agentRelationshipsCount 
            = prepareAgentRelationshipsCount(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        
        //double population = getPopulation() ; 
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        int finalMaxAgentId = populationReporter.getMaxAgentId(endCycle) ;
        int earlyMaxAgentId = populationReporter.getMaxAgentId(endCycle - getBackCycles(backYears, backMonths, backDays)) ;
        double effectivePopulation = getPopulation() + finalMaxAgentId - earlyMaxAgentId ;
        
        HashMap<Object,Integer> relationshipClazzCount ;
        int total ;
        for (String relationshipClazzName : relationshipClassNames)
        {
            relationshipClazzCount = agentRelationshipsCount.get(relationshipClazzName) ;
            total = 0 ;
            
            for (int nbRelationships : relationshipClazzCount.values())
                total += nbRelationships ;
            
            agentRelationshipsMean.put(relationshipClazzName, total/effectivePopulation) ;
        }
        
        return agentRelationshipsMean ;
    
    }
        
    /**
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param sortingProperty
     * @return Value of sortingProperty maps to String description of mean number of
     * Relationship of given class per agentId with given value for sortingProperty 
     * involved in given Relationship Class during given time period).
     */
    public String 
        prepareAgentRelationshipsMean(String[] relationshipClassNames, int backYears, int backMonths, int backDays, String sortingProperty) 
        {
            int endCycle = getMaxCycles() ;
            return prepareAgentRelationshipsMean(relationshipClassNames, backYears, backMonths, backDays, endCycle, sortingProperty) ;
        }
    
    /**
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @param sortingProperty
     * @return (HashMap) Value of sortingProperty maps to String description of mean number of
     * Relationship of given class per agentId with given value for sortingProperty 
     * involved in given Relationship Class during given time period).
     */
    public String 
        prepareAgentRelationshipsMean(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle, String sortingProperty) 
    {
        String agentRelationshipsMean = "" ;
        
        int nbAgents ;
        Object propertyValue ;
        
        // relationshipClazzName maps to agentId maps to number of given Relationships
        HashMap<Object,HashMap<Object,Integer>> agentRelationshipsCount 
            = prepareAgentRelationshipsCount(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        
        // agentId maps to value of sortingProperty
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        HashMap<Object,Object> sortedAgentReport = populationReporter.sortedAgentIds(sortingProperty) ;
        ArrayList<String> agentsAlive = populationReporter.prepareAgentsAliveRecord(endCycle-1) ;
        
        HashMap<Object,Integer> relationshipClazzCount ;
        int total ;
        for (String relationshipClazzName : relationshipClassNames)
        {
            // LOGGER.info(relationshipClazzName) ;
            HashMap<Object,Integer> agentRelationshipsTotal = new HashMap<Object,Integer>() ;
            HashMap<Object,Integer> nbAgentsMap = new HashMap<Object,Integer>() ;
            // agentId maps to number of given Relationships
            relationshipClazzCount = agentRelationshipsCount.get(relationshipClazzName) ;
            
            for (Object agentId : sortedAgentReport.keySet())
            {
                if (!agentsAlive.contains(agentId))
                    continue ;
                propertyValue = sortedAgentReport.get(agentId.toString()) ;
                //LOGGER.info(sortedAgentReport.get(agentId.toString()).toString()) ;
                if (!agentRelationshipsTotal.containsKey(propertyValue))                
                {
                    agentRelationshipsTotal.put(propertyValue, 0) ;
                    nbAgentsMap.put(propertyValue, 0) ;
                }
                if (relationshipClazzCount.containsKey(agentId))
                {
                    total = agentRelationshipsTotal.get(propertyValue) ;
                    total += relationshipClazzCount.get(agentId) ;
                    agentRelationshipsTotal.put(propertyValue,total) ;
                }
                nbAgents = nbAgentsMap.get(propertyValue) ;
                nbAgentsMap.put(propertyValue,nbAgents + 1) ;
            }
            // LOGGER.info(agentRelationshipsTotal.toString());
            // LOGGER.info(nbAgentsMap.toString()) ;
            for (Object sortingValue : agentRelationshipsTotal.keySet())
                agentRelationshipsMean += ADD_REPORT_PROPERTY(relationshipClazzName + "_" + sortingValue.toString(),
                    ((double) agentRelationshipsTotal.get(sortingValue))/nbAgentsMap.get(sortingValue)) ;
        }
        return agentRelationshipsMean ;
    }
        
    /**
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param lastYear
     * @return (HashMap) year of interest maps to (Number[]) Array of mean number of
     * Relationships of each relationshipClassName involved in per agentId during 
     * backMonths months, backDays days for each of backYears years counting back 
     * from lastYear).
     */
    public HashMap<Comparable,Number[]> 
        prepareAgentRelationshipsMeanYears(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int lastYear) 
        {
            HashMap<Comparable,Number[]> agentRelationshipsMeanYears = new HashMap<Comparable,Number[]>() ;
            
            int maxCycles = getMaxCycles() ;
            
            int endCycle ;
            HashMap<Comparable,Number> agentRelationshipsMean ;
            for (int year = 0 ; year < backYears ; year++ )
            {
                Number[] yearlyAgentRelationshipsMean = new Number[relationshipClassNames.length] ;
               
                endCycle = maxCycles - year * DAYS_PER_YEAR ;
                agentRelationshipsMean = prepareAgentRelationshipsMean(relationshipClassNames, 0, backMonths, backDays, endCycle);
               
                for (int classIndex = 0 ; classIndex < relationshipClassNames.length ; classIndex++ )
                    yearlyAgentRelationshipsMean[classIndex] = agentRelationshipsMean.get(relationshipClassNames[classIndex]) ;
                
                agentRelationshipsMeanYears.put(lastYear - year, (Number[]) yearlyAgentRelationshipsMean.clone()) ;
            }
            
            return agentRelationshipsMeanYears ;
        }
    
    
    
    /**
     * Calls prepareAgentRelationshipsRecord() and counts the Relationships.
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) relationshipClassName maps to (agentId maps to number of
     * Relationships of given class involved in during given time period).
     */
    public HashMap<Object,HashMap<Object,Integer>> 
        prepareAgentRelationshipsCount(String[] relationshipClassNames, int backYears, int backMonths, int backDays) 
        {
            int endCycle = getMaxCycles() ;
            return prepareAgentRelationshipsCount(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        }
    
    
    /**
     * Calls prepareAgentRelationshipsRecord() and counts the Relationships.
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return (HashMap) relationshipClassName maps to (agentId maps to number of
     * Relationships of given class involved in during given time period).
     */
    public HashMap<Object,HashMap<Object,Integer>> 
        prepareAgentRelationshipsCount(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle) 
    {
        HashMap<Object,HashMap<Object,Integer>> agentRelationshipsCount
            = new HashMap<Object,HashMap<Object,Integer>>() ;
        for (String relationshipClassName : relationshipClassNames)
            agentRelationshipsCount.put(relationshipClassName, new HashMap<Object,Integer>()) ;
        
        // Prepare agentRelationshipsRecord
        // Class[] parameterClazzes = new Class[] {String[].class,int.class,int.class,int.class,int.class} ;
        //Object[] parameters = new Object[] {relationshipClassNames, backYears, backMonths, backDays, endCycle} ;
        HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> agentRelationshipsRecord 
        = prepareAgentRelationshipsRecord(relationshipClassNames, backYears, backMonths, backDays, endCycle)  ;
        //    = (HashMap<Object,HashMap<Object,ArrayList<Object>>>) getRecord("agentRelationships",this,parameterClazzes,parameters) ;
        
        HashMap<Comparable,ArrayList<Comparable>> agentRelationships ;
        int count ;
        
        for (Object relationshipClassName : agentRelationshipsRecord.keySet())
        {
            if (!Arrays.asList(relationshipClassNames).contains((String) relationshipClassName))
                continue ;
            agentRelationships = agentRelationshipsRecord.get(relationshipClassName);
            for (Object agentId : agentRelationships.keySet())
            {
                count = agentRelationships.get(agentId).size();
                agentRelationshipsCount.get(relationshipClassName).put(agentId, count) ;
            }
        }
        return agentRelationshipsCount ;
    }
        
    /**
     * All Relationships entered except for those broken up before given period.
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) relationshipClassName maps to (agentId maps to relationshipIds 
     * involved in during specified time period).
     */
    public HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> 
        prepareAgentRelationshipsRecord(String[] relationshipClassNames, int backYears, int backMonths, int backDays) 
        {
            int endCycle = getMaxCycles() ;
            return prepareAgentRelationshipsRecord(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        }
    
        
    /**
     * All Relationships entered except for those broken up before given period.
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return (HashMap) relationshipClassName maps to (agentId maps to relationshipIds 
     * involved in during specified time period).
     */
    public HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> 
        prepareAgentRelationshipsRecord(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle) 
    {
        HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> agentRelationshipsRecord 
                = new HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>() ;
        for (String relationshipClassName : relationshipClassNames)
            agentRelationshipsRecord.put(relationshipClassName, new HashMap<Comparable,ArrayList<Comparable>>()) ;
        
        int backCycles = GET_BACK_CYCLES(backYears, backMonths, backDays, endCycle) ;
        
        // Each record is a HashMap where relationshipClassName maps to a 
            //  HashMap where agentIds map to new relationshipIds.
        ArrayList<HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>> agentsEnteredRelationshipReport 
         = prepareAgentsEnteredRelationshipReport(relationshipClassNames, 0, 0, endCycle, endCycle) ;
        
        for (HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> record : agentsEnteredRelationshipReport)
            for (String relationshipClassName : relationshipClassNames)
            {
                HashMap<Comparable,ArrayList<Comparable>> agentRelationshipClassRecord = record.get(relationshipClassName) ;
                for (Comparable agentId : agentRelationshipClassRecord.keySet())
                {
                    for (Comparable relationshipId : agentRelationshipClassRecord.get(agentId))
                        agentRelationshipsRecord.put(relationshipClassName, 
                            UPDATE_HASHMAP(agentId,relationshipId,agentRelationshipsRecord.get(relationshipClassName),false)) ;
                }
            }
        
        // Remove relationshipIds which have broken up.
        ArrayList<String> breakupReport = (ArrayList<String>) getReport("breakup",this) ; //   prepareBreakupReport() ;
        // relationshipId maps to relationshipClassName
        HashMap<Object,String> relationshipClazzReport = prepareRelationshipClazzReport() ;
        // relationshipId maps to (String[]) agentIds
        HashMap<Object,String[]> relationshipAgentReport = (HashMap<Object,String[]>) getReport("relationshipAgent",this) ; // prepareRelationshipAgentReport() ;
        
        String relationshipClassName ;
        int enterCycle = endCycle - backCycles ;
        String breakupRecord ; 
        ArrayList<Comparable> relationshipIdList ;
        for (int enteredIndex = 0 ; enteredIndex < enterCycle ; enteredIndex++ )
        {
            breakupRecord = breakupReport.get(enteredIndex);
            relationshipIdList = EXTRACT_ALL_VALUES(RELATIONSHIPID,breakupRecord);
            for (Object relationshipId : relationshipIdList)
            {
                // Get relationshipId relationshipClass
                relationshipClassName = relationshipClazzReport.get(relationshipId) ;
                //LOGGER.log(Level.INFO,"enteredIndex:{0} {1}", new Object[] {enteredIndex,agentRelationshipsRecord.get(relationshipClassName).keySet()}) ;
                if (!agentRelationshipsRecord.containsKey(relationshipClassName))
                    continue ;
                for (String agentId : relationshipAgentReport.get(relationshipId))
                    agentRelationshipsRecord.get(relationshipClassName).get(agentId).remove(relationshipId) ;
            }
        }
        return agentRelationshipsRecord ;
    }
        
    /**
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) relationshipClassName maps to the number of Agents involved in 
     * given class of Relationship during the specified period.
     */
    public HashMap<Comparable,Number> prepareNumberRelationshipsReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays)
    {
        int endCycle = getMaxCycles() ; 
        
        return prepareNumberRelationshipsReport(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
    }
    
        
    /**
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return (HashMap) relationshipClassName maps to the number of Agents involved in 
     * given class of Relationship during the specified period.
     */
    public HashMap<Comparable,Number> prepareNumberRelationshipsReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle)
    {
        return prepareNumberRelationshipsReport(relationshipClassNames, backYears, backMonths, backDays, endCycle, new ArrayList<String>()) ;
    }
        
    /**
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @param agentList
     * @return (HashMap) relationshipClassName maps to the number of Agents involved in 
     * given class of Relationship during the specified period.
     */
    public HashMap<Comparable,Number> prepareNumberRelationshipsReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle, ArrayList<String> agentList )
    {
        HashMap<Comparable,Number> numberRelationshipsReport = new HashMap<Comparable,Number>() ;
        
        // Prepare agentRelationshipsRecord
//        Class[] parameterClazzes = new Class[] {String[].class,int.class,int.class,int.class,int.class} ;
//        Object[] parameters = new Object[] {relationshipClassNames, backYears, backMonths, backDays, endCycle} ;
        HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> agentRelationshipsRecord
            = (HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>) prepareAgentRelationshipsRecord(relationshipClassNames, backYears, backMonths, backDays) ;
        // getRecord("agentRelationships",this,parameterClazzes,parameters) ;
        HashMap<Object,String> relationshipClazzReport = prepareRelationshipClazzReport() ;
        // (HashMap<Object,String>) getReport("relationshipClazz",this) ;   // 
        
        ArrayList<Object> totalAgents = new ArrayList<Object>() ;
        ArrayList<Object> relationshipClazzAgents ;
        for (String relationshipClassName : relationshipClassNames)
        {
            HashMap<Comparable,ArrayList<Comparable>> agentRelationships 
                    = agentRelationshipsRecord.get(relationshipClassName) ;
            
            // Consider only Agents who have been involved in relationshipClazzName Relationships
            relationshipClazzAgents = new ArrayList<Object>(agentRelationships.keySet()) ;
            for (Comparable agentId : agentRelationships.keySet())
            {
                ArrayList<Comparable> relationshipIds = agentRelationships.get(agentId) ;
                boolean keepAgentId = false ;
                for (Object relationshipId : relationshipIds)
                {
                    keepAgentId = relationshipClassName.equals(relationshipClazzReport.get(relationshipId)) ;
                    break ;
                }
                if (!keepAgentId)
                    relationshipClazzAgents.remove(agentId) ;
            }
            // Consider only sorted Agents from agentList
            if (!agentList.isEmpty())
                relationshipClazzAgents.retainAll(agentList) ;
            
            numberRelationshipsReport.put(relationshipClassName, relationshipClazzAgents.size()) ;
            
            relationshipClazzAgents.removeAll(totalAgents) ;
            totalAgents.addAll(relationshipClazzAgents) ;
        }
        numberRelationshipsReport.put("total", totalAgents.size()) ;
        
        return numberRelationshipsReport ;
    }
    
    /**
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return (HashMap) relationshipClassName maps to the proportion of Agents involved in 
     * given class of Relationship during the specified period.
     */
    public HashMap<Comparable,Number> prepareProportionRelationshipsReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle)
    {
        HashMap<Comparable,Number> proportionRelationshipsReport = new HashMap<Comparable,Number>() ;
        
        HashMap<Comparable,Number> numberRelationshipsReport = prepareNumberRelationshipsReport(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        int finalMaxAgentId = populationReporter.getMaxAgentId(endCycle) ;
        int earlyMaxAgentId = populationReporter.getMaxAgentId(endCycle - getBackCycles(backYears, backMonths, backDays)) ;
        double effectivePopulation = getPopulation() + finalMaxAgentId - earlyMaxAgentId ;
        
        //HashMap<Object,Integer> relationshipClazzCount ;
        //int total ;
        for (Comparable relationshipClazzName : numberRelationshipsReport.keySet())
        {
            proportionRelationshipsReport.put(relationshipClazzName,numberRelationshipsReport.get(relationshipClazzName).doubleValue()/effectivePopulation) ;
        }
        
        return proportionRelationshipsReport ;
    }
    /**
     * First count the number of Relationships each Agent has entered up to now,
     * then subtract those which have broken up.
     * @param relationshipClassNames
     * @return Each relationshipId gives relationshipClassName maps to 
 (the number of current Relationships for each Agent).
     */
    public ArrayList<HashMap<Object,HashMap<Object,Integer>>> prepareAgentNumberRelationshipsReport(String[] relationshipClassNames) 
    {
        ArrayList<HashMap<Object,HashMap<Object,Integer>>> agentNumberRelationshipsReport 
                = new ArrayList<HashMap<Object,HashMap<Object,Integer>>>() ;
        
        ArrayList<HashMap<Object,HashMap<Object,Integer>>> agentEnterRelationshipsReport 
                = new ArrayList<HashMap<Object,HashMap<Object,Integer>>>() ;
        
        ArrayList<HashMap<Object,HashMap<Object,Integer>>> agentBreakupRelationshipsReport 
                = new ArrayList<HashMap<Object,HashMap<Object,Integer>>>() ;
        
        // ArrayList of relationshipClassName maps to (agentId maps to relationshipIds)
        ArrayList<HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>> agentsEnteredRelationshipReport 
                = prepareAgentsEnteredRelationshipReport(relationshipClassNames) ;
        //for (HashMap<Object,HashMap<Object,ArrayList<Object>>> aerRecord : agentsEnteredRelationshipReport)
            //LOGGER.log(Level.INFO, "agentsEnteredRelationship{0}", aerRecord) ;
      // logger.log(level.info, "{0}", agentsEnteredRelationshipReport);
        // relationshipId maps to relationshipClassName ;
        HashMap<Object,String> relationshipClazzReport 
                = (HashMap<Object,String>) getReport("relationshipClazz",this) ;
        //prepareRelationshipClazzReport() ;
      // logger.log(level.info, "{0}", relationshipClazzReport);
        
        ArrayList<String> breakupReport = (ArrayList<String>) getReport("breakup",this) ; //   prepareBreakupReport() ;
    
        // relationshipClassName maps to (agentId maps to relationshipIds)
        HashMap<Object,HashMap<Comparable,ArrayList<Comparable>>> agentRelationships 
                = new HashMap<Object,HashMap<Comparable,ArrayList<Comparable>>>() ;
        for (Object relationshipClassName : relationshipClassNames)
            agentRelationships.put(relationshipClassName, new HashMap<Comparable,ArrayList<Comparable>>()) ;
        
        //LOGGER.log(Level.INFO, "{0}", agentsEnteredRelationshipReport);
        int reportsSize = agentsEnteredRelationshipReport.size() ;
        
        // RelationshipClassName maps to (agentId maps to Relationship count) 
        HashMap<Object,HashMap<Object,Integer>> agentRelationshipsCount = new HashMap<Object,HashMap<Object,Integer>>() ;
        for (Object relationshipClassName : relationshipClassNames)
            agentRelationshipsCount.put(relationshipClassName, new HashMap<Object,Integer>()) ;
        
        // LOGGER.info("Find the Relationships entered into up to and including given cycle") ;
        for (int enteredIndex = 0 ; enteredIndex < reportsSize ; enteredIndex++ )
        {
            // Formation of new Relationships
            HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> enteredRecord 
                    = agentsEnteredRelationshipReport.get(enteredIndex) ;
            for (Comparable relationshipClassName : relationshipClassNames)
            {    
                HashMap<Comparable,ArrayList<Comparable>> agentsRelationshipClassRecord 
                    = new HashMap<Comparable,ArrayList<Comparable>>() ;
                agentsRelationshipClassRecord = enteredRecord.get(relationshipClassName) ;
                
                HashMap<Object,Integer> agentCount = new HashMap<Object,Integer>() ;
                agentCount = agentRelationshipsCount.get(relationshipClassName) ;
                
                if (agentsRelationshipClassRecord.size() > 0)
                    for (Comparable agentId : agentsRelationshipClassRecord.keySet())
                    {
                        //LOGGER.info(String.valueOf(agentId)) ;
                        // Number of new Relationships
                        int newTotal = agentsRelationshipClassRecord.get(agentId).size() ;

                        // Plus ones already formed
                        if (agentCount.containsKey(agentId))
                            newTotal += agentCount.get(agentId) ;
                        agentCount.put(agentId, newTotal) ;
                        //LOGGER.log(Level.INFO, "{0}", agentRelationshipsCount);

                        for (Comparable relationshipId : agentsRelationshipClassRecord.get(agentId))
                            agentRelationships.put(relationshipClassName, 
                                    UPDATE_HASHMAP(relationshipId,agentId,agentRelationships.get(relationshipClassName))) ;
                    }
                agentRelationshipsCount.put(relationshipClassName,(HashMap<Object,Integer>) agentCount.clone()) ;
            }
            HashMap<Object,HashMap<Object,Integer>> enterAgentRelationshipsCount 
                     = new HashMap<Object,HashMap<Object,Integer>>() ; 
                enterAgentRelationshipsCount = (HashMap<Object,HashMap<Object,Integer>>) agentRelationshipsCount.clone() ;
            agentEnterRelationshipsReport.add((HashMap<Object,HashMap<Object,Integer>>) enterAgentRelationshipsCount.clone()) ;
            
            //LOGGER.log(Level.INFO, "agentEnterRelationshipsLast:{0}", agentEnterRelationshipsReport.get(agentEnterRelationshipsReport.size()-1)) ;
            //LOGGER.log(Level.INFO, "agentEnterRelationshipsFirst:{0}", agentEnterRelationshipsReport.get(testIndex)) ;
        }
        
        // relationshipClassName maps to (Count number of broken-up Relationships of each Agent.)
        HashMap<Object,HashMap<Comparable,Number>> agentBreakupsCount 
                = new HashMap<Object,HashMap<Comparable,Number>>() ;
        for (String relationshipClassName : relationshipClassNames)
            agentBreakupsCount.put(relationshipClassName, new HashMap<Comparable,Number>()) ;
        
        // LOGGER.info("// Find the Relationships broken up from up to given cycle") ;
        for (int breakupIndex = 0 ; breakupIndex < (reportsSize-1) ; breakupIndex++ )
        {
            // Formation of new Relationships
            String breakupRecord = breakupReport.get(breakupIndex) ; // agentsBreakupRelationshipReport.get(breakupIndex) ;

            //for (Object agentId : breakupRecord.keySet())
            for (Object relationshipId : EXTRACT_ALL_VALUES(RELATIONSHIPID,breakupRecord,0)) 
            {
                // Relationship breakups
                Object relationshipClassName = relationshipClazzReport.get(relationshipId) ; // EXTRACT_VALUE("relationship",) ;
                if (! Arrays.asList(relationshipClassNames).contains((String) relationshipClassName))
                    continue ;

                HashMap<Comparable,Number> agentBreakups = new HashMap<Comparable,Number>() ;
                
                
                for (Comparable agentId : agentRelationships.get(relationshipClassName).get(relationshipId))
                {
                    //int subtractTotal = breakupRecord.get(agentId).size() ;
                    //agentBreakupsCount.put(relationshipClassName,INCREMENT_HASHMAP(agentId,agentBreakupsCount.get(relationshipClassName))) ;
                    agentBreakups = INCREMENT_HASHMAP(agentId,agentBreakupsCount.get(relationshipClassName)) ;
//                if (agentBreakupsCount.containsKey(agentId))
//                {
//                    // Plus ones already formed
//                    subtractTotal += agentBreakupsCount.get(agentId) ;
//                }
//                agentBreakupsCount.put(agentId, subtractTotal) ;
                }
                agentBreakupsCount.put(relationshipClassName,(HashMap<Comparable,Number>) agentBreakups.clone()) ;
            
            }
            HashMap<Object,HashMap<Object,Integer>> breakupAgentRelationshipsCount 
                     = new HashMap<Object,HashMap<Object,Integer>>() ; 
            breakupAgentRelationshipsCount = (HashMap<Object,HashMap<Object,Integer>>) agentBreakupsCount.clone() ;
            agentBreakupRelationshipsReport.add((HashMap<Object,HashMap<Object,Integer>>) breakupAgentRelationshipsCount.clone()) ;
        }
        
        // LOGGER.info("// Subtract broken-up Relationships from commenced Relationships for each Agent.") ;
        for (int breakupIndex = 0 ; breakupIndex < (reportsSize-1) ; breakupIndex++ )
        {
            HashMap<Object,HashMap<Object,Integer>> numberRelationshipsCount = new HashMap<Object,HashMap<Object,Integer>>() ;
            HashMap<Object,HashMap<Object,Integer>> enterRecord = agentEnterRelationshipsReport.get(breakupIndex+1) ;
            //LOGGER.log(Level.INFO, "enterRecord:{0}", enterRecord);
            HashMap<Object,HashMap<Object,Integer>> breakupRecord = agentBreakupRelationshipsReport.get(breakupIndex) ;
            //LOGGER.log(Level.INFO, "breakupRecord:{0}", breakupRecord);
            
            for (Object relationshipClassName : relationshipClassNames)
            {
                numberRelationshipsCount.put(relationshipClassName, new HashMap<Object,Integer>()) ;
                for (Object agentId : breakupRecord.get(relationshipClassName).keySet())
                {
    //                if (!enterRecord.containsKey(agentId))
    //                    continue ;  // Relationship entered during burn-in
                    try
                    {
                        numberRelationshipsCount.get(relationshipClassName).
                                put(agentId, enterRecord.get(relationshipClassName).get(agentId) - breakupRecord.get(relationshipClassName).get(agentId)) ;
                    }
                    catch (Exception e)
                    {
                      // logger.log(level.info,"agentId:{0} relationshipClassName:{1} index:{2}", new Object[] {agentId,relationshipClassName,breakupIndex}) ;
                      // logger.log(level.info, "{0}", enterRecord.get(relationshipClassName));
                      // logger.log(level.info, "{0}", breakupRecord.get(relationshipClassName));
                        //assert(false) ;
                        //numberRelationshipsCount.get(relationshipClassName).
                            //    put(agentId, enterRecord.get(relationshipClassName).get(agentId) - breakupRecord.get(relationshipClassName).get(agentId)) ;
                    }
                }
            }
            //LOGGER.log(Level.INFO, "{0}", numberRelationshipsCount) ;
            agentNumberRelationshipsReport.add((HashMap<Object,HashMap<Object,Integer>>) numberRelationshipsCount.clone()) ;
        }
        return agentNumberRelationshipsReport ;
    }
    
    /**
     * Indicates which Agents were infected at which Sites for which cycles.
     * TODO: Adapt to multiple Report files
     * @param siteNames
     * @return HashMap key agentId, value HashMap key siteName value ArrayList of cycles when infected
     */
    public HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> prepareAgentInfectionReport(String[] siteNames)
    {
        return prepareAgentInfectionReport(siteNames,0,0,getBackCycles(0,0,getMaxCycles())) ;
    }
    
    /**
     * Indicates which Agents were infected at which Sites for which cycles.
     * TODO: Adapt to multiple Report files
     * @param siteNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return HashMap key agentId, value HashMap key siteName value ArrayList of cycles when infected
     */
    public HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> 
        prepareAgentInfectionReport(String[] siteNames, int backYears, int backMonths, int backDays)
    {
        HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> agentInfectionReport = new HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>() ;

        int backCycles = getBackCycles(backYears, backMonths, backDays) ;
        int maxCycles = getMaxCycles() ;
        int startCycle = maxCycles - backCycles ;
        int infectionCycle ;
        ArrayList<String> backCyclesReport = getBackCyclesReport(backYears, backMonths, backDays, maxCycles) ;

        for (int recordIndex = 0 ; recordIndex < backCycles ; recordIndex++ )
        {
            String record = backCyclesReport.get(recordIndex) ; 
            ArrayList<String> agentIdArray = EXTRACT_ARRAYLIST(record,AGENTID) ;
            for ( String agentString : agentIdArray )
            {
                String agentId = EXTRACT_VALUE(AGENTID,agentString) ;
                //siteInfectionReport = agentInfectionReport.get(agentId) ;
                infectionCycle = startCycle + recordIndex ;
                for ( Comparable siteName : siteNames )
                    if (record.contains(siteName.toString()))
                        agentInfectionReport = UPDATE_HASHMAP(agentId,siteName,infectionCycle,agentInfectionReport) ;
            }
        }
        return agentInfectionReport ;
    }
    
    /**
     * 
     * @param relationshipClassNames
     * @return Each relationshipId is a HashMap indicating new relationshipIds for relevant (key) Agents
     */
    public ArrayList<HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>> 
        prepareAgentsEnteredRelationshipReport(String[] relationshipClassNames)
    {
        int endCycle = getMaxCycles() ;
        return prepareAgentsEnteredRelationshipReport(relationshipClassNames,0,0,endCycle,endCycle) ;
    }
        
    /**
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return Each relationshipId is a HashMap indicating new relationshipIds for relevant (key) Agents
     */
    public ArrayList<HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>> 
        prepareAgentsEnteredRelationshipReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays)
    {
        int endCycle = getMaxCycles() ;
        return prepareAgentsEnteredRelationshipReport(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
    }
    
    
    /**
     * Report of which Agents entered which relationships and when.
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return Each record is a HashMap where relationshipClassName maps to a 
     * HashMap where agentIds map to new relationshipIds.
     */
    public ArrayList<HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>> 
        prepareAgentsEnteredRelationshipReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle)
    {
        ArrayList<HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>> agentsEnteredRelationshipReport 
                = new ArrayList<HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>>() ; 
        
        //HashMap<Object,String[]> relationshipAgentReport 
          //      = prepareRelationshipAgentReport() ; // (HashMap<Object,String[]>) getReport("relationshipAgent",this) ; // 
        //LOGGER.log(Level.INFO, "{0}", relationshipAgentReport) ;
        
        // How many cycles far back do we count back from endCycle?
        int backCycles = GET_BACK_CYCLES(backYears, backMonths, backDays, endCycle) ;
        ArrayList<String> commenceReport = (ArrayList<String>) getReport("commence",this) ; //  prepareCommenceReport() ;
        HashMap<Object,HashMap<Comparable,ArrayList<Comparable>>> commenceRelationshipRecord 
                = new HashMap<Object,HashMap<Comparable,ArrayList<Comparable>>>();
        for (String relationshipClassName : relationshipClassNames)
            commenceRelationshipRecord.put(relationshipClassName, new HashMap<Comparable,ArrayList<Comparable>>()) ;
        
        for (int recordIndex = endCycle - 1 ; recordIndex >= (endCycle - backCycles) ; recordIndex-- )
        {
            String record = commenceReport.get(recordIndex) ;
            
            commenceRelationshipRecord = new HashMap<Object,HashMap<Comparable,ArrayList<Comparable>>>();
            for (String relationshipClassName : relationshipClassNames)
                commenceRelationshipRecord.put(relationshipClassName, new HashMap<Comparable,ArrayList<Comparable>>()) ;
            ArrayList<String> relationshipIdArray = EXTRACT_ARRAYLIST(record,RELATIONSHIPID) ;
            //LOGGER.log(Level.INFO,"{0}",relationshipIdArray) ;
            for (String relationshipString : relationshipIdArray)
            {
                //LOGGER.info(relationshipString) ;
                Comparable relationshipIdValue = EXTRACT_VALUE(RELATIONSHIPID,relationshipString) ;
                String relationshipClassName = EXTRACT_VALUE(RELATIONSHIP,relationshipString) ;
                String agentId0 = EXTRACT_VALUE(AGENTID0,relationshipString) ;
                String agentId1 = EXTRACT_VALUE(AGENTID1,relationshipString) ;
                if (!commenceRelationshipRecord.containsKey(relationshipClassName)) 
                {
                    //LOGGER.info("commenceRelationshipRecord does not contain key " + relationshipClassName) ;
                    continue ;
                }
                //Object[] agentIdValues = relationshipAgentReport.get(relationshipIdValue) ; 
                
                //String agentId0Value = EXTRACT_VALUE(AGENTID0,relationshipString) ;
                commenceRelationshipRecord.put(relationshipClassName,
                        UPDATE_HASHMAP((Comparable) agentId0, (Comparable) relationshipIdValue,commenceRelationshipRecord.get(relationshipClassName))) ;
                
                //String agentId1Value = EXTRACT_VALUE(AGENTID1,relationshipString) ;
                commenceRelationshipRecord.put(relationshipClassName,
                        UPDATE_HASHMAP(agentId1,relationshipIdValue,commenceRelationshipRecord.get(relationshipClassName))) ;
            }
            
            agentsEnteredRelationshipReport.add(0,(HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>) commenceRelationshipRecord.clone()) ;
        }
        return agentsEnteredRelationshipReport ;
    }
        
    
    /**
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) Relationship class maps to number of Agents entering such
     * Relationship during given time.
     */
    public HashMap<Comparable,Number> 
        prepareNumberAgentsEnteredRelationshipReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays)
        {
            int endCycle = getMaxCycles() ;
            
            return prepareNumberAgentsEnteredRelationshipReport(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        }
    
    /**
     * TODO: Change entering to being in
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return (HashMap) Relationship class maps to number of Agents entering such
     * Relationship during given time.
     */
    public HashMap<Comparable,Number> 
        prepareNumberAgentsEnteredRelationshipReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle)
    {
        HashMap<Comparable,Number> agentsEnteredRelationshipReport = new HashMap<Comparable,Number>() ;
        
        // (HashMap) relationshipClassName maps to (agentIds maps to number of new Relationships in given time).
        HashMap<Comparable,HashMap<Object,Integer>> numberRecentRelationshipsReport 
                = prepareNumberRecentRelationshipsReport(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        int finalMaxAgentId = populationReporter.getMaxAgentId(endCycle) ;
        int earlyMaxAgentId = populationReporter.getMaxAgentId(endCycle - getBackCycles(backYears, backMonths, backDays)) ;
        double effectivePopulation = getPopulation() + finalMaxAgentId - earlyMaxAgentId ;
        
        for (Comparable relationshipClassName : relationshipClassNames )  // numberRecentRelationshipsReport.keySet())
            agentsEnteredRelationshipReport.put(relationshipClassName, numberRecentRelationshipsReport.get(relationshipClassName).keySet().size()/effectivePopulation) ;
    
        return agentsEnteredRelationshipReport ;
    }
    
    /**
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param lastYear
     * @return year-by-year Report of numbers of Agents entering each Relationship Class
     */
    public HashMap<Comparable,Number[]> 
        prepareNumberAgentsEnteredRelationshipYears(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int lastYear)
        {
            HashMap<Comparable,Number[]> numberAgentsEnteredRelationshipYears = new HashMap<Comparable,Number[]>() ;
            
            int maxCycles = getMaxCycles() ;
            
            int endCycle ;
            HashMap<Comparable,Number> numberAgentsEnteredRelationship ;
            for (int year = 0 ; year < backYears ; year++ )
            {
                Number[] yearlyNumberAgentsEnteredRelationship = new Number[relationshipClassNames.length] ;
               
                endCycle = maxCycles - year * DAYS_PER_YEAR ;
                numberAgentsEnteredRelationship = prepareNumberAgentsEnteredRelationshipReport(relationshipClassNames, 0, backMonths, backDays, endCycle);
               
                for (int classIndex = 0 ; classIndex < relationshipClassNames.length ; classIndex++ )
                    yearlyNumberAgentsEnteredRelationship[classIndex] = numberAgentsEnteredRelationship.get(relationshipClassNames[classIndex]) ;
                
                numberAgentsEnteredRelationshipYears.put(lastYear - year, (Number[]) yearlyNumberAgentsEnteredRelationship.clone()) ;
            }
            return numberAgentsEnteredRelationshipYears ;
        }
    
    /**
     * Filters Relationships in fullReport records to be class relationshipClazzName
     * @param relationshipClazzName
     * @param fullReport
     * @return 
     */
    protected ArrayList<String> filterRelationshipClazzReport(String relationshipClazzName, ArrayList<String> fullReport)
    {
        if (relationshipClazzName.isEmpty())
            return fullReport ;
        
        ArrayList<String> relationshipClazzReport = new ArrayList<String>() ;
        
        HashMap<Object,String> relationshipReport = prepareRelationshipClazzReport() ;
        String relationshipId ;
        String filteredRecord ;
        
        for (String record : fullReport)
        {
            filteredRecord = "" ;
            ArrayList<String> encounterRecords = EXTRACT_ARRAYLIST(record,RELATIONSHIPID) ;
            for (String encounter : encounterRecords)
            {
                relationshipId = EXTRACT_VALUE(RELATIONSHIPID,encounter) ; 
                //
                if (relationshipClazzName.equals(relationshipReport.get(relationshipId))) 
                    filteredRecord = filteredRecord + encounter ;
            }
            relationshipClazzReport.add(filteredRecord) ;
        }
        
        return relationshipClazzReport ;
    }
    
    /**
     * Filters Relationship entries in fullReport to be con/dis-cordant with respect
     * to propertyName.
     * @param propertyName
     * @param concordant
     * @param fullReport
     * @return (ArrayList) String records including only appropriately con/dis-cordant
     * Relationship entries
     */
    protected ArrayList<String> filterByConcordance(String propertyName, boolean concordant, ArrayList<String> fullReport)
    {
        if (propertyName.isEmpty())
            return fullReport ;
        
        ArrayList<String> filteredReport = new ArrayList<String>() ;
        
        String filteredRecord ;
        
        // (HashMap) relationshipId maps to String describing boolean concordance of propertName between involved Agents.
        HashMap<Object,String> relationshipConcordanceReport = prepareRelationshipConcordanceReport(propertyName) ;
        
            //LOGGER.log(Level.INFO,"{0}", fullReport) ;
        for (String fullRecord : fullReport)
        {
            //LOGGER.log(Level.INFO,"{0}", fullRecord) ;
            ArrayList<String> relationshipList = EXTRACT_ARRAYLIST(fullRecord,RELATIONSHIPID) ;
            filteredRecord = "" ;
            //LOGGER.log(Level.INFO,"{0}", relationshipList) ;
            for (String relationshipEntry : relationshipList)
            {
                //LOGGER.info(relationshipEntry);
                String relationshipId = EXTRACT_VALUE(RELATIONSHIPID,relationshipEntry) ;
                String concordanceString = relationshipConcordanceReport.get(relationshipId) ;
                
                if (TRUE.equals(EXTRACT_VALUE(propertyName,concordanceString)))
                    filteredRecord += relationshipEntry ;
            }
            filteredReport.add(filteredRecord) ;
        }
        return filteredReport ;
    }
    
    /**
     * Prepares report on whether Agents in a Relationship are concordant with 
     * regards to propertyName.
     * @param propertyName
     * @return (HashMap) relationshipId maps to String describing boolean concordance
     * of propertName between involved Agents.
     */
    public HashMap<Object,String> prepareRelationshipConcordanceReport(String propertyName)
    {
        //LOGGER.info("prepareRelationshipConcordanceReport");
        HashMap<Object,String> relationshipConcordanceReport = new HashMap<Object,String>() ; 
        String concordanceString ;
        
        HashMap<Object,String[]> relationshipAgentReport 
                = (HashMap<Object,String[]>) getReport("relationshipAgent",this) ; // prepareRelationshipAgentReport() ; 
        // (HashMap<Object,String[]>) getReport("relationshipAgent",this) ; // 
        
        ArrayList<String[]> relationshipAgentsList = new ArrayList<String[]>() ;
        
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        
        HashMap<String[],Boolean> concordanceBoolean ;
        
        for (String[] pair : relationshipAgentReport.values())
            relationshipAgentsList.add(pair) ;
        
        concordanceBoolean = populationReporter.getConcordants(propertyName,relationshipAgentsList) ;
        
        for (Object relationshipId : relationshipAgentReport.keySet())
        {
            String[] concordanceKey = relationshipAgentReport.get(relationshipId) ;
            concordanceString = Reporter.ADD_REPORT_PROPERTY(propertyName, concordanceBoolean.get(concordanceKey)) ;
            relationshipConcordanceReport.put(relationshipId, concordanceString) ;
        }
        
        return relationshipConcordanceReport ;
    }
    
    /**
     * 
     * @return (HashMap) relationshipId maps to String describing boolean concordance between
     * involved Agents.
     */
    public HashMap<Object,String> prepareRelationshipConcordantsReport()
    {
        HashMap<Object,String> relationshipConcordantsReport = new HashMap<Object,String>() ;
        
        HashMap<Object,String[]> relationshipAgentReport 
                = (HashMap<Object,String[]>) getReport("relationshipAgent",this) ; //  prepareRelationshipAgentReport() ; 
        // (HashMap<Object,String[]>) getReport("relationshipAgent",this) ; //  ;
        
        String concordantOutput ;
        String value0 ;
                
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        
        // Identify census properties for all Agents.
        HashMap<Object,String> censusPropertyReport = populationReporter.prepareCensusPropertyReport() ;
        
        ArrayList<String> censusProperties = IDENTIFY_PROPERTIES(censusPropertyReport.get("0")) ;
        for (Object relationshipId : relationshipAgentReport.keySet())
        {
            String[] agentIds = relationshipAgentReport.get(relationshipId) ;
            String censusRecord0 = censusPropertyReport.get(agentIds[0]) ;
            String censusRecord1 = censusPropertyReport.get(agentIds[1]) ;
            concordantOutput = "" ;
            for (String propertyName : censusProperties)
            {
                value0 = EXTRACT_VALUE(propertyName,censusRecord0);
                concordantOutput += ADD_REPORT_PROPERTY(propertyName,COMPARE_VALUE(propertyName,value0,censusRecord1)) ;
            }
            relationshipConcordantsReport.put(relationshipId, concordantOutput) ;
        }
        return relationshipConcordantsReport ;
    }
    
    /**
     * 
     * @return (HashMap) relationshipId maps to relationship.subclass.getName()
     */
    protected HashMap<Object,String> prepareRelationshipClazzReport()
    {
        HashMap<Object,String> relationshipClazzReport = new HashMap<Object,String>() ; 
        
        ArrayList<String> commenceReport = (ArrayList<String>) getReport("commence",this) ; //  prepareCommenceReport() ;
        
        String relationshipId ;
        String relationshipClassName ;
        
        for (String record : commenceReport)
        {
            ArrayList<String> relationshipEntries = EXTRACT_ARRAYLIST(record,RELATIONSHIPID) ;
            
            for (String entry : relationshipEntries)
            {
                relationshipId = EXTRACT_VALUE(RELATIONSHIPID,entry);
                relationshipClassName = EXTRACT_VALUE("relationship",entry) ;
                relationshipClazzReport.put(relationshipId,relationshipClassName) ;
            }
        }
        
        return relationshipClazzReport ;
    }
    
    /**
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) relationshipClassName maps to HashMap where the number 
     * of new Relationships in given period maps to number
     * of Agents who had that many Relationships during that period.
     */
    public HashMap<Comparable,HashMap<Comparable,Number>> 
        prepareRecentRelationshipsReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays)
        {
            int endCycle = getMaxCycles() ; 
            
            return prepareRecentRelationshipsReport(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        }

    
    /**
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return (HashMap) relationshipClassName maps to HashMap where the number 
     * of new Relationships in given period maps to the number
     * of Agents who had that many Relationships during that period.
     */
    public HashMap<Comparable,HashMap<Comparable,Number>> 
        prepareRecentRelationshipsReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle)
    {
        HashMap<Comparable,HashMap<Comparable,Number>> recentRelationshipsReport = new HashMap<Comparable,HashMap<Comparable,Number>>() ; 
        for (String relationshipClass : relationshipClassNames)
            recentRelationshipsReport.put(relationshipClass, new HashMap<Comparable,Number>()) ;
        recentRelationshipsReport.put("total", new HashMap<Comparable,Number>()) ;
         
        // (HashMap) agentIds maps to number of new Relationships in given time.
        HashMap<Comparable,HashMap<Object,Integer>> numberRecentRelationshipsReport 
                  = prepareNumberRecentRelationshipsReport(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        ArrayList<String> agentsAlive = populationReporter.prepareAgentsAliveRecord(endCycle) ;
        
        int newRelationships ;
        //int population = getPopulation() ;
        
        for (Comparable relationshipClassName : numberRecentRelationshipsReport.keySet())
        {
            for (String agentId : agentsAlive)
            {
                if (numberRecentRelationshipsReport.get(relationshipClassName).keySet().contains(agentId))
                    newRelationships = numberRecentRelationshipsReport.get(relationshipClassName).get(agentId);
                else
                    newRelationships = 0 ;

                recentRelationshipsReport.put(relationshipClassName,
                        INCREMENT_HASHMAP(newRelationships,recentRelationshipsReport.get(relationshipClassName))) ;
            }
            
            HashMap<Object,Number> numberRelationshipsReport = (HashMap<Object,Number>) recentRelationshipsReport.get(relationshipClassName).clone() ;
            for (Object numberKey : numberRelationshipsReport.keySet())
                numberRelationshipsReport.put(numberKey, numberRelationshipsReport.get(numberKey).doubleValue()/agentsAlive.size()) ;
            recentRelationshipsReport.put(relationshipClassName, (HashMap<Comparable,Number>) numberRelationshipsReport.clone()) ;
        }
          
        return recentRelationshipsReport ;
    }
        
    private HashMap<Comparable,HashMap<Object,Integer>> 
        prepareNumberRecentRelationshipsReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays)
        {
            int endCycle = getMaxCycles() ;
            
            return prepareNumberRecentRelationshipsReport(relationshipClassNames, backYears, backMonths, backDays, endCycle) ;
        }
    
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) relationshipClassName maps to 
     * (agentIds maps to number of new Relationships in given time).
     */
    private HashMap<Comparable,HashMap<Object,Integer>> 
        prepareNumberRecentRelationshipsReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle)
    {
        HashMap<Comparable,HashMap<Object,Integer>> numberRecentRelationshipsReport = new HashMap<Comparable,HashMap<Object,Integer>>() ;
        for (String relationshipClassName : relationshipClassNames)
            numberRecentRelationshipsReport.put(relationshipClassName, new HashMap<Object,Integer>()) ;
        numberRecentRelationshipsReport.put(TOTAL, new HashMap<Object,Integer>()) ;
        
        // Each relationshipId is a HashMap indicating new relationshipIds for relevant (key) Agents
        ArrayList<HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>> agentsEnteredRelationshipReport 
                = prepareAgentsEnteredRelationshipReport(relationshipClassNames,backYears,backMonths,backDays, endCycle) ;
        int newRelationships ;
                
        for (HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> record : agentsEnteredRelationshipReport)
            for (Comparable relationshipClassName : record.keySet())
                for (Object agentId : record.get(relationshipClassName).keySet())
                {
                    newRelationships = record.get(relationshipClassName).get(agentId).size();
                    numberRecentRelationshipsReport.put(relationshipClassName,
                            UPDATE_HASHMAP(agentId,newRelationships,numberRecentRelationshipsReport.get(relationshipClassName))) ;
                    numberRecentRelationshipsReport.put(TOTAL,
                            UPDATE_HASHMAP(agentId,newRelationships,numberRecentRelationshipsReport.get(TOTAL))) ;
                }
            
        return numberRecentRelationshipsReport ;
    }
    
    /**
     * 
     * @return (HashMap) indicating breakup of relationshipIds for relevant (key) agentIds
     */
    private HashMap<Comparable,ArrayList<Comparable>> prepareAgentsBreakupRelationshipReport(int backYears, int backMonths, int backDays)
    {
        HashMap<Comparable,ArrayList<Comparable>> agentsBreakupRelationshipRecord 
                = new HashMap<Comparable,ArrayList<Comparable>>() ;
            
        ArrayList<String> breakupReport = (ArrayList<String>) getReport("breakup",this) ; //  prepareBreakupReport() ;
        
        for (String record : breakupReport)
        {
            ArrayList<String> relationshipIdArray = EXTRACT_ARRAYLIST(record,RELATIONSHIPID) ;
            for (String relationshipString : relationshipIdArray)
            {
                String relationshipIdValue = EXTRACT_VALUE(RELATIONSHIPID,relationshipString) ;
                
                String agentId0Value = EXTRACT_VALUE(AGENTID0,relationshipString) ;
                if (!agentId0Value.isEmpty())
                    agentsBreakupRelationshipRecord = UPDATE_HASHMAP(agentId0Value,relationshipIdValue,agentsBreakupRelationshipRecord,false) ;
                
                String agentId1Value = EXTRACT_VALUE(AGENTID1,relationshipString) ;
                if (!agentId1Value.isEmpty())
                    agentsBreakupRelationshipRecord = UPDATE_HASHMAP(agentId1Value,(Comparable) relationshipIdValue,agentsBreakupRelationshipRecord,false) ;
            }
        }
        return agentsBreakupRelationshipRecord ;
    }
    
    /**
     * 
     * @return Report with breakup-relevant information from input records.
     */
    protected ArrayList<String> prepareBreakupReport()
    {
        ArrayList<String> breakupReport = new ArrayList<String>() ;
        
        String record ;
        
        // Loop through records in saved files
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
    protected ArrayList<String> prepareCommenceReport()
    {
        ArrayList<String> commenceReport = new ArrayList<String>() ;
        // LOGGER.info("prepareCommenceReport");
        //Include burn-in Relationships
        //ArrayList<String> inputString = new ArrayList<String>() ;
        //LOGGER.info(Relationship.BURNIN_COMMENCE) ;
        
        String record ;
        
        record = prepareBurninRecord() ;    // "0," + 
        /*if (record.length() > 2)
        {
            record += inputString.get(0).substring(2) ; // Leave out the "0," from the report
            commenceReport.add(record) ;
            //inputString.set(0, record) ;
        }*/
        //    inputString.add(relationshipId + "clear:") ;
        

        //for (int reportNb = 0 ; reportNb < inputString.size() ; reportNb += outputCycle )
        // Read in Relationship commencements from simulation.
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
            for (String inputRecord : input)
            {
            //inputString.add(inputRecord) ;
                //record = inputString.get(reportNb) ;
                int relationshipIdIndex = INDEX_OF_PROPERTY(RELATIONSHIPID,inputRecord) ;
                int clearIndex = INDEX_OF_PROPERTY("clear",inputRecord) ;
                if (relationshipIdIndex >= 0 && (relationshipIdIndex < clearIndex)) 
                    record = inputRecord.substring(relationshipIdIndex,clearIndex) + record ;
                else 
                    LOGGER.warning("No Relationships commenced in record " + inputRecord) ;
                commenceReport.add(record) ;
                record = "" ;
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
        ArrayList<String> burninCommenceList ;
        ArrayList<Comparable> burninBreakup ;
        String relationshipId ;
        
        // Get relationshipIds commenced during burn-in
        if (burninBreakupStatic.isEmpty())    // If nothing in Relationship.BURNIN_COMMENCE
        {
            burninCommenceStatic = getMetaDatum("Relationship.BURNIN_COMMENCE") ;
            burninBreakupStatic = getMetaDatum("Relationship.BURNIN_BREAKUP") ;
        }
        // else
        //     LOGGER.info(burninBreakupStatic) ;
        burninCommenceList = EXTRACT_ARRAYLIST(burninCommenceStatic,RELATIONSHIPID) ;
        burninBreakup = EXTRACT_ALL_VALUES(RELATIONSHIPID,burninBreakupStatic,0) ;
        
        for (String relationshipEntry : burninCommenceList)
        {
            relationshipId = EXTRACT_VALUE(RELATIONSHIPID,relationshipEntry) ;
            if (!burninBreakup.contains(relationshipId))
                burninCommence += relationshipEntry ;
        }
        
        //burninCommence += "clear:" ;
        return burninCommence ;
    }
    
}
