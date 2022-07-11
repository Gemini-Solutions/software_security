import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SanitizeTest {

    @Test
    void forString() {

        assertEquals("XXXSampleStringXXX", Sanitize.dataInput("SampleString"), "Objects should be identical");
        assertEquals("XXX12T34XXX", Sanitize.dataInput("12T34"), "Objects should be identical");
        assertEquals("XXX@#$Test%^XXX", Sanitize.dataInput("@#$Test%^"), "Objects should be identical");
    }

    @Test
    void simpleList() {
        List<Object> list = Arrays.asList("100", "hi");
        List<Object> exList = Arrays.asList("XXX100XXX", "XXXhiXXX");
        assertEquals(exList, Sanitize.dataInput(list), "Objects should be identical");
    }

    @Test
    void simpleListWithNumbersAndBoolean() {
        List<Object> list = Arrays.asList("100", 23, false, "hi", true, 33.43);
        List<Object> exList = Arrays.asList("XXX100XXX", 23, false, "XXXhiXXX", true, 33.43);
        assertEquals(exList, Sanitize.dataInput(list), "Objects should be identical");
    }

    @Test
    void listWithinList() {
        List<Object> list = new LinkedList<>(Arrays.asList("100", 23, false, "hi", true, 33.43));
        List<Object> exList = new LinkedList<>(Arrays.asList("XXX100XXX", 23, false, "XXXhiXXX", true, 33.43));
        List<Object> SubList = Arrays.asList("SubListValue", 23, false);
        List<Object> exSubList = Arrays.asList("XXXSubListValueXXX", 23, false);
        list.add(SubList);
        exList.add(exSubList);
        System.out.println(exList);
        List<Object> fun = Sanitize.dataInput(list);
        System.out.println(fun);
        assertEquals(exList, fun, "Objects should be identical");
    }

    @Test
    void simpleJsonWithinList() {
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
    void simpleJSONwithStringArray() {
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
        array2.put("XXXsampleXXX");
        List<Object> exList = Arrays.asList("XXX100XXX", 23, false, "XXXhiXXX", true, 33.43);
        array2.put(exList);
        exJson.put("XXXcourseXXX", array2);

        System.out.println("Expected: " + exJson);
        JSONObject funcReturnList = Sanitize.dataInput(json);
        System.out.println("Actual: " + funcReturnList);
    }

    @Test
    void forClass() throws IllegalAccessException {
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
    void liftOfClassObjects() throws IllegalAccessException {
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


}