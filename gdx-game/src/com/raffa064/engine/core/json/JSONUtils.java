package com.raffa064.engine.core.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
	Provide methods to facilitate JSON usage
*/

public class JSONUtils {
    public static boolean getBoolean(JSONObject json, String name, boolean defaultValue) throws JSONException {
        if (json.has(name)) {
            return json.getBoolean(name);
        }
        return defaultValue;
    }

    public static double getDouble(JSONObject json, String name, double defaultValue) throws JSONException {
        if (json.has(name)) {
            return json.getDouble(name);
        }
        return defaultValue;
    }

    public static int getInt(JSONObject json, String name, int defaultValue) throws JSONException {
        if (json.has(name)) {
            return json.getInt(name);
        }
        return defaultValue;
    }

    public static long getLong(JSONObject json, String name, long defaultValue) throws JSONException {
        if (json.has(name)) {
            return json.getLong(name);
        }
        return defaultValue;
    }

    public static String getString(JSONObject json, String name, String defaultValue) throws JSONException {
        if (json.has(name)) {
            return json.getString(name);
        }
        return defaultValue;
    }

    public static JSONArray getJSONArray(JSONObject json, String name, JSONArray defaultValue) throws JSONException {
        if (json.has(name)) {
            return json.getJSONArray(name);
        }
        return defaultValue;
    }

    public static JSONObject getJSONObject(JSONObject json, String name, JSONObject defaultValue) throws JSONException {
        if (json.has(name)) {
            return json.getJSONObject(name);
        }
        return defaultValue;
    }

    public static JSONObject getJSON(JSONObject json, String name) throws JSONException {
        return getJSONObject(json, name, new JSONObject());
    }

    public static JSONObject getJSON(JSONObject json, String name, JSONObject defaultValue) throws JSONException {
        return getJSONObject(json, name, defaultValue);
    }
}

