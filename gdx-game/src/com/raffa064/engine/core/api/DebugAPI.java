package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;
import java.util.List;

/*
	API to provide debug functions
*/

public class DebugAPI extends API {
	public DebugAPI(App app) {
		super(app);
	}
	
	@Override
	public API.APIState createState() {
		return buildState();
	}

	@Override
	public void useState(API.APIState state) {
	}

	public void expect(Object value, Object expected, String message) {
		if (value != expected) {
			error(message);
		}
	}

	public void range(float value, float a, float b, String message) {
		if (value < Math.min(a, b) || value > Math.max(a, b)) {
			error(message);
		}
	}

	public void match(String value, String regex, String message) {
		if (!value.matches(regex)) {
			error(message);
		}
	}

	public void notNull(Object value, String message) {
		if (value == null) {
			error(message);
		}
	}

	public <T extends Object> void index(int index, T[] array, String message) {
		if (index < 0 || index >= array.length) {
			error(message);
		}
	}

	public void index(int index, List array, String message) {
		if (index < 0 || index >= array.size()) {
			error(message);
		}
	}

	public void min(float value, float min, String message) {
		if (value < min) {
			error(message);
		}
	}

	public void max(float value, float max, String message) {
		if (value > max) {
			error(message);
		}
	}

	public void condition(String message, boolean... conditions) {
		for (boolean condition : conditions) {
			if (!condition) {
				error(message);
				break;
			}
		}
	}

	public void set(Object value, List list, String message) {
		if (!list.contains(value)) {
			error(message);
		}
	}

	public <T> void set(T value, T... list, String message) {
		for (T item : list) {
			if (value.equals(item)) return;
		}

		error(message);
	}

	public void error(String message) {
		System.out.println(message);
	}
}
