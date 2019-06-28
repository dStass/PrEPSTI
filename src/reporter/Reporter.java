/**
 * 
 */
package reporter ;

import agent.MSM;
import community.* ;

import java.io.* ;

import java.lang.reflect.*;
import java.util.ArrayList ;
import java.util.Arrays;
import java.util.Set ;
import java.util.Collections;
import java.util.HashMap ;

import java.util.logging.Level;

//import org.jfree.chart.* ;

/**
 * @author Michael Walker
 */
public class Reporter {

    /** Name of simulation. */
    protected String simName ;    
    /** Input report. */ 
    protected ArrayList<String> input ;

    /** Output report. */
    protected ArrayList<String> output ;
    
    /** Reader for accessing saved reports. */
    protected Reader reader ;
    
    /**
     * To avoid generating reports more than once. 
     * reportName maps to report.
     */
    static public HashMap<String,Object> reportList = new HashMap<String,Object>() ;
    
    /** Names of properties for filtering records. */
    private ArrayList<String> filterPropertyNames = new ArrayList<String>() ;
    
    /**
     * (String) values of properties for filtering records. An empty
     * String means that the property need only be present
     */
    private ArrayList<String> filterPropertyValues = new ArrayList<String>() ;

    /** The number of Community cycles to pass between reports. */ 
    protected int outputCycle = 1 ;

    /** String representation of 'None'. */
    static String NONE = "None" ;
    /** static String representation of 'true'. */
    static public String TRUE = "true" ;
    /** static String representation of 'false'. */
    static public String FALSE = "false" ;
    /** static String representation of 'clear'. */
    static public String CLEAR = "clear" ;
    /** static String representation of ','. */
    static public String COMMA = "," ;
    /** static String representation of 'agentId'. */
    static public String AGENTID = "agentId" ;
    /** static String representation of 'agentId0'. */
    static public String AGENTID0 = "agentId0" ;
    /** static String representation of 'agentId1'. */
    static public String AGENTID1 = "agentId1" ;
    /** static String representation of 'relationshipId'. */
    static public String RELATIONSHIPID = "relationshipId" ;
    
    /** Number of days in a year. */
    static public int DAYS_PER_YEAR = 365 ;
    /** Number of days in a month. */
    static final int DAYS_PER_MONTH = 31 ;

    
    /** Logger of Reporter Class. */
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("reporter") ;

    public static final String ADD_REPORT_LABEL(String label)
    {
        return label + ":" ;
    }
    
    public static final String ADD_REPORT_PROPERTY(String label, String value)
    {
        String report = ADD_REPORT_LABEL(label) ;
        return report + value + " " ;
    }
        
    public static final String ADD_REPORT_PROPERTY(String label)
    {
        return ADD_REPORT_PROPERTY(label,"") ;
    }
        
    public static final String ADD_REPORT_PROPERTY(String label, Object value)
    {
        String report = ADD_REPORT_LABEL(label) ;
        return report + String.valueOf(value) + " " ;
    }
     
    /**
     * Avoid having to add ":" whenever the index of a property name is needed.
     * Used when startIndex is zero or not given
     * @param property
     * @param record
     * @return indexOf(property + ":")
     */
    public static final int INDEX_OF_PROPERTY(String property, String record)
    {
        return INDEX_OF_PROPERTY(property,0,record) ;
    }
    
    /**
     * Avoid having to add ":" whenever the index of a property name is needed
     * @param property
     * @param startIndex
     * @param report
     * @return indexOf(property + ":")
     */
    public static final int INDEX_OF_PROPERTY(String property, int startIndex, String report)
    {
        property += ":" ;
        return report.indexOf(property,startIndex) ;
    }
    
    /**
     * 
     * @param mapList - either HashMap or ArrayList
     * @param elementNb
     * @return (String) representation of mapList.get(elementNb)
     */
    protected static String PRESENT_ELEMENT(Object mapList, Object elementNb)
    {
        if (mapList instanceof HashMap)
        {
            return GET_ELEMENT((HashMap<?,?>) mapList, elementNb).toString() ;
        }
    return GET_ELEMENT((ArrayList<?>) mapList, (Integer) elementNb).toString() ;
    }

    /**
     * 
     * @param hashmap
     * @param elementNb
     * @return (Object) hashmap.get(elementNb) or (Object) "None" if elementKey not a key 
     */
    private static Object GET_ELEMENT(HashMap<?,?> hashmap, Object elementKey)
    {
            if (hashmap.containsKey(elementKey))
            {
                    return (Object) hashmap.get(elementKey) ;
            }
            String message = "None" ;
            return (Object) message ;
    }

    /**
     * 
     * @param arrayList
     * @param elementNb
     * @return (Object) arrayList[elementNb] or (Object) "None" if not available
     */
    private static Object GET_ELEMENT(ArrayList<?> arrayList, int elementNb)
    {
            if (arrayList.size() > elementNb)
            {
                    return (Object) arrayList.get(elementNb) ;
            }
            String message = NONE ;
            return (Object) message ;
    }
    
    /**
     * Filters records leaving only those encounters containing propertyName with (String) value.
     * @param propertyName
     * @param value
     * @param bound
     * @param fullReport
     * @return 
     */
    protected static ArrayList<String> FILTER_REPORT(String propertyName, String value, String bound, ArrayList<String> fullReport)
    {
        ArrayList<String> filteredReport = new ArrayList<String>() ;
        
        String filteredRecord ;
        
        for (String record : fullReport)
        {
            filteredRecord = BOUNDED_STRING_BY_VALUE(propertyName,value,bound,record);
            filteredReport.add(filteredRecord) ;
        }
        
        return filteredReport ;
    }
    
    
    /**
     * Sorts String entries of unsortedReport according to value of propertyName.
     * @param unsortedReport
     * @param sortingReport
     * @return 
     */
    protected static HashMap<Object,HashMap<Object,Integer>> SORT_REPORT(HashMap<Object,Integer> unsortedReport, 
            HashMap<Object,Object> sortingReport)
    {
        HashMap<Object,HashMap<Object,Integer>> outputHashMap = new HashMap<Object,HashMap<Object,Integer>>() ;
        //HashMap<Object,?> entryHashMap = new HashMap<Object,Object>() ;
        
        ArrayList<String> unsortedEntries ;
        Object sortingValue ;
                
        for (Object unsortedKey : unsortedReport.keySet())
        {
                Object sortingKey = (Object) sortingReport.get(unsortedKey) ;
                /*if (!entryHashMap.containsKey(sortingKey))
                    entryHashMap.put(sortingValue, ) ;
                entryHashMap.put(sortingValue,entryHashMap.get(sortingValue) + entry) ;
            }
            for (Object entryKey : entryHashMap.keySet())
            {*/
                if (!outputHashMap.containsKey(sortingKey))
                    outputHashMap.put(sortingKey, new HashMap<Object,Integer>()) ;
                outputHashMap.get(sortingKey).put(unsortedKey, unsortedReport.get(unsortedKey)) ;
            //}
        }
        return outputHashMap ;
    }
    
    /**
     * Sorts entries of unsortedReport according to sortingReport, only considering
     * the values in (Object[]) values.
     * @param unsortedReport
     * @param sortingReport
     * @param values
     * @return 
     */
    protected static HashMap<Object,HashMap<Object,ArrayList<Object>>> 
        SORT_RECORD(HashMap<Object,ArrayList<Object>> unsortedReport, 
            HashMap<Object, ArrayList<Object>> sortingReport, Object[] values)
    {
        HashMap<Object,HashMap<Object,ArrayList<Object>>> sortedReport 
                = new HashMap<Object,HashMap<Object,ArrayList<Object>>>() ;
        for (Object value : values )
        {
            sortedReport.put(value, new HashMap<Object,ArrayList<Object>>()) ;
            for (Object key : unsortedReport.keySet())
            {
                ArrayList<Object> arrayList = new ArrayList<Object>() ;
                for (Object entry : unsortedReport.get(key))
                    if (sortingReport.get(value).contains(entry))
                        arrayList.add(entry) ;
                if (!arrayList.isEmpty())
                    sortedReport.get(value).put(key,arrayList) ;
            }
        }
        return sortedReport ;
    }

    /**
     * Sorts entries of unsortedReport according to sortingReport, only considering
     * the values in (Object[]) values.
     * The nested HashMap in unsortedReport is intended to hold temporal (cycle) data.
     * @param unsortedReport
     * @param sortingReport
     * @param values
     * @return 
     */
    protected static HashMap<Object,HashMap<Object,HashMap<Object,ArrayList<Object>>>> 
        SORT_REPORT(HashMap<Object,HashMap<Object,ArrayList<Object>>> unsortedReport, 
            HashMap<Object, ArrayList<Object>> sortingReport, Object[] values) 
    {
        HashMap<Object,HashMap<Object,HashMap<Object,ArrayList<Object>>>> sortedReport 
                = new HashMap<Object,HashMap<Object,HashMap<Object,ArrayList<Object>>>>() ;
        
        for (Object value : values )
        {
            sortedReport.put(value, new HashMap<Object,HashMap<Object,ArrayList<Object>>>()) ;
        
            HashMap<Object,HashMap<Object,ArrayList<Object>>> hashMap1 = new HashMap<Object,HashMap<Object,ArrayList<Object>>>() ;
            for (Object key1 : unsortedReport.keySet())
            {
                HashMap<Object,ArrayList<Object>> hashMap2 = new HashMap<Object,ArrayList<Object>>() ;
                for (Object key2 : unsortedReport.get(key1).keySet())
                {
                    if (!sortingReport.get(value).contains(String.valueOf(key2)))
                        continue ;
                    hashMap2.put(key2, unsortedReport.get(key1).get(key2)) ;
                            //updateHashMap(key1,unsortedReport.get(key1).get(key2),hashMap1) ;
                }
                if (!hashMap2.keySet().isEmpty())
                    hashMap1.put(key1, hashMap2) ;
            }     
            if (!hashMap1.keySet().isEmpty())
                sortedReport.put(value, hashMap1) ;
            LOGGER.log(Level.INFO, "{0}", sortedReport);
        }
        return sortedReport ;
    }
        
    /**
     * Extracts bounded substrings whose propertyName == value
     * @param propertyName 
     * @param value
     * @param bound - String bounding substrings of interest
     * @param string
     * @return String boundedOutput
     */
    public static String BOUNDED_STRING_BY_VALUE(String propertyName, String value, String bound, String string)
    {
        String boundedOutput = "" ;
        String boundedString ;
        for (int indexStart = Reporter.INDEX_OF_PROPERTY(bound,string) ; indexStart >= 0 ; indexStart = INDEX_OF_PROPERTY(bound,indexStart+1,string))
        {
            boundedString = EXTRACT_BOUNDED_STRING(bound, string, indexStart) ;
            // This if statement moved to COMPARE_VALUE()
            //if (INDEX_OF_PROPERTY(propertyName,boundedString) >= 0)
            
            // TODO: Label Sites site0, site1 in generation of encounterString so that boolean || is not necessary
            if (COMPARE_VALUE(propertyName,value,boundedString) || Reporter.COMPARE_VALUE(propertyName,value,boundedString,boundedString.lastIndexOf(propertyName))) 
                boundedOutput += boundedString ;
        }
        return boundedOutput ;
    }
    
    /**
     * 
     * @param propertyName
     * @param values
     * @param bound
     * @param report
     * @return HashMap key:values, entries: ArrayList of values of bound
     */
    protected static HashMap<Object,ArrayList<Object>> SORT_BOUNDED_STRING_ARRAY(String propertyName, String[] values, String bound, ArrayList<String> report)
    {
        HashMap<Object,ArrayList<Object>> sortedHashMap = new HashMap<Object,ArrayList<Object>>() ;
        int indexStart ;
        String boundedString ;
        Object key ;
        String boundValue ;
        
        // Initialise output HashMap
        for (Object value : values)
            sortedHashMap.put(value,new ArrayList<Object>()) ;
        sortedHashMap.put(NONE,new ArrayList<Object>()) ;

        for (String record : report)
        {
            key = "" ;
            String checkRecord = BOUNDED_STRING_BY_CONTENTS(propertyName,bound,record) ;
            if (checkRecord.isEmpty())
            {
                LOGGER.info(propertyName + " " + bound + " checkRecord is empty " + record);
                continue ;
            }
            indexStart = Reporter.INDEX_OF_PROPERTY(bound,checkRecord);
            while (indexStart >= 0)
            {
                boundedString = EXTRACT_BOUNDED_STRING(bound, checkRecord, indexStart) ;
                key = (Object) EXTRACT_VALUE(propertyName,boundedString) ;
                if (String.valueOf(key).isEmpty())
                    break ;
                else
                {
                    boundValue = EXTRACT_VALUE(bound,boundedString) ;
                    sortedHashMap = UPDATE_HASHMAP(key,boundValue,sortedHashMap) ;
                }
                indexStart = INDEX_OF_PROPERTY(bound,indexStart+1,checkRecord);
            }
            //LOGGER.log(Level.INFO, "key:{0}", new Object[]{key});
            //LOGGER.log(Level.INFO, "{0}", sortedHashMap);
        }
        return sortedHashMap ;
    }
    
    /**
     * Extracts bounded substrings containing propertyName as substring
     * @param propertyName 
     * @param bound - String bounding substrings of interest
     * @param string
     * @return String boundedOutput
     */
    protected static String BOUNDED_STRING_BY_CONTENTS(String propertyName, String bound, String string)
    {
        String boundedOutput = "" ;
        String boundedString ;
        for (int indexStart = Reporter.INDEX_OF_PROPERTY(bound,string) ; indexStart >= 0 ; indexStart = INDEX_OF_PROPERTY(bound,indexStart+1,string) )
        {
            boundedString = EXTRACT_BOUNDED_STRING(bound, string, indexStart) ;
            if (boundedString.contains(propertyName))   //(COMPARE_VALUE(propertyName,value,boundedString)) 
                boundedOutput += boundedString ;
        }
        return boundedOutput ;
    }

    /**
     * Extracts bounded substrings containing propertyName as substring
     * @param propertyName 
     * @param bound - String bounding substrings of interest
     * @param string
     * @return String boundedOutput
     */
    protected static String BOUNDED_STRING_FROM_ARRAY(String propertyName, ArrayList<Object> values, String bound, String string)
    {
        String boundedOutput = "" ;
        String boundedString ;
        String value ;
        for (int indexStart = Reporter.INDEX_OF_PROPERTY(bound,string) ; indexStart >= 0 ; indexStart = INDEX_OF_PROPERTY(bound,indexStart+1,string) )
        {
            boundedString = EXTRACT_BOUNDED_STRING(bound, string, indexStart) ;
            value = EXTRACT_VALUE(propertyName, boundedString) ;
            if (values.contains(value))   //(COMPARE_VALUE(propertyName,value,boundedString)) 
                boundedOutput += boundedString ;
        }
        return boundedOutput ;
    }

    /**
     * 
     * @param string
     * @param bound
     * @return (ArrayList(String)) of bounded substrings of string.
     */
    public static ArrayList<String> EXTRACT_ARRAYLIST(String string, String bound)
    {
        return EXTRACT_ARRAYLIST(string, bound, "") ;
    }
    
        /**
     * 
     * @param string
     * @param bound
     * @param flag
     * @return (ArrayList(String)) of bounded substrings of string containing 
     * flag as a substring.
     */
    protected static ArrayList<String> EXTRACT_ARRAYLIST(String string, String bound, String flag)
    {
        ArrayList<String> outputArray = new ArrayList<String>() ;
        String extractedString ;
        
        // Require bounded strings contain flag.
        if (!flag.isEmpty())
            string = BOUNDED_STRING_BY_CONTENTS(flag,bound,string) ;
        
        // Extract individual bounded strings.
        for (int indexStart = Reporter.INDEX_OF_PROPERTY(bound, string) ; indexStart >= 0 ;  indexStart = INDEX_OF_PROPERTY(bound, indexStart+1, string))
        {
            extractedString = EXTRACT_BOUNDED_STRING(bound, string, indexStart);
            if (!extractedString.isEmpty()) 
                outputArray.add(extractedString) ;
        }
        return outputArray ;
    }

    /**
     * 
     * @param bound - subString bounding subStrings of interest
     * @param string - String to parse
     * @param indexStart - index in string of first bound
     * @return subString of string bounded by bound
     */
    public static String EXTRACT_BOUNDED_STRING(String bound, String string, int indexStart)
    {
        int index0 = INDEX_OF_PROPERTY(bound, indexStart, string) ;
        if (index0 == -1)
            return "" ;
        int index1 = INDEX_OF_PROPERTY(bound,index0+1,string) ;
        if (index1 == -1) index1 = string.length() ;
        return string.substring(index0, index1) ;

    }

    /**
     * When position within string is not known, call EXTRACT_VALUE(startIndex = 0)
     * @param propertyName - name of variable whose value is wanted
     * @param string
     * @return (String) value of propertyName
     */
    public static String EXTRACT_VALUE(String propertyName, String string)
    {
        return Reporter.EXTRACT_VALUE(propertyName, string, 0) ;
    }

    /**
     * 
     * @param propertyName
     * @param string
     * @return ArrayList of (String) values of propertyName from String string
     */
    public static ArrayList<Object> EXTRACT_ALL_VALUES(String propertyName, String string)
    {
        return Reporter.EXTRACT_ALL_VALUES(propertyName,string,0) ;
    }
    
    /**
     * 
     * @param propertyName
     * @param string
     * @param startIndex
     * @return ArrayList of (String) values of propertyName from String string
     */
    public static ArrayList<Object> EXTRACT_ALL_VALUES(String propertyName, String string, int startIndex)
    {
        ArrayList<Object> values = new ArrayList<Object>() ;
        int index = INDEX_OF_PROPERTY(propertyName,startIndex,string) ; 
        
        while ( index >= 0 )
        {
            values.add(Reporter.EXTRACT_VALUE(propertyName, string, index)) ;
            index = INDEX_OF_PROPERTY(propertyName, index+1, string) ;
        }
        return values ;
    }
    
    /**
     * The space character indicates the end of the value.  
     * @param propertyName - property whose value is wanted
     * @param string - information source/report
     * @param startIndex - string index of value, assumed exact if index greater 
     * than 0, otherwise search
     * @return (String) value of valueName as stored in string
     */
    public static String EXTRACT_VALUE(String propertyName, String string, int startIndex)
    {
        // Find value of valueName in string
        startIndex = INDEX_OF_PROPERTY(propertyName, startIndex, string) ;
        if (startIndex < 0)
            return "" ;
        startIndex += propertyName.length() + 1 ;    // +1 is for ":" following propertyName
        int valueEndIndex = string.indexOf(" ", startIndex) ;
        if (valueEndIndex < 0)
            valueEndIndex = string.length() ;
        return string.substring(startIndex, valueEndIndex) ;
    }

    /**
     * 
     * @param propertyName
     * @param string
     * @param startIndex
     * @return (String) value of propertyName in string if it is the next property,
     * otherwise return "None"
     */
    protected static String EXTRACT_VALUE_IF_NEXT(String propertyName, String string, int startIndex)
    {
        startIndex = IS_PROPERTY_NAME_NEXT(propertyName, string, startIndex) ;
        if (startIndex > 0)
            return Reporter.EXTRACT_VALUE(propertyName, string, startIndex) ;
        return NONE ;
    }

    /**
     * 
     * @param record
     * @return String[] pairs of agentIds corresponding to relationships described in record
     */
    protected String[] EXTRACT_AGENTIDS(String record)
    {
        return Reporter.this.EXTRACT_AGENTIDS(record, 0) ;
    }
    
    /**
     * 
     * @param record
     * @param startIndex
     * @return String[] pairs of agentIds corresponding to relationships described in record
     */
    protected String[] EXTRACT_AGENTIDS(String record, int startIndex)
    {
            String agentId0 = Reporter.EXTRACT_VALUE("agentId0", record, startIndex) ;

            startIndex = INDEX_OF_PROPERTY("agentId1", startIndex, record) ;
            String agentId1 = Reporter.EXTRACT_VALUE("agentId1", record, startIndex) ;
            return new String[] {agentId0,agentId1} ;
    }
    
    /**
     * 
     * @param propertyName
     * @param string
     * @param startIndex
     * @return index of propertyName in string if propertyName is next property in string, -1 otherwise
     */
    protected static int IS_PROPERTY_NAME_NEXT(String propertyName, String string, int startIndex)
    {
        int colonIndex = string.indexOf(":",startIndex) ;
        int propertyIndex = INDEX_OF_PROPERTY(propertyName, startIndex, string) ;
        
        // If propertyName names the first property after position startIndex
        if ((propertyIndex < colonIndex) && (propertyIndex > 1))
            return propertyIndex ;
        return -1 ;
    }
    
    /**
     * Compares the String representation of the value of propertyName to @param value
     * @param propertyName
     * @param value
     * @param string
     * @param startIndex
     * @return true if the String representation of the value of propertyName equals (String) value
     */
    protected static boolean COMPARE_VALUE(String propertyName, String value, String string, int startIndex)
    {
        if (INDEX_OF_PROPERTY(propertyName,startIndex,string) >= 0)
            return Reporter.EXTRACT_VALUE(propertyName, string, startIndex).equals(value) ;
        return false ;
    }
    
    /**
     * Finds the number of times propertyName occurs in string and the number of 
     * times it has the value value.toString().
     * @param propertyName
     * @param value
     * @param string
     * @param startIndex
     * @return (int[2]) The number of 'propertyName has value' incidents, number of propertyName incidents.
     */
    protected static int[] COUNT_VALUE_INCIDENCE(String propertyName, String value, String string, int startIndex)
    {
        int count = 0 ;
        int total = 0 ;
        for (int index = INDEX_OF_PROPERTY(propertyName, startIndex, string) ; index >= 0 ; 
            index = INDEX_OF_PROPERTY(propertyName, index+1, string))
        {
            total++ ;
            if (Reporter.COMPARE_VALUE(propertyName, value, string, index))
                count++ ;
        }
        if (total == 0)
            return new int[] {0,0} ;
        return new int[] {count,total} ;
    }
    
    /**
     * Compares the String representation of the value of propertyName to @param value
     * @param propertyName
     * @param value
     * @param string
     * @return true if the String representation of the value of propertyName equals (String) value
     */
    protected static boolean COMPARE_VALUE(String propertyName, String value, String string)
    {
        return Reporter.COMPARE_VALUE(propertyName, value, string, Reporter.INDEX_OF_PROPERTY(propertyName,string)) ;
    }
    
    
    /*protected static HashMap<String,ArrayList<String>> updateStringHashMap(String keyString, String entryString, HashMap<String,ArrayList<String>> valueMap)
    {
        
    }*/
    
    /**
     * Puts entries into HashMap whose keys are the agentIds
     * and values are arrays of their partners Ids. 
     * Creates key and associated int[] if necessary.
     * 
     * @param key - (String) usually agentId but need not be.
     * @param entry - String to convert and go into int[] at key. 
     * @param valueMap - Adding entry and sometimes key to this HashMap
     * @return partnerMap - HashMap indicating partnerIds of each agent (key: agentId)
     */
    /*protected static HashMap<Integer,ArrayList<Integer>> UPDATE_HASHMAP(String keyString, String entryString, HashMap<Integer,ArrayList<Integer>> valueMap)
    {
        int key = Integer.valueOf(keyString) ;
        int boundValue = Integer.valueOf(entryString) ;
        return UPDATE_HASHMAP(key, boundValue, valueMap) ;
    }*/
    
    /**
     * Puts entries into HashMap whose keys are the agentIds
     * and values are arrays of their partners Ids. 
     * Creates key and associated Object[] if necessary.
     * 
     * @param key - usually agentId but need not be.
     * @param entry - Object to go into Object[] at key. 
     * @param valueMap - Adding boundValue and sometimes key to this HashMap
     * @return partnerMap - HashMap indicating partnerIds of each agent (key: agentId)
     */
    public static HashMap<Object,ArrayList<Object>> UPDATE_HASHMAP(Object key, Object entry, HashMap<Object,ArrayList<Object>> valueMap)
    {
        return Reporter.UPDATE_HASHMAP(key, entry, valueMap, true) ;
    }
		
    /**
     * Puts entries into HashMap whose keys are the agentIds
     * and values are arrays of their partners Ids. 
     * Creates key and associated int[] if necessary.
     * @param key - usually agentId but need not be.
     * @param entry - int to go into int[] at key. 
     * @param valueMap - Adding boundValue and sometimes key to this HashMap
     * @param allowDuplicates
     * @return partnerMap - HashMap indicating partnerIds of each agent (key: agentId)
     */
    protected static HashMap<Object,ArrayList<Object>> UPDATE_HASHMAP(Object key, Object entry, HashMap<Object,ArrayList<Object>> valueMap, boolean allowDuplicates)
    {
        //HashMap<Integer,ArrayList<Integer>> partnerMap = new HashMap<Integer,ArrayList<Integer>>() ;
        
        ArrayList<Object> entryArray ;
        if (valueMap.containsKey(key))
        {
            entryArray = valueMap.get(key) ;
        }
        else
        {
            entryArray = new ArrayList<Object>() ;
        }
        if (allowDuplicates || !entryArray.contains(entry))
        {
            entryArray.add(entry) ;
            valueMap.put(key, entryArray) ;
        }

        return valueMap ;
    }
    
    /**
     * Adds (Integer) entry to value of (Object) key in valueMap.
     * @param key
     * @param entry
     * @param valueMap
     * @return 
     */
    protected static HashMap<Object,Integer> UPDATE_HASHMAP(Object key, Integer entry, HashMap<Object,Integer> valueMap)
    {
        Integer listEntry ;
        
        if (valueMap.containsKey(key))
            listEntry = valueMap.get(key) ;
        else
            listEntry = 0 ;
            
        valueMap.put(key, listEntry + entry) ;

        return valueMap ;
    }
		
    /**
     * Increments entries into HashMap whose keys are the agentIds
     * Creates key and associated int[] if necessary.
     * @param key - usually agentId but need not be.
     * @param valueMap - Adding boundValue and sometimes key to this HashMap
     * @return partnerMap - HashMap indicating partnerIds of each agent (key: agentId)
     */
    protected static HashMap<Object,Number> INCREMENT_HASHMAP(Object key, HashMap<Object,Number> valueMap)
    {
        //HashMap<Integer,ArrayList<Integer>> partnerMap = new HashMap<Integer,ArrayList<Integer>>() ;
        
        if (valueMap.containsKey(key))
            valueMap.put(key, valueMap.get(key).intValue() + 1) ;
        else
            valueMap.put(key, 1) ;
        
        return valueMap ;
    }
	
    /**
     * Puts entries in HashMap<Integer,HashMap<Integer,ArrayList<Integer>>> after 
     * converting input Strings to Integer.
     * @param keyString
     * @param entryString
     * @param cycle
     * @param valueMap
     * @return 
     */
    /*protected static HashMap<Integer,HashMap<Integer,ArrayList<Integer>>> UPDATE_HASHMAP(String keyString, String entryString, 
            int cycle, HashMap<Integer,HashMap<Integer,ArrayList<Integer>>> valueMap)
    {
        int key = Integer.valueOf(keyString) ;
        int boundValue = Integer.valueOf(entryString) ;
        return UPDATE_HASHMAP(key, boundValue, cycle, valueMap) ;
    }*/
    
    /**
     * Puts entries in HashMap(Integer,HashMap(Integer,ArrayList(Integer))), 
     * creating keys in either HashMap when necessary and simply updating otherwise.
     * @param key
     * @param key2
     * @param cycle
     * @param valueMap
     * @return 
     */
    protected static HashMap<Object,HashMap<Object,ArrayList<Object>>> UPDATE_HASHMAP(Object key, 
            Object key2, int cycle, HashMap<Object,HashMap<Object,ArrayList<Object>>> valueMap)
    {
        //HashMap<Integer,ArrayList<Integer>> partnerMap = new HashMap<Integer,ArrayList<Integer>>() ;
        
        HashMap<Object,ArrayList<Object>> entryHashMap ;
        if (valueMap.containsKey(key))
        {
            entryHashMap = valueMap.get(key) ;
        }
        else
        {
            entryHashMap = new HashMap<Object,ArrayList<Object>>() ;
            //entryArray.add(key2) ;
        }
        valueMap.put(key, UPDATE_HASHMAP((Object) key2,(Object) cycle,entryHashMap)) ;

        return valueMap ;
    }
    
    /**
     * Converts HashMap(Object,ArrayList(Object)) to HashMap(String,ArrayList(String)) .
     * @param objectHashMap
     * @return 
     */
    static protected HashMap<String,ArrayList<String>> HASHMAP_STRING(HashMap<Object,ArrayList<Object>> objectHashMap)
    {
        HashMap<String,ArrayList<String>> stringHashMap = new HashMap<String,ArrayList<String>>() ;
        
        ArrayList<Object> entryObject ;
        ArrayList<String> entryString = new ArrayList<String>() ;
        
        for (Object key : objectHashMap.keySet())
        {
            entryObject = objectHashMap.get(key) ;
            for (Object entry : entryObject)
                entryString.add((String) entry) ;
            stringHashMap.put((String) key, entryString ) ;
        }
        return stringHashMap ;
    }
		
    /**
     * Converts HashMap(Object,ArrayList(Object)) to HashMap(Number,ArrayList(Number))
     * @param objectHashMap
     * @return 
     */
    static protected HashMap<Number,ArrayList<Number>> HASHMAP_NUMBER(HashMap<Object,ArrayList<Object>> objectHashMap)
    {
        HashMap<Number,ArrayList<Number>> numberHashMap = new HashMap<Number,ArrayList<Number>>() ;
        
        ArrayList<Object> entryObject ;
        ArrayList<Number> entryNumber = new ArrayList<Number>() ;
        
        for (Object key : objectHashMap.keySet())
        {
            entryObject = objectHashMap.get(key) ;
            for (Object entry : entryObject)
                entryNumber.add((Number) entry) ;
            numberHashMap.put((Number) key, entryNumber ) ;
        }
        return numberHashMap ;
    }
		
    /**
     * Given objectHashMap maps key to ((HashMap) subKey maps to object), finds 
     * number of subKeys for each key.
     * @param objectHashMap
     * @return (HashMap) showing number of keys in each subHashMap.
     */
    static protected HashMap<Object,Number> COUNT_SUB_KEYS(HashMap<Object,HashMap<Object,ArrayList<Object>>> objectHashMap)
    {
        HashMap<Object,Number> outputHashMap = new HashMap<Object,Number>() ;
        
        for (Object key : objectHashMap.keySet() )  // numberRecentRelationshipsReport.keySet())
            outputHashMap.put(key, objectHashMap.get(key).keySet().size()) ;
    
        return outputHashMap ;
    }
    
    /**
     * Converts HashMap(Object,HashMap(Object,ArrayList(Object)))
     * to HashMap(Number,HashMap(Number,ArrayList(Number)))
     * @param objectHashMapHashMap
     * @return 
     */
    static protected HashMap<Number,HashMap<Number,ArrayList<Number>>> HASHMAP_HASHMAP_NUMBER(HashMap<Object,HashMap<Object,ArrayList<Object>>> objectHashMapHashMap )
    {
        HashMap<Number,HashMap<Number,ArrayList<Number>>> numberHashMapHashMap = new HashMap<Number,HashMap<Number,ArrayList<Number>>>() ;
        
        HashMap<Number,ArrayList<Number>> numberHashMap = new HashMap<Number,ArrayList<Number>>() ;
        
        for (Object key : objectHashMapHashMap.keySet())
        {
            numberHashMap = HASHMAP_NUMBER(objectHashMapHashMap.get(key)) ;
            numberHashMapHashMap.put((Number) key, numberHashMap) ;
        }
        return numberHashMapHashMap ;
    }
    
    /**
     * Restructures paramHashMap so that most-nested values become keys.
     * Values are HashTable of ArrayList of nested keys.
     * key1 maps to key2 maps to arrayValue becomes arrayValue maps to key1 maps to key2 .
     * @param paramHashMap
     * @return HashTable
     */
    static public HashMap<Object,HashMap<Object,ArrayList<Object>>> 
        INVERT_HASHMAP_HASHMAP(HashMap<Object,HashMap<Object,ArrayList<Object>>> paramHashMap)
    {
        //LOGGER.info("INVERT_HASHMAP_HASHMAP()");
        HashMap<Object,HashMap<Object,ArrayList<Object>>> invertedHashMap = new HashMap<Object,HashMap<Object,ArrayList<Object>>>() ;
        HashMap<Object,ArrayList<Object>> cycleHashMap ;
        
        for( Object key1 : paramHashMap.keySet() )
        {
            for (Object key2 : paramHashMap.get(key1).keySet())
            {
            //LOGGER.info(paramHashMap.get(key1).get(key2).toString());
                for (Object cycle : paramHashMap.get(key1).get(key2))
                {
            //LOGGER.info(cycle.toString());
                    if (!invertedHashMap.keySet().contains(cycle))
                        cycleHashMap = new HashMap<Object,ArrayList<Object>>() ;
                    else 
                        cycleHashMap = invertedHashMap.get(cycle) ;
                    invertedHashMap.put(cycle, UPDATE_HASHMAP(key1,key2,cycleHashMap)) ;
                }
            }
        }
        return invertedHashMap ;
    }
        
    /**
     * Convert ArrayList of HashMaps to form usable by multiPlotCycle().
     * @param initialList
     * @param initialKeys
     * @return (HashMap) of Number[] 
     */
    static public ArrayList<ArrayList<Object>> 
        INVERT_ARRAY_HASHMAP(ArrayList<HashMap<Object,Number>> initialList, Object[] initialKeys)
    {
        ArrayList<ArrayList<Object>> invertedList = new ArrayList<ArrayList<Object>>() ;
        
        int arraySize = initialKeys.length ;
        
        for (HashMap<Object,Number> record : initialList)
        {
            ArrayList<Number> innerList = new ArrayList<Number>() ;
            
            // Substitute values into invertedMap        
            for (int index = 0 ; index < arraySize ; index++ )
            {
                Object key = initialKeys[index] ;
                if (record.containsKey(key))
                    innerList.add(record.get(key)) ;
                else
                    innerList.add(0) ;
            }
            
            invertedList.add((ArrayList<Object>) innerList.clone()) ;
        }
        
        return invertedList ;
    }
    
    /**
     * Convert HashMap of HashMaps to form usable by plotHashMap().
     * @param initialMap
     * @param initialKeys
     * @return (HashMap) of Number[] 
     */
    static public HashMap<Object,Number[]> 
        INVERT_HASHMAP_ARRAY(HashMap<Object,HashMap<Object,Number>> initialMap, Object[] initialKeys)
    {
        HashMap<Object,Number[]> invertedMap = new HashMap<Object,Number[]>() ;
        
        HashMap<Object,Number> innerMap ;
        
        ArrayList<Number> invertList ;
        
        int arraySize = initialKeys.length ;
        
        // Set up invertedMap to be able to hold values
        for (Object key : initialKeys)
            for (Object innerKey : initialMap.get(key).keySet()) 
                if (!invertedMap.containsKey(innerKey))
                    invertedMap.put(innerKey, new Number[initialKeys.length]) ;
            
        // Substitute values into invertedMap        
        for (int index = 0 ; index < arraySize ; index++ )
            for (Object innerKey : invertedMap.keySet())
            {
                Object key = initialKeys[index] ;
                if (initialMap.get(key).containsKey(innerKey))
                    invertedMap.get(innerKey)[index] = initialMap.get(key).get(innerKey) ;
                else
                    invertedMap.get(innerKey)[index] = 0 ;
            }
        
        return invertedMap ;
    }
    
    /**
     * Convert HashMap of HashMaps to form usable by plotSpline().
     * @param initialMap
     * @param initialKeys
     * @return (HashMap) of (ArrayList) of Number 
     */
    static public HashMap<Object,Number[]> 
        INVERT_HASHMAP_LIST(HashMap<Object,HashMap<Object,Number>> initialMap, Object[] initialKeys)
    {
        HashMap<Object,Number[]> invertedMap = new HashMap<Object,Number[]>() ;
        
        int arraySize = initialKeys.length ;
        
        // Set up invertedMap to be able to hold values
        for (Object key : initialKeys)
            for (Object innerKey : initialMap.get(key).keySet()) 
                if (!invertedMap.containsKey(innerKey))
                    invertedMap.put(innerKey, new Number[arraySize]) ;
            
        // Substitute values into invertedMap        
        for (int index = 0 ; index < arraySize ; index++ )
            for (Object innerKey : invertedMap.keySet())
            {
                Object key = initialKeys[index] ;
                if (initialMap.containsKey(key) && initialMap.get(key).containsKey(innerKey))
                    invertedMap.get(innerKey)[index] = initialMap.get(key).get(innerKey) ;
                else
                    invertedMap.get(innerKey)[index] = 0 ;
            }
        
        return invertedMap ;
    }
    
    /**
     * Sorts hashMap entries according to SORT_BOUNDED_STRING_ARRAY, only including values in
 (Object[]) values.
     * @param hashMap
     * @param sortedHashMap
     * @param values
     * @return 
     */
    static protected HashMap<Object,HashMap<Object,ArrayList<Object>>> SORT_HASHMAP(HashMap<Object,ArrayList<Object>> hashMap, 
            HashMap<Object,ArrayList<Object>> sortedHashMap, Object[] values )
    {
        // Output HashMap
        HashMap<Object,HashMap<Object,ArrayList<Object>>> outputHashMap 
                = new HashMap<Object,HashMap<Object,ArrayList<Object>>>() ;
        
        // Sorted HashMap entries
        HashMap<Object,ArrayList<Object>> newEntry = new HashMap<Object,ArrayList<Object>>() ;
        for (Object value : values)
            newEntry.put(value, new ArrayList<Object>()) ;
        
        // Sorting loop.
        for (Object value : values)
        {
            for (Object key : hashMap.keySet())
                for (Object entry : hashMap.get(key))
                    if (sortedHashMap.get(value).contains(entry))
                        newEntry.get(key).add(entry) ;
            outputHashMap.put(value,newEntry) ;
        }
        return outputHashMap ;
    }
    
    /**
     * Invoked to sort HashMap entries when all categories are of interest.
     * @param hashMap
     * @param sortedHashMap
     * @return 
     */
    static protected HashMap<Object,HashMap<Object,ArrayList<Object>>> SORT_HASHMAP(HashMap<Object,ArrayList<Object>> hashMap, 
            HashMap<Object,ArrayList<Object>> sortedHashMap)
    {
        Object[] values = sortedHashMap.keySet().toArray() ;
        return SORT_HASHMAP(hashMap, sortedHashMap, values ) ;
    }
    
    /**
     * For sorting HashMaps when only one value is required
     * @param hashMap
     * @param sortedHashMap
     * @param value
     * @return 
     */
    static protected HashMap<Object,ArrayList<Object>> SORT_HASHMAP(HashMap<Object,ArrayList<Object>> hashMap, 
            HashMap<Object,ArrayList<Object>> sortedHashMap, Object value )
    {
        return SORT_HASHMAP(hashMap, sortedHashMap, new Object[] {value} ).get(value) ;
    }
    
    /**
     * Invoked to sort hashMap.keySet() according to values.
     * @param hashMap
     * @param sortingHashMap
     * @param values
     * @return 
     */
    static protected HashMap<Object,HashMap<Object,Object>> SORT_HASHMAP_KEYS(HashMap<Object,Object> hashMap, 
            HashMap<Object,ArrayList<Object>> sortingHashMap, Object[] values)
    {
        HashMap<Object,HashMap<Object,Object>> outputHashMap = new HashMap<Object,HashMap<Object,Object>>() ;
        
        HashMap<Object,Object> keyHashMap = new HashMap<Object,Object>() ;
        for (Object value : values)
        {
            for (Object key : hashMap.keySet())
                if (sortingHashMap.get(value).contains(key))
                    keyHashMap.put(key, hashMap.get(key)) ;
            outputHashMap.put(value, keyHashMap) ;
        }
        return outputHashMap ;
    }
    
    /**
     * Used to go back from end of Report by a specified amount. Checks that this 
     * does not go past the beginning of the report to cause an error.
     * @param backYears
     * @param backMonths
     * @param backDays
     * @param maxCycles
     * @return (int) The number of cycles specified by backYears, backMonths, backDays
     * or maxCycles, whichever is smaller. 
     */
    static protected int GET_BACK_CYCLES(int backYears, int backMonths, int backDays, int maxCycles)
    {
        int daysPerYear = 365 ;
        int daysPerMonth = 31 ;
        int backCycles ;
        
        // Don't go further back than records allow.
        if ((backYears * daysPerYear) > maxCycles)
        {
            backYears = maxCycles/daysPerYear ;
            LOGGER.warning("Tried to go back more years than records allow.") ;
        }
        backCycles = backYears * daysPerYear ;
        
        if ((backMonths * daysPerMonth + backCycles) > maxCycles)
        {
            backMonths = ((maxCycles - backCycles)/daysPerMonth) ;
            LOGGER.warning("Tried to go back more months than records allow.") ;
        }
        backCycles += backMonths * daysPerMonth + backDays ;
        
        if (backCycles > maxCycles)
        {
            backCycles = maxCycles ;
            LOGGER.warning("Tried to go back more days than records allow.") ;
        }
        
        return backCycles ;
    }
    
    /**
     * Extracts keys, usually agentIds, of HashMap in each record.
     * @param cycles
     * @param report
     * @return HashMap showing the cycles containing each key 
     */
    static protected HashMap<Object,ArrayList<Object>> FIND_AGENTID_KEYS(Integer[] cycles, ArrayList<HashMap<Object,?>> report)
    {
        HashMap<Object,ArrayList<Object>> agentIdKeys = new HashMap<Object,ArrayList<Object>>() ;

        for (int index : cycles)
            for ( Object agentId : report.get(index).keySet() )
                agentIdKeys = UPDATE_HASHMAP(index,agentId,agentIdKeys) ;
        return agentIdKeys ;
    }

    /**
     * Extracts values from ArrayList value, usually agentIds, of HashMap in each record.
     * @param cycles
     * @param report
     * @return HashMap showing the cycles containing each value in ArrayList value of HashMap 
     */
    static protected HashMap<Object,ArrayList<Object>> FIND_AGENTID_VALUES(Integer[] cycles, ArrayList<HashMap<Object,ArrayList<?>>> report)
    {
        HashMap<Object,ArrayList<Object>> agentIdValues = new HashMap<Object,ArrayList<Object>>() ;

        for (int index : cycles)
        {
            HashMap<Object,ArrayList<?>> cycleHashMap = report.get(index) ;
            for ( Object key : cycleHashMap.keySet() )
                for ( Object agentId : cycleHashMap.get(key))
                    agentIdValues = UPDATE_HASHMAP(index,agentId,agentIdValues) ;
        }
        return agentIdValues ;
    }
    
    /**
     * 
     * @param propertyName
     * @param reportList
     * @return (ArrayList) report with (ArrayList) subreports where the values 
     * in the subreports are averaged over the innermost ArrayList.
     */
    static public ArrayList<ArrayList<Object>> 
        PREPARE_MEAN_REPORT(String propertyName, ArrayList<ArrayList<ArrayList<Object>>> reportList)
    {
        // Find mean of reports
        ArrayList<ArrayList<Object>> meanReport = new ArrayList<ArrayList<Object>>() ;
        
        ArrayList<ArrayList<Object>> firstReport = reportList.get(0) ;
        int nbReports = reportList.size() ;
        int nbCycles = firstReport.size() ;
        int nbSubReports = firstReport.get(0).size() ;
        for (int cycle = 0 ; cycle < nbCycles ; cycle++)
        {
            String itemString ;
            ArrayList<Object> meanRecord = new ArrayList<Object>() ;
            for (int itemIndex = 0 ; itemIndex < nbSubReports ; itemIndex++ )
            {
                double itemValue = 0.0 ;
                for (ArrayList<ArrayList<Object>> report : reportList)
                {
                    ArrayList<Object> record = report.get(cycle) ;
                    itemValue += Double.valueOf(Reporter.EXTRACT_VALUE(propertyName,String.valueOf(record.get(itemIndex)))) ;
                }
                itemString = Reporter.ADD_REPORT_PROPERTY(propertyName, itemValue/nbReports) ;
                meanRecord.add(itemString) ;
            }
            meanReport.add((ArrayList<Object>) meanRecord.clone()) ;
        }
        return meanReport ;
    }

    /**
     * 
     * @param propertyName
     * @param reportList
     * @return (ArrayList) report with (ArrayList) subreports where the values 
     * in the subreports are averaged over the innermost ArrayList.
     */
    static public HashMap<Object,String> 
        PREPARE_MEAN_HASHMAP_REPORT(String propertyName, ArrayList<HashMap<Object,String>> reportList)
    {
        // Find mean of reports
        HashMap<Object,String> meanReport = new HashMap<Object,String>() ;
        
        HashMap<Object,String> firstReport = reportList.get(0) ;
        int nbReports = reportList.size() ;
        String firstReportString = firstReport.values().iterator().next() ;
        ArrayList<String> reportProperties = IDENTIFY_PROPERTIES(firstReportString) ;
        for (Object cycle : firstReport.keySet())
        {
            String itemString ;
            String meanRecord = "" ;
            //Number[] meanRecord = new Number[nbSubReports] ;
            //for (int itemIndex = 0 ; itemIndex < reportProperties.size() ; itemIndex++ );
            {
                double itemValue = 0.0 ;
                for (HashMap<Object,String> report : reportList)
                {
                    String record = report.get(cycle) ;
                    itemValue += Double.valueOf(Reporter.EXTRACT_VALUE(propertyName,record)) ;
                }
                itemString = Reporter.ADD_REPORT_PROPERTY(propertyName, itemValue/nbReports) ;
                meanRecord.concat(itemString) ;
            }
            meanReport.put(cycle,meanRecord) ;
        }
        return meanReport ;
    }

    static public String 
        PREPARE_MEAN_REPORT(ArrayList<String> reportList)
    {
        // Find mean of reports
        String meanReport = "" ; 
        
        String firstReport = reportList.get(0) ;
        int nbReports = reportList.size() ;
        ArrayList<String> reportProperties = IDENTIFY_PROPERTIES(firstReport) ;
        for (String propertyName : reportProperties)
        {
            double itemValue = 0.0 ;
            for (String record : reportList)
                itemValue += Double.valueOf(Reporter.EXTRACT_VALUE(propertyName,record)) ;
            meanReport += Reporter.ADD_REPORT_PROPERTY(propertyName, itemValue/nbReports) ;
        }
        return meanReport ;
    }

    /**
     * 
     * @param propertyName
     * @param reportList
     * @return (ArrayList) report with (ArrayList) subreports where the values 
     * in the subreports are averaged over the innermost ArrayList.
     */
    static public HashMap<Object,String> 
        PREPARE_MEAN_HASHMAP_REPORT(ArrayList<HashMap<Object,String>> reportList)
    {
        // Find mean of reports
        HashMap<Object,String> meanReport = new HashMap<Object,String>() ;
        
        HashMap<Object,String> firstReport = reportList.get(0) ;
        int nbReports = reportList.size() ;
        String firstReportString = firstReport.values().iterator().next() ;
        String meanRecord = "" ;
        ArrayList<String> reportProperties = IDENTIFY_PROPERTIES(firstReportString) ;
        
        
        for (Object key : firstReport.keySet())
        {
            for (String propertyName : reportProperties)
            {
                double itemValue = 0.0 ;
                for (HashMap<Object,String> report : reportList)
                {
                    String record = report.get(key) ;
                    itemValue += Double.valueOf(Reporter.EXTRACT_VALUE(propertyName,record)) ;
                }
                meanRecord += ADD_REPORT_PROPERTY(propertyName,itemValue/nbReports) ;
            }
            meanReport.put(key,meanRecord) ;
            
            // Prepare for next key
            meanRecord = "" ;
        }
        return meanReport ;
    }

    /**
     * 
     * @param propertyName
     * @param reportList
     * @return (HashMap) report with where the values are averaged reports in reportList.
     */
    static public HashMap<Object,Number[]> PREPARE_GRAY_REPORT(String[] simNames, String folderPath, int startYear, int endYear)
    {
        HashMap<Object,Number[]> grayReport = new HashMap<Object,Number[]>() ;
        int nbColumns = 4+4+4+1+1 ;
        String[] colNames = new String[nbColumns] ;
        
        String[] siteNames = MSM.SITE_NAMES ;
        int arraysLength = siteNames.length + 1 ;
        String[] arrayNames = new String[arraysLength] ;
        for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
            arrayNames[siteIndex] = siteNames[siteIndex] ;
        arrayNames[siteNames.length] = "all" ;
        
        ArrayList<Object> sortedYears = new ArrayList<Object>() ;
        
        int backYears = 1 + endYear - startYear ;
        
        ArrayList<HashMap<Object,String>> prevalenceReports = new ArrayList<HashMap<Object,String>>() ;  
        ArrayList<HashMap<Object,String>> notificationReports = new ArrayList<HashMap<Object,String>>() ;
        ArrayList<HashMap<Object,Number>> beenTestedReports = new ArrayList<HashMap<Object,Number>>() ;
        
        ArrayList<HashMap<Object,String>> incidenceReports = new ArrayList<HashMap<Object,String>>() ;  
        ArrayList<ArrayList<Object>> condomUseReports = new ArrayList<ArrayList<Object>>() ;
        for (String simulation : simNames)
        {
            ScreeningReporter screeningReporter = new ScreeningReporter(simulation, folderPath);
            prevalenceReports.add(screeningReporter.prepareYearsPrevalenceRecord(siteNames, backYears, endYear)) ;
            notificationReports.add(screeningReporter.prepareYearsNotificationsRecord(siteNames, backYears, endYear)) ;
            beenTestedReports.add(screeningReporter.prepareYearsBeenTestedReport(backYears, 0, 0, endYear)) ;
             
            EncounterReporter encounterReporter = new EncounterReporter(simulation, folderPath) ;
            incidenceReports.add(encounterReporter.prepareYearsIncidenceRecord(siteNames, backYears, endYear)) ;
            condomUseReports.add(encounterReporter.prepareYearsCondomUseRecord(backYears, endYear)) ;
        }
        HashMap<Object,String> prevalenceRecordYears = PREPARE_MEAN_HASHMAP_REPORT(prevalenceReports) ;
        HashMap<Object,String> notificationsRecordYears = PREPARE_MEAN_HASHMAP_REPORT(notificationReports) ;
        HashMap<Object,Number> beenTestedReportYears = MEAN_HASHMAP_REPORT(beenTestedReports) ;
        
        HashMap<Object,String> incidenceReportYears = PREPARE_MEAN_HASHMAP_REPORT(incidenceReports) ;
        ArrayList<Object> condomUseYears = AVERAGED_REPORT(condomUseReports,"proportion") ;
        
        // Construct Gray report
        for (int year = startYear ; year <= endYear ; year++)
        {
            Number[] values = new Number[nbColumns] ;
            //LOGGER.log(Level.INFO, "{0}", prevalenceRecordYears.get(year));
            for (int index = 0 ; index < arraysLength ; index++ )    // 
            {
                values[index] = Double.valueOf(EXTRACT_VALUE(arrayNames[index],prevalenceRecordYears.get(year))) ;
                values[arraysLength + index] = Double.valueOf(EXTRACT_VALUE(arrayNames[index],incidenceReportYears.get(year))) ;
                values[2*arraysLength + index] = Double.valueOf(EXTRACT_VALUE(arrayNames[index],notificationsRecordYears.get(year))) ;
            }
            //LOGGER.log(Level.INFO, "condom:{0}|", condomUseYears.get(year - startYear).toString());
            String condomValue = condomUseYears.get(year - startYear).toString() ;
            values[3*arraysLength] = Double.valueOf(EXTRACT_VALUE("proportion",condomValue)) ;
            values[3*arraysLength + 1] = beenTestedReportYears.get(year) ;
            
            grayReport.put(year, values) ;
            sortedYears.add(year) ;
        }
        
        // Set up column names in report
        for (int index = 0 ; index < arraysLength ; index++ )    // 
        {
            colNames[index] = "prevalence_" + arrayNames[index] ;
            colNames[arraysLength + index] = "incidence_" + arrayNames[index] ;
            colNames[2*arraysLength + index] = "notification_" + arrayNames[index] ;
        }
        colNames[3*arraysLength] = "condom_use" ;
        colNames[3*arraysLength + 1] = "testing_coverage" ;
        
        WRITE_CSV(grayReport,"year",colNames,sortedYears,"Gray_report",simNames[0],"C:\\Users\\MichaelWalker\\UNSW\\NSW PSRP - Documents\\Model development\\output\\") ;
        return grayReport ;
    }
    
    /**
     * Stores an ArrayList (String) report as a csv file for other packages to read.
     * @param report 
     * @param reportName 
     * @param simName 
     * @param folderPath 
     */
    static public void WRITE_CSV(ArrayList<Object> report, String reportName, String simName, String folderPath)
    {
        Reporter.WRITE_CSV(report, new String[] {reportName}, simName, folderPath) ;
    }


    /**
     * Stores an ArrayList (String) report as a csv file for other packages to read.
     * @param report 
     * @param scoreNames 
     * @param simName 
     * @param folderPath 
     */
    static public void WRITE_CSV(ArrayList<Object> report, String[] scoreNames, String simName, String folderPath)
    {
        String filePath ;
        String line ;
        
        ArrayList<String> properties = new ArrayList<String>() ;
        String fileHeader = "cycle," ;
        
        //Object firstRecord = report.get(0) ;
        filePath = folderPath + simName + scoreNames[0] + ".csv";
        properties = IDENTIFY_PROPERTIES((String) report.get(0)) ;    // TODO: Try (String) instead of String.valueOf()
        //for (int index = 1 ; index < properties.size() ; index++ )
          //  fileHeader += "," + properties.get(index) ;
        fileHeader += String.join(COMMA, properties) ;
        try
        {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath,false));
            fileWriter.write(fileHeader) ;
            fileWriter.newLine();
            int cycle = 0 ;
            for (Object record : report)
            {
                line = String.valueOf(cycle) ;
                for (String property : properties)
                    line += COMMA + EXTRACT_VALUE(property,(String) record) ;
                fileWriter.write(line) ;
                fileWriter.newLine() ;
                cycle++ ;
            }
            fileWriter.close() ;
        }
        catch( Exception e )
        {
            LOGGER.info(e.toString()) ;
        }
    }


    /**
     * Stores an ArrayList (String) report as a csv file for other packages to read.
     * @param report 
     * @param scoreNames 
     * @param property 
     * @param simName 
     * @param folderPath 
     */
    static public void WRITE_CSV(ArrayList<ArrayList<Object>> report, String[] scoreNames, String property, String simName, String folderPath)
    {
        String filePath ;
        String line ;
        String fileHeader = "cycle," ;
        
        {
            filePath = folderPath + simName + property + ".csv";
            fileHeader += String.join(COMMA, scoreNames) ;
            fileHeader += COMMA + property ;
        }
        try
        {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath,false));
            fileWriter.write(fileHeader) ;
            fileWriter.newLine();
            for (int cycle = 0; cycle < 4000 ; cycle++)
            {
            line = String.valueOf(cycle) ;
            for (Object record : report)
            {
                //for (String entry : (ArrayList<String>) record)
                String entry = ((ArrayList<String>) record).get(cycle) ;
                line += COMMA + EXTRACT_VALUE(property,entry) ;
            }
                fileWriter.write(line) ;
                fileWriter.newLine() ;
            }
            fileWriter.close() ;
        }
        catch( Exception e )
        {
            LOGGER.info(e.toString()) ;
        }
    }
    
    /**
     * Stores a (HashMap of ArrayList or Object) report as a csv file for other packages to read.
     * @param report 
     * @param categoryName 
     * @param scoreNames 
     * @param reportName 
     * @param simName 
     * @param folderPath 
     */
    static public void WRITE_CSV(HashMap<Object,Number[]> report, String categoryName, String[] scoreNames, String reportName, String simName, String folderPath)
    {
        WRITE_CSV(report, categoryName, scoreNames, new ArrayList<Object>(), reportName, simName, folderPath) ;
    }
    
    /**
     * Stores a (HashMap of ArrayList or Object) report as a csv file for other packages to read.
     * @param report 
     * @param categoryName 
     * @param scoreNames 
     * @param reportName 
     * @param simName 
     * @param folderPath 
     */
    static public boolean WRITE_CSV(HashMap<Object,Number[]> report, String categoryName, String[] scoreNames, ArrayList<Object> categoryList, String reportName, String simName, String folderPath)
    {
        String filePath = folderPath + simName + reportName + ".csv" ;
        String line ;
        Object[] value ;
        
        int nbProperties = 0 ;
        //Determine if report values are ArrayList<Object>
        Object[] firstRecord = (Object[]) report.values().toArray()[0] ;
        //LOGGER.log(Level.INFO, "{0}", firstRecord);

        String fileHeader = categoryName.concat(COMMA) ;
        fileHeader += String.join(COMMA, scoreNames)  ;
        //LOGGER.info(fileHeader) ;
        
        //HashMap<Object,Number[]> writeReport = report.size()
        try
        {
            nbProperties = firstRecord.length ;
            //fileHeader += COMMA + reportName ;
        }
        catch ( Exception e )
        {
            LOGGER.info("values are not Arrays");
            nbProperties = 1 ;
        }
        //LOGGER.log(Level.INFO, "nbProperties:{1}", new Object[] {nbProperties});
        if (categoryList.isEmpty())
            categoryList = new ArrayList<Object>(report.keySet()) ;
        
        try
        {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath,false));
            fileWriter.write(fileHeader) ;
            fileWriter.newLine() ;
            for (Object key : categoryList)
            {
                value = report.get(key) ;
                line = String.valueOf(key) ;
                if (nbProperties > 1)
                    for (Object item : value)
                        line += COMMA + String.valueOf(item) ;
                else
                    line += COMMA + String.valueOf(value[0]) ;
                fileWriter.write(line) ; 
                fileWriter.newLine() ;
                //LOGGER.info(line);
            }
            fileWriter.close() ;
        }
        catch( Exception e )
        {
            LOGGER.info(e.toString()) ;
            return false ;
        }
        return true ;
    }
    
    /**
     * Stores a (HashMap of ArrayList or Object) report as a csv file for other packages to read.
     * @param report
     * @param categoryName
     * @param categoryList
     * @param reportName
     * @param simName
     * @param folderPath 
     */
    static public boolean WRITE_CSV_STRING(HashMap<Object,String> report, String categoryName, ArrayList<Object> categoryList, String reportName, String simName, String folderPath)
    {
        HashMap<Object,Number[]> newReport = new HashMap<Object,Number[]>() ;
        
        String scoreString ;
        String reportValue ;
        Number scoreValue ;
        ArrayList<String> propertyList = IDENTIFY_PROPERTIES(report.values().iterator().next()) ;
        String[] propertyArray = new String[propertyList.size()] ;
        boolean propertyConstructed = false ;
        for (Object key : report.keySet())
        {
            reportValue = report.get(key) ;
            
            Number[] numberArray = new Number[propertyList.size()] ;
            for (int propertyIndex = 0 ; propertyIndex < numberArray.length ; propertyIndex++ )
            {
                scoreString = EXTRACT_VALUE(propertyList.get(propertyIndex),reportValue) ;
                if (int.class.isInstance(scoreString) || Integer.class.isInstance(scoreString)) 
                    scoreValue = Integer.valueOf(scoreString) ;
                else
                    scoreValue = Double.valueOf(scoreString) ;
                numberArray[propertyIndex] = scoreValue ;
                
                if (!propertyConstructed)
                    propertyArray[propertyIndex] = propertyList.get(propertyIndex) ;
            }
            newReport.put(key, numberArray) ;
        }
        
        return WRITE_CSV(newReport, categoryName, propertyArray, categoryList, reportName, simName, folderPath) ;
    }

    /**
     * Stores a (HashMap of ArrayList or Object) report as a csv file for other packages to read.
     * @param report
     * @param categoryName
     * @param categoryList
     * @param reportName
     * @param simName
     * @param folderPath 
     */
    static public boolean WRITE_CSV_STRING(HashMap<Object,String> report, String categoryName, String reportName, String simName, String folderPath)
    {
        return WRITE_CSV_STRING(report, categoryName, new ArrayList<Object>(), reportName, simName, folderPath) ;
    }
    
    /**
     * TODO: Unit test
     * @param record
     * @return (ArrayList) String names of properties with given values in record.
     */
    static public ArrayList<String> IDENTIFY_PROPERTIES(String record)
    {
        ArrayList<String> propertyArray = new ArrayList<String>() ;
        
        int colonIndex = record.indexOf(":") ;
        int spaceIndex ;
        int nextColonIndex ;
        int propertyIndex = 0 ;
        
        while (colonIndex > 0)
        {
            nextColonIndex = record.indexOf(":",colonIndex+1) ;
            spaceIndex = record.indexOf(" ",colonIndex) ;
            if  (spaceIndex < nextColonIndex || nextColonIndex < 0)
            {
                propertyArray.add(record.substring(propertyIndex, colonIndex)) ;
                propertyIndex = spaceIndex + 1 ;
            }
            else
                propertyIndex = colonIndex + 1 ;
            colonIndex = nextColonIndex ;
        }
        return propertyArray ;
    }
    
    /**
     * TODO: Unit test
     * @param record
     * @return (ArrayList) String names of labels without given values in record.
     */
    static public ArrayList<String> IDENTIFY_LABELS(String record)
    {
        ArrayList<String> labelArray = new ArrayList<String>() ;
        
        int colonIndex = record.indexOf(":") ;
        int spaceIndex ;
        int nextColonIndex ;
        int propertyIndex = 0 ;
        
        while (colonIndex > 0)
        {
            nextColonIndex = record.indexOf(":",colonIndex+1) ;
            spaceIndex = record.indexOf(" ",colonIndex) ;
            if  (spaceIndex < nextColonIndex || nextColonIndex < 0)
            {
                propertyIndex = spaceIndex + 1 ;
            }
            else
            {
                labelArray.add(record.substring(propertyIndex, colonIndex)) ;
                propertyIndex = colonIndex + 1 ;
            }
            colonIndex = nextColonIndex ;
        }
        return labelArray ;
    }
    
    /**
     * Averages value of propertyName over (ArrayList) reports
     * @param reports
     * @param propertyName
     * @return (ArrayList) AVERAGED_REPORT
     */
    static public ArrayList<Object> AVERAGED_REPORT(ArrayList<ArrayList<Object>> reports, String propertyName)
    {
        ArrayList<String> propertyNames = new ArrayList<String>() ;
        propertyNames.add(propertyName) ;
        return AVERAGED_REPORT(reports, propertyNames) ;
    }
    
    /**
     * Averages value of propertyName over (ArrayList) reports
     * @param reports
     * @param propertyNames
     * @return (ArrayList) AVERAGED_REPORT
     */
    static public ArrayList<Object> AVERAGED_REPORT(ArrayList<ArrayList<Object>> reports, ArrayList<String> propertyNames)
    {
        ArrayList<Object> averagedReport = new ArrayList<Object>() ;
        
        String record ;
        String meanRecord ;
        double meanValue ;
        
        int nbRecords = reports.get(0).size() ;
        int nbReports = reports.size() ;
        
        ArrayList<String> meanProperties ;
        if (propertyNames.isEmpty())
            meanProperties = IDENTIFY_PROPERTIES((String) reports.get(0).get(0)) ;
        else
        {
            LOGGER.info("Correctly identified propertyName 'proportion'") ;
            meanProperties = propertyNames ;
        }
        
        for (int cycle = 0 ; cycle < (nbRecords) ; cycle++ )
        {
            meanRecord = "" ;
            for (String property : meanProperties)
            {
                meanValue = 0.0 ;
                for (ArrayList<Object> report : reports)
                {
                    record = (String) report.get(cycle) ;
                    meanValue += Double.valueOf(EXTRACT_VALUE(property,record)) ;
                }
                meanValue = meanValue/nbReports ;
                meanRecord += ADD_REPORT_PROPERTY(property,meanValue) ;
            }
            averagedReport.add(meanRecord) ;
        }
        LOGGER.log(Level.INFO, "{0}", averagedReport);
        return averagedReport ;
    }

    /**
     * Averages over (Number[]) entries in (ArrayList) reports
     * @param reports 
     * @return (HashMap) AVERAGED_REPORT
     */
    static public HashMap<Object,Number[]> AVERAGED_HASHMAP_REPORT(ArrayList<HashMap<Object,Number[]>> reports)
    {
        HashMap<Object,Number[]> averagedReport = new HashMap<Object,Number[]>() ;
        
        double nbReports = reports.size() ;
        double meanValue ;
        int nbEntries = 0 ;
        
        HashMap<Object,Number[]> sampleReport = reports.get(0) ;
        nbEntries = sampleReport.values().iterator().next().length ;
        
        for (Object key : sampleReport.keySet())
        {
            Number[] meanRecord = new Number[nbEntries] ;
            for (int entry = 0 ; entry < nbEntries ; entry++ )
            {
                meanValue = 0.0 ;
                for (HashMap<Object,Number[]> report : reports)
                    meanValue += report.get(key)[entry].doubleValue() ;
                meanRecord[entry] = meanValue/nbReports ;
            }
            averagedReport.put(key,(Number[]) meanRecord.clone()) ;
        }
        
        return averagedReport ;
    }

    /**
     * Averages over (Number) entries in (ArrayList) reports
     * @param reports 
     * @return (HashMap) AVERAGED_REPORT
     */
    static public HashMap<Object,Number> MEAN_HASHMAP_REPORT(ArrayList<HashMap<Object,Number>> reports)
    {
        HashMap<Object,Number> averagedReport = new HashMap<Object,Number>() ;
        
        double nbReports = reports.size() ;
        double meanValue ;
        
        //HashMap<Object,Number> sampleReport = reports.get(0) ;
        
        ArrayList<Object> keyList = new ArrayList<Object>() ;
        for (HashMap<Object,Number> report : reports)
            for (Object key : report.keySet())
                if (!keyList.contains(key))
                    keyList.add(key) ;
        
        
        for (Object key : keyList)
        {
            meanValue = 0.0 ;
            for (HashMap<Object,Number> report : reports)
                if (report.containsKey(key) && report.get(key) != null)
                    meanValue += report.get(key).doubleValue() ;
            averagedReport.put(key,meanValue/nbReports) ;
        }
        return averagedReport ;
    }

    
    public Reporter()
    {
        
    }
    
    public Reporter(String simname, ArrayList<String> report)
    {
        input = report ;
        simName = simname ;
        //this.generateReports = generateReports ;
        //this.encounterReports = encounterReports ;
        //this.clearReports = clearReports ;
        //this.screenReports = screenReports ;
    }
    
    public Reporter(String simname, String fileName)
    {
        initReporter(simname, fileName) ;
    }

    /**
     * Initialises Reporter from saved simulation files. Allows for easier
     * construction using reflection.
     * @param simname
     * @param fileName 
     */
    protected final void initReporter(String simname, String fileName)
    {
        simName = simname ;
        String reporterName = this.getClass().asSubclass(this.getClass()).getSimpleName().toLowerCase() ;
        // What sort of Reporter is this? Needed to identify files.
        reporterName = reporterName.substring(0,reporterName.lastIndexOf("reporter")) ;
        reader = new Reader(simname, reporterName, fileName) ;
        input = reader.updateOutputArray() ;
        
    }
    
    /**
     * Loads the next Report file into input if there is another file to read.
     * Resets fileIndex to 0 if there is not.
     * @return true if update successful, false if all files have already been read.
     */
    protected boolean updateReport()
    {
        if (reader.fileIndex >= reader.fileNames.size())
        {
            reader.fileIndex = 0 ;
            input = reader.updateOutputArray() ;
            return false ;
        }
        input = reader.updateOutputArray();
        return true ;
    }
    
    /**
     * 
     * @param backCycles
     * @param endCycle
     * @return Report of backCycles records leading up to endCycle'th
     */
    protected ArrayList<String> getBackCyclesReport(int backCycles, int endCycle)
    {
        return reader.getBackCyclesReport(backCycles, endCycle) ;
    }
        
    /**
     * Resets reader to first input file before returning.
     * @return Last saved file of reader.
     */
    protected ArrayList<String> getFinalReport()
    {
        ArrayList<String> finalReport = reader.getFinalReport() ;
        updateReport() ;
        return finalReport ;
    }
    
    /**
     * Reads value from corresponding METADATA file.
     * @return (int) the total number of cycles in the corresponding simulation.
     */
    public int getMaxCycles()
    {
        return Integer.valueOf(getMetaDatum("Community.MAX_CYCLES")) ;
    }
    
    /**
     * Reads value from corresponding METADATA file.
     * @return (int) the total number of cycles in the corresponding simulation.
     */
    public int getPopulation()
    {
        return Integer.valueOf(getMetaDatum("Community.POPULATION")) ;
    }
    
    /**
     * Reads String from METADATA file, strips square brackets, and splits into
     * String[] at "," .
     * @return (String[]) names of Sites included in simulation.
     */
    protected String[] getSiteNames()
    {
        //Read (String) line in METADATA file
        String nameString = getMetaDatum("Agent.SITE_NAMES") ;
        int leftIndex = nameString.indexOf("[") ;
        int rightIndex = nameString.indexOf("]") ;
        
        // Convert to String[] and remove whitespace
        String[] nameArray = nameString.substring(leftIndex, rightIndex).split(COMMA) ;
        for (int nameIndex = 0 ; nameIndex < nameArray.length ; nameIndex++ )
            nameArray[nameIndex] = nameArray[nameIndex].trim() ;
            
        return nameArray ;
    }
    
    protected ArrayList<String> getBackCyclesReport(int backYears, int backMonths, int backDays)
    {
        int backCycles = getBackCycles(backYears, backMonths, backDays) ;
        return reader.getBackCyclesReport(backCycles) ;
    }
    
    protected ArrayList<String> getBackCyclesReport(int backYears, int backMonths, int backDays, int endCycle)
    {
        int backCycles = GET_BACK_CYCLES(backYears, backMonths, backDays, endCycle) ;
        
        return reader.getBackCyclesReport(backCycles,endCycle) ;
    }
    
    protected int getBackCycles(int backYears, int backMonths, int backDays)
    {
        int maxCycles = getMaxCycles() ; 
        
        return GET_BACK_CYCLES(backYears, backMonths, backDays, maxCycles) ;
    }
    
    
    /**
     * 
     * @return (String) Path to folder with input files from simulation.
     */
    public String getFolderPath()
    {
        return reader.getFolderPath() ;
    }
    
    /**
     * 
     * @return (ArrayList(String)) complete input from concatenation of all
     * input files.
     */
    public ArrayList<String> getFullInput()
    {
        ArrayList<String> fullInput = (ArrayList<String>) input.clone() ;
        
        while(updateReport())
            fullInput.addAll((ArrayList<String>) input.clone()) ;
        return fullInput ;
    }
    
    /**
     * FIXME: only compatible with Reporter initiated from file.
     * @return opening record of opening input file 
     */
    public String getInitialRecord()
    {
        String initialRecord = "" ;
        try
        {
            initialRecord = reader.getInitialRecord() ;
        }
        catch( Exception e )
        {
            LOGGER.info(e.toString()) ;
        }
        return initialRecord ;
    }
    
    /**
     * FIXME: only compatible with Reporter initiated from file.
     * @return final record of final input file 
     */
    public String getFinalRecord()
    {
        String finalRecord = "" ;
        try
        {
            finalRecord = reader.getFinalRecord() ;
        }
        catch( Exception e )
        {
            LOGGER.info(e.toString()) ;
        }
        return finalRecord ;
    }
    
    /**
     * 
     * @param metaDatum
     * @return (String) value of metaDatum from reader.metaData .
     */
    public String getMetaDatum(String metaDatum)
    {
        return reader.getMetaDatum(metaDatum).trim() ;
    }
    
    /**
     * 
     * @param recordNb
     * @return output[reportNb] or error String if not available
     */
    protected String presentRecord(int recordNb)
    {
        if (recordNb < output.size())
            return output.get(recordNb) ;

        String message = "Requested cycle " + Integer.toString(recordNb) + "unavailable" ;
        return message ;
    }
    
    /**
     * If report reportName has already been found and stored then return it.
     * Otherwise, find prepareReport method, prepare the report and store it in
     * Reporter.reportList for later. Used when there are no parameters.
     * @param reportName
     * @param reporter
     * @return reporter.report specified by reportName
     */
    public Object getReport(String reportName, Reporter reporter)
    {
        return getReport(reportName, reporter, new Class[] {}, new Object[] {}) ;
    }
    
    
    /**
     * If report reportName has already been found and stored then return it.
     * Otherwise, find prepareReport method, prepare the report and store it in
     * Reporter.reportList for later.
     * @param reportName
     * @param reporter
     * @param parametersClazzes
     * @param parameters
     * @return (Object) reporter.report specified by reportName
     */
    public Object getReport(String reportName, Reporter reporter, Class[] parametersClazzes, Object[] parameters)
    {
        if (reportList.containsKey(reportName))
        {
            LOGGER.info("recall " + reportName) ;
            return reportList.get(reportName) ;
        }
        
        String methodName = reportName.substring(0, 1).toUpperCase() 
                + reportName.substring(1) ;
        methodName = "prepare" + methodName + "Report" ;
        
        return getReport(reportName, methodName, reporter, parametersClazzes, parameters) ;
    }
    
    /**
     * Invokes Method methodName
     * @param reportName
     * @param methodName
     * @param reporter
     * @param parametersClazzes
     * @param parameters
     * @return (Object) reporter.report specified by reportName
     */
    public Object getReport(String reportName, String methodName, Reporter reporter, Class[] parametersClazzes, Object[] parameters)
    {
        Object report = new Object() ;
        
        Class reporterClazz = reporter.getClass().asSubclass(Reporter.class) ;
        LOGGER.info("prepare " + reportName) ;
        try
        {
            // Call prepareReport()
            Method prepareMethod = reporterClazz.getDeclaredMethod(methodName, parametersClazzes) ;
            report = prepareMethod.invoke(reporter, parameters) ;
            // Save in reportList for later retrieval
            reportList.put(reportName, report) ;
            //LOGGER.log(Level.INFO, "{0}", reportList.keySet());
        }
        catch ( Exception e )
        {
            LOGGER.severe(e.toString());
        }
        //LOGGER.log(Level.INFO, "{1} {0}", new Object[] {report,reportName}) ;
        
        return report ;
    }
    
    /**
     * If report reportName has already been found and stored then return it.
     * Otherwise, find prepareReport method, prepare the report and store it in
     * Reporter.reportList for later.
     * @param recordName
     * @param reporter
     * @param parametersClazzes
     * @param parameters
     * @return reporter.report specified by reportName
     */
    public Object getRecord(String recordName, Reporter reporter, Class[] parametersClazzes, Object[] parameters)
    {
        if (reportList.containsKey(recordName))
        {
            LOGGER.info("recall " + recordName) ;
            return reportList.get(recordName) ;
        }
        
        String methodName = recordName.substring(0, 1).toUpperCase() 
                + recordName.substring(1) ;
        methodName = "prepare" + methodName + "Record" ;
        
        return getReport(recordName, methodName, reporter, parametersClazzes, parameters) ;
    }
    
    
    /**
     * Adds filter to Reporter.
     * @param name
     * @param value 
     */
    protected void addFilter(String name, String value)
    {
        filterPropertyNames.add(name) ;
        filterPropertyValues.add(value) ;
    }
    
    /**
     * Removes from each Record those bounded substrings either missing required 
     * property or with incorrect value for that property.
     * TODO: Implement Arrays of values.
     * @param rawReport
     * @return 
     */
    protected ArrayList<String> applyFilters(ArrayList<String> rawReport)
    {
        ArrayList<String> filteredReport = new ArrayList<String>() ;
        
        String bound = getFilterBound(rawReport.get(0)) ;
        
        for (int filterIndex = 0 ; filterIndex < filterPropertyNames.size() ; filterIndex++ )
        {
            String filterName = filterPropertyNames.get(filterIndex) ;
            String filterValue = filterPropertyValues.get(filterIndex) ;
            if (filterValue.isEmpty())    // Require contents only
                for (String record : rawReport)
                    filteredReport.add(BOUNDED_STRING_BY_CONTENTS(filterName,bound,record)) ;
            else    // Property filterName required to have filterValue
                for (String record : rawReport)
                    filteredReport.add(BOUNDED_STRING_BY_VALUE(filterName, filterValue, bound, record)); // Filter record // Filter record
        }
        return filteredReport ;
    }
    
    /**
     * 
     * @param rawRecord - Usually first record in Report to be filtered.
     * @return 
     */
    protected String getFilterBound(String rawRecord)
    {
        String bound ;
        int commaIndex = rawRecord.indexOf(",") ;
        int spaceIndex = rawRecord.indexOf(" ") ;
        int colonIndex1 = rawRecord.indexOf(":") ;
        int colonIndex2 = rawRecord.indexOf(":",colonIndex1+1) ;
        if (colonIndex2 > spaceIndex)
            bound = rawRecord.substring(commaIndex+1, colonIndex1) ;
        else    // spaceIndex > colonIndex2
            bound = rawRecord.substring(colonIndex1+1, colonIndex2) ;
        return bound ;
    }

    /**
     * getter() for simName.
     * @return (String) simName
     */
    public String getSimName()
    {
        return simName ;
    }
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args)
    {
        String folderPath = "output/prePrEP/" ;
        //String[] simNames = new String[] {"to2014contact96dPop40000Cycles4380","to2014contact96cPop40000Cycles4380","to2014contact96bPop40000Cycles4380","to2014contact96aPop40000Cycles4380"} ;
        String[] simNames = new String[] {"gamma4bbPop40000Cycles4380","gamma4bcPop40000Cycles4380","gamma4bdPop40000Cycles4380","gamma4bePop40000Cycles4380","gamma4bfPop40000Cycles4380"} ; 
       
        PREPARE_GRAY_REPORT(simNames,folderPath,2007,2016) ;
    }

    /**
     * Object to read saved File output and feed it to Reporter
     */
    protected class Reader
    {
    //	ArrayList<String> encounterReports, ArrayList<String> clearReports, ArrayList<String> screenReports)
        private ArrayList<String> fileNames = new ArrayList<String>() ;
        private String folderPath ;
        private int fileIndex = 0;
        private int cyclesPerFile ;
        private String simName ;
        ArrayList<String> metaData ;
        
        protected Reader()
        {
            
        }
        
        protected Reader(String simName, String reporterName, String filePath)
        {
            this.simName = simName ;
            this.folderPath = filePath ;
            if ("screening".equals(reporterName))    // TODO: Remove need for this step
                reporterName = "infection" ;
            fileNames = initFileNames(simName + reporterName) ;
            initMetaData() ;
            cyclesPerFile = initCyclesPerFile() ;
        }
        
        /**
         * 
         * @return (String) path to folder with Reader's files.
         */
        protected String getFolderPath()
        {
            return folderPath ;
        }
        
        /**
         * Updates Reader output from next file.
         * TODO: Implement all this with an iterator.
         * @return Updated Reporter.input .
         */
        protected ArrayList<String> updateOutputArray()
        {
            ArrayList<String> outputArray = new ArrayList<String>() ;
            String record ;
            if (fileIndex >= fileNames.size())
                return outputArray ;
            try
            {
                //LOGGER.info(folderPath + getFileName());
                BufferedReader fileReader = new BufferedReader(new FileReader(folderPath + getFileName())) ;
                record = fileReader.readLine() ;
                if (record == null)
                    LOGGER.info(Level.WARNING + ":Empty report file");
                while (record != null)
                {
                    outputArray.add(record) ;
                    record = fileReader.readLine() ;
                }
                fileReader.close() ;
            }
            catch ( Exception e )
            {
                LOGGER.log(Level.SEVERE, e.toString());
                record = "" ;
            }
            if (outputArray.isEmpty())
                LOGGER.log(Level.SEVERE, "Empty Report from File at {0}", new Object[]{folderPath});
            
            return outputArray ;
        }
        
        /**
         * Adjusts the value of fileIndex to keep track of Report files. 
         * @return (String) fileName of next file.
         */
        private String getFileName()
        {
            fileIndex++ ;
            return fileNames.get(fileIndex-1) ;
        }
        
        /**
         * 
         * @param simName
         * @return ArrayList of fileNames, excluding the METADATA file.
         */
        protected ArrayList<String> initFileNames(String simName)
        {
            ArrayList<String> nameArray = new ArrayList<String>() ;
            File folder = new File(folderPath) ;
            for (File file : folder.listFiles()) 
                if (file.isFile()) 
                {
                    String fileName = file.getName() ;
                    if (fileName.startsWith(simName) && fileName.endsWith("txt"))
                        nameArray.add(fileName) ;
                }
            //LOGGER.log(Level.INFO, "{0}", nameArray) ;
            //Collections.sort(nameArray, String.CASE_INSENSITIVE_ORDER) ;
            nameArray.sort(String.CASE_INSENSITIVE_ORDER);
            //LOGGER.log(Level.INFO, "{0}", nameArray) ;
            return nameArray ;
        }
        
        /**
         * 
         * @return (int) number of cycles in each file.
         */
        private int initCyclesPerFile()
        {
            if (fileNames.size() == 1)
                return Integer.valueOf(getMetaDatum("Community.MAX_CYCLES").trim()) ; // Cannot use getMaxCycles()
            String fileName1 = fileNames.get(1) ;
            int dashIndex = fileName1.indexOf("-") + 1 ; // Want following position
            int dotIndex = fileName1.indexOf("txt") - 1 ; // -1 for "."
            
            return Integer.valueOf(fileName1.substring(dashIndex,dotIndex)) ;
        }
        
        /**
         * @return opening record of opening input file
         * @throws FileNotFoundException
         * @throws IOException 
         */
        private String getInitialRecord() throws FileNotFoundException, IOException
        {
            BufferedReader fileReader = new BufferedReader(new FileReader(folderPath + fileNames.get(0))) ;
            String record = fileReader.readLine() ;
            fileReader.close() ;
            //LOGGER.info(record) ;
            if (record == null)
                LOGGER.info(Level.WARNING + ":Empty report file");

            return record ;
        }
    
        /**
         * @return opening record of opening input file
         * @throws FileNotFoundException
         * @throws IOException 
         */
        private String getFinalRecord() throws FileNotFoundException, IOException
        {
            // Read final input file
            BufferedReader fileReader = new BufferedReader(new FileReader(folderPath + fileNames.get(fileNames.size()-1))) ;
            String outputString = "" ;
            // Find last line
            for (String record = "" ;  record != null ; record = fileReader.readLine() )
                outputString = record ;
            fileReader.close() ;
            
            if (outputString.isEmpty())
                LOGGER.info(Level.WARNING + ":Empty report file");

            return outputString ;
        }
    
        /**
         * 
         * @return (ArrayList) Final saved installment of report.
         */
        private ArrayList<String> getFinalReport()
        {
            fileIndex = fileNames.size() - 1 ;
            return updateOutputArray() ;
        }
        
        /**
         * 
         * @param cycle
         * @return (ArrayList) Report containing the specified (int) cycle
         */
        private void getSpecificFile(int cycle)
        {
            int divCycle = cycle/cyclesPerFile ;
            if (cyclesPerFile * divCycle == cycle)
                fileIndex = divCycle - 1 ;
            else
                fileIndex = divCycle ;
        }
        
        /**
         * 
         * @param backCycles
         * @param endCycle
         * @return Report of backCycles records leading up to endCycle'th
         */
        private ArrayList<String> getBackCyclesReport(int backCycles, int endCycle)
        {
            ArrayList<String> outputList = new ArrayList<String>() ;
            
            try
            {
                int startCycle = endCycle - backCycles ;
                
                // Open file
                getSpecificFile(startCycle) ;
                BufferedReader fileReader = new BufferedReader(new FileReader(folderPath + fileNames.get(fileIndex))) ;
                        
                boolean newFile = false ;

                // Move to starting line in file
                int startLine = startCycle % cyclesPerFile ;
                int endLine = startLine + backCycles ;
                int pauseLine ;
                
                String outputString ;

                int readLines = 0 ;
                // Skip unwanted lines
                for (int lineNb = 0 ; lineNb < startLine ; lineNb++ )
                    fileReader.readLine() ;
                while (readLines < backCycles)
                {
                    // Open new file if reached end of previous one
                    if (newFile)
                    {
                        fileReader = new BufferedReader(new FileReader(folderPath + fileNames.get(fileIndex))) ;
                        newFile = false ;
                    }

                    // End loop at end of file if not before
                    pauseLine = endLine;
                    if (pauseLine > cyclesPerFile)
                        pauseLine = cyclesPerFile ;

                    for (int lineNb = startLine ; lineNb < pauseLine ; lineNb++ )
                    {
                        outputString = fileReader.readLine() ;
                        if (outputString == null)
                            break ;
                        outputList.add(outputString) ;
                        readLines++ ;
                    }
                    fileReader.close() ;

                    // Prepare for next file
                    startLine = 0 ;
                    endLine -= cyclesPerFile ;
                    fileIndex++ ;
                    newFile = true ;
                }
            }
            catch( Exception e )
            {
                LOGGER.severe(e.toString());
                assert(false) ;
            }
            return outputList ;
        }
                
        /**
         * Reads backwards through the files. Used when only last backCycles are 
         * of interest.
         * @param backCycles
         * @return Report of last backCycles read from files .
         */
        private ArrayList<String> getBackCyclesReport(int backCycles)
        {
            ArrayList<String> outputList = new ArrayList<String>() ;
            ArrayList<String> fileList = new ArrayList<String>() ;
            
            fileIndex = fileNames.size() - 1 ;
            // From which line do we had files
            int fromLine = 0 ;
            
            try
            {
                while (backCycles > 0)
                {
                    BufferedReader fileReader = new BufferedReader(new FileReader(folderPath + getFileName())) ;
                    fileIndex = fileIndex - 2 ; // -2 to compensate for fileIndex++ in getFileName()

                    for (String record = fileReader.readLine() ;  record != null ; record = fileReader.readLine() )
                    {
                        fileList.add(record) ;
                        backCycles-- ;
                    }
                    if (backCycles < 0)
                        fromLine = -backCycles ;

                    outputList.addAll(0,fileList.subList(fromLine, fileList.size())) ;

                    fileReader.close() ;
                }
            }
            catch ( Exception e )
            {
                LOGGER.log(Level.SEVERE, "{0}", e.toString());
            }
            if (outputList.isEmpty())
                LOGGER.log(Level.SEVERE, "Empty Report from File at {0}", new Object[]{folderPath});
            
            fileIndex = 0 ;
            
            return outputList ;
        }
        
        /**
         * Initialises metaData property of Reader from file,
         */
        private void initMetaData() 
        {
            metaData = new ArrayList<String>() ;
            try
            {
                BufferedReader fileReader = new BufferedReader(new FileReader(folderPath + simName + "-METADATA.txt")) ;
                ArrayList<String> outputString = new ArrayList<String>() ;
                // Find last line
                for (String record = "" ;  record != null ; record = fileReader.readLine() )
                    outputString.add(record) ;
                
                fileReader.close() ;

                metaData = outputString ;
            }
            catch (Exception e)
            {
                LOGGER.info(e.toString()) ;
            }
        }
        
        /**
         * 
         * @param metaDatum
         * @return (String) value of metaDatum from metaData.
         */
        private String getMetaDatum(String metaDatum)
        {
            for (String record : metaData)
            {
                if (record.isEmpty())
                    continue ;
                int colonIndex = record.indexOf(":") ;
                if (metaDatum.equals(record.substring(0, colonIndex)))
                    return record.substring(colonIndex + 1) ;
            }
            return "" ;
        }
    }
}
