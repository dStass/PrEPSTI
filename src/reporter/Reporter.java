/**
 * 
 */
package reporter ;

import community.* ;

import java.io.* ;

import java.lang.reflect.*;
import java.util.ArrayList ;
import java.util.Arrays;
import java.util.HashMap ;

import java.util.logging.Level;

import org.jfree.chart.* ;

/**
 * @author Michael Walker
 */
public class Reporter {

    // ArrayList<String> report properties
    //ArrayList<String> generateReports ;
    //ArrayList<String> encounterReports ;
    //ArrayList<String> clearReports ;
    //ArrayList<String> screenReports ;
    
    /** Input report. */ 
    protected ArrayList<String> input ;

    /** Output report. */
    protected ArrayList<String> output ;

    /** The number of Community cycles to pass between reports. */ 
    protected int outputCycle = 1 ;

    /** String representation of 'None'. */
    static String NONE = "None" ;
    /** static String representation of 'true'. */
    static String TRUE = "true" ;
    /** static String representation of 'false'. */
    static String FALSE = "false" ;
    /** static String representation of 'agentId'. */
    static public String AGENTID = "agentId" ;
    /** static String representation of 'agentId0'. */
    static public String AGENTID0 = "agentId0" ;
    /** static String representation of 'agentId1'. */
    static public String AGENTID1 = "agentId1" ;
    
    /** Logger of Reporter Class. */
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("reporter") ;

    public static final String addReportLabel(String label)
    {
        return label + ":" ;
    }
    
    public static final String addReportProperty(String label, String value)
    {
        String report = addReportLabel(label) ;
        return report + value + " " ;
    }
        
    public static final String addReportProperty(String label, Object value)
    {
        String report = addReportLabel(label) ;
        return report + String.valueOf(value) + " " ;
    }
     
    /**
     * Avoid having to add ":" whenever the index of a property name is needed.
     * Used when startIndex is zero or not given
     * @param property
     * @param report
     * @return indexOf(property + ":")
     */
    public static final int indexOfProperty(String property, String record)
    {
        return indexOfProperty(property,0,record) ;
    }
    
    /**
     * Avoid having to add ":" whenever the index of a property name is needed
     * @param property
     * @param startIndex
     * @param report
     * @return indexOf(property + ":")
     */
    public static final int indexOfProperty(String property, int startIndex, String report)
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
    protected static String presentElement(Object mapList, Object elementNb)
    {
        if (mapList instanceof HashMap)
        {
            return getElement((HashMap<?,?>) mapList, elementNb).toString() ;
        }
    return getElement((ArrayList<?>) mapList, (Integer) elementNb).toString() ;
    }

    /**
     * 
     * @param hashmap
     * @param elementNb
     * @return (Object) hashmap.get(elementNb) or (Object) "None" if elementKey not a key 
     */
    private static Object getElement(HashMap<?,?> hashmap, Object elementKey)
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
    private static Object getElement(ArrayList<?> arrayList, int elementNb)
    {
            if (arrayList.size() > elementNb)
            {
                    return (Object) arrayList.get(elementNb) ;
            }
            String message = NONE ;
            return (Object) message ;
    }
    
    /**
     * Sorts String entries of unsortedReport according to value of propertyName.
     * @param propertyName
     * @param selectString
     * @param unsortedReport
     * @param sortingReport
     * @param values
     * @return 
     * FIXME: There are redundant parameters and the final form is not yet decided.
     */
    protected static HashMap<Object,ArrayList<String>> sortReport(String propertyName, String selectString, 
            ArrayList<String> unsortedReport, HashMap<Object, ArrayList<Object>> sortingReport, Object[] values)
    {
        HashMap<Object,ArrayList<String>> outputHashMap = new HashMap<Object,ArrayList<String>>() ;
        for (Object value : values )
            outputHashMap.put(value, new ArrayList<String>()) ;
        
        for (String record : unsortedReport)
        {
            ArrayList<String> entries = new ArrayList<String>() ;
            String value = extractValue(propertyName, record) ;
            outputHashMap.get(value).add(record) ;
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
        sortRecord(HashMap<Object,ArrayList<Object>> unsortedReport, 
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
        sortReport(HashMap<Object,HashMap<Object,ArrayList<Object>>> unsortedReport, 
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
    protected static String boundedStringByValue(String propertyName, String value, String bound, String string)
    {
        int indexStart = indexOfProperty(bound,string) ;
        String boundedOutput = "" ;
        String boundedString ;
        while (indexStart >= 0)
        {
            boundedString = extractBoundedString(bound, string, indexStart) ;
            
            // This if statement moved to compareValue()
            //if (indexOfProperty(propertyName,boundedString) >= 0)
            //{
                if (compareValue(propertyName,value,boundedString)) 
                    boundedOutput += boundedString ;
            //}
            indexStart = indexOfProperty(bound,indexStart+1,string) ;
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
    protected static HashMap<Object,ArrayList<Object>> sortBoundedStringArray(String propertyName, String[] values, String bound, ArrayList<String> report)
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
            String checkRecord = boundedStringByContents(propertyName,bound,record) ;
            if (checkRecord.isEmpty())
            {
                LOGGER.info(propertyName + " " + bound + " checkRecord is empty " + record);
                continue ;
            }
            indexStart = indexOfProperty(bound,checkRecord);
            while (indexStart >= 0)
            {
                boundedString = extractBoundedString(bound, checkRecord, indexStart) ;
                key = (Object) extractValue(propertyName,boundedString) ;
                boundValue = extractValue(bound,boundedString) ;
                sortedHashMap = updateHashMap(key,boundValue,sortedHashMap) ;
                indexStart = indexOfProperty(bound,indexStart+1,checkRecord);
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
    protected static String boundedStringByContents(String propertyName, String bound, String string)
    {
        int indexStart = indexOfProperty(bound,string) ;
        String boundedOutput = "" ;
        String boundedString ;
        while (indexStart >= 0)
        {
            boundedString = extractBoundedString(bound, string, indexStart) ;
            if (boundedString.contains(propertyName))   //(compareValue(propertyName,value,boundedString)) 
                boundedOutput += boundedString ;
                
            indexStart = indexOfProperty(bound,indexStart+1,string) ;
            //LOGGER.log(Level.INFO, "index:{0} propertyName:{3} boundedString:{2}", new Object[] {indexStart,bound,boundedString,propertyName});
        
        }
        return boundedOutput ;
    }

    /**
     * 
     * @param string
     * @param bound
     * @return (ArrayList<String>) of bounded substrings of string.
     */
    protected static ArrayList<String> extractArrayList(String string, String bound)
    {
        int indexStart = indexOfProperty(bound, string) ;
        ArrayList<String> outputArray = new ArrayList<String>() ;
        String extractedString ;
        while (indexStart >= 0) 
        {
            extractedString = extractBoundedString(bound, string, indexStart);
            if (!extractedString.isEmpty()) 
                outputArray.add(extractedString) ;
            indexStart = indexOfProperty(bound, indexStart+1, string) ;
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
    protected static String extractBoundedString(String bound, String string, int indexStart)
    {
        int index0 = indexOfProperty(bound, indexStart, string) ;
        if (index0 == -1)
            return "" ;
        int index1 = indexOfProperty(bound,index0+1,string) ;
        if (index1 == -1) index1 = string.length() ;
        return string.substring(index0, index1) ;

    }

    /**
     * When position within string is not known, call extractValue(startIndex = 0)
     * @param propertyName - name of variable whose value is wanted
     * @param string
     * @return (String) value of propertyName
     */
    public static String extractValue(String propertyName, String string)
    {
        return extractValue(propertyName, string, 0) ;
    }

    /**
     * 
     * @param propertyName
     * @param string
     * @param startIndex
     * @return ArrayList of (String) values of propertyName from String string
     */
    public static ArrayList<Object> extractAllValues(String propertyName, String string, int startIndex)
    {
        ArrayList<Object> values = new ArrayList<Object>() ;
        int index = indexOfProperty(propertyName,startIndex,string) ; 
        
        while ( index >= 0 )
        {
            values.add(extractValue(propertyName, string, index)) ;
            index = indexOfProperty(propertyName, index+1, string) ;
        }
        return values ;
    }
    
    /**
     * The space character indicates the end of the value.  
     * @param propertyName - property whose value is wanted
     * @param string - information source/report
     * @param startIndex - string index of value, assumed exact if > 0, otherwise search
     * @return (String) value of valueName as stored in string
     */
    public static String extractValue(String propertyName, String string, int startIndex)
    {
        // Find value of valueName in string
        startIndex = indexOfProperty(propertyName, startIndex, string) ;
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
    protected static String extractValueIfNext(String propertyName, String string, int startIndex)
    {
        startIndex = isPropertyNameNext(propertyName, string, startIndex) ;
        if (startIndex > 0)
            return extractValue(propertyName, string, startIndex) ;
        return NONE ;
    }
    
    /**
     * 
     * @param propertyName
     * @param string
     * @param startIndex
     * @return index of propertyName in string if propertyName is next property in string, -1 otherwise
     */
    protected static int isPropertyNameNext(String propertyName, String string, int startIndex)
    {
        int colonIndex = string.indexOf(":",startIndex) ;
        int propertyIndex = indexOfProperty(propertyName, startIndex, string) ;
        
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
    protected static boolean compareValue(String propertyName, String value, String string, int startIndex)
    {
        if (indexOfProperty(propertyName,string) >= 0)
            return extractValue(propertyName, string, startIndex).equals(value) ;
        return false ;
    }
    
    /**
     * Finds the number of times propertyName occurs in string and the number of 
     * times it has the value value.toString().
     * @param propertyName
     * @param value
     * @param string
     * @param startIndex
     * @return (int[2]) The number of value incidents, number of propertyName incidents.
     */
    protected static int[] countValueIncidence(String propertyName, String value, String string, int startIndex)
    {
        int count = 0 ;
        int total = 0 ;
        int index = indexOfProperty(propertyName, startIndex, string) ;
        while ( index >= 0 )
        {
            total++ ;
            if (compareValue(propertyName, value, string, index))
                count++ ;
            index = indexOfProperty(propertyName, index+1, string) ;
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
    protected static boolean compareValue(String propertyName, String value, String string)
    {
        return compareValue(propertyName, value, string, indexOfProperty(propertyName,string)) ;
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
    /*protected static HashMap<Integer,ArrayList<Integer>> updateHashMap(String keyString, String entryString, HashMap<Integer,ArrayList<Integer>> valueMap)
    {
        int key = Integer.valueOf(keyString) ;
        int boundValue = Integer.valueOf(entryString) ;
        return updateHashMap(key, boundValue, valueMap) ;
    }*/
    
    /**
     * Puts entries into HashMap whose keys are the agentIds
     * and values are arrays of their partners Ids. 
     * Creates key and associated int[] if necessary.
     * @param key - usually agentId but need not be.
     * @param entry - int to go into int[] at key. 
     * @param valueMap - Adding boundValue and sometimes key to this HashMap
     * @return partnerMap - HashMap indicating partnerIds of each agent (key: agentId)
     */
    public static HashMap<Object,ArrayList<Object>> updateHashMap(Object key, Object entry, HashMap<Object,ArrayList<Object>> valueMap)
    {
        return updateHashMap(key, entry, valueMap, true) ;
    }
		
    /**
     * Puts entries into HashMap whose keys are the agentIds
     * and values are arrays of their partners Ids. 
     * Creates key and associated int[] if necessary.
     * @param key - usually agentId but need not be.
     * @param entry - int to go into int[] at key. 
     * @param valueMap - Adding boundValue and sometimes key to this HashMap
     * @return partnerMap - HashMap indicating partnerIds of each agent (key: agentId)
     */
    protected static HashMap<Object,ArrayList<Object>> updateHashMap(Object key, Object entry, HashMap<Object,ArrayList<Object>> valueMap, boolean allowDuplicates)
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
     * Increments entries into HashMap whose keys are the agentIds
     * Creates key and associated int[] if necessary.
     * @param key - usually agentId but need not be.
     * @param valueMap - Adding boundValue and sometimes key to this HashMap
     * @return partnerMap - HashMap indicating partnerIds of each agent (key: agentId)
     */
    protected static HashMap<Object,Integer> incrementHashMap(Object key, HashMap<Object,Integer> valueMap)
    {
        //HashMap<Integer,ArrayList<Integer>> partnerMap = new HashMap<Integer,ArrayList<Integer>>() ;
        
        if (valueMap.containsKey(key))
        {
            valueMap.put(key, valueMap.get(key) + 1) ;
        }
        else
        {
            valueMap.put(key, 1) ;
        }
        
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
    /*protected static HashMap<Integer,HashMap<Integer,ArrayList<Integer>>> updateHashMap(String keyString, String entryString, 
            int cycle, HashMap<Integer,HashMap<Integer,ArrayList<Integer>>> valueMap)
    {
        int key = Integer.valueOf(keyString) ;
        int boundValue = Integer.valueOf(entryString) ;
        return updateHashMap(key, boundValue, cycle, valueMap) ;
    }*/
    
    /**
     * Puts entries in HashMap<Integer,HashMap<Integer,ArrayList<Integer>>>, 
     * creating keys in either HashMap when necessary and simply updating otherwise.
     * @param keyString
     * @param entryString
     * @param cycle
     * @param valueMap
     * @return 
     */
    protected static HashMap<Object,HashMap<Object,ArrayList<Object>>> updateHashMap(Object key, 
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
        valueMap.put(key, updateHashMap((Object) key2,(Object) cycle,entryHashMap)) ;

        return valueMap ;
    }
    
    /**
     * Converts HashMap<Object,ArrayList<Object>> to HashMap<String,ArrayList<String>>
     * @param objectHashMap
     * @return 
     */
    static protected HashMap<String,ArrayList<String>> hashMapString(HashMap<Object,ArrayList<Object>> objectHashMap)
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
     * Converts HashMap<Object,ArrayList<Object>> to HashMap<Number,ArrayList<Number>>
     * @param objectHashMap
     * @return 
     */
    static protected HashMap<Number,ArrayList<Number>> hashMapNumber(HashMap<Object,ArrayList<Object>> objectHashMap)
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
     * Converts HashMap<Object,HashMap<Object,ArrayList<Object>>> to HashMap<Number,HashMap<Number,ArrayList<Number>>>
     * @param objectHashMap
     * @return 
     */
    static protected HashMap<Number,HashMap<Number,ArrayList<Number>>> hashMapHashMapNumber(HashMap<Object,HashMap<Object,ArrayList<Object>>> objectHashMapHashMap )
    {
        HashMap<Number,HashMap<Number,ArrayList<Number>>> numberHashMapHashMap = new HashMap<Number,HashMap<Number,ArrayList<Number>>>() ;
        
        HashMap<Number,ArrayList<Number>> numberHashMap = new HashMap<Number,ArrayList<Number>>() ;
        
        for (Object key : objectHashMapHashMap.keySet())
        {
            numberHashMap = hashMapNumber(objectHashMapHashMap.get(key)) ;
            numberHashMapHashMap.put((Number) key, numberHashMap) ;
        }
        return numberHashMapHashMap ;
    }
    
    /**
     * Restructures paramHashMap so that most-nested values become keys.
     * Values are HashTable of ArrayList of nested keys.
     * key1 -> key2 -> arrayValue becomes arrayValue -> key1 -> key2 .
     * @param paramHashMap
     * @return HashTable
     */
    static public HashMap<Object,HashMap<Object,ArrayList<Object>>> 
        invertHashMapHashMap(HashMap<Object,HashMap<Object,ArrayList<Object>>> paramHashMap)
    {
        //LOGGER.info("invertHashMapHashMap()");
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
                    invertedHashMap.put(cycle, updateHashMap(key1,key2,cycleHashMap)) ;
                }
            }
        }
        return invertedHashMap ;
    }
    
    /**
     * Sorts hashMap entries according to sortBoundedStringArray, only including values in
 (Object[]) values.
     * @param hashMap
     * @param sortedHashMap
     * @param values
     * @return 
     */
    static protected HashMap<Object,HashMap<Object,ArrayList<Object>>> sortHashMap(HashMap<Object,ArrayList<Object>> hashMap, 
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
    static protected HashMap<Object,HashMap<Object,ArrayList<Object>>> sortHashMap(HashMap<Object,ArrayList<Object>> hashMap, 
            HashMap<Object,ArrayList<Object>> sortedHashMap)
    {
        Object[] values = sortedHashMap.keySet().toArray() ;
        return sortHashMap(hashMap, sortedHashMap, values ) ;
    }
    
    /**
     * For sorting HashMaps when only one value is required
     * @param hashMap
     * @param sortedHashMap
     * @param value
     * @return 
     */
    static protected HashMap<Object,ArrayList<Object>> sortHashMap(HashMap<Object,ArrayList<Object>> hashMap, 
            HashMap<Object,ArrayList<Object>> sortedHashMap, Object value )
    {
        return sortHashMap(hashMap, sortedHashMap, new Object[] {value} ).get(value) ;
    }
    
    /**
     * Invoked to sort hashMap.keySet() according to values.
     * @param hashMap
     * @param sortingHashMap
     * @param values
     * @return 
     */
    static protected HashMap<Object,HashMap<Object,Object>> sortHashMapKeys(HashMap<Object,Object> hashMap, 
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
     * Extracts keys, usually agentIds, of HashMap in each record.
     * @param cycles
     * @param report
     * @return HashMap showing the cycles containing each key 
     */
    static protected HashMap<Object,ArrayList<Object>> findAgentIdKeys(Integer[] cycles, ArrayList<HashMap<Object,?>> report)
    {
        HashMap<Object,ArrayList<Object>> agentIdKeys = new HashMap<Object,ArrayList<Object>>() ;

        for (int index : cycles)
            for ( Object agentId : report.get(index).keySet() )
                agentIdKeys = updateHashMap(index,agentId,agentIdKeys) ;
        return agentIdKeys ;
    }

    /**
     * Extracts values from ArrayList value, usually agentIds, of HashMap in each record.
     * @param cycles
     * @param report
     * @return HashMap showing the cycles containing each value in ArrayList value of HashMap 
     */
    static protected HashMap<Object,ArrayList<Object>> findAgentIdValues(Integer[] cycles, ArrayList<HashMap<Object,ArrayList<?>>> report)
    {
        HashMap<Object,ArrayList<Object>> agentIdValues = new HashMap<Object,ArrayList<Object>>() ;

        for (int index : cycles)
        {
            HashMap<Object,ArrayList<?>> cycleHashMap = report.get(index) ;
            for ( Object key : cycleHashMap.keySet() )
                for ( Object agentId : cycleHashMap.get(key))
                    agentIdValues = updateHashMap(index,agentId,agentIdValues) ;
        }
        return agentIdValues ;
    }




    
    public Reporter()
    {
        
    }
    
    public Reporter(String simname, ArrayList<String> report)
    {
        input = report ;
        //this.generateReports = generateReports ;
        //this.encounterReports = encounterReports ;
        //this.clearReports = clearReports ;
        //this.screenReports = screenReports ;
    }
    
    public Reporter(String simname, String fileName)
    {
        fileName = Community.FILE_PATH + "Report" + Community.NAME_ROOT + ".txt" ;
        Reader reader = new Reader(simname,fileName) ;
        input = reader.getFiledReport() ;
    }


    /**
     * 
     * @param reportNb
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
     * Object to read saved File output and feed it to Reporter
     */
    protected class Reader
    {
    //	ArrayList<String> encounterReports, ArrayList<String> clearReports, ArrayList<String> screenReports)
        private ArrayList<String> outputArray = new ArrayList<String>() ;
        protected Reader(String simName, String filePath)
        {
            String record = "record" ;
            while (!record.isEmpty())
            {
//            File folder = new File(".");
//File[] listOfFiles = folder.listFiles();
//
//    for (File file : folder.listFiles()) {
//      if (file.isFile()) {
//        System.out.println("File " + file.getName());
//      } else if (file.isDirectory()) {
//        System.out.println("Directory " + file.getName());
//      }
//    }
            //LOGGER.info(filePath);
                try
                {
                    BufferedReader fileReader = new BufferedReader(new FileReader(filePath)) ;
                    //new FileInputStream(".").;
                    //BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream("LICENSE"))) ;
                    //BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\MichaelWalker\\OneDrive - UNSW\\gonorrhoeaPrEP\\simulator\\PrEPSTI\\LICENSE"))) ;
                    //BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream("dist/README"))) ;
                    //BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\MichaelWalker\\OneDrive - UNSW\\gonorrhoeaPrEP\\simulator\\output\\testFile.txt"))) ;
                    record = fileReader.readLine() ;
                    outputArray.add(record) ;
                }
                catch ( Exception e )
                {
                    LOGGER.log(Level.SEVERE, e.toString());
                    record = "" ;
                }
            }
            if (outputArray.isEmpty())
                LOGGER.log(Level.SEVERE, "Empty Report from File at {0}", new Object[]{filePath});
            
        }
        
        protected ArrayList<String> getFiledReport()
        {
            return outputArray ;
        }
    }
    /*
                outputCycle = 5 ;
                globalFolder = "folder/address/here/" ;

                logFilePath = globalFolder + simName ;
                logFile = new File(logFilePath) ;

                errorFilePath = globalFolder + simName ;
                errorFile = new File(errorFilePath) ;

                outputFilePath = globalFolder + simName ;
                outputFile = new File(outputFilePath) ;
                return ;
        }

        protected void outputAgents(Agent[] agents, int cycle)
        {
                outputFile. 
        }
        */

}
