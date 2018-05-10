/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter.presenter;


import java.util.ArrayList ;

import reporter.ScreeningReporter ;

/**
 *
 * @author Michael Walker
 */
public class ScreeningPresenter extends Presenter {
    
    private ScreeningReporter reporter ; 
    
    
    public ScreeningPresenter()
    {
        super() ;   
    }
    
    public ScreeningPresenter(String applicationTitle, String chartTitle)
    {
        super(applicationTitle, chartTitle);
    }
    
    public ScreeningPresenter(String applicationTitle, String chartTitle, ScreeningReporter reporter)
    {
        super(applicationTitle,chartTitle,reporter) ;
        setReporter(reporter) ;
    }
    
    /**
     * Overrides super.setReporter() because reporter is now PopulationReporter
     * @param reporter 
     */
    public void setReporter(ScreeningReporter reporter)
    {
        this.reporter = reporter ;
    }

    /**
     * Plots the population prevalence of STI over time (cycles).
     */
    public void plotPrevalence()
    {
        ArrayList<String> prevalenceReport = reporter.preparePrevalenceReport() ;
        
        plotCycleValue("prevalence", prevalenceReport) ;
    }
    
    /**
     * Plots the population prevalence of symptomatic STI over time (cycles).
     */
    public void plotSymptomPrevalence()
    {
        ArrayList<String> symptomaticReport = reporter.preparePrevalenceReport() ;
        
        plotCycleValue("symptomatic", symptomaticReport) ;
    }
    
    /**
     * Plots the proportion of Agents with an STI that are symptomatic over time (cycles).
     */
    public void plotProportionSymptomatic()
    {
        ArrayList<String> symptomaticReport = reporter.preparePrevalenceReport() ;
        plotCycleValue("proportion", symptomaticReport) ;
    }
    
}
