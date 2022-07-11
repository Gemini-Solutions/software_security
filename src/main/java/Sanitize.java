import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Sanitize {

    public static String dataInput(String a) {
        return dataConverter(a);
    }

    public static List<Object> dataInput(List<Object> arg) {
        try {
            for (Object item : arg) {
                if (item instanceof String) {
                    arg.set(arg.indexOf(item), dataConverter((String) item));
                } else if (item instanceof Collection<?>) {
                    dataInput((List<Object>) item);
                } else if (item instanceof JSONObject) {
                    dataInput((JSONObject) item);
                } else if (item != null) {
                    dataInput(item);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return arg;
    }

    public static List<Object> dataInput(JSONArray arg) {
        List<Object> simpleList = new ArrayList<>();
        try {
            arg.iterator().forEachRemaining(ele -> {
                if (ele instanceof JSONObject) {
                    simpleList.add(dataInput((JSONObject) ele));
                } else if (ele instanceof JSONArray) {
                    simpleList.add(dataInput((JSONArray) ele));
                } else {
                    if (ele instanceof String) {
                        simpleList.add(dataConverter((String) ele));
                    } else {
                        simpleList.add(ele);
                    }
                }
            });
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return simpleList;
    }


    public static JSONObject dataInput(JSONObject arg) {
        try {
            List<Object> keys = new ArrayList<>();
            arg.keys().forEachRemaining(key -> {
                Object value = arg.get(key);
                keys.add(key);
                if (value instanceof JSONArray) {
                    arg.put(key, dataInput((JSONArray) value));

                } else {
                    if (value instanceof String) {
                        arg.put(key, dataConverter((String) value));
                    }
                }
            });
            List<Object> newKeys = new ArrayList<>(keys);
            dataInput(newKeys);
            for (int i = 0; i < newKeys.size(); i++) {
                arg.put((String) newKeys.get(i), arg.get((String) keys.get(i)));
                arg.remove((String) keys.get(i));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return arg;
    }

    public static Object dataInput(Object arg) throws IllegalAccessException {
        JSONObject json = new JSONObject();
        List<Object> list = new ArrayList<>();
        if (!(arg.getClass().getName().contains("java."))) {
            for (Field field : arg.getClass().getFields()) {
                if (field.getType().isInstance("")) {
                    field.set(arg, dataConverter((String) field.get(arg)));
                }else if (field.getType().isInstance(json)){
                    field.set(arg,dataInput((JSONObject) field.get(arg)));
                }else if (field.getType().isInstance(list)){
                    field.set(arg,dataInput((List<Object>) field.get(arg)));
                }
            }
        }
        return arg;
    }

    public static String dataConverter(String s) {
        return sanitization(s);
    }

    public static String sanitization(String s) {
        return "XXX" + s + "XXX";
    }

}
