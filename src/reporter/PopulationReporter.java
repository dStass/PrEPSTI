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
    
    public PopulationReporter(String simname, ArrayList<String> report) 
    {
        super(simname, report);
    }

    /**
     * FIXME: Passing and implementation of fileName not finalised.
     * @param simName
     * @param fileName 
     */
    public PopulationReporter(String simName, String fileName)
    {
        fileName = "PopulationReport" + Community.NAME_ROOT + ".txt" ;  // Community.FILE_PATH + 
        Reader reader = new Reader(simName,fileName) ;
        input = reader.getFiledReport() ;
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
        ArrayList<ArrayList<String>> agentDeathReport = prepareAgentDeathReport() ;
        
        int reportSize = agentBirthReport.size() ;
        for (int recordIndex = 0 ; recordIndex < reportSize; recordIndex++ )
        {
            ArrayList<Object> birthRecordObject = agentBirthReport.get(recordIndex) ;
            ArrayList<String> birthRecord = new ArrayList<String>() ; 
            for (Object agentId : birthRecordObject)
                birthRecord.add((String) agentId) ;
            
            Integer maxBirthId = Integer.valueOf(Collections.max(birthRecord)) ;
            
            countBirthReport.add(maxBirthId) ;
        }
        
        // how many deaths?
        int nbDeaths = 0 ;
        for (int recordIndex = 0 ; recordIndex < (reportSize - 1) ; recordIndex++ )
        {
            ArrayList<String> deathRecord = agentDeathReport.get(recordIndex) ;
            
            nbDeaths += deathRecord.size() ;
            String record = "Population:" ;
            int currentValue = countBirthReport.get(recordIndex + 1) ;
            populationReport.set(recordIndex+1, record + String.valueOf(currentValue - nbDeaths)) ;
            
        }
        return populationReport ;
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
        
        for (int reportNb = 0 ; reportNb < birthReport.size() ; reportNb++ )
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
    public ArrayList<ArrayList<String>> prepareAgentDeathReport()
    {
        ArrayList<ArrayList<String>> agentDeathReport = new ArrayList<ArrayList<String>>() ;
        
        ArrayList<String> deathReport = prepareDeathReport() ;
        int ageIndex ;
        int agentIdIndex ;
        for (int recordNb = 0 ; recordNb < deathReport.size() ; recordNb++ )
        {
            ArrayList<String> agentDeathRecord = new ArrayList<String>() ;  //.clear();
            String record = deathReport.get(recordNb) ;
            //LOGGER.info(record);
            ArrayList<String> deathRecords = extractArrayList(record, DEATH) ;
            for (String deathRecord : deathRecords)
            {
                agentIdIndex = DEATH.length() + 1 ;    // 5 letter in DEATH plus one for ":"
                while (agentIdIndex > 0)
                {
                    agentDeathRecord.add(extractValue(AGENTID, deathRecord,agentIdIndex)) ;
                    ageIndex = deathRecord.indexOf(deathRecord,agentIdIndex+8) ;    // 7 letters in AGENTID plus one for ":"
                    agentIdIndex = isPropertyNameNext(AGENTID,deathRecord,ageIndex+4) ;    // 3 letter in AGE plus one for ":"
                }
            }
            agentDeathReport.add(agentDeathRecord) ;
        }
        return agentDeathReport ;
    }
    
    /**
     * Assumes death report structure of death:agentId:x age:y agentId:x age:y ...
     * @return ArrayList of ArrayLists of (String) ages-at-death of agents who died 
     * in each cycle
     */
    public ArrayList<ArrayList<Object>> prepareAgeDeathReport()
    {
        ArrayList<ArrayList<Object>> ageDeathReport = new ArrayList<ArrayList<Object>>() ;
        
        ArrayList<Object> ageDeathRecord  = new ArrayList<Object>() ;
        int ageIndex ;
        int agentIdIndex ;
        String ageString = AGE + ":" ;
                    
        ArrayList<String> deathReport = prepareDeathReport() ;
        
        for (int reportNb = 0 ; reportNb < deathReport.size() ; reportNb++ )
        {
            ageDeathRecord = new ArrayList<Object>() ;  //.clear();
            String record = deathReport.get(reportNb) ;
            //LOGGER.log(Level.INFO, "{0} {1}", new Object[] {reportNb,record});
            ArrayList<String> deathRecords = extractArrayList(record, DEATH) ;
            for (String deathRecord : deathRecords)
            {
                //LOGGER.info(deathRecord);
                agentIdIndex = isPropertyNameNext(AGENTID,deathRecord,DEATH.length()+1) ;
                while (agentIdIndex > 0)
                {
                    ageIndex = deathRecord.indexOf(ageString,agentIdIndex);
                    ageDeathRecord.add(extractValue(AGE, deathRecord,ageIndex)) ;
                    agentIdIndex = isPropertyNameNext(AGENTID,deathRecord,ageIndex+4) ;    // 3 letters in AGE, +1 for ":"
                    LOGGER.log(Level.INFO, "agentIdIndex:{0} {1} {2} {3}", new Object[] {agentIdIndex,ageIndex,deathRecord.indexOf(AGE,ageIndex),deathRecord});
                }
            }
            ageDeathReport.add(ageDeathRecord) ;
        }
        return ageDeathReport ;
    }
    
    /**
     * 
     * @return (HashMap) key is String.valueOf(age) and value is the number to
     * die at that age.
     */
    public HashMap<Object,Integer> prepareAgeAtDeathReport()
    {
        HashMap<Object,Integer> ageAtDeathMap = new HashMap<Object,Integer>() ;
        
        // Contains age-at-death data
        ArrayList<ArrayList<Object>> ageDeathReport = prepareAgeDeathReport() ;
        
        for (ArrayList<Object> ageArray : ageDeathReport)
        {
            for (Object ageString : ageArray)
                ageAtDeathMap = incrementHashMap(ageString,ageAtDeathMap) ;
        }
        return ageAtDeathMap ;
    }
    
    private ArrayList<String> prepareDeathReport()
    {
        ArrayList<String> deathReport = new ArrayList<String>() ;
        
        String record ;
        int deathIndex ;
        String valueString ;

        for (int reportNb = 0 ; reportNb < input.size() ; reportNb += outputCycle )
        {
            record = input.get(reportNb) ;
            //LOGGER.log(Level.INFO, "{0} {1}", new Object[] {reportNb,record});
            deathIndex = indexOfProperty("death",record) ;
            if (deathIndex < 0)
                continue ;
            deathReport.add(record.substring(deathIndex)) ;
        }
        return deathReport ;
    }
    
    public ArrayList<String> prepareBirthReport()
    {
        ArrayList<String> birthReport = new ArrayList<String>() ;
        
        String record ;
        int agentIndex ;
        String valueString ;

        for (int reportNb = 0 ; reportNb < input.size() ; reportNb += outputCycle )
        {
            record = input.get(reportNb) ;
            birthReport.add(record.substring(indexOfProperty("birth",record),indexOfProperty("death",record))) ;
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
