package org.zigmoi.expncal.commons;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HashMapOp {
	
    public static Object getKeyFromValue(Map hm, Object value) {
    
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {return o;}
        }
	return null;
    }    

	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByKeys(Map<K,V> map){
        List<K> keys = new LinkedList<>(map.keySet());
        Collections.sort(keys);
      
        Map<K,V> sortedMap = new LinkedHashMap<>();
        for(K key: keys){
            sortedMap.put(key, map.get(key));
        }
      
        return sortedMap;
    }
  
    public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
        
        List<Map.Entry<K,V>> entries = new LinkedList<>(map.entrySet());
      
        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

            @Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
      
        Map<K,V> sortedMap = new LinkedHashMap<>();
      
        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        } 
        
        return sortedMap;
    }
}