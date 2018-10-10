/**
 * 
 */
package reporter ;

import community.* ;
import site.* ;
/**
* @author Michael Walker
*/


//import java.io.* ;
import java.util.Arrays ;
import java.util.ArrayList ;
import java.util.Collection;
import java.util.Collections;
import java.util.Set ;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.HashMap ;
import java.util.logging.Level;
import static reporter.Reporter.extractArrayList;


public class EncounterReporter extends Reporter {
    
    /** String representation of "contact". */
    static String CONTACT = "contact" ;

    static String TRANSMISSION = "transmission" ;
    static String CONDOM = "condom" ;
    static String RECTUM = "Rectum" ;
    static String URETHRA = "Urethra" ;

    public EncounterReporter()
    {
        
    }
    
    public EncounterReporter(String simname, ArrayList<String> report) {
        super(simname, report);
        // TODO Auto-generated constructor stub
    }

    public EncounterReporter(String simName, String reportFilePath)
    {        
        super(simName, reportFilePath) ;
    }

        // Logger
        //java.util.logging.Logger logger = java.util.logging.Logger.getLogger("reporter") ;

    /**
     * 
     * @return String[] report of sexual contacts where STI transmission occurred
     */    
    public ArrayList<String> prepareTransmissionReport()
    {
        ArrayList<String> transmissionReport = new ArrayList<String>() ;

        String record ;

        for (boolean nextInput = true ; nextInput ; nextInput = updateReport())
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
     * @return (ArrayList) The number of transmissions in each cycle.
     */
    public ArrayList<Object> prepareTransmissionCountReport()
    {
        ArrayList<Object> nbTransmissions = new ArrayList<Object>() ;
        
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
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
        
        // Put keys of sortedReport in order
        Object[] objectKeys = sortedReport.keySet().toArray() ;
        Integer[] sortedKeys = new Integer[objectKeys.length] ;
        
        for (int keyIndex = 0; keyIndex < objectKeys.length ; keyIndex++)
            sortedKeys[keyIndex] = (Integer) objectKeys[keyIndex] ;
        Arrays.sort(sortedKeys) ;
        
        // Loop through keys
        for (int key = 0 ; key <= sortedKeys[sortedKeys.length-1] ; key++)
        {
            ArrayList<Object> recordArray = new ArrayList<Object>() ;
            if (sortedReport.keySet().contains(key))
            {
                HashMap<Object,ArrayList<Object>> cycleHashMap = sortedReport.get(key) ;
                //count = 0;
                for ( ArrayList<Object> value : cycleHashMap.values() )
                    recordArray.addAll(value) ;
            }
            else
                recordArray.add("0") ;
            
            nbTransmissions.add(recordArray) ;
        }
        return nbTransmissions ;
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
     * @return HashMap sorting values maps to correspondingTransmissionReport
     */
    public HashMap<Object,HashMap<Object,HashMap<Object,ArrayList<Object>>>> 
        prepareReceiveSortPrepStatusReport(String[] values )
    {
        LOGGER.info("prepareAgentToAgentReport()") ;
        HashMap<Object,HashMap<Object,ArrayList<Object>>> transmissionReport = prepareAgentToAgentReport() ;
        //LOGGER.log(Level.INFO, "{0}", transmissionReport);
        LOGGER.info("sortPrepStatus()");
        PopulationReporter populationReporter = new PopulationReporter(getMetaDatum("Community.NAME_ROOT"), getFolderPath()); 
        HashMap<Object,ArrayList<Object>> sortingReport = populationReporter.sortPrepStatus() ;
        LOGGER.log(Level.INFO, "{0}", sortingReport);
        
        LOGGER.info("sortReport()");
        //String[] values = new String[] {TRUE, FALSE} ;
        return Reporter.sortReport(transmissionReport, sortingReport, values) ;
    }
        
    /**
     * The number of Agents who have always, or not always, used a condom during anal
     * sex, or never had anal sex in the given time period.
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param relationshipClazzNames
     * @return 
     */
    public HashMap<Object,Number[]> prepareNumberCondomlessReport(int backYears, int backMonths, int backDays, String[] relationshipClazzNames)
    {
        HashMap<Object,Number[]> numberCondomlessReport = new HashMap<Object,Number[]>() ;
        String[] condomStati = new String[] {"always","not_always","no_AI"} ;
        for (String status : condomStati)
            numberCondomlessReport.put(status, new Number[relationshipClazzNames.length]) ;
        
        int statusIndex ;
        
        HashMap<String,HashMap<Object,ArrayList<Object>>> agentAnalIntercourseReport ;
        
        // Prepare agentRelationshipsRecord
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        Class[] parameterClazzes = new Class[] {String[].class,int.class,int.class,int.class} ;
        Object[] parameters = new Object[] {relationshipClazzNames, backYears, backMonths, backDays} ;
        HashMap<Object,HashMap<Object,ArrayList<Object>>> agentRelationshipsRecord 
                = (HashMap<Object,HashMap<Object,ArrayList<Object>>>) getRecord("agentRelationships",relationshipReporter,parameterClazzes,parameters) ;
        //relationshipReporter.prepareAgentRelationshipsRecord(relationshipClazzNames, backYears, backMonths, backDays) ;
        
        String relationshipClazz ;
        String condomStatus ;
        double agentsInvolved ;
        for (int relationshipClazzIndex = 0 ; relationshipClazzIndex < relationshipClazzNames.length ; relationshipClazzIndex++ )
        {
            relationshipClazz = relationshipClazzNames[relationshipClazzIndex] ;
            agentsInvolved = (double) agentRelationshipsRecord.get(relationshipClazz).keySet().size() ;
            agentAnalIntercourseReport 
                = prepareAgentAnalIntercourseReport(backYears, backMonths, backDays, relationshipClazz) ;
            // Those agentIds who had anal intercourse but did not always use condoms.
            Set withoutCondomSet = agentAnalIntercourseReport.get(FALSE).keySet() ;
        
            // Those agentIds who had anal intercourse and always used condoms
            Set withCondomSet = agentAnalIntercourseReport.get(TRUE).keySet() ;
            withCondomSet.removeAll(withoutCondomSet) ;
            int withCondom = withCondomSet.size() ;
            int withoutCondom = withoutCondomSet.size() ;
            
            // Proportion always using condom for anal sex
            statusIndex = 0 ;
            condomStatus = condomStati[statusIndex] ;
            Number[] numberReportEntries 
                = numberCondomlessReport.get(condomStatus) ;
            numberReportEntries[relationshipClazzIndex] = withCondom/agentsInvolved ;
            numberCondomlessReport.put(condomStatus, (Number[]) numberReportEntries.clone()) ;
            // Proportion sometimes having condomless anal sex
            statusIndex = 1 ;
            condomStatus = condomStati[statusIndex] ;
            numberReportEntries 
                = numberCondomlessReport.get(condomStatus) ;
            numberReportEntries[relationshipClazzIndex] = withoutCondom/agentsInvolved ;
            numberCondomlessReport.put(condomStatus, (Number[]) numberReportEntries.clone()) ;
            // Proportion who never had anal sex
            statusIndex = 2 ;
            condomStatus = condomStati[statusIndex] ;
            numberReportEntries 
                = numberCondomlessReport.get(condomStatus) ;
            numberReportEntries[relationshipClazzIndex] = (agentsInvolved - withCondom - withoutCondom)/agentsInvolved ;
            numberCondomlessReport.put(condomStatus, (Number[]) numberReportEntries.clone()) ;
            
        }
        
        return numberCondomlessReport ;
    }
        
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param relationshipClazzName
     * @return (HashMap) agentId maps to (ArrayList) of cycles in which they took 
     * part in anal intercourse.
     */
    public HashMap<String,HashMap<Object,ArrayList<Object>>> prepareAgentAnalIntercourseReport(int backYears, int backMonths, int backDays, String relationshipClazzName)
    {
        HashMap<String,HashMap<Object,ArrayList<Object>>> agentAnalIntercourseReport 
                = new HashMap<String,HashMap<Object,ArrayList<Object>>>() ;
        
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        HashMap<Object,String[]> relationshipAgentReport = (HashMap<Object,String[]>) getReport("relationshipAgent",relationshipReporter) ; //  relationshipReporter.prepareRelationshipAgentReport() ;
        LOGGER.log(Level.INFO, "relationshipAgentReport {0}", relationshipAgentReport);
        
        int maxCycles = Integer.valueOf(getMetaDatum("Community.MAX_CYCLES")) ;
        int backCycles = getBackCycles(backYears, backMonths, backDays, maxCycles) ;
        int startCycle = maxCycles - backCycles ;
        
        ArrayList<String> encounterReport = this.getBackCyclesReport(0, 0, backCycles) ;
        LOGGER.log(Level.INFO, "encounterReport {0}", encounterReport);
        ArrayList<String> relationshipClassReport = relationshipReporter.filterRelationshipClazzReport(relationshipClazzName,encounterReport) ;
        LOGGER.log(Level.INFO, "relationshipClassReport {0}", relationshipClassReport);
        ArrayList<String> intercourseReport = prepareFilteredReport(CONDOM,"",relationshipClassReport) ;
        LOGGER.log(Level.INFO, "intercourseReport {0}", intercourseReport);
        for (String condom : new String[] {TRUE,FALSE})
        {
            agentAnalIntercourseReport.put(condom, new HashMap<Object,ArrayList<Object>>()) ;
            
            // Filter by condom use
            ArrayList<String> condomReport = prepareFilteredReport(CONDOM,condom,intercourseReport) ;

            for (int recordNb = 0 ; recordNb < backCycles ; recordNb++ ) 
            {
                String record = condomReport.get(recordNb) ;
                ArrayList<String> encounters = extractArrayList(record,RELATIONSHIPID) ; 
                for (String encounter : encounters)
                {
                    Object[] agentIds = relationshipAgentReport.get(extractValue(RELATIONSHIPID,encounter)) ;
                    for (Object agentId : agentIds)
                        agentAnalIntercourseReport.put(CONDOM, updateHashMap(agentId,(startCycle + recordNb),agentAnalIntercourseReport.get(CONDOM))) ;
                }
            }
        
        }
        return agentAnalIntercourseReport ;
    }
    
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return (HashMap) Number of condomless intercourse acts maps to the Number
     * of Agents to have committed that many such acts in the last backYears years,
     * backMonths months and backDays days.
     */        
    public HashMap<Object,Number> prepareNumberAgentCondomlessReport(int backYears, int backMonths, int backDays, String relationshipClazzName, String concordanceName, boolean concordant)
    {
        HashMap<Object,Number> numberAgentCondomlessReport = new HashMap<Object,Number>() ;
        
        HashMap<Object,ArrayList<Object>> agentCondomlessReport 
                = prepareAgentCondomlessReport(backYears, backMonths, backDays, relationshipClazzName, concordanceName, concordant) ;
        
        for (ArrayList<Object> condomlessValue : agentCondomlessReport.values() ) 
        {
            int intercourses = condomlessValue.size() ;
            numberAgentCondomlessReport = incrementHashMap(intercourses,numberAgentCondomlessReport) ;
        }
        
        return numberAgentCondomlessReport ;
    }
    
    /**
     * 
     * @return (HashMap) agentId maps to the cycles in which they had condomless 
     * anal intercourse in a (relationshipClazzName) Relationship.
     */
    private HashMap<Object,ArrayList<Object>> prepareAgentCondomlessReport(int backYears, int backMonths, int backDays, String relationshipClazzName)
    {
        return prepareAgentCondomlessReport(backYears, backMonths, backDays, relationshipClazzName, "", true) ;
    }
    
    /**
     * 
     * @return (HashMap) agentId maps to the cycles in which they had condomless 
     * anal intercourse in a (relationshipClazzName) Relationship con/dis-cordant 
     * according to concordanceName.
     */
    private HashMap<Object,ArrayList<Object>> prepareAgentCondomlessReport(int backYears, int backMonths, int backDays, String relationshipClazzName, String concordanceName, boolean concordant)
    {
        HashMap<Object,ArrayList<Object>> agentCondomlessIntercourse = new HashMap<Object,ArrayList<Object>>() ;
        
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        HashMap<Object,String[]> relationshipAgentReport = (HashMap<Object,String[]>) getReport("relationshipAgent",relationshipReporter) ; //  relationshipReporter.prepareRelationshipAgentReport() ;
        
        int maxCycles = Integer.valueOf(getMetaDatum("Community.MAX_CYCLES")) ;
        int backCycles = getBackCycles(backYears, backMonths, backDays, maxCycles) ;
        int startCycle = maxCycles - backCycles ;
        
        ArrayList<String> encounterReport = getBackCyclesReport(0, 0, backCycles) ;
        ArrayList<String> condomlessReport = prepareFilteredReport(CONDOM,FALSE,encounterReport) ;
        ArrayList<String> concordantReport = relationshipReporter.filterByConcordance(concordanceName, concordant, condomlessReport) ;
        ArrayList<String> finalReport = relationshipReporter.filterRelationshipClazzReport(relationshipClazzName,concordantReport) ;
        
        int reportSize = finalReport.size() ;
        for (int recordNb = 0 ; recordNb < reportSize ; recordNb++ ) 
        {
            String record = finalReport.get(recordNb) ;
            ArrayList<String> encounters = extractArrayList(record,RELATIONSHIPID) ; 
            for (String encounter : encounters)
            {
                String[] agentIds = relationshipAgentReport.get(extractValue(RELATIONSHIPID,encounter)) ;
                for (String agentId : agentIds)
                    agentCondomlessIntercourse = updateHashMap(agentId,startCycle + recordNb,agentCondomlessIntercourse) ;
            }
        }
        
        return agentCondomlessIntercourse ;
    }
    
    /**
     * Filters records leaving only those encounters containing propertyName withCondom (String) value.
     * @param propertyName
     * @param value
     * @param fullReport
     * @return 
     */
    private ArrayList<String> prepareFilteredReport(String propertyName, String value, ArrayList<String> fullReport)
    {
        ArrayList<String> filteredReport = new ArrayList<String>() ;
        
        String filteredRecord ;
        
        for (String record : fullReport)
        {
            if (value.isEmpty())
                filteredRecord = encounterByContents(propertyName,record);
            else
                filteredRecord = encounterByValue(propertyName,value,record);
            filteredReport.add(filteredRecord) ;
        }
        
        return filteredReport ;
    }
    
    /**
     * 
     * @return Report of number of condom usages and opportunities for condom use and
     * proportion of opportunities taken.
     */
    public ArrayList<Object> prepareCondomUseReport()
    {
        ArrayList<Object> condomUseReport = new ArrayList<Object>() ;
        int opportunities ;
        int usages ;
        double proportion ;
        int[] condomData ;
        String reportOutput ;
        
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
            for (String record : input)
            {
                condomData = countValueIncidence(CONDOM,TRUE,record,0) ;
                usages = condomData[0] ;
                opportunities = condomData[1] ;
                proportion = ((double) usages)/opportunities ;
                reportOutput = Reporter.addReportProperty("usages", usages) ;
                reportOutput += Reporter.addReportProperty("opportunities", opportunities) ;
                reportOutput += Reporter.addReportProperty("proportion", proportion) ;
                condomUseReport.add(reportOutput) ;
            }
        
        return condomUseReport ;
    }
    
    /**
     * TODO: Implement complete census of Agents.
     * @param census
     * @return (ArrayList) Report on combinations of combinations of condom use
 withCondom either seroSorting or seroPositioning.
     */
    public ArrayList<Object> prepareProtectionReport(ArrayList<String> census)
    {
        ArrayList<Object> protectionReport = new ArrayList<Object>() ;
        String protectionRecord ;
        
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        HashMap<Object,String[]> relationshipAgentReport = (HashMap<Object,String[]>) getReport("relationshipAgent",relationshipReporter) ; //  relationshipReporter.prepareRelationshipAgentReport() ;
        
        int condomOnly ;
        int onlySeroSort ;
        int onlySeroPosition ;
        int condomSeroSort ;
        int condomSeroPosition ;
        int unprotected ;
        int total ;
        boolean finished = false ;
        
        ArrayList<String> encounters ;
        ArrayList<String> contacts ;
        String[] agentIds = new String[2] ;
        
        boolean seroSort ;
        boolean seroPosition ;
        boolean condom ;
        
        // Make census easy to access.
        HashMap<String,String> agentProperties = new HashMap<String,String>() ;
        String agentKey ;
        String agentValue ;
        for (String record : census)
        {
            int agentIndex = 0 ;
            while (agentIndex >= 0)
            {
                agentValue = extractBoundedString(AGENTID,record,agentIndex) ;
                agentKey = extractValue(AGENTID,agentValue) ;
                agentProperties.put(agentKey, agentValue) ;
                agentIndex = record.indexOf(AGENTID,agentIndex + 1) ;
            }
        }
        LOGGER.log(Level.INFO, "{0}", census);
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport())
        {
            // Check each record.
            for (String record : input)
            {
                //LOGGER.info(record) ;
                condomOnly = 0 ;
                onlySeroSort = 0 ;
                onlySeroPosition = 0 ;
                condomSeroSort = 0 ;
                condomSeroPosition = 0 ;
                unprotected = 0 ;
                total = 0 ;

                // get Agent properties for each encounter 
                encounters = extractArrayList(record,RELATIONSHIPID) ;
                for (String encounter : encounters)
                {
                    seroPosition = false ;
                    seroSort = false ;
                    agentIds = relationshipAgentReport.get(extractValue(RELATIONSHIPID,encounter)) ;
                    finished = !(agentProperties.containsKey(agentIds[0]) && agentProperties.containsKey(agentIds[1])) ;
                    for (String agentId : agentIds)
                    {
                        if (!(agentProperties.containsKey(agentId)))
                            LOGGER.info("Missing agentId " + agentId); 
                    
                        if (false && extractValue("seroSort",agentProperties.get(agentId)).equals(TRUE))
                        {
                            seroSort = true ;
                            break ;
                        }
                        else if (extractValue("seroPosition",agentProperties.get(agentId)).equals(TRUE))
                        {
                            seroPosition = true ;
                            break ;
                        }
                    }

                    // Get condom use for each Urethral contact and combine outcomes.
                    contacts = extractArrayList(encounter,CONTACT) ;
                    for (String contact : contacts)
                    {
                        if (contact.indexOf(CONDOM) < 0)
                            continue ;
                        total++ ;
                        condom = TRUE.equals(extractValue(CONDOM,contact)) ;
                        if (condom)
                        {
                            if (seroSort)
                                condomSeroSort++ ;
                            else if (seroPosition)
                                condomSeroPosition++ ;
                            else    // neither seroPosition nor seroSort
                                condomOnly++ ;
                        }
                        else 
                        {
                            if (seroSort)
                                onlySeroSort++ ;
                            else if (seroPosition)
                                onlySeroPosition++ ;
                            else
                                unprotected++ ;
                        }
                    } // contacts
                }  // encounters
                //if (finished) 
                  //  break ;
                protectionRecord = Reporter.addReportProperty("condomOnly",((double) condomOnly)/total) ;   
                protectionRecord += Reporter.addReportProperty("onlySeroPosition",((double) onlySeroPosition)/total) ;
                protectionRecord += Reporter.addReportProperty("onlySeroSort",((double) onlySeroSort)/total) ;
                protectionRecord += Reporter.addReportProperty("condomSeroPosition",((double) condomSeroPosition)/total) ;
                protectionRecord += Reporter.addReportProperty("condomSeroSort",((double) condomSeroSort)/total) ;
                protectionRecord += Reporter.addReportProperty("unprotected",((double) unprotected)/total) ;
                protectionReport.add(protectionRecord) ;
            }    // report
        }
        LOGGER.log(Level.INFO, "{0}", protectionReport);
        return protectionReport ;    
    }
        
    /**
     * TODO: Replace ArrayList withCondom set.
     * @return (HashMap) key is the transmitting agentId and entries are receiving agentIds
     */
    public HashMap<Object,ArrayList<Object>> prepareAgentToAgentRecord()
    {
        HashMap<Object,ArrayList<Object>> transmissionRecord = new HashMap<Object,ArrayList<Object>>() ;
        
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        HashMap<Object,String[]> relationshipAgentReport = (HashMap<Object,String[]>) getReport("relationshipAgent",relationshipReporter) ; //   relationshipReporter.prepareRelationshipAgentReport() ;
        
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
            for (encounterIndex = record.indexOf(RELATIONSHIPID) ; encounterIndex >= 0 ; encounterIndex = record.indexOf(RELATIONSHIPID,encounterIndex+1))
            {
                String encounterString = extractEncounter(record,encounterIndex) ;
                agentIdPair = relationshipAgentReport.get(extractValue(RELATIONSHIPID,encounterString,0)) ;
                
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
        
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        HashMap<Object,String[]> relationshipAgentReport = (HashMap<Object,String[]>) getReport("relationshipAgent",relationshipReporter) ; //  relationshipReporter.prepareRelationshipAgentReport() ;
        
        
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
            for (encounterIndex = record.indexOf(RELATIONSHIPID) ; encounterIndex >= 0 ; encounterIndex = record.indexOf(RELATIONSHIPID,encounterIndex+1) )
            {
                String encounterString = extractEncounter(record,encounterIndex) ;
                agentIdPair = relationshipAgentReport.get(extractValue(RELATIONSHIPID,encounterString,0)) ;
                
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
                        objectReport = Reporter.updateHashMap(agentIdPair[0], agentIdPair[1], cycle, objectReport) ;
                        break ;
                    }
                    else    // falseIndex < trueIndex
                    {
                        objectReport = Reporter.updateHashMap(agentIdPair[1], agentIdPair[0], cycle, objectReport) ;
                        break ;
                    }
                    //contactIndex = encounterString.indexOf(CONTACT,contactIndex+1);
                }
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
        
        HashMap<Object,ArrayList<Object>> agentToAgentRecord ; 
        
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
     * 
     * @return (HashMap) Number of Agents responsible for given number of transmissions.
     */
    public HashMap<Object,Number> prepareNumberAgentTransmissionReport()
    {
        HashMap<Object,Number> numberAgentTransmissionReport = new HashMap<Object,Number>() ;

        HashMap<Object,Integer> agentTransmissionCountReport = prepareAgentTransmissionCountReport() ;
        
        Collection<Integer> agentTransmissionCountValues = agentTransmissionCountReport.values() ;
        
        int maxValue = Collections.max(agentTransmissionCountValues) ;
        
        // To track how agentIds have had more than given Relationships
        int nbAgents ;
        
        for (int key = maxValue ; key > 0 ; key-- )
        {
            nbAgents = Collections.frequency(agentTransmissionCountValues,key) ;
            numberAgentTransmissionReport.put(key, nbAgents) ;
        }
        
        return numberAgentTransmissionReport ;
    }        
   
    /**
     * Sorts transmission Report according to sortingProperty of Agents.
     * @param sortingProperty
     * @return (HashMap) sortingProperty maps to (HashMap) agentTransmissionReport
     */
    public HashMap<Object,HashMap<Object,Number>> prepareNumberAgentTransmissionReport(String sortingProperty)
    {
        HashMap<Object,HashMap<Object,Number>> numberAgentTransmissionReport = new HashMap<Object,HashMap<Object,Number>>() ;

        HashMap<Object,Integer> agentTransmissionCountReport = prepareAgentTransmissionCountReport() ;
        HashMap<Object,Number> transmissionReport ;
        
        PopulationReporter sortingReporter = new PopulationReporter(simName,getFolderPath()) ;
        HashMap<Object,Object> sortingReport = sortingReporter.sortedAgentIds(sortingProperty) ;
        LOGGER.log(Level.INFO, "{0}", sortingReport);
        HashMap<Object,HashMap<Object,Integer>> sortedAgentTransmissionCountReport 
                = sortReport(agentTransmissionCountReport, sortingReport) ;
        
        // Find highest value to count down from amongst all sorting variables
        ArrayList<Integer> agentTransmissionCountList = new ArrayList<Integer>() ;
        for (HashMap<Object,Integer> agentTransmissionCount : sortedAgentTransmissionCountReport.values()) 
            agentTransmissionCountList.addAll(agentTransmissionCount.values()) ;
        int maxValue = Collections.max(agentTransmissionCountList) ;
            
        for (Object sortingKey : sortedAgentTransmissionCountReport.keySet())
        {
            transmissionReport = new HashMap<Object,Number>() ;
            Collection<Integer> agentTransmissionCountValues = sortedAgentTransmissionCountReport.get(sortingKey).values() ;
        
            // To track how agentIds have had more than given Relationships
            int nbAgents ;

            for (int key = maxValue ; key > 0 ; key-- )
            {
                nbAgents = Collections.frequency(agentTransmissionCountValues,key) ;
                transmissionReport.put(key, nbAgents) ;
            }
            numberAgentTransmissionReport.put(sortingKey, transmissionReport) ;
        }
        
        return numberAgentTransmissionReport ;
    }        
   
    /**
     * 
     * @return (HashMap) Number of Agents responsible for a given number 
     * or more transmissions.
     */
    public HashMap<Object,Number> prepareCumulativeAgentTransmissionReport()
    {
        HashMap<Object,Number> cumulativeAgentTransmissionReport = new HashMap<Object,Number>() ;

        HashMap<Object,Integer> agentTransmissionCountReport = prepareAgentTransmissionCountReport() ;
        
        Collection<Integer> agentTransmissionCountValues = agentTransmissionCountReport.values() ;
        
        int maxValue = Collections.max(agentTransmissionCountValues) ;
        
        // To track how agentIds have had more than given Relationships
        int agentsOver = 0 ;
        
        for (int key = maxValue ; key > 0 ; key-- )
        {
            agentsOver += Collections.frequency(agentTransmissionCountValues,key) ;
            cumulativeAgentTransmissionReport.put(key, agentsOver) ;
        }
        
        return cumulativeAgentTransmissionReport ;
        
    }        
   
    
    /**
     * @param siteNames (String[]) names of body sites in sexual contact
     * @return String[] report of sexual contacts where STI transmission occurred
     */    
    public HashMap<Object,Number> prepareFromSiteToSiteReport(String siteNames[])
    {
        // Output HashMap
        HashMap<Object,Number> fromSiteToSiteReport = new HashMap<Object,Number>() ;
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
            
            // Cycle through contact: substrings, noting fromSite, toSite where 
            // transmission occurs
            //LOGGER.info(report);
            for (contactIndex = 0 ; contactIndex >= 0 ; contactIndex = indexOfProperty(CONTACT,contactIndex+1,report) )
            {
                contactString = extractBoundedString(CONTACT,report,contactIndex) ;
                
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
     * Extracts encounter reports where propertyName.value = value for at least 
     * one contact
     * @param propertyName
     * @param value
     * @param report
     * @return 
     */
    private String encounterByValue(String propertyName, String value, String record)
    {
        int indexContact ;
        String methodOutput = "" ;
        String encounterString ;
        String contactString ;
        for (int indexStart = 0 ; indexStart >= 0 ; indexStart = indexOfProperty(RELATIONSHIPID,indexStart+1, record) )
        {
            // Encounter to study
            encounterString = extractEncounter(record, indexStart) ;

            // Skip to next loop if no contact
            indexContact = indexOfProperty(CONTACT,encounterString) ;
            if (indexContact < 0)
                continue ;
            
            String encounterOpening = encounterString.substring(0, indexContact ) ;
            String encounterOutput = "" ;

            // check contacts for desired value of propertyName
            contactString = boundedStringByValue(propertyName,value,CONTACT,encounterString) ; 
            encounterOutput += contactString ;
                
            // Only include encounter in reportOutput if any of its contacts are included 
            if (encounterOutput.length() > 0)
                methodOutput += encounterOpening + encounterOutput ; // Include agentId

        }
        // If no positive cases are returned
        if ("".equals(methodOutput)) 
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
        int indexContact ;
        String methodOutput = "" ;
        String encounterString ;
        String contactString ;
        for (int indexStart = 0 ; indexStart >= 0 ; indexStart = indexOfProperty(RELATIONSHIPID,indexStart+1, report) )
        {
            // Encounter to study
            encounterString = extractEncounter(report, indexStart) ;

            // Initialise reportOutput for encounter
            indexContact = indexOfProperty(CONTACT,encounterString) ;
            String encounterOpening = encounterString.substring(0, indexContact ) ;
            String encounterOutput = "" ;

            // check contacts for desired propertyName
            contactString = boundedStringByContents(propertyName,CONTACT,encounterString) ; 
            encounterOutput += contactString ;
            // Only include encounter in reportOutput if any of its contacts are included 
            if (encounterOutput.length() > 0)
                methodOutput += encounterOpening + encounterOutput ; // Include agentId
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
        return extractBoundedString(CONTACT, encounter, indexStart) ;
    }

    /**
     * 
     * @param String string
     * @return substrings corresponding to encounters
     */
    private String extractEncounter(String string, int indexStart)
    {
        return extractBoundedString(RELATIONSHIPID, string, indexStart) ;
    }

    
}
