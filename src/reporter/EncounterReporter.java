/**
 * 
 */
package reporter ;

/**
* @author Michael Walker
*/


import java.io.* ;
import java.util.ArrayList ;
import java.util.HashMap ;

//import java.util.logging.* ;
import java.util.logging.Level;

public class EncounterReporter extends Reporter {

    public EncounterReporter(String simname, ArrayList<String> reports) {
        super(simname, reports);
        // TODO Auto-generated constructor stub
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
    public ArrayList<HashMap<Integer,ArrayList<Integer>>> preparePartnersReport()
    {
            ArrayList<HashMap<Integer,ArrayList<Integer>>> partnersReport = 
                            new ArrayList<HashMap<Integer,ArrayList<Integer>>>() ;
            ArrayList<String[]> pairArray ;
            String report ;
            for (int reportNb = 0 ; reportNb < input.size() ; reportNb += outputCycle )
            {
                    report = input.get(reportNb) ;
                    LOGGER.log(Level.INFO, "prepare: {0}", report);
                    pairArray = reportAgentIdPairs(report) ;
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

        String report ;

        for (int reportNb = 0 ; reportNb < input.size() ; reportNb += outputCycle )
        {
            report = input.get(reportNb) ;
            LOGGER.log(Level.INFO, "prepare: {0}", report) ;
            transmissionReport.add(encounterByValue("transmission","true",report)) ;
        }
        return transmissionReport ;
    }
		
    /**
     * From ArrayList of doublets puts agentIds into HashMap whose keys are the agentIds
     * and values are arrays of their partners Ids.
     * @param pairArray ArrayList<String[]> of agentId doublets indicating sexual encounters 
     * @return partnerMap - HashMap indicating partnerIds of each agent (key: agentId)
     */
    private HashMap<Integer,ArrayList<Integer>> agentPartners(ArrayList<String[]> pairArray)
    {
        HashMap<Integer,ArrayList<Integer>> partnerMap = new HashMap<Integer,ArrayList<Integer>>() ;
        int agentNb0 ;
        int agentNb1 ;
        for (String[] pairString : pairArray)
        { 
            agentNb0 = Integer.parseInt(pairString[0]) ;
            agentNb1 = Integer.parseInt(pairString[1]) ;

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
    private ArrayList<String[]> reportAgentIdPairs(String report)
    {
        LOGGER.info(report);
        ArrayList<String[]> agentIdPairs = new ArrayList<String[]>() ;
        for (int startIndex = 0 ; startIndex != -1; startIndex = report.indexOf("agentId0", startIndex + 1))
        {
            String[] agentIdPair = extractAgentIds(report,startIndex) ;
            agentIdPairs.add(agentIdPair) ;

            // Find startIndex for next loop
            //startIndex = report.indexOf("agentId0", startIndex + 1) ;
        }
        return agentIdPairs ;
    }

    /**
     * Extracts contact reports where propertyName.value = value
     * @param propertyName
     * @param value
     * @param report
     * @return 
     */
    private String encounterByValue(String propertyName, String value, String report)
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
                    indexContact = encounterString.indexOf("contact:", indexStart) ;
                    String encounterOpening = encounterString.substring(0, indexContact ) ;
                    String encounterOutput = "" ;

                    // check contacts for desired value of propertyName
                    while (indexContact >= 0)
                    {
                            contactString = boundedStringByValue(propertyName,value,"contact:",encounterString) ; 
                            // if contactString contains actual contact information
                            if (!"contact:".equals(contactString)) 
                            {
                                    encounterOutput += contactString ;
                            }
                            // Find next contact
                            indexContact = encounterString.indexOf("contact:", indexContact + 1) ;
                    }
                    // Only include encounter in output if any of its contacts are included 
                    if (encounterOutput.length() > 0)
                            methodOutput += encounterOpening + encounterOutput ; // Include agentId

                    // Prepare for next loop
                    indexStart = report.indexOf("agentId0:",indexStart+1) ;
            }
            // If no positive cases are returned
            if ("".equalsIgnoreCase(methodOutput)) methodOutput = "None" ;

            return methodOutput ;
    }

        
	    /**
		 * 
		 * @param String encounter
		 * @return substrings corresponding to individual sexual contacts within String encounter
		 */
		private String extractContact(String encounter, int indexStart)
		{
			return extractBoundedString(encounter, "contact:", indexStart) ;
		}
		
		/**
		 * 
		 * @param String string
		 * @return substrings corresponding to encounters
		 */
		private String extractEncounter(String string, int indexStart)
		{
			return extractBoundedString(string, "agentId0:", indexStart) ;
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
			
			startIndex = report.indexOf("agentId1", startIndex) ;
			String agentId1 = extractValue("agentId1", report, startIndex) ;
			return new String[] {agentId0,agentId1} ;
		}

}
