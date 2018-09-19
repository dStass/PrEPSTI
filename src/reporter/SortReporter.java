/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter;

import agent.MSM;
/*import reporter.* ;
import reporter.EncounterReporter ;
//import reporter.ScreeningReporter ;
import reporter.RelationshipReporter ;
import reporter.PopulationReporter ;
*/

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
    
    public SortReporter()
    {
        
    }
        
    public SortReporter(String simname, Reporter unsortedReporter, Reporter sortingReporter) 
    {
        super(simname,unsortedReporter.input) ;
        
        this.unsortedReporter = unsortedReporter ;
        this.sortingReporter = sortingReporter ;
    }
/**
     * @param simName (String) Root of simulation name.
     * @param reportFilePath (String) Path to saved files.
     * @param unsortedName (String) Reporter type whose output needs to be sorted.
     * @param sortingName (String) Reporter type used to sort.
     */
    public SortReporter(String simName, String reportFilePath, String unsortedName, String sortingName)
    {
        String unsortedClassName = "" ;
        if ("infection".equals(unsortedName))
            unsortedClassName = "Screening" ;
        else
            unsortedClassName = unsortedName.substring(0,1).toUpperCase() + unsortedName.substring(1).toLowerCase() ;
        unsortedClassName = "reporter." + unsortedClassName + "Reporter" ;
        String sortingClassName ;
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
     * TODO: Sort out Object vs String both here and related Methods.
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
     * @return HashMap of age maps to mean number of Relationships entered into by 
     * that age. Calculated from final age of each Agent, meaning age-at-death or
     * age at end of simulation.
     */
    public HashMap<Object,HashMap<Object,Number>> prepareAgeNumberEnteredRelationshipRecord(String[] relationshipClassNames)
    {
        HashMap<Object,HashMap<Object,Number>> ageNumberEnteredRelationshipRecord 
                = new HashMap<Object,HashMap<Object,Number>>() ;
        for (String relationshipClassName : relationshipClassNames)
            ageNumberEnteredRelationshipRecord.put(relationshipClassName, new HashMap<Object,Number>()) ;
        

        // key:age value:ArrayList of new Relationships for each Agent, sums to 
        //total number of new Relationships formed by that age.
        HashMap<Object,HashMap<Object,ArrayList<Object>>> ageEnteredRelationshipRecord 
                = new HashMap<Object,HashMap<Object,ArrayList<Object>>>() ;
        for (String relationshipClassName : relationshipClassNames)
            ageEnteredRelationshipRecord.put(relationshipClassName, new HashMap<Object,ArrayList<Object>>()) ;
        
        //key:agentId value: Age for final record in report
        HashMap<Object,Integer> sortAgeRecord = ((PopulationReporter) sortingReporter).sortAgeRecord() ;
        
        // Each record is a HashMap indicating new relationshipIds for relevant (key) Agents
        ArrayList<HashMap<Object,HashMap<Object,ArrayList<Object>>>> agentsEnteredRelationshipReport 
                = ((RelationshipReporter) unsortedReporter).prepareAgentsEnteredRelationshipReport(relationshipClassNames) ;
        
        // Find Relationships entered by Agents with given final age.
        for (HashMap<Object,HashMap<Object,ArrayList<Object>>> enteredRelationshipRecord : agentsEnteredRelationshipReport)
            for (Object relationshipClassName : enteredRelationshipRecord.keySet())
                for (Object agentId : enteredRelationshipRecord.get(relationshipClassName).keySet())
                    ageEnteredRelationshipRecord.put(relationshipClassName,
                            updateHashMap(sortAgeRecord.get(agentId),
                                    enteredRelationshipRecord.get(relationshipClassName).get(agentId).size(),ageEnteredRelationshipRecord.get(relationshipClassName))) ; 

        for (Object relationshipClassName : ageEnteredRelationshipRecord.keySet())
            for (Object ageKey : ageEnteredRelationshipRecord.get(relationshipClassName).keySet())
            {
                ArrayList<Object> ageRecord = (ArrayList<Object>) ageEnteredRelationshipRecord.get(relationshipClassName).get(ageKey) ;
                //LOGGER.log(Level.INFO, "ageKey:{0} ageRecord:{1}", new Object[] {ageKey,ageRecord});

                // Count Agents of final age ageKey 
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
                ageNumberEnteredRelationshipRecord.get(relationshipClassName).put(ageKey,mean) ;
            }
        return ageNumberEnteredRelationshipRecord ;
    }
    
    /**
     * 
     * @param partnerCount (int) the maximum number of new partners in the given time frame.
     * @param binSize (int) the range in partner number per bin.
     * @param backYears (int) the number of previous years to consider.
     * @param backMonths (int) plus the number of previous months to consider.
     * @param backDays (int) plus the number of previous days to consider.
     * @return Report of incidence of STI among MSM with partnerCount new Relationships in the 
     * past backYears years, backMonths months and backDays days.
     */
    public HashMap<Object,Number[]> prepareSortIncidenceReport(int partnerCount, int binSize, int backYears, int backMonths, int backDays)
    {
        HashMap<Object,Number[]> sortIncidenceReport = new HashMap<Object,Number[]>() ;
        String label ;
        // Initialisation
        int finalRecordNb = Integer.valueOf(sortingReporter.getMetaDatum("Community.MAX_CYCLES")) - 1 ;
        int recordIndex = finalRecordNb ;    // TODO: Generalize to arbitrary recordIndex.
        
        // Digits in number of partners for ordering bin labels
        int totalDigits = (int) Math.log10(partnerCount) + 1 ;
        
        // Report of numbers of new Relationships in given time to agentIds
        ArrayList<ArrayList<Object>> screenSortAgentNewPartnersReport 
            = prepareScreenSortAgentNewPartnersReport(partnerCount, backYears, backMonths, backDays, recordIndex) ;

        // Reference population for determining relative bar widths
        double referencePopulation = 0 ;
        for ( int binIndex = 0 ; binIndex < binSize ; binIndex++ )
            referencePopulation += screenSortAgentNewPartnersReport.get(binIndex).size() ;
        
        // Report of agentIds maps to number of incidents in given time
        HashMap<Object,Number> sortAgentIncidentReport 
                = prepareSortAgentIncidentsReport(backYears, backMonths, backDays) ;
        // HashMap (binned) numbers of new Relationships maps to total incidence
        // of all corresponding agentIds
        
        for (int partnerIndex = 0 ; partnerIndex <= partnerCount ; partnerIndex = partnerIndex + binSize )
        {
            int incidents = 0 ;
            double population = 0 ;
            
            //Adjust final bin size if 
            if ((partnerCount + 1) < (partnerIndex + binSize))
                binSize = partnerCount + 1 - partnerIndex ;
            for ( int binIndex = 0 ; binIndex < binSize ; binIndex++ )
            {
                ArrayList<Object> agentIdList = screenSortAgentNewPartnersReport.get(partnerIndex + binIndex) ;
                population += agentIdList.size() ;
                for (Object agentId : agentIdList)
                    if (sortAgentIncidentReport.containsKey(agentId))    // if Agent had incidents of STI
                        incidents += sortAgentIncidentReport.get(agentId).intValue() ;
            }
            
            // Label bin
            label = String.valueOf(partnerIndex) ;
            // Pad start of label with spaces for proper ordering
            int nbDigits = label.length() ;
            for (int digitIndex = nbDigits ; digitIndex < totalDigits ; digitIndex++ )
                label = " ".concat(label) ;
            
            if (binSize > 1)
            {
                if (partnerCount > (partnerIndex + binSize))
                    label += "-" + String.valueOf(partnerIndex + binSize - 1) ;
                else if (partnerCount > (partnerIndex))
                    label += "-" + String.valueOf(partnerCount - 1) ;
            }
            //LOGGER.log(Level.INFO, "{0} {1} {2}", new Object[] {referencePopulation,population/referencePopulation,((double) incidents)/population});
            sortIncidenceReport.put(label, new Number[] {population/referencePopulation,((double) incidents)/population}) ;
        }
        
        return sortIncidenceReport ;
    }
    
    /**
     * 
     * @param maxConcurrency (int) the maximum number of new partners in the given time frame.
     * @param backYears (int) the number of previous years to consider.
     * @param backMonths (int) plus the number of previous months to consider.
     * @param backDays (int) plus the number of previous days to consider.
     * @return Report of incidence of STI in the past backYears years, backMonths months and backDays days
     * among MSM with concurrency between 0 and maxConcurrency .
     */
    public HashMap<Object,Number[]> prepareSortConcurrencyIncidenceReport(int maxConcurrency, int backYears, int backMonths, int backDays)
    {
        HashMap<Object,Number[]> sortIncidenceReport = new HashMap<Object,Number[]>() ;
        String label ;
        // Initialisation
        int finalRecordNb = Integer.valueOf(sortingReporter.getMetaDatum("Community.MAX_CYCLES")) - 1 ;
        int recordIndex = finalRecordNb ;    // TODO: Generalize to arbitrary recordIndex.
        
        // Digits in number of partners for ordering bin labels
        int totalDigits = (int) Math.log10(maxConcurrency) + 1 ;
        
        // Report of numbers of new Relationships in given time to agentIds
        ArrayList<ArrayList<Object>> sortAgentConcurrencyReport 
            = prepareAgentConcurrencyReport(maxConcurrency, backYears, backMonths, backDays, recordIndex) ;
        LOGGER.log(Level.INFO, "{0}", sortAgentConcurrencyReport);

        // Reference population for determining relative bar widths
        double referencePopulation = sortAgentConcurrencyReport.get(0).size() ;
        
        // Report of agentIds maps to number of incidents in given time
        HashMap<Object,Number> sortAgentIncidentReport 
                = prepareSortAgentIncidentsReport(backYears, backMonths, backDays) ; // Removed binSize=1
        // HashMap (binned) numbers of new Relationships maps to total incidence
        // of all corresponding agentIds
        
        int incidents = 0 ;
        double population ;
        for (int concurrencyIndex = 1 ; concurrencyIndex <= maxConcurrency ; concurrencyIndex++ )
        {
            ArrayList<Object> agentIdList = sortAgentConcurrencyReport.get(concurrencyIndex - 1) ;
            population = agentIdList.size() ;
            for (Object agentId : agentIdList)
                if (sortAgentIncidentReport.containsKey(agentId))    // if Agent had incidents of STI
                    incidents = sortAgentIncidentReport.get(agentId).intValue() ;
            //LOGGER.log(Level.INFO, "{0} {1} {2}", new Object[] {referencePopulation,population/referencePopulation,((double) incidents)/population});
            sortIncidenceReport.put(concurrencyIndex, new Number[] {population/referencePopulation,((double) incidents)/population}) ;
        }
        return sortIncidenceReport ;
    }
    
    private ArrayList<ArrayList<Object>> prepareAgentConcurrencyReport(int maxConcurrency, int backYears, int backMonths, int backDays, int recordIndex)
    {
        ArrayList<ArrayList<Object>> agentConcurrencyReport = new ArrayList<ArrayList<Object>>() ;
        for (int concurrency = 0 ; concurrency < maxConcurrency ; concurrency++ )    // No zero concurrency!
            agentConcurrencyReport.add(new ArrayList<Object>()) ;
        
        ArrayList<Object> agentsAlive = ((PopulationReporter) sortingReporter).prepareAgentsAliveRecord(recordIndex) ;
        
        ArrayList<String> birthReport = ((PopulationReporter) sortingReporter).prepareBirthReport() ;
        for (String record : birthReport)
        {
            ArrayList<String> agentList = extractArrayList(record,AGENTID) ;
            for (String agentRecord : agentList)
            {
                Object agentId = extractValue(AGENTID,agentRecord) ;
                if (!agentsAlive.contains(agentId))
                    continue ;
                int concurrency = Integer.valueOf(extractValue("concurrency",agentRecord)) ;
                agentConcurrencyReport.get(concurrency - 1).add(agentId) ;    // -1 due to concurrency != 0
            }
        }
        return agentConcurrencyReport ;
    }
    
    /**
     * 
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return report where agentId maps to incidents in the last backYears. backMonths and backDays
     */
    public HashMap<Object,Number> prepareSortAgentIncidentsReport(int backYears, int backMonths, int backDays)
    {
        HashMap<Object,Number> agentIncidenceCount = new HashMap<Object,Number>() ;
        
        int daysPerYear = 365 ;
        int daysPerMonth = 31 ;
        
        int finalRecordNb = Integer.valueOf(sortingReporter.getMetaDatum("Community.MAX_CYCLES")) - 1 ;
        int recordIndex = finalRecordNb ;    // TODO: Generalize to arbitrary recordIndex.
        
        // transmitting agentId maps to receiving agentId maps to (ArrayList) cycle of infection
        HashMap<Object,HashMap<Object,ArrayList<Object>>> agentToAgentReport = ((EncounterReporter) unsortedReporter).prepareAgentToAgentReport() ;
        // Inverts to Cycle maps to infecting agentId maps to (ArrayList) receiviing AgentIds
        HashMap<Object,HashMap<Object,ArrayList<Object>>> invertedHashMap = EncounterReporter.invertHashMapHashMap(agentToAgentReport) ;
        
        ArrayList<ArrayList<Object>> unsortedReport = ((EncounterReporter) unsortedReporter).prepareReceiveCountReport(invertedHashMap) ;
        
        // New partners per agentId

        // Modify backYears if necessary so you don't go past beginning of simulation
        if (daysPerYear*backYears > recordIndex)
            backYears = recordIndex/daysPerYear ;
        // Modify backYears if necessary so you don't go past beginning of simulation
        if ((daysPerYear*backYears + daysPerMonth*backMonths) > recordIndex)
            backMonths = (recordIndex - daysPerYear*backYears)/daysPerMonth ;
        
        int backCycles = backYears*daysPerYear + backMonths*daysPerMonth + backDays ;
        if (backCycles > recordIndex)
            backCycles = (recordIndex - 1) ;
        
        // Count new relationships for each Agent over past backYears years
        for (int cycle = 0 ; cycle < backCycles ; cycle++)
        {
            ArrayList<Object> receivingAgentIds = unsortedReport.get(recordIndex - cycle) ;
            for (Object agentId : receivingAgentIds)
                agentIncidenceCount = incrementHashMap(agentId,agentIncidenceCount) ;
        }
        return agentIncidenceCount ;
    }

    
    /**
     * 
     * @param maxPartnerCount
     * @param binSize (int) Range of partner numbers in each bin
     * @param backYears
     * @param backMonths
     * @param backDays
     * @return prevalence report including only those Agents with the given number of
     * new Relationships in the last backYears
     */
    public ArrayList<String> prepareSortPrevalenceReport(int maxPartnerCount, int binSize, int backYears, int backMonths, int backDays)
    {
        ArrayList<String> prevalenceReport = new ArrayList<String>() ;
        int finalRecordNb = Integer.valueOf(sortingReporter.getMetaDatum("Community.MAX_CYCLES")) - 1 ;
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
        
        ArrayList<Object> agentIdList ;
        
        // FIXME: Only considers prevalences in final cycle.
        int recordIndex = finalRecordNb ;
        String record = unsortedReporter.getFinalRecord() ;
        ArrayList<String> infections = extractArrayList(record,AGENTID) ;
                
        // Nb new Relationships maps to agentIds
        ArrayList<ArrayList<Object>> screenSortAgentNewPartnersReport 
            = prepareScreenSortAgentNewPartnersReport(maxPartnerCount, backYears, backMonths, backDays, recordIndex) ;

        // Count through number of partners
        for (int partnerIndex = 0 ; partnerIndex <= maxPartnerCount ; partnerIndex += binSize )
        {
            population = 0 ;
            nbInfected = 0 ;
            nbSymptomatic = 0 ;
            if ((partnerIndex + binSize) > (maxPartnerCount + 1))    // If next bin goes past maxPartnerCount
                binSize = maxPartnerCount + 1 - partnerIndex ;       // adjust binSize accordingly (binSize not used again)
            for (int binIndex = 0 ; binIndex < binSize ; binIndex++ )
            {
                // Want prevalence only among agents with given number of partners.
                agentIdList = screenSortAgentNewPartnersReport.get(partnerIndex+binIndex) ;
                
                population += agentIdList.size() ;
                // Determine nb of infected
                for (String infection : infections)
                {
                    if (!agentIdList.contains(extractValue(AGENTID,infection))) 
                        continue ;
                    nbInfected++ ;
                    // Determine nb of symptomatic infected
                    for (String siteName : siteNames)
                        if (compareValue(siteName,TRUE,infection))
                        {
                            nbSymptomatic++ ;
                            break ;
                        }
                }
                //LOGGER.info(record) ;
            }

            //population = Community.POPULATION ;
            if (population > 0)
            {
                entry = addReportProperty("prevalence",((double) nbInfected)/population) ;
                entry += addReportProperty("symptomatic",((double) nbSymptomatic)/population) ;
            }
            else
            {
                entry = addReportProperty("prevalence",0.0) ;
                entry += addReportProperty("symptomatic",0.0) ;
            }
            if (nbInfected > 0)
                entry += addReportProperty("proportion",((double) nbSymptomatic)/nbInfected) ;
            else 
                entry += addReportProperty("proportion",0.0) ;
            entry += addReportProperty("population",population) ;
            
            prevalenceReport.add(entry) ;
        }

        return prevalenceReport ;
    }
    
    /**
     * 
     * @param partnerCount
     * @param backYears
     * @return Number of new Relationships maps to agentIds
     */
    public ArrayList<ArrayList<Object>> prepareScreenSortAgentNewPartnersReport(int partnerCount, int backYears, int backMonths, int backDays, int recordIndex)
    {
        // number of partners maps to agentIds
        ArrayList<ArrayList<Object>> screenSortAgentNewPartnersReport = new ArrayList<ArrayList<Object>>() ;
        for (int count = 0 ; count <= partnerCount ; count++ )
            screenSortAgentNewPartnersReport.add(new ArrayList<Object>()) ;
        
        int daysPerYear = 365 ;
        int daysPerMonth = 31 ;
        
        ArrayList<ArrayList<Object>> sortingReport = ((RelationshipReporter) sortingReporter).prepareAgentCommenceReport() ;
        
        // New partners per agentId
        HashMap<Object,Number> agentCommenceCount = new HashMap<Object,Number>() ;

        // Modify backYears if necessary so you don't go past beginning of simulation
        if (daysPerYear*backYears > recordIndex)
            backYears = recordIndex/daysPerYear ;
        // Modify backMonths if necessary so you don't go past beginning of simulation
        if ((daysPerYear*backYears + daysPerMonth*backMonths) > recordIndex)
            backMonths = (recordIndex - daysPerYear*backYears)/daysPerMonth ;
        
        int backCycles = backYears*daysPerYear + backMonths*daysPerMonth + backDays ;
        if (backCycles >= recordIndex)
            backCycles = (recordIndex - 1) ;
        
        // Count new relationships for each Agent over past backYears years
        for (int cycle = 0 ; cycle < backCycles ; cycle++)
            for (Object agentId : sortingReport.get(recordIndex-cycle))
                agentCommenceCount = incrementHashMap(agentId,agentCommenceCount) ;
        //LOGGER.log(Level.INFO, "{0}", agentCommenceCount);

        PopulationReporter populationReporter = new PopulationReporter(sortingReporter.simName,sortingReporter.getFolderPath()) ;
        ArrayList<Object> agentsAlive = populationReporter.prepareAgentsAliveRecord(recordIndex) ;
        
        // Which Agents have had Array_position new Relationships in last backYears
        for (Object agentKey : agentsAlive)    
        {
            if (!agentCommenceCount.keySet().contains(agentKey))    // Agents with no new Relationships in given time
            {
                screenSortAgentNewPartnersReport.get(0).add(agentKey) ;    //LOGGER.log(Level.INFO, "{0}", agentKey);
                continue ;
            }
            int relationshipCount = agentCommenceCount.get(agentKey).intValue() ;
            if (relationshipCount <= partnerCount)
                screenSortAgentNewPartnersReport.get(relationshipCount).add(agentKey) ;
            else  // Had partnerCount or more new partners
                screenSortAgentNewPartnersReport.get(partnerCount).add(agentKey) ;
        }
        return screenSortAgentNewPartnersReport ;
    }
}
