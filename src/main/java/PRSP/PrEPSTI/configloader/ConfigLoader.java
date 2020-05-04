package PRSP.PrEPSTI.configloader;

// imports

// JAVA imports:
import java.io.* ;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

// JSON imports:
import org.json.simple.parser.*;
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 

// PrEPSTI imports
import PRSP.PrEPSTI.agent.Agent;
import PRSP.PrEPSTI.agent.MSM;
import PRSP.PrEPSTI.community.Community;
import PRSP.PrEPSTI.community.Relationship;
import PRSP.PrEPSTI.reporter.Reporter;
import PRSP.PrEPSTI.reporter.presenter.Presenter;


/**
 * Class responsible for default and config loading
 * Sets static variables of required classes
 * @author David
 */
public class ConfigLoader {

    // final definitions 
    private static final String CONFIG_PATH = "configs/";
    private static final String DEFAULT_JSON_FILE = ConfigLoader.CONFIG_PATH + "default_json.json";
    private static final String CONFIG_JSON_FILE = ConfigLoader.CONFIG_PATH + "config.json";

    // loaded JSONObjects
    private static JSONObject loadedJSON;

    // contains key = method name, value = variables loaded for that method
    private static HashMap<String, HashMap> classMethodVariablesHashMap;

    // contains colours
    private static ArrayList<ArrayList<Integer>> colours;

    // constant definitions
    public static final int MAX_YEARS = 99; // used for drawing points


    /**
     * method to load 
     */
    public static void load() {

        // Instantiations:
        ConfigLoader.classMethodVariablesHashMap = new HashMap();
        ConfigLoader.colours = new ArrayList<ArrayList<Integer>>();

        // load information for this class
        ConfigLoader.readJSON("default");
        ConfigLoader.loadConfigLoaderSettings();
        
        // load information for other classes
        ConfigLoader.loadInformationIntoClasses();
        ConfigLoader.readJSON("config");
        ConfigLoader.loadInformationIntoClasses();
    }


    /*
     * * * * * * * * * * * * * * * * * * * * *
     *              JSON LOADING             *
     * * * * * * * * * * * * * * * * * * * * *
     */


    /**
     * 
     * @param configType - takes in "config" or "default" to load file
     */
    private static void readJSON(String configType) {
        
        String configString = "";
        if (configType == "default") {
            configString = ConfigLoader.DEFAULT_JSON_FILE;
        } else if (configType == "config") {
            configString = ConfigLoader.CONFIG_JSON_FILE;
        }

        try {
            Object obj = new JSONParser().parse(new FileReader(configString));
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


    private static void loadConfigLoaderSettings() {
        JSONObject configLoaderJSON = (JSONObject) ConfigLoader.loadedJSON.get("config_loader");
        if (configLoaderJSON == null) return;

        JSONArray coloursJSONArray = (JSONArray) configLoaderJSON.get("colours");

        for (int i = 0; i < coloursJSONArray.size(); ++i) {
            JSONArray rgbJSONArray = (JSONArray) coloursJSONArray.get(i);

            ArrayList<Integer> rgbArrayList = new ArrayList<Integer>();
            for (int j = 0; j < rgbJSONArray.size(); ++j) {
                int col = ((Number) rgbJSONArray.get(j)).intValue();
                rgbArrayList.add(col);
            }
            ConfigLoader.colours.add(rgbArrayList);
        }
    }


    private static void loadCommunity() {
        JSONObject communityJSON = (JSONObject) ConfigLoader.loadedJSON.get("community");
        if (communityJSON == null) return;
            
        // load variables:
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
        if (MAX_CYCLES != null) Community.LOADED_MAX_CYCLES = Integer.parseInt(MAX_CYCLES);

        String RELOAD_SIMULATION = (String) communityJSON.get("RELOAD_SIMULATION");
        if (RELOAD_SIMULATION != null) Community.RELOAD_SIMULATION = RELOAD_SIMULATION;

        // load methods:
        loadMethodVariablesHashMap("community", communityJSON);
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
        loadMethodVariablesHashMap("msm", msmJSON);
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
        
        // load variables
        String PLOT_FILE = (String) presenterJSON.get("PLOT_FILE");
        if (PLOT_FILE != null) Presenter.PLOT_FILE = Boolean.parseBoolean(PLOT_FILE);
        
        String FILENAME = (String) presenterJSON.get("FILENAME");
        if (FILENAME != null) Presenter.FILENAME = FILENAME;

        JSONArray DATA_SCORE = (JSONArray) presenterJSON.get("DATA_SCORE");
        if (DATA_SCORE != null)
            Presenter.DATA_SCORE = ConfigLoader.convertJSONArrayToStringArray(DATA_SCORE);

        String FIGURE_WIDTH = (String) presenterJSON.get("FIGURE_WIDTH");
        if (FIGURE_WIDTH != null) Presenter.FIGURE_WIDTH = Integer.parseInt(FIGURE_WIDTH);

        String FIGURE_HEIGHT = (String) presenterJSON.get("FIGURE_HEIGHT");
        if (FIGURE_HEIGHT != null) Presenter.FIGURE_HEIGHT = Integer.parseInt(FIGURE_HEIGHT);
    }


    /*
     * * * * * * * * * * * * * * * * * * * * *
     *            HELPER FUNCTIONS           *
     * * * * * * * * * * * * * * * * * * * * *
     */

    private static HashMap<String, HashMap> getMethodsHashMapFromJSONObject(JSONObject jsonObject) {
        JSONObject methodsJSON = (JSONObject) jsonObject.get("methods");

        if (methodsJSON == null) return null;

        HashMap <String, HashMap> methodToVariablesMapHashMap = ConfigLoader.convertJSONObjectToHashMap_StringToNewHashMap(methodsJSON);
            for (HashMap.Entry<String, HashMap> entry : methodToVariablesMapHashMap.entrySet()) {
                String methodName = entry.getKey();
                JSONObject methodVariablesJSON = (JSONObject) methodsJSON.get(methodName);
                HashMap <String, String> methodVariablesToValues = ConfigLoader.convertJSONObjectToHashMap_StringToString(methodVariablesJSON);
                methodToVariablesMapHashMap.put(methodName, methodVariablesToValues);
            }

        // TODO: if nothing inside methods
        return methodToVariablesMapHashMap;
    }


    /*
     * Give this function a JSONObject, it will iterate over it,
     * adding each key-value pairs into a hashmap which will be returned
     * 
     * @pre JSONObject must only contain String to String key-value pairs
     * @post returns a hashmap with each key value pair
     *  
     */
    private static HashMap <String, HashMap> convertJSONObjectToHashMap_StringToNewHashMap(JSONObject jsonObject) {
        HashMap<String, HashMap> toReturn = new HashMap();

        for (Object keyObject : jsonObject.keySet()) {
            String key = keyObject.toString();
            toReturn.put(key, new HashMap());
        }

        return toReturn;
    }


    private static HashMap<String, String> convertJSONObjectToHashMap_StringToString(JSONObject jsonObject) {
        HashMap<String, String> toReturn = new HashMap();
        for (Object keyObject : jsonObject.keySet()) {
            String key = keyObject.toString();
            String value = jsonObject.get(key).toString();
            toReturn.put(key, value);
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


    /**
     * 
     * @param methodName
     * @param methodJSON
     * 
     * entire default methods will be overwritten by config file
     * 
     */
    private static void loadMethodVariablesHashMap(String methodName, JSONObject methodJSON) {
        HashMap<String, HashMap> methodToVariablesMapHashMap = ConfigLoader.getMethodsHashMapFromJSONObject(methodJSON);
        if (methodToVariablesMapHashMap != null) {
            ConfigLoader.classMethodVariablesHashMap.put(methodName, methodToVariablesMapHashMap);
        }
    }

    /*
     * * * * * * * * * * * * * * * * * * * * *
     *        PUBLIC HELPER FUNCTIONS        *
     * * * * * * * * * * * * * * * * * * * * *
     */    

    public static int getMethodVariableInteger(String className, String methodName, String variableName) {
        return Integer.parseInt(ConfigLoader.getMethodVariable(className, methodName, variableName));
    }

    public static double getMethodVariableDouble(String className, String methodName, String variableName) {
        return Double.parseDouble(ConfigLoader.getMethodVariable(className, methodName, variableName));
    }

    public static boolean getMethodVariableBoolean(String className, String methodName, String variableName) {
        return Boolean.parseBoolean(ConfigLoader.getMethodVariable(className, methodName, variableName));
    }

    public static String getMethodVariableString(String className, String methodName, String variableName) {
        return ConfigLoader.getMethodVariable(className, methodName, variableName);
    }

    private static String getMethodVariable(String className, String methodName, String variableName) {
        HashMap<String, HashMap> classHashMap = ConfigLoader.classMethodVariablesHashMap.get(className);
        HashMap<String, String> methodHashMap = classHashMap.get(methodName);
        String value = methodHashMap.get(variableName);
        return value;
    }


    /**
     * returns ArrayList containing ArrayList<Integers> (representing RGB)
     * @return a shallow copy of ConfigLoader.colours 
     */
    public static ArrayList<ArrayList<Integer>> getColours() {
        return (ArrayList<ArrayList<Integer>>) ConfigLoader.colours.clone();
    }

    /**
     * returns ArrayList containing ArrayList<Integers> (representing RGB)
     * @return a shuffled shallow copy of ConfigLoader.colours
     */
    public static ArrayList<ArrayList<Integer>> getColoursShuffled() {
        ArrayList<ArrayList<Integer>> cloneList = (ArrayList<ArrayList<Integer>>) ConfigLoader.colours.clone();
        Collections.shuffle(cloneList);
        return cloneList;
    }
}