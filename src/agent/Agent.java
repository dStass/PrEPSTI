/**
 * 
 */
package agent;

import community.* ;
import reporter.* ;

import reporter.Reporter ;
import site.* ;

import java.util.Random ;

//import com.sun.media.jfxmedia.logging.Logger;
import java.lang.reflect.*;


import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.List;

/**
 * @author <a href = "mailto:mlwalker@kirby.unsw.edu.au">Michael Walker</a>
 *
 */
public abstract class Agent {
    // String representation of Agent subclass
    final private String agent ;

    // Number in Community.population
    private int agentId ;

    /** Age of Agent */
    private int age ;

    /** Number of days since last birthday. */
    private int cyclesModYear ;
    
    // Age beyond which Agents are removed from the population. */
    static int MAX_LIFE = 65 ;
    
    
    /** Names of Sites for Agent*/ 
    static public String[] SITE_NAMES = new String[] {} ;
    
    
    // Age-specific death rates from ABS.Stat
    static double RISK_60 = 5.6 ;
    static double RISK_55 = 3.7 ;
    static double RISK_50 = 2.5 ;
    static double RISK_45 = 1.6 ;
    static double RISK_40 = 1.2 ; 
    static double RISK_35 = 0.8 ;
    static double RISK_30 = 0.6 ;
    static double RISK_25 = 0.4 ;
    static double RISK_20 = 0.4 ;
    static double RISK_15 = 0.2 ;

    final static double DAYS_PER_YEAR = 365.25 ;
    
    // Need to generate random numbers
    /** Generate and record Random number seed. */
    static long RANDOM_SEED = System.nanoTime() ;
    static Random RAND = new Random(RANDOM_SEED) ;
    
    static public void SET_RAND(long seed)
    {
        RANDOM_SEED = seed ;
        RAND = new Random(RANDOM_SEED) ;
    }
    
    /** get RANDOM_SEED.
     * @return  */
    static public final long GET_RANDOM_SEED()
    {
        return RANDOM_SEED ;
    }
    
    /** String representation of "true". */
    static String TRUE = "true" ;
    /** String representation of "false". */
    static String FALSE = "false" ;
    /** String representation of "Monogomous". */
    static String MONOGOMOUS = "Monogomous" ;
    /** String representation of "Regular". */
    static String REGULAR = "Regular" ;
    /** String representation of "Casual". */
    static String CASUAL = "Casual" ;
    
    // agentId of next Agent to be created, current number plus one
    static int NB_AGENTS_CREATED = 0 ;

    /** Probability of screening in a given cycle when symptomatic is false. */
    static double SCREEN_PROBABILITY = 0.001 ;
    
    /** 
     * The fraction by which infidelity is multiplied every additional year after 
 the startAge of 30. 
     */
    static double INFIDELITY_FRACTION = 0.5 ;
    
    // Standard String null response 
    static String NONE = "none" ;

    // The maximum number of relationships an agent may be willing to sustain
    //static int MAX_RELATIONSHIPS = 15;

    // number of relationships willing to maintain at once
    private int concurrency ;

    /** odds of choosing a Monogomous Relationship. */
    double monogomousOdds = Monogomous.BREAKUP_PROBABILITY ;
    /** odds of choosing a Regular Relationship. */
    double regularOdds = Regular.BREAKUP_PROBABILITY ;
    /** odds of choosing a Casual Relationship. */
    double casualOdds = Casual.BREAKUP_PROBABILITY ;
 
    // probability of cheating on a monogomous spouse
    private double infidelity = 0.03;

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
    
        /** Days between asymptomatic STI screens . */
    private int screenCycle = 92 ;
    
    /** Cycles remaining until next STI screen. */
    private int screenTime ;

    
    // names of fields of interest to the census.
    private String[] censusFieldNames = {"agentId","agent","concurrency","infidelity"} ;
            //"symptomatic","available","inMonogomous","regularNumber","casualNumber",
            //"nbRelationships",

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
    
    /**
     * TODO: Implement Method based on relative frequency of types of sexual contact.
     * @param agent0
     * @param agent1
     * @param relationshipClazzName
     * @return Probabilistically chosen Site[2] of Sites for sexual contact.
     */
    public static Site[] chooseSitesNew(Agent agent0, Agent agent1, String relationshipClazzName)
    {
        return chooseSites(agent0, agent1, relationshipClazzName) ;
    }
    
    /**
     * @param agent0
     * @param agent1
     * @return Probabilistically chosen Site[2] of Sites for sexual contact.
     */
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
	
    /**
     * 
     * @param agent0
     * @param agent1
     * @param relationshipName
     * @return (boolean) whether condom is used in sexual encounter.
     */
    public static boolean useCondom(Agent agent0, Agent agent1, String relationshipName)
    {
        return ((agent0.chooseCondom(relationshipName, agent1)) || (agent1.chooseCondom(relationshipName, agent0))) ;
    }

    /**
     * Reloads Agents from a saved simulation to continue it.
     * @param simName 
     */
    static public ArrayList<Agent> reloadAgents(String simName)
    {
        ArrayList<Agent> agents = new ArrayList<Agent>() ;
        PopulationReporter populationReporter = new PopulationReporter(simName,"output/test/") ;
        
        ArrayList<String> birthReport = populationReporter.prepareBirthReport() ;
        
        ArrayList<ArrayList<Object>> agentDeathReport = populationReporter.prepareAgentDeathReport() ;
        
        ScreeningReporter screeningReporter = new ScreeningReporter(simName,"output/test/") ;
        
        String screeningRecord = screeningReporter.getFinalRecord() ;
        
        int infectionIndex ;
        int startAge ;
        String id ;
                        
        boolean symptoms ;
        String className ;
        Site[] sites ;
        //int daysPerYear = 365 ;
        Class clazz ;
        
        // Get ArrayList of dead Agents so we don't waste time reading their data
        ArrayList deadAgentIds = new ArrayList<Object>() ;
        for (ArrayList<Object> agentDeathRecord : agentDeathReport)
            deadAgentIds.addAll(agentDeathRecord) ;
        
        //int maxCycles = populationReporter.getMaxCycles() ;
        int birthIndex = 0 ;
        for (String birthRecord : birthReport)
        {
            ArrayList<String> birthList = Reporter.extractArrayList(birthRecord,"agentId") ;
            birthIndex += birthList.size() ;
            ArrayList<String> properties = Reporter.identifyProperties(birthList.get(0)) ;
            for (String birth : birthList)
            {
                // Extract Agents still living
                id = Reporter.extractValue("agentId", birth);
                if (deadAgentIds.contains(id))
                    continue ;
                
                startAge = Integer.valueOf(Reporter.extractValue("age",birth));
                //age += (maxCycles - birthIndex)/daysPerYear ;
                className = Reporter.extractValue("agent",birth);
                try
                {
                    clazz = Class.forName(className);
                    MSM newAgent = (MSM) clazz.getConstructor(int.class).newInstance(startAge);
                    reloadAgent(newAgent, birth, properties) ;
                    
                    infectionIndex = screeningRecord.indexOf(Reporter.AGENTID.concat(id)) ;
                    String infectionString ;
                    infectionString = Reporter.extractBoundedString(Reporter.AGENTID, screeningRecord, infectionIndex);
                    
                    sites = newAgent.getSites();
                    for (Site site : sites)
                    {
                        if (infectionString.contains(site.getSite()))
                        {
                            site.receiveInfection(1.1) ;
                            symptoms = Boolean.getBoolean(Reporter.extractValue(site.getSite(),infectionString));
                            if (symptoms)
                                site.setSymptomatic(1.1) ;
                            else
                                site.setSymptomatic(-0.1) ;
                        }
                    }
                    agents.add(newAgent) ;
                }
                catch ( Exception e )
                {
                    LOGGER.log(Level.SEVERE, "{0} {1}", new Object[]{e.getClass().getCanonicalName(), className});
                }
            }
        }
        NB_AGENTS_CREATED = birthIndex ;
        
        return agents ;
    }
    
    static void reloadAgent(MSM newAgent, String census, ArrayList<String> propertyArray)
    {
        propertyArray.remove("agent") ;
        propertyArray.remove("age") ;

        String valueString ;
        String setterName ;
        Class propertyClazz ;
        Method setMethod ;
        Method valueOfMethod ;
        try
        {
            for (String property : propertyArray)
            {
                valueString = Reporter.extractValue(property, census) ;
                propertyClazz = MSM.class.getDeclaredField(property).getType() ;
                valueOfMethod = propertyClazz.getMethod("valueOf", String.class) ;
                LOGGER.log(Level.INFO,"{0}",valueOfMethod.invoke(valueString));

                setterName = "set" + property.substring(0,1).toUpperCase() 
                        + property.substring(1) ;
                setMethod = MSM.class.getDeclaredMethod(setterName, propertyClazz) ;
                setMethod.invoke(newAgent, valueOfMethod.invoke(valueString)) ;
            }
        }
        catch (Exception e)
        {
            LOGGER.severe(e.toString());
        }
        
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
            cyclesModYear = RAND.nextInt(365) ;
            
            initConcurrency() ;
            initInfidelity() ;
            
            // initRelationshipOdds() ;    // Defined in preamble.

            Class<?> clazz = this.getClass() ;
            agent = clazz.asSubclass(clazz).getSimpleName() ;

    }

    public int getAgentId()
    {
            return agentId ;
    }

    private void setAgentId(int id)
    {
        this.agentId = id ;
    }
    
    /**
     * Initialises startAge of new Agents
     * @param startAge = -1, choose random startAge from 16 to 65, for initialising community
                 = 0 , choose random startAge from 16 to 20, new agents entering sexual maturity
                 > 0 , set startAge to startAge
     */
    private int initAge(int startAge)
    {
        if (startAge == -1) // Choose random startAge from 16 to 65
            age = RAND.nextInt(50) + 16 ;
        else if (startAge == 0) // Choose random startAge from 16 to 25, reaching sexual maturity
            age = RAND.nextInt(10) + 16 ;
        else
            age = startAge ;
        return age ;
    }

    /**
     * Initialises odds for different Relationship types
     */
    private void initRelationshipOdds()
    {
        try
        {
            // odds of choosing a Monogomous Relationship
            monogomousOdds = RAND.nextDouble() * Monogomous.BREAKUP_PROBABILITY ;
            // odds of choosing a Regular Relationship
            regularOdds = RAND.nextDouble() * Regular.BREAKUP_PROBABILITY ;
            // odds of choosing a Casual Relationship
            casualOdds = RAND.nextDouble() * Casual.BREAKUP_PROBABILITY ;
        }
        catch ( Exception e )
        {
            LOGGER.log(Level.INFO, "{0}", e.getLocalizedMessage());
        }

    }
    
    /**
     * Initialises Agent infectedStatus while ensuring consistency with 
     * Site.infectedStatus .
     */
    final public void initInfectedStatus()
    {
        boolean infected = false ;    //  getInfectedStatus() ;
        for (Site site : getSites())
        {
            infected = infected || site.initialiseInfection() ;
            setSymptomatic(site) ;
        }
        setInfectedStatus(infected) ;
    }
       
    /**
     * 
     * @return (int) startAge of Agent.
     */
    public int getAge() 
    {
            return age;
    }

    /**
     * Sets startAge of Agent to (int) startAge.
     * @param age 
     */
    public void setAge(int age) 
    {
            this.age = age;
    }

    public ArrayList<Relationship> getCurrentRelationships()
    {
            return currentRelationships ;
    }

    public ArrayList<Integer> getCurrentPartnerIds()
    {
            return currentPartnerIds ;
    }
    
    /**
     * 
     * @return (int) the number of current Relationships.
     */
    public int getNumberCurrentRelationships()
    {
        return currentPartnerIds.size() ;
    }

    /**
     * Randomly choose the number of simultaneous relationships an agent may have
     */
    private int initConcurrency()
    {
            concurrency = RAND.nextInt(getMaxRelationships()) + 1 ;
            return concurrency ;
    }

    public int getConcurrency()
    {
            return concurrency ;
    }
    
    	
    abstract int getMaxRelationships() ;
    
    /**
     * 
     * @return (int) the number of orgies in a community per cycle
     */
    //abstract public int getOrgyNumber() ;
    
    /**
     * 
     * @return (int) number of Agents invited to join any given orgy
     */
    abstract public int getGroupSexEventSize();

    /**
     * 
     * @return (double) the probability of Agents joining an orgy when invited
     */
    abstract public double getJoinGroupSexEventProbability() ;
    
    public double getMonogomousOdds()
    {
            return monogomousOdds ;
    }

    public double getRegularOdds()
    {
            return regularOdds ;
    }

    public double getCasualOdds()
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
        String censusReport = "" ;
        censusReport += Reporter.addReportProperty("agentId",agentId) ;
        censusReport += Reporter.addReportProperty("agent",agent) ;
        censusReport += Reporter.addReportProperty("age",getAge()) ;  // Reporter.addReportProperty("startAge", getAge()) ;
        censusReport += Reporter.addReportProperty("concurrency",concurrency) ;
        censusReport += Reporter.addReportProperty("infidelity",infidelity) ;
        censusReport += Reporter.addReportProperty("screenInterval",getScreenCycle()) ;
        
        /*Class fieldClazz ;
        Class agentClazz ;
        String getterName ;
        Method getMethod ;
        for (String fieldName : getCensusFieldNames() )
        {
            censusReport += Reporter.addReportProperty(fieldName,Agent.class.getField(fieldName).get)  ;
            try
            {
                //fieldClazz = this.getClass().getField(fieldName).getClass() ;
                agentClazz = Agent.class ;    // Class.forName("String") ;
                getterName = "get" + fieldName.substring(0,1).toUpperCase() 
                        + fieldName.substring(1) ;
                getMethod = agentClazz.getMethod(getterName, (Class[]) null) ;
                censusReport += Reporter.addReportProperty(fieldName,getMethod.invoke(this)) ;
            }
            catch ( NoSuchMethodException nsme)
            {
                LOGGER.log(Level.INFO, "NSME {0} {1}", new Object[]{fieldName, nsme.getLocalizedMessage()});
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
            //catch ( ClassNotFoundException cnfe)
            {
              //  LOGGER.log(Level.INFO, "CNFE {0} {1}", new Object[]{fieldName, cnfe.getLocalizedMessage()});
            } 
            //catch (NoSuchFieldException nsfe)
            {
              //  LOGGER.log(Level.INFO, "{0} {1}", new Object[]{fieldName, nsfe.getMessage()}) ;
            }
        }*/
        return censusReport ;
    }
    
    /**
     * 
     * @return (String[]) Names of MSM fields of relevance to a census.
     */
    protected String[] getCensusFieldNames()
    {
        return censusFieldNames ;
    }

   
    /**
     * 
     * @return Site[] sites
     */
    abstract public Site[] getSites() ;
    
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

    public void ageOneDay()
    {
        cyclesModYear++ ;
        if (cyclesModYear > 363)
        {
            age++ ;
            cyclesModYear = 0 ;
        }
    }
    
    /**
     * Called to adjust condom use to reflect behavioural trends.
     */
    abstract public void adjustCondomUse() ;
    
    /**
     * Invoked every 365 cycles to startAge the agent by one year.
     * In turn invokes ageEffects() to handle the effects of startAge.
     * @return String description of agentId, startAge, and any altered quantities if any, 
     empty otherwise
     */
    public String ageOneYear()
    {
            String report = "" ;
            age++ ;
            LOGGER.info(String.valueOf(age));
            /*report += ageEffects() ;

            // Prepare report if not still empty.
            if (! report.isEmpty())
            {
                report += Reporter.addReportProperty("agentId",agentId) ;
                report += Reporter.addReportProperty("startAge",startAge) ;
            }*/
            return report ;
    }

    /**
     * Promiscuity and infidelity decrease from startAge == 30
     * 
     * @return String description of any altered quantities if any, empty otherwise
     */
    protected String ageEffects()
    {
        String report = "" ;
        if (age > 30)
        {
            // TODO: Modify propensity to consent to Casual Relationships
            //report += Reporter.addReportProperty("consentCasual",concurrency) ;
            if (infidelity > 0.0)
            {
                infidelity *= INFIDELITY_FRACTION ;
                report += Reporter.addReportProperty("infidelity",infidelity) ;
            }
        }
        return report ;
    }

    /**
     * 
     * @param relationshipClazzName
     * @param partner
     * @return true if Agent decides to use a condom, false otherwise
     */
    abstract protected boolean chooseCondom(String relationshipClazzName, Agent partner);
            
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
    public boolean setSymptomatic(Site site)
    {
            return symptomatic = (symptomatic || site.getSymptomatic()) ;
    }

    /** screenTime setter(). */
    public void setScreenTime(int time)
    {
        screenTime = time ;
    }

    /** screenTime getter(). */
    public int getScreenTime()
    {
        return screenTime ;
    }

    /** screenCycle setter(). */
    public void setScreenCycle(int screen)
    {
        screenCycle = screen ;
    }

    /** screenCycle getter(). */
    public int getScreenCycle()
    {
        return screenCycle ;
    }
    
    protected void decrementScreenTime()
    {
        screenTime-- ;
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
     * Invoked when Agent is symptomatic. Call each site.treat(). If all treatments successful, call clearSymptomatic()
     * @return true if all sites successfully treated, false otherwise
     */
    public boolean treatSymptomatic()
    {
        Site[] sites = getSites() ;
        boolean successful = true ;
        for (Site site : sites)
            site.treat() ;
//            if ((site.getInfectedStatus()!=0))
//                successful = (successful && site.treat()) ;
        //if (successful) 
            infectedStatus = false ;
            clearSymptomatic();
        screenTime = screenCycle ;
        return successful ;
    }

    /**
     * Invoked when Agent is asymptomatic. Call each site.treat(). If all treatments successful, call clearSymptomatic()
     * @return true if all sites successfully treated, false otherwise
     */
    public boolean treat()
    {
        Site[] sites = getSites() ;
        boolean successful = true ;
        for (Site site : sites)
            if ((site.getInfectedStatus()!=0))
                site.clearInfection();
        infectedStatus = false ;
        clearSymptomatic();
        screenTime = screenCycle ;
        return successful ;
    }

    /**
     * Set symptomatic to false. Called only when all sites have been successfully treated.
     */
    protected void clearSymptomatic()
    {
        symptomatic = false ;
    }
    
    public void clearInfection()
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

    /**
     * Setter of infectedStatus, and also ensures that symptomatic is
     * false if infectedStatus is false.
     * @param infected 
     */
    public void setInfectedStatus(boolean infected)
    {
        infectedStatus = infected ;
        symptomatic = symptomatic && infectedStatus ;
    }
    
    /**
     * Progress infection, invoking each site.progressInfection and 
     * keeping track of whether the Agent is still infected.
     * @return (boolean) whether the infection was cleared
     */
    public boolean progressInfection()
    {
        Site[] sites = getSites() ;
        boolean stillInfected = false ;
        for (Site site : sites)
        {
            stillInfected = (stillInfected || (site.progressInfection() > 0)) ;
        }
        setInfectedStatus(stillInfected) ;
        return !stillInfected ;
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
     * the number of relationships already entered compared to concurrency.
     * It is advisable for subclasses of Agent to invoke this Method if they 
     * override with super.consent() .
     * 
     * @param relationshipClazzName - name relationship subclass
     * @param partner - agent for sharing proposed relationship
     * @return (Boolean) true if accept and false otherwise
     */
    public boolean consent(String relationshipClazzName, Agent partner)
    {
        if (inMonogomous)
            if (RAND.nextDouble() > infidelity) 
                return false ;
        if (MONOGOMOUS.equals(relationshipClazzName))
            return (0 == currentRelationships.size()) ;
        if (REGULAR.equals(relationshipClazzName))
            return available ;
        else    // Asking for Casual Relationship
            return consentCasual(partner) ;
        //return available ;
    }
    
    /**
     * Whether to accept proposed Casual Relationship.
     * @param partner (Agent) with whom Relationship is proposed.
     * @return (Boolean) true if accept and false otherwise
     */
    abstract protected boolean consentCasual(Agent partner);
    
    /**
     * Probabilistically decide to accept invitation to join orgy
     * @param args
     * @return true to join and false otherwise
     */
    final public boolean joinGroupSexEvent(Object[] args)
    {
        if (inMonogomous)
            if (RAND.nextDouble() > infidelity)
                return false ;
        return (RAND.nextDouble() < getJoinGroupSexEventProbability()) ;
    }

    /**
     * Sets the availability according to the number of relationships and participation in 
     * a Regular relationship
     * @return (boolean) available
     */
    protected boolean updateAvailable()
    {
        available = (nbRelationships <= concurrency) ;
        return available ;
    }

    protected boolean getAvailable()
    {
        return available ;
    }

    /**
     * Adds relationship to ArrayList relationships and initiates entry into a relationship
     * @param relationship
     * @return String report number of Relationships
     */
    public String enterRelationship(Relationship relationship)
    {
        //String record = Reporter.addReportProperty(Reporter.AGENTID0, agentId) ;
        int partnerId = relationship.getPartnerId(agentId) ;
        //record += Reporter.addReportProperty(Reporter.AGENTID1,partnerId) ;
        //record += relationship.getRelationship() + " " ;

        currentRelationships.add(relationship) ;
        currentPartnerIds.add(partnerId) ;
        nbRelationships++ ;

        updateAvailable() ;

        return relationship.getRecord() ;
        //return record ;
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
        updateAvailable() ;
    }

    /**
     * To avoid double application of Relationship.Methods() we act from the 
     * Agent with the smallest agentId
     * @param relationship
     * @return true if relationship left, false otherwise
     */
    final private String endRelationship(Relationship relationship)
    { 
        String record = "" ;
        //record = Reporter.addReportLabel("death");
        record += Reporter.addReportProperty(Relationship.RELATIONSHIP_ID, relationship.getRelationshipId()) ;
        
        relationship.getPartner(this).leaveRelationship(relationship) ;
        leaveRelationship(relationship) ;

        return record ;
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

    //TODO: Clean up leaveRelationship(int agentNb)
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
     * Calls death() to see if Agent dies and removes from agents if so.
     * @return true if agent dies and false otherwise
     */
    final public boolean grimReaper()
    {
        if (RAND.nextDouble() < getRisk() ) 
        {
            death() ;
            return true ;
        }
        return false ;
    }
    
    /**
     * Based on data from the Australian Bureau of Statistics for Australian 
     * population (http://stat.data.abs.gov.au/).
     * @return (double) probability of dying during any cycle depending on the 
 properties, here startAge, of the Agent.
     */
    protected double getRisk()
    {
        double risk ;
        if (age > 65)
            return 1.0 ;
        else if (age > 60)
            risk = RISK_60 ;
        else if (age > 55) 
            risk = RISK_55 ;
        else if (age > 50) 
            risk = RISK_50 ;
        else if (age > 45) 
            risk = RISK_45 ;
        else if (age > 40) 
            risk = RISK_40 ;
        else if (age > 35) 
            risk = RISK_35 ;
        else if (age > 30) 
            risk = RISK_30 ;
        else if (age > 25)
            risk = RISK_25 ;
        else if (age > 20)
            risk = RISK_20 ;
        else 
            risk = RISK_15 ;
        double noRisk = Math.pow((1 - risk/1000),1/DAYS_PER_YEAR) ;
        return 1 - noRisk ;
    }
        
        
    /**
     * Make agent die and clear their Relationships
 Default based on startAge, uniform probability with cutoff at maxLife
     * @return true if they die, false otherwise
     */
    final private String death()
    {
        String report = Reporter.addReportLabel("death") ; 
        //report += Reporter.addReportProperty("agentId",agentId) ;
        //report += Reporter.addReportProperty("startAge",startAge) ;
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
        
        int startIndex = (nbRelationships - 1) ;
        String record = "" ;
        for (int relationshipIndex = startIndex ; relationshipIndex >= 0 ; 
                relationshipIndex-- )
        {
            relationship = currentRelationships.get(relationshipIndex) ;
            Agent agentLowerId = relationship.getLowerIdAgent() ;
            record += agentLowerId.endRelationship(relationship) ;
        }
        Relationship.APPEND_DEATH_RECORD(record);
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
