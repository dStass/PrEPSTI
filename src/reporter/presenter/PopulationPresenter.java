/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter.presenter;

import reporter.* ;

import java.util.ArrayList ;
import java.util.HashMap;

/**
 * For presenting results from the PopulationReporter
 * Methods typically relevant to birth, death, and appropriate ages
 * @author MichaelWalker
 */
public class PopulationPresenter extends Presenter{
    
    private PopulationReporter reporter ;
    
    public PopulationPresenter()
    {
        super() ;   
    }
    
    public PopulationPresenter(String applicationTitle, String chartTitle)
    {
        super(applicationTitle, chartTitle);
    }
    
    public PopulationPresenter(String applicationTitle, String chartTitle, PopulationReporter reporter)
    {
        super(applicationTitle,chartTitle,reporter) ;
        setReporter(reporter) ;
    }
    
    /**
     * Overrides super.setReporter() because reporter is now PopulationReporter
     * @param reporter 
     */
    public void setReporter(PopulationReporter reporter)
    {
        this.reporter = reporter ;
    }

    /**
     * Find and plot the number of deaths as a function of time/cycle
     */
    public void plotDeathsPerCycle()
    {
        ArrayList<ArrayList<String>> agentDeathReport = reporter.prepareAgentDeathReport() ;
        
        // ArrayList<String> deathsPerCycle = prepareDeathsPerCycle() ;
        
        plotEventsPerCycle("Deaths",agentDeathReport) ;
        
        return ;
    }

    /**
     * Find and plot the number of deaths as a function of time/cycle
     */
    public void plotBirthsPerCycle()
    {
        ArrayList<ArrayList<String>> agentBirthReport = reporter.prepareAgentBirthReport() ;
        
        // ArrayList<String> deathsPerCycle = prepareDeathsPerCycle() ;
        
        plotEventsPerCycle("Births",agentBirthReport) ;
        
        return ;
    }
    
    /**
     * Plot the number of deaths that occurred for a given age
     */
    public void plotAgeAtDeath()
    {
        HashMap<String,Integer> ageAtDeathReport = reporter.prepareAgeAtDeathReport() ;
        
        plotHashMap("Age", "Number of deaths", ageAtDeathReport ) ;
    }


}
