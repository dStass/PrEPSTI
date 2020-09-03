/**
 * 
 */
package PRSP.PrEPSTI.community;

import PRSP.PrEPSTI.agent.*;
import PRSP.PrEPSTI.site.*;
import PRSP.PrEPSTI.reporter.*;

import java.io.*;
//import java.io.FileWriter ;
//import java.io.IOException;

//import reporter.* ; 
//import reporter.ScreeningReporter ;
import PRSP.PrEPSTI.reporter.presenter.*;

import PRSP.PrEPSTI.configloader.ConfigLoader;
import PRSP.PrEPSTI.mdll.MDLL;
import PRSP.PrEPSTI.mdll.MDLLForwardIterator;
import PRSP.PrEPSTI.mdll.MDLLNode;

import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/******************************************************************
 * @author Michael Luke Walker
 *
 *******************************************************************/
public class Community {
    
    // Default variables
    static public String FILE_PATH ;
    static public String NAME_ROOT ;
    static public int POPULATION ;
    static public String COMMENT ;
    static public boolean DYNAMIC ; // Whether parameters change throughout simulation.
    static public String REBOOT_PATH ;
    static public String REBOOT_SIMULATION; // "to2014fix3Choice23aaPop40000Cycles4745" ; // "debugRebootPop20000Cycles1825" ; 
    static public boolean PLOT_FILE ; // Whether to try and plot figures after a simulation, not on an HPC usually
    static public int REBOOT_FROM_CYCLE = -1 ;  // DEFAULT = -1, reboot from the end using -REBOOT file, otherwise generate $REBOOT_FROM_CYCLE-REBOOT file
    static public int LOADED_REBOOT_FROM_CYCLE = -1 ;  // DEFAULT = -1, reboot from the end using -REBOOT file, otherwise generate $REBOOT_FROM_CYCLE-REBOOT file
    
    // hashmap with key = method name, value = hashmap that contains
    // variable names and its literal value as a String
    // static public HashMap<String, HashMap> METHOD_CONFIG;

    // input variables
    static public int LOADED_MAX_CYCLES ;
    static private int MAX_CYCLES ;
    static public int START_YEAR ;
    static public int END_YEAR ;
    
    // derived variables
    static public String SIM_NAME;
    static String NAME_SUFFIX;
    static int AGENTS_PER_DAY;

    //static public String FILE_PATH = "/srv/scratch/z3524276/prepsti/output/test/" ;
    //static public String FILE_PATH = "/short/is14/mw7704/prepsti/output/year2007/" ;
    
    
    /** Dump reports to disk after this many cycles. */
    static int DUMP_CYCLE = 2000 ; // ((int) Math.pow(10, 7))/POPULATION ;
    /** Whether to dump partial reports during simulation. */
    static final boolean PARTIAL_DUMP = (DUMP_CYCLE > 0) ;
    
    /**
     * (String) Name of previous burn-in to reload.
     * Not reloaded if this is an empty string.
     */
    static final String RELOAD_BURNIN = "" ; // "seek2Pop40000Cycles730" ;
    
    /**
     * (String) Name of previous simulation to reload.
     * Nothing reloaded if this is an empty string.
     */
    
    static public String getFilePath()
    {
        return FILE_PATH ;
    }
    
    /** Generate and record Random number seed. */
    static long RANDOM_SEED = System.nanoTime() ;
    static Random RAND = new Random(RANDOM_SEED) ;
    
    /** Agents in population */
    // private ArrayList<Agent> agents = new ArrayList<Agent>() ;
    private MDLL<Agent> agentsMDLL = new MDLL<Agent>();
    
    /** 
     * Agents in population who have not yet changed to current yearly parameter settings.
     */
    ArrayList<Agent> unchangedAgents ;
    /**
     * Index indicating Agents who have not yet changed to current yearly parameter settings
     */
    private int unchangedIndex0 ;
    private int unchangedIndex1 ;
    
    /** Total number of agents. */
    private int population = POPULATION ;
    ///** Current number of sexually live agents */
    //private int currentPopulation = population ;

    // Number of new population members and also average number of deaths per cycle
    private double birthRate = population/(50 * 365) ;
    private int birthBase = (int) Math.floor(birthRate);
    private double birthRemainder = birthRate - birthBase ;

    //private ArrayList<Relationship> relationships = new ArrayList<Relationship>() ;

    public static String TRUE = "true" ;
    public static String FALSE = "false" ;
    
    private String initialRecord ;
    protected ArrayList<String> relationshipReport = new ArrayList<String>() ;
    protected ArrayList<String> encounterReport = new ArrayList<String>() ;
    protected ArrayList<String> screeningReport = new ArrayList<String>() ;
    protected ArrayList<String> populationReport = new ArrayList<String>() ;
    private Scribe scribe = new Scribe() ;
    // Define Scribe in full in Community() constructor
    //private Scribe scribe = new Scribe(SIM_NAME, new String[] {"relationship","encounter","screening", "population"}) ;

    // output to small file when cycling through parameter values
    static private String OUTPUT_RETURN = "" ;

    // Logger
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("reporter") ;
	private static final String[] RELATIONSHIP_CLAZZ_NAMES = new String[] {"Casual","Regular","Monogomous"};
    public static ArrayList<String[]> timeStamps = null;
    public static ConcurrentHashMap<String, Float> methodsTimeStamp = null;
    static Long timeInitial = null;


    public static void main(String[] args) {
        timeStamps = new ArrayList<String[]>();
        methodsTimeStamp = new ConcurrentHashMap<String, Float>();


        timeInitial = System.nanoTime();

        Community.timeStamps.add(new String[] {"initialTime", String.valueOf(0)});


        float timeAging = 0;
        float timeSubmit = 0;
        float timeDumping = 0;

        float t1 = 0;
        float t2 = 0;
        ConfigLoader.load();  // set static variables

        // pause simulation while config data are read in
        try { Thread.sleep(100); }
        catch (InterruptedException e) { e.printStackTrace(); }
        
        // derived variables
        Community.AGENTS_PER_DAY = Community.POPULATION / 365 ;

        // MAX_CYCLES
        Community.MAX_CYCLES = Community.generateTrueCycles(Community.LOADED_MAX_CYCLES) ;
        
        // REBOOT_FROM_CYCLE
        Community.REBOOT_FROM_CYCLE = Community.generateTrueCycles(Community.LOADED_REBOOT_FROM_CYCLE) ;
        
        // Pop[POPULATION]Cycles[MAX_CYCLES]
        Community.NAME_SUFFIX = "Pop" + String.valueOf(Community.POPULATION) 
                              + "Cycles" + String.valueOf(Community.MAX_CYCLES);

        // handle arguments 
        switch (args.length) 
        {
            default:
            case 4:
                switch (args[3]) 
                {
                case "gadi":
                    //Community.FILE_PATH = "/scratch/is14/mw7704/prepsti/" + Community.FILE_PATH; 
                    Community.DUMP_CYCLE = 2000 ;
                    break ;
                case "katana":
                    //Community.FILE_PATH = "/srv/scratch/z3524276/prepsti/" + Community.FILE_PATH ;
                    Community.DUMP_CYCLE = 2000 ;
                    break ;
                default:
                	if (!args[3].isEmpty())
                		REBOOT_SIMULATION = args[3] ;
                    break ;
                } ;
            case 3:
                switch (args[2]) 
                {
                    case "gadi":
                        //Community.FILE_PATH = "/scratch/is14/mw7704/prepsti/" + Community.FILE_PATH; 
                        Community.DUMP_CYCLE = 2000 ;
                        break ;
                    case "katana":
                        //Community.FILE_PATH = "/srv/scratch/z3524276/prepsti/" + Community.FILE_PATH ;
                        Community.DUMP_CYCLE = 2000 ;
                        break ;
                    default:
                    	if (!args[2].isEmpty())
                    		REBOOT_SIMULATION = args[2] ;
                        break ;
                } ;
            case 2: 
            	Community.FILE_PATH = args[1] ;
            case 1:
                Community.NAME_ROOT = args[0] ;
            case 0:
                break ;
        }

        Community.SIM_NAME = Community.NAME_ROOT ; // + Community.NAME_SUFFIX;
        Community.OUTPUT_RETURN += Community.SIM_NAME + " " ;
        
        // if (args.length > argIndex)
        // {
        //     LOGGER.info(args[argIndex]);
        //     if (args[argIndex].equals("gadi"))
        //     {
        //         FILE_PATH = "/scratch/is14/mw7704/prepsti/" + FILE_PATH ;
        //         DUMP_CYCLE = 500 ;
        //     }
        //     else if (args[argIndex].equals("katana"))
        //     {
        //         FILE_PATH = "/srv/scratch/z3524276/prepsti/" + FILE_PATH ;
        //         DUMP_CYCLE = 500 ;
        //     }
        //     argIndex++ ;
        // }
        // /*
        // if (args.length > argIndex)
        // {
        //     MSM.SET_ADJUST_CASUAL_CONSENT(Double.valueOf(args[argIndex]));
        //     argIndex++ ;
        // }
        // */


        // if (args.length > argIndex)
        // {
        //     int siteIndex = 0 ;
        //     for (String infected : MSM.SITE_NAMES)
        //         for (String clear : MSM.SITE_NAMES)
        //         {
        //             MSM.SET_INFECT_PROBABILITY(infected, clear, Double.valueOf(args[argIndex + siteIndex])) ;
        //             OUTPUT_RETURN += args[argIndex + siteIndex] + " " ;
        //             siteIndex++ ;
        //         }
        //     if (siteIndex != 9)    // 9 transmissionProbabilities or none
        //         LOGGER.severe("Transmission probabilities missing. Only found " + String.valueOf(siteIndex-3) + " out of 9") ;
        // }
        
        COMMENT += MSM.TRANSMISSION_PROBABILITY_REPORT() ;
        
        Community.ADD_TIME_STAMP("before reports/presenters created");

        // Needed to avoid NoClassDefFoundError on HPC
        PopulationReporter populationReporter = new PopulationReporter() ;
        RelationshipReporter relationshipReporter = new RelationshipReporter() ;
        EncounterReporter encounterReporter = new EncounterReporter() ;
        ScreeningReporter screeningReporter = new ScreeningReporter() ;
        
        PopulationPresenter populationPresenter = new PopulationPresenter() ;
        RelationshipPresenter relationshipPresenter = new RelationshipPresenter() ;
        EncounterPresenter encounterPresenter = new EncounterPresenter() ;
        ScreeningPresenter screeningPresenter = new ScreeningPresenter() ;

        // Record starting time to measure running time
        long startTime = System.nanoTime();
        Community.ADD_TIME_STAMP("after reporters/presenters created");
        // LOGGER.log(Level.INFO, "Seed:{0}", System.currentTimeMillis());
    
        // Establish Community of Agents for simulation
        LOGGER.info(SIM_NAME);
        Community community = new Community(REBOOT_SIMULATION, REBOOT_FROM_CYCLE) ;
        

        Community.ADD_TIME_STAMP("new community created");

        // Establish conditions for specific simulation questions
        //System.out.println(community.initialiseCommunity()) ;

        // For generating reports
        String relationshipRecord ;
        String encounterRecord ;
        //String clearanceRecord ;
        String screeningRecord ;
        String populationRecord ;
        //String deathReport ;
        //String censusRecord ;

        // To record cycle number in every record
        String cycleString ;
        
        // System.out.println("population: " + POPULATION + ", Cycles: " + Community.MAX_CYCLES );
        /*
        int outputInterval ;
        if (POPULATION < MAX_CYCLES)
            outputInterval = POPULATION/2 ;
        else
            outputInterval = POPULATION / (MAX_CYCLES) ;
        if (outputInterval < 100)
            outputInterval = 100 ;
        */
        
        

        if (!REBOOT_SIMULATION.isEmpty())
        {
            StringBuilder sbRelationshipRecords = new StringBuilder();
            // add original BURNIN_COMMENCE
            // relationshipRecordsArrayList.add(Relationship.BURNIN_COMMENCE);


            for (Agent agent : community.agentsMDLL)
            {
                for (Relationship relationship : agent.getCurrentRelationships())
                {
                    if (relationship.getLowerIdAgent() == agent) {
                        // relationshipRecordsArrayList.add(0, relationship.getRecord());
                        sbRelationshipRecords.append(relationship.getRecord());
                        // Relationship.BURNIN_COMMENCE = relationship.getRecord() + Relationship.BURNIN_COMMENCE ;
                    }
                }
            }
            sbRelationshipRecords.append(Relationship.BURNIN_COMMENCE);
            Relationship.BURNIN_COMMENCE = sbRelationshipRecords.toString();
        }
        else if (RELOAD_BURNIN.isEmpty())
        {
            // Generate relationships for simulation
            String commenceString ;
            String breakupString ;
            String relationshipId ;
            HashMap<Object,String> commenceMap = new HashMap<Object,String>() ;
            ArrayList<String> commenceList = new ArrayList<String>() ;
            ArrayList<Comparable> breakupList ;
            float timeGeneratingRel = 0;
            
            LOGGER.info("burning in Relationships") ;
            for (int burnin = 0 ; burnin < 2500 ; burnin++ ) // 20000
            {   
                t1 = System.nanoTime();
                commenceString = community.generateRelationships() ;
                timeGeneratingRel += (System.nanoTime() - t1);

                commenceList = Reporter.EXTRACT_ARRAYLIST(commenceString, Reporter.RELATIONSHIPID) ;
                for (String commence : commenceList)
                {
                    //if (!commence.contains("Casual"))
                    {
                        relationshipId = Reporter.EXTRACT_VALUE(Reporter.RELATIONSHIPID, commence) ;
                        commenceMap.put(relationshipId,commence) ;
                    }
                }

                breakupString = community.clearRelationships(); // .substring(6) ;
                breakupList = Reporter.EXTRACT_ALL_VALUES(Reporter.RELATIONSHIPID, breakupString) ;
                for (Comparable breakup : breakupList)
                    if (commenceMap.containsKey(breakup))
                        commenceMap.remove(breakup) ;
            }
            StringBuilder sbBurninCommence = new StringBuilder();

            for (String commence : commenceMap.values()) {
                // sbBurninCommence.insert(commence)
                // Relationship.BURNIN_COMMENCE = commence + Relationship.BURNIN_COMMENCE ;
                sbBurninCommence.append(commence);
            }
            sbBurninCommence.append(Relationship.BURNIN_COMMENCE);
            Relationship.BURNIN_COMMENCE = sbBurninCommence.toString();
        }
        else
        {
            Relationship.BURNIN_COMMENCE = community.reloadBurnin() + " clear:";
            Relationship.BURNIN_BREAKUP = "" ;
            Community.COMMENT += "Burnin reloaded from " + RELOAD_BURNIN ;
        }

        Community.ADD_TIME_STAMP("after burnin");        
        // simulation of maxCycles cycles
        
        cycleString = "0," ;
        StringBuilder sbPopulationRecord = new StringBuilder();
        sbPopulationRecord.append(cycleString);
        sbPopulationRecord.append(Reporter.ADD_REPORT_LABEL("birth"));
        sbPopulationRecord.append(community.initialRecord);
        // populationRecord = cycleString + Reporter.ADD_REPORT_LABEL("birth") + community.initialRecord ;
        
        //outputInterval = 1 ;

        float timeGenRel = 0;
        float timeRunEnc = 0;
        float timeProInf = 0;
        float timeClearRel = 0;
        float timeIntCom = 0;
        float timeGrimReaper = 0;

        for (int cycle = 0; cycle < Community.MAX_CYCLES; cycle++)
        {	
            //if ((cycle % 10) == 0) //((cycle/outputInterval) * outputInterval))
              // logger.log(level.info, "Cycle no. {0}", cycleString);

        	StringBuilder sbRelationshipRecord = new StringBuilder();

            t1 = System.nanoTime();
            if (DYNAMIC)
                sbPopulationRecord.append(community.interveneCommunity(cycle));
            // populationRecord += community.interveneCommunity(cycle) ;
            timeIntCom += (System.nanoTime() - t1);
            //LOGGER.log(Level.INFO,"{0} {1}", new Object[] {Relationship.NB_RELATIONSHIPS,Relationship.NB_RELATIONSHIPS_CREATED});
            // update relationships and perform sexual encounters, report them
            //LOGGER.info("generate") ;
            t1 = System.nanoTime();
            sbRelationshipRecord.append(cycleString) ;
            sbRelationshipRecord.append(community.generateRelationships()) ;
            // relationshipRecord = cycleString + community.generateRelationships();
            timeGenRel += (System.nanoTime() - t1) ;
            
            //LOGGER.info("encounter");
            t1 = System.nanoTime();
            encounterRecord = cycleString + community.runEncounters();
            timeRunEnc += (System.nanoTime() - t1);
            
            //LOGGER.info("clear");
            t1 = System.nanoTime();
            sbRelationshipRecord.append(community.clearRelationships()) ;
            // relationshipRecord += community.clearRelationships() ;
            timeClearRel += (System.nanoTime() - t1) ;

            // treat symptomatic agents
            
            //LOGGER.info("progress");
            t1 = System.nanoTime();
            screeningRecord = cycleString + community.progressInfection() ;
            timeProInf += (System.nanoTime() - t1);
            
            //deathRecord = cycleString
            int deltaPopulation = community.agentsMDLL.size() ;  // Current population
            
            //LOGGER.info("death");
            t1 = System.nanoTime();
            sbPopulationRecord.append(community.grimReaper());
            // populationRecord += community.grimReaper() ;
            timeGrimReaper += (System.nanoTime() - t1);
            // Record Relationships ended due to death
            
            sbRelationshipRecord.append(Relationship.READ_DEATH_RECORD());
            // relationshipRecord += Relationship.READ_DEATH_RECORD() ;
            
            // How many births to maintain population?
            deltaPopulation = deltaPopulation - community.agentsMDLL.size() ;

            t1 = System.nanoTime();
            community.submitRecords(sbRelationshipRecord.toString(),encounterRecord,screeningRecord,sbPopulationRecord.toString()) ;  // 
            t2 = System.nanoTime();
            timeSubmit += (t2-t1);
            // Deal with effects of aging.
            // To include in populationRecord move this above community.submitRecords()
            t1 = System.nanoTime();
            community.ageOneDay();
            t2 = System.nanoTime();
            timeAging += (t2-t1);

            if (PARTIAL_DUMP)
                if ((((cycle+1)/DUMP_CYCLE) * DUMP_CYCLE) == (cycle+1) )
                {   
                    // Community.ADD_TIME_STAMP("before dump");
                    t1 = System.nanoTime();
                    community.dump();
                    t2 = System.nanoTime();
                    timeDumping += (t2-t1);
                    // Community.ADD_TIME_STAMP("after dump");

                }
            cycleString = Integer.toString(cycle+1) + "," ;
            sbPopulationRecord = new StringBuilder();
            sbPopulationRecord.append(cycleString);
            sbPopulationRecord.append(community.births(deltaPopulation, cycle));
            // populationRecord = cycleString + community.births(deltaPopulation, cycle) ;
        }
        // Final dump() or whole dump if no partial dumps
        if (!PARTIAL_DUMP || (((Community.MAX_CYCLES)/DUMP_CYCLE) * DUMP_CYCLE) != Community.MAX_CYCLES ) {
            t1 = System.nanoTime();
            community.dump() ;
            t2 = System.nanoTime();
            timeDumping += (t2-t1);
        }
        
        Community.ADD_TIME_STAMP("after all dumps, time_aging = " + String.valueOf(timeAging/1000000000f)
            + ", \ntimeSubmit = " + String.valueOf(timeSubmit/1000000000f)
            + ", \ntimeAging = " + String.valueOf(timeAging/1000000000f)
            + ", \ntimeDumping = " + String.valueOf(timeDumping/1000000000f)
            + ", \ntimeGenRel = " + String.valueOf(timeGenRel/1000000000f)
            + ", \ntimeGrimReaper = " + String.valueOf(timeGrimReaper/1000000000f)
            + ", \ntimeProInf = " + String.valueOf(timeProInf/1000000000f)
            + ", \ntimeIntCom = " + String.valueOf(timeIntCom/1000000000f)
            + ", \ntimeClearRel = " + String.valueOf(timeClearRel/1000000000f)
            + ", \ntimeRunEnc = " + String.valueOf(timeRunEnc/1000000000f));
        

        t1 = System.nanoTime();
        community.dumpMetaData() ;
        t2 = System.nanoTime();
        float timeDumpMeta = (t2-t1);

        t1 = System.nanoTime();
        community.dumpRebootData() ;
        t2 = System.nanoTime();
        float timeDumpReboot = (t2-t1);

        Community.ADD_TIME_STAMP("after dumping meta: "+ String.valueOf(timeDumpMeta/1000000000f) 
            + " \n reboot: " + String.valueOf(timeDumpReboot/1000000000f));

        long elapsedTime = System.nanoTime() - startTime ;
        long milliTime = elapsedTime/1000000 ;
        int seconds = (int) milliTime/1000 ;
        int minutes = seconds/60 ;
        System.out.println("population: " + POPULATION + ", Cycles: " + MAX_CYCLES);
        System.out.println("Elapsed running time: " + seconds + "seconds") ;
        System.out.println("Elapsed running time: " + minutes + "minutes") ;
        
        String[] relationshipClassNames = new String[] {"Casual","Regular","Monogomous"} ; // "Casual","Regular","Monogomous"
        
        
        encounterReporter = new EncounterReporter(SIM_NAME, FILE_PATH) ;
        screeningReporter = new ScreeningReporter(SIM_NAME, FILE_PATH) ;

        //Community.ADD_TIME_STAMP("after new encounter and screening Reporters");
        
        //screeningReporter = new ScreeningReporter(SIM_NAME,FILE_PATH) ;
                //new ScreeningReporter("prevalence",community.screeningReport) ;
                
        //ArrayList<Object> pharynxPrevalenceReport = screeningReporter.preparePrevalenceReport("Pharynx") ;
        //Reporter.WRITE_CSV(pharynxPrevalenceReport, "Pharynx", SIM_NAME, FILE_PATH);
        //ScreeningPresenter screeningPresenter 
          //      = new ScreeningPresenter("prevalence",Community.SIM_NAME,screeningReporter) ;
        //screeningPresenter.multiPlotScreening(new Object[] {"prevalence", new String[] {"Pharynx","Urethra","Rectum"},"coprevalence",new String[] {"Rectum","Pharynx"}});
        //ScreeningPresenter screeningPresenter2 
          //      = new ScreeningPresenter("prevalence",Community.SIM_NAME,screeningReporter) ;
        //screeningPresenter2.plotPrevalence();
        //screeningPresenter2.plotNotificationsPerCycle();


        // David, please do not comment this out! It is useful to me.
        LOGGER.info(MSM.TRANSMISSION_PROBABILITY_REPORT());

        if (PLOT_FILE)
        {
            ScreeningPresenter screeningPresenter3 = new ScreeningPresenter(SIM_NAME, "multi_prevalence", screeningReporter) ;
            screeningPresenter3.multiPlotScreening(new Object[] {"prevalence","prevalence", new String[] {"Pharynx","Rectum","Urethra"}}) ;  // ,"coprevalence",new String[] {"Pharynx","Rectum"},new String[] {"Urethra","Rectum"}
            // screeningPresenter3.plotYearsAtRiskIncidenceReport(new String[] {"Pharynx","Rectum","Urethra"}, 3, 2020, "statusHIV");
        }
        //Community.ADD_TIME_STAMP("after PLOT_FILE");
        HashMap<Object,Number> finalNotificationsRecord = new HashMap<Object,Number>() ;
        
        for (boolean unique : new boolean[] {})    // false,
        {
            // LOGGER.info("unique:" + String.valueOf(unique)) ;
            //HashMap<Object,Number> finalPositivityRecord = new HashMap<Object,Number>() ;
            String notificationsRecord = screeningReporter.prepareFinalNotificationsRecord(new String[] {"Pharynx","Rectum","Urethra"}, unique, 0, Reporter.DAYS_PER_YEAR) ;
            //HashMap<Object,Number[]> notificationsRecord = screeningReporter.prepareFinalNotificationsRecord(new String[] {"Pharynx","Rectum","Urethra"}, unique, 0, Reporter.DAYS_PER_YEAR) ;
            for (Object key : new String[] {"Pharynx","Rectum","Urethra","all"})
            {
                if (unique)
                    finalNotificationsRecord.put(key, Double.valueOf(Reporter.EXTRACT_VALUE(key.toString(), notificationsRecord))) ; //.get(key)[0]) ;
                //finalPositivityRecord.put(key, notificationsRecord.get(key)[1]) ;
            }
            //LOGGER.log(Level.INFO, "Positivity unique:{0} {1}", new Object[] {unique,finalPositivityRecord});
            OUTPUT_RETURN += Reporter.EXTRACT_VALUE("all",notificationsRecord) ; //.("all")[0] + " " ;
            //community.dumpOutputReturn() ;
        }

        //Community.ADD_TIME_STAMP("after bool unique");
        
        //LOGGER.log(Level.INFO, "Notification rate {0}", new Object[] {finalNotificationsRecord});
        String[] siteNames = new String[] {"Pharynx","Rectum","Urethra"} ;
        //screeningReporter = new ScreeningReporter(SIM_NAME,FILE_PATH) ;
        //String prevalenceReports = "" ;
        ArrayList<String> prevalenceReport ;
        for (String siteName : siteNames)
        {
            prevalenceReport = screeningReporter.preparePrevalenceReport(siteName) ;
            //LOGGER.info(String.valueOf(prevalenceReport.size())) ;
            // LOGGER.log(Level.INFO,"{0} {1}", new Object[] {siteName, prevalenceReport.get(prevalenceReport.size() - 1)}) ;
        }
        prevalenceReport = screeningReporter.preparePrevalenceReport() ;
        //Community.ADD_TIME_STAMP("after prev reports");
        // LOGGER.log(Level.INFO,"{0} {1}", new Object[] {"all", prevalenceReport.get(prevalenceReport.size() - 1)}) ;

        HashMap<Comparable,String> incidenceReport = new HashMap<Comparable,String>() ;
        HashMap<Comparable,String> incidenceReportPrep = new HashMap<Comparable,String>() ;
        if (DYNAMIC)
        {
            incidenceReport = screeningReporter.prepareYearsAtRiskIncidenceReport(siteNames, END_YEAR + 1 - START_YEAR, END_YEAR, "statusHIV") ;
            incidenceReportPrep = screeningReporter.prepareYearsAtRiskIncidenceReport(siteNames, END_YEAR + 1 - START_YEAR, END_YEAR, "prepStatus") ;
        }
        //Community.ADD_TIME_STAMP("after incidence DYNAMIC");

        //String finalPrevalencesRecord = screeningReporter.prepareFinalPrevalencesSortedRecord(siteNames, "statusHIV") ;
        //LOGGER.log(Level.INFO, "prevalence {0}", finalPrevalencesRecord) ;
        
        //EncounterReporter encounterReporter = new EncounterReporter("Agent to Agent",community.encounterReport) ;
        //encounterReporter = new EncounterReporter(Community.SIM_NAME,Community.FILE_PATH) ;
        
        String finalAtRiskString = screeningReporter.prepareFinalAtRiskIncidentsRecord(siteNames, 0, "statusHIV");
        
        // commented out:
        // String[] finalAtRiskArray = finalAtRiskString.split(" ");
        // int total = 4;
        // String hivStatusDifferences = "";
        // for (int i = 0; i < total; ++i)
        // {   
        //     String[] falseProperty = finalAtRiskArray[i].split(":");
        //     String[] trueProperty = finalAtRiskArray[i + total].split(":");
        //     hivStatusDifferences += trueProperty[0] + "-" + falseProperty[0] + ": " + String.valueOf(Float.valueOf(trueProperty[1]) - Float.valueOf(falseProperty[1])) + "\n";
        // }
        // LOGGER.info("by HIV-status " + finalAtRiskString) ;
        // LOGGER.info("differences:\n" + hivStatusDifferences) ;


        LOGGER.info("by HIV-status " + finalAtRiskString) ;
        //LOGGER.info("Incidence " + encounterReporter.prepareFinalIncidenceRecord(new String[] {"Pharynx","Rectum","Urethra"}, 0, 0, 365, MAX_CYCLES).toString());
        // LOGGER.info("Incidence " + encounterReporter.prepareSortedFinalIncidenceRecord(siteNames, 0, 0, 365, MAX_CYCLES, "statusHIV").toString());


        // old code
        //            EncounterPresenter encounterPresenter
//                    = new EncounterPresenter(SIM_NAME,"multi prevalence",encounterReporter) ;
//            encounterPresenter.multiPlotScreening(new Object[] {"prevalence","prevalence",new String[] {"Pharynx","Rectum","Urethra"}}) ;  // ,"coprevalence",new String[] {"Pharynx","Rectum"},new String[] {"Urethra","Rectum"}
        //HashMap<Object,Number> finalTransmissionsRecord = encounterReporter.prepareFinalIncidenceRecord(new String[] {"Pharynx","Rectum","Urethra"}, 0, Reporter.DAYS_PER_YEAR) ;
        //LOGGER.log(Level.INFO, "{0}", finalTransmissionsRecord);
//        EncounterPresenter encounterPresenter = new EncounterPresenter(Community.SIM_NAME,"agent to agent", encounterReporter) ;
//        encounterPresenter.plotCondomUse();
//        EncounterPresenter encounterPresenter2 = new EncounterPresenter(Community.SIM_NAME,"agent to agent", encounterReporter) ;
//        encounterPresenter2.plotProtection();
        //encounterPresenter.plotNbTransmissions(); 
        //encounterPresenter.plotTransmittingSites(new String[] {"Urethra","Rectum","Pharynx"});
        //encounterPresenter.plotFromSiteToSite(new String[] {"Urethra","Rectum","Pharynx"});
        //encounterPresenter.plotAgentToAgentNetwork();
        
        //PopulationReporter populationReporter = new PopulationReporter("deaths per cycle",community.populationReport) ;
        //PopulationPresenter populationPresenter = new PopulationPresenter("age-at-death","age-at-death",populationReporter) ;
        //populationPresenter.plotAgeAtDeath();
        //PopulationPresenter populationPresenter = new PopulationPresenter("deaths per cycle","deaths per cycle",populationReporter) ;
        //populationPresenter.plotDeathsPerCycle();
        if (!incidenceReport.isEmpty())
        {
            Reporter.DUMP_OUTPUT("riskyIncidence_HIV",SIM_NAME,FILE_PATH,incidenceReport);
            LOGGER.info(incidenceReport.toString()) ;
            Reporter.DUMP_OUTPUT("riskyIncidence_Prep",SIM_NAME,FILE_PATH,incidenceReportPrep);
        }
        /*Community.ADD_TIME_STAMP("FINAL TIMESTAMP");
        
        LOGGER.info("Time Stamps:");
        double totalTime = Double.valueOf(timeStamps.get(timeStamps.size() - 2)[1]);
        double prev = 0;
        for (String[] s : timeStamps) {
            double curr = Double.valueOf(s[1]);
            double difference = curr - prev;
            double percentage = 100 * (difference / totalTime);
            prev = curr;
            // System.out.println(s[0] + " -> stamp: " + s[1] + ", time taken: "
            //     + String.valueOf(difference) + "s, " + String.valueOf(percentage) + "% of sim");
            //System.out.println(String.valueOf(percentage) + "% : " + s[0] + ", "+ String.valueOf(difference)+"s");
        }
        */
        long timeFinal = System.nanoTime();
        float timeRan = (timeFinal - timeInitial)/  1000000000f;
        LOGGER.info("Task completed in " + String.valueOf(timeRan));

        // System.out.println("\n\nTime taken for each methods:");
        // RECORD_METHOD_TIME("TOTAL", System.nanoTime() - timeInitial);
        // HashMap<String, Float> methodPercentages = FINALISE_METHOD_TIME();
        // ArrayList<String> keys = new ArrayList<String>(Community.methodsTimeStamp.keySet());
        // Collections.sort(keys);
        // for (String s : keys) {
        //     System.out.println("- " + methodPercentages.get(s) * 100 + "% : " + s + " -> " + Community.methodsTimeStamp.get(s) / 1_000_000_000 + "s");
        // }
        // System.out.println("DONE");
    }
 
    public static void ADD_TIME_STAMP(String name) {
        Community.timeStamps.add(new String[] {name, String.valueOf((System.nanoTime() - timeInitial)/1000000000f)});
    }

    public static void RECORD_METHOD_TIME(String name, float time) {
        if (!Community.methodsTimeStamp.containsKey(name)) {
            float zero = 0;
            Community.methodsTimeStamp.put(name, zero);
        }
        Community.methodsTimeStamp.put(name, Community.methodsTimeStamp.get(name) + time);
    }

    public static HashMap<String, Float> FINALISE_METHOD_TIME() {
        HashMap<String, Float> toReturn = new HashMap<String, Float>();
        for (String name : Community.methodsTimeStamp.keySet()) {
            if (name.equals("TOTAL")) continue;
            toReturn.put(name, Community.methodsTimeStamp.get(name) / Community.methodsTimeStamp.get("TOTAL"));
        }
        toReturn.put("TOTAL", (float) 1.0);
        return toReturn;
    }
    
    /**
     * Community object containing all agent(s) and Relationships and methods 
     * for pairing agent(s) into relationship(s) and for ending relationship(s)
     */
    public Community() {
        // Populate community with agents
        initialiseCommunity();
        /*
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
        */
    }
    
    /**
     * Reads seeds for random number generation from saved metadata of simName 
     * and initialises them.
     * @param simName 
     */
    private void rebootRandomSeeds(String folderPath, String simName)
    {
        HashMap<String, Long> seeds = Reporter.parseSeedsFromMetadata(simName, folderPath);
        long seed = Long.valueOf(seeds.get("Community.REBOOT_SEED")) ;
        RANDOM_SEED = seed ;
        RAND = new Random(seed) ;

        seed = Long.valueOf(seeds.get("Agent.REBOOT_SEED")) ;
        Agent.SET_RAND(seed) ;

        seed = Long.valueOf(seeds.get("Site.REBOOT_SEED")) ;
        Site.SET_RAND(seed) ;

        seed = Long.valueOf(seeds.get("Relationship.REBOOT_SEED")) ;
        Relationship.SET_RAND(seed) ;
    }

    private void rebootRandomSeeds(String simName) {
        rebootRandomSeeds(ConfigLoader.REBOOT_PATH, simName);
    }
    
    /**
     * Initialises community based on previously saved simulation, if given.
     * @param simName - Name of simulation to be reloaded.
     * recommence simulation.
     */
    public Community(String simName)
    {
        initialRecord = "" ;

        if (!simName.isEmpty()) rebootRandomSeeds(simName) ;
        initialiseCommunity();         
    }
    
    /**
     * Initialises community based on previously saved simulation, if given.
     * @param simName - Name of simulation to be reloaded.
     * @param fromCycle - the cycle of reloaded simulation from which to 
     * recommence simulation.
     */
    public Community(String simName, int fromCycle)
    {
        long t1 = System.nanoTime();
        if (simName.isEmpty())
            initialiseCommunity();
        else
        {   
            String rebootedSimName = simName;
            String rebootedFolderPath = ConfigLoader.REBOOT_PATH;
            if (fromCycle >= 0) {
                double tbefore = System.nanoTime();
                PopulationReporter populationReporter = new PopulationReporter(simName, ConfigLoader.REBOOT_PATH);
                RelationshipReporter relationshipReporter = new RelationshipReporter(simName, ConfigLoader.REBOOT_PATH);
                ScreeningReporter screeningReporter = new ScreeningReporter(simName, ConfigLoader.REBOOT_PATH);
                double tafter = System.nanoTime();
                Community.ADD_TIME_STAMP("new reports: " + (tafter - tbefore) / 1_000_000_000 + "s");
                
                // generate the exact pseudorandom sequence for agent birth days
                // rebootRandomSeeds(rebootedFolderPath, rebootedSimName) ;
                // for (int i = 0; i < 4; ++i) Agent.GET_NEXT_RANDOM_DOUBLE();

                int cycleToGenerateReportUpTo = fromCycle;

                // generate rebooted metalabels and metadata
                ArrayList<String> metaLabels = new ArrayList<String>() ; 
                ArrayList<Object> metaData = new ArrayList<Object>() ;


                /* * * * * * * * * *
                 *      AGENTS     *
                 * * * * * * * * * */

                tbefore = System.nanoTime();

                // generate our reboot census
                HashMap<Integer, String> populationCensusUpToCycle = populationReporter.prepareCensusReport(cycleToGenerateReportUpTo, screeningReporter);
                tafter = System.nanoTime();
                Community.ADD_TIME_STAMP("after prepareCensusReport: " + (tafter - tbefore) / 1_000_000_000 + "s");

                // extract agent census data and write to internal metadata
                
                // DEBUG: sort agents by id
                // TreeSet<Integer> sortedAgentKeySet = new TreeSet<Integer>();
                // sortedAgentKeySet.addAll(populationCensusUpToCycle.keySet());


                // add rebooted agent data to metadata
                metaLabels.add("Agents") ;
                StringBuilder sbAgentsReboot = new StringBuilder();
                for (Integer agentId : populationCensusUpToCycle.keySet()) {
                    String newAgentRecord = populationCensusUpToCycle.get(agentId);
                    sbAgentsReboot.append(newAgentRecord);
                }
                metaData.add(sbAgentsReboot.toString()) ;


                /* * * * * * * * * *
                 *  RELATIONSHIPS  *
                 * * * * * * * * * */

                // extract relationship data and write to internal metadata
                tbefore = System.nanoTime();
                HashMap<Integer, String> relationshipRecordHashMap = relationshipReporter.prepareRelationshipRecordHashMap(cycleToGenerateReportUpTo);
                tafter = System.nanoTime();
                Community.ADD_TIME_STAMP("after prepareRelationshipRecordHashMap: " + (tafter - tbefore) / 1_000_000_000 + "s");
                
                // DEBUG: sort relationships by id
                // TreeSet<Integer> sortedRelationshipKeySet = new TreeSet<Integer>();
                // sortedRelationshipKeySet.addAll(relationshipRecordHashMap.keySet());

                // add rebooted relationship data to metadata
                metaLabels.add("Relationships") ;
                StringBuilder sbRelationshipReboot = new StringBuilder();
                for (Integer relationshipId : relationshipRecordHashMap.keySet()) {
                    sbRelationshipReboot.append(relationshipRecordHashMap.get(relationshipId));
                    sbRelationshipReboot.append(' ');
                }
                metaData.add(sbRelationshipReboot.toString()) ;
                
                // dump new metadata
                rebootedSimName = simName + "REBOOT" + String.valueOf(fromCycle);
                rebootedFolderPath = Community.FILE_PATH;

                // TODO: extract "test/" from CONFIG
                dumpRebootData(rebootedFolderPath, rebootedSimName, metaLabels, metaData);

                HashMap<String, String> modifiedProperties = new HashMap<String, String>();
                modifiedProperties.put("Community.MAX_CYCLES", String.valueOf(fromCycle));
                Reporter.DUPLICATE_METADATA_WITH_MODIFIED_PROPERTIES
                    (ConfigLoader.REBOOT_PATH, simName, rebootedFolderPath, rebootedSimName, modifiedProperties);
            }

            rebootRandomSeeds(rebootedFolderPath, rebootedSimName) ;
            // this.agents = Agent.REBOOT_AGENTS(ConfigLoader.REBOOT_PATH, simName) ;
            this.agentsMDLL = Agent.REBOOT_AGENTS(rebootedFolderPath, rebootedSimName);
            StringBuilder sbInitialRecord = new StringBuilder();

            this.initialRecord = "" ; 
            for (Agent agent : agentsMDLL)
                sbInitialRecord.append(agent.getCensusReport());
                // initialRecord += agent.getCensusReport() ;
            sbInitialRecord.append("!");
            // initialRecord.concat("!") ;
            this.initialRecord = sbInitialRecord.toString();
            
            Relationship.REBOOT_RELATIONSHIPS(rebootedFolderPath, rebootedSimName, agentsMDLL) ;
            scribe = new Scribe(SIM_NAME, new String[] {"relationship","encounter","screening", "population"}) ;
        }

        long t2 = System.nanoTime();
        Community.RECORD_METHOD_TIME("Community.newCommunity", t2 - t1);
    }

    /**
     * For setting up specific questions. Code will flux dramatically.
     * TODO: Generalise to include agents other than MSM.
     */
    private String initialiseCommunity()
    {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> initRecordArrayList = new ArrayList<String>();
        initRecordArrayList.add("!");
        String report = "" ;
        scribe = new Scribe(SIM_NAME, new String[] {"relationship","encounter","screening", "population"}) ;

        for (int id = 0 ; id <  population ; id++ ) 
        {
            //Class<?> AgentClazz = Class.forName("MSM") ; 
            //Agent newAgent = (Agent) AgentClazz.newInstance() ;
            //Constructor<?> agentConstructor = AgentClazz.getDeclaredConstructors()[0] ;

            // Call generateAgent to get randomly chosen combination of Agent subclasses
            //and to impose initial conditions, such as prepStatus=false

            MSM newAgent = generateAgent(-1) ;  //new MSM(-1) ;
            //newAgent.setPrepStatus(false) ;
            
            // agents.add(newAgent) ;
            agentsMDLL.add(newAgent.getAgentId(), newAgent);

            // Record newAgent for later reporting
            // initialRecord = newAgent.getCensusReport() + initialRecord ;
            initRecordArrayList.add(0, newAgent.getCensusReport());
            //LOGGER.info(initialRecord);
        }

        for (String s : initRecordArrayList) sb.append(s);
        initialRecord = sb.toString();

        //String relationshipRecord = generateRelationships() ;
        // Clear all initial infections
        
        // Infect one RiskyMSM.urethra
//        for (Agent agent : agents) 
//        {
//            // Start with RiskyMSM
//            if (!agent.toString().equals("RiskyMSM"))
//                continue ;
//            MSM msm = (MSM) agent ;
//            
//            // Start with single Rectum infection
//            Site site = msm.getUrethra() ;
//            boolean infected = site.receiveInfection(1.1) ;
//            site.chooseSymptomatic(-1) ;
//            msm.setInfectedStatus(infected);
//            msm.chooseSymptomatic(site) ;
//            break ;
//        }
//        return "Population clear except for one RiskyMSM with asymptomatic Urethral infection." ;
        report = "Currently no Agents on PrEP" ;
        return report ;
    }
    
    /**
     * For addressing specific questions. Code will flux dramatically.
     * TODO: Generalise to not be MSM specific.
     */
    private String interveneCommunity(int cycle)
    {
        // loading in from json
        int startCycle = ConfigLoader.getMethodVariableInteger("community", "interveneCommunity", "startCycle");
        
        // When to end burn-in
        startCycle = Community.generateTrueCycles(startCycle);
        int startYear = START_YEAR - 2007 ;

        // No more burn-in if starting at a later date than 2007
        if (startYear > 0)
            startCycle = 0 ;
        
        if ((cycle < startCycle))
            return "" ;
        
        // Run through year 0
        int year = (cycle - startCycle + 183)/365 + startYear ;
        //if (year == 0)
          //  return "" ;
        
        String report = "" ;
        if ((year - startYear) * 365 == (cycle - startCycle))    // Things to do at the start of each year
        {
            // unchangedAgents = (ArrayList<Agent>) agents.clone() ;
            unchangedAgents = agentsMDLL.toArrayList();
            unchangedIndex1 = unchangedAgents.size() ;
        }
        else
        	unchangedIndex1 -= AGENTS_PER_DAY ;
          //  unchangedAgents.retainAll(agents) ;    // Remove dead Agents
          
        // Choose Agents to change that day
        // ArrayList<Agent> changeAgents = new ArrayList<Agent>() ;
        HashMap<Integer, Agent> changeAgentsHashMap = new HashMap<Integer, Agent>();
        // HashMap<String, Agent> changeAgentsHash
        
        // How many Agents?
        int nbChangeAgents = AGENTS_PER_DAY ;
        
        if (unchangedIndex1 < AGENTS_PER_DAY)    // Clean the leftovers  
            nbChangeAgents = unchangedIndex1 ;
        unchangedIndex0 = unchangedIndex1 - nbChangeAgents ;



        // changeAgents.addAll(unchangedAgents.subList(unchangedIndex0, unchangedIndex1)) ;
        for (int i = unchangedIndex0; i < unchangedIndex1; ++i) {
            changeAgentsHashMap.put(unchangedAgents.get(i).getAgentId(), unchangedAgents.get(i));
        }
        
        // keep ids we want to remove to a list
        ArrayList<Integer> removeIds = new ArrayList<Integer>();
        for (Integer agentId : changeAgentsHashMap.keySet()) {
            if (!agentsMDLL.contains(agentId)) {
                removeIds.add(agentId);
            }
        }

        // remove agents
        for (Integer agentId : removeIds) {
            changeAgentsHashMap.remove(agentId);
        }

        // retained agents
        ArrayList<Agent> changeAgents = new ArrayList<Agent>() ;
        for (Integer agentId : changeAgentsHashMap.keySet()) {
            Agent agent = changeAgentsHashMap.get(agentId);
            changeAgents.add(agent);
        }


        


        //int changeIndex ;
        //for (int index = 0 ; index < nbChangeAgents ; index++ )
        {
        //	changeIndex = unchangedAgents.size() - 1 - index ; 
        //    changeAgents.add(unchangedAgents.get(changeIndex)) ;
        //    unchangedAgents.remove(changeIndex) ;
        }

//        if (unchangedAgents.size() >= AGENTS_PER_DAY)
  //          changeAgents = new ArrayList<Agent>(unchangedAgents.subList(0, AGENTS_PER_DAY)) ;
    //    else    // if (unchangedAgents.size() < AGENTS_PER_DAY)    // Clean the leftovers  
      //      changeAgents.addAll(unchangedAgents) ;
        
        //unchangedAgents.removeAll(changeAgents) ;

        // changeAgents.retainAll(agents) ;
        // Make changes
        report += Agent.REINIT(changeAgents, year) ;
        
        //report = "parameters adjusted according to ARTB" ;  // PrEP introduced" ; // gradually" ;

        return report ;
    }
    
    /**
     * Initialises a new Agent and sets any initial conditions.
     * @param startAge (int) The age with which the new Agent is born to the community.
     * @return MSM subclass newAgent 
     */
    private MSM generateAgent(int startAge)
    {
        // if Agent.subclass == MSM
        MSM newAgent = new MSM(startAge);
        //MSM newAgent = MSM.BIRTH_MSM(startAge);
        
        // Impose initial condition here
            
        return newAgent ;
    }

    /**
     * Generate relationships within the community 
     * by calling appropriate static MSM Method.
     * 
     * @return (String) report of Relationships generated
     */
    private String generateRelationships()    // 
    {
        //String report = "" ;
        float t1 = System.nanoTime();
        // ArrayList<Agent> availableAgents = agentsMDLL.toArrayList() ;
        // ArrayList<Agent> availableAgents = agents;
        MDLL<Agent> availableMDLL = agentsMDLL.returnShuffled();
        
        // Collections.shuffle(availableAgents, RAND) ;
        // for (Agent a : availableAgents) availableMDLL.add(a.getAgentId(), a);

        // System.out.println("timeClone + timeShuffle = " + String.valueOf((System.nanoTime() - t1)/1000000000f));
        String[] relationshipClazzNames ;
        // relationshipClazzNames = new String[] {"Casual","Regular","Monogomous"} ;
        relationshipClazzNames = Community.RELATIONSHIP_CLAZZ_NAMES;
        String toReturn = MSM.GENERATE_RELATIONSHIPS(availableMDLL, relationshipClazzNames) ;
        float t2 = System.nanoTime();
        Community.RECORD_METHOD_TIME("Community.generateRelationships", t2 - t1);
        // System.out.println("generateTime = " + String.valueOf((System.nanoTime() - t1)/1000000000f));
        return toReturn;

    }
    
    /**
     * Returns an ArrayList of Agents seeking a relationshipClazzName Relationship.
     * May be overridden to accomodate serosorting etc.
     * @param relationshipClazzName
     * @param number (int) 
     * @return 
     */
    private ArrayList<ArrayList<Agent>> seekingAgents(ArrayList<Agent> agentList, String relationshipClazzName)
    {
        ArrayList<ArrayList<Agent>> seekingAgentList = new ArrayList<ArrayList<Agent>>() ;
        ArrayList<Agent> list = new ArrayList<Agent>() ;
        // Determine which Agents seek out which Relationship Class
        for (Agent agent : agentList)
            if (agent.seekRelationship(relationshipClazzName))
                list.add(agent) ;
    
        seekingAgentList.add(list) ;

        return seekingAgentList ;    
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
        for (Agent agent : agentsMDLL)
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

            record += Reporter.ADD_REPORT_PROPERTY("groupSex", gseNumber) ;
            // Every Agent in a given orgy has a Casual Relationship with
            //evey other Agent at that orgy
            //FIXME: Sexual contacts between pairs are clustered
            //for (Agent agent0 : gseAgents)
            for (int gseIndex0 = gseAgentStart ; gseIndex0 < gseAgentEnd ; gseIndex0++ )
            {
                Agent agent0 = gseAgents.get(gseIndex0) ;
                //record += Reporter.ADD_REPORT_PROPERTY(Reporter.AGENTID,agent0.getAgentId()) ;
                for (int orgyIndex1 = gseAgentStart ; orgyIndex1 < gseAgentEnd ; orgyIndex1++ )
                {
                    Agent agent1 = gseAgents.get(orgyIndex1) ;
                    if (agent0 == agent1)
                        continue ;    // Require two distinct Agents in a Relationship
                    Casual relationship = new Casual();
                    record += relationship.addAgents(agent0, agent1) ;
    
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
        for (Agent agent : agentsMDLL)
            agent.ageOneDay() ;
    }

    /**
     * 
     * @return String giving each Agent and properties of interest to census
     */
    private String getCensus()
    {
        String record = "" ;
        for (Agent agent : agentsMDLL)
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
        return births(nbBirths,0) ;
    }

    /**
     * Handles introduction of new Agents every cycle
     * Agents are 'born' to the simulation at 16-20 years old.
     * @param nbBirths (int) The number of new Agents to be born.
     * @return 
     */
    private String births(int nbBirths, int cycle)
    {
    	StringBuilder sbRecord = new StringBuilder();
        sbRecord.append("birth:");
        // String record = "birth:" ;
        MSM newAgent ;
        for (int birth = 0 ; birth < nbBirths ; birth++ )
        {
            newAgent = generateAgent(0) ; // MSM.BIRTH_MSM(0) ;
            newAgent.update(Math.floorDiv(cycle, 365)) ;
            // agentsMDLL.add(newAgent) ;
            agentsMDLL.add(newAgent.getAgentId(), newAgent);
            sbRecord.append(newAgent.getCensusReport());
            // record += newAgent.getCensusReport() ;
            //currentPopulation++ ;
        }

        sbRecord.append("!") ;
        return sbRecord.toString() ;
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
    	StringBuffer sbRecord = new StringBuffer();
        sbRecord.append("death:");
        // String record = "death:" ;
        
        // ArrayList<Agent> newAgentsList = new ArrayList<Agent>();
        // MDLL<Agent> newAgentsMDLL = new MDLL<Agent>();
        // MDLLForwardIterator<Agent> agentsMDLLForwardIterator = agentsMDLL.getForwardIterator();
        //for (int agentIndex = agents.size() - 1 ; agentIndex >= 0 ; agentIndex-- )
        // while (agentsMDLLForwardIterator.hasNext())
        // {
        //     Agent agent  = agentsMDLLForwardIterator.next();
        //     //agent = agents.get(agentIndex) ; 
        //     if (agent.grimReaper())
        //     {
        //         //agents.remove(agent) ;
        //         sbRecord.append(Reporter.ADD_REPORT_PROPERTY("agentId", agent.getAgentId())) ;
        //         agentsMDLLForwardIterator.iterateBack();

        //         agentsMDLL.remove(agent.getAgentId());
        //         //record += Reporter.ADD_REPORT_PROPERTY("age", agent.getAge()) ;
        //         //currentPopulation-- ;
        //     }
        //     else 
        //         // newAgentsList.add(agent) ;
        //         newAgentsMDLL.add(agent.getAgentId(), agent);
        // }
        HashMap<Integer, Agent> toRemove = new HashMap<Integer, Agent>();
        for (Agent agent : agentsMDLL) {

            if (agent.grimReaper()) {
                sbRecord.append(Reporter.ADD_REPORT_PROPERTY("agentId", agent.getAgentId())) ;
                toRemove.put(agent.getAgentId(), agent);
            }
        }

        for (Integer agentId : toRemove.keySet()) {
            agentsMDLL.remove(agentId);
        }

        return sbRecord.toString() ;
    }

    /**
     * For each Agent, for each Relationship for which they are the partner with 
     * the lower agentID, call Relationship.encounter()
     * @return String record of agentIds in each Relationship and the 
     * description of each sexual contact returned by Relationship.encounter()
     */
    private String runEncounters()
    {   
        float t0 = System.nanoTime();
        StringBuffer stringBufferRecord = new StringBuffer();

        // parallelised retrieval of elements
        agentsMDLL.getStream().parallel().forEach( agent -> {

            // Agent agent = agentsMDLL.getInternalMap().get(agentId).getObject();
            if (agent == null) return;
            for (Relationship relationship : agent.getCurrentRelationships())
            {
                if (agent != relationship.getLowerIdAgent())
                    continue ;
            
                if (RAND.nextDouble() < relationship.getEncounterProbability()) 
                {
                    String newRecord = Reporter.ADD_REPORT_PROPERTY(Reporter.RELATIONSHIPID, relationship.getRelationshipId()) 
                                        + relationship.encounter() ;
                    stringBufferRecord.append(newRecord);
                }
            }
        });
        // }

        float t1 = System.nanoTime();
        Community.RECORD_METHOD_TIME("Community.runEncounters", t1-t0);
        return stringBufferRecord.toString() ;
    }
    
    // private String runEncounters()
    // {
    //     StringBuilder sbRecord = new StringBuilder();
    //     //ArrayList<Relationship> currentRelationships ;
        
    //     // LOGGER.info("nb relationships: " + relationships.size());
    //     for (Agent agent : agents)
    //     {
    //         for (Relationship relationship : agent.getCurrentRelationships())
    //         {
    //             // WARNING: May cause future problems with hetero couples
    //             // Does agent have lower agentId than partner
    //             // Avoid checking relationship twice by accessing only through the 
    //             // agent with the lower agentId
    //             // TODO: Incorporate this into Agent.Method()
    //             //int agentId = agent.getAgentId() ;
    //             //int partnerId = relationship.getPartnerId(agentId) ;
                
    //             if (agent != relationship.getLowerIdAgent())
    //                 continue ;
    //             //try
    //             {
    //                 if (RAND.nextDouble() < relationship.getEncounterProbability()) 
    //                 {
    //                     String newRecord = Reporter.ADD_REPORT_PROPERTY(Reporter.RELATIONSHIPID, relationship.getRelationshipId()) 
    //                                      + relationship.encounter() ;
    //                     sbRecord.append(newRecord);
    //                 }
    //                 //System.out.println(record);
    //             }
    //             /*catch (NoSuchMethodException nsme)
    //             {
    //                 LOGGER.severe(nsme.getLocalizedMessage());
    //                 sbRecord.append(nsme.toString());
    //                 // record += nsme.toString(); //  .getMessage() ;
    //             }
    //             catch (InvocationTargetException ite)
    //             {
    //                 LOGGER.severe(ite.getLocalizedMessage());
    //                 //record += ite.getMessage() ;
    //             }
    //             catch (IllegalAccessException iae)
    //             {
    //                 LOGGER.severe(iae.getLocalizedMessage());
    //                 // record += iae.getMessage() ;
    //                 sbRecord.append(iae.getMessage());
    //             }*/
    //         }
    //     }
    //     return sbRecord.toString() ;
    // }

    /**
     * Loops through relationships and probabilistically chooses to end them.
     * TODO: Return useful (String) record.
     * @return (String) ""
     */
    private String clearRelationships() 
    {
        StringBuilder sbRecord = new StringBuilder();
        sbRecord.append(Reporter.ADD_REPORT_LABEL("clear"));
        // String record = Reporter.ADD_REPORT_LABEL("clear") ;
        String record = "";
        ArrayList<Relationship> currentRelationships ;
        Relationship relationship ;

        for (Agent agent : agentsMDLL)
        {
            currentRelationships = agent.getCurrentRelationships() ;
            for (int relationshipIndex = (currentRelationships.size() - 1) ; relationshipIndex >= 0 ; 
                    relationshipIndex-- )
            {
                relationship = currentRelationships.get(relationshipIndex) ;
                // Avoid checking relationship twice
                //int agentId = agent.getAgentId() ;
                if (agent == relationship.getLowerIdAgent()) sbRecord.append(endRelationship(relationship));
                    // record += endRelationship(relationship) ;
                //LOGGER.log(Level.INFO, "nbRelationships: {0}", new Object[]{nbRelationships});

            }
        }
        record = sbRecord.toString();
        return record ;
    }

    /*****************************************************************
     * Calls breakup() of Relationship relationship, which probabilistically
     * calls Relationship.del() 
     * @param relationship (Relationship) The Relationship to be ended.
     *****************************************************************/
    private String endRelationship(Relationship relationship) 
    {
        if (relationship.breakup())
        {
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
    private String progressInfection()
    {
        return progressInfection(new Object[] {}) ;
    }

    /**
     * Progresses course of STI in Agents who have one.
     * Treats Agents who are symptomatic or randomly choose to be treated.
     * Tracks if treatment was successful.
     * Check if disease has run its course and clears it if so.
     * @return (String) record in STIs progress
     */
    private String progressInfection(Object[] args)
    {
    	StringBuilder sbRecord = new StringBuilder();
        int infected ;
        int anyInfected = 0 ;
        //long startTime = System.nanoTime() ;

        for (Agent agent : agentsMDLL)
        {
            // record += agent.progressSitesInfection(cycle)
            //LOGGER.log(Level.INFO,"infected:{0}",agent.getAgentId());
            //record += Reporter.ADD_REPORT_PROPERTY("agentId",agent.getAgentId()) ;
            infected = agent.getInfectedStatus();
            anyInfected += infected ;
            //record += Reporter.ADD_REPORT_PROPERTY("infected", infected) ;
            
            // Due for an STI screen?
            if (RAND.nextDouble() < agent.getScreenProbability(args)) 
            {
            	sbRecord.append(Reporter.ADD_REPORT_PROPERTY("agentId",agent.getAgentId()));
                sbRecord.append(Reporter.ADD_REPORT_LABEL("tested"));
                // record += Reporter.ADD_REPORT_PROPERTY("agentId",agent.getAgentId()) ;
                // record += Reporter.ADD_REPORT_LABEL("tested") ;
                if ((infected) > 0)
                {
                    //LOGGER.info("screening agentId:"+String.valueOf(agent.getAgentId())) ;
                    for (Site site : agent.getSites())
                    {
                        if (agent.getInfectedStatus(site) > 0)
                        	sbRecord.append(Reporter.ADD_REPORT_PROPERTY(site.toString(), agent.getSymptomatic(site))) ;
                        // record += Reporter.ADD_REPORT_PROPERTY(site.toString(), agent.getSymptomatic(site)) ;
                    }
                // boolean tested = ((record.contains("Rectum") || record.contains("Urethra")) || !(RAND.nextDouble() < 0.5)) ;
                //boolean tested = ((record.contains("Urethra")) || !(RAND.nextDouble() < 0.5)) ;
                //if (tested)
                    {
                        agent.treat() ;
                        sbRecord.append(Reporter.ADD_REPORT_LABEL("treated")) ;
                        // record += Reporter.ADD_REPORT_LABEL("treated") ;
                    }
                }
                else
                	sbRecord.append("clear") ;
                    // record += "clear" ;
                sbRecord.append(" ") ;
                // record += " " ;
            }
            else if ((infected) > 0)
            {
            	sbRecord.append(Reporter.ADD_REPORT_PROPERTY("agentId",agent.getAgentId())) ;
                // record += Reporter.ADD_REPORT_PROPERTY("agentId",agent.getAgentId()) ;
                for (Site site : agent.getSites())
                {
                    if (agent.getInfectedStatus(site) > 0)
                    	sbRecord.append(Reporter.ADD_REPORT_PROPERTY(site.toString(), agent.getSymptomatic(site))) ;
                        // record += Reporter.ADD_REPORT_PROPERTY(site.toString(), agent.getSymptomatic(site)) ;
                    //LOGGER.info(site.toString()) ;
                }
                
                // agent.progressSitesInfection() allow infection to run one cycle of its course
                // and returns boolean whether agent is cleared (!stillInfected)
                if (agent.progressSitesInfection())
                {
                	sbRecord.append(Reporter.ADD_REPORT_PROPERTY("cleared"));
                    // record += Reporter.ADD_REPORT_PROPERTY("cleared") ;
                    //LOGGER.info("cleared");
                }
                else if (agent.getSymptomatic())
                {
                    if (agent.treatSymptomatic())  
                    {
                        //record += Reporter.ADD_REPORT_LABEL("tested") ;
                    	sbRecord.append(Reporter.ADD_REPORT_PROPERTY("tested","treated")) ;
                        // record += Reporter.ADD_REPORT_PROPERTY("tested","treated") ;
                        //LOGGER.info("treated");
                    }
                }
            }
        }
        //LOGGER.info(record) ;
        return sbRecord.toString() ;
    }

    /**
     * Progresses course of STI in Agents who have one.
     * Treats Agents who are symptomatic or randomly choose to be treated.
     * Tracks if treatment was successful.
     * Check if disease has run its course and clears it if so.
     * @return (String) record in STIs progress
     */
    private String progressInfection_Site(int cycle)
    {
        String agentRecord ;
        String record = "" ;
        int siteInfected ;
        boolean treat ;
        //boolean allSites ;
        //ArrayList<Site> untestedSites ;
        //int siteIndex ;
        //int testedIndex ;
        //long startTime = System.nanoTime() ;

        for (Agent agent : agentsMDLL)
        {
            agentRecord = "" ; 
            treat = false ;
            //allSites = false ;
            //untestedSites = new ArrayList<Site>() ;
            /*for (Site site : agent.getSites())
                untestedSites.add(site) ;*/
            //LOGGER.log(Level.INFO, "agentId:{0}", agent.getAgentId());
            
            for (Site site : agent.getSites())
            {
                siteInfected = (site.getInfectedStatus()) ;
                //LOGGER.log(Level.INFO, "{0} {1}", new Object[] {site.toString(),siteInfected}) ;

                // Due for an STI screen?
                if (RAND.nextDouble() < site.getScreenProbability(new String[] {Integer.toString(cycle)})) 
                {
                    if (siteInfected > 0)
                    {        
                        //LOGGER.info("infected") ;
                        agentRecord += Reporter.ADD_REPORT_PROPERTY(site.toString(), site.getSymptomatic()) ;
                        treat = site.treat() ;
                    }
                    else
                        agentRecord += Reporter.ADD_REPORT_PROPERTY(site.toString(),Reporter.CLEAR) ;
                    
                    agentRecord += Reporter.ADD_REPORT_PROPERTY("tested") ;
                    //untestedSites.remove(site) ;
                }
                else if (siteInfected > 0)
                {
                    agentRecord += Reporter.ADD_REPORT_PROPERTY(site.toString(), site.getSymptomatic()) ;

                    // agent.progressSitesInfection() allow infection to run one cycle of its course
                    // and returns boolean whether agent is cleared (!stillInfected)
                    //if (agent.progressSitesInfection())
                    if (site.progressInfection())    // STI has run its course and clears naturally
                    {
                        agentRecord += Reporter.ADD_REPORT_PROPERTY("cleared") ;
                        agent.updateInfectedStatus() ;
                    }
                    else if (site.getSymptomatic())
                        if (site.treatSymptomatic())    // Incubation period has passed
                        {
                            //allSites = true ;
                            treat = true ;
                            agentRecord += Reporter.ADD_REPORT_PROPERTY("tested") ;
                            //untestedSites.remove(site) ;
                        }
                }
            }
            /*if (allSites)  // if any Site symptomatic
                for (Site site : untestedSites)
                {
                    siteIndex = agentRecord.indexOf(site.toString()) ;
                    if (siteIndex > -1)    // Site infected but not tested
                    {
                        // Skip value of siteName
                        testedIndex = agentRecord.indexOf(" ",siteIndex) + 1 ;
                        if (agentRecord.substring(testedIndex).startsWith(Reporter.CLEAR))    // infection cleared naturally
                            testedIndex = agentRecord.indexOf(" ",testedIndex) + 1 ;
                        else    // infection undetected
                            site.treat() ;
                        agentRecord = agentRecord.substring(0, testedIndex) + Reporter.ADD_REPORT_PROPERTY("tested")
                                + agentRecord.substring(testedIndex) ;
                    }
                    else
                    {
                        if (site.getInfectedStatus() != 0)
                        {        
                            //LOGGER.info("infected") ;
                            agentRecord += Reporter.ADD_REPORT_PROPERTY(site.toString(), site.getSymptomatic()) ;
                            site.treat() ;
                        }
                        else
                            agentRecord += Reporter.ADD_REPORT_PROPERTY(site.toString(),Reporter.CLEAR) ;
                        
                        agentRecord += Reporter.ADD_REPORT_PROPERTY("tested") ;
                    }
                }*/
            if (treat)
            {
                agentRecord += Reporter.ADD_REPORT_PROPERTY("treated") ;
                agent.updateInfectedStatus() ;
            }
            if (!agentRecord.isEmpty())
            {
                record += Reporter.ADD_REPORT_PROPERTY(Reporter.AGENTID, agent.getAgentId()) ;
                record += agentRecord ;
            }
        }
        
        return record ;
    }

    private void submitRecords(String generateRecord, String encounterRecord, //String clearRecord, 
            String screeningRecord, String populationRecord)
    {
            relationshipReport.add(generateRecord) ;
            //LOGGER.info(encounterRecord);
            encounterReport.add(encounterRecord) ;
            //encounterScribe.writeRecord(encounterRecord);

            //clearReport.add(clearRecord) ;
            screeningReport.add(screeningRecord) ;
            populationReport.add(populationRecord) ;
    }

    /**
     * Saves Reports to file via scribe.dump() and empties them in memory.
     */
    private void dump()
    {
        scribe.dump(this) ;
                //MetaData() ;
        try
        {
            for (String scribeName : scribe.properties)    // Reset after dump
            {
                this.getClass().getDeclaredField(scribeName).set(this, (ArrayList<String>) new ArrayList<String>()) ;
            }
        }
        catch ( Exception nsfe )
        {
            LOGGER.log(Level.SEVERE, "{0} {1}", new Object[] {nsfe.getLocalizedMessage(), nsfe.toString()});
        }
    }
    
    /**
     * Saves metadata of simulation to file via scribe.dumpMetaData.
     */
    private void dumpMetaData()
    {
        ArrayList<String> metaLabels = new ArrayList<String>() ; 
        ArrayList<Object> metaData = new ArrayList<Object>() ; 
        metaLabels.add("Community.NAME_ROOT") ;
        metaData.add(Community.SIM_NAME) ;
        metaLabels.add("Community.FILE_PATH") ;
        metaData.add(Community.FILE_PATH) ;
        metaLabels.add("Community.POPULATION") ;
        metaData.add(Community.POPULATION) ;
        metaLabels.add("total_nb_agents") ;
        metaData.add(agentsMDLL.size()) ;
        metaLabels.add("Community.MAX_CYCLES") ;
        metaData.add(Community.MAX_CYCLES) ;
        metaLabels.add("Community.REBOOT_SEED") ;
        metaData.add(Community.GET_REBOOT_SEED()) ;
        
        metaLabels.add("Agent.SITE_NAMES") ;
        metaData.add(Arrays.asList(MSM.SITE_NAMES)) ; //TODO: Use Agent.SITE_NAMES
        metaLabels.add("Agent.REBOOT_SEED") ;
        metaData.add(Agent.GET_REBOOT_SEED()) ;
        
        metaLabels.add("Site.REBOOT_SEED") ;
        metaData.add(Site.GET_REBOOT_SEED()) ;
        
        metaLabels.add("Relationship.REBOOT_SEED") ;
        metaData.add(Relationship.GET_REBOOT_SEED()) ;
        metaLabels.add("Relationship.BURNIN_COMMENCE") ;
        metaData.add(Relationship.BURNIN_COMMENCE) ;
        metaLabels.add("Relationship.BURNIN_BREAKUP") ;
        metaData.add(Relationship.BURNIN_BREAKUP) ;
        
        metaLabels.add("Comment") ;
        metaData.add(Community.COMMENT) ;
        
        scribe.dumpMetaData(metaLabels,metaData) ;
    }
    
    private void dumpOutputReturn()
    {
        String fileName = OUTPUT_RETURN.substring(0, OUTPUT_RETURN.indexOf(" ")) + ".txt" ;
        try
        {
            BufferedWriter metadataWriter = new BufferedWriter(new FileWriter(Community.FILE_PATH + fileName,true)) ;
            metadataWriter.write(OUTPUT_RETURN) ;
            metadataWriter.newLine() ;
            metadataWriter.close() ;
        }
        catch ( Exception e )
        {
            LOGGER.severe(e.toString()) ;
        }
    }
    
    /**
     * Generates seed for random number generator to use upon reboot.
     * @return (long) seed for random number generation
     */
    static public final long GET_REBOOT_SEED()
    {
        return RAND.nextLong() ;
    }

    /**
     * Generates seed for random number generator to use upon reboot.
     * @return (long) seed for random number generation
     */
    static public final long GET_RANDOM_SEED()
    {
        return RANDOM_SEED ;
    }
    /**
     * Saves Agent, infection, and Relationship data to file in order to resume
     * simulation later. 
     */
    private void dumpRebootData()
    {
        // LOGGER.info("dumpRebootData()");
        ArrayList<String> metaLabels = new ArrayList<String>() ; 
        ArrayList<Object> metaData = new ArrayList<Object>() ; 
        
        StringBuilder sbAgent = new StringBuilder();
        StringBuilder sbRelationships = new StringBuilder();
                
        // Agents:
        metaLabels.add("Agents") ;
        // String agentsReboot = "" ;

        // if (ConfigLoader.DEBUG) {
        //     // sort agents by id
        //     Collections.sort(agents, (a1, a2) -> { return a1.getAgentId() > a2.getAgentId() ? 1 : -1;});
        // }
        for (Agent agent : agentsMDLL) {
            // agentsReboot += agent.getRebootData() ;
            // agentsRebootArrayList.add(agent.getRebootData());
            sbAgent.append(agent.getRebootData());
        }
        
        // use a StringBuilder to build new string
        metaData.add(sbAgent.toString()) ; 
        
        metaLabels.add("Relationships") ;
        String relationshipReboot = "" ;

        // sort relationships by id
        ArrayList<Relationship> relationships = new ArrayList<Relationship>();
        for (Agent agent : agentsMDLL) {
            for (Relationship relationship : agent.getCurrentRelationships())
                if (relationship.getLowerIdAgent() == agent) {
                    if (ConfigLoader.DEBUG) relationships.add(relationship);
                    else {
                        // relationshipReboot += relationship.getRecord();
                        // relationshipsRebootArrayList.add(relationship.getRecord());
                        sbRelationships.append(relationship.getRecord());
                    } 
                }
        }
        
        if (ConfigLoader.DEBUG) {
            LOGGER.info("Sorting relationships");
            Collections.sort(relationships, (r1, r2) -> { return r1.getRelationshipId() > r2.getRelationshipId() ? 1 : -1;});
            for (Relationship relationship : relationships) {
                relationshipReboot += relationship.getRecord();
            }
        }

        if (ConfigLoader.DEBUG) metaData.add(relationshipReboot);
        else metaData.add(sbRelationships.toString()) ; 
     
        // LOGGER.info("scribe.dumpRebootData()");
        scribe.dumpRebootData(metaLabels, metaData);
    }

    public void dumpRebootData(String folderPath, String simName, ArrayList<String> metaLabels, ArrayList<Object> metaData) {
        Scribe scribe = new Scribe(folderPath);
        scribe.dumpRebootData(simName, metaLabels, metaData);
    }
    
    /**
     * Reloads the Relationships saved during burn-in of a previous simulation.
     */
    private String reloadBurnin()
    {
        Reporter reporter = new Reporter(Community.RELOAD_BURNIN, Community.FILE_PATH) ;
        
        String breakupString = reporter.getMetaDatum("Relationship.BURNIN_BREAKUP") ;
        ArrayList<Comparable> breakupList = Reporter.EXTRACT_ALL_VALUES(Relationship.RELATIONSHIP_ID, breakupString) ;
        String commenceString = reporter.getMetaDatum("Relationship.BURNIN_COMMENCE") ;
        String returnString = "" ;
        
        String boundedString ;
        String relationshipId = "" ; 
        int agentId0 ;
        int agentId1 ;
        for (int commenceIndex = 0 ; commenceIndex >= 0 ; commenceIndex = commenceString.indexOf(Relationship.RELATIONSHIP_ID,commenceIndex+1)) 
        {
            boundedString = Reporter.EXTRACT_BOUNDED_STRING(Relationship.RELATIONSHIP_ID, commenceString, commenceIndex);
            relationshipId = Reporter.EXTRACT_VALUE(Relationship.RELATIONSHIP_ID, boundedString) ;
            if (!breakupList.contains(relationshipId)) 
            {
                returnString += boundedString ;
                String relationshipClazzName = "PRSP.PrEPSTI.community."+ Reporter.EXTRACT_VALUE("relationship", boundedString) ;
                try
                {
                    Relationship relationship = (Relationship) Class.forName(relationshipClazzName).newInstance();
                    relationship.setRelationshipId(Integer.valueOf(relationshipId)) ;
                    agentId0 = Integer.valueOf(Reporter.EXTRACT_VALUE(Reporter.AGENTID0,boundedString)) ;
                    agentId1 = Integer.valueOf(Reporter.EXTRACT_VALUE(Reporter.AGENTID1,boundedString)) ;
                    relationship.addAgents(agentsMDLL.get(agentId0), agentsMDLL.get(agentId1)) ;
                    
                }
                catch ( Exception e )
                {
                    LOGGER.severe(e.toString()) ;
                }
            }
            
        }
        Relationship.NB_RELATIONSHIPS_CREATED = Integer.valueOf(relationshipId) + 1 ;
        return returnString ;
    }
    
    /**
     * Object to gather data and record it to Files
     */
    //public Reporter(String simName, ArrayList<String> generateReports, 

    //	ArrayList<String> encounterReports, ArrayList<String> clearReports, ArrayList<String> screenReports)
    
    

    /*
    * * * * * * * * * * * * * * * * * * * * *
    *            HELPER FUNCTIONS           *
    * * * * * * * * * * * * * * * * * * * * *
    */

    /**
     * returns cycles if it is less than MAX_YEARS,
     * otherwise, we multiply it by DAYS_PER_YEAR and return that
     */
    private static int generateTrueCycles(int cycles) {
        cycles = cycles > ConfigLoader.MAX_YEARS 
                        ? cycles 
                        : cycles * ConfigLoader.DAYS_PER_YEAR;
        return cycles;
    }
    
    private class Scribe{

        String extension = ".txt" ;
        /** The number of Community cycles to pass between reports. */
        int dumpCycle ;
        
        /** Keeps track of how many dumps have taken place so far for labelling purposes. */
        int dumpsSoFar = 0 ;

        String simName ;
        String[] properties ;

        String globalFolder = Community.FILE_PATH ;
        //String fileName ;

        // File paths
        String logFilePath ;
        String errorFilePath ;
        //String outputFilePath ;


        // File objects
        //BufferedWriter[] fileWriters ;
        /*
        File logFile ;
        File errorFile ;
        File outputFile ;
        */

        private Scribe() 
        {
            
        }

        private Scribe(String folderPath) {
            globalFolder = folderPath;
        }
        
        private Scribe(String simName, String[] propertyNames) 
        {
            this.simName = simName ;
            if (Community.PARTIAL_DUMP)
                this.dumpCycle = Community.DUMP_CYCLE ;
            else
                this.dumpCycle = 0 ;
            
            this.properties = new String[propertyNames.length] ;
            for (int propertyIndex = 0 ; propertyIndex < propertyNames.length ; propertyIndex++ )
                properties[propertyIndex] = propertyNames[propertyIndex] + "Report" ;
        }
        
        
        private void dump(Community community)
        {
            String property ;
            
            for (int index = 0 ; index < properties.length ; index++ )
            {
                property = properties[index] ;
                try
                {
                    BufferedWriter fileWriter = openFile(property) ;
                    writeReport(fileWriter,(ArrayList<String>) Community.class.getDeclaredField(property).get(community)) ;
                    closeFile(fileWriter) ;
                }
                catch ( Exception e )
                {
                    LOGGER.log(Level.SEVERE, e.toString());
                }
            }
            dumpsSoFar++ ;
            LOGGER.log(Level.INFO, "dumpsSoFar:{0} ", new Object[] {dumpsSoFar}) ; //,(new File(globalFolder)).listFiles().length,properties.length});
        }
        
        /**
         * Opens file and writes metadata to it.
         * @param metaLabels
         * @param metaData 
         */
        protected void dumpMetaData(ArrayList<String> metaLabels, ArrayList<Object> metaData)
        {
            String fileName = simName + "-METADATA" + extension ;
            try
            {
                BufferedWriter metadataWriter = new BufferedWriter(new FileWriter(globalFolder + fileName,false)) ;
                writeMetaData(metadataWriter,metaLabels,metaData) ;
                metadataWriter.close() ;
            } 
            catch ( Exception e )
            {
                LOGGER.severe(e.toString());
            }
        }
        
        /**
         * Opens file and writes metadata to it.
         * @param metaLabels
         * @param metaData 
         */
        protected void dumpRebootData(ArrayList<String> metaLabels, ArrayList<Object> metaData)
        {
            String fileName = simName + "-REBOOT" + extension ;
            // LOGGER.info(fileName);
            try
            {
                BufferedWriter metadataWriter = new BufferedWriter(new FileWriter(globalFolder + fileName,false)) ;
                writeMetaData(metadataWriter,metaLabels,metaData) ;
                metadataWriter.close() ;
            } 
            catch ( Exception e )
            {
                LOGGER.severe(e.toString());
            }
        }

        /**
         * Opens file and writes metadata to it.
         * @param metaLabels
         * @param metaData 
         */
        protected void dumpRebootData(String simName, ArrayList<String> metaLabels, ArrayList<Object> metaData)
        {
            String fileName = simName + "-REBOOT" + extension ;
            // LOGGER.info(fileName);
            try
            {
                BufferedWriter metadataWriter = new BufferedWriter(new FileWriter(globalFolder + fileName,false)) ;
                writeMetaData(metadataWriter,metaLabels,metaData) ;
                metadataWriter.close() ;
            } 
            catch ( Exception e )
            {
                LOGGER.severe(e.toString());
            }
        }

        
        /**
         * Filenames are appended according to their first cycle. This cycle 
         * number if padded out with leading zeroes to generate filename order on disk.
         * @param property
         * @return (BufferedWriter) file for dumping property data for the current
         * dump cycle after constructing the appropriate filename.
         * @throws IOException 
         */
        private BufferedWriter openFile(String property) throws IOException 
        {
            String fileName = simName + property ; 
            if (dumpCycle > 0)
            {
                int dumpDigits = (int) Math.floor(Math.log10(MAX_CYCLES-1)) + 1 ;
                String nameIndex = String.valueOf(dumpsSoFar * dumpCycle) ;
                while (nameIndex.length() <  dumpDigits)
                    nameIndex = "0" + nameIndex ;
                fileName += "-" + nameIndex;
            }
            String outputFilePath = globalFolder + fileName + extension ;
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outputFilePath,false));
            return fileWriter ;
        }


        //outputCycle = 5 ;


        /*
        logFilePath = globalFolder + simName ;
        logFile = new File(logFilePath) ;

        errorFilePath = globalFolder + simName ;
        errorFile = new File(errorFilePath) ;
        */

        /**
         * Writes given String record to BufferedWriter fileWriter.
         * @param fileWriter
         * @param record
         * @throws Exception 
         */
        private void writeRecord(BufferedWriter fileWriter, String record) throws Exception
        {
                fileWriter.write(record);
                fileWriter.newLine();
        }

        /**
         * Writes (String) ArrayList report to BufferedWriter fileWriter.
         * @param fileWriter
         * @param report
         * @throws Exception 
         */
        private void writeReport(BufferedWriter fileWriter, ArrayList<String> report) throws Exception
        {
            for (String record : report)
                writeRecord(fileWriter, record) ;
        }
        
        /**
         * Writes metadata-related variables to BufferedWriter fileWriter.
         * @param fileWriter
         * @param labels - (ArrayList String) names of variables being written.
         * @param metadata - (ArrayList Object) values of variables being written.
         */
        private void writeMetaData(BufferedWriter fileWriter, ArrayList<String> labels, ArrayList<Object> metadata)
        {
            try
            {
                for (int dataLine = 0 ; dataLine < metadata.size() ; dataLine++ )
                    writeRecord(fileWriter, Reporter.ADD_REPORT_PROPERTY(labels.get(dataLine), metadata.get(dataLine))) ;
            }
            catch ( Exception e )
            {
                LOGGER.log(Level.SEVERE, e.toString()) ;
            }
        }

        /**
         * Closes BufferedWriter fileWriter.
         * @param fileWriter 
         */
        private void closeFile(BufferedWriter fileWriter)
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


    }


}
    
