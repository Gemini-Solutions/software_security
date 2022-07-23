/* Note : 1. Every Instanceof condition is to check the type of element with the desired type
 *         2. dataInput is being used recursively throughout for the cases like object within same object*/

import org.json.JSONArray;
import org.json.JSONObject;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Sanitize {

    public static String dataInput(String a) {
        /* This method takes string as input and return sanitize string which comes from sanitization.
           LOGIC: Takes a string and sent this to dataConverter which trigger sanitization method. */
        return dataConverter(a);
    }

    public static List<Object> dataInput(List<Object> arg) {
        /*This method takes List<Object> as input and return sanitize List<Object> which comes from sanitization.
          LOGIC:Takes a List<Object> then iterate it to check for string, collection, object & jsonObject
          if element is of string type then call dataConverter and sanitize the string otherwise call
          dataInput according to the type of element.*/
        try {
            for (Object item : arg) {
                if (item instanceof String) {
                    arg.set(arg.indexOf(item), dataConverter((String) item));
                } else if (item instanceof Collection<?>) {
                    dataInput((List<Object>) item);
                } else if (item instanceof JSONObject) {
                    dataInput((JSONObject) item);
                } else if (item != null) {
                    // Check for object type.
                    dataInput(item);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return arg;
    }

    public static List<Object> dataInput(JSONArray arg) {
         /*This method takes JSONArray as input and return sanitize  List<Object> which comes from sanitization.
          LOGIC:Takes an JSONArray then check its type whether it is String, JSONObject & List<Object>.
          if value is of string type then call dataConverter and sanitize the string otherwise call
          dataInput according to the type of element.*/
        List<Object> simpleList = new ArrayList<>();
        try {
            /*Here each element will be saved in simpleList after sanitization and the list will be added
            to key which has this JSONArray */
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
       /*This method takes JSONObject as input and return sanitize JSONObject which comes from sanitization.
          LOGIC:[For Value]Takes a JSONObject then iterate it to check type of value whether it is string & JSONArray.
          if value is of string type then call dataConverter and sanitize the string otherwise call
          dataInput according to the type of element.
          NOTE: 1. In JSON Object if you add a list then its type will be JSONArray.
                2. If we change key in fist iteration then after first key it will return null as Json Object is
                being updated.*/

        try {
            List<Object> keys = new ArrayList<>();
            arg.keys().forEachRemaining(key -> {
                Object value = arg.get(key);
                keys.add(key);
                if (value instanceof JSONObject) {
                    arg.put(key, dataInput((JSONObject) value));
                } else if (value instanceof JSONArray) {
                    arg.put(key, dataInput((JSONArray) value));
                } else if (value instanceof String) {
                    arg.put(key, dataConverter((String) value));
                }
            });
            /*[For Keys]: At above iteration we take all the key and store this into a list and sent
              it to dataInput(List<Object>) then we iterate new keys list and add old key value into new keys and
              then remove the old key.*/
            List<Object> newKeys = new ArrayList<>(keys);
            dataInput(newKeys);
            for (int i = 0; i < newKeys.size(); i++) {
                if(!newKeys.get(i).equals(keys.get(i))) {
                    arg.put((String) newKeys.get(i), arg.get((String) keys.get(i)));
                    arg.remove((String) keys.get(i));
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return arg;
    }

    public static Object dataInput(Object arg) throws IllegalAccessException {
        /*This method takes Object as input and return sanitize Object which comes from sanitization.
          LOGIC:Takes an Object then check its type whether it is String, JSONObject & List<Object>.
          if value is of string type then call dataConverter and sanitize the string otherwise call
          dataInput according to the type of element.*/
        JSONObject json = new JSONObject();
        List<Object> list = new ArrayList<>();
        /*Below Condition is to check if object is user defined or not*/
        if (!(arg.getClass().getName().contains("java."))) {
            Field[] fields = arg.getClass().getFields();
            if( fields.length == 0){
                fields = arg.getClass().getDeclaredFields();
            }
            for (Field field : fields) {
                /* Below condition is to check the type of particular field and isInstance takes value
                to compare that's why we have to initialize string, json and list*/
                if (field.getType().isInstance("")) {
                    field.set(arg, dataConverter((String) field.get(arg)));
                } else if (field.getType().isInstance(json)) {
                    field.set(arg, dataInput((JSONObject) field.get(arg)));
                } else if (field.getType().isInstance(list)) {
                    field.set(arg, dataInput((List<Object>) field.get(arg)));
                }
            }
        }
        return arg;
    }

    public static String dataConverter(String s) {
        /*This method takes string as input and return the string returned from sanitization method*/
        return sanitization(s);
    }

    public static String sanitization(String s) {
        /*This method takes string as input and return sanitized string*/
        PolicyFactory policy = Sanitizers.FORMATTING;
        return policy.sanitize(s).replace("&#64;", "@");
//        return "XXX"+s+"XXX";
    }

}
