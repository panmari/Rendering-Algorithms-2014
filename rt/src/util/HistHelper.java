package util;

import java.util.concurrent.ConcurrentHashMap;
/**
 * Computes a histogram after repeatedly adding values using the add method. 
 *
 */
public class HistHelper {

	public static ConcurrentHashMap<String, Integer> m = new ConcurrentHashMap<>();

	public static void add(float f) {
		String s = String.format("%.1f", f);
		m.put(s, m.getOrDefault(s, 0) + 1);
	}
	
	public static void print() {
		System.out.println(m);
	}
}
