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
    static JSONObject loadedDefaultsJSON; 
    static JSONObject loadedConfigJSON;
    
    // load jsons into class
    public static void load() {
        readDefaultsJSON();
        readConfigJSON();
        loadDefaults();
        LOGGER.info("Test Loading Defaults");
    }



    /*
     * * * * * * * * * * * * * * * * * * * * *
     *                DEFAULTS               *
     * * * * * * * * * * * * * * * * * * * * *
     */

    private static void readDefaultsJSON() {
        try {
            Object obj = new JSONParser().parse(new FileReader(DEFAULT_JSON_FILE));
            loadedDefaultsJSON = (JSONObject) obj;
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
    private static void loadDefaults() {
        loadCommunityDefaults();
        loadPathsDefaults();
        loadMSMDefaults();

    }


    private static void loadCommunityDefaults() {
        JSONObject communityDefaultsJSON = (JSONObject) loadedDefaultsJSON.get("community");
            
        // Set community defaults
        Community.FILE_PATH = (String) communityDefaultsJSON.get("FILE_PATH");
        Community.NAME_ROOT = (String) communityDefaultsJSON.get("NAME_ROOT");
        Community.POPULATION = Integer.parseInt((String) communityDefaultsJSON.get("POPULATION"));
        Community.COMMENT = (String) communityDefaultsJSON.get("COMMENT");
        Community.DYNAMIC = Boolean.parseBoolean((String) communityDefaultsJSON.get("DYNAMIC"));
        Community.RELOAD_SIMULATION = (String) communityDefaultsJSON.get("RELOAD_SIMULATION");
    }


    /**
     * method handles loading shared paths including:
     * - REBOOT_PATH for Agent and Relationship
     * - REPORT_PATH for Reporter
     * - DATA_PATH for Report and Presenter
     */
    private static void loadPathsDefaults() {
        JSONObject pathsDefaultsJSON = (JSONObject) loadedDefaultsJSON.get("paths");
        
        // reboot path for Relationship and Agent
        String rebootPath = (String) pathsDefaultsJSON.get("REBOOT_PATH");
        Relationship.FOLDER_PATH = rebootPath;
        Agent.FOLDER_PATH = rebootPath;

        // report path for Reporter
        String reportPath = (String) pathsDefaultsJSON.get("REPORT_PATH");
        Reporter.REPORT_FOLDER = reportPath;

        // data path for Reporter and Presenter
        String dataPath = (String) pathsDefaultsJSON.get("DATA_PATH");
        Reporter.DATA_FOLDER = dataPath;
        Presenter.FOLDER_PATH = dataPath;
    }

    /**
     * handles loading MSM default values
     * contains default variables inside methods
     * will implement a hashmap inside MSM class to extract this info
     */
    private static void loadMSMDefaults() {
        JSONObject msmDefaultsJSON = (JSONObject) loadedDefaultsJSON.get("msm");

        // set group sex event size and HIV risky correlation
        MSM.GROUP_SEX_EVENT_SIZE = Integer.parseInt((String) msmDefaultsJSON.get("GROUP_SEX_EVENT_SIZE"));
        MSM.HIV_RISKY_CORRELATION = Double.parseDouble((String) msmDefaultsJSON.get("HIV_RISKY_CORRELATION"));

        // load function methods - set MSM.METHOD_CONFIG
        JSONObject defaultMethodsJSON = (JSONObject) msmDefaultsJSON.get("methods");
        HashMap <String, HashMap> methodToVariablesMapHashMap = convertJSONObjectToHashMapStringToHashMap(defaultMethodsJSON);
        for (HashMap.Entry<String, HashMap> entry : methodToVariablesMapHashMap.entrySet()) {
            String methodName = entry.getKey();
            JSONObject methodVariablesJSON = (JSONObject) defaultMethodsJSON.get(methodName);
            HashMap <String, String> methodVariablesToValues = convertJSONObjectToHashMapStringToString(methodVariablesJSON);
            methodToVariablesMapHashMap.put(methodName, methodVariablesToValues);

        }

        LOGGER.info("@@@@@@@@!@#@#@#" + methodToVariablesMapHashMap.toString());
        

    }


    /*
     * * * * * * * * * * * * * * * * * * * * *
     *                 CONFIG                *
     * * * * * * * * * * * * * * * * * * * * *
     */

    private static void readConfigJSON() {
        try {
            Object obj = new JSONParser().parse(new FileReader(CONFIG_JSON_FILE));
            loadedConfigJSON = (JSONObject) obj;
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
}
