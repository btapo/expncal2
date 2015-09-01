package org.zigmoi.expncal.storage.cache.interfaces;

import org.zigmoi.expncal.interfaces.IBase;

public interface ICache extends IBase {

	public Object get(String cacheId);
	public void add(String cacheId, Object cacheItem);
	public void remove(String cacheId);
	public boolean has(String cacheId);
}
