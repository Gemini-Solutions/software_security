/*Note : Every Comment is about the type of data which is given to dataInput method.*/
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.owasp.html.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SanitizeTest {

    @Test
    void forString() {
        /* For Basic Strings */
        assertEquals("XXXSampleStringXXX", Sanitize.dataInput("SampleString"), "Objects should be identical");
        assertEquals("XXX12T34XXX", Sanitize.dataInput("12T34"), "Objects should be identical");
        assertEquals("XXX@#$Test%^XXX", Sanitize.dataInput("@#$Test%^"), "Objects should be identical");
    }

    @Test
    void simpleList() {
        /*Basic String List*/
        List<Object> list = Arrays.asList("100", "hi");
        List<Object> exList = Arrays.asList("XXX100XXX", "XXXhiXXX");
        assertEquals(exList, Sanitize.dataInput(list), "Objects should be identical");
    }

    @Test
    void simpleListWithNumbersAndBoolean() {
        /* List of string number and boolean */
        List<Object> list = Arrays.asList("100", 23, false, "hi", true, 33.43);
        List<Object> exList = Arrays.asList("XXX100XXX", 23, false, "XXXhiXXX", true, 33.43);
        assertEquals(exList, Sanitize.dataInput(list), "Objects should be identical");
    }

    @Test
    void listWithinList() {
        /* list of list */
        List<Object> list = new LinkedList<>(Arrays.asList("100", 23, false, "hi", true, 33.43));
        List<Object> exList = new LinkedList<>(Arrays.asList("XXX100XXX", 23, false, "XXXhiXXX", true, 33.43));
        List<Object> SubList = Arrays.asList("SubListValue", 23, false);
        List<Object> exSubList = Arrays.asList("XXXSubListValueXXX", 23, false);
        list.add(SubList);
        exList.add(exSubList);
        System.out.println("Expected: " + exList);
        List<Object> fun = Sanitize.dataInput(list);
        System.out.println("Actual: " + fun);
        assertEquals(exList, fun, "Objects should be identical");
    }

    @Test
    void simpleJsonWithinList() {
        /*list of json object */
        List<Object> list = new LinkedList<>(Arrays.asList("100", 23, false, "hi", true, 33.43));
        List<Object> exList = new LinkedList<>(Arrays.asList("XXX100XXX", 23, false, "XXXhiXXX", true, 33.43));
        JSONObject json = new JSONObject();
        json.put("name", "Student");
        json.put("age", 23);
        list.add(json);
        JSONObject exJson = new JSONObject();
        exJson.put("name", "XXXStudentXXX");
        exJson.put("age", 23);
        exList.add(exJson);
        System.out.println("Expected: " + exList);
        List<Object> funcReturnList = Sanitize.dataInput(list);
        System.out.println("Actual: " + funcReturnList);
    }

    @Test
    void JsonWithJsonArrayWithinList() {
        /*This is a list which has json object and that json object has json array in it*/
        List<Object> list = new LinkedList<>(Arrays.asList("100", 23, false, "hi", true, 33.43));
        List<Object> exList = new LinkedList<>(Arrays.asList("XXX100XXX", 23, false, "XXXhiXXX", true, 33.43));
        JSONObject json = new JSONObject();
        json.put("name", "Student");
        json.put("age", 23);
        JSONArray array1 = new JSONArray();
        JSONObject item1 = new JSONObject();
        item1.put("information", "test");
        item1.put("id", 3);
        array1.put(item1);
        json.put("course", array1);
        list.add(json);
        JSONObject exJson = new JSONObject();
        exJson.put("XXXnameXXX", "XXXStudentXXX");
        exJson.put("XXXageXXX", 23);
        JSONArray array2 = new JSONArray();
        JSONObject item2 = new JSONObject();
        item2.put("XXXinformationXXX", "XXXtestXXX");
        item2.put("XXXidXXX", 3);
        array2.put(item2);
        exJson.put("XXXcourseXXX", array2);
        exList.add(exJson);


        System.out.println("Expected: " + exList);
        List<Object> funcReturnList = Sanitize.dataInput(list);
        System.out.println("Actual: " + funcReturnList);
    }

    @Test
    void simpleJSON() {
        /*Simple JSON Object */
        JSONObject json = new JSONObject();
        json.put("name", "Student");
        json.put("age", 23);
        JSONObject exJson = new JSONObject();
        exJson.put("XXXnameXXX", "XXXStudentXXX");
        exJson.put("XXXageXXX", 23);

        System.out.println("Expected: " + exJson);
        JSONObject funcReturnList = Sanitize.dataInput(json);
        System.out.println("Actual: " + funcReturnList);
    }

    @Test
    void simpleJSONwithJsonArray() {
        /*This is json object with json array which has json object, string & another json array and*/
        JSONObject json = new JSONObject();
        json.put("name", "Student");
        json.put("age", 23);
        JSONArray array1 = new JSONArray();
        JSONObject item1 = new JSONObject();
        item1.put("information", "test");
        item1.put("id", 3);
        array1.put(item1);
        JSONObject item11 = new JSONObject();
        item11.put("info", "teeest");
        item11.put("id1", 3);
        array1.put("sample");
        List<Object> list = new LinkedList<>(Arrays.asList("100", 23, false, "hi", true, 33.43));
        list.add(item11);
        array1.put(list);
        json.put("course", array1);
        JSONObject exJson = new JSONObject();
        exJson.put("XXXnameXXX", "XXXStudentXXX");
        exJson.put("XXXageXXX", 23);
        JSONArray array2 = new JSONArray();
        JSONObject item2 = new JSONObject();
        item2.put("XXXinformationXXX", "XXXtestXXX");
        item2.put("XXXidXXX", 3);
        array2.put(item2);
        JSONObject item22 = new JSONObject();
        item22.put("XXXinfoXXX", "XXXteeestXXX");
        item22.put("XXXid1XXX", 3);
        array2.put("XXXsampleXXX");
        List<Object> exList = new LinkedList<>(Arrays.asList("XXX100XXX", 23, false, "XXXhiXXX", true, 33.43));
        exList.add(item22);
        array2.put(exList);
        exJson.put("XXXcourseXXX", array2);

        System.out.println("Expected: " + exJson);
        JSONObject funcReturnList = Sanitize.dataInput(json);
        System.out.println("Actual: " + funcReturnList);
    }

    @Test
    void forClass() throws IllegalAccessException {
        /* Basic Class */
        Employee emp = new Employee();
        emp.setEmp_id(101);
        emp.setName("Emma Watson");
        emp.setDepartment("IT");
        emp.setSalary(15000);
        emp.setAddress("New Delhi");
        emp.setEmail("Emmawatson123@gmail.com");

        System.out.println();

        System.out.println(emp.toStr());
        Sanitize.dataInput(emp);
        System.out.println(emp.toStr());

    }

    @Test
    void listOfClassObjects() throws IllegalAccessException {
        /* this list consist of two objects */
        Employee emp = new Employee();
        emp.setEmp_id(101);
        emp.setName("Emma Watson");
        emp.setDepartment("IT");
        emp.setSalary(15000);
        emp.setAddress("New Delhi");
        emp.setEmail("Emmawatson123@gmail.com");

        Employee emp2 = new Employee();
        emp2.setEmp_id(101);
        emp2.setName("name");
        emp2.setDepartment("IT");
        emp2.setSalary(15000);
        emp2.setAddress("delhi");
        emp2.setEmail("name123@gmail.com");

        List<Object> list = new ArrayList<>();
        list.add(emp);
        list.add(emp2);
        System.out.println(emp.toStr());
        System.out.println(emp2.toStr());
        Sanitize.dataInput(list);

        System.out.println(emp.toStr());
        System.out.println(emp2.toStr());

    }

    @Test
    void forClassWithJson() throws IllegalAccessException {
        /* this test case consist of inheritance, class with json  and class with list */
        JSONObject json = new JSONObject();
        json.put("name", "Student");
        json.put("age", 23);
        List<Object> list = new ArrayList<>();
        list.add("emp");
        list.add("emp2");
        MountainBike mb = new MountainBike(3, 100, 25, list, "bicycleName", json, "this is message");
        System.out.println(mb.toString());
        Sanitize.dataInput(mb);
        System.out.println(mb.toString());
    }

    @Test
    void sanitize() {
        // Example of JAVA HTML Sanitizer
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        //PolicyFactory policy = Sanitizers.FORMATTING;
        String untrustedHTML = "<!DOCTYPE html> <html> <body> <h2>HTML Links</h2> " +
                "<p>HTML links are defined with the a tag:</p> <a href=\"https://www.w3schools.com\">This is a link</a>" +
                "</body> </html>" ;
        String safeHTML = policy.sanitize(untrustedHTML);
        //System.out.println("Before data : " + untrustedHTML);
        //System.out.println("After data : " + safeHTML.trim());

        // Case : When we don't want to allow anything in a policy.
        PolicyFactory policyBuilder = new HtmlPolicyBuilder()
                .toFactory();
        String safeNew = policyBuilder.sanitize(safeHTML);
        System.out.println(safeNew.trim());
    }



}