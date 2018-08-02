/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporter.presenter;

import reporter.* ;
import community.Community ;

import java.util.ArrayList ;
import java.util.HashMap;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Class to plot data from EncounterReporter
 * @author MichaelWalker
 */


public class EncounterPresenter extends Presenter {
    
    private EncounterReporter reporter ;
    
    public static void main(String[] args)
    {
        try
        {
            String simName = "introPrepCalibration48Pop40000Cycles20000" ; // args[0] ;
            String chartTitle = "condom_use" ; // args[1] ;
            String reportFileName = "../output/test/" ; // args[2] ;
            EncounterPresenter encounterPresenter = new EncounterPresenter(simName,chartTitle,reportFileName) ;
            //encounterPresenter.plotCondomUse();
            encounterPresenter.plotProtection() ;

            //String methodName = args[3] ;
            //Method method = EncounterPresenter.class.getMethod(methodName) ;

            //method.invoke(encounterPresenter, (Object[]) Arrays.copyOfRange(args,4,args.length)) ;
        }
        catch ( Exception e )
        {
            LOGGER.log(Level.SEVERE, "{0}", e.toString());
        }
    }
    
    public EncounterPresenter()
    {
        super() ;   
    }
    
    public EncounterPresenter(String applicationTitle, String chartTitle)
    {
        super(applicationTitle, chartTitle);
    }
    
    public EncounterPresenter(String simName, String chartTitle, String reportFilePath)
    {
        super(simName,chartTitle) ;
        setReporter(new EncounterReporter(simName,reportFilePath)) ;
    }
    
    public EncounterPresenter(String applicationTitle, String chartTitle, EncounterReporter reporter)
    {
        super(applicationTitle,chartTitle,reporter) ;
        setReporter(reporter) ;
    }
    
    /**
     * Overrides super.setReporter() because reporter is now PopulationReporter
     * @param reporter 
     */
    public void setReporter(EncounterReporter reporter)
    {
        this.reporter = reporter ;
    }

    /**
     * Generates HashMap whose keys are Site names (String) and values the
     * number of times an infection has been transmitted from that site
     * TODO: use Agent.getSiteNames() to automatically generate Site names
     * @param siteNames (String[]) names of body Sites to consider
     */
    public void plotTransmittingSites(String[] siteNames)
    {
        // HashMap to be plotted
        HashMap<Object,Integer> transmittingSites = new HashMap<Object,Integer>() ;
        for (String name : siteNames)
            transmittingSites.put(name, 0) ;

        // For counting infected Sites
        int count ;
        
        // To record whether given Site was responsible for transmission
        ArrayList<Object> infectedStatus ;
        
        ArrayList<String> transmissionReport = reporter.prepareTransmissionReport() ;
        
        for (String report : transmissionReport)
            for (String name : siteNames)
            {
                infectedStatus = Reporter.extractAllValues(name, report, 0) ;
                count = 0 ;
                for (Object site : infectedStatus)
                    count += Integer.valueOf((String) site) ;
                count += transmittingSites.get(name) ;
                transmittingSites.put(name, count) ;
            }
        
        plotHashMap("Site","Ongoing transmissions",transmittingSites) ;
        
    }
    
    
    public void plotFromSiteToSite(String[] siteNames)
    {
        // HashMap to be plotted
        // (String) key has format infectedsiteToReceivingsite
        HashMap<Object,Integer> fromSiteToSiteReport = reporter.prepareFromSiteToSiteReport(siteNames) ;
        plotHashMap("Site to Site","transmissions",fromSiteToSiteReport) ;
        
    }
    
    /**
     * Plots the number of transmissions in a given cycle.
     */
    public void plotNbTransmissions()
    {
        ArrayList<Object> nbTransmissionReport = reporter.prepareTransmissionCountReport() ;
        
        plotCycleValue("transmission", nbTransmissionReport) ;
    }

    /**
     * Plots the proportion of opportunities for condom use for which a condom is 
     * actually used in a given cycle.
     */
    public void plotCondomUse()
    {
        ArrayList<Object> condomUseReport = reporter.prepareCondomUseReport() ;
        
        plotCycleValue("proportion", condomUseReport) ;
    }
    
    
    public void plotProtection()
    {
        PopulationReporter populationReporter = new PopulationReporter(applicationTitle,Community.FILE_PATH) ;
        ArrayList<String> census = populationReporter.prepareBirthReport() ;
        
        plotProtection(census) ;
    }
    
    public void plotProtection(ArrayList<String> census)
    {
        ArrayList<String> practices = new ArrayList<String>() ;
        practices.add("condomOnly") ;
        practices.add("onlySeroPosition") ;
        //practices.add("onlySeroSort") ;
        practices.add("condomSeroPosition") ;
        //practices.add("condomSeroSort") ;
        practices.add("unprotected") ;
        //LOGGER.log(Level.INFO, "{0}", practices);
        plotProtection(census, practices) ;
    }
    
    /**
     * Plots the combinations of protection-related practices used as a 
     * proportion of condom-relevant contacts per cycle.
     * @param census
     * @param practices 
     */
    public void plotProtection(ArrayList<String> census, ArrayList<String> practices)
    {
        ArrayList<Object> protectionReport = reporter.prepareProtectionReport(census) ;
        
        multiPlotCycleValue(practices, protectionReport) ;
    }
            

    /**
     * Produces a scatter plot of which Agents were infected by which other Agents.
     */
    public void plotAgentToAgent()
    {
        HashMap<Object,ArrayList<Object>> transmittingAgentsReport = reporter.prepareAgentToAgentRecord() ;
        plotHashMapScatter("infectious agent", "receiving agent", transmittingAgentsReport ) ;
    }
    
    /**
     * plotAgentToAgentNetwork() draws network of Agents infecting other Agents .
     */
    public void plotAgentToAgentNetwork()
    {
        HashMap<Object,HashMap<Object,ArrayList<Object>>> transmittingAgentsReport = reporter.prepareAgentToAgentReport() ;
        HashMap<Object,HashMap<Object,ArrayList<Object>>> invertedTransmittingAgentsReport 
                = Reporter.invertHashMapHashMap(transmittingAgentsReport) ;
        
        ArrayList<HashMap<Object,ArrayList<Object>>> plottingAgentsReport = new ArrayList<HashMap<Object,ArrayList<Object>>>() ;
        for (int cycle = 0 ; cycle < invertedTransmittingAgentsReport.keySet().size() ; cycle++ )
        {
            if (invertedTransmittingAgentsReport.keySet().contains(cycle))
            {
                plottingAgentsReport.add(invertedTransmittingAgentsReport.get(cycle)) ;
                //LOGGER.log(Level.INFO, "{0} {1}", new Object[] {cycle,invertedTransmittingAgentsReport.get(cycle)});
            }
            else
                plottingAgentsReport.add(new HashMap<Object,ArrayList<Object>>()) ;
        }
        //LOGGER.log(Level.INFO, "{0}", transmittingAgentsReport);
        callPlotNetwork("cycle", "agentId", plottingAgentsReport) ;    // (HashMap<Number,HashMap<Number,ArrayList<Number>>>) 
    }
    
    /*
    Everything below here are conditional plots.
    */
    
    
}
