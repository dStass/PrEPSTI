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
 
    public double[] preparePrevalenceReport()
    {
        double[] prevalence = new double[input.size()];
        int population ; 
        
        int index ;
        String record ;
        for (int recordNb = 0 ; recordNb < input.size() ; recordNb++ )
        {
            population = 0 ;
            record = input.get(recordNb) ;
            int[] incidence = countValueIncidence(INFECTED, TRUE, record, 0) ;
            prevalence[recordNb] = ((double) incidence[0])/incidence[1] ;
        }
        return prevalence ;
    }
    
    public double[] prepareSymptomPrevalenceReport()
    {
        double[] symptomatic = new double[input.size()];
        int population ; 
        
        int index ;
        String record ;
        for (int recordNb = 0 ; recordNb < input.size() ; recordNb++ )
        {
            population = 0 ;
            record = input.get(recordNb) ;
            int[] incidence = countValueIncidence(SYMPTOMATIC, TRUE, record, 0) ;
            symptomatic[recordNb] = ((double) incidence[0])/incidence[1] ;
        }
        return symptomatic ;
    }
    
}
