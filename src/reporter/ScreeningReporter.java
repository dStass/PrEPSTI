/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter;

/**
 *
 * @author MichaelWalker
 */

import java.io.* ;
import java.util.ArrayList ;


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
     * @return (ArrayList<String>) indicating the total prevalence, prevalence of 
     * symptomatic infection, and proportion of symptomatic infection in each cycle.
     */
    public ArrayList<String> preparePrevalenceReport()
    {
        ArrayList<String> prevalence = new ArrayList<String>() ;
        String entry ;
        for (String record : input)
        {
            int[] incidence = countValueIncidence(INFECTED, TRUE, record, 0) ;
            entry = addReportProperty("prevalence",((double) incidence[0])/incidence[1]) ;
            int[] symptoms = countValueIncidence(SYMPTOMATIC, TRUE, record, 0) ;
            entry += addReportProperty("symptomatic",((double) symptoms[0])/symptoms[1]) ;
            entry += addReportProperty("proportion",((double) symptoms[0])/incidence[0]) ;
            
            prevalence.add(entry) ;
        }
        return prevalence ;
    }
    
}
