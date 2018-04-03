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
    private String agent ;

    // Number in Community.population
    private int agentId ;

    // Age of Agent
    private int age ;

    // Age beyond which no agents live
    static int maxLife = 65 ;

    // Need to generate random numbers
    static Random rand = new Random() ;

    // agentId of next Agent to be created, current number plus one
    static int nbAgentsCreated = 0 ;

    // Probability of screening in a given cycle when symptomatic is false
    static double SCREEN_PROBABILITY = 0.01 ;

    // Agent subclasses need their own array of Sites e.g. penis, anus, pharynx
    private Site[] sites ;

    // The maximum number of relationships an agent may be willing to sustain
    static int maxRelationships = 15;

    // number of relationships willing to maintain at once
    private int promiscuity ;

    // probability of choosing each Relationship subclass
    // odds of Monogomous, Regular and Casual
    int monogomousOdds = rand.nextInt(11) ;
    int regularOdds = rand.nextInt(11 - monogomousOdds) ;
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

    // infection and symptomatic status' , respectively
    private boolean infectedStatus ;
    private boolean symptomatic ;
    
    // names of fields of interest to the census.
    private String[] censusFieldNames = {"agentId","age","agent","infectedStatus",
            "symptomatic","available","inMonogomous","regularNumber","casualNumber",
            "nbRelationships","promiscuity"} ;

    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("agent") ;

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
    static double getInfectProbability(Agent infectedAgent, Agent clearAgent, int infectionStatus,
    		Site infectedSite, Site clearSite)
    {
    	return 0.5 ;
    }
	
    /**
     *  
     * Agent Class  
     */
    public Agent(int startAge)
    {
            this.agentId = nbAgentsCreated ;
            nbAgentsCreated++ ;
            initAge(startAge) ;
            setSites() ;

            //Check that setSites(Site[]) was properly implemented by agent Subclass 
            if (!(sites.length >= 1)) System.err.println("ERROR: Agent subClass.setSites(Site[]) must initialise Sites") ;

            initPromiscuity() ;
            initInfidelity() ;

            Class<?> clazz = this.getClass() ;
            agent = clazz.asSubclass(clazz).getSimpleName() ;
            return ;
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
            age = rand.nextInt(50) + 16 ;
        else if (startAge == 0) // Choose random age from 16 to 20, reaching sexual maturity
            age = rand.nextInt(5) + 16 ;
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

    protected int getMaxRelationships()
    {
            return maxRelationships ;
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
            promiscuity = rand.nextInt(getMaxRelationships()) + 1 ;
            return promiscuity ;
    }

    public int getPromiscuity()
    {
            return promiscuity ;
    }

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
     * Randomly choose the agent's probability of cheating on a monogomous spouse
     */
    private void initInfidelity()
    {
            infidelity = 0.1 * rand.nextInt(getMaxRelationships()) ;
            return ;
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
     * Adds relevant body Sites to particular subClass of agent, 
     * then puts in a Site[] to call setSites(Site[])
     *  e.g. this.mySite = new MySite() ;
     * Each such Site requires its own getter(). 
     */
    abstract void setSites() ;

    /**
     * Sets Site[] sites, called from setSites()
     * @param sites
     */
    protected void setSites(Site[] sites)
    {
            this.sites = sites ;
            return ;
    }

    /**
     * 
     * @return Site[] sites
     */
    public Site[] getSites()
    {
            return sites ;
    }

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
        if ((age > 30))
            {
                    if (promiscuity > 0)
                    {
                            promiscuity-- ;
                            report += "promiscuity:" + promiscuity + " " ;
                    }
            if (infidelity > 0.0)
                    {
                    infidelity *= 0.5 ;
                        report += "infidelity" + infidelity + " " ;
                    }
            }
        return report ;
    }

    /**
     * Probabilistically transmits infection to receiving site.
     * @param transmitProbability
     * @param site
     * @return True if receiving site becomes infected, false otherwise
     */
    public boolean receiveInfection(double transmitProbability, Site site)
    {
            if (site.receiveInfection(transmitProbability))
            {
                    setSymptomatic(site) ;
                    return true ;
            }
            return false ;
    }

    public boolean getSymptomatic()
    {
            return symptomatic ;
    }

    protected boolean setSymptomatic(Site site)
    {
            return symptomatic = (symptomatic || site.getSymptomatic()) ;
    }

    /**
     * May be overridden for specific agent subclasses with specific screening behaviours 
     * @return Probability of screening when symptomatic is false
     */
    public double getScreenProbability(String[] args)
    {
            return SCREEN_PROBABILITY ;
    }

    /**
     * Call each site.treat(). If all treatments successful, call clearSymptomatic()
     * @return true if all sites auccessfully treated, false otherwise
     */
    public boolean treat()
    {
            boolean successful = true ;
            for (Site site : sites)
            {
                    if (site.getSymptomatic())
                    {
                            successful = (successful && site.treat()) ;
                    }
            }
            if (successful) clearSymptomatic();
            return successful ;
    }


    /**
     * Set symptomatic to false. Called only when all sites have been successfully treated
     */
    protected void clearSymptomatic()
    {
            symptomatic = false ;
            return ;
    }

    protected boolean getInfectedStatus()
    {
            return infectedStatus ;
    }

    protected void setInfectedStatus(boolean infected)
    {
            infectedStatus = infected ;
            return ;
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
        {
            if (rand.nextDouble() > infidelity) 
                return false ;
        }
        return available ;
    }

    /**
     * Sets the availability according to the number of relationships and participation in 
     * a monogomous relationship
     * @return
     */
    protected boolean setAvailable()
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


        setAvailable() ;

        report = relationship.getReport() ;
        return report ;
    }

    /**
     * For new Relationships for which this Agent has the lower agentId
     * @return 
     */
    public String augmentLowerAgentId()
    {
            lowerAgentId += 2^(nbRelationships - 1);
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
            lowerAgentId -= 2^relationshipIndex;
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
            setAvailable() ;
            return ;
    }

    /**
     * To avoid double application of Relationship.Methods() we act from the 
     * Agent with the smallest agentId
     * @param relationship
     * @return true if relationship left, false otherwise
     */
    public String endRelationship(Relationship relationship)
    { 
            String report = "ended:" + relationship.getReport() ;

            diminishLowerAgentId(relationship) ;

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
        return "none" ;
    }

    /**
     * Override and call in subclass if details about possible partners are 
     * needed.
     * @param relationshipClazzName
     * @param agent
     * @return String[] args of relationshipClazzName and other Properties 
     * relevant to deciding consent()
     */
    public String[] consentArgs(String relationshipClazzName, Agent agent) 
    {
        String[] consentArgs = {relationshipClazzName} ;
        return consentArgs ;
    }

    /**
     * Removes relationship and partner and modifies nbRelationships count by -1
     * TODO: Change to String Method and return report
     * @param relationship
     */
    public void leaveRelationship(Relationship relationship)
    {
        // Leave specific relationship subclass
        try
        {
            String leaveMethodName = "leave" + relationship.getRelationship() ;
            Method leaveRelationshipMethod = Agent.class.getMethod(leaveMethodName, Relationship.class) ;
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
        currentRelationships.remove(relationship) ;
        nbRelationships-- ;
        return ;
    }

    private void leaveRelationship(int agentNb)
    {
            //Convert agentNb to Object so not treated as index
            currentPartnerIds.remove(agentNb) ;
            //lostPartners.add((Object) agentNb) ;

            nbRelationships-- ;
            return ;
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
            return ;
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
            return ;
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
        return ;
    }

    public void leaveCasual(Relationship relationship)
    {
        leaveRelationship(relationship) ;
        casualNumber-- ;
        return ;
    }

    public void leaveCasual(int agentNb)
    {
            leaveCasual(agentNb) ;
            casualNumber-- ;
            return ;
    }

    public void leaveRegular(Relationship relationship)
    {
        leaveRelationship(relationship) ;
        regularNumber-- ;
        return ;
    }

    private void leaveRegular(int agentNb)
    {
            leaveRelationship(agentNb) ;
            regularNumber-- ;
            return ;
    }

    public void leaveMonogomous(Relationship relationship)
    {
        leaveRelationship(relationship) ;
        inMonogomous = false ;
        return ;
    }

    private void leaveMonogomous(int agentNb)
    {
            leaveRelationship(agentNb) ;
            inMonogomous = false ;
            return ;
    }

    /**
     * Randomly choose whether agent dies on this occassion.
     * Default based on age, uniform probability with cutoff at maxLife
     * @return true if they die, false otherwise
     */
    public boolean death()
    {
        // double risk = Math.exp(-age*Math.log(2)/halfLife) ;
        double risk = (maxLife - age)/((double) maxLife) ;
        LOGGER.info(Double.toString(risk));
        if (rand.nextDouble() > risk ) 
        {
            clearRelationships() ;
            return true ;
        }
        return false ;
    }

    protected void clearRelationships()
    {
        for (int relationshipIndex = (nbRelationships - 1) ; relationshipIndex >= 0 ; 
                relationshipIndex-- )
        {
            leaveRelationship(currentRelationships.get(relationshipIndex)) ;
        }
        return ;
    }
	
    /**
     * 
     * @return subclass.getName() of agent type
     */
    public String getAgent()
    {
            return agent ;
    }

}
