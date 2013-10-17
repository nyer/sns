package org.nyer.sns.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

public class JsonUtil {

    public static String getString(JSONObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return jsonObject.getString(key);
        }
        return null;
    }

    public static String getString(JSONObject jsonObject, String key, String defaultValue) {
        try {
            if (jsonObject.containsKey(key)) {
                return jsonObject.getString(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return defaultValue;
    }

    public static Integer getInteger(JSONObject jsonObject, String key) {
        try {
            if (jsonObject.has(key)) {
                return jsonObject.getInt(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public static Integer getInt(JSONObject jsonObject, String key, int defaultValue) {
        try {
            if (jsonObject.has(key)) {
                return jsonObject.getInt(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return defaultValue;
    }

    public static Long getLong(JSONObject jsonObject, String key) {
        try {
            if (jsonObject.has(key)) {
                return Long.parseLong(jsonObject.getString(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static Long getLong(JSONObject jsonObject, String key, long defaultValue) {
        try {
            if (jsonObject.has(key)) {
                return Long.parseLong(jsonObject.getString(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0l;
        }
        return defaultValue;
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String key) {
        try {
            if (jsonObject.has(key)) {
                return jsonObject.getJSONArray(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String key) {
        try {
            if (jsonObject.has(key)) {
                return jsonObject.getJSONObject(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static <T> List<T> parseJsonArray(String json, Class<T> elementClass) {
        List<T> list = new ArrayList<T>();
        JSONArray jsonArray = JSONArray.fromObject(json);
        for (Object obj : jsonArray) {
            @SuppressWarnings("unchecked")
            T t = (T) JSONObject.toBean((JSONObject) obj, elementClass);
            list.add(t);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> parseJsonArray(String json, Class<T> elementClass, final String... ignoredProperties) {
        List<T> list = new ArrayList<T>();
        JsonConfig jsonConfig = createJsonConfig(elementClass, ignoredProperties);

        JSONArray jsonArray = JSONArray.fromObject(json);
        for (Object obj : jsonArray) {
            list.add((T) JsonUtil.toBean((JSONObject) obj, jsonConfig));
        }
        return list;
    }

    public static Object toBean(Class<?> clazz, String src) {
        JSONObject obj = JSONObject.fromObject(src);
        return JSONObject.toBean(obj, clazz);
    }

    public static Object toBean(JSONObject jsonObject, Class<?> rootClass, final String... ignoredProperties) {
        return toBean(jsonObject, createJsonConfig(rootClass, ignoredProperties));
    }

    public static Object toBean(JSONObject jsonObject, JsonConfig jsonConfig) {
        return JSONObject.toBean(jsonObject, jsonConfig);
    }

    public static JsonConfig createJsonConfig(Class<?> rootClass, final String... ignoredProperties) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setRootClass(rootClass);
        jsonConfig.setJavaPropertyFilter(new PropertyFilter() {

            @Override
            public boolean apply(Object source, String name, Object value) {
                for (String ignoredProperty : ignoredProperties) {
                    if (ignoredProperty.equals(name)) {
                        return true;
                    }
                }
                return false;
            }
        });

        return jsonConfig;
    }
}
