package community;

import agent.* ;
import site.* ;
import reporter.Reporter ;


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
    
    private int relationshipId ;
    
    /** Simple name of relationship subclass (see constructor). */
    private String relationship ;
    
    static public String RELATIONSHIP_ID = "relationshipId" ;
    
    /** Random number generator. */
    static Random RAND = new Random() ;
    
    /** Site name of Rectum */
    static String RECTUM = "Rectum" ;
    /** Site name of Rectum */
    static String URETHRA = "Urethra" ;
    /** Site name of Rectum */
    static String PHARYNX = "Pharynx" ;
    
    static public String DEATH_RECORD = "death:" ;
    
    static public String BURNIN_COMMENCE = "clear:" ;
    static public String BURNIN_BREAKUP = "" ;
    
    static public void APPEND_DEATH_RECORD(String record)
    {
        DEATH_RECORD += record ;
    }
    
    static public String READ_DEATH_RECORD()
    {
        String output ;
        output = DEATH_RECORD ;
        DEATH_RECORD = "death:" ;
        return output ;
    }
    
    
    /** Current number of relationships in the simulation */
    static int NB_RELATIONSHIPS = 0; 

    /** Total number of relationships created in the simulation */
    static int NB_RELATIONSHIPS_CREATED = 0; 

    /** Probability of sexual encounter within Relationship in any cycle */
    static double ENCOUNTER_PROBABILITY = 0.5 ;
    
    /** One less than the maximum number of contacts allowed in a sexual encounter. */
    static int MAXIMUM_CONTACTS = 4 ;
    
    // TODO: Move condom variables to STI
    // Probability of using a condom for couplings with a Site.Urethra
    static double CONDOM_USE = 0.5;
    
    // Protective effect of condom
    static final double CONDOM_EFFECT = 0.60 ;
    
    // Number of sexual contacts per cycle ;
    //private int contacts ;
    
    // Probability of breakup() in a given cycle. This value chosen for debugging
    static double BREAKUP_PROBABILITY = -1.0 ;
    
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
    	double monogomousOdds = agent0.getMonogomousOdds() + agent1.getMonogomousOdds() ;
    	double regularOdds = agent0.getRegularOdds() + agent1.getRegularOdds() ;
    	double casualOdds = agent0.getCasualOdds() + agent1.getCasualOdds() ;
    	double totalOdds = monogomousOdds + regularOdds + casualOdds ;
    	double choice = RAND.nextDouble() * totalOdds ;
        if (choice < monogomousOdds)
    		return Monogomous.class ;
    	if (choice < (monogomousOdds + regularOdds))
    		return Regular.class ;
    	return Casual.class ;
    }
    
    /**
     * Decrements NB_RELATIONSHIPS by one. Called by Agent.leaveRelationship().
     */
    static public void diminishNbRelationships()
    {
        NB_RELATIONSHIPS-- ;
    }
   
    public Relationship()
    {
        NB_RELATIONSHIPS++ ;
        this.relationshipId = NB_RELATIONSHIPS_CREATED ;
        NB_RELATIONSHIPS_CREATED++ ;
    	Class<?> clazz = this.getClass() ;
    	relationship = clazz.asSubclass(clazz).getSimpleName() ;
    }
    
    public Relationship(Agent agent0, Agent agent1) {
    	NB_RELATIONSHIPS++ ;
        this.relationshipId = NB_RELATIONSHIPS_CREATED ;
        NB_RELATIONSHIPS_CREATED++ ;
        addAgents(agent0, agent1) ;
    	Class<?> clazz = this.getClass() ;
    	relationship = clazz.asSubclass(clazz).getSimpleName() ;
    }
    
    final public int getRelationshipId()
    {
        return relationshipId ;
    }
    
    /**
     * Adds Agents to Relationship and establishes which has the lower AgentId
     * Arrange that agent0 should always have the lower agentId
     * @param agent0
     * @param agent1
     */
    final protected String addAgents(Agent agent0, Agent agent1)
    {
        String report = "" ;
        this.agent0 = agent0 ;
    	this.agent1 = agent1 ;
    	
        this.agent1.enterRelationship(this) ;
        report += this.agent0.enterRelationship(this) ;
        //this.agent0.augmentLowerAgentId() ;
        
        return report ;
    }
    
    /**
     * When an Agent needs to know who their partner is
     * @param agentId
     * @return (int) agentId's partner's Id 
     */
    final public int getPartnerId(int agentId)
    {
    	if (agent0.getAgentId() == agentId)
    		return agent1.getAgentId() ;
    	return agent0.getAgentId() ;
    }
    
    /**
     * When an Agent needs their 
     * @param agent - calling Agent
     * @return (Agent) agent's partner 
     */
    final public Agent getPartner(Agent agent)
    {
    	if (agent0 == agent)
    		return agent1 ;
    	return agent0 ;
    }
    
    /**
     * The probability of any sexual contact in any cycle.
     * @return 
     */
    protected double getEncounterProbability()
    {
        return ENCOUNTER_PROBABILITY ;
    }
    
    /*********************************************************************
     * Probabilistically ends relationship on this cycle by choosing a 
     * random double between 0.0 and 1.0 
     * @return True if less than breakupProbability, False otherwise 
     *********************************************************************/
    protected boolean breakup()
    {
        if (RAND.nextDouble() < getBreakupProbability()) 
        {
            agent0.leaveRelationship(this) ;  
            agent1.leaveRelationship(this) ;  
            return true ;
        }
    	return false ;
    }

    /**
     * Since breakupProbability is static, this getter allows it to be called
     * from the Relationship base class
     * @return (double) the probability of a relationship ending in a given cycle
     */
    protected double getBreakupProbability()
    {
        return BREAKUP_PROBABILITY ;
    }
    
    /**
     * Runs through the number of sexual contacts, chooses Sites and tracks STI 
     * transmission
     * @return (String) report including agentIds, respective Sites, Site infection status, 
     * and whether transmission occurred
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException 
     */
    final protected String encounter() throws NoSuchMethodException, InvocationTargetException,
    IllegalAccessException
    {
    	String report = "" ;
        double infectProbability ;
            
    	int contacts = chooseNbContacts() ;
    
        // Initialising Agents and corresponding Sites 
    	report += "agentId0:" + Integer.toString(agent0.getAgentId()) + " " ;
        report += "agentId1:" + Integer.toString(agent1.getAgentId()) + " " ;
        
        if ((!agent0.getInfectedStatus()) && (!agent1.getInfectedStatus()))
            return report ;
        
    	for (int contact= 0; contact < contacts; contact++)
    	{
            //Class<?> agentClazz = agent0.getClass() ; //.asSubclass(agent0.getClass()) ;

            // TODO: Generalise to arbitrary Agent subClasses
            //Method siteMethod = MSM.class.getMethod("chooseSites", Agent.class, Agent.class) ;
                    //Site[] sites = (Site[]) siteMethod.invoke(agent0,agent1) ;
            Site[] sites = MSM.chooseSites(agent0, agent1) ;
            Site site0 = sites[0] ;
            Site site1 = sites[1] ;
            int infectStatus0 = site0.getInfectedStatus() ;
            int infectStatus1 = site1.getInfectedStatus() ;
            // no risk of transmission if both sites have same infectStatus
            if (infectStatus0 == infectStatus1) 
                continue ;	

            
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
        
            // Choose whether condom is used, if any Urethra Sites
            infectProbability = 1.0;
            if ((URETHRA.equals(site0.getSite()) && (RECTUM.equals(site1.getSite()))) 
                    || (URETHRA.equals(site1.getSite())&& RECTUM.equals(site0.getSite())))
            {
                report += "condom:" ;
                // TODO: Make probability of condom use depend on other Site
                //HIV status, etc
                //if (rand.nextDouble() < CONDOM_USE)
                if (Agent.useCondom(agent0, agent1, relationship))
                {
                    infectProbability*= (1.0 - CONDOM_EFFECT) ;
                    report += "true " ;
                }
                else 
                    report += "false " ;
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
                report += " " ; 	
            }
            else    // agent1 must be infected
            {
                infectProbability*= MSM.getInfectProbability(agent1, agent0, infectStatus1, site1, site0) ;
                //infectProbability*= (double) getInfectionMethod.
                        //	invoke(agent1,agent0,infectStatus1,site1,site0) ;

                // Probabilistically transmit infection to site0
                //site0.receive(infectName0,transmit0) ;
                report += Boolean.toString(agent0.receiveInfection(infectProbability,site0)) ;	    	
                report += " " ;
            }
    	}
        return report ;   	
    
    }
    
    /**
     * Chooses the number of sexual contacts for a given relationship in a given
     * cycle. Called by encounter()
     * @return 
     */
    private int chooseNbContacts()
    {
    	return RAND.nextInt(MAXIMUM_CONTACTS) + 1 ;
    }
    
    /**
     * 
     * @return (Agent[2]) The Agents in the relationship.
     */
    protected Agent[] getAgents()
    {
    	return new Agent[] {agent0,agent1} ;
    }
    
    public Agent getLowerIdAgent()
    {
        return agent0 ;
    }
    
    public String getRecord()
    {
    	String record = Reporter.addReportProperty(RELATIONSHIP_ID,relationshipId) ; 
        record += Reporter.addReportProperty("relationship",getRelationship()) ;
    	record += Reporter.addReportProperty(Reporter.AGENTID0,agent0.getAgentId());
    	record += Reporter.addReportProperty(Reporter.AGENTID1,agent1.getAgentId()) ;
    	return record ;
    }
    
    public String endRelationship()
    {
        return Reporter.addReportProperty(RELATIONSHIP_ID,relationshipId) ; 
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
