package configloader;

/*
 * Imports
 */

// JSON imports:
import java.io.* ;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.parser.*;
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 


import agent.Agent;
import agent.MSM;
import community.Community;
import community.Relationship;
import reporter.Reporter;
import reporter.presenter.Presenter;



/**
 * Class responsible for default and config loading
 * Sets static variables of required classes
 * @author David
 */
public class ConfigLoader {
    // Michael's LOGGER
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("reporter");

    // final definitions 
    static final public String DEFAULT_JSON_FILE = "default_config.json";
    static final public String CONFIG_JSON_FILE = "config.json";

    // loaded JSONObjects
    static JSONObject loadedJSON; 
    
    // load jsons into class
    public static void load() {
        ConfigLoader.readDefaultsJSON();
        ConfigLoader.loadInformationIntoClasses();
        ConfigLoader.readConfigJSON();
        ConfigLoader.loadInformationIntoClasses();
        LOGGER.info("Test Loading Defaults");
    }



    /*
     * * * * * * * * * * * * * * * * * * * * *
     *                DEFAULTS               *
     * * * * * * * * * * * * * * * * * * * * *
     */

    private static void readDefaultsJSON() {
        try {
            Object obj = new JSONParser().parse(new FileReader(ConfigLoader.DEFAULT_JSON_FILE));
            ConfigLoader.loadedJSON = (JSONObject) obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * Extract community from JSON and load into Communit class
     */
    private static void loadInformationIntoClasses() {
        ConfigLoader.loadCommunity();
        ConfigLoader.loadPaths();
        ConfigLoader.loadMSM();
        ConfigLoader.loadReporter();
        ConfigLoader.loadPresenter();

    }


    private static void loadCommunity() {
        JSONObject communityJSON = (JSONObject) ConfigLoader.loadedJSON.get("community");
        if (communityJSON == null) return;
            
        // Set community defaults
        String FILE_PATH = (String) communityJSON.get("FILE_PATH");
        if (FILE_PATH != null) Community.FILE_PATH = FILE_PATH;
        
        String NAME_ROOT = (String) communityJSON.get("NAME_ROOT");
        if (NAME_ROOT != null) Community.NAME_ROOT = NAME_ROOT;

        String POPULATION = (String) communityJSON.get("POPULATION");
        if (POPULATION != null) Community.POPULATION = Integer.parseInt(POPULATION);

        String COMMENT = (String) communityJSON.get("COMMENT");
        if (COMMENT != null) Community.COMMENT = COMMENT;

        String DYNAMIC = (String) communityJSON.get("DYNAMIC");
        if (DYNAMIC != null) Community.DYNAMIC = Boolean.parseBoolean(DYNAMIC);

        String MAX_CYCLES = (String) communityJSON.get("MAX_CYCLES");
        if (MAX_CYCLES != null) Community.MAX_CYCLES = Integer.parseInt(MAX_CYCLES);

        String RELOAD_SIMULATION = (String) communityJSON.get("RELOAD_SIMULATION");
        if (RELOAD_SIMULATION != null) Community.RELOAD_SIMULATION = RELOAD_SIMULATION;
    }


    /**
     * method handles loading shared paths including:
     * - REBOOT_PATH for Agent and Relationship
     * - REPORT_PATH for Reporter
     * - DATA_PATH for Report and Presenter
     */
    private static void loadPaths() {
        JSONObject pathsJSON = (JSONObject) ConfigLoader.loadedJSON.get("paths");
        if (pathsJSON == null) return;
        
        // reboot path for Relationship and Agent
        String rebootPath = (String) pathsJSON.get("REBOOT_PATH");
        if (rebootPath != null) {
            Relationship.FOLDER_PATH = rebootPath;
            Agent.FOLDER_PATH = rebootPath;
        }

        // report path for Reporter
        String reportPath = (String) pathsJSON.get("REPORT_PATH");
        if (reportPath != null) Reporter.REPORT_FOLDER = reportPath;

        // data path for Reporter and Presenter
        String dataPath = (String) pathsJSON.get("DATA_PATH");
        if (dataPath != null) {
            Reporter.DATA_FOLDER = dataPath;
            Presenter.FOLDER_PATH = dataPath;
        }
    }

    /**
     * handles loading MSM default values
     * contains default variables inside methods
     * will implement a hashmap inside MSM class to extract this info
     */
    private static void loadMSM() {
        JSONObject msmJSON = (JSONObject) ConfigLoader.loadedJSON.get("msm");
        if (msmJSON == null) return;

        // set group sex event size and HIV risky correlation
        String groupSexEventSizeStr = (String) msmJSON.get("GROUP_SEX_EVENT_SIZE");
        if (groupSexEventSizeStr != null)
            MSM.GROUP_SEX_EVENT_SIZE = Integer.parseInt(groupSexEventSizeStr);
        
        String hivRiskyCorrelation = (String) msmJSON.get("HIV_RISKY_CORRELATION");
        if (hivRiskyCorrelation != null) 
            MSM.HIV_RISKY_CORRELATION = Double.parseDouble(hivRiskyCorrelation);

        // load function methods - set MSM.METHOD_CONFIG
        // in the json file under MSM, there is methods : { ... }
        // this will contain function_name -> {} pairs where {} contains 
        // key-value pairs signifying what variables should be set to
        // this converts from JSON format to a Java HashMap
        // for easy access from within the MSM class (remove the need to deal with JSONObjects)
        JSONObject defaultMethodsJSON = (JSONObject) msmJSON.get("methods");
        if (defaultMethodsJSON != null) {
            HashMap <String, HashMap> methodToVariablesMapHashMap = ConfigLoader.convertJSONObjectToHashMapStringToHashMap(defaultMethodsJSON);
            for (HashMap.Entry<String, HashMap> entry : methodToVariablesMapHashMap.entrySet()) {
                String methodName = entry.getKey();
                JSONObject methodVariablesJSON = (JSONObject) defaultMethodsJSON.get(methodName);
                HashMap <String, String> methodVariablesToValues = ConfigLoader.convertJSONObjectToHashMapStringToString(methodVariablesJSON);
                methodToVariablesMapHashMap.put(methodName, methodVariablesToValues);
            }
            // set MSM config to above hashmap
            MSM.METHOD_CONFIG = methodToVariablesMapHashMap;
        }

    }


    private static void loadReporter() {
        JSONObject reporterJSON = (JSONObject) ConfigLoader.loadedJSON.get("reporter");
        if (reporterJSON == null) return;

        String WRITE_REPORT = (String) reporterJSON.get("WRITE_REPORT");
        if (WRITE_REPORT != null) {
            Reporter.WRITE_REPORT = Boolean.parseBoolean(WRITE_REPORT);
        }
    }

    private static void loadPresenter() {
        JSONObject presenterJSON = (JSONObject) ConfigLoader.loadedJSON.get("presenter");
        if (presenterJSON == null) return;

        String PLOT_FILE = (String) presenterJSON.get("PLOT_FILE");
        if (PLOT_FILE != null) Presenter.PLOT_FILE = Boolean.parseBoolean(PLOT_FILE);
        
        String FILENAME = (String) presenterJSON.get("FILENAME");
        if (FILENAME != null) Presenter.FILENAME = FILENAME;

        JSONArray DATA_SCORE = (JSONArray) presenterJSON.get("DATA_SCORE");
        if (DATA_SCORE != null)
            Presenter.DATA_SCORE = ConfigLoader.convertJSONArrayToStringArray(DATA_SCORE);
    }



    /*
     * * * * * * * * * * * * * * * * * * * * *
     *                 CONFIG                *
     * * * * * * * * * * * * * * * * * * * * *
     */

    private static void readConfigJSON() {
        try {
            Object obj = new JSONParser().parse(new FileReader(ConfigLoader.CONFIG_JSON_FILE));
            ConfigLoader.loadedJSON = (JSONObject) obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    /*
     * * * * * * * * * * * * * * * * * * * * *
     *            HELPER FUNCTIONS           *
     * * * * * * * * * * * * * * * * * * * * *
     */

    
    /*
     * Give this function a JSONObject, it will iterate over it,
     * adding each key-value pairs into a hashmap which will be returned
     * 
     * @pre JSONObject must only contain String to String key-value pairs
     * @post returns a hashmap with each key value pair
     *  
     */

    private static HashMap<String, String> convertJSONObjectToHashMapStringToString(JSONObject jsonObject) {
        HashMap<String, String> toReturn = new HashMap();
        for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            String value = (String) jsonObject.get(key);
            toReturn.put(key, value);
        }
        return toReturn;
    }

    private static HashMap <String, HashMap> convertJSONObjectToHashMapStringToHashMap(JSONObject jsonObject) {
        HashMap<String, HashMap> toReturn = new HashMap();
        for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            toReturn.put(key, new HashMap());
        }
        return toReturn;
    }

    private static String[] convertJSONArrayToStringArray (JSONArray jsonArray) {
        String[] toReturn = new String[jsonArray.size()];
        for (int i = 0; i < toReturn.length; ++i) {
            toReturn[i] = (String) (jsonArray.get(i));
        }        
        return toReturn;
    }


}
