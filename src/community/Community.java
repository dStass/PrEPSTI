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
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;

import org.jfree.chart.* ;

/******************************************************************
 * @author Michael Luke Walker
 *
 *******************************************************************/
public class Community {
    static public int POPULATION = 40000;
    static public int MAX_CYCLES = 5000;
    //static public String NAME_ROOT = "testPlotSortRelationshipsByAge"
    //static public String NAME_ROOT = "testPlotSortPrevalenceYear3Partners10"
    static public String NAME_ROOT = "NoprepGSE20sizeContacts5"
    //static public String NAME_ROOT = "testBurnin5000"
            + "Pop" + String.valueOf(POPULATION) + "Cycles" + String.valueOf(MAX_CYCLES) ;

    static public String FILE_PATH = "../output/test/" ;
    
    static public String getFilePath()
    {
        return FILE_PATH ;
    }
    
    static Random RAND = new Random() ;

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
    
    private static String TRUE = "true" ;
    private static String FALSE = "false" ;
    
    private String initialRecord ;
    private ArrayList<String> genericReport = new ArrayList<String>() ;
    private ArrayList<String> relationshipReport = new ArrayList<String>() ;
    private ArrayList<String> encounterReport = new ArrayList<String>() ;
    //private ArrayList<String> clearReport = new ArrayList<String>() ;
    private ArrayList<String> infectionReport = new ArrayList<String>() ;
    private ArrayList<String> populationReport = new ArrayList<String>() ;
    //private ArrayList<String> censusReport = new ArrayList<String>() ;
    
    private Scribe encounterScribe ;  // = new Scribe() ;


    // Logger
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("reporter") ;

    public static void main(String[] args)
    {
        //POPULATION = Integer.valueOf(args[0]) ;
        //MAX_CYCLES = Integer.valueOf(args[1]) ;
        
        // Record starting time to measure running time
        long startTime = System.nanoTime() ;
        
        // Establish Community of Agents for simulation
        LOGGER.info(Community.NAME_ROOT);
        Community community = new Community() ;
        
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
        if (outputInterval < 10)
            outputInterval = 10 ;
        
        // Generate relationships for simulation
        for (int burnin = 0 ; burnin < 2000 ; burnin++ )
        {
            Relationship.BURNIN_COMMENCE = community.generateRelationships() + Relationship.BURNIN_COMMENCE ;
            Relationship.BURNIN_BREAKUP = community.clearRelationships().substring(6) + Relationship.BURNIN_BREAKUP ;
        }
        
        //outputInterval = 1 ;
        for (int cycle = 0; cycle < MAX_CYCLES; cycle++)
        {	
            if (cycle == ((cycle/outputInterval) * outputInterval))
                LOGGER.log(Level.INFO, "Cycle no. {0}", cycleString);

            if (cycle == 4000 && false)
                community.interveneCommunity() ;
            //LOGGER.log(Level.INFO,"{0} {1}", new Object[] {Relationship.NB_RELATIONSHIPS,Relationship.NB_RELATIONSHIPS_CREATED});
            // update relationships and perform sexual encounters, report them
            relationshipRecord = cycleString + community.generateRelationships();
            encounterRecord = cycleString + community.runEncounters();
            //clearanceRecord ; 
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
            
            cycleString = Integer.toString(cycle+1) + "," ;
            populationRecord = cycleString + community.births(deltaPopulation) ;
        }
        //community.encounterScribe = community.new Scribe(Community.NAME_ROOT) ;
        //community.dump(new String[] {"relationship","encounter","infection", "population"}) ;
        //community.encounterScribe.closeFile();
        
        long elapsedTime = System.nanoTime() - startTime ;
        long milliTime = elapsedTime/1000000 ;
        int seconds = (int) milliTime/1000 ;
        int minutes = seconds/60 ;
        System.out.println("population: " + POPULATION + ", Cycles: " + MAX_CYCLES);
        System.out.println("Elapsed running time: " + milliTime + "millseconds") ;
        System.out.println("Elapsed running time: " + seconds + "seconds") ;
        System.out.println("Elapsed running time: " + minutes + "minutes") ;
        
        EncounterReporter encounterReporter = new EncounterReporter("Agent to Agent",community.encounterReport) ;
        EncounterPresenter encounterPresenter = new EncounterPresenter(Community.NAME_ROOT,"agent to agent", encounterReporter) ;
        encounterPresenter.plotNbTransmissions(); 
        //encounterPresenter.plotTransmittingSites(new String[] {"Urethra","Rectum","Pharynx"});
        //encounterPresenter.plotFromSiteToSite(new String[] {"Urethra","Rectum","Pharynx"});
        //encounterPresenter.plotAgentToAgentNetwork();
        
        //PopulationReporter populationReporter = new PopulationReporter("age-at-death",community.populationReport) ;
        //PopulationPresenter populationPresenter = new PopulationPresenter("age-at-death","age-at-death",populationReporter) ;
        //populationPresenter.plotAgeAtDeath();
        //PopulationPresenter populationPresenter = new PopulationPresenter("births per cycle","births per cycle",populationReporter) ;
        //populationPresenter.plotBirthsPerCycle();
        
        //SortReporter sortReporter = new SortReporter("Prep +ve infections per cycle",encounterReporter,populationReporter) ;
        //SortPresenter sortPresenter = new SortPresenter("Prep +ve infections per cycle","Infections per cycle",sortReporter) ;
        //sortPresenter.plotReceiveSortPrepStatusReport(TRUE);
        //RelationshipReporter relationshipReporter 
          //      = new RelationshipReporter("New Relationships per cycle",community.relationshipReport) ;
        //ScreeningReporter screeningReporter = new ScreeningReporter("prevalence",community.infectionReport) ;
        //SortReporter sortReporter = new SortReporter("prevalence",screeningReporter,relationshipReporter) ;
        //SortPresenter sortPresenter = new SortPresenter("prevalence","prevalence",sortReporter) ;
        //sortPresenter.plotSortPrevalence(10,3);
        
        //RelationshipReporter relationshipReporter 
          //      = new RelationshipReporter("Cumulative Relationships per age",community.relationshipReport) ;
        //PopulationReporter populationReporter = new PopulationReporter("age",community.populationReport) ;
        //SortReporter sortReporter = new SortReporter("Cumulative Relationships per age",relationshipReporter,populationReporter) ;
        //SortPresenter sortPresenter = new SortPresenter("age","nbRelationships",sortReporter) ;
        //sortPresenter.plotAgeNumberEnteredRelationshipRecord() ;
        
        ScreeningReporter screeningReporter = 
                new ScreeningReporter("prevalence",community.infectionReport) ;
        ScreeningPresenter screeningPresenter 
                = new ScreeningPresenter("prevalence",Community.NAME_ROOT,screeningReporter) ;
        screeningPresenter.plotPrevalence();
        
        //RelationshipReporter relationshipReporter 
          //      = new RelationshipReporter("New Relationships per cycle",community.relationshipReport) ;
        //RelationshipPresenter relationshipPresenter 
          //      = new RelationshipPresenter("New relationships per cycle","New relationships per cycle",relationshipReporter) ;
        //relationshipPresenter.plotNewRelationshipsPerCycle();
        //RelationshipReporter relationshipReporter 
          //      = new RelationshipReporter("Relationship breakups per cycle",community.relationshipReport) ;
        //RelationshipPresenter relationshipPresenter 
          //      = new RelationshipPresenter("Relationship breakups per cycle","Relationship breakups per cycle",relationshipReporter) ;
        //relationshipPresenter.plotBreakupsPerCycle();
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
        RelationshipReporter relationshipReporter 
                = new RelationshipReporter("Mean number of Relationships",community.relationshipReport) ;
        RelationshipPresenter relationshipPresenter 
                = new RelationshipPresenter("Mean number of Relationships",Community.NAME_ROOT,relationshipReporter) ;
        relationshipPresenter.plotMeanNumberRelationshipsReport();
        
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

    /**
     * For setting up specific questions. Code will flux dramatically.
     */
    private String initialiseCommunity()
    {
        
        // Clear all initial infections
        for (Agent agent : agents)
        {
            ((MSM) agent).setPrepStatus(false);
            //agent.clearInfection();
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
//        return "Population clear except for one RiskyMSM with asymptomatic Rectum infection." ;
        return "";
    }
    
    /**
     * For addressing specific questions. Code will flux dramatically.
     */
    private String interveneCommunity()
    {
        for (Agent agent : agents)
        {
            double prepProbability = ((MSM) agent).getProbabilityPrep() ;
            ((MSM) agent).reinitPrepStatus(true) ; // RAND.nextDouble() < prepProbability) ;
        }
        return "PrEP introduced" ;
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
            record += Reporter.addReportProperty("agentId",newAgent.getAgentId()) ;
            record += Reporter.addReportProperty("age",newAgent.getAge()) ; 
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
        int lowerAgentId ;
        int indexInteger ;

        // LOGGER.info("nb relationships: " + relationships.size());
        for (Agent agent : agents)
        {
            for (Relationship relationship : agent.getCurrentRelationships())
            {
                // WARNING: May cause future problems with hetero couples
                // Does agent have lower agentId than partner
                //indexInteger = (int) Math.pow(2, relationshipIndex);
                // Avoid checking relationship twice by accessing only through the 
                // agent with the lower agentId
                // TODO: Incorporate this into Agent.Method()
                //int agentId = agent.getAgentId() ;
                //int partnerId = relationship.getPartnerId(agentId) ;
                /*if (partnerId < agentId)
                        continue ;
                */
                //Relationship relationship = currentRelationships.get(relationshipIndex) ;
                //if ((indexInteger & lowerAgentId) != indexInteger)

                if (agent != relationship.getLowerIdAgent())
                    continue ;
                try
                {
                    if (RAND.nextDouble() < relationship.getEncounterProbability())
                        record += relationship.encounter() ;
                    //System.out.println(record);
                }
                catch (NoSuchMethodException nsme)
                {
                    LOGGER.info(nsme.getLocalizedMessage());
                    record += nsme.getCause(); //  .getMessage() ;
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
        boolean symptomatic ;
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
                if (infected)
                {
                    //LOGGER.info("screening agentId:"+String.valueOf(agent.getAgentId())) ;
                    record += Reporter.addReportProperty("agentId",agent.getAgentId()) ;
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
                // and returns boolean whether agent is cleared (!stillInfected)
                if (agent.progressInfection())
                {
                    record += Reporter.addReportLabel("cleared") ;
                    //LOGGER.info("cleared");
                }
                else if (agent.getSymptomatic())
                    if (agent.treatSymptomatic())  
                    {
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

    private void dump(String[] scribeNames)
    {
        try
        {
            for (String scribeName : scribeNames)
            {
                //scribeName = scribeName.substring(0,1).toUpperCase() 
                  //      + scribeName.substring(1) ;
                LOGGER.info(scribeName) ;
                String reportName = scribeName + "Report" ;
                ArrayList<String> report = (ArrayList<String>) Community.class.getDeclaredField(reportName).get(this) ;
                Scribe scribe = new Scribe(scribeName + Community.NAME_ROOT) ;
                scribe.writeReport(report);
                scribe.closeFile();
            }
        }
        catch ( Exception nsfe )
        {
            LOGGER.log(Level.SEVERE, "{0} {1}", new Object[] {nsfe.getLocalizedMessage(), nsfe.toString()});
        }
    }
    
    /**
     * Object to gather data and record it to Files
     */
    //public Reporter(String simName, ArrayList<String> generateReports, 

    //	ArrayList<String> encounterReports, ArrayList<String> clearReports, ArrayList<String> screenReports)
    private class Scribe{

        String extension = ".txt" ;
        // The number of Community cycles to pass between reports 
        int outputCycle ;

        String name ;

        String globalFolder = Community.FILE_PATH ;
        String fileName ;

        // File paths
        String logFilePath ;
        String errorFilePath ;
        String outputFilePath ;


        // File objects
        BufferedWriter fileWriter ;
        /*
        File logFile ;
        File errorFile ;
        File outputFile ;
        */

        private Scribe(String name) 
        {
            this.name = name ;
            fileName = name + extension ;
            outputFilePath = globalFolder + fileName ;
            try
            {
                fileWriter = new BufferedWriter(new FileWriter(outputFilePath,true));
            } //the true will append the new data
            catch ( Exception e )
            {
                LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
                closeFile() ;
            }
        }


        //outputCycle = 5 ;


        /*
        logFilePath = globalFolder + simName ;
        logFile = new File(logFilePath) ;

        errorFilePath = globalFolder + simName ;
        errorFile = new File(errorFilePath) ;
        */

        private void writeRecord(String record) throws Exception
        {
                fileWriter.write(record);
                fileWriter.newLine();
        }

        private void writeReport(ArrayList<String> report)
        {
            try
            {
                for (String record : report)
                    writeRecord(record) ;
            }
            catch ( Exception e )
            {
                LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
                closeFile() ;
            }
        }

        private void closeFile()
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

        private String getOutputFilePath()
        {
            return outputFilePath ;
        }

    }


}
