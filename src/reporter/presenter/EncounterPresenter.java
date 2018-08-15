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
            String simName = "noPrepCalibration86Pop40000Cycles5000" ; // Community.NAME_ROOT ; // "introPrepCalibration48Pop40000Cycles7000" ; // args[0] ;
            String chartTitle = "site_to_site" ; // args[1] ;
            String reportFileName = "output/test/" ; // args[2] ;
            EncounterPresenter encounterPresenter = new EncounterPresenter(simName,chartTitle,reportFileName) ;
            //encounterPresenter.plotCondomUse();
            //encounterPresenter.plotProtection() ;
            //encounterPresenter.plotNbTransmissions();
            //encounterPresenter.plotNumberAgentTransmissionReport() ;
            encounterPresenter.plotFromSiteToSite(new String[] {"Rectum","Urethra","Pharynx"});
            //encounterPresenter.plotReceiveSortPrepStatusReport("true") ;

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
        HashMap<Object,Number> transmittingSites = new HashMap<Object,Number>() ;
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
                count += Integer.valueOf(String.valueOf(transmittingSites.get(name))) ;
                transmittingSites.put(name, count) ;
            }
        
        plotHashMap("Site","Ongoing transmissions",transmittingSites) ;
        
    }
    
    /**
     * Plots the number of site-specific transmissions
     * @param siteNames - The Sites to be considered in this plot.
     */
    public void plotFromSiteToSite(String[] siteNames)
    {
        // HashMap to be plotted
        // (String) key has format infectedsiteToReceivingsite
        HashMap<Object,Number> fromSiteToSiteReport = reporter.prepareFromSiteToSiteReport(siteNames) ;
        
        plotHashMap("Site to Site","transmissions",fromSiteToSiteReport) ;        
    }
    
    /**
     * Plots the number of Agents responsible for the given number of transmissions.
     */
    public void plotNumberAgentTransmissionReport()
    {
        HashMap<Object,Number> numberAgentTransmissionReport = reporter.prepareNumberAgentTransmissionReport() ;
        
        plotHashMap("Number of transmissions","No of agents",numberAgentTransmissionReport) ;
    }
    
    /**
     * Plots the number of Agents responsible for the given number of transmissions
     * after sorting them for sortingProperty.
     */
    public void plotNumberAgentTransmissionReport(String sortingProperty)
    {
        HashMap<Object,HashMap<Object,Number>> numberAgentTransmissionReport = reporter.prepareNumberAgentTransmissionReport(sortingProperty) ;
        
        HashMap<Object,Number[]> plotHashMap = prepareSortedHashMap(numberAgentTransmissionReport) ;
        String[] scoreNames = (String[]) numberAgentTransmissionReport.keySet().toArray() ;
        
        plotHashMap("Number of transmissions",scoreNames,plotHashMap) ;
    }
    
    /**
     * Plots the number of Agents responsible for at least the given number of transmissions.
     */
    public void plotCumulativeAgentTransmissionReport()
    {
        HashMap<Object,Number> cumulativeAgentTransmissionReport = reporter.prepareCumulativeAgentTransmissionReport() ;
        
        plotHashMap("Minimum number of transmissions","No of agents",cumulativeAgentTransmissionReport) ;
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
        LOGGER.info("prepareBirthReport()");
        ArrayList<String> census = populationReporter.prepareBirthReport() ;
        LOGGER.log(Level.INFO, "{0}", census);
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
    
    /**
     * Plots the number of infections received by Agents with PrEP status,
     * given by (boolean) value, per cycle.
     * @param value 
     */
    public void plotReceiveSortPrepStatusReport(String value)
    {
        LOGGER.info("prepareReceiveSortPrepStatusReport");
        HashMap<Object,HashMap<Object,ArrayList<Object>>> receiveSortPrepStatusReport 
                = reporter.prepareReceiveSortPrepStatusReport(value) ;
        LOGGER.log(Level.INFO, "{0}", receiveSortPrepStatusReport);
        HashMap<Object,HashMap<Object,ArrayList<Object>>> invertedPrepStatusReport 
                = SortReporter.invertHashMapHashMap(receiveSortPrepStatusReport) ;
        LOGGER.info("prepareTransmissionCountReport");
        ArrayList<ArrayList<Object>> nbTransmissionReport 
                = reporter.prepareReceiveCountReport(invertedPrepStatusReport) ;
        LOGGER.log(Level.INFO, "{0}", nbTransmissionReport);
        LOGGER.info("plotCycleValue");
        plotEventsPerCycle("nbTransmissions", nbTransmissionReport) ;
    }
    
    
    
}
