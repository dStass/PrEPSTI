/**
 * 
 */
package PRSP.PrEPSTI.reporter ;

import PRSP.PrEPSTI.agent.MSM;
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
import static PRSP.PrEPSTI.reporter.Reporter.EXTRACT_ARRAYLIST;


public class EncounterReporter extends Reporter {
    
    /** String representation of "contact". */
    static String CONTACT = "contact" ;

    static String TRANSMISSION = "transmission" ;
    static public String INCIDENCE = "incidence" ;
    static String CONDOM = "condom" ;
    static String PHARYNX = "Pharynx" ;
    static String RECTUM = "Rectum" ;
    static String URETHRA = "Urethra" ;

    static boolean WRITE_REPORT = true ;
    
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
     * Extract site names involved in a sexual contact, ordered to correspond with the 
     * Agents involved.
     * @param contactString
     * @return (String[]) Names of Sites, ordered to correspond with the involved Agents.
     */
    static String[] EXTRACT_CONTACT_SITES(String contactString)
    {
        String[] foundSites = new String[2] ;
        String[] contactSites = new String[2] ;
        
        int index = 0 ;
        for (String siteName : MSM.SITE_NAMES)
        {
            if (contactString.contains(siteName))
            {
                foundSites[index] = siteName ;
                index++ ;
            }
        }
        if (index == 0)
            LOGGER.severe("No sites found in contactString") ;
        else if (index == 1)    // Both Agents used same Site
        {
            contactSites[0] = foundSites[0] ;
            contactSites[1] = foundSites[0] ;
        }
        else if (contactString.indexOf(foundSites[1]) < contactString.indexOf(foundSites[0]))
        {
            contactSites[0] = foundSites[1] ;
            contactSites[1] = foundSites[0] ;
        }
        else
            contactSites = foundSites ;
        
        return contactSites ;
    }
    
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
                transmissionReport.add(encounterByValue(TRANSMISSION,TRUE,record)) ;
            }
            
        return transmissionReport ;
    }
    
    /**
     * 
     * @param siteNames
     * @return Records of final transmissions for specified siteNames and in total.
     */
    public HashMap<Comparable,Number> prepareFinalTransmissionsRecord(String[] siteNames)
    {
        int endCycle = getMaxCycles() ;
        
        return prepareFinalTransmissionsRecord(siteNames, 0, endCycle) ;
    }
    
    /**
     * 
     * @param siteNames
     * @return Records of final transmissions for specified siteNames and in total.
     */
    public HashMap<Comparable,Number> prepareFinalTransmissionsRecord(String[] siteNames, int backYears, int endCycle)
    {
        HashMap<Comparable,Number> finalTransmissionsRecord = new HashMap<Comparable,Number>() ;
        
        int rate ;
        
        String finalTransmissionRecord = getBackCyclesReport(0,0,1,endCycle).get(0) ; // getFinalRecord() ;
        String finalRecord = encounterByValue(TRANSMISSION,TRUE,finalTransmissionRecord) ;
        
        double population = getPopulation() ;
        // Rate measured per 100 man-years
        double denominator = population/(100.0 * DAYS_PER_YEAR) ;
        for (String siteName : siteNames)
        {
            // Count infected siteName
            rate = COUNT_VALUE_INCIDENCE(siteName,"0",finalRecord,0)[1];
            finalTransmissionsRecord.put(siteName,rate/denominator) ;
        }
        
        // All encounters with transmission
        rate = COUNT_VALUE_INCIDENCE(RELATIONSHIPID,"",finalRecord,0)[1];
        finalTransmissionsRecord.put("all",rate/denominator) ;
        
        return finalTransmissionsRecord ;
    }
 
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param lastYear
     * @return Year-by-year report for backYears years OF INCIDENTS on last day
     * of each year ending lastYear.
     */
    public HashMap<Comparable,String> 
        prepareYearsIncidenceRecord(String[] siteNames, int backYears, int lastYear) 
        {
            HashMap<Comparable,String> incidenceRecordYears = new HashMap<Comparable,String>() ;
            //HashMap<Object,Number[]> incidenceRecordYears = new HashMap<Object,Number[]>() ;
            
            int maxCycles = getMaxCycles() ;
            
            String incidenceRecord ;
            for (int year = 0 ; year < backYears ; year++ )
            {
                //Number[] yearlyIncidenceRecord = new Number[siteNames.length + 1] ;
               
                //endCycle = maxCycles - year * DAYS_PER_YEAR ;
                incidenceRecord = prepareFinalIncidenceRecord(siteNames, year, 0, DAYS_PER_YEAR, maxCycles);
                
                //for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
                  //  yearlyIncidenceRecord[siteIndex] = incidenceRecord.get(siteNames[siteIndex]) ;
                //yearlyIncidenceRecord[siteNames.length] = incidenceRecord.get("all") ;
                incidenceRecordYears.put(lastYear - year, incidenceRecord) ;
            }
            CLEAR_REPORT_LIST() ;
            return incidenceRecordYears ;
        }
    
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param lastYear
     * @param sortingProperty
     * @return Year-by-year report for backYears years OF INCIDENTS on last day
     * of each year ending lastYear.
     */
    public HashMap<Comparable,String> 
        prepareYearsIncidenceReport(String[] siteNames, int backYears, int lastYear, String sortingProperty) 
        {
            if (sortingProperty.isEmpty())
                return prepareYearsIncidenceRecord(siteNames, backYears, lastYear) ;
            HashMap<Comparable,String> incidenceRecordYears = new HashMap<Comparable,String>() ;
            //HashMap<Object,Number[]> incidenceRecordYears = new HashMap<Object,Number[]>() ;
            
            int maxCycles = getMaxCycles() ;
            
            String incidenceRecord ;
            for (int year = 0 ; year < backYears ; year++ )
            {
                //Number[] yearlyIncidenceRecord = new Number[siteNames.length + 1] ;
               
                //endCycle = maxCycles - year * DAYS_PER_YEAR ;
                incidenceRecord = prepareSortedFinalIncidenceRecord(siteNames, year, 0, DAYS_PER_YEAR, maxCycles, sortingProperty);
                
                //for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
                  //  yearlyIncidenceRecord[siteIndex] = incidenceRecord.get(siteNames[siteIndex]) ;
                //yearlyIncidenceRecord[siteNames.length] = incidenceRecord.get("all") ;
                incidenceRecordYears.put(lastYear - year, incidenceRecord) ;
            }
            CLEAR_REPORT_LIST() ;
            return incidenceRecordYears ;
        }
    
    
    /**
     * 
     * @param siteNames
     * @param backMonths
     * @param backDays
     * @return Records of final incidence for specified siteNames and in total.
     */
    public String prepareFinalIncidenceRecord(String[] siteNames, int backMonths, int backDays)
    {
        //HashMap<Object,Number>
        int endCycle = getMaxCycles() ;
        
        return prepareFinalIncidenceRecord(siteNames, 0, backMonths, backDays, endCycle) ;
    }
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return Records of final incidence for specified siteNames and in total.
     */
    public String prepareFinalIncidenceRecord(String[] siteNames, int backYears, int backMonths, int backDays, int endCycle)
    {
    	StringBuilder sbFinalIncidence = new StringBuilder();
        //String finalIncidence = "" ; // new HashMap<Object,Number>() ;
        
        endCycle = endCycle - (backYears * DAYS_PER_YEAR) ;
        
        int incidents ;
        String record ;
        
        //String finalIncidenceRecord ; // getFinalRecord() ;
        //LOGGER.log(Level.INFO, "{0} {1} {2}", new Object[] {backMonths, backDays, endCycle});
        ArrayList<String> finalIncidentsReport = getBackCyclesReport(0, backMonths, backDays, endCycle) ;
        
        double population = Double.valueOf(getPopulation()) ; 
        
        // Adjust incidence rate for sampling time and days per year
        //* 100 because units  per 100 person years
        double denominator = population * getBackCycles(0,backMonths,backDays)/(100.0 * DAYS_PER_YEAR) ;
        
        incidents = 0 ;
        for (String finalIncidentsRecord : finalIncidentsReport)
        {
            // Select encounters where TRANSMISSION occurred
            record = encounterByValue(TRANSMISSION,TRUE,finalIncidentsRecord) ;
            
            // record = record.substring(record.lastIndexOf(RELATIONSHIPID)) ;
            // if (COUNT_VALUE_INCIDENCE(RELATIONSHIPID,"",record,0)[1] > 1)
            // LOGGER.info(record);
            // Count them
            incidents += COUNT_VALUE_INCIDENCE(RELATIONSHIPID,"",record,0)[1] ;
        }
        sbFinalIncidence.append(ADD_REPORT_PROPERTY("all",incidents/denominator));
        // finalIncidence += ADD_REPORT_PROPERTY("all",incidents/denominator) ;
        
        for (String siteName : siteNames)
        {
            incidents = 0 ;
            for (String finalIncidentsRecord : finalIncidentsReport)
            {
                // Count siteName being infected
                record = encounterByValue(siteName,"0",finalIncidentsRecord) ;
                
                //record = encounterByValue(TRANSMISSION,TRUE,record) ;
                
                incidents += COUNT_VALUE_INCIDENCE(TRANSMISSION,TRUE,record,0)[0] ;
                
            }
            sbFinalIncidence.append(ADD_REPORT_PROPERTY(siteName,incidents/denominator));
            // finalIncidence += ADD_REPORT_PROPERTY(siteName,incidents/denominator) ;
        }
        
        return sbFinalIncidence.toString() ;
    }
    
    
    /**
     * 
     * @param siteNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @param sortingProperty
     * @return Records of final incidence for specified siteNames and in total.
     */
    public String prepareSortedFinalIncidenceRecord(String[] siteNames, int backYears, int backMonths, int backDays, int endCycle, String sortingProperty)
    {
    	StringBuilder sbFinalIncidence = new StringBuilder() ;
        // String finalIncidence = "" ; // new HashMap<Object,Number>() ;
        HashMap<Object,Number[]> sortedFinalIncidence = new HashMap<Object,Number[]>() ;
        ArrayList<String> siteNameList = new ArrayList<String>(Arrays.asList(siteNames)) ;
        siteNameList.add("all") ;
        String[] agentIds = new String[2] ;
        endCycle = endCycle - (backYears * DAYS_PER_YEAR) ;
        
        int incidents ;
        String record ;
        
        ArrayList<String> finalIncidentsReport = getBackCyclesReport(0, backMonths, backDays, endCycle) ;
        
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        HashMap<Object,String[]> relationshipAgentReport = relationshipReporter.prepareRelationshipAgentReport() ;
                //(HashMap<Object,String[]>) getReport("relationshipAgent",relationshipReporter) ; //  
        
        PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
        HashMap<String,ArrayList<String>> sortedAgentReport = populationReporter.agentIdSorted(sortingProperty) ;
        HashMap<Object,Object> sortedAgentIds = populationReporter.sortedAgentIds(sortingProperty) ;
        ArrayList<String> agentsAliveReport = populationReporter.prepareAgentsAliveRecord(endCycle - DAYS_PER_YEAR) ;

        for (String finalIncidentsRecord : finalIncidentsReport)
        {
            // All retained contacts of all retained encounters have transmission.
            record = encounterByValue(TRANSMISSION,TRUE,finalIncidentsRecord) ;
            for (int encounterIndex = record.indexOf(RELATIONSHIPID) ; encounterIndex > -1 ; encounterIndex = record.indexOf(RELATIONSHIPID,encounterIndex + 1))
            {
                boolean[] incidenceAgentIds = new boolean[] {false,false} ;
                String encounterString = extractEncounter(record,encounterIndex) ;
                String relationshipId = EXTRACT_VALUE(RELATIONSHIPID,encounterString) ;
                agentIds = relationshipAgentReport.get(relationshipId) ;
                //Object[] sortingValues = new Object[] {sortedAgentIds.get(agentIds[0]),sortedAgentIds.get(agentIds[1])} ;
                
                for (int contactIndex = encounterString.indexOf(CONTACT) ; contactIndex > -1 ; contactIndex = encounterString.indexOf(CONTACT,contactIndex + 1))
                {
                    String contactString = extractContact(encounterString,contactIndex) ;
                    Object incidentValue = "" ;
                    String incidentSiteName = "" ;
                    String incidentAgentId = "" ;
                    String siteNameA = "" ;
                    String siteNameB = "" ;
                    
                    // Find siteA in contact
                    int siteIndexA = -1 ;
                    int indexA = 0 ;
                    while (siteIndexA < 0)
                    {
                        siteIndexA = contactString.indexOf(siteNames[indexA]) ;
                        siteNameA = siteNames[indexA] ;
                        indexA++ ;
                    }
                    
                    // Find siteB in contact
                    int indexB = indexA ;
                    // Does siteNameB == siteNameA ?
                    int siteIndexB = contactString.indexOf(siteNameA,siteIndexA + 1) ;
                    // indexB == siteNames.length only if siteNameB == siteNameA
                    //for (String siteName : siteNames)
                    while ((siteIndexB < 0) && (indexB < (siteNames.length + 1)))
                    {
                        siteIndexB = contactString.indexOf(siteNames[indexB]) ;
                        indexB++ ;
                    }
                    
                    siteNameB = siteNames[indexB - 1] ;
                    
                    if ((siteIndexB < 0) || (siteIndexA < 0))
                        LOGGER.severe("Site missing in contactSting " + contactString) ;
                    
                    // Determine transmitting and receiving Sites.
                    boolean AthenB = siteIndexA < siteIndexB ;
                    int incidentIndex ;
                    // Receiving Site has value "0" in contactString.
                    if ("0".equals(EXTRACT_VALUE(siteNameB,contactString,siteIndexB)))
                    {
                        incidentSiteName = siteNameB ;
                        if (AthenB)
                            incidentIndex = 1 ;
                        else
                            incidentIndex = 0 ;
                    }
                    else if ("1".equals(EXTRACT_VALUE(siteNameB,contactString,siteIndexB)))
                    {
                        incidentSiteName = siteNameA ;
                        if (AthenB)
                            incidentIndex = 0 ;
                        else
                            incidentIndex = 1 ;
                    }
                    else
                    {
                        LOGGER.log(Level.SEVERE, "siteIndexA:{0} siteIndexB:{1} siteNameA:{2} siteNameB:{3} {4}",
                                new Object[] {indexA, indexB, siteNameA, siteNameB, contactString});
                        incidentIndex = -1 ;
                    }
                    
                    // To track any Site
                    //incidentValue = sortingValues[incidentIndex] ;
                    incidentAgentId = agentIds[incidentIndex] ;
                    incidenceAgentIds[incidentIndex] = true ;

                    // Count site-specific incidents
                    incidentValue = sortedAgentIds.get(incidentAgentId) ;
                    if (!sortedFinalIncidence.containsKey(incidentValue))
                    {
                        Number[] siteArray = new Number[siteNames.length + 1] ;
                        for (int index = 0 ; index < siteArray.length ; index++)
                            siteArray[index] = 0.0 ;
                        sortedFinalIncidence.put(incidentValue, siteArray) ;
                    }
                    Number[] siteArray = sortedFinalIncidence.get(incidentValue) ;
                    incidents = siteArray[siteNameList.indexOf(incidentSiteName)].intValue() ;
                    incidents++ ;
                    siteArray[siteNameList.indexOf(incidentSiteName)] = incidents ;
                    sortedFinalIncidence.put(incidentValue,siteArray) ;
                }
                for (int allIndex = 0 ; allIndex < 2 ; allIndex++ )
                    if (incidenceAgentIds[allIndex])
                    {
                        String incidentAgentId = agentIds[allIndex] ;
                        Object incidentValue = sortedAgentIds.get(incidentAgentId) ;
                        Number[] siteArray = sortedFinalIncidence.get(incidentValue) ;
                        incidents = siteArray[siteNameList.indexOf("all")].intValue() ;
                        incidents++ ;
                        siteArray[siteNameList.indexOf("all")] = incidents ;
                        sortedFinalIncidence.put(incidentValue,siteArray) ;
                    }
            }
        }
        
        for (Object sortingKey : sortedFinalIncidence.keySet())
        {
            ArrayList<String> sortedAgents = sortedAgentReport.get(sortingKey) ;
            sortedAgents.retainAll(agentsAliveReport) ;
            int population = sortedAgents.size() ;
            double denominator = population * getBackCycles(0,backMonths,backDays)/(100.0 * DAYS_PER_YEAR) ;
            for (int siteIndex = 0 ; siteIndex < siteNameList.size() ; siteIndex++ )
            {
                String entryName = siteNameList.get(siteIndex) + "_" + sortingKey.toString() ;
                Number entryValue = sortedFinalIncidence.get(sortingKey)[siteIndex] ;
                sbFinalIncidence.append(ADD_REPORT_PROPERTY(entryName,entryValue.doubleValue()/denominator));
                // finalIncidence += ADD_REPORT_PROPERTY(entryName,entryValue.doubleValue()/denominator) ;
            }
        }

        return sbFinalIncidence.toString() ;
    }
    
    /**
     * 
     * @return (ArrayList) The number of transmissions in each cycle per 
     * population in Site siteName, or in general if site.isEmpty() .
     */
    public ArrayList<String> prepareTransmissionCountReport(String siteName)
    {
        ArrayList<String> nbTransmissions = new ArrayList<String>() ;
        
        double incidence ;
        String transmissionString ;
        int transmissions ;
        int population = getPopulation() ;
        
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
            for (String record : input)
            {
                //LOGGER.info(record);
                transmissions = 0 ;
                if (!siteName.isEmpty())
                {
                    record = BOUNDED_STRING_BY_VALUE(siteName,"0",CONTACT,record) ;
                    transmissions = COUNT_VALUE_INCIDENCE(TRANSMISSION, TRUE, record, 0)[0];
                }
                else
                {
                    record = BOUNDED_STRING_BY_VALUE(TRANSMISSION, TRUE,RELATIONSHIPID,record) ;
                    transmissions = COUNT_VALUE_INCIDENCE(RELATIONSHIPID, "", record, 0)[1];
                }
                incidence = ((double) transmissions)/population;
                transmissionString = Reporter.ADD_REPORT_PROPERTY(TRANSMISSION, transmissions) ;
                transmissionString += Reporter.ADD_REPORT_PROPERTY("rate",incidence) ;
                nbTransmissions.add(transmissionString) ;
            }
            
        return nbTransmissions ;
    }
    
    /**
     * 
     * @param sortedReport
     * @return Report of Transmissions per cycle to Agents sorted in sortedReport.
     */
    public ArrayList<ArrayList<Comparable>> prepareReceiveCountReport( HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> sortedReport )
    {
        ArrayList<ArrayList<Comparable>> nbTransmissions = new ArrayList<ArrayList<Comparable>>() ;
        
        // Put keys of sortedReport in order
        Object[] objectKeys = sortedReport.keySet().toArray() ;
        Integer[] sortedKeys = new Integer[objectKeys.length] ;
        
        for (int keyIndex = 0; keyIndex < objectKeys.length ; keyIndex++)
            sortedKeys[keyIndex] = (Integer) objectKeys[keyIndex] ;
        Arrays.sort(sortedKeys) ;
        
        // Loop through keys
        for (int key = 0 ; key <= sortedKeys[sortedKeys.length-1] ; key++)
        {
            ArrayList<Comparable> recordArray = new ArrayList<Comparable>() ;
            if (sortedReport.keySet().contains(key))
            {
                HashMap<Comparable,ArrayList<Comparable>> cycleHashMap = sortedReport.get(key) ;
                //count = 0;
                for ( ArrayList<Comparable> value : cycleHashMap.values() )
                    recordArray.addAll(value) ;
            }
            else
                recordArray.add("0") ;
            
            nbTransmissions.add(recordArray) ;
        }
        return nbTransmissions ;
    }
    
    
    public HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>
        prepareReceiveSortPrepStatusReport(String value )
    {
        HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> outputHashMap 
                = prepareReceiveSortPrepStatusReport(new String[] {value}).get(value) ;
        return outputHashMap ;
    }

    /**
     * 
     * @param values
     * @return HashMap sorting values maps to correspondingTransmissionReport
     */
    public HashMap<Object,HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>> 
        prepareReceiveSortPrepStatusReport(String[] values )
    {
        // LOGGER.info("prepareAgentToAgentReport()") ;
        HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> transmissionReport = prepareAgentToAgentReport() ;
        //LOGGER.log(Level.INFO, "{0}", transmissionReport);
        // LOGGER.info("sortPrepStatus()");
        PopulationReporter populationReporter = new PopulationReporter(getMetaDatum("Community.NAME_ROOT"), getFolderPath()); 
        HashMap<Comparable,ArrayList<Comparable>> sortingReport = populationReporter.sortPrepStatus() ;
        // LOGGER.log(Level.INFO, "{0}", sortingReport);
        
        // LOGGER.info("sortReport()");
        //String[] values = new String[] {TRUE, FALSE} ;
        return Reporter.SORT_REPORT(transmissionReport, sortingReport, values) ;
    }
        
    /**
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param lastYear
     * @return Year-by-year report of the number of Agents who have (not) always
     * used condoms for anal intercourse, or never had it.
     */
    public HashMap<Comparable,HashMap<Object,String>> 
    prepareNumberCondomlessYears(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int lastYear)
    {
        HashMap<Comparable,HashMap<Object,String>> numberCondomlessYears = new HashMap<Comparable,HashMap<Object,String>>();
        // HashMap<Object,HashMap<Object,Number[]>>() ;

        int maxCycles = getMaxCycles() ;

        int endCycle ;
        HashMap<Object,String> numberCondomlessRelationship ;
        //HashMap<Object,Number[]> numberCondomlessRelationship ;
        for (int year = 0 ; year < backYears ; year++ )
        {
            endCycle = maxCycles - year * DAYS_PER_YEAR ;
            numberCondomlessRelationship = prepareNumberCondomlessReport(0, backMonths, backDays, endCycle, relationshipClassNames);

            //HashMap<Object,Number[]> yearlyNumberCondomlessRelationship = new HashMap<Object,Number[]>() ;

            //for (Object relationshipClassName : relationshipClassNames)
              //  yearlyNumberCondomlessRelationship.put(relationshipClassName, numberCondomlessRelationship.get(relationshipClassName)) ;

            numberCondomlessYears.put(lastYear - year, numberCondomlessRelationship) ;
        }

        return numberCondomlessYears ;
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
    public HashMap<Object,String> prepareNumberCondomlessReport(int backYears, int backMonths, int backDays, String[] relationshipClazzNames)
    {
        // HashMap<Object,Number[]>
        
        return prepareNumberCondomlessReport(backYears, backMonths, backDays, getMaxCycles(), relationshipClazzNames) ;
    }
    

    /**
     * The number of Agents who have always, or not always, used a condom during anal
     * sex, or never had anal sex in the given time period.
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @param relationshipClazzNames
     * @return 
     */
    public HashMap<Object,String> prepareNumberCondomlessReport(int backYears, int backMonths, int backDays, int endCycle, String[] relationshipClazzNames)
    {
        //String numberCondomlessReport = "" ; 
        HashMap<Object,String> numberCondomlessReport = new HashMap<Object,String>() ;
        //new HashMap<Object,Number[]>() ;
        String[] condomStati = new String[] {"always","not_always","no_AI"} ;
        //for (String status : condomStati)
          //  numberCondomlessReport.put(status, "") ; // new Number[relationshipClazzNames.length]) ;
        String reportEntry ;
        int statusIndex ;
        
        HashMap<String,HashMap<Comparable,ArrayList<Comparable>>> agentAnalIntercourseReport ;
        
        // Prepare agentRelationshipsRecord
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        //Class[] parameterClazzes = new Class[] {String[].class, int.class, int.class, int.class, int.class} ;
        //Object[] parameters = new Object[] {relationshipClazzNames, backYears, backMonths, backDays, endCycle} ;
        HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> agentRelationshipsRecord 
          //      = (HashMap<Object,HashMap<Object,ArrayList<Object>>>) getRecord("agentRelationships",relationshipReporter,parameterClazzes,parameters) ;
            = relationshipReporter.prepareAgentRelationshipsRecord(relationshipClazzNames, backYears, backMonths, backDays, endCycle) ;
        
        //String relationshipClazz ;
        //String condomStatus ;
        double agentsInvolved ;
        //String numberReportEntry = "" ;
        //for (int relationshipClazzIndex = 0 ; relationshipClazzIndex < relationshipClazzNames.length ; relationshipClazzIndex++ );
        for (String relationshipClazz : relationshipClazzNames)
        {
            //relationshipClazz = relationshipClazzNames[relationshipClazzIndex] ;
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
            //numberCondomlessReport += ADD_REPORT_LABEL(relationshipClazz) ;
            reportEntry = ADD_REPORT_PROPERTY(condomStati[statusIndex],withCondom/agentsInvolved) ;
            //Number[] numberReportEntries   = numberCondomlessReport.get(condomStatus) ;
            //numberReportEntries[relationshipClazzIndex] = withCondom/agentsInvolved ;
            //numberCondomlessReport.put(condomStatus, (Number[]) numberReportEntries.clone()) ;
            
            // Proportion sometimes having condomless anal sex
            statusIndex = 1 ;
            reportEntry += ADD_REPORT_PROPERTY(condomStati[statusIndex],withoutCondom/agentsInvolved) ;
            //numberReportEntries = numberCondomlessReport.get(condomStatus) ;
            //numberReportEntries[relationshipClazzIndex] = withoutCondom/agentsInvolved ;
            //numberCondomlessReport.put(condomStatus, (Number[]) numberReportEntries.clone()) ;
            
            // Proportion who never had anal sex
            statusIndex = 2 ;
            reportEntry += ADD_REPORT_PROPERTY(condomStati[statusIndex],(agentsInvolved - withCondom - withoutCondom)/agentsInvolved) ;
            //numberReportEntries = numberCondomlessReport.get(condomStatus) ;
            //numberReportEntries[relationshipClazzIndex] = (agentsInvolved - withCondom - withoutCondom)/agentsInvolved ;
            //numberCondomlessReport.put(condomStatus, (Number[]) numberReportEntries.clone()) ;
            numberCondomlessReport.put(relationshipClazz, reportEntry) ;
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
    public HashMap<String,HashMap<Comparable,ArrayList<Comparable>>> prepareAgentAnalIntercourseReport(int backYears, int backMonths, int backDays, String relationshipClazzName)
    {
        HashMap<String,HashMap<Comparable,ArrayList<Comparable>>> agentAnalIntercourseReport 
                = new HashMap<String,HashMap<Comparable,ArrayList<Comparable>>>() ;
        
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        HashMap<Object,String[]> relationshipAgentReport = relationshipReporter.prepareRelationshipAgentReport() ;
                // (HashMap<Object,String[]>) getReport("relationshipAgent",relationshipReporter) ; //  
        //LOGGER.log(Level.INFO, "relationshipAgentReport {0}", relationshipAgentReport);
        
        int maxCycles = getMaxCycles() ;
        int backCycles = GET_BACK_CYCLES(backYears, backMonths, backDays, maxCycles) ;
        int startCycle = maxCycles - backCycles ;
        
        ArrayList<String> encounterReport = this.getBackCyclesReport(0, 0, backCycles) ;
        //LOGGER.log(Level.INFO, "encounterReport {0}", encounterReport);
        ArrayList<String> relationshipClassReport = relationshipReporter.filterRelationshipClazzReport(relationshipClazzName,encounterReport) ;
        //LOGGER.log(Level.INFO, "relationshipClassReport {0}", relationshipClassReport);
        ArrayList<String> intercourseReport = prepareFilteredReport(CONDOM,"",relationshipClassReport) ;
        //LOGGER.log(Level.INFO, "intercourseReport {0}", intercourseReport);
        for (String condom : new String[] {TRUE,FALSE})
        {
            agentAnalIntercourseReport.put(condom, new HashMap<Comparable,ArrayList<Comparable>>()) ;
            
            // Filter by condom use
            ArrayList<String> condomReport = prepareFilteredReport(CONDOM,condom,intercourseReport) ;
            //LOGGER.log(Level.INFO,"condomReport:{0}",condomReport) ;

            for (int recordNb = 0 ; recordNb < backCycles ; recordNb++ ) 
            {
                String record = condomReport.get(recordNb) ;
                ArrayList<String> encounters = EXTRACT_ARRAYLIST(record,RELATIONSHIPID) ; 
                for (String encounter : encounters)
                {
                    Comparable[] agentIds = relationshipAgentReport.get(EXTRACT_VALUE(RELATIONSHIPID,encounter)) ;
                    for (Comparable agentId : agentIds)
                        agentAnalIntercourseReport.put(condom, UPDATE_HASHMAP(agentId,(Comparable) (startCycle + recordNb),agentAnalIntercourseReport.get(condom))) ;
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
    public HashMap<Comparable,Number> prepareNumberAgentCondomlessReport(int backYears, int backMonths, int backDays, String relationshipClazzName, String concordanceName, boolean concordant)
    {
        int endCycle = getMaxCycles() ;
        
        return prepareNumberAgentCondomlessReport(backYears, backMonths, backDays, endCycle, relationshipClazzName, concordanceName, concordant) ;
    }
    
    
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @param relationshipClazzName
     * @param concordanceName
     * @param concordant
     * @return (HashMap) Number of condomless intercourse acts maps to the Number
     * of Agents to have committed that many such acts in the last backYears years,
     * backMonths months and backDays days.
     */        
    public HashMap<Comparable,Number> prepareNumberAgentCondomlessReport(int backYears, int backMonths, int backDays, int endCycle, String relationshipClazzName, String concordanceName, boolean concordant)
    {
        HashMap<Comparable,Number> numberAgentCondomlessReport = new HashMap<Comparable,Number>() ;
        
        HashMap<Comparable,ArrayList<Comparable>> agentCondomlessReport 
                = prepareAgentCondomlessReport(backYears, backMonths, backDays, endCycle, relationshipClazzName, concordanceName, concordant) ;
        
        for (ArrayList<Comparable> condomlessValue : agentCondomlessReport.values() ) 
        {
            int intercourses = condomlessValue.size() ;
            numberAgentCondomlessReport = INCREMENT_HASHMAP(intercourses,numberAgentCondomlessReport) ;
        }
        
        return numberAgentCondomlessReport ;
    }
    
    /**
     * 
     * @return (HashMap) agentId maps to the cycles in which they had condomless 
     * anal intercourse in a (relationshipClazzName) Relationship.
     */
    private HashMap<Comparable,ArrayList<Comparable>> prepareAgentCondomlessReport(int backYears, int backMonths, int backDays, String relationshipClazzName)
    {
        int endCycle = getMaxCycles() ;
        
        return prepareAgentCondomlessReport(backYears, backMonths, backDays, endCycle, relationshipClazzName, "", true) ;
    }
    
    /**
     * 
     * @return (HashMap) agentId maps to the cycles in which they had condomless 
     * anal intercourse in a (relationshipClazzName) Relationship con/dis-cordant 
     * according to concordanceName.
     */
    private HashMap<Comparable,ArrayList<Comparable>> prepareAgentCondomlessReport(int backYears, int backMonths, int backDays, int endCycle, String relationshipClazzName, String concordanceName, boolean concordant)
    {
        HashMap<Comparable,ArrayList<Comparable>> agentCondomlessIntercourse = new HashMap<Comparable,ArrayList<Comparable>>() ;
        
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        HashMap<Object,String[]> relationshipAgentReport = relationshipReporter.prepareRelationshipAgentReport() ;
                //(HashMap<Object,String[]>) getReport("relationshipAgent",relationshipReporter) ; //  
        
        int backCycles = GET_BACK_CYCLES(backYears, backMonths, backDays, endCycle) ;
        int startCycle = endCycle - backCycles ;
        
        ArrayList<String> encounterReport = getBackCyclesReport(0, 0, backCycles, endCycle) ;
        ArrayList<String> condomlessReport = prepareFilteredReport(CONDOM,FALSE,encounterReport) ;
        ArrayList<String> concordantReport = relationshipReporter.filterByConcordance(concordanceName, concordant, condomlessReport) ;
        ArrayList<String> finalReport = relationshipReporter.filterRelationshipClazzReport(relationshipClazzName,concordantReport) ;
        
        int reportSize = finalReport.size() ;
        for (int recordNb = 0 ; recordNb < reportSize ; recordNb++ ) 
        {
            String record = finalReport.get(recordNb) ;
            ArrayList<String> encounters = EXTRACT_ARRAYLIST(record,RELATIONSHIPID) ; 
            for (String encounter : encounters)
            {
                String[] agentIds = relationshipAgentReport.get(EXTRACT_VALUE(RELATIONSHIPID,encounter)) ;
                for (String agentId : agentIds)
                    agentCondomlessIntercourse = UPDATE_HASHMAP(agentId, (startCycle + recordNb),agentCondomlessIntercourse) ;
            }
        }
        
        return agentCondomlessIntercourse ;
    }
    
    /**
     * 
     * @return (HashMap) agentId maps to the cycles in which they had condomless 
     * anal intercourse in a (relationshipClazzName) Relationship con/dis-cordant 
     * according to concordanceName.
     */
    private HashMap<Comparable,ArrayList<Comparable>> prepareAgentCondomlessPositionReport(int backYears, int backMonths, int backDays, int endCycle, String relationshipClazzName, String concordanceName, boolean concordant, String position)
    {
        HashMap<Comparable,ArrayList<Comparable>> agentCondomlessIntercourse = new HashMap<Comparable,ArrayList<Comparable>>() ;
        
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        HashMap<Object,String[]> relationshipAgentReport = relationshipReporter.prepareRelationshipAgentReport() ;
                //(HashMap<Object,String[]>) getReport("relationshipAgent",relationshipReporter) ; //  
        
        int positionIndex = -1 ;
        if ("insertive".equals(position))
            positionIndex = 2 ;
        else if ("receptive".equals(position))
            positionIndex = 1 ;
        
        int backCycles = GET_BACK_CYCLES(backYears, backMonths, backDays, endCycle) ;
        int startCycle = endCycle - backCycles ;
        
        ArrayList<String> encounterReport = getBackCyclesReport(0, 0, backCycles, endCycle) ;
        ArrayList<String> condomlessReport = prepareFilteredReport(CONDOM,FALSE,encounterReport) ;
        ArrayList<String> concordantReport = relationshipReporter.filterByConcordance(concordanceName, concordant, condomlessReport) ;
        ArrayList<String> finalReport = relationshipReporter.filterRelationshipClazzReport(relationshipClazzName,concordantReport) ;
        
        int reportSize = finalReport.size() ;
        for (int recordNb = 0 ; recordNb < reportSize ; recordNb++ ) 
        {
            String record = finalReport.get(recordNb) ;
            ArrayList<String> encounters = EXTRACT_ARRAYLIST(record,RELATIONSHIPID) ; 
            for (String encounter : encounters)
            {
                String[] agentIds = relationshipAgentReport.get(EXTRACT_VALUE(RELATIONSHIPID,encounter)) ;
                ArrayList<String> contactList = EXTRACT_ARRAYLIST(encounter,CONTACT,CONDOM) ;
                // Each Integer encodes positions taken by Agent, 1:receptive 2:insertive 3:both
                Integer[] agentPositions = new Integer[2] ;
                for (String contact : contactList)
                {
                    String[] contactSites = EXTRACT_CONTACT_SITES(contact) ;
                    for (int agentIndex = 0 ; agentIndex < 2 ; agentIndex++ )
                    {
                        if (contactSites[agentIndex].equals(RECTUM))
                            agentPositions[agentIndex] = agentPositions[agentIndex] | 1 ;
                        else if (contactSites[agentIndex].equals(URETHRA))
                            agentPositions[agentIndex] = agentPositions[agentIndex] | 2 ;
                    }
                }
                for (int agentIndex = 0 ; agentIndex < 2 ; agentIndex++ )
                    if ((agentPositions[agentIndex] & positionIndex) == positionIndex)
                        agentCondomlessIntercourse = UPDATE_HASHMAP(agentIds[agentIndex], (Comparable) (startCycle + recordNb),agentCondomlessIntercourse) ;
            }
        }
        
        return agentCondomlessIntercourse ;
    }
    
    /**
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @param concordanceName
     * @param concordant
     * @return Reports the percent of Agents who have been involved in each subclass of Relationship 
     * who have partaken in condomless anal intercourse within those Relationships.
     */
    public String preparePercentAgentCondomlessReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle, String concordanceName, boolean concordant)
    {
        return preparePercentAgentCondomlessReport(relationshipClassNames, backYears, backMonths, backDays, endCycle, concordanceName, concordant, "") ;
    }
    
    /**
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @param concordanceName
     * @param concordant
     * @return Reports the percent of Agents who have been involved in each subclass of Relationship 
     * who have partaken in condomless anal intercourse within those Relationships.
     */
    public String preparePercentAgentCondomlessReport(String[] relationshipClassNames, int backYears, int backMonths, int backDays, int endCycle, String concordanceName, boolean concordant, String sortingProperty)
    {
        String percentCondomlessReport = "" ;
        //HashMap<Object,Number> percentCondomlessRelationship = new HashMap<Object,Number>() ;
        String property ;
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        // (HashMap) relationshipClassName maps to the number of Agents involved in given class of Relationship 
        //during the specified period.
        
        // Get Report of sortingValue mapping to agentIds
        HashMap<String,ArrayList<String>> sortedAgentReport = new HashMap<String,ArrayList<String>>() ;
        if (sortingProperty.isEmpty())
            sortedAgentReport.put("",new ArrayList<String>()) ;
        else
        {
            PopulationReporter populationReporter = new PopulationReporter(simName,getFolderPath()) ;
            sortedAgentReport = populationReporter.agentIdSorted(sortingProperty) ;
        }
        // LOGGER.info("sortedAgentReport " + sortedAgentReport.toString()) ;
        
        for (String relationshipClazzName : relationshipClassNames)
        {
            //agentId maps to the cycles in which they had condomless anal intercourse 
            HashMap<Comparable,ArrayList<Comparable>> agentCondomlessReport = prepareAgentCondomlessReport(backYears, backMonths, backDays, endCycle, relationshipClazzName, concordanceName, concordant) ; 
            ArrayList<Object> activeAgentList = new ArrayList<Object>() ;
            ArrayList<Object> uniqueAgentList = new ArrayList<Object>() ;
            //for (Object numberKey : agentCondomlessReport.keySet()) //numberAgentCondomlessReport.keySet())
            //for (Object keyValue : agentCondomlessReport.values())
            //{
                //ArrayList<Object> newAgents = agentCondomlessReport.get(keyValue) ;
                activeAgentList.addAll(agentCondomlessReport.keySet()) ;
                activeAgentList.removeAll(uniqueAgentList) ;
                uniqueAgentList.addAll(activeAgentList) ;
            //}
            
            for (Object sortingValue : sortedAgentReport.keySet())
            {
                ArrayList<String> sortedAgents = sortedAgentReport.get(sortingValue) ;
                String[] clazzList = new String[] {relationshipClazzName} ;
                HashMap<Comparable,Number> numberRelationshipsReport 
                    = relationshipReporter.prepareNumberRelationshipsReport(clazzList, backYears, backMonths, backDays, endCycle, sortedAgents) ;

                property = relationshipClazzName ;
                if ("" != sortingValue)
                {
                    uniqueAgentList.retainAll(sortedAgents) ;
                    property += "_" + sortingValue ;
                }
                int activeAgents = uniqueAgentList.size() ;
        
                Double totalAgents = numberRelationshipsReport.get(relationshipClazzName).doubleValue() ;
                //percentCondomlessRelationship.put(relationshipClazzName,activeAgents/totalAgents) ;
                percentCondomlessReport += ADD_REPORT_PROPERTY(property,activeAgents/totalAgents) ;
            }
        }
        return percentCondomlessReport ;
    }
    
    /**
     * 
     * @param relationshipClassNames
     * @param backYears
     * @param lastYear
     * @param concordanceName
     * @param concordant
     * @param sortingProperty
     * @return year-by-year report
     */
    public HashMap<Comparable,String> preparePercentAgentCondomlessYears(String[] relationshipClassNames, int backYears, int lastYear, String concordanceName, boolean concordant, String sortingProperty)
    {
        HashMap<Comparable,String> percentAgentCondomlessYears = new HashMap<Comparable,String>() ;
        //HashMap<Object,Number[]> percentAgentCondomlessYears = new HashMap<Object,Number[]>() ;
    
        int maxCycles = getMaxCycles() ;
            
        int endCycle ;
        
        for (int year = 0 ; year < backYears ; year++ )
        {
            String yearlyNumberAgentsEnteredRelationship ;

            endCycle = maxCycles - year * DAYS_PER_YEAR ;
            yearlyNumberAgentsEnteredRelationship 
                = preparePercentAgentCondomlessReport(relationshipClassNames, 0, 0, DAYS_PER_YEAR, endCycle, concordanceName, concordant, sortingProperty);

            // for (int classIndex = 0 ; classIndex < relationshipClassNames.length ; classIndex++ )
            //     yearlyNumberAgentsEnteredRelationship[classIndex] = percentAgentCondomlessRecord.get(relationshipClassNames[classIndex]) ;

            percentAgentCondomlessYears.put(lastYear - year, yearlyNumberAgentsEnteredRelationship) ;
        }
        // LOGGER.info(percentAgentCondomlessYears.toString()) ;

        return percentAgentCondomlessYears ;
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
    public ArrayList<String> prepareCondomUseReport()
    {
        ArrayList<String> condomUseReport = new ArrayList<String>() ;
        int opportunities ;
        int usages ;
        double proportion ;
        int[] condomData ;
        String reportOutput ;
        
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
            for (String record : input)
            {
                condomData = COUNT_VALUE_INCIDENCE(CONDOM,TRUE,record,0) ;
                usages = condomData[0] ;
                opportunities = condomData[1] ;
                proportion = ((double) usages)/opportunities ;
                reportOutput = Reporter.ADD_REPORT_PROPERTY("usages", usages) ;
                reportOutput += Reporter.ADD_REPORT_PROPERTY("opportunities", opportunities) ;
                reportOutput += Reporter.ADD_REPORT_PROPERTY("proportion", proportion) ;
                condomUseReport.add(reportOutput) ;
            }
        
        return condomUseReport ;
    }
    
    /**
     * WARNING: If this quantity is of particular importance then consider rerunning
     * the simulation including sexual encounters in which no transmission can take place.
     * @param backYears
     * @param lastYear
     * @return Year-by-year records of how many times a condom was used, how many opportunities 
     * there were to use one, and their ratio.
     */
    public ArrayList<String> prepareYearsCondomUseRecord(int backYears, int lastYear) 
    {
        ArrayList<String> condomUseRecordYears = new ArrayList<String>() ;

        // Whether to save this Report to file
        boolean writeLocal = WRITE_REPORT ;
        // Do not save subreports
        WRITE_REPORT = false ;

        //Count from the last cycle of the simulation.
        int maxCycles = getMaxCycles() ;

        for (int year = 0 ; year < backYears ; year++ )    // year is number of years previously
            condomUseRecordYears.add(0,prepareFinalCondomUseRecord(year, 0, DAYS_PER_YEAR, maxCycles)) ;
        
        //if (writeLocal)
          //  WRITE_CSV(condomUseRecordYears, "Year", "condom_use", simName, getFolderPath()) ;
        WRITE_REPORT = writeLocal ;

        return condomUseRecordYears ;
    }
    
    /**
     * WARNING: If this quantity is of particular importance then consider rerunning
     * the simulation including sexual encounters in which no transmission can take place.
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param endCycle
     * @return (String) report of how many times a condom was used, how many opportunities 
     * there were to use one, and their ratio.
     */
    public String prepareFinalCondomUseRecord(int backYears, int backMonths, int backDays, int endCycle)
    {
        endCycle -= backYears * DAYS_PER_YEAR ;
        ArrayList<String> finalCondomUseReport = getBackCyclesReport(0, backMonths, backDays, endCycle) ;
        
        
        String reportOutput ;
        int[] condomData ;
        int usages = 0;
        int opportunities = 0 ;
        double proportion ;
        
        for (String finalCondomUseRecord : finalCondomUseReport)
        {
            condomData = COUNT_VALUE_INCIDENCE(CONDOM,TRUE,finalCondomUseRecord,0) ;
            usages += condomData[0] ;
            opportunities += condomData[1] ;
        }
        proportion = ((double) usages)/opportunities ;
        reportOutput = Reporter.ADD_REPORT_PROPERTY("usages", usages) ;
        reportOutput += Reporter.ADD_REPORT_PROPERTY("opportunities", opportunities) ;
        reportOutput += Reporter.ADD_REPORT_PROPERTY("proportion", proportion) ;
        //finalCondomUseReport.add(reportOutput) ;
        
        //if (WRITE_REPORT)
          //  WRITE_CSV(finalCondomUse, "Site", new String[] {"incidence","positivity"}, "finalNotifications", simName, getFolderPath()) ;
        return reportOutput ;
    }
    
    /**
     * TODO: Implement complete census of Agents.
     * @param census
     * @return (ArrayList) Report on combinations of combinations of condom use
 withCondom either seroSorting or seroPositioning.
     */
    public ArrayList<String> prepareProtectionReport(ArrayList<String> census)
    {
        ArrayList<String> protectionReport = new ArrayList<String>() ;
        String protectionRecord ;
        
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        HashMap<Object,String[]> relationshipAgentReport = relationshipReporter.prepareRelationshipAgentReport() ;
                //(HashMap<Object,String[]>) getReport("relationshipAgent",relationshipReporter) ; //  
        
        int condomOnly ;
        int onlySeroSort ;
        int onlySeroPosition ;
        int condomSeroSort ;
        int condomSeroPosition ;
        int unprotected ;
        int total ;
        boolean finished ;
        
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
                agentValue = EXTRACT_BOUNDED_STRING(AGENTID,record,agentIndex) ;
                agentKey = EXTRACT_VALUE(AGENTID,agentValue) ;
                agentProperties.put(agentKey, agentValue) ;
                agentIndex = record.indexOf(AGENTID,agentIndex + 1) ;
            }
        }
        //LOGGER.log(Level.INFO, "{0}", census);
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
                encounters = EXTRACT_ARRAYLIST(record,RELATIONSHIPID) ;
                for (String encounter : encounters)
                {
                    seroPosition = false ;
                    seroSort = false ;
                    agentIds = relationshipAgentReport.get(EXTRACT_VALUE(RELATIONSHIPID,encounter)) ;
                    finished = !(agentProperties.containsKey(agentIds[0]) && agentProperties.containsKey(agentIds[1])) ;
                    for (String agentId : agentIds)
                    {
                        // if (!(agentProperties.containsKey(agentId)))
                        //     LOGGER.info("Missing agentId " + agentId); 
                    
                        if (EXTRACT_VALUE("seroSort",agentProperties.get(agentId)).equals(TRUE))
                        {
                            seroSort = true ;
                            break ;
                        }
                        else if (EXTRACT_VALUE("seroPosition",agentProperties.get(agentId)).equals(TRUE))
                        {
                            seroPosition = true ;
                            break ;
                        }
                    }

                    // Get condom use for each Urethral contact and combine outcomes.
                    contacts = EXTRACT_ARRAYLIST(encounter,CONTACT) ;
                    for (String contact : contacts)
                    {
                        if (contact.indexOf(CONDOM) < 0)
                            continue ;
                        total++ ;
                        condom = TRUE.equals(EXTRACT_VALUE(CONDOM,contact)) ;
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
                protectionRecord = Reporter.ADD_REPORT_PROPERTY("condomOnly",((double) condomOnly)/total) ;   
                protectionRecord += Reporter.ADD_REPORT_PROPERTY("onlySeroPosition",((double) onlySeroPosition)/total) ;
                protectionRecord += Reporter.ADD_REPORT_PROPERTY("onlySeroSort",((double) onlySeroSort)/total) ;
                protectionRecord += Reporter.ADD_REPORT_PROPERTY("condomSeroPosition",((double) condomSeroPosition)/total) ;
                protectionRecord += Reporter.ADD_REPORT_PROPERTY("condomSeroSort",((double) condomSeroSort)/total) ;
                protectionRecord += Reporter.ADD_REPORT_PROPERTY("unprotected",((double) unprotected)/total) ;
                protectionReport.add(protectionRecord) ;
            }    // report
        }
        // LOGGER.log(Level.INFO, "{0}", protectionReport);
        return protectionReport ;    
    }
        
    /**
     * TODO: Replace ArrayList withCondom set.
     * @return (HashMap) key is the transmitting agentId and entries are receiving agentIds
     */
    public HashMap<Comparable,ArrayList<Comparable>> prepareAgentToAgentRecord()
    {
        HashMap<Comparable,ArrayList<Comparable>> transmissionRecord = new HashMap<Comparable,ArrayList<Comparable>>() ;
        
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        HashMap<Object,String[]> relationshipAgentReport = relationshipReporter.prepareRelationshipAgentReport() ;
                //(HashMap<Object,String[]>) getReport("relationshipAgent",relationshipReporter) ; //   
        
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
                agentIdPair = relationshipAgentReport.get(EXTRACT_VALUE(RELATIONSHIPID,encounterString,0)) ;
                
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
                        Reporter.UPDATE_HASHMAP(Integer.valueOf(agentIdPair[0]), Integer.valueOf(agentIdPair[1]), transmissionRecord) ;
                    else    // falseIndex < trueIndex
                        Reporter.UPDATE_HASHMAP(Integer.valueOf(agentIdPair[1]), Integer.valueOf(agentIdPair[0]), transmissionRecord) ;
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
    public HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> prepareAgentToAgentReport()
    {
        HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> objectReport = 
                            new HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>>() ;
        
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,getFolderPath()) ;
        HashMap<Object,String[]> relationshipAgentReport = relationshipReporter.prepareRelationshipAgentReport() ; 
        // (HashMap<Object,String[]>) getReport("relationshipAgent",relationshipReporter) ;
        
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
                agentIdPair = relationshipAgentReport.get(EXTRACT_VALUE(RELATIONSHIPID,encounterString,0)) ;
                
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
                        objectReport = Reporter.UPDATE_HASHMAP(agentIdPair[0], agentIdPair[1], cycle, objectReport) ;
                        break ;
                    }
                    else    // falseIndex < trueIndex
                    {
                        objectReport = Reporter.UPDATE_HASHMAP(agentIdPair[1], agentIdPair[0], cycle, objectReport) ;
                        break ;
                    }
                    //contactIndex = encounterString.indexOf(CONTACT,contactIndex+1);
                }
            }
        }
        //return HASHMAP_HASHMAP_NUMBER(objectReport) ;
        return objectReport ;
    }
    
    /**
     * This method makes use of agentToAgentReport mapping agentId0 to agentId1 
     * to Array of cycles in which transmission occurred.
     * @return HashMap agentId to number of times Agent transmitted disease.
     */
    public HashMap<Comparable,Integer> prepareAgentTransmissionCountReport()
    {
        HashMap<Comparable,Integer> agentTransmissionCountReport 
                = new HashMap<Comparable,Integer>() ;
        
        HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> agentToAgentReport 
                = prepareAgentToAgentReport() ;
        
        HashMap<Comparable,ArrayList<Comparable>> agentToAgentRecord ; 
        
        for (Comparable agentId : agentToAgentReport.keySet())
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
    public HashMap<Comparable,Number> prepareNumberAgentTransmissionReport()
    {
        HashMap<Comparable,Number> numberAgentTransmissionReport = new HashMap<Comparable,Number>() ;

        HashMap<Comparable,Integer> agentTransmissionCountReport = prepareAgentTransmissionCountReport() ;
        
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
    public HashMap<Comparable,HashMap<Comparable,Number>> prepareNumberAgentTransmissionReport(String sortingProperty)
    {
        HashMap<Comparable,HashMap<Comparable,Number>> numberAgentTransmissionReport = new HashMap<Comparable,HashMap<Comparable,Number>>() ;

        HashMap<Comparable,Integer> agentTransmissionCountReport = prepareAgentTransmissionCountReport() ;
        HashMap<Comparable,Number> transmissionReport ;
        
        PopulationReporter sortingReporter = new PopulationReporter(simName,getFolderPath()) ;
        HashMap<Object,Object> sortingReport = sortingReporter.sortedAgentIds(sortingProperty) ;
        // LOGGER.log(Level.INFO, "{0}", sortingReport);
        HashMap<Comparable,HashMap<Comparable,Integer>> sortedAgentTransmissionCountReport 
                = SORT_REPORT(agentTransmissionCountReport, sortingReport) ;
        
        // Find highest value to count down from amongst all sorting variables
        ArrayList<Integer> agentTransmissionCountList = new ArrayList<Integer>() ;
        for (HashMap<Comparable,Integer> agentTransmissionCount : sortedAgentTransmissionCountReport.values()) 
            agentTransmissionCountList.addAll(agentTransmissionCount.values()) ;
        int maxValue = Collections.max(agentTransmissionCountList) ;
            
        for (Comparable sortingKey : sortedAgentTransmissionCountReport.keySet())
        {
            transmissionReport = new HashMap<Comparable,Number>() ;
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
    public HashMap<Comparable,Number> prepareCumulativeAgentTransmissionReport()
    {
        HashMap<Comparable,Number> cumulativeAgentTransmissionReport = new HashMap<Comparable,Number>() ;

        HashMap<Comparable,Integer> agentTransmissionCountReport = prepareAgentTransmissionCountReport() ;
        
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
    public HashMap<Comparable,Number> prepareFromSiteToSiteReport(String[] siteNames)
    {
        return prepareFromSiteToSiteReport(siteNames, 0) ;
    }
    
    /**
     * @param siteNames (String[]) names of body sites in sexual contact
     * @return String[] report of sexual contacts where STI transmission occurred
     */    
    public HashMap<Comparable,Number> prepareFromSiteToSiteReport(String siteNames[], int startCycle)
    {
        // Output HashMap
        HashMap<Comparable,Number> fromSiteToSiteReport = new HashMap<Comparable,Number>() ;
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
        
        // report String
        String report ;
        
        // Report of encounters showing contacts resulting in transmission.
        ArrayList<String> transmissionReport = prepareTransmissionReport() ;

        // Cycle through reports
        for (int reportNb = 0 ; reportNb < transmissionReport.size() ; reportNb += outputCycle )
        {
            report = transmissionReport.get(reportNb) ;
            // Extract contacts in which transmission occurs
            
            // Cycle through contact: substrings, noting fromSite, toSite where 
            // transmission occurs
            //LOGGER.info(report);
            for (contactIndex = 0 ; contactIndex >= 0 ; contactIndex = INDEX_OF_PROPERTY(CONTACT,contactIndex+1,report) )
            {
                contactString = EXTRACT_BOUNDED_STRING(CONTACT,report,contactIndex) ;
                
                // This reset is needed here
                fromName = "" ;
                toName = "" ;
                
                //Cycle through all possible site names
                for (String name0 : siteNames)
                {
                    // ! site name in contactString
                    if (! contactString.contains(name0))
                        continue ;
                    //nameIndex = Reporter.INDEX_OF_PROPERTY(name0,contactString) ;
                    value0 = EXTRACT_VALUE(name0,contactString) ;
                    
                    for (String name1 : siteNames)
                    {
                        // nameIndex+1 because both Sites might have the same name
                        //nameIndex = Reporter.INDEX_OF_PROPERTY(name0,nameIndex+1,contactString) ;
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
                    if (toName.isEmpty())  // matching sites in transmission
                    {
                        toName = name0 ;
                        fromName = name0 ;
                    }
                
                    key = fromName + " to " + toName ;
                    fromSiteToSiteReport = INCREMENT_HASHMAP(key,fromSiteToSiteReport) ;
                    break ;
                }
            }
        }
        if (WRITE_REPORT)
            WRITE_CSV(fromSiteToSiteReport, "site_to_site", "transmissions", "site to site transmissions", simName, REPORT_FOLDER) ;
        //             WRITE_CSV_STRING(fromSiteToSiteReport, categoryName, reportName, nameSimulation, REPORT_FOLDER) ;
        
        return fromSiteToSiteReport ;
    }
		
    /**
     * From ArrayList of doublets puts agentIds into HashMap whose keys are the agentIds
     * and values are arrays of their partners Ids.
     * @param pairArray ArrayList<String[]> of agentId doublets indicating sexual encounters 
     * @return partnerMap - HashMap indicating partnerIds of each agent (key: agentId)
     */
    private HashMap<Comparable,ArrayList<Comparable>> agentPartners(ArrayList<String[]> pairArray)
    {
        HashMap<Comparable,ArrayList<Comparable>> partnerMap = new HashMap<Comparable,ArrayList<Comparable>>() ;
        String agentNb0 ;
        String agentNb1 ;
        for (String[] pairString : pairArray)
        { 
            agentNb0 = pairString[0] ;
            agentNb1 = pairString[1] ;

            UPDATE_HASHMAP(agentNb0,agentNb1,partnerMap) ;
            UPDATE_HASHMAP(agentNb1,agentNb0,partnerMap) ;

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
        for (int indexStart = 0 ; indexStart >= 0 ; indexStart = INDEX_OF_PROPERTY(RELATIONSHIPID,indexStart+1, record) )
        {
            // Encounter to study
            encounterString = extractEncounter(record, indexStart) ;

            // Skip to next loop if no contact
            indexContact = INDEX_OF_PROPERTY(CONTACT,encounterString) ;
            if (indexContact < 0)
                continue ;
            
            String encounterOpening = encounterString.substring(0, indexContact ) ;
            String encounterOutput = "" ;

            // check contacts for desired value of propertyName
            contactString = BOUNDED_STRING_BY_VALUE(propertyName,value,CONTACT,encounterString) ; 
            encounterOutput += contactString ;
                
            // Only include encounter in reportOutput if any of its contacts are included 
            if (!encounterOutput.isEmpty())
                methodOutput += encounterOpening + encounterOutput ; // Include agentId

        }
        // If no positive cases are returned
        if (methodOutput.isEmpty()) 
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
        for (int indexStart = 0 ; indexStart >= 0 ; indexStart = INDEX_OF_PROPERTY(RELATIONSHIPID,indexStart+1, report) )
        {
            // Encounter to study
            encounterString = extractEncounter(report, indexStart) ;

            // Initialise reportOutput for encounter
            indexContact = INDEX_OF_PROPERTY(CONTACT,encounterString) ;
            String encounterOpening = encounterString.substring(0, indexContact ) ;
            String encounterOutput = "" ;

            // check contacts for desired propertyName
            contactString = BOUNDED_STRING_BY_CONTENTS(propertyName,CONTACT,encounterString) ; 
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
        return EXTRACT_BOUNDED_STRING(CONTACT, encounter, indexStart) ;
    }

    /**
     * 
     * @param String string
     * @return substrings corresponding to encounters
     */
    private String extractEncounter(String string, int indexStart)
    {
        return EXTRACT_BOUNDED_STRING(RELATIONSHIPID, string, indexStart) ;
    }

    
}
