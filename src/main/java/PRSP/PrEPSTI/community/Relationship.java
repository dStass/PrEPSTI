package PRSP.PrEPSTI.community;

import PRSP.PrEPSTI.agent.* ;
import java.io.BufferedReader;

//import java.io.File;
import java.io.FileReader;
import PRSP.PrEPSTI.site.* ;
import PRSP.PrEPSTI.reporter.Reporter ;


import java.util.Random ;
//import java.util.logging.Logger;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet ;
import java.util.HashMap ;
import java.util.logging.Level;
//import java.util.logging.Level;

import PRSP.PrEPSTI.reporter.RelationshipReporter ;

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
    final String relationship ;
    
    public static String RELATIONSHIP_ID = "relationshipId" ;
    
    //static String FOLDER_PATH = "/srv/scratch/z3524276/prepsti/"
    //static String FOLDER_PATH = "/short/is14/mw7704/prepsti/"
    public static String FOLDER_PATH ;
    
    /** Generate and record Random number seed. */
    static long RANDOM_SEED = System.nanoTime() ;
    /** Random number generator. */
    static Random RAND = new Random(RANDOM_SEED) ;
    
    static public void SET_RAND(long seed)
    {
        RANDOM_SEED = seed ;
        RAND = new Random(RANDOM_SEED) ;
    }
    
    /** 
     * get RANDOM_SEED.
     * @return (long) 
     */
    static public final long GET_RANDOM_SEED()
    {
        return RANDOM_SEED ;
    }
    
    /**
     * Generates seed for random number generator to use upon reboot.
     * @return (long) seed for random number generation
     */
    static public final long GET_REBOOT_SEED()
    {
        return RAND.nextLong() ;
    }
    
    /** Site name of Rectum */
    static String RECTUM = "Rectum" ;
    /** Site name of Rectum */
    static String URETHRA = "Urethra" ;
    /** Site name of Rectum */
    static String PHARYNX = "Pharynx" ;
    
    static public String DEATH_RECORD = "death:" ;
    static public StringBuilder SB_DEATH_RECORD = new StringBuilder("death:");

    static public String BURNIN_COMMENCE = "clear:" ;
    static public String BURNIN_BREAKUP = "" ;
    
    /**
     * Adds to the Relationship record the Relationships which ended due to death.
     * @param record 
     */
    static public void APPEND_DEATH_RECORD(String record)
    {
    	// DEATH_RECORD += record ;
        SB_DEATH_RECORD.append(record);
    }
    
    static public String READ_DEATH_RECORD()
    {
        String output ;
        output = SB_DEATH_RECORD.toString() ;
        SB_DEATH_RECORD = new StringBuilder("death:") ;
        return output ;
    }
    
    /**
     * return polymorphic Relationship object of child classes from name (String)
     * * Casual
     * * Regular
     * * Monogomous
     * 
     * @param className
     * @return
     */
    public static Relationship GET_RELATIONSHIP_FROM_CLASS_NAME(String className) {
        Relationship relationship = null;
        switch(className) {
            case "Casual":
                relationship = new Casual();
                break;
            case "Regular":
                relationship = new Regular();
                break;
            case "Monogomous":
                relationship = new Monogomous();
                break;
            default:
                break;
        }
        return relationship;
    }
    
    /** Current number of relationships in the simulation */
    static int NB_RELATIONSHIPS = 0; 

    /** Total number of relationships created in the simulation */
    static int NB_RELATIONSHIPS_CREATED = 0; 

    /** Probability of sexual encounter within Relationship in any cycle */
    static double ENCOUNTER_PROBABILITY = 0.5 ;
    
    /** 
     * The maximum number of contacts allowed in a sexual encounter.
     * Adjusted down from 5 16/1/19 to reduce coprevalence.
     */
    static int MAXIMUM_CONTACTS = 3 ; // 5 ;
    
    static void SET_MAXIMUM_CONTACTS(int contacts)
    {
        MAXIMUM_CONTACTS = contacts ;
    }
    
    // TODO: Move condom variables to STI
    // Probability of using a condom for couplings with a Site.Urethra
    //static double CONDOM_USE = 0.5;
    
    // Protective effect of condom
    static double CONDOM_EFFECT = 0.86 ;
    
    static void SET_CONDOM_EFFECT(double effectiveness)
    {
        CONDOM_EFFECT = effectiveness ;
    }
    
    // Number of sexual contacts per cycle ;
    //private int contacts ;
    
    // Probability of breakup() in a given cycle. This value chosen for debugging.
    // Subclasses use their own values.
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
        // Odds for different Relationships
    	double monogomousOdds = agent0.getMonogomousOdds() + agent1.getMonogomousOdds() ;
    	double regularOdds = agent0.getRegularOdds() + agent1.getRegularOdds() ;
    	double casualOdds = agent0.getCasualOdds() + agent1.getCasualOdds() ;
        
    	double totalOdds = monogomousOdds + regularOdds + casualOdds ;
        
        // Choose Relationship subclass 
    	double choice = RAND.nextDouble() * totalOdds ;
        if (choice < monogomousOdds)
    		return Monogomous.class ;
    	if (choice < (monogomousOdds + regularOdds))
    		return Regular.class ;
    	return Casual.class ;
    }
    
    /**
     * Restores from saved simulation the Relationships among all Agents.
     * @param simName
     * @param agents
     * @return 
     */
    static public int RELOAD_RELATIONSHIPS(String simName, ArrayList<Agent> agents)
    {
        int nbRelationships = 0 ;
        
        RelationshipReporter relationshipReporter = new RelationshipReporter(simName,"output/prePrEP/") ;
        //RelationshipReporter relationshipReporter = new RelationshipReporter(simName,"/srv/scratch/z3524276/prepsti/output/test/") ;
        //RelationshipReporter relationshipReporter = new RelationshipReporter(simName,"/short/is14/mw7704/prepsti/output/year2007/") ;
        
        HashMap<Object,String[]> relationshipAgentReport 
                = relationshipReporter.prepareRelationshipAgentReport() ;
        
        String[] relationshipClassNames = new String[] {"Regular","Monogomous"} ; // "Casual",
        HashMap<Comparable,HashMap<Comparable,ArrayList<Comparable>>> relationshipsRecord 
                = relationshipReporter.prepareAgentRelationshipsRecord(relationshipClassNames, 0, 0, 1) ;
        ArrayList<Object> currentRelationshipIds = new ArrayList<Object>() ;
        String[] agentIds = new String[2] ;
        
        int agentIndex0 = 0 ;
        int agentIndex1 = 0 ;
        try
        {
            for (String relationshipName : relationshipClassNames)
            {
                Class relationshipClazz = Class.forName("PRSP.PrEPSTI.community.".concat(relationshipName)) ;
                for (ArrayList<Comparable> relationshipIdList : relationshipsRecord.get(relationshipName).values())
                {
                    for (Object relationshipId : relationshipIdList)
                    {
                        if (currentRelationshipIds.contains(relationshipId))
                            continue ;
                        currentRelationshipIds.add(relationshipId) ;
                        agentIds = relationshipAgentReport.get(relationshipId) ;
                        for (int agentIndex = 0 ; agentIndex < agents.size() ; agentIndex++ )
                        {
                            if (agents.get(agentIndex).getAgentId() == Integer.valueOf(agentIds[0]))
                                agentIndex0 = agentIndex ;
                            else if (agents.get(agentIndex).getAgentId() == Integer.valueOf(agentIds[1]))
                                agentIndex1 = agentIndex ;
                        }
                        Relationship relationship = (Relationship) relationshipClazz.newInstance();
                        relationship.addAgents(agents.get(agentIndex0), agents.get(agentIndex1)) ;
                        nbRelationships++ ;
                    }
                }

            }
            // LOGGER.info("nbRelationships:".concat(String.valueOf(nbRelationships)));
        }
        catch ( Exception e )
        {
            LOGGER.severe(e.toString()) ;
        }
        return nbRelationships ;
    }
    
    /**
     * Restores from saved simulation the Relationships among all Agents.
     * @param simName
     * @param agents
     * @return
     */
    static public int REBOOT_RELATIONSHIPS(String simName, ArrayList<Agent> agents) {
        return REBOOT_RELATIONSHIPS(FOLDER_PATH, simName, agents);
    }


    /**
     * Restores from saved simulation the Relationships among all Agents.
     * @param simName
     * @param agents
     * @return 
     */
    static public int REBOOT_RELATIONSHIPS(String folderPath, String simName, ArrayList<Agent> agents)
    {
        NB_RELATIONSHIPS = 0 ;        
        String relationshipRecord  = "" ;
        Integer relationshipId ;
        ArrayList<Integer> currentRelationshipIds = new ArrayList<Integer>() ;
        
        String rebootFileName = simName.concat("-REBOOT.txt") ;
        //ArrayList<String> nameArray = new ArrayList<String>() ;
        try
        {
            BufferedReader fileReader = new BufferedReader(new FileReader(folderPath + rebootFileName)) ;
            // Find Relationship line
            for (String record = "" ;  record != null ; record = fileReader.readLine() )
            {
                int relationshipIndex = record.indexOf(Reporter.RELATIONSHIPID) ;
                if (relationshipIndex > 0)
                {
                    relationshipRecord = record.substring(relationshipIndex) ;
                    break ;
                }
            }
            fileReader.close() ;
            String agentId0 ;
            String agentId1 ;
            int agentIndex0 = 0 ;
            int agentIndex1 = 0 ;
            int agentsFound ;
            int agentIndex ;
            String relationshipClazzName ;

            ArrayList<String> relationshipIdList = Reporter.EXTRACT_ARRAYLIST(relationshipRecord, Reporter.RELATIONSHIPID) ;
       
            for (String relationshipString : relationshipIdList)
            {
                relationshipId = Integer.valueOf(Reporter.EXTRACT_VALUE(Reporter.RELATIONSHIPID, relationshipString)) ;
                if (currentRelationshipIds.contains(relationshipId))
                {
                    LOGGER.severe(relationshipId + " found again!");
                    continue ;
                }
                currentRelationshipIds.add(relationshipId) ;

                agentId0 = Reporter.EXTRACT_VALUE(Reporter.AGENTID0, relationshipString) ;
                agentId1 = Reporter.EXTRACT_VALUE(Reporter.AGENTID1, relationshipString) ;

                // Add Agents of correct agentId
                agentsFound = 0 ;
                agentIndex = 0 ;
                //for (int agentIndex = 0 ; agentIndex < agents.size() ; agentIndex++ )
                while (agentsFound < 2)
                {
                    if (agents.get(agentIndex).getAgentId() == Integer.valueOf(agentId0))
                    {
                        agentIndex0 = agentIndex ;
                        agentsFound++ ;
                    }
                    else if (agents.get(agentIndex).getAgentId() == Integer.valueOf(agentId1))
                    {
                        agentIndex1 = agentIndex ;
                        agentsFound++ ;
                    }
                    agentIndex++ ;
                }

                // Find Relationship Class and create Relationship
                relationshipClazzName = Reporter.EXTRACT_VALUE("relationship", relationshipString) ;
                Class relationshipClazz = Class.forName("PRSP.PrEPSTI.community.".concat(relationshipClazzName)) ;

                Relationship relationship = (Relationship) relationshipClazz.getDeclaredConstructor().newInstance() ;
                relationship.addAgents(agents.get(agentIndex0), agents.get(agentIndex1)) ;
                NB_RELATIONSHIPS++ ;
                relationship.setRelationshipId(relationshipId) ;
            }
            // LOGGER.info("nbRelationships:".concat(String.valueOf(NB_RELATIONSHIPS)));
        }
        catch ( Exception e )
        {
            LOGGER.severe(e.toString()) ;
        }
        NB_RELATIONSHIPS_CREATED = 1 + ((Integer) Collections.max(new HashSet(currentRelationshipIds))) ;
        return NB_RELATIONSHIPS ;
    }
    
    /**
     * Decrements NB_RELATIONSHIPS by one. Called by Agent.leaveRelationship().
     */
    static public void DIMINISH_NB_RELATIONSHIPS()
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
    
    /**
     * Getter Method for relationshipId
     * @return relationshipId
     */
    final public synchronized int getRelationshipId()
    {
        return relationshipId ;
    }
    
    /**
     * Setter Method for relationshipId. Used for rebooting 
     * previous simulation.
     * @param relationshipId 
     */
    final public void setRelationshipId(int relationshipId)
    {
        this.relationshipId = relationshipId ;
    }
    
    /**
     * Adds Agents to Relationship and establishes which has the lower AgentId
     * Arrange that agent0 should always have the lower agentId
     * @param agent0
     * @param agent1
     * @return (String) report
     */
    final public String addAgents(Agent agent0, Agent agent1)
    {
        //String report = "" ;
        this.agent0 = agent0 ;
    	this.agent1 = agent1 ;
    	
        this.agent1.enterRelationship(this) ;
        String report = this.agent0.enterRelationship(this) ;
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
    protected synchronized double getEncounterProbability()
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
    final protected String encounter() 
    {
        String report = "" ;
        StringBuilder sbReport = new StringBuilder();
        double infectProbability ;
            
    	int contacts = chooseNbContacts() ;
    
        // Initialising Agents and corresponding Sites 
    	/*report += "agentId0:" + Integer.toString(agent0.getAgentId()) + " " ;
        report += "agentId1:" + Integer.toString(agent1.getAgentId()) + " " ;*/
        
        // return if neither Agent is infected
        //if ((!agent0.getInfectedStatus()) && (!agent1.getInfectedStatus()))
    	if ((agent0.getInfectedStatus() | agent1.getInfectedStatus()) == 0)
            return report ;
        
        // Loop through sexual contacts
    	for (int contact= 0; contact < contacts; contact++)
    	{
            //Class<?> agentClazz = agent0.getClass() ; //.asSubclass(agent0.getClass()) ;

            // TODO: Generalise to arbitrary Agent subClasses
            Site[] sites = MSM.CHOOSE_SITES(agent0, agent1) ;
            Site site0 = sites[0] ;
            Site site1 = sites[1] ;
            
            // Are contact sites infected?
            int infectStatus0 = site0.getInfectedStatus() ;
            int infectStatus1 = site1.getInfectedStatus() ;
            // no risk of transmission if both sites have same infectStatus
            // Comment this out if interested in HIV prevention and other sexual practices
            if (infectStatus0 == infectStatus1) 
                continue ;	

            
            // Update report
            sbReport.append(Reporter.ADD_REPORT_PROPERTY("contact", contact));
            sbReport.append(Reporter.ADD_REPORT_PROPERTY(site0.toString(),infectStatus0));
            sbReport.append(Reporter.ADD_REPORT_PROPERTY(site1.toString(),infectStatus1));

            // report += Reporter.ADD_REPORT_PROPERTY("contact", contact) ;
            // report += Reporter.ADD_REPORT_PROPERTY(site0.toString(),infectStatus0) ;
            // report += Reporter.ADD_REPORT_PROPERTY(site1.toString(),infectStatus1) ;


            // compare Infection status of both sites
            /*
            Infection infection0 = site0.getInfection();
            Infection infection1 = site1.getInfection();

            String infectName0 = infection0.getClass().getName(); 
            String infectName1 = infection1.getClass().getName(); 
            */
        
            // Choose whether condom is used, if any Urethra involved
            infectProbability = 1.0;
            if ((URETHRA.equals(site0.toString()) && (RECTUM.equals(site1.toString()))) 
                    || (URETHRA.equals(site1.toString())&& RECTUM.equals(site0.toString())))
            {   
                sbReport.append("condom: ");
                // report += "condom:" ;
                
                if (Agent.USE_CONDOM(agent0, agent1, relationship))
                {
                    infectProbability *= (1.0 - CONDOM_EFFECT) ;
                    sbReport.append("true");
                    // report += "true " ;
                }
                else 
                {
                    sbReport.append("false");
                    // report += "false " ;
                }
            }

            //TODO: Generalise to other subclasses of Agent
            //Method getInfectionMethod = agentClazz.getMethod("GET_INFECT_PROBABILITY", Agent.class,
                    //	Agent.class, int.class, Site.class, Site.class ) ;
            // Method getInfectionMethod = Agent.class.getMethod("GET_INFECT_PROBABILITY", Agent.class,
                    //	Agent.class, int.class, Site.class, Site.class ) ;
            if (infectStatus0 > 0) // && (infectStatus1 == 0))
            {
                infectProbability*= MSM.GET_INFECT_PROBABILITY(site0, site1) ; 
                                //(double) getInfectionMethod.
                                //invoke(agent0,agent1,infectStatus0,site0,site1) ;

                // Probabilistically transmit infection to site1
                //site1.receive(infectName0,transmit0) ;

                sbReport.append(Reporter.ADD_REPORT_PROPERTY("transmission", Boolean.toString(agent1.receiveInfection(infectProbability,site1))));
                // report += Reporter.ADD_REPORT_PROPERTY("transmission", Boolean.toString(agent1.receiveInfection(infectProbability,site1))) ;  
            }
            else if (infectStatus1 > 0) // && (infectStatus0 == 0))    // agent1 must be infected
            {
                infectProbability*= MSM.GET_INFECT_PROBABILITY(site1, site0) ;
                //infectProbability*= (double) getInfectionMethod.
                        //	invoke(agent1,agent0,infectStatus1,site1,site0) ;

                // Probabilistically transmit infection to site0
                //site0.receive(infectName0,transmit0) ;

                sbReport.append(Reporter.ADD_REPORT_PROPERTY("transmission", Boolean.toString(agent0.receiveInfection(infectProbability,site0)))); 
                // report += Reporter.ADD_REPORT_PROPERTY("transmission", Boolean.toString(agent0.receiveInfection(infectProbability,site0))) ; 
            }
        }
        
        report = sbReport.toString();
        return report ;   	
    
    }
    
    /**
     * Allows unit testing of encounter() without compromising scope.
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException 
     */
    final public String testEncounter() throws NoSuchMethodException, InvocationTargetException,
    IllegalAccessException
    {
        return encounter() ;
    }
    
    /**
     * Chooses the number of sexual contacts for a given relationship in a given
     * cycle. Called by encounter()
     * @return int between 1 and MAXIMUM_CONTACTS
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
    
    public synchronized Agent getLowerIdAgent()
    {
        return agent0 ;
    }
    
    public String getRecord()
    {   
        StringBuilder sbRecord = new StringBuilder();
    	sbRecord.append(Reporter.ADD_REPORT_PROPERTY(RELATIONSHIP_ID,relationshipId)) ; 
        sbRecord.append(Reporter.ADD_REPORT_PROPERTY("relationship",getRelationship())) ;
        sbRecord.append(Reporter.ADD_REPORT_PROPERTY(Reporter.AGENTID0,agent0.getAgentId()));
        sbRecord.append(Reporter.ADD_REPORT_PROPERTY(Reporter.AGENTID1,agent1.getAgentId()));
        return sbRecord.toString() ;
    }
    
    public String endRelationship()
    {
        return Reporter.ADD_REPORT_PROPERTY(RELATIONSHIP_ID,relationshipId) ; 
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
