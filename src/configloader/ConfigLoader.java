package configloader;

/*
 * Imports
 */

// JSON imports:
import java.io.* ;

import org.json.simple.parser.*;
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 


import agent.Agent;
import agent.MSM;
import community.Community;
import reporter.presenter.Presenter;



/**
 * Class responsible for default and config loading
 * Sets static variables of required classes
 * @author David
 */
public class ConfigLoader {
    static final public String DEFAULT_JSON_FILE = "default_config.json";
    static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("reporter");
    static JSONObject loadedDefaultsJSON; 
    
    private static void loadCommunityDefaults() {
        JSONObject jsonCommunity = (JSONObject) loadedDefaultsJSON.get("Community");
            

        // Set Community defaults
        Community.FILE_PATH = (String) jsonCommunity.get("FILE_PATH");
        Community.NAME_ROOT = (String) jsonCommunity.get("NAME_ROOT");
        Community.POPULATION = Integer.parseInt((String) jsonCommunity.get("POPULATION"));
        Community.COMMENT = (String) jsonCommunity.get("COMMENT");
        Community.DYNAMIC = Boolean.parseBoolean((String) jsonCommunity.get("DYNAMIC"));
        Community.RELOAD_SIMULATION = (String) jsonCommunity.get("RELOAD_SIMULATION");
    }

    private static void loadDefaults() {
        loadCommunityDefaults();

    }

    public static void load() {
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
        loadDefaults();
        LOGGER.info("Test Loading Defaults");
    }


}
