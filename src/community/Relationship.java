package community;

import agent.* ;
import site.* ;

import java.util.Random ;
//import java.util.logging.Logger;
import java.lang.reflect.*;
import java.util.logging.Level;

/*********************************************************************
 * Defines class Relationship with two Agent objects and probabilities
 * and Methods for encounter() and breakup().
 * @author MichaelWalker
 *
 ****************************************/
public class Relationship {
	// Two people are required to form the relationship
    private Agent agent0 ;	
    private Agent agent1 ;
    
    // To identify relationship subclass, see constructor
    private String relationship ;
    
    // Random number generator
    static Random rand = new Random() ;
    
    // TODO: Move condom variables to MSM
    // Probability of using a condom
    static double CONDOM_USE = 0.5;
    
    // Protective effect of condom
    static final double CONDOM_EFFECT = 0.9 ;
    
    // Number of sexual contacts per cycle ;
    //private int contacts ;
    
    // Probability of breakup() in a given cycle
    static double breakupProbability ;
    
    //LOGGER
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("relationship") ;

    /**
     * Randomly chooses one of the available Relationship subclasses. 
     * The odds of each subclass are the mean of the corresponding odds for each agent 
     * @param agent0
     * @param agent1
     * @return relationshipSubclass.class
     */
    static Class chooseRelationship(Agent agent0, Agent agent1 )
    {
    	int monogomousOdds = agent0.getMonogomousOdds() + agent1.getMonogomousOdds() ;
    	int regularOdds = agent0.getRegularOdds() + agent1.getRegularOdds() ;
    	int casualOdds = agent0.getCasualOdds() + agent1.getCasualOdds() ;
    	int totalOdds = monogomousOdds + regularOdds + casualOdds ;
    	
        int choice = rand.nextInt(totalOdds) ;
        if (choice < monogomousOdds)
    		return Monogomous.class ;
    	if (choice < (monogomousOdds + regularOdds))
    		return Regular.class ;
    	return Casual.class ;
    }
   
    public Relationship()
    {
    	Class<?> clazz = this.getClass() ;
    	relationship = clazz.asSubclass(clazz).getSimpleName() ;
    }
    
    public Relationship(Agent agent0, Agent agent1) {
    	addAgents(agent0, agent1) ;
    	Class<?> clazz = this.getClass() ;
    	relationship = clazz.asSubclass(clazz).getName() ;
    }
    
    /**
     * Adds Agents to Relationship and establishes which has the lower AgentId
     * Arrange that agent0 should always have the lower agentId
     * @param agent0
     * @param agent1
     */
    final protected String addAgents(Agent agent0, Agent agent1)
    {
        if (agent0.getId() < agent1.getId())
        {
    	this.agent0 = agent0 ;
    	this.agent1 = agent1 ;
        	agent0.augmentLowerAgentId() ;
    	}
    	else 
        {
    	this.agent1 = agent0 ;
    	this.agent0 = agent1 ;
        	agent1.augmentLowerAgentId() ;
        }
        
        agent1.enterRelationship(this) ;
        return agent0.enterRelationship(this) ;
    }
    
    /**
     * When an Agent needs to know who their partner is
     * @param agentId
     * @return (int) agentId's partner's Id 
     */
    public int getPartnerId(int agentId)
    {
    	if (agent0.getId() == agentId)
    		return agent1.getId() ;
    	return agent0.getId() ;
    }
    
    /**
     * When an Agent needs their 
     * @param agent - calling Agent
     * @return (Agent) agent's partner 
     */
    public Agent getPartner(Agent agent)
    {
    	if (agent0 == agent)
    		return agent1 ;
    	return agent0 ;
    }
    
    /*********************************************************************
     * Probabilistically ends relationship ends on this cycle by choosing a 
     * random double between 0.0 and 1.0 
     * @return True if less than breakupProbability, False otherwise 
     *********************************************************************/
    protected boolean breakup() 
    {
        LOGGER.log(Level.INFO, String.valueOf(breakupProbability) );
    	if (rand.nextDouble() < breakupProbability)
            {
                agent0.endRelationship(this) ;  //(agent1.getId()) ;
                //agent1.leaveRelationship(this) ;  //(agent0.getId()) ;
                return true ;
            }
		
    	return false;
    }

    
    protected String encounter() throws NoSuchMethodException, InvocationTargetException,
    IllegalAccessException
    {
    	String report = "" ;
    	int contacts = chooseNbContacts() ;
    	
    	// Initialising Agents and corresponding Sites 
    	
    	for (int contact= 0; contact < contacts; contact++)
    	{
    		//Class<?> agentClazz = agent0.getClass() ; //.asSubclass(agent0.getClass()) ;
    	
    		// TODO: Generalise to arbitrary Agent subClasses
    		//Method siteMethod = MSM.class.getMethod("chooseSites", Agent.class, Agent.class) ;
			//Site[] sites = (Site[]) siteMethod.invoke(agent0,agent1) ;
    		Site[] sites = MSM.chooseSites(agent0, agent1) ;
			Site site0 = sites[0] ;
			Site site1 = sites[1] ;
    		int infectStatus0 = site0.getInfectStatus() ;
	    	int infectStatus1 = site1.getInfectStatus() ;
    		
	    	// Update report
	    	report += "contact:" + Integer.toString(contact) + " " ;
	    	report += site0.getSite() + ":" + Integer.toString(infectStatus0) + " " ;
	    	report += site1.getSite() + ":" + Integer.toString(infectStatus1) + " " ;
	    	// compare Infection status of both sites
	    	/*
	    	Infection infection0 = site0.getInfection();
	    	Infection infection1 = site1.getInfection();
	    	
	    	String infectName0 = infection0.getClass().getName(); 
	    	String infectName1 = infection1.getClass().getName(); 
	    	*/
	    			
	    	// no risk of transmission if both sites have same infectStatus
	    	//if (infectName0.equals(infectName1))
	    	if (infectStatus0 == infectStatus1) continue ;	
	    	
	    	// Choose whether condom is used, if any Penis Sites
	    	double infectProbability = 1.0 ;
	    	if ("Penis".equals(site0.getSite()) || "Penis".equals(site1.getSite()))
	    	{
	    		report += "condom:" ;
	    		if (rand.nextDouble() < CONDOM_USE)
	    		{
	    			infectProbability= 1.0 - CONDOM_EFFECT ;
		    		report += "true " ;
	    		}
	    		else report += "false " ;
	    	}
	    		
	    	// call static getInfectProbability
	    	report += "transmission:" ;
	    	//TODO: Generalise to other subclasses of Agent
	    	//Method getInfectionMethod = agentClazz.getMethod("getInfectProbability", Agent.class,
    			//	Agent.class, int.class, Site.class, Site.class ) ;
	    	// Method getInfectionMethod = Agent.class.getMethod("getInfectProbability", Agent.class,
    			//	Agent.class, int.class, Site.class, Site.class ) ;
	    	if (infectStatus0 != 0)
	    	{
	    		infectProbability*= MSM.getInfectProbability(agent0, agent1, infectStatus0, site0, site1) ; 
	    				//(double) getInfectionMethod.
	    				//invoke(agent0,agent1,infectStatus0,site0,site1) ;
	    		
		    	// Probabilistically transmit infection to site1
	    		//site1.receive(infectName0,transmit0) ;
	    		report += Boolean.toString(agent1.receiveInfection(infectProbability,site1)) ;	    	
	    	}
	    	else    // agent1 must be infected
	    	{
	    		infectProbability*= MSM.getInfectProbability(agent1, agent0, infectStatus1, site1, site0) ;
            	//infectProbability*= (double) getInfectionMethod.
	    			//	invoke(agent1,agent0,infectStatus1,site1,site0) ;
	    		
		    	// Probabilistically transmit infection to site0
	    		//site0.receive(infectName0,transmit0) ;
            	report += Boolean.toString(agent0.receiveInfection(infectProbability,site0)) ;	    	
	    	}
	    	report += " " ;
    	}
    	return report ;   	
    
    }
    
    private int chooseNbContacts()
    {
    	return rand.nextInt(3) + 1 ;
    }
    
    protected Agent[] getAgents()
    {
    	return new Agent[] {agent0,agent1} ;
    }
    
    public Agent getLowerIdAgent()
    {
        return agent0 ;
    }
    
    public String getReport()
    {
    	String report = getRelationship() + ":" ;
    	report += "agentId0:" + Integer.toString(agent0.getId()) + " " ;
    	report += "agentId1:" + Integer.toString(agent1.getId()) + " " ;
    	return report ;
    }

    /**
     * 
     * @return (String) name of Relationship.subClass of relationship
     */
    public String getRelationship()
    {
    	return relationship ;
    }
}
