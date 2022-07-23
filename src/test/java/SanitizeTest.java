/*Note : randomXSSAttack is used in inputs to create untrusted string
 *        assertEquals is compares the input data with the expected data */

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.html.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SanitizeTest {

    Object randomXSSAttack() {
        // This function return an element from the xssAttackList randomly and each element is a XSS attack.
        List<Object> xssAttackList = Arrays.asList(
                "<xss onafterscriptexecute=alert(1)><script>1</script>",
                "<image src=validimage.png onloadend=alert(1)>",
                "<style>@keyframes x{}</style><xss style=\"animation-name:x\" instantiation=\"alert(1)\"></xss>",
                "<style>:target {color:red;}</style><xss id=x style=\"transition:color 1s\" onwebkittransitionend=alert(1)></xss>",
                "<video philanthropically=alert(1)><source src=\"valid video.mp4\" type=\"video/mp4\"></video>",
                "<audio underloaded=alert(1)><source src=\"validation.wav\" type=\"audio/wav\"></audio>");

        Random rand = new Random();
        return xssAttackList.get(rand.nextInt(xssAttackList.size()));

    }

    @Test
    void forString() {
        /* For Basic Strings */
        assertEquals("helloSampleString", Sanitize.dataInput("hello" + randomXSSAttack() + "Sam" + randomXSSAttack() + "pleString"));
        assertEquals("12T34", Sanitize.dataInput("12T" + randomXSSAttack() + "34"), "Objects should be identical");
        assertEquals("AnotherBig String", Sanitize.dataInput("Another" + randomXSSAttack() + "Big Str" + randomXSSAttack() + "ing"), "Objects should be identical");
    }


    @Test
    void simpleList() {
        /*simple string list*/
        // Input List
        List<Object> list = Arrays.asList("10" + randomXSSAttack() + "0", "hi" + randomXSSAttack());

        // Expected List
        List<Object> exList = Arrays.asList("100", "hi");

        // Testing
        assertEquals(exList, Sanitize.dataInput(list), "Objects should be identical");
    }

    @Test
    void simpleListWithNumbersAndBoolean() {
        /* List of string number and boolean */
        // Input List
        List<Object> list = Arrays.asList("1" + randomXSSAttack() + "00", 23, false, "h" + randomXSSAttack() + "i", true, 33.43);

        // Expected List
        List<Object> exList = Arrays.asList("100", 23, false, "hi", true, 33.43);

        // Testing
        assertEquals(exList, Sanitize.dataInput(list), "Objects should be identical");
    }

    @Test
    void listWithinList() {
        /* list of list */
        // Input Main List
        List<Object> list = new LinkedList<>(Arrays.asList("10" + randomXSSAttack() + "0", 23, false, randomXSSAttack() + "hi", true, 33.43));
        // Adding sub list into main list
        list.add(Arrays.asList("SubLi" + randomXSSAttack() + "stVa" + randomXSSAttack() + "lue", 23, false));

        // Expected Main List
        List<Object> exList = new LinkedList<>(Arrays.asList("100", 23, false, "hi", true, 33.43));
        // Adding sub list into main list
        exList.add(Arrays.asList("SubListValue", 23, false));
        // Testing
        assertEquals(exList, Sanitize.dataInput(list), "Objects should be identical");
    }

    @Test
    void simpleJsonWithinList() throws IOException {
        /*list of json object */
        // Input List
        List<Object> list = new LinkedList<>(Arrays.asList("10" + randomXSSAttack() + "0", 23, false, randomXSSAttack() + "hi", true, 33.43));
        // JSON Object
        JSONObject json = new JSONObject();
        json.put("na" + randomXSSAttack() + "me", "S" + randomXSSAttack() + "tudent");
        json.put("ag" + randomXSSAttack() + "e", 23);
        // Adding JSON to list
        list.add(json);

        // Expected List
        List<Object> exList = new LinkedList<>(Arrays.asList("100", 23, false, "hi", true, 33.43));
        // JSON object
        JSONObject exJson = new JSONObject();
        exJson.put("name", "Student");
        exJson.put("age", 23);
        // Adding JSON to List
        exList.add(exJson);

        // Sanitizing the input list
        System.out.println(list);
        Sanitize.dataInput(list);
        System.out.println(list);

        // Iterating List to compare the input list and expected list
        // Logic : Check element for its type and perform assertion accordingly.
        for (Object ele : list) {
            if (ele instanceof String) {
                assertEquals(exList.get(list.indexOf(ele)), ele);
            } else if (ele instanceof JSONObject) {
                // Using JSONAssert because simple assert can't compare JSON object (async).
                JSONAssert.assertEquals(exList.get(list.indexOf(ele)).toString(), ele.toString(), JSONCompareMode.NON_EXTENSIBLE);
            }
        }
    }

    @Test
    void JsonWithJsonArrayWithinList() {
        /*This is a list which has json object and that json object has json array in it*/
        // Input List
        List<Object> list = new LinkedList<>(Arrays.asList("100", 23, false, "hi", true, 33.43));
        // json  for list
        JSONObject json = new JSONObject();
        json.put("na" + randomXSSAttack() + "me", randomXSSAttack() + "Student");
        json.put("age", 23);
        // item1 into json
        JSONObject item1 = new JSONObject();
        item1.put("infor" + randomXSSAttack() + "mation", "te" + randomXSSAttack() + "st");
        item1.put("idw", 3);
        // Adding one list as a key value pair to item1
        item1.put("array1", Arrays.asList("arra" + randomXSSAttack() + "y1", 23, "h" + randomXSSAttack() + "i", true));
        // item1 into json
        json.put("list", item1);
        // adding json to list
        list.add(json);

        // Expected List
        List<Object> exList = new LinkedList<>(Arrays.asList("100", 23, false, "hi", true, 33.43));
        // json  for exList
        JSONObject exJson = new JSONObject();
        exJson.put("name", "Student");
        exJson.put("age", 23);
        // exItem1 for json
        JSONObject exItem1 = new JSONObject();
        exItem1.put("information", "test");
        exItem1.put("idw", 3);
        // Adding one list as a key value pair to exItem1
        exItem1.put("array1", Arrays.asList("array1", 23, "hi", true));
        // exItem1 into json
        exJson.put("list", exItem1);
        // adding json to list
        exList.add(exJson);

        // Sanitizing the data
        Sanitize.dataInput(list);

        // Testing
        for (Object ele : list) {
            if (ele instanceof String) {
                assertEquals(exList.get(list.indexOf(ele)), ele);
            } else if (ele instanceof JSONObject) {
                // Using JSONAssert because simple assert can't compare JSON object (async).
                JSONAssert.assertEquals(exList.get(list.indexOf(ele)).toString(), ele.toString(), JSONCompareMode.NON_EXTENSIBLE);
            }
        }
    }

    @Test
    void simpleJSON() {
        /*Simple JSON Object */
        // Input JSON
        JSONObject json = new JSONObject();
        json.put("nam"+randomXSSAttack()+"e", randomXSSAttack()+"Student");
        json.put("a"+randomXSSAttack()+"ge", 23);

        // Expected JSON
        JSONObject exJson = new JSONObject();
        exJson.put("name", "Student");
        exJson.put("age", 23);

        // Sanitizing the data
        Sanitize.dataInput(json);

        // Testing
        JSONAssert.assertEquals(exJson, json, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void simpleJSONwithJsonArray() {
        /*This is json object with json array which has json object, string & another json array and*/

        // Input JSON
        JSONObject json = new JSONObject();
        json.put("nam"+randomXSSAttack()+"e", randomXSSAttack()+"Student");
        json.put("age"+randomXSSAttack(), 23);
        JSONArray array1 = new JSONArray();
        JSONObject item1 = new JSONObject();
        item1.put("informati"+randomXSSAttack()+"on", "test");
        item1.put("id"+randomXSSAttack(), 3);
        // Adding item1 & simple string to array1
        array1.put(item1);
        array1.put("sam"+randomXSSAttack()+"ple");
        List<Object> list = new LinkedList<>(Arrays.asList("100", 23, false, "hi", true, 33.43));
        JSONObject item11 = new JSONObject();
        item11.put("in"+randomXSSAttack()+"fo", randomXSSAttack()+"teeest");
        item11.put("id1", 3);
        // Adding item11 to list
        list.add(item11);
        // Adding list to array1
        array1.put(list);
        // Adding array1 to json
        json.put("cour"+randomXSSAttack()+"se", array1);

        // Expected JSON
        JSONObject exJson = new JSONObject();
        exJson.put("name", "Student");
        exJson.put("age", 23);
        JSONArray array2 = new JSONArray();
        JSONObject item2 = new JSONObject();
        item2.put("information", "test");
        item2.put("id", 3);
        // Adding item2 & simple string to array2
        array2.put(item2);
        array2.put("sample");
        List<Object> exList = new LinkedList<>(Arrays.asList("100", 23, false, "hi", true, 33.43));
        JSONObject item22 = new JSONObject();
        item22.put("info", "teeest");
        item22.put("id1", 3);
        // Adding item22 to exList
        exList.add(item22);
        // Adding exList to array2
        array2.put(exList);
        // Adding array2 to exJson
        exJson.put("course", array2);

        // Sanitizing the data
        Sanitize.dataInput(json);

        // Testing
        JSONAssert.assertEquals(exJson, json, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void forClass() throws IllegalAccessException {
        /* Basic Class */
        // Input class object
        Employee emp = new Employee();
        // Adding data to class variables
        emp.setEmp_id(101);
        emp.setName(randomXSSAttack()+"Emma Watson"+randomXSSAttack());
        emp.setDepartment("IT"+randomXSSAttack());
        emp.setSalary(15000);
        emp.setAddress("New D" + randomXSSAttack() + "elhi");
        emp.setEmail("Emmawa"+randomXSSAttack()+"tson123@gmail.com");

        // Input class object
        Employee exEmp = new Employee();
        // Adding data to class variables
        exEmp.setEmp_id(101);
        exEmp.setName("Emma Watson");
        exEmp.setDepartment("IT");
        exEmp.setSalary(15000);
        exEmp.setAddress("New Delhi");
        exEmp.setEmail("Emmawatson123@gmail.com");

        // Sanitizing the data
        Sanitize.dataInput(emp);

        // As assertEquals not working for class object(Different instances).
        // We are using toStr method from employee class which returns a string with class variables value.
        assertEquals(exEmp.toStr(),emp.toStr());
    }


    @Test
    void forClassWithJson() throws IllegalAccessException {
        // This function use InheritExample Class
        /* this test case consist of inheritance, class with json  and class with list */
        // Input class object
        // Initializing the json object and adding data
        JSONObject json = new JSONObject();
        json.put("name"+randomXSSAttack(), randomXSSAttack()+"Stud"+randomXSSAttack()+"ent");
        json.put("age"+randomXSSAttack(), 23);
        // Initializing the list and adding data
        List<Object> list = new ArrayList<>();
        list.add("emp"+randomXSSAttack());
        list.add("em"+randomXSSAttack()+"p2");
        // Creating class object and assigning data to variable.
        MountainBike mb = new MountainBike(3, 100, 25, list, "bicycleNam"+randomXSSAttack()+"e", json, randomXSSAttack()+"this is message");

        // Expected class object
        // Initializing the json object and adding data
        JSONObject exJson = new JSONObject();
        exJson.put("name","Student");
        exJson.put("age", 23);
        // Initializing the list and adding data
        List<Object> exList = new ArrayList<>();
        exList.add("emp");
        exList.add("emp2");
        // Creating class object and assigning data to variable.
        MountainBike exMb = new MountainBike(3, 100, 25, exList, "bicycleName", exJson, "this is message");

        // Sanitizing the data
        Sanitize.dataInput(mb);

        // Testing
        // As assertEquals not working for class object(Different instances).
        // We are using toString method from MountainBike class which returns a string with class variables value.
        assertEquals(exMb.toString(),mb.toString());
    }

    @Test
    void listOfClassObjects() throws IllegalAccessException {
        /* this list consist of two objects */

        // Input List
        List<Object> list = new ArrayList<>();
        // 1 class object for input list
        Employee emp = new Employee();
        emp.setEmp_id(101);
        emp.setName("Emma " + randomXSSAttack() + "Watson");
        emp.setDepartment(randomXSSAttack() + "IT");
        emp.setSalary(15000);
        emp.setAddress("New Del" + randomXSSAttack() + "hi");
        emp.setEmail("Emmawatson123@gmail.com");
        // 2 class object for input list
        Employee emp2 = new Employee();
        emp2.setEmp_id(101);
        emp2.setName("name");
        emp2.setDepartment("IT");
        emp2.setSalary(15000);
        emp2.setAddress("delhi");
        emp2.setEmail("name123@gmail.com");
        // adding class object into list
        list.add(emp);
        list.add(emp2);

        // Expected List
        List<Object> exList = new ArrayList<>();
        // 1 class object for expected list
        Employee exEmp = new Employee();
        exEmp.setEmp_id(101);
        exEmp.setName("Emma Watson");
        exEmp.setDepartment("IT");
        exEmp.setSalary(15000);
        exEmp.setAddress("New Delhi");
        exEmp.setEmail("Emmawatson123@gmail.com");
        // 2 class object for expected list
        Employee exEmp2 = new Employee();
        exEmp2.setEmp_id(101);
        exEmp2.setName("name");
        exEmp2.setDepartment("IT");
        exEmp2.setSalary(15000);
        exEmp2.setAddress("delhi");
        exEmp2.setEmail("name123@gmail.com");
        // adding class object into exList
        exList.add(exEmp);
        exList.add(exEmp2);

        // sanitizing the list
        Sanitize.dataInput(list);

        /*As simple assert is not working with list of class object.
         * We are using toStr method which gives all the variable value defined in class object*/
        assertEquals(emp.toStr(), exEmp.toStr());
        assertEquals(emp2.toStr(), exEmp2.toStr());
    }

    // Ignore
    @Test
    void sanitize() {
        // Example of JAVA HTML Sanitizer
        PolicyFactory policy = Sanitizers.FORMATTING;
        //PolicyFactory policy = Sanitizers.FORMATTING;
        String untrustedHTML = "<!DOCTYPE html> <html> <body> <h2>HTML Links</h2> " +
                "<p>HTML links are defined with the a tag:</p> <a href=\"https://www.w3schools.com\">This is a link</a>" +
                "</body> </html>";
        String safeHTML = policy.sanitize(untrustedHTML);
        //System.out.println("Before data : " + untrustedHTML);
        //System.out.println("After data : " + safeHTML.trim());
        // Case : When we don't want to allow anything in a policy.
        PolicyFactory policyBuilder = new HtmlPolicyBuilder()
                .toFactory();
        String safeNew = policyBuilder.sanitize(safeHTML);
        System.out.println(safeNew.trim());
    }

    // Ignore
    @Test
    void esa1pi() {
        Encoder encoder = ESAPI.encoder();
        String untrustedHTML = "<!DOCTYPE html> <html> <body> <h2>HTML Links</h2> " +
                "<p>HTML links are defined with the a tag:</p> <a href=\"https://www.w3schools.com\">This is a link</a>" +
                "</body> </html>";
        String encoded = encoder.encodeForHTML(untrustedHTML);
        System.out.println(encoded);
        System.out.println(encoder.canonicalize(encoded));
    }
}