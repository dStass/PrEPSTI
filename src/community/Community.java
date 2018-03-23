/**
 * 
 */
package community;

import agent.* ;
import reporter.* ; 

import java.util.Random;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.jfree.chart.* ;

/******************************************************************
 * @author Michael Luke Walker
 *
 *******************************************************************/
public class Community {
    private ArrayList<Agent> agents = new ArrayList<Agent>() ;
    
    // Total number of agents, make larger once testing is complete!
    private int population = 100;

    // Number of new population members and also average number of deaths per year
    private int birthRate = 1 ;

    // Current number of relationships in the community
    private int nbRelationships = 0; 

    // Maximum number of allowed relationships
    private int maxRelationships = population ;    //* (population - 1))/2 ;

    static Random rand = new Random() ;

    // List of indices in agents of available agents. Repetition indicates promiscuity
    //ArrayList<Integer> availableAgentsIds = new ArrayList<Integer>() ;

    //private ArrayList<Relationship> relationships = new ArrayList<Relationship>() ;

    // The number of years covered in the simulation
    private static int year = 0 ;
    // The number of cycles (days) since the current year began
    private static int cyclesModYear = 0 ; 

    private ArrayList<String> Reports = new ArrayList<String>() ;
    private ArrayList<String> relationshipReports = new ArrayList<String>() ;
    private ArrayList<String> encounterReports = new ArrayList<String>() ;
    private ArrayList<String> clearReports = new ArrayList<String>() ;
    private ArrayList<String> screenReports = new ArrayList<String>() ;
    private ArrayList<String> CensusReports = new ArrayList<String>() ;
    


    // Logger
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("reporter") ;

    public static void main(String[] args)
    {
        Community community = new Community() ;

        // For generating reports
        String relationshipReport ;
        String encounterReport ;
        String clearanceReport ;
        String screenReport ;
        String populationReport ;
        //String deathReport ;
        
        // Use this to record the census at every cycle
        // String censusReport

        String cycleString ;

        // simulation of maxCycles cycles
        int maxCycles = 10 ;
        for (int cycle = 0; cycle < maxCycles; cycle++)
        {	
            cycleString = Integer.toString(cycle) + "," ;

            // update relationships and perform sexual encounters, report them
            relationshipReport = cycleString + community.generateRelationships();
            encounterReport = cycleString + community.runEncounters();
            clearanceReport = cycleString + community.clearRelationships();

            // treat symptomatic agents
            screenReport = cycleString + community.screenAgents(cycle) ;

            populationReport = cycleString + community.births() ;
            //deathReport = cycleString
            populationReport += community.grimReaper() ;

            community.submitReports(relationshipReport,encounterReport,clearanceReport,screenReport) ;

            cyclesModYear++ ;
            if (cyclesModYear > 363)
            {
                year++ ;
                cyclesModYear = 0 ;
                for (Agent agent : community.agents)
                {
                    agent.ageOneYear() ;
                }
            }
        }
        EncounterReporter partnersReporter = new EncounterReporter("testPairs",community.encounterReports) ;
        //Reporter partnersReporter = new Reporter("testPairs",community.generateReports,
                //	community.encounterReports,community.clearReports,community.screenReports) ;
        ArrayList<HashMap<Integer,ArrayList<Integer>>> partnersReport = partnersReporter.preparePartnersReport() ;
        System.out.println(partnersReport.get(0).get(0).size()) ; //  toString());
        System.out.println(partnersReport.get(0).get(4).size()) ; // .toString());
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
        String relationshipReport = generateRelationships() ;
        return ;
    }

        /**
         * Calls chooseMSM() to decide which MSM are RiskyMSM and safeMSM.
         * TODO: Generalise to more general types of Agent
         * @return MSM newAgent OR new MSM() if error occurs
         */
        private MSM generateAgent()
        {
            String agentClazzName = MSM.chooseMSM() ;
            try
            {
	    //Class<?> agentClazz = Class.forName(agentClazzName) ;
            MSM newAgent = (MSM) Class.forName(agentClazzName).getConstructor(int.class).newInstance(-1);
            return newAgent ;
            }
            catch ( NoSuchMethodException nsme )
            {
                LOGGER.info(nsme.getLocalizedMessage());
            }
            catch ( InstantiationException ie )
            {
                LOGGER.info(ie.getLocalizedMessage());
            }
            catch ( IllegalAccessException iae )
            {
                LOGGER.info(iae.getLocalizedMessage());
            }
            catch ( InvocationTargetException ite )
            {
                LOGGER.info(ite.getLocalizedMessage());
            }
            catch ( ClassNotFoundException cnfe )
            {
                LOGGER.info(cnfe.getLocalizedMessage());
            }
            return new MSM(-1) ;
        }
	
	/**
	 * Generate relationships within the community. 
	 * The agents are shuffled and then split in half, with the first
	 * half in relationship proposals with the second half.
         * 
         * @return (String) report of Relationships generated
	 */
	protected String generateRelationships()
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
                    String relationshipClazzName = Relationship.chooseRelationship(agent0, agent1) ;

                    // Argument String[] for Agent.consent 
                    if (agent0.consent(relationshipClazzName,agent1) && agent1.consent(relationshipClazzName,agent0))
                    {
                        Class<?> relationshipClazz = Class.forName(relationshipClazzName) ;
                        String enterMethodName = "enter" + relationshipClazzName ;
                        Method enterRelationshipMethod = relationshipClazz.getMethod(enterMethodName, Relationship.class ) ;

                        Relationship relationship = (Relationship) relationshipClazz.newInstance();
                        //Relationship relationship = (Relationship) relationshipClazz.getConstructor().newInstance();
                        relationship.addAgents(agent0, agent1);
                        nbRelationships++ ;

                        enterRelationshipMethod.invoke(agent0, relationship) ;
                        enterRelationshipMethod.invoke(agent1, relationship) ;

                        // report contains relationship subclass and agentIds
                        report += agent0.enterRelationship(relationship) ;
                        agent1.enterRelationship(relationship) ;

                        // report += relationship.getReport() + " " ;

                    }
		}
            }
            catch ( NoSuchMethodException nsme )
            {
                LOGGER.info(nsme.getLocalizedMessage());
            }
            catch ( ClassNotFoundException cnfe )
            {
                LOGGER.info(cnfe.getLocalizedMessage());
            }
            catch ( IllegalAccessException iae )
            {
                LOGGER.info(iae.getMessage());
            }
            catch ( InstantiationException ie)
            {
                LOGGER.info(ie.getLocalizedMessage()) ;
            }
            catch ( InvocationTargetException ite )
            {
                LOGGER.info(ite.getLocalizedMessage());
            }

            return report ;
	}
        
        /**
         * TODO: Implement generalised getCensusData() in Agent and subtypes
         * @return String giving each Agent and properties of interest to census
         */
        protected String getCensus()
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
                MSM newAgent = new MSM(0) ;
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
                int agentInd = rand.nextInt(agents.size()) ;
                Agent agent = agents.get(agentInd) ; 
                if (grimReaper(agent))
                {
                    report += "agentId:" + Integer.toString(agent.getId()) + " " ;
                    report += "age:" + Integer.toString(agent.getAge()) + " " ;
                }
            }
            //prepare report
            if (! report.isEmpty())
                report = "death:" + report ;

            return report ;
	}
	
        /**
         * Calls agent.death() to see if they die and removes them from agents
         * if so
         * @param agent
         * @return true if agent dies and false otherwise
         */
	private boolean grimReaper(Agent agent)
	{
		if (agent.death())
		{
			agents.remove((Object) agent) ;
			return true ;
		}
		return false ;
	}
	
        /**
         * For each Agent, for each Relationship for which they are the partner with 
         * the lower agentID, call Relationship.encounter()
         * @return String report of agentIds in each Relationship and the 
         * description of each sexual contact returned by Relationship.encounter()
         */
	protected String runEncounters()
	{
		String report = "" ;
		// LOGGER.info("nb relationships: " + relationships.size());
		for (Agent agent : agents)
		{
			// Might this agent have the lower agentId
			// TODO: Improve precision
			if (agent.getLowerAgentId() == 0)
				continue ;
			
			for (Relationship relationship : agent.getCurrentRelationships())
			{
				// Avoid checking relationship twice by accessing only through the 
				// agent with the lower agentId
				// TODO: Incorporate this into Agent.Method()
				int agentId = agent.getId() ;
				int partnerId = relationship.getPartnerId(agentId) ;
				if (partnerId < agentId)
					continue ;
				try
				{
					
					//Agent[] agents = relationship.getAgents() ;
					// TODO: Perhaps replace "agentId0/1" with "agentIds"
					report += "agentId0:" + Integer.toString(agentId) + " " ;
					report += "agentId1:" + Integer.toString(partnerId) + " " ;
					report += relationship.encounter() ;
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
	protected String clearRelationships() 
	{
		String report = "" ;
		
		for (Agent agent : agents)
		{
			// Skip agents who can't end relationships
			if (agent.getLowerAgentId() == 0)
				continue ;
			
			for (Relationship relationship : agent.getCurrentRelationships())
			{
				// Avoid checking relationship twice
				int agentId = agent.getId() ;
				if (agentId < relationship.getPartnerId(agentId))
					endRelationship(relationship) ;
			}
		}

		return report ;
	}

	/*****************************************************************
	 * Calls breakup() of Relationship relationship, which probabilitically
	 * calls Relationship.del() 
	 * @param relationship
	 *****************************************************************/
	private boolean endRelationship(Relationship relationship) 
	{
		return relationship.breakup() ;
	}
	
	/**
	 * Check every agent and treat those who are symptomatic
	 */
	private String screenAgents(int cycle)
	{
		String report = "" ;
		for (Agent agent : agents)
		{
			report += "agentId:" + Integer.toString(agent.getId()) + " " ;
			boolean symptomatic = agent.getSymptomatic() ;
			report += "symptomatic:" + Boolean.toString(symptomatic) + " " ;
			if (symptomatic)
			{
				report += "treatment:" + Boolean.toString(agent.treat()) ;
			}
			else if (agent.getScreenProbability(new String[] {Integer.toString(cycle)}) < rand.nextDouble()) 
			{
				report += "treatment:" + Boolean.toString(agent.treat()) ;
			}
		}
		return report ;
	}
	
	private void submitReports(String generateReport, String encounterReport, String clearReport, String screenReport)
	{
		relationshipReports.add(generateReport) ;
		//LOGGER.info(encounterReport);
		encounterReports.add(encounterReport) ;
		clearReports.add(clearReport) ;
		screenReports.add(screenReport) ;
		return ;
	}
}
