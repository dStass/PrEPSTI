/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter;

import agent.MSM;
import community.Community ;
/**
 *
 * @author MichaelWalker
 */

import java.io.* ;
import java.util.ArrayList ;
import java.util.logging.Level;


public class ScreeningReporter extends Reporter {

    static String INFECTED = "infected" ;
    static String SYMPTOMATIC = "symptomatic" ;
    
    
    public ScreeningReporter(String simname, ArrayList<String> reports) {
        super(simname, reports);
        // TODO Auto-generated constructor stub
    }

    public ScreeningReporter(String simname, String reportFilePath)
    {
        super(simname,reportFilePath) ;
    }
 
    /**
     * 
     * @return (ArrayList\<String\>) indicating the total prevalence, prevalence of 
     * symptomatic infection, and proportion of symptomatic infection in each cycle.
     */
    public ArrayList<Object> preparePrevalenceReport()
    {
        return preparePrevalenceReport(input) ;
    }
    
    /**
     * 
     * @param report
     * @return (ArrayList\<String\>) indicating the total prevalence, prevalence of 
     * symptomatic infection, and proportion of symptomatic infection in each report cycle.
     */
    public ArrayList<Object> preparePrevalenceReport(ArrayList<String> report)
    {
        ArrayList<Object> prevalenceReport = new ArrayList<Object>() ;
        int population ;
        int nbInfected ;
        int nbSymptomatic ;
        String entry ;
        String[] siteNames = MSM.SITE_NAMES ;
        for (int siteIndex = 0 ; siteIndex < siteNames.length ; siteIndex++ )
        {
            String siteName = siteNames[siteIndex] ;
            siteNames[siteIndex] = siteName.substring(0,1).toUpperCase() 
                    + siteName.substring(1) ;
        }
        for (String record : input)
        {
            nbSymptomatic = 0 ;
            ArrayList<String> infections = extractArrayList(record,AGENTID) ;
            nbInfected = infections.size() ;
            for (String infection : infections)
                for (String siteName : MSM.SITE_NAMES)
                    if (compareValue(siteName,TRUE,infection))
                    {
                        nbSymptomatic++ ;
                        break ;
                    }
            
            //LOGGER.info(record) ;
            
            population = Community.POPULATION ;
            
            entry = addReportProperty("prevalence",((double) nbInfected)/population) ;
            entry += addReportProperty("symptomatic",((double) nbSymptomatic)/population) ;
            entry += addReportProperty("proportion",((double) nbSymptomatic)/nbInfected) ;
            
            prevalenceReport.add(entry) ;
        }
        return prevalenceReport ;
    }
    
    public ArrayList<Object> prepareSitePrevalenceReport(String siteName) 
    {
        ArrayList<Object> sitePrevalenceReport = new ArrayList<Object>() ;
        
        int population ;
        int[] nbSymptomatic ;
        String entry ;
        for (String record : input)
        {
            nbSymptomatic = countValueIncidence(siteName,TRUE,record,0) ;
                        
//            if (nbSymptomatic[0] == nbSymptomatic[1])
//                LOGGER.info(record);
            
            population = Community.POPULATION ;
            
            entry = addReportProperty("prevalence",((double) nbSymptomatic[1])/population) ;
            entry += addReportProperty("symptomatic",((double) nbSymptomatic[0])/population) ;
            entry += addReportProperty("proportion",((double) nbSymptomatic[0])/nbSymptomatic[1]) ;
            sitePrevalenceReport.add(entry) ;
        }
        return sitePrevalenceReport ;
    }
    
}
