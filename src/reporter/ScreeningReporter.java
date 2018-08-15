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
    
    public ScreeningReporter()
    {
        
    }
    
    public ScreeningReporter(String simname, ArrayList<String> report) {
        super(simname, report);
        // TODO Auto-generated constructor stub
    }

    public ScreeningReporter(String simName, String reportFilePath)
    {
        super(simName,reportFilePath) ;
    }
 
    /**
     * 
     * @return (ArrayList\<String\>) indicating the total coprevalence, coprevalence of 
     * symptomatic infection, and proportion of symptomatic infection in each report cycle.
     */
    public ArrayList<Object> preparePrevalenceReport()
    {
        ArrayList<Object> prevalenceReport = new ArrayList<Object>() ;
        
        int population = Community.POPULATION ;
        int nbInfected ;
        int nbSymptomatic ;
        String entry ;
        String[] siteNames = MSM.SITE_NAMES ;
        boolean nextInput = true ; 

        while (nextInput)
        {
        
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
                entry = addReportProperty("prevalence",((double) nbInfected)/population) ;
                entry += addReportProperty("symptomatic",((double) nbSymptomatic)/population) ;
                entry += addReportProperty("proportion",((double) nbSymptomatic)/nbInfected) ;

                prevalenceReport.add(entry) ;
            }
            nextInput = updateReport() ;
        }
        return prevalenceReport ;
    }
    
    /**
     * 
     * @return (ArrayList\<String\>) indicating the total coprevalence, coprevalence of 
     * symptomatic infection, and proportion of symptomatic infection at site given 
     * by siteName in each report cycle.
     */
    public ArrayList<Object> preparePrevalenceReport(String siteName) 
    {
        ArrayList<Object> sitePrevalenceReport = new ArrayList<Object>() ;
        
        boolean nextInput = true ; 
        
        while (nextInput)
        {
            int population = Community.POPULATION ;
            int[] nbSymptomatic ;
            String entry ;
            for (String record : input)
            {
                nbSymptomatic = countValueIncidence(siteName,TRUE,record,0) ;

    //            if (nbSymptomatic[0] == nbSymptomatic[1])
    //                LOGGER.info(record);


                entry = addReportProperty("prevalence",((double) nbSymptomatic[1])/population) ;
                entry += addReportProperty("symptomatic",((double) nbSymptomatic[0])/population) ;
                entry += addReportProperty("proportion",((double) nbSymptomatic[0])/nbSymptomatic[1]) ;
                sitePrevalenceReport.add(entry) ;
            }
            nextInput = updateReport() ;
        }
        return sitePrevalenceReport ;
    }
    
    /**
     * 
     * @param siteNames
     * @return ArrayList of coprevalence of coninfection of Sites named in siteNames.
     */
    public ArrayList<Object> prepareCoPrevalenceReport(String[] siteNames) 
    {
        ArrayList<Object> siteCoPrevalenceReport = new ArrayList<Object>() ;
        
        int population = Community.POPULATION ;
        int[] nbSymptomatic ;
        Double coprevalence ;
        String entry ;
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport())
        {
            for (String record : input)
            {
                entry = record ;
                for (String siteName : siteNames)
                {
                    entry = boundedStringByContents(siteName,AGENTID,entry) ;
                }
                if (entry.isEmpty())
                    coprevalence = 0.0 ;
                else 
                    coprevalence = (Double.valueOf(countValueIncidence(AGENTID,"",entry,0)[1]))/population ;
                siteCoPrevalenceReport.add("coprevalence:" + String.valueOf(coprevalence) + " ") ;
                //LOGGER.info("coprevalence:" + String.valueOf(coprevalence) + " ");
            }
        }
        return siteCoPrevalenceReport ;
    }
    
    /** 
     * 
     * @return Report indicating the number and per population of incidents
     * of infection.
     */
    public ArrayList<Object> prepareIncidenceReport()
    {
        return prepareIncidenceReport("") ;
    }
    
    public ArrayList<Object> prepareIncidenceReport(String siteName)
    {
        ArrayList<Object> incidenceReport = new ArrayList<Object>() ;
        
        int incidents ;
        double incidence ;
        int population = Community.POPULATION ;
        String output ;
        
        // Loop through Reporter input files 
        for (boolean nextInput = true ; nextInput ; nextInput = updateReport() )
            for (String record : input)
            {
                if (!siteName.isEmpty())
                    record = boundedStringByContents(siteName,AGENTID,record) ;
                
                incidents = countValueIncidence("treated","",record,0)[1];
                incidence = ((double) incidents)/population;

                output = Reporter.addReportProperty("incidents", incidents) ;
                output += Reporter.addReportProperty("incidence", incidence) ;

                incidenceReport.add(output) ;
            }
        return incidenceReport ;
    }
    
}
