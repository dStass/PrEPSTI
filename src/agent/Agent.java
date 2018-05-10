/**
 * 
 */
package agent;

import community.* ;


import site.* ;

import java.util.Random ;

//import com.sun.media.jfxmedia.logging.Logger;
import java.lang.reflect.*;


import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.List;

/**
 * @author Michael Walker
 * @email mlwalker@kirby.unsw.edu.au
 *
 */
public abstract class Agent {
    // String representation of Agent subclass
    final private String agent ;

    // Number in Community.population
    final private int agentId ;

    // Age of Agent
    private int age ;

    // Age beyond which no agents live
    static int MAX_LIFE = 65 ;

    // Need to generate random numbers
    static Random RAND = new Random() ;

    // agentId of next Agent to be created, current number plus one
    static int NB_AGENTS_CREATED = 0 ;

    // Probability of screening in a given cycle when symptomatic is false
    static double SCREEN_PROBABILITY = 0.01 ;
    
    // Standard String null response 
    static String NONE = "none" ;

    // The maximum number of relationships an agent may be willing to sustain
    //static int MAX_RELATIONSHIPS = 15;

    // number of relationships willing to maintain at once
    private int promiscuity ;

    // probability of choosing each Relationship subclass
    // odds of choosing a Monogomous Relationship, Regular and Casual
    int monogomousOdds = RAND.nextInt(11) ;
    // odds of choosing a Regular Relationship
    int regularOdds = RAND.nextInt(11 - monogomousOdds) ;
    // odds of choosing a Casual Relationship
    int casualOdds = 10 - monogomousOdds - regularOdds ;

    // probability of cheating on a monogomous spouse
    private double infidelity;

    // current partners
    private ArrayList<Integer> currentPartnerIds = new ArrayList<Integer>() ;


    private ArrayList<Relationship> currentRelationships 
            = new ArrayList<Relationship>() ;

    private int lowerAgentId = 0 ;

    // number of Casual Relationships
    private int casualNumber = 0 ;

    // number of Regular Relationships
    private int regularNumber = 0 ;

    // whether currently in a Monogomous Relationship 
    private boolean inMonogomous = false ;

    // total numberof Relationships
    private int nbRelationships = 0 ;

    // current availability for a new relationship
    private boolean available = true ;

    /** Whether the Agent is infected with an STI of interest. */
    private boolean infectedStatus = false ;
    /** Whether any Site of the Agent is showing symptoms of the STI.
     * Should be false if infecteStatus is false.
     */
    private boolean symptomatic = false ;
    
    // names of fields of interest to the census.
    private String[] censusFieldNames = {"agentId","age","agent","infectedStatus",
            "symptomatic","available","inMonogomous","regularNumber","casualNumber",
            "nbRelationships","promiscuity"} ;

    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("agent") ;

    /**
     * In subclasses the choice of site might depend on the Class of Relationship.
     * @param agent0
     * @param agent1
     * @param relationshipClazzName
     * @return Probabilistically chosen Site[2] of Sites for sexual contact.
     */
    public static Site[] chooseSites(Agent agent0, Agent agent1, String relationshipClazzName)
    {
        return chooseSites(agent0, agent1) ;
    }
    
    public static Site[] chooseSites(Agent agent0, Agent agent1)
    {
            Site site0 = agent0.chooseSite() ;
            Site site1 = agent1.chooseSite(site0) ;
    return new Site[] {site0,site1} ;
    }

    /**
     * Multiple parameters needed as subclasses may have very elaborate schemes
     * @param infectedAgent
     * @param clearAgent
     * @param infectionStatus
     * @param infectedSite
     * @param clearSite
     * @return probability of infection passing from infectedSite to clearSite
     */
    public static double getInfectProbability(Agent infectedAgent, Agent clearAgent, int infectionStatus,
    		Site infectedSite, Site clearSite)
    {
    	return 0.5 ;
    }
	
    public static boolean useCondom(Agent agent0, Agent agent1, String relationshipName)
    {
        if (agent0.chooseCondom(agent1))
            return true ;
        return agent1.chooseCondom(agent0) ;
    }

   
    
    /**
     *  
     * Agent Class  
     * @param startAge - (int) Age at (sexual) birth
     */
    public Agent(int startAge)
    {
            this.agentId = NB_AGENTS_CREATED ;
            NB_AGENTS_CREATED++ ;
            initAge(startAge) ;
            
            initPromiscuity() ;
            initInfidelity() ;
            
            if (casualOdds < 0)
                LOGGER.log(Level.INFO, "{0} {1}", new Object[]{String.valueOf(agentId), String.valueOf(casualOdds)}) ;

            Class<?> clazz = this.getClass() ;
            agent = clazz.asSubclass(clazz).getSimpleName() ;

    }

    public int getId()
    {
            return agentId ;
    }

    /**
     * Initialises age of new Agents
     * @param startAge = -1, choose random age from 16 to 65, for initialising community
     *                 = 0 , choose random age from 16 to 20, new agents entering sexual maturity
     *                 > 0 , set age to startAge
     */
    private int initAge(int startAge)
    {
        if (startAge == -1) // Choose random age from 16 to 65
            age = RAND.nextInt(50) + 16 ;
        else if (startAge == 0) // Choose random age from 16 to 25, reaching sexual maturity
            age = RAND.nextInt(10) + 16 ;
        else
            age = startAge ;
        return age ;
    }

    public int getAge() {
            return age;
    }

    public void setAge(int age) {
            this.age = age;
    }

    public ArrayList<Relationship> getCurrentRelationships()
    {
            return currentRelationships ;
    }

    public ArrayList<Integer> getCurrentPartnerIds()
    {
            return currentPartnerIds;
    }

    /**
     * Randomly choose the number of simultaneous relationships an agent may have
     */
    private int initPromiscuity()
    {
            promiscuity = RAND.nextInt(getMaxRelationships()) + 1 ;
            return promiscuity ;
    }

    public int getPromiscuity()
    {
            return promiscuity ;
    }
    
    	
    abstract int getMaxRelationships() ;
    
    /**
     * 
     * @return (int) the number of orgies in a community per cycle
     */
    abstract public int getOrgyNumber() ;
    
    /**
     * 
     * @return (int) number of Agents invited to join any given orgy
     */
    abstract public int getOrgySize() ;

    /**
     * 
     * @return (double) the probability of Agents joining an orgy when invited
     */
    abstract public double getJoinOrgyProbability() ;
    
    public int getMonogomousOdds()
    {
            return monogomousOdds ;
    }

    public int getRegularOdds()
    {
            return regularOdds ;
    }

    public int getCasualOdds()
    {
            return casualOdds ;
    }

    /**
     * Randomly choose the agent's probability of cheating on a monogomous spouse.
     */
    private void initInfidelity()
    {
        int maximum = getMaxRelationships() ;
            infidelity = 0.1 * RAND.nextInt(maximum)/maximum ;
    }

    public double getInfidelity()
    {
            return infidelity ;
    }

    public String getCensusReport()
    {
        Class fieldClazz ;
        Class agentClazz ;
        String getterName ;
        Method getMethod ;
        String censusReport = "" ;
        for (String fieldName : censusFieldNames )
        {
            try
            {
                censusReport += fieldName + ":" ;
                fieldClazz = Class.forName(fieldName) ;
                agentClazz = Class.forName(agent) ;
                getterName = "get" + fieldName.substring(0,1).toUpperCase() 
                        + fieldName.substring(1) ;
                getMethod = agentClazz.getDeclaredMethod(getterName, (Class[]) null) ;
                censusReport += String.valueOf(getMethod.invoke(this)) + " " ;
            }
            catch ( NoSuchMethodException nsme)
            {
                LOGGER.log(Level.INFO, "{0}{1}", new Object[]{fieldName, nsme.getLocalizedMessage()});
            }
            catch ( ClassNotFoundException cnfe)
            {
                LOGGER.log(Level.INFO, "{0}{1}", new Object[]{fieldName, cnfe.getLocalizedMessage()});
            } 
            catch (IllegalAccessException iae) 
            {
                Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, iae);
            } 
            catch (IllegalArgumentException iae) 
            {
                Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, iae);
            } 
            catch (InvocationTargetException ite) 
            {
                Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ite);
            }
        }
        return censusReport ;
    }
    
    public String[] getCensusFieldNames()
    {
        return censusFieldNames ;
    }

   
    /**
     * 
     * @return Site[] sites
     */
    abstract protected Site[] getSites() ;
    
    /**
     * Used when choosing Site for sexual encounter
     * @return randomly chosen available Site
     */
    abstract Site chooseSite() ;

    /**
     * For when the choice of site depends on already chosen site
     * @param site
     * @return randomly chosen available Site
     */
    protected Site chooseSite(Site site)
    {
            return chooseSite() ;
    }

    /**
     * Called every 365 cycles to age the agent by one year.
     * @return String description of agentId, age, and any altered quantities if any, 
     *     empty otherwise
     */
    public String ageOneYear()
    {
            String report ;
            age++ ;
            report = ageEffects() ;

            // Prepare report if not still empty.
            if (! report.isEmpty())
            {
                    report =  "age:" + age + " " + report ;
                    report = "agentId:" + Integer.toString(agentId) + " " + report ;
            }
            return report ;
    }

    /**
     * Promiscuity and infidelity decrease from age == 30
	 * 
     * @return String description of any altered quantities if any, empty otherwise
     */
    private String ageEffects()
    {
        String report = "" ;
        if (age > 30)
        {
            // TODO: Re-implement and ensure that Agent.this is removed
            //from Community.agents
            if (age > 65)
            {
                report = death() ;
                return report ;
            }
            if (promiscuity > 0)
            {
                promiscuity-- ;
                report += "promiscuity:" + promiscuity + " " ;
            }
            //if (infidelity > 0.0)
            {
                infidelity *= 0.5 ;
                report += "infidelity:" + infidelity + " " ;
            }
        }
        return report ;
    }

    /**
     * 
     * @param partner
     * @return true if Agent decides to use a condom, false otherwise
     */
    abstract protected boolean chooseCondom(Agent partner) ;
            
    /**
     * Probabilistically transmits infection to receiving site.
     * @param transmitProbability
     * @param site
     * @return true if receiving site becomes infected, false otherwise
     */
    public boolean receiveInfection(double transmitProbability, Site site)
    {
            if (site.receiveInfection(transmitProbability))
            {
                infectedStatus = true ;
                setSymptomatic(site) ;
                return true ;
            }
            return false ;
    }

    /**
     * Whether the Agent is infected and symptomatic at any Site with any STI
     * @return symptomatic
     */
    public boolean getSymptomatic()
    {
            return symptomatic ;
    }

    /**
     * The Agent becomes symptomatic if and only if the newly infected Site is.
     * @param site
     * @return 
     */
    protected boolean setSymptomatic(Site site)
    {
            return symptomatic = (symptomatic || site.getSymptomatic()) ;
    }

    /**
     * May be overridden for specific agent subclasses with specific screening behaviours 
     * @param args (Object[]) for compatability with inherited versions which need input
     * parameters
     * @return Probability of screening when symptomatic is false
     */
    public double getScreenProbability(String[] args)
    {
            return SCREEN_PROBABILITY ;
    }

    /**
     * Call each site.treat(). If all treatments successful, call clearSymptomatic()
     * @return true if all sites successfully treated, false otherwise
     */
    public boolean treat()
    {
        Site[] sites = getSites() ;
        boolean successful = true ;
        for (Site site : sites)
            if ((site.getInfectedStatus()!=0))
                successful = (successful && site.treat()) ;
        if (successful) 
            infectedStatus = false ;
            clearSymptomatic();
        return successful ;
    }

    /**
     * Set symptomatic to false. Called only when all sites have been successfully treated.
     */
    protected void clearSymptomatic()
    {
        symptomatic = false ;
    }
    
    protected void clearInfection()
    {
        infectedStatus = false ;
        symptomatic = false ;
        Site[] sites = getSites() ;
        for (Site site : sites)
            site.clearInfection() ;
    }

    public boolean getInfectedStatus()
    {
        return infectedStatus ;
    }

    protected void setInfectedStatus(boolean infected)
    {
        infectedStatus = infected ;
    }

    /**
     * Called by Methods using consentArgs() to generate desired data
     * TODO: Remove middle step if Relationship.subclass and Agent partner not
     * adequate for decision.
     * 
     * @param consentArgs
     * @return consent(String,Agent)
     */
    public boolean consent(Object[] consentArgs)
    {
        return consent((String) consentArgs[0],(Agent) consentArgs[1]) ;
    }
    
    /**
     * Whether to enter a proposed relationship of class relationshipClazz .
     * Currently according to whether in a monogomous relationship and 
     * the number of relationships already entered compared to promiscuity.
     * 
     * @param relationshipClazzName - name relationship subclass
     * @param partner - agent for sharing proposed relationship
     * @return true if accept and false otherwise
     */
    public boolean consent(String relationshipClazzName, Agent partner)
    {
        if (inMonogomous)
            if (RAND.nextDouble() > infidelity) 
                return false ;
        return available ;
    }
    
    /**
     * Probabilistically decide to accept invitation to join orgy
     * @param args
     * @return true to join and false otherwise
     */
    final public boolean joinOrgy(double joinOrgyProbability, Object[] args)
    {
        if (inMonogomous)
            if (RAND.nextDouble() > infidelity)
                return false ;
        if (RAND.nextDouble() < joinOrgyProbability)
            return available ;
        return false ;
    }

    /**
     * Sets the availability according to the number of relationships and participation in 
     * a monogomous relationship
     * @return (Boolean) available
     */
    protected boolean findAvailable()
    {
        available = (nbRelationships < promiscuity) ;
        return available ;
    }

    protected boolean getAvailable()
    {
        return available ;
    }

    /**
     * Adds relationship to ArrayList relationships and initiates entry into a relationship
     * TODO: Ensure that Agent never has more than one concurrent Relationship with the same partner
     * @param relationship
     * @return String report number of Relationships
     */
    public String enterRelationship(Relationship relationship)
    {
        String report = Integer.toString(agentId) + ":" ;
        int partnerId = relationship.getPartnerId(agentId) ;
        report += Integer.toString(partnerId) + ":" ;
        //report += relationship.getRelationship() + " " ;

        currentRelationships.add(relationship) ;
        currentPartnerIds.add(partnerId) ;
        nbRelationships++ ;

        findAvailable() ;

        report = relationship.getReport() ;
        return report ;
    }

    /**
     * For new Relationships for which this Agent has the lower agentId
     * @return 
     */
    public String augmentLowerAgentId()
    {
        if (nbRelationships != currentRelationships.size())
            LOGGER.log(Level.SEVERE, "nbRelationships:{0} not equal to currentRelationships.size():{1}", 
                    new Object[]{nbRelationships,currentRelationships.size()});
        
        int lowerAgentInt = (int) Math.pow(2,(nbRelationships - 1)) ;
        if ((lowerAgentId & lowerAgentInt) == lowerAgentInt )
            LOGGER.log(Level.SEVERE, "agentId:{0} already lowerAgentId of Relationship with {3} {1} {2}", 
                    new Object[]{agentId,lowerAgentId,lowerAgentInt,currentRelationships.get((nbRelationships-1)).getPartnerId(agentId)});
        lowerAgentId = lowerAgentId ^ lowerAgentInt;
        return Integer.toString(lowerAgentId) ;
    }

    /**
     * For ending Relationships for which this Agent has the lower agentId
     * @param relationship
     * @return 
     */
    public String diminishLowerAgentId(Relationship relationship)
    {
        int relationshipIndex = currentRelationships.indexOf(relationship) ;
        if (relationshipIndex < 0)
        {
            LOGGER.log(Level.SEVERE, "Agent {0} shares no such Relationship with Agent {1}", 
                    new Object[]{agentId,relationship.getPartnerId(agentId)});
            return "No such relationship" ;
        }
        int lowerAgentInt = (int) Math.pow(2,relationshipIndex) ;
        
        /*if ((lowerAgentId & lowerAgentInt) != lowerAgentInt)
            LOGGER.log(Level.SEVERE, "agentId:{0} lowerAgentId:{1} lowerAgentInt:{2} nbRelationships:{3}", 
                    new Object[]{agentId,lowerAgentId,lowerAgentInt,nbRelationships});
        */
        
        // All bits up to and including relationshipIndex
        int smallerIndexMask = (2 * lowerAgentInt) - 1 ;
        
        // Keep only bits less than relationshipIndex
        int smallerIndex = lowerAgentId & (smallerIndexMask >> 1) ;
        if (smallerIndex > lowerAgentId)
            LOGGER.log(Level.SEVERE,"smallerIndex > lowerAgentId, agentId:{0} smallerIndex:{1} lowerAgentId:{2}", 
                    new Object[]{agentId,smallerIndex,lowerAgentId}) ;
        
        // Keep only bits greater than relationshipIndex
        int greaterIndex = lowerAgentId & ~smallerIndexMask ;
        if (greaterIndex < 0)
            LOGGER.log(Level.SEVERE, "agentId:{0} lowerAgentId:{1} smallerIndex:{2} nbRelationships{3}", 
                    new Object[]{agentId,lowerAgentId,smallerIndex,nbRelationships});
        
        // Move those bits one place to the right (smaller)
        greaterIndex = greaterIndex >> 1 ;
        
        // Combine to find new lowerAgentId
        lowerAgentId = smallerIndex + greaterIndex ;
        if ((agentId == 0) && false )
            LOGGER.log(Level.INFO, "agentId:{0} smallerIndex:{1} greaterIndex:{3} lowerAgentInt:{2}", 
                new Object[]{agentId,smallerIndex,lowerAgentInt,greaterIndex});
        
        return Integer.toString(lowerAgentId) ;
    }

    public int getLowerAgentId()
    {
        return lowerAgentId ;
    }

    public void enterRelationship(int agentNb)
    {
        currentPartnerIds.add(agentNb) ;

        nbRelationships++ ;

        // Open to more ?
        findAvailable() ;
    }

    /**
     * To avoid double application of Relationship.Methods() we act from the 
     * Agent with the smallest agentId
     * @param relationship
     * @return true if relationship left, false otherwise
     */
    final private String endRelationship(Relationship relationship)
    { 
        String report = "ended:" + relationship.getReport() ;
        
        relationship.getPartner(this).leaveRelationship(relationship) ;
        leaveRelationship(relationship) ;

        return report ;
    }

    /**
     * 
     * @return String indicating disease (HIV) status
     */
    public String declareStatus()
    {
        return NONE ;
    }

    /**
     * Override and call in subclass if details about possible partners are 
     * needed.
     * TODO: Currently not used but may be in future if Relationship subclass
     * and Agent partner are not longer enough data
     * 
     * @param relationshipClazzName
     * @param agent
     * @return String[] args of relationshipClazzName and other Properties 
     * relevant to deciding consent()
     */
    protected Object[] consentArgs(String relationshipClazzName, Agent agent) 
    {
        Object[] consentArgs = {relationshipClazzName,agent} ;
        return consentArgs ;
    }

    /**
     * Removes relationship and partner and modifies nbRelationships count by -1
     * TODO: Change to String Method and return report
     * @param relationship
     */
    public void leaveRelationship(Relationship relationship)
    {
        diminishLowerAgentId(relationship) ;
        // Leave specific relationship subclass
        try
        {
            String leaveMethodName = "leave" + relationship.getRelationship() ;
            Method leaveRelationshipMethod = Agent.class.getMethod(leaveMethodName, Relationship.class) ;
            leaveRelationshipMethod.invoke(this, relationship) ;
        }
        catch ( IllegalAccessException iae)
        {
            LOGGER.info(iae.getLocalizedMessage());
        }
        catch ( InvocationTargetException ite )
        {
            LOGGER.info(ite.getLocalizedMessage());
        }
        catch ( NoSuchMethodException nsme )
        {
            LOGGER.info(nsme.getLocalizedMessage()) ;
        }

        /*String debug = Integer.toString(relationship.getPartnerId(agentId)) + "::" ;
        for (int partnerI : currentPartnerIds)
        {
            debug += Integer.toString(partnerI) + " " ;
            debug += Integer.toString(currentPartnerIds.indexOf(partnerI)) + " : ";
        }
        LOGGER.info(debug);*/
        int partnerId = relationship.getPartnerId(agentId) ;
        int partnerIndex = currentPartnerIds.indexOf(partnerId) ;
        currentPartnerIds.remove(partnerIndex) ;
        int relationshipIndex = currentRelationships.indexOf(relationship) ;
        currentRelationships.remove(relationshipIndex) ;
        Relationship.diminishNbRelationships() ;
        nbRelationships-- ;
        
    }

    //TODO: Clean up leaveRelationshipType(int agentNb)
    private void leaveRelationship(int agentNb)
    {
            //Convert agentNb to Object so not treated as index
            currentPartnerIds.remove(agentNb) ;
            //lostPartners.add((Object) agentNb) ;

            nbRelationships-- ;
    }

    public String enterCasual(Relationship relationship)
    {
        String report = "casual:" ;
        report += enterRelationship(relationship) ;
        casualNumber++ ;
        return report;
    }

    protected void enterCasual(int agentNb)
    {
            enterRelationship(agentNb) ;
            casualNumber++ ;
    }

    public String enterRegular(Relationship relationship)
    {
        String report = "regular:" ;
            report += enterRelationship(relationship) ;
            regularNumber++ ;
            return report ;
    }

    protected void enterRegular(int agentNb)
    {
            enterRelationship(agentNb) ;
            regularNumber++ ;
    }

    public String enterMonogomous(Relationship relationship)
    {
        String report = "monogomous:" ;
        report += enterRelationship(relationship) ;
        inMonogomous = true ;
        return report ;
    }

    protected void enterMonogomous(int agentNb)
    {
        enterRelationship(agentNb) ;
        inMonogomous = true ;
    }

    public void leaveCasual(Relationship relationship)
    {
        //leaveRelationship(relationship) ;
        casualNumber-- ;
    }

    public void leaveCasual(int agentNb)
    {
        leaveCasual(agentNb) ;
        casualNumber-- ;
    }

    public void leaveRegular(Relationship relationship)
    {
        //leaveRelationship(relationship) ;
        regularNumber-- ;
    }

    private void leaveRegular(int agentNb)
    {
        //leaveRelationship(agentNb) ;
        regularNumber-- ;
    }

    public void leaveMonogomous(Relationship relationship)
    {
        //leaveRelationship(relationship) ;
        inMonogomous = false ;
    }

    private void leaveMonogomous(int agentNb)
    {
        //leaveRelationship(agentNb) ;
        inMonogomous = false ;
    }

    /**
     * Calls agent.death() to see if they die and removes them from agents
     * if so
     * @param agent
     * @return true if agent dies and false otherwise
     */
    final public boolean grimReaper()
    {
        // double risk = Math.exp(-age*Math.log(2)/halfLife) ;
        double risk = Math.pow(((MAX_LIFE - age)/((double) MAX_LIFE)),2) ;
        if (RAND.nextDouble() > risk ) 
        {
            death() ;
            return true ;
        }
        return false ;
    }
        
        
    /**
     * Make agent die and clear their Relationships
     * Default based on age, uniform probability with cutoff at maxLife
     * @return true if they die, false otherwise
     */
    final private String death()
    {
        // FIXME: Should maybe use Reporter.addReportProperty()
        String report = "death:agentId:" + String.valueOf(agentId) + " ";
        report += "age:" + String.valueOf(age) + " " ;
        //report += "nbPartners:" + String.valueOf(currentRelationships.size()) + " ";
        clearRelationships() ;
        return report ;
    }

    /**
     * Removes all Relationships of Agent. Called by death()
     * TODO: Add code to properly keep track of Community.nbRelationships
     */
    final private void clearRelationships()
    {
        Relationship relationship ;
        int partnerId ;
        int startIndex = (nbRelationships - 1) ;
        
        for (int relationshipIndex = startIndex ; relationshipIndex >= 0 ; 
                relationshipIndex-- )
        {
            relationship = currentRelationships.get(relationshipIndex) ;
            Agent agentLowerId = relationship.getLowerIdAgent() ;
            agentLowerId.endRelationship(relationship) ;
        }
    }
	
    /**
     * 
     * @return subclass.getName() of agent type
     */
    public String getAgent()
    {
            return agent ;
    }

    
    public void checkInfectedStatus()
    {
        Site[] sites = getSites() ;
        String siteReport = "" ;
        for (Site site : sites)
            siteReport += site.getSite() + ":" + site.getInfectedStatus() + " " ;
        LOGGER.log(Level.INFO, "Agent:{0} {1}", new Object[]{getInfectedStatus(),siteReport});
    }
    
}
