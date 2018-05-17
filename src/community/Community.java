/**
 * 
 */
package community;

import agent.* ;
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
    private ArrayList<Agent> agents = new ArrayList<Agent>() ;
    
    // Total number of agents, make larger once testing is complete!
    private int population = 1000 ;

    // Number of new population members and also average number of deaths per cycle
    private int birthRate = 2 ;

    // Current number of relationships in the community
    private int nbRelationships = 0; 

    static Random RAND = new Random() ;

    // List of indices in agents of available agents. Repetition indicates promiscuity
    //ArrayList<Integer> availableAgentsIds = new ArrayList<Integer>() ;

    //private ArrayList<Relationship> relationships = new ArrayList<Relationship>() ;

    // The number of years covered in the simulation
    private static int year = 0 ;
    // The number of cycles (days) since the current year began
    private static int cyclesModYear = 0 ; 

    private ArrayList<String> genericReport = new ArrayList<String>() ;
    private ArrayList<String> relationshipReport = new ArrayList<String>() ;
    private ArrayList<String> encounterReport = new ArrayList<String>() ;
    private ArrayList<String> clearReport = new ArrayList<String>() ;
    private ArrayList<String> screenReport = new ArrayList<String>() ;
    private ArrayList<String> censusReport = new ArrayList<String>() ;
    
    private Scribe encounterScribe ;  // = new Scribe() ;


    // Logger
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("reporter") ;

    public static void main(String[] args)
    {
        // Record starting time to measure running time
        long startTime = System.nanoTime() ;
        
        Community community = new Community() ;

        // For generating reports
        String relationshipRecord ;
        String encounterRecord ;
        String clearanceRecord ;
        String screenRecord ;
        String populationRecord ;
        //String deathReport ;
        
        // Use this to record the census at every cycle
        // String censusReport

        String cycleString ;
        

        // simulation of maxCycles cycles
        int maxCycles = 1000 ;
        String scribeName = "siteToSiteEncounterPopulation" + String.valueOf(community.population) ;
        scribeName += "Cycles" + String.valueOf(maxCycles) ;
        community.encounterScribe = community.new Scribe(scribeName) ;
        
        System.out.println("population: " + community.population + ", Cycles: " + maxCycles);
        for (int cycle = 0; cycle < maxCycles; cycle++)
        {	
            cycleString = Integer.toString(cycle) + "," ;
            
            LOGGER.log(Level.INFO, "Cycle no. {0}", cycleString);

            // update relationships and perform sexual encounters, report them
            relationshipRecord = cycleString + community.generateRelationships();
            encounterRecord = cycleString + community.runEncounters();
            clearanceRecord = cycleString + community.clearRelationships();

            // treat symptomatic agents
            screenRecord = cycleString + community.screenAgents(cycle) ;

            //deathRecord = cycleString
            populationRecord = cycleString + community.births() ;
            populationRecord += community.grimReaper() ;

            community.submitRecords(relationshipRecord,encounterRecord,
                    clearanceRecord,screenRecord,populationRecord) ;

            cyclesModYear++ ;
            if (cyclesModYear > 363)
            {
                year++ ;
                cyclesModYear = 0 ;
                populationRecord += community.ageOneYear() ;
            }
        }
        community.encounterScribe.closeFile();
        
        long elapsedTime = System.nanoTime() - startTime ;
        long milliTime = elapsedTime/1000000 ;
        int seconds = (int) milliTime/1000 ;
        int minutes = seconds/60 ;
        System.out.println("population: " + community.population + ", Cycles: " + maxCycles);
        System.out.println("Elapsed running time: " + milliTime + "millseconds") ;
        System.out.println("Elapsed running time: " + seconds + "seconds") ;
        System.out.println("Elapsed running time: " + minutes + "minutes") ;
        //EncounterReporter encounterReporter = new EncounterReporter("Agent to Agent",community.encounterReport) ;
        //EncounterPresenter encounterPresenter = new EncounterPresenter("Agent to Agent","agent to agent", encounterReporter) ;
        //encounterPresenter.plotTransmittingSites(new String[] {"Penis","Rectum","Pharynx"});
        //encounterPresenter.plotFromSiteToSite(new String[] {"Penis","Rectum","Pharynx"});
        //encounterPresenter.plotAgentToAgentNetwork();
        
        PopulationReporter censusReporter = new PopulationReporter("age-at-death",community.censusReport) ;
        PopulationPresenter censusPresenter = new PopulationPresenter("age-at-death","age-at-death",censusReporter) ;
        censusPresenter.plotAgeAtDeath();
        //PopulationPresenter censusPresenter = new PopulationPresenter("deaths per cycle","deaths per cycle",censusReporter) ;
        //censusPresenter.plotDeathsPerCycle();
        
        //ScreeningReporter screeningReporter = 
          //      new ScreeningReporter("prevalence",community.screenReport) ;
        //ScreeningPresenter screeningPresenter 
          //      = new ScreeningPresenter("prevalence","prevalence",screeningReporter) ;
        //screeningPresenter.plotPrevalence();
        
        //EncounterReporter partnersReporter = new EncounterReporter("testPairs",community.encounterReport) ;
        //Reporter partnersReporter = new Reporter("testPairs",community.generateReport,
                //	community.encounterReport,community.clearReport,community.screenReport) ;
        //ArrayList<HashMap<Integer,ArrayList<Integer>>> partnersReport = partnersReporter.preparePartnersReport() ;
        //System.out.println(partnersReport.get(0).get(0).size()) ; //  toString());
        //System.out.println(partnersReport.get(0).get(4).size()) ; // .toString());
    }

    /**
     * Community object containing all agent(s) and Relationships and methods 
     * for pairing agent(s) into relationship(s) and for ending relationship(s)
     * TODO: Generalise to include agents other than MSM.
     */
    public Community() {
        // Populate community with agents
        String agentReport = "" ;
        for (int id = 0 ; id < population ; id++ ) {
            //Class<?> AgentClazz = Class.forName("MSM") ; 
            //Agent newAgent = (Agent) AgentClazz.newInstance() ;
            //Constructor<?> agentConstructor = AgentClazz.getDeclaredConstructors()[0] ;

            // Call generateAgent to get randomly chosen combination of Agent subclasses
            MSM newAgent = generateAgent() ;  //new MSM(-1) ;
            agents.add(newAgent) ;

            // Record newAgent for later reporting
            agentReport += newAgent.getId() + ":" ;
            agentReport += newAgent.getAgent() + " " ;
        }
        //String relationshipRecord = generateRelationships() ;
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
                    if (agent1.getCurrentPartnerIds().contains(agent0.getId()))
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
            
            // TODO: Uncomment this line when ready to debug
            arrangeOrgies() ;
            
            return report ;
	}
        
        /**
         * Selects getOrgyNumber() groups of getOrgySize() Agents,
         * and invites them to join an orgy by calling Agent.joinOrgy() .
         * Those that return true enter a Casual Relationship with every other
         * participant.
         * TODO: Arrange for contacts in arbitrary order
         * TODO: Enable by calling from generateRelationships()
         * @return 
         */
        private String arrangeOrgies()
        {
            String report = "" ;
            Agent sampleAgent = agents.get(0) ;
            int orgySize = sampleAgent.getOrgySize() ;
            int orgyNumber = sampleAgent.getOrgyNumber() ;
            double joinOrgyProbability = sampleAgent.getJoinOrgyProbability() ;
            Object[] invitedAgents ;
                
            for (int orgyIndex = 0 ; orgyIndex < orgyNumber ; orgyIndex++)
            {
                // Invite next orgySize Agents to an orgy
                int startIndex = orgyIndex * orgySize ;
                int endIndex = startIndex + orgySize ;
                // Check that required number of Agents is not too much (unlikely)
                if (endIndex > agents.size())
                    break ;
                
                // Invited Agents subArrayList of agents
                invitedAgents = agents.subList(startIndex, endIndex).toArray();
                ArrayList<Agent> orgyAgents = new ArrayList<Agent>() ;
                for (Object agent : invitedAgents)
                {
                    if (((Agent) agent).joinOrgy(joinOrgyProbability,null))
                        orgyAgents.add((Agent) agent) ;    // Agent agrees to join orgy
                }
                //Require orgies have at least three participants
                if (orgyAgents.size() < 3)
                    continue ;
                
                // Every Agent in a given orgy has a Casual Relationship with
                //evey other Agent at that orgy
                //FIXME: Sexual contacts between pairs are clustered
                for (Agent agent0 : orgyAgents)
                {
                    for (Agent agent1 : orgyAgents)
                    {
                        if (agent0 == agent1)
                            continue ;    // Require two distinct Agents in a Relationship
                        Casual relationship = new Casual(agent0, agent1);
                        nbRelationships++ ;

                        report += agent0.enterCasual(relationship) ;
                        agent1.enterCasual(relationship) ;
                        report += "orgy:" + String.valueOf(orgyIndex) + " " ;
                    }
                }
            }
            return report ;
        }
        
        private String ageOneYear()
        {
            String report = "" ;
            String agentReport ;
            int nbAgentRelationships ;
            for (Agent agent : agents)
            {
                nbAgentRelationships = agent.getCurrentRelationships().size() ;
                agentReport = agent.ageOneYear() ;
                if (agentReport.contains("death"))
                    nbRelationships -= nbAgentRelationships ;
                report += agentReport ;
            }
            return report ;
        }
        
        /**
         * TODO: Implement generalised getCensusData() in Agent and subtypes
         * @return String giving each Agent and properties of interest to census
         */
        private String getCensus()
        {
            String report = "" ;
            String agentIdString ;
            String agentType ;
            String age ;
            for (Agent agent : agents )
            {
                report += agent.getCensusReport() ;
            }
            return report ;
        }


	/**
	 * Handles introduction of new Agents every cycle
	 * Agents are 'born' to the simulation at 16-20 years old
	 * @return String of agentId for each agent born
	 */
	private String births()
	{
            String report = "birth:" ;
            int currentPopulation = agents.size() ;
            for (int birth = 0 ; birth < birthRate ; birth++ )
            {
                MSM newAgent = MSM.birthMSM(0) ;
                agents.add(newAgent) ;
                report += "agentId:" + Integer.toString(newAgent.getId()) + " " ;
                report += "startAge:" + Integer.toString(newAgent.getAge()) + " " ; 
                currentPopulation++ ;
            }
            report += Integer.toString(currentPopulation) + " " ;
                

            return report ;
	}

        /**
         * Chooses number of Agents who might die. If all die and the population
         * has increased then the number of agents is the same as the original 
         * population. If the number of agents is within birthRate of the original
         * population size then the population might dip lower.
         * @return String report of agentIds who died and their age-at-death
         */
	private String grimReaper()
	{
            String report = "" ;
            int deaths = (agents.size() - population) ;
            if (deaths < birthRate)
                deaths = birthRate ;
            for (int death = 0 ; death < deaths ; death++)
            {
                int agentInd = RAND.nextInt(agents.size()) ;
                Agent agent = agents.get(agentInd) ; 
                if (agent.grimReaper())
                {
                    nbRelationships-= agent.getCurrentRelationships().size() ;
                    agents.remove(agent) ;
                    report += Reporter.addReportProperty("agentId", agent.getId()) ;
                    report += Reporter.addReportProperty("age", agent.getAge()) ;
                }
            }
            //prepare report
            report = "death:" + report ;
            

            return report ;
	}
	
        /**
         * For each Agent, for each Relationship for which they are the partner with 
         * the lower agentID, call Relationship.encounter()
         * @return String report of agentIds in each Relationship and the 
         * description of each sexual contact returned by Relationship.encounter()
         */
	private String runEncounters()
	{
            String report = "" ;
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
                    //int agentId = agent.getId() ;
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
                        report += relationship.encounter() ;
                        //System.out.println(report);
                    }
                    catch (NoSuchMethodException nsme)
                    {
                        LOGGER.info(nsme.getLocalizedMessage());
                        report += nsme.getCause(); //  .getMessage() ;
                    }
                    catch (InvocationTargetException ite)
                    {
                        LOGGER.info(ite.getLocalizedMessage());
                        //report += ite.getMessage() ;
                    }
                    catch (IllegalAccessException iae)
                    {
                        LOGGER.info(iae.getLocalizedMessage());
                        report += iae.getMessage() ;
                    }
                }
            }
            return report ;
	}
	
	/**
	 * loop through relationships and probabilistically choose to end them
	 * @return number of relationships before and after
	 */
	private String clearRelationships() 
	{
            String report = "" ;
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
                    //int agentId = agent.getId() ;
                    if (agent == relationship.getLowerIdAgent())
                        endRelationship(relationship) ;
                    //LOGGER.log(Level.INFO, "nbRelationships: {0}", new Object[]{nbRelationships});
                    
                }
            }
            return report ;
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
                return relationship.getReport() ;
            }
            return "" ;
	}
	
	/**
	 * Check every agent and treat those who are symptomatic
	 */
	private String screenAgents(int cycle)
	{
            String report = "" ;
            boolean infected ;
            boolean symptomatic ;
                    
            for (Agent agent : agents)
            {
                report += Reporter.addReportProperty("agentId",agent.getId()) ;
                infected = agent.getInfectedStatus();
                report += Reporter.addReportProperty("infected", infected) ;
                if (infected)
                {
                    symptomatic = agent.getSymptomatic();
                    report += Reporter.addReportProperty("symptomatic", symptomatic) ;
                    if (symptomatic)
                    {
                            report += Reporter.addReportProperty("treatment",agent.treat()) ;
                    }
                    else if (RAND.nextDouble() < agent.getScreenProbability(new String[] {Integer.toString(cycle)})) 
                    {
                            report += Reporter.addReportProperty("treatment",agent.treat()) ;
                    }
                }
            }
            return report ;
	}
	
	private void submitRecords(String generateRecord, String encounterRecord, String clearRecord, String screenRecord, String populationRecord)
	{
		relationshipReport.add(generateRecord) ;
		//LOGGER.info(encounterRecord);
		encounterReport.add(encounterRecord) ;
                encounterScribe.writeRecord(encounterRecord);
                
		clearReport.add(clearRecord) ;
		screenReport.add(screenRecord) ;
                censusReport.add(populationRecord) ;
	}
        
        /**
         * Object to gather data and record it to Files
         */
        //public Reporter(String simName, ArrayList<String> generateReports, 

        //	ArrayList<String> encounterReports, ArrayList<String> clearReports, ArrayList<String> screenReports)
        private class Scribe{

            // The number of Community cycles to pass between reports 
            int outputCycle ;

            String name ;

            String globalFolder = "../output/test/" ;
            String fileName = ".txt" ;

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
                fileName = name + fileName ;
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

        private void writeRecord(String record)
        {
            try
            {
                fileWriter.write(record);
                fileWriter.newLine();
            }//appends the string to the file
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
