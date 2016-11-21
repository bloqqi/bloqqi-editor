package org.bloqqi.editor;

import java.util.HashMap;
import java.util.Map;

public class Properties {
	public static String KEY_BLOCK_NAME;
	
	private Map<String, Object> map;
	private Properties() {
		map = new HashMap<String, Object>();
	}
	
	public Object get(String key) {
		return map.get(key);
	}
	public double getDouble(String key, double defaultValue) {
		Object v = get(key);
		return v != null ? (Double) v : defaultValue;
	}
	public int getInt(String key, int defaultValue) {
		Object v = get(key);
		return v != null ? (Integer) v : defaultValue;
	}
	public String getString(String key, String defaultValue) {
		Object v = get(key);
		return v != null ? (String) v : defaultValue;
	}
	
	public void put(String key, Object value) {
		map.put(key, value);
	}
	
	private static Properties instance;
	public static Properties instance() {
		if (instance == null) {
			instance = new Properties();
		}
		return instance;
	}
}
