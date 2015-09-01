package org.zigmoi.expncal.storage.cache.abstracts;

import org.zigmoi.expncal.storage.cache.accessor.Cache;
import org.zigmoi.expncal.storage.cache.Constants;
import org.zigmoi.expncal.storage.cache.interfaces.ICache;
import java.util.concurrent.ConcurrentHashMap;
import org.zigmoi.expncal.commons.DateTimeOp;

public abstract class ACache implements ICache {

    static ConcurrentHashMap<String, ConcurrentHashMap> cacheInstanceWareHouse = new ConcurrentHashMap<>();

    public synchronized static Object getStatic (String cacheId) {
        
        if (cacheInstanceWareHouse.containsKey(cacheId)) {

            int accessCount = (int) cacheInstanceWareHouse.get(cacheId).get(Constants.INDX_ACCESS_COUNT);
            int getCount = (int) cacheInstanceWareHouse.get(cacheId).get(Constants.INDX_COUNT_OP_GET);
            cacheInstanceWareHouse.get(cacheId).put(Constants.INDX_ACCESS_COUNT, ++accessCount);
            cacheInstanceWareHouse.get(cacheId).put(Constants.INDX_COUNT_OP_GET, ++getCount);
            cacheInstanceWareHouse.get(cacheId).put(Constants.INDX_LAST_OP, "get");
            cacheInstanceWareHouse.get(cacheId).put(Constants.INDX_LAST_ACCESS, DateTimeOp.getSysTimeHHMMSSMS());

            return cacheInstanceWareHouse.get(cacheId).get(Constants.INDX_VALUE);
        }
        return null;
    }
	
	public synchronized static boolean hasStatic (String cacheId) {
		return cacheInstanceWareHouse.containsKey(cacheId);
	}

    public synchronized static void addStatic (String cacheId, Object cacheItem) {
        
        int accessCount = 0;
        int addCount = 0;
        if (cacheInstanceWareHouse.containsKey(cacheId)) {
            accessCount = (int) cacheInstanceWareHouse.get(cacheId).get(Constants.INDX_ACCESS_COUNT);
            addCount = (int) cacheInstanceWareHouse.get(cacheId).get(Constants.INDX_COUNT_OP_ADD);
        } else {
            cacheInstanceWareHouse.put(cacheId, new ConcurrentHashMap<String, Object>());
        }
        cacheInstanceWareHouse.get(cacheId).put(Constants.INDX_ACCESS_COUNT, ++accessCount);
        cacheInstanceWareHouse.get(cacheId).put(Constants.INDX_COUNT_OP_GET, ++addCount);
        cacheInstanceWareHouse.get(cacheId).put(Constants.INDX_LAST_OP, "add");
        cacheInstanceWareHouse.get(cacheId).put(Constants.INDX_LAST_ACCESS, DateTimeOp.getSysTimeHHMMSSMS());

        cacheInstanceWareHouse.get(cacheId).put(Constants.INDX_VALUE, cacheItem);
    }

    public synchronized static void removeStatic (String cacheId) {
        
        if (cacheInstanceWareHouse.containsKey(cacheId)) {
            cacheInstanceWareHouse.remove(cacheId);
        }
    }
	
	public final static String getSeparator() {
		return Constants.SEPARATOR_DOT;
	}
    
    public static void main(String[] args) {
        //////////////// Change the above code to receive data from static hash map to synchronized
//        ACache.add("aaaaaa", "sjkdhfkshdfkshdf");
//        System.out.println(ACache.get("aaaaaa"));
        Cache ch = new Cache(ACache.class.getName());
        Cache ch2 = new Cache(ACache.class.getName());
        ch2.add("bbbbbb", "cccccc");
        ch.add("aaaaaa", ch2);
        Cache ch3 = (Cache)ch.get("aaaaaa");
        System.out.println(ch3.get("bbbbbb"));
        
    }
}
