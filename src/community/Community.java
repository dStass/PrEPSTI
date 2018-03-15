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
import java.util.List;

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

	private ArrayList<String> generateReports = new ArrayList<String>() ;
	private ArrayList<String> encounterReports = new ArrayList<String>() ;
	private ArrayList<String> clearReports = new ArrayList<String>() ;
	private ArrayList<String> screenReports = new ArrayList<String>() ;
	

	// Logger
	java.util.logging.Logger logger = java.util.logging.Logger.getLogger("reporter") ;
	
	public static void main(String[] args)
	{
		Community community = new Community() ;
		
		// For generating reports
		String generateReport ;
		String encounterReport ;
		String clearanceReport ;
		String screenReport ;
		String birthReport ;
		String deathReport ;
		
		String cycleString ;
		
		// simulation of maxCycles cycles
		int maxCycles = 10 ;
		for (int cycle = 0; cycle < maxCycles; cycle++)
		{	
			cycleString = Integer.toString(cycle) + "," ;
			
			// update relationships and perform sexual encounters, report them
			generateReport = cycleString + community.generateRelationships();
			encounterReport = cycleString + community.runEncounters();
			clearanceReport = cycleString + community.clearRelationships();
			
			// treat symptomatic agents
			screenReport = cycleString + community.screenAgents(cycle) ;
			
			birthReport = cycleString + community.births() ;
			deathReport = cycleString + community.grimReaper() ;
			
			community.submitReports(generateReport,encounterReport,clearanceReport,screenReport) ;
			
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
		for (int id = 0 ; id < population ; id++ ) {
			//Class<?> AgentClazz = Class.forName("MSM") ; 
			//Agent newAgent = (Agent) AgentClazz.newInstance() ;
			//Constructor<?> agentConstructor = AgentClazz.getDeclaredConstructors()[0] ;
			//newAgent = (Agent) agentConstructor.newInstance(id) ; 
			MSM newAgent = new MSM(-1) ;
			agents.add(newAgent) ;
		}
		String relationshipReport = generateRelationships() ;
		return ;
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
			String[] consentArgs = {relationshipClazzName} ;
			if (agent0.consent(consentArgs) && agent1.consent(consentArgs))
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
                    logger.info(nsme.getLocalizedMessage());
                }
                catch ( ClassNotFoundException cnfe )
                {
                    logger.info(cnfe.getLocalizedMessage());
                }
		catch ( IllegalAccessException iae )
                {
                    logger.info(iae.getMessage());
                }
                catch ( InstantiationException ie)
                {
                    logger.info(ie.getLocalizedMessage()) ;
                }
                catch ( InvocationTargetException ite )
                {
                    logger.info(ite.getLocalizedMessage());
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
		String report = "born:" ;
		int currentPopulation = agents.size() ;
		for (int birth = 0 ; birth < birthRate ; birth++ )
		{
			agents.add(new MSM(0)) ;
		    report += Integer.toString(currentPopulation) + " " ;
			currentPopulation++ ;
		}
		
		return report ;
	}

	private String grimReaper()
	{
		String report = "" ;
		int deaths = (agents.size() - population) ;
		if (deaths < birthRate)
			deaths = birthRate ;
		for (int death = 0 ; death < birthRate ; death++)
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
	
	private boolean grimReaper(Agent agent)
	{
		if (agent.death())
		{
			agents.remove((Object) agent) ;
			return true ;
		}
		return false ;
	}
	
	protected String runEncounters()
	{
		String report = "" ;
		// logger.info("nb relationships: " + relationships.size());
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
					com.sun.media.jfxmedia.logging.Logger.logMsg(0, nsme.getLocalizedMessage());
					report += nsme.getCause(); //  .getMessage() ;
				}
				catch (InvocationTargetException ite)
				{
					com.sun.media.jfxmedia.logging.Logger.logMsg(0, ite.getLocalizedMessage());
					//report += ite.getMessage() ;
				}
				catch (IllegalAccessException iae)
				{
					com.sun.media.jfxmedia.logging.Logger.logMsg(0, iae.getLocalizedMessage());
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
		generateReports.add(generateReport) ;
		//logger.info(encounterReport);
		encounterReports.add(encounterReport) ;
		clearReports.add(clearReport) ;
		screenReports.add(screenReport) ;
		return ;
	}
}
