
public class CacheManager {

	
	private Cache[] caches;
	private int hits, misses, cacheLevel;

	public CacheManager(int levels) {
		caches = new Cache[levels];
	}

	public void createCache(int S, int L, int M) {
		caches[cacheLevel] = new Cache(S, L, M);
	}

}