/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter;

import community.Community ;
import community.Relationship ;

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
    
    public RelationshipReporter(String simname, ArrayList<String> report) 
    {
        super(simname, report);
    }

    /**
     * FIXME: Passing and implementation of fileName not finalised.
     * @param simName
     * @param fileName 
     */
    public RelationshipReporter(String simName, String fileName)
    {
        fileName = "RelationshipReport" + Community.NAME_ROOT + ".txt" ; // Community.FILE_PATH + 
        System.out.println(fileName);
        Reader reader = new Reader(simName,fileName) ;
        input = reader.getFiledReport() ;
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
        
        for (int reportNb = 0 ; reportNb < commenceReport.size() ; reportNb++ )
        {
            String record = commenceReport.get(reportNb) ;
            //LOGGER.info(record);
            //int startIndex = indexOfProperty(RELATIONSHIP_ID,report) ;
            relationshipCommenceReport.add(extractAllValues(RELATIONSHIP_ID, record,0)) ;
            //relationshipCommenceReport.add(extractAllValues(AGENTID1, report,0)) ;
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
            //relationshipBreakupReport.add(extractAllValues(AGENTID1, report,0)) ;
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
        
        for (int reportNb = 0 ; reportNb < commenceReport.size() ; reportNb++ )
        {
            String record = commenceReport.get(reportNb) ;
            //LOGGER.info(record);
            //int startIndex = indexOfProperty(RELATIONSHIP_ID,report) ;
            agentCommenceReport.add(extractAllValues(AGENTID0,record,0)) ;
            agentCommenceReport.add(extractAllValues(AGENTID1,record,0)) ;
        }
        return agentCommenceReport ;
    }
    
    /**
     * 
     * @return ArrayList of ArrayLists of (String) RelationshipIds of relationships
     * that break up in each cycle
     */
    public ArrayList<ArrayList<Object>> prepareAgentBreakupReport()
    {
        ArrayList<ArrayList<Object>> agentBreakupReport = new ArrayList<ArrayList<Object>>() ;
        
        ArrayList<String> breakupReport = prepareBreakupReport() ;
        
        for (int reportNb = 0 ; reportNb < breakupReport.size() ; reportNb++ )
        {
            String report = breakupReport.get(reportNb) ;
            agentBreakupReport.add(extractAllValues(AGENTID0, report,0)) ;
            agentBreakupReport.add(extractAllValues(AGENTID1, report,0)) ;
        }
        return agentBreakupReport ;
    }
    
    
    public HashMap<Object,Integer> prepareLengthAtBreakupReport()
    {
        HashMap<Object,Integer> lengthAtBreakupMap = new HashMap<Object,Integer>() ;
        
        // Contains age-at-death data
        HashMap<Object,Integer> relationshipLengthReport = prepareRelationshipLengthReport() ;
        
        for ( int length : relationshipLengthReport.values())
        {
            LOGGER.log(Level.INFO, "Relationship length:{0} ", new Object[] {length});
            lengthAtBreakupMap = incrementHashMap(length,lengthAtBreakupMap) ;
        }
        //LOGGER.log(Level.INFO, "{0}", lengthAtBreakupMap);
        
        return lengthAtBreakupMap ;
    }
    
    /**
     * 
     * @return (HashMap) key is String.valueOf(length) and value is the number to
     * die relationships of that length
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
        for (int index = 0 ; index < (relationshipBreakupReport.size() - 1 ) ; index++ )
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
            if (relationshipLengthMap.get(relationshipId) < 0)
            {
                int newValue = relationshipLengthMap.get(relationshipId) + Community.MAX_CYCLES ;
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
    public HashMap<Object,Integer> prepareCumulativeLengthReport()
    {
        HashMap<Object,Integer> cumulativeRelationshipLengthReport = new HashMap<Object,Integer>() ;
        
        //TODO: Separate out action on individual RECORD
        ArrayList<HashMap<Object,Integer>> agentsCumulativeRelationshipReport 
                = prepareAgentsCumulativeRelationshipReport() ;
        
        HashMap<Object,Integer> lengthAtBreakupReport
                = prepareLengthAtBreakupReport() ;
        
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
        for (int lengthKey = maxValue ; lengthKey >= 0 ; lengthKey-- )
        {
            relationshipsUnder += lengthAtBreakupReport.get(lengthKey) ;
            cumulativeRelationshipLengthReport.put(lengthKey, relationshipsUnder) ;
        }
        return cumulativeRelationshipLengthReport ;
    }
    
    /**
     * @return A snapshot of how many agentIds have more had how many or more Relationships
     */
    public HashMap<Object,Integer> prepareCumulativeRelationshipRecord()
    {
        HashMap<Object,Integer> cumulativeRelationshipRecord = new HashMap<Object,Integer>() ;
        
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
    public ArrayList<HashMap<Object,Integer>> prepareAgentsCumulativeRelationshipReport()
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
     * @return (ArrayList) report of mean number of relationships 
     */
    public ArrayList<Object> prepareMeanNumberRelationshipsReport()
    {
        ArrayList<Object> meanNumberRelationshipsReport = new ArrayList<Object>() ;
        
        //ArrayList<Object> populationReport = (new PopulationReporter("",input)).preparePopulationReport() ;
        
        ArrayList<HashMap<Object,Integer>> agentNumberRelationshipsReport
                = prepareAgentNumberRelationshipsReport() ;
        int population = Community.POPULATION ; // = Integer.valueOf(extractValue("Population", (String) populationReport.get(recordIndex))) ;

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
        
        HashMap<Object,Integer> agentBreakupsCount = new HashMap<Object,Integer>() ;
        
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
                
                String agentId0Value = extractValue(AGENTID0,relationshipString) ;
                commenceRelationshipRecord = updateHashMap(agentId0Value,relationshipIdValue,commenceRelationshipRecord) ;
                
                String agentId1Value = extractValue(AGENTID1,relationshipString) ;
                commenceRelationshipRecord = updateHashMap(agentId1Value,relationshipIdValue,commenceRelationshipRecord) ;
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
    
    /**
     * 
     * @return Report with breakup-relevant information from input records.
     */
    private ArrayList<String> prepareBreakupReport()
    {
        ArrayList<String> breakupReport = new ArrayList<String>() ;
        
        String record ;
        int breakupIndex ;
        String valueString ;

        //Include burn-in Relationships
        ArrayList<String> inputString = new ArrayList<String>() ;
        if (!Relationship.BURNIN_BREAKUP.isEmpty())
        {
            inputString.add("clear:") ;
            // Add burn-in breakups to input breakups
            String newInput = input.get(0) + " " + Relationship.BURNIN_BREAKUP.substring(0) ; // Relationship.BURNIN_RECORD.indexOf("clear:")+6)
            input.set(0,newInput) ;
            
            //LOGGER.info(input.get(0));
        }
        for (String inputRecord : input)
            inputString.add(inputRecord) ;

        for (int reportNb = 0 ; reportNb < inputString.size() ; reportNb += outputCycle )
        {
            record = inputString.get(reportNb) ;
            record = record.substring(record.indexOf("clear:")) ;
            //LOGGER.log(Level.INFO, "{0} {1}", new Object[] {reportNb,record});
            
            breakupReport.add(record) ;
        }
        return breakupReport ;
    }
    
    private ArrayList<String> prepareCommenceReport()
    {
        ArrayList<String> commenceReport = new ArrayList<String>() ;
        
        String record ;
        int agentIndex ;
        String valueString ;
        
        //Include burn-in Relationships
        ArrayList<String> inputString = new ArrayList<String>() ;
        if (!Relationship.BURNIN_COMMENCE.equals("clear:"))
            inputString.add(Relationship.BURNIN_COMMENCE) ;
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
    
    
}
