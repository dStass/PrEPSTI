/**
 * 
 */
package community;

import agent.* ;
import site.* ;
        
import java.io.* ;
//import java.io.FileWriter ;
//import java.io.IOException;

import reporter.* ; 
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
    static public int POPULATION = 40000 ;
    static public int MAX_CYCLES = 1000 ;
    //static public String NAME_ROOT = "RelationshipCalibration4" 
    static public String NAME_ROOT = "NoPrepCalibration16" 
    //        + "DecliningCondomsAlteredTesting"
            + "Pop" + String.valueOf(POPULATION) + "Cycles" + String.valueOf(MAX_CYCLES) ;

    static final String COMMENT = "MAX_RELATIONSHIPS set to 4 "  // "Uses parameters from NoPrepCalibration53" ;
    //+ "all encounters are recorded in full" ;
            + "consentCasualProbability * 5/12 "
    /*static final String COMMENT = "every cycle after 1000 Agents "
            + "reduce their chances of choosing condoms. "
            + "Every year after cycle 1000 testing rates are altered to compare with "
            + "long-term data." ;*/
            //+ "Assumes number of Agents at least N times number of cycles minus 1000." ;*/
            + "" ;
    
    static public String FILE_PATH = "output/test/" ;
    /** Dump reports to disk after this many cycles. */
    static final int DUMP_CYCLE = ((int) Math.pow(10, 7))/POPULATION ;
    /** Whether to dump partial reports during simulation. */
    static final boolean PARTIAL_DUMP = (DUMP_CYCLE > 0) ;
    /** How many digits represent cycles in DUMP filename. */
    static final int DUMP_DIGITS = (int) Math.floor(Math.log10(MAX_CYCLES-1)) + 1 ;
    
    /**
     * (String) Name of previous burn-in to reload.
     * Not reloaded if this is an empty string.
     */
    static final String RELOAD_BURNIN = "" ; // "NoPrepCalibration15Pop40000Cycles1000" ;
    
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
    /** Current number of sexually live agents */
    private int currentPopulation = population ;

    // Number of new population members and also average number of deaths per cycle
    private double birthRate = population/(50 * 365.25) ;
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
    
    private Scribe scribe = new Scribe(NAME_ROOT, new String[] {"relationship","encounter","infection", "population"}) ;


    // Logger
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("reporter") ;

    public static void main(String[] args)
    {
        //POPULATION = Integer.valueOf(args[0]) ;
        //MAX_CYCLES = Integer.valueOf(args[1]) ;
        
        // Record starting time to measure running time
        long startTime = System.nanoTime() ;
        LOGGER.log(Level.INFO, "Seed:{0}", String.valueOf(System.currentTimeMillis()));
    
        // Establish Community of Agents for simulation
        LOGGER.info(Community.NAME_ROOT);
        Community community = new Community() ;
        
        // Establish conditions for specific simulation questions
        System.out.println(community.initialiseCommunity()) ;

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
        

        // simulation of maxCycles cycles
        
        cycleString = "0," ;
        populationRecord = cycleString + Reporter.addReportLabel("birth") + community.initialRecord ;
        //LOGGER.info(community.initialRecord);
        System.out.println("population: " + POPULATION + ", Cycles: " + MAX_CYCLES );
        int outputInterval ;
        
        if (POPULATION < MAX_CYCLES)
            outputInterval = POPULATION/2 ;
        else
            outputInterval = POPULATION / (MAX_CYCLES) ;
        if (outputInterval < 100)
            outputInterval = 100 ;
        
        if (RELOAD_BURNIN.isEmpty())
        {
            // Generate relationships for simulation
            String commence ;
            String breakup ;
            
            for (int burnin = 0 ; burnin < 2000 ; burnin++ )
            {
                commence = community.generateRelationships() ;
                Relationship.BURNIN_COMMENCE = commence + Relationship.BURNIN_COMMENCE ;
                
                breakup = community.clearRelationships().substring(6) ;
                Relationship.BURNIN_BREAKUP += breakup ;
                //needMore = needMore || ((breakup.length() * burnin) >= Relationship.BURNIN_BREAKUP.length());
                //LOGGER.log(Level.INFO, "commence:{0} breakup:{1}", new Object[] {commence.length(),breakup.length()}) ;
                // 6 = "clear:".length()
            }
           
            //for (Agent agent : community.agents)
            {
              //  LOGGER.log(Level.INFO, "agentId:{0} nbRelationships:{1}", new Object[] {agent.getAgentId(),agent.getCurrentPartnerIds().size()});
            }
        }
        else
        {
            Relationship.BURNIN_COMMENCE = community.reloadBurnin() + " clear:";
                Relationship.BURNIN_BREAKUP = "" ;
        }
        
        //outputInterval = 1 ;
        for (int cycle = 0; cycle < MAX_CYCLES; cycle++)
        {	
            if (cycle == ((cycle/outputInterval) * outputInterval))
                LOGGER.log(Level.INFO, "Cycle no. {0}", cycleString);

            //community.interveneCommunity(cycle) ;
            
            //LOGGER.log(Level.INFO,"{0} {1}", new Object[] {Relationship.NB_RELATIONSHIPS,Relationship.NB_RELATIONSHIPS_CREATED});
            // update relationships and perform sexual encounters, report them
            relationshipRecord = cycleString + community.generateRelationships();
            encounterRecord = cycleString + community.runEncounters();
            relationshipRecord += community.clearRelationships();
            
            // treat symptomatic agents
            //LOGGER.info("infectionRecord") ;
            infectionRecord = cycleString + community.progressInfection(cycle) ;
            
            //deathRecord = cycleString
            int deltaPopulation = community.agents.size() ;  // Current poulation
            populationRecord += community.grimReaper() ;
            // Record Relationships ended due to death
            relationshipRecord += Relationship.READ_DEATH_RECORD() ;
            //LOGGER.info(relationshipRecord);
            
            // How many births to maintain population?
            deltaPopulation = deltaPopulation - community.agents.size() ;

            //LOGGER.info("submitRecords()");
            community.submitRecords(relationshipRecord,encounterRecord,infectionRecord,populationRecord) ;  // 

            // Deal with effects of aging.
            // To include in populationRecord move this above community.submitRecords()
            community.ageOneDay();
//            cyclesModYear++ ;
//            if (cyclesModYear > 363)
//            {
//                year++ ;
//                cyclesModYear = 0 ;
//                populationRecord += community.ageOneYear() ;
//            }
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
        
        long elapsedTime = System.nanoTime() - startTime ;
        long milliTime = elapsedTime/1000000 ;
        int seconds = (int) milliTime/1000 ;
        int minutes = seconds/60 ;
        System.out.println("population: " + POPULATION + ", Cycles: " + MAX_CYCLES);
        System.out.println("Elapsed running time: " + milliTime + "millseconds") ;
        System.out.println("Elapsed running time: " + seconds + "seconds") ;
        System.out.println("Elapsed running time: " + minutes + "minutes") ;
        
        String[] relationshipClassNames = new String[] {"Casual","Regular","Monogomous"} ; // "Casual","Regular","Monogomous"
        
        //RelationshipReporter relationshipReporter = new RelationshipReporter(Community.NAME_ROOT,Community.FILE_PATH) ;
        //HashMap<Object,HashMap<Object,Number>> relationshipReport 
          //      = relationshipReporter.prepareCumulativeRelationshipRecord(-1, relationshipClassNames, 0, 6, 0) ;
        //HashMap<Object,Number[]> plotReport = Reporter.invertHashMapList(relationshipReport, relationshipClassNames) ;
        //Reporter.writeCSV(plotReport, "Number of Relationships", relationshipClassNames, "cumulativeRelationship", NAME_ROOT, "output/test/") ;
        
        ScreeningReporter screeningReporter = 
                //new ScreeningReporter("prevalence",community.infectionReport) ;
                new ScreeningReporter(NAME_ROOT,FILE_PATH) ;
        //ArrayList<Object> pharynxPrevalenceReport = screeningReporter.preparePrevalenceReport("Pharynx") ;
        //Reporter.writeCSV(pharynxPrevalenceReport, "Pharynx", NAME_ROOT, FILE_PATH);
        //ScreeningPresenter screeningPresenter 
          //      = new ScreeningPresenter("prevalence",Community.NAME_ROOT,screeningReporter) ;
        //screeningPresenter.multiPlotScreening(new Object[] {"prevalence", new String[] {"Pharynx","Urethra","Rectum"},"coprevalence",new String[] {"Rectum","Pharynx"}});
        //ScreeningPresenter screeningPresenter2 
          //      = new ScreeningPresenter("prevalence",Community.NAME_ROOT,screeningReporter) ;
        //screeningPresenter2.plotPrevalence();
        //screeningPresenter2.plotIncidencePerCycle();
        ScreeningPresenter screeningPresenter3 
                = new ScreeningPresenter(NAME_ROOT,"multi prevalence",screeningReporter) ;
        screeningPresenter3.multiPlotScreening(new Object[] {"prevalence","prevalence",new String[] {"Pharynx","Rectum","Urethra"},"coprevalence",new String[] {"Pharynx","Rectum"}});  // ,"coprevalence",new String[] {"Pharynx","Rectum"},new String[] {"Urethra","Rectum"}
        
        //EncounterReporter encounterReporter = new EncounterReporter("Agent to Agent",community.encounterReport) ;
        //EncounterReporter encounterReporter = new EncounterReporter(Community.NAME_ROOT,Community.FILE_PATH) ;
//        EncounterPresenter encounterPresenter = new EncounterPresenter(Community.NAME_ROOT,"agent to agent", encounterReporter) ;
//        encounterPresenter.plotCondomUse();
//        EncounterPresenter encounterPresenter2 = new EncounterPresenter(Community.NAME_ROOT,"agent to agent", encounterReporter) ;
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
        
        //SortReporter sortReporter2 = new SortReporter("Prep +ve infections per cycle",encounterReporter,populationReporter) ;
        //SortPresenter sortPresenter2 = new SortPresenter("Prep +ve infections per cycle","Infections per cycle",sortReporter2) ;
        //sortPresenter2.plotReceiveSortPrepStatusReport(TRUE);
        //RelationshipReporter relationshipReporter 
          //      = new RelationshipReporter("New Relationships per cycle",community.relationshipReport) ;
        //ScreeningReporter screeningReporter = new ScreeningReporter("prevalence",community.infectionReport) ;
        //SortReporter sortReporter = new SortReporter("prevalence",screeningReporter,relationshipReporter) ;
        //SortPresenter sortPresenter = new SortPresenter("prevalence","prevalence",sortReporter) ;
        //sortPresenter.plotSortPrevalence(5,3);  // Up to 5 partners the past 3 years
        
        //RelationshipReporter relationshipReporter 
          //      = new RelationshipReporter("Cumulative Relationships per age",community.relationshipReport) ;
        //PopulationReporter populationReporter = new PopulationReporter("age",community.populationReport) ;
        //SortReporter sortReporter = new SortReporter("Cumulative Relationships per age",relationshipReporter,populationReporter) ;
        //SortPresenter sortPresenter = new SortPresenter("age","nbRelationships",sortReporter) ;
        //sortPresenter.plotAgeNumberEnteredRelationshipRecord() ;
        
        //RelationshipReporter relationshipReporter 
          //      = new RelationshipReporter("New Relationships per cycle",community.relationshipReport) ;
        //RelationshipPresenter relationshipPresenter 
          //      = new RelationshipPresenter("New relationships per cycle","New relationships per cycle",relationshipReporter) ;
        //relationshipPresenter.plotNewRelationshipsPerCycle();
        //RelationshipPresenter relationshipPresenter2 
          //      = new RelationshipPresenter("New Regular relationships per cycle","New Regular relationships per cycle",relationshipReporter) ;
        //relationshipPresenter2.plotNewRelationshipsPerCycle(new String[] {"Regular"});
        //RelationshipReporter relationshipReporter 
          //      = new RelationshipReporter("Relationship breakups per cycle",community.relationshipReport) ;
        //RelationshipPresenter relationshipPresenter2 
          //      = new RelationshipPresenter("Relationship breakups per cycle","Relationship breakups per cycle",relationshipReporter) ;
        //relationshipPresenter2.plotBreakupsPerCycle();
        //RelationshipReporter relationshipReporter 
        //        = new RelationshipReporter("Relationships of given length",community.relationshipReport) ;
        //RelationshipPresenter relationshipPresenter 
          //      = new RelationshipPresenter("Relationships of given length","Relationships of given length",relationshipReporter) ;
        //relationshipPresenter.plotRelationshipLength();
        //RelationshipReporter relationshipReporter 
          //      = new RelationshipReporter("Cumulative Relationships to date",community.relationshipReport) ;
        //RelationshipPresenter relationshipPresenter 
          //      = new RelationshipPresenter("Cumulative Relationships to date","Cumulative Relationships to date",relationshipReporter) ;
        //relationshipPresenter.plotCumulativeRelationships();
        //RelationshipReporter relationshipReporter 
          //      = new RelationshipReporter(Community.NAME_ROOT,Community.FILE_PATH) ; 
        //RelationshipPresenter relationshipPresenter2 
          //      = new RelationshipPresenter("Mean number of Relationships",Community.NAME_ROOT,relationshipReporter) ;
        //relationshipPresenter2.plotMeanNumberRelationshipsReport();
        
    }

    /**
     * Community object containing all agent(s) and Relationships and methods 
     * for pairing agent(s) into relationship(s) and for ending relationship(s)
     * TODO: Generalise to include agents other than MSM.
     */
    public Community() {
        // Populate community with agents
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
    }
    
    public Community(String simName)
    {
        initialRecord = "0," ;
        
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
                
        agents = Agent.reloadAgents(simName) ;
        
        for (Agent agent : agents)
            initialRecord += agent.getCensusReport() ;
    }

    /**
     * For setting up specific questions. Code will flux dramatically.
     */
    private String initialiseCommunity()
    {
        
        // Clear all initial infections
        for (Agent agent : agents)
        {
            ((MSM) agent).setPrepStatus(false);
//            agent.clearInfection();
        }
        
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
//            site.setSymptomatic(-1) ;
//            msm.setInfectedStatus(infected);
//            msm.setSymptomatic(site) ;
//            break ;
//        }
//        return "Population clear except for one RiskyMSM with asymptomatic Urethral infection." ;
        return "Currently no Agents on PrEP";
    }
    
    /**
     * For addressing specific questions. Code will flux dramatically.
     */
    private String interveneCommunity(int cycle)
    {
        if (cycle < 1000)
            return "" ;
        
        int year = (cycle-1000)/365 ;
        if (year * 365 == (cycle - 1000))
        {
            for (Agent agent : agents)
            {
                try
                {
                    ((MSM) agent).reinitScreenCycle(year);
                }
                catch( Exception e ) // cycle extends beyond trend data
                {
                    break ;
                }
            }
        }

        int agentId = 5*(cycle-1000) ;
        for (int index = 0 ; index < 5 ; index++ )
            agents.get(agentId + index).adjustCondomUse();
        /*for (Agent agent : agents)
        {
            if (false && !((MSM) agent).getPrepStatus())
            {
                //double prepProbability = ((MSM) agent).getProbabilityPrep() ;
                ((MSM) agent).reinitPrepStatus(true) ; // RAND.nextDouble() < prepProbability) ;
                break ;
            }
        }*/
        return "condom use halved for " ;  // PrEP introduced" ; // gradually" ;
    }
    
    /**
     * Calls birthMSM() to generate either RiskyMSM and SafeMSM.
     * TODO: Generalise to more general types of Agent
     * @return MSM subclass newAgent 
     */
    private MSM generateAgent()
    {
        // if Agent.subclass == MSM
        MSM newAgent = MSM.birthMSM(-1);
        return newAgent ;
    }

    /**
     * Generate relationships within the community. 
     * The agents are shuffled and then split in half, with the first
     * half in relationship proposals with the second half.
     * 
     * @return (String) report of Relationships generated
     */
    private String generateRelationships()
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
                String relationshipClazzName = relationshipClazz.getSimpleName() ;

                // Argument String[] for Agent.consent 
                // TODO: Use Agent.consentArgs()
                if (agent0.consent(relationshipClazzName,agent1) && agent1.consent(relationshipClazzName,agent0))
                {
                    //String enterMethodName = "enter" + relationshipClazzName ;
                    //Method enterRelationshipMethod = Agent.class.getDeclaredMethod(enterMethodName, Relationship.class ) ;

                    Relationship relationship = (Relationship) relationshipClazz.newInstance();
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

            record += Reporter.addReportProperty("groupSex", gseNumber) ;
            // Every Agent in a given orgy has a Casual Relationship with
            //evey other Agent at that orgy
            //FIXME: Sexual contacts between pairs are clustered
            //for (Agent agent0 : gseAgents)
            for (int gseIndex0 = gseAgentStart ; gseIndex0 < gseAgentEnd ; gseIndex0++ )
            {
                Agent agent0 = gseAgents.get(gseIndex0) ;
                //record += Reporter.addReportProperty(Reporter.AGENTID,agent0.getAgentId()) ;
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

    private String ageOneYear()
    {
        String record = "" ;
        String agentReport ;
        int nbAgentRelationships ;
        for (Agent agent : agents)
        {
            //nbAgentRelationships = agent.getCurrentRelationships().size() ;
            agentReport = agent.ageOneYear() ;
            //if (agentReport.contains("death"))
              //  nbRelationships -= nbAgentRelationships ;
            record += agentReport ;
        }
        return record ;
    }

    /**
     * TODO: Implement generalised getCensusData() in Agent and subtypes
     * @return String giving each Agent and properties of interest to census
     */
    private String getCensus()
    {
        String record = "" ;
        String agentIdString ;
        String agentType ;
        String age ;
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
            MSM newAgent = MSM.birthMSM(0) ;
            agents.add(newAgent) ;
            record += newAgent.getCensusReport() ;
            //record += Reporter.addReportProperty("agentId",newAgent.getAgentId()) ;
            //record += Reporter.addReportProperty("age",newAgent.getAge()) ; 
            currentPopulation++ ;
        }
        record += Reporter.addReportProperty("currentPopulation",currentPopulation) ;


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
                record += Reporter.addReportProperty("agentId", agent.getAgentId()) ;
                //record += Reporter.addReportProperty("age", agent.getAge()) ;
                currentPopulation-- ;
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
        ArrayList<Relationship> currentRelationships ;
        
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
                        record += Reporter.addReportProperty(Reporter.RELATIONSHIPID, relationship.getRelationshipId()) 
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
        String record = Reporter.addReportLabel("clear") ;
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
        String record = "" ;
        boolean infected ;
        //long startTime = System.nanoTime() ;

        for (Agent agent : agents)
        {
            //LOGGER.log(Level.INFO,"infected:{0}",agent.getAgentId());
            //record += Reporter.addReportProperty("agentId",agent.getAgentId()) ;
            infected = agent.getInfectedStatus();
            //record += Reporter.addReportProperty("infected", infected) ;
            
            // Due for an STI screen?
            if (RAND.nextDouble() < agent.getScreenProbability(new String[] {Integer.toString(cycle)})) 
            {
                record += Reporter.addReportProperty("agentId",agent.getAgentId()) ;
                record += Reporter.addReportLabel("tested") ;
                if (infected)
                {
                    //LOGGER.info("screening agentId:"+String.valueOf(agent.getAgentId())) ;
                    
                    for (Site site : agent.getSites())
                    {
                        if (site.getInfectedStatus() == 0)
                            continue ;
                        record += Reporter.addReportProperty(site.getSite(), site.getSymptomatic()) ;
                    }
                    agent.treat() ;
                    record += Reporter.addReportLabel("treated") ;
                }
                continue ;  // Move to next Agent
            }
            if (infected)
            {
                //LOGGER.log(Level.INFO, "infected:{0}", agent.getAgentId());
                record += Reporter.addReportProperty("agentId",agent.getAgentId()) ;
                for (Site site : agent.getSites())
                {
                    if (site.getInfectedStatus() != 0)
                        record += Reporter.addReportProperty(site.getSite(), site.getSymptomatic()) ;
                    //LOGGER.info(site.getSite()) ;
                }
                
                // agent.progressInfection() allow infection to run one cycle of its course
                // and returns boolean when agent is cleared (!stillInfected)
                if (agent.progressInfection())
                {
                    record += Reporter.addReportLabel("cleared") ;
                    //LOGGER.info("cleared");
                }
                else if (agent.getSymptomatic())
                    if (agent.treatSymptomatic())  
                    {
                        record += Reporter.addReportLabel("tested") ;
                        record += Reporter.addReportLabel("treated") ;
                        //LOGGER.info("treated");
                    }
            }
        }
//            long elapsedTime = System.nanoTime() - startTime ;
//        long milliTime = elapsedTime/1000000 ;
//        int seconds = (int) milliTime/1000 ;
//        System.out.println("Elapsed running time: " + elapsedTime + "millseconds") ;
//        System.out.println("Elapsed running time: " + milliTime + "millseconds") ;

        //LOGGER.info(record);
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
    
    
    private void dumpMetaData()
    {
        ArrayList<String> metaLabels = new ArrayList<String>() ; 
        ArrayList<Object> metaData = new ArrayList<Object>() ; 
        metaLabels.add("Community.NAME_ROOT") ;
        metaData.add(Community.NAME_ROOT) ;
        metaLabels.add("Community.FILE_PATH") ;
        metaData.add(Community.FILE_PATH) ;
        metaLabels.add("Community.POPULATION") ;
        metaData.add(Community.POPULATION) ;
        metaLabels.add("Community.MAX_CYCLES") ;
        metaData.add(Community.MAX_CYCLES) ;
        metaLabels.add("community.RANDOM_SEED") ;
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
     * Reloads the Relationships saved during burn-in of a previous simulation.
     */
    private String reloadBurnin()
    {
        Reporter reporter = new Reporter(Community.RELOAD_BURNIN, Community.FILE_PATH) ;
        
        String breakupString = reporter.getMetaDatum("Relationship.BURNIN_BREAKUP") ;
        ArrayList<Object> breakupList = Reporter.extractAllValues(Relationship.RELATIONSHIP_ID, breakupString) ;
        String commenceString = reporter.getMetaDatum("Relationship.BURNIN_COMMENCE") ;
        String returnString = "" ;
        
        String boundedString ;
        String relationshipId = "" ; 
        int agentId0 ;
        int agentId1 ;
        for (int commenceIndex = 0 ; commenceIndex >= 0 ; commenceIndex = commenceString.indexOf(Relationship.RELATIONSHIP_ID,commenceIndex+1)) 
        {
            boundedString = Reporter.extractBoundedString(Relationship.RELATIONSHIP_ID, commenceString, commenceIndex);
            relationshipId = Reporter.extractValue(Relationship.RELATIONSHIP_ID, boundedString) ;
            if (!breakupList.contains(relationshipId)) 
            {
                returnString += boundedString ;
                String relationshipClazzName = "community." + Reporter.extractValue("relationship", boundedString) ;
                try
                {
                    Relationship relationship = (Relationship) Class.forName(relationshipClazzName).newInstance();
                    relationship.setRelationshipId(Integer.valueOf(relationshipId)) ;
                    nbRelationships++ ;
                    agentId0 = Integer.valueOf(Reporter.extractValue(Reporter.AGENTID0,boundedString)) ;
                    agentId1 = Integer.valueOf(Reporter.extractValue(Reporter.AGENTID1,boundedString)) ;
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
                String nameIndex = String.valueOf(dumpsSoFar * dumpCycle) ;
                while (nameIndex.length() < Community.DUMP_DIGITS)
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
                    writeRecord(fileWriter, Reporter.addReportProperty(labels.get(dataLine), metadata.get(dataLine))) ;
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
