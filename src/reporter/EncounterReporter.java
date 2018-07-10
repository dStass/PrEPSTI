/**
 * 
 */
package reporter ;

import community.* ;

/**
* @author Michael Walker
*/


import java.io.* ;
import java.util.Arrays ;
import java.util.ArrayList ;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.HashMap ;
import java.util.logging.Level;


public class EncounterReporter extends Reporter {
    
    /** String representation of "contact". */
    static String CONTACT = "contact" ;

    static String TRANSMISSION = "transmission" ;

    public EncounterReporter(String simname, ArrayList<String> report) {
        super(simname, report);
        // TODO Auto-generated constructor stub
    }

    public EncounterReporter(String simname, String fileName)
    {        
        fileName = "EncounterReport" + Community.NAME_ROOT + ".txt" ;  // Community.FILE_PATH + 
        Reader reader = new Reader(simname,fileName) ;
        input = reader.getFiledReport() ;
    }
    // Was hiding field in Reporter. May yet delete there
    //ArrayList<String> input ;
		
    // Was hiding field in Reporter. May yet delete there
    // Output report
    //ArrayList<?> output ;

            // File paths
            private String logFilePath ;
            private String errorFilePath ;
            private String outputFilePath ;

            private String globalFolder ;

            // File objects
            File logFile ;
            File errorFile ;
            File outputFile ;

        // Logger
        //java.util.logging.Logger logger = java.util.logging.Logger.getLogger("reporter") ;

    /**
     * Cycles through encounterReports, finding the agent pairs and storing HashTable 
     * of each agent's partners for each cycle
     *  
     * @return ArrayList<HashMap>
     */
    public ArrayList<HashMap<Object,ArrayList<Object>>> preparePartnersReport()
    {
            ArrayList<HashMap<Object,ArrayList<Object>>> partnersReport  
                    = new ArrayList<HashMap<Object,ArrayList<Object>>>() ;
            ArrayList<String[]> pairArray ;
            
            for (String record : input)
            {
                partnersReport = new ArrayList<HashMap<Object,ArrayList<Object>>>() ;
                pairArray = reportAgentIdPairs(record) ;
                partnersReport.add(agentPartners(pairArray)) ;
            }
            return partnersReport ;
    }

    /**
     * 
     * @return String[] report of sexual contacts where STI transmission occurred
     */    
    public ArrayList<String> prepareTransmissionReport()
    {
        ArrayList<String> transmissionReport = new ArrayList<String>() ;

        String record ;

        for (int reportNb = 0 ; reportNb < input.size() ; reportNb += outputCycle )
        {
            record = input.get(reportNb) ;
            //LOGGER.log(Level.INFO, "prepare: {0}", record) ;
            transmissionReport.add(encounterByValue("transmission","true",record)) ;
        }
        return transmissionReport ;
    }
    
    /**
     * 
     * @return (ArrayList<String>) The number of transmissions in each cycle.
     */
    public ArrayList<Object> prepareTransmissionCountReport()
    {
        ArrayList<Object> nbTransmissions = new ArrayList<Object>() ;
        
        for (String record : input)
        {
            int[] incidence = countValueIncidence("transmission", TRUE, record, 0) ;
            nbTransmissions.add("transmission:" + String.valueOf(incidence[0])) ;
        }
        return nbTransmissions ;
    }
    
    /**
     * 
     * @param sortedReport
     * @return Report of Transmissions per cycle to Agents sorted in sortedReport.
     */
    public ArrayList<ArrayList<Object>> prepareReceiveCountReport( HashMap<Object,HashMap<Object,ArrayList<Object>>> sortedReport )
    {
        ArrayList<ArrayList<Object>> nbTransmissions = new ArrayList<ArrayList<Object>>() ;
        
        Object[] objectKeys = sortedReport.keySet().toArray() ;
        Integer[] sortedKeys = new Integer[objectKeys.length] ;
        
        for (int keyIndex = 0; keyIndex < objectKeys.length ; keyIndex++)
            sortedKeys[keyIndex] = (Integer) objectKeys[keyIndex] ;
        Arrays.sort(sortedKeys) ;
        
        LOGGER.log(Level.INFO, "nb sorted keys: {0}", objectKeys.length);
        //for (int key : sortedKeys )
        for (int key = 0 ; key <= sortedKeys[sortedKeys.length-1] ; key++)
        {
            ArrayList<Object> recordArray = new ArrayList<Object>() ;
            if (sortedReport.keySet().contains(key))
            {
                HashMap<Object,ArrayList<Object>> cycleHashMap = sortedReport.get(key) ;
                //count = 0;
                for ( ArrayList<Object> value : cycleHashMap.values() )
                    recordArray.add(value) ;
            }
            else
                recordArray.add("0") ;
            
            nbTransmissions.add(recordArray) ;
        }
        return nbTransmissions ;
            
        
    }
    
    /**
     * TODO: Replace ArrayList with set.
     * @return (HashMap) key is the transmitting agentId and entries are receiving agentIds
     */
    public HashMap<Object,ArrayList<Object>> prepareAgentToAgentRecord()
    {
        HashMap<Object,ArrayList<Object>> transmissionRecord = new HashMap<Object,ArrayList<Object>>() ;
        
        // Only consider contacts where transmission occurred
        ArrayList<String> transmissionReport = prepareTransmissionReport() ;
        int encounterIndex ;
        int contactIndex ;
        String contact ;
        String[] agentIdPair ;
        int spaceIndex ;
        int trueIndex ;
        int falseIndex ;
                
        // Check each record (cycle) in transmissionReport
        for (String record : transmissionReport)
        {
            encounterIndex = record.indexOf("agentId0");
            while (encounterIndex >= 0)
            {
                String encounterString = extractEncounter(record,encounterIndex) ;
                agentIdPair = extractAgentIds(encounterString,0);
                
                // Check each sexual contact 
                contactIndex = encounterString.indexOf(CONTACT);
                while (contactIndex >= 0)
                {
                    contact = extractContact(encounterString,contactIndex) ;
                    
                    // Skip number of contact
                    spaceIndex = contact.indexOf(" ");

                    trueIndex = contact.indexOf("1",spaceIndex);
                    falseIndex = contact.indexOf("0",spaceIndex);
                    if (trueIndex < falseIndex)    
                        Reporter.updateHashMap(Integer.valueOf(agentIdPair[0]), Integer.valueOf(agentIdPair[1]), transmissionRecord) ;
                    else    // falseIndex < trueIndex
                        Reporter.updateHashMap(Integer.valueOf(agentIdPair[1]), Integer.valueOf(agentIdPair[0]), transmissionRecord) ;
                    contactIndex = encounterString.indexOf(CONTACT,contactIndex+1);
                }
                encounterIndex = record.indexOf("agentId0",encounterIndex+1) ;
            }
        }
        return transmissionRecord ;
    }
    
    /**
     * 
     * @return (HashMap) report of which Agent infected which other Agents in 
     * which cycle.
     */
    public HashMap<Object,HashMap<Object,ArrayList<Object>>> prepareAgentToAgentReport()
    {
        HashMap<Object,HashMap<Object,ArrayList<Object>>> objectReport = 
                            new HashMap<Object,HashMap<Object,ArrayList<Object>>>() ;
        
        
        // Only consider contacts where transmission occurred
        ArrayList<String> transmissionReport = prepareTransmissionReport() ;
        
        int encounterIndex ;
        int contactIndex ;
        String[] agentIdPair ;
        int spaceIndex ;
        int trueIndex ;
        int falseIndex ;
                
        // Check each record (cycle) in transmissionReport
        for (int cycle = 0 ; cycle < transmissionReport.size(); cycle++ )
        {
            String record = transmissionReport.get(cycle) ;
            encounterIndex = record.indexOf("agentId0");
            while (encounterIndex >= 0)
            {
                String encounterString = extractEncounter(record,encounterIndex) ;
                agentIdPair = extractAgentIds(encounterString,0);
                
                // Check each sexual contact 
                contactIndex = encounterString.indexOf(CONTACT);
                while (contactIndex >= 0)
                {
                    String contact = extractContact(encounterString,contactIndex) ;
                    
                    // Skip number of contact
                    spaceIndex = contact.indexOf(" ");

                    trueIndex = contact.indexOf("1",spaceIndex);
                    falseIndex = contact.indexOf("0",spaceIndex);
                    if (trueIndex < falseIndex)    
                    {
                        objectReport = Reporter.updateHashMap(Integer.valueOf(agentIdPair[0]), Integer.valueOf(agentIdPair[1]), cycle, objectReport) ;
                        break ;
                    }
                    else    // falseIndex < trueIndex
                    {
                        objectReport = Reporter.updateHashMap(Integer.valueOf(agentIdPair[1]), Integer.valueOf(agentIdPair[0]), cycle, objectReport) ;
                        break ;
                    }
                    //contactIndex = encounterString.indexOf(CONTACT,contactIndex+1);
                }
                encounterIndex = record.indexOf("agentId0",encounterIndex+1) ;
            }
        }
        //return hashMapHashMapNumber(objectReport) ;
        return objectReport ;
    }
    
    /**
     * This method makes use of agentToAgentReport mapping agentId0 to agentId1 
     * to Array of cycles in which transmission occurred.
     * @return HashMap agentId to number of times Agent transmitted disease.
     */
    public HashMap<Object,Integer> prepareAgentTransmissionCountReport()
    {
        HashMap<Object,Integer> agentTransmissionCountReport 
                = new HashMap<Object,Integer>() ;
        
        HashMap<Object,HashMap<Object,ArrayList<Object>>> agentToAgentReport 
                = prepareAgentToAgentReport() ;
        
        HashMap<Object,ArrayList<Object>> agentToAgentRecord = new HashMap<Object,ArrayList<Object>>() ;
        
        for (Object agentId : agentToAgentReport.keySet())
        {
            int agentTotal = 0 ;
            agentToAgentRecord = agentToAgentReport.get(agentId) ;
            for (Object agentKey : agentToAgentRecord.keySet()) 
                agentTotal += agentToAgentRecord.get(agentKey).size() ;
            agentTransmissionCountReport.put(agentId, agentTotal) ;
        }
        return agentTransmissionCountReport ;
    }
		
    /**
     * @param siteNames (String[]) names of body sites in sexual contact
     * @return String[] report of sexual contacts where STI transmission occurred
     */    
    public HashMap<Object,Integer> prepareFromSiteToSiteReport(String siteNames[])
    {
        // Output HashMap
        HashMap<Object,Integer> fromSiteToSiteReport = new HashMap<Object,Integer>() ;
        int contactIndex ;
        
        // String describing sexual contact
        String contactString ;
        // name of transmitting site 
        String fromName ;
        // name of site receiving infection
        String toName ;
        // HashMap key
        String key ;
        // values of site0, site1
        String value0 ;
        String value1 ;
        // report String
        String report ;
        
        ArrayList<String> transmissionReport = prepareTransmissionReport() ;

        // Cycle through reports
        for (int reportNb = 0 ; reportNb < transmissionReport.size() ; reportNb += outputCycle )
        {
            report = transmissionReport.get(reportNb) ;
            // Extract contacts in which transmission occurs
            //report = encounterByValue("transmission","true",report) ;
            contactIndex = 0 ;
            // Cycle through contact: substrings, noting fromSite, toSite where 
            // transmission occurs
            //LOGGER.info(report);
            while (contactIndex >= 0)
            {
                contactString = extractBoundedString("contact",report,contactIndex) ;
                
                // This reset is needed here
                fromName = "" ;
                toName = "" ;
                
                //Cycle through all possible site names
                for (String name0 : siteNames)
                {
                    // ! site name in contactString
                    if (! contactString.contains(name0))
                        continue ;
                    //nameIndex = Reporter.indexOfProperty(name0,contactString) ;
                    value0 = extractValue(name0,contactString) ;
                    
                    for (String name1 : siteNames)
                    {
                        // nameIndex+1 because both Sites might have the same name
                        //nameIndex = Reporter.indexOfProperty(name0,nameIndex+1,contactString) ;
                        if (! contactString.contains(name1) || name1.equals(name0))
                            continue ;
                        // Assign toName and fromName
                        if ("0".equals(value0))
                        {
                            toName = name0 ;
                            fromName = name1 ;
                        }
                        else
                        {
                            toName = name1 ;
                            fromName = name0 ;
                        }
                        
                        break ;
                    }
                    if ("".equals(toName))  // identical sites in transmission
                    {
                        toName = name0 ;
                        fromName = name0 ;
                    }
                
                    key = fromName + " to " + toName ;
                    fromSiteToSiteReport = incrementHashMap(key,fromSiteToSiteReport) ;
                    break ;
                }
                contactIndex = indexOfProperty("contact",contactIndex+1,report) ;
            }
        }
        return fromSiteToSiteReport ;
    }
		
    /**
     * From ArrayList of doublets puts agentIds into HashMap whose keys are the agentIds
     * and values are arrays of their partners Ids.
     * @param pairArray ArrayList<String[]> of agentId doublets indicating sexual encounters 
     * @return partnerMap - HashMap indicating partnerIds of each agent (key: agentId)
     */
    private HashMap<Object,ArrayList<Object>> agentPartners(ArrayList<String[]> pairArray)
    {
        HashMap<Object,ArrayList<Object>> partnerMap = new HashMap<Object,ArrayList<Object>>() ;
        String agentNb0 ;
        String agentNb1 ;
        for (String[] pairString : pairArray)
        { 
            agentNb0 = pairString[0] ;
            agentNb1 = pairString[1] ;

            updateHashMap(agentNb0,agentNb1,partnerMap) ;
            updateHashMap(agentNb1,agentNb0,partnerMap) ;

        }
        return partnerMap ;
    }

    /**
     * Extracts pairs of agentId0, agentId1 corresponding to relationships
     * @param report
     * @return ArrayList of String doublets
     */
    private ArrayList<String[]> reportAgentIdPairs(String record)
    {
        ArrayList<String[]> agentIdPairs = new ArrayList<String[]>() ;
        for (int startIndex = 0 ; startIndex != -1; startIndex = indexOfProperty("agentId0", startIndex + 1, record))
        {
            String[] agentIdPair = extractAgentIds(record,startIndex) ;
            agentIdPairs.add(agentIdPair) ;

            // Find startIndex for next loop
            //startIndex = indexOfProperty("agentId0", startIndex + 1,report) ;
        }
        return agentIdPairs ;
    }

    /**
     * Extracts encounter reports where propertyName.value = value for at least 
     * one contact
     * @param propertyName
     * @param value
     * @param report
     * @return 
     */
    private String encounterByValue(String propertyName, String value, String record)
    {
        int indexStart = 0 ;
        int indexContact ;
        String methodOutput = "" ;
        String encounterString ;
        String contactString ;
        while (indexStart >= 0)
        {
            // Encounter to study
            encounterString = extractEncounter(record, indexStart) ;

            // Prepare for next loop
            indexStart = indexOfProperty("agentId0",indexStart+1, record) ;
            
            // Initialise output for encounter
            indexContact = indexOfProperty("contact",encounterString) ;
            
            // Skip to next loop if no contact
            if (indexContact < 0)
                continue ;
            
            String encounterOpening = encounterString.substring(0, indexContact ) ;
            String encounterOutput = "" ;

            // check contacts for desired value of propertyName
            while (indexContact >= 0)
            {
                contactString = boundedStringByValue(propertyName,value,"contact",encounterString) ; 
                // if contactString contains actual contact information
                if (!"".equals(contactString)) 
                {
                        encounterOutput += contactString ;
                }
                // Find next contact
                indexContact = indexOfProperty("contact", indexContact + 1, encounterString) ;
            }
            // Only include encounter in output if any of its contacts are included 
            if (encounterOutput.length() > 0)
                methodOutput += encounterOpening + encounterOutput ; // Include agentId

        }
        // If no positive cases are returned
        if ("".equalsIgnoreCase(methodOutput)) 
            methodOutput = "None" ;

        return methodOutput ;
    }
    
    /**
     * Extracts encounter reports containing propertyName for at least 
     * one contact
     * @param propertyName
     * @param value
     * @param report
     * @return 
     */
    private String encounterByContents(String propertyName, String report)
    {
        int indexStart = 0 ;
        int indexContact ;
        String methodOutput = "" ;
        String encounterString ;
        String contactString ;
        while (indexStart >= 0)
        {
            // Encounter to study
            encounterString = extractEncounter(report, indexStart) ;

            // Initialise output for encounter
            indexContact = indexOfProperty("contact",encounterString) ;
            String encounterOpening = encounterString.substring(0, indexContact ) ;
            String encounterOutput = "" ;

            // check contacts for desired value of propertyName
            while (indexContact >= 0)
            {
                contactString = boundedStringByContents(propertyName,"contact",encounterString) ; 
                // if contactString contains actual contact information
                if (!"".equals(contactString)) 
                {
                        encounterOutput += contactString ;
                }
                // Find next contact
                indexContact = indexOfProperty("contact", indexContact + 1, encounterString) ;
            }
            // Only include encounter in output if any of its contacts are included 
            if (encounterOutput.length() > 0)
                methodOutput += encounterOpening + encounterOutput ; // Include agentId

            // Prepare for next loop
            indexStart = indexOfProperty("agentId0",indexStart+1, report) ;
        }
        // If no positive cases are returned
        if ("".equalsIgnoreCase(methodOutput)) 
            methodOutput = "None" ;

        return methodOutput ;
    }
    
    /**
     * 
     * @param String encounter
     * @return substrings corresponding to individual sexual contacts within String encounter
     */
    private String extractContact(String encounter, int indexStart)
    {
        return extractBoundedString("contact", encounter, indexStart) ;
    }

    /**
     * 
     * @param String string
     * @return substrings corresponding to encounters
     */
    private String extractEncounter(String string, int indexStart)
    {
        return extractBoundedString("agentId0", string, indexStart) ;
    }

    /**
     * 
     * @param report
     * @param startIndex
     * @return String[] pairs of agentIds corresponding to relationships described in report
     */
    private String[] extractAgentIds(String report, int startIndex)
    {
            String agentId0 = extractValue("agentId0", report, startIndex) ;

            startIndex = indexOfProperty("agentId1", startIndex, report) ;
            String agentId1 = extractValue("agentId1", report, startIndex) ;
            return new String[] {agentId0,agentId1} ;
    }

}
