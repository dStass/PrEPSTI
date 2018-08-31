/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter;

import community.Community ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap ;
import java.util.logging.Level;


/**
 *
 * @author MichaelWalker
 */
public class PopulationReporter extends Reporter {
    
    static String DEATH = "death" ;
    static String BIRTH = "birth" ;
    static String AGE = "age" ;
    static String START_AGE = "startAge" ;
    
    public PopulationReporter()
    {
        
    }
    
    public PopulationReporter(String simname, ArrayList<String> report) 
    {
        super(simname, report);
    }

    /**
     * FIXME: Passing and implementation of fileName not finalised.
     * @param simName
     * @param reportFilePath
     */
    public PopulationReporter(String simName, String reportFilePath)
    {
        super(simName, reportFilePath) ;
    }
    
    /**
     * 
     * @return Report showing population in each cycle.
     */
    public ArrayList<Object> preparePopulationReport()
    {
        ArrayList<Object> populationReport = new ArrayList<Object>() ;
        ArrayList<Integer> countBirthReport = new ArrayList<Integer>() ;
        
        ArrayList<ArrayList<Object>> agentBirthReport = prepareAgentBirthReport() ;
        ArrayList<ArrayList<Object>> agentDeathReport = prepareAgentDeathReport() ;
        
        int reportSize = agentBirthReport.size() ;
        
        Integer maxBirthId = Community.POPULATION - 1 ;    //TODO: Read from METADATA
        countBirthReport.add(maxBirthId + 1) ;    //TODO: Read from METADATA
            
        for (int recordIndex = 0 ; recordIndex < reportSize; recordIndex++ )
        {
            ArrayList<Object> birthRecordObject = agentBirthReport.get(recordIndex) ;
            ArrayList<String> birthRecord = new ArrayList<String>() ; 
            if (!birthRecordObject.isEmpty())
            {
                for (Object agentId : birthRecordObject)
                    birthRecord.add((String) agentId) ;
            
                // +1 Allows for numbering from 0
                maxBirthId = Integer.valueOf(Collections.max(birthRecord));
            }
            countBirthReport.add(maxBirthId+1) ;
        }
        
        // how many deaths?
        int nbDeaths = 0 ;
        for (int recordIndex = 0 ; recordIndex < reportSize ; recordIndex++ )
        {
            ArrayList<Object> deathRecord = agentDeathReport.get(recordIndex) ;
            nbDeaths += deathRecord.size() ;
            String record = "Population:" ;
            int currentValue = countBirthReport.get(recordIndex + 1) ;
            populationReport.add(record + String.valueOf(currentValue - nbDeaths)) ;
        }
        return populationReport ;
    }
    
    
    public String getInitialRecord()
    {
        String initialRecord = super.getInitialRecord() ;
        return initialRecord.substring(0, initialRecord.indexOf(DEATH)) ;
    }
    
    /**
     * 
     * @param sortingProperty
     * @return (HashMap) agentId maps to (String) value of sortingProperty
     */
    protected HashMap<Object,Object> sortedAgentIds(String sortingProperty)
    {
        HashMap<Object,Object> sortedHashMap = new HashMap<Object,Object>() ;
        
        ArrayList<String> birthReport = prepareBirthReport() ;
        
        for (String record : birthReport)
        {
            ArrayList<String> censusArray = extractArrayList(record,AGENTID) ;
            for (String birth : censusArray)
            {
                String agentId = extractValue(AGENTID,birth) ;
                String sortingValue = extractValue(sortingProperty,birth) ;
                sortedHashMap.put((Object) agentId, sortingValue) ;
            }
        }
        return sortedHashMap ;
    }
    
    /**
     * FIXME: Only works for final record.
     * @param recordNb
     * @return List of agentIds of Agents living at recordNb.
     */
    public ArrayList<Object> prepareAgentsAliveRecord(int recordNb)
    {
        ArrayList<Object> agentsAliveRecord = new ArrayList<Object>() ;
        
        int maxAgentId = getMaxAgentId() ;
        ArrayList<Object> agentsDeadRecord = prepareAgentsDeadRecord(recordNb) ;
        // Cycle through all born and keep those who haven't died.
        for (int agentAlive = 0 ; agentAlive <= maxAgentId ; agentAlive++ )
            if (!agentsDeadRecord.contains(String.valueOf(agentAlive)))
                agentsAliveRecord.add(String.valueOf(agentAlive)) ;
        
        return agentsAliveRecord ;
    }
    
    /**
     * TODO: Extend to allow specification of record number, currently fixed at final record.
     * @return (int) agentId of last Agent born.
     */
    public int getMaxAgentId()
    {
        ArrayList<String> populationReport = getFinalReport() ;
        
        String populationRecord ;
        int deathIndex ;
        String birthRecord = "" ;
        int birthIndex = -1 ;
        //Cycle backwards until final birth is found
        for( int recordIndex = populationReport.size() -1 ; birthIndex < 0 ; recordIndex-- )
        {
            populationRecord = populationReport.get(recordIndex);
            deathIndex = populationRecord.indexOf(DEATH);
            birthRecord = populationRecord.substring(0, deathIndex);
            birthIndex = birthRecord.lastIndexOf(AGENTID) ;
        }
        
        return Integer.valueOf(extractValue(AGENTID,birthRecord,birthIndex)) ;
    }
    
    /**
     * Cycles through deathReport up to recordNb to find agentIds of Agents who 
     * have died.
     * @param recordNb
     * @return List of agentIds of dead Agents.
     */
    public ArrayList<Object> prepareAgentsDeadRecord(int recordNb)
    {
        ArrayList<Object> agentsDeadRecord = new ArrayList<Object>() ;
        
        ArrayList<String> deathReport = prepareDeathReport() ;
        
        for (int recordIndex = 0 ; recordIndex < recordNb ; recordIndex++ )
        {
            String record = deathReport.get(recordIndex) ;
            //LOGGER.info(record);
            ArrayList<Object> deadAgentList = extractAllValues(AGENTID, record) ;
            agentsDeadRecord.addAll(deadAgentList) ;
        }
            
        return agentsDeadRecord ;
    }
    /**
     * 
     * @return ArrayList of ArrayLists of (String) agentIds of agents 'born'
     * in each cycle
     */
    public ArrayList<ArrayList<Object>> prepareAgentBirthReport()
    {
        ArrayList<ArrayList<Object>> agentBirthReport = new ArrayList<ArrayList<Object>>() ;
        
        ArrayList<String> birthReport = prepareBirthReport() ;
        
        // Zeroeth record left out to stop population swamping the plot.
        for (int reportNb = 1 ; reportNb < birthReport.size() ; reportNb++ )
        {
            String report = birthReport.get(reportNb) ;
            int startIndex = indexOfProperty("agendId",report) ;
            agentBirthReport.add(extractAllValues("agentId", report, startIndex)) ;
        }
        return agentBirthReport ;
    }
    
    /**
     * 
     * @return ArrayList of ArrayLists of (String) ages-at-birth of agents 'born'
     * in each cycle
     */
    public ArrayList<ArrayList<Object>> prepareAgeBirthReport()
    {
        ArrayList<ArrayList<Object>> ageBirthReport = new ArrayList<ArrayList<Object>>() ;
        
        ArrayList<String> birthReport = prepareBirthReport() ;
        
        for (int reportNb = 0 ; reportNb < birthReport.size() ; reportNb++ )
        {
            String report = birthReport.get(reportNb) ;
            int startIndex = indexOfProperty("age",report) ;
            ageBirthReport.add(extractAllValues("age", report, startIndex)) ;
        }
        return ageBirthReport ;
    }
    
    /**
     * 
     * @return ArrayList of ArrayLists of (String) agentIds of agents who died 
     * in each cycle
     */
    public ArrayList<ArrayList<Object>> prepareAgentDeathReport()
    {
        ArrayList<ArrayList<Object>> agentDeathReport = new ArrayList<ArrayList<Object>>() ;
        
        ArrayList<String> deathReport = prepareDeathReport() ;
        
        for (int recordNb = 0 ; recordNb < deathReport.size() ; recordNb++ )
        {
            ArrayList<Object> agentDeathRecord = new ArrayList<Object>() ;  //.clear();
            String record = deathReport.get(recordNb) ;
            //LOGGER.info(record);
            ArrayList<String> deathRecords = extractArrayList(record, DEATH) ;
            for (String deathRecord : deathRecords)
                agentDeathReport.add(extractAllValues(AGENTID,deathRecord,0)) ;
        }
        return agentDeathReport ;
    }
    
    /**
     * Assumes death report structure of death:agentId:x age:y agentId:x age:y ...
     * @return ArrayList of ArrayLists of (String) ages-at-death of agents who died 
     * in each cycle
     */
    public HashMap<Object,Integer> prepareAgeDeathReport()
    {
        ArrayList<ArrayList<Object>> ageDeathReport = new ArrayList<ArrayList<Object>>() ;
        
        HashMap<Object,Integer> agentAgeHashMap = new HashMap<Object,Integer>() ;
        
        int daysInYear = 365 ;
        
        ArrayList<String>  birthReport = prepareBirthReport() ;
        ArrayList<String>  deathReport = prepareDeathReport() ;
        
        ArrayList<Object> ageDeathRecord ;
        
        for (int reportNb = 0 ; reportNb < deathReport.size() ; reportNb++ )
        {
            ageDeathRecord = new ArrayList<Object>() ;  //.clear();
            String record = deathReport.get(reportNb) ;
            ArrayList<Object> agentIds = extractAllValues(AGENTID,record) ;
            for (Object agentId : agentIds)
                agentAgeHashMap.put(agentId, reportNb/daysInYear) ;
        }
        
        for (int recordNb = 0 ; recordNb < birthReport.size() ; recordNb++ )
        {
            String record = birthReport.get(recordNb) ;
            ArrayList<String> birthRecords = extractArrayList(record,AGENTID) ;
            for (String birthRecord : birthRecords )
            {
                String agentId = extractValue(AGENTID,birthRecord) ;
                if (agentAgeHashMap.keySet().contains(agentId))
                {
                    int birthAge = Integer.valueOf(extractValue(AGE,birthRecord)) ;
                    agentAgeHashMap.put(agentId, agentAgeHashMap.get(agentId) - recordNb/daysInYear + birthAge) ;
                }
            }
        }
        return agentAgeHashMap ;
    }
    
    /**
     * 
     * @return (HashMap) key is String.valueOf(age) and value is the number to
     * die at that age.
     */
    public HashMap<Object,Number> prepareAgeAtDeathReport()
    {
        HashMap<Object,Number> ageAtDeathMap = new HashMap<Object,Number>() ;
        
        // Contains age-at-death data
        HashMap<Object,Integer> ageDeathReport = prepareAgeDeathReport() ;
        
        for (Object agentId : ageDeathReport.keySet())
        {
            String ageString = String.valueOf(ageDeathReport.get(agentId)) ;
            ageAtDeathMap = incrementHashMap(ageString,ageAtDeathMap) ;
        }
        return ageAtDeathMap ;
    }
    
    /**
     * cycle maps to (ArrayList) agentIds
     * @return (ArrayList) report of (ArrayList) of agentId who dies in each cycle.
     */
    public ArrayList<ArrayList<Object>> prepareDeathsPerCycleReport()
    {
        ArrayList<ArrayList<Object>> deathsPerCycleReport = new ArrayList<ArrayList<Object>>() ;
        
        ArrayList<String> deathReport = prepareDeathReport() ;
        for (String record : deathReport)
        {
            ArrayList<String> stringArray = extractArrayList(record,AGENTID);
            deathsPerCycleReport.add((ArrayList<Object>) stringArray.clone()) ;
        }
        return deathsPerCycleReport ;
        
    }
    
    /**
     * 
     * @param startRecordNb
     * @param endRecordNb
     * @return List of agentIds of agents who died from startRecordNb to before
     * endRecordNb.
     */
    public ArrayList<Object> prepareDeathsDuringPeriodReport(int startRecordNb, int endRecordNb)
    {
        ArrayList<Object> deathsDuringPeriodReport = new ArrayList<Object>() ;
        
        ArrayList<String> deathReport = prepareDeathReport() ;
        
        for (int recordNb = startRecordNb ; recordNb < endRecordNb ; recordNb++ )
        {
            ArrayList<Object> stringArray = extractAllValues(AGENTID,deathReport.get(recordNb),0) ;
            deathsDuringPeriodReport.addAll(stringArray) ;
        }
        
        return deathsDuringPeriodReport ;
    }
    
    /**
     * 
     * @return Report listing deaths in each cycle.
     */
    private ArrayList<String> prepareDeathReport()
    {
        ArrayList<String> deathReport = new ArrayList<String>() ;
        
        String record ;
        int deathIndex ;
        String valueString ;

        boolean nextInput = true ; 
        
        while (nextInput)
        {
            for (int reportNb = 0 ; reportNb < input.size() ; reportNb += outputCycle )
            {
                record = input.get(reportNb) ;
                //LOGGER.log(Level.INFO, "{0} {1}", new Object[] {reportNb,record});
                deathIndex = indexOfProperty(DEATH,record) ;
                if (deathIndex < 0)
                    continue ;
                deathReport.add(record.substring(deathIndex)) ;
            }
        nextInput = updateReport() ;
        }
        return deathReport ;
    }
    
    public ArrayList<String> prepareBirthReport()
    {
        ArrayList<String> birthReport = new ArrayList<String>() ;
        
        String record ;
        int agentIndex ;
        String valueString ;

        boolean nextInput = true ; 
        
        while (nextInput)
        {
            for (int reportNb = 0 ; reportNb < input.size() ; reportNb += outputCycle )
            {
                record = input.get(reportNb) ;
                birthReport.add(record.substring(indexOfProperty("birth",record),indexOfProperty("death",record))) ;
            }
        nextInput = updateReport() ;
        }
        return birthReport ;
    }
    
    /**
     * For sorting final record according to Agent.getAge() .
     * @return HashMap (int) AgentId mapped to age.
     */
    public HashMap<Object,Integer> sortAgeRecord()
    {
        //HashMap<Object,ArrayList<Object>> sortAgeRecord = new HashMap<Object,ArrayList<Object>>() ;
        
        HashMap<Object,Integer> agentAgeHashMap = new HashMap<Object,Integer>() ;
        
        int daysInYear = 365 ;
        
        ArrayList<String>  birthReport = prepareBirthReport() ;
        ArrayList<String>  deathReport = prepareDeathReport() ;
        
        int nbCycles = birthReport.size() ;
        
        for (int recordIndex = 0 ; recordIndex < nbCycles ; recordIndex++ )
        {
            String birthRecord = birthReport.get(recordIndex) ;
            ArrayList<String> birthArray = extractArrayList(birthRecord,AGENTID) ;
            for (String birthAgent : birthArray)
            {
                Object agentId = extractValue(AGENTID,birthAgent) ;
                int age = Integer.valueOf(extractValue(AGE,birthAgent)) ;
                agentAgeHashMap.put(agentId, age + (nbCycles - recordIndex)/daysInYear) ;
            }
            String deathRecord = deathReport.get(recordIndex) ;
            ArrayList<String> deathArray = extractArrayList(deathRecord,AGENTID) ;
            for (String deathAgent : deathArray) 
            {
                Object agentId = extractValue(AGENTID,deathAgent) ;
                int correctAge = agentAgeHashMap.get(agentId) - (nbCycles - recordIndex)/daysInYear ;
                agentAgeHashMap.put(agentId,correctAge) ;
            }
            
        }
        
        // Put into form ageRange={agentId}
//        for (Object agentId : agentAgeHashMap.keySet()) 
//        {
//            int age = agentAgeHashMap.get(agentId) ;
//            // Sort into age ranges (n*5 + 1) to (n+1)*5
//            int ageRange = ((age-1)/5) * 5 + 5 ;
//            sortAgeRecord = updateHashMap(ageRange,agentId,sortAgeRecord) ;
//        }
        return agentAgeHashMap ;
    }
    
    /**
     * 
     * @return (HashMap) key: prepStatus, value: ArrayList of agentIds
     */
    public HashMap<Object,ArrayList<Object>> sortPrepStatus()
    {
        ArrayList<String> openingArray = new ArrayList<String>() ;
        openingArray.add(input.get(0)) ;
        return sortBoundedStringArray("prepStatus", 
                new String[] {Reporter.TRUE,Reporter.FALSE}, AGENTID, openingArray ) ;
    }
    
}
