/**
 * 
 */
package reporter ;


import java.io.* ;
import java.lang.reflect.*;
import java.util.ArrayList ;
import java.util.HashMap ;

import com.sun.media.jfxmedia.logging.Logger;
//import java.util.logging.* ;
/**
 * @author Michael Walker
 */
public class Reporter {

	// ArrayList<String> report properties
	//ArrayList<String> generateReports ;
	//ArrayList<String> encounterReports ;
	//ArrayList<String> clearReports ;
	//ArrayList<String> screenReports ;
	ArrayList<String> input ;
	
	// Output report
	ArrayList<?> output ;

	// The number of Community cycles to pass between reports 
	protected int outputCycle = 1 ;
	
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
	java.util.logging.Logger logger = java.util.logging.Logger.getLogger("reporter") ;
	
	
	/**
	 * Object to gather data and record it to Files
     */
	//public Reporter(String simName, ArrayList<String> generateReports, 
		//	ArrayList<String> encounterReports, ArrayList<String> clearReports, ArrayList<String> screenReports)
	public Reporter(String simname, ArrayList<String> reports)
	{
		input = reports ;
		//this.generateReports = generateReports ;
		//this.encounterReports = encounterReports ;
		//this.clearReports = clearReports ;
		//this.screenReports = screenReports ;
	}
	
	/**
	protected Reporter(String simName) 
	{
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
	
	/**
	 * 
	 * @param reportNb
	 * @return output[reportNb] or error String if not available
	 */
	protected String presentReport(int reportNb)
	{
		if (reportNb < output.size())
			return output.get(reportNb).toString() ;

        String message = "Requested cycle " + Integer.toString(reportNb) + "unavailable" ;
		return message ;
	}
	
	/**
	 * 
	 * @param mapList - either HashMap or ArrayList
	 * @param elementNb
	 * @return (String) representation of mapList.get(elementNb)
	 */
	protected String presentElement(Object mapList, Object elementNb)
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
	private Object getElement(HashMap<?,?> hashmap, Object elementKey)
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
	private Object getElement(ArrayList<?> arrayList, int elementNb)
	{
		if (arrayList.size() > elementNb)
		{
			return (Object) arrayList.get(elementNb) ;
		}
		String message = "None" ;
		return (Object) message ;
	}
	
	/**
	 * Extracts bounded substrings whose propertyName == value
	 * @param propertyName 
	 * @param value
	 * @param bound - String bounding substrings of interest
	 * @param string
	 * @return String output
	 */
	protected String boundedStringByValue(String propertyName, String value, String bound, String string)
	{
		int indexStart = string.indexOf(bound) ;
		String output = "" ;
		String boundedString ;
		while (indexStart >= 0)
		{
			indexStart++ ;
			boundedString = extractBoundedString(string, bound, indexStart) ;
			if (compareValue(propertyName,value,boundedString,indexStart)) 
			{
				output += bound + boundedString ;
			}
			indexStart = string.indexOf(bound,indexStart+1) ;
		}
		return output ;
	}
	
	/**
	 * 
	 * @param string
	 * @param bound
	 * @return
	 */
	private ArrayList<String> extractArrayList(String string, String bound)
	{
		int indexStart = 0 ;
		ArrayList<String> outputArray = new ArrayList<String>() ;
		while (indexStart >= 0) 
		{
			outputArray.add(extractBoundedString(string, bound, indexStart)) ;
			indexStart++ ;
		}
		return outputArray ;
	}

    /**
	 * 
	 * @param string
	 * @param bound
	 * @return subString of string bounded by bound
	 */
	protected String extractBoundedString(String string, String bound, int indexStart)
	{
		int index0 = string.indexOf(bound, indexStart) + bound.length() ;
		int index1 = string.indexOf(bound,index0) ;
		if (index1 == -1) index1 = string.length() ;
		return string.substring(index0, index1) ;
		
	}
	
	/**
	 * When position within string is not known, call extractValue(startIndex = 0)
	 * @param valueName - name of variable whose value is wanted
	 * @param string
	 * @return (String) value of valueName
	 */
	protected String extractValue(String valueName, String string)
	{
		return extractValue(valueName, string, 0) ;
	}
	
	/**
	 * The space character indicates the end of the value.  
	 * @param valueName - variable whose value is wanted
	 * @param string - information source/report
	 * @param startIndex - string index of value, assumed exact if > 0, otherwise search
	 * @return (String) value of valueName as stored in string
	 */
	protected String extractValue(String propertyName, String string, int startIndex)
	{
		// Find value of valueName in string
		String valueString = "null" ;
		int valueEndIndex ; 
		
		// Do we already know the starting index of valueName?
		if (startIndex > 0 ) 
			{
			    startIndex+= propertyName.length() + 1 ;    // +1 is for ":" following valueName
			    valueEndIndex = string.indexOf(" ", startIndex) ;
			    valueString = string.substring(startIndex, valueEndIndex) ;
			}
		else 
			{
			    int valueStartIndex = string.indexOf(propertyName) + propertyName.length() + 1 ;
			    valueEndIndex = string.indexOf(" ",valueStartIndex) ;
			    valueString = string.substring(valueStartIndex, valueEndIndex) ;
			}
		
		return valueString ;
	}
	
	protected boolean compareValue(String propertyName, String value, String string, int startIndex)
	{
		return extractValue(propertyName, string, startIndex).equals(value) ;
	}
}
