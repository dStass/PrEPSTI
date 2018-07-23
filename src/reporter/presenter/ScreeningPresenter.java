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
    
    public String PREVALENCE = "prevalence" ;
    public String COPREVALENCE = "coprevalence" ;
    public String SYMPTOMATIC = "symptomatic" ;
    
    
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
        ArrayList<Object> prevalenceReport = reporter.preparePrevalenceReport() ;
        //LOGGER.log(Level.INFO, "{0}", prevalenceReport);
        plotCycleValue("prevalence", prevalenceReport) ;
    }
    
    /**
     * Plots the population prevalence of symptomatic STI over time (cycles).
     */
    public void plotSymptomPrevalence()
    {
        ArrayList<Object> symptomaticReport = reporter.preparePrevalenceReport() ;
        
        plotCycleValue("symptomatic", symptomaticReport) ;
    }
    
    /**
     * Plots the proportion of Agents with an STI that are symptomatic over time (cycles).
     */
    public void plotProportionSymptomatic()
    {
        ArrayList<Object> symptomaticReport = reporter.preparePrevalenceReport() ;
        plotCycleValue("proportion", symptomaticReport) ;
    }
    
    /**
     * Plots the population prevalence of infected siteName over time (cycles).
     * @param siteName - Name of Site to plot for.
     */
    public void plotSitePrevalence(String siteName)
    {
        ArrayList<Object> prevalenceReport = reporter.prepareSitePrevalenceReport(siteName) ;
        
        plotCycleValue("prevalence", prevalenceReport) ;
    }
    
    /**
     * Co-plots the population prevalence of infected siteNames over time (cycles).
     * @param siteNames - Array of Sites to plot for.
     */
    public void plotSitePrevalence(String[] siteNames)
    {
        ArrayList<ArrayList<Object>> prevalenceReports = new ArrayList<ArrayList<Object>>() ;
        
        for (String siteName : siteNames)
            prevalenceReports.add(reporter.prepareSitePrevalenceReport(siteName)) ;
        
        multiPlotCycleValue("prevalence", prevalenceReports, siteNames) ;
    }
    
    /**
     * Plots the population prevalence of symptomatic siteName over time (cycles).
     * @param siteName - Name of Site to plot for.
     */
    public void plotSiteSymptomPrevalence(String siteName)
    {
        ArrayList<Object> symptomaticReport = reporter.prepareSitePrevalenceReport(siteName) ;
        
        plotCycleValue("symptomatic", symptomaticReport) ;
    }
    
    /**
     * Co-plots the population prevalence of symptomatic siteNames over time (cycles).
     * @param siteNames - Array of Sites to plot for.
     */
    public void plotSiteSymptomPrevalence(String[] siteNames)
    {
        ArrayList<ArrayList<Object>> symptomaticReports = new ArrayList<ArrayList<Object>>() ;
        
        for (String siteName : siteNames)
            symptomaticReports.add(reporter.prepareSitePrevalenceReport(siteName)) ;
        
        multiPlotCycleValue("symptomatic", symptomaticReports, siteNames) ;
    }
    
    /**
     * Plots the proportion of Agents with an infected siteName that are symptomatic over time (cycles).
     * @param siteName - Name of Site to plot for.
     */
    public void plotSiteProportionSymptomatic(String siteName)
    {
        ArrayList<Object> symptomaticReport = reporter.prepareSitePrevalenceReport(siteName) ;
        plotCycleValue("proportion", symptomaticReport) ;
    }
    
    /**
     * Plots the population prevalence of coinfected siteNames over time (cycles).
     * @param (String[]) siteNames - Name of coinfected Sites to plot for.
     */
    public void plotSiteCoPrevalence(String[] siteNames)
    {
        ArrayList<Object> coprevalenceReport = reporter.prepareSiteCoPrevalenceReport(siteNames) ;
        
        plotCycleValue("coprevalence", coprevalenceReport) ;
    }
    
    /**
     * Plots the rate if STI incidents per head of population in a given cycle.
     */
    public void plotIncidencePerCycle()
    {
        ArrayList<Object> incidenceReport = reporter.prepareIncidenceReport() ;
        
        plotCycleValue("incidence", incidenceReport) ;
    }
    
    /**
     * Generates above plots and puts them on the same set of axes with a legend.
     * @param options ArrayList of plot names accompanied by String[] of parameters
     */
    public void multiPlotScreening(Object[] options)
    {
        ArrayList<ArrayList<Object>> multiPlotReports = new ArrayList<ArrayList<Object>>() ;
        ArrayList<String> multiPlotNames = new ArrayList<String>() ;
        ArrayList<String> legend = new ArrayList<String>() ;
        String legendEntry ;
        Object option ;
        Object nextOption ;
        String[] siteNames ;
        
        for (int argIndex = 0 ; argIndex < options.length ; argIndex++ )
        {
            option = options[argIndex] ;
            if (COPREVALENCE.equals(option))
            {
                nextOption = options[argIndex + 1] ;
                while (nextOption.getClass().getSimpleName().equals("String[]"))
                {
                    siteNames = (String[]) nextOption ;
                    multiPlotReports.add(reporter.prepareSiteCoPrevalenceReport(siteNames)) ;
                    multiPlotNames.add(COPREVALENCE) ;
                    argIndex++ ;
                    
                    legendEntry = "" ;
                    for (String siteName : siteNames)
                        legendEntry += siteName + "_"  ;
                    legend.add(legendEntry + "coprevalence") ; //.substring(0, legendEntry.length() - 1)) ;  // Leave out last "_"
                    if (argIndex == options.length-1)
                        break ;
                    nextOption = options[argIndex + 1] ;
                }
            }

            if (PREVALENCE.equals(option) || SYMPTOMATIC.equals(option))
            {
                // Total prevalence since not followed by String[] of siteNames
                if (argIndex == options.length-1 || options[argIndex + 1].getClass().getSimpleName().equals("String"))
                {
                    multiPlotReports.add(reporter.preparePrevalenceReport()) ;
                    multiPlotNames.add((String) option) ;
                    legend.add("total" + "_" + ((String) option)) ;  
                }
                else // if siteNames specified by following argument(s)
                {
                    nextOption = (String[]) options[argIndex + 1] ;
                    while (nextOption.getClass().getSimpleName().equals("String[]"))
                    {
                        siteNames = (String[]) nextOption ;
                        for (String siteName : siteNames)
                        {
                            multiPlotReports.add(reporter.prepareSitePrevalenceReport(siteName)) ;
                            multiPlotNames.add((String) option) ;
                            legend.add(((String) option) + "_" + siteName) ;  
                        }
                        argIndex++ ;
                        if (argIndex == options.length - 1)
                            break ;
                        nextOption = options[argIndex + 1] ;
                    }
                }
            }
        }
        String[] legendArray =  new String[legend.size()] ;
        for (int i = 0 ; i < legendArray.length ; i++ )
            legendArray[i] = legend.get(i) ;
        multiPlotCycleValue(multiPlotNames,multiPlotReports,legendArray) ;
        
    }
    
    
//            (boolean plotPrevalence, boolean plotSymptomatic, boolean plotCoPrevalence, boolean plotCoSymptomatic, 
//            boolean plotRectum, boolean plotUrethra, boolean plotPharynx)
//            boolean plotPharynxSymptomPrevalence, boolean plotIncidence, boolean plotRectumIncidence, boolean plotUrethraIncidents, 
//            boolean plotPharynxIncidents)

    
}
