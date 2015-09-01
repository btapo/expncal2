package org.zigmoi.expncal.commons;

import org.zigmoi.expncal.storage.cache.accessor.Cache;
import org.zigmoi.expncal.storage.cache.interfaces.ICache;

public class SequenceOp {

	public synchronized static long getNextSequence(int seqId) {
		return getNextSequence(String.valueOf(seqId));
	}

	public synchronized static long getNextSequence(String seqId) {
		ICache ch = new Cache(SequenceOp.class.getName());
		int seq = 0;
		if (ch.has(seqId)) {
			seq = (int) ch.get("getNextSequence" + Cache.getSeparator() + seqId);
			return ++seq;
		} else {
			ch.add(seqId, ++seq);
			return seq;
		}
	}
}
