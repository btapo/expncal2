package org.zigmoi.expncal.storage.cache.accessor;

import org.zigmoi.expncal.storage.cache.Constants;
import org.zigmoi.expncal.storage.cache.abstracts.ACache;

public class Cache extends ACache {
    
    String parentId;
    
    public Cache(String cl) {
        parentId = cl;
    }
    
    public Cache(Cache cache, String cl) {
        parentId = cache.parentId + Constants.SEPARATOR_DOT + cl;
    }
    
	@Override
    public Object get(String cacheId) {
        return ACache.getStatic(parentId + Constants.SEPARATOR_DOT + cacheId);
    }
    
	@Override
    public void add(String cacheId, Object cacheItem) {
        ACache.addStatic(parentId + Constants.SEPARATOR_DOT + cacheId, cacheItem);
    }
	
	@Override
	public boolean has(String cacheId) {
		return ACache.hasStatic(parentId + Constants.SEPARATOR_DOT + cacheId);
	}

    @Override
    public void remove(String cacheId) {
        ACache.removeStatic(parentId + Constants.SEPARATOR_DOT + cacheId);
    }
}