/**
 * 
 */
package community;

import agent.* ;
import static community.Community.RAND;
import site.* ;
import reporter.* ;
        
import java.io.* ;
//import java.io.FileWriter ;
//import java.io.IOException;

import reporter.* ; 
import reporter.ScreeningReporter ;
import reporter.presenter.* ;

import java.util.Random;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;

//import org.jfree.chart.* ;

/******************************************************************
 * @author Michael Luke Walker
 *
 *******************************************************************/
public class Community {
    static final public int POPULATION = 40000 ;
    static public int MAX_CYCLES ; // = 350 ; 
    static public String NAME_ROOT = "" ;
    //static public String NAME_ROOT = "TestUrethraSymp60a2" ;
    //static public String NAME_ROOT = "CorrectedSafe46a" ;
    //static public String NAME_ROOT = "From2007To2012a" ;
    //static public String NAME_ROOT = "IntroPrepCalibration74acycle6000" ;
    //static public String NAME_ROOT = "FallingCondomUseNew5a" ;
    //static public String NAME_ROOT = "AllRiskyI" ;
    //static public String NAME_ROOT = "AllSexualContacts" 
    //        + "DecliningCondomsAlteredTesting" ;
    static String NAME_SUFFIX = "Pop" + String.valueOf(POPULATION) + "Cycles" + String.valueOf(MAX_CYCLES) ;
    static public String SIM_NAME = NAME_ROOT + NAME_SUFFIX ;

    static String COMMENT = ""
            //+ "Removed extra choice under serosorting for RiskyMSM"
            //+ "of accepting Casual Relationships back to EPIC-inspired levels. "
            //+ "probabilityUseCondom becomes zero at cycle 2095. "
            // + "every cycle from 3000 "
            //+ "Agents reduce their chances of choosing condoms by up to 0.5"
            //+ "parameters are adjusted according to ARTB data on a yearly basis"
            //+ "Continue From2007To2012NoCondomIII to see if prevalence rises. "
            //+ "five RiskyMSM go on PrEP "
            //+ "Testing rates are altered to compare with long-term data " 
            //+ "and their condom usage rates are multiplied by random fraction between 0 and 1."
            //+ "All encounters are recorded in full." 
            //+ "Test of loading burn-in. " // Uses From2007To2011p5v3aAdjust. "
            //+ "with 500 cycle grace period."
            //+ "Test of Urethra symptomaticProbability 0.75 "
            + "" ;
    
    static boolean TO_PLOT ; //= true ;
    static public String FILE_PATH = "output/" ;
    //static public String FILE_PATH = "/srv/scratch/z3524276/prepsti/output/test/" ;
    //static public String FILE_PATH = "/short/is14/mw7704/prepsti/output/year2007/" ;
    /** Dump reports to disk after this many cycles. */
    /** Whether parameters change throughout simulation. */
    static boolean DYNAMIC = false ;
    
    static final int DUMP_CYCLE = ((int) Math.pow(10, 7))/POPULATION ;
    /** Whether to dump partial reports during simulation. */
    static final boolean PARTIAL_DUMP = (DUMP_CYCLE > 0) ;
    
    /**
     * (String) Name of previous burn-in to reload.
     * Not reloaded if this is an empty string.
     */
    static final String RELOAD_BURNIN = "" ; // Year2007New8bPop40000Cycles4000" ; // "NoPrepCalibration24Pop40000Cycles8000" ;
    
    /**
     * (String) Name of previous simulation to reload.
     * Not reloaded if this is an empty string.
     */
    static final String RELOAD_SIMULATION = "" ; // "turn40over53aPop40000Cycles2000" ; // "max3contacts54eEXTPop40000Cycles750" ; // "test3aPop4000Cycles500" ; // "agentScreen26bPop40000Cycles1500" ;
    
    static public String getFilePath()
    {
        return FILE_PATH ;
    }
    
    /** Generate and record Random number seed. */
    static long RANDOM_SEED = System.nanoTime() ;
    static Random RAND = new Random(RANDOM_SEED) ;
    

    private ArrayList<Agent> agents = new ArrayList<Agent>() ;
    
    /** Total number of agents. */
    private int population = POPULATION ;
    ///** Current number of sexually live agents */
    //private int currentPopulation = population ;

    // Number of new population members and also average number of deaths per cycle
    private double birthRate = population/(50 * 365) ;
    private int birthBase = (int) Math.floor(birthRate);
    private double birthRemainder = birthRate - birthBase ;

    // Current number of relationships in the community
    private int nbRelationships = 0; 
    
    // List of indices in agents of available agents. Repetition indicates promiscuity
    //ArrayList<Integer> availableAgentsIds = new ArrayList<Integer>() ;

    //private ArrayList<Relationship> relationships = new ArrayList<Relationship>() ;

//    /** The number of years covered in the simulation. */
    //private static int year = 0 ;
//    /** The number of cycles (days) since the current year began. */
    //private static int cyclesModYear = 0 ;
    
    public static String TRUE = "true" ;
    public static String FALSE = "false" ;
    
    private String initialRecord ;
    protected ArrayList<String> relationshipReport = new ArrayList<String>() ;
    protected ArrayList<String> encounterReport = new ArrayList<String>() ;
    protected ArrayList<String> infectionReport = new ArrayList<String>() ;
    protected ArrayList<String> populationReport = new ArrayList<String>() ;
    private Scribe scribe = new Scribe() ;
    // Define Scribe in full in Community() constructor
    //private Scribe scribe = new Scribe(SIM_NAME, new String[] {"relationship","encounter","infection", "population"}) ;


    // Logger
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("reporter") ;

    public static void main(String[] args)
    {
        //String infectedSiteName ;
        //double urethralTransmission ;
        if (args.length > 0)
        {
            LOGGER.info(args[0]);
            NAME_ROOT = args[0] ;
            SIM_NAME = NAME_ROOT + NAME_SUFFIX ;
        }
        //MAX_CYCLES = Integer.valueOf(args[1]) ;
        if (args.length > 1)
        {
            LOGGER.info(args[1]) ;
            int time = Integer.valueOf(args[1]) ;
            if (time > 99)    // time given assumed to be days
                MAX_CYCLES = time ;
            else    // time given assumed to be years
                MAX_CYCLES = time * Reporter.DAYS_PER_YEAR ;
            NAME_SUFFIX = "Pop" + String.valueOf(POPULATION) + "Cycles" + MAX_CYCLES ;
            SIM_NAME = NAME_ROOT + NAME_SUFFIX ;
        }
        if (args.length > 2)
        {
            LOGGER.info(args[2]) ;
            FILE_PATH += args[2] ;
            /*
            urethralTransmission = Double.valueOf(args[1]) ;
            MSM.SET_INFECT_PROBABILITY("URETHRA","RECTUM",urethralTransmission) ;
            MSM.SET_INFECT_PROBABILITY("RECTUM","URETHRA",urethralTransmission) ;
            */
        }
        if (args.length > 3)
        {
            LOGGER.info(args[3]);
            if (args[3].equals("raijin"))
                FILE_PATH = "/short/is14/mw7704/prepsti/" + FILE_PATH ;
            else if (args[3].equals("katana"))
                FILE_PATH = "/srv/scratch/z3524276/prepsti/" + FILE_PATH ;
        }
        // Whether to plot prevalence upon completion.
        // Must be false when run on an HPC cluster.
        TO_PLOT = (!FILE_PATH.contains("prepsti")) ;
        
        // Record starting time to measure running time
        long startTime = System.nanoTime() ;
        LOGGER.log(Level.INFO, "Seed:{0}", System.currentTimeMillis());
    
        // Establish Community of Agents for simulation
        LOGGER.info(SIM_NAME);
        
        Community community = new Community(RELOAD_SIMULATION,200) ;
        
        
        // Establish conditions for specific simulation questions
        //System.out.println(community.initialiseCommunity()) ;

        // For generating reports
        String relationshipRecord ;
        String encounterRecord ;
        //String clearanceRecord ;
        String infectionRecord ;
        String populationRecord ;
        //String deathReport ;
        //String censusRecord ;

        // To record cycle number in every record
        String cycleString ;
        
        System.out.println("population: " + POPULATION + ", Cycles: " + MAX_CYCLES );
        /*
        int outputInterval ;
        if (POPULATION < MAX_CYCLES)
            outputInterval = POPULATION/2 ;
        else
            outputInterval = POPULATION / (MAX_CYCLES) ;
        if (outputInterval < 100)
            outputInterval = 100 ;
        */
        
        if (!RELOAD_SIMULATION.isEmpty())
        {
            // Do nothing here but avoid the alternatives.
        }
        else if (RELOAD_BURNIN.isEmpty())
        {
            // Generate relationships for simulation
            String commenceString ;
            String breakupString ;
            ArrayList<String> commenceList = new ArrayList<String>() ;
            ArrayList<String> breakupList ;
            
            for (int burnin = 0 ; burnin < 5000 ; burnin++ )
            {
                commenceString = community.generateRelationships(true) ;
                commenceList.addAll(Reporter.EXTRACT_ARRAYLIST(commenceString, Reporter.RELATIONSHIPID)) ;
                
                breakupString = community.clearRelationships().substring(6) ;
                breakupList = Reporter.EXTRACT_ARRAYLIST(breakupString, Reporter.RELATIONSHIPID) ;
                for (String breakup : breakupList)
                {
                    for (String commence : commenceList)
                        if (commence.startsWith(breakup))
                        {
                            commenceList.remove(commence) ;
                            break ;
                        }
                    //Relationship.BURNIN_BREAKUP += breakup ;
                }
            }
            
            for (String commence : commenceList)
            {
                Relationship.BURNIN_COMMENCE += commence ;
            }
        }
        else
        {
            Relationship.BURNIN_COMMENCE = community.reloadBurnin() + " clear:";
            Relationship.BURNIN_BREAKUP = "" ;
            Community.COMMENT += "Burnin reloaded from " + RELOAD_BURNIN ;
        }
        
        // simulation of maxCycles cycles
        
        cycleString = "0," ;
        populationRecord = cycleString + Reporter.ADD_REPORT_LABEL("birth") + community.initialRecord ;
        
        //outputInterval = 1 ;
        for (int cycle = 0; cycle < MAX_CYCLES; cycle++)
        {	
            //if (cycle == ((cycle/outputInterval) * outputInterval))
            //LOGGER.log(Level.INFO, "Cycle no. {0}", cycleString);

            if (DYNAMIC)
                community.interveneCommunity(cycle) ;
            
            //LOGGER.log(Level.INFO,"{0} {1}", new Object[] {Relationship.NB_RELATIONSHIPS,Relationship.NB_RELATIONSHIPS_CREATED});
            // update relationships and perform sexual encounters, report them
            //LOGGER.info("generate") ;
            relationshipRecord = cycleString + community.generateRelationships(false);
            
            //LOGGER.info("encounter");
            encounterRecord = cycleString + community.runEncounters();
            
            //LOGGER.info("clear");
            relationshipRecord += community.clearRelationships();
            
            // treat symptomatic agents
            
            //LOGGER.info("progress");
            infectionRecord = cycleString + community.progressInfection(cycle) ;
            
            //deathRecord = cycleString
            int deltaPopulation = community.agents.size() ;  // Current population
            
            //LOGGER.info("death");
            populationRecord += community.grimReaper() ;
            // Record Relationships ended due to death
            
            relationshipRecord += Relationship.READ_DEATH_RECORD() ;
            
            // How many births to maintain population?
            deltaPopulation = deltaPopulation - community.agents.size() ;

            community.submitRecords(relationshipRecord,encounterRecord,infectionRecord,populationRecord) ;  // 

            // Deal with effects of aging.
            // To include in populationRecord move this above community.submitRecords()
            community.ageOneDay();

            if (PARTIAL_DUMP)
                if ((((cycle+1)/DUMP_CYCLE) * DUMP_CYCLE) == (cycle+1) )
                {
                    community.dump();
                }
                    

            cycleString = Integer.toString(cycle+1) + "," ;
            populationRecord = cycleString + community.births(deltaPopulation) ;
        }
        // Final dump() or whole dump if no partial dumps
        if (!PARTIAL_DUMP || (((Community.MAX_CYCLES)/DUMP_CYCLE) * DUMP_CYCLE) != Community.MAX_CYCLES )
            community.dump() ;
        community.dumpMetaData() ;
        community.dumpRebootData() ;
        
        long elapsedTime = System.nanoTime() - startTime ;
        long milliTime = elapsedTime/1000000 ;
        int seconds = (int) milliTime/1000 ;
        int minutes = seconds/60 ;
        System.out.println("population: " + POPULATION + ", Cycles: " + MAX_CYCLES);
        System.out.println("Elapsed running time: " + milliTime + "millseconds") ;
        System.out.println("Elapsed running time: " + seconds + "seconds") ;
        System.out.println("Elapsed running time: " + minutes + "minutes") ;
        
        //if (TO_PLOT)
        {
        String[] relationshipClassNames = new String[] {"Casual","Regular","Monogomous"} ; // "Casual","Regular","Monogomous"
        
        //RelationshipReporter relationshipReporter = new RelationshipReporter(Community.SIM_NAME,Community.FILE_PATH) ;
        //HashMap<Object,HashMap<Object,Number>> relationshipReport 
          //      = relationshipReporter.prepareCumulativeRelationshipRecord(-1, relationshipClassNames, 0, 6, 0) ;
        //HashMap<Object,Number[]> plotReport = Reporter.INVERT_HASHMAP_LIST(relationshipReport, relationshipClassNames) ;
        //Reporter.WRITE_CSV(plotReport, "Number of Relationships", relationshipClassNames, "cumulativeRelationship", SIM_NAME, "output/test/") ;
        
        ScreeningReporter screeningReporter = new ScreeningReporter(SIM_NAME,FILE_PATH) ;
                //new ScreeningReporter("prevalence",community.infectionReport) ;
                
        //ArrayList<Object> pharynxPrevalenceReport = screeningReporter.preparePrevalenceReport("Pharynx") ;
        //Reporter.WRITE_CSV(pharynxPrevalenceReport, "Pharynx", SIM_NAME, FILE_PATH);
        //ScreeningPresenter screeningPresenter 
          //      = new ScreeningPresenter("prevalence",Community.SIM_NAME,screeningReporter) ;
        //screeningPresenter.multiPlotScreening(new Object[] {"prevalence", new String[] {"Pharynx","Urethra","Rectum"},"coprevalence",new String[] {"Rectum","Pharynx"}});
        //ScreeningPresenter screeningPresenter2 
          //      = new ScreeningPresenter("prevalence",Community.SIM_NAME,screeningReporter) ;
        //screeningPresenter2.plotPrevalence();
        //screeningPresenter2.plotNotificationsPerCycle();
        if (TO_PLOT)
        {
            ScreeningPresenter screeningPresenter3 
                    = new ScreeningPresenter(SIM_NAME,"multi prevalence",screeningReporter) ;
            screeningPresenter3.multiPlotScreening(new Object[] {"prevalence","prevalence",new String[] {"Pharynx","Rectum","Urethra"}}) ;  // ,"coprevalence",new String[] {"Pharynx","Rectum"},new String[] {"Urethra","Rectum"}
        }
        
        {
            HashMap<Object,Number> finalNotificationsRecord = new HashMap<Object,Number>() ;
            for (boolean unique : new boolean[] {false,true})
            {
                HashMap<Object,Number> finalPositivityRecord = new HashMap<Object,Number>() ;
                HashMap<Object,Number[]> notificationsRecord = screeningReporter.prepareFinalNotificationsRecord(new String[] {"Pharynx","Rectum","Urethra"}, unique, 0, Reporter.DAYS_PER_YEAR) ;
                for (Object key : notificationsRecord.keySet())
                {
                    if (unique)
                        finalNotificationsRecord.put(key, notificationsRecord.get(key)[0]) ;
                    finalPositivityRecord.put(key, notificationsRecord.get(key)[1]) ;
                }
                LOGGER.log(Level.INFO, "Positivity unique:{0} {1}", new Object[] {unique,finalPositivityRecord});
            }
            LOGGER.log(Level.INFO, "Notification rate {0}", new Object[] {finalNotificationsRecord});
            screeningReporter = new ScreeningReporter(SIM_NAME,FILE_PATH) ;
            String prevalenceReports = "" ;
            ArrayList<Object> prevalenceReport ;
            for (String siteName : new String[] {"Pharynx","Rectum","Urethra"})
            {
                prevalenceReport = screeningReporter.preparePrevalenceReport(siteName) ;
                //LOGGER.info(String.valueOf(prevalenceReport.size())) ;
                //LOGGER.log(Level.INFO,"{0} {1}", new Object[] {siteName, prevalenceReport.get(prevalenceReport.size() - 1)}) ;
            }
            prevalenceReport = screeningReporter.preparePrevalenceReport() ;
            //LOGGER.log(Level.INFO,"{0} {1}", new Object[] {"all", prevalenceReport.get(prevalenceReport.size() - 1)}) ;
    
        }
        //EncounterReporter encounterReporter = new EncounterReporter("Agent to Agent",community.encounterReport) ;
        //EncounterReporter encounterReporter = new EncounterReporter(Community.SIM_NAME,Community.FILE_PATH) ;
//        EncounterPresenter encounterPresenter = new EncounterPresenter(Community.SIM_NAME,"agent to agent", encounterReporter) ;
//        encounterPresenter.plotCondomUse();
//        EncounterPresenter encounterPresenter2 = new EncounterPresenter(Community.SIM_NAME,"agent to agent", encounterReporter) ;
//        encounterPresenter2.plotProtection();
        //encounterPresenter.plotNbTransmissions(); 
        //encounterPresenter.plotTransmittingSites(new String[] {"Urethra","Rectum","Pharynx"});
        //encounterPresenter.plotFromSiteToSite(new String[] {"Urethra","Rectum","Pharynx"});
        //encounterPresenter.plotAgentToAgentNetwork();
        
        //PopulationReporter populationReporter = new PopulationReporter("deaths per cycle",community.populationReport) ;
        //PopulationPresenter populationPresenter = new PopulationPresenter("age-at-death","age-at-death",populationReporter) ;
        //populationPresenter.plotAgeAtDeath();
        //PopulationPresenter populationPresenter = new PopulationPresenter("deaths per cycle","deaths per cycle",populationReporter) ;
        //populationPresenter.plotDeathsPerCycle();
        
        }
    }

    /**
     * Community object containing all agent(s) and Relationships and methods 
     * for pairing agent(s) into relationship(s) and for ending relationship(s)
     * TODO: Generalise to include agents other than MSM.
     */
    public Community() {
        // Populate community with agents
        System.out.println(initialiseCommunity()) ;
        /*
        initialRecord = "0," ;
        for (int id = 0 ; id < population ; id++ ) 
        {
            //Class<?> AgentClazz = Class.forName("MSM") ; 
            //Agent newAgent = (Agent) AgentClazz.newInstance() ;
            //Constructor<?> agentConstructor = AgentClazz.getDeclaredConstructors()[0] ;

            // Call generateAgent to get randomly chosen combination of Agent subclasses
            MSM newAgent = generateAgent() ;  //new MSM(-1) ;
            agents.add(newAgent) ;

            // Record newAgent for later reporting
            initialRecord += newAgent.getCensusReport() ;
            //LOGGER.info(initialRecord);
        }
        //String relationshipRecord = generateRelationships() ;
        */
    }
    
    public Community(String simName)
    {
        initialRecord = "" ;

        if (!simName.isEmpty())
        {
            //FIXME: Must ensure that random generator is properly calibrated after 
            // generating new Agents, even after reloading seed.
            Reporter reporter = new Reporter(simName,"output/test/") ;
            long seed = Long.valueOf(reporter.getMetaDatum("Community.RANDOM_SEED")) ;
            RANDOM_SEED = seed ;
            RAND = new Random(seed) ;

            seed = Long.valueOf(reporter.getMetaDatum("Agent.RANDOM_SEED")) ;
            Agent.SET_RAND(seed) ;

            seed = Long.valueOf(reporter.getMetaDatum("Site.RANDOM_SEED")) ;
            Site.SET_RAND(seed) ;

            seed = Long.valueOf(reporter.getMetaDatum("Relationship.RANDOM_SEED")) ;
            Relationship.SET_RAND(seed) ;
        }
        System.out.println(initialiseCommunity()) ;
                
    }
    
    /**
     * Initialises community based on previously saved simulation, if given.
     * @param simName - Name of simulation to be reloaded.
     * @param fromCycle - the cycle of reloaded simulation from which to 
     * recommence simulation.
     */
    public Community(String simName, int fromCycle)
    {
        if (simName.isEmpty())
            System.out.println(initialiseCommunity()) ;
        else
        {
            this.agents = Agent.REBOOT_AGENTS(simName,true) ;
            this.initialRecord = "" ; 
            for (Agent agent : agents)
                initialRecord += agent.getCensusReport() ;
            nbRelationships = Relationship.REBOOT_RELATIONSHIPS(simName, agents) ;
            scribe = new Scribe(SIM_NAME, new String[] {"relationship","encounter","infection", "population"}) ;
        }
    }

    /**
     * For setting up specific questions. Code will flux dramatically.
     */
    private String initialiseCommunity()
    {
        String report = "" ;
        initialRecord = "" ;
        
        scribe = new Scribe(SIM_NAME, new String[] {"relationship","encounter","infection", "population"}) ;
        for (int id = 0 ; id <  population ; id++ ) 
        {
            //Class<?> AgentClazz = Class.forName("MSM") ; 
            //Agent newAgent = (Agent) AgentClazz.newInstance() ;
            //Constructor<?> agentConstructor = AgentClazz.getDeclaredConstructors()[0] ;

            // Call generateAgent to get randomly chosen combination of Agent subclasses
            //and to impose initial conditions, such as prepStatus=false
            MSM newAgent = generateAgent(-1) ;  //new MSM(-1) ;
            newAgent.setPrepStatus(false) ;
            //newAgent.setPrepStatus(newAgent.getAgent().equals("RiskyMSM"));
            agents.add(newAgent) ;

            // Record newAgent for later reporting
            initialRecord += newAgent.getCensusReport() ;
            //LOGGER.info(initialRecord);
        }
        //String relationshipRecord = generateRelationships() ;
        // Clear all initial infections
        
        // Infect one RiskyMSM.urethra
//        for (Agent agent : agents) 
//        {
//            // Start with RiskyMSM
//            if (!agent.getAgent().equals("RiskyMSM"))
//                continue ;
//            MSM msm = (MSM) agent ;
//            
//            // Start with single Rectum infection
//            Site site = msm.getUrethra() ;
//            boolean infected = site.receiveInfection(1.1) ;
//            site.chooseSymptomatic(-1) ;
//            msm.setInfectedStatus(infected);
//            msm.chooseSymptomatic(site) ;
//            break ;
//        }
//        return "Population clear except for one RiskyMSM with asymptomatic Urethral infection." ;
        report = "Currently no Agents on PrEP" ;
        return report ;
    }
    
    /**
     * For addressing specific questions. Code will flux dramatically.
     * TODO: Generalise to not be MSM specific.
     */
    private String interveneCommunity(int cycle)
    {
        int startCycle = 365 * 3 ;
        if (cycle < startCycle)
            return "" ;
        if (2<0)
        {
            for (Agent agent : agents)
            {
                agent.setProbabilityUseCondom(1.0);
                agent.setRiskyStatus(false) ;
            }
            return "" ;
        }
        int year = (cycle - startCycle)/365 ;
        if (year == 0)
            return "" ;
        
        String report = "" ;
        
        if (year * 365 != (cycle - startCycle))
            return report ;
        LOGGER.info(String.valueOf(year)) ;
        Agent.REINIT(agents, year) ;
        if (2 > 0)
            return report;
        try
        {
            if ((year == 0)) // && (year < 6))
                for (Agent agent : agents)
                {
                //agent.reinitScreenCycle(year);
                //agent.reinitProbabilityAntiViral(year) ;
                //agent.reinitProbablityDiscloseHIV(year);
                //agent.reinitRiskOdds(year);
                    //agent.stopCondomUse() ;
                    //agent.scaleProbabilityUseCondom(.075);
                    //agent.adjustProbabilityUseCondom();
                }
        }
        catch( Exception e ) // cycle extends beyond trend data
        {
            LOGGER.severe(e.toString()) ;
        }
        report = "parameters adjusted according to ARTB" ;  // PrEP introduced" ; // gradually" ;


        /*
        int agentId = 5*(cycle-1000) ;
        //for (int index = 0 ; index < 5 ; index++ )
          //  agents.get(agentId + index).adjustCondomUse() ;
        int newPrep = 5 ;
        for (Agent agent : agents)
        {
            if ("RiskyMSM".equals(agent.getAgent()) && !((MSM) agent).getPrepStatus())
            {
                //double prepProbability = ((MSM) agent).getProbabilityPrep() ;
                ((MSM) agent).reinitPrepStatus(true) ; // RAND.nextDouble() < prepProbability) ;
                agent.adjustCondomUse();
                newPrep-- ;
                if (newPrep == 0)
                    break ;
            }
        }
        //report = "condom use reduced with increased testing" ;  // PrEP introduced" ; // gradually" ;
        */
        return report ;
    }
    
    /**
     * Calls BIRTH_MSM() to generate either RiskyMSM and SafeMSM.
     * TODO: Generalise to more general types of Agent
     * @return MSM subclass newAgent 
     */
    private MSM generateAgent(int startAge)
    {
        // if Agent.subclass == MSM
        MSM newAgent = new MSM(startAge);
        //MSM newAgent = MSM.BIRTH_MSM(startAge);
        
        // Impose initial condition here
        newAgent.setPrepStatus(false) ;
            
        return newAgent ;
    }

    /**
     * Generate relationships within the community. 
     * The agents are shuffled and then split in half, with the first
     * half in relationship proposals with the second half.
     * 
     * @return (String) report of Relationships generated
     */
    private String generateRelationships(boolean burnin)
    {
        String report = "" ;

        Collections.shuffle(agents) ;
        int halfAgents = Math.floorDiv(agents.size(), 2) ;
        try
        {
            for (int index = 0 ; index < halfAgents ; index++ )
            {
                Agent agent0 = agents.get(index) ;
                Agent agent1 = agents.get(index + halfAgents) ;

                // Have only one Relationship between two given Agents
                if (agent1.getCurrentPartnerIds().contains(agent0.getAgentId()))
                        continue ;

                // Tell Agents which type of Relationship is being proposed.
                Class<?> relationshipClazz = Relationship.chooseRelationship(agent0, agent1) ;
                if (burnin)
                    if (Casual.class.equals(relationshipClazz))
                        continue ;
                String relationshipClazzName = relationshipClazz.getSimpleName() ;

                // Argument String[] for Agent.consent 
                // TODO: Use Agent.consentArgs()
                if (agent0.consent(relationshipClazzName,agent1) && agent1.consent(relationshipClazzName,agent0))
                {
                    //String enterMethodName = "enter" + relationshipClazzName ;
                    //Method enterRelationshipMethod = Agent.class.getDeclaredMethod(enterMethodName, Relationship.class ) ;

                    Relationship relationship = (Relationship) relationshipClazz.newInstance() ;
                    nbRelationships++ ;
                    report += relationship.addAgents(agent0, agent1);
                    
                    // These lines now called indirectly from relationship.addAgents() ;
                    //report += enterRelationshipMethod.invoke(agent0, relationship) ;
                    //enterRelationshipMethod.invoke(agent1, relationship) ;


                    //report += relationship.getReport() + " " ;

                }
            }
            //LOGGER.log(Level.INFO, "createRelationships {0}", new Object[]{nbRelationships});

        }
        catch ( IllegalAccessException iae )
        {
            LOGGER.info(iae.getMessage());
        LOGGER.info("iae");
        }
        catch ( InstantiationException ie)
        {
            LOGGER.info(ie.getLocalizedMessage()) ;
        LOGGER.info("ie");
        }
        
        if (!burnin)
            report += runGroupSex() ;

        return report ;
    }

    /**
     * Selects getOrgyNumber() groups of getGroupSexEventSize() Agents,
 and invites them to join an orgy by calling Agent.joinGroupSexEvent() .
     * Those that return true enter a Casual Relationship with every other
     * participant.
     * TODO: Arrange for contacts in arbitrary order
     * @return 
     */
    private String runGroupSex()
    {
        String record = "" ;
        int gseNumber = 0 ;

        // Extracting a static field from unknown class
        // TODO: Implement getter for arbitrary Agent subclass.
        int gseSize = MSM.GROUP_SEX_EVENT_SIZE ;

        ArrayList<Agent> gseAgents = new ArrayList<Agent>() ;

        // Collect Agents willing to join an orgy
        // TODO: Check RiskyMSM only to save time
        for (Agent agent : agents)
           if (agent.joinGroupSexEvent(null))
                gseAgents.add((Agent) agent) ;    // Agent agrees to join orgy

        // Cluster into cliques of size getGroupSexEventSize()
        int nbGseAgents = gseAgents.size() ;
        int gseAgentStart = 0 ;
        int gseAgentEnd ;

        //Require orgies have at least three participants
        // FIXME:  Is this necessary?
        /*if (nbGseAgents < 3)
            gseAgentStart = nbGseAgents ;*/

        while (gseAgentStart < nbGseAgents)
        {
            gseAgentEnd = gseAgentStart + gseSize ;
            if (gseAgentEnd > gseAgents.size() - 3)
                gseAgentEnd = nbGseAgents ;

            record += Reporter.ADD_REPORT_PROPERTY("groupSex", gseNumber) ;
            // Every Agent in a given orgy has a Casual Relationship with
            //evey other Agent at that orgy
            //FIXME: Sexual contacts between pairs are clustered
            //for (Agent agent0 : gseAgents)
            for (int gseIndex0 = gseAgentStart ; gseIndex0 < gseAgentEnd ; gseIndex0++ )
            {
                Agent agent0 = gseAgents.get(gseIndex0) ;
                //record += Reporter.ADD_REPORT_PROPERTY(Reporter.AGENTID,agent0.getAgentId()) ;
                for (int orgyIndex1 = gseAgentStart ; orgyIndex1 < gseAgentEnd ; orgyIndex1++ )
                {
                    Agent agent1 = gseAgents.get(orgyIndex1) ;
                    if (agent0 == agent1)
                        continue ;    // Require two distinct Agents in a Relationship
                    Casual relationship = new Casual();
                    record += relationship.addAgents(agent0, agent1) ;
                    nbRelationships++ ;

                }
            }
            gseAgentStart = gseAgentEnd ;
            gseNumber++ ;
        }
        return record ;
    }

    /**
     * Used to track the age of Agents with each cycle.
     */
    private void ageOneDay()
    {
        //String record = "" ;
        for (Agent agent : agents)
            agent.ageOneDay() ;
    }

    /**
     * 
     * @return String giving each Agent and properties of interest to census
     */
    private String getCensus()
    {
        String record = "" ;
        for (Agent agent : agents )
        {
            record += agent.getCensusReport() ;
        }
        return record ;
    }


    /**
     * Handles introduction of new Agents every cycle
     * Agents are 'born' to the simulation at 16-20 years old
     * @return String of agentId for each agent born
     */
    private String births()
    {
        //String record = "birth:" ;
        //int currentPopulation = agents.size() ;
        int nbBirths = birthBase ;
        if (RAND.nextDouble() < birthRemainder)
            nbBirths++ ;
        return births(nbBirths) ;
    }

    private String births(int nbBirths)
    {
        String record = "birth:" ;
        for (int birth = 0 ; birth < nbBirths ; birth++ )
        {
            MSM newAgent = generateAgent(0) ; // MSM.BIRTH_MSM(0) ;
            agents.add(newAgent) ;
            record += newAgent.getCensusReport() ;
            //record += Reporter.ADD_REPORT_PROPERTY("agentId",newAgent.getAgentId()) ;
            //record += Reporter.ADD_REPORT_PROPERTY("age",newAgent.getAge()) ; 
            //currentPopulation++ ;
        }
        //record += Reporter.ADD_REPORT_PROPERTY("currentPopulation",currentPopulation) ;


        return record ;
    }

    /**
     * Chooses number of Agents who might die. If all die and the population
     * has increased then the number of agents is the same as the original 
     * population. If the number of agents is within birthRate of the original
     * population size then the population might dip lower.
     * @return String record of agentIds who died and their age-at-death
     */
    private String grimReaper()
    {
        String record = "" ;
        Agent agent ;
        /*int deaths = (agents.size() - population) ;
        if (deaths < birthRate)
            deaths = birthBase ;
        for (int death = 0 ; death < deaths ; death++)
        {
            int agentInd = RAND.nextInt(agents.size()) ;
            Agent agent = agents.get(agentInd) ; */
        for (int agentIndex = agents.size() - 1 ; agentIndex >= 0 ; agentIndex-- )
        {
            agent = agents.get(agentIndex) ; 
            if (agent.grimReaper())
            {
                nbRelationships-= agent.getCurrentRelationships().size() ;
                agents.remove(agent) ;
                record += Reporter.ADD_REPORT_PROPERTY("agentId", agent.getAgentId()) ;
                //record += Reporter.ADD_REPORT_PROPERTY("age", agent.getAge()) ;
                //currentPopulation-- ;
            }
        }
        //prepare record
        record = "death:" + record ;

        return record ;
    }

    /**
     * For each Agent, for each Relationship for which they are the partner with 
     * the lower agentID, call Relationship.encounter()
     * @return String record of agentIds in each Relationship and the 
     * description of each sexual contact returned by Relationship.encounter()
     */
    private String runEncounters()
    {
        String record = "" ;
        //ArrayList<Relationship> currentRelationships ;
        
        // LOGGER.info("nb relationships: " + relationships.size());
        for (Agent agent : agents)
        {
            for (Relationship relationship : agent.getCurrentRelationships())
            {
                // WARNING: May cause future problems with hetero couples
                // Does agent have lower agentId than partner
                // Avoid checking relationship twice by accessing only through the 
                // agent with the lower agentId
                // TODO: Incorporate this into Agent.Method()
                //int agentId = agent.getAgentId() ;
                //int partnerId = relationship.getPartnerId(agentId) ;
                
                if (agent != relationship.getLowerIdAgent())
                    continue ;
                try
                {
                    if (RAND.nextDouble() < relationship.getEncounterProbability())
                        record += Reporter.ADD_REPORT_PROPERTY(Reporter.RELATIONSHIPID, relationship.getRelationshipId()) 
                                + relationship.encounter() ;
                    //System.out.println(record);
                }
                catch (NoSuchMethodException nsme)
                {
                    LOGGER.info(nsme.getLocalizedMessage());
                    record += nsme.toString(); //  .getMessage() ;
                }
                catch (InvocationTargetException ite)
                {
                    LOGGER.info(ite.getLocalizedMessage());
                    //record += ite.getMessage() ;
                }
                catch (IllegalAccessException iae)
                {
                    LOGGER.info(iae.getLocalizedMessage());
                    record += iae.getMessage() ;
                }
            }
        }
        return record ;
    }

    /**
     * Loops through relationships and probabilistically chooses to end them.
     * TODO: Return useful (String) record.
     * @return (String) ""
     */
    private String clearRelationships() 
    {
        String record = Reporter.ADD_REPORT_LABEL("clear") ;
        ArrayList<Relationship> currentRelationships ;
        Relationship relationship ;

        for (Agent agent : agents)
        {
            currentRelationships = agent.getCurrentRelationships() ;
            for (int relationshipIndex = (currentRelationships.size() - 1) ; relationshipIndex >= 0 ; 
                    relationshipIndex-- )
            {
                relationship = currentRelationships.get(relationshipIndex) ;
                // Avoid checking relationship twice
                //int agentId = agent.getAgentId() ;
                if (agent == relationship.getLowerIdAgent())
                    record += endRelationship(relationship) ;
                //LOGGER.log(Level.INFO, "nbRelationships: {0}", new Object[]{nbRelationships});

            }
        }
        return record ;
    }

    /*****************************************************************
     * Calls breakup() of Relationship relationship, which probabilistically
     * calls Relationship.del() 
     * @param relationship
     *****************************************************************/
    private String endRelationship(Relationship relationship) 
    {
        if (relationship.breakup())
        {
            nbRelationships-- ;
            return relationship.endRelationship() ;
        }
        return "" ;
    }

    /**
     * Progresses course of STI in Agents who have one.
     * Treats Agents who are symptomatic or randomly choose to be treated.
     * Tracks if treatment was successful.
     * Check if disease has run its course and clears it if so.
     * @return (String) record in STIs progress
     */
    private String progressInfection(int cycle)
    {
        return progressInfection(new Object[] {cycle}) ;
    }

    /**
     * Progresses course of STI in Agents who have one.
     * Treats Agents who are symptomatic or randomly choose to be treated.
     * Tracks if treatment was successful.
     * Check if disease has run its course and clears it if so.
     * @return (String) record in STIs progress
     */
    private String progressInfection(Object[] args)
    {
        String record = "" ;
        boolean infected ;
        //long startTime = System.nanoTime() ;

        for (Agent agent : agents)
        {
            // record += agent.progressSitesInfection(cycle)
            //LOGGER.log(Level.INFO,"infected:{0}",agent.getAgentId());
            //record += Reporter.ADD_REPORT_PROPERTY("agentId",agent.getAgentId()) ;
            infected = agent.getInfectedStatus();
            //record += Reporter.ADD_REPORT_PROPERTY("infected", infected) ;
            
            // Due for an STI screen?
            if (RAND.nextDouble() < agent.getScreenProbability(args)) 
            {
                record += Reporter.ADD_REPORT_PROPERTY("agentId",agent.getAgentId()) ;
                record += Reporter.ADD_REPORT_LABEL("tested") ;
                if (infected)
                {
                    //LOGGER.info("screening agentId:"+String.valueOf(agent.getAgentId())) ;
                    
                    for (Site site : agent.getSites())
                    {
                        if (agent.getInfectedStatus(site) != 0)
                            record += Reporter.ADD_REPORT_PROPERTY(site.getSite(), agent.getSymptomatic(site)) ;
                    }
                    agent.treat() ;
                    record += Reporter.ADD_REPORT_LABEL("treated") ;
                }
                record += " " ;
            }
            else if (infected)
            {
                //LOGGER.log(Level.INFO, "infected:{0}", agent.getAgentId());
                record += Reporter.ADD_REPORT_PROPERTY("agentId",agent.getAgentId()) ;
                for (Site site : agent.getSites())
                {
                    if (agent.getInfectedStatus(site) != 0)
                        record += Reporter.ADD_REPORT_PROPERTY(site.getSite(), agent.getSymptomatic(site)) ;
                    //LOGGER.info(site.getSite()) ;
                }
                
                // agent.progressSitesInfection() allow infection to run one cycle of its course
                // and returns boolean whether agent is cleared (!stillInfected)
                if (agent.progressSitesInfection())
                {
                    record += Reporter.ADD_REPORT_LABEL("cleared") ;
                    record += " " ;
                    //LOGGER.info("cleared");
                }
                else if (agent.getSymptomatic())
                    if (agent.treatSymptomatic())  
                    {
                        //record += Reporter.ADD_REPORT_LABEL("tested") ;
                        record += Reporter.ADD_REPORT_PROPERTY("tested","treated") ;
                        //LOGGER.info("treated");
                    }
            }
        }
        //LOGGER.info(record)
        return record ;
    }

    /**
     * Progresses course of STI in Agents who have one.
     * Treats Agents who are symptomatic or randomly choose to be treated.
     * Tracks if treatment was successful.
     * Check if disease has run its course and clears it if so.
     * @return (String) record in STIs progress
     */
    private String progressInfection_Site(int cycle)
    {
        String agentRecord ;
        String record = "" ;
        boolean siteInfected ;
        boolean treat ;
        //boolean allSites ;
        //ArrayList<Site> untestedSites ;
        //int siteIndex ;
        //int testedIndex ;
        //long startTime = System.nanoTime() ;

        for (Agent agent : agents)
        {
            agentRecord = "" ; 
            treat = false ;
            //allSites = false ;
            //untestedSites = new ArrayList<Site>() ;
            /*for (Site site : agent.getSites())
                untestedSites.add(site) ;*/
            //LOGGER.log(Level.INFO, "agentId:{0}", agent.getAgentId());
            
            for (Site site : agent.getSites())
            {
                siteInfected = (site.getInfectedStatus() != 0) ;
                //LOGGER.log(Level.INFO, "{0} {1}", new Object[] {site.getSite(),siteInfected}) ;

                // Due for an STI screen?
                if (RAND.nextDouble() < site.getScreenProbability(new String[] {Integer.toString(cycle)})) 
                {
                    if (siteInfected)
                    {        
                        //LOGGER.info("infected") ;
                        agentRecord += Reporter.ADD_REPORT_PROPERTY(site.getSite(), site.getSymptomatic()) ;
                        treat = site.treat() ;
                    }
                    else
                        agentRecord += Reporter.ADD_REPORT_PROPERTY(site.getSite(),Reporter.CLEAR) ;
                    
                    agentRecord += Reporter.ADD_REPORT_PROPERTY("tested") ;
                    //untestedSites.remove(site) ;
                }
                else if (siteInfected)
                {
                    agentRecord += Reporter.ADD_REPORT_PROPERTY(site.getSite(), site.getSymptomatic()) ;

                    // agent.progressSitesInfection() allow infection to run one cycle of its course
                    // and returns boolean whether agent is cleared (!stillInfected)
                    //if (agent.progressSitesInfection())
                    if (site.progressInfection())    // STI has run its course and clears naturally
                    {
                        agentRecord += Reporter.ADD_REPORT_PROPERTY("cleared") ;
                        agent.updateInfectedStatus() ;
                    }
                    else if (site.getSymptomatic())
                        if (site.treatSymptomatic())    // Incubation period has passed
                        {
                            //allSites = true ;
                            treat = true ;
                            agentRecord += Reporter.ADD_REPORT_PROPERTY("tested") ;
                            //untestedSites.remove(site) ;
                        }
                }
            }
            /*if (allSites)  // if any Site symptomatic
                for (Site site : untestedSites)
                {
                    siteIndex = agentRecord.indexOf(site.getSite()) ;
                    if (siteIndex > -1)    // Site infected but not tested
                    {
                        // Skip value of siteName
                        testedIndex = agentRecord.indexOf(" ",siteIndex) + 1 ;
                        if (agentRecord.substring(testedIndex).startsWith(Reporter.CLEAR))    // infection cleared naturally
                            testedIndex = agentRecord.indexOf(" ",testedIndex) + 1 ;
                        else    // infection undetected
                            site.treat() ;
                        agentRecord = agentRecord.substring(0, testedIndex) + Reporter.ADD_REPORT_PROPERTY("tested")
                                + agentRecord.substring(testedIndex) ;
                    }
                    else
                    {
                        if (site.getInfectedStatus() != 0)
                        {        
                            //LOGGER.info("infected") ;
                            agentRecord += Reporter.ADD_REPORT_PROPERTY(site.getSite(), site.getSymptomatic()) ;
                            site.treat() ;
                        }
                        else
                            agentRecord += Reporter.ADD_REPORT_PROPERTY(site.getSite(),Reporter.CLEAR) ;
                        
                        agentRecord += Reporter.ADD_REPORT_PROPERTY("tested") ;
                    }
                }*/
            if (treat)
            {
                agentRecord += Reporter.ADD_REPORT_PROPERTY("treated") ;
                agent.updateInfectedStatus() ;
            }
            if (!agentRecord.isEmpty())
            {
                record += Reporter.ADD_REPORT_PROPERTY(Reporter.AGENTID, agent.getAgentId()) ;
                record += agentRecord ;
            }
        }
        
        return record ;
    }

    private void submitRecords(String generateRecord, String encounterRecord, //String clearRecord, 
            String infectionRecord, String populationRecord)
    {
            relationshipReport.add(generateRecord) ;
            //LOGGER.info(encounterRecord);
            encounterReport.add(encounterRecord) ;
            //encounterScribe.writeRecord(encounterRecord);

            //clearReport.add(clearRecord) ;
            infectionReport.add(infectionRecord) ;
            populationReport.add(populationRecord) ;
    }

    /**
     * Saves Reports to file via scribe.dump() and empties them in memory.
     */
    private void dump()
    {
        scribe.dump(this) ;
                //MetaData() ;
        try
        {
            for (String scribeName : scribe.properties)
            {
                this.getClass().getDeclaredField(scribeName).set(this, (ArrayList<String>) new ArrayList<String>()) ;
            }
        }
        catch ( Exception nsfe )
        {
            LOGGER.log(Level.SEVERE, "{0} {1}", new Object[] {nsfe.getLocalizedMessage(), nsfe.toString()});
        }
    }
    
    /**
     * Saves metadata of simulation to file via scribe.dumpMetaData.
     */
    private void dumpMetaData()
    {
        ArrayList<String> metaLabels = new ArrayList<String>() ; 
        ArrayList<Object> metaData = new ArrayList<Object>() ; 
        metaLabels.add("Community.NAME_ROOT") ;
        metaData.add(Community.SIM_NAME) ;
        metaLabels.add("Community.FILE_PATH") ;
        metaData.add(Community.FILE_PATH) ;
        metaLabels.add("Community.POPULATION") ;
        metaData.add(Community.POPULATION) ;
        metaLabels.add("Community.MAX_CYCLES") ;
        metaData.add(Community.MAX_CYCLES) ;
        metaLabels.add("Community.RANDOM_SEED") ;
        metaData.add(RANDOM_SEED) ;
        
        metaLabels.add("Agent.SITE_NAMES") ;
        metaData.add(Arrays.asList(MSM.SITE_NAMES)) ; //TODO: Use Agent.SITE_NAMES
        metaLabels.add("Agent.RANDOM_SEED") ;
        metaData.add(Agent.GET_RANDOM_SEED()) ;
        
        metaLabels.add("Site.RANDOM_SEED") ;
        metaData.add(Site.GET_RANDOM_SEED()) ;
        
        metaLabels.add("Relationship.RANDOM_SEED") ;
        metaData.add(Relationship.GET_RANDOM_SEED()) ;
        metaLabels.add("Relationship.BURNIN_COMMENCE") ;
        metaData.add(Relationship.BURNIN_COMMENCE) ;
        metaLabels.add("Relationship.BURNIN_BREAKUP") ;
        metaData.add(Relationship.BURNIN_BREAKUP) ;
        
        metaLabels.add("Comment") ;
        metaData.add(Community.COMMENT) ;
        
        scribe.dumpMetaData(metaLabels,metaData) ;
    }
    
    /**
     * Saves Agent, infection, and Relationship data to file in order to resume
     * simulation later. 
     */
    private void dumpRebootData()
    {
        ArrayList<String> metaLabels = new ArrayList<String>() ; 
        ArrayList<Object> metaData = new ArrayList<Object>() ; 
        
        metaLabels.add("Agents") ;
        String agentsReboot = "" ;
        for (Agent agent : agents)
            agentsReboot += agent.getRebootData() ;
        metaData.add(agentsReboot) ; 
        
        metaLabels.add("Relationships") ;
        String relationshipReboot = "" ;
        for (Agent agent : agents)
            for (Relationship relationship : agent.getCurrentRelationships())
                if (relationship.getLowerIdAgent() == agent)
                    relationshipReboot +=relationship.getRecord() ;
        metaData.add(relationshipReboot) ; 
     
        scribe.dumpRebootData(metaLabels, metaData);
    }
    
    /**
     * Reloads the Relationships saved during burn-in of a previous simulation.
     */
    private String reloadBurnin()
    {
        Reporter reporter = new Reporter(Community.RELOAD_BURNIN, Community.FILE_PATH) ;
        
        String breakupString = reporter.getMetaDatum("Relationship.BURNIN_BREAKUP") ;
        ArrayList<Object> breakupList = Reporter.EXTRACT_ALL_VALUES(Relationship.RELATIONSHIP_ID, breakupString) ;
        String commenceString = reporter.getMetaDatum("Relationship.BURNIN_COMMENCE") ;
        String returnString = "" ;
        
        String boundedString ;
        String relationshipId = "" ; 
        int agentId0 ;
        int agentId1 ;
        for (int commenceIndex = 0 ; commenceIndex >= 0 ; commenceIndex = commenceString.indexOf(Relationship.RELATIONSHIP_ID,commenceIndex+1)) 
        {
            boundedString = Reporter.EXTRACT_BOUNDED_STRING(Relationship.RELATIONSHIP_ID, commenceString, commenceIndex);
            relationshipId = Reporter.EXTRACT_VALUE(Relationship.RELATIONSHIP_ID, boundedString) ;
            if (!breakupList.contains(relationshipId)) 
            {
                returnString += boundedString ;
                String relationshipClazzName = "community." + Reporter.EXTRACT_VALUE("relationship", boundedString) ;
                try
                {
                    Relationship relationship = (Relationship) Class.forName(relationshipClazzName).newInstance();
                    relationship.setRelationshipId(Integer.valueOf(relationshipId)) ;
                    nbRelationships++ ;
                    agentId0 = Integer.valueOf(Reporter.EXTRACT_VALUE(Reporter.AGENTID0,boundedString)) ;
                    agentId1 = Integer.valueOf(Reporter.EXTRACT_VALUE(Reporter.AGENTID1,boundedString)) ;
                    relationship.addAgents(agents.get(agentId0), agents.get(agentId1)) ;
                    
                }
                catch ( Exception e )
                {
                    LOGGER.severe(e.toString()) ;
                }
            }
            
        }
        Relationship.NB_RELATIONSHIPS_CREATED = Integer.valueOf(relationshipId) + 1 ;
        return returnString ;
    }
    
    /**
     * Object to gather data and record it to Files
     */
    //public Reporter(String simName, ArrayList<String> generateReports, 

    //	ArrayList<String> encounterReports, ArrayList<String> clearReports, ArrayList<String> screenReports)
    private class Scribe{

        String extension = ".txt" ;
        /** The number of Community cycles to pass between reports. */
        int dumpCycle ;
        
        /** Keeps track of how many dumps have taken place so far for labelling purposes. */
        int dumpsSoFar = 0 ;

        String simName ;
        String[] properties ;

        String globalFolder = Community.FILE_PATH ;
        //String fileName ;

        // File paths
        String logFilePath ;
        String errorFilePath ;
        //String outputFilePath ;


        // File objects
        BufferedWriter[] fileWriters ;
        /*
        File logFile ;
        File errorFile ;
        File outputFile ;
        */

        private Scribe() 
        {
            
        }
        
        private Scribe(String simName, String[] propertyNames) 
        {
            this.simName = simName ;
            if (Community.PARTIAL_DUMP)
                this.dumpCycle = Community.DUMP_CYCLE ;
            else
                this.dumpCycle = 0 ;
            
            this.properties = new String[propertyNames.length] ;
            for (int propertyIndex = 0 ; propertyIndex < propertyNames.length ; propertyIndex++ )
                properties[propertyIndex] = propertyNames[propertyIndex] + "Report" ;
        }
        
        
        private void dump(Community community)
        {
            String property ;
            
            for (int index = 0 ; index < properties.length ; index++ )
            {
                property = properties[index] ;
                try
                {
                    BufferedWriter fileWriter = openFile(property) ;
                    writeReport(fileWriter,(ArrayList<String>) Community.class.getDeclaredField(property).get(community)) ;
                    closeFile(fileWriter) ;
                }
                catch ( Exception e )
                {
                    LOGGER.log(Level.SEVERE, e.toString());
                }
            }
            dumpsSoFar++ ;
            LOGGER.log(Level.INFO, "dumpsSoFar:{0} nb_Files:{1} properties:{2}", new Object[] {dumpsSoFar,(new File(globalFolder)).listFiles().length,properties.length});
        }
        
        /**
         * Opens file and writes metadata to it.
         * @param metaLabels
         * @param metaData 
         */
        protected void dumpMetaData(ArrayList<String> metaLabels, ArrayList<Object> metaData)
        {
            String fileName = simName + "-METADATA" + extension ;
            try
            {
                BufferedWriter metadataWriter = new BufferedWriter(new FileWriter(globalFolder + fileName,false)) ;
                writeMetaData(metadataWriter,metaLabels,metaData) ;
                metadataWriter.close() ;
            } 
            catch ( Exception e )
            {
                LOGGER.info(e.toString());
            }
        }
        
        /**
         * Opens file and writes metadata to it.
         * @param metaLabels
         * @param metaData 
         */
        protected void dumpRebootData(ArrayList<String> metaLabels, ArrayList<Object> metaData)
        {
            String fileName = simName + "-REBOOT" + extension ;
            LOGGER.info(fileName);
            try
            {
                BufferedWriter metadataWriter = new BufferedWriter(new FileWriter(globalFolder + fileName,false)) ;
                writeMetaData(metadataWriter,metaLabels,metaData) ;
                metadataWriter.close() ;
            } 
            catch ( Exception e )
            {
                LOGGER.info(e.toString());
            }
        }
        
        /**
         * Filenames are appended according to their first cycle. This cycle 
         * number if padded out with leading zeroes to generate filename order on disk.
         * @param property
         * @return (BufferedWriter) file for dumping property data for the current
         * dump cycle after constructing the appropriate filename.
         * @throws IOException 
         */
        private BufferedWriter openFile(String property) throws IOException 
        {
            String fileName = simName + property ; 
            if (dumpCycle > 0)
            {
                int dumpDigits = (int) Math.floor(Math.log10(MAX_CYCLES-1)) + 1 ;
                String nameIndex = String.valueOf(dumpsSoFar * dumpCycle) ;
                while (nameIndex.length() <  dumpDigits)
                    nameIndex = "0" + nameIndex ;
                fileName += "-" + nameIndex;
            }
            String outputFilePath = globalFolder + fileName + extension ;
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outputFilePath,false));
            return fileWriter ;
        }


        //outputCycle = 5 ;


        /*
        logFilePath = globalFolder + simName ;
        logFile = new File(logFilePath) ;

        errorFilePath = globalFolder + simName ;
        errorFile = new File(errorFilePath) ;
        */

        /**
         * Writes given String record to BufferedWriter fileWriter.
         * @param fileWriter
         * @param record
         * @throws Exception 
         */
        private void writeRecord(BufferedWriter fileWriter, String record) throws Exception
        {
                fileWriter.write(record);
                fileWriter.newLine();
        }

        /**
         * Writes (String) ArrayList report to BufferedWriter fileWriter.
         * @param fileWriter
         * @param report
         * @throws Exception 
         */
        private void writeReport(BufferedWriter fileWriter, ArrayList<String> report) throws Exception
        {
            for (String record : report)
                writeRecord(fileWriter, record) ;
        }
        
        /**
         * Writes metadata-related variables to BufferedWriter fileWriter.
         * @param fileWriter
         * @param labels - (ArrayList String) names of variables being written.
         * @param metadata - (ArrayList Object) values of variables being written.
         */
        private void writeMetaData(BufferedWriter fileWriter, ArrayList<String> labels, ArrayList<Object> metadata)
        {
            try
            {
                for (int dataLine = 0 ; dataLine < metadata.size() ; dataLine++ )
                    writeRecord(fileWriter, Reporter.ADD_REPORT_PROPERTY(labels.get(dataLine), metadata.get(dataLine))) ;
            }
            catch ( Exception e )
            {
                LOGGER.log(Level.SEVERE, e.toString()) ;
            }
        }

        /**
         * Closes BufferedWriter fileWriter.
         * @param fileWriter 
         */
        private void closeFile(BufferedWriter fileWriter)
        {
            try
            {
                fileWriter.close();
            }
            catch ( IOException ioe )
            {
                LOGGER.log(Level.SEVERE, ioe.getLocalizedMessage());
            }
        }


    }

}
    