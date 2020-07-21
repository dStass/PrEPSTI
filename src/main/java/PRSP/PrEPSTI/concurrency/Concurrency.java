package PRSP.PrEPSTI.concurrency;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Concurrency {

    /**
     * Convert a <b>nested</b> HashMap -> ConcurrentHashMap
     * Nested HashMaps call convertNormalToConcurrentHashMap recursively
     * Does NOT invoke clone on objects
     * @param <K> key type: such as String or Integer
     * @param <V> value type: such as String or Integer <b>or</b> HashMap
     * @param <M> return value type: IF V is a HashMap, M will be a ConcurrentHashMap, otherwise M=V
     * @param inputHashMap
     * @return ConcurrentHashMap
     */
    public static <K,V,M> ConcurrentHashMap<K,M> convertNormalToConcurrentHashMap(HashMap<K,V> inputHashMap) {    
        V valueClass = null;
        for (Map.Entry<K,V> entry : inputHashMap.entrySet()) {
            valueClass = entry.getValue();
            break;
        }
        
        ConcurrentHashMap<K, M> returnConcurrentHashMap = new ConcurrentHashMap<K, M>();
        for (Map.Entry<K,V> entry : inputHashMap.entrySet())
        {
            // if the value is of type hashmap, we can recursively call convertToConcurrentHashMap
            if (valueClass instanceof HashMap) {
                // Concurrency<K, V> c = new Concurrency<K, V>();
                returnConcurrentHashMap.put(entry.getKey(), (M) Concurrency.convertNormalToConcurrentHashMap((HashMap<K, V>) entry.getValue()));
            }
            else
            {
                returnConcurrentHashMap.put(entry.getKey(), (M) entry.getValue());
            }
        }
        return returnConcurrentHashMap;
    }

    /**
     * Convert a <b>nested</b> ConcurrentHashMap -> HashMap
     * Nested HashMaps call convertConcurrentToNormalHashMap recursively
     * Does NOT invoke clone on objects
     * @param <K> key type: such as String or Integer
     * @param <V> value type: such as String or Integer <b>or</b> ConcurrentHashMap
     * @param <M> return value type: IF V is a ConcurrentHashMap, M will be a HashMap, otherwise M=V
     * @param inputHashMap
     * @return ConcurrentHashMap
     */
    public static <K,V,M> HashMap<K,M> convertConcurrentToNormalHashMap(ConcurrentHashMap<K,V> inputConcurrentHashMap) {    
        V valueClass = null;
        for (Map.Entry<K,V> entry : inputConcurrentHashMap.entrySet()) {
            valueClass = entry.getValue();
            break;
        }
        
        HashMap<K, M> returnHashMap = new HashMap<K, M>();
        for (Map.Entry<K,V> entry : inputConcurrentHashMap.entrySet())
        {
            // if the value is of type hashmap, we can recursively call convertToConcurrentHashMap
            if (valueClass instanceof ConcurrentHashMap) {
                returnHashMap.put(entry.getKey(), (M) Concurrency.convertConcurrentToNormalHashMap((ConcurrentHashMap<K, V>) entry.getValue()));
            }
            else
            {
                returnHashMap.put(entry.getKey(), (M) entry.getValue());
            }
        }
        return returnHashMap;
    }

    public static void main(String[] args) {

        // example of how to use convertToConcurrentHashMap
        HashMap<String, HashMap<String, String>> originalHM = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> animalsHM = new HashMap<String, String>();
        HashMap<String, String> birdsHM = new HashMap<String, String>();
        animalsHM.put("cow", "moo");
        animalsHM.put("sheep", "baa");

        birdsHM.put("chicken", "cluck");
        birdsHM.put("duck", "quack");
        originalHM.put("animalsHM", animalsHM);
        originalHM.put("birdsHM", birdsHM);

        ConcurrentHashMap<String, ConcurrentHashMap<String, String>> convertedToConcurrentHM = Concurrency.convertNormalToConcurrentHashMap(originalHM);
        System.out.println(convertedToConcurrentHM.getClass().toString() + ":" + convertedToConcurrentHM.toString());
        System.out.println("Converted sub-types:");
        for (Map.Entry<String, ConcurrentHashMap<String, String>> entry : convertedToConcurrentHM.entrySet()) {
            System.out.println("-->  " + entry.getKey() + ":" + entry.getValue().getClass());
        }

        System.out.println();
        HashMap<String, HashMap<String, String>> convertedBackToNormalHM = Concurrency.convertConcurrentToNormalHashMap(convertedToConcurrentHM);
        System.out.println(convertedBackToNormalHM.getClass().toString() + ":" + convertedBackToNormalHM.toString());
        System.out.println("convertedBack sub-types:");
        for (Map.Entry<String, HashMap<String, String>> entry : convertedBackToNormalHM.entrySet()) {
            System.out.println("-->  " + entry.getKey() + ":" + entry.getValue().getClass());
        }
    }
}